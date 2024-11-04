package es.aritzherrero.ejerciciol.Control;

import es.aritzherrero.ejerciciol.DAO.AeropuertoDAO;
import es.aritzherrero.ejerciciol.DAO.AeropuertoPrivadoDAO;
import es.aritzherrero.ejerciciol.DAO.AeropuertoPublicoDAO;
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
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Controlador para la gestión de aeropuertos en la aplicación.
 * Permite la visualización, edición, eliminación y adición de aeropuertos.
 */
public class EjercicioL_Aeropuerto_Control implements Initializable {

    @FXML
    private TableView tabla;
    @FXML
    private TextField filtroNombre;
    @FXML
    private RadioButton rbPublicos;
    @FXML
    private RadioButton rbPrivados;
    @FXML
    private MenuItem menuEditarAeropuerto;
    @FXML
    private MenuItem menuBorrarAeropuerto;
    @FXML
    private MenuItem menuInfoAeropuerto;

    private ObservableList<Object> masterData = FXCollections.observableArrayList();
    private ObservableList<Object> filteredData = FXCollections.observableArrayList();

    /**
     * Inicializa el controlador. Configura los listeners y carga los datos iniciales.
     *
     * @param url            URL para localizar recursos.
     * @param resourceBundle El conjunto de recursos que contiene el controlador.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tabla.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal) -> deshabilitarMenus(newVal == null));

        ToggleGroup grupoAeropuertos = new ToggleGroup();
        rbPublicos.setToggleGroup(grupoAeropuertos);
        rbPrivados.setToggleGroup(grupoAeropuertos);

        grupoAeropuertos.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == rbPublicos) {
                try {
                    cargarPublicos();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (newToggle == rbPrivados) {
                try {
                    cargarPrivados();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        filtroNombre.textProperty().addListener((observable, oldValue, newValue) -> filtrar());
    }

    /**
     * Muestra información sobre el aeropuerto seleccionado en una alerta.
     *
     * @param event Evento de acción.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    @FXML
    void infoAeropuerto(ActionEvent event) throws SQLException {
        Object aeropuerto = tabla.getSelectionModel().getSelectedItem();
        if (aeropuerto == null) {
            alerta("Selecciona un aeropuerto antes de ver su información");
            return;
        }

        String info = generarInfoAeropuerto(aeropuerto);

        // Mostrar información en una alerta
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.setTitle("Información");
        alerta.setContentText(info);
        alerta.showAndWait();
    }

    /**
     * Genera una cadena con la información detallada del aeropuerto.
     *
     * @param aeropuerto Objeto del aeropuerto seleccionado.
     * @return La información formateada del aeropuerto.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    private String generarInfoAeropuerto(Object aeropuerto) throws SQLException {
        StringBuilder info = new StringBuilder();

        Aeropuerto airport = (aeropuerto instanceof AeropuertoPublico)
                ? ((AeropuertoPublico) aeropuerto).getAeropuerto()
                : ((AeropuertoPrivado) aeropuerto).getAeropuerto();

        info.append("Nombre: ").append(airport.getNombre())
                .append("\nPaís: ").append(airport.getDireccion().getPais())
                .append("\nDirección: C\\ ").append(airport.getDireccion().getCalle()).append(" ")
                .append(airport.getDireccion().getNumero()).append(", ").append(airport.getDireccion().getCiudad())
                .append("\nAño de inauguración: ").append(airport.getAnio_inauguracion())
                .append("\nCapacidad: ").append(airport.getCapacidad())
                .append("\nAviones:");

        ObservableList<Avion> aviones = AvionDAO.cargarListado(airport);
        for (Avion avion : aviones) {
            info.append("\n\tModelo: ").append(avion.getModelo())
                    .append("\n\tNúmero de asientos: ").append(avion.getNumero_asientos())
                    .append("\n\tVelocidad máxima: ").append(avion.getVelocidad_maxima())
                    .append(avion.isActivado() ? "\n\tActivado" : "\n\tDesactivado");
        }

        if (aeropuerto instanceof AeropuertoPublico) {
            AeropuertoPublico aeropuertoPublico = (AeropuertoPublico) aeropuerto;
            info.append("\nPúblico")
                    .append("\nFinanciación: ").append(aeropuertoPublico.getFinanciacion())
                    .append("\nNúmero de trabajadores: ").append(aeropuertoPublico.getNum_trabajadores());
        } else {
            AeropuertoPrivado aeropuertoPrivado = (AeropuertoPrivado) aeropuerto;
            info.append("\nPrivado")
                    .append("\nNúmero de socios: ").append(aeropuertoPrivado.getNumero_socios());
        }

        return info.toString();
    }

    /**
     * Abre una ventana para añadir un nuevo aeropuerto.
     *
     * @param event Evento de acción.
     */
    @FXML
    void aniadirAeropuerto(ActionEvent event) {
        abrirVentana("/fxml/ejercicioL_DatosAeropuerto.fxml", "AVIONES - AÑADIR AEROPUERTO");
    }

