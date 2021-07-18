/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.vo;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 *
 * @author Alexander
 */
public class TradeVO implements Serializable{
    private static final long serialVersionUID = 7526472295622776147L;

    //private NumberFormat nf = NumberFormat.getInstance();
//    private NumberFormat numberFormatter =
//            NumberFormat.getNumberInstance(new Locale("es", "ES"));

    private String id;
    private String folio;
    private String folioRepo;
    private String fecha;
    private Date tradedatetime;
    private Date tradedate;
    private String fecha_evento="&nbsp;";
    private String hora;
    private int transacTime;
    private String instrumento;
    private double cantidad;
    
    private double totalTransado;
    private String totalTransadoFmt;
    private double monto;
    private int numNegocios;
    private double precio;
    //aplica para repos
    private double variacion;
    private double precioMayor;
    private double precioMedio;
    private double precioMenor;
    private double precioCierre;
    
    private String condicion="N";
    private double varDiaria;
    private double varMensual;
    private double varAnual;
    private String lugarPago;
    private String metodologia="1";
    private String instancia="1";
    private String comprador="KOM";
    private String vendedor="BEN";
    private String opDirecto="OPD";
    private String mercado;
    private String mercadoNombre;
    private int settleType;
    private String settleTypeStr;
    /** propios de X-Stream*/
    private String boardID;
    private String className;
    private String securityType;
    private String source;

    private double valorPar;
    private boolean future;
    private Date settlementDate;
    private String strSettlementDate;
    private String settlementDate_yyyymmdd;
    private String settlementDate_yyyymmdd_2;
    private String settlementDate_ddmmyyyy_3;
    private String settlementDate_ddmm;
    private String anticipable;
    private boolean fromCrossOrder;
    private boolean maturity;
    private long askOfferFolio;
    private long bidOfferFolio;
    private String[] fundNames=new String[2];
    private boolean VC;//marca de venta corta
    private boolean isRepo;
    private boolean isRepoFuture;
    private boolean isDeleted;
    private int valid;
//    private SideVO sides;
//    private ArrayList<TradeEventVO> events;
    private HashMap<String,Double> amounts;
//    private HashMap<String, MarketsValuesVO> marketsValues;
    
    //para exportacion de archivos
    /** fecha en formato yyyyMMdd*/
    private String fechacorta;
    private String fecha_yyyymmdd;//DATE_FORMAT(trade_datetime, '%Y%m%d') fecha_yyyymmdd,
    private String fecha_ddmmyyyy;//DATE_FORMAT(trade_datetime, '%d%m%Y') fecha_ddmmyyyy,
    private String fecha_ddmmyy;//DATE_FORMAT(trade_datetime, '%d%m%y') fecha_ddmmyy,
    private String fecha_yymmdd;//DATE_FORMAT(trade_datetime, '%y%m%d') fecha_yymmdd,
    private String hora_hhmidd;//DATE_FORMAT(trade_datetime, '%H%i%s') hora_hhmidd,
    private String maturity_date;
    
    /**campos agregados 22-07-2015*/
    private int isOAI;
    private double precioOAI;
    private String precioOAIFmt;
    private String cantidadFmt      = "";
    private String precioFmt        = "";
    private String variacionFmt     = "";
    private String labelMonedaMonto = "";
    private String labelComprador   = "";
    private String labelVendedor    = "";
    private String flagVentaCorta   = "";
    private String fondoComprador   = "";
    private String fondoVendedor    = "";
    private String brokerComprador  = "";
    private String brokerVendedor   = "";
    
    private String precioMenorFmt   = "";
    private String precioMayorFmt   = "";
    private String precioMedioFmt   = "";
    private String varAnualFmt      = "";
    private String varMensualFmt    = "";
    private String varDiariaFmt     = "";
    private String precioCierreFmt  = "";
    
    //**comparadores
    public static class CompTransactTime implements Comparator<TradeVO> {
        private int mod = 1;
        public CompTransactTime(boolean desc) {
            if (desc) mod =-1;
        }
        public int compare(TradeVO arg0, TradeVO arg1) {
            int delta = arg0.transacTime - arg1.transacTime;
            delta = mod * delta;
            return arg0.transacTime - arg1.transacTime;
        }
    }
    
