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
public class FiltroBusquedaCRUDVO implements Serializable{
    private static final long serialVersionUID = 7895472374799776220L;
   
    private String nombre;
    private String empresaId;
    private String departamentoId;
    private String descripcion;
    private String nombreCenco;
    
    private int estado      = 1;
    private int regionId    = 1;
    private int cencoId     = -1;
    private int turnoId     = -1;
    private String runEmpleado;

    private String tipo;
    private String justificaHrs; 
    private String pagadaPorEmpleador;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getJustificaHrs() {
        return justificaHrs;
    }

    public void setJustificaHrs(String justificaHrs) {
        this.justificaHrs = justificaHrs;
    }

    public String getPagadaPorEmpleador() {
        return pagadaPorEmpleador;
    }

    public void setPagadaPorEmpleador(String pagadaPorEmpleador) {
        this.pagadaPorEmpleador = pagadaPorEmpleador;
    }
    
    
    
    public int getTurnoId() {
        return turnoId;
    }

    public void setTurnoId(int turnoId) {
        this.turnoId = turnoId;
    }

    public String getRunEmpleado() {
        return runEmpleado;
    }

    public void setRunEmpleado(String runEmpleado) {
        this.runEmpleado = runEmpleado;
    }
    
    
    
    public int getCencoId() {
        return cencoId;
    }

    public void setCencoId(int cencoId) {
        this.cencoId = cencoId;
    }
    
    
    
    public String getDepartamentoId() {
        return departamentoId;
    }

    public void setDepartamentoId(String departamentoId) {
        this.departamentoId = departamentoId;
    }

    public String getNombreCenco() {
        return nombreCenco;
    }

    public void setNombreCenco(String nombreCenco) {
        this.nombreCenco = nombreCenco;
    }

    
    
    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    
    
    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
    
    
    
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    
    
}
