/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.vo;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author Alexander
 */
public class DetalleAsistenciaVO implements Serializable{
    private static final long serialVersionUID = 872774229569759620L;
    
    private String rut;
    private String nombreEmpleado;
    private String empresaId;
    private String deptoId;
    private int cencoId;
    private String rutEmpleado;
    private String fechaCalculo;
    private String fechaHoraCalculo;
    private String fechaEntradaMarca;
    private String fechaSalidaMarca;
    private String labelFechaEntradaMarca;
    private String labelFechaSalidaMarca;
    private String rowKey;
    private String fechaMarcaEntradaLabel;
    private String fechaMarcaSalidaLabel;
    private String horaEntrada      = "00:00:00";
    private String horaSalida       = "00:00:00";
    private String horaEntradaTeorica= "00:00:00";
    private String horaSalidaTeorica= "00:00:00";
    private String hhmmTeoricas     = "00:00:00";
    private int horasReales;
    private int minutosReales;
    private int horasTeoricas;
    private int holguraMinutos;
    private boolean esFeriado;
    private int horasExtras;
    private int minutosExtras;
    private int minutosExtrasAl50;
    private int minutosExtrasAl100;
    private int horasAusencia;
    private String horaInicioAusencia;
    private String horaFinAusencia;
    private String horaMinsExtras;
    private String horasMinsExtrasAutorizadas;
    private int minutosTeoricos;
    private boolean art22;
    private int minutosAtraso;
    private int minutosNoTrabajadosEntrada;
    private int minutosNoTrabajadosSalida;
    private String autorizaMinsNoTrabajadosEntrada="N";
    private String autorizaMinsNoTrabajadosSalida="N";
    private String hhmmAtraso;
    private String autorizaAtraso   = "N";
    private String autorizaHrsExtras = "N";
    private String hhmmJustificadas;
    private int intHHExtras;
    private int intMinsExtras;
    private String comentarioMarcaEntrada;
    private String comentarioMarcaSalida;
    private String horaMinsSalidaAnticipada;

    public String getHhmmTeoricas() {
        return hhmmTeoricas;
    }

    public void setHhmmTeoricas(String hhmmTeoricas) {
        this.hhmmTeoricas = hhmmTeoricas;
    }

    public String getHoraMinsSalidaAnticipada() {
        return horaMinsSalidaAnticipada;
    }

    public void setHoraMinsSalidaAnticipada(String horaMinsSalidaAnticipada) {
        this.horaMinsSalidaAnticipada = horaMinsSalidaAnticipada;
    }
    
    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    public String getComentarioMarcaEntrada() {
        return comentarioMarcaEntrada;
    }

    public void setComentarioMarcaEntrada(String comentarioMarcaEntrada) {
        this.comentarioMarcaEntrada = comentarioMarcaEntrada;
    }

    public String getComentarioMarcaSalida() {
        return comentarioMarcaSalida;
    }

    public void setComentarioMarcaSalida(String comentarioMarcaSalida) {
        this.comentarioMarcaSalida = comentarioMarcaSalida;
    }

    
    
    public String getHorasMinsExtrasAutorizadas() {
        return horasMinsExtrasAutorizadas;
    }

    public void setHorasMinsExtrasAutorizadas(String horasMinsExtrasAutorizadas) {
        this.horasMinsExtrasAutorizadas = horasMinsExtrasAutorizadas;
    }

    public int getIntHHExtras() {
        return intHHExtras;
    }

    public void setIntHHExtras(int intHHExtras) {
        this.intHHExtras = intHHExtras;
    }

    public int getIntMinsExtras() {
        return intMinsExtras;
    }

    public void setIntMinsExtras(int intMinsExtras) {
        this.intMinsExtras = intMinsExtras;
    }
    
    
    public String getHhmmJustificadas() {
        return hhmmJustificadas;
    }

    public void setHhmmJustificadas(String hhmmJustificadas) {
        this.hhmmJustificadas = hhmmJustificadas;
    }
    
    public String getAutorizaAtraso() {
        return autorizaAtraso;
    }

    /**
     *
     * @param autorizaAtraso
     */
    public void setAutorizaAtraso(String autorizaAtraso) {
        this.autorizaAtraso = autorizaAtraso;
    }

    public String getAutorizaHrsExtras() {
        return autorizaHrsExtras;
    }

    public void setAutorizaHrsExtras(String autorizaHrsExtras) {
        this.autorizaHrsExtras = autorizaHrsExtras;
    }
    
    public String getHhmmAtraso() {
        return hhmmAtraso;
    }

    public void setHhmmAtraso(String hhmmAtraso) {
        this.hhmmAtraso = hhmmAtraso;
    }
    
    /**
     *
     * @return
     */
    public String getFechaCalculo() {
        return fechaCalculo;
    }

