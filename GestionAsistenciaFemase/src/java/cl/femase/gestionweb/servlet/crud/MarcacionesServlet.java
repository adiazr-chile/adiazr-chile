/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cl.femase.gestionweb.servlet.crud;

import cl.femase.gestionweb.business.CentroCostoBp;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.MaintenanceEventsBp;
import cl.femase.gestionweb.business.MarcasBp;
import cl.femase.gestionweb.business.MarcasEventosBp;
import cl.femase.gestionweb.business.NotificacionBp;
import cl.femase.gestionweb.business.TurnosBp;
import cl.femase.gestionweb.common.ClientInfo;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.Utilidades;
import com.google.gson.Gson;
import cl.femase.gestionweb.dao.DetalleAusenciaDAO;
import cl.femase.gestionweb.dao.EmpleadosDAO;
import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.vo.DispositivoVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MarcaVO;
import cl.femase.gestionweb.vo.MarcasEventosVO;
import cl.femase.gestionweb.vo.NotificacionVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.*;
import java.util.*;

@WebServlet("/api/marcaciones")
public class MarcacionesServlet extends BaseServlet {

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
        //Leer body como String
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
        System.out.println("[MarcacionesServlet.processRequest]params. accion: " + accion);

        MarcasBp auxnegocio             = new MarcasBp(appProperties);
        TurnosBp turnoBp                = new TurnosBp(appProperties);
        EmpleadosBp empleadosBp         = new EmpleadosBp(appProperties);
        MaintenanceEventsBp eventosBp   = new MaintenanceEventsBp(appProperties);
        CentroCostoBp centroCostoBp     = new CentroCostoBp(appProperties);
        
