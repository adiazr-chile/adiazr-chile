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
public class MarcacionVirtualVO implements Serializable{

    private static final long serialVersionUID = 5698454195698776220L;

    private String rutEmpleado;
    private String nombreEmpleado;
    private String empresaId;
    private String empresaNombre;
    private String rowKey;
    private String username;
    private String fechaAsignacion;
    private String comentario;
    private String admiteMarcaVirtual;//'S' o 'N'
    private String deptoId;
    private String deptoNombre;
    private int cencoId;
    private String cencoNombre;

    private String fecha1;
    private String fecha2;
    private String fecha3;
    private String fecha4;
    private String fecha5;
    private String fecha6;
    private String fecha7;
    private boolean insertaEvento=true;
    private String registrarUbicacion;//'S' o 'N'
    private String marcaMovil;//'S' o 'N'
    private String movilId;

    public String getMovilId() {
        return movilId;
    }

    public void setMovilId(String movilId) {
        this.movilId = movilId;
    }

    public String getMarcaMovil() {
        return marcaMovil;
    }

    public void setMarcaMovil(String marcaMovil) {
        this.marcaMovil = marcaMovil;
    }

    
    public String getRegistrarUbicacion() {
        return registrarUbicacion;
    }

    public void setRegistrarUbicacion(String registrarUbicacion) {
        this.registrarUbicacion = registrarUbicacion;
    }

    public boolean isInsertaEvento() {
        return insertaEvento;
    }

    public void setInsertaEvento(boolean insertaEvento) {
        this.insertaEvento = insertaEvento;
    }
    
    public String getEmpresaNombre() {
        return empresaNombre;
    }

    public void setEmpresaNombre(String empresaNombre) {
        this.empresaNombre = empresaNombre;
    }

    public String getDeptoNombre() {
        return deptoNombre;
    }

    public void setDeptoNombre(String deptoNombre) {
        this.deptoNombre = deptoNombre;
    }

    public String getCencoNombre() {
        return cencoNombre;
    }

    public void setCencoNombre(String cencoNombre) {
        this.cencoNombre = cencoNombre;
    }

    public String getFecha1() {
        return fecha1;
    }

    public void setFecha1(String fecha1) {
        if (fecha1 == null) fecha1 = "";
        this.fecha1 = fecha1;
    }

    public String getFecha2() {
        return fecha2;
    }

    public void setFecha2(String fecha2) {
        if (fecha2 == null) fecha2 = "";
        this.fecha2 = fecha2;
    }

    public String getFecha3() {
        return fecha3;
    }

    public void setFecha3(String fecha3) {
        if (fecha3 == null) fecha3 = "";
        this.fecha3 = fecha3;
    }

    public String getFecha4() {
        return fecha4;
    }

    public void setFecha4(String fecha4) {
        if (fecha4 == null) fecha4 = "";
        this.fecha4 = fecha4;
    }

    public String getFecha5() {
        return fecha5;
    }

    public void setFecha5(String fecha5) {
        if (fecha5 == null) fecha5 = "";
        this.fecha5 = fecha5;
    }

    public String getFecha6() {
        return fecha6;
    }

    public void setFecha6(String fecha6) {
        if (fecha6 == null) fecha6 = "";
        this.fecha6 = fecha6;
    }

    public String getFecha7() {
        return fecha7;
    }

    public void setFecha7(String fecha7) {
        if (fecha7 == null) fecha7 = "";
        this.fecha7 = fecha7;
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

    public int getCencoId() {
        return cencoId;
    }

    public void setCencoId(int cencoId) {
        this.cencoId = cencoId;
    }

    public String getAdmiteMarcaVirtual() {
        return admiteMarcaVirtual;
    }

    public void setAdmiteMarcaVirtual(String admiteMarcaVirtual) {
        this.admiteMarcaVirtual = admiteMarcaVirtual;
    }
    
    public String getRutEmpleado() {
        return rutEmpleado;
    }

    public void setRutEmpleado(String rutEmpleado) {
        this.rutEmpleado = rutEmpleado;
    }

    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(String fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    @Override
    public String toString() {
        return "MarcacionVirtualVO{" + "rutEmpleado=" + rutEmpleado + ", nombreEmpleado=" + nombreEmpleado + ", empresaId=" + empresaId + ", empresaNombre=" + empresaNombre + ", rowKey=" + rowKey + ", username=" + username + ", fechaAsignacion=" + fechaAsignacion + ", comentario=" + comentario + ", admiteMarcaVirtual=" + admiteMarcaVirtual + ", deptoId=" + deptoId + ", deptoNombre=" + deptoNombre + ", cencoId=" + cencoId + ", cencoNombre=" + cencoNombre + ", fecha1=" + fecha1 + ", fecha2=" + fecha2 + ", fecha3=" + fecha3 + ", fecha4=" + fecha4 + ", fecha5=" + fecha5 + ", fecha6=" + fecha6 + ", fecha7=" + fecha7 + ", registrarUbicacion=" + registrarUbicacion + ", marcaMovil=" + marcaMovil + ", movilId=" + movilId + '}';
    }
    
    
}
