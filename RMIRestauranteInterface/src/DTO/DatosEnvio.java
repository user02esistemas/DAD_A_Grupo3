package DTO;
import java.io.Serializable;

public class DatosEnvio implements Serializable {
    private static final long serialVersionUID = 1L;
    private int idDatosEnvio;
    private String destinatario;
    private String telefono;
    private String email;
    private String direccion;
    private int idMovimiento;

    public DatosEnvio() {}
    public DatosEnvio(int idDatosEnvio, String destinatario, String telefono, String email, String direccion, int idMovimiento) {
        this.idDatosEnvio = idDatosEnvio;
        this.destinatario = destinatario;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.idMovimiento = idMovimiento;
    }

    public int getIdDatosEnvio() { return idDatosEnvio; }
    public void setIdDatosEnvio(int idDatosEnvio) { this.idDatosEnvio = idDatosEnvio; }
    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public int getIdMovimiento() { return idMovimiento; }
    public void setIdMovimiento(int idMovimiento) { this.idMovimiento = idMovimiento; }
}
