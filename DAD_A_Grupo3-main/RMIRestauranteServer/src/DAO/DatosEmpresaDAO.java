package DAO;
import DTO.DatosEmpresa;
import Interface.IDatosEmpresa;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatosEmpresaDAO extends UnicastRemoteObject implements IDatosEmpresa {

    public DatosEmpresaDAO() throws RemoteException {
    }

    @Override
    public List<DatosEmpresa> listar() throws RemoteException {
        List<DatosEmpresa> lista = new ArrayList<>();
        String sql =
            "SELECT IdEmpresa, RUC, RazonSocial, Direccion, Telefono, Email, " +
            "COALESCE(QrBaseUrl, 'http://localhost:8080/RMIRestauranteWeb') AS QrBaseUrl, " +
            "COALESCE(QrSecret, 'overos2026') AS QrSecret " +
            "FROM DatosEmpresa";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                DatosEmpresa de = new DatosEmpresa();
                de.setIdEmpresa(rs.getInt("IdEmpresa"));
                de.setRuc(rs.getString("RUC"));
                de.setRazonSocial(rs.getString("RazonSocial"));
                de.setDireccion(rs.getString("Direccion"));
                de.setTelefono(rs.getString("Telefono"));
                de.setEmail(rs.getString("Email"));
                de.setQrBaseUrl(rs.getString("QrBaseUrl"));
                de.setQrSecret(rs.getString("QrSecret"));
                lista.add(de);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean actualizar(DatosEmpresa obj) throws RemoteException {
        String sql = "UPDATE DatosEmpresa SET RUC=?, RazonSocial=?, Direccion=?, Telefono=?, Email=?, QrBaseUrl=?, QrSecret=? WHERE IdEmpresa=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, obj.getRuc());
            ps.setString(2, obj.getRazonSocial());
            ps.setString(3, obj.getDireccion());
            ps.setString(4, obj.getTelefono());
            ps.setString(5, obj.getEmail());
            ps.setString(6, obj.getQrBaseUrl());
            ps.setString(7, obj.getQrSecret());
            ps.setInt(8, obj.getIdEmpresa());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}