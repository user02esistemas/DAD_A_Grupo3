package Admin;

import Main.RMIConnection;
import DTO.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Insets;
import java.util.List;

public class MozoController {
    @FXML private TableView<Mozo> tblDatos;
    @FXML private TableColumn<Mozo, Integer> colId;
    @FXML private TableColumn<Mozo, String> colNombre;
    @FXML private TableColumn<Mozo, String> colApellido;
    @FXML private TableColumn<Mozo, String> colTelefono;
    @FXML private TableColumn<Mozo, String> colSalon;
    @FXML private TableColumn<Mozo, String> colAcciones;
    @FXML private TextField txtBuscar;
    @FXML private Label lblTotal;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idMozo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colSalon.setCellValueFactory(new PropertyValueFactory<>("nombreSalon"));
        colAcciones.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Mozo m = getTableView().getItems().get(getIndex());
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
            List<Mozo> lista = RMIConnection.getMozoDAO().listar();
            tblDatos.setItems(FXCollections.observableArrayList(lista));
            lblTotal.setText("Total: " + lista.size() + " mozos");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void buscar() {
        try {
            List<Mozo> all = RMIConnection.getMozoDAO().listar();
            String busq = txtBuscar.getText().toLowerCase();
            ObservableList<Mozo> filtered = FXCollections.observableArrayList();
            for (Mozo m : all) {
                if (m.getNombre().toLowerCase().contains(busq) || m.getApellido().toLowerCase().contains(busq))
                    filtered.add(m);
            }
            tblDatos.setItems(filtered);
            lblTotal.setText("Total: " + filtered.size() + " mozos");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void nuevo() { showDialog(null); }
    private void editar(Mozo m) { showDialog(m); }

    private void showDialog(Mozo existing) {
        Dialog<Mozo> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Nuevo Mozo" : "Editar Mozo");
        ButtonType saveType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        TextField nombre = new TextField(existing != null ? existing.getNombre() : "");
        nombre.setPromptText("Nombre");
        TextField apellido = new TextField(existing != null ? existing.getApellido() : "");
        apellido.setPromptText("Apellido");
        TextField telefono = new TextField(existing != null ? existing.getTelefono() : "");
        telefono.setPromptText("Telefono");
        TextField usuario = new TextField(existing != null ? existing.getUsuario() : "");
        usuario.setPromptText("Usuario");
        PasswordField clave = new PasswordField();
        clave.setText(existing != null ? existing.getClave() : "");
        clave.setPromptText("Clave");
        CheckBox activo = new CheckBox("Activo");
        activo.setSelected(existing == null || existing.isActivo());

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

        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(8, nombre, apellido, telefono, usuario, clave, activo, cbSalon);
        vbox.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(vbox);

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                Mozo m = existing != null ? existing : new Mozo();
                m.setNombre(nombre.getText());
                m.setApellido(apellido.getText());
                m.setTelefono(telefono.getText());
                m.setUsuario(usuario.getText());
                m.setClave(clave.getText());
                m.setActivo(activo.isSelected());
                String selSalon = cbSalon.getValue();
                if (selSalon != null)
                    try { m.setIdSalon(Integer.parseInt(selSalon.split(" - ")[0])); } catch (Exception ignored) {}
                return m;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(m -> {
            try {
                if (existing == null) RMIConnection.getMozoDAO().insertar(m);
                else RMIConnection.getMozoDAO().actualizar(m);
                cargarDatos();
            } catch (Exception e) { e.printStackTrace(); }
        });
    }

    private void eliminar(Mozo m) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Eliminar mozo " + m.getNombre() + "?");
        alert.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try { RMIConnection.getMozoDAO().eliminar(m.getIdMozo()); cargarDatos(); }
                catch (Exception e) { e.printStackTrace(); }
            }
        });
    }
}
