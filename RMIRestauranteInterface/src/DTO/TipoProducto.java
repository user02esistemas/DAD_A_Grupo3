package DTO;
import java.io.Serializable;

public class TipoProducto implements Serializable{
    private static final long serialVersionUID = 1L;
    private int idTipoProducto;
    private String nombre;

    public TipoProducto() {
    }

    public TipoProducto(int idTipoProducto, String nombre) {
        this.idTipoProducto = idTipoProducto;
        this.nombre = nombre;
    }

    public int getIdTipoProducto() {
        return idTipoProducto;
    }

    public void setIdTipoProducto(int idTipoProducto) {
        this.idTipoProducto = idTipoProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    
}
