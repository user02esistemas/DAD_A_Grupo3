package DAO;
import DTO.Administrador;
import Interface.IAdministrador;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;

public class AdministradorDAO extends UnicastRemoteObject implements IAdministrador {

    public AdministradorDAO() throws RemoteException {
    }

    @Override
    public Administrador login(String usuario, String clave) throws RemoteException {
        String sql = "SELECT * FROM Administrador WHERE Usuario = ? AND Clave = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, clave);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Administrador(
                        rs.getInt("IdAdministrador"),
                        rs.getString("Nombre"),
                        rs.getString("Usuario"),
                        rs.getString("Clave")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}