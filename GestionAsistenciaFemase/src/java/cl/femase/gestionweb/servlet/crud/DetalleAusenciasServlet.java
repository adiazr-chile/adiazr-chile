/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cl.femase.gestionweb.servlet.crud;

import cl.femase.gestionweb.business.DetalleAusenciaBp;
import cl.femase.gestionweb.business.DetalleTurnosBp;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.MaintenanceEventsBp;
import cl.femase.gestionweb.business.TurnoRotativoBp;
import cl.femase.gestionweb.business.TurnosBp;
import cl.femase.gestionweb.business.VacacionesBp;
import cl.femase.gestionweb.common.ClientInfo;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.Utilidades;
import com.google.gson.Gson;
import cl.femase.gestionweb.dao.DetalleAusenciaDAO;
import cl.femase.gestionweb.dao.EmpleadosDAO;
import cl.femase.gestionweb.dao.PermisosAdministrativosDAO;
import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.vo.DetalleAusenciaVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import cl.femase.gestionweb.vo.VacacionesVO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@WebServlet("/api/detalle_ausencias")
public class DetalleAusenciasServlet extends BaseServlet {

    private DetalleAusenciaDAO dao;
    private EmpleadosDAO daoEmpleados;
    
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        if (userConnected != null){
            setResponseHeaders(response);processRequest(request, response);
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
        }

    }

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        if (userConnected != null){
            setResponseHeaders(response);processRequest(request, response);
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
        }
    }
    
    /**
    * 
    * @param req
    * @param resp
    */
    @Override
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String jsonStr = sb.toString();
        JsonObject jsonObj = JsonParser.parseString(jsonStr).getAsJsonObject();
        String accion = jsonObj.has("accion") ? jsonObj.get("accion").getAsString() : null;

        Gson gson = new Gson();
        resp.setContentType("application/json; charset=UTF-8");

        HttpSession session = req.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties = (PropertiesVO) application.getAttribute("appProperties");
        UsuarioVO userConnected = (UsuarioVO) session.getAttribute("usuarioObj");

        dao = new DetalleAusenciaDAO(appProperties);
        daoEmpleados = new EmpleadosDAO(appProperties);
        System.out.println("[DetalleAusenciasServlet.processRequest]params. accion: " + accion);

        if ("listar".equalsIgnoreCase(accion)) {
            System.out.println("[DetalleAusenciasServlet.processRequest]params. Listar ausencias para el empleado");
            String runEmpleado = jsonObj.has("empleado") ? jsonObj.get("empleado").getAsString() : null;
            String fechaInicio = jsonObj.has("fechaInicio") ? jsonObj.get("fechaInicio").getAsString() : null;
            String fechaFin = jsonObj.has("fechaFin") ? jsonObj.get("fechaFin").getAsString() : null;
            String strTipoAusencia = jsonObj.has("tipoAusencia") ? jsonObj.get("tipoAusencia").getAsString() : null;
            int tipoAusencia = Integer.parseInt(strTipoAusencia);

            EmpleadoVO infoEmpleado = daoEmpleados.getEmpleadoByEmpresaRun(userConnected.getEmpresaId(), runEmpleado);
            req.setAttribute("admAusencias_cencoId", infoEmpleado.getCencoId());
            req.setAttribute("admAusencias_cencoNombre", infoEmpleado.getCencoNombre());

            System.out.println("[DetalleAusenciasServlet.listar]params. " +
                " EmpresaId: " + userConnected.getEmpresaId() +
                " , runEmpleado: " + runEmpleado +
                " , fechaInicio: " + fechaInicio +
                " , fechaFin: " + fechaFin +
                " , tipoAusencia: " + tipoAusencia);

            List<DetalleAusenciaVO> lista =
                dao.getAusenciasFiltro(userConnected.getEmpresaId(),
                    runEmpleado,
                    fechaInicio,
                    fechaFin,
                    tipoAusencia);

            gson.toJson(lista, resp.getWriter());
        } else if ("modificar".equalsIgnoreCase(accion)) {
            System.out.println("[DetalleAusenciasServlet.processRequest] Modificar registro");
            boolean exito = false;
            String mensaje = "";

            try {
                // Usar el objeto VO para mapear todos los datos recibidos del front
                DetalleAusenciaModVO data = gson.fromJson(jsonStr, DetalleAusenciaModVO.class); 
                System.out.println("[DetalleAusenciasServlet.processRequest]Datos recibidos:" + data.toString());
                ResultadoAccionVO resultObj = modificarRegistroAusencia(session, req, appProperties, userConnected, data);
                mensaje = resultObj.getMensaje();
                exito = resultObj.isExito();
                
                System.out.println("[DetalleAusenciasServlet.processRequest]Ausencia modificada. "
                    + "Retornar, exito?" + exito 
                    +", mensaje: "+ mensaje);
                
            } catch (Exception e) {
                mensaje = "Error (Exception) al modificar el registro: " + e.getMessage();
                exito = false;
                System.err.println("Error al recibir datos para modificar registro: " + e.toString());
            }

            // Respuesta simple en JSON
            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            JsonObject respuesta = new JsonObject();
            respuesta.addProperty("exito", exito);
            respuesta.addProperty("mensaje", mensaje);
            out.print(respuesta.toString());
            out.flush();
            out.close();
            return;

        } else if ("eliminar".equalsIgnoreCase(accion)) {
            int correlativoAusencia = jsonObj.has("id") ? jsonObj.get("id").getAsInt() : 0;
            //int correlativoAusencia = Integer.parseInt(req.getParameter("id"));
            System.out.println("[DetalleAusenciasServlet.processRequest]"
                + "Eliminar registro, detalle_ausencia.id= " + correlativoAusencia);
            boolean exito = false;
            String mensaje = "";
            try {
                // Usar el objeto VO para mapear todos los datos recibidos del front
                DetalleAusenciaModVO data = gson.fromJson(jsonStr, DetalleAusenciaModVO.class); 
                System.out.println("[DetalleAusenciasServlet.processRequest]Datos recibidos:" + data.toString());
                ResultadoAccionVO resultObj = eliminarRegistroAusencia(session, req, appProperties, userConnected, correlativoAusencia);
                mensaje = resultObj.getMensaje();
                exito = resultObj.isExito();
                
                System.out.println("[DetalleAusenciasServlet.processRequest]Ausencia eliminada. "
                    + "Retornar, exito?" + exito 
                    +", mensaje: "+ mensaje);
                
            } catch (Exception e) {
                mensaje = "Error (Exception) al modificar el registro: " + e.getMessage();
                exito = false;
                System.err.println("Error al recibir datos para modificar registro: " + e.toString());
            }

            // Respuesta simple en JSON
            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            JsonObject respuesta = new JsonObject();
            respuesta.addProperty("exito", exito);
            respuesta.addProperty("mensaje", mensaje);
            out.print(respuesta.toString());
            out.flush();
            out.close();
            return;
        } else {
            resp.getWriter().print("{\"result\":\"error\", \"msg\":\"Accion no soportada\"}");
        }
    }

    /**
    * 
    */
    private ResultadoAccionVO eliminarRegistroAusencia(HttpSession _session, 
            HttpServletRequest _request, 
            PropertiesVO _appProperties, 
            UsuarioVO _userConnected,
            int _correlativoAusencia){
        
        ResultadoAccionVO resultadoEliminar = new ResultadoAccionVO();
         
        System.out.println(WEB_NAME+"[DetalleAusenciasServlet]"
            + "Ausencias - Eliminar registro detalle ausencia, "
            + "correlativo: " + _correlativoAusencia);
        try{
            VacacionesBp vacacionesBp = new VacacionesBp(_appProperties);
            DetalleAusenciaBp detAusenciaBp = new DetalleAusenciaBp(_appProperties);
            HashMap<String, Double> parametrosSistema = (HashMap<String, Double>)_session.getAttribute("parametros_sistema");
            
            DetalleAusenciaVO detalleAusencia = 
                detAusenciaBp.getDetalleAusenciaByCorrelativo(_correlativoAusencia);
            
            MaintenanceEventVO eventoAuditoria = new MaintenanceEventVO();
            eventoAuditoria.setUsername(_userConnected.getUsername());
            eventoAuditoria.setDatetime(new Date());
            eventoAuditoria.setUserIP(_request.getRemoteAddr());
            eventoAuditoria.setType("DAU");
            eventoAuditoria.setEmpresaIdSource(_userConnected.getEmpresaId());
            
            System.out.println(WEB_NAME+"[servlet."
                + "DetalleAusenciasServlet.eliminarRegistroAusencia]"
                + "Antes de ejecutar el DELETE en tabla 'detalle_ausencia'");
            ResultCRUDVO doDelete = detAusenciaBp.delete(detalleAusencia, eventoAuditoria);
            System.out.println(WEB_NAME+"[servlet."
                + "DetalleAusenciasServlet.eliminarRegistroAusencia]"
                + "Despues de ejecutar el DELETE en tabla 'detalle_ausencia'");
            System.out.println(WEB_NAME+"[servlet."
                + "DetalleAusenciasServlet.eliminarRegistroAusencia]"
                + "Delete hay Error()? " + doDelete.isThereError()
                +", idAusencia: " + detalleAusencia.getIdAusencia());
            if (!doDelete.isThereError()) {
                if (detalleAusencia.getIdAusencia() == Constantes.ID_AUSENCIA_VACACIONES){//VACACIONES
                    System.out.println(WEB_NAME+"[servlet."
                        + "DetalleAusenciasServlet.eliminarRegistroAusencia]Delete vacacion."
                        + "Recalcular saldo dias de vacaciones "
                        + "para empleado. "
                        + "empresa_id: " + detalleAusencia.getEmpresaId()
                        +", rutEmpleado: " + detalleAusencia.getRutEmpleado());
                    vacacionesBp.calculaDiasVacaciones(_userConnected.getUsername(), 
                        detalleAusencia.getEmpresaId(), detalleAusencia.getRutEmpleado(), 
                        parametrosSistema);
                    System.out.println(WEB_NAME+"[servlet."
                        + "DetalleAusenciasServlet.eliminarRegistroAusencia]"
                        + "Actualizar saldos de vacaciones "
                        + "en tabla detalle_ausencia "
                        + "(usar nueva funcion setsaldodiasvacacionesasignadas). "
                        + "Run: "+ detalleAusencia.getRutEmpleado());
                    //DetalleAusenciaBp detAusenciaBp = new DetalleAusenciaBp(appProperties);
                    detAusenciaBp.actualizaSaldosVacaciones(detalleAusencia.getRutEmpleado());
                }else if (detalleAusencia.getIdAusencia() == Constantes.ID_AUSENCIA_PERMISO_ADMINISTRATIVO){//PA
                    System.out.println(WEB_NAME+"[servlet."
                        + "DetalleAusenciasServlet.eliminarRegistroAusencia]"
                        + "20250305-01: Seteo de saldo PA después de eliminar un registro PA. "
                        + "Delete ausencia PA."
                        + "Setear nuevo saldo de permiso administrativo (saldo_PA) "
                        + "para empleado. "
                        + "Invocar nueva funcion 'set_saldo_permiso_administrativo'"
                        + ". Empresa_id: " + detalleAusencia.getEmpresaId()
                        + ", rutEmpleado: " + detalleAusencia.getRutEmpleado()
                    );

                    //invocar metodo que llame a la funcion en BD 'set_saldo_permiso_administrativo'
                    PermisosAdministrativosDAO permisosDao = new PermisosAdministrativosDAO(_appProperties);
                    /**
                    *   codempresa
                    *   rutempleado
                    *   anio_calc: Extraerlo de la fecha de inicio del PA
                    *   semestre: usar el mes de la misma fecha de inicio, 
                    *       que se encuentre entre el 1 al 6, y del 7 al 12 para 1er o 2do semestre respectivamente.
                    */
                    SimpleDateFormat FECHA_FORMAT = 
                        new SimpleDateFormat("yyyy-MM-dd", new Locale("es","CL"));
                    String strFechaInicioPA = detalleAusencia.getFechaInicioAsStr();
                    Date fechaInicioPA  = null;
                    int semestrePA      = 0;
                    int anioPA          = 0;
                    try{    
                        fechaInicioPA   = FECHA_FORMAT.parse(strFechaInicioPA);
                        anioPA          = Integer.parseInt(Utilidades.getDatePartAsString(fechaInicioPA, "yyyy"));
                        semestrePA      = Utilidades.getSemestre(fechaInicioPA);
                        ResultCRUDVO resultadoPA = 
                            permisosDao.setSaldoPermisoAdministrativo(detalleAusencia.getEmpresaId(), 
                                detalleAusencia.getRutEmpleado(), 
                                anioPA, 
                                String.valueOf(semestrePA));
                    }catch(ParseException pex){
                        System.err.println(WEB_NAME+"[SDetalleAusenciasServlet.eliminarRegistroAusencia]"
                            + "Error al parsear fecha inicio PA: " + pex.toString());
                        resultadoEliminar.setMensaje("Error al parsear fecha inicio PA: " + pex.toString());
                    }
                }
                resultadoEliminar.setMensaje("Registro eliminado exitosamente");
                resultadoEliminar.setExito(true);
            }else{
                resultadoEliminar.setMensaje("Error al eliminar registro");
            }
        } catch (Exception e) {
            System.err.println("Error al recibir datos para eliminar registro: " + e.toString());
            resultadoEliminar.setMensaje("Error (Exception) al modificar el registro: " + e.getMessage());
            resultadoEliminar.setExito(false);
        }    
        return resultadoEliminar;
    }
    
    
    /**
    * 
    */
    private ResultadoAccionVO modificarRegistroAusencia(HttpSession _session, 
            HttpServletRequest _request, 
            PropertiesVO _appProperties, 
            UsuarioVO _userConnected, 
            DetalleAusenciaModVO _data){
        ResultadoAccionVO resultadoModificar=new ResultadoAccionVO();
        
        MaintenanceEventVO eventoAuditoria = new MaintenanceEventVO();
        eventoAuditoria.setUsername(_userConnected.getUsername());
        eventoAuditoria.setDatetime(new Date());
        eventoAuditoria.setUserIP(_request.getRemoteAddr());
        eventoAuditoria.setType("DAU");
        eventoAuditoria.setEmpresaIdSource(_userConnected.getEmpresaId());
                
        HashMap<String, Double> parametrosSistema = (HashMap<String, Double>)_session.getAttribute("parametros_sistema");
        DetalleAusenciaBp detAusenciaBp = new DetalleAusenciaBp(_appProperties);
        VacacionesBp vacacionesBp = new VacacionesBp(_appProperties);
        DetalleAusenciaVO detalleActual = 
            detAusenciaBp.getDetalleAusenciaByCorrelativo(_data.getCorrelativo());
        
        _data.setEmpresaId(_userConnected.getEmpresaId());
        _data.setNombreAusencia(detalleActual.getNombreAusencia());
        _data.setRutAutorizador(detalleActual.getRutAutorizador());
        System.out.println(WEB_NAME+"[DetalleAusenciasServlet]update - "
            + "Datos existentes:"
            + "rutEmpleado: " + detalleActual.getRutEmpleado()
            + ", correlativo: " + _data.getCorrelativo()
            + ", fechaInicio: " + detalleActual.getFechaInicioAsStr()
            + ", fechaFin: " + detalleActual.getFechaFinAsStr()
            + ", tipoAusencia: " + detalleActual.getTipoAusencia()
            + ", idAusencia: " + detalleActual.getIdAusencia()
            + ", rutAutorizador: " + detalleActual.getRutAutorizador());
        boolean sePuedeModificar = true;
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd", new Locale("es","CL"));
        Calendar cal = Calendar.getInstance(new Locale("es","CL"));
        Date fechaActual = cal.getTime();                     
        try{
            Date dteFecFinAusencia = sdf3.parse(detalleActual.getFechaFinAsStr());
            if (dteFecFinAusencia.before(fechaActual)){
                System.out.println(WEB_NAME+"[DetalleAusenciasServlet]"
                    + "Fecha fin ausencia pasada, no se puede modificar...");
                sePuedeModificar = false;
            }
        }catch(ParseException pex){
            System.err.println("[DetalleAusenciasServlet]"
                + "Error al parsear "
                + "fecha fin ausencia: " + pex.toString());
        }
        if (sePuedeModificar){
                _data.setRutEmpleado(detalleActual.getRutEmpleado());
                ArrayList<DetalleAusenciaVO> ausenciasConflicto
                    =new ArrayList<>();
                if (_data.getPermiteHora()!=null && 
                    _data.getPermiteHora().compareTo("N") == 0){
                    System.out.println(WEB_NAME+"[DetalleAusenciasServlet]"
                        + "Mantenedor - "
                        + "Detalle Ausencias (update) - "
                        + "Ausencia x dias."
                        + " Validar ausencias conflicto.");
                    //validar ausencias conflicto 
                    ausenciasConflicto = 
                        detAusenciaBp.getAusenciasConflicto(_data.getRutEmpleado(),
                            false,
                            _data.getFechaInicioAsStr(),
                            _data.getFechaFinAsStr(),
                            null,
                            null);
                }else if (_data.getPermiteHora().compareTo("S") == 0){
                            System.out.println(WEB_NAME+"[DetalleAusenciasServlet]"
                                + "Mantenedor - "
                                + "Detalle Ausencias (update) - Ausencia x horas."
                                + " Validar ausencias conflicto.");
                            ausenciasConflicto = 
                            detAusenciaBp.getAusenciasConflicto(_data.getRutEmpleado(),
                                true,
                                _data.getFechaInicioAsStr(),
                                _data.getFechaFinAsStr(), 
                                _data.getHoraInicioFullAsStr(), 
                                _data.getHoraFinFullAsStr());
                }
                boolean hayConflicto=false;
                Iterator<DetalleAusenciaVO> itAc = ausenciasConflicto.iterator();
                while(itAc.hasNext()){
                    if (itAc.next().getCorrelativo() != _data.getCorrelativo()){
                        hayConflicto = true;
                        break;
                    }
                }

                if (!hayConflicto){
                    VacacionesVO saldoVacaciones = new VacacionesVO();
                    if (_data.getIdAusencia() == Constantes.ID_AUSENCIA_VACACIONES){//id ausencia VACACIONES
                        //saldo_vacaciones = saldo_actual - ( dias_efectivos vacaciones que se estan modificando ) 
                        System.out.println(WEB_NAME+"[DetalleAusenciasServlet]"
                            + "Actualizar Vacacion existente. "
                            + "Correlativo ausencia: " + detalleActual.getCorrelativo()
                            + ", diasEfectivos actuales: " + detalleActual.getDiasEfectivosVacaciones());
                        saldoVacaciones = 
                            validarVacaciones(_data,
                                detalleActual,
                                _appProperties,
                                _userConnected,
                                _request);
                        _data.setDiasEfectivosVacaciones(saldoVacaciones.getDiasEfectivos());
                    }

                    System.out.println(WEB_NAME+"[DetalleAusenciasServlet]"
                        + "Admin detalle Ausencias - Actualizar "
                        + ". Msg Validacion vacaciones: "  + saldoVacaciones.getMensajeValidacion());
                    if (saldoVacaciones.getMensajeValidacion() == null){
                        System.out.println(WEB_NAME+"[DetalleAusenciasServlet]"
                            + "Mantenedor - Detalle "
                            + "Ausencias - Actualizar "
                            + "detalle ausencia, "
                            + "correlativo: " + _data.getCorrelativo()
                            + ", tipoAusencia: " + _data.getNombreAusencia());

                        _data.setAusenciaAutorizada("S");
                        
                        DetalleAusenciaVO ausenciaModificar= new DetalleAusenciaVO();
                        ausenciaModificar.setCorrelativo(_data.getCorrelativo());
                        ausenciaModificar.setRutEmpleado(_data.getRutEmpleado());
                        ausenciaModificar.setIdAusencia(_data.getIdAusencia());
                        ausenciaModificar.setNombreAusencia(_data.getNombreAusencia());
                        ausenciaModificar.setFechaInicioAsStr(_data.getFechaInicioAsStr());
                        ausenciaModificar.setHoraInicioFullAsStr(_data.getHoraInicioFullAsStr());
                        ausenciaModificar.setFechaFinAsStr(_data.getFechaFinAsStr());
                        ausenciaModificar.setHoraFinFullAsStr(_data.getHoraFinFullAsStr());
                        ausenciaModificar.setRutAutorizador(_data.getRutAutorizador());
                        ausenciaModificar.setAusenciaAutorizada(_data.getAusenciaAutorizada());
                        ausenciaModificar.setPermiteHora(_data.getPermiteHora());
                        
                        ResultCRUDVO doUpdate = detAusenciaBp.update(ausenciaModificar, eventoAuditoria);
                        resultadoModificar.setMensaje("Datos actualizados exitosamente.");
                        resultadoModificar.setExito(true);
                        //listaObjetos.add(auxdata);

                        if (!doUpdate.isThereError() 
                            && _data.getIdAusencia() == Constantes.ID_AUSENCIA_VACACIONES){//VACACIONES
                            //no setea dias efectivos al update
                            System.out.println(WEB_NAME+"[DetalleAusenciasServlet]"
                                + "Update vacaciones."
                                + "ReCalcular saldo dias de vacaciones "
                                + "para empleado. "
                                + "empresa_id: " + _data.getEmpresaId()
                                +", rutEmpleado: " + _data.getRutEmpleado());
                            vacacionesBp.calculaDiasVacaciones(_userConnected.getUsername(), 
                                _data.getEmpresaId(), _data.getRutEmpleado(), 
                                parametrosSistema);
                            System.out.println(WEB_NAME+"[DetalleAusenciasServlet]"
                                + "Actualizar saldos de vacaciones "
                                + "en tabla detalle_ausencia "
                                + "(usar nueva funcion setsaldodiasvacacionesasignadas). "
                                + "Run: "+ _data.getRutEmpleado());
                            detAusenciaBp.actualizaSaldosVacaciones(_data.getRutEmpleado());


                        }

//                        DetalleAusenciaVO ausenciaPostUpdate = 
//                            detAusenciaBp.getDetalleAusenciaByCorrelativo(auxdata.getCorrelativo());

//                        System.out.println(WEB_NAME+"[DetalleAusenciasServlet]"
//                            + "Despues de Actualizar ausencia: "
//                            + "RUN: " + auxdata.getRutEmpleado()
//                            + ", correlativo: " + auxdata.getCorrelativo()
//                            + ", diasAcumuladosVacacionesAsignadas: " + ausenciaPostUpdate.getDiasAcumuladosVacacionesAsignadas()
//                            + ", saldoDiasVacacionesAsignadas: " + ausenciaPostUpdate.getSaldoDiasVacacionesAsignadas());
//                        //Convert Java Object to Json
//                        String json=gson.toJson(ausenciaPostUpdate);					
//                        //Return Json in the format required by jTable plugin
//                        listData="{\"Result\":\"OK\",\"Record\":"+json+"}";
//                        String aux2 = "\""+"Horas de inicio/fin no validas"+"\"";
//                        if (!horasOk) listData = "{\"Result\":\"ERROR\",\"Message\":"+aux2+"}";
//                        System.err.println("-->listData: "+listData);
                    }else{
                        System.out.println(WEB_NAME+"[DetalleAusenciasServlet]"
                            + "Mantenedor.DetalleAusencias - Actualizar "
                            + "Hay conflicto con el saldo de dias de vacaciones");
                        //guardar en log de eventos
                        addLogEventos(_request, detalleActual.getRutEmpleado(), detalleActual.getEmpresaId(), null, -1, 
                            "Modificar ausencia: Hay conflicto con el saldo de dias de vacaciones: " + saldoVacaciones.getMensajeValidacion());
                        //mensajeFinalUpd= "\"" + saldoVacaciones.getMensajeValidacion() + "\"";
                        resultadoModificar.setMensaje(saldoVacaciones.getMensajeValidacion());
                        //listData = "{\"Result\":\"ERROR\",\"Message\":"+mensajeFinalUpd+"}";
                    }    
                }else{
                    System.out.println(WEB_NAME+"[DetalleAusenciasServlet]"
                        + "Mantenedor.DetalleAusencias - Actualizar "
                        + "Hay conflicto con ausencia existentes");
                    //mensajeFinalUpd= "\""+"Hay conflicto con ausencias existentes..."+"\"";
                    resultadoModificar.setMensaje("Hay conflicto con ausencias existentes...");
                    //guardar en log de eventos
                    addLogEventos(_request, detalleActual.getRutEmpleado(), detalleActual.getEmpresaId(), null, -1, 
                        "Modificar ausencia: Hay conflicto con ausencias existentes.");
//                                        mensajeFinal = "Hay conflicto con ausencias existentes...<br>";
//                                        mensajeFinal += Utilidades.getAusenciasConflictoStr(ausenciasConflicto);
                    //listData = "{\"Result\":\"ERROR\",\"Message\":"+mensajeFinalUpd+"}";
                }
        }else{
            //mensajeFinalUpd= "\""+"Esta ausencia no se puede modificar. Ausencia pasada."+"\"";
            resultadoModificar.setMensaje("Esta ausencia no se puede modificar. Ausencia pasada.");
            //guardar en log de eventos
            addLogEventos(_request, detalleActual.getRutEmpleado(), detalleActual.getEmpresaId(), null, -1, 
                "Modificar ausencia: Esta ausencia no se puede modificar (Fecha pasada).");
            //listData = "{\"Result\":\"ERROR\",\"Message\":"+mensajeFinalUpd+"}";
        }
        
        return resultadoModificar;
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
    private VacacionesVO validarVacaciones(DetalleAusenciaModVO _datosAusencia,
            DetalleAusenciaVO _datosAusenciaExistente,
            PropertiesVO _appProperties, 
            UsuarioVO _userConnected, 
            HttpServletRequest _request){
        VacacionesVO saldoVacaciones=new VacacionesVO();
                
        ////String mensaje = null;
        EmpleadosBp empleadoBp  = new EmpleadosBp(_appProperties);
        TurnosBp turnoBp        = new TurnosBp(_appProperties);
        DetalleTurnosBp detalleTurnoBp = new DetalleTurnosBp(_appProperties);
        TurnoRotativoBp turnoRotBp = new TurnoRotativoBp(_appProperties);
        VacacionesBp vacacionesBp = new VacacionesBp(_appProperties);
               
        MaintenanceEventVO resultado=new MaintenanceEventVO();
        resultado.setUsername(_userConnected.getUsername());
        resultado.setDatetime(new Date());
        resultado.setUserIP(_request.getRemoteAddr());
        resultado.setType("DAU");
        resultado.setEmpresaIdSource(_userConnected.getEmpresaId());
        
//        int idTurnoRotativo = turnoBp.getTurnoRotativo(_datosAusencia.getEmpresaId());
//        EmpleadoVO infoEmpleado = 
//            empleadoBp.getEmpleado(_datosAusencia.getEmpresaId(), 
//                _datosAusencia.getRutEmpleado());
        
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
        
        System.out.println(WEB_NAME+"[servlet."
            + "DetalleAusenciasServlet.validarVacaciones]"
            + "EmpresaId: " + _datosAusencia.getEmpresaId()
            + ", rutEmpleado: " + _datosAusencia.getRutEmpleado()
            + ", fecha inicio: " + _datosAusencia.getFechaInicioAsStr()
            + ", fecha fin: " + _datosAusencia.getFechaFinAsStr());

        if (infoVacaciones.isEmpty()){
            System.out.println(WEB_NAME+"[servlet."
                + "DetalleAusenciasServlet.validarVacaciones]"
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
                        
            System.out.println(WEB_NAME+"[servlet."
                + "DetalleAusenciasServlet.validarVacaciones]"
                + "Iterar fechas en el rango. "
                + "Inicio: " + fechaInicioVacaciones
                + ", Fin: " + fechaFinVacaciones);
            saldoVacaciones = infoVacaciones.get(0);
            double saldoDias = saldoVacaciones.getSaldoDias();
            
            double diasEfectivosActuales = 0;
            if (_datosAusenciaExistente != null) diasEfectivosActuales = _datosAusenciaExistente.getDiasEfectivosVacaciones();
            
            diasSolicitados = 
                vacacionesBp.getDiasEfectivos(fechaInicioVacaciones, 
                    fechaFinVacaciones, 
                    saldoVacaciones.getDiasEspeciales(),
                    _datosAusencia.getEmpresaId(),
                    _datosAusencia.getRutEmpleado());
            double nuevoSaldoDias = saldoDias + diasEfectivosActuales;
            
            System.out.println(WEB_NAME+"[servlet."
                + "DetalleAusenciasServlet.validarVacaciones]"
                + "EmpresaId: " + _datosAusencia.getEmpresaId()
                + ", rutEmpleado: " + _datosAusencia.getRutEmpleado()
                + ", dias solicitados: " + diasSolicitados
                + ", dias efectivos vacacion existente (DEF): " + diasEfectivosActuales    
                + ", dias disponibles(a la fecha) (SALDO_DIAS): " + saldoDias
                + ", nuevo saldo dias disponibles(SALDO_DIAS + DEF): " + nuevoSaldoDias);
     
            //2020-03-15. Se deshabilita validacion de dias solicitados vs el saldo actual.
            if (diasSolicitados > nuevoSaldoDias) {
                System.out.println(WEB_NAME+"[servlet."
                    + "DetalleAusenciasServlet.validarVacaciones]"
                    + "EmpresaId: " + _datosAusencia.getEmpresaId()
                    + ", rutEmpleado: " + _datosAusencia.getRutEmpleado()
                    + ", los dias solicitados ("+diasSolicitados+") "
                    + "superan el saldo de dias disponibles: "+nuevoSaldoDias);
                
                saldoVacaciones.setMensajeValidacion("Los dias solicitados "
                    + "("+diasSolicitados+") superan el saldo "
                    + "de dias disponibles: " + nuevoSaldoDias);
                //enviar correo al director?
            }else{
                System.out.println(WEB_NAME+"[servlet."
                    + "DetalleAusenciasServlet.validarVacaciones]"
                    + "EmpresaId: " + _datosAusencia.getEmpresaId()
                    + ", rutEmpleado: " + _datosAusencia.getRutEmpleado()
                    + ", dias solicitados(A): " + diasSolicitados);
     
                double saldoFinal = nuevoSaldoDias - diasSolicitados;
                saldoVacaciones.setSaldoDias(saldoFinal);
                saldoVacaciones.setDiasEfectivos(diasSolicitados);
                //_datosAusencia.getFechaInicioAsStr(), _datosAusencia.getFechaFinAsStr()
                saldoVacaciones.setFechaInicioUltimasVacaciones(fechaInicioVacaciones);
                saldoVacaciones.setFechaFinUltimasVacaciones(fechaFinVacaciones);
                vacacionesBp.updateSaldoYUltimasVacaciones(saldoVacaciones, resultado);
                System.out.println(WEB_NAME+"[servlet."
                    + "DetalleAusenciasServlet.validarVacaciones]"
                    + "EmpresaId: " + _datosAusencia.getEmpresaId()
                    + ", rutEmpleado: " + _datosAusencia.getRutEmpleado()
                    + ", dias solicitados(B): " + saldoVacaciones.getDiasEfectivos());
            }
        }
       
        return saldoVacaciones;
    }
    
     /**
    * Add evento al log
    */
    private void addLogEventos(HttpServletRequest _request, 
            String _rutEmpleado,
            String _empresaId, 
            String _deptoId, 
            int _cencoId,
            String _detalleEvento){
        
        HttpSession session         = _request.getSession(true);
        ServletContext application  = this.getServletContext();
        PropertiesVO appProperties  = (PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected     = (UsuarioVO)session.getAttribute("usuarioObj");
        MaintenanceEventsBp eventosBp   = new MaintenanceEventsBp(appProperties);
        
        MaintenanceEventVO resultado = new MaintenanceEventVO();
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.printClientInfo(_request);
        resultado.setOperatingSystem(clientInfo.getClientOS(_request));
        resultado.setBrowserName(clientInfo.getClientBrowser(_request));
        resultado.setUsername(userConnected.getUsername());
        resultado.setDatetime(new Date());
        resultado.setUserIP(_request.getRemoteAddr());
        resultado.setType("DAU");
        resultado.setEmpresaIdSource(userConnected.getEmpresaId());
        
        //agregar evento al log.
        resultado.setRutEmpleado(_rutEmpleado);
        resultado.setEmpresaId(_empresaId);
        resultado.setDeptoId(_deptoId);
        resultado.setCencoId(_cencoId);
        resultado.setDescription(_detalleEvento);
        
        eventosBp.addEvent(resultado);
    }
    
    public static class DetalleAusenciaModVO {

        private int correlativo;
        private String empresaId;
        private String rutEmpleado;
        private String nombreEmpleado;
        private String nombreCargoEmpleado;
        private int idAusencia;
        private String nombreAusencia;
        private String permiteHora;
        private String horaInicioFullAsStr;
        private String horaFinFullAsStr;
        private String rutAutorizador;
        private String fechaInicioAsStr;
        private String fechaFinAsStr;
        private String ausenciaAutorizada;
        private double diasEfectivosVacaciones;
        
        public DetalleAusenciaModVO() {}
        
        public String getNombreAusencia() {
            return nombreAusencia;
        }

        // Getters
        public void setNombreAusencia(String nombreAusencia) {        
            this.nombreAusencia = nombreAusencia;
        }

        public String getEmpresaId() {
            return empresaId;
        }
        
        public double getDiasEfectivosVacaciones() {
            return diasEfectivosVacaciones;
        }
        
        public int getCorrelativo() {
            return correlativo;
        }
        public String getRutEmpleado() {
            return rutEmpleado;
        }
        public String getNombreEmpleado() {
            return nombreEmpleado;
        }
        public String getNombreCargoEmpleado() {
            return nombreCargoEmpleado;
        }
        public int getIdAusencia() {
            return idAusencia;
        }
        public String getPermiteHora() {
            return permiteHora;
        }
        public String getHoraInicioFullAsStr() {
            return horaInicioFullAsStr;
        }
        public String getHoraFinFullAsStr() {
            return horaFinFullAsStr;
        }
        public String getRutAutorizador() {
            return rutAutorizador;
        }
        public String getFechaInicioAsStr() {
            return fechaInicioAsStr;
        }
        public String getFechaFinAsStr() {
            return fechaFinAsStr;
        }
        public String getAusenciaAutorizada() {
            return ausenciaAutorizada;
        }

        // Setters
        
        public void setEmpresaId(String empresaId) {
            this.empresaId = empresaId;
        }
        public void setCorrelativo(int correlativo) {
            this.correlativo = correlativo;
        }
        public void setRutEmpleado(String rutEmpleado) {
            this.rutEmpleado = rutEmpleado;
        }
        public void setNombreEmpleado(String nombreEmpleado) {
            this.nombreEmpleado = nombreEmpleado;
        }
        public void setNombreCargoEmpleado(String nombreCargoEmpleado) {
            this.nombreCargoEmpleado = nombreCargoEmpleado;
        }
        public void setIdAusencia(int idAusencia) {
            this.idAusencia = idAusencia;
        }
        public void setPermiteHora(String permiteHora) {
            this.permiteHora = permiteHora;
        }
        public void setHoraInicioFullAsStr(String horaInicioFullAsStr) {
            this.horaInicioFullAsStr = horaInicioFullAsStr;
        }
        public void setHoraFinFullAsStr(String horaFinFullAsStr) {
            this.horaFinFullAsStr = horaFinFullAsStr;
        }
        public void setRutAutorizador(String rutAutorizador) {
            this.rutAutorizador = rutAutorizador;
        }
        public void setFechaInicioAsStr(String fechaInicioAsStr) {
            this.fechaInicioAsStr = fechaInicioAsStr;
        }
        public void setFechaFinAsStr(String fechaFinAsStr) {
            this.fechaFinAsStr = fechaFinAsStr;
        }
        public void setAusenciaAutorizada(String ausenciaAutorizada) {
            this.ausenciaAutorizada = ausenciaAutorizada;
        }
        
        public void setDiasEfectivosVacaciones(double diasEfectivosVacaciones) {
            this.diasEfectivosVacaciones = diasEfectivosVacaciones;
        }

        @Override
        public String toString() {
            return "DetalleAusenciaModVO{" + "correlativo=" + correlativo + ", rutEmpleado=" + rutEmpleado + ", nombreEmpleado=" + nombreEmpleado + ", nombreCargoEmpleado=" + nombreCargoEmpleado + ", idAusencia=" + idAusencia + ", permiteHora=" + permiteHora + ", horaInicioFullAsStr=" + horaInicioFullAsStr + ", horaFinFullAsStr=" + horaFinFullAsStr + ", rutAutorizador=" + rutAutorizador + ", fechaInicioAsStr=" + fechaInicioAsStr + ", fechaFinAsStr=" + fechaFinAsStr + ", ausenciaAutorizada=" + ausenciaAutorizada + '}';
        }
        
        
    }
    
    /**
    * 
    */
    public class ResultadoAccionVO {
        private boolean exito=false;
        private String mensaje="";

        public boolean isExito() {
            return exito;
        }

        public void setExito(boolean exito) {
            this.exito = exito;
        }

        public String getMensaje() {
            return mensaje;
        }

        public void setMensaje(String mensaje) {
            this.mensaje = mensaje;
        }
        
        
    }

}
