/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

//Cierre seguro de la app, destruye la sesion y cierra la base de datos
public final class Sistema {
    
    private Sistema(){}
    
    public static void liberarRecursos(){
        Sesion.cerrar( );
        Conexion.cerrarConexion();
    }
    
    public static void salir(){
        liberarRecursos();
        System.exit(0);
    }
}
