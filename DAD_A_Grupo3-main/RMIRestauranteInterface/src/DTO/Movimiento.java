package DTO;
import java.io.Serializable;
import java.time.LocalDateTime;

import java.util.List;

public class Movimiento implements Serializable {
    private static final long serialVersionUID = 1L;
    private int idMovimiento;
    private LocalDateTime fecha;
    private String nroDocumento;
    private int idCliente;
    private int idTipoMovimiento;
    private double totalPagar;
    private int idDatosEmpresa;
    private int idEstadoMovimiento;
    private int numPersonas;
    private List<Integer> listaMesas;
    private String nombreCliente;
    private String nombreEstadoMovimiento;
    private String nombreTipoMovimiento;
    private String codigoComanda;
    private int idMozo;
    private int idEstadoComanda;
    private int idMesaGrupo;
    private String nombreMozo;
    private String nombreEstadoComanda;
    private String numeroMesa;

    public Movimiento() {
    }

    public Movimiento(int idMovimiento, LocalDateTime fecha, String nroDocumento, int idCliente, int idTipoMovimiento, double totalPagar, int idDatosEmpresa, int idEstadoMovimiento, int numPersonas, List<Integer> listaMesas, String nombreCliente, String nombreEstadoMovimiento, String nombreTipoMovimiento) {
        this.idMovimiento = idMovimiento;
        this.fecha = fecha;
        this.nroDocumento = nroDocumento;
        this.idCliente = idCliente;
        this.idTipoMovimiento = idTipoMovimiento;
        this.totalPagar = totalPagar;
        this.idDatosEmpresa = idDatosEmpresa;
        this.idEstadoMovimiento = idEstadoMovimiento;
        this.numPersonas = numPersonas;
        this.listaMesas = listaMesas;
        this.nombreCliente = nombreCliente;
        this.nombreEstadoMovimiento = nombreEstadoMovimiento;
        this.nombreTipoMovimiento = nombreTipoMovimiento;
    }

    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getNroDocumento() {
        return nroDocumento;
    }

    public void setNroDocumento(String nroDocumento) {
        this.nroDocumento = nroDocumento;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdTipoMovimiento() {
        return idTipoMovimiento;
    }

    public void setIdTipoMovimiento(int idTipoMovimiento) {
        this.idTipoMovimiento = idTipoMovimiento;
    }

    public double getTotalPagar() {
        return totalPagar;
    }

    public void setTotalPagar(double totalPagar) {
        this.totalPagar = totalPagar;
    }

    public int getIdDatosEmpresa() {
        return idDatosEmpresa;
    }

    public void setIdDatosEmpresa(int idDatosEmpresa) {
        this.idDatosEmpresa = idDatosEmpresa;
    }

    public int getIdEstadoMovimiento() {
        return idEstadoMovimiento;
    }

    public void setIdEstadoMovimiento(int idEstadoMovimiento) {
        this.idEstadoMovimiento = idEstadoMovimiento;
    }

    public int getNumPersonas() {
        return numPersonas;
    }

    public void setNumPersonas(int numPersonas) {
        this.numPersonas = numPersonas;
    }

    public List<Integer> getListaMesas() {
        return listaMesas;
    }

    public void setListaMesas(List<Integer> listaMesas) {
        this.listaMesas = listaMesas;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getNombreEstadoMovimiento() {
        return nombreEstadoMovimiento;
    }

    public void setNombreEstadoMovimiento(String nombreEstadoMovimiento) {
        this.nombreEstadoMovimiento = nombreEstadoMovimiento;
    }

    public String getNombreTipoMovimiento() {
        return nombreTipoMovimiento;
    }

    public void setNombreTipoMovimiento(String nombreTipoMovimiento) {
        this.nombreTipoMovimiento = nombreTipoMovimiento;
    }

    public String getCodigoComanda() { return codigoComanda; }
    public void setCodigoComanda(String codigoComanda) { this.codigoComanda = codigoComanda; }
    public int getIdMozo() { return idMozo; }
    public void setIdMozo(int idMozo) { this.idMozo = idMozo; }
    public int getIdEstadoComanda() { return idEstadoComanda; }
    public void setIdEstadoComanda(int idEstadoComanda) { this.idEstadoComanda = idEstadoComanda; }
    public int getIdMesaGrupo() { return idMesaGrupo; }
    public void setIdMesaGrupo(int idMesaGrupo) { this.idMesaGrupo = idMesaGrupo; }
    public String getNombreMozo() { return nombreMozo; }
    public void setNombreMozo(String nombreMozo) { this.nombreMozo = nombreMozo; }
    public String getNombreEstadoComanda() { return nombreEstadoComanda; }
    public void setNombreEstadoComanda(String nombreEstadoComanda) { this.nombreEstadoComanda = nombreEstadoComanda; }
    public String getNumeroMesa() { return numeroMesa; }
    public void setNumeroMesa(String numeroMesa) { this.numeroMesa = numeroMesa; }
}
