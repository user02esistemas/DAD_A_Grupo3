package DTO;
import java.io.Serializable;

public class EstadoComanda implements Serializable {
    private static final long serialVersionUID = 1L;
    private int idEstadoComanda;
    private String nombre;

    public EstadoComanda() {}

    public EstadoComanda(int idEstadoComanda, String nombre) {
        this.idEstadoComanda = idEstadoComanda;
        this.nombre = nombre;
    }

    public int getIdEstadoComanda() { return idEstadoComanda; }
    public void setIdEstadoComanda(int idEstadoComanda) { this.idEstadoComanda = idEstadoComanda; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
