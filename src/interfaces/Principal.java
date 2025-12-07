/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package interfaces;

import conexionbd.Reservacion;
import conexionbd.SqlLlamadas;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class Principal extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Principal.class.getName());
    private DefaultTableModel m;
    private SqlLlamadas querys;
    private Thread timeStart = new Thread(new ActualizarTiempo());
    private int idReservacion = -1;
    private List<String> clientes, empleados;
    private boolean updateMode = false;

    public Principal() {
        initComponents();
        querys = new SqlLlamadas();
        idReservacion = querys.getUltimoIDInsertado("Reservaciones") + 1;
        m = (DefaultTableModel) tblReservaciones.getModel();
        clientes = new ArrayList<>();
        empleados = new ArrayList<>();
        iniciarComponentes();

    }

    private void iniciarComponentes() {
        llenarCmb();
        timeStart.start();
        llenarTabla();
        limpiarCampos();
    }//handleTime

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
            while (true) {
                try {
                    // Formatear la fecha como "yyyy-MM-dd HH:mm"
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    txtFechaR.setText(LocalDateTime.now().format(formatter));
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    System.getLogger(Principal.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
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
                fila[1] = SqlLlamadas.buscarNombreCliente(reservacion.getClienteID());

                // ERROR: reservacion.getNombreEmpleado() no existe
                // Lo cambiamos a buscar el nombre usando el ID
                fila[2] = SqlLlamadas.buscarNombreEmpleado(reservacion.getEmpleadoID());

                // Formatear las fechas para mejor visualización
                if (reservacion.getFechaReservacion() != null) {
                    fila[3] = reservacion.getFechaReservacion().toString().substring(0, 16); // Sin segundos
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
        txtFechaR = new javax.swing.JTextField();
        lblEntrada = new javax.swing.JLabel();
        txtFechaE = new javax.swing.JTextField();
        txtFechaS = new javax.swing.JTextField();
        lblFechaSalida = new javax.swing.JLabel();
        txtEstado = new javax.swing.JTextField();
        lblEstado = new javax.swing.JLabel();
        btnInsertar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        btnConsultar = new javax.swing.JButton();
        btnLimpiarCampos = new javax.swing.JButton();
        cmbCliente = new javax.swing.JComboBox<>();
        cmbEmpleado = new javax.swing.JComboBox<>();

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

        txtFechaR.setEnabled(false);

        lblEntrada.setText("Fecha Entrada");

        lblFechaSalida.setText("Fecha Salida");

        lblEstado.setText("Estado");

        btnInsertar.setText("Insertar");
        btnInsertar.addActionListener(this::btnInsertarActionPerformed);

        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(this::btnEliminarActionPerformed);

        btnActualizar.setText("Actualizar");
        btnActualizar.addActionListener(this::btnActualizarActionPerformed);

        btnConsultar.setText("Filtrar");
        btnConsultar.addActionListener(this::btnConsultarActionPerformed);

        btnLimpiarCampos.setText("Limpiar");
        btnLimpiarCampos.addActionListener(this::btnLimpiarCamposActionPerformed);

        cmbCliente.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Selecciona" }));
        cmbCliente.addItemListener(this::cmbClienteItemStateChanged);

        cmbEmpleado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Selecciona" }));

        javax.swing.GroupLayout pnlFondoLayout = new javax.swing.GroupLayout(pnlFondo);
        pnlFondo.setLayout(pnlFondoLayout);
        pnlFondoLayout.setHorizontalGroup(
            pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFondoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(pnlFondoLayout.createSequentialGroup()
                        .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(pnlFondoLayout.createSequentialGroup()
                                    .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblFechaSalida)
                                        .addComponent(lblEntrada))
                                    .addGap(56, 56, 56)
                                    .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtFechaS, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtFechaE, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(pnlFondoLayout.createSequentialGroup()
                                    .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblReservacion)
                                        .addComponent(lblCliente)
                                        .addComponent(lblEmpleado)
                                        .addComponent(lblFechaRegistro))
                                    .addGap(52, 52, 52)
                                    .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txtFechaR, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                                        .addComponent(txtReservacion, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                                        .addComponent(cmbCliente, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(cmbEmpleado, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFondoLayout.createSequentialGroup()
                                    .addComponent(lblEstado)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(pnlFondoLayout.createSequentialGroup()
                                .addComponent(btnConsultar, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnInsertar, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnActualizar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnLimpiarCampos, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 113, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlFondoLayout.setVerticalGroup(
            pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFondoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblReservacion)
                    .addComponent(txtReservacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCliente)
                    .addComponent(cmbCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEmpleado)
                    .addComponent(cmbEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFechaRegistro)
                    .addComponent(txtFechaR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblEntrada)
                    .addComponent(txtFechaE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFechaS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFechaSalida))
                .addGap(18, 18, 18)
                .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEstado))
                .addGap(18, 18, 18)
                .addGroup(pnlFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConsultar)
                    .addComponent(btnInsertar)
                    .addComponent(btnEliminar)
                    .addComponent(btnActualizar)
                    .addComponent(btnLimpiarCampos))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(10, Short.MAX_VALUE))
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
        updateMode = true;
        txtReservacion.setText(m.getValueAt(row, 0).toString());
        cmbCliente.setSelectedItem(m.getValueAt(row, 1).toString());
        cmbEmpleado.setSelectedItem(m.getValueAt(row, 2).toString());
        txtFechaR.setText(m.getValueAt(row, 3).toString());
        txtFechaE.setText(m.getValueAt(row, 4).toString());
        txtFechaS.setText(m.getValueAt(row, 5).toString());
        txtEstado.setText(m.getValueAt(row, 6).toString());
    }//GEN-LAST:event_tblReservacionesMouseClicked

    private void limpiarCampos() {
        idReservacion = querys.getUltimoIDInsertado("Reservaciones") + 1;
        txtReservacion.setText(""+idReservacion);
        cmbCliente.setSelectedIndex(0);
        cmbEmpleado.setSelectedIndex(0);
        txtFechaR.setText("");
        txtFechaE.setText("");
        txtFechaS.setText("");
        txtEstado.setText("");
    }//limpiarCampos
    
    private void btnInsertarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertarActionPerformed
        Reservacion reservacion = new Reservacion();
        int idCliente = SqlLlamadas.buscarIdCliente(cmbCliente.getSelectedItem().toString());
        int idEmpleado = SqlLlamadas.buscarIdEmpleado(cmbEmpleado.getSelectedItem().toString());

        // ERROR: Validar que no sean "Selecciona"
        if (cmbCliente.getSelectedIndex() == 0 || cmbEmpleado.getSelectedIndex() == 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "Selecciona un cliente y un empleado válido");
            return;
        }

        // ERROR: Validar que las fechas no estén vacías
        if (txtFechaE.getText().trim().isEmpty() || txtFechaS.getText().trim().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Las fechas de entrada y salida son obligatorias");
            return;
        }

        try {
            reservacion.setClienteID(idCliente);
            reservacion.setEmpleadoID(idEmpleado);
            reservacion.setFechaReservacion(LocalDateTime.now());
            reservacion.setFechaEntrada(LocalDate.parse(txtFechaE.getText()));
            reservacion.setFechaSalida(LocalDate.parse(txtFechaS.getText()));
            reservacion.setEstado(txtEstado.getText());
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
    }//GEN-LAST:event_btnInsertarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        querys.eliminarReservacion(Integer.parseInt(txtReservacion.getText()));
        llenarTabla();
        limpiarCampos();
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void btnConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultarActionPerformed
        // TODO add your handling code here:
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
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnConsultar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnInsertar;
    private javax.swing.JButton btnLimpiarCampos;
    private javax.swing.JComboBox<String> cmbCliente;
    private javax.swing.JComboBox<String> cmbEmpleado;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCliente;
    private javax.swing.JLabel lblEmpleado;
    private javax.swing.JLabel lblEntrada;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblFechaRegistro;
    private javax.swing.JLabel lblFechaSalida;
    private javax.swing.JLabel lblReservacion;
    private javax.swing.JPanel pnlFondo;
    private javax.swing.JTable tblReservaciones;
    private javax.swing.JTextField txtEstado;
    private javax.swing.JTextField txtFechaE;
    private javax.swing.JTextField txtFechaR;
    private javax.swing.JTextField txtFechaS;
    private javax.swing.JTextField txtReservacion;
    // End of variables declaration//GEN-END:variables
}
