/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.servlet.reportes;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.DetalleAusenciaBp;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.EmpresaBp;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.common.DatabaseLocator;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.dao.PermisosAdministrativosDAO;
import cl.femase.gestionweb.vo.AsistenciaTotalesVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.EmpresaVO;
import cl.femase.gestionweb.vo.PermisoAdministrativoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import net.sf.jasperreports.engine.JasperRunManager;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
 
/**
*
* Esta clase se encarga de obtener y generar un 
* Informe con los permisos administrativos solicitados desde el 
* 1-enero anio en curso hasta la fecha de generacion del informe.
* 
* @author Alexander
*/
@WebServlet(name = "PermisosAdministrativosCurrentYearReport", urlPatterns = {"/servlet/PermisosAdministrativosCurrentYearReport"})
public class PermisosAdministrativosCurrentYearReport extends BaseServlet {

    String JASPERFILENAME = "pa_hasta_la_fecha.jasper"; //informe_pa_current_date.jasper";
           
    
    DatabaseLocator dbLocator;
    String m_dbpoolName;
//    LinkedHashMap<String, String> hashPdfFilesPaths=new LinkedHashMap<>();
    HashMap<String, Double> m_parametrosSistema;
            
    /**
    * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
    * methods.
    *
    * @param request servlet request
    * @param response servlet response
    * @param _rutParam
    * 
    * @return 
    * @throws ServletException if a servlet-specific error occurs
    * @throws IOException if an I/O error occurs
    */
    protected FileGeneratedVO processRequestRut(HttpServletRequest request, 
                HttpServletResponse response, 
                String _rutParam)
            throws ServletException, IOException {
        
        HttpSession session         = request.getSession(true);
        ServletContext application  = this.getServletContext();
        PropertiesVO appProperties  = (PropertiesVO)application.getAttribute("appProperties");
        String tipoParam = "1";
        String formato = "pdf";
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        FileGeneratedVO fileGenerated = null;
        String fileName = "";        
        String fullPath = "";
        
        try {
            dbLocator  = DatabaseLocator.getInstance();
            m_dbpoolName = appProperties.getDbPoolName();
        } catch (DatabaseException ex) {
            System.err.println("[servlet.reportes."
                + "PermisosAdministrativosCurrentYearReport.processRequestRut]"
                + "Error: "+ex.toString());
        }
        System.out.println(WEB_NAME+"[PermisosAdministrativosCurrentYearReport.processRequestRut]"
            + "Abrir conexion a la BD. Datasource: " + m_dbpoolName);
        System.out.println(WEB_NAME+"[servlet.reportes."
            + "PermisosAdministrativosCurrentYearReport.processRequestRut]tipoParam: "+tipoParam);
        if (tipoParam.compareTo("1") == 0){
            if (_rutParam==null) return null;
            
            System.out.println(WEB_NAME+"[servlet.reportes."
                + "PermisosAdministrativosCurrentYearReport]"
                + "tipo: " + tipoParam
                + ", formato: " + formato);
            fileName = "pa_informe_" + _rutParam + "." + formato;
            
            String jasperFileName = appProperties.getReportesPath() 
                    + File.separator 
                    + JASPERFILENAME;
            System.out.println(WEB_NAME + "[servlet.reportes."
                + "PermisosAdministrativosCurrentYearReport.processRequestRut]"
                + "jasperfile: " + jasperFileName
                +", datasource a usar: "+m_dbpoolName);
            
            Map reportParameters = getParameters(userConnected.getUsername(),
                request, _rutParam);
            if (reportParameters != null){
                Connection dbConn=null;
                try {
                    dbConn = dbLocator.getConnection(m_dbpoolName,"[PermisosAdministrativosCurrentYearReport.doPost]");
                } catch (DatabaseException ex) {
                    System.err.println("[servlet.reportes."
                        + "PermisosAdministrativosCurrentYearReport."
                        + "processRequestRut]Error: "+ex.toString());
                }
                if (formato.compareTo("pdf") == 0){
                    System.out.println(WEB_NAME+"[servlet.reportes."
                        + "PermisosAdministrativosCurrentYearReport.processRequestRut]"
                        + "Generar PDF."
                        + " fileName: " + fileName);
                    try{
                        fullPath = 
                            appProperties.getPathExportedFiles()+File.separator+fileName;
                        JasperRunManager.runReportToPdfFile(jasperFileName, fullPath, reportParameters, dbConn);
                    
                        // Cargar el archivo .jasper
                        //JasperReport jasperReport = JasperCompileManager.compileReport("ruta/a/tu_informe.jrxml");

                        //--JRResultSetDataSource dataSource = new JRResultSetDataSource(resultSet);
                        //--JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, reportParameters, dataSource);

                        // Exportar a PDF
                        //--JasperExportManager.exportReportToPdfFile(jasperPrint, "ruta/a/tu_informe.pdf");
                    
                    
                    }catch(Exception e){
                        System.err.println("[servlet.reportes."
                            + "PermisosAdministrativosCurrentYearReport.processRequestRut]"
                            + "Error al generar pdf: "+e.toString());
                        e.printStackTrace();
                    }
                }
                fileGenerated = new FileGeneratedVO(fileName,fullPath);     
            }else{
                fileGenerated = null;
            }
        }
        
        return fileGenerated;
    }
    
