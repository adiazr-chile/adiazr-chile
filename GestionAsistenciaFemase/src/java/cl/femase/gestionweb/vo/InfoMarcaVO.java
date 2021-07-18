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
public class InfoMarcaVO implements Serializable{
    private static final long serialVersionUID = 6579472338799776220L;
   
    private String rowKey;
    private String fecha;
    private String fechaLabel;
    private int codDia;
    private String empresaId;
    private String rutEmpleado;
    private String fechaHoraMarca;
    private String horaMarca;
    private String fechaHoraMarcaLabel;
    private int tipoMarca;
    private String codDispositivo;
    private int idTurnoEmpleado;
    private int idTurnoAsignado;
    private String labelTurno;
    private String horaEntradaTurno;
    private String horaSalidaTurno;
    private int minsColacion;
    private String turnoNocturno;
    private int numMarcas =0;
    private boolean tipoMarcaDuplicado;
    private String labelCalendario;
    
    private String fieldFecha;
    private String fieldEvento;
    
    private String hora;
    private String minutos;
    private String segundos;
    private String id;
    private String hashcode;
    private String comentario;
    private String fechaHoraActualizacion;
    private int codTipoMarcaManual;
    private String nombreTipoMarcaManual;
    
    private String ausenciaNombre;
    private String ausenciaPorHora;
    private String fechaInicioAusencia;
    private String fechaFinAusencia;
    private int tipoEvento;
    private String masInfo;
    private String horaInicioAusencia;
    private String horaFinAusencia;
    private int correlativo;

    public int getCorrelativo() {
        return correlativo;
    }

    public void setCorrelativo(int correlativo) {
        this.correlativo = correlativo;
    }
    
    public String getHoraInicioAusencia() {
        return horaInicioAusencia;
    }

    public void setHoraInicioAusencia(String horaInicioAusencia) {
        this.horaInicioAusencia = horaInicioAusencia;
    }

    public String getHoraFinAusencia() {
        return horaFinAusencia;
    }

