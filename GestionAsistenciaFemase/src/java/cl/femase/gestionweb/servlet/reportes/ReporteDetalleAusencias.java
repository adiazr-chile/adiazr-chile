/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.servlet.reportes;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.DetalleAusenciaBp;
import cl.femase.gestionweb.common.PdfUtilWriter;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.DetalleAusenciaVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Alexander
 */
public class ReporteDetalleAusencias extends BaseServlet {

    private PropertiesVO appProperties;
    private static final int BUFSIZE = 4096;
    
    @Override
    public void init() throws ServletException {
        super.init();
    }
    
    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(true);
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        
        String formato      = request.getParameter("formato");
        /** filtros de busqueda */
        String rutEmpleado=null;
        String rutAutorizador=null; 
        String fechaIngresoInicio=null; 
        String fechaIngresoFin=null;
        int ausenciaId=-1;

        if (request.getParameter("paramRutEmpleado") != null) 
                rutEmpleado = request.getParameter("paramRutEmpleado");
        if (request.getParameter("paramRutAutorizador") != null) 
            rutAutorizador  = request.getParameter("paramRutAutorizador");
        if (request.getParameter("paramFechaIngresoInicio") != null) 
            fechaIngresoInicio  = request.getParameter("paramFechaIngresoInicio");
        if (request.getParameter("paramFechaIngresoFin") != null) 
            fechaIngresoFin  = request.getParameter("paramFechaIngresoFin");
        if (request.getParameter("paramAusenciaId") != null) 
            ausenciaId  = Integer.parseInt(request.getParameter("paramAusenciaId"));

        System.out.println("cl.femase.gestionweb."
            + "servlet.reportes."
            + "ReporteDetalleAusencias."
            + "processRequest(). "
            + "formato= " +formato);
        
        ServletContext application = this.getServletContext();
        appProperties=(PropertiesVO)application.getAttribute("appProperties");
        PdfUtilWriter auxpdfWriter=new PdfUtilWriter(session);
        DetalleAusenciaBp auxnegocio = new DetalleAusenciaBp(appProperties);
        
