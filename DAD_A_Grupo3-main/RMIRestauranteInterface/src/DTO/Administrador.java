package DTO;
import java.io.Serializable;

public class Administrador implements Serializable{
    private static final long serialVersionUID = 1L;
    private int idAdministrador;
    private String nombre;
    private String usuario;
    private String clave;
    
    public Administrador(){
        
    }

    public Administrador(int idAdministrador, String nombre, String usuario, String clave) {
        this.idAdministrador = idAdministrador;
        this.nombre = nombre;
        this.usuario = usuario;
        this.clave = clave;
    }

    public int getIdAdministrador() {
        return idAdministrador;
    }

    public void setIdAdministrador(int idAdministrador) {
        this.idAdministrador = idAdministrador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }
    
    
}
