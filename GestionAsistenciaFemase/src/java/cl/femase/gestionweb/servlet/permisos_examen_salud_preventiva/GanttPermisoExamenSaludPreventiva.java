package cl.femase.gestionweb.servlet.permisos_examen_salud_preventiva;

import cl.femase.gestionweb.business.CentroCostoBp;
import cl.femase.gestionweb.business.DetalleAusenciaBp;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.dao.DetalleAusenciaDAO;
import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.vo.DetalleAusenciaVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.FiltroBusquedaJsonVO;
import cl.femase.gestionweb.vo.GanttSerieValueVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;

@WebServlet(name = "GanttPermisoExamenSaludPreventiva", urlPatterns = {"/servlet/GanttPermisoExamenSaludPreventiva"})
public class GanttPermisoExamenSaludPreventiva extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 196L;
    
    public GanttPermisoExamenSaludPreventiva() {
        
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
    protected void processRequest(HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        EmpleadosBp empleadosBp         = new EmpleadosBp(appProperties);
        DetalleAusenciaBp ausenciasBp   = new DetalleAusenciaBp(appProperties);
        CentroCostoBp cencoBp   = new CentroCostoBp(appProperties);
        
        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME+"[servlet.GanttPermisoExamenSaludPreventiva]"
                + "action is: " + request.getParameter("action"));
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            if (action.compareTo("loadResult") == 0) {
                System.out.println(WEB_NAME+"[servlet.GanttPermisoExamenSaludPreventiva]"
                    + "mostrar gantt de Permisos Examen Salud Preventiva, generar json...");
                String paramCencoID = request.getParameter("cencoId");
                String paramAnio    = request.getParameter("paramAnio");
                String paramMes     = request.getParameter("paramMes");
                String anioMes      = paramAnio + "-" + paramMes;
                String paramEmpresa = null;
                String paramDepto   = null;
                String cencoId      = "";
                System.out.println(WEB_NAME+"[servlet.GanttPermisoExamenSaludPreventiva]"
                    + "token param 'cencoID'= " + paramCencoID);
                if (paramCencoID != null && paramCencoID.compareTo("-1") != 0){
                    StringTokenizer tokenCenco  = new StringTokenizer(paramCencoID, "|");
                    if (tokenCenco.countTokens() > 0){
                        while (tokenCenco.hasMoreTokens()){
                            paramEmpresa   = tokenCenco.nextToken();
                            paramDepto     = tokenCenco.nextToken();
                            cencoId     = tokenCenco.nextToken();
                        }
                    }
                }
//                VacacionesBp vacacionesBp = new VacacionesBp(appProperties);
                DetalleAusenciaDAO detalleAusenciaBp = new DetalleAusenciaDAO(appProperties);
                                
                System.out.println(WEB_NAME+"[servlet.GanttPermisoExamenSaludPreventiva]"
                    + "Obtener permisos examen salud preventiva. "
                    + "empresa: " + paramEmpresa
                    + ", depto: " + paramDepto
                    + ", cenco: " + cencoId
                    + ", anio: " + paramAnio
                    + ", mes: " + paramMes);

                int intCencoId=-1;
                if (cencoId.compareTo("") != 0) intCencoId = Integer.parseInt(cencoId);
                                
                try{
                    //nom empresa, depto y cenco
                    String jsonOutput = cencoBp.getEmpresaDeptoCencoJson(paramEmpresa, paramDepto, intCencoId);
                    FiltroBusquedaJsonVO labelsFiltro = (FiltroBusquedaJsonVO)new Gson().fromJson(jsonOutput, FiltroBusquedaJsonVO.class);
                    
                    ArrayList<GanttSerieValueVO> arbol = new ArrayList<>();
                    
                    EmpleadoVO filtroEmpleado = new EmpleadoVO();
                    filtroEmpleado.setEmpresaId(paramEmpresa);
                    filtroEmpleado.setDeptoId(paramDepto);
                    filtroEmpleado.setCencoId(intCencoId);
                    filtroEmpleado.setEstado(1);
                    filtroEmpleado.setEmpleadoVigente(true);
                    filtroEmpleado.setArticulo22(false);

                    /**
                     *  start: '2020-08-01',
                        end: '2020-08-08',
                        name: 'Alberto Miguel Lopez Rodriguez' --- rut, nombre del empleado y dias_efectivos ausencia,
                        id: "Task 0", -- correlativo ausencia
                        progress: 100
                     */
                    
                    List<EmpleadoVO> listaEmpleados = 
                        empleadosBp.getEmpleadosByFiltro(filtroEmpleado);
                    Iterator<EmpleadoVO> it = listaEmpleados.iterator();
                    while(it.hasNext()){
                        EmpleadoVO empleado = it.next();
                        String fullName = empleado.getShortFullNameCapitalize();
                        System.out.println(WEB_NAME+"[servlet.GanttPermisoExamenSaludPreventiva]"
                            + "empleado name: " + fullName);
                        List<DetalleAusenciaVO> listaPESP = 
                            ausenciasBp.getPermisosExamenSaludPreventivaByAnioMesInicio(empleado.getRut(), 
                                paramAnio + "-" + paramMes);
                        
                        Iterator<DetalleAusenciaVO> itPESP = listaPESP.iterator();
                        while(itPESP.hasNext()){
                            DetalleAusenciaVO ausencia = itPESP.next();
                            
                            GanttSerieValueVO itValue = new GanttSerieValueVO();
                            itValue.setId("" + ausencia.getCorrelativo());
                            
                            String auxName = fullName + " / " + empleado.getRut() + " / " + ausencia.getDiasSolicitados() + " dias";
                            System.out.println(WEB_NAME+"[servlet.GanttPermisoExamenSaludPreventiva]"
                                + "auxName: "+ auxName);
                            itValue.setName(auxName);
                            itValue.setStart(ausencia.getFechaInicioAsStr());
                            itValue.setEnd(ausencia.getFechaFinAsStr());
                            arbol.add(itValue);
                        }    
                        //rama.setSeries(series);//add lista de ausencias del empleado
                        //arbol.add(rama);
                    }
                    
                    System.out.println(WEB_NAME+"[servlet.GanttPermisoExamenSaludPreventiva]"
                        + "items a mostrar: "+ arbol.size());
                    
                    String arbolToJson = gson.toJson(arbol);
                    System.out.println(WEB_NAME+"[servlet.GanttPermisoExamenSaludPreventiva]"
                        + "Json string: " + arbolToJson);
                    request.setAttribute("jsonData", arbolToJson);
                    
                    String labelAnioMes = null;
                    SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM", new Locale("es","CL"));
                    SimpleDateFormat sdf1 = new SimpleDateFormat("MMMM yyyy", new Locale("es","CL"));
                    try {
                        Date dateAnioMes = sdf0.parse(anioMes);
                        labelAnioMes = sdf1.format(dateAnioMes);
                        System.out.println(WEB_NAME+"[servlet.GanttPermisoExamenSaludPreventiva]fecha anio mes: "+ dateAnioMes);
                        System.out.println(WEB_NAME+"[servlet.GanttPermisoExamenSaludPreventiva]anioMes: "+anioMes
                            + ", fmt1: " + labelAnioMes);
                        // uppercase first letter of each word
                        String output = Arrays.stream(labelAnioMes.split("\\s+"))
                            .map(t -> t.substring(0, 1).toUpperCase() + t.substring(1))
                            .collect(Collectors.joining(" "));
                        
                        labelAnioMes = output;
                        // print the string
                    } catch (ParseException ex) {
                        System.err.println("[servlet.GanttPermisoExamenSaludPreventiva]error al parsear fecha: " + ex.toString());
                    }
                    
                    request.setAttribute("empresaName", labelsFiltro.getEmpresanombre());
                    request.setAttribute("deptoName", labelsFiltro.getDeptonombre());
                    request.setAttribute("cencoName", labelsFiltro.getCenconombre());
                    request.setAttribute("anioMes", paramAnio + "-" + paramMes);
                    request.setAttribute("labelAnioMes", labelAnioMes);
                    
//                    response.setContentType("application/json");
//                    response.getWriter().write(arbolToJson);
        
                    request.getRequestDispatcher("/permisos_examen_salud_preventiva/gantt_viewer/gantt_view.jsp").forward(request, response);
                }catch(IOException ex){
                    String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                    response.getWriter().print(error);
                    ex.printStackTrace();
                }   
            }
        }
    }
    
}
