
package conexionbd;

import java.sql.Timestamp;
import java.sql.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SqlLlamadas {
        
    public boolean insertarReservacion(Reservacion reservacion) {
        String sql = "INSERT INTO Reservaciones (ClienteID, EmpleadoID, FechaReservacion, " +
                     "FechaEntrada, FechaSalida, Estado) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, reservacion.getClienteID());
            pstmt.setInt(2, reservacion.getEmpleadoID());
            pstmt.setTimestamp(3, Timestamp.valueOf(reservacion.getFechaReservacion()));
            pstmt.setDate(4, Date.valueOf(reservacion.getFechaEntrada()));
            pstmt.setDate(5, Date.valueOf(reservacion.getFechaSalida()));
            pstmt.setString(6, reservacion.getEstado());
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            // Puede lanzar error si viola el check constraint de fechas o estado
            System.err.println("Error al insertar reservación: " + e.getMessage());
            return false;
        }//catch
    }//insertarReservación
    
    public List<Reservacion> obtenerTodasReservaciones() {
        List<Reservacion> reservaciones = new ArrayList<>();
        String sql = "SELECT * FROM Reservaciones ORDER BY FechaReservacion DESC";
        
        try (Connection conn = Conexion.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Reservacion r = new Reservacion();
                r.setReservacionID(rs.getInt("ReservacionID"));
                r.setClienteID(rs.getInt("ClienteID"));
                r.setEmpleadoID(rs.getInt("EmpleadoID"));
                r.setFechaReservacion(rs.getTimestamp("FechaReservacion").toLocalDateTime());
                r.setFechaEntrada(rs.getDate("FechaEntrada").toLocalDate());
                r.setFechaSalida(rs.getDate("FechaSalida").toLocalDate());
                r.setEstado(rs.getString("Estado"));
                
                reservaciones.add(r);
            }//while
            
        } catch (SQLException e) {
            System.err.println("Error al obtener reservaciones: " + e.getMessage());
        }//catch
        
        return reservaciones;
    }//obtenerTodasReservaciones
    
    public boolean actualizarReservacionSP(int reservacionID, LocalDate fechaEntrada, 
                                           LocalDate fechaSalida, String estado) {
        // Primero creamos el procedimiento almacenado si no existe
        
        String callSP = "{CALL sp_ActualizarReservacion(?, ?, ?, ?)}";
        
        try (Connection conn = Conexion.getConnection();
            CallableStatement cstmt = conn.prepareCall(callSP)) {
            
            cstmt.setInt(1, reservacionID);
            cstmt.setDate(2, Date.valueOf(fechaEntrada));
            cstmt.setDate(3, Date.valueOf(fechaSalida));
            cstmt.setString(4, estado);
            
            int filasAfectadas = cstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            // El procedimiento validará automáticamente los constraints
            System.err.println("Error al actualizar reservación: " + e.getMessage());
            return false;
        }//
    }//actualizarReservacion
    
    
    
    public boolean eliminarReservacion(int reservacionID) {
        String sql = "DELETE FROM Reservaciones WHERE ReservacionID = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, reservacionID);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar reservación: " + e.getMessage());
            return false;
        }
    }//eliminarReservacion
}//SqlLlamadas
