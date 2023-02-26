/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.dao.ParametroDAO;
import cl.femase.gestionweb.dao.UsersDAO;
import cl.femase.gestionweb.vo.CargoVO;
import cl.femase.gestionweb.vo.CentroCostoVO;
import cl.femase.gestionweb.vo.DepartamentoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.EmpresaVO;
import cl.femase.gestionweb.vo.ParametroVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.ResultadoCargaCsvVO;
import cl.femase.gestionweb.vo.ResultadoCargaDataCsvVO;
import cl.femase.gestionweb.vo.TurnoVO;
import cl.femase.gestionweb.vo.UsuarioCentroCostoVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Alexander
 */
public class EmpleadosBp  extends BaseBp{

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.EmpleadosDAO empleadosDao;
    private final cl.femase.gestionweb.business.AsignacionTurnoBp asignacionTurnoBp;
    private final cl.femase.gestionweb.dao.PermisosAdministrativosDAO permisosAdminDao;
    private final cl.femase.gestionweb.dao.ParametroDAO daoParams;
    
    /**
    * 
     * @param props
    */
    public EmpleadosBp(PropertiesVO props) {
        this.props          = props;
        eventsService       = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        empleadosDao        = new cl.femase.gestionweb.dao.EmpleadosDAO(this.props);
        asignacionTurnoBp   = new AsignacionTurnoBp(this.props);
        permisosAdminDao    = new cl.femase.gestionweb.dao.PermisosAdministrativosDAO(this.props);
        daoParams           = new cl.femase.gestionweb.dao.ParametroDAO(this.props);
    }

    public EmpleadosBp() {
        eventsService       = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        empleadosDao        = new cl.femase.gestionweb.dao.EmpleadosDAO(this.props);
        asignacionTurnoBp   = new AsignacionTurnoBp(this.props);
        permisosAdminDao    = new cl.femase.gestionweb.dao.PermisosAdministrativosDAO(this.props);
        daoParams           = new cl.femase.gestionweb.dao.ParametroDAO(this.props);
    }
    
    public List<EmpleadoVO> getEmpleadosByTurno(String _empresaId, 
            String _deptoId, 
            int _cencoId,
            int _turnoId,
            String _rutEmpleado,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
    
        List<EmpleadoVO> lista = 
            empleadosDao.getEmpleadosByTurno(_empresaId, 
            _deptoId, 
            _cencoId,
            _turnoId,
            _rutEmpleado,
            _jtStartIndex, 
            _jtPageSize, _jtSorting);

        return lista;
    }
    
    public int getEmpleadosByTurnoCount(String _empresaId, 
            String _deptoId, 
            int _cencoId,
            int _turnoId,
            String _rutEmpleado){
    
        return empleadosDao.getEmpleadosByTurnoCount(_empresaId, 
            _deptoId, 
            _cencoId,
            _turnoId,
            _rutEmpleado);
    }
    
    /**
     * 
     * @param _empresaId
     * @param _deptoId
     * @param _cencoId
     * @param _cargo
     * @param _rutEmpleado
     * @param _nombres
     * @param _apePaterno
     * @param _jtStartIndex
     * @param _apeMaterno
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<EmpleadoVO> getEmpleadosShort(String _empresaId, 
            String _deptoId, 
            int _cencoId,
            int _cargo,
            String _rutEmpleado,
            String _nombres,
            String _apePaterno,
            String _apeMaterno,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<EmpleadoVO> lista = 
            empleadosDao.getEmpleadosShort(_empresaId, 
            _deptoId, 
            _cencoId,
            _cargo,
            _rutEmpleado,
            _nombres,
            _apePaterno,
            _apeMaterno, _jtStartIndex, 
            _jtPageSize, _jtSorting);

        return lista;
    }
    
    /**
     * 
     * @param _empresaId
     * @param _deptoId
     * @param _cencoId
     * @param _turnoId
     *
     * @return 
     */
    public List<EmpleadoVO> getEmpleadosNew(String _empresaId, 
            String _deptoId, 
            int _cencoId,
            int _turnoId){
        
        List<EmpleadoVO> lista = 
            empleadosDao.getEmpleadosNew(_empresaId, 
            _deptoId, 
            _cencoId,
            _turnoId);

        return lista;
    }
    
    public String getEmpleadoJson(String _empresaId, 
            String _deptoId, 
            int _cencoId,
            String _rutEmpleado){
        
        String jsonOutput = 
            empleadosDao.getEmpleadoJson(_empresaId, _deptoId, _cencoId, _rutEmpleado);

        return jsonOutput;
    }
    
    public String getEmpleadosJson(String _empresaId, 
            String _deptoId, 
            int _cencoId){
        
        String jsonOutput = 
            empleadosDao.getEmpleadosJson(_empresaId, 
                    _deptoId, _cencoId);

        return jsonOutput;
    }
    
