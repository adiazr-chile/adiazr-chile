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
public class VacacionesVO implements Serializable{

    private static final long serialVersionUID = 5698472295698776220L;

    private String username;
    private String rowKey;
    private String empresaId;
    private String empresaKey;
    private String deptoId;
    private String deptoNombre;
    private String rutEmpleado;
    private String nombreEmpleado;
    private String fechaCalculo;
    private String fechaEvento;
    
    private double diasAcumulados;
    private double diasProgresivos = 0;
    private double saldoDias       = 0;
    private double diasZonaExtrema;
    private double diasAdicionales;
        
    private String diasEspeciales  = "N";
    
    private String fechaInicioUltimasVacaciones;
    private String fechaFinUltimasVacaciones;
    private int cencoId = -1;
    private String cencoNombre;
    private String mensajeValidacion;
    private double diasEfectivos = 0;
    
    //nuevos atributos. 23-02-2020
    /**
    * @Deprecated
    */
    private int numActualCotizaciones;
    
    private String comentario;
    private String fechaInicioContrato;
    private String fechaDesvinculacion;
    private String esZonaExtrema;
    private String afpCode;
    private String afpName;
    private String fechaCertifVacacionesProgresivas;
    
    /**
     * 19-08-2021: agregados para implementar cambios a calculo de vacaciones progresivas
     */
    private String fechaBaseVp;
    private String mensajeVp;
    private int numCotizaciones;
    private String otraInstitucionEmisoraCertif;
    
    private double saldoDiasVBA;
    private double saldoDiasVP;

    public String getFechaDesvinculacion() {
        return fechaDesvinculacion;
    }

    public void setFechaDesvinculacion(String fechaDesvinculacion) {
        this.fechaDesvinculacion = fechaDesvinculacion;
    }

    public double getSaldoDiasVBA() {
        return saldoDiasVBA;
    }

    public void setSaldoDiasVBA(double saldoDiasVBA) {
        this.saldoDiasVBA = saldoDiasVBA;
    }

    public double getSaldoDiasVP() {
        return saldoDiasVP;
    }

    public void setSaldoDiasVP(double saldoDiasVP) {
        this.saldoDiasVP = saldoDiasVP;
    }
    
    public String getMensajeVp() {
        return mensajeVp;
    }

    public void setMensajeVp(String mensajeVp) {
        this.mensajeVp = mensajeVp;
    }

    public String getFechaBaseVp() {
        return fechaBaseVp;
    }

    public void setFechaBaseVp(String fechaBaseVp) {
        this.fechaBaseVp = fechaBaseVp;
    }

    public int getNumCotizaciones() {
        return numCotizaciones;
    }

    public void setNumCotizaciones(int numCotizaciones) {
        this.numCotizaciones = numCotizaciones;
    }

    public String getOtraInstitucionEmisoraCertif() {
        return otraInstitucionEmisoraCertif;
    }

    public void setOtraInstitucionEmisoraCertif(String otraInstitucionEmisoraCertif) {
        this.otraInstitucionEmisoraCertif = otraInstitucionEmisoraCertif;
    }
    
    
    
    public String getEmpresaKey() {
        return empresaKey;
    }

