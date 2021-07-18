/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.vo;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Alexander
 */
public class PersonalEmpresaVO implements Serializable{

    private static final long serialVersionUID = 7587472295698776220L;

    private String rutEmpleado;
    private String empresaId;
    private String deptoId;
    private int cencoId;
    private EmpleadoVO empleado;
    private EmpresaVO empresa;
    private DepartamentoVO departamento;
    private CentroCostoVO cencosto;
    private Date fechaIngreso;
    private int estado;

    public EmpleadoVO getEmpleado() {
        return empleado;
    }

    public void setEmpleado(EmpleadoVO empleado) {
        this.empleado = empleado;
    }

    public EmpresaVO getEmpresa() {
        return empresa;
    }

    public void setEmpresa(EmpresaVO empresa) {
        this.empresa = empresa;
    }

    /**
     *
     * @return
     */
    public DepartamentoVO getDepartamento() {
        return departamento;
    }

    public void setDepartamento(DepartamentoVO departamento) {
        this.departamento = departamento;
    }

    public CentroCostoVO getCencosto() {
        return cencosto;
    }

    public void setCencosto(CentroCostoVO cencosto) {
        this.cencosto = cencosto;
    }

    public String getRutEmpleado() {
        return rutEmpleado;
    }

    public void setRutEmpleado(String rutEmpleado) {
        this.rutEmpleado = rutEmpleado;
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

    public void setDeptoId(String deptoId) {
        this.deptoId = deptoId;
    }

    public int getCencoId() {
        return cencoId;
    }

    public void setCencoId(int cencoId) {
        this.cencoId = cencoId;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
    
    
}
