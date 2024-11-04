package es.aritzherrero.ejerciciol.DAO;

import es.aritzherrero.ejerciciol.Modelo.Direccion;
import es.aritzherrero.ejerciciol.db.ConexionDB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DireccionDAO {

    /**
     * Obtiene una dirección de la base de datos dado su ID.
     *
     * @param id Identificador de la dirección en la base de datos.
     * @return Dirección correspondiente al ID o null si no se encuentra.
     */
    public static Direccion getDireccion(int id) throws SQLException {
        ConexionDB connection = null;
        Direccion direccion = null;
        try {
            connection = new ConexionDB();
            String consulta = "SELECT id, pais, ciudad, calle, numero FROM direcciones WHERE id = ?";
            PreparedStatement pstmt = connection.getConexion().prepareStatement(consulta);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String pais = rs.getString("pais");
                String ciudad = rs.getString("ciudad");
                String calle = rs.getString("calle");
                int numero = rs.getInt("numero");
                direccion = new Direccion(id, pais, ciudad, calle, numero);
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
        return direccion;
    }

    /**
     * Modifica los datos de una dirección en la base de datos.
     *
     * @param direccion Dirección actual que se desea modificar.
     * @param direccionNueva Nuevos datos para actualizar la dirección.
     * @return true si la actualización fue exitosa; false en caso contrario.
     */
    public static boolean modificar(Direccion direccion, Direccion direccionNueva) throws SQLException {
        ConexionDB connection = null;
        try {
            connection = new ConexionDB();
            String consulta = "UPDATE direcciones SET pais = ?, ciudad = ?, calle = ?, numero = ? WHERE id = ?";
            PreparedStatement pstmt = connection.getConexion().prepareStatement(consulta);
            pstmt.setString(1, direccionNueva.getPais());
            pstmt.setString(2, direccionNueva.getCiudad());
            pstmt.setString(3, direccionNueva.getCalle());
            pstmt.setInt(4, direccionNueva.getNumero());
            pstmt.setInt(5, direccion.getId());
            int filasAfectadas = pstmt.executeUpdate();
            pstmt.close();
            System.out.println("Actualizada dirección");
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
     * Inserta una nueva dirección en la base de datos.
     *
     * @param direccion Objeto Dirección que se desea insertar en la base de datos.
     * @return ID de la nueva dirección si la inserción fue exitosa; -1 en caso contrario.
     */
    public static int insertar(Direccion direccion) throws SQLException {
        ConexionDB connection = null;
        try {
            connection = new ConexionDB();
            String consulta = "INSERT INTO direcciones (pais, ciudad, calle, numero) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = connection.getConexion().prepareStatement(consulta, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, direccion.getPais());
            pstmt.setString(2, direccion.getCiudad());
            pstmt.setString(3, direccion.getCalle());
            pstmt.setInt(4, direccion.getNumero());
            int filasAfectadas = pstmt.executeUpdate();
            System.out.println("Nueva entrada en dirección");
            if (filasAfectadas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    rs.close();
                    pstmt.close();
                    return id;
                }
                rs.close();
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
     * Elimina una dirección de la base de datos.
     *
     * @param direccion Objeto Dirección que se desea eliminar de la base de datos.
     * @return true si la eliminación fue exitosa; false en caso contrario.
     */
    public static boolean eliminar(Direccion direccion) throws SQLException {
        ConexionDB connection = null;
        try {
            connection = new ConexionDB();
            String consulta = "DELETE FROM direcciones WHERE id = ?";
            PreparedStatement pstmt = connection.getConexion().prepareStatement(consulta);
            pstmt.setInt(1, direccion.getId());
            int filasAfectadas = pstmt.executeUpdate();
            pstmt.close();
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
