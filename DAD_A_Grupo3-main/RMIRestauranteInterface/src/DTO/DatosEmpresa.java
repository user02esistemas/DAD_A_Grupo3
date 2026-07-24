package DTO;
import java.io.Serializable;

public class DatosEmpresa implements Serializable{
    private static final long serialVersionUID = 1L;
    private int idEmpresa;
    private String ruc;
    private String razonSocial;
    private String direccion;
    private String telefono;
    private String email;
    private String qrBaseUrl;
    private String qrSecret;

    public DatosEmpresa() {
    }

    public DatosEmpresa(int idEmpresa, String ruc, String razonSocial) {
        this.idEmpresa = idEmpresa;
        this.ruc = ruc;
        this.razonSocial = razonSocial;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getQrBaseUrl() { return qrBaseUrl; }
    public void setQrBaseUrl(String qrBaseUrl) { this.qrBaseUrl = qrBaseUrl; }
    public String getQrSecret() { return qrSecret; }
    public void setQrSecret(String qrSecret) { this.qrSecret = qrSecret; }
}
