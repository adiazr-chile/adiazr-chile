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
public class OrganizacionEmpresaVO implements Serializable{

    private static final long serialVersionUID = 7009472299878776220L;

    private String empresaId;
    private String empresaIdParam;
    private String empresaNombre;
    private String deptoId;
    private String deptoIdParam;
    private String deptoNombre;
    private int cencoId = -1;
    private String cencoIdParam;
    private String cencoNombre;

    /**
     *
     * @return
     */
    public String getEmpresaIdParam() {
        return empresaIdParam;
    }

    public void setEmpresaIdParam(String empresaIdParam) {
        this.empresaIdParam = empresaIdParam;
    }

    public String getDeptoIdParam() {
        return deptoIdParam;
    }

    public void setDeptoIdParam(String deptoIdParam) {
        this.deptoIdParam = deptoIdParam;
    }

    public String getCencoIdParam() {
        return cencoIdParam;
    }

    public void setCencoIdParam(String cencoIdParam) {
        this.cencoIdParam = cencoIdParam;
    }

    public String getEmpresaId() {
        return empresaId;
        
    }

    public void setEmpresaId(String _empresaId) {
        this.empresaId = _empresaId;
        setEmpresaIdParam("'"+_empresaId+"'");
    }

    /**
     *
     * @return
     */
    public String getEmpresaNombre() {
        return empresaNombre;
    }

    public void setEmpresaNombre(String empresaNombre) {
        this.empresaNombre = empresaNombre;
    }

    public String getDeptoId() {
        return deptoId;
    }

    public void setDeptoId(String _deptoId) {
        this.deptoId = _deptoId;
        setDeptoIdParam("'"+_deptoId+"'");
    }

    public String getDeptoNombre() {
        return deptoNombre;
    }

    public void setDeptoNombre(String deptoNombre) {
        this.deptoNombre = deptoNombre;
    }

    public int getCencoId() {
        return cencoId;
    }

    public void setCencoId(int _cencoId) {
        this.cencoId = _cencoId;
        setCencoIdParam("'"+_cencoId+"'");
    }

    /**
     *
     * @return
     */
    public String getCencoNombre() {
        return cencoNombre;
    }

    public void setCencoNombre(String cencoNombre) {
        this.cencoNombre = cencoNombre;
    }
    
    
    
}
