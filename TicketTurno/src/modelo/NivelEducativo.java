/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

public class NivelEducativo {
    
    private int idNivel;
    private String nombre;
        
    public NivelEducativo(){
    }
        
    public NivelEducativo(int idNivel, String nombre){
        this.idNivel = idNivel;
        this.nombre = nombre;
    }
        
    public int getIdNivel() { return idNivel; }
    public void setIdNivel(int idNivel) { this.idNivel = idNivel; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return nombre;
    } 
}
