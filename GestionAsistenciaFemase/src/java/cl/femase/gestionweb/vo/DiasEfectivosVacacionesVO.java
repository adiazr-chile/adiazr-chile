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
public class DiasEfectivosVacacionesVO implements Serializable{

    private static final long serialVersionUID = 5698478895698776220L;

    private int diasEfectivosVBA = 0;
    private int diasEfectivosVP  = 0;
    private double saldoVBAPreVacaciones = 0;
    private double saldoVPPreVacaciones = 0;
    private double saldoVBAPostVacaciones = 0;
    private double saldoVPPostVacaciones = 0;
    
    public DiasEfectivosVacacionesVO() {
        
    }

    public int getDiasEfectivosVBA() {
        return diasEfectivosVBA;
    }

    public void setDiasEfectivosVBA(int _diasEfectivosVBA) {
        this.diasEfectivosVBA = _diasEfectivosVBA;
    }

    public int getDiasEfectivosVP() {
        return diasEfectivosVP;
    }

    public void setDiasEfectivosVP(int _diasEfectivosVP) {
        this.diasEfectivosVP = _diasEfectivosVP;
    }

    public double getSaldoVBAPreVacaciones() {
        return saldoVBAPreVacaciones;
    }

    public void setSaldoVBAPreVacaciones(double saldoVBAPreVacaciones) {
        this.saldoVBAPreVacaciones = saldoVBAPreVacaciones;
    }

    public double getSaldoVPPreVacaciones() {
        return saldoVPPreVacaciones;
    }

    public void setSaldoVPPreVacaciones(double saldoVPPreVacaciones) {
        this.saldoVPPreVacaciones = saldoVPPreVacaciones;
    }

    public double getSaldoVBAPostVacaciones() {
        return saldoVBAPostVacaciones;
    }

    public void setSaldoVBAPostVacaciones(double saldoVBAPostVacaciones) {
        this.saldoVBAPostVacaciones = saldoVBAPostVacaciones;
    }

    public double getSaldoVPPostVacaciones() {
        return saldoVPPostVacaciones;
    }

    public void setSaldoVPPostVacaciones(double saldoVPPostVacaciones) {
        this.saldoVPPostVacaciones = saldoVPPostVacaciones;
    }

    @Override
    public String toString() {
        return "DiasEfectivosVacacionesVO{" 
            + "diasEfectivosVBA=" + diasEfectivosVBA 
            + ", diasEfectivosVP=" + diasEfectivosVP 
            + ", saldoVBAPreVacaciones=" + saldoVBAPreVacaciones 
            + ", saldoVPPreVacaciones=" + saldoVPPreVacaciones 
            + ", saldoVBAPostVacaciones=" + saldoVBAPostVacaciones 
            + ", saldoVPPostVacaciones=" + saldoVPPostVacaciones + '}';
    }
    
    
}