    /**
    * 
    * Obtiene parametros a setear en el reporte
    */
    private HashMap getParameters(String _username, 
            HttpServletRequest _request, 
            String _rutEmpleado){
        
        ServletContext application  = this.getServletContext();
        HttpSession session = _request.getSession(true);
        PropertiesVO appProperties  = (PropertiesVO)application.getAttribute("appProperties");
        EmpleadosBp empleadosBp     = new EmpleadosBp(appProperties);
        EmpresaBp empresaBp         = new EmpresaBp(appProperties);
        SimpleDateFormat sdfyyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        int semestreActual = Utilidades.getSemestre(currentDate);
                    
        System.out.println(WEB_NAME+"[servlet.reportes."
            + "PermisosAdministrativosCurrentYearReport.getParameters]Inicio...");
        String empresaParam = _request.getParameter("empresa");
        int anioParam = Integer.parseInt(_request.getParameter("paramAnio"));
        
        HashMap parameters = new HashMap();
                
        System.out.println(WEB_NAME+"[servlet.reportes."
            + "PermisosAdministrativosCurrentYearReport.getParameters]. "
            + "Param rut= " + _rutEmpleado);
        EmpleadoVO infoEmpleado = empleadosBp.getEmpleado(empresaParam, _rutEmpleado);
        Date fechaInicioContrato = infoEmpleado.getFechaInicioContrato();
        Date fechaDesvinculacion = infoEmpleado.getFechaDesvinculacion();
        HashMap<String, Double> parametrosSistema = (HashMap<String, Double>)session.getAttribute("parametros_sistema");
        
        System.out.println(WEB_NAME+"[servlet.reportes."
            + "PermisosAdministrativosCurrentYearReport.getParameters]"
            + "Obtener Info resumen Permisos Administrativos (tabla 'permiso_administrativo')");
        double diasDisponibles  = 0;
        double diasUtilizados   = 0;
        PermisosAdministrativosDAO permisoAdminDao = 
            new PermisosAdministrativosDAO(appProperties);
        List<PermisoAdministrativoVO> infoPA = 
            permisoAdminDao.getResumenPermisosAdministrativos(empresaParam, 
                _rutEmpleado, anioParam, -1, -1, -1, "pa.run_empleado");
        PermisoAdministrativoVO saldoPA = new PermisoAdministrativoVO();
        if (!infoPA.isEmpty()){
            saldoPA = infoPA.get(0);
            diasDisponibles = saldoPA.getDiasDisponiblesSemestre1();
            diasUtilizados  = saldoPA.getDiasUtilizadosSemestre1();
            if (semestreActual == 2){
                diasDisponibles = saldoPA.getDiasDisponiblesSemestre2();
                diasUtilizados  = saldoPA.getDiasUtilizadosSemestre2();
            }
        }
        double MAXIMO_SEMESTRAL_DIAS_PA = parametrosSistema.get(Constantes.ID_PARAMETRO_MAXIMO_SEMESTRAL_DIAS_PA);
        if (MAXIMO_SEMESTRAL_DIAS_PA == 0) MAXIMO_SEMESTRAL_DIAS_PA = 3;
        
        System.out.println(WEB_NAME+"[servlet.reportes."
            + "PermisosAdministrativosCurrentYearReport.getParameters]Datos en resumen de permisos Administrativos."
            + " Num maximo de dias PA x semestre: " + MAXIMO_SEMESTRAL_DIAS_PA
            + ", dias utilizados: " + diasUtilizados        
            + ", dias disponibles: " + diasDisponibles
            + ", zona extrema?: " + infoEmpleado.getCentroCosto().getZonaExtrema());
       
        //************************************************************************************
        //************************************************************************************
        //cabecera empresa
        EmpresaVO empresa = empresaBp.getEmpresaByKey(empresaParam);
        parameters.put("empresa_id", infoEmpleado.getEmpresa().getId());
        parameters.put("empresa_nombre", empresa.getNombre());
        parameters.put("empresa_rut", empresa.getRut());
        parameters.put("empresa_direccion", empresa.getDireccion());
        //nuevos
        parameters.put("empresa_comuna", empresa.getComunaNombre());
        parameters.put("empresa_region", empresa.getRegionNombre());
        
        //**********************
        parameters.put("es_zona_extrema", infoEmpleado.getCentroCosto().getZonaExtrema());
        parameters.put("dias_acumulados", MAXIMO_SEMESTRAL_DIAS_PA);
        
        //Dias que restan (-)
        parameters.put("dias_utilizados", diasUtilizados);
        parameters.put("dias_disponibles", diasDisponibles);
        
        parameters.put("rut", _rutEmpleado);//cod_interno
        parameters.put("rut_full", infoEmpleado.getCodInterno());
        parameters.put("cod_interno", _rutEmpleado);
        parameters.put("nombre", infoEmpleado.getNombres() + " " + infoEmpleado.getApeMaterno());
        parameters.put("cargo", infoEmpleado.getNombreCargo());
        parameters.put("cenco_id", ""+infoEmpleado.getCentroCosto().getId());
        parameters.put("cenco_nombre", infoEmpleado.getCentroCosto().getNombre());
        parameters.put("fecha_ingreso", infoEmpleado.getFechaInicioContratoAsStr());
        //parameters.put("startDate", startDateParam);
        parameters.put("anio", anioParam);
            
        System.out.println(WEB_NAME+"[servlet.reportes."
            + "PermisosAdministrativosCurrentYearReport.getParameters]Fin.");
        
        return parameters;
    }
    
