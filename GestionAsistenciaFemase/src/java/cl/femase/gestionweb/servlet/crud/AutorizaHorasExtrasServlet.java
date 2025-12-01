/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cl.femase.gestionweb.servlet.crud;

import cl.femase.gestionweb.business.MaintenanceEventsBp;
import cl.femase.gestionweb.common.ClientInfo;
import cl.femase.gestionweb.dao.DetalleAsistenciaDAO;
import com.google.gson.Gson;
import cl.femase.gestionweb.dao.EmpleadosDAO;
import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.vo.DetalleAsistenciaVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
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

@WebServlet("/api/autoriza_horas_extras")
public class AutorizaHorasExtrasServlet extends BaseServlet {

    private DetalleAsistenciaDAO dao;
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

        dao = new DetalleAsistenciaDAO(appProperties);
        daoEmpleados = new EmpleadosDAO(appProperties);
        System.out.println("[AutorizaHorasExtrasServlet.processRequest]params. accion: " + accion);

        if ("listar".equalsIgnoreCase(accion)) {
            System.out.println("[AutorizaHorasExtrasServlet.processRequest]params. "
                + "Listar detalle_asistencia para el empleado");
            String runEmpleado = jsonObj.has("empleado") ? jsonObj.get("empleado").getAsString() : null;
            String fechaInicio = jsonObj.has("fechaInicio") ? jsonObj.get("fechaInicio").getAsString() : null;
            String fechaFin = jsonObj.has("fechaFin") ? jsonObj.get("fechaFin").getAsString() : null;
            
//            EmpleadoVO infoEmpleado = daoEmpleados.getEmpleadoByEmpresaRun(userConnected.getEmpresaId(), runEmpleado);
//            req.setAttribute("admAusencias_cencoId", infoEmpleado.getCencoId());
//            req.setAttribute("admAusencias_cencoNombre", infoEmpleado.getCencoNombre());

            System.out.println("[AutorizaHorasExtrasServlet.listar]params. " +
                " EmpresaId: " + userConnected.getEmpresaId() +
                " , runEmpleado: " + runEmpleado +
                " , fechaInicio: " + fechaInicio +
                " , fechaFin: " + fechaFin );

            List<DetalleAsistenciaVO> lista =
                dao.getDetalleAsistenciaFiltro(userConnected.getEmpresaId(),
                    runEmpleado,
                    fechaInicio,
                    fechaFin);
            // 1) Obtener el String JSON
            String json = gson.toJson(lista);

            // 2) Mostrarlo en consola (o en tu logger)
            System.out.println("[AutorizaHorasExtrasServlet.listar]"
                + "JSON DetalleAsistenciaVO = " + json);

            // 3) Enviar al frontend
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(json);

//            gson.toJson(lista, resp.getWriter());
        } else if ("modificar".equalsIgnoreCase(accion)) {
            System.out.println("[AutorizaHorasExtrasServlet.processRequest] Modificar registro");
            boolean exito = false;
            String mensaje = "";

            try {
                // Usar el objeto VO para mapear todos los datos recibidos del front
                DetalleAsistenciaModVO data = gson.fromJson(jsonStr, DetalleAsistenciaModVO.class); 
                data.setEmpresaId(userConnected.getEmpresaId());
                System.out.println("[AutorizaHorasExtrasServlet.processRequest]Datos recibidos:" + data.toString());
                ResultadoAccionVO resultObj = modificarRegistroAsistencia(req, appProperties, userConnected, data);
                mensaje = resultObj.getMensaje();
                exito = resultObj.isExito();
                
                System.out.println("[AutorizaHorasExtrasServlet.processRequest]Registro de detalle_ausencia modificado. "
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
    private ResultadoAccionVO modificarRegistroAsistencia(HttpServletRequest _request, 
            PropertiesVO _appProperties, 
            UsuarioVO _userConnected, 
            DetalleAsistenciaModVO _data){
        ResultadoAccionVO resultadoModificar=new ResultadoAccionVO();
        
        MaintenanceEventVO eventoAuditoria = new MaintenanceEventVO();
        eventoAuditoria.setUsername(_userConnected.getUsername());
        eventoAuditoria.setDatetime(new Date());
        eventoAuditoria.setUserIP(_request.getRemoteAddr());
        eventoAuditoria.setType("DAU");
        eventoAuditoria.setEmpresaIdSource(_userConnected.getEmpresaId());
                
        //HashMap<String, Double> parametrosSistema = (HashMap<String, Double>)_session.getAttribute("parametros_sistema");
        DetalleAsistenciaDAO daoAsistencia = new DetalleAsistenciaDAO(_appProperties);
        System.out.println(WEB_NAME+"[AutorizaHorasExtrasServlet]update - "
            + "Datos existentes:"
            + "Empresa_id: " + _data.getEmpresaId()
            + ", rutEmpleado: " + _data.getRutEmpleado()
            + ", fechaMarcaEntrada: " + _data.getFechaEntrada()
            + ", horas extras totales: " + _data.getHorasExtras()
            + ", horas extras autorizadas: " + _data.getHorasExtrasAutorizadas()
            + ", Autoriza horas extras? (S: Si, N: No): " + _data.getHorasExtrasAutorizadasSN()
            );
        
        ResultCRUDVO updateResult = daoAsistencia.autorizaHorasExtras(_data.getEmpresaId(), 
            _data.getRutEmpleado(), 
            _data.getFechaEntrada(), 
            _data.getHorasExtrasAutorizadasSN(), 
            _data.getHorasExtrasAutorizadas());
        if (!updateResult.isThereError()){
            resultadoModificar.setMensaje("Datos actualizados exitosamente."
                + " Hrs extras autorizadas: " + _data.getHorasExtrasAutorizadas()
                + ", autorizadas? " + _data.getHorasExtrasAutorizadasSN());
            resultadoModificar.setExito(true);
        }else{
            resultadoModificar.setMensaje(updateResult.getMsgError());
            resultadoModificar.setExito(false);
        }
       
        //guardar en log de eventos
        addLogEventos(_request, _data.getRutEmpleado(), _data.getEmpresaId(), null, -1, 
            "Autorizacion de Horas extras: " + resultadoModificar.getMensaje());
        
        return resultadoModificar;
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
    
    public static class DetalleAsistenciaModVO {

        private String empresaId;
        private String rutEmpleado;
        private String nombreEmpleado;
        private String centroCosto;
        private String fechaEntrada;
        private String horaEntrada;
        private String fechaSalida;
        private String horaSalida;
        private String horaEntradaTeorica;
        private String horaSalidaTeorica;
        private String horasTeoricas;
        private String horasTrabajadas;
        private String horasPresenciales;
        private String horasExtras;
        private String horasExtrasAutorizadas;
        private String horasExtrasAutorizadasSN;

        public DetalleAsistenciaModVO() {
        }

        // Getters y setters

        public String getEmpresaId() {
            return empresaId;
        }

        public void setEmpresaId(String empresaId) {
            this.empresaId = empresaId;
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

        public String getCentroCosto() {
            return centroCosto;
        }

        public void setCentroCosto(String centroCosto) {
            this.centroCosto = centroCosto;
        }

        public String getFechaEntrada() {
            return fechaEntrada;
        }

        public void setFechaEntrada(String fechaEntrada) {
            this.fechaEntrada = fechaEntrada;
        }

        public String getHoraEntrada() {
            return horaEntrada;
        }

        public void setHoraEntrada(String horaEntrada) {
            this.horaEntrada = horaEntrada;
        }

        public String getFechaSalida() {
            return fechaSalida;
        }

        public void setFechaSalida(String fechaSalida) {
            this.fechaSalida = fechaSalida;
        }

        public String getHoraSalida() {
            return horaSalida;
        }

        public void setHoraSalida(String horaSalida) {
            this.horaSalida = horaSalida;
        }

        public String getHoraEntradaTeorica() {
            return horaEntradaTeorica;
        }

        public void setHoraEntradaTeorica(String horaEntradaTeorica) {
            this.horaEntradaTeorica = horaEntradaTeorica;
        }

        public String getHoraSalidaTeorica() {
            return horaSalidaTeorica;
        }

        public void setHoraSalidaTeorica(String horaSalidaTeorica) {
            this.horaSalidaTeorica = horaSalidaTeorica;
        }

        public String getHorasTeoricas() {
            return horasTeoricas;
        }

        public void setHorasTeoricas(String horasTeoricas) {
            this.horasTeoricas = horasTeoricas;
        }

        public String getHorasTrabajadas() {
            return horasTrabajadas;
        }

        public void setHorasTrabajadas(String horasTrabajadas) {
            this.horasTrabajadas = horasTrabajadas;
        }

        public String getHorasPresenciales() {
            return horasPresenciales;
        }

        public void setHorasPresenciales(String horasPresenciales) {
            this.horasPresenciales = horasPresenciales;
        }

        public String getHorasExtras() {
            return horasExtras;
        }

        public void setHorasExtras(String horasExtras) {
            this.horasExtras = horasExtras;
        }

        public String getHorasExtrasAutorizadas() {
            return horasExtrasAutorizadas;
        }

        public void setHorasExtrasAutorizadas(String horasExtrasAutorizadas) {
            this.horasExtrasAutorizadas = horasExtrasAutorizadas;
        }

        public String getHorasExtrasAutorizadasSN() {
            return horasExtrasAutorizadasSN;
        }

        public void setHorasExtrasAutorizadasSN(String horasExtrasAutorizadasSN) {
            this.horasExtrasAutorizadasSN = horasExtrasAutorizadasSN;
        }

        @Override
        public String toString() {
            return "DetalleAsistenciaModVO{" + "rutEmpleado=" + rutEmpleado + ", nombreEmpleado=" + nombreEmpleado + ", centroCosto=" + centroCosto + ", fechaEntrada=" + fechaEntrada + ", horaEntrada=" + horaEntrada + ", fechaSalida=" + fechaSalida + ", horaSalida=" + horaSalida + ", horaEntradaTeorica=" + horaEntradaTeorica + ", horaSalidaTeorica=" + horaSalidaTeorica + ", horasTeoricas=" + horasTeoricas + ", horasTrabajadas=" + horasTrabajadas + ", horasPresenciales=" + horasPresenciales + ", horasExtras=" + horasExtras + ", horasExtrasAutorizadas=" + horasExtrasAutorizadas + ", horasExtrasAutorizadasSN=" + horasExtrasAutorizadasSN + '}';
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