    public void setEmpresaKey(String empresaKey) {
        this.empresaKey = empresaKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(String fechaEvento) {
        this.fechaEvento = fechaEvento;
    }
    
    public String getAfpCode() {
        return afpCode;
    }

    public void setAfpCode(String afpCode) {
        this.afpCode = afpCode;
    }

    public String getAfpName() {
        return afpName;
    }

    public void setAfpName(String afpName) {
        this.afpName = afpName;
    }

    public String getFechaCertifVacacionesProgresivas() {
        return fechaCertifVacacionesProgresivas;
    }

    public void setFechaCertifVacacionesProgresivas(String fechaCertifVacacionesProgresivas) {
        this.fechaCertifVacacionesProgresivas = fechaCertifVacacionesProgresivas;
    }

    public String getEsZonaExtrema() {
        return esZonaExtrema;
    }

    public void setEsZonaExtrema(String esZonaExtrema) {
        this.esZonaExtrema = esZonaExtrema;
    }
    
    public String getFechaInicioContrato() {
        return fechaInicioContrato;
    }

    public void setFechaInicioContrato(String fechaInicioContrato) {
        this.fechaInicioContrato = fechaInicioContrato;
    }
    
    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public int getNumActualCotizaciones() {
        return numActualCotizaciones;
    }

    public void setNumActualCotizaciones(int numActualCotizaciones) {
        this.numActualCotizaciones = numActualCotizaciones;
    }

    public double getDiasEfectivos() {
        return diasEfectivos;
    }

    public void setDiasEfectivos(double diasEfectivos) {
        this.diasEfectivos = diasEfectivos;
    }
    
    public String getMensajeValidacion() {
        return mensajeValidacion;
    }

    public void setMensajeValidacion(String mensajeValidacion) {
        this.mensajeValidacion = mensajeValidacion;
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
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

    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public String getRutEmpleado() {
        return rutEmpleado;
    }

    public void setRutEmpleado(String rutEmpleado) {
        this.rutEmpleado = rutEmpleado;
    }

    public String getFechaCalculo() {
        return fechaCalculo;
    }

    public void setFechaCalculo(String fechaCalculo) {
        this.fechaCalculo = fechaCalculo;
    }

    public String getDiasEspeciales() {
        return diasEspeciales;
    }

    public void setDiasEspeciales(String diasEspeciales) {
        this.diasEspeciales = diasEspeciales;
    }

    public double getDiasAcumulados() {
        return diasAcumulados;
    }

    public void setDiasAcumulados(double diasAcumulados) {
        this.diasAcumulados = diasAcumulados;
    }

    public double getDiasProgresivos() {
        return diasProgresivos;
    }

    public void setDiasProgresivos(double diasProgresivos) {
        this.diasProgresivos = diasProgresivos;
    }

    public double getSaldoDias() {
        return saldoDias;
    }

    public void setSaldoDias(double saldoDias) {
        this.saldoDias = saldoDias;
    }

    public double getDiasZonaExtrema() {
        return diasZonaExtrema;
    }

    public void setDiasZonaExtrema(double diasZonaExtrema) {
        this.diasZonaExtrema = diasZonaExtrema;
    }

    public double getDiasAdicionales() {
        return diasAdicionales;
    }

    public void setDiasAdicionales(double diasAdicionales) {
        this.diasAdicionales = diasAdicionales;
    }

    public String getFechaInicioUltimasVacaciones() {
        return fechaInicioUltimasVacaciones;
    }

    public void setFechaInicioUltimasVacaciones(String fechaInicioUltimasVacaciones) {
        this.fechaInicioUltimasVacaciones = fechaInicioUltimasVacaciones;
    }

    public String getFechaFinUltimasVacaciones() {
        return fechaFinUltimasVacaciones;
    }

    public void setFechaFinUltimasVacaciones(String fechaFinUltimasVacaciones) {
        this.fechaFinUltimasVacaciones = fechaFinUltimasVacaciones;
    }

    @Override
    public String toString() {
        return "VacacionesVO{" + "empresaId=" + empresaId + ", deptoId=" + deptoId + ", deptoNombre=" + deptoNombre + ", rutEmpleado=" + rutEmpleado + ", nombreEmpleado=" + nombreEmpleado + ", fechaCalculo=" + fechaCalculo + ", diasAcumulados=" + diasAcumulados + ", diasProgresivos=" + diasProgresivos + ", saldoDias=" + saldoDias + ", fechaInicioUltimasVacaciones=" + fechaInicioUltimasVacaciones + ", fechaFinUltimasVacaciones=" + fechaFinUltimasVacaciones + ", cencoId=" + cencoId + ", cencoNombre=" + cencoNombre + '}';
    }

    
    
}
