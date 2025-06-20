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
import cl.femase.gestionweb.business.TurnoRotativoBp;
import cl.femase.gestionweb.business.TurnosBp;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.ExcelReportWriter;
import cl.femase.gestionweb.common.UtilZipFiles;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.common.freemarker.PdfGenerator;
import cl.femase.gestionweb.vo.DetalleAsistenciaVO;
import cl.femase.gestionweb.vo.DetalleTurnoVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.FiltroBusquedaJsonVO;
import cl.femase.gestionweb.vo.FreemarkerTemplateVO;
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
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
@WebServlet(name = "ReporteHorasExtras", urlPatterns = {"/ReporteHorasExtras"})
public class ReporteHorasExtras extends BaseServlet {

    static String REPORT_NAME_CSV = "_reporte_exceso_jornada.csv";
    static String REPORT_NAME_XLS = "_reporte_exceso_jornada.xls";
    static String REPORT_NAME_XML = "_reporte_exceso_jornada.xml";
    static String REPORT_NAME_PDF = "_reporte_exceso_jornada.pdf";
    static String REPORT_LABEL = "Reporte de Exceso de Jornada";
    static String ZIP_FILE_NAME = "_reporte_exceso_jornada.zip";
    
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
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        
        int intCencoId = -1;
        String paramEmpresa = "";
        String paramDepto   = "";
        String strCencoId   = "";
        String paramCencoID = request.getParameter("cencoId");
        String startDate    = request.getParameter("fechaInicioAsStr");
        String endDate      = request.getParameter("fechaFinAsStr");
        String paramFormato   = request.getParameter("formato");
        String paramTurno   = request.getParameter("turno");
        String paramTurnoRotativo   = request.getParameter("turno_rotativo");
        
