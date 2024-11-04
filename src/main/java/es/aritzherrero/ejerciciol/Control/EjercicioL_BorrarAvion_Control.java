package es.aritzherrero.ejerciciol.Control;

import es.aritzherrero.ejerciciol.DAO.AeropuertoDAO;
import es.aritzherrero.ejerciciol.DAO.AvionDAO;
import es.aritzherrero.ejerciciol.Modelo.Aeropuerto;
import es.aritzherrero.ejerciciol.Modelo.Avion;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Controlador para la interfaz de eliminar un avión de un aeropuerto.
 * Esta clase maneja la interacción del usuario para seleccionar un aeropuerto y un avión, y realizar la eliminación.
 */
public class EjercicioL_BorrarAvion_Control implements Initializable {

    @FXML
    private ComboBox<Aeropuerto> cbAeropuerto; // ComboBox para seleccionar el aeropuerto
    @FXML
    private ComboBox<Avion> cbAvion; // ComboBox para seleccionar el avión

    /**
     * Inicializa el controlador. Carga la lista de aeropuertos en el ComboBox
     * y establece un listener para actualizar los aviones al cambiar de aeropuerto.
     *
     * @param url La URL para localizar recursos.
     * @param resourceBundle El conjunto de recursos que contiene el controlador.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<Aeropuerto> aeropuertos;
        try {
            aeropuertos = AeropuertoDAO.cargarListado(); // Cargar la lista de aeropuertos desde la base de datos
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        cbAeropuerto.setItems(aeropuertos); // Asignar la lista de aeropuertos al ComboBox
        cbAeropuerto.getSelectionModel().select(0); // Seleccionar el primer aeropuerto por defecto
        cbAeropuerto.valueProperty().addListener(new ChangeListener<Aeropuerto>() {
            @Override
            public void changed(ObservableValue<? extends Aeropuerto> observableValue, Aeropuerto oldValue, Aeropuerto newValue) {
                try {
                    cambioAeropuerto(newValue); // Cambiar aviones al seleccionar un nuevo aeropuerto
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        try {
            cambioAeropuerto(cbAeropuerto.getSelectionModel().getSelectedItem()); // Inicializar la lista de aviones
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Actualiza la lista de aviones en función del aeropuerto seleccionado.
     *
     * @param aeropuerto El aeropuerto seleccionado.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    public void cambioAeropuerto(Aeropuerto aeropuerto) throws SQLException {
        if (aeropuerto != null) {
            ObservableList<Avion> aviones = AvionDAO.cargarListado(aeropuerto); // Cargar la lista de aviones del aeropuerto
            cbAvion.setItems(aviones); // Asignar la lista de aviones al ComboBox
            cbAvion.getSelectionModel().select(0); // Seleccionar el primer avión por defecto
        }
    }

    /**
     * Cancela la operación y cierra la ventana actual.
     *
     * @param event Evento de acción al hacer clic en el botón de cancelar.
     */
    @FXML
    void cancelar(ActionEvent event) {
        Stage stage = (Stage) cbAeropuerto.getScene().getWindow(); // Obtener la ventana actual
        stage.close(); // Cerrar la ventana
    }

    /**
     * Elimina el avión seleccionado y muestra una confirmación o error.
     *
     * @param event Evento de acción al hacer clic en el botón de guardar.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    @FXML
    void guardar(ActionEvent event) throws SQLException {
        Avion avion = cbAvion.getSelectionModel().getSelectedItem(); // Obtener el avión seleccionado
        boolean resultado = AvionDAO.eliminar(avion); // Intentar eliminar el avión de la base de datos
        if (resultado) {
            confirmacion("Avión eliminado correctamente"); // Mostrar confirmación de eliminación
            Stage stage = (Stage) cbAeropuerto.getScene().getWindow(); // Cerrar la ventana actual
            stage.close();
        } else {
            alerta("Ha habido un error eliminando el avión. Por favor inténtelo de nuevo"); // Mostrar alerta de error
        }
    }

    /**
     * Muestra una alerta con un mensaje de error.
     *
     * @param texto El mensaje de error a mostrar.
     */
    public void alerta(String texto) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setHeaderText(null);
        alerta.setTitle("ERROR");
        alerta.setContentText(texto);
        alerta.showAndWait();
    }

    /**
     * Muestra una alerta de confirmación.
     *
     * @param texto El mensaje de confirmación a mostrar.
     */
    public void confirmacion(String texto) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.setTitle("Info");
        alerta.setContentText(texto);
        alerta.showAndWait();
    }
}
