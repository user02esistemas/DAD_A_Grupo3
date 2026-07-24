package DTO;
import java.io.Serializable;

public class TipoDocumento implements Serializable{
    private static final long serialVersionUID = 1L;
    private int idTipoDocumento;
    private String nombre;

    public TipoDocumento() {
    }

    public TipoDocumento(int idTipoDocumento, String nombre) {
        this.idTipoDocumento = idTipoDocumento;
        this.nombre = nombre;
    }

    public int getIdTipoDocumento() {
        return idTipoDocumento;
    }

    public void setIdTipoDocumento(int idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
}
