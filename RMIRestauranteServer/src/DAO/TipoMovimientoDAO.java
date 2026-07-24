package DAO;
import DTO.TipoMovimiento;
import Interface.ITipoMovimiento;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoMovimientoDAO extends UnicastRemoteObject implements ITipoMovimiento {

    public TipoMovimientoDAO() throws RemoteException {
    }

    @Override
    public List<TipoMovimiento> listar() throws RemoteException {
        List<TipoMovimiento> lista = new ArrayList<>();
        String sql = "SELECT IdTipoMovimiento, Nombre FROM TipoMovimiento ORDER BY IdTipoMovimiento";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new TipoMovimiento(rs.getInt("IdTipoMovimiento"), rs.getString("Nombre")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}