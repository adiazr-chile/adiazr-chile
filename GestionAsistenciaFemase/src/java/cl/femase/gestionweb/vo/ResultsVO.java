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
public class ResultsVO implements Serializable{

    private static final long serialVersionUID = 7526472295622776220L;

    private int offset=-1;
    private int limit=-1;

    // la p�gina actual ?de cara al usuario?
    private int paginaActual; // el usuario empieza a contar de 1, no de 0
    private int paginaAnterior; // aqu� se deber�a de controlar el valor de 0
    private int paginaSiguiente; // la misma raz�n
    private int totalPaginas; // total de paginas
    private int numRegsPorPagina=25; // numero de registros a mostrar

    public ResultsVO() {
        
    }

    public int getNumRegsPorPagina() {
        return numRegsPorPagina;
    }

    public void setNumRegsPorPagina(int numRegsPorPagina) {
        this.numRegsPorPagina = numRegsPorPagina;
    }

   
    public int getTotalPaginas() {
        return totalPaginas;
    }

    public void setTotalPaginas(int totalPaginas) {
        this.totalPaginas = totalPaginas;
    }

    
    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getPaginaActual() {
        return paginaActual;
    }

    public void setPaginaActual(int paginaActual) {
        this.paginaActual = paginaActual;
    }

    /**
     *
     * @return
     */
    public int getPaginaAnterior() {
        return paginaAnterior;
    }

    public void setPaginaAnterior(int paginaAnterior) {
        this.paginaAnterior = paginaAnterior;
    }

    public int getPaginaSiguiente() {
        return paginaSiguiente;
    }

    public void setPaginaSiguiente(int paginaSiguiente) {
        this.paginaSiguiente = paginaSiguiente;
    }

    
}
