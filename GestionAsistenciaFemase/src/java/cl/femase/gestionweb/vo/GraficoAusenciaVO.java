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
public class GraficoAusenciaVO implements Serializable{

    private static final long serialVersionUID = 7587692295698776220L;

    private final String nombreAusencia;
    private final int numJustificaciones;

    public GraficoAusenciaVO(String nombreAusencia, int numJustificaciones) {
        this.nombreAusencia = nombreAusencia;
        this.numJustificaciones = numJustificaciones;
    }

    public String getNombreAusencia() {
        return nombreAusencia;
    }

    public int getNumJustificaciones() {
        return numJustificaciones;
    }

    
    
}
