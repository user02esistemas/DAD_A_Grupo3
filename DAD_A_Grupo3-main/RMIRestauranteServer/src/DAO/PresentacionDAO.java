package DAO;
import DTO.Presentacion;
import Interface.IPresentacion;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PresentacionDAO extends UnicastRemoteObject implements IPresentacion {

    public PresentacionDAO() throws RemoteException {
    }

    @Override
    public List<Presentacion> listar(String texto) throws RemoteException {
        List<Presentacion> lista = new ArrayList<>();
        String sql = "SELECT pre.IdPresentacion, pre.IdProducto, pre.Nombre, pre.Precio, " +
                "pre.Stock, pre.IdEstadoPresentacion, pre.ImagenUrl, " +
                "p.Nombre AS NomProd, ep.Nombre AS NomEst " +
                "FROM Presentacion pre " +
                "INNER JOIN Producto p ON pre.IdProducto = p.IdProducto " +
                "INNER JOIN EstadoPresentacion ep ON pre.IdEstadoPresentacion = ep.IdEstadoPresentacion " +
                "WHERE pre.Nombre LIKE ? ORDER BY pre.IdPresentacion";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Presentacion pre = new Presentacion();
                pre.setIdPresentacion(rs.getInt("IdPresentacion"));
                pre.setIdProducto(rs.getInt("IdProducto"));
                pre.setNombre(rs.getString("Nombre"));
                pre.setPrecio(rs.getDouble("Precio"));
                pre.setStock(rs.getInt("Stock"));
                pre.setIdEstadoPresentacion(rs.getInt("IdEstadoPresentacion"));
                pre.setImagenUrl(rs.getString("ImagenUrl"));
                pre.setNombreProducto(rs.getString("NomProd"));
                pre.setNombreEstadoPresentacion(rs.getString("NomEst"));
                lista.add(pre);
            }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Presentacion> listarPorProducto(int idProducto) throws RemoteException {
        List<Presentacion> lista = new ArrayList<>();
        String sql = "SELECT pre.IdPresentacion, pre.IdProducto, pre.Nombre, pre.Precio, " +
                "pre.Stock, pre.IdEstadoPresentacion, pre.ImagenUrl, " +
                "p.Nombre AS NomProd, ep.Nombre AS NomEst " +
                "FROM Presentacion pre " +
                "INNER JOIN Producto p ON pre.IdProducto = p.IdProducto " +
                "INNER JOIN EstadoPresentacion ep ON pre.IdEstadoPresentacion = ep.IdEstadoPresentacion " +
                "WHERE pre.IdProducto = ? ORDER BY pre.IdPresentacion";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Presentacion pre = new Presentacion();
                pre.setIdPresentacion(rs.getInt("IdPresentacion"));
                pre.setIdProducto(rs.getInt("IdProducto"));
                pre.setNombre(rs.getString("Nombre"));
                pre.setPrecio(rs.getDouble("Precio"));
                pre.setStock(rs.getInt("Stock"));
                pre.setIdEstadoPresentacion(rs.getInt("IdEstadoPresentacion"));
                pre.setImagenUrl(rs.getString("ImagenUrl"));
                pre.setNombreProducto(rs.getString("NomProd"));
                pre.setNombreEstadoPresentacion(rs.getString("NomEst"));
                lista.add(pre);
            }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean insertar(Presentacion obj) throws RemoteException {
        String sql = "INSERT INTO Presentacion (IdProducto, Nombre, Precio, Stock, IdEstadoPresentacion, ImagenUrl) " +
                "VALUES (?,?,?,?,?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, obj.getIdProducto());
            ps.setString(2, obj.getNombre());
            ps.setDouble(3, obj.getPrecio());
            ps.setInt(4, obj.getStock());
            ps.setInt(5, obj.getIdEstadoPresentacion());
            ps.setString(6, obj.getImagenUrl());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean actualizar(Presentacion obj) throws RemoteException {
        String sql = "UPDATE Presentacion SET IdProducto=?, Nombre=?, Precio=?, Stock=?, " +
                "IdEstadoPresentacion=?, ImagenUrl=? WHERE IdPresentacion=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, obj.getIdProducto());
            ps.setString(2, obj.getNombre());
            ps.setDouble(3, obj.getPrecio());
            ps.setInt(4, obj.getStock());
            ps.setInt(5, obj.getIdEstadoPresentacion());
            ps.setString(6, obj.getImagenUrl());
            ps.setInt(7, obj.getIdPresentacion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) throws RemoteException {
        String sql = "DELETE FROM Presentacion WHERE IdPresentacion=?";
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