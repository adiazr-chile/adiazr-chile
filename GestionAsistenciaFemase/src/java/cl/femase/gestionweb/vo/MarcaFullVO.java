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
public class MarcaFullVO implements Serializable{
    private static final long serialVersionUID = 6579472338799776220L;
   
    private String rowKey;
    private String fecha;
    private int codDia;
    private String empresaId;
    private String rutEmpleado;
    private String fechaHoraMarca;
    private int tipoMarca;
    private String codDispositivo;
    private int idTurnoEmpleado;
    private int idTurnoRotativo;
    private int idTurnoNormal;
    private String horaEntradaTurno;
    private String horaSalidaTurno;
    private int minsColacion;

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    
    
    public int getIdTurnoNormal() {
        return idTurnoNormal;
    }

    public void setIdTurnoNormal(int idTurnoNormal) {
        this.idTurnoNormal = idTurnoNormal;
    }

    
    
    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getCodDia() {
        return codDia;
    }

    public void setCodDia(int codDia) {
        this.codDia = codDia;
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

    public String getFechaHoraMarca() {
        return fechaHoraMarca;
    }

    public void setFechaHoraMarca(String fechaHoraMarca) {
        this.fechaHoraMarca = fechaHoraMarca;
    }

    public int getTipoMarca() {
        return tipoMarca;
    }

    public void setTipoMarca(int tipoMarca) {
        this.tipoMarca = tipoMarca;
    }

    public String getCodDispositivo() {
        return codDispositivo;
    }

    public void setCodDispositivo(String codDispositivo) {
        this.codDispositivo = codDispositivo;
    }

    public int getIdTurnoEmpleado() {
        return idTurnoEmpleado;
    }

    public void setIdTurnoEmpleado(int idTurnoEmpleado) {
        this.idTurnoEmpleado = idTurnoEmpleado;
    }

    public int getIdTurnoRotativo() {
        return idTurnoRotativo;
    }

    public void setIdTurnoRotativo(int idTurnoRotativo) {
        this.idTurnoRotativo = idTurnoRotativo;
    }

    public String getHoraEntradaTurno() {
        return horaEntradaTurno;
    }

    public void setHoraEntradaTurno(String horaEntradaTurno) {
        this.horaEntradaTurno = horaEntradaTurno;
    }

    public String getHoraSalidaTurno() {
        return horaSalidaTurno;
    }

    public void setHoraSalidaTurno(String horaSalidaTurno) {
        this.horaSalidaTurno = horaSalidaTurno;
    }

    public int getMinsColacion() {
        return minsColacion;
    }

    public void setMinsColacion(int minsColacion) {
        this.minsColacion = minsColacion;
    }
    
    
    
}
