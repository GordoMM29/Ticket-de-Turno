package controlador;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import javax.swing.WindowConstants;
import modelo.Municipio;
import modelo.MunicipioDAO;
import modelo.SolicitudDAO;
import util.Sesion;
import vista.FrmDashboard;
import vista.Mensajes;

/**
 * Tablero con la grafica de solicitudes Pendientes y Resueltas,
 * filtrable por municipio o por el total.
 */
public class DashboardController {

    private final FrmDashboard vista;
    private final Runnable alVolver;
    private final SolicitudDAO solicitudDAO = new SolicitudDAO();
    private final MunicipioDAO municipioDAO = new MunicipioDAO();

    public DashboardController(FrmDashboard vista, Runnable alVolver) {
        this.vista = vista;
        this.alVolver = alVolver;
    }

    public void iniciar() {
        if (!Sesion.haySesion()) {
            vista.dispose();
            alVolver.run();
            return;
        }
        try {
            vista.cargarMunicipios(municipioDAO.listar());   // agrega "Todos" al inicio
        } catch (SQLException ex) {
            Mensajes.error(vista, "No se pudieron cargar los municipios: " + ex.getMessage());
        }

        // Los listeners se conectan despues de llenar el combo
        vista.getCmbMunicipio().addActionListener(e -> actualizar());
        vista.getBtnActualizar().addActionListener(e -> actualizar());
        vista.getBtnVolver().addActionListener(e -> volver());

        vista.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        vista.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                volver();
            }
        });

        actualizar();
        vista.setLocationRelativeTo(null);
        vista.setVisible(true);
    }

    private void actualizar() {
        Municipio elegido = vista.getMunicipioSeleccionado();
        // id 0 = "Todos los municipios"
        Integer idMunicipio = (elegido == null || elegido.getIdMunicipio() == 0)
                ? null : elegido.getIdMunicipio();
        try {
            int[] conteo = solicitudDAO.contarPorEstatus(idMunicipio);
            vista.mostrarDatos(conteo[0], conteo[1]);
        } catch (SQLException ex) {
            Mensajes.error(vista, "No se pudieron obtener los datos: " + ex.getMessage());
        }
    }

    private void volver() {
        vista.dispose();
        alVolver.run();
    }
}
