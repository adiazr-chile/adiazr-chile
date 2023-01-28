/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.servlet.procesos;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.CentroCostoBp;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.MarcasBp;
import cl.femase.gestionweb.business.NotificacionBp;
import cl.femase.gestionweb.common.ClientInfo;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.GetPropertyValues;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.MarcaVO;
import cl.femase.gestionweb.vo.NotificacionVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import com.google.gson.Gson;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Alexander
 */
@WebServlet(name = "MarcacionVirtualServlet", urlPatterns = {"/MarcacionVirtualServlet"})
public class MarcacionVirtualServlet extends BaseServlet {

    GetPropertyValues m_properties = new GetPropertyValues();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        if (userConnected != null){
            setResponseHeaders(response);
            processRequest(request, response);
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                + " no valida");
            System.err.println("[GestionFemase.MarcacionVirtualServlet.doPost]Sesion de usuario "+request.getParameter("username")
                + " no valida");
            request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        if (userConnected != null){
            setResponseHeaders(response);
            processRequest(request, response);
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                + " no valida");
            System.err.println("[GestionFemase.MarcacionVirtualServlet.doPost]Sesion de usuario "+request.getParameter("username")
                + " no valida");
            request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
        }
    }
    
    /**
    * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
    * methods.
    *
    * @param request servlet request
    * @param response servlet response
    * @throws ServletException if a servlet-specific error occurs
    * @throws IOException if an I/O error occurs
    */
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        EmpleadosBp empleadosbp = new EmpleadosBp(appProperties); 
        CentroCostoBp cencobp   = new CentroCostoBp(appProperties); 
        MarcasBp marcasBp       = new MarcasBp(appProperties);
        
        MarcaVO infomarca = new MarcaVO();
        SimpleDateFormat fechaFmt = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat horaFmt = new SimpleDateFormat("HH:mm:ss");
        Locale localeCl = new Locale("es", "CL");
        Calendar calendarioHoy=Calendar.getInstance(localeCl);
        String fechaActual = fechaFmt.format(calendarioHoy.getTime());
        String horaActual = horaFmt.format(calendarioHoy.getTime());
        
        ClientInfo clientInfo = new ClientInfo();
        
        MaintenanceEventVO resultado=new MaintenanceEventVO();
        resultado.setUsername(userConnected.getUsername());
        resultado.setDatetime(new Date());
        resultado.setUserIP(request.getRemoteAddr());
        /**
        * Agregar info a tabla eventos: Sistema operativo y navegador
        */
        resultado.setOperatingSystem(clientInfo.getClientOS(request));
        resultado.setBrowserName(clientInfo.getClientBrowser(request));
        resultado.setType("MAR");
        resultado.setEmpresaIdSource(userConnected.getEmpresaId());
        resultado.setRutEmpleado(userConnected.getUsername());
        
        //Obtener: cenco y cod dispositivo asociado...para luego insertar la marca
        EmpleadoVO infoEmpleado = 
            empleadosbp.getEmpleado(userConnected.getEmpresaId(), 
                userConnected.getUsername());
//        String deptoId  = infoEmpleado.getDeptoId();
//        int cencoId     = infoEmpleado.getCencoId();
//        CentroCostoVO cenco = cencobp.getCentroCostoByKey(deptoId, cencoId);
        //geolocalizacion
        String latitud = request.getParameter("latitud");
        String longitud = request.getParameter("longitud");
        if (latitud == null) latitud = "";
        if (longitud == null) longitud = "";
        String soloHora = horaActual;//request.getParameter("form_hora_server");
        
