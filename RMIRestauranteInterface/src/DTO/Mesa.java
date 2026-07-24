package DTO;
import java.io.Serializable;

public class Mesa implements Serializable{
    private static final long serialVersionUID = 1L;
    private int idMesa;
    private int idSalon;
    private String numero;
    private int capacidad;
    private int idEstadoMesa;
    private String nombreSalon;
    private String nombreEstadoMesa;

    public Mesa() {
    }

    public Mesa(int idMesa, int idSalon, String numero, int capacidad, int idEstadoMesa, String nombreSalon, String nombreEstadoMesa) {
        this.idMesa = idMesa;
        this.idSalon = idSalon;
        this.numero = numero;
        this.capacidad = capacidad;
        this.idEstadoMesa = idEstadoMesa;
        this.nombreSalon = nombreSalon;
        this.nombreEstadoMesa = nombreEstadoMesa;
    }

    public int getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(int idMesa) {
        this.idMesa = idMesa;
    }

    public int getIdSalon() {
        return idSalon;
    }

    public void setIdSalon(int idSalon) {
        this.idSalon = idSalon;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public int getIdEstadoMesa() {
        return idEstadoMesa;
    }

    public void setIdEstadoMesa(int idEstadoMesa) {
        this.idEstadoMesa = idEstadoMesa;
    }

    public String getNombreSalon() {
        return nombreSalon;
    }

    public void setNombreSalon(String nombreSalon) {
        this.nombreSalon = nombreSalon;
    }

    public String getNombreEstadoMesa() {
        return nombreEstadoMesa;
    }

    public void setNombreEstadoMesa(String nombreEstadoMesa) {
        this.nombreEstadoMesa = nombreEstadoMesa;
    }
    
}
