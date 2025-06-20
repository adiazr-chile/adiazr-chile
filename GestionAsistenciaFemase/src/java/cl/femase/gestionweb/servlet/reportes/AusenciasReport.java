/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.servlet.reportes;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.DetalleAusenciaBp;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.common.DatabaseLocator;
import cl.femase.gestionweb.vo.AsistenciaTotalesVO;
import cl.femase.gestionweb.vo.DetalleAusenciaVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
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
 * @author Alexander
 */
public class AusenciasReport extends BaseServlet {

    
    DatabaseLocator dbLocator;
    String m_dbpoolName;
    LinkedHashMap<String, String> hashPdfFilesPaths=new LinkedHashMap<>();
    
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
        String tipoParam = request.getParameter("tipo");
        String formato = request.getParameter("formato");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        FileGeneratedVO fileGenerated = null;
        String fileName = "";        
        String fullPath = "";
        
        try {
            dbLocator  = DatabaseLocator.getInstance();
            m_dbpoolName = appProperties.getDbPoolName();
        } catch (DatabaseException ex) {
            System.err.println("Error: "+ex.toString());
        }
        System.out.println(WEB_NAME+"[AusenciasReport.processRequestRut]"
            + "Abrir conexion a la BD. Datasource: " + m_dbpoolName);
        System.out.println(WEB_NAME+"[servlet.reportes."
            + "AusenciasReport]tipoParam: "+tipoParam);
        if (tipoParam.compareTo("1") == 0){
            if (_rutParam==null) return null;
            String jasperfile= "detalle_ausencias.jasper";
            System.out.println(WEB_NAME+"[AusenciasReport]"
                + "tipo: " + tipoParam
                + ", formato: " + formato);
            fileName = "ausencias_"+_rutParam+"."+formato;
            
            String jasperFileName = appProperties.getReportesPath() + File.separator +jasperfile;
            System.out.println(WEB_NAME+"[AusenciasReport]"
                + "jasperfile: " + jasperFileName);
            
            Map parameters = getParameters(request, _rutParam);
            if (parameters != null){
                Connection dbConn=null;
                try {
                    dbConn = dbLocator.getConnection(m_dbpoolName,"[AusenciasReport.doPost]");
                } catch (DatabaseException ex) {
                    System.err.println("[AusenciasReport]Error: "+ex.toString());
                }
                if (formato.compareTo("pdf") == 0){
                    System.out.println(WEB_NAME+"[processRequestRut]"
                        + "Generar PDF."
                        + " fileName: " + fileName);
                    try{
                        fullPath = 
                            appProperties.getPathExportedFiles()+File.separator+fileName;
                        JasperRunManager.runReportToPdfFile(jasperFileName, fullPath, parameters, dbConn);
                    }catch(Exception e){
                        System.err.println("[processRequestRut]"
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
     * Obtiene parametros a setear en el reporte
     */
    private HashMap getParameters(HttpServletRequest _request, 
            String _rutEmpleado){
        
        ServletContext application  = this.getServletContext();
        PropertiesVO appProperties  = (PropertiesVO)application.getAttribute("appProperties");
        EmpleadosBp empleadosBp     = new EmpleadosBp(appProperties);
        //DetalleAsistenciaBp detAsistenciaBp = new DetalleAsistenciaBp(appProperties);
        
        //String rutParam = _request.getParameter("rut");
        String startDateParam = _request.getParameter("startDate");
        String endDateParam = _request.getParameter("endDate");
        String empresaParam = _request.getParameter("empresa");
        String tipoParam = _request.getParameter("tipo");
        HashMap parameters = new HashMap();
                
        System.out.println(WEB_NAME+"AusenciasReport."
            + "getParameters. "
            + "Param rut= " + _rutEmpleado
            + ", startDate= " +startDateParam
            + ", endDate= " +endDateParam
            + ", tipo= " +tipoParam);
        EmpleadoVO infoempleado = empleadosBp.getEmpleado(empresaParam, _rutEmpleado);

        parameters.put("rut", _rutEmpleado);//cod_interno
        parameters.put("rut_full", infoempleado.getCodInterno());
        parameters.put("cod_interno", _rutEmpleado);
        parameters.put("nombre", infoempleado.getNombres() + " " +infoempleado.getApeMaterno());
        parameters.put("cargo", infoempleado.getNombreCargo());
        parameters.put("empresa_id", infoempleado.getEmpresa().getId());
        parameters.put("empresa_nombre", infoempleado.getEmpresa().getNombre());
        parameters.put("cenco_id", ""+infoempleado.getCentroCosto().getId());
        parameters.put("cenco_nombre", infoempleado.getCentroCosto().getNombre());
        parameters.put("fecha_ingreso", infoempleado.getFechaInicioContratoAsStr());
        parameters.put("startDate", startDateParam);
        parameters.put("endDate", endDateParam);
            
        System.out.println(WEB_NAME+"AusenciasReport."
            + "getParameters. "
            + "startDate= " +startDateParam
            + ", endDate= " +endDateParam);
        
        return parameters;
    }
    
    private List<EmpleadoVO> getEmpleados(String _empresaId,
            String _deptoId, int _cencoId, int _turnoId){
    
        List<EmpleadoVO> listaEmpleados;
        listaEmpleados = new ArrayList<>();
        
        LinkedHashMap<String, AsistenciaTotalesVO>  totalesAsistencia 
            = new LinkedHashMap<>();
        EmpleadosBp empleadosBp = new EmpleadosBp(new PropertiesVO());
        System.out.println(WEB_NAME+"[AusenciasReport.getEmpleados]"
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
        System.out.println(WEB_NAME+"[AusenciasReport.doGet]entrando...");
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
        String tipoParam = request.getParameter("tipo");
        String formato = request.getParameter("formato");
        String startDateParam = request.getParameter("startDate");
        String endDateParam = request.getParameter("endDate");
        
        ServletContext application  = this.getServletContext();
        PropertiesVO appProperties  = (PropertiesVO)application.getAttribute("appProperties");
        DetalleAusenciaBp detAusenciasBp = new DetalleAusenciaBp(appProperties);
        
        System.out.println(WEB_NAME+"[AusenciasReport.doPost]"
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
            System.out.println(WEB_NAME+"[AusenciasReport.doPost]"
                + "Generar informes para todos los empleados de "
                + "empresaId: " + empresaId
                +", deptoId: " + deptoId
                +", cencoId: " + cencoId);
            List<EmpleadoVO> listaEmpleados = 
                getEmpleados(empresaId, deptoId, cencoId, turnoId);
            FileGeneratedVO archivoGenerado;
            String nombreCenco="";
            for (EmpleadoVO empleado : listaEmpleados) {
                System.out.println(WEB_NAME+"[AusenciasReport.doPost]"
                    + "Generar informe para empleado rut: " + empleado.getRut()
                    +", nombre: " + empleado.getNombreCompleto());
                List<DetalleAusenciaVO> ausencias = 
                    detAusenciasBp.getDetallesAusencias("detalle_ausencias",
                    empleado.getRut(), 
                    null, startDateParam, 
                    endDateParam, -1, -1, -1, "fecha_inicio");
                if (!ausencias.isEmpty()){
                    if (nombreCenco.compareTo("") == 0) nombreCenco = empleado.getCencoNombre();
                    archivoGenerado = processRequestRut(request, response, empleado.getRut());
                    if (archivoGenerado != null){
                        System.out.println(WEB_NAME+"[AusenciasReport.doPost]Add "
                            + "archivo generado: " + archivoGenerado.getFileName());
                        archivosGenerados.put(empleado.getRut(), archivoGenerado.getFilePath());
                    }
                }   
            }
            if (archivosGenerados.size() > 0){
                archivoGenerado = mergePdfFiles(archivosGenerados, userConnected, nombreCenco);
                showFileToDownload(archivoGenerado, tipoParam, formato, response);
                File auxpdf=new File(archivoGenerado.getFilePath());
                auxpdf.delete();
            }else{
                session.setAttribute("mensaje", Constantes.REPORTE_SIN_DATOS);
                request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
            }
            
        }else{
            System.out.println(WEB_NAME+"[AusenciasReport.doPost]"
                + "Generar reporte asistencia solo para el rut: " + rutParam);
            FileGeneratedVO filegenerated=processRequestRut(request, response, rutParam);
            if (filegenerated != null){
                System.out.println(WEB_NAME+"[AusenciasReport.doPost]Add "
                    + "archivo generado: " + filegenerated.getFileName());
                showFileToDownload(filegenerated, tipoParam, formato, response);
                File auxpdf=new File(filegenerated.getFilePath());
                auxpdf.delete();
            }else{
                session.setAttribute("mensaje", Constantes.REPORTE_SIN_DATOS);
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
        System.out.println(WEB_NAME+"[AusenciasReport.showFileToDownload]Generar PDF."
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
            System.err.println("[AusenciasReport.showFileToDownload]Error al generar pdf: "+e.toString());
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
