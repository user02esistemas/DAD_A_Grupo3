package Admin;

import Main.RMIConnection;
import DTO.Salon;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Insets;
import java.util.List;

public class SalonController {
    @FXML private TableView<Salon> tblDatos;
    @FXML private TableColumn<Salon, Integer> colId;
    @FXML private TableColumn<Salon, String> colNombre;
    @FXML private TableColumn<Salon, String> colAcciones;
    @FXML private Label lblTotal;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idSalon"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colAcciones.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Salon s = getTableView().getItems().get(getIndex());
                Button btnEdit = new Button("Editar");
                btnEdit.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white; -fx-font-size: 11;");
                btnEdit.setOnAction(e -> editar(s));
                Button btnDel = new Button("Eliminar");
                btnDel.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-size: 11;");
                btnDel.setOnAction(e -> eliminar(s));
                setGraphic(new javafx.scene.layout.HBox(4, btnEdit, btnDel));
            }
        });
        cargarDatos();
    }

    @FXML
    private void cargarDatos() {
        try {
            List<Salon> lista = RMIConnection.getSalonDAO().listar();
            tblDatos.setItems(FXCollections.observableArrayList(lista));
            lblTotal.setText("Total: " + lista.size() + " salones");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void nuevo() { showDialog(null); }
    private void editar(Salon s) { showDialog(s); }

    private void showDialog(Salon existing) {
        Dialog<Salon> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Nuevo Salon" : "Editar Salon");
        ButtonType saveType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        TextField nombre = new TextField(existing != null ? existing.getNombre() : "");
        nombre.setPromptText("Nombre del Salon");

        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(8, nombre);
        vbox.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(vbox);

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                Salon s = existing != null ? existing : new Salon();
                s.setNombre(nombre.getText());
                return s;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(s -> {
            try {
                if (existing == null) RMIConnection.getSalonDAO().insertar(s);
                else RMIConnection.getSalonDAO().actualizar(s);
                cargarDatos();
            } catch (Exception e) { e.printStackTrace(); }
        });
    }

    private void eliminar(Salon s) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Eliminar salon " + s.getNombre() + "?");
        alert.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try { RMIConnection.getSalonDAO().eliminar(s.getIdSalon()); cargarDatos(); }
                catch (Exception e) { e.printStackTrace(); }
            }
        });
    }
}