//        String soloHora = request.getParameter("hh")
//            +":"+request.getParameter("mm")
//            +":"+request.getParameter("ss");
        //cod dispositivo
        String deviceId = request.getParameter("device_id");
        //tipo marca. Entrada/Salida
        String tipoMarca = request.getParameter("tipoMarca");
        String fechaHora = fechaActual + " " + soloHora;
        
        System.out.println(WEB_NAME+"[MarcacionVirtualServlet."
            + "processRequest]"
            + "Run empleado: " + userConnected.getUsername()
            + ", fecha_hora: " + fechaHora
            + ", devideId: " + deviceId
            + ", tipoMarca: " + tipoMarca
            + ", latitud: " + latitud
            + ", longitud: " + longitud);
        //seteo de los datos para insertar la marca    
        infomarca.setLatitud(latitud);
        infomarca.setLongitud(longitud);
        infomarca.setFecha(fechaActual);//solo fecha: yyyy-MM-dd
        infomarca.setHora(soloHora);//solo hora: hh:mm:ss
        infomarca.setEmpresaCod(userConnected.getEmpresaId());
        infomarca.setRutEmpleado(userConnected.getUsername());
        infomarca.setFechaHora(fechaHora);
        infomarca.setCodDispositivo(deviceId);
        infomarca.setTipoMarca(Integer.parseInt(tipoMarca));
        infomarca.setId("Id.Marcacion Virtual");
        //setear ubicacion...
        infomarca.setComentario("Marcacion Virtual desde la IP: "+ resultado.getUserIP());
        infomarca.setCodTipoMarcaManual(Constantes.COD_MARCA_VIRTUAL);
        if (infomarca.getRutEmpleado() != null 
                && infomarca.getRutEmpleado().compareTo("") != 0){
            infomarca.setCodInternoEmpleado(infoEmpleado.getCodInterno());
            resultado.setEmpresaId(infoEmpleado.getEmpresa().getId());
            resultado.setDeptoId(infoEmpleado.getDepartamento().getId());
            resultado.setCencoId(infoEmpleado.getCentroCosto().getId());
        }
        
        /**
        *  Empl_rut + Fecha + Hora + Evento
        */
        String keyPhrase = infomarca.getRutEmpleado()+infomarca.getFechaHora()+infomarca.getTipoMarca();
        infomarca.setHashcode(Utilidades.getMD5EncryptedValue(keyPhrase));

        System.out.println(WEB_NAME+"[GestionFemase.MarcacionVirtualServlet]Insertar marca. "
            + "Empresa: " + userConnected.getEmpresaId()
            + ", rut: " + infomarca.getRutEmpleado()
            + ", fechaHora: " + infomarca.getFechaHora()
            + ", CodDispositivo: " + infomarca.getCodDispositivo()
            + ", TipoMarca: " + infomarca.getTipoMarca()
            + ", hashcode(md5: " + infomarca.getHashcode());

        ResultCRUDVO doCreate = marcasBp.insertWithLog(infomarca, resultado);
        System.out.println(WEB_NAME+"[GestionFemase.MarcacionVirtualServlet]Mensaje "
            + "del sp new_inserta_marca_manual: " + doCreate.getMsgFromSp());
        //if (!doCreate.isThereError()){
            informaCreacionMarca(doCreate.getMsgFromSp(), infomarca, userConnected, request);
        //}else{
            //informaCreacionMarca(doCreate.getMsgFromSp(), infomarca, userConnected, request);
        //}
//        infomarca.setRowKey(infomarca.getEmpresaCod()
//            + "|" + infomarca.getRutEmpleado()
//            + "|" + infomarca.getFechaHora()
//            + "|" + infomarca.getTipoMarca());
//        hashMarcas.put(infomarca.getRowKey(), infomarca);
//
        Gson gson = new Gson();
        //Convert Java Object to Json
        String json=gson.toJson(infomarca);					
       
        //Return Json in the format required by jTable plugin
        String jsonMsgFromSp = gson.toJson(doCreate.getMsgFromSp());
        String jsonOkMessage="{\"Result\":\"OK\",\"Record\":"+json+"}";
        System.out.println(WEB_NAME+"[GestionFemase.MarcacionVirtualServlet]result sp: "+ jsonMsgFromSp);
        String resultMessage = "";
        
        if (!doCreate.getMsgFromSp().startsWith("00-")){
            resultMessage = "Error al registrar marca virtual: " + jsonMsgFromSp;//jsonMsgFromSp;
            System.out.println(WEB_NAME+"[GestionFemase.MarcacionVirtualServlet]"+ resultMessage);
            request.setAttribute("isOk", false);
        }else{
            resultMessage = "Marca Virtual registrada exitosamente";//jsonOkMessage;
            System.out.println(WEB_NAME+"[GestionFemase.MarcacionVirtualServlet]"+ resultMessage);
            request.setAttribute("isOk", true);
        }
        request.setAttribute("resultMessage", resultMessage);        
        request.getRequestDispatcher("/marcacion_virtual/resultado_marcacion_virtual.jsp").forward(request, response);    
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
        String asuntoMail   = "Sistema de Gestion-Registro de asistencia virtual";
        String mailTo = empleado.getEmail();
        
        String mailBody = "Evento:" + _evento
            +"<br>RUN: " + empleado.getCodInterno()
            +"<br>Nombre: " + empleado.getNombreCompleto()
            +"<br>Institucion: " + empleado.getEmpresaNombre()
            +"<br>RUT: " + empleado.getEmpresaRut()
            +"<br>Ubicacion: " + empleado.getEmpresaDireccion()
            +"<br>Centro de costo: " + empleado.getCencoNombre()
            +"<br>Cod dispositivo: " + _marcaCreada.getCodDispositivo()    
            +"<br>Tipo Marca: " + tiposMarcas.get(_marcaCreada.getTipoMarca())+" virtual"
            +"<br>Fecha/Hora Marca: " + _marcaCreada.getFechaHora()
            +"<br>Codigo Hash: " + _marcaCreada.getHashcode()
            +"<br>Usuario que crea el registro: " + _userConnected.getUsername();
                
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
        
        System.out.println(WEB_NAME+"[mantencionEvento]"
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
        
        notificacion.setMarcaVirtual("S"); 
        notificacion.setLatitud(_marcaCreada.getLatitud());
        notificacion.setLongitud(_marcaCreada.getLongitud());
        if (_marcaCreada.getLatitud() != null 
            && _marcaCreada.getLatitud().compareTo("") != 0){
                notificacion.setRegistraUbicacion("S");
        }
        
        notificacion.setComentario(_marcaCreada.getComentario());
        
        notificacionBp.insert(notificacion, resultado);
        
    }
    
    /**
    * Returns a short description of the servlet.
    *
    * @return a String containing servlet description
    */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
