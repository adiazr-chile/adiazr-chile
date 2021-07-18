/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.servlet.reportes;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.CentroCostoBp;
import cl.femase.gestionweb.business.DetalleAsistenciaBp;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.MaintenanceEventsBp;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.ExcelReportWriter;
import cl.femase.gestionweb.vo.DetalleAsistenciaVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.FiltroBusquedaJsonVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.ReportDetailHeaderVO;
import cl.femase.gestionweb.vo.ReportDetailVO;
import cl.femase.gestionweb.vo.ReportHeaderVO;
import cl.femase.gestionweb.vo.ReportVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import com.google.gson.Gson;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Alexander
 */
@WebServlet(name = "ReporteAsistenciaCert", urlPatterns = {"/ReporteAsistenciaCert"})
public class ReporteAsistencia extends BaseServlet {

    static String REPORT_NAME_CSV = "_reporte_asistencia.csv";
    static String REPORT_NAME_XLS = "_reporte_asistencia.xls";
    static String REPORT_NAME_XML = "_reporte_asistencia.xml";
    static String REPORT_LABEL = "Reporte de Asistencia";
    ExcelReportWriter excelReportWriter = new ExcelReportWriter();
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(true);
        if (session!=null) session.removeAttribute("mensaje");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        
        int intCencoId = -1;
        String paramEmpresa = "";
        String paramDepto   = "";
        String strCencoId   = "";
        String paramCencoID = request.getParameter("cencoId");
        String startDate    = request.getParameter("fechaInicioAsStr");
        String endDate      = request.getParameter("fechaFinAsStr");
        String paramTurno   = request.getParameter("turno");
        String paramTurnoRotativo   = request.getParameter("turno_rotativo");
        String paramFormato   = request.getParameter("formato");
        
        int intTurno = -1;
        if (paramTurno != null 
            && paramTurno.compareTo("-1") != 0){
                intTurno = Integer.parseInt(paramTurno);
        }
        System.out.println("[servlet.ReporteAsistencia]"
            + "token param 'cencoID'= " + paramCencoID
            +", turnoId: "+ paramTurno
            +", turnoRotativoId: "+ paramTurnoRotativo);
        
        if (paramCencoID != null && paramCencoID.compareTo("-1") != 0){
            StringTokenizer tokenCenco  = new StringTokenizer(paramCencoID, "|");
            if (tokenCenco.countTokens() > 0){
                while (tokenCenco.hasMoreTokens()){
                    paramEmpresa   = tokenCenco.nextToken();
                    paramDepto     = tokenCenco.nextToken();
                    strCencoId     = tokenCenco.nextToken();
                    intCencoId = Integer.parseInt(strCencoId);
                }
            }
        }
        System.out.println("[servlet.ReporteAsistencia]"
            + "empresa: " + paramEmpresa
            + ", depto: " + paramDepto
            + ", cenco: " + intCencoId
            + ", turno: " + paramTurno);
        ArrayList<EmpleadoVO> listaEmpleados = new ArrayList<>();
        String[] empleadosSelected = request.getParameterValues("rutEmpleado");
        if (empleadosSelected != null){
            for (int x = 0; x < empleadosSelected.length; x++){
                System.out.println("[servlet.ReporteAsistencia]"
                    + "rut seleccionado[" + x + "] = " + empleadosSelected[x]);
                EmpleadoVO auxEmpleado=new EmpleadoVO();
                auxEmpleado.setRut(empleadosSelected[x]);
                listaEmpleados.add(auxEmpleado);
            }     
        }else System.out.println("[servlet.ReporteAsistencia]"
            + "No hay empleados seleccionados");
             
        String fileName = userConnected.getUsername()+REPORT_NAME_CSV;
        String fullFilePath="";
        
