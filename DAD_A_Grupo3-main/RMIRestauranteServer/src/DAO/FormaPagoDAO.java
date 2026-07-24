package DAO;
import DTO.FormaPago;
import Interface.IFormaPago;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FormaPagoDAO extends UnicastRemoteObject implements IFormaPago {

    public FormaPagoDAO() throws RemoteException {
    }

    @Override
    public List<FormaPago> listar() throws RemoteException {
        List<FormaPago> lista = new ArrayList<>();
        String sql = "SELECT IdFormaPago, Nombre FROM FormaPago ORDER BY IdFormaPago";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new FormaPago(rs.getInt("IdFormaPago"), rs.getString("Nombre")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}