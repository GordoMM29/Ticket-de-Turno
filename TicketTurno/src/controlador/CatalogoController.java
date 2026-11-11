package controlador;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import javax.swing.WindowConstants;
import modelo.AsuntoDAO;
import modelo.CatalogoDAO;
import modelo.MunicipioDAO;
import modelo.NivelEducativoDAO;
import util.Sesion;
import vista.FrmCatalogos;
import vista.Mensajes;

/**
 * CRUD de los catalogos: Municipios, Niveles educativos y Asuntos.
 * (Los estatus Pendiente/Resuelto son fijos del sistema y no se editan.)
 */
public class CatalogoController {

    // El orden coincide con las opciones del combo de la ventana
    private final CatalogoDAO<?>[] daos = {new MunicipioDAO(), new NivelEducativoDAO(), new AsuntoDAO()};
    private final String[] nombres = {"municipio", "nivel educativo", "asunto"};
    private final int[] longitudMaxima = {60, 40, 100};

    private final FrmCatalogos vista;
    private final Runnable alVolver;

    public CatalogoController(FrmCatalogos vista, Runnable alVolver) {
        this.vista = vista;
        this.alVolver = alVolver;
    }

    public void iniciar() {
        if (!Sesion.haySesion()) {
            vista.dispose();
            alVolver.run();
            return;
        }
        vista.getCmbCatalogo().addActionListener(e -> cargar());
        vista.getBtnAgregar().addActionListener(e -> agregar());
        vista.getBtnGuardar().addActionListener(e -> guardarCambios());
        vista.getBtnEliminar().addActionListener(e -> eliminar());
        vista.getBtnVolver().addActionListener(e -> volver());

        vista.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        vista.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                volver();
            }
        });

        cargar();
        vista.setLocationRelativeTo(null);
        vista.setVisible(true);
    }

    private int indice() {
        return Math.max(0, Math.min(vista.getIndiceCatalogo(), daos.length - 1));
    }

    private CatalogoDAO<?> actual() {
        return daos[indice()];
    }

    private void cargar() {
        try {
            vista.cargarTabla(actual().listarFilas());
            vista.limpiarFormulario();
        } catch (SQLException ex) {
            Mensajes.error(vista, "No se pudo cargar el catalogo: " + ex.getMessage());
        }
    }

    /** Devuelve el texto escrito si es valido, o null (avisando) si no lo es. */
    private String textoValido() {
        String texto = vista.getNombre().trim();
        if (texto.isEmpty()) {
            Mensajes.aviso(vista, "Escribe el nombre del " + nombres[indice()] + ".");
            return null;
        }
        if (texto.length() > longitudMaxima[indice()]) {
            Mensajes.aviso(vista, "El nombre es demasiado largo (maximo "
                    + longitudMaxima[indice()] + " caracteres).");
            return null;
        }
        return texto;
    }

    private void agregar() {
        String texto = textoValido();
        if (texto == null) {
            return;
        }
        try {
            actual().insertar(texto);
            cargar();
        } catch (SQLException ex) {
            mostrarError(ex, "agregar");
        }
    }

    private void guardarCambios() {
        Integer id = vista.getIdSeleccionado();
        if (id == null) {
            Mensajes.aviso(vista, "Selecciona un registro de la tabla para modificarlo.");
            return;
        }
        String texto = textoValido();
        if (texto == null) {
            return;
        }
        try {
            actual().actualizar(id, texto);
            cargar();
        } catch (SQLException ex) {
            mostrarError(ex, "modificar");
        }
    }

    private void eliminar() {
        Integer id = vista.getIdSeleccionado();
        if (id == null) {
            Mensajes.aviso(vista, "Selecciona un registro de la tabla para eliminarlo.");
            return;
        }
        if (!Mensajes.confirmar(vista, "¿Seguro que quieres eliminar este " + nombres[indice()] + "?")) {
            return;
        }
        try {
            actual().eliminar(id);
            cargar();
        } catch (SQLException ex) {
            mostrarError(ex, "eliminar");
        }
    }

    private void mostrarError(SQLException ex, String accion) {
        if (ex.getErrorCode() == 1062) {
            Mensajes.aviso(vista, "Ya existe un registro con ese nombre.");
        } else if (ex.getErrorCode() == 1451) {
            Mensajes.aviso(vista, "No se puede eliminar: hay solicitudes registradas que lo usan.");
        } else {
            Mensajes.error(vista, "No se pudo " + accion + ": " + ex.getMessage());
        }
    }

    private void volver() {
        vista.dispose();
        alVolver.run();
    }
}
