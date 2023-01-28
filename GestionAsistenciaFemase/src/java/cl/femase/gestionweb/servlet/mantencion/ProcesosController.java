package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.ProcesosBp;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.ProcesoFiltroVO;
import cl.femase.gestionweb.vo.ProcesoProgramacionVO;
import cl.femase.gestionweb.vo.ProcesoVO;
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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import static org.quartz.JobBuilder.newJob;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import static org.quartz.TriggerBuilder.newTrigger;
import org.quartz.impl.StdSchedulerFactory;

public class ProcesosController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 995L;
    
    public ProcesosController() {
        
    }

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

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        ProcesosBp auxnegocio=new ProcesosBp(appProperties);

        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME+"[ProcesosController]action is: " + request.getParameter("action"));
            List<ProcesoVO> listaObjetos = new ArrayList<ProcesoVO>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO logEvento=new MaintenanceEventVO();
            logEvento.setUsername(userConnected.getUsername());
            logEvento.setDatetime(new Date());
            logEvento.setUserIP(request.getRemoteAddr());
            logEvento.setType("PRO");
                        
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "proc_name asc";
            /** filtros de busqueda */
            String nombre      = "";
            int estado = -1;
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            
            if (jtSorting.contains("empresaId")) jtSorting = jtSorting.replaceFirst("empresaId","empresa_id");
            else if (jtSorting.contains("id")) jtSorting = jtSorting.replaceFirst("id","proc_id");
            else if (jtSorting.contains("estado")) jtSorting = jtSorting.replaceFirst("estado","proc_estado");
            else if (jtSorting.contains("nombre")) jtSorting = jtSorting.replaceFirst("nombre","proc_name");
            else if (jtSorting.contains("jobName")) jtSorting = jtSorting.replaceFirst("jobName","proc_jobname");
            else if (jtSorting.contains("fechaHoraActualizacion")) jtSorting = jtSorting.replaceFirst("fechaHoraActualizacion","proc_fec_actualizacion");
            
            //objeto usado para update/insert
            ProcesoVO auxdata = new ProcesoVO();
            if(request.getParameter("rowKey") != null){
                auxdata.setRowKey(request.getParameter("rowKey"));
            } 
            if(request.getParameter("empresaId") != null){
                auxdata.setEmpresaId(request.getParameter("empresaId"));
            } 
            if(request.getParameter("id") != null){
                auxdata.setId(Integer.parseInt(request.getParameter("id")));
            }
            if(request.getParameter("estado") != null){
                auxdata.setEstado(Integer.parseInt(request.getParameter("estado")));
            }
            if(request.getParameter("nombre") != null){
                auxdata.setNombre(request.getParameter("nombre"));
            }
             if(request.getParameter("jobName") != null){
                auxdata.setJobName(request.getParameter("jobName"));
            }
             
