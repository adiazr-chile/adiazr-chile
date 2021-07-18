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
public class ProcesoFiltroVO implements Serializable{

    private static final long serialVersionUID = 7605143885698776220L;

    private String empresaId;
    private int procesoId;
    private String code;
    private String label;
    private boolean isList;
    private boolean isCheckbox;
    private boolean isDate;
    private String sourceTable="";
    private String format;
    private String defaultValue;

    public boolean isIsCheckbox() {
        return isCheckbox;
    }

    public void setIsCheckbox(boolean isCheckbox) {
        this.isCheckbox = isCheckbox;
    }

    
    
    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isIsList() {
        return isList;
    }

    public void setIsList(boolean isList) {
        this.isList = isList;
    }

    public boolean isIsDate() {
        return isDate;
    }

    public void setIsDate(boolean isDate) {
        this.isDate = isDate;
    }

    public String getSourceTable() {
        return sourceTable;
    }

    public void setSourceTable(String sourceTable) {
        this.sourceTable = sourceTable;
    }
    
    
}
