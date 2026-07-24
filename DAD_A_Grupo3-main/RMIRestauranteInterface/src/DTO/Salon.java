package DTO;
import java.io.Serializable;

public class Salon implements Serializable {
    private static final long serialVersionUID = 1L;
    private int idSalon;
    private String nombre;
    
    public Salon() {}

    public Salon(int idSalon, String nombre) {
        this.idSalon = idSalon;
        this.nombre = nombre;
    }

    public int getIdSalon() { 
        return idSalon; 
    }
    public void setIdSalon(int idSalon) { 
        this.idSalon = idSalon; 
    }
    public String getNombre() { 
        return nombre; 
    }
    public void setNombre(String nombre) {
        this.nombre = nombre; 
    }
}