        if (userConnected != null){
            List<DetalleAusenciaVO> listaObjetos = 
                auxnegocio.getDetallesAusencias("detalle_ausencias",
                    rutEmpleado,
                    rutAutorizador, 
                    fechaIngresoInicio, 
                    fechaIngresoFin,
                    ausenciaId,
                    0, 
                    0, 
                    "detalle_ausencia.rut_empleado asc");

            session.setAttribute("detalleAusencias|"+userConnected.getUsername(), listaObjetos);
            
            //// /**Genera el PDF con la informacion obtenida*/
            String outputfilename="detalle_ausencias.pdf";
            String fullPDFXLSfilename = appProperties.getPathExportedFiles()
                    + File.separator
                    +"reportes"
                    + File.separator
                    +outputfilename;
            File pointToFile = new File(fullPDFXLSfilename);
            boolean isOk = auxpdfWriter.doReporteDetalleAusencias(listaObjetos,
                appProperties.getImagesPath() + File.separator + "logo_femase_encabezado.png",
                "",
                "",
                fullPDFXLSfilename);
////
    ////        String excelsalida  = saveDataToExcel(rows, auxfilename,
    ////            "RESUMEN TRIMESTRAL POR NEMOTECNICO",
    ////            "Trimestral",
    ////            "Periodo:" + strstartdate +" al "+ strenddate, 
    ////            nemotecnico,"TRIMESTRE");
    ////        System.out.println("--->PATH FINAL: "+fullPDFXLSfilename);        
    ////        pointToFile = new File(fullPDFXLSfilename);
    ////
            ByteArrayInputStream byteArrayInputStream = 
                new ByteArrayInputStream(FileUtils.readFileToByteArray(pointToFile));

            response.reset();
            response.setHeader("Content-disposition", "inline;filename="+outputfilename);
            response.setContentType("application/pdf");
            if (formato.compareTo("excel") == 0){
                response.setContentType("application/vnd.ms-excel");
            }
            int bytesRead;
            byte[] byteArray = new byte[1024];
            while((bytesRead = byteArrayInputStream.read(byteArray)) != -1) {
                response.getOutputStream().write(byteArray, 0, bytesRead);	
            }

            response.getOutputStream().flush();
            response.getOutputStream().close();
                
            if (pointToFile.delete()){
                System.out.println("cl.femase.gestionweb."
                    + "servlet.ReporteDetalleAusencias."
                    + "Archivo PDF eliminado con exito...");
            }
            
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            System.err.println("Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
        }              
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
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
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        System.out.println("cl.femase.gestionweb.servlet."
                + "reportes.ReporteDetalleAusencias.doGet(). Formato: "+request.getParameter("formato"));
        if (request.getParameter("formato")!=null && 
            request.getParameter("formato").compareTo("csv")==0){
            
            System.out.println("[GestionFemase.ReporteDetalleAusencias]"
                + "userConnected: " + userConnected);
                           
            /** filtros de busqueda */
            String rutEmpleado=null;
            String rutAutorizador=null; 
            String fechaIngresoInicio=null; 
            String fechaIngresoFin=null;
            int ausenciaId=-1;

            if (request.getParameter("paramRutEmpleado") != null) 
                    rutEmpleado = request.getParameter("paramRutEmpleado");
            if (request.getParameter("paramRutAutorizador") != null) 
                rutAutorizador  = request.getParameter("paramRutAutorizador");
            if (request.getParameter("paramFechaIngresoInicio") != null) 
                fechaIngresoInicio  = request.getParameter("paramFechaIngresoInicio");
            if (request.getParameter("paramFechaIngresoFin") != null) 
                fechaIngresoFin  = request.getParameter("paramFechaIngresoFin");
            if (request.getParameter("paramAusenciaId") != null) 
                ausenciaId  = Integer.parseInt(request.getParameter("paramAusenciaId"));
            
            ServletContext application = this.getServletContext();
            appProperties=(PropertiesVO)application.getAttribute("appProperties");
            DetalleAusenciaBp auxnegocio = new DetalleAusenciaBp(appProperties);
        
            if (userConnected != null){
                List<DetalleAusenciaVO> listaObjetos = 
                auxnegocio.getDetallesAusencias("detalle_ausencias", 
                    rutEmpleado,
                    rutAutorizador, 
                    fechaIngresoInicio, 
                    fechaIngresoFin,
                    ausenciaId,
                    0, 
                    0, 
                    "detalle_ausencia.rut_empleado asc");

                session.setAttribute("detalleAusencias|"+userConnected.getUsername(),listaObjetos);
                request.getRequestDispatcher("/DataExportServlet?type=detalleAusenciasToCSV").forward(request, response);//frameset
            }else{
                session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                        +" no valida");
                System.err.println("Sesion de usuario "+request.getParameter("username")
                        +" no valida");
                request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
            }   
        
        }else{
            //guardar reporte 
            PdfUtilWriter auxpdfWriter=new PdfUtilWriter(session);
            List<DetalleAusenciaVO> listaDetalle = 
                (List<DetalleAusenciaVO>)session.getAttribute("detalleAusencias|"+userConnected.getUsername());

            String outputfilename="detalleAusencias.pdf";
                String fullPDFXLSfilename = appProperties.getPathExportedFiles()
                    + File.separator
                    +"reportes"
                    + File.separator
                    +outputfilename;
                File pointToFile = new File(fullPDFXLSfilename);
                boolean isOk = auxpdfWriter.doReporteDetalleAusencias(listaDetalle,
                    appProperties.getImagesPath() + File.separator + "logo_femase_encabezado.png",
                    "",
                    "",
                    fullPDFXLSfilename);

                ByteArrayInputStream byteArrayInputStream = 
                    new ByteArrayInputStream(FileUtils.readFileToByteArray(pointToFile));

                response.reset();
                response.setHeader("Content-disposition", "inline;filename="+outputfilename);
                response.setContentType("application/pdf");

                int bytesRead;
                byte[] byteArray = new byte[1024];
                while((bytesRead = byteArrayInputStream.read(byteArray)) != -1) {
                    response.getOutputStream().write(byteArray, 0, bytesRead);	
                }

                response.getOutputStream().flush();
                response.getOutputStream().close();
                
                if (pointToFile.delete()){
                    System.out.println("cl.femase.gestionweb."
                        + "servlet.ReporteDetalleAusencias."
                        + "Archivo PDF eliminado con exito...");
                }
            //setResponseHeaders(response);processRequest(request, response);
        }
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        setResponseHeaders(response);processRequest(request, response);
    }

    
