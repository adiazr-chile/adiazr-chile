/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.CentroCostoVO;
import cl.femase.gestionweb.vo.InfoMarcaVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Alexander
 */
public class VisorMarcasBp  extends BaseBp{

    public PropertiesVO props;
    private final cl.femase.gestionweb.dao.MarcasDAO marcasDao;
    
    private static String SIN_TURNO = "Sin turno";
    private static final int MARCA_ENTRADA   = 1;
    private static final int MARCA_SALIDA    = 2;
    private static final Locale LOCALE_CL = new Locale("es", "CL");
    private static final HashMap<Integer,String> DIAS_SEMANA = new HashMap<>();
    private static final HashMap<Integer,String> TIPO_EVENTO = new HashMap<>();
    private static final SimpleDateFormat formatFechaLabel = new SimpleDateFormat("dd/MM/yyyy");
    
    private static final int FALTA_MARCA_ENTRADA    = 100;
    private static final int FALTA_MARCA_SALIDA     = 200;
    private static final int AUSENCIA               = 300;
    private static final int FALTAN_AMBAS_MARCAS    = 400;
    private static final int LIBRE                  = 500;
    private static final int FERIADO                = 600;
    private static final int HAY_MARCA              = 700;
    
    public VisorMarcasBp() {
        marcasDao = new cl.femase.gestionweb.dao.MarcasDAO(this.props);
    }
    
    public VisorMarcasBp(PropertiesVO props) {
        this.props = props;
        marcasDao = new cl.femase.gestionweb.dao.MarcasDAO(this.props);
        
        DIAS_SEMANA.put(1, "Lu");
        DIAS_SEMANA.put(2, "Ma");
        DIAS_SEMANA.put(3, "Mi");
        DIAS_SEMANA.put(4, "Ju");
        DIAS_SEMANA.put(5, "Vi");
        DIAS_SEMANA.put(6, "Sa");
        DIAS_SEMANA.put(7, "Do");
        
        TIPO_EVENTO.put(FALTA_MARCA_ENTRADA, "Falta Marca Entrada");
        TIPO_EVENTO.put(FALTA_MARCA_SALIDA, "Falta Marca Salida");
        TIPO_EVENTO.put(AUSENCIA, "Ausencia");
        TIPO_EVENTO.put(FALTAN_AMBAS_MARCAS, "Faltan ambas marcas");
        TIPO_EVENTO.put(LIBRE, "Libre");
        TIPO_EVENTO.put(FERIADO, "Feriado");
        TIPO_EVENTO.put(HAY_MARCA, "Hay marca");
    }
    
    /**
    *   21-09-2019 
    * 
    */
    
