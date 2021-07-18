/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.vo;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Alexander
 */
public class DetalleTurnoVO implements Serializable{

    private static final long serialVersionUID = 6637742295633779620L;

    private int idTurno;
    private String nombreTurno;
    private int codDia;
    private String labelDia;
    private String labelCortoDia;
    private String horaEntrada;
    private String horaSalida;
    private Date fechaModificacion;
    private String fechaModificacionAsStr;
    private int totalHoras;
    private int holgura;
    private int minutosColacion;
    private String nocturno;
    
    public DetalleTurnoVO() {
        
    }

    public String getNocturno() {
        return nocturno;
    }

    public void setNocturno(String nocturno) {
        this.nocturno = nocturno;
    }

    public int getMinutosColacion() {
        return minutosColacion;
    }

    public void setMinutosColacion(int minutosColacion) {
        this.minutosColacion = minutosColacion;
    }

    
    public int getHolgura() {
        return holgura;
    }

    public void setHolgura(int holgura) {
        this.holgura = holgura;
    }

    public int getTotalHoras() {
        return totalHoras;
    }

    public void setTotalHoras(int totalHoras) {
        this.totalHoras = totalHoras;
    }

    public String getNombreTurno() {
        return nombreTurno;
    }

    public void setNombreTurno(String nombreTurno) {
        this.nombreTurno = nombreTurno;
    }

    public Date getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public String getFechaModificacionAsStr() {
        return fechaModificacionAsStr;
    }

    public void setFechaModificacionAsStr(String fechaModificacionAsStr) {
        this.fechaModificacionAsStr = fechaModificacionAsStr;
    }

    
    
    public String getLabelDia() {
        return labelDia;
    }

    public void setLabelDia(String labelDia) {
        this.labelDia = labelDia;
    }

    public String getLabelCortoDia() {
        return labelCortoDia;
    }

    public void setLabelCortoDia(String labelCortoDia) {
        this.labelCortoDia = labelCortoDia;
    }

    public int getIdTurno() {
        return idTurno;
    }

    public void setIdTurno(int idTurno) {
        this.idTurno = idTurno;
    }

    public int getCodDia() {
        return codDia;
    }

    public void setCodDia(int codDia) {
        this.codDia = codDia;
    }

    public String getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(String horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public String getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(String horaSalida) {
        this.horaSalida = horaSalida;
    }

    
    
}
