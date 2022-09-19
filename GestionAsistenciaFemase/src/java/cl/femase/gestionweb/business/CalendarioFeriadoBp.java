/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.CalendarioFeriadoVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.InfoFeriadoJsonObjectVO;
import cl.femase.gestionweb.vo.InfoFeriadoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class CalendarioFeriadoBp  extends BaseBp{

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.CalendarioFeriadoDAO feriadosDao;
    
    public CalendarioFeriadoBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        feriadosDao = new cl.femase.gestionweb.dao.CalendarioFeriadoDAO(this.props);
    }

    public List<CalendarioFeriadoVO> getFeriados(int _anio, int _mes,
            int _tipoFeriado,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<CalendarioFeriadoVO> lista = 
            feriadosDao.getFeriados(_anio, _mes, _tipoFeriado,
                _jtStartIndex, 
                _jtPageSize, _jtSorting);

        return lista;
    }

    public HashMap<String, String> getHashFeriadosNew(String _startDate, String _endDate){
        
        HashMap<String, String> lista= 
            feriadosDao.getHashFeriadosNew(_startDate, _endDate);

        return lista;
    }
    
    /**
    * Retorna objeto que indica si la fecha es feriado
    * Invoca a la funcion 'validaFechaFeriado'
    * 
    * @param _fecha
    * @param _empresaId
    * @param _runEmpleado
    * 
    * @return 
    */
    public InfoFeriadoVO validaFeriado(String _fecha, 
            String _empresaId, 
            String _runEmpleado){
        InfoFeriadoVO infoFeriado = new InfoFeriadoVO();
        EmpleadosBp empleadobp=new EmpleadosBp();
        EmpleadoVO auxEmpleado = 
            empleadobp.getEmpleado(_empresaId, _runEmpleado);
        
        int cencoId = auxEmpleado.getCencoId();
        String jsonOutput = feriadosDao.esFeriadoJson(_empresaId, cencoId, _runEmpleado, _fecha);
        try { 
            if (jsonOutput != null){
                Gson gson = new Gson();
                InfoFeriadoJsonObjectVO datos[] = gson.fromJson(jsonOutput, InfoFeriadoJsonObjectVO[].class);
                InfoFeriadoJsonObjectVO dataFeriadoFromJson = datos[0];
                if (dataFeriadoFromJson != null){
                    System.out.println(WEB_NAME+"[CalendarioFeriadoBp.validaFeriado]"
                        + "Objeto from json: " + dataFeriadoFromJson.toString());
                    infoFeriado.setFecha(dataFeriadoFromJson.getFecha());
                    infoFeriado.setFeriado(dataFeriadoFromJson.isFeriado());
                    infoFeriado.setTipoFeriado(dataFeriadoFromJson.getTipoferiado());
                    infoFeriado.setDescripcion(dataFeriadoFromJson.getDescripcion());
                    infoFeriado.setRespaldoLegal(dataFeriadoFromJson.getRespaldo_legal());
                }
                
            }
        }catch(JsonSyntaxException ex){
            System.err.println("[CalendarioFeriadoBp.validaFeriado]Seteo InfoFeriadoVO]"
                + "error_1: " + ex.toString());
        }catch(Exception ex2){
            System.err.println("[CalendarioFeriadoBp.validaFeriado]Seteo InfoFeriadoVO]"
                + "error_2: " + ex2.toString());
        }
        
        return infoFeriado;
    }
    
    /**
    * 
    * Invoca en un solo SQL a la funcion 'validafechaferiado', de tal forma
    * de invocarla por cada fecha en el rango especificado.
    * Luego cada registro JSON obtenido es parseado a un objeto InfoFeriadoVO.
    * La key de cada item en el hash es empresaId|runEmpleado|fecha
    * 
    * @param _empresaId
    * @param _runEmpleado
    * @param _startDate
    * @param _endDate
    * @return 
    */
    public LinkedHashMap<String, InfoFeriadoVO> getFechas(String _empresaId, 
            String _runEmpleado, 
            String _startDate, 
            String _endDate){
    
        System.out.println(WEB_NAME+"[CalendarioFeriadoBp.getFechas]"
            + "empresaId: " + _empresaId
            + ", runEmpleado: " + _runEmpleado
            + ", startDate: " + _startDate
            + ", endDate: " + _endDate);
        
        LinkedHashMap<String, InfoFeriadoVO> hashFechas = new LinkedHashMap<>();
        EmpleadosBp empleadobp=new EmpleadosBp();
        EmpleadoVO auxEmpleado = 
            empleadobp.getEmpleado(_empresaId, _runEmpleado);
        Gson gson = new Gson();
        int cencoId = auxEmpleado.getCencoId();
        String jsonOutput = feriadosDao.getValidaFechasJson(_empresaId, cencoId, _runEmpleado, _startDate, _endDate);
        InfoFeriadoJsonObjectVO datos[] = gson.fromJson(jsonOutput, InfoFeriadoJsonObjectVO[].class);
        
        if (datos != null){
            for (int x = 0; x < datos.length ; x++){
                InfoFeriadoJsonObjectVO dataFeriadoFromJson = datos[x];
                if (dataFeriadoFromJson != null){
                    System.out.println(WEB_NAME+"[CalendarioFeriadoBp.getFechas]"
                        + "Objeto from json: " + dataFeriadoFromJson.toString());
                    InfoFeriadoVO infoFeriado = new InfoFeriadoVO();
                    infoFeriado.setFecha(dataFeriadoFromJson.getFecha());
                    infoFeriado.setFeriado(dataFeriadoFromJson.isFeriado());
                    infoFeriado.setTipoFeriado(dataFeriadoFromJson.getTipoferiado());
                    infoFeriado.setDescripcion(dataFeriadoFromJson.getDescripcion());
                    infoFeriado.setRespaldoLegal(dataFeriadoFromJson.getRespaldo_legal());
                    hashFechas.put(_empresaId + "|" + _runEmpleado + "|" + infoFeriado.getFecha(), infoFeriado);
                }
            }
        }else{
            System.out.println(WEB_NAME+"[CalendarioFeriadoBp.getFechas]"
                + "empresaId: " + _empresaId
                + ", runEmpleado: " + _runEmpleado
                + ", startDate: " + _startDate
                + ", endDate: " + _endDate
                + ". No hay fechas en tabla calendario_feriados");
        }
        return hashFechas;
    }
    
    public MaintenanceVO update(CalendarioFeriadoVO _objectToUpdate, 
            MaintenanceEventVO _eventdata){
        MaintenanceVO updValues = feriadosDao.update(_objectToUpdate);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }
    
    public MaintenanceVO delete(String _rowKey, 
            MaintenanceEventVO _eventdata){
        MaintenanceVO updValues = feriadosDao.delete(_rowKey);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }
    
    /**
    * 
    * @param _objToInsert
    * @param _eventdata
    * @return 
    */
    public MaintenanceVO insert(CalendarioFeriadoVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        MaintenanceVO insValues = feriadosDao.insert(_objToInsert);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    /**
    * 
    * @param _anio
    * @param _mes
    * @param _tipoFeriado
    * @return 
    */
    public int getFeriadosCount(int _anio, int _mes, int _tipoFeriado){
        return feriadosDao.getFeriadosCount(_anio, _mes, _tipoFeriado);
    }

}