    /**
     *
     */
    public static class CompFolio implements Comparator<TradeVO> {
        private int mod = 1;
        public CompFolio(boolean desc) {
            if (desc) mod =-1;
        }
        @Override
        public int compare(TradeVO arg0, TradeVO arg1) {
            return mod*arg0.folio.compareTo(arg1.folio);
        }
    }
    
    public static class CompInstrumento implements Comparator<TradeVO> {
        private int mod = 1;
        public CompInstrumento(boolean desc) {
            if (desc) mod =-1;
        }
        @Override
        public int compare(TradeVO arg0, TradeVO arg1) {
            return mod*arg0.instrumento.compareTo(arg1.instrumento);
        }
    }
    
    public static class CompCantidad implements Comparator<TradeVO> {
        private int mod = 1;

        /**
         *
         * @param desc
         */
        public CompCantidad(boolean desc) {
            if (desc) mod =-1;
        }
        public int compare(TradeVO arg0, TradeVO arg1) {
            double delta = arg0.cantidad - arg1.cantidad;
            delta = mod * delta;
            if(delta > 0.00001) return 1;
            if(delta < -0.00001) return -1;
            return 0;
        }
    }
    
    public static class CompPrecio implements Comparator<TradeVO> {
        private int mod = 1;
        public CompPrecio(boolean desc) {
            if (desc) mod =-1;
        }
        public int compare(TradeVO arg0, TradeVO arg1) {
            double delta = arg0.precio - arg1.precio;
            delta = mod * delta;
            if(delta > 0.00001) return 1;
            if(delta < -0.00001) return -1;
            return 0;
        }
    }
    
    public static class CompSettleDate implements Comparator<TradeVO> {
        private int mod = 1;
        public CompSettleDate(boolean desc) {
            if (desc) mod =-1;
        }
        @Override
        public int compare(TradeVO arg0, TradeVO arg1) {
            return mod*arg0.settlementDate.compareTo(arg1.settlementDate);
        }
    }
    
    public static class CompTradeDate implements Comparator<TradeVO> {
        private int mod = 1;
        public CompTradeDate(boolean desc) {
            if (desc) mod =-1;
        }
        @Override
        public int compare(TradeVO arg0, TradeVO arg1) {
            return mod*arg0.tradedate.compareTo(arg1.tradedate);
        }
    }
    
    public static class CompTradeDateTime implements Comparator<TradeVO> {
        private int mod = 1;
        public CompTradeDateTime(boolean desc) {
            if (desc) mod =-1;
        }
        @Override
        public int compare(TradeVO arg0, TradeVO arg1) {
            return mod*arg0.tradedatetime.compareTo(arg1.tradedatetime);
        }
    }

    public boolean isFuture() {
        return future;
    }

    public void setFuture(boolean future) {
        this.future = future;
    }

    public boolean isIsRepo() {
        return isRepo;
    }

    public void setIsRepo(boolean isRepo) {
        this.isRepo = isRepo;
    }

    public String getLabelMonedaMonto() {
        return labelMonedaMonto;
    }

    public void setLabelMonedaMonto(String labelMonedaMonto) {
        this.labelMonedaMonto = labelMonedaMonto;
    }

    public String getLabelComprador() {
        return labelComprador;
    }

    public void setLabelComprador(String labelComprador) {
        this.labelComprador = labelComprador;
    }

    public String getLabelVendedor() {
        return labelVendedor;
    }

    public void setLabelVendedor(String labelVendedor) {
        this.labelVendedor = labelVendedor;
    }

    public String getFlagVentaCorta() {
        return flagVentaCorta;
    }

    public void setFlagVentaCorta(String flagVentaCorta) {
        this.flagVentaCorta = flagVentaCorta;
    }

    public String getFondoComprador() {
        return fondoComprador;
    }

    public void setFondoComprador(String fondoComprador) {
        this.fondoComprador = fondoComprador;
    }

    public String getFondoVendedor() {
        return fondoVendedor;
    }

    /**
     *
     * @param fondoVendedor
     */
    public void setFondoVendedor(String fondoVendedor) {
        this.fondoVendedor = fondoVendedor;
    }

    public String getBrokerComprador() {
        return brokerComprador;
    }

    public void setBrokerComprador(String brokerComprador) {
        this.brokerComprador = brokerComprador;
    }

    public String getBrokerVendedor() {
        return brokerVendedor;
    }

    public void setBrokerVendedor(String brokerVendedor) {
        this.brokerVendedor = brokerVendedor;
    }
   
