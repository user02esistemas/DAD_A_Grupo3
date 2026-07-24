package DAO;
import DTO.EstadoPedido;
import Interface.IEstadoPedido;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstadoPedidoDAO extends UnicastRemoteObject implements IEstadoPedido {

    public EstadoPedidoDAO() throws RemoteException {
    }

    @Override
    public List<EstadoPedido> listar() throws RemoteException {
        List<EstadoPedido> lista = new ArrayList<>();
        String sql = "SELECT IdEstadoPedido, Nombre FROM EstadoPedido ORDER BY IdEstadoPedido";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new EstadoPedido(
                    rs.getInt("IdEstadoPedido"),
                    rs.getString("Nombre")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public EstadoPedido buscar(int id) throws RemoteException {
        String sql = "SELECT IdEstadoPedido, Nombre FROM EstadoPedido WHERE IdEstadoPedido=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    return new EstadoPedido(
                        rs.getInt("IdEstadoPedido"),
                        rs.getString("Nombre")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}