//            System.out.println(WEB_NAME+"--->rowKey: " + auxdata.getRowKey());
            String keyEmpresa = "";
            String keyIdProceso = "";
            
            if (auxdata.getRowKey()!=null){
                StringTokenizer keyToken = new StringTokenizer(auxdata.getRowKey(), "|");
                keyEmpresa = keyToken.nextToken();
                keyIdProceso = keyToken.nextToken();
                auxdata.setEmpresaId(keyEmpresa);
                auxdata.setId(Integer.parseInt(keyIdProceso));
            }
            if (action.compareTo("list") == 0) {
                System.out.println(WEB_NAME+"Mantenedor - Procesos - "
                    + "mostrando procesos. "
                    + "empresa: "+auxdata.getEmpresaId()
                    + ", nombre: "+auxdata.getNombre());
                try{
                    int objectsCount = 0;
                    if (auxdata.getEmpresaId().compareTo("-1")!=0){
                        listaObjetos = auxnegocio.getProcesos(auxdata.getEmpresaId(), 
                            auxdata.getNombre(),
                            startPageIndex, 
                            numRecordsPerPage, 
                            jtSorting);
                        objectsCount = 
                            auxnegocio.getProcesosCount(auxdata.getEmpresaId(),auxdata.getNombre());
                    }
                    
                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaObjetos,
                        new TypeToken<List<ProcesoVO>>() {}.getType());

                    JsonArray jsonArray = element.getAsJsonArray();
                    String listData=jsonArray.toString();
                        
                    //Return Json in the format required by jTable plugin
                    listData="{\"Result\":\"OK\",\"Records\":" + 
                        listData+",\"TotalRecordCount\": " + 
                        objectsCount + "}";
                    response.getWriter().print(listData);
                    //request.getRequestDispatcher("/mantenedores/mantenedoresFrmSet.jsp").forward(request, response);
                }catch(Exception ex){
                    String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                    response.getWriter().print(error);
                    ex.printStackTrace();
                }   
            }else if (action.compareTo("create") == 0) {
                    System.out.println(WEB_NAME+"Mantenedor - Procesos - Insertar...");
                    System.out.println(WEB_NAME+"Proceso rowKey: "+auxdata.getRowKey()
                        +", empresaId: "+auxdata.getEmpresaId()
                        +", idproceso: "+auxdata.getId());
                    logEvento.setEmpresaId(auxdata.getEmpresaId());
                    ResultCRUDVO doCreate = auxnegocio.insert(auxdata, logEvento);					
                    listaObjetos.add(auxdata);

                    //Convert Java Object to Json
                    String json=gson.toJson(auxdata);					
                    //Return Json in the format required by jTable plugin
                    String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                    response.getWriter().print(listData);
            }else if (action.compareTo("update") == 0) {  
                    System.out.println(WEB_NAME+"Mantenedor - Procesos - Actualizar...");
                    try{
                        System.out.println(WEB_NAME+"Proceso rowKey: " + auxdata.getRowKey()
                            +", empresaId: " + auxdata.getEmpresaId()
                            +", idproceso: " + auxdata.getId());
                        logEvento.setEmpresaId(auxdata.getEmpresaId());
                        ResultCRUDVO doUpdate = auxnegocio.update(auxdata, logEvento);
                        listaObjetos.add(auxdata);

                        //Convert Java Object to Json
                        String json=gson.toJson(auxdata);					
                        //Return Json in the format required by jTable plugin
                        String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                        response.getWriter().print(listData);

                    }catch(Exception ex){
                        String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getStackTrace().toString()+"}";
                        response.getWriter().print(error);
                    }
            }else if (action.compareTo("load_programacion") == 0) {  
                    String paramEmpresa = request.getParameter("empresaProceso");
                    int paramIdProceso = Integer.parseInt(request.getParameter("idProceso"));
                    int codDia = Integer.parseInt(request.getParameter("codDia"));
                    if (codDia == -1) codDia = 1;//rescatar programacion de dia lunes    
                    ProcesoProgramacionVO programacion = 
                        auxnegocio.getProgramacion(paramEmpresa, paramIdProceso, codDia);
                    ArrayList<String> listaHoras = new ArrayList<>();
                    if (programacion!=null){
                        StringTokenizer tokenHoras = new StringTokenizer(programacion.getHorasEjecucion(), ",");
                        while (tokenHoras.hasMoreTokens()){
                            listaHoras.add(tokenHoras.nextToken());
                        }
                    }
                    request.setAttribute("listaHoras", listaHoras);
                    request.setAttribute("empresaProcesoSelected", paramEmpresa);
                    request.setAttribute("procesoSelected", paramIdProceso);
                    request.setAttribute("diaSelected", Integer.parseInt(request.getParameter("codDia")));
                    
                    request.getRequestDispatcher("/mantencion/programacion_proceso.jsp").forward(request, response);        
            }else if (action.compareTo("programar_proceso") == 0) {  
                        String paramEmpresa = request.getParameter("empresaProceso");
                        int paramIdProceso = Integer.parseInt(request.getParameter("idProceso"));
                        int codDia = Integer.parseInt(request.getParameter("codDia"));
                        String strHoras = "";
                        String strMsg="Programacion actualizada correctamente";
                        String strMsgError="";
                        String[] horasSelected = request.getParameterValues("horas");
                        logEvento.setEmpresaId(paramEmpresa);
                        ProcesoProgramacionVO programacion = new ProcesoProgramacionVO();
                        
                        if (horasSelected != null){
                            for (int x = 0;x < horasSelected.length; x++){
                                System.out.println(WEB_NAME+"[ProcesosController]"
                                    + "guardar programacion proceso[" + x + "] = " + horasSelected[x]);
                                strHoras += horasSelected[x] + ",";
                            }
                            if (strHoras.compareTo("") != 0) strHoras = strHoras.substring(0, strHoras.length()-1);
                        }
                        programacion.setEmpresaId(paramEmpresa);
                        programacion.setProcesoId(paramIdProceso);
                        programacion.setHorasEjecucion(strHoras);
                        if (codDia != -1){
                           programacion.setCodDia(codDia);
                        }         
                        //sino se especifica el dia, se elimina toda la programacion del proceso
                        ResultCRUDVO deleteProg = auxnegocio.deleteProgramacion(programacion, logEvento);
                        if (!deleteProg.isThereError()){
                            if (codDia != -1 && horasSelected != null){
                                System.out.println(WEB_NAME+"[ProcesosController]"
                                    + "guardar programacion dia especifico. "
                                    + " empresaID= " + paramEmpresa
                                    + ",procesoID= " + paramIdProceso
                                    + ",codDia= "+codDia
                                    + ",horas= "+strHoras);
                                auxnegocio.insertProgramacion(programacion, logEvento);
                            }else if (horasSelected != null){
                                    //eliminar toda la programacion del proceso
                                   for (int intDia = 1;intDia <= 7; intDia++){
                                        programacion.setCodDia(intDia);
                                        System.out.println(WEB_NAME+"[ProcesosController]"
                                            + "guardar programacion. "
                                            + " empresaID= " + paramEmpresa
                                            + ",procesoID= " + paramIdProceso
                                            + ",codDia= " + intDia
                                            + ",horas= " + strHoras);
                                        auxnegocio.insertProgramacion(programacion, logEvento);
                                   } 
                            }
                        }else{
                            strMsgError = "Error al guardar programacion: "+deleteProg.getMsgError();
                        }
                            
                        //cargar pagina con programacion resultante
                        if (codDia == -1){
                            codDia = 1;
                        }
                        programacion = 
                            auxnegocio.getProgramacion(paramEmpresa, paramIdProceso, codDia);
                        ArrayList<String> listaHoras = new ArrayList<>();
                        if (programacion!=null){
                            StringTokenizer tokenHoras = new StringTokenizer(programacion.getHorasEjecucion(), ",");
                            while (tokenHoras.hasMoreTokens()){
                                listaHoras.add(tokenHoras.nextToken());
                            }
                        }

                        System.out.println(WEB_NAME+"[ProcesosController]"
                            + "recargar programacion de jobs quartz. "
                            + " EmpresaID = " + paramEmpresa
                            + ", procesoID = " + paramIdProceso
                            + ", codDia = "+codDia);

                        auxnegocio.scheduleJobs();
                        
                        request.setAttribute("listaHoras", listaHoras);
                        request.setAttribute("empresaProcesoSelected", paramEmpresa);
                        request.setAttribute("procesoSelected", paramIdProceso);
                        request.setAttribute("diaSelected", codDia);
                        request.setAttribute("mensaje", strMsg);
                        request.setAttribute("mensajeError", strMsgError);
                        request.getRequestDispatcher("/mantencion/programacion_proceso.jsp").forward(request, response);   
            }else if (action.compareTo("load_filtros") == 0) {
                        String paramEmpresa = request.getParameter("empresaProceso");
                        int paramIdProceso = Integer.parseInt(request.getParameter("idProceso"));
                        System.out.println(WEB_NAME+"[ProcesosController."
                            + "loadFiltros]"
                            + "empresaId: "+ paramEmpresa    
                            + ", procesoId: "+ paramIdProceso);
                        LinkedHashMap<String, ProcesoFiltroVO> filtros 
                            = auxnegocio.getFiltros(paramEmpresa, paramIdProceso);
                        
                        request.setAttribute("empresaProcesoSelected", paramEmpresa);
                        request.setAttribute("procesoSelected", paramIdProceso);
                        request.setAttribute("filtros", filtros);
                        
                        request.getRequestDispatcher("/mantencion/ejecucion_procesos.jsp").forward(request, response);
            }else if (action.compareTo("ejecutar_proceso") == 0) {
                    String paramEmpresa = request.getParameter("empresaProceso");
                    int paramIdProceso = Integer.parseInt(request.getParameter("idProceso"));
                    System.out.println(WEB_NAME+"[ProcesosController."
                        + "ejecutar]Ejecutar proceso. "
                        + "EmpresaId: "+ paramEmpresa    
                        + ", procesoId: "+ paramIdProceso);
                    try{    
                        ProcesoVO proceso = auxnegocio.getProceso(paramEmpresa, paramIdProceso);
                        int correlativo = 1;
                        if (proceso != null){
                            SchedulerFactory sf = new StdSchedulerFactory();
                            Scheduler sched = sf.getScheduler();
                            sched.clear();
                            sched.start();
                            Class jobClass=null;
                            String strClassRef=""; 
                            // define the job and tie it to our MyJob class
                            strClassRef="cl.femase.gestionweb.jobs."+proceso.getJobName();
                            jobClass = Class.forName(strClassRef);//, true, ClassLoader.getSystemClassLoader());
                            String jobname = proceso.getJobName()+"_"+correlativo;
                            JobDetail job = newJob(jobClass)
                                .withIdentity(jobname, "group_" + proceso.getJobName())
                                .build();            
                            System.out.println(WEB_NAME+"[ProcesosController."
                                + "ejecutar]Ejecutar proceso. "
                                + "jobName: "+ proceso.getJobName());
                            job.getJobDataMap().put("exec_user", userConnected.getUsername());
                            job.getJobDataMap().put("proceso_id", paramIdProceso);
                            
                            LinkedHashMap<String, ProcesoFiltroVO> filtros 
                                = auxnegocio.getFiltros(paramEmpresa, paramIdProceso);
                            if (!filtros.isEmpty()){
                                Set<String> keysFiltros = filtros.keySet();
                                for(String k:keysFiltros){
                                    ProcesoFiltroVO filtro = filtros.get(k);
                                    String filtroValue = request.getParameter(filtro.getCode());
                                    System.out.println(WEB_NAME+"[ProcesosController."
                                        + "ejecutar]filtro: "+filtro.getCode()
                                        + ", value: " + filtroValue);
                                    if (filtro.getCode().compareTo("cenco_id") == 0){
                                        String[] cencosSelected = request.getParameterValues("cenco_id");
                                        if (cencosSelected != null){
                                            String filtrocencos="";
                                            for (int x = 0; x < cencosSelected.length;x++){
                                                filtrocencos += cencosSelected[x]+",";
                                            }
                                            filtrocencos = filtrocencos.substring(0, filtrocencos.length()-1);
                                            System.out.println(WEB_NAME+"[ProcesosController."
                                                + "ejecutar]cencosSeleccionados: "+filtrocencos);
                                            job.getJobDataMap().put(filtro.getCode(), filtrocencos);
                                        }
                                    }else{
                                        job.getJobDataMap().put(filtro.getCode(), filtroValue);
                                    }
                                }//fin iteracion de filtros
                            }else{
                                System.out.println(WEB_NAME+"[ProcesosController."
                                    + "ejecutar]No hay filtros definidos. "
                                    + "Usar empresa seleccionada: " + paramEmpresa);
                                job.getJobDataMap().put("empresa_id", paramEmpresa);
                            }
                            System.out.println(WEB_NAME+"[ProcesosController."
                                + "ejecutar]Ejecutar job: "+strClassRef);
                            SimpleTrigger trigger = (SimpleTrigger)newTrigger()
                                .withIdentity("trigger"+correlativo, "group_"+proceso.getJobName())
                                .startNow() //ejecutar inmediatamente
                                .forJob(jobname, "group_"+proceso.getJobName()) // identify job with name, group strings
                                .build();

                            // Tell quartz to schedule the job using our trigger
                            sched.scheduleJob(job, trigger);
                            
                        }
                    }catch(SchedulerException shex){
                        System.err.println("[ProcesosController."
                            + "ejecutar]Error al ejecutar job: "+shex.toString());
                    }catch(ClassNotFoundException cnf){
                        System.err.println("[ProcesosController."
                            + "ejecutar]Error al ejecutar job: "+cnf.toString());
                    }
                    request.setAttribute("empresaProcesoSelected", paramEmpresa);
                    request.setAttribute("procesoSelected", paramIdProceso);

                    request.getRequestDispatcher("/mantencion/ejecucion_procesos.jsp").forward(request, response);
            }
      }
    }
    
}
