/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.vo;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author Alexander
 */
public class CentroCostoVO implements Serializable{

    private static final long serialVersionUID = 7637542295698779620L;

    private int id=-1;
    private String nombre;
    private int estado;

    private String direccion;
    private int comunaId;
    private int regionId;
    private String telefonos;
    private String email;
    private String uploadMessageError;
    private String deptoId;
    private String deptoNombre;
    private String empresaId;
    private String empresaNombre;
    private HashMap<String, DispositivoVO> dispositivos;
    private String dispositivosAsString;
    private String label;
    private String emailNotificacion;
    private String zonaExtrema="N";
        
    public CentroCostoVO() {
    }

//    public void setDispositivosAsString(String dispositivosAsString) {
//        for (String key : this.dispositivos.keySet()) {
//            DispositivoVO aux1 = this.dispositivos.get(key);
//            dispositivosAsString += aux1.getId() + "," + aux1.getNombreTipo();
//        }
//        System.out.println(WEB_NAME+"cl.femase.gestionweb.vo.CentroCostoVO.getDispositivosAsString(): dispositivosAsString= "+dispositivosAsString);
//        this.dispositivosAsString = dispositivosAsString;
//    }

    public String getZonaExtrema() {
        return zonaExtrema;
    }

    public void setZonaExtrema(String zonaExtrema) {
        this.zonaExtrema = zonaExtrema;
    }

    public String getEmailNotificacion() {
        return emailNotificacion;
    }

    public void setEmailNotificacion(String emailNotificacion) {
        this.emailNotificacion = emailNotificacion;
    }
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    
    
    public String getDispositivosAsString() {
        return dispositivosAsString;
    }

    public HashMap<String, DispositivoVO> getDispositivos() {
        return dispositivos;
    }

    public void setDispositivos(HashMap<String, DispositivoVO> dispositivos) {
        this.dispositivos = dispositivos;
        String aux2="";
        for (String key : this.dispositivos.keySet()) {
            DispositivoVO aux1 = this.dispositivos.get(key);
            aux2 += aux1.getId()+",";
        }
        
        if (aux2.compareTo("") != 0){
            aux2 = aux2.substring(0, aux2.length()-1);
        }
    
        this.dispositivosAsString = aux2;
    }

    public String getDeptoNombre() {
        return deptoNombre;
    }

    public void setDeptoNombre(String deptoNombre) {
        this.deptoNombre = deptoNombre;
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

    
    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }
    
    
    public String getDeptoId() {
        return deptoId;
    }

    public CentroCostoVO(int id, String nombre, int estado, String _deptoId) {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
        this.deptoId = _deptoId;
    }
    
    /**
     *
     * @param deptoId
     */
    public void setDeptoId(String deptoId) {
        this.deptoId = deptoId;
    }

    public String getUploadMessageError() {
        return uploadMessageError;
    }

    public void setUploadMessageError(String uploadMessageError) {
        this.uploadMessageError = uploadMessageError;
    }

    
    
    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getComunaId() {
        return comunaId;
    }

    public void setComunaId(int comunaId) {
        this.comunaId = comunaId;
    }

    public String getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(String telefonos) {
        this.telefonos = telefonos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    
}
