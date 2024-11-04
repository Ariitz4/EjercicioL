package es.aritzherrero.ejerciciol.Control;
import es.aritzherrero.ejerciciol.DAO.AvionDAO;
import es.aritzherrero.ejerciciol.Modelo.Aeropuerto;
import es.aritzherrero.ejerciciol.Modelo.AeropuertoPrivado;
import es.aritzherrero.ejerciciol.Modelo.AeropuertoPublico;
import es.aritzherrero.ejerciciol.Modelo.Avion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controlador de la aplicación de gestión de aeropuertos.
 */
public class EjercicioL_Aeropuerto_Control implements Initializable {
    @FXML private MenuItem menuBorrarAeropuerto;
    @FXML private ToggleGroup rbGroup;
    @FXML private RadioButton rbPublicos;
    @FXML private RadioButton rbPrivados;
    @FXML private TextField filtroNombre;
    @FXML private MenuItem menuEditarAeropuerto;
    @FXML private MenuItem menuInfoAeropuerto;
    @FXML private TableView<Aeropuerto> tabla;

    private final ObservableList<Aeropuerto> masterData = FXCollections.observableArrayList();
    private final ObservableList<Aeropuerto> filteredData = FXCollections.observableArrayList();

    /**
     * Inicializa el controlador, configurando los listeners y cargando datos iniciales.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableSelectionListener();
        setupRadioButtonListener();
        setupFilterListener();
        cargarPublicos();
    }

    /**
     * Configura el listener para la selección de la tabla.
     */
    private void setupTableSelectionListener() {
        tabla.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            deshabilitarMenus(newValue == null);
        });
    }

    /**
     * Configura el listener para los botones de selección de aeropuerto.
     */
    private void setupRadioButtonListener() {
        rbGroup.selectedToggleProperty().addListener((observable, oldBtn, newBtn) -> {
            if (newBtn.equals(rbPublicos)) {
                cargarPublicos();
            } else {
                cargarPrivados();
            }
        });
    }

    /**
     * Configura el listener para el campo de filtro de nombre.
     */
    private void setupFilterListener() {
        filtroNombre.setOnKeyTyped(keyEvent -> filtrar());
    }

    /**
     * Añade un nuevo aeropuerto.
     *
     * @param event Evento de acción.
     */
    @FXML
    void aniadirAeropuerto(ActionEvent event) {
        abrirVentana("/fxml/DatosAeropuerto.fxml", "AVIONES - AÑADIR AEROPUERTO");
        reloadAirportData();
    }

    /**
     * Edita un aeropuerto seleccionado.
     *
     * @param event Evento de acción.
     */
    @FXML
    void editarAeropuerto(ActionEvent event) {
        Aeropuerto aeropuerto = tabla.getSelectionModel().getSelectedItem();
        if (aeropuerto == null) {
            alerta("Selecciona un aeropuerto antes de editarlo");
        } else {
            abrirVentana("/fxml/DatosAeropuerto.fxml", "AVIONES - EDITAR AEROPUERTO", aeropuerto);
            reloadAirportData();
        }
    }

    /**
     * Borra un aeropuerto seleccionado.
     *
     * @param event Evento de acción.
     */
    @FXML
    void borrarAeropuerto(ActionEvent event) {
        Aeropuerto aeropuerto = tabla.getSelectionModel().getSelectedItem();
        if (aeropuerto == null) {
            alerta("Selecciona un aeropuerto antes de eliminarlo");
        } else {
            if (confirmarEliminacion()) {
                eliminarAeropuerto(aeropuerto);
            }
        }
    }

    /**
     * Muestra información de un aeropuerto seleccionado.
     *
     * @param event Evento de acción.
     */
    @FXML
    void infoAeropuerto(ActionEvent event) {
        Aeropuerto aeropuerto = tabla.getSelectionModel().getSelectedItem();
        if (aeropuerto == null) {
            alerta("Selecciona un aeropuerto antes de ver su información");
        } else {
            mostrarInformacionAeropuerto(aeropuerto);
        }
    }

    /**
     * Añade un avión a un aeropuerto.
     *
     * @param event Evento de acción.
     */
    @FXML
    void aniadirAvion(ActionEvent event) {
        abrirVentana("/fxml/AniadirAvion.fxml", "AVIONES - AÑADIR AVIÓN");
    }

    /**
     * Activa o desactiva un avión.
     *
     * @param event Evento de acción.
     */
    @FXML
    void activarDesactivarAvion(ActionEvent event) {
        abrirVentana("/fxml/ActivarDesactivarAvion.fxml", "AVIONES - ACTIVAR/DESACTIVAR AVIÓN");
    }

    /**
     * Borra un avión de un aeropuerto.
     *
     * @param event Evento de acción.
     */
    @FXML
    void borrarAvion(ActionEvent event) {
        abrirVentana("/fxml/BorrarAvion.fxml", "AVIONES - BORRAR AVIÓN");
    }

    /**
     * Carga la lista de aeropuertos públicos en la tabla.
     */
    public void cargarPublicos() {
        cargarAeropuertos(DaoAeropuertoPublico.cargarListado(), true);
    }

    /**
     * Carga la lista de aeropuertos privados en la tabla.
     */
    public void cargarPrivados() {
        cargarAeropuertos(DaoAeropuertoPrivado.cargarListado(), false);
    }

    /**
     * Abre una ventana FXML y establece el controlador.
     *
     * @param fxmlPath Ruta al archivo FXML.
     * @param title Título de la ventana.
     */
    private void abrirVentana(String fxmlPath, String title) {
        abrirVentana(fxmlPath, title, null);
    }

    /**
     * Abre una ventana FXML y establece el controlador con un aeropuerto seleccionado.
     *
     * @param fxmlPath Ruta al archivo FXML.
     * @param title Título de la ventana.
     * @param aeropuerto Aeropuerto seleccionado, si existe.
     */
    private void abrirVentana(String fxmlPath, String title, Aeropuerto aeropuerto) {
        try {
            Window ventana = rbPrivados.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            DatosAeropuertoController controlador = new DatosAeropuertoController(aeropuerto);
            fxmlLoader.setController(controlador);
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/imagenes/avion.png")));
            stage.setTitle(title);
            stage.initOwner(ventana);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            alerta("Error abriendo ventana, por favor inténtelo de nuevo");
        }
    }

    /**
     * Recarga la lista de aeropuertos después de añadir o editar.
     */
    private void reloadAirportData() {
        if (rbPublicos.isSelected()) {
            cargarPublicos();
        } else {
            cargarPrivados();
        }
    }

    /**
     * Confirma la eliminación de un aeropuerto.
     *
     * @return true si el usuario confirma, false de lo contrario.
     */
    private boolean confirmarEliminacion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(tabla.getScene().getWindow());
        alert.setHeaderText(null);
        alert.setTitle("Confirmación");
        alert.setContentText("¿Estás seguro que quieres eliminar ese aeropuerto? Esto también eliminará los aviones en este aeropuerto.");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Elimina un aeropuerto seleccionado.
     *
     * @param aeropuerto Aeropuerto a eliminar.
     */
    private void eliminarAeropuerto(Aeropuerto aeropuerto) {
        ObservableList<Avion> aviones = DaoAvion.cargarListado(aeropuerto);
        for (Avion avion : aviones) {
            if (!DaoAvion.eliminar(avion)) {
                alerta("No se pudo eliminar ese aeropuerto. Inténtelo de nuevo");
                return;
            }
        }
        if (aeropuerto instanceof AeropuertoPublico) {
            DaoAeropuertoPublico.eliminar((AeropuertoPublico) aeropuerto);
            cargarPublicos();
        } else {
            DaoAeropuertoPrivado.eliminar((AeropuertoPrivado) aeropuerto);
            cargarPrivados();
        }
        confirmacion("Aeropuerto eliminado correctamente");
    }

    /**
     * Muestra información sobre un aeropuerto.
     *
     * @param aeropuerto Aeropuerto cuya información se mostrará.
     */
    private void mostrarInformacionAeropuerto(Aeropuerto aeropuerto) {
        StringBuilder sb = new StringBuilder();
        sb.append("Aeropuerto: ").append(aeropuerto.getNombre()).append("\n");
        sb.append("Tipo: ").append(aeropuerto instanceof AeropuertoPublico ? "Público" : "Privado").append("\n");
        sb.append("Ubicación: ").append(aeropuerto.getUbicacion()).append("\n");
        sb.append("Número de Aviones: ").append(AvionDAO.cargarListado(aeropuerto).size()).append("\n");
        alerta(sb.toString());
    }

    /**
     * Filtra la lista de aeropuertos por el texto introducido en el campo de filtro.
     */
    private void filtrar() {
        String filtro = filtroNombre.getText().toLowerCase();
        filteredData.setAll(masterData.filtered(aeropuerto ->
                aeropuerto.getNombre().toLowerCase().contains(filtro)));
        tabla.setItems(filteredData);
    }

    /**
     * Deshabilita los menús de edición e información si no hay un aeropuerto seleccionado.
     *
     * @param deshabilitar true para deshabilitar, false para habilitar.
     */
    private void deshabilitarMenus(boolean deshabilitar) {
        menuEditarAeropuerto.setDisable(deshabilitar);
        menuInfoAeropuerto.setDisable(deshabilitar);
        menuBorrarAeropuerto.setDisable(deshabilitar);
    }

    /**
     * Muestra una alerta con un mensaje específico.
     *
     * @param mensaje Mensaje de la alerta.
     */
    private void alerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra una alerta de confirmación.
     *
     * @param mensaje Mensaje de la confirmación.
     */
    private void confirmacion(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

