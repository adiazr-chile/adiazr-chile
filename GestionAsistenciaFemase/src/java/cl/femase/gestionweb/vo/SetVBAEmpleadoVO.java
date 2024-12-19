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
public class SetVBAEmpleadoVO implements Serializable{

    private static final long serialVersionUID = 5677472295698776220L;

    private String empresa_id;
    private String run_empleado;
    private String fecha_inicio_contrato;
    private String fecha_inicio_periodo;
    private String fecha_fin_periodo;
    
    private String es_periodo_en_curso;

    public String isPeriodoEnCurso() {
        return es_periodo_en_curso;
    }

    public void setEsPeriodoEnCurso(String es_periodo_en_curso) {
        this.es_periodo_en_curso = es_periodo_en_curso;
    }
    
    public String getEmpresa_id() {
        return empresa_id;
    }

    public void setEmpresa_id(String empresa_id) {
        this.empresa_id = empresa_id;
    }

    public String getRun_empleado() {
        return run_empleado;
    }

    public void setRun_empleado(String run_empleado) {
        this.run_empleado = run_empleado;
    }

    public String getFecha_inicio_contrato() {
        return fecha_inicio_contrato;
    }

    public void setFecha_inicio_contrato(String fecha_inicio_contrato) {
        this.fecha_inicio_contrato = fecha_inicio_contrato;
    }

    public String getFecha_inicio_periodo() {
        return fecha_inicio_periodo;
    }

    public void setFecha_inicio_periodo(String fecha_inicio_periodo) {
        this.fecha_inicio_periodo = fecha_inicio_periodo;
    }

    public String getFecha_fin_periodo() {
        return fecha_fin_periodo;
    }

    public void setFecha_fin_periodo(String fecha_fin_periodo) {
        this.fecha_fin_periodo = fecha_fin_periodo;
    }

    @Override
    public String toString() {
        return "SetVBAEmpleadoVO{" + "empresa_id=" + empresa_id + ", run_empleado=" + run_empleado + ", fecha_inicio_contrato=" + fecha_inicio_contrato + ", fecha_inicio_periodo=" + fecha_inicio_periodo + ", fecha_fin_periodo=" + fecha_fin_periodo + '}';
    }

    
    
    
    
}
