/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.servlet.reportes;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.CentroCostoBp;
import cl.femase.gestionweb.business.DepartamentoBp;
import cl.femase.gestionweb.business.DetalleAsistenciaBp;
import cl.femase.gestionweb.business.DetalleTurnosBp;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.EmpresaBp;
import cl.femase.gestionweb.business.MaintenanceEventsBp;
import cl.femase.gestionweb.business.TurnoRotativoBp;
import cl.femase.gestionweb.business.TurnosBp;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.common.DatabaseLocator;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.vo.AsistenciaTotalesVO;
import cl.femase.gestionweb.vo.CentroCostoVO;
import cl.femase.gestionweb.vo.DepartamentoVO;
import cl.femase.gestionweb.vo.DetalleAsistenciaVO;
import cl.femase.gestionweb.vo.DetalleTurnoVO;
import cl.femase.gestionweb.vo.DiferenciaHorasVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.EmpresaVO;
import cl.femase.gestionweb.vo.FiltroBusquedaJsonVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.TotalesSemanaVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import com.google.gson.Gson;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRCsvDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
       
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
 
/**
 * Permite generar reportes de asistencia para uno o mas empleados.
 * Si se selecciona un solo empleado, se genera un PDF.
 * Si se seleccionan TODOS los empleados de un centro de costo,
 * se genera un PDF por cada empleado y luego se unen (metodo 'mergePdfFiles')
 * todos los PDF en uno solo. El PDF resultante tendra el nombre del cenco seleccionado.
 * 
 *
 * @author Alexander
 */
public class ReporteAsistenciaSemanal extends BaseServlet {

    
    EmpleadosBp m_empleadosBp               = new EmpleadosBp(null);
    DatabaseLocator m_dbLocator;
    String m_dbpoolName;
    Connection m_databaseConnection         = null;
    
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    //private String DOWNLOAD_FILE_NAME="asistencia.pdf";
    LinkedHashMap<String, String> hashPdfFilesPaths=new LinkedHashMap<>();
   
    List<DetalleAsistenciaVO> m_registros;
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @param _empleado
     * @param _detalleAsistencia
     * 
     * @return 
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected FileGeneratedVO processRequestRut(HttpServletRequest request, 
                HttpServletResponse response, 
                EmpleadoVO _empleado, 
                List<DetalleAsistenciaVO> _detalleAsistencia)
            throws ServletException, IOException {
        
        HttpSession session         = request.getSession(true);
        ServletContext application  = this.getServletContext();
        PropertiesVO appProperties  = (PropertiesVO)application.getAttribute("appProperties");
        String tipoParam = request.getParameter("tipo");
        String formato = request.getParameter("formato");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        FileGeneratedVO fileGenerated = null;
        System.out.println(WEB_NAME+"[servlet.reportes."
            + "ReporteAsistenciaSemanal.processRequestRut]"
            + "tipoParam: "+tipoParam
            + ", usuario conectado: "+userConnected.getUsername());
        if (_empleado == null) return null;
        String jasperFilename   = "asistencia_semanal.jasper";
        String jasperFullPath   = appProperties.getReportesPath() + File.separator + jasperFilename;
        String outputFileName      = "asistencia_semanal_" + _empleado.getRut() + ".pdf";
        if (formato.compareTo("xls") == 0){
            outputFileName      = "asistencia_semanal_" + _empleado.getRut() + ".xlsx";
        }
        
        String outputFullPath      = appProperties.getPathExportedFiles() + File.separator + outputFileName;
        
        System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
            + "processRequestRut]"
            + "Iterar "
            + "empresaId: " + _empleado.getEmpresaId()
            + ", empleadoRut: " + _empleado.getRut());
        Map parameters = getParameters(request, _empleado, _detalleAsistencia);
            
        if (parameters != null && !m_registros.isEmpty()){
                System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.processRequestRut]Generar archivo...");
                try{
                    String csvFile = appProperties.getPathExportedFiles()+
                        File.separator+
                        userConnected.getUsername()+"_asistencia_semanal.csv";
                    String[] columnNames = 
                        new String[] {"Fecha",
                            "Hora_Entrada",
                            "Hora_Salida",
                            "Entrada_comentario",
                            "Salida_comentario",
                            "Entrada_teorica",
                            "Salida_teorica",
                            "Horas_presenciales",
                            "Hhmm_atraso",
                            "Hhmm_justificadas",
                            "Hhmm_extras",
                            "Hhmm_ausencia",
                            "Hhmm_trabajadas",
                            "Observacion",
                            "Hhmm_no_trabajadas"};
                    getCSVFile(_empleado, 
                        csvFile, columnNames, m_registros);
                    
                    System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                        + "processRequestRut]"
                        + "jasperFullPath: " + jasperFullPath
                        + ", csvFile: " + csvFile
                        + ", pdfFullPath: " + outputFullPath);
                    
                    if (formato.compareTo("pdf") == 0){
                        fillJasperReportToPdf(jasperFullPath, 
                            parameters,
                            csvFile, 
                            outputFullPath);
                    }else{//en excel
                        fillJasperReportToXLSX(jasperFullPath, 
                            parameters,
                            csvFile, 
                            outputFileName, 
                            response);
                    }
                }catch(JRException jex){
                    System.err.println("[processRequestRut.processRequestRut]"
                        + "Error al leer template jasper report: "+jex.toString());
                    jex.printStackTrace();
                }
                
                fileGenerated = new FileGeneratedVO(outputFileName, outputFullPath);
        }else{
            System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                + "processRequestRut]NO Generar archivo...");
            fileGenerated = null;
        }
        
        return fileGenerated;
    }
   
    /**
    * 
    */
    private JRCsvDataSource getDataSource(String _csvFilename) throws JRException
    {
        String[] columnNames = 
            new String[] {"Fecha",
            "Hora_Entrada",
            "Hora_Salida",
            "Entrada_comentario",
            "Salida_comentario",
            "Entrada_teorica",
            "Salida_teorica",
            "Hrs_teoricas",
            "Horas_presenciales",
            "Hhmm_atraso",
            "Hhmm_justificadas",
            "Hhmm_extras",
            "Hhmm_ausencia",
            "Hhmm_trabajadas",
            "Observacion",
            "Hhmm_no_trabajadas"};
        //String csvfileName = "C:\\Proyectos\\GestionFemaseRuntime\\output\\admin_asistencia_semanal_sin_headers.csv";//asistencia_salida.csv";
        JRCsvDataSource ds = new JRCsvDataSource(JRLoader.getLocationInputStream(_csvFilename));
        //ds.setRecordDelimiter("\r\n");
        ds.setColumnNames(columnNames);
        return ds;
    }
    
    /**
    * 
    */
    private void fillJasperReportToPdf(String _jasperReportName,
            Map _parameters,
            String _csvFilename, 
            String _outputPdfFilename) throws JRException
    {
        String outputfile = JasperFillManager.fillReportToFile(_jasperReportName,
            _parameters, getDataSource(_csvFilename));
        JasperExportManager.exportReportToPdfFile(outputfile,
            _outputPdfFilename);
    }
    
    /**
    * 
    */
    private void fillJasperReportToXLSX(String _sourceFileName,
            Map _parameters,
            String _csvFilename, 
            String _outputXlsFilename,
            HttpServletResponse _response) throws JRException
    {
        
        System.out.println("[ReporteAsistenciaSemanal.fillJasperReportToXLSX]Generar reporte en formato XLSX");
        try{
            JasperPrint jasperPrint = JasperFillManager.fillReport(_sourceFileName, _parameters,getDataSource(_csvFilename));
            JRXlsxExporter exporter = new JRXlsxExporter();
            SimpleXlsxReportConfiguration reportConfigXLS = new SimpleXlsxReportConfiguration();
            reportConfigXLS.setSheetNames(new String[] { "sheet1" });
            exporter.setConfiguration(reportConfigXLS);
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(_response.getOutputStream()));
            _response.setHeader("Content-Disposition", "attachment;filename=" + _outputXlsFilename);
            _response.setContentType("application/octet-stream");
            exporter.exportReport();
        }catch(IOException ioex){
            System.err.println("[ReporteAsistenciaSemanal.fillJasperReportToXLSX]Error: "+ ioex.toString());
        }
        
    }
        
