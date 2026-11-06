/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

public class Asunto {
    
    private int idAsunto;
    private String descripcion;
    
    public Asunto(){
    }
    
    public Asunto(int idAsunto,String descripcion){
        this.descripcion = descripcion;
        this.idAsunto = idAsunto;
    }
    
    public int getIdAsunto() { return idAsunto; }
    public void setIdAsunto(int idAsunto) { this.idAsunto = idAsunto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() {
        return descripcion;
    }
}
