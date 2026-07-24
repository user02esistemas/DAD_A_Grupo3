package DTO;
import java.io.Serializable;

public class TipoMovimiento implements Serializable{
    private static final long serialVersionUID = 1L;
    private int idTipoMovimiento;
    private String nombre;

    public TipoMovimiento() {
    }
    
    
    public TipoMovimiento(int idTipoMovimiento, String nombre) {
        this.idTipoMovimiento = idTipoMovimiento;
        this.nombre = nombre;
    }

    public int getIdTipoMovimiento() {
        return idTipoMovimiento;
    }

    public void setIdTipoMovimiento(int idTipoMovimiento) {
        this.idTipoMovimiento = idTipoMovimiento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    
    
}
