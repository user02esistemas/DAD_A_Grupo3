package DAO;
import DTO.CodigoPago;
import Interface.ICodigoPago;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CodigoPagoDAO extends UnicastRemoteObject implements ICodigoPago {

    public CodigoPagoDAO() throws RemoteException {
    }

    @Override
    public CodigoPago generarCodigo(int idMovimiento) throws RemoteException {
        String sqlExistente = "SELECT cp.IdCodigoPago, cp.Codigo, cp.IdMovimiento, cp.MontoTotal, cp.Pagado, " +
                "cp.FechaCreacion, cp.FechaPago " +
                "FROM CodigoPago cp " +
                "WHERE cp.IdMovimiento = ? AND cp.Pagado = FALSE " +
                "ORDER BY cp.FechaCreacion DESC LIMIT 1";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sqlExistente)) {
            ps.setInt(1, idMovimiento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        String codigo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String sqlInsert = "INSERT INTO CodigoPago (Codigo, IdMovimiento, MontoTotal, Pagado) " +
                "VALUES (?, ?, 0, FALSE)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, codigo);
            ps.setInt(2, idMovimiento);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return buscar(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CodigoPago validarCodigo(String codigo) throws RemoteException {
        String sql = "SELECT cp.IdCodigoPago, cp.Codigo, cp.IdMovimiento, cp.MontoTotal, cp.Pagado, " +
                "cp.FechaCreacion, cp.FechaPago " +
                "FROM CodigoPago cp WHERE cp.Codigo = ? AND cp.Pagado = FALSE";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean marcarPagado(int idCodigoPago) throws RemoteException {
        String sql = "UPDATE CodigoPago SET Pagado = TRUE, FechaPago = NOW() " +
                "WHERE IdCodigoPago = ? AND Pagado = FALSE";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCodigoPago);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public CodigoPago buscar(int id) throws RemoteException {
        String sql = "SELECT cp.IdCodigoPago, cp.Codigo, cp.IdMovimiento, cp.MontoTotal, cp.Pagado, " +
                "cp.FechaCreacion, cp.FechaPago " +
                "FROM CodigoPago cp WHERE cp.IdCodigoPago = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<CodigoPago> listar() throws RemoteException {
        List<CodigoPago> lista = new ArrayList<>();
        String sql = "SELECT cp.IdCodigoPago, cp.Codigo, cp.IdMovimiento, cp.MontoTotal, cp.Pagado, " +
                "cp.FechaCreacion, cp.FechaPago " +
                "FROM CodigoPago cp ORDER BY cp.FechaCreacion DESC";
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

    private CodigoPago mapear(ResultSet rs) throws SQLException {
        CodigoPago cp = new CodigoPago();
        cp.setIdCodigoPago(rs.getInt("IdCodigoPago"));
        cp.setCodigo(rs.getString("Codigo"));
        cp.setIdMovimiento(rs.getInt("IdMovimiento"));
        cp.setMontoTotal(rs.getDouble("MontoTotal"));
        cp.setPagado(rs.getBoolean("Pagado"));
        Timestamp fc = rs.getTimestamp("FechaCreacion");
        if (fc != null) cp.setFechaCreacion(fc.toLocalDateTime());
        Timestamp fp = rs.getTimestamp("FechaPago");
        if (fp != null) cp.setFechaPago(fp.toLocalDateTime());
        return cp;
    }
}