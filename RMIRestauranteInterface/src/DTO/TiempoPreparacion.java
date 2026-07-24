package DTO;
import java.io.Serializable;

public class TiempoPreparacion implements Serializable {
    private static final long serialVersionUID = 1L;
    private int idTiempoPreparacion;
    private String nombre;
    private String tiempo; 

    public TiempoPreparacion() {
    }

    public TiempoPreparacion(int idTiempoPreparacion, String nombre, String tiempo) {
        this.idTiempoPreparacion = idTiempoPreparacion;
        this.nombre = nombre;
        this.tiempo = tiempo;
    }

    public int getIdTiempoPreparacion() {
        return idTiempoPreparacion;
    }

    public void setIdTiempoPreparacion(int idTiempoPreparacion) {
        this.idTiempoPreparacion = idTiempoPreparacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }
    
    
}
