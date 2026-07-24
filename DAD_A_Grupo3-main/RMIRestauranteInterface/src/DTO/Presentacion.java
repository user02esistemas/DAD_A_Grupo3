package DTO;
import java.io.Serializable;

public class Presentacion implements Serializable{
    private static final long serialVersionUID = 1L;
    private int idPresentacion;
    private int idProducto;
    private String nombre;
    private double precio;
    private int stock;
    private int idEstadoPresentacion;
    private String imagenUrl;
    private String nombreProducto;
    private String nombreEstadoPresentacion;

    public Presentacion() {
    }

    public Presentacion(int idPresentacion, int idProducto, String nombre, double precio, int stock, int idEstadoPresentacion, String imagenUrl, String nombreProducto, String nombreEstadoPresentacion) {
        this.idPresentacion = idPresentacion;
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.idEstadoPresentacion = idEstadoPresentacion;
        this.imagenUrl = imagenUrl;
        this.nombreProducto = nombreProducto;
        this.nombreEstadoPresentacion = nombreEstadoPresentacion;
    }

    public int getIdPresentacion() {
        return idPresentacion;
    }

    public void setIdPresentacion(int idPresentacion) {
        this.idPresentacion = idPresentacion;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getIdEstadoPresentacion() {
        return idEstadoPresentacion;
    }

    public void setIdEstadoPresentacion(int idEstadoPresentacion) {
        this.idEstadoPresentacion = idEstadoPresentacion;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getNombreEstadoPresentacion() {
        return nombreEstadoPresentacion;
    }

    public void setNombreEstadoPresentacion(String nombreEstadoPresentacion) {
        this.nombreEstadoPresentacion = nombreEstadoPresentacion;
    }
    
    
}
