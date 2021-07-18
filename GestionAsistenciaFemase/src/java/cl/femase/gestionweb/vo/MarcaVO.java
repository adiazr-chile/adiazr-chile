/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.vo;

import java.io.Serializable;
import java.util.StringTokenizer;

/**
 *
 * @author Alexander
 */
public class MarcaVO implements Serializable{
    private static final long serialVersionUID = 7899472338799776220L;
   
    private String fechaHoraKey;
    private String id;
    private String hashcode;
    private String codDispositivo;
    private String empresaKey;
    private String empresaCod;
    private String rutEmpleado;
    private String nombreEmpleado;
    private String codInternoEmpleado;
    private String rutKey;
    private int cencoId;
    private String fechaHora;
    private String fechaHoraStr;
    private String fechaHoraCalculos;
    private int tipoMarca;
    private String nombreTipoMarca;
    private int anioMarca;
    private int mesMarca;
    private int diaMarca;
    private String fecha;
    private String hora;
    private String minutos;
    private String segundos;
    private boolean insertOk;
    private String message;
    private String comentario;
    private String uploadMessageError;
    private String rowKey;
    private String fechaHoraActualizacion;
    private int codTipoMarcaManual;
    private String nombreTipoMarcaManual;
    private String soloHora;
    private String labelTurno;
    private String masInfo;
    private String latitud;
    private String longitud;
    private int correlativo;    
    private String googleMapUrl;

    public String getGoogleMapUrl() {
        return googleMapUrl;
    }

    public void setGoogleMapUrl(String googleMapUrl) {
        this.googleMapUrl = googleMapUrl;
    }
    
    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    public String getNombreTipoMarca() {
        return nombreTipoMarca;
    }

    public void setNombreTipoMarca(String nombreTipoMarca) {
        this.nombreTipoMarca = nombreTipoMarca;
    }

    public int getCorrelativo() {
        return correlativo;
    }

