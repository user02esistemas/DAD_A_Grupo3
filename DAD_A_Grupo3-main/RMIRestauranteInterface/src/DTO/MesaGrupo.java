package DTO;
import java.io.Serializable;

public class MesaGrupo implements Serializable {
    private static final long serialVersionUID = 1L;
    private int idMesaGrupo;
    private String nombre;
    private int numPersonas;
    private String mesas;

    public MesaGrupo() {}
    public MesaGrupo(int idMesaGrupo, String nombre, int numPersonas) {
        this.idMesaGrupo = idMesaGrupo;
        this.nombre = nombre;
        this.numPersonas = numPersonas;
    }

    public int getIdMesaGrupo() { return idMesaGrupo; }
    public void setIdMesaGrupo(int idMesaGrupo) { this.idMesaGrupo = idMesaGrupo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getNumPersonas() { return numPersonas; }
    public void setNumPersonas(int numPersonas) { this.numPersonas = numPersonas; }
    public String getMesas() { return mesas; }
    public void setMesas(String mesas) { this.mesas = mesas; }
}