        int intTurno = -1;
        if (paramTurno != null 
            && paramTurno.compareTo("-1") != 0){
                intTurno = Integer.parseInt(paramTurno);
        }
        System.out.println(WEB_NAME+"[servlet.ReporteHorasExtras]"
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
        System.out.println(WEB_NAME+"[servlet.ReporteHorasExtras]"
            + "empresa: " + paramEmpresa
            + ", depto: " + paramDepto
            + ", cenco: " + intCencoId);
        ArrayList<EmpleadoVO> listaEmpleados = new ArrayList<>();
        String[] empleadosSelected = request.getParameterValues("rutEmpleado");
        ArrayList<FileGeneratedVO> archivosGenerados = new ArrayList<>();
        if (empleadosSelected != null){
            for (int x = 0; x < empleadosSelected.length; x++){
                if (empleadosSelected[x].compareTo("-1") != 0){
                    System.out.println(WEB_NAME+"[servlet.ReporteHorasExtras]"
                        + "rut seleccionado[" + x + "] = " + empleadosSelected[x]);
                    EmpleadoVO auxEmpleado=new EmpleadoVO();
                    auxEmpleado.setRut(empleadosSelected[x]);
                    listaEmpleados.add(auxEmpleado);
                }
            }     
        }else System.out.println(WEB_NAME+"[servlet.ReporteHorasExtras]"
            + "No hay empleados seleccionados");
                    
        //***************************************************************************
        //*******************INICIO BLOQUE ****************************
        //***************************************************************************
        String fileName = userConnected.getUsername()+REPORT_NAME_CSV;
        String fullFilePath="";
        FileGeneratedVO fileGenerated;
        if (paramFormato.compareTo("xls") == 0){
            fileName = userConnected.getUsername() + REPORT_NAME_XLS;
            //mostrar CSV. Generar link para download del archivo xls
            for (int i = 0; i < listaEmpleados.size(); i++) {
                EmpleadoVO itEmpleado = listaEmpleados.get(i);
                System.out.println(WEB_NAME+"[servlet.ReporteHorasExtras]"
                    + "Generar XLS para el RUN empleado: " + itEmpleado.getRut());
                String outputFilePath = appProperties.getPathExportedFiles()+
                    File.separator+
                    itEmpleado.getRut() + REPORT_NAME_XLS;
                writeXLSFile(request, paramEmpresa, 
                    paramDepto, intCencoId, 
                    startDate, endDate, 
                    intTurno, itEmpleado, 
                    outputFilePath);
                //---- Agrega archivo generado ----------
                fileGenerated = new FileGeneratedVO("", outputFilePath);
                archivosGenerados.add(fileGenerated);
                //---------------------------------------
            }
            
        }else if (paramFormato.compareTo("csv") == 0){
            //mostrar CSV. Generar link para download del archivo csv
            for (int i = 0; i < listaEmpleados.size(); i++) {
                EmpleadoVO itEmpleado = listaEmpleados.get(i);
                System.out.println(WEB_NAME+"[servlet.ReporteHorasExtras]"
                    + "Generar CSV para el RUN empleado: " + itEmpleado.getRut());    
                String outputFilePath = appProperties.getPathExportedFiles()+
                    File.separator+
                    itEmpleado.getRut() + REPORT_NAME_CSV;
                writeCSVFile(request, paramEmpresa, 
                    paramDepto, intCencoId, 
                    startDate, endDate, intTurno, 
                    itEmpleado, outputFilePath);
                //---- Agrega archivo generado ----------
                fileGenerated = new FileGeneratedVO("", outputFilePath);
                archivosGenerados.add(fileGenerated);
                //---------------------------------------
            }
        }else if (paramFormato.compareTo("xml") == 0){
            fileName = userConnected.getUsername() + REPORT_NAME_XML;
            //mostrar CSV. Generar link para download del archivo xls
            for (int i = 0; i < listaEmpleados.size(); i++) {
                EmpleadoVO itEmpleado = listaEmpleados.get(i);
                System.out.println(WEB_NAME+"[servlet.ReporteHorasExtras]"
                    + "Generar XML para el RUN empleado: " + itEmpleado.getRut());
                String outputFilePath = appProperties.getPathExportedFiles()+
                    File.separator+
                    itEmpleado.getRut() + REPORT_NAME_XML;
                writeXMLFile(request, paramEmpresa, 
                    paramDepto, intCencoId, 
                    startDate, endDate, intTurno, 
                    itEmpleado, outputFilePath);
                //---- Agrega archivo generado ----------
                fileGenerated = new FileGeneratedVO("", outputFilePath);
                archivosGenerados.add(fileGenerated);
                //---------------------------------------
            }
        }else if (paramFormato.compareTo("pdf") == 0){
            //mostrar PDF. Generar link para download del archivo PDF
            fileName = userConnected.getUsername() + REPORT_NAME_PDF;
            for (int i = 0; i < listaEmpleados.size(); i++) {
                EmpleadoVO itEmpleado = listaEmpleados.get(i);
                System.out.println(WEB_NAME+"[servlet.ReporteHorasExtras]"
                    + "Generar PDF para el RUN empleado: " + itEmpleado.getRut());
                fullFilePath = 
                    writePDFFile(request, paramEmpresa, 
                        paramDepto, intCencoId, 
                        startDate, endDate, intTurno, itEmpleado);
                //---- Agrega archivo generado ----------
                fileGenerated = new FileGeneratedVO(fileName, fullFilePath);
                archivosGenerados.add(fileGenerated);
                //---------------------------------------
            }
        }
        
        if (!archivosGenerados.isEmpty()){
            //UtilZipFiles utilZip = new UtilZipFiles();
            List<File> fileList = new ArrayList<>();
            for (int i = 0;i < archivosGenerados.size(); i++) {
                FileGeneratedVO archivo = archivosGenerados.get(i);
                System.out.println(WEB_NAME 
                    + "[servlet.ReporteHorasExtras]add to ZIP: " 
                    + " filename: " + archivo.getFileName()
                    + ", filePath: " + archivo.getFilePath());
                File newfile = new File(archivo.getFilePath());
                fileList.add(newfile);
            }
            System.out.println(WEB_NAME 
                + "[servlet.ReporteHorasExtras]"
                + "Comprimir archivos generados...");
            //
            String outputZipFile = appProperties.getPathExportedFiles() +
                File.separator + userConnected.getUsername() + ZIP_FILE_NAME;
            UtilZipFiles.compressFiles(fileList, outputZipFile);
            
            System.out.println(WEB_NAME 
                + "[servlet.ReporteHorasExtras]"
                + "Eliminar archivos generados...");
            UtilZipFiles.deleteFiles(fileList);
            
            fileGenerated = new FileGeneratedVO(userConnected.getUsername() + ZIP_FILE_NAME, outputZipFile);
        }else {
            fileGenerated = new FileGeneratedVO(fileName, fullFilePath);
        }
        //***************************************************************************
        //******************* FIN BLOQUE ****************************
        //***************************************************************************
        
        if (fileGenerated != null){
            System.out.println(WEB_NAME+"[servlet.ReporteHorasExtras]"
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
            resultado.setType("IHE");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
            resultado.setEmpresaId(paramEmpresa);
            resultado.setDeptoId(paramDepto);
            resultado.setCencoId(intCencoId);
            resultado.setDescription("Consulta informe de exceso de jornada.");
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
    * @param _empleado
    * @param _outputFilePath
    * @throws jakarta.servlet.ServletException 
    * @throws java.io.IOException 
    */
    protected void writeCSVFile(HttpServletRequest _request,
        String _empresaId,
        String _deptoId, 
        int _cencoId,
        String _startDate,
        String _endDate,
        int _idTurno,
        EmpleadoVO _empleado, 
        String _outputFilePath)
    throws ServletException, IOException {
        PrintWriter outfile=null;
        try {
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            HttpSession session = _request.getSession(true);
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
            System.out.println(WEB_NAME+"[servlet.ReporteHorasExtras.setInfoToCSV]"
                + "empresaId: " + _empresaId
                + ", deptoId: " + _deptoId
                + ", cencoId: " + _cencoId
                + ", startDate: " + _startDate    
                + ", endDate: " + _endDate
                + ", idTurno: " + _idTurno);
            CentroCostoBp cencoBp                   = new CentroCostoBp(appProperties);
            EmpleadosBp empleadoBp  = new EmpleadosBp(appProperties);
            TurnosBp turnoBp        = new TurnosBp(appProperties);
            TurnoRotativoBp turnoRotBp = new TurnoRotativoBp(appProperties);
            
            DetalleAsistenciaBp detalleAsistenciaBp = new DetalleAsistenciaBp(appProperties);
            
            List<EmpleadoVO> empleadosList = new ArrayList<>();
            empleadosList.add(_empleado);
            
            LinkedHashMap<String,List<DetalleAsistenciaVO>> listaDetalles = 
                detalleAsistenciaBp.getDetallesInforme(empleadosList, 
                    _startDate, _endDate, _idTurno);
            String jsonOutput = cencoBp.getEmpresaDeptoCencoJson(_empresaId, _deptoId, _cencoId);
            FiltroBusquedaJsonVO labelsFiltro = 
                (FiltroBusquedaJsonVO)new Gson().fromJson(jsonOutput, FiltroBusquedaJsonVO.class);
            int idTurnoRotativo = turnoBp.getTurnoRotativo(_empresaId);
            String separatorFields = ";";
            
            FileWriter filewriter = new FileWriter(_outputFilePath);
            outfile     = new PrintWriter(filewriter);
            Calendar calNow = Calendar.getInstance(new Locale("es", "CL"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "ES"));
            Utilidades utils=new Utilidades();
            int filas=0;

            EmpleadoVO infoEmpleado = 
                empleadoBp.getEmpleado(_empresaId, _empleado.getRut());
                    
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
                + "Turno;"    
                + "Hora entrada;"
                + "Hora salida;"
                + "Presencia;"    
                + "Horas extras");
            
            if (listaDetalles != null && !listaDetalles.isEmpty()){
                Set<String> keys = listaDetalles.keySet();
                for(String k:keys){
                    System.out.println(WEB_NAME+"[servlet.ReporteHorasExtras.setInfoToCSV]"
                        + "key empleado: " + k);
                    if (k != null && k.compareTo("")!=0){    
                        filas = 0;
                        List<DetalleAsistenciaVO> details = listaDetalles.get(k);
                        for (DetalleAsistenciaVO detail: details) {
//                            EmpleadoVO infoEmpleado = 
//                                empleadoBp.getEmpleado(_empresaId, detail.getRut());
                            String labelTurno="";
                            if (idTurnoRotativo == infoEmpleado.getIdTurno()){
                                System.out.println(WEB_NAME+"[servlet."
                                    + "ReporteHorasExtras.setInfoToCSV]"
                                    + "Empleado.rut: " + infoEmpleado.getRut()
                                    + ", nombres: " + infoEmpleado.getNombres()
                                    + ", Tiene turno rotativo");
                                DetalleTurnoVO detalleTurnoRotativo = 
                                    turnoRotBp.getAsignacionTurnoByFecha(_empresaId, 
                                    detail.getRut(), detail.getFechaEntradaMarca());
                                if (detalleTurnoRotativo != null){
                                    labelTurno = "(" + detalleTurnoRotativo.getIdTurno()+") "+
                                        detalleTurnoRotativo.getNombreTurno();
                                }else labelTurno = "Sin turno";
                            }else{
                                labelTurno = "(" + infoEmpleado.getIdTurno()+") "+
                                    infoEmpleado.getNombreTurno();
                            }
                                
                            //escribir lineas en el archivo
                            outfile.print(detail.getLabelFechaEntradaMarca());
                            outfile.print(separatorFields);
                            outfile.print(labelTurno);
                            outfile.print(separatorFields);
                            outfile.print(detail.getHoraEntrada());//hora entrada real
                            outfile.print(separatorFields);
                            outfile.print(detail.getHoraSalida());//hora salida real
                            outfile.print(separatorFields);
                            
                            outfile.print(detail.getHrsPresenciales());
                            outfile.print(separatorFields);
                            
                            if (filas < details.size()){
                                outfile.println(detail.getHoraMinsExtras());
                            }else{
                                outfile.print(detail.getHoraMinsExtras());
                            }
                            filas++;
                        }    
                    }
                }

            }else{
                outfile.print(Constantes.NO_HAY_INFO_EXCESO_JORNADA);
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
    * @param _empleado
    * @param _outputFilePath 
    * 
    * @throws jakarta.servlet.ServletException 
    * @throws java.io.IOException 
    */
    protected void writeXLSFile(HttpServletRequest _request,
        String _empresaId,
        String _deptoId, 
        int _cencoId,
        String _startDate,
        String _endDate,
        int _idTurno,
        EmpleadoVO _empleado,
        String _outputFilePath)
    throws ServletException, IOException {
        String excelFilePath = "";
        try {
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            HttpSession session = _request.getSession(true);
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
            System.out.println(WEB_NAME+"[servlet.ReporteHorasExtras.writeXLSFile]"
                + "empresaId: " + _empresaId
                + ", deptoId: " + _deptoId
                + ", cencoId: " + _cencoId
                + ", startDate: " + _startDate    
                + ", endDate: " + _endDate
                + ", idTurno: " + _idTurno);
            CentroCostoBp cencoBp   = new CentroCostoBp(appProperties);
            EmpleadosBp empleadoBp  = new EmpleadosBp(appProperties);
            TurnosBp turnoBp        = new TurnosBp(appProperties);
            TurnoRotativoBp turnoRotBp = new TurnoRotativoBp(appProperties);
                        
            DetalleAsistenciaBp detalleAsistenciaBp = new DetalleAsistenciaBp(appProperties);
            
            List<EmpleadoVO> empleadosList = new ArrayList<>();
            empleadosList.add(_empleado);
            
            LinkedHashMap<String,List<DetalleAsistenciaVO>> listaDetalles = 
                detalleAsistenciaBp.getDetallesInforme(empleadosList, 
                    _startDate, _endDate, _idTurno);
            
            String jsonOutput = cencoBp.getEmpresaDeptoCencoJson(_empresaId, _deptoId, _cencoId);
            FiltroBusquedaJsonVO labelsFiltro = 
                (FiltroBusquedaJsonVO)new Gson().fromJson(jsonOutput, FiltroBusquedaJsonVO.class);
            int idTurnoRotativo = turnoBp.getTurnoRotativo(_empresaId);
            Calendar calNow = Calendar.getInstance(new Locale("es", "CL"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "ES"));
            Utilidades utils=new Utilidades();
            
            EmpleadoVO infoEmpleado = 
                empleadoBp.getEmpleado(_empresaId, _empleado.getRut());
            
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
            headersDetail.setLabelTurnoHeader("Turno");
            headersDetail.setHoraEntradaHeader("Hora entrada");
            headersDetail.setHoraSalidaHeader("Hora salida");
            headersDetail.setPresenciaHeader("Presencia");
            headersDetail.setHorasExtrasHeader("Horas extras");
            
            if (listaDetalles != null && !listaDetalles.isEmpty()){
                Set<String> keys = listaDetalles.keySet();
                for(String k:keys){
                    System.out.println(WEB_NAME+"[servlet.ReporteHorasExtras.writeXLSFile]"
                        + "key empleado: " + k);
                    if (k != null && k.compareTo("")!=0){    
                        List<DetalleAsistenciaVO> details = listaDetalles.get(k);
                        for (DetalleAsistenciaVO detail: details) {
                            String labelTurno="";
                            if (idTurnoRotativo == infoEmpleado.getIdTurno()){
                                System.out.println(WEB_NAME+"[servlet."
                                    + "ReporteHorasExtras.writeXLSFile]"
                                    + "Empleado.rut: " + infoEmpleado.getRut()
                                    + ", nombres: " + infoEmpleado.getNombres()
                                    + ", Tiene turno rotativo");
                                DetalleTurnoVO detalleTurno = 
                                    turnoRotBp.getAsignacionTurnoByFecha(_empresaId, 
                                    detail.getRut(), detail.getFechaEntradaMarca());
                                labelTurno = "(" + detalleTurno.getIdTurno()+") "+
                                    detalleTurno.getNombreTurno();
                            }else{
                                labelTurno = "(" + infoEmpleado.getIdTurno()+") "+
                                    infoEmpleado.getNombreTurno();
                            }
                            //****se lineas de detalle
                            ReportDetailVO detailReport = new ReportDetailVO();
                            detailReport.setFecha(detail.getLabelFechaEntradaMarca());
                            detailReport.setLabelTurno(labelTurno);
                            detailReport.setHoraEntrada(detail.getHoraEntrada());
                            detailReport.setHoraSalida(detail.getHoraSalida());
                            detailReport.setPresencia(detail.getHrsPresenciales());
                            detailReport.setHorasExtras(detail.getHoraMinsExtras());
                            detalles.add(detailReport);
                        }    
                    }
                }
            }else{
                System.out.println(WEB_NAME+"[servlet."
                    + "ReporteHorasExtras.writeXLSFile]"
                    + Constantes.NO_HAY_INFO_ASISTENCIA);
                ReportDetailVO detailReport = new ReportDetailVO();
                detailReport.setFecha(Constantes.SIN_DATOS);
                detailReport.setHoraEntrada(Constantes.SIN_DATOS);
                detailReport.setHoraSalida(Constantes.SIN_DATOS);
                detalles.add(detailReport);
            }
                       
            ReportVO reportData = new ReportVO();
            reportData.setHeader(header);
            reportData.setHeadersDetail(headersDetail);
            reportData.setDetalle(detalles);
            
            excelReportWriter.writeReportInExcel(reportData, excelFilePath, "HRSEXTRAS");
            
            //Close the File Writer
            filewriter.close();
               
        } finally {
            //if (outfile!=null) outfile.close();
        }

    }
    
    /**
    * Genera archivo PDF con los datos obtenidos
    * 
    * @param _request
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @param _startDate
    * @param _endDate
    * @param _idTurno
    * @param _empleado
    * @return 
    * @throws jakarta.servlet.ServletException 
    * @throws java.io.IOException 
    */
    protected String writePDFFile(HttpServletRequest _request,
        String _empresaId,
        String _deptoId, 
        int _cencoId,
        String _startDate,
        String _endDate,
        int _idTurno,
        EmpleadoVO _empleado)
    throws ServletException, IOException {
        
        String outputFilePath = "";
        
        try {
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            HttpSession session = _request.getSession(true);
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
            System.out.println(WEB_NAME+"[servlet.ReporteHorasExtras.writePDFFile]"
                + "empresaId: " + _empresaId
                + ", deptoId: " + _deptoId
                + ", cencoId: " + _cencoId
                + ", startDate: " + _startDate    
                + ", endDate: " + _endDate
                + ", idTurno: " + _idTurno);
            CentroCostoBp cencoBp   = new CentroCostoBp(appProperties);
            EmpleadosBp empleadoBp  = new EmpleadosBp(appProperties);
            TurnosBp turnoBp        = new TurnosBp(appProperties);
            TurnoRotativoBp turnoRotBp = new TurnoRotativoBp(appProperties);
                        
            DetalleAsistenciaBp detalleAsistenciaBp = new DetalleAsistenciaBp(appProperties);
            
            List<EmpleadoVO> empleadosList = new ArrayList<>();
            empleadosList.add(_empleado);
            
            LinkedHashMap<String,List<DetalleAsistenciaVO>> listaDetalles = 
                detalleAsistenciaBp.getDetallesInforme(empleadosList, 
                    _startDate, _endDate, _idTurno);
            String jsonOutput = cencoBp.getEmpresaDeptoCencoJson(_empresaId, _deptoId, _cencoId);
            FiltroBusquedaJsonVO labelsFiltro = 
                (FiltroBusquedaJsonVO)new Gson().fromJson(jsonOutput, FiltroBusquedaJsonVO.class);
            int idTurnoRotativo = turnoBp.getTurnoRotativo(_empresaId);
            Calendar calNow = Calendar.getInstance(new Locale("es", "CL"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "ES"));
            Utilidades utils=new Utilidades();
            
            EmpleadoVO infoEmpleado = 
                empleadoBp.getEmpleado(_empresaId, _empleado.getRut());
            
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
            headersDetail.setLabelTurnoHeader("Turno");
            headersDetail.setHoraEntradaHeader("Hora entrada");
            headersDetail.setHoraSalidaHeader("Hora salida");
            headersDetail.setPresenciaHeader("Presencia");
            headersDetail.setHorasExtrasHeader("Horas extras");
            
            if (listaDetalles != null && !listaDetalles.isEmpty()){
                Set<String> keys = listaDetalles.keySet();
                for(String k:keys){
                    System.out.println(WEB_NAME+"[servlet.ReporteHorasExtras.writePDFFile]"
                        + "key empleado: " + k);
                    if (k != null && k.compareTo("")!=0){    
                        List<DetalleAsistenciaVO> details = listaDetalles.get(k);
                        for (DetalleAsistenciaVO detail: details) {
                            String labelTurno="";
                            if (idTurnoRotativo == infoEmpleado.getIdTurno()){
                                System.out.println(WEB_NAME+"[servlet."
                                    + "ReporteHorasExtras.writePDFFile]"
                                    + "Empleado.rut: " + infoEmpleado.getRut()
                                    + ", nombres: " + infoEmpleado.getNombres()
                                    + ", Tiene turno rotativo");
                                DetalleTurnoVO detalleTurno = 
                                    turnoRotBp.getAsignacionTurnoByFecha(_empresaId, 
                                    detail.getRut(), detail.getFechaEntradaMarca());
                                labelTurno = "(" + detalleTurno.getIdTurno()+") "+
                                    detalleTurno.getNombreTurno();
                            }else{
                                labelTurno = "(" + infoEmpleado.getIdTurno()+") "+
                                    infoEmpleado.getNombreTurno();
                            }
                            //****se lineas de detalle
                            ReportDetailVO detailReport = new ReportDetailVO();
                            detailReport.setFecha(detail.getLabelFechaEntradaMarca());
                            detailReport.setLabelTurno(labelTurno);
                            detailReport.setHoraEntrada(detail.getHoraEntrada());
                            detailReport.setHoraSalida(detail.getHoraSalida());
                            detailReport.setPresencia(detail.getHrsPresenciales());
                            detailReport.setHorasExtras(detail.getHoraMinsExtras());
                            detalles.add(detailReport);
                        }    
                    }
                }
            }else{
                System.out.println(WEB_NAME+"[servlet."
                    + "ReporteHorasExtras.writePDFFile]set datos por defecto. "
                    + Constantes.NO_HAY_INFO_ASISTENCIA);
                ReportDetailVO detailReport = new ReportDetailVO();
                detailReport.setFecha(Constantes.SIN_DATOS);
                detailReport.setLabelTurno(Constantes.SIN_DATOS);
                detailReport.setHorasTrabajadas(Constantes.SIN_DATOS);
                detailReport.setHoraEntrada(Constantes.SIN_DATOS);
                detailReport.setHoraSalida(Constantes.SIN_DATOS);
                detailReport.setPresencia(Constantes.SIN_DATOS);
                detailReport.setHorasExtras(Constantes.SIN_DATOS);
                detalles.add(detailReport);
            }
                       
            for (int i = 0; i < detalles.size(); i++) {
                ReportDetailVO detail = detalles.get(i);
                System.out.println("[Reporte de hrs extras]Detalle.data: " + detalles.get(i).toString());
            }
            
            ReportVO reportData = new ReportVO();
            reportData.setHeader(header);
            reportData.setHeadersDetail(headersDetail);
            reportData.setDetalle(detalles);
            
            //set datos del template freemarker
            FreemarkerTemplateVO template = new FreemarkerTemplateVO();
            template.setReportTitle("Reporte de Exceso de Jornada");
            template.setReportAbrev("reporte_exceso_jornada");
            template.setReportLogo("logo_fundacion_01.png");
            template.setTemplateName("reporte_exceso_jornada.ftl");
            PdfGenerator pdfGenerator=new PdfGenerator(appProperties, template);
            outputFilePath = pdfGenerator.generateReport(reportData);
            System.out.println(WEB_NAME+"[servlet."
                + "ReporteHorasExtras.writePDFFile]"
                + "outputFilePath (PDF):" + outputFilePath);
               
        } finally {
            //if (outfile!=null) outfile.close();
        }

        return outputFilePath;
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
    * @param _empleado
    * @param _outputFilePath
    * 
    * @throws jakarta.servlet.ServletException 
    * @throws java.io.IOException 
    */
    protected void writeXMLFile(HttpServletRequest _request,
        String _empresaId,
        String _deptoId, 
        int _cencoId,
        String _startDate,
        String _endDate,
        int _idTurno,
        EmpleadoVO _empleado, 
        String _outputFilePath)
    throws ServletException, IOException {
        String xmlFilePath = "";
        try {
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            HttpSession session = _request.getSession(true);
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
            System.out.println(WEB_NAME+"[servlet.ReporteHorasExtras.writeXMLFile]"
                + "empresaId: " + _empresaId
                + ", deptoId: " + _deptoId
                + ", cencoId: " + _cencoId
                + ", startDate: " + _startDate    
                + ", endDate: " + _endDate
                + ", idTurno: " + _idTurno);
            CentroCostoBp cencoBp   = new CentroCostoBp(appProperties);
            EmpleadosBp empleadoBp  = new EmpleadosBp(appProperties);
            TurnosBp turnoBp        = new TurnosBp(appProperties);
            TurnoRotativoBp turnoRotBp = new TurnoRotativoBp(appProperties);
                        
            DetalleAsistenciaBp detalleAsistenciaBp = new DetalleAsistenciaBp(appProperties);
            
            List<EmpleadoVO> empleadosList = new ArrayList<>();
            empleadosList.add(_empleado);
            
            LinkedHashMap<String,List<DetalleAsistenciaVO>> listaDetalles = 
                detalleAsistenciaBp.getDetallesInforme(empleadosList, 
                    _startDate, _endDate, _idTurno);
            String jsonOutput = cencoBp.getEmpresaDeptoCencoJson(_empresaId, _deptoId, _cencoId);
            FiltroBusquedaJsonVO labelsFiltro = 
                (FiltroBusquedaJsonVO)new Gson().fromJson(jsonOutput, FiltroBusquedaJsonVO.class);
            int idTurnoRotativo = turnoBp.getTurnoRotativo(_empresaId);
            Calendar calNow = Calendar.getInstance(new Locale("es", "CL"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "ES"));
            Utilidades utils=new Utilidades();
            
            EmpleadoVO infoEmpleado = 
                empleadoBp.getEmpleado(_empresaId, _empleado.getRut());
            
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
                    System.out.println(WEB_NAME+"[servlet.ReporteHorasExtras.writeXMLFile]"
                        + "key empleado: " + k);
                    if (k != null && k.compareTo("")!=0){    
                        List<DetalleAsistenciaVO> details = listaDetalles.get(k);
                        for (DetalleAsistenciaVO detailAsistencia: details) {
                            String labelTurno="";
                            if (idTurnoRotativo == infoEmpleado.getIdTurno()){
                                System.out.println(WEB_NAME+"[servlet."
                                    + "ReporteHorasExtras.writeXMLFile]"
                                    + "Empleado.rut: " + infoEmpleado.getRut()
                                    + ", nombres: " + infoEmpleado.getNombres()
                                    + ", Tiene turno rotativo");
                                DetalleTurnoVO detalleTurno = 
                                    turnoRotBp.getAsignacionTurnoByFecha(_empresaId, 
                                    detailAsistencia.getRut(), detailAsistencia.getFechaEntradaMarca());
                                labelTurno = "(" + detalleTurno.getIdTurno()+") "+
                                    detalleTurno.getNombreTurno();
                            }else{
                                labelTurno = "(" + infoEmpleado.getIdTurno()+") "+
                                    infoEmpleado.getNombreTurno();
                            }
                            //****se lineas de detalle
                            // employee element
                            Element detail = document.createElement("detail");
                            root.appendChild(detail);
                            appendChild(document, detail, "fecha", detailAsistencia.getLabelFechaEntradaMarca());
                            appendChild(document, detail, "turno", labelTurno);
                            appendChild(document, detail, "hora_entrada", detailAsistencia.getHoraEntrada());
                            appendChild(document, detail, "hora_salida", detailAsistencia.getHoraSalida());
                            appendChild(document, detail, "presencia", detailAsistencia.getHrsPresenciales());
                            appendChild(document, detail, "horas_extras", detailAsistencia.getHoraMinsExtras());
            
                        }    
                    }
                }
            }else{
                System.out.println(WEB_NAME+"[servlet."
                    + "ReporteHorasExtras.writeXMLFile]"
                    + Constantes.NO_HAY_INFO_ASISTENCIA);
                Element detail = document.createElement("detail");
                root.appendChild(detail);
                appendChild(document, detail, "fecha", Constantes.SIN_DATOS);
                appendChild(document, detail, "turno", Constantes.SIN_DATOS);
                appendChild(document, detail, "hora_entrada", Constantes.SIN_DATOS);
                appendChild(document, detail, "hora_salida", Constantes.SIN_DATOS);
                appendChild(document, detail, "presencia", Constantes.SIN_DATOS);
                appendChild(document, detail, "horas_extras", Constantes.SIN_DATOS);
                            
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
            System.out.println(WEB_NAME+"[servlet."
                + "ReporteHorasExtras.writeXMLFile]"
                + "Done creating XML File");
            
        }catch (ParserConfigurationException | TransformerException pce) {
            pce.printStackTrace();
        } finally {
            //if (outfile!=null) outfile.close();
        }

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
            System.err.println("[servlet.ReporteHorasExtras."
                + "showFileToDownload]Error al "
                + "abrir archivo: "+ioex.toString());
        }
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
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
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
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
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
