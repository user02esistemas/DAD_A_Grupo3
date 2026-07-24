package DAO;
import DTO.EstadoPresentacion;
import Interface.IEstadoPresentacion;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstadoPresentacionDAO extends UnicastRemoteObject implements IEstadoPresentacion {

    public EstadoPresentacionDAO() throws RemoteException {
    }

    @Override
    public List<EstadoPresentacion> listar() throws RemoteException {
        List<EstadoPresentacion> lista = new ArrayList<>();
        String sql = "SELECT IdEstadoPresentacion, Nombre FROM EstadoPresentacion ORDER BY IdEstadoPresentacion";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new EstadoPresentacion(rs.getInt("IdEstadoPresentacion"), rs.getString("Nombre")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}