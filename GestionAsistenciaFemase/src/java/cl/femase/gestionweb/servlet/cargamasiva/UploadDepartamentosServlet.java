/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.servlet.cargamasiva;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.DepartamentoBp;
import cl.femase.gestionweb.vo.DepartamentoVO;
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
public class UploadDepartamentosServlet extends BaseServlet {

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
                DepartamentoBp deptoBp=new DepartamentoBp(appProperties);
                MaintenanceEventVO resultado=new MaintenanceEventVO();
                LinkedHashMap<String,DepartamentoVO> deptosOk=
                    new LinkedHashMap<>();
                LinkedHashMap<String,DepartamentoVO> deptosError=
                    new LinkedHashMap<>();
                try {
                    List<FileItem> multiparts = new ServletFileUpload(
                        new DiskFileItemFactory()).parseRequest(request);

                    for(FileItem item : multiparts){
                        if(!item.isFormField()){
                            File auxfile = new File(item.getName());
                            String filename = auxfile.getName();
                            System.out.println(WEB_NAME+"[UploadDepartamentosServlet]"
                                + "Filename: "+filename);
                            String extension = FilenameUtils.getExtension(filename);
                            String filePathLoaded= pathUploadedFiles + File.separator + filename;
                            System.out.println(WEB_NAME+"[UploadDepartamentosServlet]"
                                + "filePathLoaded="+filePathLoaded);
                            item.write( new File(filePathLoaded));
                            if (extension.compareTo("csv") == 0){
                                Reader in = new FileReader(filePathLoaded);
                                Iterable<CSVRecord> records = 
                                CSVFormat.RFC4180.withHeader("empresa","id",
                                    "nombre").parse(in);
                                DepartamentoVO data;
                                for (CSVRecord record : records) {
                                    System.out.println(WEB_NAME+"[UploadDepartamentosServlet]"
                                        + "recordNumber: "+record.getRecordNumber()
                                        + "Linea csv: "+record.toString());
                                    if (record.getRecordNumber()>1){
                                        String empresa = record.get("empresa");
                                        String id = record.get("id");
                                        String nombre = record.get("nombre");
                                        
                                        data=new DepartamentoVO();
                                        data.setEmpresaId(empresa);
                                        data.setId(id);
                                        data.setNombre(nombre);
                                                                                    
                                        System.out.println(WEB_NAME+"[UploadDepartamentosServlet]"
                                            + "datadepto:"
                                            + data.toString());
                                        
                                        //insertar departamento
                                        resultado.setUsername(userConnected.getUsername());
                                        resultado.setDatetime(new Date());
                                        resultado.setUserIP(request.getRemoteAddr());
                                        resultado.setType("ADE");
                                        resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                                        
                                        ResultCRUDVO insertDepto = 
                                            deptoBp.insert(data, resultado);
                                        if (!insertDepto.isThereError()){
                                            deptosOk.put(data.getNombre(), data);
                                        }else{
                                           data.setUploadMessageError(insertDepto.getMsgError());
                                           deptosError.put(data.getNombre(), data); 
                                        }
                                    }
                                }
                            }else{
                                session.setAttribute("mensaje", 
                                    "El formato debe ser CSV");
                            }

                        }
                    }
           
                    //File uploaded successfully
                    request.setAttribute("tipo", "UploadDeptos");
                    request.setAttribute("deptosOk", deptosOk);
                    request.setAttribute("deptosError", deptosError);
                    session.setAttribute("mensaje", "Archivo Cargado");
                } catch (Exception ex) {
                   session.setAttribute("mensaje", "File Upload Failed due to " + ex);
                }          
         
            }else{
                session.setAttribute("mensaje",
                    "Sorry this Servlet only handles file upload request");
            }
            request.getRequestDispatcher("/cargamasiva/upload_deptos_result.jsp").forward(request, response);
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            System.err.println("Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
        }
    }
}