    /**
    * 
     * @param _empresaId
     * @param _rutEmpleado
     * @param _startDate
     * @param _endDate
     * @param _regionIdEmpleado
     * @param _comunaIdEmpleado
     * @param _infoCenco
     * @return 
    */
    public LinkedHashMap<String, InfoMarcaVO> 
        setMarcasTurnoNormal(String _empresaId, 
            String _rutEmpleado,
            String _startDate,
            String _endDate,
            int _regionIdEmpleado, 
            int _comunaIdEmpleado, 
            CentroCostoVO _infoCenco){
        
        LinkedHashMap<String,InfoMarcaVO> hashMarcasFinal
            = new LinkedHashMap<>();
                    
        LinkedHashMap<String, InfoMarcaVO> hashMarcas 
            = getHashMarcasTurnoNormal(_empresaId,
                _rutEmpleado, 
                _startDate, 
                _endDate, 
                _regionIdEmpleado, 
                _comunaIdEmpleado, 
                _infoCenco);
        System.out.println(WEB_NAME+"[VisorMarcasBp.setMarcasTurnoNormal]"
            + "*********************************************");
        System.out.println(WEB_NAME+"[VisorMarcasBp.setMarcasTurnoNormal]"
            + "SET INICIAL MARCAS TURNO NORMAL. "
            + "Empresa_id= " + _empresaId
            + ", rut_empleado: " + _rutEmpleado
            + ", fecha_desde: " + _startDate
            + ", fecha_hasta: " + _endDate);
        
        Set set = hashMarcas.entrySet();
        Iterator iterator = set.iterator();
        int numMarcas   = 0;
        int correlativo = 0;
        String fechaAnterior = "";
        String fechaLabelAnterior = "";
        boolean tipoMarcaDuplicadoAnterior  = false;
        int tipoMarcaAnterior = 0;
        int codDiaAnterior = 0;
        int idTurnoAsignadoAnterior = 0;
        String horaEntradaTurnoAnterior = "";
        String horaSalidaTurnoAnterior = "";
        while (iterator.hasNext()) {
            Map.Entry item = (Map.Entry) iterator.next();
            String fechaKey = (String)item.getKey();
            InfoMarcaVO registro = (InfoMarcaVO)item.getValue();
            registro.setMasInfo(getInfoAdicional(registro));
            
            if (registro.getHoraInicioAusencia()!=null){
                registro.setMasInfo(registro.getAusenciaNombre()
                    +" de " 
                    + registro.getHoraInicioAusencia() 
                    + " a " 
                    + registro.getHoraFinAusencia());
                System.out.println(WEB_NAME+"[VisorMarcasBp.setMarcasTurnoNormal]"
                    + "seteo de ausencia por hora.NEW: "+registro.getMasInfo());
            }
            
            if (fechaAnterior.compareTo(registro.getFecha()) != 0){//nueva fecha
                //if (numMarcas == 1 || tipoMarcaDuplicadoAnterior){
                if (numMarcas == 1){
                    String faltaMarca = "Salida.";
                    int tipoEvento = FALTA_MARCA_SALIDA;
                    if (tipoMarcaAnterior == MARCA_SALIDA){
                        faltaMarca = "Entrada.";
                        tipoEvento = FALTA_MARCA_ENTRADA;
                    }
                    System.out.println(WEB_NAME+"[VisorMarcasBp.setMarcasTurnoNormal]Fecha = " + DIAS_SEMANA.get(codDiaAnterior) + " " + fechaAnterior 
                        + " ---> Falta marca de "+faltaMarca);
                    String fieldFechaAnt = DIAS_SEMANA.get(codDiaAnterior) + " " + fechaLabelAnterior;
                    String fieldEventoAnt = "Falta marca de " + faltaMarca;
                    InfoMarcaVO registroAux = new InfoMarcaVO();
                    registroAux.setEmpresaId(_empresaId);
                    registroAux.setRutEmpleado(_rutEmpleado);
                    registroAux.setFecha(fechaAnterior);
                    registroAux.setCodDia(codDiaAnterior);
                    registroAux.setFieldFecha(fieldFechaAnt);
                    registroAux.setFieldEvento(fieldEventoAnt);
                    registroAux.setTipoEvento(tipoEvento);
                    registroAux.setTipoMarca(tipoEvento);
                    if (idTurnoAsignadoAnterior != -1){
                        registroAux.setLabelTurno("(" + idTurnoAsignadoAnterior + ") "
                            + horaEntradaTurnoAnterior
                            + " a " + horaSalidaTurnoAnterior);
                    }
                    hashMarcasFinal.put(fechaLabelAnterior + "|" 
                        + fieldEventoAnt+"|"+correlativo, registroAux);
                    correlativo++;
                    fechaAnterior = null;
                    tipoMarcaDuplicadoAnterior = false;
                    tipoMarcaAnterior = -1;
                    codDiaAnterior = -1;
                    idTurnoAsignadoAnterior = -1;
                    horaEntradaTurnoAnterior = "";
                    horaSalidaTurnoAnterior = "";
                } 
                numMarcas = 0;
            }else{
                if (registro.getTipoMarca() == tipoMarcaAnterior){
                    registro.setTipoMarcaDuplicado(true);
                    tipoMarcaDuplicadoAnterior = true;
                }
            }
            
            if (registro.getTipoMarca() == -1 
                    && registro.getIdTurnoAsignado() != -1){
                String aux = TIPO_EVENTO.get(FALTAN_AMBAS_MARCAS);
                int tipoEvento = FALTAN_AMBAS_MARCAS;
                
                if (registro.getLabelCalendario() != null){
                    System.out.println(WEB_NAME+"[VisorMarcasBp.setMarcasTurnoNormal]Fecha = " + DIAS_SEMANA.get(registro.getCodDia()) + " " + registro.getFechaLabel() 
                        + " ---> Feriado(" + registro.getLabelCalendario() + ")");
                    aux = "Feriado(" + registro.getLabelCalendario() + ")";
                    tipoEvento = FERIADO;
                    registro.setTipoEvento(FERIADO);
                    registro.setTipoMarca(FERIADO);
                    registro.setFieldEvento("Feriado (" + registro.getLabelCalendario() + ")");
                }
                if (registro.getAusenciaNombre()!=null){
                    if (registro.getAusenciaPorHora().compareTo("N")==0){
                        aux = registro.getAusenciaNombre();
                        tipoEvento = AUSENCIA;
                        registro.setMasInfo(aux);
                    }
                }
                registro.setFieldFecha(DIAS_SEMANA.get(registro.getCodDia()) + " " + registro.getFechaLabel());
                registro.setFieldEvento(aux);
                registro.setTipoEvento(tipoEvento);
                registro.setTipoMarca(tipoEvento);
                registro.setLabelTurno("(" + registro.getIdTurnoAsignado() + ") "
                    + registro.getHoraEntradaTurno()
                    + " a " + registro.getHoraSalidaTurno() );
                
                hashMarcasFinal.put(registro.getFieldFecha() + "|" 
                    + registro.getFieldEvento(), registro);
                System.out.println(WEB_NAME+"[VisorMarcasBp.setMarcasTurnoNormal]"
                    + "Fecha = " + DIAS_SEMANA.get(registro.getCodDia()) + " " + registro.getFechaLabel() 
                    + " --->"+ aux 
                    +". num marcas= " + numMarcas
                    + ". IdTurnoAsignado= " + registro.getIdTurnoAsignado() 
                    + ", entradaTeorica = " + registro.getHoraEntradaTurno()
                    + ", salidaTeorica = " + registro.getHoraSalidaTurno()
                    + ", nombreAusencia = " + registro.getAusenciaNombre()
                    + ", ausencia por hora = " + registro.getAusenciaPorHora());
            }else if (registro.getIdTurnoAsignado() == -1){
                    String aux = "";
                    boolean tieneMarcas=false;
                    
                    if (registro.getFechaHoraMarca()!=null){
                        aux = ". " + registro.getLabelTipoMarca()
                            + ", fechaHora:" + registro.getFechaHoraMarca();
                        tieneMarcas = true;
                        System.out.println(WEB_NAME+"[VisorMarcasBp.setMarcasTurnoNormal]"
                            + "Fecha = " + DIAS_SEMANA.get(registro.getCodDia()) + " " + registro.getFechaLabel() 
                            + " ---> Libre, tipo_marca: " + registro.getTipoMarca() );
                    }
                    registro.setFieldFecha(DIAS_SEMANA.get(registro.getCodDia()) + " " + registro.getFechaLabel());
                    registro.setLabelTurno("Sin turno");
                    if (registro.getLabelCalendario()==null){
                        System.out.println(WEB_NAME+"[VisorMarcasBp.setMarcasTurnoNormal]"
                            + "Fecha = " + DIAS_SEMANA.get(registro.getCodDia()) + " " + registro.getFechaLabel() 
                            + " ---> Libre");
                        registro.setTipoEvento(LIBRE);    
                        registro.setFieldEvento(TIPO_EVENTO.get(LIBRE)+ aux );
                        registro.setMasInfo(registro.getFieldEvento());
                        if (!tieneMarcas) registro.setTipoMarca(LIBRE);
                        
                    }else{
                        System.out.println(WEB_NAME+"[VisorMarcasBp.setMarcasTurnoNormal]"
                            + "Fecha = " + DIAS_SEMANA.get(registro.getCodDia()) + " " + registro.getFechaLabel() 
                            + " ---> Feriado(" + registro.getLabelCalendario() + ")");
                        registro.setTipoEvento(FERIADO);
                        if (!tieneMarcas) registro.setTipoMarca(FERIADO);
                        registro.setFieldEvento("Feriado (" + registro.getLabelCalendario() + ")"+ aux);
                        registro.setMasInfo(registro.getFieldEvento());
                    }
                        
                    hashMarcasFinal.put(registro.getFieldFecha() + "|" 
                        + registro.getFieldEvento(), registro);
            } else{//hay registro de marca
                    registro.setFieldFecha(DIAS_SEMANA.get(registro.getCodDia()) + " " + registro.getFechaLabel());
                    registro.setTipoEvento(HAY_MARCA);
                    registro.setFieldEvento("Hay marca de " + registro.getLabelTipoMarca()
                        + ", fechaHora:" + registro.getFechaHoraMarca());
                    registro.setLabelTurno("(" + registro.getIdTurnoAsignado() + ") "
                        + registro.getHoraEntradaTurno()
                        + " a " + registro.getHoraSalidaTurno() );
                    
                    hashMarcasFinal.put(registro.getFieldFecha() + "|" 
                        + registro.getFieldEvento(), registro);
                    System.out.println(WEB_NAME+"[VisorMarcasBp.setMarcasTurnoNormal]"
                        + "Fecha = " + DIAS_SEMANA.get(registro.getCodDia()) + " " + registro.getFecha() 
                        + " --->Datos de marca existente."
                        + " IdTurnoAsignado = " + registro.getIdTurnoAsignado()
                        + ", fechaHoraMarca = " + registro.getFechaHoraMarca()
                        + ", tipoMarca = " + registro.getLabelTipoMarca());
                    numMarcas++;
                    registro.setNumMarcas(numMarcas);
            }
            fechaAnterior       = registro.getFecha();
            fechaLabelAnterior  = registro.getFechaLabel();
            tipoMarcaAnterior   = registro.getTipoMarca();
            codDiaAnterior      = registro.getCodDia();
            idTurnoAsignadoAnterior = registro.getIdTurnoAsignado();
            horaEntradaTurnoAnterior = registro.getHoraEntradaTurno();
            horaSalidaTurnoAnterior = registro.getHoraSalidaTurno();
            //revisar cuando hay turno o no....   
        }
        
        return hashMarcasFinal;
    }
        