//    /**
//     * obtiene parametros a setear en el reporte
//     */
//    private HashMap getParameters(HttpServletRequest _request){
//        
//        ServletContext application  = this.getServletContext();
//        PropertiesVO appProperties  = (PropertiesVO)application.getAttribute("appProperties");
//        EmpleadosBp empleadosBp     = new EmpleadosBp(appProperties);
//        DetalleAsistenciaBp detAsistenciaBp = new DetalleAsistenciaBp(appProperties);
//        
//        String rutParam = _request.getParameter("rut");
//        String startDateParam = _request.getParameter("startDate");
//        String endDateParam = _request.getParameter("endDate");
//        String empresaParam = _request.getParameter("empresa");
//        String tipoParam = _request.getParameter("tipo");
//        HashMap parameters = new HashMap();
//                
//        System.out.println(WEB_NAME+"ReporteAsistenciaSemanal."
//            + "getParameters. "
//            + "Param rut= " +rutParam
//            + ", startDate= " +startDateParam
//            + ", endDate= " +endDateParam
//            + ", tipo= " +tipoParam);
//        
//        String strAuxHp     = "";
//        String strAuxHt     = "";
//        String strAuxAtraso = "";
//        String strAuxHextras = "";
//        String strAuxHaus   = "";
//        String strAuxHrsJustificadas = "";
//        
//        int countDiasTrabajados = 0;
//        int countDiasAusente   = 0;
//        List<DetalleAsistenciaVO> registros 
//            = detAsistenciaBp.getDetalles(rutParam, startDateParam, endDateParam);
//        if (registros.size() >0){
//            ArrayList<String> listaHorasPresenciales = new ArrayList<>();
//            ArrayList<String> listaHorasTrabajadas = new ArrayList<>();
//            ArrayList<String> listaHorasAusencia = new ArrayList<>();
//            ArrayList<String> listaHorasAtraso = new ArrayList<>();
//            ArrayList<String> listaHorasExtras = new ArrayList<>();
//            ArrayList<String> listaHorasJustificadas = new ArrayList<>();
//            for (DetalleAsistenciaVO detalle : registros) {
//                strAuxHp = detalle.getHrsPresenciales();
//                listaHorasPresenciales.add(strAuxHp);
//
//                strAuxHt = detalle.getHrsTrabajadas();
//                listaHorasTrabajadas.add(strAuxHt);
//
//                strAuxHaus = detalle.getHrsAusencia();
//                listaHorasAusencia.add(strAuxHaus);
//
//                //marzo 11,2018
//                strAuxAtraso = detalle.getHhmmAtraso();
//                if (strAuxAtraso != null) listaHorasAtraso.add(strAuxAtraso);
//                strAuxHextras = detalle.getHorasMinsExtrasAutorizadas();
//                if (strAuxHextras != null) listaHorasExtras.add(strAuxHextras);
//                strAuxHrsJustificadas = detalle.getHhmmJustificadas();
//                if (strAuxHrsJustificadas != null) listaHorasJustificadas.add(strAuxHrsJustificadas);
//                if ((detalle.getHoraEntrada() != null && detalle.getHoraEntrada().compareTo("00:00:00") != 0 
//                        && detalle.getHoraSalida() != null && detalle.getHoraSalida().compareTo("00:00:00") != 0)
//                        || (detalle.getObservacion()!=null && detalle.getObservacion().compareTo("Dia libre") == 0)){
//                    countDiasTrabajados++;
//                }
//
//                if (detalle.getHrsAusencia() != null 
//                        && detalle.getHrsAusencia().compareTo("") != 0){
//                    countDiasAusente++;
//                }
//
//            }//fin iteracion detalle ausencia
//
//            String totalHrsPresenciales = "00:00";
//            String totalHrsTrabajadas = "00:00";
//            String totalHrsAusencia = "00:00";
//            String totalHrsExtras = "00:00";
//            String totalHrsAtraso = "00:00";
//            String totalHrsJustificadas = "00:00";
//
//            if (listaHorasPresenciales.size() > 0){
//                totalHrsPresenciales = Utilidades.sumTimesList(listaHorasPresenciales);
//            }
//            if (listaHorasTrabajadas.size() > 0){
//                totalHrsTrabajadas = Utilidades.sumTimesList(listaHorasTrabajadas);
//            }
//            if (listaHorasAusencia.size() > 0){
//                totalHrsAusencia = Utilidades.sumTimesList(listaHorasAusencia);
//            }
//            if (listaHorasExtras.size() > 0){
//                totalHrsExtras = Utilidades.sumTimesList(listaHorasExtras);
//            }
//            if (listaHorasAtraso.size() > 0){
//                totalHrsAtraso = Utilidades.sumTimesList(listaHorasAtraso);
//            }
//            if (listaHorasJustificadas.size() > 0){
//                totalHrsJustificadas = Utilidades.sumTimesList(listaHorasJustificadas);
//            }
//        
//            EmpleadoVO infoempleado = empleadosBp.getEmpleado(empresaParam, rutParam);
//
//            parameters.put("rut", rutParam);//cod_interno
//            parameters.put("rut_full", infoempleado.getCodInterno());
//            parameters.put("cod_interno", rutParam);
//            parameters.put("nombre", infoempleado.getNombres() + " " +infoempleado.getApeMaterno());
//            parameters.put("cargo", infoempleado.getNombreCargo());
//            parameters.put("empresa_id", infoempleado.getEmpresa().getId());
//            parameters.put("empresa_nombre", infoempleado.getEmpresa().getNombre());
//            parameters.put("cenco_id", ""+infoempleado.getCentroCosto().getId());
//            parameters.put("cenco_nombre", infoempleado.getCentroCosto().getNombre());
//            parameters.put("fecha_ingreso", infoempleado.getFechaInicioContratoAsStr());
//            parameters.put("startDate", startDateParam);
//            parameters.put("endDate", endDateParam);
//            parameters.put("totalHrsPresenciales", totalHrsPresenciales);
//            parameters.put("totalHrsTrabajadas", totalHrsTrabajadas);
//            parameters.put("totalHrsAusencia", totalHrsAusencia);
//            parameters.put("totalHrsAtraso", totalHrsAtraso);
//            parameters.put("totalHrsExtras", totalHrsExtras);
//            parameters.put("totalHrsJustificadas", totalHrsJustificadas);
//            parameters.put("diasTrabajados", countDiasTrabajados);
//            parameters.put("diasAusente", countDiasAusente);
//
//        }
//        return parameters;
//    }
    
    /**
     * obtiene parametros a setear en el reporte
     */
    private HashMap getParameters(HttpServletRequest _request, 
            EmpleadoVO _objEmpleado,
            List<DetalleAsistenciaVO> _registros){
         
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        HttpSession session = _request.getSession(true);
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
                    
        DetalleAsistenciaBp detAsistenciaBp   = new DetalleAsistenciaBp(null, userConnected);
        
        //String rutParam = _request.getParameter("rut");
        String startDateParam = _request.getParameter("startDate");
        String endDateParam = _request.getParameter("endDate");
        String empresaParam = _request.getParameter("empresa");
        String tipoParam = _request.getParameter("tipo");
        String turnoParam = _request.getParameter("turno");
        HashMap parameters = null;
        EmpresaBp empresaBp         = new EmpresaBp(appProperties);
        EmpleadosBp empleadosBp = new EmpleadosBp(new PropertiesVO());
        //TurnosBp turnoBp                = new TurnosBp(appProperties);
        EmpleadoVO infoEmpleado = 
            empleadosBp.getEmpleado(_objEmpleado.getEmpresaId(), 
                _objEmpleado.getRut());
        //DetalleTurnosBp detalleTurnoBp = new DetalleTurnosBp(new PropertiesVO());
        //TurnoRotativoBp turnoRotativoBp = new TurnoRotativoBp(new PropertiesVO());
        
        System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
            + "getParameters]"
            + "Param rut= " + _objEmpleado.getRut()
            + ", empresaId= " + _objEmpleado.getEmpresaId()    
            + ", startDate= " +startDateParam
            + ", endDate= " +endDateParam
            + ", tipo= " +tipoParam
            + ", turnoId= " +turnoParam);
        
        String strAuxHp     = "";
        String strAuxHt     = "";
        String strAuxAtraso = "";
        String strAuxHextras = "";
        String strAuxHaus   = "";
        String strAuxHrsJustificadas = "";
        String strHrsNoTrabajadas = "";
        //String hrsTeoricasMenosColacion = "";
        
        int countDiasTrabajados = 0;
        int countDiasAusente   = 0;
        
        if (_registros == null){
            System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                + "getParameters]Consultar detalles_asistencia a la BD...");
            m_registros = detAsistenciaBp.getDetalles(_objEmpleado.getRut(), 
                    startDateParam, endDateParam);
        }else{
            System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                + "getParameters]"
                + "Param rut= " + _objEmpleado.getRut()
                + ", empresaId= " + _objEmpleado.getEmpresaId()
                + ", _registros.size()= " + _registros.size());
            m_registros = _registros; 
        }
        
        if (m_registros.size() >0){
            parameters = new HashMap();
            ArrayList<String> listaHorasPresenciales = new ArrayList<>();
            ArrayList<String> listaHorasTeoricas = new ArrayList<>();
            ArrayList<String> listaHorasTrabajadas = new ArrayList<>();
            ArrayList<String> listaHorasNoTrabajadas = new ArrayList<>();
            ArrayList<String> listaHorasAusencia = new ArrayList<>();
            ArrayList<String> listaHorasAtraso = new ArrayList<>();
            ArrayList<String> listaHorasExtras = new ArrayList<>();
            ArrayList<String> listaHorasJustificadas = new ArrayList<>();
            
            for (DetalleAsistenciaVO detalle : m_registros) {
                strAuxHp = detalle.getHrsPresenciales();
                listaHorasPresenciales.add(strAuxHp);

                strAuxHt = detalle.getHrsTrabajadas();
                listaHorasTrabajadas.add(strAuxHt);

                strAuxHaus = detalle.getHrsAusencia();
                listaHorasAusencia.add(strAuxHaus);

                //marzo 11,2018
                strAuxAtraso = detalle.getHhmmAtraso();
                if (strAuxAtraso != null) listaHorasAtraso.add(strAuxAtraso);
                strAuxHextras = detalle.getHorasMinsExtrasAutorizadas();
                if (strAuxHextras != null) listaHorasExtras.add(strAuxHextras);
                strAuxHrsJustificadas = detalle.getHhmmJustificadas();
                System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                    + "getParameters]fecha: " + detalle.getFechaEntradaMarca()
                    + ", hrs trabajadas: " + strAuxHt
                    + ", observacion: " + detalle.getObservacion());
                
                if (strAuxHrsJustificadas != null) listaHorasJustificadas.add(strAuxHrsJustificadas);
                
                strHrsNoTrabajadas = detalle.getHrsNoTrabajadas();
                if (strHrsNoTrabajadas!=null && strHrsNoTrabajadas.compareTo("-") != 0){
                    System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                        + "getParameters]Add hrs NO trabajadas: " + strHrsNoTrabajadas);
                    listaHorasNoTrabajadas.add(strHrsNoTrabajadas);
                }
                
                System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                    + "getParameters]fecha: " + detalle.getFechaEntradaMarca()
                    + ", detalle.getHoraEntrada(): " + detalle.getHoraEntrada()
                    + ", detalle.getHoraSalida(): " + detalle.getHoraSalida()    
                    + ", observacion: " + detalle.getObservacion()
                    + ", strAuxHt: " + strAuxHt);
                
                if ((detalle.getHoraEntrada() != null && detalle.getHoraEntrada().compareTo("00:00:00") != 0 
                        && detalle.getHoraSalida() != null && detalle.getHoraSalida().compareTo("00:00:00") != 0)
                        || (detalle.getObservacion()!=null && detalle.getObservacion().compareTo("Libre") == 0)
                        || (strAuxHt != null) ){
                    countDiasTrabajados++;
                    System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                        + "getParameters]Sumar dias trabajados...");
                }else{
                    System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                        + "getParameters]"
                        + "Fecha: " + detalle.getFechaEntradaMarca()    
                        + ".  ---->NO SUMAR dias trabajados<----");
                }
                
                
                System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                    + "getParameters]calcular hrs teoricas...");
                DetalleAsistenciaVO auxDetail = 
                    getHrsTeoricas(detalle, infoEmpleado);
                detalle.setHhmmTeoricas(auxDetail.getHhmmTeoricas());
                //detalle.setObservacion(auxDetail.getObservacion());
                
                listaHorasTeoricas.add(auxDetail.getHhmmTeoricas());
                
                System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                    + "getParameters]"
                    + "Fecha entrada= " + detalle.getFechaEntradaMarca()
                    + ", getObservacion= " + detalle.getHrsAusencia()
                    + ", getObservacion= " + detalle.getObservacion());
