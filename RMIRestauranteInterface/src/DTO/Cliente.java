package DTO;
import java.io.Serializable;

public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;
    private int idCliente;
    private int idTipoDocumento;
    private String nombre;
    private String documento;
    private int idTipoCliente;
    private String apellido;
    private String telefono;
    private String sexo;
    private String direccion;
    private String email;
    private String nombreTipoDocumento;
    private String nombreTipoCliente;

    public Cliente() {}

    public Cliente(int idCliente, int idTipoDocumento, String nombre, String documento,
                   int idTipoCliente, String apellido, String telefono) {
        this.idCliente = idCliente;
        this.idTipoDocumento = idTipoDocumento;
        this.nombre = nombre;
        this.documento = documento;
        this.idTipoCliente = idTipoCliente;
        this.apellido = apellido;
        this.telefono = telefono;
    }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public int getIdTipoDocumento() { return idTipoDocumento; }
    public void setIdTipoDocumento(int idTipoDocumento) { this.idTipoDocumento = idTipoDocumento; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    public int getIdTipoCliente() { return idTipoCliente; }
    public void setIdTipoCliente(int idTipoCliente) { this.idTipoCliente = idTipoCliente; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNombreTipoDocumento() { return nombreTipoDocumento; }
    public void setNombreTipoDocumento(String nombreTipoDocumento) { this.nombreTipoDocumento = nombreTipoDocumento; }
    public String getNombreTipoCliente() { return nombreTipoCliente; }
    public void setNombreTipoCliente(String nombreTipoCliente) { this.nombreTipoCliente = nombreTipoCliente; }
}
