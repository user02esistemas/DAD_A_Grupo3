package DTO;
import java.io.Serializable;

public class EstadoMovimiento implements Serializable {
    private static final long serialVersionUID = 1L;
    private int idEstadoMovimiento;
    private String nombre;

    public EstadoMovimiento() {
    }

    public EstadoMovimiento(int idEstadoMovimiento, String nombre) {
        this.idEstadoMovimiento = idEstadoMovimiento;
        this.nombre = nombre;
    }

    public int getIdEstadoMovimiento() {
        return idEstadoMovimiento;
    }

    public void setIdEstadoMovimiento(int idEstadoMovimiento) {
        this.idEstadoMovimiento = idEstadoMovimiento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    
}
