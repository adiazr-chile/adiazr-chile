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
public class AusenciaVO implements Serializable{

    private static final long serialVersionUID = 6737742295637459620L;

    private int id;
    private String nombre;
    private String nombreCorto;
    private int tipoId;
    private String tipoNombre;
    private int estado;
    private String estadoNombre;
    private String justificaHoras       = "S";
    private String pagadaPorEmpleador   = "S";
    
    public AusenciaVO() {
        
    }

    public String getNombreCorto() {
        return nombreCorto;
    }

    public void setNombreCorto(String nombreCorto) {
        this.nombreCorto = nombreCorto;
    }

    public String getPagadaPorEmpleador() {
        return pagadaPorEmpleador;
    }

    public void setPagadaPorEmpleador(String pagadaPorEmpleador) {
        this.pagadaPorEmpleador = pagadaPorEmpleador;
    }

    public String getJustificaHoras() {
        return justificaHoras;
    }

    public void setJustificaHoras(String justificaHoras) {
        this.justificaHoras = justificaHoras;
    }

    
    public int getTipoId() {
        return tipoId;
    }

    public void setTipoId(int tipoId) {
        this.tipoId = tipoId;
    }

    public String getTipoNombre() {
        return tipoNombre;
    }

    public void setTipoNombre(String tipoNombre) {
        this.tipoNombre = tipoNombre;
    }

    public String getEstadoNombre() {
        return estadoNombre;
    }

    public void setEstadoNombre(String estadoNombre) {
        this.estadoNombre = estadoNombre;
    }

    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

   
    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

   
    
    
}
