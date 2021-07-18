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
public class ProcesoProgramacionVO implements Serializable{

    private static final long serialVersionUID = 7603463885698776220L;

    private String empresaId;
    private int procesoId;
    private int codDia=-1;
    private String jobName;
    private String horasEjecucion;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    
    
    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public int getProcesoId() {
        return procesoId;
    }

    public void setProcesoId(int procesoId) {
        this.procesoId = procesoId;
    }

    public int getCodDia() {
        return codDia;
    }

    public void setCodDia(int codDia) {
        this.codDia = codDia;
    }

    public String getHorasEjecucion() {
        return horasEjecucion;
    }

    public void setHorasEjecucion(String horasEjecucion) {
        this.horasEjecucion = horasEjecucion;
    }
    
}
