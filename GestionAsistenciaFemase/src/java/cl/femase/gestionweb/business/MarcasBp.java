/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.CentroCostoVO;
import cl.femase.gestionweb.vo.InfoMarcaVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.MarcaJsonVO;
import cl.femase.gestionweb.vo.MarcaVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Alexander
 */
public class MarcasBp  extends BaseBp{

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.MarcasDAO marcasDao;
    private final DetalleTurnosBp detalleTurnoBp;
    private final EmpleadosBp empleadosBp;
    private final VisorMarcasBp visorMarcasBp;
    Locale localeCl = new Locale("es", "CL");
    
    private static String SIN_TURNO = "Sin turno";
    /**
    *  formato fecha yyyy-MM-dd
    */
    SimpleDateFormat fechaFmt = new SimpleDateFormat("yyyy-MM-dd");
    /**
    *  formato fecha dd-MM-yyyy
    */
    SimpleDateFormat fechaFmt2 = new SimpleDateFormat("dd-MM-yyyy");
    /**
    *  formato fecha yyyy-MM-dd HH:mm:ss
    */
    SimpleDateFormat fechaHoraFmt1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
    *  formato fecha dd/MM/yyyy HH:mm:ss
    */
    SimpleDateFormat fechaHoraFmt2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    /**
    *  formato fecha dd/MM/yyyy
    */
    SimpleDateFormat fechaHoraFmt3 = new SimpleDateFormat("dd/MM/yyyy");

    /**
    *  formato fecha HH:mm:ss
    */
    SimpleDateFormat horaFmt = new SimpleDateFormat("HH:mm:ss");
    
    public MarcasBp() {
        eventsService   = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        marcasDao       = new cl.femase.gestionweb.dao.MarcasDAO(this.props);
        detalleTurnoBp  = new DetalleTurnosBp(this.props);
        empleadosBp     = new EmpleadosBp(this.props);
        visorMarcasBp   = new VisorMarcasBp(this.props);
    }
    
    public MarcasBp(PropertiesVO props) {
        this.props      = props;
        eventsService   = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        marcasDao       = new cl.femase.gestionweb.dao.MarcasDAO(this.props);
        detalleTurnoBp  = new DetalleTurnosBp(this.props);
        empleadosBp     = new EmpleadosBp(this.props);
        visorMarcasBp   = new VisorMarcasBp(this.props);
    }

    public LinkedHashMap<String, MarcaVO> getMarcasEmpleado(String _empresaId,
            String _rutEmpleado, String _startDate, 
            String _endDate){
        
        LinkedHashMap<String, MarcaVO> lista = 
            marcasDao.getMarcasEmpleado(_empresaId, _rutEmpleado, _startDate, _endDate);

        return lista;
    }
    
    /**
     *
     * @param _empresaId
     * @param _rutEmpleado
     * @param _fechaHora
     * @param _tipoMarca
     * @param _historico
     * @return
     */
    public MarcaVO getMarcaByKey(String _empresaId,
            String _rutEmpleado, 
            String _fechaHora, 
            int _tipoMarca, boolean _historico){
    
        return marcasDao.getMarcaByKey(_empresaId, 
                _rutEmpleado, 
                _fechaHora, _tipoMarca, _historico);
    }
    
    /**
    * 
    * @param _hashcode
    * @param _historico
    * @return 
    */
    public MarcaVO getMarcaByHashcode(String _hashcode, boolean _historico){
    
        return marcasDao.getMarcaByHashcode(_hashcode, _historico);
    }
    
    public MarcaVO getMarcaByTipo(String _empresaId,
            String _rutEmpleado, String _startDate, 
            String _endDate,int _tipoMarca,String _fechaHoraEntrada){
        
        return marcasDao.getMarcaByTipo(_empresaId, _rutEmpleado, 
            _startDate, _endDate,_tipoMarca,_fechaHoraEntrada);

    }

    /**
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * @param _startDate
    * @param _endDate
     * @param _tipoMarcaManual
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    * 
    */
    public List<MarcaVO> getMarcasVirtuales(String _empresaId,
            String _rutEmpleado, 
            String _startDate, 
            String _endDate,
            int _tipoMarcaManual,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
    
        List<MarcaVO> lista = 
            marcasDao.getMarcasVirtuales(_empresaId, _rutEmpleado, 
                _startDate, _endDate, _tipoMarcaManual, 
                _jtStartIndex, _jtPageSize, _jtSorting);
        return lista;
    }
    
