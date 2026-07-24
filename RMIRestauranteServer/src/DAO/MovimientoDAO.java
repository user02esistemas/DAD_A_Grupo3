package DAO;
import DTO.Movimiento;
import DTO.MovimientoPedido;
import DTO.Cliente;
import DTO.DatosEmpresa;
import Interface.IMovimiento;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MovimientoDAO extends UnicastRemoteObject implements IMovimiento {

    public MovimientoDAO() throws RemoteException {
    }

    @Override
    public int insertar(Movimiento obj) throws RemoteException {
        int idGenerado = 0;
        String sql = "INSERT INTO Movimiento (Fecha, NroDocumento, IdCliente, IdTipoMovimiento, " +
                "TotalPagar, IdDatosEmpresa, IdEstadoMovimiento, NumPersonas) " +
                "VALUES (NOW(),?,?,?,?,?,?,?)";
        try (Connection con = Conexion.getConexion()) {
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, obj.getNroDocumento());
                ps.setInt(2, obj.getIdCliente());
                ps.setInt(3, obj.getIdTipoMovimiento());
                ps.setDouble(4, obj.getTotalPagar());
                ps.setInt(5, obj.getIdDatosEmpresa());
                ps.setInt(6, obj.getIdEstadoMovimiento());
                ps.setInt(7, obj.getNumPersonas());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        idGenerado = rs.getInt(1);
                    }
                }
            }
            if (obj.getListaMesas() != null && !obj.getListaMesas().isEmpty()) {
                try (PreparedStatement pstmMesa = con.prepareStatement(
                        "INSERT INTO MovimientoMesa (IdMovimiento, IdMesa) VALUES (?, ?)")) {
                    for (int idMesa : obj.getListaMesas()) {
                        pstmMesa.setInt(1, idGenerado);
                        pstmMesa.setInt(2, idMesa);
                        pstmMesa.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return idGenerado;
    }

    @Override
    public boolean actualizar(Movimiento obj) throws RemoteException {
        String sql = "UPDATE Movimiento SET NroDocumento=?, IdCliente=?, IdTipoMovimiento=?, " +
                "TotalPagar=?, IdEstadoMovimiento=?, NumPersonas=?, IdMozo=?, IdEstadoComanda=?, IdMesaGrupo=? " +
                "WHERE IdMovimiento=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, obj.getNroDocumento());
            ps.setInt(2, obj.getIdCliente());
            ps.setInt(3, obj.getIdTipoMovimiento());
            ps.setDouble(4, obj.getTotalPagar());
            ps.setInt(5, obj.getIdEstadoMovimiento());
            ps.setInt(6, obj.getNumPersonas());
            ps.setInt(7, obj.getIdMozo());
            ps.setInt(8, obj.getIdEstadoComanda());
            if (obj.getIdMesaGrupo() > 0) { ps.setInt(9, obj.getIdMesaGrupo()); } else { ps.setNull(9, java.sql.Types.INTEGER); }
            ps.setInt(10, obj.getIdMovimiento());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) throws RemoteException {
        String sql = "DELETE FROM Movimiento WHERE IdMovimiento=?";
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
    public List<Movimiento> listarActivos() throws RemoteException {
        List<Movimiento> lista = new ArrayList<>();
        String sql = "SELECT m.IdMovimiento, m.TotalPagar, " +
                "STRING_AGG(CONCAT('Mesa: ', me.Numero), ', ') AS Mesas " +
                "FROM Movimiento m " +
                "INNER JOIN MovimientoMesa mm ON m.IdMovimiento = mm.IdMovimiento " +
                "INNER JOIN Mesa me ON mm.IdMesa = me.IdMesa " +
                "WHERE m.IdEstadoMovimiento = 1 " +
                "GROUP BY m.IdMovimiento, m.TotalPagar";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Movimiento m = new Movimiento();
                m.setIdMovimiento(rs.getInt("IdMovimiento"));
                m.setTotalPagar(rs.getDouble("TotalPagar"));
                m.setNroDocumento(rs.getString("Mesas"));
                lista.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Movimiento> listarVentasCerradas() throws RemoteException {
        List<Movimiento> lista = new ArrayList<>();
        String sql = "SELECT m.IdMovimiento, m.Fecha, m.TotalPagar, m.NroDocumento, " +
                "c.Nombre, c.Apellido " +
                "FROM Movimiento m " +
                "INNER JOIN Cliente c ON m.IdCliente = c.IdCliente " +
                "WHERE m.IdEstadoMovimiento = 2 ORDER BY m.Fecha DESC";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Movimiento m = new Movimiento();
                m.setIdMovimiento(rs.getInt("IdMovimiento"));
                Timestamp ts = rs.getTimestamp("Fecha");
                if (ts != null) {
                    m.setFecha(ts.toLocalDateTime());
                }
                m.setTotalPagar(rs.getDouble("TotalPagar"));
                m.setNroDocumento(rs.getString("NroDocumento"));
                m.setNombreCliente(rs.getString("Nombre") + " " + rs.getString("Apellido"));
                lista.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<MovimientoPedido> obtenerPedidosPorMesa(int idMesa) throws RemoteException {
        List<MovimientoPedido> lista = new ArrayList<>();
        String sql = "SELECT SUM(mp.Cantidad) AS Cantidad, SUM(mp.Total) AS Total, " +
                "pre.Nombre AS NomPres, pre.Precio " +
                "FROM Movimiento m " +
                "INNER JOIN MovimientoMesa mm ON m.IdMovimiento = mm.IdMovimiento " +
                "INNER JOIN MovimientoPedido mp ON m.IdMovimiento = mp.IdMovimiento " +
                "INNER JOIN Presentacion pre ON mp.IdPresentacion = pre.IdPresentacion " +
                "WHERE mm.IdMesa = ? AND m.IdEstadoMovimiento = 1 " +
                "GROUP BY pre.Nombre, pre.Precio";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMesa);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MovimientoPedido mp = new MovimientoPedido();
                    mp.setCantidad(rs.getInt("Cantidad"));
                    mp.setTotal(rs.getDouble("Total"));
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
    public int obtenerIdMovimientoActivoPorMesa(int idMesa) throws RemoteException {
        String sql = "SELECT m.IdMovimiento FROM Movimiento m " +
                "INNER JOIN MovimientoMesa mm ON m.IdMovimiento = mm.IdMovimiento " +
                "WHERE mm.IdMesa = ? AND m.IdEstadoComanda IN (1,2)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMesa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("IdMovimiento");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public Cliente obtenerClienteDelMovimiento(int idMovimiento) throws RemoteException {
        String sql = "SELECT c.* FROM Cliente c INNER JOIN Movimiento m ON c.IdCliente = m.IdCliente WHERE m.IdMovimiento = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMovimiento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cliente c = new Cliente();
                    c.setIdCliente(rs.getInt("IdCliente"));
                    c.setNombre(rs.getString("Nombre"));
                    c.setApellido(rs.getString("Apellido"));
                    c.setDocumento(rs.getString("Documento"));
                    c.setTelefono(rs.getString("Telefono"));
                    return c;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<MovimientoPedido> obtenerPedidosPorMovimiento(int idMovimiento) throws RemoteException {
        List<MovimientoPedido> lista = new ArrayList<>();
        String sql = "SELECT SUM(mp.Cantidad) AS Cantidad, SUM(mp.Total) AS Total, " +
                "pre.Nombre AS NomPres, pre.Precio " +
                "FROM MovimientoPedido mp " +
                "INNER JOIN Presentacion pre ON mp.IdPresentacion = pre.IdPresentacion " +
                "WHERE mp.IdMovimiento = ? GROUP BY pre.Nombre, pre.Precio";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMovimiento);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MovimientoPedido mp = new MovimientoPedido();
                    mp.setCantidad(rs.getInt("Cantidad"));
                    mp.setTotal(rs.getDouble("Total"));
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
    public DatosEmpresa obtenerDatosEmpresaPorMovimiento(int idMovimiento) throws RemoteException {
        String sql = "SELECT de.* FROM DatosEmpresa de INNER JOIN Movimiento m ON de.IdEmpresa = m.IdDatosEmpresa WHERE m.IdMovimiento = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMovimiento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DatosEmpresa emp = new DatosEmpresa();
                    emp.setIdEmpresa(rs.getInt("IdEmpresa"));
                    emp.setRuc(rs.getString("RUC"));
                    emp.setRazonSocial(rs.getString("RazonSocial"));
                    return emp;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Movimiento buscar(int id) throws RemoteException {
        String sql = "SELECT m.*, ep.Nombre AS NomEstado, " +
                "COALESCE(c.Nombre, '') || ' ' || COALESCE(c.Apellido, '') AS NombreCliente, " +
                "mo.Nombre AS NombreMozo, ec.Nombre AS NombreEstadoComanda, " +
                "STRING_AGG(CONCAT(me.Numero), ', ') AS NumeroMesa " +
                "FROM Movimiento m " +
                "INNER JOIN EstadoMovimiento ep ON m.IdEstadoMovimiento = ep.IdEstadoMovimiento " +
                "LEFT JOIN Cliente c ON m.IdCliente = c.IdCliente " +
                "LEFT JOIN Mozo mo ON m.IdMozo = mo.IdMozo " +
                "LEFT JOIN EstadoComanda ec ON m.IdEstadoComanda = ec.IdEstadoComanda " +
                "LEFT JOIN MovimientoMesa mm ON m.IdMovimiento = mm.IdMovimiento " +
                "LEFT JOIN Mesa me ON mm.IdMesa = me.IdMesa " +
                "WHERE m.IdMovimiento = ? " +
                "GROUP BY m.IdMovimiento, ep.Nombre, c.Nombre, c.Apellido, mo.Nombre, ec.Nombre";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Movimiento m = new Movimiento();
                    m.setIdMovimiento(rs.getInt("IdMovimiento"));
                    Timestamp ts = rs.getTimestamp("Fecha");
                    if (ts != null) m.setFecha(ts.toLocalDateTime());
                    m.setNroDocumento(rs.getString("NroDocumento"));
                    m.setIdCliente(rs.getInt("IdCliente"));
                    m.setIdTipoMovimiento(rs.getInt("IdTipoMovimiento"));
                    m.setTotalPagar(rs.getDouble("TotalPagar"));
                    m.setIdDatosEmpresa(rs.getInt("IdDatosEmpresa"));
                    m.setIdEstadoMovimiento(rs.getInt("IdEstadoMovimiento"));
                    m.setNumPersonas(rs.getInt("NumPersonas"));
                    m.setNombreEstadoMovimiento(rs.getString("NomEstado"));
                    m.setCodigoComanda(rs.getString("CodigoComanda"));
                    m.setIdMozo(rs.getInt("IdMozo"));
                    m.setIdEstadoComanda(rs.getInt("IdEstadoComanda"));
                    m.setNombreCliente(rs.getString("NombreCliente"));
                    m.setNombreMozo(rs.getString("NombreMozo"));
                    m.setNombreEstadoComanda(rs.getString("NombreEstadoComanda"));
                    m.setNumeroMesa(rs.getString("NumeroMesa"));
                    return m;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public double totalVentasHoy() throws RemoteException {
        String sql = "SELECT COALESCE(SUM(TotalPagar), 0) FROM Movimiento WHERE DATE(Fecha) = CURRENT_DATE AND IdEstadoMovimiento = 2";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean cerrarVenta(int idMovimiento) throws RemoteException {
        String sqlNoPagados = "SELECT COUNT(*) FROM MovimientoPedido WHERE IdMovimiento = ? AND Pagado = FALSE";
        String sqlCerrar = "UPDATE Movimiento SET IdEstadoMovimiento = 2, IdEstadoComanda = 5 WHERE IdMovimiento = ?";
        String sqlLiberarMesas = "UPDATE Mesa SET IdEstadoMesa = 1 WHERE IdMesa IN " +
                "(SELECT IdMesa FROM MovimientoMesa WHERE IdMovimiento = ?)";
        Connection con = null;
        try {
            con = Conexion.getConexion();
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sqlNoPagados)) {
                ps.setInt(1, idMovimiento);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        con.rollback();
                        return false;
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement(sqlCerrar)) {
                ps.setInt(1, idMovimiento);
                int updated = ps.executeUpdate();
                if (updated == 0) {
                    con.rollback();
                    return false;
                }
            }

            try (PreparedStatement ps = con.prepareStatement(sqlLiberarMesas)) {
                ps.setInt(1, idMovimiento);
                ps.executeUpdate();
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ignored) {}
            }
            e.printStackTrace();
            return false;
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (SQLException ignored) {}
            }
        }
    }

    @Override
    public Movimiento buscar(String codigoComanda) throws RemoteException {
        String sql = "SELECT m.*, ec.Nombre AS NomEstadoCom, mo.Nombre AS NomMozo, " +
                "me.Numero AS NumMesa FROM Movimiento m " +
                "INNER JOIN EstadoComanda ec ON m.IdEstadoComanda = ec.IdEstadoComanda " +
                "LEFT JOIN Mozo mo ON m.IdMozo = mo.IdMozo " +
                "LEFT JOIN MovimientoMesa mm ON m.IdMovimiento = mm.IdMovimiento " +
                "LEFT JOIN Mesa me ON mm.IdMesa = me.IdMesa " +
                "WHERE m.CodigoComanda = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoComanda);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Movimiento m = new Movimiento();
                    m.setIdMovimiento(rs.getInt("IdMovimiento"));
                    Timestamp ts = rs.getTimestamp("Fecha");
                    if (ts != null) m.setFecha(ts.toLocalDateTime());
                    m.setCodigoComanda(rs.getString("CodigoComanda"));
                    m.setIdMozo(rs.getInt("IdMozo"));
                    m.setIdEstadoComanda(rs.getInt("IdEstadoComanda"));
                    m.setIdMesaGrupo(rs.getInt("IdMesaGrupo"));
                    m.setTotalPagar(rs.getDouble("TotalPagar"));
                    m.setNombreMozo(rs.getString("NomMozo"));
                    m.setNombreEstadoComanda(rs.getString("NomEstadoCom"));
                    m.setNumeroMesa(rs.getString("NumMesa"));
                    return m;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Movimiento> listarPorEstadoComanda(int idEstadoComanda) throws RemoteException {
        List<Movimiento> lista = new ArrayList<>();
        String sql = "SELECT m.*, ec.Nombre AS NomEstadoCom, mo.Nombre AS NomMozo, " +
                "STRING_AGG(me.Numero, ', ') AS Mesas FROM Movimiento m " +
                "INNER JOIN EstadoComanda ec ON m.IdEstadoComanda = ec.IdEstadoComanda " +
                "LEFT JOIN Mozo mo ON m.IdMozo = mo.IdMozo " +
                "LEFT JOIN MovimientoMesa mm ON m.IdMovimiento = mm.IdMovimiento " +
                "LEFT JOIN Mesa me ON mm.IdMesa = me.IdMesa " +
                "WHERE m.IdEstadoComanda = ? GROUP BY m.IdMovimiento, ec.Nombre, mo.Nombre " +
                "ORDER BY m.Fecha DESC";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEstadoComanda);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Movimiento m = new Movimiento();
                    m.setIdMovimiento(rs.getInt("IdMovimiento"));
                    Timestamp ts = rs.getTimestamp("Fecha");
                    if (ts != null) m.setFecha(ts.toLocalDateTime());
                    m.setCodigoComanda(rs.getString("CodigoComanda"));
                    m.setTotalPagar(rs.getDouble("TotalPagar"));
                    m.setIdEstadoComanda(rs.getInt("IdEstadoComanda"));
                    m.setNombreEstadoComanda(rs.getString("NomEstadoCom"));
                    m.setNombreMozo(rs.getString("NomMozo"));
                    m.setNumeroMesa(rs.getString("Mesas"));
                    lista.add(m);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Movimiento> listarPorMozo(int idMozo) throws RemoteException {
        List<Movimiento> lista = new ArrayList<>();
        String sql = "SELECT m.*, ec.Nombre AS NomEstadoCom, " +
                "STRING_AGG(me.Numero, ', ') AS Mesas FROM Movimiento m " +
                "INNER JOIN EstadoComanda ec ON m.IdEstadoComanda = ec.IdEstadoComanda " +
                "LEFT JOIN MovimientoMesa mm ON m.IdMovimiento = mm.IdMovimiento " +
                "LEFT JOIN Mesa me ON mm.IdMesa = me.IdMesa " +
                "WHERE m.IdMozo = ? GROUP BY m.IdMovimiento, ec.Nombre " +
                "ORDER BY m.Fecha DESC";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMozo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Movimiento m = new Movimiento();
                    m.setIdMovimiento(rs.getInt("IdMovimiento"));
                    Timestamp ts = rs.getTimestamp("Fecha");
                    if (ts != null) m.setFecha(ts.toLocalDateTime());
                    m.setCodigoComanda(rs.getString("CodigoComanda"));
                    m.setTotalPagar(rs.getDouble("TotalPagar"));
                    m.setIdEstadoComanda(rs.getInt("IdEstadoComanda"));
                    m.setNombreEstadoComanda(rs.getString("NomEstadoCom"));
                    m.setNumeroMesa(rs.getString("Mesas"));
                    lista.add(m);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Movimiento> listarComandasAbiertas() throws RemoteException {
        List<Movimiento> lista = new ArrayList<>();
        String sql = "SELECT m.*, ec.Nombre AS NomEstadoCom, mo.Nombre AS NomMozo, " +
                "STRING_AGG(me.Numero, ', ') AS Mesas FROM Movimiento m " +
                "INNER JOIN EstadoComanda ec ON m.IdEstadoComanda = ec.IdEstadoComanda " +
                "LEFT JOIN Mozo mo ON m.IdMozo = mo.IdMozo " +
                "LEFT JOIN MovimientoMesa mm ON m.IdMovimiento = mm.IdMovimiento " +
                "LEFT JOIN Mesa me ON mm.IdMesa = me.IdMesa " +
                "WHERE m.IdEstadoComanda NOT IN (5,6) " +
                "GROUP BY m.IdMovimiento, ec.Nombre, mo.Nombre " +
                "ORDER BY m.Fecha DESC";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Movimiento m = new Movimiento();
                m.setIdMovimiento(rs.getInt("IdMovimiento"));
                Timestamp ts = rs.getTimestamp("Fecha");
                if (ts != null) m.setFecha(ts.toLocalDateTime());
                m.setCodigoComanda(rs.getString("CodigoComanda"));
                m.setTotalPagar(rs.getDouble("TotalPagar"));
                m.setIdEstadoComanda(rs.getInt("IdEstadoComanda"));
                m.setNombreEstadoComanda(rs.getString("NomEstadoCom"));
                m.setNombreMozo(rs.getString("NomMozo"));
                m.setNumeroMesa(rs.getString("Mesas"));
                lista.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean abrirComanda(Movimiento obj) throws RemoteException {
        String sqlMov = "INSERT INTO Movimiento (Fecha, NroDocumento, IdCliente, IdTipoMovimiento, " +
                "TotalPagar, IdDatosEmpresa, IdEstadoMovimiento, NumPersonas, " +
                "CodigoComanda, IdMozo, IdEstadoComanda, IdMesaGrupo) " +
                "VALUES (NOW(),?,?,?,?,?,?,?,?,?,?,?)";
        String sqlMM = "INSERT INTO MovimientoMesa (IdMovimiento, IdMesa) VALUES (?, ?)";
        String sqlMesa = "UPDATE Mesa SET IdEstadoMesa = 2 WHERE IdMesa = ?";
        Connection con = null;
        try {
            con = Conexion.getConexion();
            con.setAutoCommit(false);
            int idGenerado = 0;

            try (PreparedStatement ps = con.prepareStatement(sqlMov, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, obj.getNroDocumento());
                ps.setInt(2, obj.getIdCliente());
                ps.setInt(3, obj.getIdTipoMovimiento());
                ps.setDouble(4, obj.getTotalPagar());
                ps.setInt(5, obj.getIdDatosEmpresa());
                ps.setInt(6, obj.getIdEstadoMovimiento());
                ps.setInt(7, obj.getNumPersonas());
                ps.setString(8, obj.getCodigoComanda());
                if (obj.getIdMozo() > 0) { ps.setInt(9, obj.getIdMozo()); } else { ps.setNull(9, java.sql.Types.INTEGER); }
                ps.setInt(10, obj.getIdEstadoComanda());
                if (obj.getIdMesaGrupo() > 0) { ps.setInt(11, obj.getIdMesaGrupo()); } else { ps.setNull(11, java.sql.Types.INTEGER); }
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) idGenerado = rs.getInt(1);
                }
            }
            if (idGenerado <= 0) {
                con.rollback();
                return false;
            }

            if (obj.getListaMesas() != null && !obj.getListaMesas().isEmpty()) {
                try (PreparedStatement pstmMesa = con.prepareStatement(sqlMM)) {
                    for (int idMesa : obj.getListaMesas()) {
                        pstmMesa.setInt(1, idGenerado);
                        pstmMesa.setInt(2, idMesa);
                        pstmMesa.addBatch();
                    }
                    pstmMesa.executeBatch();
                }
                try (PreparedStatement psEstado = con.prepareStatement(sqlMesa)) {
                    for (int idMesa : obj.getListaMesas()) {
                        psEstado.setInt(1, idMesa);
                        psEstado.addBatch();
                    }
                    psEstado.executeBatch();
                }
            }

            con.commit();
            obj.setIdMovimiento(idGenerado);
            return true;
        } catch (SQLException e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ignored) {}
            }
            e.printStackTrace();
            return false;
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (SQLException ignored) {}
            }
        }
    }
}