package cl.femase.gestionweb.servlet.fiscalizacion;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.vo.EmpleadoConsultaFiscalizadorVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.FiltroBusquedaEmpleadosVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.StringTokenizer;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class EmpleadosBuscar extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final String TABLE_NAME     = "EmpleadosBuscar";
    private static final String SERVLET_NAME   = "[EmpleadosBuscar.servlet]";
    private static final String FORWARD_PAGE        = "/fiscalizador/main_reportes.jsp";
    private static final String ATTRIBUTTE_LIST_NAME  = "fiscaliza_lista_empleados";
    
    public EmpleadosBuscar() {
        
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
    protected void doPost(HttpServletRequest request, 
            HttpServletResponse response) 
                throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        if (userConnected != null){
            setResponseHeaders(response);
            processRequest(request, response);
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
    */
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        EmpleadosBp empleadosBp = new EmpleadosBp(appProperties);

        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME + SERVLET_NAME
                + "action is: " + request.getParameter("action"));
            
            String action=(String)request.getParameter("action");
            
//            MaintenanceEventVO resultado=new MaintenanceEventVO();
//            resultado.setUsername(userConnected.getUsername());
//            resultado.setDatetime(new Date());
//            resultado.setUserIP(request.getRemoteAddr());
//            resultado.setType("AEM");
//            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                   
            /** filtros de busqueda de empleados*/
            String nombre           = request.getParameter("nombre");
            String run              = request.getParameter("run");
            String desde            = request.getParameter("desde");
            String hasta            = request.getParameter("hasta");
            String periodo          = request.getParameter("periodo");
            
            //combos
            String centroCostoId    = request.getParameter("centroCostoId");
            String turnoId          = request.getParameter("turnoId");
            String cargoId          = request.getParameter("cargoId");
            String tipoReporte      = request.getParameter("tipoReporte");
            String formatoSalida    = request.getParameter("formatoSalida");
            
            if (turnoId == null) turnoId = "-1";
            if (cargoId == null) cargoId = "-1";
            if (tipoReporte == null) tipoReporte = "-1";
            
            System.out.println(WEB_NAME + SERVLET_NAME + " - " + TABLE_NAME
                + ". Parametros de busqueda recibidos: "
                + "Nombre: " + nombre
                + " , Run: " + run
                + " , desde: " + desde
                + " , hasta: " + hasta
                + " , centroCostoId: " + centroCostoId
                + " , turnoId: " + turnoId
                + " , cargoId: " + cargoId
                + " , tipoReporte: " + tipoReporte
                + " , formatoSalida: " + formatoSalida);
            
            String empresaid=userConnected.getEmpresaId();
            String deptoid=null;
            int cencoid=-1;
            
            System.out.println(WEB_NAME + SERVLET_NAME + "[processRequest]"
                + "token param 'cencoID'= " + centroCostoId);
            if (centroCostoId != null && centroCostoId.compareTo("-1") != 0){
                StringTokenizer tokenCenco  = new StringTokenizer(centroCostoId, "|");
                if (tokenCenco.countTokens() > 0){
                    while (tokenCenco.hasMoreTokens()){
                        empresaid   = tokenCenco.nextToken();
                        deptoid     = tokenCenco.nextToken();
                        cencoid     = Integer.parseInt(tokenCenco.nextToken());
                    }
                }
            }
            
            FiltroBusquedaEmpleadosVO filtroVO = new FiltroBusquedaEmpleadosVO();
            filtroVO.setTipoReporte(tipoReporte);
            filtroVO.setEmpresaId(empresaid);
            filtroVO.setCencoId(cencoid);
            filtroVO.setRunEmpleado(run);
            filtroVO.setNombre(nombre);
            filtroVO.setTurnoId(Integer.parseInt(turnoId));
            filtroVO.setCargoId(Integer.parseInt(cargoId));
            filtroVO.setDesde(desde);
            filtroVO.setHasta(hasta);
            filtroVO.setFormatoSalida(formatoSalida);

            //Set filtros de busqueda seleccionados
            request.removeAttribute("fiscaliza_filtro");
            request.setAttribute("fiscaliza_filtro", filtroVO);
           
            if (action.compareTo("list") == 0) {
                System.out.println(WEB_NAME + SERVLET_NAME + " - " + TABLE_NAME
                    + ". Mostrar registros");
                session.removeAttribute(ATTRIBUTTE_LIST_NAME);
                forwardToPage(request, response, session, empleadosBp, filtroVO);   
            }else if (action.compareTo("launchReport") == 0) {
                
//                String reportType   = request.getParameter("tipo_reporte");
//                String startDate    = request.getParameter("fecha_inicio");
//                String endDate      = request.getParameter("fecha_fin");
//                String outputFormat      = request.getParameter("formato_salida");
                
                BufferedReader reader = request.getReader();
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                String jsonString = sb.toString();
                
                JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

                // Obtener campos individuales
                System.out.println("Fecha inicio: " + jsonObject.get("fecha_inicio").getAsString());
                System.out.println("Fecha fin: " + jsonObject.get("fecha_fin").getAsString());
                System.out.println("Tipo de reporte: " + jsonObject.get("tipo_reporte").getAsString());
                System.out.println("Formato de salida: " + jsonObject.get("formato_salida").getAsString());

                String startDate   = jsonObject.get("fecha_inicio").getAsString();
                String endDate    = jsonObject.get("fecha_fin").getAsString();
                String reportType      = jsonObject.get("tipo_reporte").getAsString();
                String outputFormat = jsonObject.get("formato_salida").getAsString();
                
                ArrayList<String> runSeleccionados = new ArrayList<>();
                // Obtener arreglo de empleados (checkboxes)
                // Para arrays, obtener JsonArray y luego iterar o convertir a lista
                var empleadosJsonArray = jsonObject.getAsJsonArray("empleados");
                System.out.print("Empleados: ");
                for (var elem : empleadosJsonArray) {
                    //System.out.print(elem.getAsString() + " ");
                    runSeleccionados.add(elem.getAsString());
                }

                //String[] runSeleccionados = request.getParameterValues("empleadosSeleccionados");
                
                System.out.println(WEB_NAME + SERVLET_NAME + " - " + TABLE_NAME
                    + ". Generar reporte. "
                    + "tipo: " + reportType
                    + ", desde: " + startDate
                    + ", hasta: " + endDate
                    + ", outputFormat: " + outputFormat);
                String zipFileGenerated = 
                    generaReportes(empleadosBp, appProperties, reportType, outputFormat, empresaid, startDate, endDate, runSeleccionados);
                System.out.println(WEB_NAME + SERVLET_NAME + " - " + TABLE_NAME
                    + ". Zip generado con los reportes: " + zipFileGenerated);
                File zipFile = new File(zipFileGenerated);

                response.setContentType("application/zip");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + zipFile.getName() + "\"");
                response.setContentLength((int) zipFile.length());

                try (FileInputStream fis = new FileInputStream(zipFile);
                     ServletOutputStream os = response.getOutputStream()) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                    os.flush();
                }
                
            }
        }
    }
    
    /**
    * 
    * 
    */
    private String generaReportes(EmpleadosBp _empleadosBp,
            PropertiesVO _appProperties,
            String _reportType, 
            String _formatoSalida,
            String _empresaId,
            String _startDate, 
            String _endDate,
            ArrayList<String> _runSeleccionados){
        String zipfilePath = "";
        List<Map<String, Object>> parametros = new ArrayList<>();
        
         for (String runEmpleado : _runSeleccionados) {
            
            EmpleadoVO infoEmpleado = _empleadosBp.getEmpleadoByEmpresaRun(_empresaId, runEmpleado);
            
            System.out.println(WEB_NAME + SERVLET_NAME 
                + " - " + TABLE_NAME + ". Run seleccionado para reporte: " + runEmpleado);
            Map<String, Object> params1 = new HashMap<>();
            params1.put("RUT_EMPLEADOR", infoEmpleado.getEmpresaRut());
            params1.put("RAZON_SOCIAL_EMPLEADOR", infoEmpleado.getEmpresaNombre());
            params1.put("RUT_TRABAJADOR", runEmpleado);
            params1.put("NOMBRE_TRABAJADOR", infoEmpleado.getNombres());
            params1.put("CENCO", infoEmpleado.getCencoNombre());
            params1.put("EMPRESA_ID", _empresaId);
            params1.put("DESDE", _startDate);
            params1.put("HASTA", _endDate);
            params1.put("CARGO", infoEmpleado.getNombreCargo());
            params1.put("TURNO", infoEmpleado.getNombreTurno());
            
            parametros.add(params1);
        
        }//fin iteracion de todos los empleados seleccionados para el reporte
        System.out.println(WEB_NAME + SERVLET_NAME + " - " + TABLE_NAME + ". Report type: " + _reportType);
         
        System.out.println(WEB_NAME + SERVLET_NAME 
            + " - " + TABLE_NAME + ". Reporte Jasper: " + Constantes.REPORT_TYPE.get(_reportType).getJasperFileName());
        try{
            GeneradorReportes generador = 
                new GeneradorReportes(_appProperties, Constantes.REPORT_TYPE.get(_reportType).getJasperFileName());
            //generador.generarReporte(parametros, "asistencia", "PDF");
            //generador.generarReporte(parametros, "asistencia", "XLSX");
            zipfilePath = generador.generarReporte(parametros, _reportType, _formatoSalida);
            //generador.generarReporte(parametros, "asistencia", "RTF");
        }catch(Exception ex){
            System.err.println(WEB_NAME + SERVLET_NAME + "--Error_1: " + ex.toString());
            ex.printStackTrace();
        }
        
        return zipfilePath;
    }
    
    /**
    * 
    */
    private void forwardToPage(HttpServletRequest _request,
        HttpServletResponse _response,
        HttpSession _session,
        EmpleadosBp _empleadoBp,
        FiltroBusquedaEmpleadosVO _filtroBusqueda){
    
        try {
            List<EmpleadoConsultaFiscalizadorVO> listaObjetos = new ArrayList<>();
            //if (_filtroBusqueda.getCencoId() == -1){
            //    System.out.println("--------->Eliminar lista de empleados de la sesion<---------");
            //    _session.removeAttribute(ATTRIBUTTE_LIST_NAME);
            //    listaObjetos = null;
            //}else{
                listaObjetos = _empleadoBp.getEmpleadosFiscalizacion(_filtroBusqueda);
                
            //}
            System.out.println(WEB_NAME + SERVLET_NAME + "-" + TABLE_NAME + " - "
                + "empleados.encontrados.size(): " + listaObjetos.size());
            _session.setAttribute(ATTRIBUTTE_LIST_NAME, listaObjetos);
            RequestDispatcher vista = _request.getRequestDispatcher(FORWARD_PAGE);
            vista.forward(_request, _response);
        } catch (ServletException ex) {
            System.err.println(WEB_NAME + SERVLET_NAME + "-" + TABLE_NAME + " - "
                + "Error_1: " + ex.toString());
            ex.printStackTrace();
        } catch (IOException ex) {
            System.err.println(WEB_NAME + SERVLET_NAME + "-" + TABLE_NAME + " - "
                + "Error_2: " + ex.toString());
        }
        
    }
    
}
