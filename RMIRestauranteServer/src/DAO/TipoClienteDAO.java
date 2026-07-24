package DAO;
import DTO.TipoCliente;
import Interface.ITipoCliente;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoClienteDAO extends UnicastRemoteObject implements ITipoCliente {

    public TipoClienteDAO() throws RemoteException {
    }

    @Override
    public List<TipoCliente> listar() throws RemoteException {
        List<TipoCliente> lista = new ArrayList<>();
        String sql = "SELECT IdTipoCliente, Nombre FROM TipoCliente ORDER BY IdTipoCliente";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new TipoCliente(rs.getInt("IdTipoCliente"), rs.getString("Nombre")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}