        if ("listar".equalsIgnoreCase(accion)) {
            System.out.println("[MarcacionesServlet.processRequest]params. "
                + "Listar marcaciones del empleado");
            String runEmpleado = jsonObj.has("empleado") ? jsonObj.get("empleado").getAsString() : null;
            String fechaInicio = jsonObj.has("fechaInicio") ? jsonObj.get("fechaInicio").getAsString() : null;
            String fechaFin = jsonObj.has("fechaFin") ? jsonObj.get("fechaFin").getAsString() : null;
            String codigoHash = jsonObj.has("hashcode") ? jsonObj.get("hashcode").getAsString() : null;
            LinkedHashMap<String, MarcaVO> hashMarcas = new LinkedHashMap<String, MarcaVO>();
            
            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(req.getRemoteAddr());
            resultado.setType("MAR");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
            
            System.out.println("[MarcacionesServlet.listar]params. " +
                " EmpresaId: " + userConnected.getEmpresaId() +
                " , runEmpleado: " + runEmpleado +
                " , fechaInicio: " + fechaInicio +
                " , fechaFin: " + fechaFin +
                " , codigo hash: " + codigoHash);

            if ( codigoHash != null && codigoHash.compareTo("") != 0){
                System.out.println(WEB_NAME+"[MarcacionesServlet.listar]"
                    + "Buscar info del empleado a partir del hashcode de la marca.");
                MarcaVO marca       = auxnegocio.getMarcaByHashcode(codigoHash, false);
                //empresaId           = marca.getEmpresaCod();
                runEmpleado    = marca.getRutEmpleado();
                fechaInicio      = marca.getFecha();
                fechaFin        = marca.getFecha();
            }
            
            int idTurnoRotativo = turnoBp.getTurnoRotativo(userConnected.getEmpresaId());
            EmpleadoVO infoEmpleado = empleadosBp.getEmpleado(userConnected.getEmpresaId(), 
                runEmpleado);
                        
            boolean tieneTurnoRotativo=false;
            if (idTurnoRotativo == infoEmpleado.getIdTurno()){
                System.out.println(WEB_NAME+"[MarcacionesServlet.listarMarcas]"
                    + "Empleado.rut: " + infoEmpleado.getRut()
                    + ", nombres: " + infoEmpleado.getNombres()
                    + ", Tiene turno rotativo");
                tieneTurnoRotativo = true;
            }

            hashMarcas = auxnegocio.getHashMarcas(userConnected.getEmpresaId(),
                infoEmpleado.getDeptoId(),
                infoEmpleado.getCencoId(),
                runEmpleado, 
                null, 
                fechaInicio, 
                fechaFin,
                codigoHash,
                tieneTurnoRotativo,
                infoEmpleado.getRegionId(),
                infoEmpleado.getComunaId(),
                -1, 
                -1, 
                "rut_empleado,fecha_hora asc");
            
            List<MarcaVO> lista = new ArrayList<>(hashMarcas.values()); // respeta el orden de inserción
//            System.out.println("[MarcacionesServlet.processRequest]"
//                + "Mostrar marcas antes de seteo en pagina...");
//            for (MarcaVO marca : lista) {
//                System.out.println("[MarcacionesServlet.processRequest]"
//                    + "Marca:");
//                
//                System.out.println(marca.getId() + " - " + marca.getRun());
//            }
            
            //agregar evento al log.
            resultado.setRutEmpleado(runEmpleado);
            resultado.setEmpresaId(userConnected.getEmpresaId());
            resultado.setDeptoId(infoEmpleado.getDeptoId());
            resultado.setCencoId(infoEmpleado.getCencoid());
            resultado.setDescription(WEB_NAME+"[MarcacionesServlet.listarMarcas]"
                + "Consulta marcas. "
                + " Rut empleado: " + runEmpleado    
                + ", desde: " + fechaInicio
                + ", hasta: " + fechaFin
                + ", codigoHash: " + codigoHash);
            eventosBp.addEvent(resultado);
            
            String json = gson.toJson(lista);
            System.out.println("JSON Marcaciones=> " + json);
            resp.getWriter().write(json);
        } else if ("crear".equalsIgnoreCase(accion)) {
            System.out.println("[MarcacionesServlet.processRequest] Crear registro de marcacion...");
            boolean exito = false;
            String mensaje = "";

            try {
                // Usar el objeto VO para mapear todos los datos recibidos del front
                System.out.println("[MarcacionesServlet.processRequest]Datos recibidos: " + jsonStr);
                
                MarcaCreadaVO data = gson.fromJson(jsonStr, MarcaCreadaVO.class);
                System.out.println("[MarcacionesServlet.processRequest]"
                    + "Objeto recibido: " + data.toString());
                
                EmpleadoVO infoEmpleado = empleadosBp.getEmpleado(userConnected.getEmpresaId(), 
                    data.getRutEmpleado());
                System.out.println("[MarcacionesServlet."
                    + "processRequest]"
                    + "Rut empleado: " + data.getRutEmpleado()
                    + ", centro costo ID: " + infoEmpleado.getCencoId());
                List<DispositivoVO> dispositivos = 
                    centroCostoBp.getDispositivosAsignados(infoEmpleado.getCencoId());
                
                data.setEmpresaId(userConnected.getEmpresaId());
                data.setDispositivoId(dispositivos.get(0).getId());
                
                System.out.println("[MarcacionesServlet."
                    + "processRequest]Datos recibidos desde frontend:" + data.toString());
                ResultadoAccionVO resultObj = 
                    crearMarcacion(session, req, appProperties, userConnected, data, auxnegocio, infoEmpleado);
                mensaje = resultObj.getMensaje();
                exito = resultObj.isExito();
                System.out.println("[MarcacionesServlet.processRequest]Marcacion creada. "
                    + "Retornar, exito?" + exito 
                    +", mensaje: "+ mensaje);
                
            } catch (Exception e) {
                mensaje = "Error (Exception) al crear el registro: " + e.getMessage();
                exito = false;
                System.err.println("[MarcacionesServlet."
                    + "processRequest]"
                    + "Error al recibir datos para crear registro: " + e.toString());
                e.printStackTrace();
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
        }else if ("modificar".equalsIgnoreCase(accion)) {
            System.out.println("[MarcacionesServlet.processRequest] Modificar registro");
            boolean exito = false;
            String mensaje = "";

            try {
                // Usar el objeto VO para mapear todos los datos recibidos del front
                MarcaModificadaVO data = gson.fromJson(jsonStr, MarcaModificadaVO.class);
                data.setEmpresaId(userConnected.getEmpresaId());
                System.out.println("[MarcacionesServlet."
                    + "processRequest]Datos recibidos desde frontend:" + data.toString());
                ResultadoAccionVO resultObj = 
                    modificarMarcacion(session, req, appProperties, userConnected, data, auxnegocio);
                mensaje = resultObj.getMensaje();
                exito = resultObj.isExito();
                System.out.println("[MarcacionesServlet.processRequest]Marcacion modificada. "
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

        }else if ("eliminar".equalsIgnoreCase(accion)) {
            boolean exito = false;
            String mensaje = "";
            try {
                // Usar el objeto VO para mapear todos los datos recibidos del front
                MarcaEliminadaVO data = gson.fromJson(jsonStr, MarcaEliminadaVO.class); 
                System.out.println("[MarcacionesServlet.processRequest]Eliminar marcacion. "
                    + "Datos recibidos:" + data.toString());
                ResultadoAccionVO resultObj = eliminarMarcacion(session, req, appProperties, userConnected, data, auxnegocio);
                mensaje = resultObj.getMensaje();
                exito = resultObj.isExito();
                
                System.out.println("[MarcacionesServlet.processRequest]Ausencia eliminada. "
                    + "Retornar, exito?" + exito 
                    +", mensaje: "+ mensaje);
                
            } catch (Exception e) {
                mensaje = "Error (Exception) al eliminar el registro: " + e.getMessage();
                exito = false;
                System.err.println("Error al recibir datos para eliminar registro: " + e.toString());
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
    private ResultadoAccionVO eliminarMarcacion(HttpSession _session, 
            HttpServletRequest _request, 
            PropertiesVO _appProperties, 
            UsuarioVO _userConnected,
            MarcaEliminadaVO _data,
            MarcasBp _auxnegocio){
        
        ResultadoAccionVO resultadoEliminar = new ResultadoAccionVO();
        MaintenanceEventVO eventoAuditoria = new MaintenanceEventVO();
        eventoAuditoria.setUsername(_userConnected.getUsername());
        eventoAuditoria.setDatetime(new Date());
        eventoAuditoria.setUserIP(_request.getRemoteAddr());
        eventoAuditoria.setType("MAR");
        eventoAuditoria.setEmpresaIdSource(_userConnected.getEmpresaId());
        
        System.out.println(WEB_NAME+"[MarcacionesServlet.eliminarMarcacion]"
            + "Eliminar marcacion");
        
        System.out.println(WEB_NAME+"[MarcasController]}"
            + "Eliminar marca correlativo: " + _data.getId());
        MarcaVO marcaToDelete = new MarcaVO();    
        String newfechaHora = _data.getFecha() + " " + _data.getHora();
        marcaToDelete.setEmpresaCod(_data.getEmpresaId());
        marcaToDelete.setRutEmpleado(_data.getRutEmpleado());
        marcaToDelete.setFechaHora(_data.getFecha() +" " + _data.getHora());
        marcaToDelete.setTipoMarca(_data.getEvento());
        marcaToDelete.setHashcode(_data.getHashcode());
        marcaToDelete.setCorrelativo(_data.getId());

        System.out.println(WEB_NAME+"[MarcacionesServlet.eliminarMarcacion]"
            + "Eliminar marca,"
            + " rut: " + marcaToDelete.getRutEmpleado()
            + ", fechaHora: " + marcaToDelete.getFechaHora()
            + ", tipoMarca: " + marcaToDelete.getTipoMarca()
            + ", hashCode: " + marcaToDelete.getHashcode()
            + ", correlativo: " + marcaToDelete.getCorrelativo()    
        );
        try{
            eventoAuditoria.setEmpresaId(marcaToDelete.getEmpresaCod());
            eventoAuditoria.setRutEmpleado(marcaToDelete.getRutEmpleado());
            if (marcaToDelete.getRutEmpleado()!=null && marcaToDelete.getRutEmpleado().compareTo("")!=0){
                EmpleadosBp empleadosbp=new EmpleadosBp(_appProperties);
                EmpleadoVO infoEmpleado = empleadosbp.getEmpleado(marcaToDelete.getEmpresaCod(), marcaToDelete.getRutEmpleado());
                eventoAuditoria.setEmpresaId(infoEmpleado.getEmpresa().getId());
                eventoAuditoria.setDeptoId(infoEmpleado.getDepartamento().getId());
                eventoAuditoria.setCencoId(infoEmpleado.getCentroCosto().getId());
            }
            //buscar marca antes de ser eliminada
//            MarcaVO marcaOriginal = _auxnegocio.getMarcaByKey(_data.getEmpresaId(), 
//                _data.getRutEmpleado(),
//                _data.getFechaHoraKey(), 
//                _data.getEvento(), false);
//            
            MarcaVO marcaOriginal = _auxnegocio.getMarcaByCorrelativo(_data.getId(), true);
            
            ResultCRUDVO doDelete = _auxnegocio.delete(marcaToDelete, eventoAuditoria);

            marcaToDelete.setRowKey(marcaToDelete.getEmpresaCod()
                + "|" + marcaToDelete.getRutEmpleado()
                + "|" + marcaToDelete.getFechaHora()
                + "|" + marcaToDelete.getTipoMarca()
                + "|" + marcaToDelete.getHashcode()
                + "|" + marcaToDelete.getCorrelativo());
            //hashMarcas.put(infomarca.getRowKey(), infomarca);

            //guardar evento en tabla marcas_eventos
            if (!doDelete.isThereError()){
                resultadoEliminar.setMensaje("Marcacion eliminada exitosamente.");
                resultadoEliminar.setExito(true);
                
                MarcasEventosBp marcamodif= new MarcasEventosBp();
                MarcasEventosVO aux = new MarcasEventosVO();

                System.out.println(WEB_NAME+"[MarcasController]inserta "
                    + "marca evento: ELIMINACION.");
                aux.setCorrelativo(marcaToDelete.getCorrelativo());
                aux.setCodDispositivo(marcaOriginal.getCodDispositivo());
                aux.setCodUsuario(_userConnected.getUsername());
                aux.setEmpresaCod(marcaOriginal.getEmpresaCod());
                aux.setRutEmpleado(marcaOriginal.getRutEmpleado());
                aux.setId(marcaOriginal.getId());
                aux.setHashcode(marcaOriginal.getHashcode());

                aux.setFechaHoraOriginal(marcaOriginal.getFechaHora());
                aux.setTipoMarcaOriginal(marcaOriginal.getTipoMarca());
                aux.setComentarioOriginal(marcaOriginal.getComentario());

                aux.setFechaHoraNew(marcaOriginal.getFechaHora());
                aux.setTipoMarcaNew(marcaOriginal.getTipoMarca());
                aux.setComentarioNew("");
                aux.setTipoEvento(Constantes.MARCA_ELIMINADA);
                marcamodif.insert(aux, eventoAuditoria);
            }
        } catch (Exception e) {
            System.err.println("Error al recibir datos para eliminar registro: " + e.toString());
            resultadoEliminar.setMensaje("Error (Exception) al eliminar marcacion: " + e.getMessage());
            resultadoEliminar.setExito(false);
        }
        
        
//        try{
//            
//        } catch (Exception e) {
//            System.err.println("Error al recibir datos para eliminar registro: " + e.toString());
//            resultadoEliminar.setMensaje("Error (Exception) al modificar el registro: " + e.getMessage());
//            resultadoEliminar.setExito(false);
//        }    
        return resultadoEliminar;
    }
    
    
    /**
    * 
    */
    private ResultadoAccionVO modificarMarcacion(HttpSession _session, 
            HttpServletRequest _request, 
            PropertiesVO _appProperties, 
            UsuarioVO _userConnected, 
            MarcaModificadaVO _data,
            MarcasBp _auxnegocio){
        ResultadoAccionVO resultadoModificar=new ResultadoAccionVO();
        
        MaintenanceEventVO eventoAuditoria = new MaintenanceEventVO();
        eventoAuditoria.setUsername(_userConnected.getUsername());
        eventoAuditoria.setDatetime(new Date());
        eventoAuditoria.setUserIP(_request.getRemoteAddr());
        eventoAuditoria.setType("MAR");
        eventoAuditoria.setEmpresaIdSource(_userConnected.getEmpresaId());
                
//        HashMap<String, Double> parametrosSistema = (HashMap<String, Double>)_session.getAttribute("parametros_sistema");
        _data.setEmpresaId(_userConnected.getEmpresaId());
        MarcaVO marcaAModificar = new MarcaVO();
        
        //buscar marca original existente
        MarcaVO marcaOriginal = _auxnegocio.getMarcaByKey(_data.getEmpresaId(), 
            _data.getRutEmpleado(),
            _data.getFechaHoraKey(), 
            _data.getTipoMarcaOriginal(), false);
        if (marcaOriginal != null){
            //enviar correo
            System.out.println(WEB_NAME+"[MarcacionesServlet.modificarMarcacion]"
                + "update marca."
                + "Datos de la marcacion Original. "
                + "Empresa: " + marcaOriginal.getEmpresaCod()
                + ", rut: " + marcaOriginal.getRutEmpleado()
                + ", tipoMarca: " + marcaOriginal.getTipoMarca()
                + ", fechaHora: " + marcaOriginal.getFechaHora()
                + ", comentario: " + marcaOriginal.getComentario()    
            );
            String newfechaHora = _data.getFecha() + " " + _data.getHora();
            marcaAModificar.setRutEmpleado(_data.getRutEmpleado());
            marcaAModificar.setEmpresaCod(_data.getEmpresaId());
            marcaAModificar.setEmpresaKey(_data.getEmpresaId());
            marcaAModificar.setFechaHora(newfechaHora);
            marcaAModificar.setTipoMarca(_data.getEvento());
            
            System.out.println(WEB_NAME+"[MarcacionesServlet.modificarMarcacion]"
                + "update marca."
                + "Marca a modificar: "
                + "Empresa: " + marcaAModificar.getEmpresaCod()
                + ", rut: " + marcaAModificar.getRutEmpleado()
                + ", tipoMarca: " + marcaAModificar.getTipoMarca()
                + ", fechaHora: " + marcaAModificar.getFechaHora()
                + ", data.fechaHora: " + _data.getFecha() + " " + _data.getHora()    
                + ", comentario: " + marcaAModificar.getComentario()    
            );

            /**
            *  Empl_rut + Fecha + Hora + Evento
            */
            String keyPhrase = marcaAModificar.getRutEmpleado()+marcaAModificar.getFechaHora()+marcaAModificar.getTipoMarca();
            marcaAModificar.setHashcode(Utilidades.getMD5EncryptedValue(keyPhrase));
            
            /**
            * 20200304-001: Admin marcas, 
            * Cuando se modifique el tipo de evento, 
            * se debe guardar texto en campo 'comentario': 
            *    'CAMBIO DE ENTRADA POR SALIDA' o viceversa.
            */
            if (marcaOriginal.getTipoMarca() == Constantes.MARCA_ENTRADA){
                //si la marca original era de ENTRADA y se modifica a SALIDA
                if (marcaAModificar.getTipoMarca() == Constantes.MARCA_SALIDA){
                    marcaAModificar.setComentario(Constantes.CAMBIO_MARCA_ENTRADA_POR_SALIDA);
                }
            }else if (marcaOriginal.getTipoMarca() == Constantes.MARCA_SALIDA){
                //si la marca original era de SALIDA y se modifica a ENTRADA
                if (marcaAModificar.getTipoMarca() == Constantes.MARCA_ENTRADA){
                    marcaAModificar.setComentario(Constantes.CAMBIO_MARCA_SALIDA_POR_ENTRADA);
                }
            }
            /**
            * 20200304-002: Admin marcas, Cuando se modifique la hora de una marca, 
            * se debe guardar texto en campo 'comentario': 'HORA MODIFICADA'.
            * 
            */
            //fechaHora: 2020-02-10 08:30:00
            //fechaHora: 2020-02-10 08:30:00
            StringTokenizer tokenfechaHora=new StringTokenizer(marcaAModificar.getFechaHora(), " ");
            String soloFechaNew = tokenfechaHora.nextToken();
            String soloHoraNew = tokenfechaHora.nextToken();
            if (marcaOriginal.getSoloHora().compareTo(soloHoraNew) != 0){
                //La hora de la marca original es distinta a la hora modificada
                marcaAModificar.setComentario(Constantes.CAMBIO_HORA_MARCA);
            }

            /**
            * 20200304-003: Admin marcas, Cuando se modifique la fecha de una marca, 
            * se debe guardar texto en campo 'comentario': 'FECHA MODIFICADA'.
            * 
            */
            if (marcaOriginal.getFecha().compareTo(soloFechaNew) != 0){
                //La fecha de la marca original es distinta a la fecha modificada
                marcaAModificar.setComentario(Constantes.CAMBIO_FECHA_MARCA);
            }

            //actualiza la marca
            ResultCRUDVO doUpdate = _auxnegocio.update(marcaAModificar, marcaOriginal, eventoAuditoria);
            
            if (!doUpdate.isThereError()){
                resultadoModificar.setMensaje("Marcacion modificada exitosamente.");
                resultadoModificar.setExito(true);
            
                //enviar mail con info de marca original y marca modificada...
                informaModificacionMarca("REGISTRO DE ASISTENCIA MODIFICADO", 
                    marcaOriginal, marcaAModificar, _userConnected, _request);

                //guardar evento en nueva tabla
                MarcasEventosBp marcamodif= new MarcasEventosBp();
                MarcasEventosVO aux = new MarcasEventosVO();

                System.out.println(WEB_NAME+"[MarcasController]inserta "
                    + "marca evento: modificada.");

                aux.setCodDispositivo(marcaOriginal.getCodDispositivo());
                aux.setCodUsuario(_userConnected.getUsername());
                aux.setEmpresaCod(marcaOriginal.getEmpresaCod());
                aux.setRutEmpleado(marcaOriginal.getRutEmpleado());
                aux.setId(marcaOriginal.getId());
                aux.setHashcode(marcaOriginal.getHashcode());

                aux.setFechaHoraOriginal(marcaOriginal.getFechaHora());
                aux.setTipoMarcaOriginal(marcaOriginal.getTipoMarca());
                aux.setComentarioOriginal(marcaOriginal.getComentario());

                aux.setFechaHoraNew(marcaAModificar.getFechaHora());
                aux.setTipoMarcaNew(marcaAModificar.getTipoMarca());
                aux.setComentarioNew(marcaAModificar.getComentario());
                aux.setTipoEvento(Constantes.MARCA_MODIFICADA);
                marcamodif.insert(aux, eventoAuditoria);
            }else{
                resultadoModificar.setMensaje("Error al modificar marcacion.");
                resultadoModificar.setExito(false);
            }
        }
        
        return resultadoModificar;
    }
    
    /**
    * 
    */
    private ResultadoAccionVO crearMarcacion(HttpSession _session, 
            HttpServletRequest _request, 
            PropertiesVO _appProperties, 
            UsuarioVO _userConnected, 
            MarcaCreadaVO _data,
            MarcasBp _auxnegocio,
            EmpleadoVO _infoEmpleado){
        ResultadoAccionVO resultadoCrear = new ResultadoAccionVO();
        
        MaintenanceEventVO eventoAuditoria = new MaintenanceEventVO();
        eventoAuditoria.setUsername(_userConnected.getUsername());
        eventoAuditoria.setDatetime(new Date());
        eventoAuditoria.setUserIP(_request.getRemoteAddr());
        eventoAuditoria.setType("MAR");
        eventoAuditoria.setEmpresaIdSource(_userConnected.getEmpresaId());
                
        _data.setEmpresaId(_userConnected.getEmpresaId());
        //MarcaVO marcaACrear = new MarcaVO();
        
        
        
        MarcaVO nuevaMarca = new MarcaVO();
        
        nuevaMarca.setEmpresaCod(_data.getEmpresaId());
        nuevaMarca.setRutEmpleado(_data.getRutEmpleado());
        nuevaMarca.setCodInternoEmpleado(_infoEmpleado.getCodInterno());
        nuevaMarca.setFecha(_data.getFecha());
        nuevaMarca.setHora(_data.getHora());
        nuevaMarca.setTipoMarca(Integer.parseInt(_data.getEvento()));
        nuevaMarca.setCodDispositivo(_data.getDispositivoId());
        nuevaMarca.setId("FEMASE_ID");
        nuevaMarca.setHashcode(_data.getHashcode());
        nuevaMarca.setComentario("Creada desde Admin Marcas");
        nuevaMarca.setCodTipoMarcaManual(Integer.parseInt(_data.getTipoMarcaManual()));
        
        /**
        *  Empl_rut + Fecha + Hora + Evento
        */ 
        String keyPhrase = nuevaMarca.getRutEmpleado() 
            + nuevaMarca.getFecha() 
            + nuevaMarca.getHora() 
            + nuevaMarca.getTipoMarca();
        nuevaMarca.setHashcode(Utilidades.getMD5EncryptedValue(keyPhrase));
        
        System.out.println(WEB_NAME+"[MarcacionesServlet.crearMarcacion]Insertar marca. "
            + "Empresa: " + nuevaMarca.getEmpresaCod()
            + ", rut: " + _data.getRutEmpleado()
            + ", codInterno: " + _infoEmpleado.getCodInterno()    
            + ", fechaHora: " + _data.getFecha() + " " + _data.getHora()
            + ", CodDispositivo: " + _data.getDispositivoId()
            + ", TipoMarca: " + _data.getEvento()
            + ", hashcode(md5): " + nuevaMarca.getHashcode());
        
        ResultCRUDVO doCreate = _auxnegocio.insertWithLog(nuevaMarca, eventoAuditoria);
        System.out.println(WEB_NAME + "[MarcacionesServlet.crearMarcacion]Mensaje "
            + "del sp new_inserta_marca_manual: " + doCreate.getMsgFromSp());
        resultadoCrear.setMensaje(doCreate.getMsgFromSp());
        if (!doCreate.isThereError()){
            System.out.println(WEB_NAME + "[MarcacionesServlet.crearMarcacion]"
                + "Informar creacion de la marca");
            informaCreacionMarca(doCreate.getMsgFromSp(), nuevaMarca, 
                _userConnected, _request);
            resultadoCrear.setExito(true);
            resultadoCrear.setMensaje("Marca creada exitoxamente.");
        }else{
            resultadoCrear.setExito(false);
        }
        return resultadoCrear;
    }
    
    /**
    * 
    */
    private void informaCreacionMarca(String _evento,
            MarcaVO _marcaCreada,
            UsuarioVO _userConnected, 
            HttpServletRequest _request){
    
        MarcasBp marcasBp = new MarcasBp();
        HashMap<Integer, String> tiposMarcas = marcasBp.getTiposMarca();
        
        EmpleadosBp empleadobp = new EmpleadosBp();
        EmpleadoVO empleado = 
            empleadobp.getEmpleado(_marcaCreada.getEmpresaCod(), 
            _marcaCreada.getRutEmpleado());
        String fromLabel = "Gestion asistencia";
        String fromMail = m_properties.getKeyValue("mailFrom");
        String asuntoMail   = "Sistema de Gestion-Registro de asistencia";
        String mailTo = empleado.getEmail();
        
        String mailBody = "Evento:" + _evento
            +"<br>RUN: " + empleado.getCodInterno()
            +"<br>Nombre: " + empleado.getNombreCompleto()
            +"<br>Institucion: " + empleado.getEmpresaNombre()
            +"<br>RUT: " + empleado.getEmpresaRut()
            +"<br>Ubicacion: " + empleado.getEmpresaDireccion()
            +"<br>Centro de costo: " + empleado.getCencoNombre()
            +"<br>Cod dispositivo: " + _marcaCreada.getCodDispositivo()    
            +"<br>Tipo Marca: " + tiposMarcas.get(_marcaCreada.getTipoMarca())+" manual"
            +"<br>Fecha/Hora Marca: " + _marcaCreada.getFechaHora()
            +"<br>Codigo Hash: " + _marcaCreada.getHashcode()
            +"<br>Usuario que crea el registro: " + _userConnected.getUsername()
            +"<br>---"
            +"<br>Sistema excepcional de jornada: NO APLICA"
            +"<br>Resolución Exenta: NO APLICA"
            +"<br>Geolocalización: NO APLICA"
            +"<br>Empresa Transitoria o Subcontratada: NO APLICA"
            +"<br>Nombre: NO APLICA"
            +"<br>RUT: NO APLICA";
             
        MaintenanceEventVO resultado=new MaintenanceEventVO();
        resultado.setUsername(_userConnected.getUsername());
        resultado.setDatetime(new Date());
        resultado.setUserIP(_request.getRemoteAddr());
        resultado.setType("NOT");
        resultado.setEmpresaIdSource(_userConnected.getEmpresaId());
        resultado.setEmpresaId(empleado.getEmpresaId());
        resultado.setDeptoId(empleado.getDeptoId());
        resultado.setCencoId(empleado.getCencoId());
        resultado.setRutEmpleado(empleado.getRut());
        
        System.out.println(WEB_NAME+"[MarcacionesServlet.informaCreacionMarca]"
            + "EmpresaId: " + resultado.getEmpresaId()
            + ", deptoId: " + resultado.getDeptoId()
            + ", cencoId: " + resultado.getCencoId());
        
        NotificacionBp notificacionBp=new NotificacionBp(null);
        NotificacionVO notificacion = new NotificacionVO();
        notificacion.setEmpresaId(empleado.getEmpresaId());
        notificacion.setCencoId(empleado.getCencoId());
        notificacion.setRutEmpleado(empleado.getRut());
        notificacion.setMailFrom(fromMail);
        notificacion.setMailTo(mailTo);
        notificacion.setMailSubject(asuntoMail);
        notificacion.setMailBody(mailBody);
        notificacion.setUsername(_userConnected.getUsername());
        notificacion.setComentario(_marcaCreada.getComentario());
        notificacionBp.insert(notificacion, resultado);
        
    }
    
    /**
    * 
    */
    private void informaModificacionMarca(String _evento,
            MarcaVO _marcaOriginal,MarcaVO _marcaModificada,
            UsuarioVO _userConnected,HttpServletRequest _request){
    
        MarcasBp marcasBp = new MarcasBp();
        HashMap<Integer, String> tiposMarcas = marcasBp.getTiposMarca();
        
        EmpleadosBp empleadobp = new EmpleadosBp();
        EmpleadoVO empleado = 
            empleadobp.getEmpleado(_marcaOriginal.getEmpresaCod(), 
            _marcaOriginal.getRutEmpleado());
        String fromLabel = "Gestion asistencia";
        String fromMail = m_properties.getKeyValue("mailFrom");
        String asuntoMail   = "Sistema de Gestion-Modificacion Registro de asistencia";
        String mailTo = empleado.getEmail();
        
        String mailBody = "Evento:" + _evento
            +"<br>RUN: " + empleado.getCodInterno()
            +"<br>Nombre: " + empleado.getNombreCompleto()
            +"<br>Institucion: " + empleado.getEmpresaNombre()
            +"<br>RUT: " + empleado.getEmpresaRut()
            +"<br>Ubicacion: " + empleado.getEmpresaDireccion()
            +"<br>Centro de costo: " + empleado.getCencoNombre()
            +"<br>Cod dispositivo: " + _marcaOriginal.getCodDispositivo()
            +"<br>Tipo Marca (original): " + tiposMarcas.get(_marcaOriginal.getTipoMarca())
            +"<br>Tipo Marca (modificada): " + tiposMarcas.get(_marcaModificada.getTipoMarca())    
            +"<br>Fecha/Hora Marca (original): " + _marcaOriginal.getFechaHora()
            +"<br>Fecha/Hora Marca (modificada): " + _marcaModificada.getFechaHora()
            +"<br>Codigo Hash (original): " + _marcaOriginal.getHashcode()
            +"<br>Codigo Hash (modificado): " + _marcaModificada.getHashcode()    
            +"<br>Usuario que modifica el registro: " + _userConnected.getUsername()
            +"<br>---"
            +"<br>Sistema excepcional de jornada: NO APLICA"
            +"<br>Resolución Exenta: NO APLICA"
            +"<br>Geolocalización: NO APLICA"
            +"<br>Empresa Transitoria o Subcontratada: NO APLICA"
            +"<br>Nombre: NO APLICA"
            +"<br>RUT: NO APLICA";
        MaintenanceEventVO resultado=new MaintenanceEventVO();
        resultado.setUsername(_userConnected.getUsername());
        resultado.setDatetime(new Date());
        resultado.setUserIP(_request.getRemoteAddr());
        resultado.setType("NOT");
        resultado.setEmpresaIdSource(_userConnected.getEmpresaId());
        resultado.setEmpresaId(empleado.getEmpresaId());
        resultado.setDeptoId(empleado.getDeptoId());
        resultado.setCencoId(empleado.getCencoId());
        resultado.setRutEmpleado(empleado.getRut());
            
        NotificacionBp notificacionBp=new NotificacionBp(null);
        NotificacionVO notificacion = new NotificacionVO();
        notificacion.setEmpresaId(empleado.getEmpresaId());
        notificacion.setCencoId(empleado.getCencoId());
        notificacion.setRutEmpleado(empleado.getRut());
        notificacion.setMailFrom(fromMail);
        notificacion.setMailTo(mailTo);
        notificacion.setMailSubject(asuntoMail);
        notificacion.setMailBody(mailBody);
        notificacion.setUsername(_userConnected.getUsername());
        notificacion.setComentario(_marcaModificada.getComentario());
        
        notificacionBp.insert(notificacion, resultado);
        
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
    
    public static class MarcaModificadaVO {

        public MarcaModificadaVO() {}
        
        private String accion;
        private int   id;
        private String hashcode;
        private String empresaId;
        private String fechaHoraKey;
        private String rutEmpleado;
        private String nombreEmpleado;
        private String fecha;
        private String hora;
        private int evento;
        private String comentario;
        private int tipoMarcaOriginal;

        public int getTipoMarcaOriginal() {
            return tipoMarcaOriginal;
        }

        public void setTipoMarcaOriginal(int tipoMarcaOriginal) {
            this.tipoMarcaOriginal = tipoMarcaOriginal;
        }
        
        public String getHashcode() {
            return hashcode;
        }

        public void setHashcode(String hashcode) {
            this.hashcode = hashcode;
        }

        public String getFechaHoraKey() {
            return fechaHoraKey;
        }

        public void setFechaHoraKey(String fechaHoraKey) {
            this.fechaHoraKey = fechaHoraKey;
        }
        
        public String getEmpresaId() {
            return empresaId;
        }

        public void setEmpresaId(String empresaId) {
            this.empresaId = empresaId;
        }

        public String getAccion() {
            return accion;
        }

        public void setAccion(String accion) {
            this.accion = accion;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getRutEmpleado() {
            return rutEmpleado;
        }

        public void setRutEmpleado(String rutEmpleado) {
            this.rutEmpleado = rutEmpleado;
        }

        public String getNombreEmpleado() {
            return nombreEmpleado;
        }

        public void setNombreEmpleado(String nombreEmpleado) {
            this.nombreEmpleado = nombreEmpleado;
        }

        public String getFecha() {
            return fecha;
        }

        public void setFecha(String fecha) {
            this.fecha = fecha;
        }

        public String getHora() {
            return hora;
        }

        public void setHora(String hora) {
            this.hora = hora;
        }

        public int getEvento() {
            return evento;
        }

        public void setEvento(int evento) {
            this.evento = evento;
        }

        public String getComentario() {
            return comentario;
        }

        public void setComentario(String comentario) {
            this.comentario = comentario;
        }

        @Override
        public String toString() {
            return "MarcaModificadaVO{" + "accion=" + accion + ", id=" + id + ", hashcode=" + hashcode + ", empresaId=" + empresaId + ", fechaHoraKey=" + fechaHoraKey + ", rutEmpleado=" + rutEmpleado + ", nombreEmpleado=" + nombreEmpleado + ", fecha=" + fecha + ", hora=" + hora + ", evento=" + evento + ", comentario=" + comentario + '}';
        }

    }
    
    public static class MarcaEliminadaVO {

        public MarcaEliminadaVO() {}
        
        private String accion;
        private int   id;
        private String hashcode;
        private String empresaId;
        private String fechaHoraKey;
        private String rutEmpleado;
        private String nombreEmpleado;
        private String fecha;
        private String hora;
        private int evento;
        private String comentario;

        public String getHashcode() {
            return hashcode;
        }

        public void setHashcode(String hashcode) {
            this.hashcode = hashcode;
        }

        public String getFechaHoraKey() {
            return fechaHoraKey;
        }

        public void setFechaHoraKey(String fechaHoraKey) {
            this.fechaHoraKey = fechaHoraKey;
        }
        
        public String getEmpresaId() {
            return empresaId;
        }

        public void setEmpresaId(String empresaId) {
            this.empresaId = empresaId;
        }

        public String getAccion() {
            return accion;
        }

        public void setAccion(String accion) {
            this.accion = accion;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getRutEmpleado() {
            return rutEmpleado;
        }

        public void setRutEmpleado(String rutEmpleado) {
            this.rutEmpleado = rutEmpleado;
        }

        public String getNombreEmpleado() {
            return nombreEmpleado;
        }

        public void setNombreEmpleado(String nombreEmpleado) {
            this.nombreEmpleado = nombreEmpleado;
        }

        public String getFecha() {
            return fecha;
        }

        public void setFecha(String fecha) {
            this.fecha = fecha;
        }

        public String getHora() {
            return hora;
        }

        public void setHora(String hora) {
            this.hora = hora;
        }

        public int getEvento() {
            return evento;
        }

        public void setEvento(int evento) {
            this.evento = evento;
        }

        public String getComentario() {
            return comentario;
        }

        public void setComentario(String comentario) {
            this.comentario = comentario;
        }

        @Override
        public String toString() {
            return "EditRequestVO{" + "accion=" + accion + ", id=" + id + ", rutEmpleado=" + rutEmpleado + ", nombreEmpleado=" + nombreEmpleado + ", fecha=" + fecha + ", hora=" + hora + ", evento=" + evento + ", comentario=" + comentario + '}';
        }
        
        
    }
    
    public static class MarcaCreadaVO {
        private String accion;
        private String empresaId;
        private String rutEmpleado;
        private String fecha;
        private String hora;
        private String evento;
        private String tipoMarcaManual;
        private String comentario;
        private String nombreEmpleado;
        private String hashcode;
        private String dispositivoId; // si luego lo usas

        // Constructor sin argumentos (OBLIGATORIO PARA GSON)
        public MarcaCreadaVO() {
        }

        public String getEmpresaId() {
            return empresaId;
        }

        public void setEmpresaId(String empresaId) {
            this.empresaId = empresaId;
        }

        public String getAccion() { return accion; }
        public void setAccion(String accion) { this.accion = accion; }

        public String getRutEmpleado() { return rutEmpleado; }
        public void setRutEmpleado(String rutEmpleado) { this.rutEmpleado = rutEmpleado; }

        public String getFecha() { return fecha; }
        public void setFecha(String fecha) { this.fecha = fecha; }

        public String getHora() { return hora; }
        public void setHora(String hora) { this.hora = hora; }

        public String getEvento() { return evento; }
        public void setEvento(String evento) { this.evento = evento; }

        public String getTipoMarcaManual() { return tipoMarcaManual; }
        public void setTipoMarcaManual(String tipoMarcaManual) { this.tipoMarcaManual = tipoMarcaManual; }

        public String getComentario() { return comentario; }
        public void setComentario(String comentario) { this.comentario = comentario; }

        public String getNombreEmpleado() { return nombreEmpleado; }
        public void setNombreEmpleado(String nombreEmpleado) { this.nombreEmpleado = nombreEmpleado; }

        public String getHashcode() { return hashcode; }
        public void setHashcode(String hashcode) { this.hashcode = hashcode; }

        public String getDispositivoId() { return dispositivoId; }
        public void setDispositivoId(String dispositivoId) { this.dispositivoId = dispositivoId; }
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