    /**
     *
     * @return
     */
    public int getIsOAI() {
        return isOAI;
    }

    public void setIsOAI(int isOAI) {
        this.isOAI = isOAI;
    }

    /**
     *
     * @return
     */
    public double getPrecioOAI() {
        return precioOAI;
    }

    public String getPrecioOAIFmt(){
        Locale locale  = new Locale("en", "UK");
        String pattern = "###.##";
        DecimalFormat decimalFormat1 = (DecimalFormat)
                NumberFormat.getNumberInstance(locale);
        decimalFormat1.applyPattern(pattern);

        String asformat = decimalFormat1.format(this.precioOAI);
        return asformat;
    }
    
    public void setPrecioOAI(double precioOAI) {
        this.precioOAI = precioOAI;
        Locale locale  = new Locale("en", "UK");
        String pattern = "###.##";
        DecimalFormat decimalFormat1 = (DecimalFormat)
                NumberFormat.getNumberInstance(locale);
        decimalFormat1.applyPattern(pattern);

        this.precioOAIFmt = decimalFormat1.format(this.precioOAI);
        
    }

    public int getValid() {
        return valid;
    }

    public void setValid(int valid) {
        this.valid = valid;
    }
    
    public String getValidAsString() {
        String strValid="Eliminada";
        if (this.valid==1) strValid="Vigente";
            
        return strValid;
    }

    public Date getTradedate() {
        return tradedate;
    }

    public void setTradedate(Date tradedate) {
        this.tradedate = tradedate;
    }

    
    public int getTransacTime() {
        return transacTime;
    }

    public void setTransacTime(int transacTime) {
        this.transacTime = transacTime;
    }

    
    public Date getTradedatetime() {
        return tradedatetime;
    }

    public void setTradedatetime(Date tradedatetime) {
        this.tradedatetime = tradedatetime;
    }
    
    
    public String getFecha_evento() {
        return fecha_evento;
    }

    public void setFecha_evento(String fecha_evento) {
        this.fecha_evento = fecha_evento;
    }

    /** 
     * Entrega fecha de vencimiento en formato dd/MM/yyyy
     */
    public String getSettlementDate_ddmmyyyy_3() {
        return settlementDate_ddmmyyyy_3;
    }

    public void setSettlementDate_ddmmyyyy_3(String settlementDate_ddmmyyyy_3) {
        this.settlementDate_ddmmyyyy_3 = settlementDate_ddmmyyyy_3;
    }

     /**
     * Entrega fecha de vencimiento en formato yyyy/MM/dd
     */
    public String getSettlementDate_yyyymmdd_2() {
        return settlementDate_yyyymmdd_2;
    }

    public void setSettlementDate_yyyymmdd_2(String settlementDate_yyyymmdd_2) {
        this.settlementDate_yyyymmdd_2 = settlementDate_yyyymmdd_2;
    }

    public double getVariacion() {
        return variacion;
    }

    public String getVariacionFmt() {
        return variacionFmt;//numberFormatter.format(variacion);
    }

    public void setVariacion(double variacion) {
        NumberFormat numberFormatter =
            NumberFormat.getNumberInstance(new Locale("es", "ES"));
        this.variacion      = variacion;
        this.variacionFmt   = numberFormatter.format(variacion);
    }

    
    public boolean isRepoFuture() {
        int folio=Integer.parseInt(this.folio);
        int folioRel=10000000;
        isRepoFuture=false;
        if (this.folioRepo!=null && this.folioRepo.compareTo("")>0){
            folioRel = Integer.parseInt(this.folioRepo);
        }
        if (folioRel < folio){
            isRepoFuture=true;
        }
        return isRepoFuture;
    }

    /**
     *
     * @return
     */
    public String getAnticipable() {
        return anticipable;
    }

    public void setAnticipable(String anticipable) {
        this.anticipable = anticipable;
    }

    
    public void setIsRepoFuture(boolean isRepoFuture) {
        this.isRepoFuture = isRepoFuture;
    }


    public String getFolioRepo() {
        return folioRepo;
    }

