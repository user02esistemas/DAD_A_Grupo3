package DAO;
import DTO.EstadoMovimiento;
import Interface.IEstadoMovimiento;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstadoMovimientoDAO extends UnicastRemoteObject implements IEstadoMovimiento {

    public EstadoMovimientoDAO() throws RemoteException {
    }

    @Override
    public List<EstadoMovimiento> listar() throws RemoteException {
        List<EstadoMovimiento> lista = new ArrayList<>();
        String sql = "SELECT IdEstadoMovimiento, Nombre FROM EstadoMovimiento ORDER BY IdEstadoMovimiento";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new EstadoMovimiento(rs.getInt("IdEstadoMovimiento"), rs.getString("Nombre")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}