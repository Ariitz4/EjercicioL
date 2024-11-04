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
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Controlador para la interfaz de modificación del estado (activado/desactivado) de un avión.
 * Esta clase maneja la interacción del usuario para seleccionar un aeropuerto y un avión,
 * y para modificar el estado del avión seleccionado.
 */
public class EjercicioL_AvionActivo_Control implements Initializable {

    @FXML
    private ComboBox<Aeropuerto> cbAeropuerto; // ComboBox para seleccionar el aeropuerto
    @FXML
    private ComboBox<Avion> cbAvion; // ComboBox para seleccionar el avión
    @FXML
    private RadioButton rbActivado; // RadioButton para indicar que el avión está activado
    @FXML
    private RadioButton rbDesactivado; // RadioButton para indicar que el avión está desactivado
    @FXML
    private ToggleGroup rbGroup; // Grupo de Toggle para los RadioButtons

    /**
     * Inicializa el controlador. Carga la lista de aeropuertos y establece listeners
     * para actualizar la lista de aviones y el estado del avión seleccionado.
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
                    cambioAeropuerto(newValue); // Cambiar la lista de aviones al seleccionar un nuevo aeropuerto
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

        cbAvion.valueProperty().addListener(new ChangeListener<Avion>() {
            @Override
            public void changed(ObservableValue<? extends Avion> observableValue, Avion oldValue, Avion newValue) {
                cambioAvion(newValue); // Cambiar el estado del avión al seleccionar un nuevo avión
            }
        });
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
     * Actualiza el estado del avión en función del avión seleccionado.
     *
     * @param avion El avión seleccionado.
     */
    public void cambioAvion(Avion avion) {
        if (avion != null) {
            boolean activado = avion.isActivado(); // Verificar si el avión está activado
            rbActivado.setSelected(activado); // Seleccionar el RadioButton correspondiente
            rbDesactivado.setSelected(!activado); // Seleccionar el RadioButton opuesto
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
     * Guarda los cambios realizados en el estado del avión seleccionado y muestra una
     * confirmación o error.
     *
     * @param event Evento de acción al hacer clic en el botón de guardar.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    @FXML
    void guardar(ActionEvent event) throws SQLException {
        boolean activado = rbActivado.isSelected(); // Obtener el estado del avión
        Avion avion = cbAvion.getSelectionModel().getSelectedItem(); // Obtener el avión seleccionado
        Avion avionNuevo = new Avion(avion.getId(), avion.getModelo(), avion.getNumero_asientos(),
                avion.getVelocidad_maxima(), activado, avion.getAeropuerto()); // Crear un nuevo objeto Avion
        boolean resultado = AvionDAO.modificar(avion, avionNuevo); // Intentar modificar el avión en la base de datos

        if (resultado) {
            confirmacion("Avión modificado correctamente"); // Mostrar confirmación de modificación
            Stage stage = (Stage) cbAeropuerto.getScene().getWindow(); // Cerrar la ventana actual
            stage.close();
        } else {
            alerta("Ha habido un error actualizando el avión. Por favor, inténtalo de nuevo"); // Mostrar alerta de error
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
