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
public class DuracionVO implements Serializable{

    private static final long serialVersionUID = 7636522295698779620L;

    private int id;
    private String nombre;
    private int numDias;
    private int orderDisplay;
    
    public DuracionVO() {
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

    public int getNumDias() {
        return numDias;
    }

    public void setNumDias(int numDias) {
        this.numDias = numDias;
    }

    public int getOrderDisplay() {
        return orderDisplay;
    }

    public void setOrderDisplay(int orderDisplay) {
        this.orderDisplay = orderDisplay;
    }

    
    
    
}
