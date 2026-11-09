/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.regex.Pattern;

public final class ValidadorDatos {
    
    private static final Pattern CORREO =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern NOMBRE = Pattern.compile("^[\\p{L} .'-]+$");

    private ValidadorDatos() {
    }

    // Quita espacios, guiones y parentesis del telefono
    public static String normalizarTelefono(String telefono) {
        return telefono == null ? "" : telefono.replaceAll("[\\s()-]", "");
    }

    public static boolean telefonoValido(String telefono) {
        return normalizarTelefono(telefono).matches("\\d{10}");
    }

    public static boolean correoValido(String correo) {
        return correo != null && correo.trim().length() <= 80
                && CORREO.matcher(correo.trim()).matches();
    }

    //Nombres y apellidos: solo letras, espacios, punto, apostrofe o guion
    public static boolean nombreValido(String texto, int maxLongitud) {
        if (texto == null) {
            return false;
        }
        String t = texto.trim();
        return !t.isEmpty() && t.length() <= maxLongitud && NOMBRE.matcher(t).matches();
    }
}