//                if (detalle.getHrsAusencia() != null 
//                        && detalle.getHrsAusencia().compareTo("") != 0){
//                    countDiasAusente++;
//                }

                if (detalle.getObservacion() != null 
                        && detalle.getObservacion().compareTo("Sin marcas") == 0){
                    System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.getParameters]Sumo dias de ausencia");
                    countDiasAusente++;
                }  

            }//fin iteracion detalle asistencia

            String totalHrsPresenciales = "00:00";
            String totalHrsTrabajadas   = "00:00";
            String totalHrsAusencia     = "00:00";
            String totalHrsNoTrabajadas = "00:00";
            String totalHrsExtras       = "00:00";
            String totalHrsAtraso       = "00:00";
            String totalHrsJustificadas = "00:00";
            String totalHrsTeoricas     = "00:00";

            if (!listaHorasPresenciales.isEmpty()){
                totalHrsPresenciales = Utilidades.sumTimesList(listaHorasPresenciales);
            }
            if (!listaHorasTeoricas.isEmpty()){
                totalHrsTeoricas = Utilidades.sumTimesList(listaHorasTeoricas);
            }
            if (!listaHorasTrabajadas.isEmpty()){
                totalHrsTrabajadas = Utilidades.sumTimesList(listaHorasTrabajadas);
            }
            if (!listaHorasAusencia.isEmpty()){
                totalHrsAusencia = Utilidades.sumTimesList(listaHorasAusencia);
            }
            if (!listaHorasExtras.isEmpty()){
                totalHrsExtras = Utilidades.sumTimesList(listaHorasExtras);
            }
            if (!listaHorasAtraso.isEmpty()){
                totalHrsAtraso = Utilidades.sumTimesList(listaHorasAtraso);
            }
            if (!listaHorasJustificadas.isEmpty()){
                totalHrsJustificadas = Utilidades.sumTimesList(listaHorasJustificadas);
            }
            if (!listaHorasNoTrabajadas.isEmpty()){
                totalHrsNoTrabajadas = Utilidades.sumTimesList(listaHorasNoTrabajadas);
            }
            
            String auxRut = _objEmpleado.getRut();
            System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                + "getParameters]auxRut: " + auxRut);
            
            if (_objEmpleado.getAction() != null 
                    && _objEmpleado.getAction().compareTo("solo_un_rut") == 0){
                System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                    + "getParameters]Un solo rut, "
                    + "buscar datos faltantes en tabla empleado...");
                _objEmpleado = 
                    m_empleadosBp.getEmpleado(empresaParam, 
                    _objEmpleado.getRut(), 
                    Integer.parseInt(turnoParam));
            }
            if (_objEmpleado != null){
                System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal]"
                    + "totalHrsTrabajadas: " + totalHrsTrabajadas
                    + ", countDiasTrabajados: " + countDiasTrabajados);
                
                EmpresaVO empresa = empresaBp.getEmpresaByKey(empresaParam);
                parameters.put("empresa_id", _objEmpleado.getEmpresa().getId());
                parameters.put("empresa_rut", empresa.getRut());
                parameters.put("empresa_nombre", _objEmpleado.getEmpresa().getNombre());
                parameters.put("empresa_direccion", empresa.getDireccion());
                //nuevos
                parameters.put("empresa_comuna", empresa.getComunaNombre());
                parameters.put("empresa_region", empresa.getRegionNombre());
                
                parameters.put("rut", _objEmpleado.getRut());//cod_interno
                parameters.put("rut_full", _objEmpleado.getCodInterno());
                parameters.put("cod_interno", _objEmpleado.getRut());
                parameters.put("nombre", _objEmpleado.getNombres() + " " +_objEmpleado.getApeMaterno());
                parameters.put("cargo", _objEmpleado.getNombreCargo());
                //parameters.put("turnoId", infoempleado.getNombreCargo());
                
                parameters.put("cenco_id", ""+_objEmpleado.getCentroCosto().getId());
                parameters.put("cenco_nombre", _objEmpleado.getCentroCosto().getNombre());
                parameters.put("fecha_ingreso", _objEmpleado.getFechaInicioContratoAsStr());
                parameters.put("startDate", startDateParam);
                parameters.put("endDate", endDateParam);
                parameters.put("totalHrsPresenciales", totalHrsPresenciales);
                parameters.put("totalHrsTeoricas", totalHrsTeoricas);
                
                parameters.put("totalHrsTrabajadas", totalHrsTrabajadas);
                parameters.put("totalHrsAusencia", totalHrsAusencia);
                parameters.put("totalHrsNoTrabajadas", totalHrsNoTrabajadas);
                parameters.put("totalHrsAtraso", totalHrsAtraso);
                parameters.put("totalHrsExtras", totalHrsExtras);
                parameters.put("totalHrsJustificadas", totalHrsJustificadas);
                parameters.put("diasTrabajados", countDiasTrabajados);
                parameters.put("diasAusente", countDiasAusente);
            }else{
                System.err.println("[ReporteAsistenciaSemanal."
                    + "getParameters]"
                    + "No se encontro empleado "
                    + "rut= " + auxRut
                    + ", turno= "  + turnoParam);
                parameters = null;
            }
            
            System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                + "getParameters]"
                + "startDate= " +startDateParam
                + ", endDate= " +endDateParam);
        }
        return parameters;
    }
    
    private List<EmpleadoVO> getEmpleados(String _empresaId,
            String _deptoId, 
            int _cencoId, 
            int _turnoId){
    
        List<EmpleadoVO> listaEmpleados;
        listaEmpleados = new ArrayList<>();
        
        EmpleadosBp empleadosBp = new EmpleadosBp(new PropertiesVO());
        System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.getEmpleados]"
            + "empresaId: " + _empresaId
            + ",deptoId: " + _deptoId    
            + ", cenco Id: " + _cencoId
            + ", turno_Id: " + _turnoId);
        //empleados del cenco
        listaEmpleados = empleadosBp.getEmpleadosNew(_empresaId, 
            _deptoId, 
            _cencoId, 
            _turnoId);
                        
        return listaEmpleados;
    }
    
