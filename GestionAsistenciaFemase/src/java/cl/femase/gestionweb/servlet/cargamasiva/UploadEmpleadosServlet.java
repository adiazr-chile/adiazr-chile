/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.servlet.cargamasiva;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.common.ClientInfo;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.dao.MaintenanceEventsDAO;
import cl.femase.gestionweb.vo.CentroCostoVO;
import cl.femase.gestionweb.vo.DepartamentoVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.EmpresaVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.ResultadoCargaDataCsvVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

/**
 * Servlet to handle File upload request from Client
 * @author Alexander
 */
@WebServlet(name = "UploadEmpleadosServlet", urlPatterns = {"/UploadEmpleadosServlet"})
public class UploadEmpleadosServlet extends BaseServlet {

    //private final String UPLOAD_DIRECTORY = "C:/uploads";
  
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
      
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        if (session!=null) session.removeAttribute("mensaje");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        if (userConnected != null){
            SimpleDateFormat fechaFormat = new SimpleDateFormat("dd-MM-yyyy");
            //process only if its multipart content
            if(ServletFileUpload.isMultipartContent(request)){
                String pathUploadedFiles = appProperties.getUploadsPath();
                EmpleadosBp empleadosBp=new EmpleadosBp(appProperties);
                MaintenanceEventVO resultado=new MaintenanceEventVO();
                LinkedHashMap<String,EmpleadoVO> empleadosInsertadosOk=
                    new LinkedHashMap<>();
                LinkedHashMap<String,EmpleadoVO> empleadosActualizadosOk=
                    new LinkedHashMap<>();
                LinkedHashMap<String,EmpleadoVO> empleadosError=
                    new LinkedHashMap<>();
                ArrayList<ResultadoCargaDataCsvVO> resultados = new ArrayList<>();
                try {
                    List<FileItem> multiparts = new ServletFileUpload(
                        new DiskFileItemFactory()).parseRequest(request);
                    ArrayList<EmpleadoVO> empleadosInsert = new ArrayList<>(); 
                    for(FileItem item : multiparts){
                        if(!item.isFormField()){
                            File auxfile = new File(item.getName());
                            String filename = auxfile.getName();
                            System.out.println("[UploadEmpleadosServlet]"
                                + "Filename: "+filename);
                            String extension = FilenameUtils.getExtension(filename);
                            String filePathLoaded= pathUploadedFiles + File.separator + filename;
                            System.out.println("[UploadEmpleadosServlet]filePathLoaded="+filePathLoaded);
                            item.write( new File(filePathLoaded));
                            
                            if (extension.compareTo("csv") == 0){
                                Reader in = new FileReader(filePathLoaded);
                                Iterable<CSVRecord> records = 
                                CSVFormat.RFC4180.withHeader("empresa",
                                        "depto","cenco","comuna",
                                        "turno","cargo","rut",
                                        "nombres","paterno",
                                        "materno","fecha_nacimiento",
                                        "direccion","email",
                                        "fecha_inicio_contrato","estado",
                                        "sexo","fono_fijo","fono_movil",
                                        "autoriza_ausencia",
                                        "contrato_indefinido",
                                        "fecha_fin_contrato",
                                        "art_22","cod_interno").parse(in);
                                
                                for (CSVRecord record : records) {
                                    System.out.println("[UploadEmpleadosServlet]"
                                        + "recordNumber: "+record.getRecordNumber()
                                        + ", Linea csv: "+record.toString());
                                    if (record.getRecordNumber()>1){
                                        String empresaId=null;
                                        try{
                                            empresaId = record.get("empresa");
                                        }catch(Exception ex){
                                            System.err.println("[UploadEmpleadosServlet]"
                                                + "Error "+ex.toString());
                                        }
                                        System.out.println("[UploadEmpleadosServlet]"
                                            + "empresaId: "+empresaId);
                                        String deptoId = record.get("depto");
                                        String cencoId = record.get("cenco");
                                        String comuna = record.get("comuna");
                                        String turno = record.get("turno");
                                        String cargo = record.get("cargo");
                                        System.out.println("[UploadEmpleadosServlet]"
                                            + "turno: "+turno);
                                        //datos personales
                                        String rut = record.get("rut");
                                        String nombres = record.get("nombres");
                                        String paterno = record.get("paterno");
                                        String materno = record.get("materno");
                                        String fecha_nacimiento = record.get("fecha_nacimiento");
                                        System.out.println("fechaNacimiento: "+fecha_nacimiento);
                                        String fecha_inicio_contrato = record.get("fecha_inicio_contrato");
                                        String fecha_fin_contrato = record.get("fecha_fin_contrato");
                                        String direccion = record.get("direccion");
                                        String email = record.get("email");
                                        String estado = record.get("estado");
                                        String sexo = record.get("sexo");
                                        String fono_fijo = record.get("fono_fijo");
                                        String fono_movil = record.get("fono_movil");
                                        String autoriza_ausencia = record.get("autoriza_ausencia");
                                        String contrato_indefinido = record.get("contrato_indefinido");
                                        String art_22 = record.get("art_22");
                                        String codInterno = record.get("cod_interno");

                                        EmpleadoVO dataEmpleado=new EmpleadoVO();
                                        
                                        //info laboral
                                        dataEmpleado.setRut(rut);
                                        dataEmpleado.setCodInterno(codInterno);
                                        dataEmpleado.setNombres(nombres);
                                        dataEmpleado.setApePaterno(paterno);
                                        dataEmpleado.setApeMaterno(materno);
                                        dataEmpleado.setIdTurno(Integer.parseInt(turno));
                                        dataEmpleado.setIdCargo(Integer.parseInt(cargo));
                                        
                                        Date auxDate = null;
                                        try{
                                            auxDate = fechaFormat.parse(fecha_nacimiento);
                                        }catch(ParseException pex){
                                            System.err.println("[UploadEmpleadosServlet]"
                                                + "error al parsear fecha nacimiento: "+pex.toString());
                                        }   
                                        dataEmpleado.setFechaNacimiento(auxDate);

                                        //fechas
                                        auxDate = null;
                                        try{
                                            auxDate = fechaFormat.parse(fecha_inicio_contrato);
                                        }catch(ParseException pex){
                                            System.err.println("[UploadEmpleadosServlet]"
                                                + "error al parsear fecha inicio contrato: "+pex.toString());
                                        }   
                                        dataEmpleado.setFechaInicioContrato(auxDate);
                                        auxDate = null;
                                        try{
                                            auxDate = fechaFormat.parse(fecha_fin_contrato);
                                        }catch(ParseException pex){
                                            System.err.println("[UploadEmpleadosServlet]"
                                                + "error al parsear fecha fin contrato: "+pex.toString());
                                        }   
                                        dataEmpleado.setFechaTerminoContrato(auxDate);

                                        dataEmpleado.setDireccion(direccion);
                                        try{
                                            dataEmpleado.setComunaId(Integer.parseInt(comuna));
                                        }catch(NumberFormatException nfex){
                                            System.err.println("[UploadEmpleadosServlet]"
                                                + "error al parsear comuna: "+nfex.toString());
                                            dataEmpleado.setComunaId(-1);
                                        }
                                        dataEmpleado.setEmail(email);
                                        
                                        try{
                                            dataEmpleado.setEstado(Integer.parseInt(estado));
                                        }catch(NumberFormatException nfex){
                                            System.err.println("[UploadEmpleadosServlet]"
                                                + "error al parsear estado: "+nfex.toString());
                                            dataEmpleado.setEstado(1);
                                        }
                                        dataEmpleado.setSexo(sexo);
                                        dataEmpleado.setFonoFijo(fono_fijo);
                                        dataEmpleado.setFonoMovil(fono_movil);
                                        //boolean autorizaAusencia = (if (autoriza_ausencia.compareTo("TRUE")==0))?true:false;
                                        dataEmpleado.setAutorizaAusencia((autoriza_ausencia.compareTo("TRUE")==0));
                                        dataEmpleado.setContratoIndefinido((contrato_indefinido.compareTo("TRUE")==0));
                                        dataEmpleado.setArticulo22((art_22.compareTo("TRUE")==0));
                                        EmpresaVO auxempresa=new EmpresaVO();
                                        auxempresa.setId(empresaId);
                                        dataEmpleado.setEmpresa(auxempresa);
                                        dataEmpleado.setDepartamento(new DepartamentoVO(deptoId, "Depto NN",empresaId));
                                        dataEmpleado.setCentroCosto(new CentroCostoVO(Integer.parseInt(cencoId), "cenco NN", 1, deptoId));
                                            
                                        System.out.println("[UploadEmpleadosServlet]dataempleado:"
                                            + dataEmpleado.toString());
                                     
                                        empleadosInsert.add(dataEmpleado);
                                        
                                        
                                    }
                                }//fin iteracion de empleados en el csv
                                empleadosBp.openDbConnection();
                                //guardar- Insertar/Update 
                                resultados = empleadosBp.procesaEmpleadosCSV(request,
                                    userConnected, 
                                    empleadosInsert);
                            }
                        }
                    }
           
                    //File uploaded successfully
                    request.setAttribute("tipo", "UploadEmpleados");
                    request.setAttribute("empleadosCargados", resultados);
                    request.setAttribute("empleadosInsertadosOk", empleadosInsertadosOk);
                    request.setAttribute("empleadosActualizadosOk", empleadosActualizadosOk);
                    request.setAttribute("empleadosError", empleadosError);
                    session.setAttribute("mensaje", "Archivo Cargado");
                    
                    //***************Agregar evento al log
                    MaintenanceEventVO evento=new MaintenanceEventVO();
                    evento.setUsername(userConnected.getUsername());
                    evento.setDatetime(new Date());
                    evento.setUserIP(request.getRemoteAddr());
                    evento.setType("UPLOAD_EMPL");//
                    evento.setEmpresaIdSource(userConnected.getEmpresaId());
                    ClientInfo clientInfo = new ClientInfo();
                    clientInfo.printClientInfo(request);
                    /**
                    * Sistema operativo y navegador
                    */
                    evento.setOperatingSystem(clientInfo.getClientOS(request));
                    evento.setBrowserName(clientInfo.getClientBrowser(request));
                    evento.setDescription("Carga de Empleados CSV");
                    MaintenanceEventsDAO eventsDao = new MaintenanceEventsDAO(appProperties);
                    eventsDao.addEvent(evento); 
                } catch (Exception ex) {
                   session.setAttribute("mensaje", "File Upload Failed due to " + ex);
                }          
         
            }else{
                session.setAttribute("mensaje",
                    "Sorry this Servlet only handles file upload request");
            }
            request.getRequestDispatcher("/cargamasiva/upload_empleados_result.jsp").forward(request, response);
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            System.err.println("Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
        }
    }
}
