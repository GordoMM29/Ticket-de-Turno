/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

public class Municipio {
    
    private int idMunicipio;
    private String nombre;
    private int ultimoTurno;// el contador cambia segun el municipio
    
    public Municipio(){
    }
    
    public Municipio(int idMunicipio, String nombre, int ultimoTurno){
        this.idMunicipio = idMunicipio;
        this.nombre = nombre;
        this.ultimoTurno = ultimoTurno;
    }
    
    public int getIdMunicipio() { return idMunicipio; }
    public void setIdMunicipio(int idMunicipio) { this.idMunicipio = idMunicipio; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getUltimoTurno() { return ultimoTurno; }
    public void setUltimoTurno(int ultimoTurno) { this.ultimoTurno = ultimoTurno; }

    @Override
    public String toString() {
        return nombre;
    }
    
}
