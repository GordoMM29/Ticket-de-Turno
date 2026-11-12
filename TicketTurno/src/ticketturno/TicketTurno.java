/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ticketturno;


import controlador.InicioController;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import util.Sistema;
import vista.FrmInicio;

public class TicketTurno {

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
        }

        // Pase lo que pase al salir, se cierra la base de datos y la sesion&
        Runtime.getRuntime().addShutdownHook(new Thread(() -> Sistema.liberarRecursos()));

        SwingUtilities.invokeLater(() -> {
            FrmInicio inicio = new FrmInicio();
            new InicioController(inicio).iniciar();
        });
    }
}