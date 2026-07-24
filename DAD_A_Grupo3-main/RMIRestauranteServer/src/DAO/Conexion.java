package DAO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static final String URL = "jdbc:postgresql://localhost:5432/GestionRestauranteDB";
    private static final String DRIVER = "org.postgresql.Driver";
    private static final String USR = "postgres";
    private static final String PWD = "dba";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver PostgreSQL no encontrado: " + e.getMessage());
            throw new RuntimeException("No se pudo cargar el driver PostgreSQL", e);
        }
    }

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USR, PWD);
    }
}