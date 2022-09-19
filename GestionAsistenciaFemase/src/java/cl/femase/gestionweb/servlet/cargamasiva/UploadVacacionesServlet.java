/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.servlet.cargamasiva;

import cl.femase.gestionweb.business.CentroCostoBp;
import cl.femase.gestionweb.business.DetalleAusenciaBp;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.VacacionesBp;
import cl.femase.gestionweb.common.ClientInfo;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.dao.MaintenanceEventsDAO;
import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.vo.CentroCostoVO;
import cl.femase.gestionweb.vo.DepartamentoVO;
import cl.femase.gestionweb.vo.DetalleAusenciaVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.EmpresaVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.ResultadoCargaCsvVO;
import cl.femase.gestionweb.vo.ResultadoCargaDataCsvVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import cl.femase.gestionweb.vo.VacacionesVO;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

/**
 * Servlet to handle File upload request from Client
 * @author Alexander
 */
public class UploadVacacionesServlet extends BaseServlet {

    //private final String UPLOAD_DIRECTORY = "C:/uploads";
  
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
      
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        HashMap<String, Double> parametrosSistema = (HashMap<String, Double>)session.getAttribute("parametros_sistema");
        DetalleAusenciaBp ausenciasBp   = new DetalleAusenciaBp(appProperties);
        EmpleadosBp empleadosBp         = new EmpleadosBp(appProperties);
        CentroCostoBp cencosbp          = new CentroCostoBp(null);
                
