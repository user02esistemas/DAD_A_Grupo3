package DAO;
import DTO.EstadoComanda;
import Interface.IEstadoComanda;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstadoComandaDAO extends UnicastRemoteObject implements IEstadoComanda {

    public EstadoComandaDAO() throws RemoteException {
    }

    @Override
    public List<EstadoComanda> listar() throws RemoteException {
        List<EstadoComanda> lista = new ArrayList<>();
        String sql = "SELECT IdEstadoComanda, Nombre FROM EstadoComanda ORDER BY IdEstadoComanda";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new EstadoComanda(
                    rs.getInt("IdEstadoComanda"),
                    rs.getString("Nombre")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public EstadoComanda buscar(int id) throws RemoteException {
        String sql = "SELECT IdEstadoComanda, Nombre FROM EstadoComanda WHERE IdEstadoComanda=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    return new EstadoComanda(
                        rs.getInt("IdEstadoComanda"),
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