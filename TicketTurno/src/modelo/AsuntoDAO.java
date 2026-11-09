/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AsuntoDAO extends CatalogoDAO<Asunto>{
    
    public AsuntoDAO() {
        super("asunto", "id_asunto", "descripcion", "id_asunto");
    }

    @Override
    protected Asunto mapear(ResultSet rs) throws SQLException {
        return new Asunto(rs.getInt("id_asunto"), rs.getString("descripcion"));
    }
}
