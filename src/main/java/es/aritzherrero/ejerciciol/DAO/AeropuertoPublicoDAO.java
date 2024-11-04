package es.aritzherrero.ejerciciol.DAO;
import es.aritzherrero.ejerciciol.Modelo.Aeropuerto;
import es.aritzherrero.ejerciciol.Modelo.AeropuertoPublico;
import es.aritzherrero.ejerciciol.Modelo.Direccion;
import es.aritzherrero.ejerciciol.db.ConexionDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase que gestiona las operaciones de acceso a datos de los aeropuertos públicos.
 */
public class AeropuertoPublicoDAO {

    /**
     * Obtiene un aeropuerto público por su ID.
     *
     * @param id El ID del aeropuerto público.
     * @return El aeropuerto público encontrado o null si no se encuentra.
     */
    public static AeropuertoPublico getAeropuerto(int id) throws SQLException {
        ConexionDB connection = null;
        AeropuertoPublico aeropuerto = null;

        try {
            connection = new ConexionDB();
            String consulta = "SELECT id,nombre,anio_inauguracion,capacidad,id_direccion,imagen,financiacion,num_trabajadores " +
                    "FROM aeropuertos,aeropuertos_publicos WHERE id=id_aeropuerto AND id = ?";
            PreparedStatement pstmt = connection.getConexion().prepareStatement(consulta);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int id_aeropuerto = rs.getInt("id");
                String nombre = rs.getString("nombre");
                int anio_inauguracion = rs.getInt("anio_inauguracion");
                int capacidad = rs.getInt("capacidad");
                int id_direccion = rs.getInt("id_direccion");
                Direccion direccion = DireccionDAO.getDireccion(id_direccion);
                Blob imagen = rs.getBlob("imagen");
                Aeropuerto airport = new Aeropuerto(id_aeropuerto, nombre, anio_inauguracion, capacidad, direccion, imagen);
                BigDecimal financiacion = rs.getBigDecimal("financiacion");
                int num_trabajadores = rs.getInt("num_trabajadores");
                aeropuerto = new AeropuertoPublico(airport, financiacion, num_trabajadores);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            if (connection != null) {
                connection.CloseConexion();
            }
        }
        return aeropuerto;
    }

    /**
     * Carga una lista de aeropuertos públicos.
     *
     * @return Una lista observable de aeropuertos públicos.
     */
    public static ObservableList<AeropuertoPublico> cargarListado() throws SQLException {
        ConexionDB connection = null;
        ObservableList<AeropuertoPublico> airportList = FXCollections.observableArrayList();

        try {
            connection = new ConexionDB();
            String consulta = "SELECT id,nombre,anio_inauguracion,capacidad,id_direccion,imagen,financiacion,num_trabajadores " +
                    "FROM aeropuertos,aeropuertos_publicos WHERE id=id_aeropuerto";
            PreparedStatement pstmt = connection.getConexion().prepareStatement(consulta);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                int anio_inauguracion = rs.getInt("anio_inauguracion");
                int capacidad = rs.getInt("capacidad");
                int id_direccion = rs.getInt("id_direccion");
                Direccion direccion = DireccionDAO.getDireccion(id_direccion);
                Blob imagen = rs.getBlob("imagen");
                Aeropuerto aeropuerto = new Aeropuerto(id, nombre, anio_inauguracion, capacidad, direccion, imagen);
                BigDecimal financiacion = rs.getBigDecimal("financiacion");
                int num_trabajadores = rs.getInt("num_trabajadores");
                AeropuertoPublico airport = new AeropuertoPublico(aeropuerto, financiacion, num_trabajadores);
                airportList.add(airport);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            if (connection != null) {
                connection.CloseConexion();
            }
        }
        return airportList;
    }

    /**
     * Modifica un aeropuerto público existente.
     *
     * @param aeropuerto      El aeropuerto público a modificar.
     * @param aeropuertoNuevo El nuevo aeropuerto público con los datos actualizados.
     * @return true si se actualizó correctamente, false en caso contrario.
     */
    public static boolean modificar(AeropuertoPublico aeropuerto, AeropuertoPublico aeropuertoNuevo) throws SQLException {
        ConexionDB connection = null;
        PreparedStatement pstmt;

        try {
            connection = new ConexionDB();
            String consulta = "UPDATE aeropuertos_publicos SET financiacion = ?, num_trabajadores = ? WHERE id_aeropuerto = ?";
            pstmt = connection.getConexion().prepareStatement(consulta);
            pstmt.setBigDecimal(1, aeropuertoNuevo.getFinanciacion());
            pstmt.setInt(2, aeropuertoNuevo.getNum_trabajadores());
            pstmt.setInt(3, aeropuerto.getAeropuerto().getId());

            int filasAfectadas = pstmt.executeUpdate();
            System.out.println("Actualizado aeropuerto");
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        } finally {
            if (connection != null) {
                connection.CloseConexion();
            }
        }
    }

    /**
     * Inserta un nuevo aeropuerto público en la base de datos.
     *
     * @param aeropuerto El aeropuerto público a insertar.
     * @return true si se insertó correctamente, false en caso contrario.
     */
    public static boolean insertar(AeropuertoPublico aeropuerto) throws SQLException {
        ConexionDB connection = null;
        PreparedStatement pstmt;

        try {
            connection = new ConexionDB();
            String consulta = "INSERT INTO aeropuertos_publicos (id_aeropuerto, financiacion, num_trabajadores) VALUES (?, ?, ?)";
            pstmt = connection.getConexion().prepareStatement(consulta);

            pstmt.setInt(1, aeropuerto.getAeropuerto().getId());
            pstmt.setBigDecimal(2, aeropuerto.getFinanciacion());
            pstmt.setInt(3, aeropuerto.getNum_trabajadores());

            int filasAfectadas = pstmt.executeUpdate();
            System.out.println("Nueva entrada en aeropuertos_publicos");
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        } finally {
            if (connection != null) {
                connection.CloseConexion();
            }
        }
    }

    /**
     * Elimina un aeropuerto público de la base de datos.
     *
     * @param aeropuerto El aeropuerto público a eliminar.
     * @return true si se eliminó correctamente, false en caso contrario.
     */
    public static boolean eliminar(AeropuertoPublico aeropuerto) throws SQLException {
        ConexionDB connection = null;
        PreparedStatement pstmt;

        try {
            connection = new ConexionDB();
            String consulta = "DELETE FROM aeropuertos_publicos WHERE id_aeropuerto = ?";
            pstmt = connection.getConexion().prepareStatement(consulta);
            pstmt.setInt(1, aeropuerto.getAeropuerto().getId());

            int filasAfectadas = pstmt.executeUpdate();
            System.out.println("Eliminado con éxito");
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        } finally {
            if (connection != null) {
                connection.CloseConexion();
            }
        }
    }
}

