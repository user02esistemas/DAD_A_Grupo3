package DAO;
import DTO.Notificacion;
import Interface.INotificacion;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificacionDAO extends UnicastRemoteObject implements INotificacion {

    public NotificacionDAO() throws RemoteException {
    }

    @Override
    public List<Notificacion> listar() throws RemoteException {
        List<Notificacion> lista = new ArrayList<>();
        String sql = "SELECT IdNotificacion, IdMovimiento, Tipo, Mensaje, IdDestinatario, " +
                "TipoDestinatario, Leida, FechaCreacion " +
                "FROM Notificacion ORDER BY FechaCreacion DESC";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Notificacion> listarNoLeidas(String tipoDestinatario, int idDestinatario) throws RemoteException {
        List<Notificacion> lista = new ArrayList<>();
        String sql = "SELECT IdNotificacion, IdMovimiento, Tipo, Mensaje, IdDestinatario, " +
                "TipoDestinatario, Leida, FechaCreacion " +
                "FROM Notificacion WHERE TipoDestinatario=? AND IdDestinatario=? AND Leida=false " +
                "ORDER BY FechaCreacion DESC";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tipoDestinatario);
            ps.setInt(2, idDestinatario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean marcarLeida(int idNotificacion) throws RemoteException {
        String sql = "UPDATE Notificacion SET Leida=true WHERE IdNotificacion=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idNotificacion);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean insertar(Notificacion obj) throws RemoteException {
        String sql = "INSERT INTO Notificacion (IdMovimiento, Tipo, Mensaje, IdDestinatario, TipoDestinatario) " +
                "VALUES (?,?,?,?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, obj.getIdMovimiento());
            ps.setString(2, obj.getTipo());
            ps.setString(3, obj.getMensaje());
            ps.setInt(4, obj.getIdDestinatario());
            ps.setString(5, obj.getTipoDestinatario());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) throws RemoteException {
        String sql = "DELETE FROM Notificacion WHERE IdNotificacion=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Notificacion mapear(ResultSet rs) throws SQLException {
        Notificacion n = new Notificacion();
        n.setIdNotificacion(rs.getInt("IdNotificacion"));
        n.setIdMovimiento(rs.getInt("IdMovimiento"));
        n.setTipo(rs.getString("Tipo"));
        n.setMensaje(rs.getString("Mensaje"));
        n.setIdDestinatario(rs.getInt("IdDestinatario"));
        n.setTipoDestinatario(rs.getString("TipoDestinatario"));
        n.setLeida(rs.getBoolean("Leida"));
        Timestamp ts = rs.getTimestamp("FechaCreacion");
        if (ts != null) n.setFecha(ts.toLocalDateTime());
        return n;
    }
}