/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.servlet.cargamasiva;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.UsuarioBp;
import cl.femase.gestionweb.common.ClientInfo;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.dao.MaintenanceEventsDAO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioCentroCostoVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
public class UploadUsuariosServlet extends BaseServlet {

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
                UsuarioBp usuarioBp = new UsuarioBp(appProperties);
                try {
                    List<FileItem> multiparts = new ServletFileUpload(
                        new DiskFileItemFactory()).parseRequest(request);
                    ArrayList<UsuarioVO> usuarios = new ArrayList<>(); 
                    ArrayList<UsuarioCentroCostoVO> centroscosto = new ArrayList<>();
                    
                    LinkedHashMap<String,UsuarioVO> usuariosOk=
                        new LinkedHashMap<>();
                
                    for(FileItem item : multiparts){
                        if(!item.isFormField()){
                            File auxfile = new File(item.getName());
                            String filename = auxfile.getName();
                            System.out.println("[UploadUsuariosServlet]"
                                + "Filename: "+filename);
                            String extension = FilenameUtils.getExtension(filename);
                            String filePathLoaded= pathUploadedFiles + File.separator + filename;
                            item.write( new File(filePathLoaded));
                            if (extension.compareTo("csv") == 0){
                                Reader in = new FileReader(filePathLoaded);
                                Iterable<CSVRecord> records = 
                                CSVFormat.RFC4180.withHeader("username",
                                    "password",
                                    "nombres",
                                    "paterno",
                                    "materno",
                                    "perfil_id",
                                    "email",
                                    "empresa_id",
                                    "depto_id",
                                    "centro_costo_id").parse(in);
                                
                                for (CSVRecord record : records) {
                                    System.out.println("[UploadUsuariosServlet]"
                                        + "Linea csv: "+record.toString());
                                    if (record.getRecordNumber()>1){
                                        String username = record.get("username");
                                        String password = record.get("password");
                                        String nombres = record.get("nombres");
                                        String paterno = record.get("paterno");
                                        String materno = record.get("materno");
                                        String idPerfil = record.get("perfil_id");
                                        String email = record.get("email");
                                        String empresaId = record.get("empresa_id");
                                        String deptoId = record.get("depto_id");
                                        String centroCostoId = record.get("centro_costo_id");
                                        
                                        UsuarioVO data=new UsuarioVO();
                                        data.setUsername(username);
                                        data.setPassword(password);
                                        data.setNombres(nombres);
                                        data.setApPaterno(paterno);
                                        data.setApMaterno(materno);
                                        data.setIdPerfil(Integer.parseInt(idPerfil));
                                        data.setEmail(email);
                                        data.setEmpresaId(empresaId);
                                        data.setIdPerfil(Integer.parseInt(idPerfil));
                                        
                                        UsuarioCentroCostoVO cencousuario
                                            = new UsuarioCentroCostoVO(username,
                                                Integer.parseInt(centroCostoId),
                                                -1,
                                                null,
                                                empresaId,
                                                deptoId);
                                        usuariosOk.put(data.getUsername(), data);
                                        usuarios.add(data);
                                        centroscosto.add(cencousuario);
                                    }
                                }
                                usuarioBp.openDbConnection();
                                //guardar
                                usuarioBp.saveListUsuarios(usuarios);
                                usuarioBp.saveListCencos(centroscosto);
                            }
                        }
                    }
           
                    //File uploaded successfully
                    request.setAttribute("tipo", "UploadUsuarios");
                    request.setAttribute("usuariosOk", usuariosOk);
                    session.setAttribute("mensaje", "Archivo Cargado");
                    
                    //***************Agregar evento al log
                    MaintenanceEventVO evento=new MaintenanceEventVO();
                    evento.setUsername(userConnected.getUsername());
                    evento.setDatetime(new Date());
                    evento.setUserIP(request.getRemoteAddr());
                    evento.setType("UPLOAD_USR");//
                    evento.setEmpresaIdSource(userConnected.getEmpresaId());
                    ClientInfo clientInfo = new ClientInfo();
                    clientInfo.printClientInfo(request);
                    /**
                    * Sistema operativo y navegador
                    */
                    evento.setOperatingSystem(clientInfo.getClientOS(request));
                    evento.setBrowserName(clientInfo.getClientBrowser(request));
                    evento.setDescription("Carga de Usuarios CSV");
                    MaintenanceEventsDAO eventsDao = new MaintenanceEventsDAO(appProperties);
                    eventsDao.addEvent(evento); 
                    
                } catch (Exception ex) {
                   session.setAttribute("mensaje", "File Upload Failed due to " + ex);
                   ex.printStackTrace();
                }          
         
            }else{
                session.setAttribute("mensaje",
                    "Sorry this Servlet only handles file upload request");
            }
            request.getRequestDispatcher("/cargamasiva/upload_usuarios_result.jsp").forward(request, response);
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            System.err.println("Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
        }
    }
}
