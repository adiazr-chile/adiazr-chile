package cl.femase.gestionweb.servlet.consultas;

import cl.femase.gestionweb.business.CentroCostoBp;
import cl.femase.gestionweb.business.DetalleAusenciaBp;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.VacacionesBp;
import cl.femase.gestionweb.dao.DetalleAusenciaDAO;
import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.vo.DetalleAusenciaVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.FiltroBusquedaJsonVO;
import cl.femase.gestionweb.vo.GanttSerieValueVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import cl.femase.gestionweb.vo.VacacionesVO;
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
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class GanttVacaciones extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 996L;
    
    public GanttVacaciones() {
        
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
            System.out.println(WEB_NAME+"[GanttVacaciones]"
                + "action is: " + request.getParameter("action"));
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            //response.setContentType("application/json");
       
//            if (action.compareTo("loadResult") == 0) {
//                String paramCencoID = request.getParameter("cencoId");
//                String paramAnio    = request.getParameter("paramAnio");
//                String paramMes     = request.getParameter("paramMes");
//                request.setAttribute("cencoId", paramCencoID);
//                request.setAttribute("paramAnio", paramAnio);
//                request.setAttribute("paramMes", paramMes);
//                request.getRequestDispatcher("/vacaciones/gantt_viewer/gantt_view.jsp").forward(request, response);
//                
//            }else 
            if (action.compareTo("loadResult") == 0) {
                System.out.println(WEB_NAME+"[GanttVacaciones]"
                    + "mostrar gantt vacaciones, generar json...");
                String paramCencoID = request.getParameter("cencoId");
                String paramAnio    = request.getParameter("paramAnio");
                String paramMes     = request.getParameter("paramMes");
                String anioMes      = paramAnio + "-" + paramMes;
                String paramEmpresa = null;
                String paramDepto   = null;
                String cencoId      = "";
                System.out.println(WEB_NAME+"[GanttVacaciones]"
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
                VacacionesBp vacacionesBp = new VacacionesBp(appProperties);
                DetalleAusenciaDAO detalleAusenciaBp = new DetalleAusenciaDAO(appProperties);
                                
                System.out.println(WEB_NAME+"[GanttVacaciones]"
                    + "Listar info vacaciones. "
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
                        System.out.println(WEB_NAME+"[GanttVacaciones]"
                            + "empleado name: " + fullName);
                        List<DetalleAusenciaVO> lstVacaciones = 
                            ausenciasBp.getVacacionesByAnioMesInicio(empleado.getRut(), 
                                paramAnio + "-" + paramMes);
                        
                        Iterator<DetalleAusenciaVO> itVacaciones = lstVacaciones.iterator();
                        while(itVacaciones.hasNext()){
                            DetalleAusenciaVO ausencia = itVacaciones.next();
                            
                            GanttSerieValueVO itValue = new GanttSerieValueVO();
                            itValue.setId("" + ausencia.getCorrelativo());
                            
                            if (ausencia.getDiasEfectivosVacaciones() == 0){
                                //calcular dias efectivos de vacaciones
                                List<VacacionesVO> infoVacaciones = 
                                    vacacionesBp.getInfoVacaciones(paramEmpresa, 
                                        empleado.getRut(), -1, -1, -1, "vac.rut_empleado");
                                VacacionesVO saldoVacaciones = new VacacionesVO();
                                if (!infoVacaciones.isEmpty()){
                                    saldoVacaciones = infoVacaciones.get(0);
                                    int diasFectivos = 
                                        vacacionesBp.getDiasEfectivos(ausencia.getFechaInicioAsStr(), 
                                            ausencia.getFechaFinAsStr(), 
                                            saldoVacaciones.getDiasEspeciales(),
                                            paramEmpresa, 
                                            empleado.getRut());
                                    System.out.println(WEB_NAME+"[GanttVacaciones]"
                                        + "Actualizar dias efectivos de vacaciones. "
                                        + "inicio: "+ ausencia.getFechaInicioAsStr()
                                        + ", fin: "+ ausencia.getFechaFinAsStr()
                                        + ", dias efectivos: "+ diasFectivos);
                                    detalleAusenciaBp.updateDiasEfectivosVacaciones(ausencia.getCorrelativo(), diasFectivos);
                                }
                            }
                            
                            itValue.setName(fullName + " / " + empleado.getRut() + " / " + ausencia.getDiasEfectivosVacaciones() + " dias");
                            itValue.setStart(ausencia.getFechaInicioAsStr());
                            itValue.setEnd(ausencia.getFechaFinAsStr());
                            arbol.add(itValue);
                        }    
                        //rama.setSeries(series);//add lista de ausencias del empleado
                        //arbol.add(rama);
                    }
                    
                    System.out.println(WEB_NAME+"[GanttVacaciones]"
                        + "items a mostrar: "+ arbol.size());
                    
                    String arbolToJson = gson.toJson(arbol);
                    System.out.println(WEB_NAME+"[GanttVacaciones]"
                        + "Json string: " + arbolToJson);
                    request.setAttribute("jsonData", arbolToJson);
                    
                    String labelAnioMes = null;
                    SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM", new Locale("es","CL"));
                    SimpleDateFormat sdf1 = new SimpleDateFormat("MMMM yyyy", new Locale("es","CL"));
                    try {
                        Date dateAnioMes = sdf0.parse(anioMes);
                        labelAnioMes = sdf1.format(dateAnioMes);
                        System.out.println(WEB_NAME+"fecha anio mes: "+ dateAnioMes);
                        System.out.println(WEB_NAME+"anioMes: "+anioMes
                            + ", fmt1: " + labelAnioMes);
                        // uppercase first letter of each word
                        String output = Arrays.stream(labelAnioMes.split("\\s+"))
                            .map(t -> t.substring(0, 1).toUpperCase() + t.substring(1))
                            .collect(Collectors.joining(" "));
                        
                        labelAnioMes = output;
                        // print the string
                    } catch (ParseException ex) {
                        System.err.println("error al parsear fecha: " + ex.toString());
                    }
                    
                    request.setAttribute("empresaName", labelsFiltro.getEmpresanombre());
                    request.setAttribute("deptoName", labelsFiltro.getDeptonombre());
                    request.setAttribute("cencoName", labelsFiltro.getCenconombre());
                    request.setAttribute("anioMes", paramAnio + "-" + paramMes);
                    request.setAttribute("labelAnioMes", labelAnioMes);
                    
//                    response.setContentType("application/json");
//                    response.getWriter().write(arbolToJson);
        
                    request.getRequestDispatcher("/vacaciones/gantt_viewer/gantt_view.jsp").forward(request, response);
                }catch(IOException ex){
                    String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                    response.getWriter().print(error);
                    ex.printStackTrace();
                }   
            }
        }
    }
    
}
