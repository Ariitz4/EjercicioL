package es.aritzherrero.ejerciciol.DAO;

import es.aritzherrero.ejerciciol.Modelo.Aeropuerto;
import es.aritzherrero.ejerciciol.Modelo.AeropuertoPrivado;
import es.aritzherrero.ejerciciol.Modelo.Direccion;
import es.aritzherrero.ejerciciol.db.ConexionDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AeropuertoPrivadoDAO {

    /**
     * Obtiene un aeropuerto privado de la base de datos dado su ID.
     *
     * @param id Identificador del aeropuerto en la base de datos.
     * @return AeropuertoPrivado correspondiente al ID o null si no se encuentra.
     */
    public static AeropuertoPrivado getAeropuerto(int id) throws SQLException {
        ConexionDB connection = null;
        AeropuertoPrivado aeropuerto = null;
        try {
            connection = new ConexionDB();
            String consulta = "SELECT id,nombre,anio_inauguracion,capacidad,id_direccion,imagen,numero_socios FROM aeropuertos,aeropuertos_privados WHERE id=id_aeropuerto AND id = ?";
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
                Aeropuerto airport = new Aeropuerto(id, nombre, anio_inauguracion, capacidad, direccion, imagen);
                int numero_socios = rs.getInt("numero_socios");
                aeropuerto = new AeropuertoPrivado(airport, numero_socios);
            }
            rs.close();
            pstmt.close();
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
     * Carga un listado de aeropuertos privados desde la base de datos.
     *
     * @return ObservableList con todos los aeropuertos privados.
     */
    public static ObservableList<AeropuertoPrivado> cargarListado() throws SQLException {
        ConexionDB connection = null;
        ObservableList<AeropuertoPrivado> airportList = FXCollections.observableArrayList();
        try {
            connection = new ConexionDB();
            String consulta = "SELECT id,nombre,anio_inauguracion,capacidad,id_direccion,imagen,numero_socios FROM aeropuertos,aeropuertos_privados WHERE id=id_aeropuerto";
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
                int numero_socios = rs.getInt("numero_socios");
                AeropuertoPrivado airport = new AeropuertoPrivado(aeropuerto, numero_socios);
                airportList.add(airport);
            }
            rs.close();
            pstmt.close();
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
     * Modifica los datos de un aeropuerto privado en la base de datos.
     *
     * @param aeropuerto     AeropuertoPrivado actual que se desea modificar.
     * @param aeropuertoNuevo Nuevos datos para actualizar el aeropuerto privado.
     * @return true si la actualización fue exitosa; false en caso contrario.
     */
    public static boolean modificar(AeropuertoPrivado aeropuerto, AeropuertoPrivado aeropuertoNuevo) {
        ConexionDB connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = new ConexionDB();
            String consulta = "UPDATE aeropuertos_privados SET numero_socios = ? WHERE id_aeropuerto = ?";
            pstmt = connection.getConexion().prepareStatement(consulta);
            pstmt.setInt(1, aeropuertoNuevo.getNumero_socios());
            pstmt.setInt(2, aeropuerto.getAeropuerto().getId());
            int filasAfectadas = pstmt.executeUpdate();
            System.out.println("Actualizado aeropuerto");
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (connection != null) {
                    connection.CloseConexion();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * Inserta un nuevo aeropuerto privado en la base de datos.
     *
     * @param aeropuerto Objeto AeropuertoPrivado que se desea insertar.
     * @return true si la inserción fue exitosa; false en caso contrario.
     */
    public static boolean insertar(AeropuertoPrivado aeropuerto) {
        ConexionDB connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = new ConexionDB();
            String consulta = "INSERT INTO aeropuertos_privados (id_aeropuerto,numero_socios) VALUES (?,?) ";
            pstmt = connection.getConexion().prepareStatement(consulta);
            pstmt.setInt(1, aeropuerto.getAeropuerto().getId());
            pstmt.setInt(2, aeropuerto.getNumero_socios());
            int filasAfectadas = pstmt.executeUpdate();
            System.out.println("Nueva entrada en aeropuertos_privados");
            return (filasAfectadas > 0);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (connection != null) {
                    connection.CloseConexion();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * Elimina un aeropuerto privado de la base de datos.
     *
     * @param aeropuerto Objeto AeropuertoPrivado que se desea eliminar.
     * @return true si la eliminación fue exitosa; false en caso contrario.
     */
    public static boolean eliminar(AeropuertoPrivado aeropuerto) {
        ConexionDB connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = new ConexionDB();
            String consulta = "DELETE FROM aeropuertos_privados WHERE (id_aeropuerto = ?)";
            pstmt = connection.getConexion().prepareStatement(consulta);
            pstmt.setInt(1, aeropuerto.getAeropuerto().getId());
            int filasAfectadas = pstmt.executeUpdate();
            System.out.println("Eliminado con éxito");
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (connection != null) {
                    connection.CloseConexion();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}