    /**
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * @param _startDate
    * @param _endDate
    * @return 
    * 
    */
    public int getMarcasVirtualesCount(String _empresaId,
            String _rutEmpleado, 
            String _startDate, 
            String _endDate,
            int _tipoMarcaManual){
    
        return marcasDao.getMarcasVirtualesCount(_empresaId, _rutEmpleado, 
            _startDate, _endDate, _tipoMarcaManual);
    }
    
    /**
     * 
     * @param _empresaId
     * @param _deptoId
     * @param _cencoId
     * @param _rutEmpleado
     * @param _dispositivoId
     * @param _startDate
     * @param _endDate
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     * 
     * @Deprecated 
     */
    public List<MarcaVO> getMarcas(String _empresaId,
            String _deptoId,
            int _cencoId,
            String _rutEmpleado, 
            String _dispositivoId,
            String _startDate, 
            String _endDate,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<MarcaVO> lista = 
            marcasDao.getMarcas(_empresaId,
                _deptoId,_cencoId, 
                _rutEmpleado, _dispositivoId, 
                _startDate, _endDate, _jtStartIndex, 
                _jtPageSize, _jtSorting);
        return lista;
    }
    
    public ArrayList<MarcaVO> getMarcasByKey(String _empresaId,
            String _rutEmpleado, 
            String _fechaHora, 
            int _tipoMarca, 
            boolean _historico){
        
        return marcasDao.getMarcasByKey(_empresaId, 
            _rutEmpleado, 
            _fechaHora,
            _tipoMarca, 
            _historico);
    }
    
