package DTO;
import java.io.Serializable;
import java.time.LocalDateTime;

public class RegistroCaja implements Serializable{
    private static final long serialVersionUID = 1L;
    private int idRegistroCaja;
    private double montoApertura;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
    private double montoCierre;
    private String estado;
    private int idAdministrador;

    public RegistroCaja() {
    }

    public RegistroCaja(int idRegistroCaja, double montoApertura, LocalDateTime fechaCierre, double montoCierre, String estado) {
        this.idRegistroCaja = idRegistroCaja;
        this.montoApertura = montoApertura;
        this.fechaCierre = fechaCierre;
        this.montoCierre = montoCierre;
        this.estado = estado;
    }

    public int getIdRegistroCaja() {
        return idRegistroCaja;
    }

    public void setIdRegistroCaja(int idRegistroCaja) {
        this.idRegistroCaja = idRegistroCaja;
    }

    public double getMontoApertura() {
        return montoApertura;
    }

    public void setMontoApertura(double montoApertura) {
        this.montoApertura = montoApertura;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public double getMontoCierre() {
        return montoCierre;
    }

    public void setMontoCierre(double montoCierre) {
        this.montoCierre = montoCierre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(LocalDateTime fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public int getIdAdministrador() {
        return idAdministrador;
    }

    public void setIdAdministrador(int idAdministrador) {
        this.idAdministrador = idAdministrador;
    }
}