    public void setFechaCalculo(String fechaCalculo) {
        this.fechaCalculo = fechaCalculo;
    }

    public int getMinutosExtras() {
        return minutosExtras;
    }

    public void setMinutosExtras(int minutosExtras) {
        this.minutosExtras = minutosExtras;
    }

    public String getLabelFechaEntradaMarca() {
        return labelFechaEntradaMarca;
    }

    public void setLabelFechaEntradaMarca(String labelFechaEntradaMarca) {
        this.labelFechaEntradaMarca = labelFechaEntradaMarca;
    }

    public String getLabelFechaSalidaMarca() {
        return labelFechaSalidaMarca;
    }

    public void setLabelFechaSalidaMarca(String labelFechaSalidaMarca) {
        this.labelFechaSalidaMarca = labelFechaSalidaMarca;
    }
    
    /**
     * Hrs presenciales en formato HH:mm
     */
    private String hrsPresenciales;
    
    /**
     * Hrs trabajadas en formato HH:mm
     */
    private String hrsTrabajadas;
    
    /**
     * Hrs ausencia segun turno en formato HH:mm
     */
    private String hrsAusencia;
    
    /**
     * Observacion
     */
    private String observacion;
    
    /**
    * Horas no trabajadas--> HH:mm
    */
    private String hrsNoTrabajadas;
    
    public DetalleAsistenciaVO() {
            
    }

    public String getHrsNoTrabajadas() {
        return hrsNoTrabajadas;
    }

    public void setHrsNoTrabajadas(String hrsNoTrabajadas) {
        this.hrsNoTrabajadas = hrsNoTrabajadas;
    }

    
    
    public String getHrsAusencia() {
        return hrsAusencia;
    }

    public void setHrsAusencia(String hrsAusencia) {
        this.hrsAusencia = hrsAusencia;
    }

    public String getHrsPresenciales() {
        return hrsPresenciales;
    }

    public void setHrsPresenciales(String hrsPresenciales) {
        this.hrsPresenciales = hrsPresenciales;
    }

    /**
     *
     * @return
     */
    public String getHrsTrabajadas() {
        return hrsTrabajadas;
    }

    public void setHrsTrabajadas(String hrsTrabajadas) {
        this.hrsTrabajadas = hrsTrabajadas;
    }

    public String getObservacion() {
        return observacion;
    }

    /**
    *
    * @param _observacion
    * @param _source
    */
    public void setObservacion(String _observacion, String _source) {
        System.out.println("[GestionFemase.DetalleAsistenciaVO."
            + "setObservacion]Observacion: " + _observacion 
            + ", source: " + _source);
        this.observacion = _observacion;
    }

    
    
    public String getFechaHoraCalculo() {
        return fechaHoraCalculo;
    }

    public void setFechaHoraCalculo(String fechaHoraCalculo) {
        this.fechaHoraCalculo = fechaHoraCalculo;
    }

    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public String getDeptoId() {
        return deptoId;
    }

    /**
     *
     * @param deptoId
     */
    public void setDeptoId(String deptoId) {
        this.deptoId = deptoId;
    }

    public int getCencoId() {
        return cencoId;
    }

    public void setCencoId(int cencoId) {
        this.cencoId = cencoId;
    }

    /**
     *
     * @return
     */
    public String getRutEmpleado() {
        return rutEmpleado;
    }

