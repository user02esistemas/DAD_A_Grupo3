package Admin;

import Main.Main;
import Main.RMIConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class DashboardController {
    @FXML private StackPane contentArea;

    @FXML
    private void initialize() {
        cargarStats();
    }

    private void cargarStats() {
        VBox stats = new VBox(16);
        stats.setStyle("-fx-padding: 30; -fx-alignment: center;");

        try {
            double ventas = RMIConnection.getMovimientoDAO().totalVentasHoy();
            int comandas = RMIConnection.getMovimientoDAO().listarComandasAbiertas().size();
            int mesasOcupadas = 0;
            for (var m : RMIConnection.getMesaDAO().listar("")) {
                if ("Ocupada".equalsIgnoreCase(m.getNombreEstadoMesa()))
                    mesasOcupadas++;
            }
            int reservas = RMIConnection.getReservaBuffetDAO().listar().size();

            stats.getChildren().addAll(
                tarjeta("Ventas Hoy", "S/." + String.format("%.2f", ventas), "#1B4332"),
                tarjeta("Comandas Abiertas", String.valueOf(comandas), "#2D6A4F"),
                tarjeta("Mesas Ocupadas", mesasOcupadas + " / " + (mesasOcupadas + contarMesasLibres()), "#5C4033"),
                tarjeta("Reservas Buffet", String.valueOf(reservas), "#D4A574")
            );
        } catch (Exception e) {
            stats.getChildren().add(new Label("Error cargando estadisticas: " + e.getMessage()));
        }

        contentArea.getChildren().setAll(stats);
    }

    private int contarMesasLibres() throws Exception {
        int libres = 0;
        for (var m : RMIConnection.getMesaDAO().listar("")) {
            if ("Libre".equalsIgnoreCase(m.getNombreEstadoMesa()))
                libres++;
        }
        return libres;
    }

    private javafx.scene.layout.VBox tarjeta(String titulo, String valor, String color) {
        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-text-fill: #888; -fx-font-size: 13;");
        Label lblValor = new Label(valor);
        lblValor.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 32; -fx-font-weight: bold;");
        VBox card = new VBox(4, lblTitulo, lblValor);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 12; "
            + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        card.setMaxWidth(320);
        return card;
    }

    @FXML
    private void handleLogout() {
        Main.navigateTo("Login/LoginView.fxml", "Overo's Restaurant - Inicio");
    }

    @FXML
    private void navigate(javafx.event.ActionEvent event) {
        Button btn = (Button) event.getSource();
        String id = btn.getId();
        String fxml = "";
        switch (id) {
            case "btnDash": cargarStats(); return;
            case "btnProd": fxml = "ProductoView.fxml"; break;
            case "btnPres": fxml = "PresentacionView.fxml"; break;
            case "btnMesas": fxml = "MesaView.fxml"; break;
            case "btnSalon": fxml = "SalonView.fxml"; break;
            case "btnMozos": fxml = "MozoView.fxml"; break;
            case "btnClientes": fxml = "ClienteAtendidosView.fxml"; break;
            case "btnReservas": fxml = "ReservaBuffetView.fxml"; break;
            case "btnQR": fxml = "QRView.fxml"; break;
            case "btnConfig": fxml = "ConfigView.fxml"; break;
            case "btnCaja": fxml = "../Caja/CajaView.fxml"; break;
            default: return;
        }
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxml));
            contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            System.err.println("Error cargando: " + fxml + " - " + e.getMessage());
        }
    }
}