    public void setHoraFinAusencia(String horaFinAusencia) {
        this.horaFinAusencia = horaFinAusencia;
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
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

    public int getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(int tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getFechaInicioAusencia() {
        return fechaInicioAusencia;
    }

    public void setFechaInicioAusencia(String fechaInicioAusencia) {
        this.fechaInicioAusencia = fechaInicioAusencia;
    }

    public String getFechaFinAusencia() {
        return fechaFinAusencia;
    }

    public void setFechaFinAusencia(String fechaFinAusencia) {
        this.fechaFinAusencia = fechaFinAusencia;
    }

    
    
    public String getAusenciaNombre() {
        return ausenciaNombre;
    }

    public void setAusenciaNombre(String ausenciaNombre) {
        this.ausenciaNombre = ausenciaNombre;
    }

    public String getAusenciaPorHora() {
        return ausenciaPorHora;
    }

    public void setAusenciaPorHora(String ausenciaPorHora) {
        this.ausenciaPorHora = ausenciaPorHora;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getMinutos() {
        return minutos;
    }

    public void setMinutos(String minutos) {
        this.minutos = minutos;
    }

    public String getSegundos() {
        return segundos;
    }

    public void setSegundos(String segundos) {
        this.segundos = segundos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHashcode() {
        return hashcode;
    }

    public void setHashcode(String hashcode) {
        this.hashcode = hashcode;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getFechaHoraActualizacion() {
        return fechaHoraActualizacion;
    }

    public void setFechaHoraActualizacion(String fechaHoraActualizacion) {
        this.fechaHoraActualizacion = fechaHoraActualizacion;
    }

    public int getCodTipoMarcaManual() {
        return codTipoMarcaManual;
    }

    public void setCodTipoMarcaManual(int codTipoMarcaManual) {
        this.codTipoMarcaManual = codTipoMarcaManual;
    }

    public String getNombreTipoMarcaManual() {
        return nombreTipoMarcaManual;
    }

    public void setNombreTipoMarcaManual(String nombreTipoMarcaManual) {
        this.nombreTipoMarcaManual = nombreTipoMarcaManual;
    }

    
    
    public String getHoraMarca() {
        return horaMarca;
    }

    public void setHoraMarca(String horaMarca) {
        this.horaMarca = horaMarca;
    }

    
    
    public String getFechaHoraMarcaLabel() {
        return fechaHoraMarcaLabel;
    }

    public void setFechaHoraMarcaLabel(String fechaHoraMarcaLabel) {
        this.fechaHoraMarcaLabel = fechaHoraMarcaLabel;
    }

    
    
    public String getFieldFecha() {
        return fieldFecha;
    }

    public void setFieldFecha(String fieldFecha) {
        this.fieldFecha = fieldFecha;
    }

    public String getFieldEvento() {
        return fieldEvento;
    }

    public void setFieldEvento(String fieldEvento) {
        this.fieldEvento = fieldEvento;
    }

    
    
    public String getFechaLabel() {
        return fechaLabel;
    }

    public void setFechaLabel(String fechaLabel) {
        this.fechaLabel = fechaLabel;
    }
    
    public String getLabelCalendario() {
        return labelCalendario;
    }

    public void setLabelCalendario(String labelCalendario) {
        this.labelCalendario = labelCalendario;
    }

    public boolean isTipoMarcaDuplicado() {
        return tipoMarcaDuplicado;
    }

    public void setTipoMarcaDuplicado(boolean tipoMarcaDuplicado) {
        this.tipoMarcaDuplicado = tipoMarcaDuplicado;
    }
    
    public int getNumMarcas() {
        return numMarcas;
    }

    public void setNumMarcas(int numMarcas) {
        this.numMarcas = numMarcas;
    }

    public int getIdTurnoAsignado() {
        return idTurnoAsignado;
    }

    public void setIdTurnoAsignado(int idTurnoAsignado) {
        this.idTurnoAsignado = idTurnoAsignado;
    }

    public String getTurnoNocturno() {
        return turnoNocturno;
    }

    public void setTurnoNocturno(String turnoNocturno) {
        this.turnoNocturno = turnoNocturno;
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

    public String getLabelTipoMarca(){
        String label="Entrada";
        if (this.tipoMarca==2){
            label = "Salida";
        }
        return label;
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

    @Override
    public String toString() {
        return "InfoMarcaVO{" + "rowKey=" + rowKey + ", fecha=" + fecha + ", fechaLabel=" + fechaLabel + ", codDia=" + codDia + ", empresaId=" + empresaId + ", rutEmpleado=" + rutEmpleado + ", fechaHoraMarca=" + fechaHoraMarca + ", horaMarca=" + horaMarca + ", fechaHoraMarcaLabel=" + fechaHoraMarcaLabel + ", tipoMarca=" + tipoMarca + ", codDispositivo=" + codDispositivo + ", idTurnoEmpleado=" + idTurnoEmpleado + ", idTurnoAsignado=" + idTurnoAsignado + ", labelTurno=" + labelTurno + ", horaEntradaTurno=" + horaEntradaTurno + ", horaSalidaTurno=" + horaSalidaTurno + ", minsColacion=" + minsColacion + ", turnoNocturno=" + turnoNocturno + ", numMarcas=" + numMarcas + ", tipoMarcaDuplicado=" + tipoMarcaDuplicado + ", labelCalendario=" + labelCalendario + ", fieldFecha=" + fieldFecha + ", fieldEvento=" + fieldEvento + ", hora=" + hora + ", minutos=" + minutos + ", segundos=" + segundos + ", id=" + id + ", hashcode=" + hashcode + ", comentario=" + comentario + ", fechaHoraActualizacion=" + fechaHoraActualizacion + ", codTipoMarcaManual=" + codTipoMarcaManual + ", nombreTipoMarcaManual=" + nombreTipoMarcaManual + ", ausenciaNombre=" + ausenciaNombre + ", ausenciaPorHora=" + ausenciaPorHora + ", fechaInicioAusencia=" + fechaInicioAusencia + ", fechaFinAusencia=" + fechaFinAusencia + ", tipoEvento=" + tipoEvento + ", masInfo=" + masInfo + ", horaInicioAusencia=" + horaInicioAusencia + ", horaFinAusencia=" + horaFinAusencia + '}';
    }

       
    
}
