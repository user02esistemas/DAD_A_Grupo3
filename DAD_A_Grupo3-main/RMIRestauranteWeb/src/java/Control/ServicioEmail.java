package Control;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ServicioEmail {
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    private static final String REMITENTE = "overs.restaurante@gmail.com";
    private static final String CONTRASENA = "tu-app-password-aqui";

    public static boolean enviarVoucher(String destinatario, String asunto, String cuerpo) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", String.valueOf(SMTP_PORT));

            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new javax.mail.PasswordAuthentication(REMITENTE, CONTRASENA);
                }
            });

            MimeMessage mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(REMITENTE));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            mensaje.setSubject(asunto);
            mensaje.setText(cuerpo, "UTF-8", "html");

            Transport.send(mensaje);
            System.out.println("Email enviado a: " + destinatario);
            return true;
        } catch (MessagingException e) {
            System.out.println("Error envio email: " + e.getMessage());
            return false;
        }
    }

    public static boolean enviarComprobante(String destinatario, String nombreCliente, String comprobante, String total, String fecha) {
        String asunto = "Overo's Restaurant - Tu comprobante " + comprobante;
        String cuerpo = "<div style='font-family:Arial;max-width:500px;margin:auto;border:1px solid #D4A574;border-radius:10px;padding:20px'>" +
            "<h2 style='color:#1B4332;text-align:center'>Overo's Restaurant Campestre</h2>" +
            "<hr style='border-color:#D4A574'>" +
            "<p><b>Cliente:</b> " + nombreCliente + "</p>" +
            "<p><b>Comprobante:</b> " + comprobante + "</p>" +
            "<p><b>Fecha:</b> " + fecha + "</p>" +
            "<p style='font-size:18px;color:#1B4332'><b>Total: S/ " + total + "</b></p>" +
            "<hr style='border-color:#D4A574'>" +
            "<p style='text-align:center;color:#5C4033'>Gracias por su preferencia</p>" +
            "</div>";
        return enviarVoucher(destinatario, asunto, cuerpo);
    }
}
