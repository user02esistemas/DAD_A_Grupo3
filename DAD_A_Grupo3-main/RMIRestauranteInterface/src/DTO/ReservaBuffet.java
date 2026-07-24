package DTO;
import java.io.Serializable;
import java.time.LocalDateTime;

public class ReservaBuffet implements Serializable {
    private static final long serialVersionUID = 1L;
    private int idReserva;
    private LocalDateTime fechaHora;
    private int personas;
    private String bebidas;
    private String platosFrio;
    private String platosCaliente;
    private double total;
    private int idCliente;
    private int idEstado;
    private String nombreEstado;
    private String nombreCliente;

    public ReservaBuffet() {}

    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public int getPersonas() { return personas; }
    public void setPersonas(int personas) { this.personas = personas; }
    public String getBebidas() { return bebidas; }
    public void setBebidas(String bebidas) { this.bebidas = bebidas; }
    public String getPlatosFrio() { return platosFrio; }
    public void setPlatosFrio(String platosFrio) { this.platosFrio = platosFrio; }
    public String getPlatosCaliente() { return platosCaliente; }
    public void setPlatosCaliente(String platosCaliente) { this.platosCaliente = platosCaliente; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public int getIdEstado() { return idEstado; }
    public void setIdEstado(int idEstado) { this.idEstado = idEstado; }
    public String getNombreEstado() { return nombreEstado; }
    public void setNombreEstado(String nombreEstado) { this.nombreEstado = nombreEstado; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
}
