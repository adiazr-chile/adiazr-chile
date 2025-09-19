/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.vo;

import java.io.Serializable;

/**
 *
 * @author Alexander
 */
public class FiltroBusquedaEmpleadosVO implements Serializable{
    private static final long serialVersionUID = 7895472374799776220L;
   
    private String runEmpleado;
    private String nombre;
    private String empresaId;
    private int cencoId     = -1;
    private int turnoId     = -1;
    private int cargoId     = -1;
    private String desde;
    private String hasta;
    private String tipoReporte;
    private String formatoSalida;
    private String periodoPredefinido;

    public String getPeriodoPredefinido() {
        return periodoPredefinido;
    }

    public void setPeriodoPredefinido(String periodoPredefinido) {
        this.periodoPredefinido = periodoPredefinido;
    }
    
    public String getRunEmpleado() {
        return runEmpleado;
    }

    public void setRunEmpleado(String runEmpleado) {
        this.runEmpleado = runEmpleado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public int getCencoId() {
        return cencoId;
    }

    public void setCencoId(int cencoId) {
        this.cencoId = cencoId;
    }

    public int getTurnoId() {
        return turnoId;
    }

    public void setTurnoId(int turnoId) {
        this.turnoId = turnoId;
    }

    public int getCargoId() {
        return cargoId;
    }

    public void setCargoId(int cargoId) {
        this.cargoId = cargoId;
    }

    public String getDesde() {
        return desde;
    }

    public void setDesde(String desde) {
        this.desde = desde;
    }

    public String getHasta() {
        return hasta;
    }

    public void setHasta(String hasta) {
        this.hasta = hasta;
    }

    public String getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(String tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public String getFormatoSalida() {
        return formatoSalida;
    }

    public void setFormatoSalida(String formatoSalida) {
        this.formatoSalida = formatoSalida;
    }

    @Override
    public String toString() {
        return "FiltroBusquedaEmpleadosVO{" + "runEmpleado=" + runEmpleado + ", nombre=" + nombre + ", empresaId=" + empresaId + ", cencoId=" + cencoId + ", turnoId=" + turnoId + ", cargoId=" + cargoId + ", desde=" + desde + ", hasta=" + hasta + ", tipoReporte=" + tipoReporte + ", formatoSalida=" + formatoSalida + ", periodoPredefinido=" + periodoPredefinido + '}';
    }
    
    
    
}
