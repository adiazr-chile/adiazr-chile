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
public class DiferenciaHorasVO implements Serializable{
    
    private static final long serialVersionUID = 987774296369759620L;
    
    private String strDiferenciaHorasMinutos;
    private String strDiferenciaHorasMinutosSegundos;
    
    private int intDiferenciaHoras;
    private int intDiferenciaMinutos;
    private int intDiferenciaSegundos;
    boolean horaInicialMenor=true;
    boolean horaInicialMayor=false;

    public DiferenciaHorasVO() {
    }

    public boolean isHoraInicialMenor() {
        return horaInicialMenor;
    }

    public void setHoraInicialMenor(boolean horaInicialMenor) {
        this.horaInicialMenor = horaInicialMenor;
    }

    public boolean isHoraInicialMayor() {
        return horaInicialMayor;
    }

    public void setHoraInicialMayor(boolean horaInicialMayor) {
        this.horaInicialMayor = horaInicialMayor;
    }

    public String getStrDiferenciaHorasMinutos() {
        return strDiferenciaHorasMinutos;
    }

    public void setStrDiferenciaHorasMinutos(String strDiferenciaHorasMinutos) {
        this.strDiferenciaHorasMinutos = strDiferenciaHorasMinutos;
    }
    
    public String getStrDiferenciaHorasMinutosSegundos() {
        return strDiferenciaHorasMinutosSegundos;
    }

    public void setStrDiferenciaHorasMinutosSegundos(String strDiferenciaHorasMinutosSegundos) {
        this.strDiferenciaHorasMinutosSegundos = strDiferenciaHorasMinutosSegundos;
    }

    public int getIntDiferenciaSegundos() {
        return intDiferenciaSegundos;
    }

    public void setIntDiferenciaSegundos(int intDiferenciaSegundos) {
        this.intDiferenciaSegundos = intDiferenciaSegundos;
    }

    
    public int getIntDiferenciaHoras() {
        return intDiferenciaHoras;
    }

    public void setIntDiferenciaHoras(int intDiferenciaHoras) {
        this.intDiferenciaHoras = intDiferenciaHoras;
    }

    /**
     *
     * @return
     */
    public int getIntDiferenciaMinutos() {
        return intDiferenciaMinutos;
    }

    public void setIntDiferenciaMinutos(int intDiferenciaMinutos) {
        this.intDiferenciaMinutos = intDiferenciaMinutos;
    }
    
    
}
