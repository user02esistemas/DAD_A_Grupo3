package DAO;
import DTO.Cliente;
import Interface.ICliente;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO extends UnicastRemoteObject implements ICliente {

    public ClienteDAO() throws RemoteException {
    }

    @Override
    public List<Cliente> listar(String texto) throws RemoteException {
        List<Cliente> lista = new ArrayList<>();
        String sql =
            "SELECT c.IdCliente, c.Nombre, c.Documento, c.IdTipoDocumento, c.IdTipoCliente, " +
            "c.Apellido, c.Telefono, c.Sexo, c.Direccion, c.Email, " +
            "td.Nombre AS NomDoc, tc.Nombre AS NomCli " +
            "FROM Cliente c " +
            "INNER JOIN TipoDocumento td ON c.IdTipoDocumento = td.IdTipoDocumento " +
            "INNER JOIN TipoCliente tc ON c.IdTipoCliente = tc.IdTipoCliente " +
            "WHERE c.Nombre LIKE ? OR c.Apellido LIKE ? OR c.Documento LIKE ? " +
            "ORDER BY c.IdCliente";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            ps.setString(2, "%" + texto + "%");
            ps.setString(3, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cliente c = new Cliente();
                    c.setIdCliente(rs.getInt("IdCliente"));
                    c.setNombre(rs.getString("Nombre"));
                    c.setDocumento(rs.getString("Documento"));
                    c.setIdTipoDocumento(rs.getInt("IdTipoDocumento"));
                    c.setIdTipoCliente(rs.getInt("IdTipoCliente"));
                    c.setApellido(rs.getString("Apellido"));
                    c.setTelefono(rs.getString("Telefono"));
                    c.setSexo(rs.getString("Sexo"));
                    c.setDireccion(rs.getString("Direccion"));
                    c.setEmail(rs.getString("Email"));
                    c.setNombreTipoDocumento(rs.getString("NomDoc"));
                    c.setNombreTipoCliente(rs.getString("NomCli"));
                    lista.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean insertar(Cliente obj) throws RemoteException {
        String sql =
            "INSERT INTO Cliente (IdTipoDocumento, Nombre, Documento, IdTipoCliente, Apellido, Telefono, Sexo, Direccion, Email) " +
            "VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, obj.getIdTipoDocumento());
            ps.setString(2, obj.getNombre());
            ps.setString(3, obj.getDocumento());
            ps.setInt(4, obj.getIdTipoCliente());
            ps.setString(5, obj.getApellido());
            ps.setString(6, obj.getTelefono());
            ps.setString(7, obj.getSexo() != null ? obj.getSexo() : "M");
            ps.setString(8, obj.getDireccion());
            ps.setString(9, obj.getEmail());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean actualizar(Cliente obj) throws RemoteException {
        String sql =
            "UPDATE Cliente SET IdTipoDocumento=?, Nombre=?, Documento=?, IdTipoCliente=?, " +
            "Apellido=?, Telefono=?, Sexo=?, Direccion=?, Email=? WHERE IdCliente=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, obj.getIdTipoDocumento());
            ps.setString(2, obj.getNombre());
            ps.setString(3, obj.getDocumento());
            ps.setInt(4, obj.getIdTipoCliente());
            ps.setString(5, obj.getApellido());
            ps.setString(6, obj.getTelefono());
            ps.setString(7, obj.getSexo() != null ? obj.getSexo() : "M");
            ps.setString(8, obj.getDireccion());
            ps.setString(9, obj.getEmail());
            ps.setInt(10, obj.getIdCliente());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) throws RemoteException {
        String sql = "DELETE FROM Cliente WHERE IdCliente=?";
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
    public Cliente buscarPorId(int id) throws RemoteException {
        String sql =
            "SELECT c.*, td.Nombre AS NomDoc, tc.Nombre AS NomCli " +
            "FROM Cliente c " +
            "INNER JOIN TipoDocumento td ON c.IdTipoDocumento = td.IdTipoDocumento " +
            "INNER JOIN TipoCliente tc ON c.IdTipoCliente = tc.IdTipoCliente " +
            "WHERE c.IdCliente=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cliente c = new Cliente();
                    c.setIdCliente(rs.getInt("IdCliente"));
                    c.setNombre(rs.getString("Nombre"));
                    c.setApellido(rs.getString("Apellido"));
                    c.setDocumento(rs.getString("Documento"));
                    c.setTelefono(rs.getString("Telefono"));
                    c.setSexo(rs.getString("Sexo"));
                    c.setDireccion(rs.getString("Direccion"));
                    c.setEmail(rs.getString("Email"));
                    c.setIdTipoDocumento(rs.getInt("IdTipoDocumento"));
                    c.setIdTipoCliente(rs.getInt("IdTipoCliente"));
                    c.setNombreTipoDocumento(rs.getString("NomDoc"));
                    c.setNombreTipoCliente(rs.getString("NomCli"));
                    return c;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Cliente buscarPorDocumento(String doc) throws RemoteException {
        String sql =
            "SELECT c.*, td.Nombre AS NomDoc, tc.Nombre AS NomCli " +
            "FROM Cliente c " +
            "INNER JOIN TipoDocumento td ON c.IdTipoDocumento = td.IdTipoDocumento " +
            "INNER JOIN TipoCliente tc ON c.IdTipoCliente = tc.IdTipoCliente " +
            "WHERE c.Documento=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, doc);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cliente c = new Cliente();
                    c.setIdCliente(rs.getInt("IdCliente"));
                    c.setNombre(rs.getString("Nombre"));
                    c.setApellido(rs.getString("Apellido"));
                    c.setDocumento(rs.getString("Documento"));
                    c.setTelefono(rs.getString("Telefono"));
                    c.setEmail(rs.getString("Email"));
                    c.setIdTipoDocumento(rs.getInt("IdTipoDocumento"));
                    c.setIdTipoCliente(rs.getInt("IdTipoCliente"));
                    c.setNombreTipoDocumento(rs.getString("NomDoc"));
                    c.setNombreTipoCliente(rs.getString("NomCli"));
                    return c;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Cliente> listarAtendidos() throws RemoteException {
        List<Cliente> lista = new ArrayList<>();
        String sql =
            "SELECT c.IdCliente, c.IdTipoDocumento, c.Nombre, c.Documento, " +
            "c.IdTipoCliente, c.Apellido, c.Telefono, c.Sexo, c.Direccion, c.Email, " +
            "td.Nombre AS NomDoc, tc.Nombre AS NomCli, " +
            "COUNT(m.IdMovimiento) AS NumPedidos, MAX(m.Fecha) AS UltimaFecha " +
            "FROM Cliente c " +
            "INNER JOIN TipoDocumento td ON c.IdTipoDocumento = td.IdTipoDocumento " +
            "INNER JOIN TipoCliente tc ON c.IdTipoCliente = tc.IdTipoCliente " +
            "INNER JOIN Movimiento m ON c.IdCliente = m.IdCliente " +
            "GROUP BY c.IdCliente, c.Nombre, c.Documento, c.Apellido, c.Telefono, " +
            "c.Sexo, c.Direccion, c.Email, c.IdTipoDocumento, c.IdTipoCliente, " +
            "td.Nombre, tc.Nombre " +
            "ORDER BY MAX(m.Fecha) DESC";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setIdCliente(rs.getInt("IdCliente"));
                c.setNombre(rs.getString("Nombre"));
                c.setDocumento(rs.getString("Documento"));
                c.setIdTipoDocumento(rs.getInt("IdTipoDocumento"));
                c.setIdTipoCliente(rs.getInt("IdTipoCliente"));
                c.setApellido(rs.getString("Apellido"));
                c.setTelefono(rs.getString("Telefono"));
                c.setSexo(rs.getString("Sexo"));
                c.setDireccion(rs.getString("Direccion"));
                c.setEmail(rs.getString("Email"));
                c.setNombreTipoDocumento(rs.getString("NomDoc"));
                c.setNombreTipoCliente(rs.getString("NomCli"));
                lista.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Cliente login(String string, String string1) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}