    /**
     * Obtiene lista de empleados, con toda la info de cada empleado.
     * Recibe como parametro una lista de ruts
     * @param _empresaId
     * @param _deptoId
     * @param _cencoId
     * @param _listaRuts
     * @return 
     */
    public List<EmpleadoVO> getListaEmpleadosComplete(String _empresaId,
            String _deptoId, 
            int _cencoId,
            List<String> _listaRuts){
    
        System.out.println(WEB_NAME+"[EmpleadosBp."
            + "getListaEmpleadosComplete]"
            + "empresa: "+_empresaId
            +", depto: "+_deptoId
            +", cenco: "+_cencoId);
        
        EmpleadosBp empleadosBp = new EmpleadosBp();
        List<EmpleadoVO> listaFinalEmpleados = new ArrayList<>();
        
        Iterator<String> it = _listaRuts.iterator();
        while(it.hasNext()){
            String strRut = it.next();
            System.out.println(WEB_NAME+"[EmpleadosBp."
                + "getListaEmpleadosComplete]"
                + "get info completa de "
                + "empleado rut: "+ strRut);
            
            String jsonOutput = 
                empleadosBp.getEmpleadoJson(_empresaId,_deptoId, 
                _cencoId, strRut);
            //Type listType = new TypeToken<List<EmpleadoVO>>() {}.getType();
            //EmpleadoVO infoEmpleado = new Gson().fromJson(jsonOutput, listType);    
            //EmpleadoVO infoEmpleado = new Gson().fromJson(jsonOutput, 
             //       EmpleadoVO.class);
            
            EmpleadoVO infoEmpleado;
            try {
                infoEmpleado = (EmpleadoVO)new Gson().fromJson(jsonOutput, EmpleadoVO.class);
            }
            catch(Exception ex){
                System.err.println("[EmpleadosBp."
                    + "getListaEmpleadosComplete]parseo empleadoVO.error: "+ex.toString());
                Type clazzListType = new TypeToken<EmpleadoVO>() {}.getType();
                infoEmpleado = new Gson().fromJson(jsonOutput, clazzListType);
                System.err.println("[EmpleadosBp."
                    + "getListaEmpleadosComplete]parseo empleadoVO. 1-rut: "+infoEmpleado.getRut());
            }
            
            listaFinalEmpleados.add(infoEmpleado);
        }
        
        return listaFinalEmpleados;
    }
    
    /**
     * Obtiene lista de empleados para el centro de costo especificado (como json)
     *      * 
     * @param _empresaId
     * @param _deptoId
     * @param _cencoId
     * @return 
     */
    public List<EmpleadoVO> getListaEmpleadosJson(String _empresaId,
            String _deptoId, 
            int _cencoId){
    
        System.out.println(WEB_NAME+"[EmpleadosBp."
            + "getListaEmpleadosJson]empresa: "+_empresaId
            +", depto: "+_deptoId
            +", cenco: "+_cencoId);
        
        EmpleadosBp empleadosBp = new EmpleadosBp();
        List<EmpleadoVO> listaEmpleados = new ArrayList<>();
        if (_empresaId.compareTo("-1") != 0 
                && _deptoId.compareTo("-1") != 0
                && _cencoId != -1){
                    //todos los empleados del cenco
//                    listaEmpleados = empleadosBp.getEmpleadosShort(_empresaId, 
//                            _deptoId, 
//                            _cencoId, 
//                            -1,  
//                            null, 
//                            null, 
//                            null, 
//                            null, 
//                            0, 
//                            0, 
//                            "empleado.empl_rut");
                        String jsonOutput = 
                            empleadosBp.getEmpleadosJson(_empresaId, 
                            _deptoId, 
                            _cencoId);
                        //System.out.println(WEB_NAME+"[EmpleadosBp."
                          //  + "getListaEmpleadosJson]jsonOutput: "+jsonOutput);
                    Type listType = new TypeToken<ArrayList<EmpleadoVO>>() {}.getType();
                    listaEmpleados = new Gson().fromJson(jsonOutput, listType);

        }
        return listaEmpleados;
    }
    
    public List<EmpleadoVO> getEmpleadosByListRuts(String _empresaId, 
            String _deptoId, 
            int _cencoId,
            int _cargo,
            ArrayList<String> _empleados,
            String _nombres,
            String _apePaterno,
            String _apeMaterno,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<EmpleadoVO> lista = 
            empleadosDao.getEmpleadosByListRuts(_empresaId, 
            _deptoId, 
            _cencoId,
            _cargo,
            _empleados,
            _nombres,
            _apePaterno,
            _apeMaterno, _jtStartIndex, 
            _jtPageSize, _jtSorting);

        return lista;
    }
    
    public boolean existeEmpleado(String _empresaId,
            String _codInterno,
            String _rut){
        return empleadosDao.existeEmpleado(_empresaId, _codInterno, _rut);
    }
    
