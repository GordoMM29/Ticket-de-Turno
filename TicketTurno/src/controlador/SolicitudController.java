package controlador;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.WindowConstants;
import modelo.Asunto;
import modelo.AsuntoDAO;
import modelo.Estatus;
import modelo.Municipio;
import modelo.MunicipioDAO;
import modelo.NivelEducativo;
import modelo.NivelEducativoDAO;
import modelo.Solicitud;
import modelo.SolicitudDAO;
import util.GeneradorComprobante;
import util.ValidadorCurp;
import util.ValidadorDatos;
import vista.DlgComprobante;
import vista.FrmSolicitud;
import vista.Mensajes;

/**
 * Formulario de solicitud: registra una solicitud nueva (original == null)
 * o modifica una existente (original != null).
 */
public class SolicitudController {

    private final FrmSolicitud vista;
    private final Runnable alCerrar;
    private final Solicitud original;
    private final boolean edicion;

    private final SolicitudDAO solicitudDAO = new SolicitudDAO();
    private final MunicipioDAO municipioDAO = new MunicipioDAO();
    private final NivelEducativoDAO nivelDAO = new NivelEducativoDAO();
    private final AsuntoDAO asuntoDAO = new AsuntoDAO();

    /**
     * @param alCerrar codigo que se ejecuta al cerrar el formulario (normalmente
     *                 volver a mostrar la ventana anterior)
     * @param original solicitud a modificar, o null para registrar una nueva
     */
    public SolicitudController(FrmSolicitud vista, Runnable alCerrar, Solicitud original) {
        this.vista = vista;
        this.alCerrar = alCerrar;
        this.original = original;
        this.edicion = original != null;
    }

