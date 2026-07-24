package DTO;
import java.io.Serializable;
import java.time.LocalDateTime;

public class CodigoPago implements Serializable {
    private static final long serialVersionUID = 2L;
    private int idCodigoPago;
    private String codigo;
    private int idMovimiento;
    private double montoTotal;
    private boolean pagado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaPago;

    public CodigoPago() {}

    public CodigoPago(int idCodigoPago, String codigo, int idMovimiento, double montoTotal, boolean pagado) {
        this.idCodigoPago = idCodigoPago;
        this.codigo = codigo;
        this.idMovimiento = idMovimiento;
        this.montoTotal = montoTotal;
        this.pagado = pagado;
    }

    public int getIdCodigoPago() { return idCodigoPago; }
    public void setIdCodigoPago(int idCodigoPago) { this.idCodigoPago = idCodigoPago; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public int getIdMovimiento() { return idMovimiento; }
    public void setIdMovimiento(int idMovimiento) { this.idMovimiento = idMovimiento; }
    public double getMontoTotal() { return montoTotal; }
    public void setMontoTotal(double montoTotal) { this.montoTotal = montoTotal; }
    public boolean isPagado() { return pagado; }
    public void setPagado(boolean pagado) { this.pagado = pagado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }
}