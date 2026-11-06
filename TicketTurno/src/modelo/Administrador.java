/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

public class Administrador {
    
    private int idAdmin;
    private String usuario;
    private String contrasena;
    private String nombre;
    
    public Administrador(){
    }
    
    public Administrador(int idAdmin, String usuario, String contrasena, String nombre){
        this.idAdmin = idAdmin;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.nombre = nombre;
    }
    
    public int getIdAdmin() { return idAdmin; }
    public void setIdAdmin(int idAdmin) { this.idAdmin = idAdmin; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return nombre;
    }
}
