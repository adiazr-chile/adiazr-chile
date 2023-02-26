/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.servlet.cargamasiva;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.VacacionesBp;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import cl.femase.gestionweb.vo.VacacionesVO;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
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
public class UploadDiasVacacionesServlet extends BaseServlet {

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
            //process only if its multipart content
            if(ServletFileUpload.isMultipartContent(request)){
                String pathUploadedFiles = appProperties.getUploadsPath();
                VacacionesBp vacacionBp = new VacacionesBp(appProperties);
                try {
                    List<FileItem> multiparts = new ServletFileUpload(
                        new DiskFileItemFactory()).parseRequest(request);
                    ArrayList<VacacionesVO> registros = new ArrayList<>(); 
                    LinkedHashMap<String,VacacionesVO> registrosOk=
                        new LinkedHashMap<>();
                
                    for(FileItem item : multiparts){
                        if(!item.isFormField()){
                            File auxfile = new File(item.getName());
                            String filename = auxfile.getName();
                            System.out.println(WEB_NAME+"[UploadDiasVacacionesServlet]"
                                + "Filename: "+filename);
                            String extension = FilenameUtils.getExtension(filename);
                            String filePathLoaded= pathUploadedFiles + File.separator + filename;
                            item.write( new File(filePathLoaded));
                            if (extension.compareTo("csv") == 0){
                                Reader in = new FileReader(filePathLoaded);
                                Iterable<CSVRecord> records = 
                                CSVFormat.RFC4180.withHeader("empresa_id"                                                                                                 ,
                                    "rut_empleado",
                                    "dias_progresivos",
                                    "afp",
                                    "fecha_certif_afp",
                                    "dias_especiales",
                                    "dias_adicionales"
                                    ).parse(in);
                                
                                for (CSVRecord record : records) {
                                    System.out.println(WEB_NAME+"[UploadDiasVacacionesServlet]"
                                        + "Linea csv: "+record.toString());
                                    if (record.getRecordNumber()>1){
                                        String empresaId = record.get("empresa_id");
                                        String rutEmpleado = record.get("rut_empleado");
                                        String diasProgresivos = record.get("dias_progresivos");
                                        String afpCode = record.get("afp");
                                        String fechaCertifVacacionesProgresivas = record.get("fecha_certif_afp");
                                        String diasEspeciales = record.get("dias_especiales");//S o N
                                        String diasAdicionales = record.get("dias_adicionales");
                                        
                                        VacacionesVO infoVacacion = new VacacionesVO();
                                        
                                        infoVacacion.setEmpresaId(empresaId);
                                        infoVacacion.setRutEmpleado(rutEmpleado);
                                        infoVacacion.setDiasProgresivos(Integer.parseInt(diasProgresivos));
                                        infoVacacion.setAfpCode(afpCode);
                                        infoVacacion.setFechaCertifVacacionesProgresivas(fechaCertifVacacionesProgresivas);
                                        infoVacacion.setDiasEspeciales(diasEspeciales);
                                        infoVacacion.setDiasAdicionales(Double.parseDouble(diasAdicionales));
                                        
                                        //infoVacacion.setDiasEspeciales(Integer.parseInt(diasEspeciales));
                                        //infoVacacion.setFechaInicioUltimasVacaciones(iniUltVacaciones);
                                        //infoVacacion.setFechaFinUltimasVacaciones(finUltVacaciones);
                                        
                                        //infoVacacion.setNumActualCotizaciones(Integer.parseInt(numCotizacionesActual));
                                        
                                        registrosOk.put(infoVacacion.getEmpresaId()+"|"+infoVacacion.getRutEmpleado(), infoVacacion);
                                        registros.add(infoVacacion);
                                        
                                    }
                                }
                                vacacionBp.openDbConnection();
                                //guardar
                                vacacionBp.deleteListVacaciones(registros);
                                vacacionBp.saveListVacaciones(registros);
                                
                            }
                        }
                    }
           
                    //File uploaded successfully
                    request.setAttribute("tipo", "UploadInfoVacaciones");
                    request.setAttribute("vacacionesOk", registrosOk);
                    session.setAttribute("mensaje", "Archivo Cargado");
                } catch (Exception ex) {
                   session.setAttribute("mensaje", "File Upload Failed due to " + ex);
                   ex.printStackTrace();
                }          
         
            }else{
                session.setAttribute("mensaje",
                    "Sorry this Servlet only handles file upload request");
            }
            request.getRequestDispatcher("/cargamasiva/upload_info_vacaciones_result.jsp").forward(request, response);
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            System.err.println("Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
        }
    }
}
