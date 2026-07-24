package DAO;
import DTO.MovimientoPedido;
import Interface.IMovimientoPedido;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovimientoPedidoDAO extends UnicastRemoteObject implements IMovimientoPedido {

    public MovimientoPedidoDAO() throws RemoteException {
    }

    @Override
    public boolean insertar(MovimientoPedido obj) throws RemoteException {
        String sql = "INSERT INTO MovimientoPedido (IdMovimiento, IdPresentacion, Cantidad, " +
                "PrecioUnitario, Total, IdEstadoPedido, FechaInicio, TiempoEstimado, Pagado) " +
                "VALUES (?,?,?,?,?,1,NOW(),?,FALSE)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, obj.getIdMovimiento());
            ps.setInt(2, obj.getIdPresentacion());
            ps.setInt(3, obj.getCantidad());
            ps.setDouble(4, obj.getPrecioUnitario() > 0 ? obj.getPrecioUnitario() : obj.getPrecioPresentacion());
            ps.setDouble(5, obj.getTotal());
            ps.setInt(6, obj.getTiempoEstimado() > 0 ? obj.getTiempoEstimado() : 0);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean actualizar(MovimientoPedido obj) throws RemoteException {
        String sql = "UPDATE MovimientoPedido SET Cantidad=?, Total=? WHERE IdMovimiento=? AND IdPresentacion=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, obj.getCantidad());
            ps.setDouble(2, obj.getTotal());
            ps.setInt(3, obj.getIdMovimiento());
            ps.setInt(4, obj.getIdPresentacion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean eliminar(MovimientoPedido obj) throws RemoteException {
        String sql = "DELETE FROM MovimientoPedido WHERE IdMovimiento=? AND IdPresentacion=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, obj.getIdMovimiento());
            ps.setInt(2, obj.getIdPresentacion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<MovimientoPedido> listarPorMovimiento(int idMovimiento) throws RemoteException {
        List<MovimientoPedido> lista = new ArrayList<>();
        String sql = "SELECT mp.IdMovimiento, mp.IdPresentacion, mp.Cantidad, mp.Total, mp.Pagado, " +
                "pre.Nombre AS NomPres, pre.Precio " +
                "FROM MovimientoPedido mp " +
                "INNER JOIN Presentacion pre ON mp.IdPresentacion = pre.IdPresentacion " +
                "WHERE mp.IdMovimiento = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMovimiento);
            try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MovimientoPedido mp = new MovimientoPedido();
                mp.setIdMovimiento(rs.getInt("IdMovimiento"));
                mp.setIdPresentacion(rs.getInt("IdPresentacion"));
                mp.setCantidad(rs.getInt("Cantidad"));
                mp.setTotal(rs.getDouble("Total"));
                mp.setPagado(rs.getBoolean("Pagado"));
                mp.setNombrePresentacion(rs.getString("NomPres"));
                mp.setPrecioPresentacion(rs.getDouble("Precio"));
                lista.add(mp);
            }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<MovimientoPedido> listarPorCliente(int idCliente) throws RemoteException {
        List<MovimientoPedido> lista = new ArrayList<>();
        String sql = "SELECT mp.IdMovimiento, mp.IdPresentacion, mp.Cantidad, mp.Total, " +
                "pre.Nombre AS NomPres, pre.Precio, pr.Nombre AS NomProd, " +
                "m.NroDocumento, me.Numero AS NumMesa, " +
                "ep.Nombre AS NomEstado " +
                "FROM MovimientoPedido mp " +
                "INNER JOIN Presentacion pre ON mp.IdPresentacion = pre.IdPresentacion " +
                "INNER JOIN Producto pr ON pre.IdProducto = pr.IdProducto " +
                "INNER JOIN Movimiento m ON mp.IdMovimiento = m.IdMovimiento " +
                "INNER JOIN MovimientoMesa mm ON m.IdMovimiento = mm.IdMovimiento " +
                "INNER JOIN Mesa me ON mm.IdMesa = me.IdMesa " +
                "INNER JOIN EstadoMovimiento ep ON m.IdEstadoMovimiento = ep.IdEstadoMovimiento " +
                "WHERE m.IdCliente = ? ORDER BY mp.IdMovimiento DESC";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MovimientoPedido mp = new MovimientoPedido();
                mp.setIdMovimiento(rs.getInt("IdMovimiento"));
                mp.setIdPresentacion(rs.getInt("IdPresentacion"));
                mp.setCantidad(rs.getInt("Cantidad"));
                mp.setTotal(rs.getDouble("Total"));
                mp.setNombrePresentacion(rs.getString("NomPres"));
                mp.setPrecioPresentacion(rs.getDouble("Precio"));
                mp.setNombreProducto(rs.getString("NomProd"));
                mp.setNumeroMesa(rs.getString("NumMesa"));
                lista.add(mp);
            }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<MovimientoPedido> listarPorEstado(int idEstadoPedido) throws RemoteException {
        List<MovimientoPedido> lista = new ArrayList<>();
        String sql = "SELECT mp.*, pre.Nombre AS NomPres, pre.Precio, pr.Nombre AS NomProd, " +
                "ep.Nombre AS NomEstado, mo.Nombre AS NomMozo, me.Numero AS NumMesa " +
                "FROM MovimientoPedido mp " +
                "INNER JOIN Presentacion pre ON mp.IdPresentacion = pre.IdPresentacion " +
                "INNER JOIN Producto pr ON pre.IdProducto = pr.IdProducto " +
                "INNER JOIN EstadoPedido ep ON mp.IdEstadoPedido = ep.IdEstadoPedido " +
                "INNER JOIN Movimiento m ON mp.IdMovimiento = m.IdMovimiento " +
                "LEFT JOIN Mozo mo ON m.IdMozo = mo.IdMozo " +
                "LEFT JOIN MovimientoMesa mm ON m.IdMovimiento = mm.IdMovimiento " +
                "LEFT JOIN Mesa me ON mm.IdMesa = me.IdMesa " +
                "WHERE mp.IdEstadoPedido = ? ORDER BY mp.FechaInicio";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEstadoPedido);
            try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MovimientoPedido mp = new MovimientoPedido();
                mp.setIdMovimiento(rs.getInt("IdMovimiento"));
                mp.setIdPresentacion(rs.getInt("IdPresentacion"));
                mp.setCantidad(rs.getInt("Cantidad"));
                mp.setPrecioUnitario(rs.getDouble("PrecioUnitario"));
                mp.setTotal(rs.getDouble("Total"));
                mp.setIdEstadoPedido(rs.getInt("IdEstadoPedido"));
                Timestamp fi = rs.getTimestamp("FechaInicio");
                if (fi != null) mp.setFechaInicio(fi.toLocalDateTime());
                mp.setTiempoEstimado(rs.getInt("TiempoEstimado"));
                mp.setNombrePresentacion(rs.getString("NomPres"));
                mp.setPrecioPresentacion(rs.getDouble("Precio"));
                mp.setNombreProducto(rs.getString("NomProd"));
                mp.setNombreEstadoPedido(rs.getString("NomEstado"));
                mp.setNombreMozo(rs.getString("NomMozo"));
                mp.setNumeroMesa(rs.getString("NumMesa"));
                lista.add(mp);
            }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<MovimientoPedido> listarParaCocina() throws RemoteException {
        List<MovimientoPedido> lista = new ArrayList<>();
        String sql = "SELECT mp.*, pre.Nombre AS NomPres, pre.Precio, pr.Nombre AS NomProd, " +
                "ep.Nombre AS NomEstado, mo.Nombre AS NomMozo, me.Numero AS NumMesa, " +
                "m.CodigoComanda, tp.Duracion AS TiempoEstimadoProd " +
                "FROM MovimientoPedido mp " +
                "INNER JOIN Presentacion pre ON mp.IdPresentacion = pre.IdPresentacion " +
                "INNER JOIN Producto pr ON pre.IdProducto = pr.IdProducto " +
                "INNER JOIN EstadoPedido ep ON mp.IdEstadoPedido = ep.IdEstadoPedido " +
                "INNER JOIN Movimiento m ON mp.IdMovimiento = m.IdMovimiento " +
                "LEFT JOIN Mozo mo ON m.IdMozo = mo.IdMozo " +
                "LEFT JOIN MovimientoMesa mm ON m.IdMovimiento = mm.IdMovimiento " +
                "LEFT JOIN Mesa me ON mm.IdMesa = me.IdMesa " +
                "LEFT JOIN TiempoPreparacion tp ON pre.IdTiempoPreparacion = tp.IdTiempoPreparacion " +
                "WHERE mp.IdEstadoPedido IN (1,2) ORDER BY mp.FechaInicio";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MovimientoPedido mp = new MovimientoPedido();
                mp.setIdMovimiento(rs.getInt("IdMovimiento"));
                mp.setIdPresentacion(rs.getInt("IdPresentacion"));
                mp.setCantidad(rs.getInt("Cantidad"));
                mp.setPrecioUnitario(rs.getDouble("PrecioUnitario"));
                mp.setTotal(rs.getDouble("Total"));
                mp.setIdEstadoPedido(rs.getInt("IdEstadoPedido"));
                Timestamp fi = rs.getTimestamp("FechaInicio");
                if (fi != null) mp.setFechaInicio(fi.toLocalDateTime());
                mp.setTiempoEstimado(rs.getInt("TiempoEstimado"));
                mp.setNombrePresentacion(rs.getString("NomPres"));
                mp.setPrecioPresentacion(rs.getDouble("Precio"));
                mp.setNombreProducto(rs.getString("NomProd"));
                mp.setNombreEstadoPedido(rs.getString("NomEstado"));
                mp.setNombreMozo(rs.getString("NomMozo"));
                mp.setNumeroMesa(rs.getString("NumMesa"));
                lista.add(mp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean cambiarEstado(int idMovimiento, int idPresentacion, int nuevoEstado) throws RemoteException {
        String sql = "UPDATE MovimientoPedido SET IdEstadoPedido=?";
        if (nuevoEstado == 2) sql += ", FechaInicio=NOW()";
        if (nuevoEstado == 4) sql += ", FechaFin=NOW()";
        sql += " WHERE IdMovimiento=? AND IdPresentacion=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nuevoEstado);
            ps.setInt(2, idMovimiento);
            ps.setInt(3, idPresentacion);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public MovimientoPedido buscar(int idMovimiento, int idPresentacion) throws RemoteException {
        String sql = "SELECT mp.*, pre.Nombre AS NomPres, pre.Precio, " +
                "ep.Nombre AS NomEstado FROM MovimientoPedido mp " +
                "INNER JOIN Presentacion pre ON mp.IdPresentacion = pre.IdPresentacion " +
                "INNER JOIN EstadoPedido ep ON mp.IdEstadoPedido = ep.IdEstadoPedido " +
                "WHERE mp.IdMovimiento=? AND mp.IdPresentacion=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMovimiento);
            ps.setInt(2, idPresentacion);
            try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                MovimientoPedido mp = new MovimientoPedido();
                mp.setIdMovimiento(rs.getInt("IdMovimiento"));
                mp.setIdPresentacion(rs.getInt("IdPresentacion"));
                mp.setCantidad(rs.getInt("Cantidad"));
                mp.setPrecioUnitario(rs.getDouble("PrecioUnitario"));
                mp.setTotal(rs.getDouble("Total"));
                mp.setIdEstadoPedido(rs.getInt("IdEstadoPedido"));
                mp.setPagado(rs.getBoolean("Pagado"));
                mp.setNombrePresentacion(rs.getString("NomPres"));
                mp.setPrecioPresentacion(rs.getDouble("Precio"));
                mp.setNombreEstadoPedido(rs.getString("NomEstado"));
                return mp;
            }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<MovimientoPedido> listarNoPagados(int idMovimiento) throws RemoteException {
        List<MovimientoPedido> lista = new ArrayList<>();
        String sql = "SELECT mp.IdMovimiento, mp.IdPresentacion, mp.Cantidad, mp.Total, mp.Pagado, " +
                "pre.Nombre AS NomPres, pre.Precio, pr.Nombre AS NomProd " +
                "FROM MovimientoPedido mp " +
                "INNER JOIN Presentacion pre ON mp.IdPresentacion = pre.IdPresentacion " +
                "INNER JOIN Producto pr ON pre.IdProducto = pr.IdProducto " +
                "WHERE mp.IdMovimiento = ? AND (mp.Pagado = FALSE OR mp.Pagado IS NULL)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMovimiento);
            try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MovimientoPedido mp = new MovimientoPedido();
                mp.setIdMovimiento(rs.getInt("IdMovimiento"));
                mp.setIdPresentacion(rs.getInt("IdPresentacion"));
                mp.setCantidad(rs.getInt("Cantidad"));
                mp.setTotal(rs.getDouble("Total"));
                mp.setPagado(rs.getBoolean("Pagado"));
                mp.setNombrePresentacion(rs.getString("NomPres"));
                mp.setPrecioPresentacion(rs.getDouble("Precio"));
                mp.setNombreProducto(rs.getString("NomProd"));
                lista.add(mp);
            }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean marcarPagados(int idMovimiento, String idsPresentaciones) throws RemoteException {
        if (idsPresentaciones == null || idsPresentaciones.trim().isEmpty()) return false;
        String[] ids = idsPresentaciones.split(",");
        int[] parsed = new int[ids.length];
        try {
            for (int i = 0; i < ids.length; i++) {
                parsed[i] = Integer.parseInt(ids[i].trim());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
        String placeholders = String.join(",", java.util.Collections.nCopies(ids.length, "?"));
        String sql = "UPDATE MovimientoPedido SET Pagado = TRUE " +
                     "WHERE IdMovimiento = ? AND IdPresentacion IN (" + placeholders + ")";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMovimiento);
            for (int i = 0; i < parsed.length; i++) {
                ps.setInt(i + 2, parsed[i]);
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}