    /**
    * 
     * @param _empresaId
     * @param _rutEmpleado
     * @param _startDate
     * @param _endDate
     * @param _regionIdEmpleado
     * @param _comunaIdEmpleado
     * @param _infoCenco
     * @return 
    */
    public LinkedHashMap<String, InfoMarcaVO> 
        setMarcasTurnoRotativo(String _empresaId, 
            String _rutEmpleado,
            String _startDate,
            String _endDate,
            int _regionIdEmpleado, 
            int _comunaIdEmpleado, 
            CentroCostoVO _infoCenco){
        
        LinkedHashMap<String,InfoMarcaVO> hashMarcasFinal
            = new LinkedHashMap<>();
                    
        LinkedHashMap<String, InfoMarcaVO> hashMarcas 
            = getHashMarcasTurnoRotativo(_empresaId,
                _rutEmpleado, 
                _startDate, 
                _endDate, 
                _regionIdEmpleado, 
                _comunaIdEmpleado, 
                _infoCenco);
        System.out.println(WEB_NAME+"[VisorMarcasBp.setMarcasTurnoRotativo]"
            + "*********************************************");
        System.out.println(WEB_NAME+"[VisorMarcasBp.setMarcasTurnoRotativo]"
            + "SET INICIAL MARCAS TURNO ROTATIVO. "
            + "Empresa_id= " + _empresaId
            + ", rut_empleado: " + _rutEmpleado
            + ", fecha_desde: " + _startDate
            + ", fecha_hasta: " + _endDate);
        
        Set set = hashMarcas.entrySet();
        Iterator iterator = set.iterator();
        int numMarcas   = 0;
        int correlativo = 0;
        String fechaAnterior = "";
        String fechaLabelAnterior = "";
        boolean tipoMarcaDuplicadoAnterior  = false;
        int tipoMarcaAnterior = 0;
        int codDiaAnterior = 0;
        int idTurnoAsignadoAnterior = 0;
        String horaEntradaTurnoAnterior = "";
        String horaSalidaTurnoAnterior = "";
        
        while (iterator.hasNext()) {
            Map.Entry item = (Map.Entry) iterator.next();
            String fechaKey = (String)item.getKey();
            InfoMarcaVO registro = (InfoMarcaVO)item.getValue(); 
            registro.setMasInfo(getInfoAdicional(registro));
            if (registro.getHoraInicioAusencia()!=null){
                registro.setMasInfo(registro.getAusenciaNombre()
                    +" de " 
                    + registro.getHoraInicioAusencia() 
                    + " a " 
                    + registro.getHoraFinAusencia());
                System.out.println(WEB_NAME+"[VisorMarcasBp.setMarcasTurnoRotativo]"
                    + "seteo de ausencia por hora.NEW: "+registro.getMasInfo());
            }
            if (fechaAnterior.compareTo(registro.getFecha()) != 0){//nueva fecha
                    //if (numMarcas == 1 || tipoMarcaDuplicadoAnterior){
                    System.out.println(WEB_NAME+"[VisorMarcasBp.setMarcasTurnoRotativo]"
                        + "PASO 1");
                    if (numMarcas == 1){
                        String faltaMarca = "Salida.";
                        int tipoEvento = FALTA_MARCA_SALIDA;
                        if (tipoMarcaAnterior == MARCA_SALIDA){
                            faltaMarca = "Entrada.";
                            tipoEvento = FALTA_MARCA_ENTRADA;
                        }
                            
                        System.out.println(WEB_NAME+"[VisorMarcasBp.setMarcasTurnoRotativo]"
                            + "Fecha = " + DIAS_SEMANA.get(codDiaAnterior) + " " + fechaLabelAnterior 
                            + " ---> Falta marca de "+faltaMarca);

                        String fieldFechaAnt = DIAS_SEMANA.get(codDiaAnterior) + " " + fechaLabelAnterior;
                        String fieldEventoAnt = "Falta marca de " + faltaMarca;
                        InfoMarcaVO registroAux = new InfoMarcaVO();
                        registroAux.setEmpresaId(_empresaId);
                        registroAux.setRutEmpleado(_rutEmpleado);
                        registroAux.setFecha(fechaAnterior);
                        registroAux.setCodDia(codDiaAnterior);
                        registroAux.setFieldFecha(fieldFechaAnt);
                        registroAux.setTipoEvento(tipoEvento);
                        registroAux.setTipoMarca(tipoEvento);
                        registroAux.setFieldEvento(fieldEventoAnt);
                        if (idTurnoAsignadoAnterior != -1){
                            registroAux.setLabelTurno("(" + idTurnoAsignadoAnterior + ") "
                                + horaEntradaTurnoAnterior
                                + " a " + horaSalidaTurnoAnterior);
                        }      
                        hashMarcasFinal.put(fieldFechaAnt + "|" 
                            + fieldEventoAnt+"|"+correlativo, registroAux);
                        correlativo++;
                        fechaAnterior = null;
                        tipoMarcaDuplicadoAnterior = false;
                        tipoMarcaAnterior = -1;
                        codDiaAnterior = -1;
                        idTurnoAsignadoAnterior = -1;
                        horaEntradaTurnoAnterior = "";
                        horaSalidaTurnoAnterior = "";
                    } 
                    numMarcas = 0;
            }else{
                System.out.println(WEB_NAME+"[VisorMarcasBp.setMarcasTurnoRotativo]"
                    + "PASO 2");
                if (registro.getTipoMarca() == tipoMarcaAnterior){
                    registro.setTipoMarcaDuplicado(true);
                    tipoMarcaDuplicadoAnterior = true;
                }
            }
            
            if (registro.getTipoMarca() == -1 
                    && registro.getIdTurnoAsignado() != -1){
                System.out.println(WEB_NAME+"[VisorMarcasBp.setMarcasTurnoRotativo]"
                    + "PASO 3");
                String aux = TIPO_EVENTO.get(FALTAN_AMBAS_MARCAS);
                int tipoEvento = FALTAN_AMBAS_MARCAS;
                if (registro.getAusenciaNombre()!=null){
                    if (registro.getAusenciaPorHora().compareTo("N")==0){
                        aux = registro.getAusenciaNombre();
                        tipoEvento = AUSENCIA;
                        registro.setMasInfo(aux);
                    }
                }
                registro.setFieldFecha(DIAS_SEMANA.get(registro.getCodDia()) + " " + registro.getFechaLabel());
                registro.setFieldEvento(aux);
                registro.setTipoEvento(tipoEvento);
                registro.setTipoMarca(tipoEvento);
                registro.setLabelTurno("(" + registro.getIdTurnoAsignado() + ") "
                    + registro.getHoraEntradaTurno()
                    + " a " + registro.getHoraSalidaTurno() );
                hashMarcasFinal.put(registro.getFieldFecha() + "|" 
                    + registro.getFieldEvento(), registro);
                System.out.println(WEB_NAME+"[VisorMarcasBp.setMarcasTurnoRotativo]"
                    + "Fecha = " + DIAS_SEMANA.get(registro.getCodDia()) + " " + registro.getFechaLabel() 
                    + " --->fieldEvento(aux): " + aux 
                    +". num marcas= " + numMarcas
                    + ". IdTurnoAsignado= " + registro.getIdTurnoAsignado() 
                    + ", entradaTeorica = " + registro.getHoraEntradaTurno()
                    + ", salidaTeorica = " + registro.getHoraSalidaTurno());
            }else if (registro.getIdTurnoAsignado() == -1){
                    String aux = "";
                    System.out.println(WEB_NAME+"[VisorMarcasBp.setMarcasTurnoRotativo]"
                        + "PASO 4");
                    if (registro.getFechaHoraMarca()!=null){
                        aux = ". " + registro.getLabelTipoMarca()
                            + ", fechaHora:" + registro.getFechaHoraMarca();
                    }
                    registro.setFieldFecha(DIAS_SEMANA.get(registro.getCodDia()) + " " + registro.getFechaLabel());
                    registro.setLabelTurno("Sin turno");
                    if (registro.getAusenciaNombre()!=null){
                        if (registro.getAusenciaPorHora().compareTo("N")==0){
                            registro.setFieldEvento(registro.getAusenciaNombre());
                            registro.setTipoEvento(AUSENCIA);
                            registro.setMasInfo(registro.getAusenciaNombre());
                        }
                    }else {
                        System.out.println(WEB_NAME+"[VisorMarcasBp.setMarcasTurnoRotativo]"
                            + "PASO 5");
                        if (registro.getLabelCalendario()==null){
                            System.out.println(WEB_NAME+"[VisorMarcasBp.setMarcasTurnoRotativo]"
                                + "PASO 6");
                            System.out.println(WEB_NAME+"[VisorMarcasBp.setMarcasTurnoRotativo]"
                                + "Fecha = " + DIAS_SEMANA.get(registro.getCodDia()) + " " + registro.getFechaLabel() 
                                + " ---> Libre");
                            registro.setFieldEvento(TIPO_EVENTO.get(LIBRE) + aux);
                            registro.setMasInfo(registro.getFieldEvento());
                            registro.setTipoEvento(LIBRE);
                            registro.setTipoMarca(LIBRE);
                        }else{
                            System.out.println(WEB_NAME+"[VisorMarcasBp.setMarcasTurnoRotativo]"
                                + "PASO 7");
                            System.out.println(WEB_NAME+"[VisorMarcasBp.setMarcasTurnoRotativo]Fecha = " + DIAS_SEMANA.get(registro.getCodDia()) + " " + registro.getFechaLabel() 
                                + " ---> Feriado(" + registro.getLabelCalendario() + ")");
                            registro.setTipoEvento(FERIADO);
                            registro.setFieldEvento("Feriado (" + registro.getLabelCalendario() + ")"+aux);
                            registro.setMasInfo(registro.getFieldEvento());
                            registro.setTipoMarca(FERIADO);
                            
                            
                        }
                    }
                    hashMarcasFinal.put(registro.getFieldFecha() + "|" 
                        + registro.getFieldEvento(), registro);
            } else{//hay registro de marca
                    System.out.println(WEB_NAME+"[VisorMarcasBp.setMarcasTurnoRotativo]"
                        + "PASO 8");
                    registro.setFieldFecha(DIAS_SEMANA.get(registro.getCodDia()) + " " + registro.getFechaLabel());
                    registro.setTipoEvento(HAY_MARCA);
                    registro.setFieldEvento("Hay marca de " + registro.getLabelTipoMarca()
                        + ", fechaHora:" + registro.getFechaHoraMarca());
                    registro.setLabelTurno("(" + registro.getIdTurnoAsignado() + ") "
                        + registro.getHoraEntradaTurno()
                        + " a " + registro.getHoraSalidaTurno() );
                    hashMarcasFinal.put(registro.getFieldFecha() + "|" 
                        + registro.getFieldEvento(), registro);
                    System.out.println(WEB_NAME+"[VisorMarcasBp.setMarcasTurnoRotativo]Fecha = " + DIAS_SEMANA.get(registro.getCodDia()) + " " + registro.getFechaLabel() 
                        + " --->Datos de marca existente."
                        + " IdTurnoAsignado = " + registro.getIdTurnoAsignado()
                        + ", fechaHoraMarca = " + registro.getFechaHoraMarca()
                        + ", tipoMarca = " + registro.getLabelTipoMarca());
                    numMarcas++;
                    registro.setNumMarcas(numMarcas);
            }
            fechaAnterior       = registro.getFecha();
            fechaLabelAnterior  = registro.getFechaLabel();
            tipoMarcaAnterior   = registro.getTipoMarca();
            codDiaAnterior      = registro.getCodDia();
            idTurnoAsignadoAnterior = registro.getIdTurnoAsignado();
            horaEntradaTurnoAnterior = registro.getHoraEntradaTurno();
            horaSalidaTurnoAnterior = registro.getHoraSalidaTurno();
        }
        
         return hashMarcasFinal;
    }    
       
