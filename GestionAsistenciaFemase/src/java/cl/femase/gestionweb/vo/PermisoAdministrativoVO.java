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
public class PermisoAdministrativoVO implements Serializable{

    private static final long serialVersionUID = 5698447795698776220L;

    private String rowKey;
    private String empresaId;
    private String runEmpleado;
    private String nombreEmpleado;
    private int anio;
////    //despues eliminar este attr
////    private double diasDisponibles;
////    //eliminar este attr
////    private double diasUtilizados;
    private String lastUpdate;
    private String deptoId;
    private String deptoNombre;
    private int cencoId;
    private String cencoNombre;

    
    private double diasDisponiblesSemestre1;
    private double diasUtilizadosSemestre1;
    private double diasDisponiblesSemestre2;
    private double diasUtilizadosSemestre2;

    public double getDiasDisponiblesSemestre1() {
        return diasDisponiblesSemestre1;
    }

    public void setDiasDisponiblesSemestre1(double diasDisponiblesSemestre1) {
        this.diasDisponiblesSemestre1 = diasDisponiblesSemestre1;
    }

    public double getDiasUtilizadosSemestre1() {
        return diasUtilizadosSemestre1;
    }

    public void setDiasUtilizadosSemestre1(double diasUtilizadosSemestre1) {
        this.diasUtilizadosSemestre1 = diasUtilizadosSemestre1;
    }

    public double getDiasDisponiblesSemestre2() {
        return diasDisponiblesSemestre2;
    }

    public void setDiasDisponiblesSemestre2(double diasDisponiblesSemestre2) {
        this.diasDisponiblesSemestre2 = diasDisponiblesSemestre2;
    }

    public double getDiasUtilizadosSemestre2() {
        return diasUtilizadosSemestre2;
    }

    public void setDiasUtilizadosSemestre2(double diasUtilizadosSemestre2) {
        this.diasUtilizadosSemestre2 = diasUtilizadosSemestre2;
    }
    
    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public int getCencoId() {
        return cencoId;
    }

    public void setCencoId(int cencoId) {
        this.cencoId = cencoId;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    public String getDeptoId() {
        return deptoId;
    }

    public void setDeptoId(String deptoId) {
        this.deptoId = deptoId;
    }

    public String getDeptoNombre() {
        return deptoNombre;
    }

    public void setDeptoNombre(String deptoNombre) {
        this.deptoNombre = deptoNombre;
    }

    
    public String getCencoNombre() {
        return cencoNombre;
    }

    public void setCencoNombre(String cencoNombre) {
        this.cencoNombre = cencoNombre;
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

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    
    
}
