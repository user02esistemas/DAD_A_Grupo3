package DTO;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Notificacion implements Serializable {
    private static final long serialVersionUID = 2L;
    private int idNotificacion;
    private int idMovimiento;
    private String tipo;
    private String mensaje;
    private int idDestinatario;
    private String tipoDestinatario;
    private boolean leida;
    private LocalDateTime fecha;

    public Notificacion() {}

    public Notificacion(int idMovimiento, String tipo, String mensaje, int idDestinatario, String tipoDestinatario) {
        this.idMovimiento = idMovimiento;
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.idDestinatario = idDestinatario;
        this.tipoDestinatario = tipoDestinatario;
    }

    public int getIdNotificacion() { return idNotificacion; }
    public void setIdNotificacion(int idNotificacion) { this.idNotificacion = idNotificacion; }
    public int getIdMovimiento() { return idMovimiento; }
    public void setIdMovimiento(int idMovimiento) { this.idMovimiento = idMovimiento; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public int getIdDestinatario() { return idDestinatario; }
    public void setIdDestinatario(int idDestinatario) { this.idDestinatario = idDestinatario; }
    public String getTipoDestinatario() { return tipoDestinatario; }
    public void setTipoDestinatario(String tipoDestinatario) { this.tipoDestinatario = tipoDestinatario; }
    public boolean isLeida() { return leida; }
    public void setLeida(boolean leida) { this.leida = leida; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
