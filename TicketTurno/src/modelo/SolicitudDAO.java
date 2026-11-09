/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import util.Conexion;

/**
 * Acceso a datos de las solicitudes (tabla solicitud).
 */
public class SolicitudDAO {

    private Connection con() throws SQLException {
        return Conexion.getInstancia().getConexion();
    }

    private static String nullSiVacio(String texto) {
        return (texto == null || texto.trim().isEmpty()) ? null : texto.trim();
    }

    // REGISTRAR (asigna el turno consecutivo del municipio, con transaccion)
    public int registrar(Solicitud s) throws SQLException {
        Connection con = con();
        boolean autoCommitOriginal = con.getAutoCommit();
        try {
            con.setAutoCommit(false);
            int turno = siguienteTurno(con, s.getIdMunicipio());

            String sql = "INSERT INTO solicitud (curp, nombre_tramitante, nombre_alumno, "
                    + "apellido_paterno, apellido_materno, telefono, correo, id_nivel, "
                    + "id_municipio, id_asunto, turno, id_estatus) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, s.getCurp());
                ps.setString(2, s.getNombreTramitante());
                ps.setString(3, s.getNombreAlumno());
                ps.setString(4, s.getApellidoPaterno());
                ps.setString(5, nullSiVacio(s.getApellidoMaterno()));
                ps.setString(6, s.getTelefono());
                ps.setString(7, s.getCorreo());
                ps.setInt(8, s.getIdNivel());
                ps.setInt(9, s.getIdMunicipio());
                ps.setInt(10, s.getIdAsunto());
                ps.setInt(11, turno);
                ps.setInt(12, Estatus.PENDIENTE);
                ps.executeUpdate();
            }
            con.commit();
            s.setTurno(turno);
            s.setIdEstatus(Estatus.PENDIENTE);
            return turno;
        } catch (SQLException ex) {
            try {
                con.rollback();
            } catch (SQLException ignorar) {
                // se reporta el error original
            }
            throw ex;
        } finally {
            con.setAutoCommit(autoCommitOriginal);
        }
    }

    // Sube el contador del municipio y devuelve el nuevo valor (dentro de la transaccion)
    private int siguienteTurno(Connection con, int idMunicipio) throws SQLException {
        try (PreparedStatement up = con.prepareStatement(
                "UPDATE municipio SET ultimo_turno = ultimo_turno + 1 WHERE id_municipio = ?")) {
            up.setInt(1, idMunicipio);
            if (up.executeUpdate() == 0) {
                throw new SQLException("El municipio seleccionado no existe.");
            }
        }
        try (PreparedStatement sel = con.prepareStatement(
                "SELECT ultimo_turno FROM municipio WHERE id_municipio = ?")) {
            sel.setInt(1, idMunicipio);
            try (ResultSet rs = sel.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    // CONSULTAS
    public boolean existe(String curp) throws SQLException {
        try (PreparedStatement ps = con().prepareStatement("SELECT 1 FROM solicitud WHERE curp = ?")) {
            ps.setString(1, curp);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Solicitud buscarPorCurp(String curp) throws SQLException {
        try (PreparedStatement ps = con().prepareStatement("SELECT * FROM solicitud WHERE curp = ?")) {
            ps.setString(1, curp);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    // Para modificar en la parte publica: se pide CURP y numero de turno
    public Solicitud buscarPorCurpYTurno(String curp, int turno) throws SQLException {
        try (PreparedStatement ps = con().prepareStatement(
                "SELECT * FROM solicitud WHERE curp = ? AND turno = ?")) {
            ps.setString(1, curp);
            ps.setInt(2, turno);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    /**
     * Filas para la tabla del administrador:
     * {turno, curp, alumno, municipio, nivel, asunto, estatus, fecha}.
     * Si texto no esta vacio, filtra por CURP o por nombre del alumno.
     */
    public List<Object[]> listarFilas(String texto) throws SQLException {
        String nombreCompleto = "CONCAT_WS(' ', s.nombre_alumno, s.apellido_paterno, s.apellido_materno)";
        boolean filtrar = texto != null && !texto.trim().isEmpty();
        String sql = "SELECT s.turno, s.curp, " + nombreCompleto + " AS alumno, "
                + "m.nombre AS municipio, n.nombre AS nivel, a.descripcion AS asunto, "
                + "e.nombre AS estatus, s.fecha_registro "
                + "FROM solicitud s "
                + "JOIN municipio m ON m.id_municipio = s.id_municipio "
                + "JOIN nivel_educativo n ON n.id_nivel = s.id_nivel "
                + "JOIN asunto a ON a.id_asunto = s.id_asunto "
                + "JOIN estatus e ON e.id_estatus = s.id_estatus "
                + (filtrar ? "WHERE s.curp LIKE ? OR " + nombreCompleto + " LIKE ? " : "")
                + "ORDER BY m.nombre, s.turno";

        List<Object[]> filas = new ArrayList<Object[]>();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            if (filtrar) {
                String patron = "%" + texto.trim() + "%";
                ps.setString(1, patron);
                ps.setString(2, patron);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp fecha = rs.getTimestamp("fecha_registro");
                    filas.add(new Object[]{
                        rs.getInt("turno"),
                        rs.getString("curp"),
                        rs.getString("alumno"),
                        rs.getString("municipio"),
                        rs.getString("nivel"),
                        rs.getString("asunto"),
                        rs.getString("estatus"),
                        fecha == null ? "" : formato.format(fecha)
                    });
                }
            }
        }
        return filas;
    }

    // MODIFICAR / ESTATUS / ELIMINAR
    public boolean actualizar(Solicitud s) throws SQLException {
        String sql = "UPDATE solicitud SET nombre_tramitante = ?, nombre_alumno = ?, "
                + "apellido_paterno = ?, apellido_materno = ?, telefono = ?, correo = ?, "
                + "id_nivel = ?, id_asunto = ? WHERE curp = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, s.getNombreTramitante());
            ps.setString(2, s.getNombreAlumno());
            ps.setString(3, s.getApellidoPaterno());
            ps.setString(4, nullSiVacio(s.getApellidoMaterno()));
            ps.setString(5, s.getTelefono());
            ps.setString(6, s.getCorreo());
            ps.setInt(7, s.getIdNivel());
            ps.setInt(8, s.getIdAsunto());
            ps.setString(9, s.getCurp());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean cambiarEstatus(String curp, int idEstatus) throws SQLException {
        try (PreparedStatement ps = con().prepareStatement(
                "UPDATE solicitud SET id_estatus = ? WHERE curp = ?")) {
            ps.setInt(1, idEstatus);
            ps.setString(2, curp);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(String curp) throws SQLException {
        try (PreparedStatement ps = con().prepareStatement("DELETE FROM solicitud WHERE curp = ?")) {
            ps.setString(1, curp);
            return ps.executeUpdate() > 0;
        }
    }

    // DASHBOARD
    public int[] contarPorEstatus(Integer idMunicipio) throws SQLException {
        String sql = "SELECT id_estatus, COUNT(*) FROM solicitud "
                + (idMunicipio != null ? "WHERE id_municipio = ? " : "")
                + "GROUP BY id_estatus";
        int[] conteo = new int[2];
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            if (idMunicipio != null) {
                ps.setInt(1, idMunicipio);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (rs.getInt(1) == Estatus.PENDIENTE) {
                        conteo[0] = rs.getInt(2);
                    } else if (rs.getInt(1) == Estatus.RESUELTO) {
                        conteo[1] = rs.getInt(2);
                    }
                }
            }
        }
        return conteo;
    }

    private Solicitud mapear(ResultSet rs) throws SQLException {
        Solicitud s = new Solicitud();
        s.setCurp(rs.getString("curp"));
        s.setNombreTramitante(rs.getString("nombre_tramitante"));
        s.setNombreAlumno(rs.getString("nombre_alumno"));
        s.setApellidoPaterno(rs.getString("apellido_paterno"));
        s.setApellidoMaterno(rs.getString("apellido_materno"));
        s.setTelefono(rs.getString("telefono"));
        s.setCorreo(rs.getString("correo"));
        s.setIdNivel(rs.getInt("id_nivel"));
        s.setIdMunicipio(rs.getInt("id_municipio"));
        s.setIdAsunto(rs.getInt("id_asunto"));
        s.setTurno(rs.getInt("turno"));
        s.setIdEstatus(rs.getInt("id_estatus"));
        s.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        return s;
    }
}