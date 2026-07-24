package DTO;
import java.io.Serializable;

public class Mozo implements Serializable {
    private static final long serialVersionUID = 1L;
    private int idMozo;
    private String nombre;
    private String apellido;
    private String telefono;
    private String usuario;
    private String clave;
    private int idSalon;
    private boolean activo;
    private String nombreSalon;
    private String tipo;

    public Mozo() {}

    public Mozo(int idMozo, String nombre, String apellido, String usuario, String clave) {
        this.idMozo = idMozo;
        this.nombre = nombre;
        this.apellido = apellido;
        this.usuario = usuario;
        this.clave = clave;
    }

    public Mozo(int idMozo, String nombre, String apellido, String telefono, String usuario, String clave, int idSalon, boolean activo, String nombreSalon) {
        this.idMozo = idMozo;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.usuario = usuario;
        this.clave = clave;
        this.idSalon = idSalon;
        this.activo = activo;
        this.nombreSalon = nombreSalon;
        this.tipo = "mozo";
    }

    public Mozo(int idMozo, String nombre, String apellido, String telefono, String usuario, String clave, int idSalon, boolean activo, String nombreSalon, String tipo) {
        this.idMozo = idMozo;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.usuario = usuario;
        this.clave = clave;
        this.idSalon = idSalon;
        this.activo = activo;
        this.nombreSalon = nombreSalon;
        this.tipo = tipo;
    }

    public int getIdMozo() { return idMozo; }
    public void setIdMozo(int idMozo) { this.idMozo = idMozo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }
    public int getIdSalon() { return idSalon; }
    public void setIdSalon(int idSalon) { this.idSalon = idSalon; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public String getNombreSalon() { return nombreSalon; }
    public void setNombreSalon(String nombreSalon) { this.nombreSalon = nombreSalon; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