    public void setCorrelativo(int correlativo) {
        this.correlativo = correlativo;
    }
    
    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }
    
    public String getMasInfo() {
        return masInfo;
    }

    public void setMasInfo(String masInfo) {
        this.masInfo = masInfo;
    }
    
    public String getLabelTurno() {
        return labelTurno;
    }

    public void setLabelTurno(String labelTurno) {
        this.labelTurno = labelTurno;
    }
    
    public String getSoloHora() {
        return soloHora;
    }

    public void setSoloHora(String soloHora) {
        this.soloHora = soloHora;
    }

    
    
    public String getCodInternoEmpleado() {
        return codInternoEmpleado;
    }

    public void setCodInternoEmpleado(String codInternoEmpleado) {
        this.codInternoEmpleado = codInternoEmpleado;
    }

    
    public String getNombreTipoMarcaManual() {
        return nombreTipoMarcaManual;
    }

    public void setNombreTipoMarcaManual(String nombreTipoMarcaManual) {
        this.nombreTipoMarcaManual = nombreTipoMarcaManual;
    }
    
    public int getCodTipoMarcaManual() {
        return codTipoMarcaManual;
    }

    public void setCodTipoMarcaManual(int codTipoMarcaManual) {
        this.codTipoMarcaManual = codTipoMarcaManual;
    }
    
    public String getFechaHoraActualizacion() {
        return fechaHoraActualizacion;
    }

    public void setFechaHoraActualizacion(String fechaHoraActualizacion) {
        this.fechaHoraActualizacion = fechaHoraActualizacion;
    }

    public String getEmpresaKey() {
        return empresaKey;
    }

    public void setEmpresaKey(String empresaKey) {
        this.empresaKey = empresaKey;
    }

    public int getCencoId() {
        return cencoId;
    }

    public void setCencoId(int cencoId) {
        this.cencoId = cencoId;
    }
    
    public String getSegundos() {
        return segundos;
    }

    @Override
    public String toString() {
        return "MarcaVO{" + "fechaHoraKey=" + fechaHoraKey + ", id=" + id + ", hashcode=" + hashcode + ", codDispositivo=" + codDispositivo + ", empresaKey=" + empresaKey + ", empresaCod=" + empresaCod + ", rutEmpleado=" + rutEmpleado + ", rutKey=" + rutKey + ", cencoId=" + cencoId + ", fechaHora=" + fechaHora + ", fechaHoraStr=" + fechaHoraStr + ", fechaHoraCalculos=" + fechaHoraCalculos + ", tipoMarca=" + tipoMarca + ", anioMarca=" + anioMarca + ", mesMarca=" + mesMarca + ", diaMarca=" + diaMarca + ", fecha=" + fecha + ", hora=" + hora + ", minutos=" + minutos + ", segundos=" + segundos + ", insertOk=" + insertOk + ", message=" + message + ", comentario=" + comentario + ", uploadMessageError=" + uploadMessageError + ", rowKey=" + rowKey + '}';
    }

    public void setSegundos(String segundos) {
        this.segundos = segundos;
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public String getFechaHoraCalculos() {
        return fechaHoraCalculos;
    }

    public void setFechaHoraCalculos(String fechaHoraCalculos) {
        this.fechaHoraCalculos = fechaHoraCalculos;
    }
    
    public String getFechaHoraStr() {
        return fechaHoraStr;
    }

    public void setFechaHoraStr(String fechaHoraStr) {
        this.fechaHoraStr = fechaHoraStr;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    
    public String getUploadMessageError() {
        return uploadMessageError;
    }

    public void setUploadMessageError(String uploadMessageError) {
        this.uploadMessageError = uploadMessageError;
    }
    
    public String getRutKey() {
        return rutKey;
    }

    public void setRutKey(String rutKey) {
        this.rutKey = rutKey;
    }

    
    
    public String getFechaHoraKey() {
        return fechaHoraKey;
    }

    public void setFechaHoraKey(String fechaHoraKey) {
        this.fechaHoraKey = fechaHoraKey;
    }

    
    
    public String getMinutos() {
        return minutos;
    }

    public void setMinutos(String minutos) {
        this.minutos = minutos;
    }

    
    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     */
    public String getHashcode() {
        return hashcode;
    }

    public void setHashcode(String hashcode) {
        this.hashcode = hashcode;
    }

    /**
     *
     * @return
     */
    public int getAnioMarca() {
        return anioMarca;
    }

    public int getMesMarca() {
        return mesMarca;
    }

    public int getDiaMarca() {
        return diaMarca;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    
    public boolean isInsertOk() {
        return insertOk;
    }

    /**
     *
     * @param insertOk
     */
    public void setInsertOk(boolean insertOk) {
        this.insertOk = insertOk;
    }

    public String getRutEmpleado() {
        return rutEmpleado;
    }

    /**
     *
     * @param rutEmpleado
     */
    public void setRutEmpleado(String rutEmpleado) {
        this.rutEmpleado = rutEmpleado;
    }
    
    /**
     *
     * @return
     */
    public String getCodDispositivo() {
        return codDispositivo;
    }

    public void setCodDispositivo(String codDispositivo) {
        this.codDispositivo = codDispositivo;
    }

    public String getEmpresaCod() {
        return empresaCod;
    }

    public void setEmpresaCod(String empresaCod) {
        this.empresaCod = empresaCod;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
        StringTokenizer tokenInicial = new StringTokenizer(this.fechaHora, " ");
        String solofecha=tokenInicial.nextToken();
        
        StringTokenizer tokenfecha=new StringTokenizer(solofecha, "-");
        
        this.anioMarca = Integer.parseInt(tokenfecha.nextToken());
        this.mesMarca = Integer.parseInt(tokenfecha.nextToken());
        this.diaMarca = Integer.parseInt(tokenfecha.nextToken());
                
    }

    /**
     *
     * @return
     */
    public int getTipoMarca() {
        return tipoMarca;
    }

    public void setTipoMarca(int tipoMarca) {
        this.tipoMarca = tipoMarca;
    }

    
}
