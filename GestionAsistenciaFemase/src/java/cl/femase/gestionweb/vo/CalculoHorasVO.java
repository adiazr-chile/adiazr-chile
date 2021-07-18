/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.vo;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 *
 * @author Alexander
 */
public class CalculoHorasVO implements Serializable{

    private static final long serialVersionUID = 632774229569759620L;

    private String fechaHoraCalculo;
    private String empresaId;
    private String deptoId;
    private int cencoId;
    private String rutEmpleado;
    private String fecha;
    
    private LinkedHashMap<String, DetalleAsistenciaVO> listaDetalleCalculo;
            
    /**
     *
     */
    public CalculoHorasVO() {
    }

    /**
     *
     * @return
     */
    public LinkedHashMap<String, DetalleAsistenciaVO> getListaDetalleCalculo() {
        return listaDetalleCalculo;
    }

    public void setListaDetalleCalculo(LinkedHashMap<String, DetalleAsistenciaVO> listaDetalleCalculo) {
        this.listaDetalleCalculo = listaDetalleCalculo;
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

    public String getFechaHoraCalculo() {
        return fechaHoraCalculo;
    }

    public void setFechaHoraCalculo(String fechaHoraCalculo) {
        this.fechaHoraCalculo = fechaHoraCalculo;
    }

    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public String getRutEmpleado() {
        return rutEmpleado;
    }

    /**
     *
     * @param rutEmpleado
     */
    public void setRutEmpleado(String rutEmpleado) {
        this.rutEmpleado = rutEmpleado;
    }

    public String getFecha() {
        return fecha;
    }

    /**
     *
     * @param fecha
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    
}
