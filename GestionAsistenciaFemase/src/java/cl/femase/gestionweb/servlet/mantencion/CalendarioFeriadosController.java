package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.CalendarioFeriadoBp;
import cl.femase.gestionweb.vo.CalendarioFeriadoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.util.Arrays;
import java.util.Date;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class CalendarioFeriadosController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 997L;
    
    public CalendarioFeriadosController() {
        
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        if (session!=null) session.removeAttribute("mensaje");
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        if (session!=null) session.removeAttribute("mensaje");
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

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        if (session!=null) session.removeAttribute("mensaje");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        CalendarioFeriadoBp feriadosBp = new CalendarioFeriadoBp(appProperties);

        if(request.getParameter("action") != null){
            System.out.println("[CalendarioFeriadosController]"
                + "action is: " + request.getParameter("action"));
            List<CalendarioFeriadoVO> listaObjetos = new ArrayList<>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("AFE");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                   
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "fecha asc";
            /** filtros de busqueda */
            int paramAnio   = -1;
            int paramMes    = -1;
            int paramTipoFeriado = -1;
                        
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
           
            /** ordenamiento por las columnas definidas*/
            if (jtSorting.contains("fechaHoraIngreso")) jtSorting = jtSorting.replaceFirst("fechaHoraIngreso","fecha_hora_ingreso");
            else if (jtSorting.contains("fechaHoraActualizacion")) jtSorting = jtSorting.replaceFirst("fechaHoraActualizacion","fecha_hora_actualizacion");
            
            if (request.getParameter("paramAnio") != null) 
                paramAnio = Integer.parseInt(request.getParameter("paramAnio"));
            if (request.getParameter("paramMes") != null) 
                paramMes = Integer.parseInt(request.getParameter("paramMes"));
            if (request.getParameter("paramTipoFeriado") != null) 
                paramTipoFeriado = Integer.parseInt(request.getParameter("paramTipoFeriado"));
            
            //objeto usado para update/insert. toma los datos del formulario
            CalendarioFeriadoVO auxdata = new CalendarioFeriadoVO();
             
            if (request.getParameter("fecha") != null){
                auxdata.setFecha(request.getParameter("fecha"));
            }
            
            if (request.getParameter("label") != null){
                auxdata.setLabel(request.getParameter("label"));
            }
            if (request.getParameter("observacion") != null){
                auxdata.setObservacion(request.getParameter("observacion"));
            }
            
            if (auxdata.getFecha()!=null){
                //obtener dia-mes-anio desde la fecha en formato yyyy-mm-dd
                StringTokenizer tokenFecha=new StringTokenizer(auxdata.getFecha(), "-");
                auxdata.setAnio(Integer.parseInt(tokenFecha.nextToken()));
                auxdata.setMes(Integer.parseInt(tokenFecha.nextToken()));
                auxdata.setDia(Integer.parseInt(tokenFecha.nextToken()));
            }
            
            System.out.println("[CalendarioFeriadosController]"
                + "rowKey: " + request.getParameter("rowKey"));
            
            if (request.getParameter("rowKey") != null){
                auxdata.setRowKey(request.getParameter("rowKey"));
            }
            if (request.getParameter("irrenunciable") != null){
                auxdata.setIrrenunciable(request.getParameter("irrenunciable"));
            }
            if (request.getParameter("tipo") != null){
                auxdata.setTipo(request.getParameter("tipo"));
            }
            if (request.getParameter("respaldoLegal") != null){
                auxdata.setRespaldoLegal(request.getParameter("respaldoLegal"));
            }
            if (request.getParameter("idTipoFeriado") != null){
                auxdata.setIdTipoFeriado(Integer.parseInt(request.getParameter("idTipoFeriado")));
            }
            if (request.getParameter("regionId") != null){
                auxdata.setRegionId(Integer.parseInt(request.getParameter("regionId")));
            }
            if (request.getParameter("comunaId") != null){
                auxdata.setComunaId(Integer.parseInt(request.getParameter("comunaId")));
            }
            
            if (action.compareTo("list") == 0) {
                System.out.println("[CalendarioFeriadosController]"
                    + " listar feriados...");
                try{
                    listaObjetos = feriadosBp.getFeriados(paramAnio,paramMes,paramTipoFeriado,
                        startPageIndex, 
                        numRecordsPerPage, 
                        jtSorting);

                    //Get Total Record Count for Pagination
                    int rowsCount = feriadosBp.getFeriadosCount(paramAnio,paramMes,paramTipoFeriado);

                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaObjetos,
                            new TypeToken<List<CalendarioFeriadoVO>>() {}.getType());

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
            }else if (action.compareTo("create") == 0) {
                    System.out.println("[CalendarioFeriadosController]"
                        + "Insertar feriado...");
                    MaintenanceVO doCreate = feriadosBp.insert(auxdata, resultado);					
                    listaObjetos.add(auxdata);

                    //Convert Java Object to Json
                    String json=gson.toJson(auxdata);					
                    //Return Json in the format required by jTable plugin
                    String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                    response.getWriter().print(listData);
            }else if (action.compareTo("update") == 0) {  
                    System.out.println("[CalendarioFeriadosController]"
                        + "Actualizar Feriado, rowKey: " + request.getParameter("rowKey"));
                    try{
                        MaintenanceVO doUpdate = feriadosBp.update(auxdata, resultado);
                        listaObjetos.add(auxdata);

                        //Convert Java Object to Json
                        String json=gson.toJson(auxdata);					
                        //Return Json in the format required by jTable plugin
                        String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                        response.getWriter().print(listData);
                    }catch(IOException ex){
                        String error="{\"Result\":\"ERROR\",\"Message\":"+Arrays.toString(ex.getStackTrace())+"}";
                        response.getWriter().print(error);
                    }
            }else if (action.compareTo("delete") == 0) {  
                    System.out.println("[CalendarioFeriadosController]"
                        + "Eliminar Feriado...");
                    try{
                        MaintenanceVO doDelete = feriadosBp.delete(auxdata.getRowKey(), resultado);
                        String listData="{\"Result\":\"OK\"}";
                        response.getWriter().print(listData);
                    }catch(IOException ex){
                        String error="{\"Result\":\"ERROR\",\"Message\":"+Arrays.toString(ex.getStackTrace())+"}";
                        response.getWriter().print(error);
                    }
            }
      }
    }
    
}
