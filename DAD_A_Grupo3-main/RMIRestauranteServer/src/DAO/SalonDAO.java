package DAO;
import DTO.Salon;
import Interface.ISalon;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalonDAO extends UnicastRemoteObject implements ISalon {

    public SalonDAO() throws RemoteException {
    }

    @Override
    public List<Salon> listar() throws RemoteException {
        List<Salon> lista = new ArrayList<>();
        String sql = "SELECT IdSalon, Nombre FROM Salon ORDER BY IdSalon";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Salon(rs.getInt("IdSalon"), rs.getString("Nombre")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean insertar(Salon obj) throws RemoteException {
        String sql = "INSERT INTO Salon (Nombre) VALUES (?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, obj.getNombre());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean actualizar(Salon obj) throws RemoteException {
        String sql = "UPDATE Salon SET Nombre = ? WHERE IdSalon = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, obj.getNombre());
            ps.setInt(2, obj.getIdSalon());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) throws RemoteException {
        String sql = "DELETE FROM Salon WHERE IdSalon = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}