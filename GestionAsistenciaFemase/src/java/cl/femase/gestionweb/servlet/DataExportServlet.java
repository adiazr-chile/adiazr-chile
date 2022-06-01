/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.servlet;

import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.MarcacionVirtualBp;
import cl.femase.gestionweb.business.MarcasBp;
import cl.femase.gestionweb.business.TipoMarcaManualBp;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.dao.DispositivoMovilDAO;
import cl.femase.gestionweb.dao.LogErrorDAO;
import cl.femase.gestionweb.vo.AsignacionTurnoVO;
import cl.femase.gestionweb.vo.ComunaVO;
import cl.femase.gestionweb.vo.DetalleAusenciaVO;
import cl.femase.gestionweb.vo.DispositivoMovilVO;
import cl.femase.gestionweb.vo.DispositivoVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.EventoMantencionVO;
import cl.femase.gestionweb.vo.LogErrorVO;
import cl.femase.gestionweb.vo.MarcasEventosVO;
import cl.femase.gestionweb.vo.MarcaRechazoVO;
import cl.femase.gestionweb.vo.MarcaVO;
import cl.femase.gestionweb.vo.MarcacionVirtualVO;
import cl.femase.gestionweb.vo.OrganizacionEmpresaVO;
import cl.femase.gestionweb.vo.PermisoAdministrativoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.TipoMarcaManualVO;
import cl.femase.gestionweb.vo.UsuarioCentroCostoVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import cl.femase.gestionweb.vo.VacacionesVO;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Alexander
 */
public class DataExportServlet extends BaseServlet {

    private static final int BUFSIZE = 4096;

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        /**
        * Tipos de exportacion, valores posibles de paramType:
        *      "empleadosToCSV"   : lista de empleados a formato CSV
        *      "organizacionToCSV"  : lista de organizacion empresa a formato CSV
        *
        *      
        */
        //sesion del usuario y datos fijos para cada servlet
        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        String paramType = request.getParameter("type");
        String filePathOut="";
        switch(paramType){
            case "empleadosToCSV":
               filePathOut = exportEmpleadosToCSV(request, response);
               break;
            case "empleadosCaducadosToCSV":
                filePathOut = exportEmpleadosCaducadosToCSV(request, response);
                break;        
            case "infovacacionesCSV":
                filePathOut = exportInfoVacacionesToCSV(request, response);
                break;
            case "asignacionMarcaVirtualCSV":
                filePathOut = exportAsignacionMarcacionVirtualToCSV(request, response);
                break;
            case "movilesToCSV":
                filePathOut = exportMovilesToCSV(request, response);
                break;    
            case "vacacioneslogCSV":
                filePathOut = exportInfoVacacionesLogToCSV(request, response);
                break;    
            case "organizacionToCSV":
                filePathOut = exportOrganizacionToCSV(request, response);
                break;
            case "eventosToCSV":
                filePathOut = exportEventosToCSV(request, response);
                break;    
            case "dispositivosToCSV":
                filePathOut = exportDispositivosToCSV(request, response);
                break;
            case "usuariosToCSV":
                filePathOut = exportUsuariosToCSV(request, response);
                break;
            case "detalleAusenciasToCSV":
                filePathOut = exportDetalleAusenciasToCSV(request, response);
                break;
            case "marcasRechazadasToCSV":
                filePathOut = exportMarcasRechazadasToCSV(request, response);
                break;    
            case "marcasModifCSV":
                filePathOut = exportMarcasModificadasToCSV(request, response);
                break;
            case "asignacionTurnosToCSV":
                filePathOut = exportAsignacionTurnosToCSV(request, response);
                break;    
            case "marcasCSV":
                filePathOut = exportMarcasToCSV(request, response);
                break;
            case "logErrorToCSV":
                filePathOut = exportLogErrorToCSV(request, response);
                break;
            case "resumenPACSV":
                filePathOut = exportResumenPermisosAdministrativosToCSV(request, response);
                break;    
        }
            