    private List<EmpleadoVO> getEmpleados(String _empresaId,
            String _deptoId, int _cencoId, int _turnoId){
    
        List<EmpleadoVO> listaEmpleados;
        listaEmpleados = new ArrayList<>();
        
        LinkedHashMap<String, AsistenciaTotalesVO>  totalesAsistencia 
            = new LinkedHashMap<>();
        EmpleadosBp empleadosBp = new EmpleadosBp(new PropertiesVO());
        System.out.println(WEB_NAME+"[servlet.reportes."
            + "PermisosAdministrativosCurrentYearReport.getEmpleados]"
            + "empresaId: " + _empresaId
            + ",deptoId: " + _deptoId    
            +", cenco Id: " + _cencoId);
        //empleados del cenco
        listaEmpleados = empleadosBp.getEmpleadosNew(_empresaId, 
            _deptoId, _cencoId, _turnoId);
                        
        return listaEmpleados;
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
        System.out.println(WEB_NAME+"[PermisosAdministrativosCurrentYearReport.doGet]entrando...");
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
        HttpSession session = request.getSession(true);
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        String empresaId = request.getParameter("empresa");
        String deptoId = request.getParameter("depto");
        String strCencoId = request.getParameter("cenco");
        String paramTurnoId = request.getParameter("turnoId");
        String rutParam = request.getParameter("rut");
        String tipoParam = "1";
        String formato = request.getParameter("formato");
        String startDateParam = request.getParameter("startDate");
        String endDateParam = request.getParameter("endDate");
        
        ServletContext application  = this.getServletContext();
        PropertiesVO appProperties  = (PropertiesVO)application.getAttribute("appProperties");
        DetalleAusenciaBp detAusenciasBp = new DetalleAusenciaBp(appProperties);
        
        m_parametrosSistema = (HashMap<String, Double>)session.getAttribute("parametros_sistema");
        System.out.println(WEB_NAME+"[PermisosAdministrativosCurrentYearReport.doPost]"
            + "empresaId: " + empresaId
            + ", deptoId: " + deptoId
            + ", strCencoId: " + strCencoId
            + ", rutParam: " + rutParam
            + ", tipoParam: " + tipoParam
            + ", formato: " + formato
            + ", startDate: " + startDateParam
            + ", startDate: " + endDateParam);
        int cencoId = -1;
        if (strCencoId != null) cencoId=Integer.parseInt(strCencoId);
         
        int turnoId = -1;
        if (paramTurnoId != null) turnoId=Integer.parseInt(paramTurnoId);
        
        if (rutParam != null 
                && rutParam.compareTo("todos") == 0
                && tipoParam.compareTo("3") != 0){
            //generar informes por centro de costo.
            LinkedHashMap<String, String> archivosGenerados = new LinkedHashMap<>();
            System.out.println(WEB_NAME+"[PermisosAdministrativosCurrentYearReport.doPost]"
                + "Generar informes para todos los empleados de "
                + "empresaId: " + empresaId
                +", deptoId: " + deptoId
                +", cencoId: " + cencoId);
            List<EmpleadoVO> listaEmpleados = 
                getEmpleados(empresaId, deptoId, cencoId, turnoId);
            FileGeneratedVO archivoGenerado;
            String nombreCenco="";
            for (EmpleadoVO empleado : listaEmpleados) {
                System.out.println(WEB_NAME+"[PermisosAdministrativosCurrentYearReport.doPost]"
                    + "Generar informe para empleado rut: " + empleado.getRut()
                    +", nombre: " + empleado.getNombreCompleto());
                //List<DetalleAusenciaVO> ausencias = detAusenciasBp.getDetallesAusencias(empleado.getRut(), 
                //        null, startDateParam, endDateParam, -1, -1, -1, "fecha_inicio");
                //if (!ausencias.isEmpty()){
                    if (nombreCenco.compareTo("") == 0) nombreCenco = empleado.getCencoNombre();
                    archivoGenerado = processRequestRut(request, response, empleado.getRut());
                    if (archivoGenerado != null){
                        System.out.println(WEB_NAME+"[PermisosAdministrativosCurrentYearReport.doPost]Add "
                            + "archivo generado: " + archivoGenerado.getFileName());
                        archivosGenerados.put(empleado.getRut(), archivoGenerado.getFilePath());
                    }
                //}   
            }
            if (archivosGenerados.size() > 0){
                archivoGenerado = mergePdfFiles(archivosGenerados, userConnected, nombreCenco);
                showFileToDownload(archivoGenerado, tipoParam, formato, response);
                File auxpdf=new File(archivoGenerado.getFilePath());
                auxpdf.delete();
            }else{
                session.setAttribute("mensaje", Constantes.NO_HAY_DATOS_REPORTE);
                request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
            }
            
        }else{
            System.out.println(WEB_NAME+"[PermisosAdministrativosCurrentYearReport.doPost]"
                + "Generar reporte asistencia solo para el rut: " + rutParam);
            FileGeneratedVO filegenerated=processRequestRut(request, response, rutParam);
            if (filegenerated != null){
                System.out.println(WEB_NAME+"[PermisosAdministrativosCurrentYearReport.doPost]Add "
                    + "archivo generado: " + filegenerated.getFileName());
                showFileToDownload(filegenerated, tipoParam, formato, response);
                File auxpdf=new File(filegenerated.getFilePath());
                auxpdf.delete();
            }else{
                session.setAttribute("mensaje", Constantes.NO_HAY_DATOS_REPORTE);
                request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
            }
        }
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
        //pdf
        System.out.println(WEB_NAME+"[PermisosAdministrativosCurrentYearReport.showFileToDownload]Generar PDF."
            + " fileName: " + _fileGenerated.getFileName());
        try{
            File pointToFile = new File(_fileGenerated.getFilePath());
            ByteArrayInputStream byteArrayInputStream = 
                new ByteArrayInputStream(FileUtils.readFileToByteArray(pointToFile));
            _response.setContentType(contentType);
            _response.addHeader("Content-Disposition", 
                "attachment; filename=" + _fileGenerated.getFileName());
            OutputStream responseOutputStream = _response.getOutputStream();

            byte[] buf = new byte[4096];
            int len = -1;

            while ((len = byteArrayInputStream.read(buf)) != -1) {
              responseOutputStream.write(buf, 0, len);
            }
            responseOutputStream.flush();
            responseOutputStream.close();
        }catch(Exception e){
            System.err.println("[PermisosAdministrativosCurrentYearReport.showFileToDownload]Error al generar pdf: "+e.toString());
            e.printStackTrace();
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
        System.out.println(WEB_NAME+"[servlet.reportes."
            + "PermisosAdministrativosCurrentYearReport.mergePdfFiles]"
            + "uniendo archivos en uno solo. "
            + "Path: " + mergedFile.getAbsolutePath());

        try
        {
            PDFMergerUtility mergePdf = new PDFMergerUtility();
            for( String key : _archivos.keySet() ){
                String pathFile = _archivos.get(key);
                File itFile = new File(pathFile);
                System.out.println(WEB_NAME+"[servlet.reportes."
                    + "PermisosAdministrativosCurrentYearReport.mergePdfFiles]itera archivo " + pathFile);
                
                mergePdf.addSource(itFile);
                
            }
            mergePdf.setDestinationFileName(archivoGenerado.getFilePath());
            mergePdf.mergeDocuments();
            
            for( String key : _archivos.keySet() ){
                String pathFile2 = _archivos.get(key);
                File itFile2 = new File(pathFile2);
                System.out.println(WEB_NAME+"[servlet.reportes."
                    + "PermisosAdministrativosCurrentYearReport.mergePdfFiles]Eliminando archivo " + pathFile2);
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
