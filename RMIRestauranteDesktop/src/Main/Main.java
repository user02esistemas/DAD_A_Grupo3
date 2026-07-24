package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private static Stage primaryStage;
    private static final String CSS = Main.class.getResource("/CSS/estilo.css") != null
        ? Main.class.getResource("/CSS/estilo.css").toExternalForm() : null;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        boolean connected = RMIConnection.inicializar();
        if (!connected) {
            System.err.println("No se pudo conectar al servidor RMI");
        }
        navigateTo("Login/LoginView.fxml", "Overo's Restaurant - Inicio");
    }

    public static void navigateTo(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(Main.class.getResource("/" + fxmlPath));
            Scene scene = new Scene(root);
            if (CSS != null) scene.getStylesheets().add(CSS);
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Error cargando vista: " + fxmlPath + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