    public void setFolioRepo(String folioRepo) {
        this.folioRepo = folioRepo;
    }

    
    public boolean isDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }


    public boolean isRepo() {
        boolean esrepo = false;
        
        if ((this.getSource().compareTo("SITREL")==0 && this.getBoardID().startsWith("5-"))
              || (this.getSource().compareTo("X-STREAM")==0 && this.getBoardID().startsWith("SIMUL"))) {
        
            esrepo=true;
        }
        return esrepo;
    }
   

    public String getFormattedMaturityDate() {
        String result=null;
        try{
            String field0 = "0";
            String field1 = "0";
            if (this.mercado.compareTo("1")==0 && this.maturity_date != null){
                //IRF Para mercado=1 (IRF) maturity_date = anios y meses
                StringTokenizer tokenMat=new StringTokenizer(this.maturity_date," ");
                field0 = tokenMat.nextToken();
                field1 = tokenMat.nextToken();
            }else if (this.mercado.compareTo("2")==0  && this.maturity_date != null){
                //IIF Para mercado=2 (IIF) maturity_date = numero de dias
                StringTokenizer tokenMat=new StringTokenizer(this.maturity_date," ");
                field0 = tokenMat.nextToken();
                field1= "0";
            }

//            String field0 = this.maturity_date.substring(0,1);
//            String field1 = this.maturity_date.substring(2);

            //field0=field0.trim();
            //field1=field1.trim();
            int value0 = (new Integer(field0)).intValue();
            int value1 = (new Integer(field1)).intValue();
            // Es necesario averiguar el tipo del instrumento.
            // Es imposible hacerlo a este nivel de la capa wrapper.
            // Aqui se utiliza el truco sucio.
            DecimalFormat formatter = new DecimalFormat();
            formatter.applyPattern("000");
            result = formatter.format(value0);
            formatter.applyPattern("00");
            result = result + formatter.format(value1);
            formatter.applyPattern("00000");
            result = result + formatter.format(value0);
        }catch(NumberFormatException nfe){
            System.err.println("[getFormattedMaturityDate]Error: " + nfe.toString());
            nfe.printStackTrace();
        }
        return result;
  }

    public String getMaturity_date() {
        return maturity_date;
    }

    public void setMaturity_date(String maturity_date) {
        this.maturity_date = maturity_date;
    }

    public String getSettlementDate_ddmm() {
        return settlementDate_ddmm;
    }

    public void setSettlementDate_ddmm(String settlementDate_ddmm) {
        this.settlementDate_ddmm = settlementDate_ddmm;
    }

    
    public Date getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(Date settlementDate) {
        this.settlementDate = settlementDate;
    }

    public String getSettlementDate_yyyymmdd() {
        return settlementDate_yyyymmdd;
    }

    public void setSettlementDate_yyyymmdd(String settlementDate_yyyymmdd) {
        this.settlementDate_yyyymmdd = settlementDate_yyyymmdd;
    }

    public String getStrSettlementDate() {
        return strSettlementDate;
    }

    public void setStrSettlementDate(String strSettlementDate) {
        this.strSettlementDate = strSettlementDate;
    }

