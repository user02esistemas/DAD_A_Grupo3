package DAO;
import DTO.TipoDocumento;
import Interface.ITipoDocumento;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoDocumentoDAO extends UnicastRemoteObject implements ITipoDocumento {

    public TipoDocumentoDAO() throws RemoteException {
    }

    @Override
    public List<TipoDocumento> listar() throws RemoteException {
        List<TipoDocumento> lista = new ArrayList<>();
        String sql = "SELECT IdTipoDocumento, Nombre FROM TipoDocumento ORDER BY IdTipoDocumento";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new TipoDocumento(rs.getInt("IdTipoDocumento"), rs.getString("Nombre")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}