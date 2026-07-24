package DTO;
import java.io.Serializable;

public class MovimientoPago implements Serializable{
    private static final long serialVersionUID = 1L;
    private int idMovimiento;
    private int idFormaPago;
    private double monto;
    private String nombreFormaPago;
    
    public MovimientoPago() {
    }

    public MovimientoPago(int idMovimiento, int idFormaPago, double monto, String nombreFormaPago) {
        this.idMovimiento = idMovimiento;
        this.idFormaPago = idFormaPago;
        this.monto = monto;
        this.nombreFormaPago = nombreFormaPago;
    }

    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public int getIdFormaPago() {
        return idFormaPago;
    }

    public void setIdFormaPago(int idFormaPago) {
        this.idFormaPago = idFormaPago;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getNombreFormaPago() {
        return nombreFormaPago;
    }

    public void setNombreFormaPago(String nombreFormaPago) {
        this.nombreFormaPago = nombreFormaPago;
    }
    
}