    public void setRutEmpleado(String rutEmpleado) {
        this.rutEmpleado = rutEmpleado;
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    
    
    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getAutorizaMinsNoTrabajadosEntrada() {
        return autorizaMinsNoTrabajadosEntrada;
    }

    public void setAutorizaMinsNoTrabajadosEntrada(String autorizaMinsNoTrabajadosEntrada) {
        this.autorizaMinsNoTrabajadosEntrada = autorizaMinsNoTrabajadosEntrada;
    }

    public String getAutorizaMinsNoTrabajadosSalida() {
        return autorizaMinsNoTrabajadosSalida;
    }

    public void setAutorizaMinsNoTrabajadosSalida(String autorizaMinsNoTrabajadosSalida) {
        this.autorizaMinsNoTrabajadosSalida = autorizaMinsNoTrabajadosSalida;
    }

    
    
    public int getMinutosNoTrabajadosEntrada() {
        return minutosNoTrabajadosEntrada;
    }

    public void setMinutosNoTrabajadosEntrada(int minutosNoTrabajadosEntrada) {
        this.minutosNoTrabajadosEntrada = minutosNoTrabajadosEntrada;
    }

    public int getMinutosNoTrabajadosSalida() {
        return minutosNoTrabajadosSalida;
    }

    public void setMinutosNoTrabajadosSalida(int minutosNoTrabajadosSalida) {
        this.minutosNoTrabajadosSalida = minutosNoTrabajadosSalida;
    }

    public int getMinutosAtraso() {
        return minutosAtraso;
    }

    public void setMinutosAtraso(int minutosAtraso) {
        this.minutosAtraso = minutosAtraso;
    }

    public boolean isArt22() {
        return art22;
    }

    public void setArt22(boolean art22) {
        this.art22 = art22;
    }

    public String getFechaMarcaEntradaLabel() {
        return fechaMarcaEntradaLabel;
    }

    public String getHoraEntradaTeorica() {
        return horaEntradaTeorica;
    }

    public void setHoraEntradaTeorica(String horaEntradaTeorica) {
        this.horaEntradaTeorica = horaEntradaTeorica;
    }

    public String getHoraSalidaTeorica() {
        return horaSalidaTeorica;
    }

    public void setHoraSalidaTeorica(String horaSalidaTeorica) {
        this.horaSalidaTeorica = horaSalidaTeorica;
    }

    public int getMinutosTeoricos() {
        return minutosTeoricos;
    }

    public void setMinutosTeoricos(int minutosTeoricos) {
        this.minutosTeoricos = minutosTeoricos;
    }

    public int getHolguraMinutos() {
        return holguraMinutos;
    }

    /**
     *
     * @param holguraMinutos
     */
    public void setHolguraMinutos(int holguraMinutos) {
        this.holguraMinutos = holguraMinutos;
    }

    public boolean isEsFeriado() {
        return esFeriado;
    }

    public void setEsFeriado(boolean esFeriado) {
        this.esFeriado = esFeriado;
    }

    public int getMinutosExtrasAl50() {
        return minutosExtrasAl50;
    }

    public void setMinutosExtrasAl50(int minutosExtrasAl50) {
        this.minutosExtrasAl50 = minutosExtrasAl50;
    }

    public int getMinutosExtrasAl100() {
        return minutosExtrasAl100;
    }

    public void setMinutosExtrasAl100(int minutosExtrasAl100) {
        this.minutosExtrasAl100 = minutosExtrasAl100;
    }

//    public int getMinutosExtras() {
//        return minutosExtras;
//    }
//
//    public void setMinutosExtras(int minutosExtras) {
//        this.minutosExtras = minutosExtras;
//    }

   

    public int getMinutosReales() {
        return minutosReales;
    }

    /**
     *
     * @param minutosReales
     */
    public void setMinutosReales(int minutosReales) {
        this.minutosReales = minutosReales;
    }

    /**
     *
     * @return
     */
    public String getFechaEntradaMarca() {
        return fechaEntradaMarca;
    }

    public void setFechaEntradaMarca(String fechaMarca) {
        this.fechaEntradaMarca = fechaMarca;
        String dateString = fechaMarca;
        Date asdate=null;
        try {
            asdate = new SimpleDateFormat("yyyy-M-d").parse(dateString);
        } catch (ParseException ex) {
            System.err.println("Error al parsear fechaMarca: "+ex.toString());
        }
        String dayOfWeek = new SimpleDateFormat("EE", new Locale("es","ES")).format(asdate);
        String firstLetter = dayOfWeek.substring(0, 1).toUpperCase();
        String finalLetters = dayOfWeek.substring(1);
        
//        System.out.println(WEB_NAME+"dia labe:" + firstLetter+finalLetters+" "+dateString);
        this.fechaMarcaEntradaLabel = firstLetter+finalLetters+" "+dateString;
    }
    
    //
    public String getFechaSalidaMarca() {
        return fechaSalidaMarca;
    }

    /**
    * 
     * @param fechaMarca
    */
    public void setFechaSalidaMarca(String fechaMarca) {
        this.fechaSalidaMarca = fechaMarca;
        String dateString = fechaMarca;
        Date asdate=null;
        try {
            asdate = new SimpleDateFormat("yyyy-M-d").parse(dateString);
        } catch (ParseException ex) {
            System.err.println("Error al parsear fechaMarca: "+ex.toString());
        }
        String dayOfWeek = new SimpleDateFormat("EE", new Locale("es","ES")).format(asdate);
        String firstLetter = dayOfWeek.substring(0, 1).toUpperCase();
        String finalLetters = dayOfWeek.substring(1);
        
//        System.out.println(WEB_NAME+"dia labe:" + firstLetter+finalLetters+" "+dateString);
        this.fechaMarcaSalidaLabel = firstLetter+finalLetters+" "+dateString;
    }
    

    public String getHoraMinsExtras() {
        return horaMinsExtras;
    }

    public void setHoraMinsExtras(String horaMinsExtras) {
        this.horaMinsExtras = horaMinsExtras;
    }

    public String getHoraInicioAusencia() {
        return horaInicioAusencia;
    }

    public void setHoraInicioAusencia(String horaInicioAusencia) {
        this.horaInicioAusencia = horaInicioAusencia;
    }

    /**
     *
     * @return
     */
    public String getHoraFinAusencia() {
        return horaFinAusencia;
    }

    public void setHoraFinAusencia(String horaFinAusencia) {
        this.horaFinAusencia = horaFinAusencia;
    }

    public String getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(String horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public String getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(String horaSalida) {
        this.horaSalida = horaSalida;
    }

    public int getHorasReales() {
        return horasReales;
    }

    public void setHorasReales(int horasReales) {
        this.horasReales = horasReales;
    }

    public int getHorasTeoricas() {
        return horasTeoricas;
    }

    public void setHorasTeoricas(int horasTeoricas) {
        this.horasTeoricas = horasTeoricas;
    }

    public int getHorasExtras() {
        return horasExtras;
    }

    public void setHorasExtras(int horasExtras) {
        this.horasExtras = horasExtras;
    }

    public int getHorasAusencia() {
        return horasAusencia;
    }

    public void setHorasAusencia(int horasAusencia) {
        this.horasAusencia = horasAusencia;
    }

    @Override
    public String toString() {
        return "DetalleAsistenciaVO{" 
            + "rut=" + rut 
            + ", nombreEmpleado=" + nombreEmpleado 
            + ", empresaId=" + empresaId 
            + ", deptoId=" + deptoId 
            + ", cencoId=" + cencoId 
            + ", rutEmpleado=" + rutEmpleado 
            + ", fechaCalculo=" + fechaCalculo 
            + ", fechaHoraCalculo=" + fechaHoraCalculo 
            + ", fechaEntradaMarca=" + fechaEntradaMarca 
            + ", fechaSalidaMarca=" + fechaSalidaMarca 
            + ", labelFechaEntradaMarca=" + labelFechaEntradaMarca 
            + ", labelFechaSalidaMarca=" + labelFechaSalidaMarca 
            + ", rowKey=" + rowKey 
            + ", fechaMarcaEntradaLabel=" + fechaMarcaEntradaLabel 
            + ", fechaMarcaSalidaLabel=" + fechaMarcaSalidaLabel 
            + ", horaEntrada=" + horaEntrada 
            + ", horaSalida=" + horaSalida 
            + ", horaEntradaTeorica=" + horaEntradaTeorica 
            + ", horaSalidaTeorica=" + horaSalidaTeorica 
            + ", horasReales=" + horasReales 
            + ", minutosReales=" + minutosReales 
            + ", horasTeoricas=" + horasTeoricas 
            + ", holguraMinutos=" + holguraMinutos 
            + ", esFeriado=" + esFeriado 
            + ", horasExtras=" + horasExtras 
            + ", minutosExtras=" + minutosExtras 
            + ", minutosExtrasAl50=" + minutosExtrasAl50 
            + ", minutosExtrasAl100=" + minutosExtrasAl100 
            + ", horasAusencia=" + horasAusencia 
            + ", horaInicioAusencia=" + horaInicioAusencia 
            + ", horaFinAusencia=" + horaFinAusencia 
            + ", horaMinsExtras=" + horaMinsExtras 
            + ", horasMinsExtrasAutorizadas=" + horasMinsExtrasAutorizadas 
            + ", minutosTeoricos=" + minutosTeoricos 
            + ", art22=" + art22 
            + ", minutosAtraso=" + minutosAtraso 
            + ", minutosNoTrabajadosEntrada=" + minutosNoTrabajadosEntrada 
            + ", minutosNoTrabajadosSalida=" + minutosNoTrabajadosSalida 
            + ", autorizaMinsNoTrabajadosEntrada=" + autorizaMinsNoTrabajadosEntrada 
            + ", autorizaMinsNoTrabajadosSalida=" + autorizaMinsNoTrabajadosSalida 
            + ", hhmmAtraso=" + hhmmAtraso 
            + ", autorizaAtraso=" + autorizaAtraso 
            + ", autorizaHrsExtras=" + autorizaHrsExtras 
            + ", hhmmJustificadas=" + hhmmJustificadas 
            + ", intHHExtras=" + intHHExtras 
            + ", intMinsExtras=" + intMinsExtras 
            + ", comentarioMarcaEntrada=" + comentarioMarcaEntrada 
            + ", comentarioMarcaSalida=" + comentarioMarcaSalida 
            + ", horaMinsSalidaAnticipada=" + horaMinsSalidaAnticipada 
            + ", hrsPresenciales=" + hrsPresenciales 
            + ", hrsTrabajadas=" + hrsTrabajadas 
            + ", hrsAusencia=" + hrsAusencia 
            + ", observacion=" + observacion + '}';
    }
    
}
