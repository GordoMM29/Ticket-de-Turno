/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NivelEducativoDAO extends CatalogoDAO<NivelEducativo> {
    
    public NivelEducativoDAO(){
        super("nivel_educativo","id_nivel","nombre","id_nivel");
    }
    
    @Override
    protected NivelEducativo mapear(ResultSet rs) throws SQLException {
        return new NivelEducativo(rs.getInt("id_nivel"), rs.getString("nombre"));
    }
}
