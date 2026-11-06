/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ticketturno;


public class TicketTurno {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        //Esto noma es la prueba del modelo, despues de borra por que esto se supone que va en la base
        //xd
        modelo.Municipio m = new modelo.Municipio(30, "Saltillo", 0);
        modelo.NivelEducativo n = new modelo.NivelEducativo(2, "Primaria");
        modelo.Asunto a = new modelo.Asunto(1, "Inscripcion");
        modelo.Estatus e = new modelo.Estatus(modelo.Estatus.PENDIENTE, "Pendiente");
        modelo.Administrador adm = new modelo.Administrador(1, "admin", "hash", "Administrador General");

        modelo.Solicitud s = new modelo.Solicitud("PETD800714HCLRNV02", "Maria Torres",
        "Diego", "Perez", "Torres", "8441234567", "correo@ejemplo.com",
        n.getIdNivel(), m.getIdMunicipio(), a.getIdAsunto());
        s.setTurno(1);
        s.setIdEstatus(e.getIdEstatus());

        System.out.println(m);      // Saltillo
        System.out.println(n);      // Primaria
        System.out.println(a);      // Inscripcion
        System.out.println(e);      // Pendiente
        System.out.println(adm);    // Administrador General
        System.out.println(s);      // Solicitud(da la curp, nombre y el turno)
  
    }
    
}
