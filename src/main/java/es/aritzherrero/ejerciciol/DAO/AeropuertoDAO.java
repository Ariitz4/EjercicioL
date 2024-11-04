package es.aritzherrero.ejerciciol.DAO;

import es.aritzherrero.ejerciciol.Modelo.Aeropuerto;
import es.aritzherrero.ejerciciol.Modelo.Direccion;
import es.aritzherrero.ejerciciol.db.ConexionDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AeropuertoDAO {

    /**
     * Obtiene un aeropuerto a partir de su ID.
     *
     * @param id ID del aeropuerto a buscar.
     * @return Objeto Aeropuerto si se encuentra en la base de datos, o null si no.
     */
    public static Aeropuerto getAeropuerto(int id) throws SQLException{
        ConexionDB connection = null;
        Aeropuerto aeropuerto = null;
        try {
            connection = new ConexionDB();
            String consulta = "SELECT id, nombre, anio_inauguracion, capacidad, id_direccion, imagen FROM aeropuertos WHERE id = ?";
            PreparedStatement pstmt = connection.getConexion().prepareStatement(consulta);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                aeropuerto = new Aeropuerto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getInt("anio_inauguracion"),
                        rs.getInt("capacidad"),
                        DireccionDAO.getDireccion(rs.getInt("id_direccion")),
                        rs.getBlob("imagen")
                );
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage()); // Manejo de error SQL
        } finally {
            if (connection != null) {
                connection.CloseConexion();
            }
        }
        return aeropuerto;
    }

    /**
     * Carga un listado completo de aeropuertos desde la base de datos.
     *
     * @return ObservableList con todos los aeropuertos.
     */
    public static ObservableList<Aeropuerto> cargarListado() throws SQLException {
        ConexionDB connection = null;
        ObservableList<Aeropuerto> airportList = FXCollections.observableArrayList();
        try {
            connection = new ConexionDB();
            String consulta = "SELECT id, nombre, anio_inauguracion, capacidad, id_direccion, imagen FROM aeropuertos";
            PreparedStatement pstmt = connection.getConexion().prepareStatement(consulta);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                airportList.add(new Aeropuerto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getInt("anio_inauguracion"),
                        rs.getInt("capacidad"),
                        DireccionDAO.getDireccion(rs.getInt("id_direccion")),
                        rs.getBlob("imagen")
                ));
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage()); // Manejo de error SQL
        } finally {
            if (connection != null) {
                connection.CloseConexion();
            }
        }
        return airportList;
    }

    /**
     * Modifica los datos de un aeropuerto existente en la base de datos.
     *
     * @param aeropuerto Objeto Aeropuerto actual.
     * @param aeropuertoNuevo Objeto Aeropuerto con los nuevos datos.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public static boolean modificar(Aeropuerto aeropuerto, Aeropuerto aeropuertoNuevo) throws SQLException {
        ConexionDB connection = null;
        try {
            connection = new ConexionDB();
            String consulta = "UPDATE aeropuertos SET nombre = ?, anio_inauguracion = ?, capacidad = ?, id_direccion = ?, imagen = ? WHERE id = ?";
            PreparedStatement pstmt = connection.getConexion().prepareStatement(consulta);
            pstmt.setString(1, aeropuertoNuevo.getNombre());
            pstmt.setInt(2, aeropuertoNuevo.getAnio_inauguracion());
            pstmt.setInt(3, aeropuertoNuevo.getCapacidad());
            pstmt.setInt(4, aeropuertoNuevo.getDireccion().getId());
            pstmt.setBlob(5, aeropuertoNuevo.getImagen());
            pstmt.setInt(6, aeropuerto.getId());

            boolean updated = pstmt.executeUpdate() > 0;
            System.out.println("Actualizada aeropuerto"); // Confirmación en consola
            pstmt.close();
            return updated;
        } catch (SQLException e) {
            System.err.println(e.getMessage()); // Manejo de error SQL
            return false;
        } finally {
            if (connection != null) {
                connection.CloseConexion();
            }
        }
    }

    /**
     * Inserta un nuevo aeropuerto en la base de datos.
     *
     * @param aeropuerto Objeto Aeropuerto a insertar.
     * @return ID del aeropuerto insertado o -1 en caso de error.
     */
    public static int insertar(Aeropuerto aeropuerto) throws SQLException {
        ConexionDB connection = null;
        try {
            connection = new ConexionDB();
            String consulta = "INSERT INTO aeropuertos (nombre, anio_inauguracion, capacidad, id_direccion, imagen) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = connection.getConexion().prepareStatement(consulta, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, aeropuerto.getNombre());
            pstmt.setInt(2, aeropuerto.getAnio_inauguracion());
            pstmt.setInt(3, aeropuerto.getCapacidad());
            pstmt.setInt(4, aeropuerto.getDireccion().getId());
            pstmt.setBlob(5, aeropuerto.getImagen());

            int filasAfectadas = pstmt.executeUpdate();
            System.out.println("Nueva entrada en aeropuerto"); // Confirmación en consola

            if (filasAfectadas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    rs.close();
                    pstmt.close();
                    return id;
                }
            }
            pstmt.close();
            return -1;
        } catch (SQLException e) {
            System.err.println(e.getMessage()); // Manejo de error SQL
            return -1;
        } finally {
            if (connection != null) {
                connection.CloseConexion();
            }
        }
    }

    /**
     * Elimina un aeropuerto de la base de datos.
     *
     * @param aeropuerto Objeto Aeropuerto a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public static boolean eliminar(Aeropuerto aeropuerto) throws SQLException {
        ConexionDB connection = null;
        try {
            connection = new ConexionDB();
            String consulta = "DELETE FROM aeropuertos WHERE id = ?";
            PreparedStatement pstmt = connection.getConexion().prepareStatement(consulta);
            pstmt.setInt(1, aeropuerto.getId());

            boolean deleted = pstmt.executeUpdate() > 0;
            pstmt.close();
            System.out.println("Eliminado con éxito"); // Confirmación en consola
            return deleted;
        } catch (SQLException e) {
            System.err.println(e.getMessage()); // Manejo de error SQL
            return false;
        } finally {
            if (connection != null) {
                connection.CloseConexion();
            }
        }
    }
}