    /**
     * Abre una ventana para editar el aeropuerto seleccionado.
     *
     * @param event Evento de acción.
     */
    @FXML
    void editarAeropuerto(ActionEvent event) {
        Object aeropuerto = tabla.getSelectionModel().getSelectedItem();
        if (aeropuerto == null) {
            alerta("Selecciona un aeropuerto antes de editarlo");
            return;
        }
        abrirVentanaConAeropuerto("/fxml/ejercicioL_DatosAeropuerto.fxml", "AVIONES - EDITAR AEROPUERTO", aeropuerto);
    }

    /**
     * Elimina el aeropuerto seleccionado después de confirmar la acción.
     *
     * @param event Evento de acción.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    @FXML
    void borrarAeropuerto(ActionEvent event) throws SQLException {
        Object aeropuerto = tabla.getSelectionModel().getSelectedItem();
        if (aeropuerto == null) {
            alerta("Selecciona un aeropuerto antes de eliminarlo");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(tabla.getScene().getWindow());
        alert.setContentText("¿Estás seguro que quieres eliminar ese aeropuerto? Esto también eliminará los aviones en este aeropuerto.");

        if (alert.showAndWait().filter(res -> res == ButtonType.OK).isPresent()) {
            eliminarAeropuerto(aeropuerto);
        }
    }

    /**
     * Elimina el aeropuerto y sus aviones asociados de la base de datos.
     *
     * @param aeropuerto Objeto del aeropuerto a eliminar.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    private void eliminarAeropuerto(Object aeropuerto) throws SQLException {
        ObservableList<Avion> aviones = aeropuerto instanceof AeropuertoPublico
                ? AvionDAO.cargarListado(((AeropuertoPublico) aeropuerto).getAeropuerto())
                : AvionDAO.cargarListado(((AeropuertoPrivado) aeropuerto).getAeropuerto());

        for (Avion avion : aviones) {
            if (!AvionDAO.eliminar(avion)) {
                alerta("No se pudo eliminar ese aeropuerto. Inténtelo de nuevo");
                return;
            }
        }

        Aeropuerto airport = aeropuerto instanceof AeropuertoPublico
                ? ((AeropuertoPublico) aeropuerto).getAeropuerto()
                : ((AeropuertoPrivado) aeropuerto).getAeropuerto();

        if (eliminarAeropuertoEnBD(aeropuerto)) {
            if (AeropuertoDAO.eliminar(airport)) {
                if (aeropuerto instanceof AeropuertoPublico) cargarPublicos(); else cargarPrivados();
                confirmacion("Aeropuerto eliminado correctamente");
            } else {
                alerta("No se pudo eliminar ese aeropuerto. Inténtelo de nuevo");
            }
        } else {
            alerta("No se pudo eliminar ese aeropuerto. Inténtelo de nuevo");
        }
    }

    /**
     * Elimina el aeropuerto en la base de datos.
     *
     * @param aeropuerto Objeto del aeropuerto a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    private boolean eliminarAeropuertoEnBD(Object aeropuerto) throws SQLException {
        return aeropuerto instanceof AeropuertoPublico
                ? AeropuertoPublicoDAO.eliminar((AeropuertoPublico) aeropuerto)
                : AeropuertoPrivadoDAO.eliminar((AeropuertoPrivado) aeropuerto);
    }

    /**
     * Abre una nueva ventana utilizando el archivo FXML especificado.
     *
     * @param fxmlPath Ruta del archivo FXML.
     * @param titulo   Título de la ventana.
     */
    private void abrirVentana(String fxmlPath, String titulo) {
        try {
            Window ventana = rbPrivados.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/imagenes/avion.png")));
            stage.setTitle(titulo);
            stage.initOwner(ventana);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            alerta("Error abriendo ventana, por favor inténtelo de nuevo");
        }
    }

