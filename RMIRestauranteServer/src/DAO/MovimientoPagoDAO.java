package DAO;
import DTO.MovimientoPago;
import Interface.IMovimientoPago;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovimientoPagoDAO extends UnicastRemoteObject implements IMovimientoPago {

    public MovimientoPagoDAO() throws RemoteException {
    }

    @Override
    public boolean insertar(MovimientoPago obj) throws RemoteException {
        String sqlTotal = "SELECT TotalPagar FROM Movimiento WHERE IdMovimiento = ?";
        String sqlPagosPrevios = "SELECT COALESCE(SUM(Monto), 0) FROM MovimientoPago WHERE IdMovimiento = ?";
        String sqlInsert = "INSERT INTO MovimientoPago (IdMovimiento, IdFormaPago, Monto) VALUES (?,?,?)";
        try (Connection con = Conexion.getConexion()) {
            double totalPagar = 0;
            try (PreparedStatement ps = con.prepareStatement(sqlTotal)) {
                ps.setInt(1, obj.getIdMovimiento());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) totalPagar = rs.getDouble(1);
                }
            }
            double pagosPrevios = 0;
            try (PreparedStatement ps = con.prepareStatement(sqlPagosPrevios)) {
                ps.setInt(1, obj.getIdMovimiento());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) pagosPrevios = rs.getDouble(1);
                }
            }
            if (pagosPrevios + obj.getMonto() > totalPagar + 0.01) {
                System.out.println("Sobrepago bloqueado: previos=" + pagosPrevios + " + nuevo=" + obj.getMonto() + " > total=" + totalPagar);
                return false;
            }
            try (PreparedStatement ps = con.prepareStatement(sqlInsert)) {
                ps.setInt(1, obj.getIdMovimiento());
                ps.setInt(2, obj.getIdFormaPago());
                ps.setDouble(3, obj.getMonto());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<MovimientoPago> listarPorMovimiento(int idMovimiento) throws RemoteException {
        List<MovimientoPago> lista = new ArrayList<>();
        String sql = "SELECT mp.IdMovimiento, mp.IdFormaPago, mp.Monto, fp.Nombre AS NomForma " +
                "FROM MovimientoPago mp " +
                "INNER JOIN FormaPago fp ON mp.IdFormaPago = fp.IdFormaPago " +
                "WHERE mp.IdMovimiento = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMovimiento);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MovimientoPago mp = new MovimientoPago();
                    mp.setIdMovimiento(rs.getInt("IdMovimiento"));
                    mp.setIdFormaPago(rs.getInt("IdFormaPago"));
                    mp.setMonto(rs.getDouble("Monto"));
                    mp.setNombreFormaPago(rs.getString("NomForma"));
                    lista.add(mp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}