////    /**
////     * Permite generar un archivo excel con los
////     * datos obtenidos desde la BD
////     */
////    private String saveDataToExcel(LinkedHashMap<String,CertificadoResumenVO> _listadatos,
////            String _outputfilename,String _tituloGrilla, String _nombreHoja, 
////            String _labelFechas, String _symbol, String _labelColumnaPeriodo){
////        //Paso 5: generar excel
////        String nombreArchivo;
////        ExcelWriter ew = new ExcelWriter();
////        ew.setPathOut(appProperties.getPathExportedFiles());
////        
////        //Calendar cal        = Calendar.getInstance();
////        //SimpleDateFormat sdf    = new SimpleDateFormat("yyyyMMddHHmmss");
////        String strpriceformat="9.02f";
////        InstrumentosManager nemosManager = 
////                new InstrumentosManager(appProperties);
////        strpriceformat =  nemosManager.getFormatoPrecio(_symbol, true);
////        
////        System.out.println("[Certificados]Formato precio instrumento "
////                +strpriceformat);
////        
////        System.out.println("[Certificados]Excel de salida: "
////                +_outputfilename);
////        
////        SitrelFormatter sformat = new SitrelFormatter(strpriceformat);
////        String auxstrMaxPrice="";
////        String auxstrMinPrice="";
////        String auxstrMedPrice="";
////        String auxstrClosingPrice="";
////        //limpia la data del archivo
////        ew.createWorkBook(_outputfilename);
////        ew.createSheet(_nombreHoja);
////        ew.setColumnsSize(7, 20);
////        
////        //FILA 0
////        //Writes a String to position 0, in this case A1
////        ew.write(0,"Bolsa Electronica de Chile, Bolsa de Valores");
////        ew.mergeCells(0, 0, 0, 2);
////        
////        ew.nextRow();
////        ew.write(0,_tituloGrilla);
////        ew.mergeCells(1, 0, 1, 2);
////        
////        ew.nextRow();
////        ew.write(0,_labelFechas);
////        ew.mergeCells(2, 0, 2, 2);
////        
////        //FILA 1, vacia
////        ew.nextRow();
////        ew.nextRow();
////        
////        ew.setUseBorder(false);
////        
////        String[] cellheaders = new String[9];
////        cellheaders[0]  = "A�O";
////        cellheaders[1]  = _labelColumnaPeriodo;
////        cellheaders[2]  = "CANTIDAD";
////        cellheaders[3]  = "NUM NEG.";
////        cellheaders[4]  = "MONTO";
////        cellheaders[5]  = "P.MAX";
////        cellheaders[6]  = "P.MIN";
////        cellheaders[7]  = "P.MED";
////        cellheaders[8]  = "P.CIE";
////        
////        //nextRow(1) moves the cell cursor down 1 rows
////        ew.nextRow();
////        
////        // ----------- Seteando Headers ----------------------
////        for (int i = 0; i < cellheaders.length; i++){
////            ew.write(i,cellheaders[i]);
////            ew.makeBold(i);
////        }
////        
////        //--------------Itera la lista de objetos, seteando los datos de cada celda-----------------
////        Iterator<CertificadoResumenVO> itdatos = _listadatos.values().iterator();
////        while(itdatos.hasNext()){
////            CertificadoResumenVO detail = itdatos.next();
////            ew.nextRow();
////            
////            if (detail.getNumberOfTrades()>0){
////                ew.writeInt(0, detail.getYear());
////                ew.write(1, detail.getLabel());
////                ew.writeDouble(2, detail.getSumQuantity());
////                ew.writeDouble(3, detail.getNumberOfTrades());
////                
////                ew.writeDouble(4, detail.getSumAmount());
////                /** Precios */
////                sformat = new SitrelFormatter(strpriceformat);
////                auxstrMaxPrice = sformat.format(detail.getMaxPrice());
////                auxstrMinPrice = sformat.format(detail.getMinPrice());
////                auxstrMedPrice = sformat.format(detail.getMedPrice());
////                auxstrClosingPrice = sformat.format(detail.getClosingPrice());
////        
////                ew.write(5, auxstrMaxPrice);
////                ew.write(6, auxstrMinPrice);
////                ew.write(7, auxstrMedPrice);
////                ew.write(8, auxstrClosingPrice);
////                
////////                ew.writeDouble(5, detail.getMaxPrice());
////////                ew.writeDouble(6, detail.getMinPrice());
////////                ew.writeDouble(7, detail.getMedPrice());
////////                ew.writeDouble(8, detail.getClosingPrice());
////               
////            }
////            
////        }//fin iteracion datos  
////        
////        ew.nextRow();
////        ew.write(0, "Fuente: Bolsa Electr�nica de Chile.  www.bolchile.cl");
////                
////        //closeWorkbook closes the worksheet and flusehes the buffer
////        ew.closeWorkbook();
////
////        nombreArchivo=ew.getCompletePathFile();
////        return nombreArchivo;
////    }
    
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
