/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.vo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Alexander
 */
public class SearchFilterVO implements Serializable{

    private static final long serialVersionUID = 226472295622776147L;

    private String tipoInstrumento;
    private String fechaInicial = "";
    private String fechaFinal   = "";
    private String instrumento;
    private String instrumentoEscaped;
    private String corredor;
    private String rutCliente;
    private String tipoOperacion;
    private String folioTrade = "";
    private ResultsVO resultsVO=new ResultsVO();
    private int totalRegistros; // total de registros encontrados
    private String tipoPrecios;//BEC o BCS
    private String estado="1";//0: No valido, 1: Valido
    private String tipoUsuario = "";// BRK: Broker o OPD: Operador Directo
    private String boardID;
    private String source;
    private boolean isProduction=false;
    private String marketCapitalType;//{'I', 'E'}
    private boolean bySpecificDate;
    
    private String username;
    private String eventType;
    private String alarmCode;
    private String symbols;//symbols separados por coma

    public String getSymbols() {
        return symbols;
    }

    public void setSymbols(String symbols) {
        this.symbols = symbols;
    }

    public String getAlarmCode() {
        return alarmCode;
    }

    public void setAlarmCode(String alarmCode) {
        this.alarmCode = alarmCode;
    }
    
    /**
     *
     * @return
     */
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setInstrumentoEscaped(String instrumentoEscaped) {
        this.instrumentoEscaped = instrumentoEscaped;
    }

    
    public boolean isBySpecificDate() {
        return bySpecificDate;
    }

    public void setBySpecificDate(boolean bySpecificDate) {
        this.bySpecificDate = bySpecificDate;
    }
    
    public String getMarketCapitalType() {
        return marketCapitalType;
    }

    public void setMarketCapitalType(String marketCapitalType) {
        this.marketCapitalType = marketCapitalType;
    }

    public String getRutCliente() {
        return rutCliente;
    }

    public void setRutCliente(String rutCliente) {
        this.rutCliente = rutCliente;
    }

    
    public boolean isProduction() {
        return isProduction;
    }

    public void setIsProduction(boolean isProduction) {
        this.isProduction = isProduction;
    }
    
    
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    
    public String getBoardID() {
        return boardID;
    }

    public void setBoardID(String boardID) {
        this.boardID = boardID;
    }

    public String getFolioTrade() {
        return folioTrade;
    }

    public void setFolioTrade(String folioTrade) {
        this.folioTrade = folioTrade;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
    
    /**
     *
     * @return
     */
    public String getEstado() {
        if (estado==null) estado = "1";
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTipoPrecios() {
        return tipoPrecios;
    }

    public void setTipoPrecios(String tipoPrecios) {
        this.tipoPrecios = tipoPrecios;
    }
    
    /**
     *
     * @return
     */
    public ResultsVO getResultsVO() {
        return resultsVO;
    }

    /**
     *
     * @param resultsVO
     */
    public void setResultsVO(ResultsVO resultsVO) {
        this.resultsVO = resultsVO;
    }

    public int getTotalRegistros() {
        return totalRegistros;
    }

    public void setTotalRegistros(int totalRegistros) {
        this.totalRegistros = totalRegistros;
    }
    
    /**
     * Get the value of tipoOperacion
     *
     * @return the value of tipoOperacion
     */
    public String getTipoOperacion() {
        return tipoOperacion;
    }

    /**
     * Set the value of tipoOperacion
     *
     * @param tipoOperacion new value of tipoOperacion
     */
    public void setTipoOperacion(String tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    /**
     * Get the value of corredor
     *
     * @return the value of corredor
     */
    public String getCorredor() {
        return corredor;
    }

    /**
     * Set the value of corredor
     *
     * @param corredor new value of corredor
     */
    public void setCorredor(String corredor) {
        this.corredor = corredor;
    }

    /**
     * Get the value of instrumento
     *
     * @return the value of instrumento
     */
    public String getInstrumento() {
        return instrumento;
    }

     public String getInstrumentoEscaped() {
        String aux=instrumento;
        this.instrumentoEscaped = aux.replace("'", "\\'");
        //System.out.println("[getMnemonicEscaped]mnemonic: "+aux+
        //        ", escaped: "+mnemonicEscaped);
        return this.instrumentoEscaped;
    }
    
    /**
     * Set the value of instrumento
     *
     * @param instrumento new value of instrumento
     */
    public void setInstrumento(String instrumento) {
        this.instrumento = instrumento;
    }


    /**
     * Get the value of fechaFinal
     *
     * @return the value of fechaFinal
     */
    public String getFechaFinal() {
        return fechaFinal;
    }

    /**
     * Set the value of fechaFinal
     *
     * @param fechaFinal new value of fechaFinal
     */
    public void setFechaFinal(String fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Get the value of fechaInicial
     *
     * @return the value of fechaInicial
     */
    public String getFechaInicial() {
        return fechaInicial;
    }

    /**
     * Set the value of fechaInicial
     *
     * @param fechaInicial new value of fechaInicial
     */
    public void setFechaInicial(String fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Get the value of tipoInstrumento
     *
     * @return the value of tipoInstrumento
     */
    public String getTipoInstrumento() {
        return tipoInstrumento;
    }

    /**
     * Set the value of tipoInstrumento
     *
     * @param tipoInstrumento new value of tipoInstrumento
     */
    public void setTipoInstrumento(String tipoInstrumento) {
        this.tipoInstrumento = tipoInstrumento;
    }

}
