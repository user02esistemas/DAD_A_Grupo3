package DTO;
import java.io.Serializable;

public class EstadoMesa implements Serializable {
    private static final long serialVersionUID = 1L;
    private int idEstadoMesa;
    private String nombre;

    public EstadoMesa() {
    }

    public EstadoMesa(int idEstadoMesa, String nombre) {
        this.idEstadoMesa = idEstadoMesa;
        this.nombre = nombre;
    }

    public int getIdEstadoMesa() {
        return idEstadoMesa;
    }

    public void setIdEstadoMesa(int idEstadoMesa) {
        this.idEstadoMesa = idEstadoMesa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
}
