package DTO;
import java.io.Serializable;


public class Producto implements Serializable{
    private static final long serialVersionUID = 1L;
    private int idProducto;
    private int idTipoProducto;
    private String nombre;
    private String descripcion;
    private int idTiempoPreparacion;
    private int idEstadoProducto;
    private String nombreTipoProducto;
    private String nombreTiempoPreparacion;
    private String nombreEstadoProducto;
    private double precioMinimo;
    private String rutaImg;

    public Producto() {
    }

    public Producto(int idProducto, int idTipoProducto, String nombre, String descripcion, int idTiempoPreparacion, int idEstadoProducto, String nombreTipoProducto, String nombreTiempoPreparacion, String nombreEstadoProducto) {
        this.idProducto = idProducto;
        this.idTipoProducto = idTipoProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.idTiempoPreparacion = idTiempoPreparacion;
        this.idEstadoProducto = idEstadoProducto;
        this.nombreTipoProducto = nombreTipoProducto;
        this.nombreTiempoPreparacion = nombreTiempoPreparacion;
        this.nombreEstadoProducto = nombreEstadoProducto;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdTipoProducto() {
        return idTipoProducto;
    }

    public void setIdTipoProducto(int idTipoProducto) {
        this.idTipoProducto = idTipoProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getIdTiempoPreparacion() {
        return idTiempoPreparacion;
    }

    public void setIdTiempoPreparacion(int idTiempoPreparacion) {
        this.idTiempoPreparacion = idTiempoPreparacion;
    }

    public int getIdEstadoProducto() {
        return idEstadoProducto;
    }

    public void setIdEstadoProducto(int idEstadoProducto) {
        this.idEstadoProducto = idEstadoProducto;
    }

    public String getNombreTipoProducto() {
        return nombreTipoProducto;
    }

    public void setNombreTipoProducto(String nombreTipoProducto) {
        this.nombreTipoProducto = nombreTipoProducto;
    }

    public String getNombreTiempoPreparacion() {
        return nombreTiempoPreparacion;
    }

    public void setNombreTiempoPreparacion(String nombreTiempoPreparacion) {
        this.nombreTiempoPreparacion = nombreTiempoPreparacion;
    }

    public String getNombreEstadoProducto() {
        return nombreEstadoProducto;
    }

    public void setNombreEstadoProducto(String nombreEstadoProducto) {
        this.nombreEstadoProducto = nombreEstadoProducto;
    }

    public double getPrecioMinimo() {
        return precioMinimo;
    }

    public void setPrecioMinimo(double precioMinimo) {
        this.precioMinimo = precioMinimo;
    }

    public String getRutaImg() {
        return rutaImg;
    }

    public void setRutaImg(String rutaImg) {
        this.rutaImg = rutaImg;
    }
}
