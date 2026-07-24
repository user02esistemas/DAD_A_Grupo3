package DAO;
import DTO.MesaGrupo;
import Interface.IMesaGrupo;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MesaGrupoDAO extends UnicastRemoteObject implements IMesaGrupo {

    public MesaGrupoDAO() throws RemoteException {
    }

    @Override
    public List<MesaGrupo> listar() throws RemoteException {
        List<MesaGrupo> lista = new ArrayList<>();
        String sql = "SELECT mg.IdMesaGrupo, mg.Nombre, mg.NumPersonas, " +
                "STRING_AGG(CAST(m.Numero AS TEXT), ', ') AS Mesas " +
                "FROM MesaGrupo mg " +
                "LEFT JOIN Mesa m ON m.IdMesaGrupo = mg.IdMesaGrupo " +
                "GROUP BY mg.IdMesaGrupo, mg.Nombre, mg.NumPersonas ORDER BY mg.IdMesaGrupo";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MesaGrupo mg = new MesaGrupo();
                mg.setIdMesaGrupo(rs.getInt("IdMesaGrupo"));
                mg.setNombre(rs.getString("Nombre"));
                mg.setNumPersonas(rs.getInt("NumPersonas"));
                mg.setMesas(rs.getString("Mesas"));
                lista.add(mg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean insertar(MesaGrupo obj) throws RemoteException {
        String sql = "INSERT INTO MesaGrupo (Nombre, NumPersonas) VALUES (?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, obj.getNombre());
            ps.setInt(2, obj.getNumPersonas());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    obj.setIdMesaGrupo(rs.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean actualizar(MesaGrupo obj) throws RemoteException {
        String sql = "UPDATE MesaGrupo SET Nombre=?, NumPersonas=? WHERE IdMesaGrupo=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, obj.getNombre());
            ps.setInt(2, obj.getNumPersonas());
            ps.setInt(3, obj.getIdMesaGrupo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) throws RemoteException {
        try (Connection con = Conexion.getConexion()) {
            try (PreparedStatement ps1 = con.prepareStatement(
                    "UPDATE Mesa SET IdMesaGrupo = NULL WHERE IdMesaGrupo = ?")) {
                ps1.setInt(1, id);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = con.prepareStatement(
                    "DELETE FROM MesaGrupo WHERE IdMesaGrupo=?")) {
                ps2.setInt(1, id);
                return ps2.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public MesaGrupo buscar(int id) throws RemoteException {
        String sql = "SELECT IdMesaGrupo, Nombre, NumPersonas FROM MesaGrupo WHERE IdMesaGrupo=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    MesaGrupo mg = new MesaGrupo();
                    mg.setIdMesaGrupo(rs.getInt("IdMesaGrupo"));
                    mg.setNombre(rs.getString("Nombre"));
                    mg.setNumPersonas(rs.getInt("NumPersonas"));
                    return mg;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean agregarMesaAGrupo(int idMesa, int idGrupo) throws RemoteException {
        String sql1 = "UPDATE Mesa SET IdMesaGrupo=? WHERE IdMesa=?";
        String sql2 = "SELECT COALESCE(SUM(Capacidad), 0) FROM Mesa WHERE IdMesaGrupo=?";
        String sql3 = "UPDATE MesaGrupo SET NumPersonas=? WHERE IdMesaGrupo=?";
        try (Connection con = Conexion.getConexion()) {
            boolean ok;
            try (PreparedStatement ps = con.prepareStatement(sql1)) {
                ps.setInt(1, idGrupo);
                ps.setInt(2, idMesa);
                ok = ps.executeUpdate() > 0;
            }
            if (ok) {
                int total = 0;
                try (PreparedStatement ps2 = con.prepareStatement(sql2)) {
                    ps2.setInt(1, idGrupo);
                    try (ResultSet rs2 = ps2.executeQuery()) {
                        if (rs2.next()) total = rs2.getInt(1);
                    }
                }
                try (PreparedStatement ps3 = con.prepareStatement(sql3)) {
                    ps3.setInt(1, total);
                    ps3.setInt(2, idGrupo);
                    ps3.executeUpdate();
                }
            }
            return ok;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean sacarMesaDeGrupo(int idMesa) throws RemoteException {
        String sql1 = "SELECT IdMesaGrupo FROM Mesa WHERE IdMesa=?";
        String sql2 = "UPDATE Mesa SET IdMesaGrupo = NULL WHERE IdMesa=?";
        String sql3 = "SELECT COALESCE(SUM(Capacidad), 0) FROM Mesa WHERE IdMesaGrupo=?";
        String sql4 = "UPDATE MesaGrupo SET NumPersonas=? WHERE IdMesaGrupo=?";
        try (Connection con = Conexion.getConexion()) {
            int idGrupo = -1;
            try (PreparedStatement ps = con.prepareStatement(sql1)) {
                ps.setInt(1, idMesa);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) idGrupo = rs.getInt("IdMesaGrupo");
                }
            }
            boolean ok;
            try (PreparedStatement ps = con.prepareStatement(sql2)) {
                ps.setInt(1, idMesa);
                ok = ps.executeUpdate() > 0;
            }
            if (ok && idGrupo > 0) {
                int total = 0;
                try (PreparedStatement ps2 = con.prepareStatement(sql3)) {
                    ps2.setInt(1, idGrupo);
                    try (ResultSet rs2 = ps2.executeQuery()) {
                        if (rs2.next()) total = rs2.getInt(1);
                    }
                }
                try (PreparedStatement ps3 = con.prepareStatement(sql4)) {
                    ps3.setInt(1, total);
                    ps3.setInt(2, idGrupo);
                    ps3.executeUpdate();
                }
            }
            return ok;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Integer> obtenerMesasDelGrupo(int idGrupo) throws RemoteException {
        List<Integer> lista = new ArrayList<>();
        String sql = "SELECT IdMesa FROM Mesa WHERE IdMesaGrupo=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idGrupo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(rs.getInt("IdMesa"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean tieneGrupo(int idMesa) throws RemoteException {
        String sql = "SELECT IdMesaGrupo FROM Mesa WHERE IdMesa=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMesa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("IdMesaGrupo") > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}