//    public HashMap<String, MarketsValuesVO> getMarketsValues() {
//        return marketsValues;
//    }
//
//    public void setMarketsValues(HashMap<String, MarketsValuesVO> marketsValues) {
//        this.marketsValues = marketsValues;
//    }
//
//    
    public String getFecha_ddmmyy() {
        return fecha_ddmmyy;
    }

    /**
     *
     * @param fecha_ddmmyy
     */
    public void setFecha_ddmmyy(String fecha_ddmmyy) {
        this.fecha_ddmmyy = fecha_ddmmyy;
    }

    /**
     *
     * @return
     */
    public String getFecha_ddmmyyyy() {
        return fecha_ddmmyyyy;
    }

    public void setFecha_ddmmyyyy(String fecha_ddmmyyyy) {
        this.fecha_ddmmyyyy = fecha_ddmmyyyy;
    }

    public String getFecha_yymmdd() {
        return fecha_yymmdd;
    }

    public void setFecha_yymmdd(String fecha_yymmdd) {
        this.fecha_yymmdd = fecha_yymmdd;
    }

    public String getFecha_yyyymmdd() {
        return fecha_yyyymmdd;
    }

    public void setFecha_yyyymmdd(String fecha_yyyymmdd) {
        this.fecha_yyyymmdd = fecha_yyyymmdd;
    }

    public String getFechacorta() {
        return fechacorta;
    }

    public void setFechacorta(String fechacorta) {
        this.fechacorta = fechacorta;
    }

    /**
     *
     * @return
     */
    public String getHora_hhmidd() {
        return hora_hhmidd;
    }

    public void setHora_hhmidd(String hora_hhmidd) {
        this.hora_hhmidd = hora_hhmidd;
    }

    

    public boolean isVC() {
        return VC;
    }

    public void setVC(boolean VC) {
        this.VC = VC;
    }

    
    public long getAskOfferFolio() {
        return askOfferFolio;
    }

    public void setAskOfferFolio(long askOfferFolio) {
        this.askOfferFolio = askOfferFolio;
    }

    public long getBidOfferFolio() {
        return bidOfferFolio;
    }

    public void setBidOfferFolio(long bidOfferFolio) {
        this.bidOfferFolio = bidOfferFolio;
    }

    public boolean isFromCrossOrder() {
        return fromCrossOrder;
    }

    public void setFromCrossOrder(boolean fromCrossOrder) {
        this.fromCrossOrder = fromCrossOrder;
    }

    public String[] getFundNames() {
        return fundNames;
    }

    public void setFundNames(String[] fundNames) {
        this.fundNames = fundNames;
    }

    public boolean isMaturity() {
        return maturity;
    }

    public void setMaturity(boolean maturity) {
        this.maturity = maturity;
    }


    public double getValorPar() {
        return valorPar;
    }

    public void setValorPar(double valorPar) {
        this.valorPar = valorPar;
    }

    

    public String getSettleTypeStr() {
        settleTypeStr = String.valueOf(settleType);
        
        if (!this.isRepoFuture()){
            switch(settleType){
                case 1:settleTypeStr="PH";
                        break;
                case 2:settleTypeStr="PM";
                        break;
                case 3:settleTypeStr="CN";
                        break;
                default: settleTypeStr = String.valueOf(settleType);
                        break;
            }
        }

        if (this.boardID.compareTo("OFFSHORE")==0){
            settleTypeStr = "T+3";
        }
        
        return settleTypeStr;
    }

    public String getSettleTypeStrExport() {
        settleTypeStr = String.valueOf(settleType);
        settleTypeStr = "TPA";
        if (!this.isRepoFuture()){
            switch(settleType){
                case 1:settleTypeStr="PH";
                        break;
                case 2:settleTypeStr="PM";
                        break;
                case 3:settleTypeStr="CN";
                        break;
                default: settleTypeStr = "TPA";
                        break;
            }
        }
        return settleTypeStr;
    }

    public HashMap<String, Double> getAmounts() {
        return amounts;
    }

    public void setAmounts(HashMap<String, Double> amounts) {
        this.amounts = amounts;
    }

    /**
     *
     * @return
     */
    public int getSettleType() {
        return settleType;
    }

    public void setSettleType(int _settleType) {
        this.settleType = _settleType;
        
        this.settleTypeStr = String.valueOf(_settleType);
        
        if (!this.isRepoFuture()){
            switch(_settleType){
                case 1:this.settleTypeStr="PH";
                        break;
                case 2:this.settleTypeStr="PM";
                        break;
                case 3:this.settleTypeStr="CN";
                        break;
                default: this.settleTypeStr = String.valueOf(_settleType);
                        break;
            }
        }

        if (this.boardID.compareTo("OFFSHORE")==0){
            this.settleTypeStr = "T+3";
        }
    }


    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getMontoFmt() {
        NumberFormat numberFormatter =
            NumberFormat.getNumberInstance(new Locale("es", "ES"));
        return numberFormatter.format(monto);
    }

    public String getPrecioFmt() {
        return precioFmt;//numberFormatter.format(precio);
    }

    public String getCantidadFmt() {
        return cantidadFmt;//numberFormatter.format(cantidad);
    }

    public String getTotalTransadoFmt() {
        NumberFormat numberFormatter =
            NumberFormat.getNumberInstance(new Locale("es", "ES"));
        return numberFormatter.format(totalTransado);
    }

    public String getPrecioMayorFmt() {
        NumberFormat numberFormatter =
            NumberFormat.getNumberInstance(new Locale("es", "ES"));
        return numberFormatter.format(precioMayor);
    }

    public String getPrecioMedioFmt() {
        NumberFormat numberFormatter =
            NumberFormat.getNumberInstance(new Locale("es", "ES"));
        return numberFormatter.format(precioMedio);
    }

    public String getPrecioMenorFmt() {
        NumberFormat numberFormatter =
            NumberFormat.getNumberInstance(new Locale("es", "ES"));
        return numberFormatter.format(precioMenor);
    }

    public String getPrecioCierreFmt() {
        NumberFormat numberFormatter =
            NumberFormat.getNumberInstance(new Locale("es", "ES"));
        return numberFormatter.format(precioCierre);
    }

    public String getVarDiariaFmt() {
        NumberFormat numberFormatter =
            NumberFormat.getNumberInstance(new Locale("es", "ES"));
        return numberFormatter.format(varDiaria);
    }

    /**
     *
     * @return
     */
    public String getVarMensualFmt() {
        NumberFormat numberFormatter =
            NumberFormat.getNumberInstance(new Locale("es", "ES"));
        return numberFormatter.format(varMensual);
    }

    public String getVarAnualFmt() {
        NumberFormat numberFormatter =
            NumberFormat.getNumberInstance(new Locale("es", "ES"));
        return numberFormatter.format(varAnual);
    }

