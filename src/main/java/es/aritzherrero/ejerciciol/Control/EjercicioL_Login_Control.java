package es.aritzherrero.ejerciciol.Control;

import es.aritzherrero.ejerciciol.DAO.UsuarioDAO;
import es.aritzherrero.ejerciciol.Modelo.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controlador para el formulario de inicio de sesión.
 * Gestiona la validación de usuario y contraseña y la carga de la siguiente ventana.
 */
public class EjercicioL_Login_Control {

    @FXML
    private TextField txtPassword; // Campo de texto para la contraseña

    @FXML
    private TextField txtUsuario; // Campo de texto para el nombre de usuario

    /**
     * Maneja el evento de inicio de sesión. Valida los campos y verifica las credenciales del usuario.
     *
     * @param event El evento de acción que desencadena el inicio de sesión.
     */
    @FXML
    public void login(ActionEvent event) {
        // Obtener datos de usuario y contraseña
        String usuario = txtUsuario.getText();
        String password = txtPassword.getText();

        // Validación de campos vacíos
        String error = validarCampos(usuario, password);
        if (!error.isEmpty()) {
            mostrarAlerta(error);
            return;
        }

        // Verificar usuario en la base de datos
        Usuario user = UsuarioDAO.getUsuario(usuario);
        if (user == null) {
            manejoUsuarioNoValido();
        } else if (!password.equals(user.getPassword())) {
            manejoContraseniaIncorrecta();
        } else {
            manejoLogin();
        }
    }

    /**
     * Valida que los campos de usuario y contraseña no estén vacíos.
     *
     * @param usuario Nombre de usuario ingresado.
     * @param password Contraseña ingresada.
     * @return Mensaje de error si hay campos vacíos, o cadena vacía si no hay errores.
     */
    private String validarCampos(String usuario, String password) {
        StringBuilder error = new StringBuilder();
        if (usuario.isBlank()) {
            error.append("El campo usuario no puede estar vacío.");
        }
        if (password.isEmpty()) {
            if (error.length() > 0) error.append("\n");
            error.append("El campo password no puede estar vacío.");
        }
        return error.toString();
    }

    /**
     * Muestra una alerta de error cuando el usuario no es válido.
     * Reinicia los campos de usuario y contraseña.
     */
    private void manejoUsuarioNoValido() {
        mostrarAlerta("Usuario no válido");
        txtUsuario.clear();
        txtPassword.clear();
    }

    /**
     * Muestra una alerta de error cuando la contraseña es incorrecta.
     * Limpia el campo de contraseña.
     */
    private void manejoContraseniaIncorrecta() {
        mostrarAlerta("Contraseña incorrecta");
        txtPassword.clear();
    }

    /**
     * Muestra la siguiente ventana y cierra la ventana actual al iniciar sesión correctamente.
     */
    private void manejoLogin() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Aeropuertos.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("AVIONES - AEROPUERTOS");
            stage.show();

            // Cerrar ventana de login actual
            ((Stage) txtUsuario.getScene().getWindow()).close();
        } catch (IOException e) {
            System.err.println("Error al abrir la nueva ventana: " + e.getMessage());
            mostrarAlerta("Error abriendo ventana, por favor inténtelo de nuevo");
        }
    }

    /**
     * Muestra una alerta con el mensaje de error especificado.
     *
     * @param mensaje El mensaje de error a mostrar en la alerta.
     */
    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setHeaderText(null);
        alerta.setTitle("ERROR");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}