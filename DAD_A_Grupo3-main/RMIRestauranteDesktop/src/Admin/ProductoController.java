package Admin;

import Main.RMIConnection;
import DTO.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Insets;
import java.util.List;

public class ProductoController {
    @FXML private TableView<Producto> tblDatos;
    @FXML private TableColumn<Producto, Integer> colId;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colTipo;
    @FXML private TableColumn<Producto, String> colEstado;
    @FXML private TableColumn<Producto, String> colAcciones;
    @FXML private TextField txtBuscar;
    @FXML private Label lblTotal;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("nombreTipoProducto"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("nombreEstadoProducto"));
        colAcciones.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Producto p = getTableView().getItems().get(getIndex());
                Button btnEdit = new Button("Editar");
                btnEdit.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white; -fx-font-size: 11;");
                btnEdit.setOnAction(e -> editar(p));
                Button btnDel = new Button("Eliminar");
                btnDel.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-size: 11;");
                btnDel.setOnAction(e -> eliminar(p));
                setGraphic(new javafx.scene.layout.HBox(4, btnEdit, btnDel));
            }
        });
        cargarDatos();
    }

    private void cargarDatos() {
        try {
            List<Producto> lista = RMIConnection.getProductoDAO().listar("");
            tblDatos.setItems(FXCollections.observableArrayList(lista));
            lblTotal.setText("Total: " + lista.size() + " productos");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void buscar() {
        try {
            List<Producto> lista = RMIConnection.getProductoDAO().listar(txtBuscar.getText());
            tblDatos.setItems(FXCollections.observableArrayList(lista));
            lblTotal.setText("Total: " + lista.size() + " productos");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void nuevo() { showDialog(null); }
    private void editar(Producto p) { showDialog(p); }

    private void showDialog(Producto existing) {
        Dialog<Producto> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Nuevo Producto" : "Editar Producto");
        ButtonType saveType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        TextField nombre = new TextField(existing != null ? existing.getNombre() : "");
        nombre.setPromptText("Nombre");
        TextField desc = new TextField(existing != null ? existing.getDescripcion() : "");
        desc.setPromptText("Descripcion");
        TextField precioMin = new TextField(existing != null ? String.valueOf(existing.getPrecioMinimo()) : "0");
        precioMin.setPromptText("Precio Minimo");

        ComboBox<String> cbTipo = new ComboBox<>();
        cbTipo.setPromptText("Tipo de Producto");
        ComboBox<String> cbTiempo = new ComboBox<>();
        cbTiempo.setPromptText("Tiempo Preparacion");
        ComboBox<String> cbEstado = new ComboBox<>();
        cbEstado.setPromptText("Estado");

        try {
            List<TipoProducto> tipos = RMIConnection.getTipoProductoDAO().listar();
            for (TipoProducto t : tipos) cbTipo.getItems().add(t.getIdTipoProducto() + " - " + t.getNombre());
            List<TiempoPreparacion> tiempos = RMIConnection.getTiempoPreparacionDAO().listar();
            for (TiempoPreparacion t : tiempos) cbTiempo.getItems().add(t.getIdTiempoPreparacion() + " - " + t.getNombre());
            List<EstadoProducto> estados = RMIConnection.getEstadoProductoDAO().listar();
            for (EstadoProducto e : estados) cbEstado.getItems().add(e.getIdEstadoProducto() + " - " + e.getNombre());

            if (existing != null) {
                for (int i = 0; i < cbTipo.getItems().size(); i++)
                    if (cbTipo.getItems().get(i).startsWith(existing.getIdTipoProducto() + " - "))
                        cbTipo.getSelectionModel().select(i);
                for (int i = 0; i < cbTiempo.getItems().size(); i++)
                    if (cbTiempo.getItems().get(i).startsWith(existing.getIdTiempoPreparacion() + " - "))
                        cbTiempo.getSelectionModel().select(i);
                for (int i = 0; i < cbEstado.getItems().size(); i++)
                    if (cbEstado.getItems().get(i).startsWith(existing.getIdEstadoProducto() + " - "))
                        cbEstado.getSelectionModel().select(i);
            } else {
                if (!cbTipo.getItems().isEmpty()) cbTipo.getSelectionModel().selectFirst();
                if (!cbTiempo.getItems().isEmpty()) cbTiempo.getSelectionModel().selectFirst();
                if (!cbEstado.getItems().isEmpty()) cbEstado.getSelectionModel().selectFirst();
            }
        } catch (Exception e) { e.printStackTrace(); }

        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(8, nombre, desc, precioMin, cbTipo, cbTiempo, cbEstado);
        vbox.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(vbox);

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                Producto p = existing != null ? existing : new Producto();
                p.setNombre(nombre.getText());
                p.setDescripcion(desc.getText());
                try { p.setPrecioMinimo(Double.parseDouble(precioMin.getText())); } catch (Exception ignored) {}
                String valTipo = cbTipo.getValue();
                if (valTipo != null) try { p.setIdTipoProducto(Integer.parseInt(valTipo.split(" - ")[0])); } catch (Exception ignored) {}
                String valTiempo = cbTiempo.getValue();
                if (valTiempo != null) try { p.setIdTiempoPreparacion(Integer.parseInt(valTiempo.split(" - ")[0])); } catch (Exception ignored) {}
                String valEstado = cbEstado.getValue();
                if (valEstado != null) try { p.setIdEstadoProducto(Integer.parseInt(valEstado.split(" - ")[0])); } catch (Exception ignored) {}
                return p;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(p -> {
            try {
                if (existing == null) RMIConnection.getProductoDAO().insertar(p);
                else RMIConnection.getProductoDAO().actualizar(p);
                cargarDatos();
            } catch (Exception e) { e.printStackTrace(); }
        });
    }

    private void eliminar(Producto p) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Eliminar " + p.getNombre() + "?");
        alert.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try { RMIConnection.getProductoDAO().eliminar(p.getIdProducto()); cargarDatos(); }
                catch (Exception e) { e.printStackTrace(); }
            }
        });
    }
}
