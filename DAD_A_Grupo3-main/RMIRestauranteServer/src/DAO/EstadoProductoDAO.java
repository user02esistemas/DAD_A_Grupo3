package DAO;
import DTO.EstadoProducto;
import Interface.IEstadoProducto;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstadoProductoDAO extends UnicastRemoteObject implements IEstadoProducto {

    public EstadoProductoDAO() throws RemoteException {
    }

    @Override
    public List<EstadoProducto> listar() throws RemoteException {
        List<EstadoProducto> lista = new ArrayList<>();
        String sql = "SELECT IdEstadoProducto, Nombre FROM EstadoProducto ORDER BY IdEstadoProducto";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new EstadoProducto(rs.getInt("IdEstadoProducto"), rs.getString("Nombre")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}