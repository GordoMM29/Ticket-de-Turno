/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Valida el formato de la CURP (18 caracteres).
 * Estructura: 4 letras + 6 digitos (fecha AAMMDD) + sexo (H/M) + 2 letras de entidad + 3 consonantes internas + 2 caracteres finales (letras, numeros o combinacion). Ejemplo: PETD800714HCLRNV02
 */

public  final class ValidadorCurp {
  private static final String ENTIDADES =
            "AS|BC|BS|CC|CL|CM|CS|CH|DF|DG|GT|GR|HG|JC|MC|MN|MS|NT|NL|OC|PL|QT|QR|SP|SL|SR|TC|TS|TL|VZ|YN|ZS|NE";

    private static final Pattern PATRON = Pattern.compile(
            "^[A-Z][AEIOUX][A-Z]{2}"          // iniciales
            + "(\\d{2})(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])"  // aa mm dd
            + "[HM]"                           // sexo
            + "(" + ENTIDADES + ")"            // entidad
            + "[B-DF-HJ-NP-TV-Z]{3}"           // consonantes internas
            + "[A-Z0-9]{2}$");                 // 2 ultimos: letras y/o numeros

    private static final int[] DIAS_POR_MES = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    private ValidadorCurp() {
    }

    /** Quita espacios y la pasa a mayusculas. */
    public static String normalizar(String curp) {
        return curp == null ? "" : curp.trim().toUpperCase();
    }

    public static boolean esValida(String curp) {
        return obtenerError(curp) == null;
    }

    /** Devuelve null si la CURP es correcta, o el mensaje del problema. */
    public static String obtenerError(String curp) {
        String c = normalizar(curp);
        if (c.isEmpty()) {
            return "La CURP es obligatoria.";
        }
        if (c.length() != 18) {
            return "La CURP debe tener 18 caracteres (tiene " + c.length() + ").";
        }
        Matcher m = PATRON.matcher(c);
        if (!m.matches()) {
            return "La CURP no tiene el formato correcto (ejemplo: PETD800714HCLRNV02).";
        }
        int mes = Integer.parseInt(m.group(2));
        int dia = Integer.parseInt(m.group(3));
        if (dia > DIAS_POR_MES[mes - 1]) {
            return "La fecha de nacimiento dentro de la CURP no existe.";
        }
        return null;
    }
}