    /**
     * 22-09-2019
    *  Metodo que retorna lista de fechas y sus respectivas marcas.Se indica cuando falta una o ambas marcas., tambien cuando es dia libre o feriado.
    * 
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @param _rutEmpleado
    * @param _dispositivoId
    * @param _startDate
    * @param _endDate
     * @param _hashcode
    * @param _turnoRotativo
     * @param _regionIdEmpleado
     * @param _comunaIdEmpleado
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public LinkedHashMap<String, MarcaVO> getHashMarcas(String _empresaId,
            String _deptoId,
            int _cencoId,
            String _rutEmpleado, 
            String _dispositivoId,
            String _startDate, 
            String _endDate,
            String _hashcode,
            boolean _turnoRotativo,
            int _regionIdEmpleado, 
            int _comunaIdEmpleado,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
    
        System.out.println(WEB_NAME+"[MarcasBp.getHashMarcas]"
            + "empresaId: " + _empresaId
            + ", deptoId: " + _deptoId
            + ", cencoId: " + _cencoId
            + ", rutEmpleado: " + _rutEmpleado
            + ", regionIdEmpleado: " + _regionIdEmpleado
            + ", comunaIdEmpleado: " + _comunaIdEmpleado);
        
        CentroCostoBp cencoBp = new CentroCostoBp(new PropertiesVO());
        CentroCostoVO infoCenco = cencoBp.getCentroCostoByKey(_deptoId, _cencoId);
        System.out.println(WEB_NAME+"[MarcasBp.getHashMarcas]"
            + "regionIdCenco: " + infoCenco.getRegionId()
            + ", comunaIdCenco: " + infoCenco.getComunaId());
        
        LinkedHashMap<String, MarcaVO> hashMarcasFinal = 
            new LinkedHashMap<>();
        
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd", new Locale("es","CL"));
        Calendar calendart1 = Calendar.getInstance(new Locale("ES","cl"));
        Date auxfechaactual = calendart1.getTime();
        // Set the day of the month to the first day of the month
        calendart1.set(Calendar.DAY_OF_MONTH,
        calendart1.getActualMinimum(Calendar.DAY_OF_MONTH));
        // Extract the Date from the Calendar instance
        Date firstDayOfTheMonth = calendart1.getTime();
        //System.out.println(WEB_NAME+"1er dia del mes: " + sdf3.format(calendart1.getTime()) + ", fecha actual: " + sdf3.format(auxfechaactual));        
        
        if (_startDate == null || _startDate.compareTo("") == 0) _startDate = sdf3.format(calendart1.getTime());
        if (_endDate == null || _endDate.compareTo("") == 0) _endDate = sdf3.format(auxfechaactual);
        
        LinkedHashMap<String, InfoMarcaVO> fechasMarcas;
        if (_turnoRotativo){
            fechasMarcas = 
                visorMarcasBp.setMarcasTurnoRotativo(_empresaId, _rutEmpleado , _startDate, _endDate, _hashcode, _regionIdEmpleado, _comunaIdEmpleado, infoCenco);
        }else{
            fechasMarcas = 
                visorMarcasBp.setMarcasTurnoNormal(_empresaId, _rutEmpleado , _startDate, _endDate, _hashcode, _regionIdEmpleado, _comunaIdEmpleado, infoCenco);
        }
        
        fechasMarcas.forEach((key,value) -> {
            InfoMarcaVO registro = value;
            MarcaVO marcaFinal = new MarcaVO();
            System.out.println(WEB_NAME + "itera row final. "
                + "Key: " + key 
                + ",info: " + registro.toString());
            
            marcaFinal.setEmpresaCod(registro.getEmpresaId());
            marcaFinal.setCorrelativo(registro.getCorrelativo());
            marcaFinal.setRutEmpleado(registro.getRutEmpleado());
            marcaFinal.setFecha(registro.getFecha());
            marcaFinal.setFechaHoraStr(registro.getFieldFecha());
            if (registro.getHoraMarca() != null) {
                marcaFinal.setFechaHoraStr(registro.getFieldFecha() + " " + registro.getHoraMarca());
            }
            marcaFinal.setTipoMarca(registro.getTipoMarca());
            marcaFinal.setComentario(registro.getComentario());
            
            System.out.println(WEB_NAME + ". (1)Set rowKey: "
                + registro.getEmpresaId()
                + "|" + registro.getRutEmpleado()
                + "|" + registro.getFecha()
                + "|" + registro.getTipoMarca());
            
            marcaFinal.setRowKey(registro.getEmpresaId()
                + "|" + registro.getRutEmpleado()
                + "|" + registro.getFecha()
                + "|" + registro.getTipoMarca()
                + "|" + registro.getHashcode()
                + "|" + registro.getCorrelativo());
            
            if (registro.getFechaHoraMarca() != null){
                System.out.println(WEB_NAME + ". (2)Set rowKey: "
                    + registro.getEmpresaId()
                    + "|" + registro.getRutEmpleado()
                    + "|" + registro.getFechaHoraMarca()
                    + "|" + registro.getTipoMarca()
                    + "|" + registro.getHashcode()
                    + "|" + registro.getCorrelativo());
                marcaFinal.setRowKey(registro.getEmpresaId()
                    + "|" + registro.getRutEmpleado()
                    + "|" + registro.getFechaHoraMarca()
                    + "|" + registro.getTipoMarca()
                    + "|" + registro.getHashcode()
                    + "|" + registro.getCorrelativo());
            }
            //if (labelTurno.compareTo(SIN_TURNO) == 0) labelTurno = "";
            marcaFinal.setLabelTurno(registro.getLabelTurno());

            marcaFinal.setCodDispositivo(registro.getCodDispositivo());
            marcaFinal.setRutKey(registro.getRutEmpleado());
            //data.setCencoId(rs.getInt("cod_tipo_marca"));
            if (registro.getFechaHoraMarca()!=null){
                marcaFinal.setFechaHora(registro.getFechaHoraMarca());
            }else{
                marcaFinal.setFechaHora(registro.getFecha());
            }
            marcaFinal.setSoloHora(registro.getHoraMarca());
            marcaFinal.setFechaHoraKey(marcaFinal.getFechaHora());
            //data.setFechaHoraCalculos(rs.getString("fecha_hora_fmt"));
            marcaFinal.setHora(registro.getHora());
            marcaFinal.setFecha(registro.getFecha());
            marcaFinal.setMinutos(registro.getMinutos());
            marcaFinal.setSegundos(registro.getSegundos());
            marcaFinal.setTipoMarca(registro.getTipoMarca());
            marcaFinal.setId(registro.getId());
            marcaFinal.setHashcode(registro.getHashcode());
            marcaFinal.setComentario(registro.getComentario());
            marcaFinal.setFechaHoraActualizacion(registro.getFechaHoraActualizacion());
            marcaFinal.setCodTipoMarcaManual(registro.getCodTipoMarcaManual());
            marcaFinal.setNombreTipoMarcaManual(registro.getNombreTipoMarcaManual());
            marcaFinal.setMasInfo(registro.getMasInfo());
            
            hashMarcasFinal.put(key, marcaFinal);
        });
        
        return hashMarcasFinal;
    }
    
    public List<MarcaVO> getMarcasHist(String _empresaId,
            String _deptoId,
            int _cencoId,
            String _rutEmpleado, 
            String _dispositivoId,
            String _startDate, 
            String _endDate,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<MarcaVO> lista = 
            marcasDao.getMarcasHist(_empresaId,
                _deptoId,_cencoId, 
                _rutEmpleado, _dispositivoId, 
                _startDate, _endDate, _jtStartIndex, 
                _jtPageSize, _jtSorting);
        return lista;
    }
    
    public int getMarcasCount(String _empresaId,
            String _deptoId,
            int _cencoId,
            String _rutEmpleado, 
            String _dispositivoId,
            String _startDate, 
            String _endDate, 
            String _hashcode){
        
        return marcasDao.getMarcasCount(_empresaId, _deptoId, 
            _cencoId, _rutEmpleado, 
            _dispositivoId, _startDate, 
            _endDate, _hashcode);
    }
    
    public int getMarcasHistCount(String _empresaId,
            String _deptoId,
            int _cencoId,
            String _rutEmpleado, 
            String _dispositivoId,
            String _startDate, 
            String _endDate){
        
        return marcasDao.getMarcasHistCount(_empresaId, _deptoId, 
            _cencoId, _rutEmpleado, 
            _dispositivoId, _startDate, 
            _endDate);
    }
    
    public ResultCRUDVO update(MarcaVO _marcaModificada,
            MarcaVO _marcaOriginal,
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = marcasDao.update(_marcaModificada, _marcaOriginal);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }
    
    public ResultCRUDVO delete(MarcaVO _objectToDelete, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO deletedValues = marcasDao.delete(_objectToDelete);
        
        //if (!updValues.isThereError()){
            String msgFinal = deletedValues.getMsg();
            deletedValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return deletedValues;
    }
    
    /**
     * Inserta una marca desde carga por archivo o carga desde cliente
     * web socket
     * @param _objToInsert
     * @return 
     */
    public ResultCRUDVO insert(MarcaVO _objToInsert){
        
        ResultCRUDVO insValues = marcasDao.insert(_objToInsert);
        
        return insValues;
    }
    
