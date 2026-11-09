/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import util.GeneradorComprobante;

//ventana para mostrar el comprobante en ventana
public class DlgComprobante extends JDialog {

    private final JTextArea area = new JTextArea();
    private final String texto;
    private final String nombreArchivo;

    public DlgComprobante(Window padre, String texto, String nombreArchivo) {
        super(padre, "Comprobante de turno", ModalityType.APPLICATION_MODAL);
        this.texto = texto;
        this.nombreArchivo = nombreArchivo;

        area.setText(texto);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setBorder(new EmptyBorder(10, 10, 10, 10));
        area.setCaretPosition(0);

        JButton btnGuardar = new JButton("Guardar como .txt");
        JButton btnImprimir = new JButton("Imprimir");
        JButton btnCerrar = new JButton("Cerrar");
        btnGuardar.addActionListener(e -> guardar());
        btnImprimir.addActionListener(e -> imprimir());
        btnCerrar.addActionListener(e -> dispose());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        botones.add(btnGuardar);
        botones.add(btnImprimir);
        botones.add(btnCerrar);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(area), BorderLayout.CENTER);
        getContentPane().add(botones, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(520, 520));
        pack();
        setLocationRelativeTo(padre);
    }

    private void guardar() {
        JFileChooser selector = new JFileChooser();
        selector.setSelectedFile(new File(nombreArchivo));
        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File destino = selector.getSelectedFile();
        if (!destino.getName().toLowerCase().endsWith(".txt")) {
            destino = new File(destino.getParentFile(), destino.getName() + ".txt");
        }
        if (destino.exists() && !Mensajes.confirmar(this, "El archivo ya existe. ¿Deseas reemplazarlo?")) {
            return;
        }
        try {
            GeneradorComprobante.guardar(texto, destino);
            Mensajes.info(this, "Comprobante guardado en:\n" + destino.getAbsolutePath());
        } catch (IOException ex) {
            Mensajes.error(this, "No se pudo guardar el archivo: " + ex.getMessage());
        }
    }

    private void imprimir() {
        try {
            area.print();
        } catch (PrinterException ex) {
            Mensajes.error(this, "No se pudo imprimir: " + ex.getMessage());
        }
    }
}
