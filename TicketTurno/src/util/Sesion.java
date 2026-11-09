/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import modelo.Administrador;

//Variables globales de sesion (se borran al cerrar sesion)
public final class Sesion {
    private static Administrador administrador;
    
    private Sesion(){
    }
    
    public static void iniciar(Administrador admin){
        administrador = admin;
    }
    
    public static Administrador getAdministrador(){
        return administrador;
    }
    
    public static boolean haySesion(){
        return administrador != null;
    }
    
    public static void cerrar(){
        administrador = null;
    }   
}