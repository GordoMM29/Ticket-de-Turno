/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import modelo.Solicitud;

/**
 * Arma el comprobante con los datos basicos y el turno de la solicitud.
 */
public final class GeneradorComprobante {
    
    //Decorador
    private static final String DOBLE = "==================================================";
    private static final String SIMPLE = "--------------------------------------------------";

    private GeneradorComprobante() {
    }

    public static String generarTexto(Solicitud s, String municipio, String nivel, String asunto, String estatus) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String fecha = formato.format(s.getFechaRegistro() == null ? new Date() : s.getFechaRegistro());

        String alumno = s.getNombreAlumno() + " " + s.getApellidoPaterno();
        if (s.getApellidoMaterno() != null && !s.getApellidoMaterno().trim().isEmpty()) {
            alumno += " " + s.getApellidoMaterno();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(DOBLE).append('\n');
        sb.append("          COMPROBANTE DE TICKET DE TURNO").append('\n');
        sb.append(DOBLE).append('\n').append('\n');
        sb.append("   TURNO No. ").append(s.getTurno()).append('\n');
        sb.append("   Municipio: ").append(municipio).append('\n').append('\n');
        sb.append(SIMPLE).append('\n');
        sb.append(linea("CURP del alumno", s.getCurp()));
        sb.append(linea("Alumno", alumno));
        sb.append(linea("Realiza el tramite", s.getNombreTramitante()));
        sb.append(linea("Telefono", s.getTelefono()));
        sb.append(linea("Correo", s.getCorreo()));
        sb.append(SIMPLE).append('\n');
        sb.append(linea("Nivel que cursa", nivel));
        sb.append(linea("Asunto a tratar", asunto));
        sb.append(linea("Estatus", estatus));
        sb.append(linea("Fecha de registro", fecha));
        sb.append(SIMPLE).append('\n');
        sb.append(DOBLE).append('\n');
        sb.append("Conserve este comprobante: para modificar su\n");
        sb.append("solicitud necesitara su CURP y su numero de turno.\n");
        sb.append(DOBLE).append('\n');
        return sb.toString();
    }

    private static String linea(String etiqueta, String valor) {
        return String.format("%-19s: %s%n", etiqueta, valor == null ? "" : valor).replace("\r\n", "\n");
    }

    //Guarda el comprobante en un archivo de texto
    public static void guardar(String texto, File destino) throws IOException {
        try (Writer w = new OutputStreamWriter(new FileOutputStream(destino), StandardCharsets.UTF_8)) {
            w.write(texto.replace("\n", System.lineSeparator()));
        }
    }
}
