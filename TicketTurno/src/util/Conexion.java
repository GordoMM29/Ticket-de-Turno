/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * PATRON DE DISEÑO 1: SINGLETON.
 * Garantiza que exista una sola instancia de Conexion en toda la aplicacion.
 *
 * Los datos de conexion se leen del archivo (si existe) que cada integrante guarda en su computadora, sin subirlo a GitHub.
 */

public final class Conexion {
    
    private static Conexion instancia;
    
    private final String url;
    private final String usuario;
    private final String clave;
    private Connection conexion;
    
        private Conexion() throws SQLException {
        Properties p = new Properties();
        p.setProperty("host", "localhost");
        p.setProperty("puerto", "3307");
        p.setProperty("bd", "ticket_turno");
        p.setProperty("usuario", "root");
        p.setProperty("clave", "");

        File archivo = new File("conexion.properties");
        if (archivo.exists()) {
            try (FileInputStream in = new FileInputStream(archivo)) {
                p.load(in);
            } catch (IOException ex) {
                throw new SQLException("No se pudo leer conexion.properties: " + ex.getMessage());
            }
        }

        this.url = "jdbc:mysql://" + p.getProperty("host").trim() + ":" + p.getProperty("puerto").trim()
                + "/" + p.getProperty("bd").trim()
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
                + "&useUnicode=true&characterEncoding=UTF-8";
        this.usuario = p.getProperty("usuario").trim();
        this.clave = p.getProperty("clave");
        cargarDriver();
    }

    // Soporta el conector nuevo (8.x) y el anterior (5.x)
    private static void cargarDriver() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e1) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e2) {
                throw new SQLException("No se encontro el conector JDBC de MySQL. "
                        + "Agrega el .jar en Libraries del proyecto.");
            }
        }
    }

    public static synchronized Conexion getInstancia() throws SQLException {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

    /** Devuelve la conexion; si esta cerrada o caida, la vuelve a abrir. */
    public synchronized Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed() || !conexion.isValid(2)) {
            conexion = DriverManager.getConnection(url, usuario, clave);
        }
        return conexion;
    }

    /** Cierra la conexion y destruye la instancia unica. */
    public static synchronized void cerrarConexion() {
        if (instancia != null) {
            try {
                if (instancia.conexion != null && !instancia.conexion.isClosed()) {
                    instancia.conexion.close();
                }
            } catch (SQLException ex) {
                // no hay nada mas que hacer al cerrar
            }
            instancia.conexion = null;
            instancia = null;
        }
    }
}