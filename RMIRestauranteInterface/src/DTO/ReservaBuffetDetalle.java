package DTO;
import java.io.Serializable;

public class ReservaBuffetDetalle implements Serializable {
    private static final long serialVersionUID = 1L;
    private int idReservaBuffet;
    private int idPresentacion;
    private int cantidad;
    private String nombrePresentacion;
    private double precio;

    public ReservaBuffetDetalle() {}

    public ReservaBuffetDetalle(int idReservaBuffet, int idPresentacion, int cantidad, String nombrePresentacion, double precio) {
        this.idReservaBuffet = idReservaBuffet;
        this.idPresentacion = idPresentacion;
        this.cantidad = cantidad;
        this.nombrePresentacion = nombrePresentacion;
        this.precio = precio;
    }

    public int getIdReservaBuffet() { return idReservaBuffet; }
    public void setIdReservaBuffet(int idReservaBuffet) { this.idReservaBuffet = idReservaBuffet; }
    public int getIdPresentacion() { return idPresentacion; }
    public void setIdPresentacion(int idPresentacion) { this.idPresentacion = idPresentacion; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public String getNombrePresentacion() { return nombrePresentacion; }
    public void setNombrePresentacion(String nombrePresentacion) { this.nombrePresentacion = nombrePresentacion; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
}