//////    /**
//////     * totales de asistencia para todos los empleados del cenco seleccionado
//////     */
//////    private LinkedHashMap<String, AsistenciaTotalesVO> getTotalesAsistenciaCenco(HttpServletRequest _request){
//////    
//////        LinkedHashMap<String, AsistenciaTotalesVO>  totalesAsistencia = new LinkedHashMap<>();
//////    
//////        //DepartamentoBp deptosBp = new DepartamentoBp(new PropertiesVO());
//////        //CentroCostoBp cencosBp  = new CentroCostoBp(new PropertiesVO());
//////        EmpleadosBp empleadosBp = new EmpleadosBp(new PropertiesVO());
//////        
//////        List<EmpleadoVO> listaEmpleados          = new ArrayList<>();
//////        String empresaId = _request.getParameter("empresa"); 
//////        String cencoId = _request.getParameter("cenco");
//////        String deptoId = _request.getParameter("depto");
////////        List<DepartamentoVO> departamentos = 
////////            deptosBp.getDepartamentosEmpresa(empresaId);
////////        for (int i = 0; i < departamentos.size(); i++) {
////////            DepartamentoVO itDepto= departamentos.get(i);
////////            System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.getTotalesAsistencia]"
////////                + "Depto: " + itDepto.getId()
////////                + ", nombre: " + itDepto.getNombre());
////////            UsuarioVO auxUser = new UsuarioVO();
////////            auxUser.setIdPerfil(1);
////////            List<CentroCostoVO> cencos = 
////////                cencosBp.getCentrosCostoDepto(auxUser, itDepto.getId());
//////            //for (int j = 0; j < cencos.size(); j++) {
//////                //CentroCostoVO itCenco = cencos.get(j);
//////                System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.getTotalesAsistencia]"
//////                    + "Cenco Id: " + cencoId);
//////                //if (itCenco.getId() != -1){
//////                    //empleados del cenco
//////                    listaEmpleados = empleadosBp.getListaEmpleados(empresaId, 
//////                        deptoId, Integer.parseInt(cencoId));
//////                    //iterar empleados...
//////                    if (listaEmpleados.size() > 0){
//////                        for (EmpleadoVO empleado : listaEmpleados) {
//////                            System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.getTotalesAsistencia]"
//////                                + "Empleado a mostrar: " + empleado.getRut()
//////                                +", Cenco nombre: " + empleado.getCentroCosto().getNombre());
//////
//////                            Map valores = getParameters(_request, empleado.getRut());
//////                            if (valores != null){
//////                                AsistenciaTotalesVO totalesRut = new AsistenciaTotalesVO();
//////                                String totalHrsPresenciales = (String)valores.get("totalHrsPresenciales"); 
//////                                String totalHrsAtraso       = (String)valores.get("totalHrsAtraso"); 
//////                                String totalHrsJustificadas = (String)valores.get("totalHrsJustificadas"); 
//////                                String totalHrsExtras       = (String)valores.get("totalHrsExtras"); 
//////                                String totalHrsAusencia     = (String)valores.get("totalHrsAusencia"); 
//////                                totalesRut.setCencoNombre(empleado.getCentroCosto().getNombre());
//////                                totalesRut.setRutEmpleado(empleado.getRut());
//////                                totalesRut.setNombreEmpleado(empleado.getNombres()+" "+empleado.getApeMaterno());
//////
//////                                totalesRut.setHrsPresenciales(totalHrsPresenciales);
//////                                totalesRut.setHrsAtraso(totalHrsAtraso);
//////                                totalesRut.setHrsJustificadas(totalHrsJustificadas);
//////                                totalesRut.setHrsExtras(totalHrsExtras);
//////                                totalesRut.setHrsNoTrabajadas(totalHrsAusencia);
//////                                //key: cencoId+|+rut
//////                                totalesAsistencia.put(empleado.getCentroCosto().getId()
//////                                    + "|" + empleado.getRut(), totalesRut);
//////                            }
//////                        }
//////                    }
//////                //}
//////            //}
//////        //}
//////        return totalesAsistencia;
//////    }
    
    /**
     * Totales de asistencia para todos los empleados del o los cencos correspondientes.
     * 
     */
    private LinkedHashMap<String, AsistenciaTotalesVO> 
            getTotalesAsistenciaEmpresa(HttpServletRequest _request, 
                UsuarioVO _userConnected){
    
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        HttpSession session = _request.getSession(true);
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");        
                
        LinkedHashMap<String, AsistenciaTotalesVO>  totalesAsistencia = new LinkedHashMap<>();
    
        DepartamentoBp deptosBp = new DepartamentoBp(new PropertiesVO());
        CentroCostoBp cencosBp  = new CentroCostoBp(new PropertiesVO());
        EmpleadosBp empleadosBp = new EmpleadosBp(new PropertiesVO());
        DetalleAsistenciaBp detAsistenciaBp   = new DetalleAsistenciaBp(null, userConnected);
        
        List<EmpleadoVO> listaEmpleados          = new ArrayList<>();
        String empresaId = _request.getParameter("empresa"); 
        String deptoParamId = _request.getParameter("depto"); 
        String cencoParamId = _request.getParameter("cenco"); 
        String startDateParam = _request.getParameter("startDate");
        String endDateParam = _request.getParameter("endDate");
        
        System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
            + "getTotalesAsistenciaEmpresa]"
            + "perfil usuario: " + _userConnected.getIdPerfil()
            + "param empresa: " + empresaId);
            
        List<DepartamentoVO> departamentos=null; 
        //deptosBp.getDepartamentosEmpresa(_userConnected, empresaId);
        List<CentroCostoVO> cencos=null;
        if (deptoParamId != null && deptoParamId.compareTo("-1") != 0){
            System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                + "getTotalesAsistenciaEmpresa]"
                + "Generar reporte solo para deptoId: " + deptoParamId);
            DepartamentoVO auxDepto = 
                new DepartamentoVO(deptoParamId,"", empresaId);
            departamentos = new ArrayList<DepartamentoVO>();
            departamentos.add(auxDepto);
        }else{
            System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                + "getTotalesAsistenciaEmpresa]"
                + "Generar reporte para todos los deptos de la empresaId: " + empresaId);
            departamentos = deptosBp.getDepartamentosEmpresa(_userConnected, empresaId);
        }
        
        for (int i = 0; i < departamentos.size(); i++) {
            DepartamentoVO itDepto= departamentos.get(i);
            System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.getTotalesAsistenciaEmpresa]"
                + "Depto: " + itDepto.getId()
                + ", nombre: " + itDepto.getNombre());
            if (cencoParamId != null && cencoParamId.compareTo("-1") != 0){
                System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                    + "getTotalesAsistenciaEmpresa]"
                    + "Generar reporte solo para cencoId: " + cencoParamId);
                CentroCostoVO auxCenco = 
                    new CentroCostoVO(Integer.parseInt(cencoParamId),"", 1, deptoParamId);
                cencos = new ArrayList<CentroCostoVO>();
                cencos.add(auxCenco);
            }else{
                System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                    + "getTotalesAsistenciaEmpresa]"
                    + "Generar reporte para todos los "
                    + "cencos de la empresaId: " + empresaId
                    + ", deptoId: " + itDepto.getId());
                cencos = 
                    cencosBp.getCentrosCostoDepto(_userConnected, itDepto.getId());
            }
            for (int j = 0; j < cencos.size(); j++) {
                CentroCostoVO itCenco = cencos.get(j);
                System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.getTotalesAsistenciaEmpresa]"
                    + "Cenco Id: " + itCenco.getId() 
                    + ", Cenco Name: " + itCenco.getNombre() 
                    + ". Solo empleados vigentes y sin articulo22");
                if (itCenco.getId() != -1){
                    //empleados del cenco
                    System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.getTotalesAsistenciaEmpresa]"
                        + "Buscar empleados para empresaId: " + empresaId 
                        + ", deptoId: " + itDepto.getId()
                        + ", cencoId: " + itCenco.getId());
                    listaEmpleados = 
                        empleadosBp.getEmpleadosSimpleByFiltro(empresaId, 
                        itDepto.getId(), itCenco.getId(), 1, false);
                    if (listaEmpleados.size() > 0){
                        System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.getTotalesAsistenciaEmpresa]"
                            + "Obteniendo detalle asistencia "
                            + "para todos los empleados vigentes y sin articulo22...");
                        LinkedHashMap<String,List<DetalleAsistenciaVO>> detalles 
                            = detAsistenciaBp.getDetallesInforme(listaEmpleados, 
                                    startDateParam, 
                                    endDateParam, -1);

                        //iterar empleados...
                    
                        for (EmpleadoVO empleado : listaEmpleados) {
                            System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.getTotalesAsistenciaEmpresa]"
                                + "Empleado a mostrar: " + empleado.getRut()
                                +", Cenco nombre: " + empleado.getCencoNombre());
                            List<DetalleAsistenciaVO> detalleAsistenciaRut = detalles.get(empleado.getRut());
                            System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.getTotalesAsistenciaEmpresa]aquiii "
                                + "detalleAsistenciaRut: " + detalleAsistenciaRut);
                            
                            //m_detAsistenciaBp.openDbConnection();
                            Map valores = getParameters(_request, empleado, detalleAsistenciaRut);
                            //m_detAsistenciaBp.closeDbConnection();
                            if (valores != null){
                                AsistenciaTotalesVO totalesRut = new AsistenciaTotalesVO();
                                String totalHrsPresenciales = (String)valores.get("totalHrsPresenciales"); 
                                String totalHrsAtraso       = (String)valores.get("totalHrsAtraso"); 
                                String totalHrsJustificadas = (String)valores.get("totalHrsJustificadas"); 
                                String totalHrsExtras       = (String)valores.get("totalHrsExtras"); 
                                String totalHrsAusencia     = (String)valores.get("totalHrsAusencia"); 
                                String totalHrsTrabajadas   = (String)valores.get("totalHrsTrabajadas"); 
                                
                                totalesRut.setCencoNombre(empleado.getCencoNombre());
                                totalesRut.setRutEmpleado(empleado.getRut());
                                totalesRut.setNombreEmpleado(empleado.getNombres());

                                totalesRut.setHrsPresenciales(totalHrsPresenciales);
                                totalesRut.setHrsAtraso(totalHrsAtraso);
                                totalesRut.setHrsJustificadas(totalHrsJustificadas);
                                totalesRut.setHrsExtras(totalHrsExtras);
                                totalesRut.setHrsNoTrabajadas(totalHrsAusencia);
                                totalesRut.setHrsTotalDelDia(totalHrsTrabajadas);
                                
                                //key: cencoId+|+rut
                                totalesAsistencia.put(empleado.getCencoId()
                                    + "|" + empleado.getRut(), totalesRut);
                            }
                        }
                    }else{
                        System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.getTotalesAsistenciaEmpresa]"
                            + "No hay empleados para empresaId: " + empresaId 
                            + ", deptoId: " + itDepto.getId()
                            + ", cencoId: " + itCenco.getId());
                    }
                }
            }
        }
        return totalesAsistencia;
    }
    
    protected String setTotalesAsistenciaToCSV(HttpServletRequest _request, 
            LinkedHashMap<String, AsistenciaTotalesVO> _totalesAsistencia, EmpleadoVO _empleado)
    throws ServletException, IOException {
        
        String filePath="";
        PrintWriter outfile=null;
        try {
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            HttpSession session = _request.getSession(true);
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
            String empresaId        = _request.getParameter("empresa");
            String deptoId          = _request.getParameter("depto");
            String strCencoId       = _request.getParameter("cenco");
            String startDateParam   = _request.getParameter("startDate");
            String endDateParam     = _request.getParameter("endDate");
            int intCencoId          = Integer.parseInt(strCencoId);
            CentroCostoBp cencoBp   = new CentroCostoBp(appProperties);
            System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.setTotalesAsistenciaToCSV]"
                + "empresaId: " + empresaId
                + ", deptoId: " + deptoId
                + ", strCencoId: " + strCencoId
                + ", startDate: " + startDateParam    
                + ", endDate: " + endDateParam);
            
            String jsonOutput = cencoBp.getEmpresaDeptoCencoJson(empresaId, deptoId, intCencoId);
            FiltroBusquedaJsonVO labelsFiltro = (FiltroBusquedaJsonVO)new Gson().fromJson(jsonOutput, FiltroBusquedaJsonVO.class);
            
            String separatorFields = ";";
            if (_totalesAsistencia != null && _totalesAsistencia.size() > 0){
                filePath = appProperties.getPathExportedFiles()+
                    File.separator+
                    userConnected.getUsername()+"_resumen_asistencia.csv";
                FileWriter filewriter = new FileWriter(filePath);
                outfile     = new PrintWriter(filewriter);
                NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "ES"));
                Utilidades utils=new Utilidades();
                int filas=0;

                //cabecera del archivo
                outfile.println("Empresa;"+labelsFiltro.getEmpresanombre());
                outfile.println("Departamento;"+labelsFiltro.getDeptonombre());
                outfile.println("Centro de costo;"+labelsFiltro.getCenconombre());
                outfile.println("Fecha inicio;" + startDateParam);
                outfile.println("Fecha fin;" + endDateParam);
                outfile.println("");
                //cabecera de los datos
                outfile.println("Cenco;Rut Empleado;Nombre Empleado;Horas presenciales;"
                    + "Atrasos;Horas justificadas;Horas extras;Horas no trabajadas;Total del dia");

                Set<String> keys = _totalesAsistencia.keySet();
                for(String k:keys){
                    filas++;
                    AsistenciaTotalesVO detailObj = _totalesAsistencia.get(k);

                    //escribir lineas en el archivo
                    outfile.print(detailObj.getCencoNombre());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getRutEmpleado());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getNombreEmpleado());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getHrsPresenciales());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getHrsAtraso());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getHrsJustificadas());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getHrsExtras());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getHrsNoTrabajadas());
                    outfile.print(separatorFields);

                    if (filas < _totalesAsistencia.size()){
                        outfile.println(detailObj.getHrsTotalDelDia());
                    }else{
                        outfile.print(detailObj.getHrsTotalDelDia());
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.doGet]entrando...");
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
//
//        String startDateParam = request.getParameter("startDate");
//        String endDateParam = request.getParameter("endDate");
//        
        if (userConnected != null){
                doPost(request, response);
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                +" no valida");
            System.out.println(WEB_NAME+"Sesion de usuario "+request.getParameter("username")
                +" no valida");
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
        HttpSession session     = request.getSession(true);
        ServletContext application  = this.getServletContext();
        PropertiesVO appProperties  = (PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        String empresaId        = request.getParameter("empresa");
        String deptoId          = request.getParameter("depto");
        String strCencoId       = request.getParameter("cenco");
        String rutParam         = request.getParameter("rut");
        String turnoParam       = request.getParameter("turno");
        String tipoParam        = request.getParameter("tipo");
        String formato          = request.getParameter("formato");
        String startDateParam   = request.getParameter("startDate");
        String endDateParam     = request.getParameter("endDate");
        DetalleAsistenciaBp detAsistenciaBp   = new DetalleAsistenciaBp(null, userConnected);
        
        System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.doPost]"
            + "empresaId: " + empresaId
            + ", deptoId: " + deptoId
            + ", strCencoId: " + strCencoId
            + ", turnoId: " + turnoParam    
            + ", rutParam: " + rutParam
            + ", tipoParam: " + tipoParam
            + ", formato: " + formato);
        
        int cencoId = -1;
        if (strCencoId != null) cencoId = Integer.parseInt(strCencoId);
        int turnoId = -1;
        if (turnoParam != null) turnoId = Integer.parseInt(turnoParam);
           
        try {
            m_dbLocator             = DatabaseLocator.getInstance();
            m_dbpoolName            = appProperties.getDbPoolName();
            m_databaseConnection    = m_dbLocator.getConnection(m_dbpoolName,"[ReporteAsistenciaSemanal.doPost]");
            System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.doPost]"
                + "Abrir conexion a la BD. Datasource: " + m_dbpoolName);
        } catch (DatabaseException ex) {
            System.err.println("Error: "+ex.toString());
        }
            
        if (rutParam != null 
                && rutParam.compareTo("todos") == 0
                && tipoParam.compareTo("3") != 0){
            //generar informes por centro de costo
            LinkedHashMap<String, String> archivosGenerados = new LinkedHashMap<>();
            System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.doPost]"
                + "Generar informes para todos los empleados de "
                + "empresaId: " + empresaId
                + ", deptoId: " + deptoId
                + ", cencoId: " + cencoId
                + ", turnoId: " + turnoId);
            
            List<EmpleadoVO> listaEmpleados = 
                getEmpleados(empresaId, deptoId, cencoId, turnoId);
            
            FileGeneratedVO archivoGenerado;
            String nombreCenco="";
            
            System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.doPost]"
                + "Obteniendo detalle asistencia para todos los empleados seleccionados...");
            LinkedHashMap<String,List<DetalleAsistenciaVO>> detalles 
                = detAsistenciaBp.getDetallesInforme(listaEmpleados, startDateParam, endDateParam,-1);
                    
            for (EmpleadoVO empleado : listaEmpleados) {
                System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.doPost]"
                    + "Generar informe para empleado rut: " + empleado.getRut()
                    +", nombre: " + empleado.getNombreCompleto());
                if (nombreCenco.compareTo("") == 0) nombreCenco = empleado.getCencoNombre();
                List<DetalleAsistenciaVO> detalleAsistenciaRut = detalles.get(empleado.getRut());