    /**
    * Inserta una marca desde administrador de marcas
    * @param _objToInsert
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO insertWithLog(MarcaVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        //obtener cod_interno del 
        ResultCRUDVO insValues = marcasDao.insertaMarcaManual(_objToInsert);
        
        if (!insValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal+" "+insValues.getMsgFromSp());
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        }
        
        return insValues;
    }

    public HashMap<Integer, String> getTiposMarca(){
        return marcasDao.getTiposMarca();
    }

    public String getEmpleadosSinMarcaEntradaDiaJson(String _empresaId, 
            int _codDia, 
            String _horaLimite,
            int _cencoId){
        return marcasDao.getEmpleadosSinMarcaEntradaDiaJson(_empresaId, 
            _codDia,
            _horaLimite,
            _cencoId);
    }
    
    public String getEmpleadosConMarcasRechadasFechaJson(String _empresaId, 
            String _fecha){
        return marcasDao.getEmpleadosConMarcasRechadasFechaJson(_empresaId, _fecha);
    }
 
    /**
    * 
     * @param _empresaId
     * @param _rutEmpleado
     * @param _startDate
     * @param _endDate
     * @return 
    */
    public String getMarcasJson(String _empresaId,
            String _rutEmpleado,
            String _startDate,
            String _endDate){
        
        String jsonOutput = 
            marcasDao.getMarcasJson(_empresaId, 
                _rutEmpleado, 
                _startDate, _endDate);

        return jsonOutput;
    }
    
