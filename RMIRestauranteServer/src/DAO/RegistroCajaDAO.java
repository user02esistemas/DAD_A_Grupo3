package DAO;
import DTO.RegistroCaja;
import Interface.IRegistroCaja;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistroCajaDAO extends UnicastRemoteObject implements IRegistroCaja {

    public RegistroCajaDAO() throws RemoteException {
    }

    @Override
    public boolean estaAbierta() throws RemoteException {
        String sql = "SELECT COUNT(*) FROM RegistroCaja WHERE Estado = 'ABIERTA'";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void abrir(double monto) throws RemoteException {
        String sql = "INSERT INTO RegistroCaja (MontoApertura, Estado) VALUES (?, 'ABIERTA')";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, monto);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cerrar(double monto) throws RemoteException {
        String sqlId = "SELECT IdRegistroCaja FROM RegistroCaja WHERE Estado = 'ABIERTA' ORDER BY FechaApertura DESC LIMIT 1";
        String sqlCerrar = "UPDATE RegistroCaja SET FechaCierre = NOW(), MontoCierre = ?, Estado = 'CERRADA' WHERE IdRegistroCaja = ?";
        try (Connection con = Conexion.getConexion()) {
            int idCaja = 0;
            try (PreparedStatement ps = con.prepareStatement(sqlId);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) idCaja = rs.getInt(1);
            }
            if (idCaja <= 0) return;
            try (PreparedStatement ps = con.prepareStatement(sqlCerrar)) {
                ps.setDouble(1, monto);
                ps.setInt(2, idCaja);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<RegistroCaja> listarHistorial() throws RemoteException {
        List<RegistroCaja> lista = new ArrayList<>();
        String sql = "SELECT IdRegistroCaja, MontoApertura, FechaApertura, MontoCierre, FechaCierre, Estado FROM RegistroCaja ORDER BY IdRegistroCaja DESC";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                RegistroCaja rc = new RegistroCaja();
                rc.setIdRegistroCaja(rs.getInt("IdRegistroCaja"));
                rc.setMontoApertura(rs.getDouble("MontoApertura"));
                Timestamp fa = rs.getTimestamp("FechaApertura");
                if (fa != null) rc.setFechaApertura(fa.toLocalDateTime());
                Timestamp fc = rs.getTimestamp("FechaCierre");
                if (fc != null) rc.setFechaCierre(fc.toLocalDateTime());
                rc.setMontoCierre(rs.getDouble("MontoCierre"));
                rc.setEstado(rs.getString("Estado"));
                lista.add(rc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}