/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cl.femase.gestionweb.vo;

/**
*
* @author aledi
*/
public class AgrupadoAusenciaVO {
    private Integer idAusencia;
    private String nombreAusencia;
    private Long cantidad;

    public AgrupadoAusenciaVO(Integer id, String _nombreAusencia, Long cant) {
        this.idAusencia = id;
        this.nombreAusencia = _nombreAusencia;
        this.cantidad = cant;
    }

    public String getNombreAusencia() {
        return nombreAusencia;
    }

    public void setNombreAusencia(String nombreAusencia) {
        this.nombreAusencia = nombreAusencia;
    }

    public Integer getIdAusencia() {
        return idAusencia;
    }

    public void setIdAusencia(Integer idAusencia) {
        this.idAusencia = idAusencia;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }
    
}
