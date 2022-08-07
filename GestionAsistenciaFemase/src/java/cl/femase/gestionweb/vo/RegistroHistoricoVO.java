/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.vo;

import cl.femase.gestionweb.common.Utilidades;
import java.io.Serializable;

/**
 *
 * @author Alexander
 */
public class RegistroHistoricoVO implements Serializable{

    private static final long serialVersionUID = 3259472295693696220L;

    private String empresaId;
    private String tabla;
    private String fechaRegistro;
    private String fechaHoraTraspaso;
    private double numRegistros;
    private double numRegistrosInsertados;
    
    private String numRegistrosAsStr;
    private String numRegistrosInsertadosAsStr;

    public String getNumRegistrosAsStr() {
        return numRegistrosAsStr;
    }
    
    public String getNumRegistrosInsertadosAsStr() {
        return numRegistrosInsertadosAsStr;
    }

    public double getNumRegistrosInsertados() {
        return numRegistrosInsertados;
    }

    public void setNumRegistrosInsertados(double numRegistrosInsertados) {
        this.numRegistrosInsertados = numRegistrosInsertados;
        this.numRegistrosInsertadosAsStr = Utilidades.formatDouble(numRegistrosInsertados);
    }

    public double getNumRegistros() {
        return numRegistros;
    }

    public void setNumRegistros(double numRegistros) {
        this.numRegistros = numRegistros;
        this.numRegistrosAsStr = Utilidades.formatDouble(numRegistros);
    }
    
    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public String getTabla() {
        return tabla;
    }

    public void setTabla(String tabla) {
        this.tabla = tabla;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getFechaHoraTraspaso() {
        return fechaHoraTraspaso;
    }

    public void setFechaHoraTraspaso(String fechaHoraTraspaso) {
        this.fechaHoraTraspaso = fechaHoraTraspaso;
    }

    
}