    /**
    * 
    * @param _request
    * @param _userConnected
    * @param _empleados
    * @return 
    */
    public ArrayList<ResultadoCargaDataCsvVO> procesaEmpleadosCSV(HttpServletRequest _request,
            UsuarioVO _userConnected, 
            ArrayList<EmpleadoVO> _empleados){
        System.out.println(WEB_NAME+"[EmpleadosBp.procesaEmpleadosCSV]Entrando...");
        ArrayList<ResultadoCargaDataCsvVO> listaResultados = new ArrayList<>();
        //insertar empleado
        MaintenanceEventVO evento = new MaintenanceEventVO();
        evento.setUsername(_userConnected.getUsername());
        evento.setDatetime(new Date());
        evento.setUserIP(_request.getRemoteAddr());
        evento.setType("EMP");
        evento.setEmpresaIdSource(_userConnected.getEmpresaId());
        System.out.println(WEB_NAME+"[EmpleadosBp.procesaEmpleadosCSV]Size empleados: " + _empleados.size());
        
        EmpresaBp empresasBp    = new EmpresaBp(null);
        DepartamentoBp deptosBp = new DepartamentoBp(null);
        CentroCostoBp cencoBp   = new CentroCostoBp(null);
        CargoBp cargoBp         = new CargoBp(null);
        TurnosBp turnoBp        = new TurnosBp(null);
        
        //Itera empleados
        _empleados.forEach((EmpleadoVO empleado) -> {
            System.out.println(WEB_NAME+"[EmpleadosBp.procesaEmpleadosCSV] "
                + "Procesando empleado. "
                + "Rut(sin puntos- PK)= " + empleado.getCodInterno()
                + ", codInterno(con puntos)= " + empleado.getRut()
                + ", nombres= " + empleado.getNombres()
                + ", email= " + empleado.getEmail()
                + ", toString: "+ empleado.toString());
            
            ResultadoCargaDataCsvVO resultado=new ResultadoCargaDataCsvVO();
            resultado.setEmpleado(empleado);
            ArrayList<ResultadoCargaCsvVO> mensajes = new ArrayList<>();
            
            //validar empresa, depto, cenco, cargo y 
            boolean isOk = true;
            EmpresaVO empresa       = empresasBp.getEmpresaByKey(empleado.getEmpresa().getId());
            DepartamentoVO depto    = deptosBp.getDepartamentoByKey(empleado.getDepartamento().getId());
             System.out.println(WEB_NAME+"[EmpleadosBp.procesaEmpleadosCSV]"
                + "validar Cenco...");
            CentroCostoVO cenco     = 
                cencoBp.getCentroCostoByKey(empleado.getDepartamento().getId(), 
                    empleado.getCentroCosto().getId());
            System.out.println(WEB_NAME+"[EmpleadosBp.procesaEmpleadosCSV]"
                + "Cenco: " + cenco);
            CargoVO cargo    = cargoBp.getCargoByKey(empleado.getIdCargo());
            TurnoVO turno    = turnoBp.getTurno(empleado.getIdTurno());
            System.out.println(WEB_NAME+"[EmpleadosBp.procesaEmpleadosCSV]"
                + "Validar DeptoId: " + empleado.getDepartamento().getId()
                + ", cencoId= " + empleado.getCentroCosto().getId());
            if (empresa == null) {
                mensajes.add(new ResultadoCargaCsvVO("ERROR", "Empresa Id no existe."));
                System.out.println(WEB_NAME+"[EmpleadosBp.procesaEmpleadosCSV]"
                    + "Empresa no existe");
                isOk = false;
            }
            if (depto == null){ 
                mensajes.add(new ResultadoCargaCsvVO("ERROR", "Departamento Id no existe."));
                System.out.println(WEB_NAME+"[EmpleadosBp.procesaEmpleadosCSV]"
                    + "Departamento no existe");
                isOk = false;
            }
            if (cenco == null){
                mensajes.add(new ResultadoCargaCsvVO("ERROR", "Centro de costo Id no existe."));
                System.out.println(WEB_NAME+"[EmpleadosBp.procesaEmpleadosCSV]"
                    + "Centro de costo no existe");
                isOk = false;
            }
            if (cargo == null) {
                mensajes.add(new ResultadoCargaCsvVO("ERROR", "Cargo Id no existe."));
                System.out.println(WEB_NAME+"[EmpleadosBp.procesaEmpleadosCSV]"
                    + "Cargo no existe");
                isOk = false;
            }
            if (turno == null) {
                mensajes.add(new ResultadoCargaCsvVO("ERROR", "Turno Id no existe."));
                System.out.println(WEB_NAME+"[EmpleadosBp.procesaEmpleadosCSV]"
                    + "Turno no existe");
                isOk = false;
            }
            System.out.println(WEB_NAME+"[EmpleadosBp.procesaEmpleadosCSV]"
                + "isOk: " + isOk);
            if (isOk){
                //-------------------------------------------------------
                evento.setEmpresaId(empleado.getEmpresa().getId());
                evento.setEmpresaIdSource(empleado.getEmpresa().getId());
                evento.setRutEmpleado(empleado.getCodInterno());
                evento.setDeptoId(empleado.getDepartamento().getId());
                evento.setCencoId(empleado.getCentroCosto().getId());

                boolean existeEmpleado =this.existeEmpleado(empleado.getEmpresa().getId(), empleado.getRut(), empleado.getCodInterno()); 
                String rutPK        = empleado.getCodInterno();
                String codInterno   = empleado.getRut();
                System.out.println(WEB_NAME+"[EmpleadosBp.procesaEmpleadosCSV]"
                    + "Existe empleado. PK: " + rutPK);
            
                if (existeEmpleado){
                    //update
                    System.out.println(WEB_NAME+"[EmpleadosBp.procesaEmpleadosCSV]Actualizar datos del empleado...");
                    empleado.setCodInterno(rutPK);
                    empleado.setRut(codInterno);
                    update(empleado, evento);
                    mensajes.add(new ResultadoCargaCsvVO("OK", "Empleado ya existe."));
                    mensajes.add(new ResultadoCargaCsvVO("OK", "Empleado reemplazado exitosamente."));
                } else {
                    System.out.println(WEB_NAME+"[EmpleadosBp.procesaEmpleadosCSV]Insertar nuevo empleado...");
                    ResultCRUDVO insertObj = insert(empleado, evento);
                    if (!insertObj.isThereError()){ 
                        mensajes.add(new ResultadoCargaCsvVO("OK", "Empleado creado exitosamente."));
                        int perfilId = Constantes.ID_PERFIL_EMPLEADO;
                        String perfilName = "Empleado";
                        if (empleado.getIdCargo() == Constantes.ID_CARGO_DIRECTOR){
                            perfilId = Constantes.ID_PERFIL_DIRECTOR;
                            perfilName = "Director";
                        }
                        mensajes.add(new ResultadoCargaCsvVO("OK", "Usuario creado exitosamente. "
                            + ", perfil usuario= ("+perfilId+") "+ perfilName));
                        mensajes.add(new ResultadoCargaCsvVO("OK", "Registro en tabla 'vacaciones', creado exitosamente."));
                        
                    }else{
                        mensajes.add(new ResultadoCargaCsvVO("ERROR", insertObj.getMsgError()));
                    }
                }
            }else{
                System.out.println(WEB_NAME+"[EmpleadosBp.procesaEmpleadosCSV]"
                    + "no cumple validaciones...");
            }  
            resultado.setMensajes(mensajes);
            listaResultados.add(resultado);
                
        });//fin iteracion empleados 
        return listaResultados;
    }
    
