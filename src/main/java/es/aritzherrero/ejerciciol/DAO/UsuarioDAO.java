package es.aritzherrero.ejerciciol.DAO;

import es.aritzherrero.ejerciciol.Modelo.Usuario;
import es.aritzherrero.ejerciciol.db.ConexionDB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public static Usuario getUsuario(String usuario) {
        ConexionDB connection;
        Usuario user = null;
        try {
            connection = new ConexionDB();
            String consulta = "SELECT usuario,password FROM usuarios WHERE usuario = ?";
            PreparedStatement pstmt = connection.getConexion().prepareStatement(consulta);
            pstmt.setString(1, usuario);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String nom_usuario = rs.getString("usuario");
                String password = rs.getString("contraseña");
                user = new Usuario(nom_usuario, password);
            }
            rs.close();
            connection.CloseConexion();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return user;
    }
}
