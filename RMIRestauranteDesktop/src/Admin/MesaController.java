package Admin;

import Main.RMIConnection;
import DTO.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Insets;
import java.util.List;

public class MesaController {
    @FXML private TableView<Mesa> tblDatos;
    @FXML private TableColumn<Mesa, Integer> colId;
    @FXML private TableColumn<Mesa, String> colNumero;
    @FXML private TableColumn<Mesa, Integer> colCapacidad;
    @FXML private TableColumn<Mesa, String> colSalon;
    @FXML private TableColumn<Mesa, String> colEstado;
    @FXML private TableColumn<Mesa, String> colAcciones;
    @FXML private Label lblTotal;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idMesa"));
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colCapacidad.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
        colSalon.setCellValueFactory(new PropertyValueFactory<>("nombreSalon"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("nombreEstadoMesa"));
        colAcciones.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Mesa m = getTableView().getItems().get(getIndex());
                Button btnEdit = new Button("Editar");
                btnEdit.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white; -fx-font-size: 11;");
                btnEdit.setOnAction(e -> editar(m));
                Button btnDel = new Button("Eliminar");
                btnDel.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-size: 11;");
                btnDel.setOnAction(e -> eliminar(m));
                setGraphic(new javafx.scene.layout.HBox(4, btnEdit, btnDel));
            }
        });
        cargarDatos();
    }

    @FXML private void cargarDatos() {
        try {
            List<Mesa> lista = RMIConnection.getMesaDAO().listar("");
            tblDatos.setItems(FXCollections.observableArrayList(lista));
            lblTotal.setText("Total: " + lista.size() + " mesas");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void nuevo() { showDialog(null); }
    private void editar(Mesa m) { showDialog(m); }

    private void showDialog(Mesa existing) {
        Dialog<Mesa> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Nueva Mesa" : "Editar Mesa");
        ButtonType saveType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        TextField numero = new TextField(existing != null ? existing.getNumero() : "");
        numero.setPromptText("Numero de mesa");
        TextField capacidad = new TextField(existing != null ? String.valueOf(existing.getCapacidad()) : "4");
        capacidad.setPromptText("Capacidad");

        ComboBox<String> cbSalon = new ComboBox<>();
        cbSalon.setPromptText("Seleccionar Salon");
        try {
            List<Salon> salones = RMIConnection.getSalonDAO().listar();
            for (Salon s : salones) cbSalon.getItems().add(s.getIdSalon() + " - " + s.getNombre());
            if (existing != null && existing.getIdSalon() > 0) {
                for (int i = 0; i < cbSalon.getItems().size(); i++)
                    if (cbSalon.getItems().get(i).startsWith(existing.getIdSalon() + " - "))
                        cbSalon.getSelectionModel().select(i);
            } else if (!cbSalon.getItems().isEmpty()) {
                cbSalon.getSelectionModel().selectFirst();
            }
        } catch (Exception e) { e.printStackTrace(); }

        ComboBox<String> cbEstado = new ComboBox<>();
        cbEstado.setPromptText("Estado");
        try {
            List<EstadoMesa> estados = RMIConnection.getEstadoMesaDAO().listar();
            for (EstadoMesa em : estados) cbEstado.getItems().add(em.getIdEstadoMesa() + " - " + em.getNombre());
            if (existing != null && existing.getIdEstadoMesa() > 0) {
                for (int i = 0; i < cbEstado.getItems().size(); i++)
                    if (cbEstado.getItems().get(i).startsWith(existing.getIdEstadoMesa() + " - "))
                        cbEstado.getSelectionModel().select(i);
            } else {
                for (int i = 0; i < cbEstado.getItems().size(); i++)
                    if (cbEstado.getItems().get(i).toLowerCase().contains("libre"))
                        cbEstado.getSelectionModel().select(i);
                if (cbEstado.getSelectionModel().isEmpty() && !cbEstado.getItems().isEmpty())
                    cbEstado.getSelectionModel().selectFirst();
            }
        } catch (Exception e) { e.printStackTrace(); }

        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(8, numero, capacidad, cbSalon, cbEstado);
        vbox.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(vbox);

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                Mesa m = existing != null ? existing : new Mesa();
                m.setNumero(numero.getText());
                try { m.setCapacidad(Integer.parseInt(capacidad.getText())); } catch (Exception ignored) {}
                String selSalon = cbSalon.getValue();
                if (selSalon != null) try { m.setIdSalon(Integer.parseInt(selSalon.split(" - ")[0])); } catch (Exception ignored) {}
                String selEstado = cbEstado.getValue();
                if (selEstado != null) try { m.setIdEstadoMesa(Integer.parseInt(selEstado.split(" - ")[0])); } catch (Exception ignored) {}
                return m;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(m -> {
            try {
                if (existing == null) RMIConnection.getMesaDAO().insertar(m);
                else RMIConnection.getMesaDAO().actualizar(m);
                cargarDatos();
            } catch (Exception e) { e.printStackTrace(); }
        });
    }

    private void eliminar(Mesa m) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Eliminar mesa " + m.getNumero() + "?");
        alert.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try { RMIConnection.getMesaDAO().eliminar(m.getIdMesa()); cargarDatos(); }
                catch (Exception e) { e.printStackTrace(); }
            }
        });
    }
}
