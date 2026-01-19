/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cl.femase.gestionweb.vo;

/**
 *
 * @author aledi
 */
public class VacacionProgJsonVO {
    String empresaId;
    String rutEmpleado;
    String status;
    String message;
    Integer mesesTotales;
    Double aniosTotales;
    Double aniosEnEmpresa;
    Integer diasProgresivosPrevios;
    Integer diasProgresivosNuevos;
    Integer affectedRows;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getMesesTotales() {
        return mesesTotales;
    }

    public void setMesesTotales(Integer mesesTotales) {
        this.mesesTotales = mesesTotales;
    }

    public Double getAniosTotales() {
        return aniosTotales;
    }

    public void setAniosTotales(Double aniosTotales) {
        this.aniosTotales = aniosTotales;
    }

    public Double getAniosEnEmpresa() {
        return aniosEnEmpresa;
    }

    public void setAniosEnEmpresa(Double aniosEnEmpresa) {
        this.aniosEnEmpresa = aniosEnEmpresa;
    }

    public Integer getDiasProgresivosPrevios() {
        return diasProgresivosPrevios;
    }

    public void setDiasProgresivosPrevios(Integer diasProgresivosPrevios) {
        this.diasProgresivosPrevios = diasProgresivosPrevios;
    }

    public Integer getDiasProgresivosNuevos() {
        return diasProgresivosNuevos;
    }

    public void setDiasProgresivosNuevos(Integer diasProgresivosNuevos) {
        this.diasProgresivosNuevos = diasProgresivosNuevos;
    }

    public Integer getAffectedRows() {
        return affectedRows;
    }

    public void setAffectedRows(Integer affectedRows) {
        this.affectedRows = affectedRows;
    }
    
    
}
