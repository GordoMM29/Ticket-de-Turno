package controlador;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import javax.swing.WindowConstants;
import modelo.Administrador;
import modelo.AdministradorDAO;
import util.Sesion;
import vista.FrmInicio;
import vista.FrmLogin;
import vista.FrmMenu;
import vista.Mensajes;

/**
 * Login de administradores.
 */
public class LoginController {

    private static final int MAX_INTENTOS = 3;

    private final FrmLogin vista;
    private final FrmInicio inicio;
    private final AdministradorDAO dao = new AdministradorDAO();
    private int intentos = 0;

    public LoginController(FrmLogin vista, FrmInicio inicio) {
        this.vista = vista;
        this.inicio = inicio;
    }

    public void iniciar() {
        vista.getBtnEntrar().addActionListener(e -> entrar());
        vista.getBtnSalir().addActionListener(e -> volver());
        vista.getRootPane().setDefaultButton(vista.getBtnEntrar());

        vista.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        vista.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                volver();
            }
        });

        vista.limpiar();
        vista.setLocationRelativeTo(null);
        vista.setVisible(true);
    }

    private void entrar() {
        String usuario = vista.getUsuario().trim();
        String clave = vista.getContrasena();
        if (usuario.isEmpty() || clave.isEmpty()) {
            Mensajes.aviso(vista, "Escribe tu usuario y tu contraseña.");
            return;
        }
        try {
            Administrador admin = dao.autenticar(usuario, clave);
            if (admin == null) {
                intentos++;
                vista.limpiarContrasena();
                if (intentos >= MAX_INTENTOS) {
                    Mensajes.error(vista, "Demasiados intentos fallidos. Regresando al inicio.");
                    volver();
                } else {
                    Mensajes.error(vista, "Usuario o contraseña incorrectos (intento "
                            + intentos + " de " + MAX_INTENTOS + ").");
                }
                return;
            }
            Sesion.iniciar(admin);
            FrmMenu menu = new FrmMenu();
            vista.dispose();
            new MenuController(menu, inicio).iniciar();
        } catch (SQLException ex) {
            Mensajes.error(vista, "No se pudo validar el acceso: " + ex.getMessage());
        }
    }

    private void volver() {
        vista.dispose();
        inicio.setVisible(true);
    }
}
