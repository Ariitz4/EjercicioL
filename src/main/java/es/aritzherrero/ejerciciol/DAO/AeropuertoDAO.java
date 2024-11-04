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

/**
 * Data Access Object para gestionar las entidades Aeropuerto en la base de datos.
 */
public class AeropuertoDAO {

    /**
     * Recupera un Aeropuerto por su ID desde la base de datos.
     *
     * @param id el ID del aeropuerto.
     * @return el objeto Aeropuerto si se encuentra, null en caso contrario.
     */
    public static Aeropuerto getAeropuerto(int id) throws SQLException {
        ConexionDB connection = null;  // Conexión a la base de datos
        Aeropuerto aeropuerto = null;   // Variable para almacenar el aeropuerto recuperado

        try {
            connection = new ConexionDB();
            String consulta = "SELECT id, nombre, anio_inauguracion, capacidad, id_direccion, imagen FROM aeropuertos WHERE id = ?";
            PreparedStatement pstmt = connection.getConexion().prepareStatement(consulta);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Creación del objeto Aeropuerto a partir de los resultados
                int idAeropuerto = rs.getInt("id");
                String nombre = rs.getString("nombre");
                int anioInauguracion = rs.getInt("anio_inauguracion");
                int capacidad = rs.getInt("capacidad");
                int idDireccion = rs.getInt("id_direccion");
                Direccion direccion = DireccionDAO.getDireccion(idDireccion);
                Blob imagen = rs.getBlob("imagen");

                aeropuerto = new Aeropuerto(idAeropuerto, nombre, anioInauguracion, capacidad, direccion, imagen);
            }

            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            if (connection != null) {
                connection.CloseConexion(); // Cierre de la conexión en el bloque finally
            }
        }
        return aeropuerto;
    }

    /**
     * Carga una lista de Aeropuertos desde la base de datos.
     *
     * @return una ObservableList de Aeropuerto.
     */
    public static ObservableList<Aeropuerto> cargarListado() throws SQLException {
        ConexionDB connection = null;  // Conexión a la base de datos
        ObservableList<Aeropuerto> airportList = FXCollections.observableArrayList(); // Lista para almacenar aeropuertos

        try {
            connection = new ConexionDB();
            String consulta = "SELECT id, nombre, anio_inauguracion, capacidad, id_direccion, imagen FROM aeropuertos";
            PreparedStatement pstmt = connection.getConexion().prepareStatement(consulta);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Creación del objeto Aeropuerto a partir de los resultados
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                int anioInauguracion = rs.getInt("anio_inauguracion");
                int capacidad = rs.getInt("capacidad");
                int idDireccion = rs.getInt("id_direccion");
                Direccion direccion = DireccionDAO.getDireccion(idDireccion);
                Blob imagen = rs.getBlob("imagen");

                Aeropuerto airport = new Aeropuerto(id, nombre, anioInauguracion, capacidad, direccion, imagen);
                airportList.add(airport); // Añadir el aeropuerto a la lista
            }

            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            if (connection != null) {
                connection.CloseConexion(); // Cierre de la conexión en el bloque finally
            }
        }
        return airportList;
    }

    /**
     * Modifica un Aeropuerto existente en la base de datos.
     *
     * @param aeropuerto      el Aeropuerto existente a actualizar.
     * @param aeropuertoNuevo los nuevos datos del Aeropuerto.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public static boolean modificar(Aeropuerto aeropuerto, Aeropuerto aeropuertoNuevo) throws SQLException {
        ConexionDB connection = null; // Conexión a la base de datos

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

            int filasAfectadas = pstmt.executeUpdate(); // Ejecutar la actualización
            return filasAfectadas > 0; // Retornar si se afectaron filas
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        } finally {
            if (connection != null) {
                connection.CloseConexion(); // Cierre de la conexión en el bloque finally
            }
        }
    }

    /**
     * Inserta un nuevo Aeropuerto en la base de datos.
     *
     * @param aeropuerto el Aeropuerto a insertar.
     * @return el ID generado del nuevo Aeropuerto, o -1 si la inserción falló.
     */
    public static int insertar(Aeropuerto aeropuerto) throws SQLException {
        ConexionDB connection = null; // Conexión a la base de datos

        try {
            connection = new ConexionDB();
            String consulta = "INSERT INTO aeropuertos (nombre, anio_inauguracion, capacidad, id_direccion, imagen) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = connection.getConexion().prepareStatement(consulta, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, aeropuerto.getNombre());
            pstmt.setInt(2, aeropuerto.getAnio_inauguracion());
            pstmt.setInt(3, aeropuerto.getCapacidad());
            pstmt.setInt(4, aeropuerto.getDireccion().getId());
            pstmt.setBlob(5, aeropuerto.getImagen());

            int filasAfectadas = pstmt.executeUpdate(); // Ejecutar la inserción
            if (filasAfectadas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // Retornar el ID generado
                }
            }
            return -1; // Retornar -1 si la inserción falló
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return -1;
        } finally {
            if (connection != null) {
                connection.CloseConexion(); // Cierre de la conexión en el bloque finally
            }
        }
    }

    /**
     * Elimina un Aeropuerto de la base de datos.
     *
     * @param aeropuerto el Aeropuerto a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public static boolean eliminar(Aeropuerto aeropuerto) throws SQLException {
        ConexionDB connection = null; // Conexión a la base de datos

        try {
            connection = new ConexionDB();
            String consulta = "DELETE FROM aeropuertos WHERE id = ?";
            PreparedStatement pstmt = connection.getConexion().prepareStatement(consulta);
            pstmt.setInt(1, aeropuerto.getId());

            int filasAfectadas = pstmt.executeUpdate(); // Ejecutar la eliminación
            return filasAfectadas > 0; // Retornar si se afectaron filas
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        } finally {
            if (connection != null) {
                connection.CloseConexion(); // Cierre de la conexión en el bloque finally
            }
        }
    }
}