    /**
     * Abre una nueva ventana con un aeropuerto específico utilizando el archivo FXML.
     *
     * @param fxmlPath  Ruta del archivo FXML.
     * @param titulo    Título de la ventana.
     * @param aeropuerto Objeto del aeropuerto a editar.
     */
    private void abrirVentanaConAeropuerto(String fxmlPath, String titulo, Object aeropuerto) {
        try {
            Window ventana = rbPrivados.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            fxmlLoader.setController(new DatosAeropuertoController(aeropuerto));
            Stage stage = new Stage();
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/imagenes/avion.png")));
            stage.setTitle(titulo);
            stage.initOwner(ventana);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            alerta("Error abriendo ventana, por favor inténtelo de nuevo");
        }
    }

    /**
     * Carga la lista de aeropuertos públicos desde la base de datos.
     *
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    public void cargarPublicos() throws SQLException {
        limpiarTabla();
        ObservableList<AeropuertoPublico> aeropuertos = AeropuertoPublicoDAO.cargarListado();
        masterData.setAll(aeropuertos);
        tabla.setItems(aeropuertos);
    }

    /**
     * Carga la lista de aeropuertos privados desde la base de datos.
     *
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    public void cargarPrivados() throws SQLException {
        limpiarTabla();
        ObservableList<AeropuertoPrivado> aeropuertos = AeropuertoPrivadoDAO.cargarListado();
        masterData.setAll(aeropuertos);
        tabla.setItems(aeropuertos);
    }

    /**
     * Habilita o deshabilita los menús de edición y eliminación dependiendo de si hay un aeropuerto seleccionado.
     *
     * @param deshabilitado true si los menús deben ser deshabilitados, false en caso contrario.
     */
    public void deshabilitarMenus(boolean deshabilitado) {
        menuEditarAeropuerto.setDisable(deshabilitado);
        menuBorrarAeropuerto.setDisable(deshabilitado);
        menuInfoAeropuerto.setDisable(deshabilitado);
    }

    /**
     * Filtra la lista de aeropuertos basada en el texto ingresado en el campo de filtro.
     */
    public void filtrar() {
        String valor = filtroNombre.getText().toLowerCase();

        if (valor.isEmpty()) {
            tabla.setItems(masterData);
        } else {
            filteredData.clear();
            for (Object aeropuerto : masterData) {
                String nombre = (aeropuerto instanceof AeropuertoPublico)
                        ? ((AeropuertoPublico) aeropuerto).getAeropuerto().getNombre()
                        : ((AeropuertoPrivado) aeropuerto).getAeropuerto().getNombre();

                if (nombre.toLowerCase().contains(valor)) {
                    filteredData.add(aeropuerto);
                }
            }
            tabla.setItems(filteredData);
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
        alerta.setTitle("Confirmación");
        alerta.setContentText(texto);
        alerta.showAndWait();
    }

    /**
     * Limpia todos los elementos de la tabla.
     */
    private void limpiarTabla() {
        tabla.getItems().clear();
    }
}



