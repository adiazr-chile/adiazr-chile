package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.NotificacionBp;
import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.UsuarioBp;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.NotificacionVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class UsuariosController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 984L;
    
    public UsuariosController() {
        
    }

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
            m_logger.debug("Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
        }

    }
    
    /**
    * 
    * @param request
    * @param response
    * @throws jakarta.servlet.ServletException
    * @throws java.io.IOException
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
            m_logger.debug("Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
        }
    }

    /**
    * 
    * @param request
    * @param response
    * @throws jakarta.servlet.ServletException
    * @throws java.io.IOException
    */
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        
        UsuarioBp auxnegocio=new UsuarioBp(appProperties);

        if(request.getParameter("action") != null){
            List<UsuarioVO> listaObjetos = new ArrayList<UsuarioVO>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("USR");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
            resultado.setEmpresaId(userConnected.getEmpresaId());
            resultado.setDeptoId(userConnected.getDeptoId());
            resultado.setCencoId(userConnected.getIdCencoUsuario());
            
            System.out.println(WEB_NAME+"[UsuariosControllet]"
                + "user.empresaId: " + userConnected.getEmpresaId()
                + ", user.deptoId: " + userConnected.getDeptoId()
                + ", user.cencoId: " + userConnected.getIdCencoUsuario());
            
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "usr_username asc";
                        
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            
            if (jtSorting.contains("username")) jtSorting = jtSorting.replaceFirst("username","usr_username");
            else if (jtSorting.contains("nombres")) jtSorting = jtSorting.replaceFirst("nombres","usr_nombres");
            else if (jtSorting.contains("apPaterno")) jtSorting = jtSorting.replaceFirst("apPaterno","usr_ape_paterno");
            else if (jtSorting.contains("apMaterno")) jtSorting = jtSorting.replaceFirst("apMaterno","usr_ape_materno");
            else if (jtSorting.contains("email")) jtSorting = jtSorting.replaceFirst("email","usr_email");
            else if (jtSorting.contains("idPerfil")) jtSorting = jtSorting.replaceFirst("idPerfil","usr_perfil_id");
            else if (jtSorting.contains("estado")) jtSorting = jtSorting.replaceFirst("estado","estado_id");            
            else if (jtSorting.contains("empresaId")) jtSorting = jtSorting.replaceFirst("empresaId","empresa_id");            
            else if (jtSorting.contains("fechaCreacion")) jtSorting = jtSorting.replaceFirst("fechaCreacion","create_datetime");
            else if (jtSorting.contains("fechaActualizacion")) jtSorting = jtSorting.replaceFirst("fechaActualizacion","last_update_datetime");            
            
            /** filtros de busqueda */
            String username=null;
            String empresaId=null;
            int filtroEstado = -1;
            String password = null;
            String nombres=null;
            String apePaterno=null;
            String apeMaterno=null;
            int idPerfil=-1;
            int idEstado=-1;
            String email=null;
            
            if (request.getParameter("filtroEstado") != null) 
                filtroEstado = Integer.parseInt(request.getParameter("filtroEstado"));
            
            if (request.getParameter("nombres") != null) 
                nombres  = request.getParameter("nombres");
            if (request.getParameter("username") != null) 
                username  = request.getParameter("username");
            if (request.getParameter("empresaId") != null) 
                empresaId  = request.getParameter("empresaId");
            if (request.getParameter("password") != null) 
                password  = request.getParameter("password");
            if (request.getParameter("apPaterno") != null) 
                apePaterno  = request.getParameter("apPaterno");
            if (request.getParameter("apMaterno") != null) 
                apeMaterno  = request.getParameter("apMaterno");
            if (request.getParameter("email") != null) 
                email  = request.getParameter("email");
            if (request.getParameter("idPerfil") != null) 
                idPerfil  = Integer.parseInt(request.getParameter("idPerfil"));
            if (request.getParameter("estado") != null) 
                idEstado  = Integer.parseInt(request.getParameter("estado"));
            //objeto usado para update/insert
            UsuarioVO userdata = new UsuarioVO();
            userdata.setUsername(username);
            userdata.setPassword(password);
            userdata.setNombres(nombres);
            userdata.setEstado(idEstado);
            userdata.setApPaterno(apePaterno);
            userdata.setApMaterno(apeMaterno);
            userdata.setIdPerfil(idPerfil);
            userdata.setEmail(email);
            userdata.setEmpresaId(empresaId);
            userdata.setRunEmpleado(request.getParameter("run"));
            
            if (action.compareTo("list") == 0) {
                System.out.println(WEB_NAME+"[UsuariosControllet]"
                    + "Listar usuarios...");
                try{
                    listaObjetos = auxnegocio.getUsuarios(username, 
                        nombres, apePaterno, 
                        idPerfil, filtroEstado, 
                        empresaId, startPageIndex, 
                        numRecordsPerPage, 
                        jtSorting);

                    session.setAttribute("usuarios|"
                        + userConnected.getUsername(), listaObjetos);
                    //Get Total Record Count for Pagination
                    int rowsCount = auxnegocio.getUsuariosCount(username, 
                        nombres, apePaterno, 
                        idPerfil, filtroEstado, empresaId);

                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaObjetos,
                        new TypeToken<List<UsuarioVO>>() {}.getType());

                    JsonArray jsonArray = element.getAsJsonArray();
                    String listData=jsonArray.toString();

                    //Return Json in the format required by jTable plugin
                    listData="{\"Result\":\"OK\",\"Records\":" + 
                            listData+",\"TotalRecordCount\": " + 
                            rowsCount + "}";
                    response.getWriter().print(listData);
                    //request.getRequestDispatcher("/mantenedores/mantenedoresFrmSet.jsp").forward(request, response);
                }catch(Exception ex){
                    String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                    response.getWriter().print(error);
                    ex.printStackTrace();
                }   
            }else if (action.compareTo("changepass") == 0) {  
                    System.out.println(WEB_NAME+"[UsuariosControllet]Cambiar clave...");
                    try{
                        ResultCRUDVO doUpdate = 
                            auxnegocio.setPassword(userConnected.getUsername(), 
                                request.getParameter("password"),resultado);
                        session.setAttribute("type","changepass");
                        session.setAttribute("mensaje",
                            "Cambio de clave realizado exitosamente...");
                        
                        notificaCambioClave(userConnected, request);
                        
                        request.getRequestDispatcher("/mensaje.jsp").forward(request, response);//frameset
                        
                    }catch(IOException | ServletException ex){
                        String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getStackTrace().toString()+"}";
                        ex.printStackTrace();
                        response.getWriter().print(error);
                    }
            }
            
            
      }
    }
    
    /**
    * 
    */
    private void notificaCambioClave(UsuarioVO _userConnected, 
            HttpServletRequest _request){
    
        String fromLabel = "Gestion asistencia";
        String fromMail = m_properties.getKeyValue("mailFrom");
        String asuntoMail   = "Sistema de Gestion-Contraseña modificada con éxito";
        String mailTo = _userConnected.getEmail();
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
         // Crear un formateador de hora
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String horaFormateada = currentTime.format(formatter);
        System.out.println("[UsuariosController.notificaCambioClave]"
            + "horaFormateada: " + horaFormateada);
        String mailBody = "Estimado/a " + _userConnected.getNombreCompleto() + ","  
            +"<br>Le informamos que su contraseña para la cuenta " + _userConnected.getUsername() 
            + " se ha cambiado correctamente el " + currentDate + " a las " + horaFormateada +". "
            +"<br>Si no ha realizado este cambio, póngase en contacto con nosotros inmediatamente.";

        EmpleadosBp empleadobp = new EmpleadosBp();
        EmpleadoVO empleado = 
            empleadobp.getEmpleado(_userConnected.getEmpresaId(), 
            _userConnected.getRunEmpleado());
        
        MaintenanceEventVO resultado=new MaintenanceEventVO();
        resultado.setUsername(_userConnected.getUsername());
        resultado.setDatetime(new Date());
        resultado.setUserIP(_request.getRemoteAddr());
        resultado.setType("NOT");
        resultado.setEmpresaIdSource(_userConnected.getEmpresaId());
        if (empleado != null){
            resultado.setEmpresaId(empleado.getEmpresaId());
            resultado.setDeptoId(empleado.getDeptoId());
            resultado.setCencoId(empleado.getCencoId());
            resultado.setRutEmpleado(empleado.getRut());
        }
        NotificacionBp notificacionBp=new NotificacionBp(null);
        NotificacionVO notificacion = new NotificacionVO();
        notificacion.setEmpresaId(_userConnected.getEmpresaId());
        if (empleado != null){
            notificacion.setCencoId(empleado.getCencoId());
            notificacion.setRutEmpleado(empleado.getRut());
        }
        notificacion.setMailFrom(fromMail);
        notificacion.setMailTo(mailTo);
        notificacion.setMailSubject(asuntoMail);
        notificacion.setMailBody(mailBody);
        notificacion.setUsername(_userConnected.getUsername());
        notificacion.setComentario("Contraseña modificada con éxito");
        notificacionBp.insert(notificacion, resultado);
   
    }
}
