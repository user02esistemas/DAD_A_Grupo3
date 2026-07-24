package Login;

import Main.Main;
import Main.RMIConnection;
import DTO.Administrador;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtClave;
    @FXML private Label lblError;

    @FXML
    private void handleLogin() {
        String usuario = txtUsuario.getText().trim();
        String clave = txtClave.getText().trim();

        if (usuario.isEmpty() || clave.isEmpty()) {
            showError("Ingrese usuario y contraseña");
            return;
        }

        try {
            Administrador admin = RMIConnection.getAdministradorDAO().login(usuario, clave);
            if (admin != null) {
                Main.navigateTo("Admin/Dashboard.fxml", "Overo's - Panel de Administración");
                return;
            }
            showError("Credenciales incorrectas");
        } catch (Exception e) {
            showError("Error de conexión: " + e.getMessage());
        }
    }

    private void showError(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
    }
}
