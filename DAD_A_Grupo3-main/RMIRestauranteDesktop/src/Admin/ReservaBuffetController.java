package Admin;

import Main.RMIConnection;
import DTO.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Insets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReservaBuffetController {
    @FXML private TableView<ReservaBuffet> tblDatos;
    @FXML private TableColumn<ReservaBuffet, Integer> colId;
    @FXML private TableColumn<ReservaBuffet, String> colFecha;
    @FXML private TableColumn<ReservaBuffet, Integer> colPersonas;
    @FXML private TableColumn<ReservaBuffet, String> colCliente;
    @FXML private TableColumn<ReservaBuffet, String> colEstado;
    @FXML private TableColumn<ReservaBuffet, Double> colTotal;
    @FXML private TableColumn<ReservaBuffet, String> colAcciones;
    @FXML private Label lblTotal;

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idReserva"));
        colFecha.setCellValueFactory(cell -> {
            LocalDateTime dt = cell.getValue().getFechaHora();
            return new javafx.beans.binding.StringBinding() {
                @Override protected String computeValue() { return dt != null ? dt.format(fmt) : ""; }
            };
        });
        colPersonas.setCellValueFactory(new PropertyValueFactory<>("personas"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("nombreEstado"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colAcciones.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                ReservaBuffet r = getTableView().getItems().get(getIndex());
                Button btnConf = new Button("Confirmar");
                btnConf.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-size: 11;");
                btnConf.setOnAction(e -> confirmar(r));
                Button btnCancel = new Button("Cancelar");
                btnCancel.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-size: 11;");
                btnCancel.setOnAction(e -> cancelar(r));
                setGraphic(new javafx.scene.layout.HBox(4, btnConf, btnCancel));
            }
        });
        cargarDatos();
    }

    @FXML private void cargarDatos() {
        try {
            List<ReservaBuffet> lista = RMIConnection.getReservaBuffetDAO().listar();
            tblDatos.setItems(FXCollections.observableArrayList(lista));
            lblTotal.setText("Total: " + lista.size() + " reservas");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void nuevo() { showDialog(null); }

    private void showDialog(ReservaBuffet existing) {
        Dialog<ReservaBuffet> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Nueva Reserva" : "Editar Reserva");
        ButtonType saveType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        DatePicker dpFecha = new DatePicker(existing != null && existing.getFechaHora() != null ? existing.getFechaHora().toLocalDate() : null);
        dpFecha.setPromptText("Fecha");
        Spinner<Integer> spHora = new Spinner<>(0, 23, existing != null && existing.getFechaHora() != null ? existing.getFechaHora().getHour() : 12);
        spHora.setEditable(true);
        spHora.setPrefWidth(70);
        Spinner<Integer> spMinuto = new Spinner<>(0, 59, existing != null && existing.getFechaHora() != null ? existing.getFechaHora().getMinute() : 0);
        spMinuto.setEditable(true);
        spMinuto.setPrefWidth(70);
        javafx.scene.layout.HBox fechaBox = new javafx.scene.layout.HBox(4, dpFecha, new Label("Hora:"), spHora, new Label(":"), spMinuto);

        Spinner<Integer> spPersonas = new Spinner<>(1, 50, existing != null ? existing.getPersonas() : 1);
        spPersonas.setEditable(true);

        ComboBox<String> cbCliente = new ComboBox<>();
        cbCliente.setPromptText("Seleccionar Cliente");
        try {
            List<Cliente> clientes = RMIConnection.getClienteDAO().listar("");
            for (Cliente c : clientes)
                cbCliente.getItems().add(c.getIdCliente() + " - " + c.getNombre() + " " + c.getApellido());
            if (existing != null && existing.getIdCliente() > 0) {
                for (int i = 0; i < cbCliente.getItems().size(); i++)
                    if (cbCliente.getItems().get(i).startsWith(existing.getIdCliente() + " - "))
                        cbCliente.getSelectionModel().select(i);
            } else if (!cbCliente.getItems().isEmpty()) {
                cbCliente.getSelectionModel().selectFirst();
            }
        } catch (Exception e) { e.printStackTrace(); }

        TextField txtBebidas = new TextField(existing != null ? existing.getBebidas() : "");
        txtBebidas.setPromptText("Bebidas");
        TextField txtPlatosFrio = new TextField(existing != null ? existing.getPlatosFrio() : "");
        txtPlatosFrio.setPromptText("Platos Frios");
        TextField txtPlatosCaliente = new TextField(existing != null ? existing.getPlatosCaliente() : "");
        txtPlatosCaliente.setPromptText("Platos Calientes");
        TextField txtTotal = new TextField(existing != null ? String.valueOf(existing.getTotal()) : "0");
        txtTotal.setPromptText("Total");

        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(8, fechaBox, spPersonas, cbCliente, txtBebidas, txtPlatosFrio, txtPlatosCaliente, txtTotal);
        vbox.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(vbox);

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                ReservaBuffet r = existing != null ? existing : new ReservaBuffet();
                if (dpFecha.getValue() != null)
                    r.setFechaHora(dpFecha.getValue().atTime(spHora.getValue(), spMinuto.getValue()));
                r.setPersonas(spPersonas.getValue());
                String selCli = cbCliente.getValue();
                if (selCli != null) try { r.setIdCliente(Integer.parseInt(selCli.split(" - ")[0])); } catch (Exception ignored) {}
                r.setBebidas(txtBebidas.getText());
                r.setPlatosFrio(txtPlatosFrio.getText());
                r.setPlatosCaliente(txtPlatosCaliente.getText());
                try { r.setTotal(Double.parseDouble(txtTotal.getText())); } catch (Exception ignored) {}
                return r;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(r -> {
            try {
                if (existing == null) RMIConnection.getReservaBuffetDAO().insertar(r);
                else RMIConnection.getReservaBuffetDAO().actualizar(r);
                cargarDatos();
            } catch (Exception e) { e.printStackTrace(); }
        });
    }

    private void confirmar(ReservaBuffet r) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Confirmar reserva #" + r.getIdReserva() + "?");
        alert.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK) {
                try { RMIConnection.getReservaBuffetDAO().confirmar(r.getIdReserva()); cargarDatos(); }
                catch (Exception e) { e.printStackTrace(); }
            }
        });
    }

    private void cancelar(ReservaBuffet r) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Cancelar reserva #" + r.getIdReserva() + "?");
        alert.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK) {
                try { RMIConnection.getReservaBuffetDAO().cancelar(r.getIdReserva()); cargarDatos(); }
                catch (Exception e) { e.printStackTrace(); }
            }
        });
    }
}
