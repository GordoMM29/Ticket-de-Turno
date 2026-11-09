/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import util.Conexion;
import util.Hash;

public class AdministradorDAO {
    //validar el usuario y la contraseña
    
        public Administrador autenticar(String usuario, String clave) throws SQLException {
        String sql = "SELECT id_admin, usuario, contrasena, nombre FROM administrador "
                + "WHERE usuario = ? AND contrasena = ?";
        try (PreparedStatement ps = Conexion.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, Hash.sha256(clave));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Administrador(rs.getInt("id_admin"), rs.getString("usuario"),
                            rs.getString("contrasena"), rs.getString("nombre"));
                }
                return null;
            }
        }
    }
}
