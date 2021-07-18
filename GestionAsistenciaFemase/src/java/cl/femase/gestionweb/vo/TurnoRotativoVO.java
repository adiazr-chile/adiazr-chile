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
public class TurnoRotativoVO extends TurnoVO implements Serializable{

    private static final long serialVersionUID = 3377742295698779620L;
    private String horaEntrada;
    private String horaSalida;
    private int minutosColacion=0;
    private int holgura=0;
    private String aplicarTodos="N";
    private String nocturno="N";
        
    public TurnoRotativoVO(int _id, String _nombre) {
        setId(_id);
        setNombre(_nombre);
    }

    public String getNocturno() {
        return nocturno;
    }

    public void setNocturno(String nocturno) {
        this.nocturno = nocturno;
    }

    public int getHolgura() {
        return holgura;
    }

    public void setHolgura(int holgura) {
        this.holgura = holgura;
    }

    public String getAplicarTodos() {
        return aplicarTodos;
    }

    public void setAplicarTodos(String aplicarTodos) {
        this.aplicarTodos = aplicarTodos;
    }

    
    
    public int getMinutosColacion() {
        return minutosColacion;
    }

    public void setMinutosColacion(int minutosColacion) {
        this.minutosColacion = minutosColacion;
    }
    
    /**
     *
     * @return
     */
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
    
    /**
     *
     */
    public TurnoRotativoVO() {
        
    }

    
    
}
