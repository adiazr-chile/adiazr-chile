/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.vo;

import java.io.Serializable;

/**
 *
 * @author Alexander
 */
public class FilasAfectadasJsonVO implements Serializable{
    private static final long serialVersionUID = 7895472338799776220L;
   
    private String functionName;
    private String affectedTables   = "";
    private String affectedColumns  = "";
    private String empresaId        = "";
    private String runEmpleado      = "";
    private int affectedRows;
    private String resultado        = "";

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getAffectedTables() {
        return affectedTables;
    }

    public void setAffectedTables(String affectedTables) {
        this.affectedTables = affectedTables;
    }

    public String getAffectedColumns() {
        return affectedColumns;
    }

    public void setAffectedColumns(String affectedColumns) {
        this.affectedColumns = affectedColumns;
    }

    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public String getRunEmpleado() {
        return runEmpleado;
    }

    public void setRunEmpleado(String runEmpleado) {
        this.runEmpleado = runEmpleado;
    }

    public int getAffectedRows() {
        return affectedRows;
    }

    public void setAffectedRows(int affectedRows) {
        this.affectedRows = affectedRows;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    @Override
    public String toString() {
        return "FilaAfectadaJsonVO{" + "functionName=" + functionName + ", affectedTables=" + affectedTables + ", affectedColumns=" + affectedColumns + ", empresaId=" + empresaId + ", runEmpleado=" + runEmpleado + ", affectedRows=" + affectedRows + ", resultado=" + resultado + '}';
    }
    
}
