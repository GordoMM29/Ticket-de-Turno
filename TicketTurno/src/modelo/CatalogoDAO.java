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
import java.util.ArrayList;
import java.util.List;
import util.Conexion;

/**
 * PATRON DE DISEÑO 2: DAO (Data Access Object).
 * Aisla todo el acceso a la base de datos del resto de la aplicacion.
 *
 * Esta clase base trae el CRUD completo de un catalogo (id + texto).
 * Cada catalogo concreto (Municipio, Nivel educativo, Asunto) solo indica
 * su tabla, sus columnas y como convertir una fila en un objeto.
 */

public abstract class CatalogoDAO<T> {
    
    private final String tabla;
    private final String columnaId;
    private final String columnaTexto;
    private final String orden;

    protected CatalogoDAO(String tabla, String columnaId, String columnaTexto, String orden) {
        this.tabla = tabla;
        this.columnaId = columnaId;
        this.columnaTexto = columnaTexto;
        this.orden = orden;
    }

    // Convierte la fila actual del ResultSet en un objeto del modelo
    protected abstract T mapear(ResultSet rs) throws SQLException;

    private Connection con() throws SQLException {
        return Conexion.getInstancia().getConexion();
    }

    // Lista de objetos (para llenar combos)
    public List<T> listar() throws SQLException {
        String sql = "SELECT * FROM " + tabla + " ORDER BY " + orden;
        List<T> lista = new ArrayList<T>();
        try (PreparedStatement ps = con().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // Filas {id, texto} (para llenar tablas)
    public List<Object[]> listarFilas() throws SQLException {
        String sql = "SELECT " + columnaId + ", " + columnaTexto + " FROM " + tabla + " ORDER BY " + orden;
        List<Object[]> filas = new ArrayList<Object[]>();
        try (PreparedStatement ps = con().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                filas.add(new Object[]{rs.getInt(1), rs.getString(2)});
            }
        }
        return filas;
    }

    public T buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM " + tabla + " WHERE " + columnaId + " = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public boolean insertar(String texto) throws SQLException {
        String sql = "INSERT INTO " + tabla + " (" + columnaTexto + ") VALUES (?)";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, texto);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizar(int id, String texto) throws SQLException {
        String sql = "UPDATE " + tabla + " SET " + columnaTexto + " = ? WHERE " + columnaId + " = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, texto);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM " + tabla + " WHERE " + columnaId + " = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
