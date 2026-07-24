package DTO;
import java.io.Serializable;
import java.time.LocalDateTime;

public class MovimientoPedido implements Serializable{
    private static final long serialVersionUID = 1L;
    private int idMovimiento;
    private int idPresentacion;
    private int cantidad;
    private double precioUnitario;
    private double total;
    private int idEstadoPedido;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private int tiempoEstimado;
    private String nombrePresentacion;
    private double precioPresentacion;
    private String nombreProducto;
    private String nombreEstadoPedido;
    private int idCliente;
    private String numeroMesa;
    private String nombreMozo;
    private boolean pagado;

    public MovimientoPedido() {
    }

    public MovimientoPedido(int idMovimiento, int idPresentacion, int cantidad, double precioUnitario, double total, int idEstadoPedido, String nombrePresentacion, double precioPresentacion) {
        this.idMovimiento = idMovimiento;
        this.idPresentacion = idPresentacion;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.total = total;
        this.idEstadoPedido = idEstadoPedido;
        this.nombrePresentacion = nombrePresentacion;
        this.precioPresentacion = precioPresentacion;
    }

    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public int getIdPresentacion() {
        return idPresentacion;
    }

    public void setIdPresentacion(int idPresentacion) {
        this.idPresentacion = idPresentacion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getNombrePresentacion() {
        return nombrePresentacion;
    }

    public void setNombrePresentacion(String nombrePresentacion) {
        this.nombrePresentacion = nombrePresentacion;
    }

    public double getPrecioPresentacion() {
        return precioPresentacion;
    }

    public void setPrecioPresentacion(double precioPresentacion) {
        this.precioPresentacion = precioPresentacion;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNumeroMesa() {
        return numeroMesa;
    }

    public void setNumeroMesa(String numeroMesa) {
        this.numeroMesa = numeroMesa;
    }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
    public int getIdEstadoPedido() { return idEstadoPedido; }
    public void setIdEstadoPedido(int idEstadoPedido) { this.idEstadoPedido = idEstadoPedido; }
    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }
    public int getTiempoEstimado() { return tiempoEstimado; }
    public void setTiempoEstimado(int tiempoEstimado) { this.tiempoEstimado = tiempoEstimado; }
    public String getNombreEstadoPedido() { return nombreEstadoPedido; }
    public void setNombreEstadoPedido(String nombreEstadoPedido) { this.nombreEstadoPedido = nombreEstadoPedido; }
    public String getNombreMozo() { return nombreMozo; }
    public void setNombreMozo(String nombreMozo) { this.nombreMozo = nombreMozo; }
    public boolean isPagado() { return pagado; }
    public void setPagado(boolean pagado) { this.pagado = pagado; }
}