    public void iniciar() {
        try {
            vista.cargarNiveles(nivelDAO.listar());
            vista.cargarMunicipios(municipioDAO.listar());
            vista.cargarAsuntos(asuntoDAO.listar());
        } catch (SQLException ex) {
            Mensajes.error(vista, "No se pudieron cargar los catalogos: " + ex.getMessage());
            cerrar();
            return;
        }

        vista.limpiar();
        vista.setModoEdicion(edicion);
        if (edicion) {
            vista.cargarSolicitud(original);
        }

        vista.getBtnRegistrar().addActionListener(e -> guardar());
        vista.getBtnLimpiar().addActionListener(e -> limpiar());
        vista.getBtnVolver().addActionListener(e -> cerrar());

        vista.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        vista.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrar();
            }
        });

        vista.setLocationRelativeTo(null);
        vista.setVisible(true);
    }

    // ------------------------------------------------------------------
    private void guardar() {
        List<String> errores = validar();
        if (!errores.isEmpty()) {
            Mensajes.aviso(vista, "Corrige lo siguiente:\n- " + String.join("\n- ", errores));
            return;
        }
        try {
            if (edicion) {
                actualizar();
            } else {
                registrar();
            }
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062) {
                Mensajes.aviso(vista, "Ya existe una solicitud registrada con esa CURP.");
            } else {
                Mensajes.error(vista, "No se pudo guardar la informacion: " + ex.getMessage());
            }
        }
    }

    /** Devuelve la lista de problemas del formulario (vacia si todo esta bien). */
    private List<String> validar() {
        List<String> errores = new ArrayList<String>();
        if (!edicion) {
            String errorCurp = ValidadorCurp.obtenerError(vista.getCurp());
            if (errorCurp != null) {
                errores.add(errorCurp);
            }
        }
        if (!ValidadorDatos.nombreValido(vista.getTramitante(), 100)) {
            errores.add("Escribe el nombre de quien realiza el tramite (solo letras).");
        }
        if (!ValidadorDatos.nombreValido(vista.getNombreAlumno(), 60)) {
            errores.add("Escribe el nombre del alumno (solo letras).");
        }
        if (!ValidadorDatos.nombreValido(vista.getApPaterno(), 40)) {
            errores.add("Escribe el apellido paterno (solo letras).");
        }
        String materno = vista.getApMaterno();
        if (!materno.isEmpty() && !ValidadorDatos.nombreValido(materno, 40)) {
            errores.add("El apellido materno solo debe llevar letras.");
        }
        if (!ValidadorDatos.telefonoValido(vista.getTelefono())) {
            errores.add("El telefono debe tener 10 digitos.");
        }
        if (!ValidadorDatos.correoValido(vista.getCorreo())) {
            errores.add("El correo electronico no es valido.");
        }
        if (vista.getNivelSeleccionado() == null) {
            errores.add("Selecciona el nivel que cursa.");
        }
        if (vista.getMunicipioSeleccionado() == null) {
            errores.add("Selecciona el municipio donde estudia.");
        }
        if (vista.getAsuntoSeleccionado() == null) {
            errores.add("Selecciona el asunto a tratar.");
        }
        return errores;
    }

    private Solicitud construir(String curp) {
        Solicitud s = new Solicitud();
        s.setCurp(curp);
        s.setNombreTramitante(vista.getTramitante());
        s.setNombreAlumno(vista.getNombreAlumno());
        s.setApellidoPaterno(vista.getApPaterno());
        s.setApellidoMaterno(vista.getApMaterno());
        s.setTelefono(ValidadorDatos.normalizarTelefono(vista.getTelefono()));
        s.setCorreo(vista.getCorreo());
        s.setIdNivel(vista.getNivelSeleccionado().getIdNivel());
        s.setIdMunicipio(vista.getMunicipioSeleccionado().getIdMunicipio());
        s.setIdAsunto(vista.getAsuntoSeleccionado().getIdAsunto());
        return s;
    }

    private void registrar() throws SQLException {
        String curp = ValidadorCurp.normalizar(vista.getCurp());
        if (solicitudDAO.existe(curp)) {
            Solicitud existente = solicitudDAO.buscarPorCurp(curp);
            Mensajes.aviso(vista, "Ya existe una solicitud con esa CURP (turno "
                    + existente.getTurno() + ").\nPara cambiar sus datos usa la opcion de "
                    + "modificar con la CURP y el numero de turno.");
            return;
        }
        Solicitud nueva = construir(curp);
        solicitudDAO.registrar(nueva);
        Solicitud guardada = solicitudDAO.buscarPorCurp(curp);
        mostrarComprobante(guardada);
        vista.limpiar();
    }

    private void actualizar() throws SQLException {
        Solicitud cambios = construir(original.getCurp());
        if (!solicitudDAO.actualizar(cambios)) {
            Mensajes.error(vista, "No se pudo actualizar: la solicitud ya no existe.");
            return;
        }
        Solicitud guardada = solicitudDAO.buscarPorCurp(original.getCurp());
        Mensajes.info(vista, "Los datos se actualizaron correctamente.");
        mostrarComprobante(guardada);
        cerrar();
    }

    private void mostrarComprobante(Solicitud s) throws SQLException {
        Municipio municipio = municipioDAO.buscarPorId(s.getIdMunicipio());
        NivelEducativo nivel = nivelDAO.buscarPorId(s.getIdNivel());
        Asunto asunto = asuntoDAO.buscarPorId(s.getIdAsunto());
        String estatus = s.getIdEstatus() == Estatus.RESUELTO ? "Resuelto" : "Pendiente";

        String texto = GeneradorComprobante.generarTexto(s,
                municipio == null ? "-" : municipio.getNombre(),
                nivel == null ? "-" : nivel.getNombre(),
                asunto == null ? "-" : asunto.getDescripcion(),
                estatus);
        new DlgComprobante(vista, texto, "comprobante_turno_" + s.getTurno() + ".txt").setVisible(true);
    }

    private void limpiar() {
        if (edicion) {
            vista.cargarSolicitud(original);   // restablece los datos originales
        } else {
            vista.limpiar();
        }
    }

    private void cerrar() {
        vista.dispose();
        alCerrar.run();
    }
}
