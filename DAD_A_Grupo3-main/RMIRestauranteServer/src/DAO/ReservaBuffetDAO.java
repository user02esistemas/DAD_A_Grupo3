package DAO;
import DTO.ReservaBuffet;
import Interface.IReservaBuffet;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaBuffetDAO extends UnicastRemoteObject implements IReservaBuffet {

    public ReservaBuffetDAO() throws RemoteException {
    }

    @Override
    public List<ReservaBuffet> listar() throws RemoteException {
        List<ReservaBuffet> lista = new ArrayList<>();
        String sql =
            "SELECT rb.*, ep.Nombre AS NomEstado, c.Nombre AS NomCliente, c.Apellido AS ApeCliente " +
            "FROM ReservaBuffet rb " +
            "INNER JOIN EstadoPedido ep ON rb.IdEstado = ep.IdEstadoPedido " +
            "INNER JOIN Cliente c ON rb.IdCliente = c.IdCliente " +
            "ORDER BY rb.FechaHora DESC";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ReservaBuffet rb = new ReservaBuffet();
                rb.setIdReserva(rs.getInt("IdReserva"));
                Timestamp ts = rs.getTimestamp("FechaHora");
                if (ts != null) rb.setFechaHora(ts.toLocalDateTime());
                rb.setPersonas(rs.getInt("Personas"));
                rb.setBebidas(rs.getString("Bebidas"));
                rb.setPlatosFrio(rs.getString("PlatosFrio"));
                rb.setPlatosCaliente(rs.getString("PlatosCaliente"));
                rb.setTotal(rs.getDouble("Total"));
                rb.setIdCliente(rs.getInt("IdCliente"));
                rb.setIdEstado(rs.getInt("IdEstado"));
                rb.setNombreEstado(rs.getString("NomEstado"));
                rb.setNombreCliente(rs.getString("NomCliente") + " " + rs.getString("ApeCliente"));
                lista.add(rb);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<ReservaBuffet> listarPorEstado(int idEstado) throws RemoteException {
        List<ReservaBuffet> lista = new ArrayList<>();
        String sql =
            "SELECT rb.*, ep.Nombre AS NomEstado, c.Nombre AS NomCliente, c.Apellido AS ApeCliente " +
            "FROM ReservaBuffet rb " +
            "INNER JOIN EstadoPedido ep ON rb.IdEstado = ep.IdEstadoPedido " +
            "INNER JOIN Cliente c ON rb.IdCliente = c.IdCliente " +
            "WHERE rb.IdEstado=? ORDER BY rb.FechaHora DESC";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEstado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReservaBuffet rb = new ReservaBuffet();
                    rb.setIdReserva(rs.getInt("IdReserva"));
                    Timestamp ts = rs.getTimestamp("FechaHora");
                    if (ts != null) rb.setFechaHora(ts.toLocalDateTime());
                    rb.setPersonas(rs.getInt("Personas"));
                    rb.setBebidas(rs.getString("Bebidas"));
                    rb.setPlatosFrio(rs.getString("PlatosFrio"));
                    rb.setPlatosCaliente(rs.getString("PlatosCaliente"));
                    rb.setTotal(rs.getDouble("Total"));
                    rb.setIdCliente(rs.getInt("IdCliente"));
                    rb.setIdEstado(rs.getInt("IdEstado"));
                    rb.setNombreEstado(rs.getString("NomEstado"));
                    rb.setNombreCliente(rs.getString("NomCliente") + " " + rs.getString("ApeCliente"));
                    lista.add(rb);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public ReservaBuffet buscar(int id) throws RemoteException {
        String sql =
            "SELECT rb.*, ep.Nombre AS NomEstado, c.Nombre AS NomCliente, c.Apellido AS ApeCliente " +
            "FROM ReservaBuffet rb " +
            "INNER JOIN EstadoPedido ep ON rb.IdEstado = ep.IdEstadoPedido " +
            "INNER JOIN Cliente c ON rb.IdCliente = c.IdCliente " +
            "WHERE rb.IdReserva=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ReservaBuffet rb = new ReservaBuffet();
                    rb.setIdReserva(rs.getInt("IdReserva"));
                    Timestamp ts = rs.getTimestamp("FechaHora");
                    if (ts != null) rb.setFechaHora(ts.toLocalDateTime());
                    rb.setPersonas(rs.getInt("Personas"));
                    rb.setBebidas(rs.getString("Bebidas"));
                    rb.setPlatosFrio(rs.getString("PlatosFrio"));
                    rb.setPlatosCaliente(rs.getString("PlatosCaliente"));
                    rb.setTotal(rs.getDouble("Total"));
                    rb.setIdCliente(rs.getInt("IdCliente"));
                    rb.setIdEstado(rs.getInt("IdEstado"));
                    rb.setNombreEstado(rs.getString("NomEstado"));
                    rb.setNombreCliente(rs.getString("NomCliente") + " " + rs.getString("ApeCliente"));
                    return rb;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insertar(ReservaBuffet obj) throws RemoteException {
        String sql =
            "INSERT INTO ReservaBuffet (FechaHora, Personas, Bebidas, PlatosFrio, PlatosCaliente, " +
            "Total, IdCliente, IdEstado) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, obj.getFechaHora() != null ? Timestamp.valueOf(obj.getFechaHora()) : null);
            ps.setInt(2, obj.getPersonas());
            ps.setString(3, obj.getBebidas());
            ps.setString(4, obj.getPlatosFrio());
            ps.setString(5, obj.getPlatosCaliente());
            ps.setDouble(6, obj.getTotal());
            ps.setInt(7, obj.getIdCliente());
            ps.setInt(8, obj.getIdEstado());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean actualizar(ReservaBuffet obj) throws RemoteException {
        String sql =
            "UPDATE ReservaBuffet SET FechaHora=?, Personas=?, Bebidas=?, PlatosFrio=?, " +
            "PlatosCaliente=?, Total=?, IdCliente=?, IdEstado=? WHERE IdReserva=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, obj.getFechaHora() != null ? Timestamp.valueOf(obj.getFechaHora()) : null);
            ps.setInt(2, obj.getPersonas());
            ps.setString(3, obj.getBebidas());
            ps.setString(4, obj.getPlatosFrio());
            ps.setString(5, obj.getPlatosCaliente());
            ps.setDouble(6, obj.getTotal());
            ps.setInt(7, obj.getIdCliente());
            ps.setInt(8, obj.getIdEstado());
            ps.setInt(9, obj.getIdReserva());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) throws RemoteException {
        String sql = "DELETE FROM ReservaBuffet WHERE IdReserva=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean confirmar(int id) throws RemoteException {
        String sql = "UPDATE ReservaBuffet SET IdEstado=5 WHERE IdReserva=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean cancelar(int id) throws RemoteException {
        String sql = "UPDATE ReservaBuffet SET IdEstado=6 WHERE IdReserva=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean verificarDisponibilidad(String fecha, int personas) throws RemoteException {
        String sql =
            "SELECT COALESCE(SUM(Personas), 0) AS Total FROM ReservaBuffet " +
            "WHERE DATE(FechaHora) = ? AND IdEstado NOT IN (6)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, fecha);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int ocupadas = rs.getInt("Total");
                    return (ocupadas + personas) <= 30;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}