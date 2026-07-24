package DAO;
import DTO.EstadoMesa;
import Interface.IEstadoMesa;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstadoMesaDAO extends UnicastRemoteObject implements IEstadoMesa {

    public EstadoMesaDAO() throws RemoteException {
    }

    @Override
    public List<EstadoMesa> listar() throws RemoteException {
        List<EstadoMesa> lista = new ArrayList<>();
        String sql = "SELECT IdEstadoMesa, Nombre FROM EstadoMesa ORDER BY IdEstadoMesa";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new EstadoMesa(rs.getInt("IdEstadoMesa"), rs.getString("Nombre")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}