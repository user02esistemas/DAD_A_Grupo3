package Admin;

import Main.RMIConnection;
import DTO.Mesa;
import DTO.DatosEmpresa;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.MessageDigest;
import java.util.*;

public class QRController {
    @FXML private TableView<Mesa> tblDatos;
    @FXML private TableColumn<Mesa, Integer> colId;
    @FXML private TableColumn<Mesa, String> colNumero;
    @FXML private TableColumn<Mesa, Integer> colCapacidad;
    @FXML private TableColumn<Mesa, String> colSalon;
    @FXML private TableColumn<Mesa, String> colAcciones;
    @FXML private ImageView imgQR;
    @FXML private Label lblMesaSeleccionada;
    @FXML private Label lblTotal;

    private final Map<Integer, Image> qrImages = new HashMap<>();
    private final Map<Integer, BufferedImage> qrBuffered = new HashMap<>();
    private Mesa mesaSeleccionada;
    private String secret = "overos2026";
    private String baseUrl = "http://192.168.100.131:8081/RMIRestauranteWeb";

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idMesa"));
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colCapacidad.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
        colSalon.setCellValueFactory(new PropertyValueFactory<>("nombreSalon"));
        colAcciones.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Mesa m = getTableView().getItems().get(getIndex());
                Button btnGen = new Button("QR");
                btnGen.setStyle("-fx-background-color: #1B4332; -fx-text-fill: white; -fx-font-size: 11;");
                btnGen.setOnAction(e -> {
                    generarQRMesa(m);
                    mesaSeleccionada = m;
                    imgQR.setImage(qrImages.get(m.getIdMesa()));
                    lblMesaSeleccionada.setText("Mesa " + m.getNumero());
                });
                setGraphic(btnGen);
            }
        });

        try {
            List<DatosEmpresa> datos = RMIConnection.getDatosEmpresaDAO().listar();
            if (!datos.isEmpty()) {
                DatosEmpresa emp = datos.get(0);
                if (emp.getQrSecret() != null && !emp.getQrSecret().isEmpty()) secret = emp.getQrSecret();
                if (emp.getQrBaseUrl() != null && !emp.getQrBaseUrl().isEmpty()) baseUrl = emp.getQrBaseUrl();
            }
        } catch (Exception e) {}

        tblDatos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                mesaSeleccionada = newVal;
                if (qrImages.containsKey(newVal.getIdMesa())) {
                    imgQR.setImage(qrImages.get(newVal.getIdMesa()));
                    lblMesaSeleccionada.setText("Mesa " + newVal.getNumero());
                }
            }
        });

        cargarDatos();
    }

    @FXML
    private void cargarDatos() {
        try {
            List<Mesa> lista = RMIConnection.getMesaDAO().listar("");
            tblDatos.setItems(FXCollections.observableArrayList(lista));
            lblTotal.setText("Total: " + lista.size() + " mesas");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void generarTodos() {
        qrImages.clear();
        qrBuffered.clear();
        for (Mesa m : tblDatos.getItems()) {
            generarQRMesa(m);
        }
        tblDatos.refresh();
        if (mesaSeleccionada != null && qrImages.containsKey(mesaSeleccionada.getIdMesa())) {
            imgQR.setImage(qrImages.get(mesaSeleccionada.getIdMesa()));
        }
    }

    private void generarQRMesa(Mesa m) {
        try {
            String hash = sha256("mesa" + m.getIdMesa() + secret).substring(0, 16);
            String url = baseUrl + "/clienteCatalogo.jsp?mesa=" + m.getIdMesa() + "&h=" + hash;
            BitMatrix matrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, 200, 200);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            BufferedImage bImg = ImageIO.read(in);
            qrBuffered.put(m.getIdMesa(), bImg);
            Image fxImg = SwingFXUtils.toFXImage(bImg, null);
            qrImages.put(m.getIdMesa(), fxImg);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void guardarQR() {
        if (mesaSeleccionada == null || !qrBuffered.containsKey(mesaSeleccionada.getIdMesa())) {
            new Alert(Alert.AlertType.WARNING, "Primero seleccione y genere el QR de una mesa.").show();
            return;
        }
        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar QR");
        fc.setInitialFileName("QR_Mesa_" + mesaSeleccionada.getNumero() + ".png");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
        File file = fc.showSaveDialog(tblDatos.getScene().getWindow());
        if (file != null) {
            try {
                ImageIO.write(qrBuffered.get(mesaSeleccionada.getIdMesa()), "PNG", file);
                new Alert(Alert.AlertType.INFORMATION, "QR guardado en: " + file.getAbsolutePath()).show();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private String sha256(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes("UTF-8"));
        StringBuilder hex = new StringBuilder();
        for (byte b : hash) {
            String h = Integer.toHexString(0xff & b);
            if (h.length() == 1) hex.append('0');
            hex.append(h);
        }
        return hex.toString();
    }
}