    /**
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * @param _startDate
    * @param _endDate
    * @return 
    */
    public LinkedHashMap<String,MarcaVO> getAllMarcas(String _empresaId,
            String _rutEmpleado,
            String _startDate,
            String _endDate){
        
        LinkedHashMap<String,MarcaVO> allMarcas = new LinkedHashMap<>();
        
        String jsonOutput = 
            marcasDao.getAllMarcasJson(_empresaId, 
                _rutEmpleado, 
                _startDate, _endDate);
        System.out.println(WEB_NAME+"[GestionFemase."
            + "MarcasBp.getAllMarcas]"
            + "getMarcasJson jsonOutput: " + jsonOutput);

        Type listType = new TypeToken<ArrayList<MarcaJsonVO>>() {}.getType();
        ArrayList<MarcaJsonVO> marcas = new Gson().fromJson(jsonOutput, listType);                
        
        if (marcas == null){
            System.out.println(WEB_NAME+"[GestionFemase."
                + "MarcasBp.getAllMarcas]Buscar marcas historicas...");
            //buscar marcas historicas
            jsonOutput = marcasDao.getAllMarcasHistJson(_empresaId, 
                _rutEmpleado, 
                _startDate, _endDate);
            System.out.println(WEB_NAME+"[GestionFemase."
                + "MarcasBp.getAllMarcas]"
                + "getMarcasHistJson jsonOutput: " + jsonOutput);

            listType = new TypeToken<ArrayList<MarcaJsonVO>>() {}.getType();
            marcas = new Gson().fromJson(jsonOutput, listType);                
        }
        
        MarcaVO auxmarca = new MarcaVO();
        if (marcas != null){
            for(int x=0; x < marcas.size(); x++) {
                MarcaJsonVO itmarca = marcas.get(x); 
                System.out.println(WEB_NAME+"[GestionFemase."
                + "MarcasBp.getAllMarcas]"
                    + "iteracion: "
                    + "rut:" + itmarca.getRutempleado()
                    + ", fecha:" + itmarca.getFechahora()
                    + ", tipo:" + itmarca.getTipomarca()    
                );
                auxmarca = new MarcaVO();
                auxmarca.setCodDispositivo(itmarca.getCoddispositivo());
                auxmarca.setEmpresaCod(itmarca.getEmpresacod());
                auxmarca.setRutEmpleado(itmarca.getRutempleado());
                auxmarca.setFechaHora(itmarca.getFechahora());
                auxmarca.setFechaHoraCalculos(itmarca.getFechahoracalculos());
                auxmarca.setTipoMarca(itmarca.getTipomarca());
                auxmarca.setComentario(itmarca.getComentario());

                allMarcas.put(itmarca.getFechahora(), auxmarca);
            }
        }
        
        return allMarcas;
    }
    
    /**
     *
     * @param _empresaId
     * @param _rutEmpleado
     * @param _startDate
     * @param _endDate
     * @return
     */
    public String getMarcasHistJson(String _empresaId,
            String _rutEmpleado,
            String _startDate,
            String _endDate){
        
        String jsonOutput = 
            marcasDao.getMarcasHistJson(_empresaId, 
                _rutEmpleado, 
                _startDate, _endDate);

        return jsonOutput;
    }
    
    public String getMarcaByTipoJson(String _empresaId,
            String _rutEmpleado, 
            String _startDate, 
            String _endDate,
            int _tipoMarca,
            String _fechaHoraEntrada, 
            boolean _isHistorico){
        
        String jsonOutput = 
            marcasDao.getMarcaByTipoJson(_empresaId, _rutEmpleado, 
            _startDate, _endDate,_tipoMarca,_fechaHoraEntrada, _isHistorico);

        return jsonOutput;
    }
    
    /**
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * @param _startDate
    * @param _endDate
    * @param _tipoMarca
    * @param _isHistorico
    * @return 
    */
    public String getMarcaByTipoAndFechaJson(String _empresaId,
            String _rutEmpleado, 
            String _startDate, 
            String _endDate,
            int _tipoMarca,
            boolean _isHistorico){
        
        String jsonOutput = 
            marcasDao.getMarcaByTipoAndFechaJson(_empresaId, _rutEmpleado, 
            _startDate, _endDate,_tipoMarca, _isHistorico);

        return jsonOutput;
    }
    
}
