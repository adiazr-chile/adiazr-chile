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
public class DetalleAusenciaJsonVO implements Serializable{

    private static final long serialVersionUID = 6787742295698759620L;

    private int correlativo;
    private String rutempleado;
    private String horainiciofullasstr;
    private String horafinfullasstr;
    private String permitehora="N";
    private String nombreausencia;
    private int tipoausencia=1;
    private String justificahoras;
    private String pagadaporempleador;
    
    public DetalleAusenciaJsonVO() {
        
    }

    public int getCorrelativo() {
        return correlativo;
    }

    /**
     *
     * @param correlativo
     */
    public void setCorrelativo(int correlativo) {
        this.correlativo = correlativo;
    }

    public String getPagadaporempleador() {
        return pagadaporempleador;
    }

    public void setPagadaporempleador(String pagadaporempleador) {
        this.pagadaporempleador = pagadaporempleador;
    }

    public String getJustificahoras() {
        return justificahoras;
    }

    public void setJustificahoras(String justificahoras) {
        this.justificahoras = justificahoras;
    }

    public String getRutempleado() {
        return rutempleado;
    }

    public void setRutempleado(String rutempleado) {
        this.rutempleado = rutempleado;
    }

    public String getHorainiciofullasstr() {
        return horainiciofullasstr;
    }

    public void setHorainiciofullasstr(String horainiciofullasstr) {
        this.horainiciofullasstr = horainiciofullasstr;
    }

    public String getHorafinfullasstr() {
        return horafinfullasstr;
    }

    public void setHorafinfullasstr(String horafinfullasstr) {
        this.horafinfullasstr = horafinfullasstr;
    }

    public String getPermitehora() {
        return permitehora;
    }

    /**
     *
     * @param permitehora
     */
    public void setPermitehora(String permitehora) {
        this.permitehora = permitehora;
    }

    public String getNombreausencia() {
        return nombreausencia;
    }

    public void setNombreausencia(String nombreausencia) {
        this.nombreausencia = nombreausencia;
    }

    public int getTipoausencia() {
        return tipoausencia;
    }

    public void setTipoausencia(int tipoausencia) {
        this.tipoausencia = tipoausencia;
    }
    
    
    
    
}
