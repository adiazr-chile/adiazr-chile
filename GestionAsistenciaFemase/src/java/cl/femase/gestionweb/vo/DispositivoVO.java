/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.vo;

import java.io.Serializable;

import java.util.Date;
import java.util.LinkedHashMap;
/**
 *
 * @author Alexander
 */
public class DispositivoVO implements Serializable{

    private static final long serialVersionUID = 7599441285698776220L;

    private String id;
    private int idTipo;
    
    private int estado;
    private Date fechaIngreso;
    private String fechaIngresoAsStr;
    private String fechaHoraUltimaActualizacionAsStr;
    private String ubicacion;
    private String nombreEstado;
    private String nombreTipo;
    private String checked="";
    
    /**
     * Agregados el 12-05-2017
     */
    private String modelo="";
    private String fabricante="";
    private String firmwareVersion="";
    private String ip="";
    private String gateway="";
    private String dns="";
    
    private int idComuna;
    private String direccion="";
    private String labelComuna = "";
    
    private LinkedHashMap<String,DispositivoEmpresaVO> empresas; 
    private LinkedHashMap<String,DispositivoDepartamentoVO> departamentos;
    private LinkedHashMap<String,DispositivoCentroCostoVO> cencos;

    public String getLabelComuna() {
        return labelComuna;
    }

    public void setLabelComuna(String labelComuna) {
        this.labelComuna = labelComuna;
    }

    
    
    public int getIdComuna() {
        return idComuna;
    }

    public void setIdComuna(int idComuna) {
        this.idComuna = idComuna;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    
    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }

    
    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    /**
     *
     * @param firmwareVersion
     */
    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getDns() {
        return dns;
    }

    public void setDns(String dns) {
        this.dns = dns;
    }

    public LinkedHashMap<String, DispositivoEmpresaVO> getEmpresas() {
        return empresas;
    }

    public void setEmpresas(LinkedHashMap<String, DispositivoEmpresaVO> empresas) {
        this.empresas = empresas;
    }

    public LinkedHashMap<String, DispositivoDepartamentoVO> getDepartamentos() {
        return departamentos;
    }

    public void setDepartamentos(LinkedHashMap<String, DispositivoDepartamentoVO> departamentos) {
        this.departamentos = departamentos;
    }

    public LinkedHashMap<String, DispositivoCentroCostoVO> getCencos() {
        return cencos;
    }

    public void setCencos(LinkedHashMap<String, DispositivoCentroCostoVO> cencos) {
        this.cencos = cencos;
    }
    
    
    
    public String getFechaHoraUltimaActualizacionAsStr() {
        return fechaHoraUltimaActualizacionAsStr;
    }

    public void setFechaHoraUltimaActualizacionAsStr(String fechaHoraUltimaActualizacionAsStr) {
        this.fechaHoraUltimaActualizacionAsStr = fechaHoraUltimaActualizacionAsStr;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    public String getNombreEstado() {
        return nombreEstado;
    }

    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }
    
    
    
    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(int idTipo) {
        this.idTipo = idTipo;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getFechaIngresoAsStr() {
        return fechaIngresoAsStr;
    }

    public void setFechaIngresoAsStr(String fechaIngresoAsStr) {
        this.fechaIngresoAsStr = fechaIngresoAsStr;
    }

    
}