        if (userConnected != null){
            SimpleDateFormat fechaFormat = new SimpleDateFormat("yyyy-MM-dd");
            //process only if its multipart content
            if(ServletFileUpload.isMultipartContent(request)){
                String pathUploadedFiles = appProperties.getUploadsPath();
                ArrayList<ResultadoCargaDataCsvVO> listaResultados = new ArrayList<>();
                
//                LinkedHashMap<String,DetalleAusenciaVO> registrosOk=
//                    new LinkedHashMap<>();
//                LinkedHashMap<String,DetalleAusenciaVO> registrosError=
//                    new LinkedHashMap<>();
                try {
                    List<FileItem> multiparts = new ServletFileUpload(
                        new DiskFileItemFactory()).parseRequest(request);
                    /**
                     * Si la vacacion a insertar es anterior a la fecha actual ---> insertar vacacion
                     * Si la vacacion a insertar es posterior a la fecha actual --> 
                     *      - validar saldo de vacaciones. Si hay saldo         --> insertar vacacion
                     *                                     Si no tiene saldo    --> NO insertar vacacion, mostrar error
                     */
                    for(FileItem item : multiparts){
                        if(!item.isFormField()){
                            File auxfile = new File(item.getName());
                            String filename = auxfile.getName();
                            System.out.println(WEB_NAME+"[UploadVacacionesServlet]"
                                + "Filename: "+filename);
                            String extension = FilenameUtils.getExtension(filename);
                            String filePathLoaded= pathUploadedFiles + File.separator + filename;
                            System.out.println(WEB_NAME+"[UploadVacacionesServlet]"
                                + "filePathLoaded="+filePathLoaded);
                            item.write( new File(filePathLoaded));
                            if (extension.compareTo("csv") == 0){
                                Reader in = new FileReader(filePathLoaded);
                                Iterable<CSVRecord> records = 
                                CSVFormat.RFC4180.withHeader("empresa_id",
                                    "rut_empleado",
                                    "fecha_inicio", 
                                    "fecha_fin").parse(in);
                                DetalleAusenciaVO data;
                                for (CSVRecord record : records) {
                                    System.out.println(WEB_NAME+"[UploadVacacionesServlet]"
                                        + "recordNumber: "+record.getRecordNumber()
                                        + ". Linea csv: "+record.toString());
                                    if (record.getRecordNumber() > 0){
                                        ResultadoCargaDataCsvVO resultado=new ResultadoCargaDataCsvVO();
                                        
                                        ArrayList<ResultadoCargaCsvVO> mensajes = new ArrayList<>();
                                        
                                        String empresaId    = record.get("empresa_id");
                                        String rut          = record.get("rut_empleado");
                                        String inicio       = record.get("fecha_inicio");
                                        String fin          = record.get("fecha_fin");
                                        String rowKey       = empresaId + "|" + rut + "|" + inicio;
                                        data = new DetalleAusenciaVO();
                                        data.setEmpresaId(empresaId);
                                        data.setRutEmpleado(rut);
                                        data.setFechaInicioAsStr(inicio);
                                        data.setFechaFinAsStr(fin);
                                        //fijos
                                        data.setIdAusencia(Constantes.ID_AUSENCIA_VACACIONES);
                                        data.setAusenciaAutorizada("S");
                                        data.setPermiteHora("N");
                                        
                                        EmpleadoVO infoEmpleado = 
                                            empleadosBp.getEmpleado(empresaId, rut);
                                        boolean empleadoValido=true;
                                        if (infoEmpleado == null){
                                            System.out.println(WEB_NAME+"[UploadVacacionesServlet]"
                                                + "recordNumber:" + record.getRecordNumber() 
                                                + ", empresaId: " + empresaId
                                                + ", rut: " + rut + " no es valido...");
                                            
                                            empleadoValido = false;
                                            infoEmpleado = new EmpleadoVO();
                                            infoEmpleado.setRut(rut);
                                            
                                            EmpresaVO auxempresa = new EmpresaVO();
                                            auxempresa.setId(empresaId);
                                            infoEmpleado.setEmpresa(auxempresa);
                                            
                                            DepartamentoVO auxDepto = new DepartamentoVO();
                                            auxDepto.setId("-1");
                                            infoEmpleado.setDepartamento(auxDepto);
                                            
                                            CentroCostoVO auxCenco = new CentroCostoVO();
                                            auxCenco.setId(-1);
                                            infoEmpleado.setCentroCosto(auxCenco);
                                        }
                                        
                                        if (empleadoValido){
                                            resultado.setEmpleado(infoEmpleado);
                                            String rutAutorizador = null;
                                            //buscar al director que autoriza ausencia y que esta vigente. Si hay mas de uno, se toma el primero
                                            List<EmpleadoVO> directoresCenco = 
                                                cencosbp.getDirectoresCenco(infoEmpleado.getEmpresaId(), infoEmpleado.getDeptoId(), infoEmpleado.getCencoId());
                                            for(EmpleadoVO itEmpleado : directoresCenco){
                                                if (itEmpleado.isAutorizaAusencia() && itEmpleado.getEstado() == Constantes.ESTADO_VIGENTE){
                                                    rutAutorizador = itEmpleado.getRut();
                                                }
                                            }
                                            System.out.println(WEB_NAME+"[UploadVacacionesServlet]"
                                                + "recordNumber:" + record.getRecordNumber() 
                                                + ", empresaId: " + infoEmpleado.getEmpresaId()
                                                + ", deptoId: " + infoEmpleado.getDeptoId()
                                                + ", cencoId: " + infoEmpleado.getCencoId()
                                                + ", rutAutorizador: " + rutAutorizador);
                                            
                                            data.setRutAutorizador(rutAutorizador);
                                            infoEmpleado.setRutAutorizaVacacion(rutAutorizador);
                                            DetalleAusenciaVO infoVacacion = new DetalleAusenciaVO();
                                            infoVacacion.setFechaInicioAsStr(inicio);
                                            infoVacacion.setFechaFinAsStr(fin);
                                            infoVacacion.setRutAutorizador(rutAutorizador);
                                            resultado.setVacacion(infoVacacion);
                                            
                                            //validaciones para el ingreso de ausencia por dia
                                            ArrayList<DetalleAusenciaVO>ausenciasConflicto = 
                                                ausenciasBp.getAusenciasConflicto(data.getRutEmpleado(),
                                                    false,
                                                    data.getFechaInicioAsStr(),
                                                    data.getFechaFinAsStr(), 
                                                    null, 
                                                    null);

                                            String mensajeFinal = "Datos ingresados exitosamente.";
                                            Calendar mycal = Calendar.getInstance(new Locale("es","CL"));
                                            Date fechaActual = mycal.getTime();
                                            Date fechaHasta = fechaFormat.parse(data.getFechaFinAsStr());
                                            boolean vacacionFutura = false;
                                            if (fechaHasta.after(fechaActual)) vacacionFutura = true;
                                            System.out.println(WEB_NAME+"[UploadVacacionesServlet]"
                                                + "Vacacion futura?" + vacacionFutura);
                                            
                                            if (ausenciasConflicto.isEmpty()){
                                                VacacionesVO saldoVacaciones =  
                                                    validarVacaciones(data,
                                                        appProperties,
                                                        userConnected,
                                                        request);
                                                System.out.println(WEB_NAME+"[UploadVacacionesServlet]"
                                                    + "dias efectivos tomados: "
                                                    + saldoVacaciones.getDiasEfectivos());
                                                data.setDiasEfectivosVacaciones(saldoVacaciones.getDiasEfectivos());
                                                MaintenanceVO insertResult = new MaintenanceVO();
                                                if (vacacionFutura){
                                                    //validar saldo de vacaciones para ingreso de vacacion con fecha futura
                                                    if (saldoVacaciones.getMensajeValidacion() == null){
                                                        System.out.println(WEB_NAME+"[UploadVacacionesServlet]"
                                                            + "Vacacion fecha futura. Insertar vacacion");
                                                        insertResult = insertarVacacion(request, appProperties, parametrosSistema, userConnected, data);
                                                    }else{
                                                        mensajeFinal= saldoVacaciones.getMensajeValidacion();
                                                        System.out.println(WEB_NAME+"[UploadVacacionesServlet]"
                                                            + "Vacacion fecha futura. Erroa al Insertar vacacion");
                                                        data.setMensajeError(mensajeFinal);
                                                        mensajes.add(new ResultadoCargaCsvVO("ERROR",
                                                            "Vacacion fecha futura"));
                                                    }
                                                }else{//fin if vacacionFutura
                                                    System.out.println(WEB_NAME+"[UploadVacacionesServlet]"
                                                        + "Vacacion fecha pasada. "
                                                        + "Insertar vacacion sin validar saldo de dias");
                                                    System.out.println(WEB_NAME+"[UploadVacacionesServlet]Insertar vacacion. "
                                                        + "empresaId: " + empresaId
                                                        + ", rut: " + rut
                                                        + ", autorizador: " + data.getRutAutorizador()
                                                        + ", dias_efectivos: " + data.getDiasEfectivosVacaciones());
                                                    
                                                    insertResult = 
                                                        insertarVacacion(request, appProperties, parametrosSistema, userConnected, data);
                                                }

                                                if (insertResult.isThereError()){
                                                    System.out.println(WEB_NAME+"[UploadVacacionesServlet]"
                                                        + "Error -1- al insertar vacacion");
                                                    //registrosError.put(rowKey, data);
                                                    mensajes.add(new ResultadoCargaCsvVO("ERROR",
                                                        insertResult.getMsgError()));
                                                }else {
                                                    System.out.println(WEB_NAME+"[UploadVacacionesServlet]"
                                                        + "Vacacion insertada OK. Actualizar "
                                                        + "dias efectivos de vacaciones recien ingresadas.");
                                                    ausenciasBp.updateDiasEfectivosVacaciones(data, null);
                                                    //registrosOk.put(rowKey, data);
                                                    
                                                    mensajes.add(new ResultadoCargaCsvVO("OK",
                                                        "Vacacion insertada OK."));
                                                    
                                                }
                                            }else {
                                                System.err.println("[UploadVacacionesServlet]"
                                                    + "Hay conflicto con ausencias existentes...");
                                                request.setAttribute("msgerror", "Ausencia conflicto");
                                                mensajeFinal= "Hay conflicto con ausencias existentes...";
                                                mensajes.add(new ResultadoCargaCsvVO("ERROR",mensajeFinal));
                                                
                                                data.setMensajeError(mensajeFinal);
                                                //registrosError.put(rowKey, data);
                                            }

                                            System.out.println(WEB_NAME+"[UploadVacacionesServlet]"
                                                + "data vacacion:"
                                                + data.toString());
                                            
                                        }else{
                                            mensajes.add(new 
                                                ResultadoCargaCsvVO("ERROR", 
                                                    "Empresa/empleado no corresponde"));
                                            System.out.println(WEB_NAME+"[UploadVacacionesServlet]"
                                                + "Empresa/empleado no corresponde");
                                        }
                                        System.out.println(WEB_NAME+"[UploadVacacionesServlet]"
                                            + "Add mensaje---");
                                        resultado.setEmpleado(infoEmpleado);
                                        resultado.setMensajes(mensajes);
                                        listaResultados.add(resultado);
            
                                    }
                                }
                            }else{
                                session.setAttribute("mensaje", 
                                    "El formato debe ser CSV");
                            }
                        }
                    }
           
                    //File uploaded successfully
                    request.setAttribute("tipo", "UploadVacaciones");
                    request.setAttribute("vacacionesCargadas", listaResultados);
//                    request.setAttribute("vacacionesOk", registrosOk);
//                    request.setAttribute("vacacionesError", registrosError);
                    session.setAttribute("mensaje", "Archivo Cargado");
                    
                    //***************Agregar evento al log
                    MaintenanceEventVO evento=new MaintenanceEventVO();
                    evento.setUsername(userConnected.getUsername());
                    evento.setDatetime(new Date());
                    evento.setUserIP(request.getRemoteAddr());
                    evento.setType("UPLOAD_VAC");//
                    evento.setEmpresaIdSource(userConnected.getEmpresaId());
                    ClientInfo clientInfo = new ClientInfo();
                    clientInfo.printClientInfo(request);
                    /**
                    * Sistema operativo y navegador
                    */
                    evento.setOperatingSystem(clientInfo.getClientOS(request));
                    evento.setBrowserName(clientInfo.getClientBrowser(request));
            
                    evento.setDescription("Carga de Vacaciones CSV");
                    
                    MaintenanceEventsDAO eventsDao = new MaintenanceEventsDAO(appProperties);
                    eventsDao.addEvent(evento); 
                } catch (Exception ex) {
                   session.setAttribute("mensaje", "File Upload Failed due to " + ex);
                }          
         
            }else{
                session.setAttribute("mensaje",
                    "Sorry this Servlet only handles file upload request");
            }
            request.getRequestDispatcher("/cargamasiva/upload_vacaciones_result.jsp").forward(request, response);
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            System.err.println("Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
        }
    }
    
