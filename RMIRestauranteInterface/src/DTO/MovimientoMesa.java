package DTO;
import java.io.Serializable;

public class MovimientoMesa implements Serializable {
    private static final long serialVersionUID = 1L;
    private int idMovimiento;
    private int idMesa;

    public MovimientoMesa() {
    }

    public MovimientoMesa(int idMovimiento, int idMesa) {
        this.idMovimiento = idMovimiento;
        this.idMesa = idMesa;
    }

    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public int getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(int idMesa) {
        this.idMesa = idMesa;
    }
    
    
}
