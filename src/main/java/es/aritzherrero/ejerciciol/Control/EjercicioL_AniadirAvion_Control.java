package es.aritzherrero.ejerciciol.Control;

import es.aritzherrero.ejerciciol.DAO.AeropuertoDAO;
import es.aritzherrero.ejerciciol.DAO.AvionDAO;
import es.aritzherrero.ejerciciol.Modelo.Aeropuerto;
import es.aritzherrero.ejerciciol.Modelo.Avion;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Controlador para la interfaz de añadir un nuevo avión a un aeropuerto.
 * Esta clase maneja la interacción del usuario en el formulario de añadir avión.
 */
public class EjercicioL_AniadirAvion_Control implements Initializable {

    @FXML
    private ComboBox<Aeropuerto> comboAeropuerto; // ComboBox para seleccionar el aeropuerto
    @FXML
    private RadioButton radioActivado; // Opción para indicar que el avión está activado
    @FXML
    private RadioButton radioDesactivado; // Opción para indicar que el avión está desactivado
    @FXML
    private ToggleGroup grupoRadios; // Grupo para manejar las opciones de radio
    @FXML
    private TextField campoAsientos; // Campo de texto para el número de asientos del avión
    @FXML
    private TextField campoModelos; // Campo de texto para el modelo del avión
    @FXML
    private TextField campoVelMax; // Campo de texto para la velocidad máxima del avión

    /**
     * Inicializa el controlador. Carga la lista de aeropuertos en el ComboBox.
     *
     * @param location  URL para localizar recursos.
     * @param resources El conjunto de recursos que contiene el controlador.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<Aeropuerto> listaAeropuertos;
        try {
            listaAeropuertos = AeropuertoDAO.cargarListado(); // Cargar lista de aeropuertos desde la base de datos
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        comboAeropuerto.setItems(listaAeropuertos); // Asignar la lista de aeropuertos al ComboBox
        comboAeropuerto.getSelectionModel().select(0); // Seleccionar el primer aeropuerto por defecto
    }

    /**
     * Cancela la operación y cierra la ventana actual.
     *
     * @param event Evento de acción al hacer clic en el botón de cancelar.
     */
    @FXML
    void cancelar(ActionEvent event) {
        Stage ventana = (Stage) campoAsientos.getScene().getWindow(); // Obtener la ventana actual
        ventana.close(); // Cerrar la ventana
    }

    /**
     * Guarda la información del nuevo avión ingresada por el usuario.
     *
     * @param event Evento de acción al hacer clic en el botón de guardar.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    @FXML
    void guardar(ActionEvent event) throws SQLException {
        String mensajeError = "";
        int cantidadAsientos = 0;
        int velocidadMaxima = 0;

        // Validar el modelo del avión
        if (campoModelos.getText().isEmpty()) {
            mensajeError = "El modelo del avión no puede estar vacío.";
        }

        // Validar el número de asientos
        if (campoAsientos.getText().isEmpty()) {
            mensajeError += mensajeError.isEmpty() ? "" : "\n";
            mensajeError += "El número de asientos no puede estar vacío.";
        } else {
            try {
                cantidadAsientos = Integer.parseInt(campoAsientos.getText());
            } catch (NumberFormatException e) {
                mensajeError += mensajeError.isEmpty() ? "" : "\n";
                mensajeError += "El número de asientos debe ser un entero.";
            }
        }

        // Validar la velocidad máxima
        if (campoVelMax.getText().isEmpty()) {
            mensajeError += mensajeError.isEmpty() ? "" : "\n";
            mensajeError += "La velocidad máxima no puede estar vacía.";
        } else {
            try {
                velocidadMaxima = Integer.parseInt(campoVelMax.getText());
            } catch (NumberFormatException e) {
                mensajeError += mensajeError.isEmpty() ? "" : "\n";
                mensajeError += "La velocidad máxima debe ser un entero.";
            }
        }

        // Si hay errores de validación, mostrar alerta
        if (!mensajeError.isEmpty()) {
            mostrarAlerta(mensajeError);
        } else {
            // Crear un nuevo objeto avión y establecer sus propiedades
            Avion nuevoAvion = new Avion();
            nuevoAvion.setModelo(campoModelos.getText());
            nuevoAvion.setNumero_asientos(cantidadAsientos);
            nuevoAvion.setVelocidad_maxima(velocidadMaxima);
            nuevoAvion.setActivado(radioActivado.isSelected());
            nuevoAvion.setAeropuerto(comboAeropuerto.getSelectionModel().getSelectedItem());

            // Verificar si el avión ya existe en el aeropuerto
            ObservableList<Avion> listaAviones = AvionDAO.cargarListado();
            if (listaAviones.contains(nuevoAvion)) {
                mostrarAlerta("Este modelo ya existe en el aeropuerto. Elige otro modelo u otro aeropuerto.");
            } else {
                // Intentar insertar el nuevo avión en la base de datos
                int resultadoInsercion = AvionDAO.insertar(nuevoAvion);
                if (resultadoInsercion == -1) {
                    mostrarAlerta("Error al insertar el avión. Inténtalo de nuevo.");
                } else {
                    mostrarConfirmacion("¡Avión insertado correctamente!"); // Confirmar inserción exitosa
                    Stage ventana = (Stage) campoAsientos.getScene().getWindow(); // Cerrar la ventana actual
                    ventana.close();
                }
            }
        }
    }

    /**
     * Muestra una alerta con un mensaje de error.
     *
     * @param mensaje El mensaje de error a mostrar.
     */
    public void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Muestra una alerta de confirmación.
     *
     * @param mensaje El mensaje de confirmación a mostrar.
     */
    public void mostrarConfirmacion(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
