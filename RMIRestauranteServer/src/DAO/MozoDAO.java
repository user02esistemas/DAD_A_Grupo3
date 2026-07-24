package DAO;
import DTO.Mozo;
import Interface.IMozo;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MozoDAO extends UnicastRemoteObject implements IMozo {

    public MozoDAO() throws RemoteException {
    }

    @Override
    public Mozo login(String usuario, String clave) throws RemoteException {
        String sql = "SELECT m.*, COALESCE(s.Nombre,'') AS NomSalon FROM Mozo m LEFT JOIN Salon s ON m.IdSalon=s.IdSalon WHERE m.Usuario=? AND m.Clave=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, clave);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Mozo(
                        rs.getInt("IdMozo"),
                        rs.getString("Nombre"),
                        rs.getString("Apellido"),
                        rs.getString("Telefono"),
                        rs.getString("Usuario"),
                        rs.getString("Clave"),
                        rs.getInt("IdSalon"),
                        rs.getBoolean("Activo"),
                        rs.getString("NomSalon")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Mozo> listar() throws RemoteException {
        List<Mozo> lista = new ArrayList<>();
        String sql = "SELECT m.*, COALESCE(s.Nombre,'') AS NomSalon FROM Mozo m LEFT JOIN Salon s ON m.IdSalon=s.IdSalon ORDER BY m.IdMozo";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Mozo(
                    rs.getInt("IdMozo"),
                    rs.getString("Nombre"),
                    rs.getString("Apellido"),
                    rs.getString("Telefono"),
                    rs.getString("Usuario"),
                    rs.getString("Clave"),
                    rs.getInt("IdSalon"),
                    rs.getBoolean("Activo"),
                    rs.getString("NomSalon")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean insertar(Mozo obj) throws RemoteException {
        String sql = "INSERT INTO Mozo (Nombre, Apellido, Usuario, Clave, Telefono, IdSalon, Activo) VALUES (?,?,?,?,?,?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, obj.getNombre());
            ps.setString(2, obj.getApellido());
            ps.setString(3, obj.getUsuario());
            ps.setString(4, obj.getClave());
            ps.setString(5, obj.getTelefono());
            ps.setInt(6, obj.getIdSalon());
            ps.setBoolean(7, obj.isActivo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean actualizar(Mozo obj) throws RemoteException {
        String sql = "UPDATE Mozo SET Nombre=?, Apellido=?, Usuario=?, Clave=?, Telefono=?, IdSalon=?, Activo=? WHERE IdMozo=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, obj.getNombre());
            ps.setString(2, obj.getApellido());
            ps.setString(3, obj.getUsuario());
            ps.setString(4, obj.getClave());
            ps.setString(5, obj.getTelefono());
            ps.setInt(6, obj.getIdSalon());
            ps.setBoolean(7, obj.isActivo());
            ps.setInt(8, obj.getIdMozo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) throws RemoteException {
        String sql = "DELETE FROM Mozo WHERE IdMozo=?";
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
    public Mozo buscar(int id) throws RemoteException {
        String sql = "SELECT m.*, COALESCE(s.Nombre,'') AS NomSalon FROM Mozo m LEFT JOIN Salon s ON m.IdSalon=s.IdSalon WHERE m.IdMozo=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Mozo(
                        rs.getInt("IdMozo"),
                        rs.getString("Nombre"),
                        rs.getString("Apellido"),
                        rs.getString("Telefono"),
                        rs.getString("Usuario"),
                        rs.getString("Clave"),
                        rs.getInt("IdSalon"),
                        rs.getBoolean("Activo"),
                        rs.getString("NomSalon")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}