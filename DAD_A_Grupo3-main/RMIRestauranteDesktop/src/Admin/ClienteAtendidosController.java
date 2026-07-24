package Admin;

import Main.RMIConnection;
import DTO.Cliente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

public class ClienteAtendidosController {
    @FXML private TableView<Cliente> tblDatos;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colApellido;
    @FXML private TableColumn<Cliente, String> colDocumento;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colEmail;
    @FXML private TextField txtBuscar;
    @FXML private Label lblTotal;

    private List<Cliente> allData;

    @FXML
    private void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colDocumento.setCellValueFactory(new PropertyValueFactory<>("documento"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        cargarDatos();
    }

    @FXML
    private void cargarDatos() {
        try {
            allData = RMIConnection.getClienteDAO().listarAtendidos();
            tblDatos.setItems(FXCollections.observableArrayList(allData));
            lblTotal.setText("Total: " + allData.size() + " clientes");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void buscar() {
        String busq = txtBuscar.getText().toLowerCase();
        ObservableList<Cliente> filtered = FXCollections.observableArrayList();
        for (Cliente c : allData) {
            if (c.getNombre().toLowerCase().contains(busq)) {
                filtered.add(c);
            }
        }
        tblDatos.setItems(filtered);
        lblTotal.setText("Total: " + filtered.size() + " clientes");
    }
}
