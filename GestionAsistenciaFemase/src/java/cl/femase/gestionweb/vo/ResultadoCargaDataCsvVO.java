/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.vo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Alexander
 */
public class ResultadoCargaDataCsvVO implements Serializable{

    private static final long serialVersionUID = 8966472295622776220L;

    private EmpleadoVO empleado;
    private DetalleAusenciaVO vacacion;
    
    private ArrayList<ResultadoCargaCsvVO> mensajes;

    public DetalleAusenciaVO getVacacion() {
        return vacacion;
    }

    public void setVacacion(DetalleAusenciaVO _vacacion) {
        this.vacacion = _vacacion;
    }

    public EmpleadoVO getEmpleado() {
        return empleado;
    }

    public void setEmpleado(EmpleadoVO empleado) {
        this.empleado = empleado;
    }

    public ArrayList<ResultadoCargaCsvVO> getMensajes() {
        return mensajes;
    }

    public void setMensajes(ArrayList<ResultadoCargaCsvVO> mensajes) {
        this.mensajes = mensajes;
    }
   }
