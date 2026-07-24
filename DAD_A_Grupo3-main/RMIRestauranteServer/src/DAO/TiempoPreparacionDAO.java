package DAO;
import DTO.TiempoPreparacion;
import Interface.ITiempoPreparacion;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TiempoPreparacionDAO extends UnicastRemoteObject implements ITiempoPreparacion {

    public TiempoPreparacionDAO() throws RemoteException {
    }

    @Override
    public List<TiempoPreparacion> listar() throws RemoteException {
        List<TiempoPreparacion> lista = new ArrayList<>();
        String sql = "SELECT IdTiempoPreparacion, Nombre, Tiempo FROM TiempoPreparacion ORDER BY IdTiempoPreparacion";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new TiempoPreparacion(
                    rs.getInt("IdTiempoPreparacion"),
                    rs.getString("Nombre"),
                    rs.getString("Tiempo")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}