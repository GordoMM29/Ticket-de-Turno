package controlador;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.WindowConstants;
import util.Sesion;
import util.Sistema;
import vista.FrmCatalogos;
import vista.FrmConsulta;
import vista.FrmDashboard;
import vista.FrmInicio;
import vista.FrmMenu;
import vista.Mensajes;

/**
 * Menu principal del administrador: navega entre consulta, catalogos y dashboard.
 */
public class MenuController {

    private final FrmMenu vista;
    private final FrmInicio inicio;

    public MenuController(FrmMenu vista, FrmInicio inicio) {
        this.vista = vista;
        this.inicio = inicio;
    }

    public void iniciar() {
        if (!Sesion.haySesion()) {
            vista.dispose();
            inicio.setVisible(true);
            return;
        }
        vista.setUsuario(Sesion.getAdministrador().getNombre());

        vista.getBtnConsulta().addActionListener(e -> abrirConsulta());
        vista.getBtnCatalogos().addActionListener(e -> abrirCatalogos());
        vista.getBtnDashboard().addActionListener(e -> abrirDashboard());
        vista.getBtnCerrarSesion().addActionListener(e -> cerrarSesion());
        vista.getBtnSalir().addActionListener(e -> salir());

        vista.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        vista.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarSesion();
            }
        });

        vista.setLocationRelativeTo(null);
        vista.setVisible(true);
    }

    private void abrirConsulta() {
        FrmConsulta ventana = new FrmConsulta();
        vista.setVisible(false);
        new ConsultaController(ventana, () -> vista.setVisible(true)).iniciar();
    }

    private void abrirCatalogos() {
        FrmCatalogos ventana = new FrmCatalogos();
        vista.setVisible(false);
        new CatalogoController(ventana, () -> vista.setVisible(true)).iniciar();
    }

    private void abrirDashboard() {
        FrmDashboard ventana = new FrmDashboard();
        vista.setVisible(false);
        new DashboardController(ventana, () -> vista.setVisible(true)).iniciar();
    }

    /** Termina la sesion del administrador y regresa a la pantalla publica. */
    private void cerrarSesion() {
        if (Mensajes.confirmar(vista, "¿Deseas cerrar tu sesion?")) {
            Sesion.cerrar();
            vista.dispose();
            inicio.setVisible(true);
        }
    }

    /** Cerrar sistema: destruye la sesion, cierra la base de datos y termina. */
    private void salir() {
        if (Mensajes.confirmar(vista, "¿Deseas cerrar el sistema?")) {
            Sistema.salir();
        }
    }
}
