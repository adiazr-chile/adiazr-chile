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
public class MarcaJsonVO implements Serializable{
    private static final long serialVersionUID = 7899472338799776220L;
   
    /**
     "  coddispositivo":"CENIM_SAN_ANTONIO",
     * "empresacod":"emp01",
     * "rutempleado":"12151123-1",
     * "fechahora":"2019-01-02T09:00:00",
     * "fechahoracalculos":"2019-01-02 09:00:00",
     * "tipomarca":1,
     * "fecha_hora_actualizacion":"2019-02-10T22:55:59.805272",
     * "comentario":null}
     */
    
    private String coddispositivo;
    private String empresacod;
    private String rutempleado;
    private String fechahora;
    private String fechahoracalculos;
    private int tipomarca;
    private String comentario;

    /**
     *
     * @return
     */
    public String getCoddispositivo() {
        return coddispositivo;
    }

    public void setCoddispositivo(String coddispositivo) {
        this.coddispositivo = coddispositivo;
    }

    public String getEmpresacod() {
        return empresacod;
    }

    public void setEmpresacod(String empresacod) {
        this.empresacod = empresacod;
    }

    public String getRutempleado() {
        return rutempleado;
    }

    public void setRutempleado(String rutempleado) {
        this.rutempleado = rutempleado;
    }

    public String getFechahora() {
        return fechahora;
    }

    public void setFechahora(String fechahora) {
        this.fechahora = fechahora;
    }

    public String getFechahoracalculos() {
        return fechahoracalculos;
    }

    public void setFechahoracalculos(String fechahoracalculos) {
        this.fechahoracalculos = fechahoracalculos;
    }

    public int getTipomarca() {
        return tipomarca;
    }

    public void setTipomarca(int tipomarca) {
        this.tipomarca = tipomarca;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
    
    
}
