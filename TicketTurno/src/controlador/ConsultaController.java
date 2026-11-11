package controlador;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import javax.swing.WindowConstants;
import modelo.Estatus;
import modelo.Solicitud;
import modelo.SolicitudDAO;
import util.Sesion;
import vista.FrmConsulta;
import vista.FrmSolicitud;
import vista.Mensajes;

/**
 * Consulta del administrador: buscar por CURP o nombre, registrar, modificar,
 * eliminar y marcar como Resuelto / Pendiente.
 */
public class ConsultaController {

    private final FrmConsulta vista;
    private final Runnable alVolver;
    private final SolicitudDAO dao = new SolicitudDAO();

    public ConsultaController(FrmConsulta vista, Runnable alVolver) {
        this.vista = vista;
        this.alVolver = alVolver;
    }

    public void iniciar() {
        if (!Sesion.haySesion()) {      // seguridad: solo con sesion iniciada
            vista.dispose();
            alVolver.run();
            return;
        }
        vista.getBtnBuscar().addActionListener(e -> buscar());
        vista.getBtnVerTodas().addActionListener(e -> verTodas());
        vista.getBtnRegistrar().addActionListener(e -> registrar());
        vista.getBtnModificar().addActionListener(e -> modificar());
        vista.getBtnEliminar().addActionListener(e -> eliminar());
        vista.getBtnResolver().addActionListener(e -> cambiarEstatus(Estatus.RESUELTO, "Resuelto"));
        vista.getBtnPendiente().addActionListener(e -> cambiarEstatus(Estatus.PENDIENTE, "Pendiente"));
        vista.getBtnVolver().addActionListener(e -> volver());
        vista.getRootPane().setDefaultButton(vista.getBtnBuscar());

        vista.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        vista.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                volver();
            }
        });

        cargar("");
        vista.setLocationRelativeTo(null);
        vista.setVisible(true);
    }

    private void cargar(String texto) {
        try {
            vista.cargarTabla(dao.listarFilas(texto));
        } catch (SQLException ex) {
            Mensajes.error(vista, "No se pudieron consultar las solicitudes: " + ex.getMessage());
        }
    }

    private void buscar() {
        cargar(vista.getTextoBusqueda());
    }

    private void verTodas() {
        vista.setTextoBusqueda("");
        cargar("");
    }

    /** Devuelve la CURP de la fila elegida, o null (avisando) si no hay ninguna. */
    private String curpElegida() {
        String curp = vista.getCurpSeleccionada();
        if (curp == null) {
            Mensajes.aviso(vista, "Selecciona una solicitud de la tabla.");
        }
        return curp;
    }

    private void registrar() {
        FrmSolicitud formulario = new FrmSolicitud();
        vista.setVisible(false);
        new SolicitudController(formulario, this::regresarDeFormulario, null).iniciar();
    }

    private void modificar() {
        String curp = curpElegida();
        if (curp == null) {
            return;
        }
        try {
            Solicitud solicitud = dao.buscarPorCurp(curp);
            if (solicitud == null) {
                Mensajes.aviso(vista, "La solicitud ya no existe.");
                buscar();
                return;
            }
            FrmSolicitud formulario = new FrmSolicitud();
            vista.setVisible(false);
            new SolicitudController(formulario, this::regresarDeFormulario, solicitud).iniciar();
        } catch (SQLException ex) {
            Mensajes.error(vista, "Error de base de datos: " + ex.getMessage());
        }
    }

    private void regresarDeFormulario() {
        vista.setVisible(true);
        buscar();
    }

    private void eliminar() {
        String curp = curpElegida();
        if (curp == null) {
            return;
        }
        if (!Mensajes.confirmar(vista, "¿Seguro que quieres eliminar la solicitud de la CURP\n" + curp + "?")) {
            return;
        }
        try {
            dao.eliminar(curp);
            buscar();
        } catch (SQLException ex) {
            Mensajes.error(vista, "No se pudo eliminar: " + ex.getMessage());
        }
    }

    private void cambiarEstatus(int idEstatus, String nombre) {
        String curp = curpElegida();
        if (curp == null) {
            return;
        }
        try {
            dao.cambiarEstatus(curp, idEstatus);
            buscar();
        } catch (SQLException ex) {
            Mensajes.error(vista, "No se pudo cambiar el estatus a " + nombre + ": " + ex.getMessage());
        }
    }

    private void volver() {
        vista.dispose();
        alVolver.run();
    }
}