    /**
    * 
    * @param _empresaId
    * @param _codInterno (empleado.empl_rut)
    * 
    * @return 
    */
    public EmpleadoVO getEmpleado(String _empresaId, 
            String _codInterno){
        return empleadosDao.getEmpleado(_empresaId, _codInterno);
    }
    
    /**
    * 
    * 
    * @param _empresaId
    * @param _codInterno
    * @param _turnoId
    * @return 
    */
    public EmpleadoVO getEmpleado(String _empresaId, 
            String _codInterno, int _turnoId){
        return empleadosDao.getEmpleado(_empresaId, _codInterno, _turnoId);
    }
    
    /**
    * 
    * @param _empresaId
    * @param _runEmpleado
    * @return 
    */
    public EmpleadoVO getEmpleadoByEmpresaRun(String _empresaId, String _runEmpleado){
        return empleadosDao.getEmpleadoByEmpresaRun(_empresaId, _runEmpleado);
    }
    
    
    /**
     *
     * @param _empresaId
     * @param _codInterno
     * @param _cencoId
     * @return
     */
    public boolean tieneContratoVigente(String _empresaId, 
            String _codInterno, int _cencoId){
        return empleadosDao.tieneContratoVigente(_empresaId, _codInterno, _cencoId);
    }
        
    /**
    * 
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @param _cargo
    * @param _rutEmpleado
    * @param _nombres
    * @param _apePaterno
    * @param _apeMaterno
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<EmpleadoVO> getEmpleados(String _empresaId, 
            String _deptoId, 
            int _cencoId,
            int _cargo,
            String _rutEmpleado,
            String _nombres,
            String _apePaterno,
            String _apeMaterno,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<EmpleadoVO> lista = 
            empleadosDao.getEmpleados(_empresaId, 
            _deptoId, 
            _cencoId,
            _cargo,
            _rutEmpleado,
            _nombres,
            _apePaterno,
            _apeMaterno, _jtStartIndex, 
            _jtPageSize, _jtSorting);

        return lista;
    }
    
    
    /**
    * 
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @param _cargo
    * @param _rutEmpleado
    * @param _nombres
    * @param _apePaterno
    * @param _apeMaterno
    * @param _estado
    * @return 
    */
    public List<EmpleadoVO> getEmpleados(String _empresaId, 
            String _deptoId, 
            int _cencoId,
            int _cargo,
            String _rutEmpleado,
            String _nombres,
            String _apePaterno,
            String _apeMaterno,
            int _estado){
        
        List<EmpleadoVO> lista = 
            empleadosDao.getEmpleados(_empresaId, 
            _deptoId, 
            _cencoId,
            _cargo,
            _rutEmpleado,
            _nombres,
            _apePaterno,
            _apeMaterno, 
            _estado);

        return lista;
    }
    
