package cl.femase.gestionweb.servlet.mantencion.turnos;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.CentroCostoBp;
import cl.femase.gestionweb.business.TurnoRotativoBp;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.vo.CentroCostoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.TurnoCentroCostoVO;
import cl.femase.gestionweb.vo.TurnoRotativoVO;
import cl.femase.gestionweb.vo.UsuarioVO;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;


@WebServlet(name = "AsignacionTurnosRotativosCencosServlet", urlPatterns = {"/servlet/AsignacionTurnosRotativosCencosServlet"})
public class AsignacionTurnosRotativosCencosServlet extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 985L;
    
    static SimpleDateFormat m_dateformat = new SimpleDateFormat("yyyy-MM-dd");
    static Locale m_localeCl = new Locale("es", "CL");
    String m_strMaxFecha = "";
    
    public AsignacionTurnosRotativosCencosServlet() {
        
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        System.out.println(WEB_NAME+"[AsignacionTurnosRotativosCencosServlet]doGet...");
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
        System.out.println(WEB_NAME+"[AsignacionTurnosRotativosCencosServlet]doPost...");
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
    * 
    * @throws javax.servlet.ServletException
    * @throws java.io.IOException
    */
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
 
        TurnoRotativoBp turnosBp      = new TurnoRotativoBp(appProperties);
        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME+"[AsignacionTurnosRotativosCencosServlet]"
                + "action is: " + request.getParameter("action"));
            String action=(String)request.getParameter("action");
            //Gson gson = new Gson();
            //response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("TUR");//turnos 
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
            
            /** filtros de busqueda */
            String empresaId= null;
            String deptoId  = null;
            String cencoId  = "-1";
            String paramCencoID = request.getParameter("cencoId");
            System.out.println(WEB_NAME+"[AsignacionTurnosRotativosCencosServlet]"
                + "token param 'cencoID'= " + paramCencoID);
            if (paramCencoID != null && paramCencoID.compareTo("-1") != 0){
                StringTokenizer tokenCenco  = new StringTokenizer(paramCencoID, "--");
                if (tokenCenco.countTokens() > 0){
                    while (tokenCenco.hasMoreTokens()){
                        empresaId   = tokenCenco.nextToken();
                        deptoId     = tokenCenco.nextToken();
                        cencoId     = tokenCenco.nextToken();
                    }
                }
            }
            
            if (action.compareTo("list_turnos") == 0) {
                String labelCenco = request.getParameter("labelCenco");
                if (labelCenco == null){
                    CentroCostoBp cencosBp = new CentroCostoBp(appProperties);
                    CentroCostoVO infocenco = cencosBp.getCentroCostoByKey(deptoId, Integer.parseInt(cencoId));
                    labelCenco = infocenco.getNombre();
                }
                System.out.println(WEB_NAME+"[AsignacionTurnosRotativosCencosServlet]"
                    + "mostrar turnos rotativos asignados a "
                    + "EmpresaId: " + empresaId
                    + ", deptoId: " + deptoId
                    + ", cencoId: " + cencoId
                    + ", labelCenco: " + labelCenco);
                try{
                    LinkedHashMap<Integer, TurnoRotativoVO> listaTurnosRotativosCenco 
                        = turnosBp.getTurnosAsignadosByCenco(empresaId, 
                            deptoId,
                            Integer.parseInt(cencoId));
                    
                    LinkedHashMap<Integer, TurnoRotativoVO> listaTurnosRotativosNoAsignadosCenco 
                        = turnosBp.getTurnosNoAsignadosByCenco(empresaId, 
                            deptoId,
                            Integer.parseInt(cencoId));
                    
                    session.removeAttribute("turnosrot_noasignados_cenco");
                    session.removeAttribute("turnosrot_cenco");
                                        
                    session.setAttribute("empresaId", empresaId);
                    session.setAttribute("deptoId", deptoId);
                    session.setAttribute("cencoId", paramCencoID);
                    session.setAttribute("soloCencoId", cencoId);
                    session.setAttribute("labelCenco", labelCenco);
                    
                    session.setAttribute("turnosrot_noasignados_cenco", listaTurnosRotativosNoAsignadosCenco);
                    session.setAttribute("turnosrot_cenco", listaTurnosRotativosCenco);
                    
                    //Return Json in the format required by jTable plugin
                    request.getRequestDispatcher("/mantencion/turnos/asig_rotativos_cencos.jsp").forward(request, response);
                }catch(IOException | NumberFormatException | ServletException ex){
                    String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                    response.getWriter().print(error);
                    ex.printStackTrace();
                }   
            }else if (action.compareTo("guardar_asignacion") == 0) {
                    System.out.println(WEB_NAME+"[AsignacionTurnosRotativosCencosServlet]"
                        + "Guardar asignacion de turnos rotativos a centro de costo");
                    String[] turnosSelected = request.getParameterValues("list2");
                    String labelCenco = (String)session.getAttribute("labelCenco");
                    /**
                     * Setear lista de turnos seleccionados
                     */
                    if (turnosSelected != null){
                        ArrayList<TurnoCentroCostoVO> listaTurnos = new ArrayList<>();
                        for (int x = 0; x < turnosSelected.length; x++){
                            String idTurnoSelected = turnosSelected[x];
                            
                            TurnoCentroCostoVO asignacionTurno = new TurnoCentroCostoVO();
                            asignacionTurno.setTurnoId(Integer.parseInt(idTurnoSelected));
                            asignacionTurno.setEmpresaId(empresaId);
                            asignacionTurno.setDeptoId(deptoId);
                            asignacionTurno.setCencoId(Integer.parseInt(cencoId));
                            asignacionTurno.setCencoNombre(labelCenco);
                            
                            listaTurnos.add(asignacionTurno);
                        }
                        session.setAttribute("asignacion_turnos_cencos|" + userConnected.getUsername(), listaTurnos);
                    
                        turnosBp.eliminarAsignacionesCencos(listaTurnos);
                        turnosBp.insertarAsignacionesCencos(listaTurnos);
                    }
                    
                    
                    String downloadFile = exportResultadosToCSV(request, response);
                    session.setAttribute("downloadFileTurnosCencos|" + userConnected.getUsername(), downloadFile);
                    request.getRequestDispatcher("/mantencion/turnos/rotativos_cencos_resultado.jsp").forward(request, response);
                    
                    
            }
        }
    }
    
   
    
    protected String exportResultadosToCSV(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        String filePath="";
        PrintWriter outfile=null;
        try {
            Calendar calNow=Calendar.getInstance();
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            HttpSession session = request.getSession(true);
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
            TurnoRotativoBp turnosBp = new TurnoRotativoBp(appProperties);
            
            String separatorFields = ";";
            ArrayList<TurnoCentroCostoVO> asignaciones = (ArrayList<TurnoCentroCostoVO>)
                session.getAttribute("asignacion_turnos_cencos"+ "|" + userConnected.getUsername());
           
            if (asignaciones != null && asignaciones.size() > 0){
                filePath = appProperties.getPathExportedFiles()+
                    File.separator+
                    userConnected.getUsername()+"_asignacion_turnos_rotativos.csv";
                System.out.println(WEB_NAME+"[AsignacionTurnosRotativosCencosServlet."
                    + "exportResultadosToCSV]filePath:" + filePath);
                FileWriter filewriter = new FileWriter(filePath);
                outfile     = new PrintWriter(filewriter);
                Iterator<TurnoCentroCostoVO> iterador = asignaciones.listIterator();
                NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "ES"));
                Utilidades utils=new Utilidades();
                int filas=0;

                //cabecera
                outfile.println("Centro de costo;Turno");

                while( iterador.hasNext() ) {
                    filas++;
                    TurnoCentroCostoVO asignacion = iterador.next();
                    TurnoRotativoVO auxTurno = turnosBp.getTurno(asignacion.getTurnoId());
                    //escribir lineas en el archivo
                    outfile.print(asignacion.getCencoNombre());
                    outfile.print(separatorFields);
                    
                    if (filas < asignaciones.size()){
                        outfile.println(auxTurno.getNombre());
                    }else{
                        outfile.print(auxTurno.getNombre());
                    }
                }

               //Flush the output to the file
               outfile.flush();

               //Close the Print Writer
               outfile.close();

               //Close the File Writer
               filewriter.close();
            }
        } finally {
            if (outfile!=null) outfile.close();
        }

        return filePath;
    }
}
