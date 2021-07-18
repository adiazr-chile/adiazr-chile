/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.servlet.cargamasiva;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.MarcasBp;
import cl.femase.gestionweb.common.ClientInfo;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.dao.MaintenanceEventsDAO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.MarcaVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

/**
 * Servlet to handle File upload request from Client
 * @author Alexander
 */
public class UploadMarcasServlet extends BaseServlet {

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
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
            MarcasBp marcasBp=new MarcasBp(appProperties);
            EmpleadosBp empleadosBp = new EmpleadosBp(appProperties);
//            MaintenanceEventVO resultado=new MaintenanceEventVO();
            String empresaId = "";
            String numFicha = "";
            String fecha = "";
            String hora = "";
            String tipoMarca = "";
            int intTipoMarca=0;
            String idDispositivo = "";
            String id = "";
            String hashcode = "";
                                                                        
            try {
                List<FileItem> multiparts = new ServletFileUpload(
                    new DiskFileItemFactory()).parseRequest(request);
                
                System.out.println("[UploadMarcasServlet]"
                    + "Iterar marcas...");

                //process only if its multipart content
                if(ServletFileUpload.isMultipartContent(request)){
                    String pathUploadedFiles = appProperties.getUploadsPath();
                    System.out.println("[UploadMarcasServlet]"
                        + "paso 1...");
//                    int correlativo=1;
                    LinkedHashMap<String,MarcaVO> marcasOk=
                        new LinkedHashMap<>();
                    LinkedHashMap<String,MarcaVO> marcasError=
                        new LinkedHashMap<>();
                        
                        for(FileItem item2 : multiparts){
                            if(!item2.isFormField()){
                                // Process form file field (input type="file").
                                File csvfile = new File(item2.getName());
                                String filename = csvfile.getName();
                                String filePathLoaded= pathUploadedFiles + File.separator + filename;
                                String extension = FilenameUtils.getExtension(filename);
                                System.out.println("[UploadMarcasServlet]"  
                                    + "Filename: " + filename
                                    + ", extension: " + extension);
                                item2.write( new File(filePathLoaded));
                                if (extension.compareTo("csv") == 0){
                                    Reader reader = new FileReader(filePathLoaded);
                                    System.out.println("[UploadMarcasServlet]"  
                                        + "filePathLoaded: " + filePathLoaded); 
                                    //Reader reader = Files.newBufferedReader(Paths.get(filePathLoaded));
                                    CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
                                    Iterable<CSVRecord> csvRecords = csvParser.getRecords();
                                    for (CSVRecord csvRecord : csvRecords) {
                                        // Accessing Values by Column Index
                                        empresaId = csvRecord.get(0);
                                        numFicha = csvRecord.get(1);
                                        fecha = csvRecord.get(2);
                                        hora = csvRecord.get(3);
                                        tipoMarca = csvRecord.get(4);
                                        idDispositivo = csvRecord.get(5);
                                        id = csvRecord.get(6);
                                        hashcode = csvRecord.get(7);
                                        
                                        intTipoMarca = Integer.parseInt(tipoMarca);
                                        
                                        System.out.println("[UploadMarcasServlet]Record No - " + csvRecord.getRecordNumber());
                                        System.out.println("[UploadMarcasServlet]---------------");
                                        System.out.println("[UploadMarcasServlet]Empresa : " + empresaId);
                                        System.out.println("[UploadMarcasServlet]NumFicha : " + numFicha);
                                        System.out.println("[UploadMarcasServlet]Fecha : " + fecha);
                                        System.out.println("[UploadMarcasServlet]Hora : " + hora);
                                        System.out.println("[UploadMarcasServlet]TipoMarca : " + tipoMarca);
                                        System.out.println("[UploadMarcasServlet]IdDispositivo : " + idDispositivo);
                                        System.out.println("[UploadMarcasServlet]Id marca : " + id);
                                        System.out.println("[UploadMarcasServlet]Hashcode : " + hashcode);
                                        System.out.println("---------------\n\n");
                                        MarcaVO newMark=new MarcaVO();
                                        newMark.setCodDispositivo(idDispositivo);
                                        newMark.setEmpresaCod(empresaId);
                                        newMark.setRutEmpleado(numFicha);
                                        newMark.setFechaHora(Utilidades.getFechaHora(fecha+" "+hora));
                                        newMark.setTipoMarca(intTipoMarca);
                                        newMark.setId(id);
                                        newMark.setHashcode(hashcode);
                                                
                                        //validar si el empleado existe
                                        //EmpleadoVO empleado = empleadosBp.getEmpleado(empresaId, numFicha);
//                                        if (empleado!=null){
                                            //validar si tiene contrato vigente
//                                            System.out.println("[UploadMarcasServlet]numFicha: " + numFicha
//                                                +", rut: "+empleado.getRut());
                                            //if (empleadosBp.tieneContratoVigente(empleado.getEmpresa().getId(), empleado.getRut(), empleado.getCentroCosto().getId())){
                                                //insertar marca
                                                MaintenanceVO insertMarca = marcasBp.insert(newMark);
                                                if (!insertMarca.isThereError()){
                                                    marcasOk.put(""+csvRecord.getRecordNumber(), newMark);
                                                }else{
                                                    System.err.println("Error al insertar "
                                                        + "marca. msg Error: "+insertMarca.getMsgError());
                                                    newMark.setUploadMessageError(insertMarca.getMsgError());
                                                    marcasError.put(""+csvRecord.getRecordNumber(), newMark); 
                                                }
//                                            }else{
//                                                System.err.println("empleado no tiene contrato vigente");
//                                                newMark.setUploadMessageError("Empleado no tiene contrato vigente");
//                                                marcasError.put(""+csvRecord.getRecordNumber(), newMark); 
//                                            }
//                                        }else{
//                                            System.err.println("empleado no existe");
//                                            newMark.setUploadMessageError("Empleado no existe");
//                                            marcasError.put(""+csvRecord.getRecordNumber(), newMark); 
//                                        }
                                    }
                                    
                                }
                            }
                        }

                        //File uploaded successfully
                        request.setAttribute("tipo", "UploadMarcas");
                        request.setAttribute("marcasOk", marcasOk);
                        request.setAttribute("marcasError", marcasError);
                        session.setAttribute("mensaje", "Archivo de Marcas Cargado con exito");
                        
                        //***************Agregar evento al log
                        MaintenanceEventVO evento=new MaintenanceEventVO();
                        evento.setUsername(userConnected.getUsername());
                        evento.setDatetime(new Date());
                        evento.setUserIP(request.getRemoteAddr());
                        evento.setType("UPLOAD_MARCA");//
                        evento.setEmpresaIdSource(userConnected.getEmpresaId());
                        ClientInfo clientInfo = new ClientInfo();
                        clientInfo.printClientInfo(request);
                        /**
                        * Sistema operativo y navegador
                        */
                        evento.setOperatingSystem(clientInfo.getClientOS(request));
                        evento.setBrowserName(clientInfo.getClientBrowser(request));
                        evento.setDescription("Carga de Marcas CSV");
                        MaintenanceEventsDAO eventsDao = new MaintenanceEventsDAO(appProperties);
                        eventsDao.addEvent(evento); 
                    
                }else{
                    session.setAttribute("mensaje",
                        "Sorry this Servlet only handles file upload request");
                }
                    
                request.getRequestDispatcher("/cargamasiva/upload_marcas_result.jsp").forward(request, response);
            } catch (FileUploadException ex) {
                System.err.println("[UploadMarcasServlet]"
                    + "Error al rescatar parametros multipart: "+ex.toString());
            }catch (Exception ex2) {
                System.err.println("[UploadMarcasServlet]"
                    + "Error al subir archivo con multipart: "+ex2.toString());
                ex2.printStackTrace();
            }
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            System.err.println("Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
        }
    }
}
