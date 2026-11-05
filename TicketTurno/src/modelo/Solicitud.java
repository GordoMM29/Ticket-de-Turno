/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.sql.Timestamp;

public class Solicitud{
    
    //Atributos
    private String curp;
    private String nombreTramitante;
    private String nombreAlumno;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String telefono;
    private String correo;
    private int idNivel;
    private int idMunicipio;
    private int idAsunto;
    private int turno;
    private int idEstatus;
    private Timestamp fechaRegistro;
    
    public Solicitud(){
    }
    
        public Solicitud(String curp, String nombreTramitante, String nombreAlumno,String apellidoPaterno, String apellidoMaterno, String telefono,String correo, int idNivel, int idMunicipio, int idAsunto){
        this.curp = curp;
        this.nombreTramitante = nombreTramitante;
        this.nombreAlumno = nombreAlumno;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.telefono = telefono;
        this.correo = correo;
        this.idNivel = idNivel;
        this.idMunicipio = idMunicipio;
        this.idAsunto = idAsunto;
    }
        
    public String getCurp() { return curp; }
    public void setCurp(String curp) { this.curp = curp; }

    public String getNombreTramitante() { return nombreTramitante; }
    public void setNombreTramitante(String nombreTramitante) { this.nombreTramitante = nombreTramitante; }

    public String getNombreAlumno() { return nombreAlumno; }
    public void setNombreAlumno(String nombreAlumno) { this.nombreAlumno = nombreAlumno; }

    public String getApellidoPaterno() { return apellidoPaterno; }
    public void setApellidoPaterno(String apellidoPaterno) { this.apellidoPaterno = apellidoPaterno; }

    public String getApellidoMaterno() { return apellidoMaterno; }
    public void setApellidoMaterno(String apellidoMaterno) { this.apellidoMaterno = apellidoMaterno; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public int getIdNivel() { return idNivel; }
    public void setIdNivel(int idNivel) { this.idNivel = idNivel; }

    public int getIdMunicipio() { return idMunicipio; }
    public void setIdMunicipio(int idMunicipio) { this.idMunicipio = idMunicipio; }

    public int getIdAsunto() { return idAsunto; }
    public void setIdAsunto(int idAsunto) { this.idAsunto = idAsunto; }

    public int getTurno() { return turno; }
    public void setTurno(int turno) { this.turno = turno; }

    public int getIdEstatus() { return idEstatus; }
    public void setIdEstatus(int idEstatus) { this.idEstatus = idEstatus; }

    public Timestamp getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Timestamp fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    
    public String getNombreCompleto() {
        return nombreAlumno + " " + apellidoPaterno + " " + apellidoMaterno;
    }

    @Override
    public String toString() {
        return "Solicitud{curp=" + curp + ", alumno=" + getNombreCompleto()
                + ", turno=" + turno + "}";
    }
  
    
}
