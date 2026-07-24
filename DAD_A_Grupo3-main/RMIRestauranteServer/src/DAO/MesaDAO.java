package DAO;
import DTO.Mesa;
import Interface.IMesa;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MesaDAO extends UnicastRemoteObject implements IMesa {

    public MesaDAO() throws RemoteException {
    }

    @Override
    public List<Mesa> listar(String texto) throws RemoteException {
        List<Mesa> lista = new ArrayList<>();
        String sql = "SELECT m.IdMesa, m.Numero, m.Capacidad, m.IdSalon, m.IdEstadoMesa, " +
                "s.Nombre AS NomSalon, e.Nombre AS NomEst " +
                "FROM Mesa m " +
                "INNER JOIN Salon s ON m.IdSalon = s.IdSalon " +
                "INNER JOIN EstadoMesa e ON m.IdEstadoMesa = e.IdEstadoMesa " +
                "WHERE m.Numero LIKE ? ORDER BY m.IdMesa";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Mesa m = new Mesa();
                m.setIdMesa(rs.getInt("IdMesa"));
                m.setIdSalon(rs.getInt("IdSalon"));
                m.setNumero(rs.getString("Numero"));
                m.setCapacidad(rs.getInt("Capacidad"));
                m.setIdEstadoMesa(rs.getInt("IdEstadoMesa"));
                m.setNombreSalon(rs.getString("NomSalon"));
                m.setNombreEstadoMesa(rs.getString("NomEst"));
                lista.add(m);
            }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean insertar(Mesa obj) throws RemoteException {
        String sql = "INSERT INTO Mesa (IdSalon, Numero, Capacidad, IdEstadoMesa) VALUES (?,?,?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, obj.getIdSalon());
            ps.setString(2, obj.getNumero());
            ps.setInt(3, obj.getCapacidad());
            int estado = obj.getIdEstadoMesa();
            if (estado <= 0) estado = 1;
            ps.setInt(4, estado);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean actualizar(Mesa obj) throws RemoteException {
        String sql = "UPDATE Mesa SET IdSalon=?, Numero=?, Capacidad=?, IdEstadoMesa=? WHERE IdMesa=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, obj.getIdSalon());
            ps.setString(2, obj.getNumero());
            ps.setInt(3, obj.getCapacidad());
            ps.setInt(4, obj.getIdEstadoMesa());
            ps.setInt(5, obj.getIdMesa());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) throws RemoteException {
        String sql = "DELETE FROM Mesa WHERE IdMesa=?";
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
    public boolean cambiarEstado(int idMesa, int nuevoEstado) throws RemoteException {
        String sql = "UPDATE Mesa SET IdEstadoMesa=? WHERE IdMesa=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nuevoEstado);
            ps.setInt(2, idMesa);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}