    /**
    * Insertar ausencia=Vacacion
    */
    private MaintenanceVO insertarVacacion(HttpServletRequest _request,
            PropertiesVO _appProperties,
            HashMap<String, Double> _parametrosSistema,
            UsuarioVO _userConnected, 
            DetalleAusenciaVO _ausencia){
       
        System.out.println(WEB_NAME+"[UploadVacacionesServlet.insertarVacacion]"
            + "Insertar detalle ausencia (vacacion)");
        String mensajeFinal = null;
        DetalleAusenciaBp ausenciasBp   = new DetalleAusenciaBp(_appProperties);
        VacacionesBp vacacionesBp       = new VacacionesBp(_appProperties);
        VacacionesVO saldoVacaciones = new VacacionesVO();
                
        MaintenanceEventVO resultado=new MaintenanceEventVO();
        resultado.setUsername(_userConnected.getUsername());
        resultado.setDatetime(new Date());
        resultado.setUserIP(_request.getRemoteAddr());
        resultado.setType("DAU");
        resultado.setEmpresaIdSource(_userConnected.getEmpresaId());
                                        
        resultado.setRutEmpleado(_ausencia.getRutEmpleado());
        MaintenanceVO doCreate = ausenciasBp.insert(_ausencia, resultado);
        
////        if (!doCreate.isThereError()){
////            System.out.println(WEB_NAME+"[UploadVacacionesServlet.insertarVacacion]"
////                + "Insertar vacaciones."
////                + "Recalcular saldo dias de vacaciones "
////                + "para empleado. "
////                + "empresa_id: " + _ausencia.getEmpresaId()
////                +", rutEmpleado: " + _ausencia.getRutEmpleado());
////            vacacionesBp.calculaDiasVacaciones(_userConnected.getUsername(), 
////                _ausencia.getEmpresaId(), _ausencia.getRutEmpleado(), 
////                _parametrosSistema);
////            List<VacacionesVO> infoVacaciones = 
////                vacacionesBp.getInfoVacaciones(_ausencia.getEmpresaId(), 
////                    _ausencia.getRutEmpleado(), -1, -1, -1, "vac.rut_empleado");
////            if (infoVacaciones != null) saldoVacaciones = infoVacaciones.get(0);
////        }
        System.out.println(WEB_NAME+"[UploadVacacionesServlet.insertarVacacion]"
            + "paso 1");
        System.out.println(WEB_NAME+"[UploadVacacionesServlet.insertarVacacion]"
            + "paso 2");
        if (doCreate.isThereError()){
            mensajeFinal= "Error al insertar vacacion " + doCreate.getMsgError();
            doCreate.setMsg(mensajeFinal);
        }
        System.out.println(WEB_NAME+"[UploadVacacionesServlet.insertarVacacion]"
            + "Saliendo del metodo OK.");
        return doCreate;
    }
    