    /**
    * 
    */
    private static String getInfoAdicional(InfoMarcaVO _registro){
        String infoAdicional = "";
        if (_registro.getLabelCalendario() != null){
            infoAdicional = "Feriado (" + _registro.getLabelCalendario() + ")";
        }
        
        return infoAdicional;
    }
        
    /**
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * @param _startDate
    * @param _endDate
     * @param _regionIdEmpleado
     * @param _comunaIdEmpleado
     * @param _infoCenco
    * @return 
    */
    public LinkedHashMap<String, InfoMarcaVO> getHashMarcasTurnoRotativo(String _empresaId,
            String _rutEmpleado, 
            String _startDate, 
            String _endDate,
            int _regionIdEmpleado, 
            int _comunaIdEmpleado,
            CentroCostoVO _infoCenco){
        
        LinkedHashMap<String, InfoMarcaVO> hashMarcas =
            marcasDao.getHashMarcasTurnoRotativo(_empresaId, _rutEmpleado,
                _startDate, _endDate, _regionIdEmpleado, _comunaIdEmpleado, _infoCenco);
        
        return hashMarcas;
    }
    
    /**
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * @param _startDate
    * @param _endDate
     * @param _regionIdEmpleado
     * @param _comunaIdEmpleado
     * @param _infoCenco
    * @return 
    */
    public LinkedHashMap<String, InfoMarcaVO> getHashMarcasTurnoNormal(String _empresaId,
            String _rutEmpleado, 
            String _startDate, 
            String _endDate,
            int _regionIdEmpleado, 
            int _comunaIdEmpleado, 
            CentroCostoVO _infoCenco){
        
        LinkedHashMap<String, InfoMarcaVO> hashMarcas =
            marcasDao.getHashMarcasTurnoNormal(_empresaId, _rutEmpleado, 
                _startDate, _endDate, 
                _regionIdEmpleado, _comunaIdEmpleado,
                _infoCenco);

        return hashMarcas;
    }
}
