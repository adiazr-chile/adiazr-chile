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
public class DetalleAusenciaVO implements Serializable{

    private static final long serialVersionUID = 6737742295698759620L;

    private int correlativo;
    private String rutEmpleado;
    private String empresaId;
    private String empresaNombre;
    private String nombreEmpleado;
    private String cencoNombre;
    private String deptoNombre;
    private Date fechaIngreso;
    private String fechaIngresoAsStr;
    private int idAusencia;
    private String nombreAusencia;
    private Date fechaInicio;
    private String fechaInicioAsStr;
    private String fechainicio;
    private String fechafin;
    private String horaInicioFullAsStr;
    private String soloHoraInicio;
    private String soloMinsInicio;
    private Date fechaFin;
    private String fechaFinAsStr;
    private String horaFinFullAsStr;
    private String soloHoraFin;
    private String soloMinsFin;
    
    private String rutAutorizador;
    private String cencoAutorizador;
    private String nombreAutorizador;
    private String nomDeptoAutorizador;
    private String nomCencoAutorizador;
    
    private String ausenciaAutorizada;
    private String fechaHoraActualizacion;
    /** para aquellas ausencias que requieren*/
    private String permiteHora="N";
    private int tipoAusencia=1;
    
    private int diasAcumulados;
    private int diasProgresivos;
    private int saldoDias;
    private int diasEfectivosVacaciones;
    private String diasEspeciales  = "N";
    private String esZonaExtrema  = "N";
    private String mensajeError;
    
    private int saldoDiasVacacionesAsignadas;
    private int diasAcumuladosVacacionesAsignadas;        
    
    public DetalleAusenciaVO() {
        
    }

    public int getSaldoDiasVacacionesAsignadas() {
        return saldoDiasVacacionesAsignadas;
    }

    public void setSaldoDiasVacacionesAsignadas(int saldoDiasVacacionesAsignadas) {
        this.saldoDiasVacacionesAsignadas = saldoDiasVacacionesAsignadas;
    }

    public int getDiasAcumuladosVacacionesAsignadas() {
        return diasAcumuladosVacacionesAsignadas;
    }

    public void setDiasAcumuladosVacacionesAsignadas(int diasAcumuladosVacacionesAsignadas) {
        this.diasAcumuladosVacacionesAsignadas = diasAcumuladosVacacionesAsignadas;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }

    public String getEsZonaExtrema() {
        return esZonaExtrema;
    }

    public void setEsZonaExtrema(String esZonaExtrema) {
        this.esZonaExtrema = esZonaExtrema;
    }

    public String getDiasEspeciales() {
        return diasEspeciales;
    }

    public void setDiasEspeciales(String diasEspeciales) {
        this.diasEspeciales = diasEspeciales;
    }

    public String getFechainicio() {
        return fechainicio;
    }

    public void setFechainicio(String fechainicio) {
        this.fechainicio = fechainicio;
    }

    public String getFechafin() {
        return fechafin;
    }

    public void setFechafin(String fechafin) {
        this.fechafin = fechafin;
    }

    public int getDiasEfectivosVacaciones() {
        return diasEfectivosVacaciones;
    }

    public void setDiasEfectivosVacaciones(int diasEfectivosVacaciones) {
        this.diasEfectivosVacaciones = diasEfectivosVacaciones;
    }

    public String getCencoAutorizador() {
        return cencoAutorizador;
    }

    public void setCencoAutorizador(String cencoAutorizador) {
        this.cencoAutorizador = cencoAutorizador;
    }

    public String getEmpresaNombre() {
        return empresaNombre;
    }

    public void setEmpresaNombre(String empresaNombre) {
        this.empresaNombre = empresaNombre;
    }

    public String getCencoNombre() {
        return cencoNombre;
    }

    public void setCencoNombre(String cencoNombre) {
        this.cencoNombre = cencoNombre;
    }

    public String getDeptoNombre() {
        return deptoNombre;
    }

    public void setDeptoNombre(String deptoNombre) {
        this.deptoNombre = deptoNombre;
    }

    
    
    public int getDiasAcumulados() {
        return diasAcumulados;
    }

    public void setDiasAcumulados(int diasAcumulados) {
        this.diasAcumulados = diasAcumulados;
    }

    public int getDiasProgresivos() {
        return diasProgresivos;
    }

    public void setDiasProgresivos(int diasProgresivos) {
        this.diasProgresivos = diasProgresivos;
    }

    public int getSaldoDias() {
        return saldoDias;
    }

    public void setSaldoDias(int saldoDias) {
        this.saldoDias = saldoDias;
    }

    
    
    public int getTipoAusencia() {
        return tipoAusencia;
    }

