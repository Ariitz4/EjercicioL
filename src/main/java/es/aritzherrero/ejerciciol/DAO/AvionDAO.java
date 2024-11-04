package es.aritzherrero.ejerciciol.DAO;

import es.aritzherrero.ejerciciol.Modelo.Aeropuerto;
import es.aritzherrero.ejerciciol.Modelo.Avion;
import es.aritzherrero.ejerciciol.db.ConexionDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AvionDAO {

    /**
     * Obtiene un avión de la base de datos dado su ID.
     *
     * @param id el identificador del avión.
     * @return el objeto Avion correspondiente al ID, o null si no se encuentra.
     */
    public static Avion getAvion(int id) throws SQLException {
        ConexionDB connection = null;
        Avion avion = null;
        try {
            connection = new ConexionDB();
            String consulta = "SELECT id,modelo,numero_asientos,velocidad_maxima,activado,id_aeropuerto FROM aviones WHERE id = ?";
            PreparedStatement pstmt = connection.getConexion().prepareStatement(consulta);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Extrae los datos del avión y crea una instancia de Avion
                int id_avion = rs.getInt("id");
                String modelo = rs.getString("modelo");
                int numero_asientos = rs.getInt("numero_asientos");
                int velocidad_maxima = rs.getInt("velocidad_maxima");
                boolean activado = rs.getBoolean("activado");
                int id_aeropuerto = rs.getInt("id_aeropuerto");
                Aeropuerto aeropuerto = AeropuertoDAO.getAeropuerto(id_aeropuerto);
                avion = new Avion(id_avion, modelo, numero_asientos, velocidad_maxima, activado, aeropuerto);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            if (connection != null) {
                connection.CloseConexion();
            }
        }
        return avion;
    }

    /**
     * Carga una lista de aviones asociados a un aeropuerto específico.
     *
     * @param aeropuerto el aeropuerto cuyos aviones se desean cargar.
     * @return una lista observable de aviones.
     */
    public static ObservableList<Avion> cargarListado(Aeropuerto aeropuerto) throws SQLException {
        ConexionDB connection = null;
        ObservableList<Avion> airplaneList = FXCollections.observableArrayList();
        try {
            connection = new ConexionDB();
            String consulta = "SELECT id,modelo,numero_asientos,velocidad_maxima,activado,id_aeropuerto FROM aviones WHERE id_aeropuerto = ?";
            PreparedStatement pstmt = connection.getConexion().prepareStatement(consulta);
            pstmt.setInt(1, aeropuerto.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // Extrae los datos y crea un objeto Avion por cada fila
                int id = rs.getInt("id");
                String modelo = rs.getString("modelo");
                int numero_asientos = rs.getInt("numero_asientos");
                int velocidad_maxima = rs.getInt("velocidad_maxima");
                boolean activado = rs.getBoolean("activado");
                int id_aeropuerto = rs.getInt("id_aeropuerto");
                Aeropuerto aeropuerto_db = AeropuertoDAO.getAeropuerto(id_aeropuerto);
                Avion avion = new Avion(id, modelo, numero_asientos, velocidad_maxima, activado, aeropuerto_db);
                airplaneList.add(avion);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            if (connection != null) {
                connection.CloseConexion();
            }
        }
        return airplaneList;
    }

    /**
     * Carga una lista completa de todos los aviones en la base de datos.
     *
     * @return una lista observable de todos los aviones.
     */
    public static ObservableList<Avion> cargarListado()  throws SQLException{
        ConexionDB connection = null;
        ObservableList<Avion> airplaneList = FXCollections.observableArrayList();
        try {
            connection = new ConexionDB();
            String consulta = "SELECT id,modelo,numero_asientos,velocidad_maxima,activado,id_aeropuerto FROM aviones";
            PreparedStatement pstmt = connection.getConexion().prepareStatement(consulta);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // Extrae los datos y crea un objeto Avion por cada fila
                int id = rs.getInt("id");
                String modelo = rs.getString("modelo");
                int numero_asientos = rs.getInt("numero_asientos");
                int velocidad_maxima = rs.getInt("velocidad_maxima");
                boolean activado = rs.getBoolean("activado");
                int id_aeropuerto = rs.getInt("id_aeropuerto");
                Aeropuerto aeropuerto = AeropuertoDAO.getAeropuerto(id_aeropuerto);
                Avion avion = new Avion(id, modelo, numero_asientos, velocidad_maxima, activado, aeropuerto);
                airplaneList.add(avion);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            if (connection != null) {
                connection.CloseConexion();
            }
        }
        return airplaneList;
    }

    /**
     * Modifica la información de un avión en la base de datos.
     *
     * @param avion el avión a modificar.
     * @param avionNuevo los nuevos datos del avión.
     * @return true si se actualizó exitosamente, false en caso contrario.
     */
    public static boolean modificar(Avion avion, Avion avionNuevo)  throws SQLException{
        ConexionDB connection = null;
        try {
            connection = new ConexionDB();
            String consulta = "UPDATE aviones SET modelo = ?, numero_asientos = ?, velocidad_maxima = ?, activado = ?, id_aeropuerto = ? WHERE id = ?";
            PreparedStatement pstmt = connection.getConexion().prepareStatement(consulta);
            pstmt.setString(1, avionNuevo.getModelo());
            pstmt.setInt(2, avionNuevo.getNumero_asientos());
            pstmt.setInt(3, avionNuevo.getVelocidad_maxima());
            pstmt.setBoolean(4, avionNuevo.isActivado());
            pstmt.setInt(5, avionNuevo.getAeropuerto().getId());
            pstmt.setInt(6, avion.getId());
            int filasAfectadas = pstmt.executeUpdate();
            pstmt.close();
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
     * Inserta un nuevo avión en la base de datos.
     *
     * @param avion el avión a insertar.
     * @return el ID del avión insertado, o -1 si la inserción falla.
     */
    public static int insertar(Avion avion) throws SQLException {
        ConexionDB connection = null;
        try {
            connection = new ConexionDB();
            String consulta = "INSERT INTO aviones (modelo, numero_asientos, velocidad_maxima, activado, id_aeropuerto) VALUES (?,?,?,?,?)";
            PreparedStatement pstmt = connection.getConexion().prepareStatement(consulta, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, avion.getModelo());
            pstmt.setInt(2, avion.getNumero_asientos());
            pstmt.setInt(3, avion.getVelocidad_maxima());
            pstmt.setBoolean(4, avion.isActivado());
            pstmt.setInt(5, avion.getAeropuerto().getId());
            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);  // Retorna el ID generado
                }
            }
            pstmt.close();
            return -1;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return -1;
        } finally {
            if (connection != null) {
                connection.CloseConexion();
            }
        }
    }

    /**
     * Elimina un avión de la base de datos.
     *
     * @param avion el avión a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public static boolean eliminar(Avion avion) throws SQLException {
        ConexionDB connection = null;
        try {
            connection = new ConexionDB();
            String consulta = "DELETE FROM aviones WHERE id = ?";
            PreparedStatement pstmt = connection.getConexion().prepareStatement(consulta);
            pstmt.setInt(1, avion.getId());
            int filasAfectadas = pstmt.executeUpdate();
            pstmt.close();
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
     * Elimina todos los aviones asociados a un aeropuerto específico.
     *
     * @param aeropuerto el aeropuerto cuyos aviones se deben eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public static boolean eliminarPorAeropuerto(Aeropuerto aeropuerto) throws SQLException {
        ConexionDB connection = null;
        try {
            connection = new ConexionDB();
            String consulta = "DELETE FROM aviones WHERE id_aeropuerto = ?";
            PreparedStatement pstmt = connection.getConexion().prepareStatement(consulta);
            pstmt.setInt(1, aeropuerto.getId());
            int filasAfectadas = pstmt.executeUpdate();
            pstmt.close();
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
