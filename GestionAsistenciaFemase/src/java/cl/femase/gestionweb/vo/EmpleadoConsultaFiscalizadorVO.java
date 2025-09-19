/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.vo;

import java.io.Serializable;

/**
*
* @author Alexander
*/
public class EmpleadoConsultaFiscalizadorVO implements Serializable{

    private static final long serialVersionUID = 7599472295765776220L;
    
    private String empresaId;
    private String empresaNombre;
    private String run;
    private String nombre;
    private String deptoId;
    private String deptoNombre;
    private int cencoId;
    private String cencoNombre;
    private String fechaInicioContrato;
    private int estadoValue;
    private String estadoLabel;
    private int idTurno=-1;
    private int idCargo=-1;
    private String nombreCargo;
    private String nombreTurno;
    private String codInterno;
    private String tieneAsistencia;

    public String getTieneAsistencia() {
        return tieneAsistencia;
    }

    public void setTieneAsistencia(String tieneAsistencia) {
        this.tieneAsistencia = tieneAsistencia;
    }

    public int getEstadoValue() {
        return estadoValue;
    }

    public void setEstadoValue(int estadoValue) {
        this.estadoValue = estadoValue;
    }

    public String getEstadoLabel() {
        return estadoLabel;
    }

    public void setEstadoLabel(String estadoLabel) {
        this.estadoLabel = estadoLabel;
    }

    
    
    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public String getEmpresaNombre() {
        return empresaNombre;
    }

    public void setEmpresaNombre(String empresaNombre) {
        this.empresaNombre = empresaNombre;
    }

    public String getRun() {
        return run;
    }

    public void setRun(String run) {
        this.run = run;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDeptoId() {
        return deptoId;
    }

    public void setDeptoId(String deptoId) {
        this.deptoId = deptoId;
    }

    public String getDeptoNombre() {
        return deptoNombre;
    }

    public void setDeptoNombre(String deptoNombre) {
        this.deptoNombre = deptoNombre;
    }

    public int getCencoId() {
        return cencoId;
    }

    public void setCencoId(int cencoId) {
        this.cencoId = cencoId;
    }

    public String getCencoNombre() {
        return cencoNombre;
    }

    public void setCencoNombre(String cencoNombre) {
        this.cencoNombre = cencoNombre;
    }

    public String getFechaInicioContrato() {
        return fechaInicioContrato;
    }

    public void setFechaInicioContrato(String fechaInicioContrato) {
        this.fechaInicioContrato = fechaInicioContrato;
    }

    public int getIdTurno() {
        return idTurno;
    }

    public void setIdTurno(int idTurno) {
        this.idTurno = idTurno;
    }

    public int getIdCargo() {
        return idCargo;
    }

    public void setIdCargo(int idCargo) {
        this.idCargo = idCargo;
    }

    public String getNombreCargo() {
        return nombreCargo;
    }

    public void setNombreCargo(String nombreCargo) {
        this.nombreCargo = nombreCargo;
    }

    public String getNombreTurno() {
        return nombreTurno;
    }

    public void setNombreTurno(String nombreTurno) {
        this.nombreTurno = nombreTurno;
    }

    public String getCodInterno() {
        return codInterno;
    }

    public void setCodInterno(String codInterno) {
        this.codInterno = codInterno;
    }

    @Override
    public String toString() {
        return "EmpleadoConsultaFiscalizadorVO{" + "empresaId=" + empresaId + ", empresaNombre=" + empresaNombre + ", run=" + run + ", nombre=" + nombre + ", deptoId=" + deptoId + ", deptoNombre=" + deptoNombre + ", cencoId=" + cencoId + ", cencoNombre=" + cencoNombre + ", fechaInicioContrato=" + fechaInicioContrato + ", estadoValue=" + estadoValue + ", estadoLabel=" + estadoLabel + ", idTurno=" + idTurno + ", idCargo=" + idCargo + ", nombreCargo=" + nombreCargo + ", nombreTurno=" + nombreTurno + ", codInterno=" + codInterno + '}';
    }

    


}
