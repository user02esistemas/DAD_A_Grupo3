package DAO;
import DTO.Producto;
import Interface.IProducto;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO extends UnicastRemoteObject implements IProducto {

    public ProductoDAO() throws RemoteException {
    }

    @Override
    public List<Producto> listar(String texto) throws RemoteException {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT p.IdProducto, p.Nombre, p.Descripcion, p.IdTipoProducto, " +
                "p.IdTiempoPreparacion, p.IdEstadoProducto, " +
                "tp.Nombre AS NomTipo, tpr.Nombre AS NomTiempo, ep.Nombre AS NomEstado, " +
                "p.RutaImg, " +
                "COALESCE((SELECT MIN(pre.Precio) FROM Presentacion pre WHERE pre.IdProducto = p.IdProducto), 0) AS PrecioMin " +
                "FROM Producto p " +
                "INNER JOIN TipoProducto tp ON p.IdTipoProducto = tp.IdTipoProducto " +
                "INNER JOIN TiempoPreparacion tpr ON p.IdTiempoPreparacion = tpr.IdTiempoPreparacion " +
                "INNER JOIN EstadoProducto ep ON p.IdEstadoProducto = ep.IdEstadoProducto " +
                "WHERE p.Nombre LIKE ? ORDER BY p.IdProducto";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Producto p = new Producto();
                p.setIdProducto(rs.getInt("IdProducto"));
                p.setNombre(rs.getString("Nombre"));
                p.setDescripcion(rs.getString("Descripcion"));
                p.setIdTipoProducto(rs.getInt("IdTipoProducto"));
                p.setIdTiempoPreparacion(rs.getInt("IdTiempoPreparacion"));
                p.setIdEstadoProducto(rs.getInt("IdEstadoProducto"));
                p.setNombreTipoProducto(rs.getString("NomTipo"));
                p.setNombreTiempoPreparacion(rs.getString("NomTiempo"));
                p.setNombreEstadoProducto(rs.getString("NomEstado"));
                p.setRutaImg(rs.getString("RutaImg"));
                p.setPrecioMinimo(rs.getDouble("PrecioMin"));
                lista.add(p);
            }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean insertar(Producto obj) throws RemoteException {
        String sql = "INSERT INTO Producto (IdTipoProducto, Nombre, Descripcion, IdTiempoPreparacion, IdEstadoProducto) " +
                "VALUES (?,?,?,?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, obj.getIdTipoProducto());
            ps.setString(2, obj.getNombre());
            ps.setString(3, obj.getDescripcion());
            ps.setInt(4, obj.getIdTiempoPreparacion());
            ps.setInt(5, obj.getIdEstadoProducto());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean actualizar(Producto obj) throws RemoteException {
        String sql = "UPDATE Producto SET IdTipoProducto=?, Nombre=?, Descripcion=?, " +
                "IdTiempoPreparacion=?, IdEstadoProducto=? WHERE IdProducto=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, obj.getIdTipoProducto());
            ps.setString(2, obj.getNombre());
            ps.setString(3, obj.getDescripcion());
            ps.setInt(4, obj.getIdTiempoPreparacion());
            ps.setInt(5, obj.getIdEstadoProducto());
            ps.setInt(6, obj.getIdProducto());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) throws RemoteException {
        String sql = "DELETE FROM Producto WHERE IdProducto=?";
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
    public Producto buscar(int id) throws RemoteException {
        String sql = "SELECT p.IdProducto, p.Nombre, p.Descripcion, p.IdTipoProducto, " +
                "p.IdTiempoPreparacion, p.IdEstadoProducto, " +
                "tp.Nombre AS NomTipo, tpr.Nombre AS NomTiempo, ep.Nombre AS NomEstado, " +
                "COALESCE((SELECT MIN(pre.Precio) FROM Presentacion pre WHERE pre.IdProducto = p.IdProducto), 0) AS PrecioMin " +
                "FROM Producto p " +
                "INNER JOIN TipoProducto tp ON p.IdTipoProducto = tp.IdTipoProducto " +
                "INNER JOIN TiempoPreparacion tpr ON p.IdTiempoPreparacion = tpr.IdTiempoPreparacion " +
                "INNER JOIN EstadoProducto ep ON p.IdEstadoProducto = ep.IdEstadoProducto " +
                "WHERE p.IdProducto = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Producto p = new Producto();
                p.setIdProducto(rs.getInt("IdProducto"));
                p.setNombre(rs.getString("Nombre"));
                p.setDescripcion(rs.getString("Descripcion"));
                p.setIdTipoProducto(rs.getInt("IdTipoProducto"));
                p.setIdTiempoPreparacion(rs.getInt("IdTiempoPreparacion"));
                p.setIdEstadoProducto(rs.getInt("IdEstadoProducto"));
                p.setNombreTipoProducto(rs.getString("NomTipo"));
                p.setNombreTiempoPreparacion(rs.getString("NomTiempo"));
                p.setNombreEstadoProducto(rs.getString("NomEstado"));
                p.setPrecioMinimo(rs.getDouble("PrecioMin"));
                return p;
            }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}