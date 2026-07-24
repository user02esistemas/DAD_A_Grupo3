package DTO;
import java.io.Serializable;

public class EstadoPresentacion implements Serializable{
    private static final long serialVersionUID = 1L;
    private int idEstadoPresentacion;
    private String nombre;

    public EstadoPresentacion() {
    }

    public EstadoPresentacion(int idEstadoPresentacion, String nombre) {
        this.idEstadoPresentacion = idEstadoPresentacion;
        this.nombre = nombre;
    }

    public int getIdEstadoPresentacion() {
        return idEstadoPresentacion;
    }

    public void setIdEstadoPresentacion(int idEstadoPresentacion) {
        this.idEstadoPresentacion = idEstadoPresentacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