//                if (detalleAsistenciaRut!=null){
//                    System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.doPost]"
//                        + "detalleAsistenciaRut != null!");
//                }else{
//                    System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.doPost]"
//                        + "detalleAsistenciaRut is null!");
//                }
                empleado.setEmpresaId(empresaId);
                archivoGenerado = processRequestRut(request, response, empleado, detalleAsistenciaRut);
                
                if (archivoGenerado != null){
                    System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.doPost]Add "
                        + "archivo generado: " + archivoGenerado.getFileName());
                    archivosGenerados.put(empleado.getRut(), archivoGenerado.getFilePath());
                }    
            }// fin iteracion empleados
                        
            if (archivosGenerados.size() > 0){
                archivoGenerado = mergePdfFiles(archivosGenerados, userConnected, nombreCenco);
                showFileToDownload(archivoGenerado, tipoParam, formato, response);
                File auxpdf=new File(archivoGenerado.getFilePath());
                auxpdf.delete();
                
                //agregar evento al log.
                MaintenanceEventsBp eventosBp   = new MaintenanceEventsBp(null);
                MaintenanceEventVO resultado=new MaintenanceEventVO();
                resultado.setUsername(userConnected.getUsername());
                resultado.setDatetime(new Date());
                resultado.setUserIP(request.getRemoteAddr());
                resultado.setType("DTA");
                resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                resultado.setEmpresaId(empresaId);
                resultado.setDeptoId(deptoId);
                resultado.setCencoId(cencoId);
                resultado.setDescription("Consulta reporte de asistencia.");
                eventosBp.addEvent(resultado);
                
            }else{
                session.setAttribute("mensaje", Constantes.NO_HAY_DATOS_REPORTE);
                request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
            }
            
            //m_detAsistenciaBp.closeDbConnection();
        }else{
            System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.doPost]"
                + "Generar reporte asistencia solo para el rut: " + rutParam);
            EmpleadoVO singleEmpleado = new EmpleadoVO();
            singleEmpleado.setEmpresaId(empresaId);
            singleEmpleado.setRut(rutParam);
            singleEmpleado.setAction("solo_un_rut");
            FileGeneratedVO filegenerated=processRequestRut(request, response, singleEmpleado, null);
            if (filegenerated != null){
                System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.doPost]Add "
                    + "archivo generado: " + filegenerated.getFileName());
                showFileToDownload(filegenerated, tipoParam, formato, response);
                File auxpdf=new File(filegenerated.getFilePath());
                auxpdf.delete();
                //agregar evento al log.
                MaintenanceEventsBp eventosBp   = new MaintenanceEventsBp(null);
                MaintenanceEventVO resultado=new MaintenanceEventVO();
                resultado.setUsername(userConnected.getUsername());
                resultado.setDatetime(new Date());
                resultado.setUserIP(request.getRemoteAddr());
                resultado.setType("DTA");
                resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                resultado.setEmpresaId(empresaId);
                resultado.setDeptoId(deptoId);
                resultado.setCencoId(cencoId);
                resultado.setDescription("Consulta reporte de asistencia.");
                eventosBp.addEvent(resultado);
            }else{
                System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.doPost]No Generar "
                    + "archivo. Mostrar mensaje...");
                session.setAttribute("mensaje", Constantes.NO_HAY_DATOS_REPORTE);
                request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
            }
        }
        System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.doPost]Cerrar conexiones a la BD...");
        //m_empleadosBp.closeDbConnection();
        //m_detAsistenciaBp.closeDbConnection();
    }

    /**
    * 
    */
    private void showFileToDownload(FileGeneratedVO _fileGenerated,
            String _tipoParam,String _formato,
            HttpServletResponse _response){
        String contentType  = "application/pdf";
        File auxFile = new File(_fileGenerated.getFilePath());
        int length   = 0;
        try{
            if (_tipoParam.compareTo("3") == 0){
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
            }else{
                //pdf
                if (_formato.compareTo("pdf") == 0){
                    System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.showFileToDownload]Generar PDF."
                        + " fileName: " + _fileGenerated.getFileName()
                        + ". Mostrar PDF para guardar.");
                    //***********************************************************************
                    //********* NEW *********************************************************
                    String filePath = _fileGenerated.getFilePath();
                    System.err.println("[ReporteAsistenciaSemanal.showFileToDownload]"
                        + "Mostrar PDF...");
                    _response.setContentType("application/pdf");
                    _response.setHeader("Content-disposition","attachment;filename="+ _fileGenerated.getFileName());
                    try {
                        File f = new File(filePath);
                        FileInputStream fis = new FileInputStream(f);
                        DataOutputStream os = new DataOutputStream(_response.getOutputStream());
                        _response.setHeader("Content-Length",String.valueOf(f.length()));
                        byte[] buffer = new byte[1024];
                        int len = 0;
                        while ((len = fis.read(buffer)) >= 0) {
                            os.write(buffer, 0, len);
                        }
                    } catch (IOException e) {
                        System.err.println("[ReporteAsistenciaSemanal.showFileToDownload]"
                            + "Error: " + e.toString());
                        e.printStackTrace();
                    }
//                        
//                        File downloadFile = new File(filePath);
//                        FileInputStream inStream = new FileInputStream(downloadFile);
//
//                        // if you want to use a relative path to context root:
//                        String relativePath = getServletContext().getRealPath("");
//                        System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.showFileToDownload]"
//                            + "relativePath = " + relativePath);
//
//                        // obtains ServletContext
//                        ServletContext context = getServletContext();
//
//                        // gets MIME type of the file
//                        String mimeType = context.getMimeType(filePath);
//                        if (mimeType == null) {        
//                            // set to binary type if MIME mapping not found
//                            mimeType = "application/octet-stream";
//                        }
//                        System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.showFileToDownload]"
//                            + "MIME type: " + mimeType);
//
//                        // modifies response
//                        _response.setContentType(mimeType);
//                        _response.setContentLength((int) downloadFile.length());
//
//                        // forces download
//                        String headerKey = "Content-Disposition";
//                        String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
//                        _response.setHeader(headerKey, headerValue);
//
//                        // obtains response's output stream
//                        OutputStream outStream = _response.getOutputStream();
//
//                        byte[] buffer = new byte[4096];
//                        int bytesRead = -1;
//
//                        while ((bytesRead = inStream.read(buffer)) != -1) {
//                            outStream.write(buffer, 0, bytesRead);
//                        }
//
//                        inStream.close();
//                        outStream.close();     
//
//                        System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.showFileToDownload]"
//                            + "Mostrando PDF");
//
//    ////                        try{
//    ////                            File pointToFile = new File(_fileGenerated.getFilePath());
//    ////                            ByteArrayInputStream byteArrayInputStream = 
//    ////                                new ByteArrayInputStream(FileUtils.readFileToByteArray(pointToFile));
//    ////                            _response.setContentType(contentType);
//    ////                            _response.addHeader("Content-Disposition", 
//    ////                                "attachment; filename=" + _fileGenerated.getFileName());
//    ////                            OutputStream responseOutputStream = _response.getOutputStream();
//    ////
//    ////                            byte[] buf = new byte[4096];
//    ////                            int len = -1;
//    ////
//    ////                            while ((len = byteArrayInputStream.read(buf)) != -1) {
//    ////                              responseOutputStream.write(buf, 0, len);
//    ////                            }
//    ////                            responseOutputStream.flush();
//    ////                            responseOutputStream.close();
//    ////                        }catch(Exception e){
//    ////                            System.err.println("[showFileToDownload]Error al generar pdf: "+e.toString());
//    ////                            e.printStackTrace();
//    ////                        }
                }

            }
        }catch(IOException ioex){
            System.err.println("[ReporteAsistenciaSemanal.showFileToDownload]Error al "
                + "abrir archivo: "+ioex.toString());
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

    /**
     * Permite unir todos los PDF de cada empleado en un solo PDF.
     * 
     * @param _archivos
     * @param _usuario
     * @param _cencoNombre
     * @return 
     */
    public FileGeneratedVO mergePdfFiles(LinkedHashMap<String, String> _archivos, 
            UsuarioVO _usuario, String _cencoNombre){
        ServletContext application  = this.getServletContext();
        PropertiesVO appProperties  = (PropertiesVO)application.getAttribute("appProperties");
        FileGeneratedVO archivoGenerado = null;
        File mergedFile = new File(appProperties.getPathExportedFiles()+File.separator
            +_usuario.getUsername()
            + "_"+_cencoNombre + "_todos.pdf");

        archivoGenerado=new FileGeneratedVO(mergedFile.getName(), mergedFile.getAbsolutePath());
        System.out.println(WEB_NAME+"[mergePdfFiles]"
            + "uniendo archivos en uno solo. "
            + "Path: " + mergedFile.getAbsolutePath());

        try
        {
            PDFMergerUtility mergePdf = new PDFMergerUtility();
            for( String key : _archivos.keySet() ){
                String pathFile = _archivos.get(key);
                File itFile = new File(pathFile);
                System.out.println(WEB_NAME+"[mergePdfFiles]itera archivo " + pathFile);
                
                mergePdf.addSource(itFile);
                
            }
            mergePdf.setDestinationFileName(archivoGenerado.getFilePath());
            mergePdf.mergeDocuments();
            
            for( String key : _archivos.keySet() ){
                String pathFile2 = _archivos.get(key);
                File itFile2 = new File(pathFile2);
                System.out.println(WEB_NAME+"[mergePdfFiles]Eliminando archivo " + pathFile2);
                itFile2.delete();
            }
            
        }
        catch(IOException e)
        {

        }
        
        return archivoGenerado;
    }
    
    
    public void createPdfTodos(UsuarioVO _usuario)
    {
        PDDocument document = null;
        try
        {
            ServletContext application  = this.getServletContext();
            PropertiesVO appProperties  = (PropertiesVO)application.getAttribute("appProperties");
            String filename=appProperties.getPathExportedFiles()+File.separator+_usuario.getUsername()+"_todos.pdf";
            document=new PDDocument();
            PDPage blankPage = new PDPage();
            document.addPage( blankPage );
            document.save( filename );
        }
        catch(Exception e)
        {

        }
    }
    
    /**
    * 
    */
    private void getCSVFile(EmpleadoVO _empleadoBase,
            String _csvFilePath, 
            String[] _columnNames,
            List<DetalleAsistenciaVO> _registrosAsistencia){
        try{
            //DetalleTurnosBp detalleTurnoBp = new DetalleTurnosBp(new PropertiesVO());
            EmpleadosBp empleadosBp = new EmpleadosBp(new PropertiesVO());
            //TurnosBp turnoBp                = new TurnosBp(new PropertiesVO());
            //TurnoRotativoBp turnoRotativoBp = new TurnoRotativoBp(new PropertiesVO());
            EmpleadoVO infoEmpleado = 
                empleadosBp.getEmpleado(_empleadoBase.getEmpresaId(), _empleadoBase.getRut());
            System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                + "getCSVFile]Generar archivo CSV: " + _csvFilePath);
            //write csv file
            List<DetalleAsistenciaVO>  registrosSemana = new ArrayList<>();
            FileWriter csvWriter = new FileWriter(_csvFilePath);
            
            //escribe cabecera archivo
////            for (int i=0; i < _columnNames.length; i++)
////            {
////                String name = _columnNames[i];
////                csvWriter.append("\""+name+"\"");
////                if (i < (_columnNames.length-1)) csvWriter.append(",");
////            }
////            csvWriter.append("\n");
            int filas = 0;
            for (DetalleAsistenciaVO detalle : _registrosAsistencia) {
                System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                    + "getCSVFile]"
                    + "empresaId: " + infoEmpleado.getEmpresaId()
                    + ", rutEmpleado: " +  infoEmpleado.getRut()        
                    + ", idTurno: "+ infoEmpleado.getIdTurno()    
                    + ", itera registro asistencia: " + detalle.toString()
                    + ", hrs presenciales: " + detalle.getHrsPresenciales());
                filas++;                               
                
                String labelFecha = detalle.getLabelFechaEntradaMarca();
                String entradaComentario = detalle.getComentarioMarcaEntrada();
                String salidaComentario = detalle.getComentarioMarcaSalida();
                String hrsPresenciales  = detalle.getHrsPresenciales();
                String hhmmAtraso       = detalle.getHhmmAtraso();
                String hhmmJustificadas = detalle.getHhmmJustificadas();
                String totalDelDia      = detalle.getHrsTrabajadas();
                String observacion      = detalle.getObservacion();
                String hrsAusencia      = detalle.getHrsAusencia();
                String hrsNoTrabajadas  = detalle.getHrsNoTrabajadas();
                String hrsMinsExtras    = detalle.getHorasMinsExtrasAutorizadas();
                String hrsTeoricasMenosColacion = null;
                
                System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                    + "getCSVFile]"
                    + "labelFecha:" + labelFecha
                    + ", hrsNoTrabajadas:" + hrsNoTrabajadas);
                
                //boolean esFeriado = detalle.isEsFeriado();
                System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                    + "getCSVFile]calcular hrs teoricas...");
                DetalleAsistenciaVO auxDetail = 
                    getHrsTeoricas(detalle, infoEmpleado);
                hrsTeoricasMenosColacion = auxDetail.getHhmmTeoricas();
                detalle.setHhmmTeoricas(auxDetail.getHhmmTeoricas());
                //detalle.setObservacion(auxDetail.getObservacion());
                
                if (entradaComentario == null) entradaComentario = "";
                if (salidaComentario == null) salidaComentario = "";
                if (hrsPresenciales == null) hrsPresenciales = "-";
                if (hhmmAtraso == null) hhmmAtraso = "-";
                if (hhmmJustificadas == null) hhmmJustificadas = "-";
                if (totalDelDia == null) totalDelDia = "-";
                if (observacion == null) observacion = "-";
                if (hrsAusencia == null) hrsAusencia = "-";
                if (hrsNoTrabajadas == null) hrsNoTrabajadas = "-";
                if (hrsMinsExtras == null) hrsMinsExtras = "-";
                if (hrsTeoricasMenosColacion == null) hrsTeoricasMenosColacion = "-";
                System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                    + "getCSVFile]"
                    + "Fecha: " + detalle.getFechaEntradaMarca()
                    + ", hrsTeoricasMenosColacion(2): " + hrsTeoricasMenosColacion
                    + ", entradaComentario: " + entradaComentario
                    + ", salidaComentario: " + salidaComentario);
                /** campos:
                 * new String[] {"Fecha",
                    "Hora_Entrada",
                    "Hora_Salida",
                    "Entrada_comentario",
                    "Salida_comentario",
                    "Entrada_teorica",
                    "Salida_teorica",
                    "Horas_presenciales",
                    "Hhmm_atraso",
                    "Hhmm_justificadas",
                    "Hhmm_extras",
                    "Hhmm_ausencia",
                    "Hhmm_trabajadas",
                    "Observacion",
                    "Hhmm_no_trabajadas"};
                 * 
                 
                */
 
                /*    
                Se define el dia domingo como el dia de corte para calcular totales por semana.
                Por cada fecha incluida en el reporte.
                {
                        Guardar datos del reporte en nueva tabla 'asistencia_semanal'. (insert)
                        Si el dia de la fecha = Domingo --> insertar totales en nueva tabla 'asistencia_semanal' (insert). 
                }
                Calcular linea de totales de horas (va al final del reporte)
                */
                if (labelFecha.startsWith("Do") || filas == _registrosAsistencia.size()){//es dia domingo o es el ultimo dia de la lista
                    System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                        + "getCSVFile]Es domingo");
                    registrosSemana.add(detalle);
                    //mostrar detalle del dia domingo (si es que tiene turno este dia)
                    csvWriter.append("\"" + labelFecha
                        + "\",\"" + detalle.getHoraEntrada() 
                        + "\",\"" + detalle.getHoraSalida()
                        + "\",\"" + entradaComentario
                        + "\",\"" + salidaComentario
                        + "\",\"" + detalle.getHoraEntradaTeorica()
                        + "\",\"" + detalle.getHoraSalidaTeorica()
                        + "\",\"" + hrsTeoricasMenosColacion    
                        + "\",\"" + hrsPresenciales
                        + "\",\"" + hhmmAtraso
                        + "\",\"" + hhmmJustificadas
                        + "\",\"" + hrsMinsExtras 
                        + "\",\"" + hrsAusencia        
                        + "\",\"" + totalDelDia
                        + "\",\"" + observacion 
                        + "\",\"" + hrsNoTrabajadas    
                        + "\"");
                    csvWriter.append("\n");    
                    TotalesSemanaVO totalSemana = 
                        getTotalesSemana(registrosSemana);
                    registrosSemana.clear();
                    
                    //escribir linea de totales semana (lu-do)
                    String lineaTotSemana = "\"" + ""
                    + "\",\"" + "" 
                    + "\",\"" + ""
                    + "\",\"" + ""
                    + "\",\"" + ""
                    + "\",\"" + ""
                    + "\",\"" + "Total Semana"
                    + "\",\"" + totalSemana.getTotalHrsTeoricas()
                    + "\",\"" + totalSemana.getTotalHrsPresenciales()
                    + "\",\"" + totalSemana.getTotalHrsAtraso()
                    + "\",\"" + totalSemana.getTotalHrsJustificadas() 
                    + "\",\"" + totalSemana.getTotalHrsExtras()        
                    + "\",\"" + totalSemana.getTotalHrsNoTrabajadas()
                    + "\",\"" + totalSemana.getTotalHrsTrabajadas() + "\"";
                    
                    System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                        + "getCSVFile]lineaTotSemana: " + lineaTotSemana);
                    csvWriter.append(lineaTotSemana);
                    
                }else{
                    registrosSemana.add(detalle);
                    String auxdetail = "\"" + labelFecha
                        + "\",\"" + detalle.getHoraEntrada() 
                        + "\",\"" + detalle.getHoraSalida()
                        + "\",\"" + entradaComentario
                        + "\",\"" + salidaComentario
                        + "\",\"" + detalle.getHoraEntradaTeorica()
                        + "\",\"" + detalle.getHoraSalidaTeorica()
                        + "\",\"" + hrsTeoricasMenosColacion
                        + "\",\"" + hrsPresenciales
                        + "\",\"" + hhmmAtraso
                        + "\",\"" + hhmmJustificadas
                        + "\",\"" + hrsMinsExtras 
                        + "\",\"" + hrsAusencia        
                        + "\",\"" + totalDelDia
                        + "\",\"" + observacion 
                        + "\",\"" + hrsNoTrabajadas    
                        + "\"";
                    csvWriter.append(auxdetail);
                    System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                        + "getCSVFile]"
                        + "hrsAusencia: " + hrsAusencia
                        + ", totalDelDia: " + totalDelDia
                        + ", hrsNoTrabajadas: " + hrsNoTrabajadas);
                    System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                        + "getCSVFile]linea detalle: " + auxdetail);
                }
                
                csvWriter.append("\n");
            }
            
            csvWriter.flush();
            csvWriter.close();
        }catch(IOException ioex){
            System.err.println("[ReporteAsistenciaSemanal."
                + "getCSVFile]"
                + "Error al escribir csv: "+ioex.toString());
        }
    }
    
    /**
    * Retorna totales para una semana
    */
    private TotalesSemanaVO getTotalesSemana(List<DetalleAsistenciaVO> _registrosSemana){
        
        TotalesSemanaVO totales = new TotalesSemanaVO();
        
        ArrayList<String> listaHorasPresenciales = new ArrayList<>();
        ArrayList<String> listaHorasTeoricas = new ArrayList<>();
        ArrayList<String> listaHorasTrabajadas = new ArrayList<>();
        ArrayList<String> listaHorasNoTrabajadas = new ArrayList<>();
        ArrayList<String> listaHorasAusencia = new ArrayList<>();
        ArrayList<String> listaHorasAtraso = new ArrayList<>();
        ArrayList<String> listaHorasExtras = new ArrayList<>();
        ArrayList<String> listaHorasJustificadas = new ArrayList<>();
        String strAuxHp             = "";
        String strAuxHorasTrabajadas = "";
        String strAuxAtraso         = "";
        String strAuxHextras         = "";
        String strAuxHaus           = "";
        String strAuxHrsJustificadas = "";
        String strHrsTeoricas       = "";
        String strAuxHorasNoTrabajadas  ="";
        int countDiasTrabajados     = 0;
        int countDiasAusente        = 0;    
        
        for (DetalleAsistenciaVO detalle : _registrosSemana) {
            strAuxHp = detalle.getHrsPresenciales();
            listaHorasPresenciales.add(strAuxHp);

            System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                + "getParameters]"
                + "getHrsTrabajadas: " + detalle.getHrsTrabajadas()
                + ", getHrsAusencia: " + detalle.getHrsAusencia()
                + ", getHhmmTeoricas: " + detalle.getHhmmTeoricas()
                + ", getHrsNoTrabajadas: " + detalle.getHrsNoTrabajadas());        
            
            strAuxHorasTrabajadas = detalle.getHrsTrabajadas();
            listaHorasTrabajadas.add(strAuxHorasTrabajadas);

            strAuxHaus = detalle.getHrsAusencia();
            listaHorasAusencia.add(strAuxHaus);
            
            strAuxHorasNoTrabajadas = detalle.getHrsNoTrabajadas();
            if (strAuxHorasNoTrabajadas!=null && strAuxHorasNoTrabajadas.compareTo("-") != 0){
                System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                    + "getParameters]Add hrs NO trabajadas: " + strAuxHorasNoTrabajadas);
                listaHorasNoTrabajadas.add(strAuxHorasNoTrabajadas);
            }
            //horas teoricas
            strHrsTeoricas = detalle.getHhmmTeoricas();
            listaHorasTeoricas.add(strHrsTeoricas);
            
            //marzo 11,2018
            strAuxAtraso = detalle.getHhmmAtraso();
            if (strAuxAtraso != null) listaHorasAtraso.add(strAuxAtraso);
            strAuxHextras = detalle.getHorasMinsExtrasAutorizadas();
            if (strAuxHextras != null) listaHorasExtras.add(strAuxHextras);
            strAuxHrsJustificadas = detalle.getHhmmJustificadas();
            System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                + "getParameters]fecha: " + detalle.getFechaEntradaMarca()
                + ", hrs trabajadas: " + strAuxHorasTrabajadas
                + ", hrs No trabajadas: " + strAuxHorasNoTrabajadas
                + ", observacion: " + detalle.getObservacion());

            if (strAuxHrsJustificadas != null) listaHorasJustificadas.add(strAuxHrsJustificadas);
                
            if ((detalle.getHoraEntrada() != null && detalle.getHoraEntrada().compareTo("00:00:00") != 0 
                    && detalle.getHoraSalida() != null && detalle.getHoraSalida().compareTo("00:00:00") != 0)
                    || (detalle.getObservacion()!=null && detalle.getObservacion().compareTo("Libre") == 0)
                    || (strAuxHorasTrabajadas != null) ){
                countDiasTrabajados++;
            }
            System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                + "getParameters]"
                + "Fecha entrada= " + detalle.getFechaEntradaMarca()
                + ", getObservacion= " + detalle.getHrsAusencia()
                + ", getObservacion= " + detalle.getObservacion());
            if (detalle.getObservacion() != null 
                        && detalle.getObservacion().compareTo("Sin marcas") == 0){
                    System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.getParameters]Sumo dias de ausencia");
                    countDiasAusente++;
                }  

        }//fin iteracion detalle asistencia  

        if (!listaHorasPresenciales.isEmpty()){
            totales.setTotalHrsPresenciales(Utilidades.sumTimesList(listaHorasPresenciales));
        }
        if (!listaHorasTeoricas.isEmpty()){
            totales.setTotalHrsTeoricas(Utilidades.sumTimesList(listaHorasTeoricas));
        }
        if (!listaHorasTrabajadas.isEmpty()){
            totales.setTotalHrsTrabajadas(Utilidades.sumTimesList(listaHorasTrabajadas));
        }
        if (!listaHorasAusencia.isEmpty()){
            totales.setTotalHrsAusencia(Utilidades.sumTimesList(listaHorasAusencia));
        }
        if (!listaHorasExtras.isEmpty()){
            totales.setTotalHrsExtras(Utilidades.sumTimesList(listaHorasExtras));
        }
        if (!listaHorasAtraso.isEmpty()){
            totales.setTotalHrsAtraso(Utilidades.sumTimesList(listaHorasAtraso));
        }
        if (!listaHorasJustificadas.isEmpty()){
            totales.setTotalHrsJustificadas(Utilidades.sumTimesList(listaHorasJustificadas));
        }
        
        if (!listaHorasNoTrabajadas.isEmpty()){
            System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                + "getParameters]Sum lista de Hrs No Trabajadas");
            totales.setTotalHrsNoTrabajadas(Utilidades.sumTimesList(listaHorasNoTrabajadas));
        }
        
        totales.setDiasAusente(countDiasAusente);
        totales.setDiasTrabajados(countDiasTrabajados);
        
        System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
            + "getParameters]Objeto Totales Semana: " + totales.toString());
        
        return totales;
    }
  
    /**
    * Retorna hrs teoricas y campo observacion, dependiendo del turno y de si es feriado.
    */
    private DetalleAsistenciaVO getHrsTeoricas(DetalleAsistenciaVO _detalle, 
            EmpleadoVO _infoEmpleado){
        
        //***************Inicio calculo de hrs teoricas menos colacion *********
        //******************************************************************
        DetalleAsistenciaVO newDetail  =new DetalleAsistenciaVO();
        TurnosBp turnoBp                = new TurnosBp(new PropertiesVO());
        TurnoRotativoBp turnoRotativoBp = new TurnoRotativoBp(new PropertiesVO());
        DetalleTurnosBp detalleTurnoBp = new DetalleTurnosBp(new PropertiesVO());
        StringTokenizer tokenFecha = 
            new StringTokenizer(_detalle.getFechaEntradaMarca(), "-");
        int diaSemana = Utilidades.getDiaSemana(Integer.parseInt(tokenFecha.nextToken()), 
            Integer.parseInt(tokenFecha.nextToken()), 
            Integer.parseInt(tokenFecha.nextToken()));
        
        DetalleTurnoVO detalleTurno = null;
        int idTurnoRotativo = turnoBp.getTurnoRotativo(_infoEmpleado.getEmpresaId());
        boolean tieneTurnoRotativo=false;
        boolean calcularHrsTeoricas = true;
        String hrsTeoricasMenosColacion = null;
        boolean esFeriado = _detalle.isEsFeriado();
        boolean tieneDetalleTurnoCodDiaFeriado = false;
        boolean soloMarcaEntrada= false;
        boolean soloMarcaSalida = false;
        boolean noTieneMarcas   = false;       
        
        System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
            + "getHrsTeoricas]"
            + "Fecha: " + _detalle.getFechaEntradaMarca()
            + ", cod dia semana: " + diaSemana
            + ", es feriado? " + esFeriado    
            + ", idTurno: " + _infoEmpleado.getIdTurno());
        
        if (idTurnoRotativo == _infoEmpleado.getIdTurno()){
            tieneTurnoRotativo = true;
        }
        
        System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
            + "getHrsTeoricas]"
            + "Fecha: " + _detalle.getFechaEntradaMarca() 
           + ", horaEntrada: " + _detalle.getHoraEntrada()
            + ", horaSalida: " + _detalle.getHoraSalida()
            + ", toString: " + _detalle.toString());
        
        if (_detalle.getHoraEntrada().compareTo("00:00:00") != 0 && _detalle.getHoraSalida().compareTo("00:00:00") == 0) soloMarcaEntrada = true;
        if (_detalle.getHoraEntrada().compareTo("00:00:00") == 0 && _detalle.getHoraSalida().compareTo("00:00:00") != 0) soloMarcaSalida = true;
        if (_detalle.getHoraEntrada().compareTo("00:00:00") == 0 && _detalle.getHoraSalida().compareTo("00:00:00") == 0) noTieneMarcas = true;
        
        System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
            + "getHrsTeoricas]"
            + "Fecha: " + _detalle.getFechaEntradaMarca() 
            + ", noTieneMarcas: " + noTieneMarcas
            + ", soloMarcaEntrada: " + soloMarcaEntrada
            + ", soloMarcaSalida: " + soloMarcaSalida);
        
        if (tieneTurnoRotativo){
            detalleTurno = 
                turnoRotativoBp.getAsignacionTurnoByFecha(_infoEmpleado.getEmpresaId(), 
                _infoEmpleado.getRut(), _detalle.getFechaEntradaMarca());
            System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                + "getHrsTeoricas]"
                + "Fecha: " + _detalle.getFechaEntradaMarca()
                + ". Tiene turno rotativo");
        }else{
            System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                + "getHrsTeoricas]"
                + "Rescatar detalle turno normal. "
                + "id_turno= " + _infoEmpleado.getIdTurno()
                + ", diaSemana= " + diaSemana 
                + ", esFeriado: " + esFeriado);
            detalleTurno = 
                detalleTurnoBp.getDetalleTurno(_infoEmpleado.getEmpresaId(), 
                _infoEmpleado.getIdTurno(), diaSemana);
            
            System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                + "getHrsTeoricas]"
                + "Rescatar detalle turno normal. "
                + "id_turno= " + _infoEmpleado.getIdTurno()
                + ", diaSemana= " + diaSemana 
                + ", esFeriado: " + esFeriado
                + ", detalleTurno: " + detalleTurno);
            
            if (detalleTurno == null && esFeriado){
                System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                    + "getHrsTeoricas]"
                    + "Fecha: " + _detalle.getFechaEntradaMarca() + " es feriado. "
                    + "Ver si el detalle turno incluye cod_dia=8 (feriado/festivo)");
                detalleTurno = 
                    detalleTurnoBp.getDetalleTurno(_infoEmpleado.getEmpresaId(), 
                    _infoEmpleado.getIdTurno(), 8);
                if (detalleTurno!=null) tieneDetalleTurnoCodDiaFeriado = true;
            }
            
            System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                + "getHrsTeoricas]"
                + "Fecha: " + _detalle.getFechaEntradaMarca()
                + ". Tiene turno normal");
        }

        if (detalleTurno != null){
            //tiene turno asignado
            DiferenciaHorasVO duracionTurno = 
                Utilidades.getTimeDifference(_detalle.getFechaEntradaMarca() +" " + detalleTurno.getHoraEntrada(), 
                    _detalle.getFechaEntradaMarca() + " " + detalleTurno.getHoraSalida());
            String hhmmTurno = duracionTurno.getStrDiferenciaHorasMinutos();
            System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                + "getHrsTeoricas]hhmmTurno: " + hhmmTurno);
            //if (esFeriado) calcularHrsTeoricas = false;
        }else {
            System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                + "getHrsTeoricas]"
                + "No tiene detalle turno definido");
        }
        
        if (esFeriado && !tieneDetalleTurnoCodDiaFeriado) calcularHrsTeoricas = false;
        
        //if (esFeriado) calcularHrsTeoricas = false;
        System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
            + "getHrsTeoricas]"
            + "Fecha: " + _detalle.getFechaEntradaMarca() 
            + ", esFeriado?: " + esFeriado    
            + ", tieneDetalleTurnoCodDiaFeriado?: " + tieneDetalleTurnoCodDiaFeriado
            + ", observacion: " + newDetail.getObservacion()
            + ", calcularHrsTeoricas: " + calcularHrsTeoricas);

        if (calcularHrsTeoricas){
            DiferenciaHorasVO diferenciaTeorica = 
                Utilidades.getTimeDifference(_detalle.getFechaEntradaMarca()
                    + " " + _detalle.getHoraEntradaTeorica(), 
                    _detalle.getFechaEntradaMarca() 
                    + " " + _detalle.getHoraSalidaTeorica());
            String hrsTeoricas = diferenciaTeorica.getStrDiferenciaHorasMinutos();
            if (detalleTurno != null){
                hrsTeoricasMenosColacion = 
                    Utilidades.restarMinsHora(hrsTeoricas, detalleTurno.getMinutosColacion());
                System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                    + "getHrsTeoricas]"
                    + "Fecha: " + _detalle.getFechaEntradaMarca()
                    + ", minsColacion: " + detalleTurno.getMinutosColacion());
            }
            System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal."
                + "getHrsTeoricas]"
                + "Fecha: " + _detalle.getFechaEntradaMarca()
                + ", hrsTeoricas: " + hrsTeoricas
                + ", hrsTeoricasMenosColacion: " + hrsTeoricasMenosColacion);
            newDetail.setHhmmTeoricas(hrsTeoricasMenosColacion);
        }
        return newDetail;
        //***************Fin calculo de hrs teoricas menos colacion *********
        //******************************************************************
    } 
    
    /**
    * Retorna un valor para setear el campo Observacion de la
    * tabla 'detalle_asistencia'
    */
//    private String getObservacion(String _fecha, 
//            boolean _esFeriado, 
//            String _hhmmTurno, 
//            boolean _tieneMarcas, 
//            int _codDia){
//        String observacion="Libre";
//        if (_hhmmTurno.compareTo("") != 0){// no tiene marcas y tiene turno
//            observacion="Sin marcas";
//        }
//        
//        if (observacion.compareTo("Sin marcas") == 0 && _esFeriado && _codDia != 6) observacion = "Feriado";
//        System.out.println(WEB_NAME+"[ReporteAsistenciaSemanal.getObservacion]"
//            + " Fecha: " + _fecha
//                + ", codDia? " + _codDia
//            + ", esFeriado? " + _esFeriado
//            + ", tieneMarcas? " + _tieneMarcas
//            + ", hhmmTurno: " + _hhmmTurno
//            + ", observacion: " + observacion);
//        
//        return observacion;
//    }
    
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
