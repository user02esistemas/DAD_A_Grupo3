package DAO;
import DTO.MovimientoMesa;
import Interface.IMovimientoMesa;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovimientoMesaDAO extends UnicastRemoteObject implements IMovimientoMesa {

    public MovimientoMesaDAO() throws RemoteException {
    }

    @Override
    public boolean insertar(MovimientoMesa obj) throws RemoteException {
        String sql = "INSERT INTO MovimientoMesa (IdMovimiento, IdMesa) VALUES (?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, obj.getIdMovimiento());
            ps.setInt(2, obj.getIdMesa());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean eliminar(MovimientoMesa obj) throws RemoteException {
        String sql = "DELETE FROM MovimientoMesa WHERE IdMovimiento=? AND IdMesa=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, obj.getIdMovimiento());
            ps.setInt(2, obj.getIdMesa());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<MovimientoMesa> listarPorMovimiento(int idMovimiento) throws RemoteException {
        List<MovimientoMesa> lista = new ArrayList<>();
        String sql = "SELECT IdMovimiento, IdMesa FROM MovimientoMesa WHERE IdMovimiento=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMovimiento);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MovimientoMesa mm = new MovimientoMesa();
                    mm.setIdMovimiento(rs.getInt("IdMovimiento"));
                    mm.setIdMesa(rs.getInt("IdMesa"));
                    lista.add(mm);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}