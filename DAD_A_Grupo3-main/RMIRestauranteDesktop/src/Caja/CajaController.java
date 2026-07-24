package Caja;

import Main.RMIConnection;
import DTO.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CajaController {
    @FXML private TextField txtBuscar;
    @FXML private Label lblError;
    @FXML private Label lblMesa;
    @FXML private Label lblCliente;
    @FXML private Label lblMozo;
    @FXML private Label lblFecha;
    @FXML private TableView<MovimientoPedido> tblItems;
    @FXML private TableColumn<MovimientoPedido, String> colPresentacion;
    @FXML private TableColumn<MovimientoPedido, Integer> colCantidad;
    @FXML private TableColumn<MovimientoPedido, Double> colPrecioUnit;
    @FXML private TableColumn<MovimientoPedido, Double> colTotal;
    @FXML private TableColumn<MovimientoPedido, Boolean> colPagado;
    @FXML private Label lblSubtotal;
    @FXML private Label lblTotalPagar;
    @FXML private ComboBox<String> cboFormaPago;
    @FXML private TextField txtMontoRecibido;
    @FXML private Label lblVuelto;

    private Movimiento movimientoActual;
    private ObservableList<MovimientoPedido> itemsList;
    public static int ticketIdMovimiento = -1;

    @FXML
    private void initialize() {
        colPresentacion.setCellValueFactory(new PropertyValueFactory<>("nombrePresentacion"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecioUnit.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colPagado.setCellValueFactory(new PropertyValueFactory<>("pagado"));
        colPagado.setCellFactory(col -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();
            {
                checkBox.setOnAction(e -> {
                    MovimientoPedido mp = getTableView().getItems().get(getIndex());
                    mp.setPagado(checkBox.isSelected());
                    calcularSubtotal();
                });
            }
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(item);
                    setGraphic(checkBox);
                }
            }
        });

        cboFormaPago.setItems(FXCollections.observableArrayList("Efectivo", "Tarjeta", "Yape", "Plin"));
        cboFormaPago.getSelectionModel().selectFirst();

        txtMontoRecibido.textProperty().addListener((obs, oldVal, newVal) -> calcularVuelto());
        txtMontoRecibido.textProperty().addListener((obs, oldVal, newVal) -> calcularSubtotal());
        calcularSubtotal();
    }

    @FXML
    private void buscarComanda() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) {
            showError("Ingrese un ID o código de comanda");
            return;
        }
        hideError();
        try {
            Movimiento mov = null;
            try {
                int id = Integer.parseInt(texto);
                mov = RMIConnection.getMovimientoDAO().buscar(id);
            } catch (NumberFormatException e) {
                mov = RMIConnection.getMovimientoDAO().buscar(texto);
            }
            if (mov == null) {
                showError("Comanda no encontrada");
                limpiar();
                return;
            }
            movimientoActual = mov;
            lblMesa.setText(mov.getNumeroMesa() != null ? mov.getNumeroMesa() : "-");
            lblCliente.setText(mov.getNombreCliente() != null ? mov.getNombreCliente() : "-");
            lblMozo.setText(mov.getNombreMozo() != null ? mov.getNombreMozo() : "-");
            lblFecha.setText(mov.getFecha() != null ? mov.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "-");

            List<MovimientoPedido> items = RMIConnection.getMovimientoPedidoDAO().listarPorMovimiento(mov.getIdMovimiento());
            itemsList = FXCollections.observableArrayList(items);
            tblItems.setItems(itemsList);
            calcularSubtotal();
        } catch (Exception e) {
            showError("Error al buscar: " + e.getMessage());
        }
    }

    private void calcularSubtotal() {
        if (itemsList == null) {
            lblSubtotal.setText("Subtotal: S/ 0.00");
            lblTotalPagar.setText("Total a Pagar: S/ 0.00");
            calcularVuelto();
            return;
        }
        double subtotal = 0;
        for (MovimientoPedido mp : itemsList) {
            if (!mp.isPagado()) {
                subtotal += mp.getTotal();
            }
        }
        lblSubtotal.setText(String.format("Subtotal (pendientes): S/ %.2f", subtotal));
        lblTotalPagar.setText(String.format("Total a Pagar: S/ %.2f", subtotal));
        calcularVuelto();
    }

    private void calcularVuelto() {
        if (itemsList == null) {
            lblVuelto.setText("Vuelto: S/ 0.00");
            return;
        }
        double subtotal = 0;
        for (MovimientoPedido mp : itemsList) {
            if (!mp.isPagado()) {
                subtotal += mp.getTotal();
            }
        }
        try {
            double monto = Double.parseDouble(txtMontoRecibido.getText().trim());
            double vuelto = monto - subtotal;
            lblVuelto.setText(String.format("Vuelto: S/ %.2f", vuelto >= 0 ? vuelto : 0));
            lblVuelto.setStyle(vuelto < 0 ? "-fx-text-fill: #E74C3C; -fx-font-size: 13; -fx-font-weight: bold;" : "-fx-text-fill: #1B4332; -fx-font-size: 13; -fx-font-weight: bold;");
        } catch (NumberFormatException e) {
            lblVuelto.setText("Vuelto: S/ 0.00");
        }
    }

    @FXML
    private void cobrar() {
        try {
            if (!RMIConnection.getRegistroCajaDAO().estaAbierta()) {
                showError("La caja está cerrada. Abra caja antes de cobrar.");
                return;
            }
        } catch (Exception e) {
            showError("Error al verificar estado de caja: " + e.getMessage());
            return;
        }
        if (movimientoActual == null) {
            showError("Primero busque una comanda");
            return;
        }
        List<Integer> idsChecked = new ArrayList<>();
        for (MovimientoPedido mp : itemsList) {
            if (mp.isPagado()) {
                idsChecked.add(mp.getIdPresentacion());
            }
        }
        if (idsChecked.isEmpty()) {
            showError("Seleccione al menos un item para cobrar");
            return;
        }
        String idsStr = idsChecked.stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse("");
        try {
            RMIConnection.getMovimientoPedidoDAO().marcarPagados(movimientoActual.getIdMovimiento(), idsStr);

            // Persistir pago con forma seleccionada
            String formaPagoStr = cboFormaPago.getSelectionModel().getSelectedItem();
            int idFormaPago = 1; // default Efectivo
            switch (formaPagoStr != null ? formaPagoStr : "Efectivo") {
                case "Tarjeta": idFormaPago = 2; break;
                case "Yape": idFormaPago = 4; break;
                case "Plin": idFormaPago = 5; break;
                case "Efectivo":
                default: idFormaPago = 1; break;
            }
            double montoPagado = 0;
            for (MovimientoPedido mp : itemsList) {
                if (mp.isPagado()) montoPagado += mp.getTotal();
            }
            try {
                // TODO: manejar pagos multiples con misma forma (anular previo)
                DTO.MovimientoPago pago = new DTO.MovimientoPago();
                pago.setIdMovimiento(movimientoActual.getIdMovimiento());
                pago.setIdFormaPago(idFormaPago);
                pago.setMonto(montoPagado);
                RMIConnection.getMovimientoPagoDAO().insertar(pago);
            } catch (Exception e) {
                e.printStackTrace();
                // no abortar el cobro si el pago no se persistió (opcional)
            }

            boolean todosPagados = true;
            List<MovimientoPedido> pendientes = RMIConnection.getMovimientoPedidoDAO().listarNoPagados(movimientoActual.getIdMovimiento());
            if (pendientes != null && !pendientes.isEmpty()) {
                todosPagados = false;
            }
            if (todosPagados) {
                RMIConnection.getMovimientoDAO().cerrarVenta(movimientoActual.getIdMovimiento());
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Cobro realizado exitosamente");
            alert.showAndWait();
            ticketIdMovimiento = movimientoActual.getIdMovimiento();
            buscarComanda();
        } catch (Exception e) {
            showError("Error al cobrar: " + e.getMessage());
        }
    }

    @FXML
    private void abrirCaja() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AbrirCajaView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Abrir / Cerrar Caja");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void verTicket() {
        if (movimientoActual == null) {
            showError("Primero busque una comanda");
            return;
        }
        ticketIdMovimiento = movimientoActual.getIdMovimiento();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TicketView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ticket");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void limpiar() {
        lblMesa.setText("-");
        lblCliente.setText("-");
        lblMozo.setText("-");
        lblFecha.setText("-");
        itemsList = FXCollections.observableArrayList();
        tblItems.setItems(itemsList);
        calcularSubtotal();
    }

    private void showError(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
    }

    private void hideError() {
        lblError.setVisible(false);
    }
}
