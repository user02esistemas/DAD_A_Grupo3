package Caja;

import Main.RMIConnection;
import DTO.RegistroCaja;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import java.time.format.DateTimeFormatter;

public class AbrirCajaController {
    @FXML private Label lblEstado;
    @FXML private Label lblInfo;
    @FXML private VBox panelApertura;
    @FXML private VBox panelCierre;
    @FXML private TextField txtMontoApertura;
    @FXML private TextField txtMontoCierre;
    @FXML private Label lblError;

    @FXML
    private void initialize() {
        verificarEstado();
    }

    private void verificarEstado() {
        try {
            boolean abierta = RMIConnection.getRegistroCajaDAO().estaAbierta();
            if (abierta) {
                lblEstado.setText("Estado: Abierta");
                lblEstado.setStyle("-fx-font-size: 14; -fx-text-fill: #1B4332; -fx-font-weight: bold;");
                panelApertura.setVisible(false);
                panelApertura.setManaged(false);
                panelCierre.setVisible(true);
                panelCierre.setManaged(true);
                cargarInfoHistorial();
            } else {
                lblEstado.setText("Estado: Cerrada");
                lblEstado.setStyle("-fx-font-size: 14; -fx-text-fill: #E74C3C; -fx-font-weight: bold;");
                panelApertura.setVisible(true);
                panelApertura.setManaged(true);
                panelCierre.setVisible(false);
                panelCierre.setManaged(false);
                lblInfo.setText("La caja se encuentra cerrada. Ingrese el monto de apertura.");
            }
        } catch (Exception e) {
            lblEstado.setText("Estado: Error al verificar");
            lblInfo.setText(e.getMessage());
        }
    }

    private void cargarInfoHistorial() {
        try {
            var historial = RMIConnection.getRegistroCajaDAO().listarHistorial();
            if (historial != null && !historial.isEmpty()) {
                RegistroCaja ultimo = historial.get(historial.size() - 1);
                lblInfo.setText(String.format("Apertura: %s\nMonto apertura: S/ %.2f",
                    ultimo.getFechaApertura() != null ? ultimo.getFechaApertura().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "-",
                    ultimo.getMontoApertura()));
            }
        } catch (Exception e) {
            lblInfo.setText("No se pudo cargar el historial");
        }
    }

    @FXML
    private void abrir() {
        try {
            double monto = Double.parseDouble(txtMontoApertura.getText().trim());
            if (monto < 0) {
                showError("El monto no puede ser negativo");
                return;
            }
            RMIConnection.getRegistroCajaDAO().abrir(monto);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Caja abierta exitosamente con S/ " + String.format("%.2f", monto));
            alert.showAndWait();
            txtMontoApertura.clear();
            verificarEstado();
        } catch (NumberFormatException e) {
            showError("Ingrese un monto válido");
        } catch (Exception e) {
            showError("Error al abrir caja: " + e.getMessage());
        }
    }

    @FXML
    private void cerrar() {
        try {
            double monto = Double.parseDouble(txtMontoCierre.getText().trim());
            if (monto < 0) {
                showError("El monto no puede ser negativo");
                return;
            }
            RMIConnection.getRegistroCajaDAO().cerrar(monto);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Caja cerrada exitosamente con S/ " + String.format("%.2f", monto));
            alert.showAndWait();
            txtMontoCierre.clear();
            verificarEstado();
        } catch (NumberFormatException e) {
            showError("Ingrese un monto válido");
        } catch (Exception e) {
            showError("Error al cerrar caja: " + e.getMessage());
        }
    }

    private void showError(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
    }
}
