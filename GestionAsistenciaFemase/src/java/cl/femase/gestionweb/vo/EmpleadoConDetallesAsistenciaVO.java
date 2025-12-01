/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cl.femase.gestionweb.vo;

import java.util.List;

/**
 *
 * @author aledi
 */
public class EmpleadoConDetallesAsistenciaVO {
    private String rut;
    private String nombre;
    private String cencoNombre;
    private List<DetalleAsistenciaVO> detalles;

    public String getCencoNombre() {
        return cencoNombre;
    }

    public void setCencoNombre(String cencoNombre) {
        this.cencoNombre = cencoNombre;
    }

    
    
    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<DetalleAsistenciaVO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleAsistenciaVO> detalles) {
        this.detalles = detalles;
    }
    
    

}