//    public ArrayList<TradeEventVO> getEvents() {
//        return events;
//    }
//
//    public void setEvents(ArrayList<TradeEventVO> events) {
//        this.events = events;
//    }
//
//    public SideVO getSides() {
//        return sides;
//    }
//
//    public void setSides(SideVO _sides) {
//        this.sides = _sides;
//    }

    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    /**
     * Get the value of source
     *
     * @return the value of source
     */
    public String getSource() {
        return source;
    }

    /**
     * Set the value of source
     *
     * @param source new value of source
     */
    public void setSource(String source) {
        this.source = source;
    }

    public String getMercado() {
        return mercado;
    }

    public void setMercado(String mercado) {
        this.mercado = mercado;
        if (this.mercado.compareTo("0")==0){
            this.mercadoNombre="ACC";
        }else if (this.mercado.compareTo("1")==0){
            this.mercadoNombre="IRF";
        }if (this.mercado.compareTo("2")==0){
            this.mercadoNombre="IIF";
        }
    }

    public String getMercadoNombre() {
//        if (this.mercado.compareTo("0")==0){
//            this.mercadoNombre="ACC";
//        }else if (this.mercado.compareTo("1")==0){
//            this.mercadoNombre="IRF";
//        }if (this.mercado.compareTo("2")==0){
//            this.mercadoNombre="IIF";
//        }
        
        return mercadoNombre;
    }

    public void setMercadoNombre(String mercadoNombre) {
        this.mercadoNombre = mercadoNombre;
    }


    

    /**
     * Get the value of opDirecto
     *
     * @return the value of opDirecto
     */
    public String getOpDirecto() {
        return opDirecto;
    }

    /**
     * Set the value of opDirecto
     *
     * @param opDirecto new value of opDirecto
     */
    public void setOpDirecto(String opDirecto) {
        this.opDirecto = opDirecto;
    }

    /**
     * Get the value of vendedor
     *
     * @return the value of vendedor
     */
    public String getVendedor() {
        return vendedor;
    }

    /**
     * Set the value of vendedor
     *
     * @param vendedor new value of vendedor
     */
    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    /**
     * Get the value of comprador
     *
     * @return the value of comprador
     */
    public String getComprador() {
        return comprador;
    }

    /**
     * Set the value of comprador
     *
     * @param comprador new value of comprador
     */
    public void setComprador(String comprador) {
        this.comprador = comprador;
    }

  
    /**
     * Get the value of securityType
     *
     * @return the value of securityType
     */
    public String getSecurityType() {
        return securityType;
    }

    /**
     * Set the value of securityType
     *
     * @param securityType new value of securityType
     */
    public void setSecurityType(String securityType) {
        this.securityType = securityType;
    }

    /**
     * Get the value of className
     *
     * @return the value of className
     */
    public String getClassName() {
        return className;
    }

    /**
     * Set the value of className
     *
     * @param className new value of className
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Get the value of boardID
     *
     * @return the value of boardID
     */
    public String getBoardID() {
        return boardID;
    }

    /**
     * Set the value of boardID
     *
     * @param boardID new value of boardID
     */
    public void setBoardID(String boardID) {
        this.boardID = boardID;
        this.metodologia= "1";
        this.instancia  = "1";
        //revisar si el boardId es de la forma 'metodologia-instancia'
        if (this.source!=null && this.source.compareTo("SITREL")==0){
            int pos=boardID.indexOf("-");
            if (pos > 0){
                this.metodologia= boardID.substring(0, pos);
                this.instancia  = boardID.substring(pos+1, boardID.length());
            }
        }
    }

    /**
     * Get the value of instancia
     *
     * @return the value of instancia
     */
    public String getInstancia() {
        return instancia;
    }

    /**
     * Set the value of instancia
     *
     * @param instancia new value of instancia
     */
    public void setInstancia(String instancia) {
        this.instancia = instancia;
    }

    /**
     * Get the value of metodologia
     *
     * @return the value of metodologia
     */
    public String getMetodologia() {
        return metodologia;
    }

    /**
     * Set the value of metodologia
     *
     * @param metodologia new value of metodologia
     */
    public void setMetodologia(String metodologia) {
        this.metodologia = metodologia;
    }


    /**
     * Get the value of lugarPago
     *
     * @return the value of lugarPago
     */
    public String getLugarPago() {
        if (lugarPago != null && lugarPago.compareTo("D")==0){
            lugarPago = "DCV";
        }else if (lugarPago != null && lugarPago.compareTo("N")==0){
            lugarPago = "-";
        }
        return lugarPago;
    }

    /**
    * Set the value of lugarPago
    *
    * @param _lugarPago
    */
    public void setLugarPago(String _lugarPago) {
        if (_lugarPago != null && _lugarPago.compareTo("D")==0){
            _lugarPago = "DCV";
        }else if (_lugarPago != null && _lugarPago.compareTo("N")==0){
            _lugarPago = "-";
        }
        this.lugarPago = _lugarPago;
    }

    /**
     * Get the value of varAnual
     *
     * @return the value of varAnual
     */
    public double getVarAnual() {
        return varAnual;
    }

    /**
    * Set the value of varAnual
    *
    * @param _varAnual
    */
    public void setVarAnual(double _varAnual) {
        this.varAnual = _varAnual;
        NumberFormat numberFormatter =
            NumberFormat.getNumberInstance(new Locale("es", "ES"));
        this.varAnualFmt = numberFormatter.format(_varAnual);
    }

    /**
     * Get the value of varMensual
     *
     * @return the value of varMensual
     */
    public double getVarMensual() {
        return varMensual;
    }

    /**
     * Set the value of varMensual
     *
     * @param _varMensual
     */
    public void setVarMensual(double _varMensual) {
        this.varMensual = _varMensual;
        NumberFormat numberFormatter =
            NumberFormat.getNumberInstance(new Locale("es", "ES"));
        this.varMensualFmt = numberFormatter.format(_varMensual);
    }


    /**
     * Get the value of varDiaria
     *
     * @return the value of varDiaria
     */
    public double getVarDiaria() {
        return varDiaria;
    }

    /**
    * Set the value of varDiaria
    *
    * @param _varDiaria
    */
    public void setVarDiaria(double _varDiaria) {
        this.varDiaria = _varDiaria;
        NumberFormat numberFormatter =
            NumberFormat.getNumberInstance(new Locale("es", "ES"));
        this.varDiariaFmt = numberFormatter.format(_varDiaria);
    }

    /**
     * Get the value of condicion
     *
     * @return the value of condicion
     */
    public String getCondicion() {

        if (condicion!=null){
            if (condicion.compareTo("N")==0) condicion="Nominal";
            else if (condicion.compareTo("T")==0) condicion="Transaccion";
        }else condicion="";
        return condicion;
    }

    /**
     * Set the value of condicion
     *
     * @param condicion new value of condicion
     */
    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }


    /**
     * Get the value of precio
     *
     * @return the value of precio
     */
    public double getPrecio() {
        return precio;
    }

    /**
     * Set the value of precio
     *
     * @param precio new value of precio
     */
    public void setPrecio(double precio) {
        NumberFormat numberFormatter =
            NumberFormat.getNumberInstance(new Locale("es", "ES"));
        this.precio = precio;
        this.precioFmt = numberFormatter.format(precio);
    }



    /**
     * Get the value of precioCierre
     *
     * @return the value of precioCierre
     */
    public double getPrecioCierre() {
        return precioCierre;
    }

    /**
     * Set the value of precioCierre
     *
     * @param _precioCierre
     */
    public void setPrecioCierre(double _precioCierre) {
        this.precioCierre = _precioCierre;
        NumberFormat numberFormatter =
            NumberFormat.getNumberInstance(new Locale("es", "ES"));
        this.precioCierreFmt = numberFormatter.format(_precioCierre);
    }

    /**
     * Get the value of precioMenor
     *
     * @return the value of precioMenor
     */
    public double getPrecioMenor() {
        return precioMenor;
    }

    /**
    * Set the value of precioMenor
    *
    * @param _precioMenor
    */
    public void setPrecioMenor(double _precioMenor) {
        this.precioMenor = _precioMenor;
        NumberFormat numberFormatter =
            NumberFormat.getNumberInstance(new Locale("es", "ES"));
        this.precioMenorFmt = numberFormatter.format(_precioMenor);
    }

    /**
     * Get the value of precioMedio
     *
     * @return the value of precioMedio
     */
    public double getPrecioMedio() {
        return precioMedio;
    }

    /**
    * Set the value of precioMedio
    *
    * @param _precioMedio
    */
    public void setPrecioMedio(double _precioMedio) {
        this.precioMedio = _precioMedio;
        NumberFormat numberFormatter =
            NumberFormat.getNumberInstance(new Locale("es", "ES"));
        this.precioMedioFmt = numberFormatter.format(_precioMedio);
    }

    /**
     * Get the value of precioMayor
     *
     * @return the value of precioMayor
     */
    public double getPrecioMayor() {
        return precioMayor;
    }

    /**
     * Set the value of precioMayor
     *
     * @param _precioMayor
     */
    public void setPrecioMayor(double _precioMayor) {
        this.precioMayor = _precioMayor;
        NumberFormat numberFormatter =
            NumberFormat.getNumberInstance(new Locale("es", "ES"));
        this.precioMayorFmt = numberFormatter.format(_precioMayor);
    }

    /**
     * Get the value of numNegocios
     *
     * @return the value of numNegocios
     */
    public int getNumNegocios() {
        return numNegocios;
    }

    /**
     * Set the value of numNegocios
     *
     * @param numNegocios new value of numNegocios
     */
    public void setNumNegocios(int numNegocios) {
        this.numNegocios = numNegocios;
    }

    /**
     * Get the value of totalTransado
     *
     * @return the value of totalTransado
     */
    public double getTotalTransado() {
        return totalTransado;
    }

    /**
     * Set the value of totalTransado
     *
     * @param totalTransado new value of totalTransado
     */
    public void setTotalTransado(double totalTransado) {
        this.totalTransado = totalTransado;
        NumberFormat numberFormatter =
            NumberFormat.getNumberInstance(new Locale("es", "ES"));
        this.totalTransadoFmt = numberFormatter.format(totalTransado);
    }


    /**
     * Get the value of cantidad
     *
     * @return the value of cantidad
     */
    public double getCantidad() {
        return cantidad;
    }

    /**
     * Set the value of cantidad
     *
     * @param cantidad new value of cantidad
     */
    public void setCantidad(double cantidad) {
        NumberFormat numberFormatter =
            NumberFormat.getNumberInstance(new Locale("es", "ES"));
        this.cantidad       = cantidad;
        this.cantidadFmt    = numberFormatter.format(cantidad);
    }


    /**
     * Get the value of instrumento
     *
     * @return the value of instrumento
     */
    public String getInstrumento() {
        return instrumento;
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
     * Get the value of fecha
     *
     * @return the value of fecha
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * Set the value of fecha
     *
     * @param fecha new value of fecha
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }


    /**
     * Get the value of hora
     *
     * @return the value of hora
     */
    public String getHora() {
        return hora;
    }

    /**
     * Set the value of hora
     *
     * @param hora new value of hora
     */
    public void setHora(String hora) {
        this.hora = hora;
    }

    /**
     * Get the value of folio
     *
     * @return the value of folio
     */
    public String getFolio() {
        return folio;
    }

    /**
     * Set the value of folio
     *
     * @param folio new value of folio
     */
    public void setFolio(String folio) {
        this.folio = folio;
    }

    public void setCantidadFmt(String cantidadFmt) {
        this.cantidadFmt = cantidadFmt;
    }

    public void setTotalTransadoFmt(String totalTransadoFmt) {
        this.totalTransadoFmt = totalTransadoFmt;
    }
    
    
}