    public void setTipoAusencia(int tipoAusencia) {
        this.tipoAusencia = tipoAusencia;
    }

    
    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public int getCorrelativo() {
        return correlativo;
    }

    public void setCorrelativo(int _correlativo) {
        this.correlativo = _correlativo;
    }
    
    public String getNomDeptoAutorizador() {
        return nomDeptoAutorizador;
    }

    /**
     *
     * @param nomDeptoAutorizador
     */
    public void setNomDeptoAutorizador(String nomDeptoAutorizador) {
        this.nomDeptoAutorizador = nomDeptoAutorizador;
    }

    /**
     *
     * @return
     */
    public String getNomCencoAutorizador() {
        return nomCencoAutorizador;
    }

    public void setNomCencoAutorizador(String nomCencoAutorizador) {
        this.nomCencoAutorizador = nomCencoAutorizador;
    }

    
    public String getNombreAusencia() {
        return nombreAusencia;
    }

    public void setNombreAusencia(String nombreAusencia) {
        this.nombreAusencia = nombreAusencia;
    }

    /**
     *
     * @return
     */
    public String getPermiteHora() {
        return permiteHora;
    }

    public void setPermiteHora(String permiteHora) {
        this.permiteHora = permiteHora;
    }

    public String getHoraInicioFullAsStr() {
        return horaInicioFullAsStr;
    }

    public void setHoraInicioFullAsStr(String horaInicioFullAsStr) {
        this.horaInicioFullAsStr = horaInicioFullAsStr;
    }

    public String getSoloHoraInicio() {
        
        return soloHoraInicio;
    }

    public void setSoloHoraInicio(String soloHoraInicio) {
        this.soloHoraInicio = soloHoraInicio;
    }

    public String getSoloMinsInicio() {
        return soloMinsInicio;
    }

    public void setSoloMinsInicio(String soloMinsInicio) {
        this.soloMinsInicio = soloMinsInicio;
    }

    public String getHoraFinFullAsStr() {
        return horaFinFullAsStr;
    }

    public void setHoraFinFullAsStr(String horaFinFullAsStr) {
        this.horaFinFullAsStr = horaFinFullAsStr;
    }

    public String getSoloHoraFin() {
        return soloHoraFin;
    }

    public void setSoloHoraFin(String soloHoraFin) {
        this.soloHoraFin = soloHoraFin;
    }

    public String getSoloMinsFin() {
        return soloMinsFin;
    }

    public void setSoloMinsFin(String soloMinsFin) {
        this.soloMinsFin = soloMinsFin;
    }

    public String getFechaHoraActualizacion() {
        return fechaHoraActualizacion;
    }

    public void setFechaHoraActualizacion(String fechaHoraActualizacion) {
        this.fechaHoraActualizacion = fechaHoraActualizacion;
    }

    public String getRutEmpleado() {
        return rutEmpleado;
    }

    public void setRutEmpleado(String rutEmpleado) {
        this.rutEmpleado = rutEmpleado;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    /**
     *
     * @return
     */
    public String getFechaIngresoAsStr() {
        return fechaIngresoAsStr;
    }

    public void setFechaIngresoAsStr(String fechaIngresoAsStr) {
        this.fechaIngresoAsStr = fechaIngresoAsStr;
    }

    public int getIdAusencia() {
        return idAusencia;
    }

    public void setIdAusencia(int idAusencia) {
        this.idAusencia = idAusencia;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /**
     *
     * @return
     */
    public String getFechaInicioAsStr() {
        return fechaInicioAsStr;
    }

    /**
     *
     * @param fechaInicioAsStr
     */
    public void setFechaInicioAsStr(String fechaInicioAsStr) {
        this.fechaInicioAsStr = fechaInicioAsStr;
    }

    /**
     *
     * @return
     */
    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getFechaFinAsStr() {
        return fechaFinAsStr;
    }

    public void setFechaFinAsStr(String fechaFinAsStr) {
        this.fechaFinAsStr = fechaFinAsStr;
    }

    public String getRutAutorizador() {
        return rutAutorizador;
    }

    public void setRutAutorizador(String rutAutorizador) {
        this.rutAutorizador = rutAutorizador;
    }

    public String getNombreAutorizador() {
        return nombreAutorizador;
    }

    public void setNombreAutorizador(String nombreAutorizador) {
        this.nombreAutorizador = nombreAutorizador;
    }

    public String getAusenciaAutorizada() {
        return ausenciaAutorizada;
    }

    public void setAusenciaAutorizada(String ausenciaAutorizada) {
        this.ausenciaAutorizada = ausenciaAutorizada;
    }
    
    
    
}
