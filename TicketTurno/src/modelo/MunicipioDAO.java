/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MunicipioDAO extends CatalogoDAO<Municipio> {

    public MunicipioDAO() {
        super("municipio", "id_municipio", "nombre", "nombre");
    }

    @Override
    protected Municipio mapear(ResultSet rs) throws SQLException {
        return new Municipio(rs.getInt("id_municipio"), rs.getString("nombre"),
                rs.getInt("ultimo_turno"));
    }
}
