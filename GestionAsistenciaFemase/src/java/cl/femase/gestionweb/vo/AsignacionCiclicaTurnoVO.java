/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.vo;

import java.time.LocalDate;

/**
 *
 * @author Alexander
 */
public class AsignacionCiclicaTurnoVO implements Comparable{

    private static final long serialVersionUID = 3699992295698776220L;

    LocalDate fecha;
    int diaSemana;
    String labelDiaSemana;
    int idTurno;
    String nombreTurno;
    
    public AsignacionCiclicaTurnoVO(LocalDate _fecha, 
            int _idTurno, 
            int _diaSemana, 
            String _labelDiaSemana, 
            String _nombreTurno) {
        this.idTurno        = _idTurno;
        this.nombreTurno    = _nombreTurno;
        this.fecha          = _fecha;
        this.diaSemana      = _diaSemana;
        this.labelDiaSemana = _labelDiaSemana;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public int getIdTurno() {
        return idTurno;
    }

    public int getDiaSemana() {
        return diaSemana;
    }

    public String getLabelDiaSemana() {
        return labelDiaSemana;
    }

    public String getNombreTurno() {
        return nombreTurno;
    }


    @Override
    public int compareTo(Object o) {
        AsignacionCiclicaTurnoVO obj=(AsignacionCiclicaTurnoVO)o;
        /* For Ascending order*/
        boolean comparacion = this.fecha.isBefore(obj.getFecha());
        int retornar = 0;
        if (comparacion) retornar = -1;
        else retornar = 1;
        return retornar;
    }
    
    
}