        if (filePathOut.compareTo("") > 0){
            //generar link para download del archivo csv
            File file = new File(filePathOut);
            int length   = 0;
            ServletOutputStream outStream = response.getOutputStream();
            ServletContext context  = getServletConfig().getServletContext();
            String mimetype = context.getMimeType(filePathOut);

            // sets response content type
            if (mimetype == null) {
                mimetype = "application/octet-stream";
            }
            response.setContentType(mimetype);
            response.setContentLength((int)file.length());
            String fileName = (new File(filePathOut)).getName();

            // sets HTTP header
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            byte[] byteBuffer = new byte[BUFSIZE];
            DataInputStream in = new DataInputStream(new FileInputStream(file));

            // reads the file's bytes and writes them to the response stream
            while ((in != null) && ((length = in.read(byteBuffer)) != -1))
            {
                outStream.write(byteBuffer,0,length);
            }

            in.close();
            outStream.close();

            if (file.delete()){
                System.out.println("[DataExportServlet."
                    + "processRequest]"
                    + "Archivo CSV eliminado con exito...");
            }
        }
    } 

    /**
    * Escribe lineas en archivo, en formato CSV con la lista de empleados
    * @param request
    * @param response
    * @return 
    * @throws javax.servlet.ServletException 
    * @throws java.io.IOException 
    */
    protected String exportEmpleadosToCSV(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        String filePath="";
        PrintWriter outfile=null;
        try {
            Calendar calNow=Calendar.getInstance();
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            HttpSession session = request.getSession(true);
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
                        
            String separatorFields = ";";
            List<ComunaVO> comunasList = 
                (List<ComunaVO>)session.getAttribute("comunas");  
            HashMap<String, ComunaVO> comunasMap = new HashMap<String, ComunaVO>();
            for (ComunaVO comuna : comunasList) {
                comunasMap.put(""+comuna.getId(), comuna);
            }
            
            ArrayList<EmpleadoVO> empleadosList = 
                (ArrayList<EmpleadoVO>)session.getAttribute("empleados|"+userConnected.getUsername());
            if (empleadosList!=null && empleadosList.size()>0){
                filePath = appProperties.getPathExportedFiles()+
                    File.separator+
                    userConnected.getUsername()+"_empleados.csv";
                System.out.println("cl.femase.gestionweb.servlet."
                    + "DataExportServlet.exportEmpleadosToCSV(). filePath:" + filePath);
                FileWriter filewriter = new FileWriter(filePath);
                outfile     = new PrintWriter(filewriter);

                Iterator<EmpleadoVO> iterador = empleadosList.listIterator();
                NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "ES"));
                Utilidades utils=new Utilidades();
                int filas=0;

                //cabecera
                outfile.println("Rut;Nombres;Paterno;"
                        + "Materno;Sexo;Fec Nac;"
                        + "Direccion;Comuna;Email;"
                        + "Empresa;Departamento;"
                        + "Centro Costo;"
                        + "Fec Inicio Contrato;"
                        + "Estado;Fono Fijo;"
                        + "Fono Movil;");

                while( iterador.hasNext() ) {
                    filas++;
                    EmpleadoVO detailObj = iterador.next();

                    //escribir lineas en el archivo
                    outfile.print(detailObj.getRut());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getNombres());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getApePaterno());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getApeMaterno());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getSexo());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getFechaNacimientoAsStr());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getDireccion());
                    outfile.print(separatorFields);
                    outfile.print(comunasMap.get(""+detailObj.getComunaId()));
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getEmail());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getEmpresaNombre());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getDeptoNombre());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getCencoNombre());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getFechaInicioContratoAsStr());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getEstado());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getFonoFijo());
                    outfile.print(separatorFields);

                    if (filas < empleadosList.size()){
                        outfile.println(detailObj.getFonoMovil());
                    }else{
                        outfile.print(detailObj.getFonoMovil());
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
            if (outfile != null) outfile.close();
        }

        return filePath;
    }
    
    /**
    * Escribe lineas en archivo, en formato CSV 
    * con la lista de empleados con contratos caducados.
    * 
    * @param request
    * @param response
    * @return 
    * @throws javax.servlet.ServletException 
    * @throws java.io.IOException 
    */
    protected String exportEmpleadosCaducadosToCSV(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        String filePath="";
        PrintWriter outfile=null;
        try {
            Calendar calNow=Calendar.getInstance();
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            HttpSession session = request.getSession(true);
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
            EmpleadosBp empleadosbp = new EmpleadosBp(appProperties);
            
            String separatorFields = ";";
            
            //ArrayList<EmpleadoVO> empleadosList = 
            //    (ArrayList<EmpleadoVO>)session.getAttribute("empleados|"+userConnected.getUsername());
            String empresaid    = (String)session.getAttribute("empresaId");
            String deptoid      = (String)session.getAttribute("deptoId");
            int cencoid         = Integer.parseInt((String)session.getAttribute("cencoId"));
            int filtroIdTurno   = (Integer)session.getAttribute("filtroIdTurno");
            String filtroRut    = (String)session.getAttribute("filtroRut");
            String filtroNombre = (String)session.getAttribute("filtroNombre");
            
            String strcencos = "";
            if (cencoid == -1){
                Iterator<UsuarioCentroCostoVO> cencosIt = userConnected.getCencos().iterator();
                while (cencosIt.hasNext()) {
                    strcencos += cencosIt.next().getCcostoId()+",";
                }
                strcencos = strcencos.substring(0, strcencos.length()-1);
                System.out.println("[EmpleadosCaducadosController."
                    + "processRequest]"
                    + "Mostrar empleados "
                    + "para los cencos seleccionados: " + strcencos);
            }
            
            List<EmpleadoVO> listaEmpleados = empleadosbp.getCaducados(empresaid, 
                deptoid, 
                cencoid,
                -1,
                filtroIdTurno,
                filtroRut, 
                filtroNombre, 
                null, 
                null,
                -1,
                strcencos,
                0, 
                0, 
                "empl_rut asc");
            
            if (listaEmpleados != null && listaEmpleados.size()>0){
                filePath = appProperties.getPathExportedFiles()+
                    File.separator+
                    userConnected.getUsername()+"_contratos_caducados.csv";
                System.out.println("[DataExportServlet."
                    + "exportEmpleadosCaducadosToCSV]"
                    + "filePath:" + filePath);
                FileWriter filewriter = new FileWriter(filePath);
                outfile     = new PrintWriter(filewriter);

                HashMap<Integer,String> hashEstados=new HashMap();
                hashEstados.put(1, "Vigente");
                hashEstados.put(2, "No Vigente");
            
                Iterator<EmpleadoVO> iterador = listaEmpleados.listIterator();
                NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "ES"));
                Utilidades utils=new Utilidades();
                int filas=0;

                //cabecera
                outfile.println(
                    "Empresa;"
                    + "Departamento;"
                    + "Centro Costo;"
                    + "Rut;"
                    + "Nombres;"
                    + "Paterno;"
                    + "Materno;"
                    + "Contrato indefinido;"
                    + "Estado;"
                    + "Fec Inicio Contrato;"
                    + "Fec Termino Contrato;");

                while( iterador.hasNext() ) {
                    filas++;
                    EmpleadoVO detailObj = iterador.next();

                    //escribir lineas en el archivo
                    outfile.print(detailObj.getEmpresaNombre());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getDeptoNombre());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getCencoNombre());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getRut());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getNombres());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getApePaterno());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getApeMaterno());
                    outfile.print(separatorFields);
                    
                    String contratoIndef = "Si";
                    if (!detailObj.isContratoIndefinido()) contratoIndef="No";
                    
                    outfile.print(contratoIndef);
                    outfile.print(separatorFields);
                    
                    outfile.print(hashEstados.get(detailObj.getEstado()));
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getFechaInicioContratoAsStr());
                    outfile.print(separatorFields);
                    
                    if (filas < listaEmpleados.size()){
                        outfile.println(detailObj.getFechaTerminoContratoAsStr());
                    }else{
                        outfile.print(detailObj.getFechaTerminoContratoAsStr());
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
            if (outfile != null) outfile.close();
        }

        return filePath;
    }
    
    /**
     * Escribe lineas en archivo, en formato CSV con la lista de eventos
     * @param request
     * @param response
     * @return 
     * @throws javax.servlet.ServletException 
     * @throws java.io.IOException 
     */
    protected String exportEventosToCSV(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        String filePath="";
        PrintWriter outfile=null;
        try {
            Calendar calNow=Calendar.getInstance();
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            HttpSession session = request.getSession(true);
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
                        
            String separatorFields = ";";
            ArrayList<EventoMantencionVO> eventosList = 
                (ArrayList<EventoMantencionVO>)session.getAttribute("eventos|"+userConnected.getUsername());
            if (eventosList != null && eventosList.size() > 0){
                filePath = appProperties.getPathExportedFiles()+
                    File.separator+
                    userConnected.getUsername()+"_eventos.csv";
                System.out.println("cl.femase.gestionweb.servlet."
                    + "DataExportServlet.exportEventosToCSV(). filePath:" + filePath);
                FileWriter filewriter = new FileWriter(filePath);
                outfile     = new PrintWriter(filewriter);
                Iterator<EventoMantencionVO> iterador = eventosList.listIterator();
                NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "ES"));
                Utilidades utils=new Utilidades();
                int filas=0;

                //cabecera
                outfile.println("Usuario;Evento;IP;"
                        + "Tipo Evento;Fecha-hora");

                while( iterador.hasNext() ) {
                    filas++;
                    EventoMantencionVO detailObj = iterador.next();

                    LinkedHashMap<String,String> tipos = 
                        (LinkedHashMap<String,String>)session.getAttribute("tipos_eventos");
                    String tipoEvento=tipos.get(detailObj.getTipoEventoId());
                    //escribir lineas en el archivo
                    outfile.print(detailObj.getUsername());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getDescripcion());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getIp());
                    outfile.print(separatorFields);
                    outfile.print(tipoEvento);
                    outfile.print(separatorFields);

                    if (filas < eventosList.size()){
                        outfile.println(detailObj.getFechaHoraAsStr());
                    }else{
                        outfile.print(detailObj.getFechaHoraAsStr());
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

    /**
    * Escribe lineas en archivo, en formato CSV con informacion de vacaciones
    * 
    * @param request
    * @param response
    * @return 
    * @throws javax.servlet.ServletException 
    * @throws java.io.IOException 
    */
    protected String exportInfoVacacionesToCSV(HttpServletRequest request, 
            HttpServletResponse response)
    throws ServletException, IOException {
        System.out.println("[DataExportServlet."
            + "exportInfoVacacionesToCSV]entrando...");
        String filePath="";
        PrintWriter outfile=null;
        try {
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            HttpSession session = request.getSession(true);
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
                        
            String separatorFields = ";";
            ArrayList<VacacionesVO> vacacionesList = 
                (ArrayList<VacacionesVO>)session.getAttribute("infovacaciones|"+userConnected.getUsername());
            
            if (vacacionesList != null && vacacionesList.size() > 0){
                filePath = appProperties.getPathExportedFiles()+
                    File.separator+
                    userConnected.getUsername()+"_infovacaciones.csv";
                System.out.println("[DataExportServlet."
                    + "exportInfoVacacionesToCSV]filePath:" + filePath);
                FileWriter filewriter = new FileWriter(filePath);
                outfile     = new PrintWriter(filewriter);
                Iterator<VacacionesVO> iterador = vacacionesList.listIterator();
                //NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "ES"));
                //Utilidades utils=new Utilidades();
                int filas=0;

                //cabecera
                outfile.println("Empresa;"
                    + "Departamento;"
                    + "Centro de costo;"
                    + "Zona Extrema;"
                    + "Rut empleado;"
                    + "Nombre empleado;"
                    + "Fecha calculo;"
                    + "Fecha inicio contrato;"
                    + "Num cotizaciones;"
                    + "AFP;"
                    + "Fecha emision certificado;"    
                    + "Dias progresivos;"
                    + "Dias especiales;"
                    + "Dias adicionales;"    
                    + "Inicio ultimas vacaciones;"
                    + "Fin ultimas vacaciones;"    
                    + "Saldo");

                while( iterador.hasNext() ) {
                    filas++;
                    VacacionesVO detailObj = iterador.next();

                    //escribir lineas en el archivo
                    outfile.print(detailObj.getEmpresaId());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getDeptoNombre());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getCencoNombre());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getEsZonaExtrema());
                    outfile.print(separatorFields);
                    
                    outfile.print(detailObj.getRutEmpleado());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getNombreEmpleado());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getFechaCalculo());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getFechaInicioContrato());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getNumActualCotizaciones());
                    outfile.print(separatorFields);
                    
                    outfile.print(detailObj.getAfpName());
                    outfile.print(separatorFields);
                    
                    outfile.print(detailObj.getFechaCertifVacacionesProgresivas());
                    outfile.print(separatorFields);
                    
                    outfile.print(detailObj.getDiasProgresivos());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getDiasEspeciales());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getDiasAdicionales());
                    outfile.print(separatorFields);
                    
                    outfile.print(detailObj.getFechaInicioUltimasVacaciones());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getFechaFinUltimasVacaciones());
                    outfile.print(separatorFields);
                    
                    if (filas < vacacionesList.size()){
                        outfile.println(detailObj.getSaldoDias());
                    }else{
                        outfile.print(detailObj.getSaldoDias());
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
    
    /**
    * Escribe lineas en archivo, en formato CSV con la asignacion de marcacion virtual de empleados
    * 
    * @param request
    * @param response
    * @return 
    * @throws javax.servlet.ServletException 
    * @throws java.io.IOException 
    */
    protected String exportAsignacionMarcacionVirtualToCSV(HttpServletRequest request, 
            HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("[DataExportServlet."
            + "exportAsignacionMarcacionVirtualToCSV]entrando...");
        String filePath="";
        PrintWriter outfile=null;
        try {
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            HttpSession session = request.getSession(true);
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
                        
            String separatorFields = ";";
            
            //obtener todos los datos para exportar a CSV
            MarcacionVirtualBp negocio = new MarcacionVirtualBp(appProperties);
            Integer cencoId     = (Integer)session.getAttribute("cencoIdMV");
            String rutEmpleado  = (String)session.getAttribute("rutEmpleadoMV");
            List<MarcacionVirtualVO> listaRegistros = new ArrayList<>();
            
            if (cencoId == null) cencoId = -1; 
            if (rutEmpleado == null) rutEmpleado = "-1"; 
            
            listaRegistros = negocio.getRegistros(cencoId, 
                rutEmpleado,
                0, 
                0, 
                "ve.rut asc");
            
            if (listaRegistros != null && listaRegistros.size() > 0){
                filePath = appProperties.getPathExportedFiles()+
                    File.separator+
                    userConnected.getUsername()+"_asig_marcacion_virtual.csv";
                System.out.println("[DataExportServlet."
                    + "exportAsignacionMarcacionVirtualToCSV]filePath:" + filePath);
                FileWriter filewriter = new FileWriter(filePath);
                outfile     = new PrintWriter(filewriter);
                Iterator<MarcacionVirtualVO> iterador = listaRegistros.listIterator();
                //NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "ES"));
                //Utilidades utils=new Utilidades();
                int filas=0;

                //cabecera
                outfile.println("Empresa;"
                    + "Departamento;"
                    + "Centro de costo;"
                    + "Rut empleado;"
                    + "Nombre empleado;"
                    + "Marcacion virtual (S/N);"
                    + "Registrar ubicacion (S/N);"
                    + "Marca movil (S/N);"
                    + "Movil Id;"    
                    + "Fecha asignacion;"
                    + "Fecha 1;"
                    + "Fecha 2;"
                    + "Fecha 3;"
                    + "Fecha 4;"
                    + "Fecha 5;"
                    + "Fecha 6;"
                    + "Fecha 7");

                while( iterador.hasNext() ) {
                    filas++;
                    MarcacionVirtualVO detailObj = iterador.next();

                    //escribir lineas en el archivo
                    outfile.print(detailObj.getEmpresaNombre());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getDeptoNombre());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getCencoNombre());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getRutEmpleado());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getNombreEmpleado());
                    outfile.print(separatorFields);
                    
                    outfile.print(detailObj.getAdmiteMarcaVirtual());
                    outfile.print(separatorFields);
                    
                    outfile.print(detailObj.getRegistrarUbicacion());
                    outfile.print(separatorFields);
                    
                    outfile.print(detailObj.getMarcaMovil());
                    outfile.print(separatorFields);
                    
                    outfile.print(detailObj.getMovilId());
                    outfile.print(separatorFields);
                    
                    outfile.print(detailObj.getFechaAsignacion());
                    outfile.print(separatorFields);
                                        
                    outfile.print(detailObj.getFecha1());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getFecha2());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getFecha3());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getFecha4());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getFecha5());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getFecha6());
                    outfile.print(separatorFields);
                    
                    if (filas < listaRegistros.size()){
                        outfile.println(detailObj.getFecha7());
                    }else{
                        outfile.print(detailObj.getFecha7());
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
    
    /**
    * Escribe lineas en archivo, 
    * en formato CSV con los dispositivos moviles existentes
    * 
    * @param request
    * @param response
    * @return 
    * @throws javax.servlet.ServletException 
    * @throws java.io.IOException 
    */
    protected String exportMovilesToCSV(HttpServletRequest request, 
            HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("[DataExportServlet."
            + "exportMovilesToCSV]Entrando...");
        String filePath="";
        PrintWriter outfile=null;
        try {
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            HttpSession session = request.getSession(true);
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
                        
            String separatorFields = ";";
            
            //obtener todos los datos para exportar a CSV
            DispositivoMovilDAO movilesDao = new DispositivoMovilDAO(appProperties);
            Integer intCencoId     = (Integer)session.getAttribute("cencoIdMoviles");
            Integer intEstado = (Integer)session.getAttribute("filtroEstado");
            String filtroMovilId = (String)session.getAttribute("filtroMovilId");
            List<DispositivoMovilVO> listaRegistros 
                = movilesDao.getDispositivosMoviles(intCencoId,
                    intEstado, 
                    filtroMovilId,
                    0, 
                    0, 
                    "movil_id asc");
            
            if (listaRegistros != null && listaRegistros.size() > 0){
                filePath = appProperties.getPathExportedFiles()+
                    File.separator+
                    userConnected.getUsername()+"_dispositivos_moviles.csv";
                System.out.println("[DataExportServlet."
                    + "exportMovilesToCSV]filePath:" + filePath);
                FileWriter filewriter = new FileWriter(filePath);
                outfile     = new PrintWriter(filewriter);
                Iterator<DispositivoMovilVO> iterador = listaRegistros.listIterator();
                int filas=0;
                HashMap<Integer,String> hashEstados=new HashMap();
                hashEstados.put(1, "Vigente");
                hashEstados.put(2, "No Vigente");
                
                //cabecera
                outfile.println("Id;"
                    + "Correlativo;"
                    + "Centro de costo;"
                    + "Android Id;"
                    + "Hora creacion;"
                    + "RUN Director;"
                    + "Nombre Director;"
                    + "Estado");

                while( iterador.hasNext() ) {
                    filas++;
                    DispositivoMovilVO detailObj = iterador.next();

                    //escribir lineas en el archivo
                    outfile.print(detailObj.getId());
                    outfile.print(separatorFields);
                    
                    outfile.print(detailObj.getCorrelativo());
                    outfile.print(separatorFields);
                    
                    outfile.print(detailObj.getCencoNombre());
                    outfile.print(separatorFields);
                    
                    outfile.print(detailObj.getAndroidId());
                    outfile.print(separatorFields);
                    
                    outfile.print(detailObj.getFechaHoraCreacion());
                    outfile.print(separatorFields);
                    
                    outfile.print(detailObj.getDirectorRut());
                    outfile.print(separatorFields);
                    
                    outfile.print(detailObj.getDirectorNombre());
                    outfile.print(separatorFields);
                    
                    if (filas < listaRegistros.size()){
                        outfile.println(hashEstados.get(detailObj.getEstado()));
                    }else{
                        outfile.print(hashEstados.get(detailObj.getEstado()));
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
    
    /**
    * Escribe lineas en archivo, en formato CSV con informacion de vacaciones
    * 
    * @param request
    * @param response
    * @return 
    * @throws javax.servlet.ServletException 
    * @throws java.io.IOException 
    */
    protected String exportInfoVacacionesLogToCSV(HttpServletRequest request, 
            HttpServletResponse response)
    throws ServletException, IOException {
        System.out.println("[DataExportServlet."
            + "exportInfoVacacionesLogToCSV]entrando...");
        String filePath="";
        PrintWriter outfile=null;
        try {
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            HttpSession session = request.getSession(true);
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
                        
            String separatorFields = ";";
            ArrayList<VacacionesVO> vacacionesList = 
                (ArrayList<VacacionesVO>)session.getAttribute("vacacioneslog|"+userConnected.getUsername());
            
            if (vacacionesList != null && vacacionesList.size() > 0){
                filePath = appProperties.getPathExportedFiles()+
                    File.separator+
                    userConnected.getUsername()+"_infovacacioneshist.csv";
                System.out.println("[DataExportServlet."
                    + "exportInfoVacacionesLogToCSV]filePath:" + filePath);
                FileWriter filewriter = new FileWriter(filePath);
                outfile     = new PrintWriter(filewriter);
                Iterator<VacacionesVO> iterador = vacacionesList.listIterator();
                //NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "ES"));
                //Utilidades utils=new Utilidades();
                int filas=0;

                //cabecera
                outfile.println("Empresa;"
                    + "Departamento;"
                    + "Centro de costo;"
                    + "Zona Extrema;"
                    + "Rut empleado;"
                    + "Nombre empleado;"
                    + "Fecha evento;"
                    + "Usuario;"    
                    + "Fecha inicio contrato;"
                    + "Num cotizaciones;"
                    + "AFP;"
                    + "Fecha emision certificado;"    
                    + "Dias progresivos;"
                    + "Dias especiales;"
                    + "Dias adicionales;"    
                    + "Inicio ultimas vacaciones;"
                    + "Fin ultimas vacaciones;"    
                    + "Saldo");

                while( iterador.hasNext() ) {
                    filas++;
                    VacacionesVO detailObj = iterador.next();

                    //escribir lineas en el archivo
                    outfile.print(detailObj.getEmpresaId());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getDeptoNombre());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getCencoNombre());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getEsZonaExtrema());
                    outfile.print(separatorFields);
                    
                    outfile.print(detailObj.getRutEmpleado());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getNombreEmpleado());
                    outfile.print(separatorFields);
                    
                    outfile.print(detailObj.getFechaEvento());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getUsername());
                    outfile.print(separatorFields);
                    
                    outfile.print(detailObj.getFechaInicioContrato());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getNumActualCotizaciones());
                    outfile.print(separatorFields);
                    
                    outfile.print(detailObj.getAfpName());
                    outfile.print(separatorFields);
                    
                    outfile.print(detailObj.getFechaCertifVacacionesProgresivas());
                    outfile.print(separatorFields);
                    
                    outfile.print(detailObj.getDiasProgresivos());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getDiasEspeciales());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getDiasAdicionales());
                    outfile.print(separatorFields);
                    
                    outfile.print(detailObj.getFechaInicioUltimasVacaciones());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getFechaFinUltimasVacaciones());
                    outfile.print(separatorFields);
                    
                    if (filas < vacacionesList.size()){
                        outfile.println(detailObj.getSaldoDias());
                    }else{
                        outfile.print(detailObj.getSaldoDias());
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
    
    /**
     * Escribe lineas en archivo, en formato CSV con las marcas modificadas
     * @param request
     * @param response
     * @return 
     * @throws javax.servlet.ServletException 
     * @throws java.io.IOException 
     */
    protected String exportMarcasModificadasToCSV(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        String filePath="";
        PrintWriter outfile=null;
        try {
            Calendar calNow=Calendar.getInstance();
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            HttpSession session = request.getSession(true);
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
                        
            String separatorFields = ";";
            ArrayList<MarcasEventosVO> marcasList = 
                (ArrayList<MarcasEventosVO>)session.getAttribute("marcasModif|"+userConnected.getUsername());
            if (marcasList != null && marcasList.size() > 0){
                filePath = appProperties.getPathExportedFiles()+
                    File.separator+
                    userConnected.getUsername()+"_marcas_modificadas.csv";
                MarcasBp marcasBp = new MarcasBp(appProperties);
                HashMap<Integer, String> tiposMarcas = marcasBp.getTiposMarca();
                
                System.out.println("[DataExportServlet.exportMarcasModificadasToCSV()."
                    + " filePath:" + filePath);
                FileWriter filewriter = new FileWriter(filePath);
                outfile     = new PrintWriter(filewriter);
                Iterator<MarcasEventosVO> iterador = marcasList.listIterator();
                NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "ES"));
                Utilidades utils=new Utilidades();
                int filas=0;

                //cabecera
                outfile.println("Corr;Dispositivo;Empresa;"
                    + "Id;Hashcode;Fecha Hora modificacion;Usuario;"
                    + "Fecha Hora original;Tipo original;Comentario original;"
                    + "Fecha Hora nueva;Tipo nueva;Comentario nueva");

                while( iterador.hasNext() ) {
                    filas++;
                    MarcasEventosVO detailObj = iterador.next();

                    outfile.print(detailObj.getCorrelativo());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getCodDispositivo());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getEmpresaCod());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getId());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getHashcode());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getFechaHoraModificacion());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getCodUsuario());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getFechaHoraOriginal());
                    outfile.print(separatorFields);
                    outfile.print(tiposMarcas.get(detailObj.getTipoMarcaOriginal()));
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getComentarioOriginal());
                    outfile.print(separatorFields);
                    
                    outfile.print(detailObj.getFechaHoraNew());
                    outfile.print(separatorFields);
                    outfile.print(tiposMarcas.get(detailObj.getTipoMarcaNew()));
                    
                    outfile.print(separatorFields);

                    if (filas < marcasList.size()){
                        outfile.println(detailObj.getComentarioNew());
                    }else{
                        outfile.print(detailObj.getComentarioNew());
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
    
    /**
    * Escribe lineas en archivo, en formato CSV con las marcas existentes
    * 
    * @param request
    * @param response
    * @return 
    * @throws javax.servlet.ServletException 
    * @throws java.io.IOException 
    */
    protected String exportMarcasToCSV(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        String filePath="";
        PrintWriter outfile=null;
        try {
            Calendar calNow=Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            HttpSession session = request.getSession(true);
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
                        
            String separatorFields = ";";
            LinkedHashMap<String, MarcaVO> marcasList = 
                (LinkedHashMap<String, MarcaVO>)session.getAttribute("marcas|"+userConnected.getUsername());
            
            if (marcasList != null && marcasList.size() > 0){
                filePath = appProperties.getPathExportedFiles()+
                    File.separator+
                    userConnected.getUsername()+"_marcas_"+sdf.format(calNow.getTime())+".csv";
                MarcasBp marcasBp = new MarcasBp(appProperties);
                HashMap<Integer, String> tiposMarcas = marcasBp.getTiposMarca();
                TipoMarcaManualBp tipoManualBp = new TipoMarcaManualBp(appProperties);
                HashMap<Integer, TipoMarcaManualVO> tiposMarcasManuales = tipoManualBp.getHashTipos();
                
                System.out.println("[DataExportServlet.exportMarcasToCSV()."
                    + " filePath:" + filePath);
                FileWriter filewriter = new FileWriter(filePath);
                outfile     = new PrintWriter(filewriter);
                NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "ES"));
                Utilidades utils=new Utilidades();
                int filas=0;

                //cabecera
                outfile.println("Tipo;"
                        + "Fecha Hora;"
                        + "Correlativo;"
                        + "Dispositivo;"
                        + "Id;"
                        + "Hashcode;"
                        + "Tipo marca manual");

                for (Entry<String, MarcaVO> entry : marcasList.entrySet()) {
                    MarcaVO marca = entry.getValue();
                    String nombreTipoMarca = tiposMarcas.get(marca.getTipoMarca());
                    if (nombreTipoMarca!=null && marca.getCodDispositivo() != null){
                        filas++;

                        outfile.print(nombreTipoMarca);
                        outfile.print(separatorFields);

                        outfile.print(marca.getFechaHoraStr());
                        outfile.print(separatorFields);

                        outfile.print(marca.getCorrelativo());
                        outfile.print(separatorFields);
                        
                        outfile.print(marca.getCodDispositivo());
                        outfile.print(separatorFields);

                        outfile.print(marca.getId());
                        outfile.print(separatorFields);

                        outfile.print(marca.getHashcode());
                        outfile.print(separatorFields);
                        TipoMarcaManualVO tmm = 
                            tiposMarcasManuales.get(marca.getCodTipoMarcaManual());
                        if (tmm != null){
                            if (filas < marcasList.size()){
                                outfile.println(tmm.getNombre());
                            }else{
                                outfile.print(tmm.getNombre());
                            }
                        }else{
                            if (filas < marcasList.size()){
                                outfile.println("");
                            }else{
                                outfile.print("");
                            }
                        }
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
    
    /**
    * Escribe lineas en archivo, en formato CSV con las marcas existentes
    * 
    * @param request
    * @param response
    * @return 
    * @throws javax.servlet.ServletException 
    * @throws java.io.IOException 
    */
    protected String exportLogErrorToCSV(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        String filePath="";
        PrintWriter outfile     = null;
        FileWriter filewriter   = null;
        try {
            Calendar calNow=Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            HttpSession session = request.getSession(true);
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
                        
            LogErrorDAO logDao = new LogErrorDAO();
            String separatorFields = ";";
            
            String startDate = (String)request.getAttribute("startDate");
            String endDate = (String)request.getAttribute("endDate");
            
            List<LogErrorVO> lista = logDao.getRegistros(
                startDate,endDate, 0, 0, "fecha_hora desc");
            
            if (lista != null && lista.size() > 0){
                filePath = appProperties.getPathExportedFiles()+
                    File.separator+
                    userConnected.getUsername()+"_logerror_"+sdf.format(calNow.getTime())+".csv";
                
                System.out.println("[DataExportServlet.exportLogErrorToCSV()."
                    + " filePath:" + filePath);
                filewriter = new FileWriter(filePath);
                outfile     = new PrintWriter(filewriter);
                NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "ES"));
                Utilidades utils=new Utilidades();
                int filas=0;

                //cabecera
                outfile.println("Correlativo;"
                    + "Fecha Hora;"
                    + "Username;"
                    + "Modulo;"
                    + "Evento;"
                    + "IP;"
                    + "Label Error;"
                    + "EmpresaId");
                Iterator<LogErrorVO> it = lista.iterator();
                while(it.hasNext()){
                    LogErrorVO info = it.next();
                    filas++;
                    outfile.print(info.getCorrelativo());
                    outfile.print(separatorFields);

                    outfile.print(info.getFechaHora());
                    outfile.print(separatorFields);

                    outfile.print(info.getUserName());
                    outfile.print(separatorFields);

                    outfile.print(info.getModulo());
                    outfile.print(separatorFields);

                    outfile.print(info.getEvento());
                    outfile.print(separatorFields);
                    
//                    outfile.print(info.getDetalle());
//                    outfile.print(separatorFields);
                    
                    outfile.print(info.getIp());
                    outfile.print(separatorFields);
                    
                    outfile.print(info.getLabel());
                    outfile.print(separatorFields);
                    
                    if (filas < lista.size()){
                        outfile.println(info.getUserEmpresaId());
                    }else{
                        outfile.print(info.getUserEmpresaId());
                    }

                }
            }
                
            //Flush the output to the file
            outfile.flush();

            //Close the Print Writer
            outfile.close();

            //Close the File Writer
            filewriter.close();
        }
        finally {
            if (outfile!=null) outfile.close();
        }

        return filePath;
    }
    
    /**
     * Escribe lineas en archivo, en formato CSV 
     * con la lista de dispositivos
     * @param request
     * @param response
     * @return 
     * @throws javax.servlet.ServletException 
     * @throws java.io.IOException 
     */
    protected String exportDispositivosToCSV(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        String filePath="";
        PrintWriter outfile=null;
        try {
            Calendar calNow=Calendar.getInstance();
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            HttpSession session = request.getSession(true);
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
                        
            String separatorFields = ";";
                        
            ArrayList<DispositivoVO> dispositivosList = 
                (ArrayList<DispositivoVO>)session.getAttribute("dispositivos|"+userConnected.getUsername());
            if (dispositivosList!=null && dispositivosList.size()>0){
                filePath = appProperties.getPathExportedFiles()+
                    File.separator+
                    userConnected.getUsername()+"_dispositivos.csv";
                System.out.println("cl.femase.gestionweb.servlet."
                    + "DataExportServlet.exportDispositivosToCSV(). filePath:" + filePath);
                FileWriter filewriter = new FileWriter(filePath);
                outfile     = new PrintWriter(filewriter);
                Iterator<DispositivoVO> iterador = dispositivosList.listIterator();
                NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "ES"));
                Utilidades utils=new Utilidades();
                int filas=0;

                //cabecera
                outfile.println("Id;Tipo;Fecha Ingreso;Ubicacion;Estado");

                while( iterador.hasNext() ) {
                    filas++;
                    DispositivoVO detailObj = iterador.next();

                    //escribir lineas en el archivo
                    outfile.print(detailObj.getId());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getNombreTipo());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getFechaIngresoAsStr());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getUbicacion());

                    outfile.print(separatorFields);

                    if (filas < dispositivosList.size()){
                        outfile.println(detailObj.getNombreEstado());
                    }else{
                        outfile.print(detailObj.getNombreEstado());
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
            if (outfile != null) outfile.close();
        }

        return filePath;
    }
    
    /**
     * Escribe lineas en archivo, en formato CSV 
     * con la lista de asignacion de turnos
     * 
     * @param request
     * @param response
     * @return 
     * @throws javax.servlet.ServletException 
     * @throws java.io.IOException 
     */
    protected String exportAsignacionTurnosToCSV(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        String filePath="";
        PrintWriter outfile=null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
            Calendar calNow=Calendar.getInstance();
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            HttpSession session = request.getSession(true);
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
                        
            String separatorFields = ";";
                        
            ArrayList<AsignacionTurnoVO> asignacionesList = 
                (ArrayList<AsignacionTurnoVO>)session.getAttribute("asignaciones|"+userConnected.getUsername());
            if (asignacionesList!=null && asignacionesList.size()>0){
                filePath = appProperties.getPathExportedFiles()+
                    File.separator+
                    userConnected.getUsername() 
                        + "_asig_turnos_" + sdf.format(calNow.getTime()) + ".csv";
                System.out.println("[DataExportServlet.exportAsignacionTurnosToCSV(). "
                    + "filePath:" + filePath);
                FileWriter filewriter = new FileWriter(filePath);
                outfile     = new PrintWriter(filewriter);
                Iterator<AsignacionTurnoVO> iterador = asignacionesList.listIterator();
                NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "ES"));
                Utilidades utils=new Utilidades();
                int filas=0;

                //cabecera
                outfile.println("Empresa;Rut Empleado;Nombre Empleado;"
                    + "Departamento;Centro de Costo;"
                    + "Turno;Desde;Hasta;Fecha asignacion;Username");

                while( iterador.hasNext() ) {
                    filas++;
                    AsignacionTurnoVO detailObj = iterador.next();

                    //escribir lineas en el archivo
                    outfile.print(detailObj.getEmpresaNombre());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getRutEmpleado());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getNombreEmpleado());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getDeptoNombre());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getCencoNombre());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getNombreTurno());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getFechaDesde());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getFechaHasta());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getFechaAsignacion());
                    outfile.print(separatorFields);

                    if (filas < asignacionesList.size()){
                        outfile.println(detailObj.getUsername());
                    }else{
                        outfile.print(detailObj.getUsername());
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
            if (outfile != null) outfile.close();
        }

        return filePath;
    }
    
    protected String exportUsuariosToCSV(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        String filePath="";
        PrintWriter outfile=null;
        try {
            Calendar calNow=Calendar.getInstance();
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            HttpSession session = request.getSession(true);
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
                        
            String separatorFields = ";";
                        
            ArrayList<UsuarioVO> usuariosList = 
                (ArrayList<UsuarioVO>)session.getAttribute("usuarios|"+userConnected.getUsername());
            
            if (usuariosList != null && usuariosList.size() > 0){
                filePath = appProperties.getPathExportedFiles()+
                    File.separator+
                    userConnected.getUsername()+"_usuarios.csv";
                System.out.println("cl.femase.gestionweb.servlet."
                    + "DataExportServlet.exportUsuariosToCSV(). "
                    + "filePath:" + filePath);
                FileWriter filewriter = new FileWriter(filePath);
                outfile     = new PrintWriter(filewriter);

                Iterator<UsuarioVO> iterador = usuariosList.listIterator();
                NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "ES"));
                Utilidades utils=new Utilidades();
                int filas=0;
                HashMap<Integer,String> hashEstados=new HashMap();
                hashEstados.put(1, "Vigente");
                hashEstados.put(2, "No Vigente");
                //cabecera
                outfile.println("Username;Nombres;Ap Paterno;Ap Materno;Perfil;Email;Estado");

                while( iterador.hasNext() ) {
                    filas++;
                    UsuarioVO detailObj = iterador.next();

                    //escribir lineas en el archivo
                    outfile.print(detailObj.getUsername());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getNombres());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getApPaterno());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getApMaterno());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getNomPerfil());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getEmail());
                    outfile.print(separatorFields);

                    if (filas < usuariosList.size()){
                        outfile.println(hashEstados.get(detailObj.getEstado()));
                    }else{
                        outfile.print(hashEstados.get(detailObj.getEstado()));
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
            if (outfile != null) outfile.close();
        }

        return filePath;
    }
    
    protected String exportDetalleAusenciasToCSV(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        String filePath="";
        PrintWriter outfile=null;
        try {
            Calendar calNow=Calendar.getInstance();
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            HttpSession session = request.getSession(true);
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
            String separatorFields = ";";
            ArrayList<DetalleAusenciaVO> detalleAusenciasList = 
                (ArrayList<DetalleAusenciaVO>)session.getAttribute("detalleAusencias|"+userConnected.getUsername());
            if (detalleAusenciasList!=null && detalleAusenciasList.size()>0){
                filePath = appProperties.getPathExportedFiles()+
                    File.separator+
                    userConnected.getUsername()+"_detalle_ausencias.csv";
                System.out.println("cl.femase.gestionweb.servlet."
                    + "DataExportServlet."
                    + "exportDetalleAusenciasToCSV(). "
                    + "filePath:" + filePath);
                FileWriter filewriter = new FileWriter(filePath);
                outfile     = new PrintWriter(filewriter);

                Iterator<DetalleAusenciaVO> iterador = detalleAusenciasList.listIterator();
                NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "ES"));
                Utilidades utils=new Utilidades();
                int filas=0;
                HashMap<Integer,String> hashEstados=new HashMap();
                hashEstados.put(1, "Vigente");
                hashEstados.put(2, "No Vigente");
                //cabecera
                outfile.println("Rut;"
                    + "Nombre;"
                    + "Fec. Ingreso;"
                    + "Tipo Ausencia;"
                    + "Ausencia x hora;"
                    + "Fec. Inicio;"
                    + "Hora Inicio;"
                    + "Fecha Fin;"
                    + "Hora Fin;"
                    + "Autorizador;"
                    + "Autorizada");

                while( iterador.hasNext() ) {
                    filas++;
                    DetalleAusenciaVO detailObj = iterador.next();

                    //escribir lineas en el archivo
                    outfile.print(detailObj.getRutEmpleado());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getNombreEmpleado());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getFechaIngresoAsStr());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getNombreAusencia());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getPermiteHora());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getFechaInicioAsStr());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getHoraInicioFullAsStr());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getFechaFinAsStr());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getHoraFinFullAsStr());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getRutAutorizador());
                    outfile.print(separatorFields);

                    if (filas < detalleAusenciasList.size()){
                        outfile.println(detailObj.getAusenciaAutorizada());
                    }else{
                        outfile.print(detailObj.getAusenciaAutorizada());
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
            if (outfile != null) outfile.close();
        }

        return filePath;
    }
    
    protected String exportMarcasRechazadasToCSV(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        String filePath="";
        PrintWriter outfile=null;
        try {
            Calendar calNow=Calendar.getInstance();
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            HttpSession session = request.getSession(true);
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
                        
            ArrayList<MarcaRechazoVO> marcasRechazadasList = 
                (ArrayList<MarcaRechazoVO>)session.getAttribute("marcasRechazadas|"+userConnected.getUsername());
            if (marcasRechazadasList!=null && marcasRechazadasList.size()>0){
                filePath = appProperties.getPathExportedFiles()+
                    File.separator+
                    userConnected.getUsername()+"_marcas_rechazadas.csv";
                System.out.println("cl.femase.gestionweb.servlet."
                    + "DataExportServlet.exportMarcasRechazadasToCSV(). "
                    + "filePath:" + filePath);
                FileWriter filewriter = new FileWriter(filePath);
                outfile     = new PrintWriter(filewriter);
                String separatorFields = ";";

                Iterator<MarcaRechazoVO> iterador = marcasRechazadasList.listIterator();
                NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "ES"));
                Utilidades utils=new Utilidades();
                int filas=0;
                MarcasBp marcasBp = new MarcasBp(appProperties);
                HashMap<Integer, String> tiposMarcas = marcasBp.getTiposMarca();

                //cabecera
                outfile.println("Num Ficha;"
                    + "Fecha hora;"
                    + "Tipo;"
                    + "Id Dispositivo;"
                    + "Id marca;"
                    + "Hashcode marca;"
                    + "Motivo rechazo");

                while( iterador.hasNext() ) {
                    filas++;
                    MarcaRechazoVO detailObj = iterador.next();

                    //escribir lineas en el archivo
                    outfile.print(detailObj.getRutEmpleado());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getFechaHoraStr());
                    outfile.print(separatorFields);
                    outfile.print(tiposMarcas.get(detailObj.getTipoMarca()));
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getCodDispositivo());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getId());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getHashcode());
                    outfile.print(separatorFields);

                    if (filas < marcasRechazadasList.size()){
                        outfile.println(detailObj.getMotivoRechazo());
                    }else{
                        outfile.print(detailObj.getMotivoRechazo());
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
            if (outfile != null) outfile.close();
        }

        return filePath;
    }
    
    /**
     * Escribe lineas en archivo,
     * en formato CSV con la tabla organizacion_empresa
     * @param request
     * @param response
     * @return 
     * @throws javax.servlet.ServletException 
     * @throws java.io.IOException 
     */
    protected String exportOrganizacionToCSV(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

        String filePath="";
        PrintWriter outfile=null;
        try {
            Calendar calNow=Calendar.getInstance();
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            HttpSession session = request.getSession(true);
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
            filePath = appProperties.getPathExportedFiles()+
                    File.separator+
                    userConnected.getUsername()+"_organizacion.csv";

            FileWriter filewriter = new FileWriter(filePath);

            outfile     = new PrintWriter(filewriter);
            String separatorFields = ";";
            ArrayList<OrganizacionEmpresaVO> organizacionList = 
                    (ArrayList<OrganizacionEmpresaVO>)session.getAttribute("organizacion|"+userConnected.getUsername());
            
            Iterator<OrganizacionEmpresaVO> iterador = organizacionList.listIterator();
            NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "ES"));
            Utilidades utils=new Utilidades();
            int filas=0;
           
            //cabecera
            outfile.println("Empresa;Departamento;Centro de Costo");

            while( iterador.hasNext() ) {
                filas++;
                OrganizacionEmpresaVO organizacionObj = iterador.next();
                
                //escribir lineas en el archivo
                outfile.print(organizacionObj.getEmpresaNombre());
                outfile.print(separatorFields);
                outfile.print(organizacionObj.getDeptoNombre());
                outfile.print(separatorFields);
                
                if (filas < organizacionList.size()){
                    outfile.println(organizacionObj.getCencoNombre());
                }else{
                    outfile.print(organizacionObj.getCencoNombre());
                }
            }

            //Flush the output to the file
            outfile.flush();
            //Close the Print Writer
            outfile.close();
            //Close the File Writer
            filewriter.close();
        } finally {
            outfile.close();
        }

        return filePath;
    }

    /**
    * Escribe lineas en archivo, en formato CSV con el resumen de Permisos Administrativos de los empleados
    * 
    * @param request
    * @param response
    * @return 
    * @throws javax.servlet.ServletException 
    * @throws java.io.IOException 
    */
    protected String exportResumenPermisosAdministrativosToCSV(HttpServletRequest request, 
            HttpServletResponse response)
    throws ServletException, IOException {
        System.out.println("[DataExportServlet."
            + "exportResumenPermisosAdministrativosToCSV]entrando...");
        String filePath="";
        PrintWriter outfile=null;
        try {
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            HttpSession session = request.getSession(true);
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
                        
            String separatorFields = ";";
            ArrayList<PermisoAdministrativoVO> resumenPAList = 
                (ArrayList<PermisoAdministrativoVO>)session.getAttribute("resumenPA|"+userConnected.getUsername());
            
            if (resumenPAList != null && resumenPAList.size() > 0){
                filePath = appProperties.getPathExportedFiles()+
                    File.separator+
                    userConnected.getUsername()+"_resumen_permisos_administrativos.csv";
                System.out.println("[DataExportServlet."
                    + "exportResumenPermisosAdministrativosToCSV]filePath:" + filePath);
                FileWriter filewriter = new FileWriter(filePath);
                outfile     = new PrintWriter(filewriter);
                Iterator<PermisoAdministrativoVO> iterador = resumenPAList.listIterator();
                //NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "ES"));
                //Utilidades utils=new Utilidades();
                int filas=0;

                //cabecera
                outfile.println("Empresa;"
                    + "Departamento;"
                    + "Centro de costo;"
                    + "Run empleado;"
                    + "Nombre empleado;"
                    + "Dias disponibles;"
                    + "Dias utilizados;"
                    + "Ultima actualizacion");

                while( iterador.hasNext() ) {
                    filas++;
                    PermisoAdministrativoVO detailObj = iterador.next();

                    //escribir lineas en el archivo
                    outfile.print(detailObj.getEmpresaId());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getDeptoNombre());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getCencoNombre());
                    outfile.print(separatorFields);
                    
                    outfile.print(detailObj.getRunEmpleado());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getNombreEmpleado());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getDiasDisponibles());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getDiasUtilizados());
                    outfile.print(separatorFields);
                    
                    if (filas < resumenPAList.size()){
                        outfile.println(detailObj.getLastUpdate());
                    }else{
                        outfile.print(detailObj.getLastUpdate());
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
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        setResponseHeaders(response);processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
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

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
