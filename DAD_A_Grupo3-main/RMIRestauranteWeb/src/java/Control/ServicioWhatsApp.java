package Control;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ServicioWhatsApp {
    private static final String ACCOUNT_SID = "tu-account-sid-aqui";
    private static final String AUTH_TOKEN = "tu-auth-token-aqui";
    private static final String FROM_NUMBER = "whatsapp:+51900000000";
    private static final String API_URL = "https://api.twilio.com/2010-04-01/Accounts/";

    public static boolean enviarMensaje(String telefonoDestino, String mensaje) {
        try {
            String urlStr = API_URL + ACCOUNT_SID + "/Messages.json";
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            String auth = Base64.getEncoder().encodeToString(
                (ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(StandardCharsets.UTF_8)
            );
            conn.setRequestProperty("Authorization", "Basic " + auth);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String datos = "To=" + encodeURIComponent("whatsapp:" + telefonoDestino) +
                "&From=" + encodeURIComponent(FROM_NUMBER) +
                "&Body=" + encodeURIComponent(mensaje);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(datos.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 201 || responseCode == 200) {
                System.out.println("WhatsApp enviado a: " + telefonoDestino);
                return true;
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String linea;
                while ((linea = br.readLine()) != null) System.out.println(linea);
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error WhatsApp: " + e.getMessage());
            return false;
        }
    }

    public static boolean enviarVoucher(String telefonoDestino, String nombreCliente, String comprobante, String total, String fecha) {
        String mensaje = "*Overo's Restaurant Campestre*\n\n" +
            "Cliente: " + nombreCliente + "\n" +
            "Comprobante: " + comprobante + "\n" +
            "Fecha: " + fecha + "\n\n" +
            "*Total: S/ " + total + "*\n\n" +
            "Gracias por su preferencia!";
        return enviarMensaje(telefonoDestino, mensaje);
    }

    private static String encodeURIComponent(String s) {
        return s.replace("%", "%25").replace("+", "%2B").replace(" ", "%20");
    }
}
