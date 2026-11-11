package controlador;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import javax.swing.WindowConstants;
import modelo.Solicitud;
import modelo.SolicitudDAO;
import util.Conexion;
import util.Sistema;
import util.ValidadorCurp;
import vista.FrmInicio;
import vista.FrmLogin;
import vista.FrmSolicitud;
import vista.Mensajes;

/**
 * Pantalla publica de inicio: registrar solicitud, modificar solicitud
 * (con CURP y turno) o entrar como administrador.
 */
public class InicioController {

    private final FrmInicio vista;
    private final SolicitudDAO solicitudDAO = new SolicitudDAO();

    public InicioController(FrmInicio vista) {
        this.vista = vista;
    }

    public void iniciar() {
        vista.getBtnRegistrar().addActionListener(e -> abrirRegistro());
        vista.getBtnModificar().addActionListener(e -> abrirModificacion());
        vista.getBtnAdmin().addActionListener(e -> abrirLogin());
        vista.getBtnSalir().addActionListener(e -> salir());

        vista.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        vista.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salir();
            }
        });

        vista.setLocationRelativeTo(null);
        vista.setVisible(true);
        verificarBaseDeDatos();
    }

    private void verificarBaseDeDatos() {
        try {
            Conexion.getInstancia().getConexion();
        } catch (SQLException ex) {
            Mensajes.error(vista, "No se pudo conectar con la base de datos.\n\n" + ex.getMessage()
                    + "\n\nRevisa que MySQL este encendido, que exista la base ticket_turno\n"
                    + "y los datos del archivo conexion.properties.");
        }
    }

    private void abrirRegistro() {
        FrmSolicitud formulario = new FrmSolicitud();
        vista.setVisible(false);
        new SolicitudController(formulario, () -> vista.setVisible(true), null).iniciar();
    }

    private void abrirModificacion() {
        String curp = ValidadorCurp.normalizar(vista.getCurp());
        String errorCurp = ValidadorCurp.obtenerError(curp);
        if (errorCurp != null) {
            Mensajes.aviso(vista, errorCurp);
            return;
        }
        int turno;
        try {
            turno = Integer.parseInt(vista.getTurno().trim());
            if (turno <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            Mensajes.aviso(vista, "El numero de turno debe ser un numero entero mayor a cero.");
            return;
        }

        try {
            Solicitud solicitud = solicitudDAO.buscarPorCurpYTurno(curp, turno);
            if (solicitud == null) {
                Mensajes.aviso(vista, "No se encontro una solicitud con esa CURP y ese numero de turno.");
                return;
            }
            vista.limpiarCampos();
            FrmSolicitud formulario = new FrmSolicitud();
            vista.setVisible(false);
            new SolicitudController(formulario, () -> vista.setVisible(true), solicitud).iniciar();
        } catch (SQLException ex) {
            Mensajes.error(vista, "Error de base de datos: " + ex.getMessage());
        }
    }

    private void abrirLogin() {
        FrmLogin login = new FrmLogin();
        vista.setVisible(false);
        new LoginController(login, vista).iniciar();
    }

    private void salir() {
        if (Mensajes.confirmar(vista, "¿Deseas cerrar el sistema?")) {
            Sistema.salir();
        }
    }
}