    /**
    * 
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @return 
    */
    public List<EmpleadoVO> getEmpleadosDesvinculados(String _empresaId, 
            String _deptoId, 
            int _cencoId){
        
        List<EmpleadoVO> lista = 
            empleadosDao.getEmpleadosDesvinculados(_empresaId, 
            _deptoId, 
            _cencoId);

        return lista;
    }
    
    public List<EmpleadoVO> getEmpleadosByFiltro(EmpleadoVO _empleado){
        List<EmpleadoVO> lista = 
            empleadosDao.getEmpleadosByFiltro(_empleado);

        return lista;
    }
    
    public List<EmpleadoVO> getEmpleados(String _empresaId, 
            String _deptoId, 
            int _cencoId,
            int _cargo,
            int _idTurno,
            String _rutEmpleado,
            String _nombres,
            String _apePaterno,
            String _apeMaterno,
            int _estado,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<EmpleadoVO> lista = 
            empleadosDao.getEmpleados(_empresaId, 
            _deptoId, 
            _cencoId,
            _cargo,
            _idTurno,
            _rutEmpleado,
            _nombres,
            _apePaterno,
            _apeMaterno, 
            _estado,
            _jtStartIndex, 
            _jtPageSize, _jtSorting);

        return lista;
    }

    /**
    * 
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @param _cargo
    * @param _idTurno
    * @param _rutEmpleado
    * @param _nombres
    * @param _apePaterno
    * @param _apeMaterno
    * @param _estado
     * @param _cencosId
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<EmpleadoVO> getCaducados(String _empresaId, 
            String _deptoId, 
            int _cencoId,
            int _cargo,
            int _idTurno,
            String _rutEmpleado,
            String _nombres,
            String _apePaterno,
            String _apeMaterno,
            int _estado,
            String _cencosId,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<EmpleadoVO> lista = 
            empleadosDao.getCaducados(_empresaId, 
            _deptoId, 
            _cencoId,
            _cargo,
            _idTurno,
            _rutEmpleado,
            _nombres,
            _apePaterno,
            _apeMaterno, 
            _estado,
            _cencosId,
            _jtStartIndex, 
            _jtPageSize, _jtSorting);

        return lista;
    }
    
    /**
    * 
    * @param _objectToUpdate
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO updateEmpleadoCaducado(EmpleadoVO _objectToUpdate, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = empleadosDao.updateEmpleadoCaducado(_objectToUpdate);
        
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
    * @param _empleadoToUpdate
    * @param _eventdata
    * @return
    */
    public ResultCRUDVO update(EmpleadoVO _empleadoToUpdate, 
            MaintenanceEventVO _eventdata){
        System.out.println(WEB_NAME+"[EmpleadosBp.update]"
            + "Actualizar empleado "
            + ", rut(PK): " + _empleadoToUpdate.getCodInterno()
            + ", numFicha: " + _empleadoToUpdate.getRut()
            + ", empresaId: " + _empleadoToUpdate.getEmpresa().getId());
        
        boolean existeEmail = empleadosDao.existeEmail(_empleadoToUpdate.getRut(), 
            _empleadoToUpdate.getEmail(), _empleadoToUpdate.getEmpresa().getId());
        ResultCRUDVO updValues = new ResultCRUDVO();
        if (existeEmail){
            updValues.setThereError(true);
            updValues.setCodError(99);
            updValues.setMsgError("El email " + _empleadoToUpdate.getEmail()
                +" ya existe en el Sistema.");
            updValues.setMsg("Error al modificar empleado "
                + "Rut: '" + _empleadoToUpdate.getRut() + "'. "
                + "El email " + _empleadoToUpdate.getEmail()
                +" ya existe en el Sistema.");
            System.out.println(WEB_NAME+"[EmpleadosBp.update]Error: " + updValues.getMsg());
        }else{
            updValues = empleadosDao.update(_empleadoToUpdate);
            //if (_empleadoToUpdate.getEstado() == Constantes.ESTADO_NO_VIGENTE){
                System.out.println(WEB_NAME+"[EmpleadosBp.update]"
                    + "Modificar estado del usuario (Vigente/No Vigente).");
                UsersDAO usuarioDao = new UsersDAO(null);
                String codInterno = _empleadoToUpdate.getCodInterno();
                if (_empleadoToUpdate.getCodInternoCaracterAdicional() != null 
                    && _empleadoToUpdate.getCodInternoCaracterAdicional().compareTo("") != 0){
                        codInterno = codInterno + _empleadoToUpdate.getCodInternoCaracterAdicional();
                }
                ResultCRUDVO auxEvt = 
                    usuarioDao.setEstadoUsuario(codInterno.toUpperCase(), 
                        _empleadoToUpdate.getEstado());
                
                MaintenanceEventVO eventoUsuario = new MaintenanceEventVO();
                eventoUsuario.setUsername(_eventdata.getUsername());
                eventoUsuario.setDatetime(new Date());
                eventoUsuario.setUserIP(_eventdata.getUserIP());
                eventoUsuario.setType("USR");
                eventoUsuario.setEmpresaIdSource(_eventdata.getEmpresaId());
                eventoUsuario.setDeptoId(_empleadoToUpdate.getDepartamento().getId());
                eventoUsuario.setCencoId(_empleadoToUpdate.getCentroCosto().getId());
                eventoUsuario.setRutEmpleado(codInterno.toUpperCase());
                eventoUsuario.setType("USR");
                eventoUsuario.setDescription("Usuario "
                    + "'" + codInterno.toUpperCase() 
                    + "' queda con estado_id = " + _empleadoToUpdate.getEstado());
                eventsService.addEvent(eventoUsuario);
            //}
        }
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        System.out.println(WEB_NAME+"[EmpleadosBp.update]"
            + "Valida si hay cambio de turno para el empleado "
            + ", rut(PK): "+_empleadoToUpdate.getCodInterno()
            + ", numFicha: "+_empleadoToUpdate.getRut());
        asignacionTurnoBp.validaTurnoEmpleado(_empleadoToUpdate.getEmpresa().getId(), 
            _empleadoToUpdate.getCodInterno(),
            _empleadoToUpdate.getIdTurno(), 
            _eventdata.getUsername());
        
        return updValues;
    }
    
