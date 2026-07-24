package Admin;

import Main.RMIConnection;
import DTO.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Insets;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class PresentacionController {
    @FXML private TableView<Presentacion> tblDatos;
    @FXML private TableColumn<Presentacion, Integer> colId;
    @FXML private TableColumn<Presentacion, String> colNombre;
    @FXML private TableColumn<Presentacion, Double> colPrecio;
    @FXML private TableColumn<Presentacion, String> colProducto;
    @FXML private TableColumn<Presentacion, String> colAcciones;
    @FXML private TextField txtBuscar;
    @FXML private Label lblTotal;

    private static final String WEB_IMG_DIR = "../RMIRestauranteWeb/web/img/presentaciones/";

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idPresentacion"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colAcciones.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Presentacion p = getTableView().getItems().get(getIndex());
                Button btnEdit = new Button("Editar");
                btnEdit.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white; -fx-font-size: 11;");
                btnEdit.setOnAction(e -> editar(p));
                Button btnDel = new Button("Eliminar");
                btnDel.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-size: 11;");
                btnDel.setOnAction(e -> eliminar(p));
                setGraphic(new javafx.scene.layout.HBox(4, btnEdit, btnDel));
            }
        });
        // Ensure web images directory exists
        new File(WEB_IMG_DIR).mkdirs();
        cargarDatos();
    }

    @FXML private void cargarDatos() {
        try {
            List<Presentacion> lista = RMIConnection.getPresentacionDAO().listar("");
            tblDatos.setItems(FXCollections.observableArrayList(lista));
            lblTotal.setText("Total: " + lista.size() + " presentaciones");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void buscar() {
        try {
            List<Presentacion> lista = RMIConnection.getPresentacionDAO().listar(txtBuscar.getText());
            tblDatos.setItems(FXCollections.observableArrayList(lista));
            lblTotal.setText("Total: " + lista.size() + " presentaciones");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void nuevo() { showDialog(null); }
    private void editar(Presentacion p) { showDialog(p); }

    private String seleccionarYCopiarImagen() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar Imagen");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png","*.jpg","*.jpeg","*.gif","*.webp"));
        File file = fc.showOpenDialog(null);
        if (file == null) return null;
        try {
            String ext = file.getName().substring(file.getName().lastIndexOf('.'));
            String destName = System.currentTimeMillis() + ext;
            File dest = new File(WEB_IMG_DIR + destName);
            Files.copy(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return "img/presentaciones/" + destName;
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al copiar imagen: " + e.getMessage()).show();
            return null;
        }
    }

    private void showDialog(Presentacion existing) {
        Dialog<Presentacion> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Nueva Presentacion" : "Editar Presentacion");
        ButtonType saveType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        TextField nombre = new TextField(existing != null ? existing.getNombre() : "");
        nombre.setPromptText("Nombre");
        TextField precio = new TextField(existing != null ? String.valueOf(existing.getPrecio()) : "0");
        precio.setPromptText("Precio");
        TextField stock = new TextField(existing != null ? String.valueOf(existing.getStock()) : "0");
        stock.setPromptText("Stock");

        Label lblImagen = new Label(existing != null && existing.getImagenUrl() != null && !existing.getImagenUrl().isEmpty()
            ? "Imagen: " + existing.getImagenUrl() : "Sin imagen seleccionada");
        lblImagen.setStyle("-fx-text-fill: #636E72; -fx-font-size: 11;");
        Button btnImg = new Button("Seleccionar Imagen");
        btnImg.setStyle("-fx-background-color: #636E72; -fx-text-fill: white; -fx-font-size: 11; -fx-cursor: hand;");
        final String[] selectedImgPath = {existing != null ? existing.getImagenUrl() : ""};
        btnImg.setOnAction(e -> {
            String path = seleccionarYCopiarImagen();
            if (path != null) {
                selectedImgPath[0] = path;
                lblImagen.setText("Imagen: " + path);
            }
        });
        javafx.scene.layout.HBox imgRow = new javafx.scene.layout.HBox(8, btnImg, lblImagen);
        imgRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        ComboBox<String> cbProducto = new ComboBox<>();
        cbProducto.setPromptText("Seleccionar Producto");
        try {
            List<Producto> productos = RMIConnection.getProductoDAO().listar("");
            for (Producto p : productos) cbProducto.getItems().add(p.getIdProducto() + " - " + p.getNombre());
            if (existing != null && existing.getIdProducto() > 0) {
                for (int i = 0; i < cbProducto.getItems().size(); i++)
                    if (cbProducto.getItems().get(i).startsWith(existing.getIdProducto() + " - "))
                        cbProducto.getSelectionModel().select(i);
            } else if (!cbProducto.getItems().isEmpty()) {
                cbProducto.getSelectionModel().selectFirst();
            }
        } catch (Exception e) { e.printStackTrace(); }

        ComboBox<String> cbEstado = new ComboBox<>();
        cbEstado.setPromptText("Estado");
        try {
            List<EstadoPresentacion> estados = RMIConnection.getEstadoPresentacionDAO().listar();
            for (EstadoPresentacion ep : estados) cbEstado.getItems().add(ep.getIdEstadoPresentacion() + " - " + ep.getNombre());
            if (existing != null && existing.getIdEstadoPresentacion() > 0) {
                for (int i = 0; i < cbEstado.getItems().size(); i++)
                    if (cbEstado.getItems().get(i).startsWith(existing.getIdEstadoPresentacion() + " - "))
                        cbEstado.getSelectionModel().select(i);
            } else if (!cbEstado.getItems().isEmpty()) {
                cbEstado.getSelectionModel().selectFirst();
            }
        } catch (Exception e) { e.printStackTrace(); }

        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(8, nombre, precio, stock, imgRow, cbProducto, cbEstado);
        vbox.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(vbox);

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                Presentacion p = existing != null ? existing : new Presentacion();
                p.setNombre(nombre.getText());
                try { p.setPrecio(Double.parseDouble(precio.getText())); } catch (Exception ignored) {}
                try { p.setStock(Integer.parseInt(stock.getText())); } catch (Exception ignored) {}
                p.setImagenUrl(selectedImgPath[0]);
                String selProd = cbProducto.getValue();
                if (selProd != null) try { p.setIdProducto(Integer.parseInt(selProd.split(" - ")[0])); } catch (Exception ignored) {}
                String selEst = cbEstado.getValue();
                if (selEst != null) try { p.setIdEstadoPresentacion(Integer.parseInt(selEst.split(" - ")[0])); } catch (Exception ignored) {}
                return p;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(p -> {
            try {
                if (existing == null) RMIConnection.getPresentacionDAO().insertar(p);
                else RMIConnection.getPresentacionDAO().actualizar(p);
                cargarDatos();
            } catch (Exception e) { e.printStackTrace(); }
        });
    }

    private void eliminar(Presentacion p) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Eliminar presentacion " + p.getNombre() + "?");
        alert.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try { RMIConnection.getPresentacionDAO().eliminar(p.getIdPresentacion()); cargarDatos(); }
                catch (Exception e) { e.printStackTrace(); }
            }
        });
    }
}
