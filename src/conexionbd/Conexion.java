package conexionbd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion{
    private static String url = "jdbc:sqlserver://DESKTOP-QUA9QDT;databaseName=BDHotelRiu;encrypt=false"; 
    private static String user = "Axel";
    private static String password = "123";
    private static Connection con = null;
    
    // CREATE, READ, UPDATE, DELETE
    // CREAR, LEER, ACTUALIZAR, BORRAR
    
    public static Connection getConnection(){
        try {
            con = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            System.getLogger(Conexion.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return con;
    }//getConnection
    
}//class