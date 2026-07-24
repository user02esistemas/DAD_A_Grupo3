package Admin;

import Main.RMIConnection;
import DTO.DatosEmpresa;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.util.List;

public class ConfigController {
    @FXML private TextField txtRuc, txtRazonSocial, txtDireccion, txtTelefono, txtEmail, txtQrBaseUrl, txtQrSecret;
    @FXML private Label lblStatus;

    private DatosEmpresa datosActuales;

    @FXML
    private void initialize() {
        try {
            List<DatosEmpresa> lista = RMIConnection.getDatosEmpresaDAO().listar();
            if (!lista.isEmpty()) {
                datosActuales = lista.get(0);
                txtRuc.setText(nvl(datosActuales.getRuc()));
                txtRazonSocial.setText(nvl(datosActuales.getRazonSocial()));
                txtDireccion.setText(nvl(datosActuales.getDireccion()));
                txtTelefono.setText(nvl(datosActuales.getTelefono()));
                txtEmail.setText(nvl(datosActuales.getEmail()));
                txtQrBaseUrl.setText(nvl(datosActuales.getQrBaseUrl()));
                txtQrSecret.setText(nvl(datosActuales.getQrSecret()));
            }
        } catch (Exception e) {
            mostrarError("Error cargando datos: " + e.getMessage());
        }
    }

    private String nvl(String s) {
        return s != null ? s : "";
    }

    @FXML
    private void guardar() {
        if (datosActuales == null) {
            mostrarError("No hay datos de empresa para actualizar en la base de datos.");
            return;
        }
        try {
            datosActuales.setRuc(txtRuc.getText());
            datosActuales.setRazonSocial(txtRazonSocial.getText());
            datosActuales.setDireccion(txtDireccion.getText());
            datosActuales.setTelefono(txtTelefono.getText());
            datosActuales.setEmail(txtEmail.getText());
            datosActuales.setQrBaseUrl(txtQrBaseUrl.getText());
            datosActuales.setQrSecret(txtQrSecret.getText());

            boolean ok = RMIConnection.getDatosEmpresaDAO().actualizar(datosActuales);
            if (ok) {
                lblStatus.setText("Datos guardados correctamente.");
                lblStatus.setStyle("-fx-text-fill: #27AE60;");
            } else {
                lblStatus.setText("Error al guardar.");
                lblStatus.setStyle("-fx-text-fill: #E74C3C;");
            }
        } catch (Exception e) {
            mostrarError("Error al guardar: " + e.getMessage());
        }
    }

    private void mostrarError(String msg) {
        lblStatus.setText(msg);
        lblStatus.setStyle("-fx-text-fill: #E74C3C;");
    }
}
