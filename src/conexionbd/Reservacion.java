package conexionbd;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reservacion {
        private int reservacionID;
        private int clienteID;
        private String nombreCliente;
        private int empleadoID;
        private String nombreEmpleado;
        private LocalDateTime fechaReservacion;
        private LocalDate fechaEntrada;
        private LocalDate fechaSalida;
        private String estado;
        
        // Constructores
        public Reservacion() {}
        
        public Reservacion(int clienteID, int empleadoID, LocalDate fechaEntrada, 
                          LocalDate fechaSalida, String estado) {
            this.setClienteID(clienteID);
            this.setEmpleadoID(empleadoID);
            this.fechaReservacion = LocalDateTime.now();
            this.fechaEntrada = fechaEntrada;
            this.fechaSalida = fechaSalida;
            this.estado = estado;
        }
        
        // Getters y Setters
        public int getReservacionID() { return reservacionID; }
        public void setReservacionID(int reservacionID) { this.reservacionID = reservacionID; }
        
        public int getClienteID() { return clienteID; }
        public void setClienteID(int clienteID) { 
            this.clienteID = clienteID; 
            setNombreCliente(SqlLlamadas.buscarNombreCliente(clienteID));
        }
        
        public String getNombreCliente() {return nombreCliente; }
        public void setNombreCliente(String nombreCliente){ this.nombreCliente = nombreCliente; }
        
        public int getEmpleadoID() { return empleadoID; }
        public void setEmpleadoID(int empleadoID) { 
            this.empleadoID = empleadoID; 
            setNombreEmpleado(SqlLlamadas.buscarNombreEmpleado(empleadoID));
        }
        
        public String getNombreEmpleado() { return nombreEmpleado;}
        public void setNombreEmpleado(String nombreEmpleado) {this.nombreEmpleado = nombreEmpleado; }
        
        public LocalDateTime getFechaReservacion() { return fechaReservacion; }
        public void setFechaReservacion(LocalDateTime fechaReservacion) { this.fechaReservacion = fechaReservacion; }
        
        public LocalDate getFechaEntrada() { return fechaEntrada; }
        public void setFechaEntrada(LocalDate fechaEntrada) { this.fechaEntrada = fechaEntrada; }
        
        public LocalDate getFechaSalida() { return fechaSalida; }
        public void setFechaSalida(LocalDate fechaSalida) { this.fechaSalida = fechaSalida; }
        
        public String getEstado() { return estado; }
        public void setEstado(String estado) { 
            // Validación básica del estado
            if (!estado.equalsIgnoreCase("Finalizada") && !estado.equalsIgnoreCase("Confirmada") && 
                !estado.equalsIgnoreCase("Cancelada") && !estado.equalsIgnoreCase("Pendiente")) {
                System.out.println("El estado no es valido");
            }
            this.estado = estado; 
        }
        
        // Método de validación de fechas
        public boolean validarFechas() {
            return fechaSalida.isAfter(fechaEntrada);
        }
        
        @Override
        public String toString() {
            return String.format("ReservacionID: %d, Estado: %s, Entrada: %s, Salida: %s", 
                reservacionID, estado, fechaEntrada, fechaSalida);
        }
    }