        if (paramFormato.compareTo("xls") == 0){
            fileName = userConnected.getUsername() + REPORT_NAME_XLS;
            //mostrar CSV. Generar link para download del archivo xls
            fullFilePath = 
                writeXLSFile(request, paramEmpresa, 
                    paramDepto, intCencoId, 
                    startDate, endDate, intTurno, listaEmpleados);
        }else if (paramFormato.compareTo("csv") == 0){
                fullFilePath = 
                    writeCSVFile(request, paramEmpresa, 
                    paramDepto, intCencoId, 
                    startDate, endDate, intTurno, listaEmpleados);
        }else if (paramFormato.compareTo("xml") == 0){
                fileName = userConnected.getUsername() + REPORT_NAME_XML;
                fullFilePath = 
                    writeXMLFile(request, paramEmpresa, 
                        paramDepto, intCencoId, 
                        startDate, endDate, intTurno, listaEmpleados);
        }
        
        FileGeneratedVO fileGenerated = new FileGeneratedVO(fileName,fullFilePath);
        if (fileGenerated != null){
            System.out.println("[servlet.ReporteAsistencia]"
                + "Add archivo generado: " + fileGenerated.getFileName());
            showFileToDownload(fileGenerated, response);
            File auxpdf=new File(fileGenerated.getFilePath());
            auxpdf.delete();
            //agregar evento al log.
            MaintenanceEventsBp eventosBp   = new MaintenanceEventsBp(null);
            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("IAS");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
            resultado.setEmpresaId(paramEmpresa);
            resultado.setDeptoId(paramDepto);
            resultado.setCencoId(intCencoId);
            resultado.setDescription("Consulta " + REPORT_LABEL);
            eventosBp.addEvent(resultado);
        }        
    }

    /**
    * Obtiene la informacion para exportar a CSV
    * 
    * @param _request
     * @param _empresaId
     * @param _deptoId
     * @param _cencoId
     * @param _startDate
     * @param _endDate
     * @param _idTurno
     * @param _listaEmpleados
     * 
    * @return 
    * @throws javax.servlet.ServletException 
    * @throws java.io.IOException 
    */
    protected String writeCSVFile(HttpServletRequest _request,
        String _empresaId,
        String _deptoId, 
        int _cencoId,
        String _startDate,
        String _endDate,
        int _idTurno,
        ArrayList<EmpleadoVO> _listaEmpleados)
    throws ServletException, IOException {
        String filePath="";
        PrintWriter outfile=null;
        try {
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            HttpSession session = _request.getSession(true);
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
            System.out.println("[servlet.ReporteAsistencia.writeCSVFile]"
                + "empresaId: " + _empresaId
                + ", deptoId: " + _deptoId
                + ", cencoId: " + _cencoId
                + ", startDate: " + _startDate    
                + ", endDate: " + _endDate);
            CentroCostoBp cencoBp                   = new CentroCostoBp(appProperties);
            EmpleadosBp empleadoBp  = new EmpleadosBp(appProperties);
            //TurnosBp turnoBp        = new TurnosBp(appProperties);
            //TurnoRotativoBp turnoRotBp = new TurnoRotativoBp(appProperties);
                        
            DetalleAsistenciaBp detalleAsistenciaBp = new DetalleAsistenciaBp(appProperties);
            
            LinkedHashMap<String,List<DetalleAsistenciaVO>> listaDetalles = 
                detalleAsistenciaBp.getDetallesInforme(_listaEmpleados, 
                    _startDate, _endDate, _idTurno);
            String jsonOutput = cencoBp.getEmpresaDeptoCencoJson(_empresaId, _deptoId, _cencoId);
            FiltroBusquedaJsonVO labelsFiltro = 
                (FiltroBusquedaJsonVO)new Gson().fromJson(jsonOutput, FiltroBusquedaJsonVO.class);
            //int idTurnoRotativo = turnoBp.getTurnoRotativo(_empresaId);
            String separatorFields = ";";
            
            //cabeceras fijas
            filePath = appProperties.getPathExportedFiles()+
                File.separator+
                userConnected.getUsername()+ REPORT_NAME_CSV;
            FileWriter filewriter = new FileWriter(filePath);
            outfile     = new PrintWriter(filewriter);
            Calendar calNow = Calendar.getInstance(new Locale("es", "CL"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            int filas=0;

            EmpleadoVO infoEmpleado = 
                empleadoBp.getEmpleado(_empresaId, _listaEmpleados.get(0).getRut());
                    
            //cabecera del archivo
            outfile.println("Tipo reporte;"+REPORT_LABEL);
            outfile.println("Fecha reporte;" + sdf.format(calNow.getTime()));
            
            outfile.println("Rut trabajador;" + infoEmpleado.getRut());
            outfile.println("Nombre trabajador;" + infoEmpleado.getNombreCompleto());
            
            outfile.println("Empresa;" + labelsFiltro.getEmpresanombre());
            outfile.println("Departamento;" + labelsFiltro.getDeptonombre());
            outfile.println("Centro de costo;" + labelsFiltro.getCenconombre());
            outfile.println("Fecha inicio;" + _startDate);
            outfile.println("Fecha fin;" + _endDate);
            outfile.println("");
            //cabecera de los datos
            outfile.println("Fecha;"
                + "Asistencia;"    
                + "Justificaciones;");
            
            if (listaDetalles != null && !listaDetalles.isEmpty()){
                Set<String> keys = listaDetalles.keySet();
                for(String k:keys){
                    System.out.println("[servlet.ReporteAsistencia.writeCSVFile]"
                        + "key empleado: " + k);
                    if (k != null && k.compareTo("")!=0){    
                        filas = 0;
                        List<DetalleAsistenciaVO> details = listaDetalles.get(k);
                        for (DetalleAsistenciaVO detail: details) {
                            String asistencia = "NO";
                            if (detail.getHrsPresenciales() != null 
                                && detail.getHrsPresenciales().compareTo("") != 0){
                                    asistencia = "SI";
                            }
                            String justificacion="";
                            if (detail.getHhmmJustificadas() != null 
                                && detail.getHhmmJustificadas().compareTo("") != 0){
                                    justificacion = detail.getObservacion();
                            }
                            
                            //escribir lineas en el archivo
                            outfile.print(detail.getLabelFechaEntradaMarca());
                            outfile.print(separatorFields);
                           
                            outfile.print(asistencia);
                            outfile.print(separatorFields);
                            
                            if (filas < details.size()){
                                outfile.println(justificacion);
                            }else{
                                outfile.print(justificacion);
                            }
                            filas++;
                        }    
                    }
                }

            }else{
                outfile.print(Constantes.NO_HAY_INFORMACION);
                outfile.print(separatorFields);
            }
            
            //Flush the output to the file
            outfile.flush();

            //Close the Print Writer
            outfile.close();

            //Close the File Writer
            filewriter.close();
               
        } finally {
            if (outfile!=null) outfile.close();
        }

        return filePath;
    }
    
    /**
    * 
    */
    private void showFileToDownload(FileGeneratedVO _fileGenerated,
            HttpServletResponse _response){
        File auxFile = new File(_fileGenerated.getFilePath());
        int length   = 0;
        try{
            ServletOutputStream outStream = _response.getOutputStream();
            ServletContext context  = getServletConfig().getServletContext();
            String mimetype = context.getMimeType(auxFile.getName());

            // sets response content type
            if (mimetype == null) {
                mimetype = "application/octet-stream";
            }
            _response.setContentType(mimetype);
            _response.setContentLength((int)auxFile.length());

            // sets HTTP header
            _response.setHeader("Content-Disposition", "attachment; filename=\"" + _fileGenerated.getFileName() + "\"");

            byte[] byteBuffer = new byte[4096];
            DataInputStream in = new DataInputStream(new FileInputStream(auxFile));

            // reads the file's bytes and writes them to the response stream
            while ((in != null) && ((length = in.read(byteBuffer)) != -1))
            {
                outStream.write(byteBuffer,0,length);
            }

            in.close();
            outStream.close();
            
        }catch(IOException ioex){
            System.err.println("[servlet.ReporteAsistencia."
                + "showFileToDownload]Error al "
                + "abrir archivo: "+ioex.toString());
        }
    }
    
    /**
    * Genera archivo Excel con los datos obtenidos
    * 
    * @param _request
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @param _startDate
    * @param _endDate
    * @param _idTurno
    * @param _listaEmpleados
    * @return 
    * @throws javax.servlet.ServletException 
    * @throws java.io.IOException 
    */
    protected String writeXLSFile(HttpServletRequest _request,
        String _empresaId,
        String _deptoId, 
        int _cencoId,
        String _startDate,
        String _endDate,
        int _idTurno,
        ArrayList<EmpleadoVO> _listaEmpleados)
    throws ServletException, IOException {
        String excelFilePath = "";
        try {
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            HttpSession session = _request.getSession(true);
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
            System.out.println("[servlet.ReporteAsistencia.writeXLSFile]"
                + "empresaId: " + _empresaId
                + ", deptoId: " + _deptoId
                + ", cencoId: " + _cencoId
                + ", startDate: " + _startDate    
                + ", endDate: " + _endDate
                + ", idTurno: " + _idTurno);
            CentroCostoBp cencoBp   = new CentroCostoBp(appProperties);
            EmpleadosBp empleadoBp  = new EmpleadosBp(appProperties);
            //TurnosBp turnoBp        = new TurnosBp(appProperties);
            //TurnoRotativoBp turnoRotBp = new TurnoRotativoBp(appProperties);
                        
            DetalleAsistenciaBp detalleAsistenciaBp = new DetalleAsistenciaBp(appProperties);
            LinkedHashMap<String,List<DetalleAsistenciaVO>> listaDetalles = 
                detalleAsistenciaBp.getDetallesInforme(_listaEmpleados, 
                    _startDate, _endDate, _idTurno);
            String jsonOutput = cencoBp.getEmpresaDeptoCencoJson(_empresaId, _deptoId, _cencoId);
            FiltroBusquedaJsonVO labelsFiltro = 
                (FiltroBusquedaJsonVO)new Gson().fromJson(jsonOutput, FiltroBusquedaJsonVO.class);
            //int idTurnoRotativo = turnoBp.getTurnoRotativo(_empresaId);
            Calendar calNow = Calendar.getInstance(new Locale("es", "CL"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "ES"));
            //Utilidades utils=new Utilidades();
            
            EmpleadoVO infoEmpleado = 
                empleadoBp.getEmpleado(_empresaId, _listaEmpleados.get(0).getRut());
            
            excelFilePath = appProperties.getPathExportedFiles()+
                File.separator+
                userConnected.getUsername()+ REPORT_NAME_XLS;
            FileWriter filewriter = new FileWriter(excelFilePath);
            
            ReportHeaderVO header = new ReportHeaderVO();
            ReportDetailHeaderVO headersDetail = new ReportDetailHeaderVO();
            ArrayList<ReportDetailVO> detalles = new ArrayList<>();

            header.setReportLabelHeader("Tipo reporte");
            header.setReportLabel(REPORT_LABEL);
            
            header.setFechaReporteHeader("Fecha reporte");
            header.setFechaReporte(sdf.format(calNow.getTime()));
            
            header.setRutTrabajadorHeader("Rut trabajador");
            header.setRutTrabajador(infoEmpleado.getRut());
            
            header.setNombreTrabajadorHeader("Nombre trabajador");
            header.setNombreTrabajador(infoEmpleado.getNombreCompleto());
            
            header.setEmpresaHeader("Empresa");
            header.setEmpresaLabel(labelsFiltro.getEmpresanombre());
            
            header.setDeptoHeader("Departamento");
            header.setDeptoLabel(labelsFiltro.getDeptonombre());
            
            header.setCencoHeader("Centro de costo");
            header.setCencoLabel(labelsFiltro.getCenconombre());
            
            header.setFechaInicioHeader("Fecha inicio");
            header.setFechaInicio(_startDate);
            
            header.setFechaFinHeader("Fecha fin");
            header.setFechaFin(_endDate);
            
            //cabeceras del detalle
            headersDetail.setFechaHeader("Fecha");
            headersDetail.setAsistenciaHeader("Asistencia");
            headersDetail.setJustificacionesHeader("Justificaciones");
                        
            if (listaDetalles != null && !listaDetalles.isEmpty()){
                Set<String> keys = listaDetalles.keySet();
                for(String k:keys){
                    System.out.println("[servlet.ReporteAsistencia.writeXLSFile]"
                        + "key empleado: " + k);
                    if (k != null && k.compareTo("")!=0){    
                        List<DetalleAsistenciaVO> details = listaDetalles.get(k);
                        for (DetalleAsistenciaVO detail: details) {
                            String asistencia = "NO";
                            if (detail.getHrsPresenciales() != null 
                                && detail.getHrsPresenciales().compareTo("") != 0){
                                    asistencia = "SI";
                            }
                            String justificacion="";
                            if (detail.getHhmmJustificadas() != null 
                                && detail.getHhmmJustificadas().compareTo("") != 0){
                                    justificacion = detail.getObservacion();
                            }
                            
                            //****se lineas de detalle
                            ReportDetailVO detailReport = new ReportDetailVO();
                            detailReport.setFecha(detail.getLabelFechaEntradaMarca());
                            detailReport.setAsistencia(asistencia);
                            detailReport.setJustificacion(justificacion);
                            
                            detalles.add(detailReport);
                        }    
                    }
                }
            }else{
                System.out.println("[servlet."
                    + "ReporteAsistencia.writeXLSFile]"
                    + "No hay informacion de asistencia");
                ReportDetailVO detailReport = new ReportDetailVO();
                detailReport.setFecha(Constantes.SIN_DATOS);
                detailReport.setAsistencia(Constantes.SIN_DATOS);
                detailReport.setJustificacion(Constantes.SIN_DATOS);
                detalles.add(detailReport);
            }
                       
            ReportVO reportData = new ReportVO();
            reportData.setHeader(header);
            reportData.setHeadersDetail(headersDetail);
            reportData.setDetalle(detalles);
            
            excelReportWriter.writeReportInExcel(reportData, excelFilePath, "ASISTENCIA");
            
            //Close the File Writer
            filewriter.close();
               
        } finally {
            //if (outfile!=null) outfile.close();
        }

        return excelFilePath;
    }
    
    /**
    * Genera archivo XML con los datos obtenidos
    * 
    * @param _request
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @param _startDate
    * @param _endDate
    * @param _idTurno
    * @param _listaEmpleados
    * @return 
    * @throws javax.servlet.ServletException 
    * @throws java.io.IOException 
    */
    protected String writeXMLFile(HttpServletRequest _request,
        String _empresaId,
        String _deptoId, 
        int _cencoId,
        String _startDate,
        String _endDate,
        int _idTurno,
        ArrayList<EmpleadoVO> _listaEmpleados)
    throws ServletException, IOException {
        String xmlFilePath = "";
        try {
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            HttpSession session = _request.getSession(true);
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
            System.out.println("[servlet.ReporteAsistencia.writeXMLFile]"
                + "empresaId: " + _empresaId
                + ", deptoId: " + _deptoId
                + ", cencoId: " + _cencoId
                + ", startDate: " + _startDate    
                + ", endDate: " + _endDate
                + ", idTurno: " + _idTurno);
            CentroCostoBp cencoBp   = new CentroCostoBp(appProperties);
            EmpleadosBp empleadoBp  = new EmpleadosBp(appProperties);
            DetalleAsistenciaBp detalleAsistenciaBp = new DetalleAsistenciaBp(appProperties);
            LinkedHashMap<String,List<DetalleAsistenciaVO>> listaDetalles = 
                detalleAsistenciaBp.getDetallesInforme(_listaEmpleados, 
                    _startDate, _endDate, _idTurno);
            String jsonOutput = cencoBp.getEmpresaDeptoCencoJson(_empresaId, _deptoId, _cencoId);
            FiltroBusquedaJsonVO labelsFiltro = 
                (FiltroBusquedaJsonVO)new Gson().fromJson(jsonOutput, FiltroBusquedaJsonVO.class);
            Calendar calNow = Calendar.getInstance(new Locale("es", "CL"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            EmpleadoVO infoEmpleado = 
                empleadoBp.getEmpleado(_empresaId, _listaEmpleados.get(0).getRut());
            
            xmlFilePath = appProperties.getPathExportedFiles()+
                File.separator+
                userConnected.getUsername()+ REPORT_NAME_XML;
                        
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
 
            // root element
            Element root = document.createElement("report");
            document.appendChild(root);
 
            // header element
            Element header = document.createElement("header");
            
            root.appendChild(header);
             
            //set header fields
            appendChild(document, header, "tipo_reporte", REPORT_LABEL);
            appendChild(document, header, "fecha_reporte", sdf.format(calNow.getTime()));
            appendChild(document, header, "rut_trabajador", infoEmpleado.getRut());
            appendChild(document, header, "nombre_trabajador", infoEmpleado.getNombreCompleto());
            appendChild(document, header, "empresa", labelsFiltro.getEmpresanombre());
            appendChild(document, header, "departamento", labelsFiltro.getDeptonombre());
            appendChild(document, header, "centro_costo", labelsFiltro.getCenconombre());
            appendChild(document, header, "fecha_inicio", _startDate);
            appendChild(document, header, "fecha_fin", _endDate);
            
            if (listaDetalles != null && !listaDetalles.isEmpty()){
                Set<String> keys = listaDetalles.keySet();
                for(String k:keys){
                    System.out.println("[servlet.ReporteAsistencia.writeXMLFile]"
                        + "key empleado: " + k);
                    if (k != null && k.compareTo("")!=0){    
                        List<DetalleAsistenciaVO> details = listaDetalles.get(k);
                        for (DetalleAsistenciaVO detailAsistencia: details) {
                            String asistencia = "NO";
                            if (detailAsistencia.getHrsPresenciales() != null 
                                && detailAsistencia.getHrsPresenciales().compareTo("") != 0){
                                    asistencia = "SI";
                            }
                            String justificacion="";
                            if (detailAsistencia.getHhmmJustificadas() != null 
                                && detailAsistencia.getHhmmJustificadas().compareTo("") != 0){
                                    justificacion = detailAsistencia.getObservacion();
                            }
                            
                            //****se lineas de detalle
                            // employee element
                            Element detail = document.createElement("detail");
                            root.appendChild(detail);
                            appendChild(document, detail, "fecha", detailAsistencia.getLabelFechaEntradaMarca());
                            appendChild(document, detail, "asistencia", asistencia);
                            appendChild(document, detail, "justificaciones", justificacion);
                            
                        }    
                    }
                }
            }else{
                System.out.println("[servlet."
                    + "ReporteAsistencia.writeXMLFile]"
                    + Constantes.NO_HAY_INFO_ASISTENCIA);
                Element detail = document.createElement("detail");
                root.appendChild(detail);
                appendChild(document, detail, "fecha", Constantes.SIN_DATOS);
                appendChild(document, detail, "asistencia", Constantes.SIN_DATOS);
                appendChild(document, detail, "justificaciones", Constantes.SIN_DATOS);
                                            
            }
                       
            // create the xml file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(xmlFilePath));
 
            // If you use
            // StreamResult result = new StreamResult(System.out);
            // the output will be pushed to the standard output ...
            // You can use that for debugging 
 
            transformer.transform(domSource, streamResult);
            System.out.println("[servlet."
                + "ReporteAsistencia.writeXMLFile]"
                + "Done creating XML File");
            
        }catch (ParserConfigurationException | TransformerException pce) {
            pce.printStackTrace();
        } finally {
            //if (outfile!=null) outfile.close();
        }

        return xmlFilePath;
    }
    
    /**
    * 
    */
    private static void appendChild(Document _document, 
            Element _element,
            String _tagName, 
            String _data){
        
        Element theElement = _document.createElement(_tagName);
        theElement.appendChild(_document.createTextNode(_data));
        _element.appendChild(theElement);
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        if (session!=null) session.removeAttribute("mensaje");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        if (userConnected != null){
            setResponseHeaders(response);processRequest(request, response);
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                + " no valida");
            System.err.println("Sesion de usuario "+request.getParameter("username")
                + " no valida");
            request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        if (session!=null) session.removeAttribute("mensaje");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        if (userConnected != null){
            setResponseHeaders(response);processRequest(request, response);
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                + " no valida");
            System.err.println("Sesion de usuario "+request.getParameter("username")
                + " no valida");
            request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
        }
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

    private static class FileGeneratedVO {

        private final String fileName;
        private final String filePath;
        
        public FileGeneratedVO(String _fileName,String _filePath) {
            this.fileName = _fileName;
            this.filePath = _filePath;
        }

        public String getFileName() {
            return fileName;
        }

        public String getFilePath() {
            return filePath;
        }
   
    }
}