    /**
    * 
    * Valida si los dias de vacaciones seleccionados estan disponibles
    *   1.- Obtener valor de 'vacaciones'.'saldo_dias' --->dias disponibles de vacaciones
    * 
    *   2.- Se calculan los dias de vacaciones que se estan solicitando. 
    *       Esto se realiza en base al rango de fechas seleccionado (inicio y fin) --->dias_solicitados
    *       2.1 Se deben contabilizar solo los dias en que le corresponde turno. Sab y Domingo no se deben contabilizar
    * 
    *   3.- Si (dias_solicitados > saldo_dias) {
    *           --> retornar 'los dias solicitados ([dias_solicitados]) superan el saldo de dias disponibles: [saldo_dias]'
    *       }sino{
    *           --> vacaciones correctas. Actualizar saldo
    *       }
    *           
    */
    private VacacionesVO validarVacaciones(DetalleAusenciaVO _datosAusencia,
            PropertiesVO _appProperties, 
            UsuarioVO _userConnected, 
            HttpServletRequest _request){
        VacacionesVO saldoVacaciones=new VacacionesVO();
                
        ////String mensaje = null;
//        EmpleadosBp empleadoBp  = new EmpleadosBp(_appProperties);
//        TurnosBp turnoBp        = new TurnosBp(_appProperties);
//        DetalleTurnosBp detalleTurnoBp = new DetalleTurnosBp(_appProperties);
//        TurnoRotativoBp turnoRotBp = new TurnoRotativoBp(_appProperties);
        VacacionesBp vacacionesBp = new VacacionesBp(_appProperties);
               
        MaintenanceEventVO resultado=new MaintenanceEventVO();
        resultado.setUsername(_userConnected.getUsername());
        resultado.setDatetime(new Date());
        resultado.setUserIP(_request.getRemoteAddr());
        resultado.setType("DAU");
        resultado.setEmpresaIdSource(_userConnected.getEmpresaId());
        int diasSolicitados=0;
        /*
        *   1.- Obtener valor de 'vacaciones'.'saldo_dias' --->dias disponibles de vacaciones
        * 
        *   2.- Se calculan los dias de vacaciones que se estan solicitando. 
        *       Esto se realiza en base al rango de fechas seleccionado (inicio y fin) --->dias_solicitados
        *       2.1 Se deben contabilizar solo los dias en que le corresponde turno. Sab y Domingo no se deben contabilizar
        * 
        *   3.- Si (dias_solicitados > saldo_dias) {
        *           --> retornar 'los dias solicitados ([dias_solicitados]) superan el saldo de dias disponibles: [saldo_dias]'
        *       }sino{
        *           Vacaciones correctas.
        *           Actualizar saldo de dias de vacaciones.
        *       }
        */
        
        List<VacacionesVO> infoVacaciones = 
            vacacionesBp.getInfoVacaciones(_datosAusencia.getEmpresaId(), 
                _datosAusencia.getRutEmpleado(), -1, -1, -1, "vac.rut_empleado");
        
        System.out.println(WEB_NAME+"[UploadVacacionesServlet.validarVacaciones]"
            + "EmpresaId: " + _datosAusencia.getEmpresaId()
            + ", rutEmpleado: " + _datosAusencia.getRutEmpleado()
            + ", fecha inicio: " + _datosAusencia.getFechaInicioAsStr()
            + ", fecha fin: " + _datosAusencia.getFechaFinAsStr());

        if (infoVacaciones.isEmpty()){
            System.out.println(WEB_NAME+"[UploadVacacionesServlet.validarVacaciones]"
                + "EmpresaId: " + _datosAusencia.getEmpresaId()
                + ", rutEmpleado: " + _datosAusencia.getRutEmpleado()
                + ", No tiene informacion de vacaciones (saldo)");
            saldoVacaciones.setMensajeValidacion("No tiene informacion de vacaciones (saldo)");
        }else{
            //03-02-2020
            String fechaInicioVacaciones;
            String fechaFinVacaciones;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try{
                Date date1 = sdf.parse(_datosAusencia.getFechaInicioAsStr());
                Date date2 = sdf.parse(_datosAusencia.getFechaFinAsStr());
                fechaInicioVacaciones = _datosAusencia.getFechaInicioAsStr();
                fechaFinVacaciones = _datosAusencia.getFechaFinAsStr();
            }catch(ParseException pex){
                fechaInicioVacaciones = Utilidades.getFechaYYYYmmdd(_datosAusencia.getFechaInicioAsStr());
                fechaFinVacaciones = Utilidades.getFechaYYYYmmdd(_datosAusencia.getFechaFinAsStr());
            }
                        
            System.out.println(WEB_NAME+"[UploadVacacionesServlet.validarVacaciones]"
                + "Iterar fechas en el rango. "
                + "Inicio: " + fechaInicioVacaciones
                + ", Fin: " + fechaFinVacaciones);
            saldoVacaciones = infoVacaciones.get(0);
            double saldoDias = saldoVacaciones.getSaldoDias();
            diasSolicitados = 
                vacacionesBp.getDiasEfectivos(fechaInicioVacaciones, 
                    fechaFinVacaciones, 
                    saldoVacaciones.getDiasEspeciales(),
                    _datosAusencia.getEmpresaId(), 
                    _datosAusencia.getRutEmpleado());
            
            System.out.println(WEB_NAME+"[UploadVacacionesServlet.validarVacaciones]"
                + "EmpresaId: " + _datosAusencia.getEmpresaId()
                + ", rutEmpleado: " + _datosAusencia.getRutEmpleado()
                + ", dias solicitados: " + diasSolicitados 
                + ", dias disponibles(a la fecha): " + saldoDias);
     
            //2020-03-15. Se deshabilita validacion de dias solicitados vs el saldo actual.
            
            if (diasSolicitados > saldoDias) {
                System.out.println(WEB_NAME+"[UploadVacacionesServlet.validarVacaciones]"
                    + "EmpresaId: " + _datosAusencia.getEmpresaId()
                    + ", rutEmpleado: " + _datosAusencia.getRutEmpleado()
                    + ", los dias solicitados ("+diasSolicitados+") "
                    + "superan el saldo de dias disponibles: "+saldoDias);
                saldoVacaciones.setMensajeValidacion("Los dias solicitados "
                    + "("+diasSolicitados+") superan el saldo "
                    + "de dias disponibles: "+saldoDias);
                //enviar correo al director?
            }else{
                System.out.println(WEB_NAME+"[UploadVacacionesServlet.validarVacaciones]"
                    + "EmpresaId: " + _datosAusencia.getEmpresaId()
                    + ", rutEmpleado: " + _datosAusencia.getRutEmpleado()
                    + ", dias solicitados(A): " + diasSolicitados);
     
                double saldoFinal = saldoDias - diasSolicitados;
                saldoVacaciones.setSaldoDias(saldoFinal);
                saldoVacaciones.setDiasEfectivos(diasSolicitados);
                //_datosAusencia.getFechaInicioAsStr(), _datosAusencia.getFechaFinAsStr()
                saldoVacaciones.setFechaInicioUltimasVacaciones(fechaInicioVacaciones);
                saldoVacaciones.setFechaFinUltimasVacaciones(fechaFinVacaciones);
                vacacionesBp.updateSaldoYUltimasVacaciones(saldoVacaciones, resultado);
                System.out.println(WEB_NAME+"[UploadVacacionesServlet.validarVacaciones]"
                    + "EmpresaId: " + _datosAusencia.getEmpresaId()
                    + ", rutEmpleado: " + _datosAusencia.getRutEmpleado()
                    + ", dias solicitados(B): " + saldoVacaciones.getDiasEfectivos());
            }
        }
       
        return saldoVacaciones;
    }
}
