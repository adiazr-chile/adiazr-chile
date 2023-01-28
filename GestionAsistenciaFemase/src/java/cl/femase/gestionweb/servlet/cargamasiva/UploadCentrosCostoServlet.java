/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.servlet.cargamasiva;

import cl.femase.gestionweb.business.CentroCostoBp;
import cl.femase.gestionweb.common.ClientInfo;
import cl.femase.gestionweb.dao.MaintenanceEventsDAO;
import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.vo.CentroCostoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
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
public class UploadCentrosCostoServlet extends BaseServlet {

    //private final String UPLOAD_DIRECTORY = "C:/uploads";
  
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
      
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        if (userConnected != null){
            SimpleDateFormat fechaFormat = new SimpleDateFormat("dd-MM-yyyy");
            //process only if its multipart content
            if(ServletFileUpload.isMultipartContent(request)){
                String pathUploadedFiles = appProperties.getUploadsPath();
                CentroCostoBp cencoBp=new CentroCostoBp(appProperties);
                MaintenanceEventVO resultado=new MaintenanceEventVO();
                LinkedHashMap<String,CentroCostoVO> cencosOk=
                    new LinkedHashMap<>();
                LinkedHashMap<String,CentroCostoVO> cencosError=
                    new LinkedHashMap<>();
                try {
                    List<FileItem> multiparts = new ServletFileUpload(
                        new DiskFileItemFactory()).parseRequest(request);

                    for(FileItem item : multiparts){
                        if(!item.isFormField()){
                            File auxfile = new File(item.getName());
                            String filename = auxfile.getName();
                            System.out.println(WEB_NAME+"[UploadCentrosCostoServlet]"
                                + "Filename: "+filename);
                            String extension = FilenameUtils.getExtension(filename);
                            String filePathLoaded= pathUploadedFiles + File.separator + filename;
                            System.out.println(WEB_NAME+"--->filePathLoaded="+filePathLoaded);
                            item.write( new File(filePathLoaded));
                            if (extension.compareTo("csv") == 0){
                                Reader in = new FileReader(filePathLoaded);
                                Iterable<CSVRecord> records = 
                                CSVFormat.RFC4180.withHeader("depto_id","nombre",
                                        "estado","direccion",
                                        "id_comuna","telefonos","email"
                                        ).parse(in);
                                
                                for (CSVRecord record : records) {
                                    System.out.println(WEB_NAME+"[UploadCentrosCostoServlet]"
                                        + "Linea csv: "+record.toString());
                                    if (record.getRecordNumber()>1){
                                        String depto_id = record.get("depto_id");
                                        String nombre = record.get("nombre");
                                        String estado = record.get("estado");
                                        String direccion = record.get("direccion");
                                        String id_comuna = record.get("id_comuna");
                                        String telefonos = record.get("telefonos");
                                        String email = record.get("email");

                                        CentroCostoVO data=new CentroCostoVO();
                                        data.setDeptoId(depto_id);
                                        data.setNombre(nombre);
                                        data.setEstado(Integer.parseInt(estado));
                                        data.setDireccion(direccion);
                                        data.setComunaId(Integer.parseInt(id_comuna));
                                        data.setTelefonos(telefonos);
                                        data.setEmail(email);
                                            
                                        System.out.println(WEB_NAME+"[UploadCentrosCostoServlet]"
                                            + "datacencos:"
                                            + data.toString());
                                        
                                        //insertar centro costo
                                        resultado.setUsername(userConnected.getUsername());
                                        resultado.setDatetime(new Date());
                                        resultado.setUserIP(request.getRemoteAddr());
                                        resultado.setType("ACO");
                                        resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                                        System.out.println(WEB_NAME+"[UploadCentrosCostoServlet]"
                                            + "Insertar cenco nombre: " + data.getNombre());
                                        ResultCRUDVO insertCenco = 
                                            cencoBp.insert(data, resultado);
                                        if (!insertCenco.isThereError()){
                                            cencosOk.put(data.getNombre(), data);
                                        }else{
                                           data.setUploadMessageError(insertCenco.getMsgError());
                                           cencosError.put(data.getNombre(), data); 
                                        }
                                    }
                                }
                            }

                        }
                    }
           
                    //File uploaded successfully
                    request.setAttribute("tipo", "UploadCencos");
                    request.setAttribute("cencosOk", cencosOk);
                    request.setAttribute("cencosError", cencosError);
                    session.setAttribute("mensaje", "Archivo Cargado");
                    
                    //***************Agregar evento al log
                    MaintenanceEventVO evento=new MaintenanceEventVO();
                    evento.setUsername(userConnected.getUsername());
                    evento.setDatetime(new Date());
                    evento.setUserIP(request.getRemoteAddr());
                    evento.setType("UPLOAD_CENCO");//
                    evento.setEmpresaIdSource(userConnected.getEmpresaId());
                    ClientInfo clientInfo = new ClientInfo();
                    clientInfo.printClientInfo(request);
                    /**
                    * Sistema operativo y navegador
                    */
                    evento.setOperatingSystem(clientInfo.getClientOS(request));
                    evento.setBrowserName(clientInfo.getClientBrowser(request));
                    evento.setDescription("Carga de Centros de costo CSV");
                    MaintenanceEventsDAO eventsDao = new MaintenanceEventsDAO(appProperties);
                    eventsDao.addEvent(evento); 
                        
                } catch (Exception ex) {
                   session.setAttribute("mensaje", "File Upload Failed due to " + ex);
                }          
         
            }else{
                session.setAttribute("mensaje",
                    "Sorry this Servlet only handles file upload request");
            }
            request.getRequestDispatcher("/cargamasiva/upload_cencos_result.jsp").forward(request, response);
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            System.err.println("Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
        }
    }
}
