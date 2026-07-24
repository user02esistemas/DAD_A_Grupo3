package DAO;
import DTO.TipoProducto;
import Interface.ITipoProducto;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoProductoDAO extends UnicastRemoteObject implements ITipoProducto {

    public TipoProductoDAO() throws RemoteException {
    }

    @Override
    public List<TipoProducto> listar() throws RemoteException {
        List<TipoProducto> lista = new ArrayList<>();
        String sql = "SELECT IdTipoProducto, Nombre FROM TipoProducto ORDER BY IdTipoProducto";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new TipoProducto(rs.getInt("IdTipoProducto"), rs.getString("Nombre")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}