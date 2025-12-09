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
        String sql = "INSERT INTO Reservaciones (ClienteID, EmpleadoID, FechaReservacion, "
                + "FechaEntrada, FechaSalida, Estado) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

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

    public static String buscarNombreCliente(int idCliente) {
        String sql = "SELECT Nombre FROM Clientes WHERE ClienteID = ?";
        try (Connection conn = Conexion.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCliente);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()) return rs.getString(1);
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return null;
    }

    public static String buscarNombreEmpleado(int idEmpleado) {
        String sql = "SELECT Nombre FROM Empleados WHERE EmpleadoID = ?";
        try (Connection conn = Conexion.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idEmpleado);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()) return rs.getString(1);
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return null;
    }
    
    public static int buscarIdCliente(String nombreCliente) {
        String sql = "SELECT ClienteID FROM Clientes WHERE Nombre = '" + nombreCliente + "'";
        try (Connection conn = Conexion.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if(rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println(e.toString());
        }//catch
        return -1;
    }//buscarCliente

    public static int buscarIdEmpleado(String nombreEmpleado) {
        String sql = "SELECT EmpleadoID FROM Empleados WHERE Nombre = '" + nombreEmpleado + "'";
        try (Connection conn = Conexion.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if(rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println(e.toString());
        }//catch
        return -1;
    }//buscarCliente

    public List<Reservacion> obtenerTodasReservaciones() {
        List<Reservacion> reservaciones = new ArrayList<>();
        String sql = "SELECT * FROM Reservaciones ORDER BY FechaReservacion DESC";

        try (Connection conn = Conexion.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

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

        try (Connection conn = Conexion.getConnection(); CallableStatement cstmt = conn.prepareCall(callSP)) {

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
    
    private java.sql.Date toSQL(LocalDate fecha) {
        return (fecha == null) ? null : java.sql.Date.valueOf(fecha);
    }

    
    public ResultSet buscarReservacionesConFiltros(
        Integer reservacionID,
        Integer clienteID,
        Integer empleadoID,
        LocalDate frDesde,
        LocalDate frHasta,
        LocalDate feDesde,
        LocalDate feHasta,
        LocalDate fsDesde,
        LocalDate fsHasta,
        String estado
    ) {
        try {
            Connection cn = Conexion.getConnection();

            CallableStatement cs = cn.prepareCall("{CALL BuscarReservacionesConFiltros(?,?,?,?,?,?,?,?,?,?)}");

            // INT
            if (reservacionID == null) cs.setNull(1, java.sql.Types.INTEGER);
            else cs.setInt(1, reservacionID);

            if (clienteID == null) cs.setNull(2, java.sql.Types.INTEGER);
            else cs.setInt(2, clienteID);

            if (empleadoID == null) cs.setNull(3, java.sql.Types.INTEGER);
            else cs.setInt(3, empleadoID);

            // LocalDate → SQL Date
            cs.setObject(4, toSQL(frDesde));
            cs.setObject(5, toSQL(frHasta));
            cs.setObject(6, toSQL(feDesde));
            cs.setObject(7, toSQL(feHasta));
            cs.setObject(8, toSQL(fsDesde));
            cs.setObject(9, toSQL(fsHasta));

            // Estado
            if (estado == null || estado.equals("Todos"))
                cs.setNull(10, java.sql.Types.NVARCHAR);
            else
                cs.setString(10, estado);

            return cs.executeQuery();

        } catch (Exception e) {
            System.out.println("Error SP: " + e.getMessage());
            return null;
        }
    }

    
    
    public boolean actualizarReservacion(int reservacionID, int clienteID, int empleadoID,
        LocalDate fechaEntrada, LocalDate fechaSalida, String estado) {

        String sql = "UPDATE Reservaciones SET "
                + "ClienteID = ?, "
                + "EmpleadoID = ?, "
                + "FechaEntrada = ?, "
                + "FechaSalida = ?, "
                + "Estado = ? "
                + "WHERE ReservacionID = ?";  

        try (Connection conn = Conexion.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, clienteID);         
            pstmt.setInt(2, empleadoID);         
            pstmt.setDate(3, java.sql.Date.valueOf(fechaEntrada)); 
            pstmt.setDate(4, java.sql.Date.valueOf(fechaSalida));   
            pstmt.setString(5, estado);         
            pstmt.setInt(6, reservacionID);      

            int filasAfectadas = pstmt.executeUpdate();
            System.out.println(filasAfectadas);
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar reservación: " + e.getMessage());
            e.printStackTrace();  
            return false;
        }
    }//actualizarReservacion

    public boolean eliminarReservacion(int reservacionID) {
        String sql = "DELETE FROM Reservaciones WHERE ReservacionID = ?";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reservacionID);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar reservación: " + e.getMessage());
            return false;
        }
    }//eliminarReservacion
    
    public List<String> obtenerTodosClientes() {
        List<String> clientes = new ArrayList<>();
        String sql = "SELECT Nombre FROM Clientes ORDER BY Nombre";
        try (Connection conn = Conexion.getConnection(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clientes.add(rs.getString("Nombre"));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener clientes: " + e.toString());
        }
        return clientes;
    }

    public List<String> obtenerTodosEmpleados() {
        List<String> empleados = new ArrayList<>();
        String sql = "SELECT Nombre FROM Empleados ORDER BY Nombre";
        try (Connection conn = Conexion.getConnection(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                empleados.add(rs.getString("Nombre"));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener empleados: " + e.toString());
        }
        return empleados;
    }

    public int getUltimoIDInsertado(String tabla) {
        String sql = "SELECT IDENT_CURRENT(?) AS UltimoID";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tabla);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("UltimoID");
            }

        } catch (SQLException e) {
            System.err.println("Error: " + e.toString());
        }
        return -1;
    }
    
    public ResultSet buscarReservacionesConFiltros(
        Integer reservacionID,
        Integer clienteID,
        Integer empleadoID,
        String fechaReservacionDesde,
        String fechaReservacionHasta,
        String fechaEntradaDesde,
        String fechaEntradaHasta,
        String fechaSalidaDesde,
        String fechaSalidaHasta,
        String estado
    ) {
        try {
            Connection cn = Conexion.getConnection();
            CallableStatement cs = cn.prepareCall("{CALL BuscarReservacionesConFiltros(?,?,?,?,?,?,?,?,?,?)}");

            // INT
            if (reservacionID == null) cs.setNull(1, java.sql.Types.INTEGER);
            else cs.setInt(1, reservacionID);

            if (clienteID == null) cs.setNull(2, java.sql.Types.INTEGER);
            else cs.setInt(2, clienteID);

            if (empleadoID == null) cs.setNull(3, java.sql.Types.INTEGER);
            else cs.setInt(3, empleadoID);

            // DATETIME (SQL espera DATETIME, le enviamos String convertida)
            if (fechaReservacionDesde == null) cs.setNull(4, java.sql.Types.VARCHAR);
            else cs.setString(4, fechaReservacionDesde);

            if (fechaReservacionHasta == null) cs.setNull(5, java.sql.Types.VARCHAR);
            else cs.setString(5, fechaReservacionHasta);

            // DATE
            if (fechaEntradaDesde == null) cs.setNull(6, java.sql.Types.VARCHAR);
            else cs.setString(6, fechaEntradaDesde);

            if (fechaEntradaHasta == null) cs.setNull(7, java.sql.Types.VARCHAR);
            else cs.setString(7, fechaEntradaHasta);

            if (fechaSalidaDesde == null) cs.setNull(8, java.sql.Types.VARCHAR);
            else cs.setString(8, fechaSalidaDesde);

            if (fechaSalidaHasta == null) cs.setNull(9, java.sql.Types.VARCHAR);
            else cs.setString(9, fechaSalidaHasta);

            // Estado
            if (estado == null || estado.equals("Selecciona")) 
                cs.setNull(10, java.sql.Types.NVARCHAR);
            else 
                cs.setString(10, estado);

            return cs.executeQuery();

        } catch (Exception e) {
            System.out.println("Error en filtro: " + e.getMessage());
            return null;
        }
    }
}//SqlLlamadas
