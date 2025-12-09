
package interfaces;

import com.toedter.calendar.JDateChooser;
import conexionbd.Reservacion;
import conexionbd.SqlLlamadas;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class Principal extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Principal.class.getName());
    private DefaultTableModel m;
    private SqlLlamadas querys;
    private Thread timeStart;
    private int idReservacion = -1;
    private List<String> clientes, empleados;
    private boolean modoActualizar = false;
    private boolean actualizarTiempo = true;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Principal() {
        initComponents();
        setLocationRelativeTo(null);
        querys = new SqlLlamadas();
        idReservacion = querys.getUltimoIDInsertado("Reservaciones") + 1;
        m = (DefaultTableModel) tblReservaciones.getModel();
        clientes = new ArrayList<>();
        empleados = new ArrayList<>();
        iniciarComponentes();

    }

    private void iniciarComponentes() {
        quitarAdvertencias();
        llenarCmb();
        iniciarHiloTiempo();
        llenarTabla();
        limpiarCampos();
    }//handleTime
    
    private void quitarAdvertencias(){
        lblWCliente.setVisible(false);
        lblWEmpleado.setVisible(false);
        lblWFechaR.setVisible(false);
        lblWFechaE.setVisible(false);
        lblWFechaS.setVisible(false);
        lblWEstado.setVisible(false);
        lblWFechasInvalidas.setVisible(false);
    }//quitarAdvertencias
    
    private void iniciarHiloTiempo(){
        // Solo inicia un nuevo hilo si el hilo anterior no está vivo (o es null)
        if (timeStart == null || !timeStart.isAlive()) {
            timeStart = new Thread(new ActualizarTiempo());
            actualizarTiempo = true; // Aseguramos que la bandera esté en true
            timeStart.start();
        }
    }//iniciarHiloTiempo

    private void detenerHiloTiempo(){
        actualizarTiempo = false; // Detiene el bucle en el hilo
        
        if(timeStart != null && timeStart.isAlive()){
            timeStart.interrupt(); // Fuerza la salida del Thread.sleep()
        }//if
    }//detenerHiloTiempo
    
    private void llenarCmb() {
        cmbCliente.removeAllItems();
        cmbEmpleado.removeAllItems();
        cmbCliente.addItem("Selecciona");
        cmbEmpleado.addItem("Selecciona");
        for (String c : querys.obtenerTodosClientes()) {
            cmbCliente.addItem(c);
        }
        for (String e : querys.obtenerTodosEmpleados()) {
            cmbEmpleado.addItem(e);
        }
    }

    private class ActualizarTiempo implements Runnable {

        @Override
        public void run() {
            while (actualizarTiempo && !Thread.currentThread().isInterrupted()) {
                try {
                    txtFechaR.setDate(new java.util.Date());
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    //System.getLogger(Principal.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                    break;
                }
            }//while
        }
    }

    private void llenarTabla() {
        // Limpiar tabla completamente (tanto datos como encabezados)
        m.setRowCount(0);
        m.setColumnCount(0);

        // 1. Configurar los encabezados de la tabla según la estructura SQL Server
        m.addColumn("ID Reservación");
        m.addColumn("Cliente");
        m.addColumn("Empleado");
        m.addColumn("Fecha Reservación");
        m.addColumn("Fecha Entrada");
        m.addColumn("Fecha Salida");
        m.addColumn("Estado");

        try {
            List<Reservacion> reservaciones = querys.obtenerTodasReservaciones();

            for (Reservacion reservacion : reservaciones) {
                Object[] fila = new Object[7];

                fila[0] = reservacion.getReservacionID();

                // ERROR: reservacion.getNombreCliente() no existe
                // Lo cambiamos a buscar el nombre usando el ID
                fila[1] = reservacion.getNombreCliente();

                // ERROR: reservacion.getNombreEmpleado() no existe
                // Lo cambiamos a buscar el nombre usando el ID
                fila[2] = reservacion.getNombreEmpleado();

                // Formatear las fechas para mejor visualización
                if (reservacion.getFechaReservacion() != null) {
                    fila[3] = reservacion.getFechaReservacion().format(formatter); 
                } else {
                    fila[3] = "";
                }

                fila[4] = reservacion.getFechaEntrada() != null
                        ? reservacion.getFechaEntrada().toString()
                        : "";

                fila[5] = reservacion.getFechaSalida() != null
                        ? reservacion.getFechaSalida().toString()
                        : "";

                fila[6] = reservacion.getEstado();

                m.addRow(fila);
            }

            if (reservaciones.isEmpty()) {
                System.out.println("No hay reservaciones en la base de datos");
            }

        } catch (Exception e) {
            System.err.println("Error al llenar la tabla: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void llenarTablaReservaciones(
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

        m.setRowCount(0); m.setColumnCount(0);
        
        m.addColumn("ID Reservación");
        m.addColumn("Cliente");
        m.addColumn("Empleado");
        m.addColumn("Fecha Reservación");
        m.addColumn("Fecha Entrada");
        m.addColumn("Fecha Salida");
        m.addColumn("Estado");

        ResultSet rs = querys.buscarReservacionesConFiltros(
                reservacionID,
                clienteID,
                empleadoID,
                frDesde,
                frHasta,
                feDesde,
                feHasta,
                fsDesde,
                fsHasta,
                estado
        );

        try {
            while (rs != null && rs.next()) {
                Object[] fila = new Object[7];

                fila[0] = rs.getInt("ReservacionID");
                fila[1] = rs.getString("NombreCliente");
                fila[2] = rs.getString("NombreEmpleado");
                fila[3] = rs.getDate("FechaReservacion"); // SQL DATE
                fila[4] = rs.getDate("FechaEntrada");
                fila[5] = rs.getDate("FechaSalida");
                fila[6] = rs.getString("Estado");

                m.addRow(fila);
            }
        } catch (Exception e) {
            System.out.println("Error tabla: " + e.getMessage());
        }
    }

    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlFondo = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblReservaciones = new javax.swing.JTable();
        lblReservacion = new javax.swing.JLabel();
        txtReservacion = new javax.swing.JTextField();
        lblCliente = new javax.swing.JLabel();
        lblEmpleado = new javax.swing.JLabel();
        lblFechaRegistro = new javax.swing.JLabel();
        lblEntrada = new javax.swing.JLabel();
        lblFechaSalida = new javax.swing.JLabel();
        lblEstado = new javax.swing.JLabel();
        btnInsertar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnConsultar = new javax.swing.JButton();
        btnLimpiarCampos = new javax.swing.JButton();
        cmbCliente = new javax.swing.JComboBox<>();
        cmbEmpleado = new javax.swing.JComboBox<>();
        txtFechaE = new com.toedter.calendar.JDateChooser();
        txtFechaS = new com.toedter.calendar.JDateChooser();
        txtFechaR = new com.toedter.calendar.JDateChooser();
        cmbEstado = new javax.swing.JComboBox<>();
        lblWCliente = new javax.swing.JLabel();
        lblWEmpleado = new javax.swing.JLabel();
        lblWFechaE = new javax.swing.JLabel();
        lblWFechaR = new javax.swing.JLabel();
        lblWFechaS = new javax.swing.JLabel();
        lblWEstado = new javax.swing.JLabel();
        lblWFechasInvalidas = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlFondo.setBackground(new java.awt.Color(193, 193, 27));

        tblReservaciones.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblReservaciones.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblReservacionesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblReservaciones);

        lblReservacion.setText("ID reservación");

        txtReservacion.setEnabled(false);

        lblCliente.setText("Cliente");

        lblEmpleado.setText("Empleado");

        lblFechaRegistro.setText("Fecha Registro");

        lblEntrada.setText("Fecha Entrada");

        lblFechaSalida.setText("Fecha Salida");

        lblEstado.setText("Estado");

        btnInsertar.setText("Insertar");
        btnInsertar.addActionListener(this::btnInsertarActionPerformed);

        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(this::btnEliminarActionPerformed);

        btnConsultar.setText("Filtrar");
        btnConsultar.addActionListener(this::btnConsultarActionPerformed);

        btnLimpiarCampos.setText("Limpiar");
        btnLimpiarCampos.addActionListener(this::btnLimpiarCamposActionPerformed);

        cmbCliente.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Selecciona" }));
        cmbCliente.addItemListener(this::cmbClienteItemStateChanged);

        cmbEmpleado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Selecciona" }));

        txtFechaR.setEnabled(false);

        cmbEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Selecciona", "Confirmada", "Pendiente", "Cancelada", "Finalizada" }));

        lblWCliente.setForeground(new java.awt.Color(255, 51, 51));
        lblWCliente.setText("Complete este campo");

        lblWEmpleado.setForeground(new java.awt.Color(255, 51, 51));
        lblWEmpleado.setText("Complete este campo");

        lblWFechaE.setForeground(new java.awt.Color(255, 51, 51));
        lblWFechaE.setText("Inserte una fecha válida");

        lblWFechaR.setForeground(new java.awt.Color(255, 51, 51));
        lblWFechaR.setText("Inserte una fecha válida");

        lblWFechaS.setForeground(new java.awt.Color(255, 51, 51));
        lblWFechaS.setText("Inserte una fecha válida");

        lblWEstado.setForeground(new java.awt.Color(255, 51, 51));
        lblWEstado.setText("Complete este campo");

        lblWFechasInvalidas.setForeground(new java.awt.Color(255, 51, 51));
        lblWFechasInvalidas.setText("Verifique las fechas de entrada y salida");

        javax.swing.GroupLayout pnlFondoLayout = new javax.swing.GroupLayout(pnlFondo);
        pnlFondo.setLayout(pnlFondoLayout);
        pnlFondoLayout.setHorizontalGroup(
            pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFondoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFondoLayout.createSequentialGroup()
                        .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlFondoLayout.createSequentialGroup()
                                .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pnlFondoLayout.createSequentialGroup()
                                        .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblReservacion)
                                            .addComponent(lblCliente)
                                            .addComponent(lblEmpleado)
                                            .addComponent(lblFechaRegistro))
                                        .addGap(52, 52, 52)
                                        .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txtReservacion, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                                            .addComponent(cmbCliente, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(cmbEmpleado, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(txtFechaR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .addGroup(pnlFondoLayout.createSequentialGroup()
                                        .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblFechaSalida)
                                            .addComponent(lblEntrada))
                                        .addGap(56, 56, 56)
                                        .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txtFechaS, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                                            .addComponent(txtFechaE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(cmbEstado, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .addComponent(lblEstado))
                                .addGap(27, 27, 27)
                                .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblWFechaE)
                                    .addComponent(lblWFechaR)
                                    .addComponent(lblWFechaS)
                                    .addComponent(lblWEstado)
                                    .addComponent(lblWCliente)
                                    .addComponent(lblWEmpleado)
                                    .addComponent(lblWFechasInvalidas)))
                            .addGroup(pnlFondoLayout.createSequentialGroup()
                                .addComponent(btnConsultar, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnInsertar, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnLimpiarCampos, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 742, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlFondoLayout.setVerticalGroup(
            pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFondoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblReservacion)
                        .addComponent(txtReservacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblWFechasInvalidas, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlFondoLayout.createSequentialGroup()
                        .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnlFondoLayout.createSequentialGroup()
                                .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblCliente)
                                    .addComponent(cmbCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblWCliente))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblEmpleado)
                                    .addComponent(cmbEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblWEmpleado))
                                .addGap(12, 12, 12)
                                .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblFechaRegistro)
                                    .addComponent(txtFechaR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(lblWFechaR))
                        .addGap(12, 12, 12)
                        .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblEntrada)
                            .addComponent(txtFechaE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblWFechaE, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlFondoLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(lblFechaSalida))
                            .addGroup(pnlFondoLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtFechaS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(lblWFechaS))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEstado)
                    .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmbEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblWEstado)))
                .addGap(18, 18, 18)
                .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConsultar)
                    .addComponent(btnInsertar)
                    .addComponent(btnEliminar)
                    .addComponent(btnLimpiarCampos))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlFondo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlFondo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblReservacionesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblReservacionesMouseClicked
        int row = tblReservaciones.getSelectedRow();
    
        txtReservacion.setText(m.getValueAt(row, 0).toString());
        cmbCliente.setSelectedItem(m.getValueAt(row, 1).toString());
        cmbEmpleado.setSelectedItem(m.getValueAt(row, 2).toString());

        try {
            // Fecha Registro
            String fechaRegistroStr = m.getValueAt(row, 3).toString();
            if (!fechaRegistroStr.isEmpty()) {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime ldt = LocalDateTime.parse(fechaRegistroStr, fmt);
                java.util.Date date = java.util.Date.from(
                    ldt.atZone(ZoneId.systemDefault()).toInstant()
                );
                txtFechaR.setDate(date);
                detenerHiloTiempo(); // Detener cuando se ve un registro existente
            }

            // Fecha Entrada
            String fechaEntradaStr = m.getValueAt(row, 4).toString();
            if (!fechaEntradaStr.isEmpty()) {
                java.time.LocalDate ld = java.time.LocalDate.parse(fechaEntradaStr);
                java.util.Date date = java.sql.Date.valueOf(ld);
                txtFechaE.setDate(date);
            }

            // Fecha Salida
            String fechaSalidaStr = m.getValueAt(row, 5).toString();
            if (!fechaSalidaStr.isEmpty()) {
                java.time.LocalDate ld = java.time.LocalDate.parse(fechaSalidaStr);
                java.util.Date date = java.sql.Date.valueOf(ld);
                txtFechaS.setDate(date);
            }

        } catch (Exception e) {
            System.err.println("Error al convertir fechas: " + e.getMessage());
        }

        cmbEstado.setSelectedItem(m.getValueAt(row, 6).toString());

        // Cambiar a modo actualización
        cambiarModo(true);
    }//GEN-LAST:event_tblReservacionesMouseClicked

    private void limpiarCampos() {
        idReservacion = querys.getUltimoIDInsertado("Reservaciones") + 1;
        txtReservacion.setText("" + idReservacion);
        cmbCliente.setSelectedIndex(0);
        cmbEmpleado.setSelectedIndex(0);
        tblReservaciones.clearSelection();

        txtFechaR.setDate(new java.util.Date());
        txtFechaE.setDate(null);
        txtFechaS.setDate(null);

        cmbEstado.setSelectedIndex(0);
        cambiarModo(false);
    }
    
    private void cambiarModo(boolean esActualizacion) {
        modoActualizar = esActualizacion;
        if (esActualizacion) {
            btnInsertar.setText("Actualizar");
            txtReservacion.setEnabled(false); // No se puede cambiar el ID en actualización
            txtFechaR.setEnabled(false); // No se actualiza la fecha de registro
            detenerHiloTiempo(); // Detener el hilo cuando se está actualizando
        } else {
            btnInsertar.setText("Insertar");
            txtReservacion.setEnabled(false); // Solo lectura para mostrar ID
            txtFechaR.setEnabled(false); // Solo lectura
            if (actualizarTiempo) {
                iniciarHiloTiempo(); // Reanudar hilo si se detuvo
            }
        }
    }

    private void btnInsertarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertarActionPerformed
        if(modoActualizar){
            actualizar();
        } else{
            insertar();
        }
    }//GEN-LAST:event_btnInsertarActionPerformed

    private void actualizar(){
        if(!validarCampos()) return;
        querys.actualizarReservacion(
                Integer.parseInt(txtReservacion.getText()), 
                SqlLlamadas.buscarIdCliente(cmbCliente.getSelectedItem().toString()), 
                SqlLlamadas.buscarIdEmpleado(cmbEmpleado.getSelectedItem().toString()), 
                convertirFecha(txtFechaE), 
                convertirFecha(txtFechaS),
                cmbEstado.getSelectedItem().toString());
        limpiarCampos();
        llenarTabla();
        llenarCmb();

    }//actualizar
    
    private LocalDate convertirFecha(JDateChooser date){
        if(date.getDate() == null) return null;
        return date.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    
    

    
    private void insertar(){
        if(!validarCampos()) return;
        Reservacion reservacion = new Reservacion();
        int idCliente = SqlLlamadas.buscarIdCliente(cmbCliente.getSelectedItem().toString());
        int idEmpleado = SqlLlamadas.buscarIdEmpleado(cmbEmpleado.getSelectedItem().toString());

        try {
            reservacion.setClienteID(idCliente);
            reservacion.setEmpleadoID(idEmpleado);
            reservacion.setFechaReservacion(LocalDateTime.now());


            reservacion.setFechaEntrada(convertirFecha(txtFechaE));
            reservacion.setFechaSalida(convertirFecha(txtFechaS));

            reservacion.setEstado(cmbEstado.getSelectedItem().toString());
            querys.insertarReservacion(reservacion);
            limpiarCampos();
            llenarTabla();
            llenarCmb();

            // Actualizar el ID para mostrar
            idReservacion = querys.getUltimoIDInsertado("Reservaciones") + 1;
            txtReservacion.setText("" + idReservacion);

        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private boolean validarCampos(){
        boolean todosValidos = true;

        quitarAdvertencias();

        if(cmbCliente.getSelectedIndex() == 0) {
            lblWCliente.setVisible(true);
            todosValidos = false;
        }

        if(cmbEmpleado.getSelectedIndex() == 0){
            lblWEmpleado.setVisible(true);
            todosValidos = false;
        }

        if(txtFechaR.getDate() == null){
            lblWFechaR.setVisible(true);
            todosValidos = false;
        }

        if(txtFechaE.getDate() == null){
            lblWFechaE.setVisible(true);
            todosValidos = false;
        }

        if(txtFechaS.getDate() == null ){
            lblWFechaS.setVisible(true);
            todosValidos = false;
        }

        if(txtFechaE.getDate() != null && txtFechaS.getDate() != null) {
            
            if(!txtFechaS.getDate().after(txtFechaE.getDate()) || 
                    (convertirFecha(txtFechaS).equals(convertirFecha(txtFechaE)))){
                lblWFechasInvalidas.setVisible(true);
                todosValidos = false;
            }
        }

        if(cmbEstado.getSelectedIndex() == 0){
            lblWEstado.setVisible(true);
            todosValidos = false;
        }

        return todosValidos;
    }//validarCampos()

    
    
    
    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        querys.eliminarReservacion(Integer.parseInt(txtReservacion.getText()));
        llenarTabla();
        limpiarCampos();
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultarActionPerformed
        
        LocalDate frDesde = convertirFecha(txtFechaR);
        LocalDate frHasta = convertirFecha(txtFechaR);

        LocalDate feDesde = convertirFecha(txtFechaE);
        LocalDate feHasta = convertirFecha(txtFechaE);

        LocalDate fsDesde = convertirFecha(txtFechaS);
        LocalDate fsHasta = convertirFecha(txtFechaS);

        Integer idReservacion = txtReservacion.getText().isEmpty()
                ? null : Integer.valueOf(txtReservacion.getText());

        Integer idCliente = cmbCliente.getSelectedIndex() <= 0
                ? null : SqlLlamadas.buscarIdCliente(cmbCliente.getSelectedItem().toString());

        Integer idEmpleado = cmbEmpleado.getSelectedIndex() <= 0
                ? null : SqlLlamadas.buscarIdEmpleado(cmbEmpleado.getSelectedItem().toString());

        String estado = (cmbEstado.getSelectedIndex() <= 0)
                ? null : cmbEstado.getSelectedItem().toString();

        llenarTablaReservaciones(
                idReservacion,
                idCliente,
                idEmpleado,
                frDesde,
                frHasta,
                feDesde,
                feHasta,
                fsDesde,
                fsHasta,
                estado
        );
    }//GEN-LAST:event_btnConsultarActionPerformed

    
    private void btnLimpiarCamposActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarCamposActionPerformed
        limpiarCampos();
    }//GEN-LAST:event_btnLimpiarCamposActionPerformed

    private void cmbClienteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbClienteItemStateChanged
    }//GEN-LAST:event_cmbClienteItemStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Principal().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConsultar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnInsertar;
    private javax.swing.JButton btnLimpiarCampos;
    private javax.swing.JComboBox<String> cmbCliente;
    private javax.swing.JComboBox<String> cmbEmpleado;
    private javax.swing.JComboBox<String> cmbEstado;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCliente;
    private javax.swing.JLabel lblEmpleado;
    private javax.swing.JLabel lblEntrada;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblFechaRegistro;
    private javax.swing.JLabel lblFechaSalida;
    private javax.swing.JLabel lblReservacion;
    private javax.swing.JLabel lblWCliente;
    private javax.swing.JLabel lblWEmpleado;
    private javax.swing.JLabel lblWEstado;
    private javax.swing.JLabel lblWFechaE;
    private javax.swing.JLabel lblWFechaR;
    private javax.swing.JLabel lblWFechaS;
    private javax.swing.JLabel lblWFechasInvalidas;
    private javax.swing.JPanel pnlFondo;
    private javax.swing.JTable tblReservaciones;
    private com.toedter.calendar.JDateChooser txtFechaE;
    private com.toedter.calendar.JDateChooser txtFechaR;
    private com.toedter.calendar.JDateChooser txtFechaS;
    private javax.swing.JTextField txtReservacion;
    // End of variables declaration//GEN-END:variables
}
