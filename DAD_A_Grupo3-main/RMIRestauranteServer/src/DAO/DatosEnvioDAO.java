package DAO;
import DTO.DatosEnvio;
import Interface.IDatosEnvio;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatosEnvioDAO extends UnicastRemoteObject implements IDatosEnvio {

    public DatosEnvioDAO() throws RemoteException {
    }

    @Override
    public List<DatosEnvio> listar() throws RemoteException {
        List<DatosEnvio> lista = new ArrayList<>();
        String sql = "SELECT * FROM DatosEnvio ORDER BY IdDatosEnvio";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                DatosEnvio de = new DatosEnvio();
                de.setIdDatosEnvio(rs.getInt("IdDatosEnvio"));
                de.setDestinatario(rs.getString("Destinatario"));
                de.setTelefono(rs.getString("Telefono"));
                de.setEmail(rs.getString("Email"));
                de.setDireccion(rs.getString("Direccion"));
                de.setIdMovimiento(rs.getInt("IdMovimiento"));
                lista.add(de);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean insertar(DatosEnvio obj) throws RemoteException {
        String sql = "INSERT INTO DatosEnvio (Destinatario, Telefono, Email, Direccion, IdMovimiento) VALUES (?,?,?,?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, obj.getDestinatario());
            ps.setString(2, obj.getTelefono());
            ps.setString(3, obj.getEmail());
            ps.setString(4, obj.getDireccion());
            ps.setInt(5, obj.getIdMovimiento());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean actualizar(DatosEnvio obj) throws RemoteException {
        String sql = "UPDATE DatosEnvio SET Destinatario=?, Telefono=?, Email=?, Direccion=? WHERE IdDatosEnvio=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, obj.getDestinatario());
            ps.setString(2, obj.getTelefono());
            ps.setString(3, obj.getEmail());
            ps.setString(4, obj.getDireccion());
            ps.setInt(5, obj.getIdDatosEnvio());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) throws RemoteException {
        String sql = "DELETE FROM DatosEnvio WHERE IdDatosEnvio=?";
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
    public DatosEnvio buscar(int id) throws RemoteException {
        String sql = "SELECT * FROM DatosEnvio WHERE IdDatosEnvio=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DatosEnvio de = new DatosEnvio();
                    de.setIdDatosEnvio(rs.getInt("IdDatosEnvio"));
                    de.setDestinatario(rs.getString("Destinatario"));
                    de.setTelefono(rs.getString("Telefono"));
                    de.setEmail(rs.getString("Email"));
                    de.setDireccion(rs.getString("Direccion"));
                    de.setIdMovimiento(rs.getInt("IdMovimiento"));
                    return de;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}