    /**
    * 
    * @param _empleadoToInsert
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO insert(EmpleadoVO _empleadoToInsert, 
            MaintenanceEventVO _eventdata){
        
        boolean existeInCenco = empleadosDao.existeInCenco(_empleadoToInsert.getEmpresa().getId(), 
            _empleadoToInsert.getCentroCosto().getId(), 
            _empleadoToInsert.getRut());
        
        boolean existeEmail = empleadosDao.existeEmail(_empleadoToInsert.getRut(),
            _empleadoToInsert.getEmail(), _empleadoToInsert.getEmpresa().getId());
        ResultCRUDVO insValues = new ResultCRUDVO();
        if (existeEmail){
            insValues.setThereError(true);
            insValues.setCodError(99);
            insValues.setMsgError("El email " + _empleadoToInsert.getEmail()
                +" ya existe en el Sistema.");
            insValues.setMsg("Error al crear empleado "
                + "Rut: '" + _empleadoToInsert.getRut() + "'. "
                + "El email " + _empleadoToInsert.getEmail()
                +" ya existe en el Sistema.");
            System.out.println(WEB_NAME+"[EmpleadosBp.insert]Error: " + insValues.getMsg());
        }else if (existeInCenco){
            CentroCostoBp cencoBp = new CentroCostoBp(props);
            CentroCostoVO infoCenco = cencoBp.getCentroCostoByKey(_empleadoToInsert.getDepartamento().getId(), 
                _empleadoToInsert.getCentroCosto().getId());
            
            insValues.setThereError(true);
            insValues.setCodError(99);
            insValues.setMsgError("El RUN: " + _empleadoToInsert.getRut()
                + " ya existe en el Centro de costo [ " + _empleadoToInsert.getCentroCosto().getId()+"," + infoCenco.getNombre() +"]");
            insValues.setMsg("Error al crear empleado "
                + "EmpresaID: '" + _empleadoToInsert.getEmpresa().getId() + "'. "
                + "RUN: '" + _empleadoToInsert.getRut() + "'. "        
                + "El RUN "
                + " ya existe en el Centro de costo [ " + _empleadoToInsert.getCentroCosto().getId()+"," + infoCenco.getNombre() +"]");
            System.out.println(WEB_NAME+"[EmpleadosBp.insert]Error: " + insValues.getMsg());
        }else{
            insValues = empleadosDao.insert(_empleadoToInsert);
            int perfilUsuario = Constantes.ID_PERFIL_EMPLEADO;
            String codInterno = _empleadoToInsert.getCodInterno();
            if (_empleadoToInsert.getCodInternoCaracterAdicional() != null 
                && _empleadoToInsert.getCodInternoCaracterAdicional().compareTo("") != 0){
                    codInterno = codInterno + _empleadoToInsert.getCodInternoCaracterAdicional();
            }
            String newUsername = codInterno.toUpperCase();
            String newPassword = codInterno.toUpperCase();
        
            if (_empleadoToInsert.getIdCargo() == Constantes.ID_CARGO_DIRECTOR){
                //Insertar Usuario con perfil Empleado
                insertarUsuario(_empleadoToInsert, perfilUsuario, newUsername, newPassword, _eventdata);
                
                //Insertar usuario con perfil Director
                perfilUsuario = Constantes.ID_PERFIL_DIRECTOR;
                //insertar usuario
                /**
                *    Para usuarios con perfil Director:
                *  - Nombre usuario: Debe ser letra inicial del nombre + apellido paterno en minúscula. 
                *      Ej “fgalindo”. 
                *    - Clave usuario: la clave de usuario debe ser 
                *               letra inicial del nombre 
                *               + letra inicial del apellido paterno 
                *               + año de nacimiento en minúscula. 
                *           Ej “fg1988”.  
                */
                //de donde saco el año de nacimiento..???
                String nombres = _empleadoToInsert.getNombres().toLowerCase();
                newUsername = nombres.substring(0, 1) + _empleadoToInsert.getApePaterno().toLowerCase();
                Calendar calendar11 = Calendar.getInstance();
                calendar11.setTime(_empleadoToInsert.getFechaNacimiento());
                newPassword = nombres.substring(0, 1) + calendar11.get(Calendar.YEAR);
            }
            System.out.println(WEB_NAME+"[EmpleadosBp.insert]"
                + "Insertar usuario con perfil Empleado o Director...");
            insertarUsuario(_empleadoToInsert, perfilUsuario, newUsername, newPassword, _eventdata);
            
            //*********************************************************************
            //*********Insertar registro en la tabla permiso_administrativo *******
            Date currentDate = new Date();
            SimpleDateFormat anioFormat = new SimpleDateFormat("yyyy");
            ParametroVO parametro =
                daoParams.getParametroByKey(_empleadoToInsert.getEmpresa().getId(), 
                    Constantes.ID_PARAMETRO_MAXIMO_SEMESTRAL_DIAS_PA);
            int MAXIMO_SEMESTRAL_DIAS_PA = (int)parametro.getValor();
            int CURRENT_YEAR =  Integer.parseInt(anioFormat.format(new Date()));
            int semestreActual = Utilidades.getSemestre(currentDate);
            System.out.println(WEB_NAME+"[EmpleadosBp.insert]"
                + "Nuevo empleado. Insertar registro en la tabla permiso_administrativo. "
                + "Empresa_id: " + _empleadoToInsert.getEmpresa().getId()
                + ", RUN empleado: " + _empleadoToInsert.getCodInterno()
                + ", MAXIMO_SEMESTRAL_DIAS_PA: " + MAXIMO_SEMESTRAL_DIAS_PA
                + ", CURRENT_YEAR: " + CURRENT_YEAR
                + ", semestreActual: " + semestreActual);
            ResultCRUDVO resultadoInsert = permisosAdminDao.insertaRegistroPermisoAdministrativo(_empleadoToInsert.getEmpresa().getId(),
                _empleadoToInsert.getCodInterno(),
                MAXIMO_SEMESTRAL_DIAS_PA,
                CURRENT_YEAR,
                semestreActual);
            
            if (resultadoInsert.isThereError()){
                System.out.println(WEB_NAME+"[EmpleadosBp.insert]"
                    + "Error al Insertar registro en la tabla permiso_administrativo. "
                    + resultadoInsert.getMsgError());
            }else{
                System.out.println(WEB_NAME+"[EmpleadosBp.insert]"
                    + "Registro insertado exitosamente en la tabla permiso_administrativo. ");
            }
            
            //*********************************************************************
            
        }
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        System.out.println(WEB_NAME+"[EmpleadosBp.insert]"
            + "Ingresar asignacion de turno para el empleado "
            + ", rut(PK): " + _empleadoToInsert.getCodInterno()
            + ", numFicha: " + _empleadoToInsert.getRut());
        asignacionTurnoBp.validaTurnoEmpleado(_empleadoToInsert.getEmpresa().getId(), 
            _empleadoToInsert.getCodInterno(),
            _empleadoToInsert.getIdTurno(), 
            _eventdata.getUsername());
        return insValues;
    }
    
    /**
    * 
    * 
    */
    private boolean insertarUsuario(EmpleadoVO _empleado,
            int _perfilUsuario, String _username, String _password,
            MaintenanceEventVO _eventdata){
    
        boolean isOk =true;
        UsuarioBp usuarioBp = new UsuarioBp(null);
        UsuarioVO usuario =new UsuarioVO();
        String codInterno = _empleado.getCodInterno();
        if (_empleado.getCodInternoCaracterAdicional() != null 
            && _empleado.getCodInternoCaracterAdicional().compareTo("") != 0){
                codInterno = codInterno + _empleado.getCodInternoCaracterAdicional();
        }
        usuario.setUsername(_username);
        usuario.setPassword(_password);
        usuario.setEstado(1);
        usuario.setIdPerfil(_perfilUsuario);
        usuario.setNombres(_empleado.getNombres());
        usuario.setApPaterno(_empleado.getApePaterno());
        usuario.setApMaterno(_empleado.getApeMaterno());
        usuario.setEmail(_empleado.getEmail());
        usuario.setEmpresaId(_empleado.getEmpresa().getId());
        
        //datos faltantes para log de eventos
        _eventdata.setDeptoId(_empleado.getDepartamento().getId());
        _eventdata.setCencoId(_empleado.getCentroCosto().getId());
        _eventdata.setRutEmpleado(codInterno.toUpperCase());
        _eventdata.setType("USR");
        //Insertar usuario
        ResultCRUDVO result = usuarioBp.insert(usuario, _eventdata);
        
        //Insertar centro de costo para el nuevo usuario
        UsuarioCentroCostoVO newUserCenco=
            new UsuarioCentroCostoVO(usuario.getUsername(),
            _empleado.getCentroCosto().getId(),
            0,
            null,
            _empleado.getEmpresa().getId(),
            _empleado.getDepartamento().getId());
        usuarioBp.insertCenco(newUserCenco);
        
        return isOk;
        
    }
    
    /*
    public ResultCRUDVO delete(ContractRelationVO _relationToDelete, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO insValues = contractRelService.delete(_relationToDelete);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    */
    
    /**
    * 
    */
    public int getEmpleadosCount(String _empresaId, 
            String _deptoId, 
            int _cencoId,
            int _cargo,
            int _idTurno,
            String _rutEmpleado,
            String _nombres,
            String _apePaterno,
            String _apeMaterno, 
            int _estado){
        
        return empleadosDao.getEmpleadosCount(_empresaId, _deptoId,
            _cencoId, _cargo, _idTurno, _rutEmpleado,
            _nombres, _apePaterno, 
            _apeMaterno, _estado);
    }
    
    /**
    * 
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @param _cargo
    * @param _idTurno
    * @param _rutEmpleado
    * @param _nombres
    * @param _apePaterno
    * @param _apeMaterno
    * @param _estado
     * @param _cencosId
    * @return 
    */
    public int getCaducadosCount(String _empresaId, 
            String _deptoId, 
            int _cencoId,
            int _cargo,
            int _idTurno,
            String _rutEmpleado,
            String _nombres,
            String _apePaterno,
            String _apeMaterno, 
            int _estado,
            String _cencosId){
        
        return empleadosDao.getCaducadosCount(_empresaId, _deptoId,
            _cencoId, _cargo, _idTurno, _rutEmpleado,
            _nombres, _apePaterno, 
            _apeMaterno, _estado, _cencosId);
    }

    public List<EmpleadoVO> getListaEmpleados(String _empresaId,
            String _deptoId, 
            int _cencoId, int _turnoId){
        List<EmpleadoVO> listaEmpleados = new ArrayList<>();
        
        System.out.println(WEB_NAME+"[EmpleadosBp."
            + "getListaEmpleados]empresa: "+_empresaId
            +", depto: "+_deptoId
            +", cenco: "+_cencoId);
        
        EmpleadosBp empleadosBp = new EmpleadosBp();
        
        if (_empresaId.compareTo("-1") != 0 
                && _deptoId.compareTo("-1") != 0
                && _cencoId != -1){
                    //todos los empleados del cenco
                    listaEmpleados = empleadosBp.getEmpleadosShort(_empresaId, 
                            _deptoId, 
                            _cencoId, 
                            -1,  
                            null, 
                            null, 
                            null, 
                            null, 
                            0, 
                            0, 
                            "empleado.empl_rut");
        }
        return listaEmpleados;
    }
 
    public List<EmpleadoVO> getEmpleadosSimpleByFiltro(String _empresaId, 
            String _deptoId, 
            int _cencoId, 
            int _estado, 
            boolean _articulo22){
    
        List<EmpleadoVO> listaEmpleados = new ArrayList<>();
        
        System.out.println(WEB_NAME+"[EmpleadosBp."
            + "getEmpleadosSimpleByFiltro]empresa: " + _empresaId
            + ", depto: " + _deptoId
            + ", cenco: " + _cencoId
            + ", estado: " + _estado
            + ", articulo22: " + _articulo22);
        
        //todos los empleados del cenco
        listaEmpleados = empleadosDao.getEmpleadosSimpleByFiltro(_empresaId, 
            _deptoId, 
            _cencoId, 
            _estado, 
            _articulo22);
        
        return listaEmpleados;
    } 
    
    public List<EmpleadoVO> getListaEmpleadosSimple(String _empresaId,
            String _deptoId, 
            int _cencoId){
        List<EmpleadoVO> listaEmpleados = new ArrayList<>();
        
        System.out.println(WEB_NAME+"[EmpleadosBp."
            + "getListaEmpleadosSimple]empresa: "+_empresaId
            +", depto: "+_deptoId
            +", cenco: "+_cencoId);
        
        //todos los empleados del cenco
        listaEmpleados = empleadosDao.getEmpleadosSimple(_empresaId, 
            _deptoId, 
            _cencoId);
        
        return listaEmpleados;
    }
    
    //15-09-2018
    public void openDbConnection(){
        empleadosDao.openDbConnection();
    }
    
    public void closeDbConnection(){
        empleadosDao.closeDbConnection();
    }
    
    /**
     *
     * @param _empleados
     * @throws SQLException
     */
    public void insertListEmpleados(ArrayList<EmpleadoVO> _empleados) throws SQLException {
        empleadosDao.insertListEmpleados(_empleados);
    }
    
}
