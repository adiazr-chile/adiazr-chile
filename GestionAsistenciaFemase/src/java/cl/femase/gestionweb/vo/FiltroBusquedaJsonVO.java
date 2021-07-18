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
public class FiltroBusquedaJsonVO implements Serializable{
    private static final long serialVersionUID = 7895472338799776220L;
   
    private String empresanombre;
    private String deptonombre="";
    private String cenconombre="";

    public String getEmpresanombre() {
        return empresanombre;
    }

    public void setEmpresanombre(String empresanombre) {
        this.empresanombre = empresanombre;
    }

    public String getDeptonombre() {
        return deptonombre;
    }

    public void setDeptonombre(String deptonombre) {
        this.deptonombre = deptonombre;
    }

    public String getCenconombre() {
        return cenconombre;
    }

    public void setCenconombre(String cenconombre) {
        this.cenconombre = cenconombre;
    }
    
}
