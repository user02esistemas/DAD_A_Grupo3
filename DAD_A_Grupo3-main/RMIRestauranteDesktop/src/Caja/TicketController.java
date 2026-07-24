package Caja;

import Main.RMIConnection;
import DTO.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TicketController {
    @FXML private Label lblEmpresa;
    @FXML private Label lblRuc;
    @FXML private Label lblDireccion;
    @FXML private Label lblTelefono;
    @FXML private Label lblComandaInfo;
    @FXML private VBox vbItems;
    @FXML private Label lblSubtotal;
    @FXML private Label lblTotal;
    @FXML private Label lblFormaPago;
    @FXML private Label lblEstado;

    @FXML
    private void initialize() {
        int idMov = CajaController.ticketIdMovimiento;
        if (idMov == -1) {
            lblComandaInfo.setText("No hay comanda seleccionada");
            return;
        }
        try {
            Movimiento mov = RMIConnection.getMovimientoDAO().buscar(idMov);
            if (mov == null) {
                lblComandaInfo.setText("Comanda no encontrada");
                return;
            }

            try {
                List<DatosEmpresa> empresas = RMIConnection.getDatosEmpresaDAO().listar();
                if (empresas != null && !empresas.isEmpty()) {
                    DatosEmpresa emp = empresas.get(0);
                    lblEmpresa.setText(emp.getRazonSocial() != null ? emp.getRazonSocial() : "Restaurante");
                    lblRuc.setText("RUC: " + (emp.getRuc() != null ? emp.getRuc() : "-"));
                    lblDireccion.setText(emp.getDireccion() != null ? emp.getDireccion() : "");
                    lblTelefono.setText(emp.getTelefono() != null ? "Tel: " + emp.getTelefono() : "");
                }
            } catch (Exception ignored) {}

            String fechaStr = mov.getFecha() != null ? mov.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "-";
            lblComandaInfo.setText(String.format(
                "Comanda: %s\nMesa: %s | Mozo: %s\nFecha: %s\nCliente: %s",
                mov.getCodigoComanda() != null ? mov.getCodigoComanda() : "# " + mov.getIdMovimiento(),
                mov.getNumeroMesa() != null ? mov.getNumeroMesa() : "-",
                mov.getNombreMozo() != null ? mov.getNombreMozo() : "-",
                fechaStr,
                mov.getNombreCliente() != null ? mov.getNombreCliente() : "-"));

            List<MovimientoPedido> items = RMIConnection.getMovimientoPedidoDAO().listarPorMovimiento(idMov);
            double subtotal = 0;
            vbItems.getChildren().clear();
            for (MovimientoPedido mp : items) {
                HBox row = new HBox(8);
                Text nombre = new Text(mp.getCantidad() + " x " + mp.getNombrePresentacion());
                nombre.setStyle("-fx-font-size: 11;");
                Text precio = new Text(String.format("S/ %.2f", mp.getTotal()));
                precio.setStyle("-fx-font-size: 11;");
                precio.setTextAlignment(TextAlignment.RIGHT);
                HBox.setHgrow(precio, javafx.scene.layout.Priority.ALWAYS);
                precio.setStroke(javafx.scene.paint.Color.TRANSPARENT);
                row.getChildren().addAll(nombre, precio);
                vbItems.getChildren().add(row);
                subtotal += mp.getTotal();
            }

            lblSubtotal.setText(String.format("Subtotal: S/ %.2f", subtotal));
            lblTotal.setText(String.format("TOTAL: S/ %.2f", mov.getTotalPagar() > 0 ? mov.getTotalPagar() : subtotal));
            lblEstado.setText("Estado: " + (mov.getNombreEstadoMovimiento() != null ? mov.getNombreEstadoMovimiento() : mov.getNombreEstadoComanda() != null ? mov.getNombreEstadoComanda() : "-"));

            try {
                java.util.List<DTO.MovimientoPago> pagos = RMIConnection.getMovimientoPagoDAO().listarPorMovimiento(idMov);
                if (pagos != null && !pagos.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (DTO.MovimientoPago p : pagos) {
                        if (sb.length() > 0) sb.append(", ");
                        sb.append(p.getNombreFormaPago() != null ? p.getNombreFormaPago() : "Forma #" + p.getIdFormaPago());
                        sb.append(String.format(": S/ %.2f", p.getMonto()));
                    }
                    lblFormaPago.setText("Pago: " + sb.toString());
                } else {
                    lblFormaPago.setText("Pago: (no registrado)");
                }
            } catch (Exception e) {
                e.printStackTrace();
                lblFormaPago.setText("Pago: error al cargar");
            }
        } catch (Exception e) {
            lblComandaInfo.setText("Error al cargar ticket: " + e.getMessage());
        }
    }

    @FXML
    private void imprimir() {
        int idMov = CajaController.ticketIdMovimiento;
        System.out.println("=== TICKET COMANDA #" + idMov + " ===");
        System.out.println("Imprimiendo...");
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Ticket enviado a impresora (simulado)");
        alert.showAndWait();
    }

    @FXML
    private void cerrar() {
        javafx.stage.Stage stage = (javafx.stage.Stage) lblTotal.getScene().getWindow();
        stage.close();
    }
}
