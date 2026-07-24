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
    @FXML private ComboBox<String> cboFormaPago;
    @FXML private TextField txtMontoRecibido;
    @FXML private Label lblVuelto;

    // Active comandas table
    @FXML private TableView<Movimiento> tblComandas;
    @FXML private TableColumn<Movimiento, String> colComMesa;
    @FXML private TableColumn<Movimiento, String> colComCodigo;
    @FXML private TableColumn<Movimiento, String> colComCliente;
    @FXML private TableColumn<Movimiento, Double> colComTotal;
    @FXML private TableColumn<Movimiento, String> colComEstado;
    @FXML private TableColumn<Movimiento, Void> colComAccion;

    private Movimiento movimientoActual;
    private ObservableList<MovimientoPedido> itemsList;
    private ObservableList<Movimiento> comandasList;
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

        txtMontoRecibido.textProperty().addListener((obs, oldVal, newVal) -> { calcularVuelto(); calcularSubtotal(); });
        calcularSubtotal();

        // Active comandas table
        colComMesa.setCellValueFactory(new PropertyValueFactory<>("numeroMesa"));
        colComCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoComanda"));
        colComCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colComTotal.setCellValueFactory(new PropertyValueFactory<>("totalPagar"));
        colComEstado.setCellValueFactory(new PropertyValueFactory<>("nombreEstadoComanda"));
        colComAccion.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Movimiento m = getTableView().getItems().get(getIndex());
                Button btn = new Button("Cobrar");
                btn.setStyle("-fx-background-color: #1B4332; -fx-text-fill: white; -fx-font-size: 11; -fx-cursor: hand;");
                btn.setOnAction(e -> seleccionarComanda(m));
                setGraphic(btn);
            }
        });
        listarComandas();
    }

    @FXML
    private void listarComandas() {
        try {
            List<Movimiento> activos = RMIConnection.getMovimientoDAO().listarComandasAbiertas();
            comandasList = FXCollections.observableArrayList();
            for (Movimiento m : activos) {
                int ec = m.getIdEstadoComanda();
                int em = m.getIdEstadoMovimiento();
                // Mostrar solo Consumiendo (ec=3 + em=1) o En Cocina
                if ((ec == 3 && em == 1) || ec == 1 || ec == 2) {
                    comandasList.add(m);
                }
            }
            tblComandas.setItems(comandasList);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void seleccionarComanda(Movimiento m) {
        try {
            movimientoActual = RMIConnection.getMovimientoDAO().buscar(m.getIdMovimiento());
            if (movimientoActual != null) cargarComanda();
        } catch (Exception e) { showError("Error al cargar comanda"); }
    }

    @FXML
    private void buscarComanda() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) { showError("Ingrese ID, código o número de mesa"); return; }
        hideError();
        try {
            Movimiento mov = null;

            // Try as mesa number: find mesa ID from numero
            try {
                int numMesa = Integer.parseInt(texto);
                List<Mesa> mesas = RMIConnection.getMesaDAO().listar("");
                for (Mesa ms : mesas) {
                    if (ms.getNumero().equals(String.valueOf(numMesa)) || ms.getNumero().equals("M" + numMesa)) {
                        int idMov = RMIConnection.getMovimientoDAO().obtenerIdMovimientoActivoPorMesa(ms.getIdMesa());
                        if (idMov > 0) { mov = RMIConnection.getMovimientoDAO().buscar(idMov); break; }
                    }
                }
            } catch (NumberFormatException ignored) {}

            // Try as movement ID or comanda code
            if (mov == null) {
                try { mov = RMIConnection.getMovimientoDAO().buscar(Integer.parseInt(texto)); }
                catch (NumberFormatException e) { mov = RMIConnection.getMovimientoDAO().buscar(texto); }
            }

            if (mov == null) { showError("Comanda no encontrada"); limpiar(); return; }
            movimientoActual = mov;
            cargarComanda();
        } catch (Exception e) { showError("Error al buscar: " + e.getMessage()); }
    }

    private void cargarComanda() {
        Movimiento mov = movimientoActual;
        lblMesa.setText(mov.getNumeroMesa() != null ? mov.getNumeroMesa() : "-");
        lblCliente.setText(mov.getNombreCliente() != null ? mov.getNombreCliente() : "-");
        lblMozo.setText(mov.getNombreMozo() != null ? mov.getNombreMozo() : "-");
        lblFecha.setText(mov.getFecha() != null ? mov.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "-");
        try {
            List<MovimientoPedido> items = RMIConnection.getMovimientoPedidoDAO().listarPorMovimiento(mov.getIdMovimiento());
            itemsList = FXCollections.observableArrayList(items);
            tblItems.setItems(itemsList);
            calcularSubtotal();
        } catch (Exception e) { showError("Error al cargar items"); }
    }

    private void calcularSubtotal() {
        if (itemsList == null) {
            lblSubtotal.setText("Pendiente: S/ 0.00");
            calcularVuelto();
            return;
        }
        double pendiente = 0;
        for (MovimientoPedido mp : itemsList) {
            if (!mp.isPagado()) pendiente += mp.getTotal();
        }
        lblSubtotal.setText(String.format("Pendiente: S/ %.2f", pendiente));
        calcularVuelto();
    }

    private void calcularVuelto() {
        if (itemsList == null) { lblVuelto.setText("Vuelto: S/ 0.00"); return; }
        double pendiente = 0;
        for (MovimientoPedido mp : itemsList) {
            if (!mp.isPagado()) pendiente += mp.getTotal();
        }
        try {
            double monto = Double.parseDouble(txtMontoRecibido.getText().trim());
            double vuelto = monto - pendiente;
            lblVuelto.setText(String.format("Vuelto: S/ %.2f", Math.max(0, vuelto)));
            lblVuelto.setStyle(vuelto < 0 ? "-fx-text-fill: #E74C3C; -fx-font-size: 14; -fx-font-weight: bold;" : "-fx-text-fill: #1B4332; -fx-font-size: 14; -fx-font-weight: bold;");
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
        } catch (Exception e) { showError("Error al verificar caja: " + e.getMessage()); return; }

        if (movimientoActual == null) { showError("Busque una comanda primero"); return; }

        List<Integer> idsChecked = new ArrayList<>();
        for (MovimientoPedido mp : itemsList) {
            if (mp.isPagado()) idsChecked.add(mp.getIdPresentacion());
        }
        if (idsChecked.isEmpty()) { showError("Seleccione al menos un item Pagado"); return; }

        String idsStr = idsChecked.stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse("");
        try {
            RMIConnection.getMovimientoPedidoDAO().marcarPagados(movimientoActual.getIdMovimiento(), idsStr);

            double montoPagado = 0;
            for (MovimientoPedido mp : itemsList) {
                if (mp.isPagado()) {
                    mp.setPagado(true);
                    montoPagado += mp.getTotal();
                }
            }

            String formaPagoStr = cboFormaPago.getSelectionModel().getSelectedItem();
            int idFormaPago = switch (formaPagoStr != null ? formaPagoStr : "Efectivo") {
                case "Tarjeta" -> 2; case "Yape" -> 4; case "Plin" -> 5; default -> 1;
            };
            try {
                MovimientoPago pago = new MovimientoPago();
                pago.setIdMovimiento(movimientoActual.getIdMovimiento());
                pago.setIdFormaPago(idFormaPago);
                pago.setMonto(montoPagado);
                RMIConnection.getMovimientoPagoDAO().insertar(pago);
            } catch (Exception ignored) {}

            boolean todosPagados = true;
            List<MovimientoPedido> pendientes = RMIConnection.getMovimientoPedidoDAO().listarNoPagados(movimientoActual.getIdMovimiento());
            if (pendientes != null && !pendientes.isEmpty()) todosPagados = false;

            if (todosPagados) {
                RMIConnection.getMovimientoDAO().cerrarVenta(movimientoActual.getIdMovimiento());
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                "Cobro realizado exitosamente" + (todosPagados ? "\nVenta cerrada. Mesas liberadas." : "\nQuedan items pendientes."));
            alert.showAndWait();

            ticketIdMovimiento = movimientoActual.getIdMovimiento();
            listarComandas();
            limpiar();
        } catch (Exception e) { showError("Error al cobrar: " + e.getMessage()); }
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
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void verTicket() {
        if (movimientoActual == null) { showError("Busque una comanda primero"); return; }
        ticketIdMovimiento = movimientoActual.getIdMovimiento();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TicketView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ticket");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void limpiar() {
        lblMesa.setText("-"); lblCliente.setText("-"); lblMozo.setText("-"); lblFecha.setText("-");
        itemsList = FXCollections.observableArrayList();
        tblItems.setItems(itemsList);
        movimientoActual = null;
        calcularSubtotal();
    }

    private void showError(String msg) { lblError.setText(msg); lblError.setVisible(true); }
    private void hideError() { lblError.setVisible(false); }
}
