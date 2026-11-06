/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

public class Estatus {
    public static final int PENDIENTE = 1;
    public static final int RESUELTO = 2;
    
    private int idEstatus;
    private String nombre;
    
    public Estatus(){
    }
    
    public Estatus(int idEstatus, String nombre){
        this.idEstatus = idEstatus;
        this.nombre = nombre;
    }
    
    public int getIdEstatus() { return idEstatus; }
    public void setIdEstatus(int idEstatus) { this.idEstatus = idEstatus; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return nombre;
    }
}
