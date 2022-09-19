/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.jobs;
        
import cl.femase.gestionweb.business.MarcasBp;
import cl.femase.gestionweb.common.GetPropertyValues;
import cl.femase.gestionweb.common.MailSender;
import cl.femase.gestionweb.dao.ProcesosDAO;
import cl.femase.gestionweb.vo.EmpleadoMarcaRechazoVO;
import cl.femase.gestionweb.vo.ProcesoEjecucionVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.mail.util.ByteArrayDataSource;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.simplejavamail.email.AttachmentResource;

/**
 * Job encargado de generar archivos CSV con marcas rechazadas
 *  
 */

public class MarcasRechazadasFMCJob extends BaseJobs implements Job {

    GetPropertyValues m_properties = new GetPropertyValues();
    
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        MarcasBp marcasBp = new MarcasBp(new PropertiesVO());
        System.out.println(WEB_NAME+"[GestionFemase."
            + "MarcasRechadasFMCJob]"
            + "INICIO - Informando marcas rechazadas - " + new java.util.Date());
//        Calendar mycal = Calendar.getInstance();
//        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String outputPath = m_properties.getKeyValue("pathExportedFiles");
        
        Gson gson = new GsonBuilder().create();
        JobDataMap data = arg0.getJobDetail().getJobDataMap();
        //lectura de parametros definidos
        String empresaId = data.getString("empresa_id");
        String startDate = data.getString("date");
        String notifyCencos = data.getString("notify_centrocosto");
        if (notifyCencos == null) notifyCencos = "N";
        
        String execUser = data.getString("exec_user");
        Date start = new Date();
        ProcesosDAO daoProcesos=new ProcesosDAO(null);
        ProcesoEjecucionVO ejecucion=new ProcesoEjecucionVO();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ejecucion.setEmpresaId(empresaId);
        ejecucion.setProcesoId(39);
        ejecucion.setFechaHoraInicioEjecucion(sdf.format(start));
        
        System.out.println(WEB_NAME+"[GestionFemase."
            + "MarcasRechadasFMCJob]Ejecutar."
            + "Empresa:" + empresaId
            + ", startDate:" + startDate
            + ", notifyCencos?:" + notifyCencos);
        String jsonMsg = marcasBp.getEmpleadosConMarcasRechadasFechaJson(empresaId, 
            startDate);
        System.out.println(WEB_NAME+"[GestionFemase."
            + "MarcasRechadasFMCJob]Json "
            + "Empleados con marcas rechazadas dia-str: " + jsonMsg);
        if (jsonMsg != null && jsonMsg.compareTo("null") != 0){
            EmpleadoMarcaRechazoVO[] empleadosMarcasRechazos = gson.fromJson(jsonMsg, EmpleadoMarcaRechazoVO[].class);

            /**
             * Seteo de objeto para envio de correo
             * 
             */
            String fromLabel = "Gestion asistencia";
            String fromMail = m_properties.getKeyValue("mailFrom");
            String mailBody = "<p>Junto con saludar, "
                + "en el presente correo se adjuntan los colaboradores "
                + "cuyas marcas han sido rechazadas el dia "+startDate+".</p> "
                + "<p>Favor verificar o actualizar los datos "
                + "en el sistema WEB (vigencia, licencias, vacaciones, etc.)."
                + "</p><br><br><b> Atte. Equipo Soporte FEMASE</b>";
            
            LinkedHashMap<String, ArrayList<EmpleadoMarcaRechazoVO>> empleadosCenco = new LinkedHashMap<>();
            try {

                List<AttachmentResource> attachList = new ArrayList<>();
                
                /**
                * Iterar los resultados y generar archivo csv por cada cenco. Luego
                * enviar dicho archivo por correo al correo definido en el cenco
                */

                /**
                * 1.- Generando CSV de empleados sin marca de entrada (semana)
                */
                String cencoAnterior = "";
                String emailCencoAnterior = "";
                String cencoIteracion = "";
                ArrayList<EmpleadoMarcaRechazoVO> auxEmpleados = new ArrayList<>();
                System.out.println(WEB_NAME+"1.- Iterar empleados con marcas rechazadas...");
                for(EmpleadoMarcaRechazoVO empleado : empleadosMarcasRechazos) {
                    cencoIteracion = empleado.getCenco_nombre();
    //                System.out.println(WEB_NAME+"--->Cenco Anterior: "+ cencoAnterior
    //                    +", cencoIteracion: "+cencoIteracion);
                    if (cencoAnterior.compareTo("") != 0 && cencoAnterior.compareTo(cencoIteracion) != 0){
                        empleadosCenco.put(cencoAnterior + "|" + emailCencoAnterior, auxEmpleados);
                        auxEmpleados = new ArrayList<>(); 
                        auxEmpleados.add(empleado);
                    }else {
                        auxEmpleados.add(empleado);
                    }
                    cencoAnterior = empleado.getCenco_nombre();
                    emailCencoAnterior = empleado.getEmail_notificacion();
                }

                for (Map.Entry<String, ArrayList<EmpleadoMarcaRechazoVO>> entry : empleadosCenco.entrySet()) {
                    String strkey = entry.getKey();//cenco nombre|email notificacion
                    StringTokenizer keytoken= new StringTokenizer(strkey, "|");
                    String cencoNombre  = keytoken.nextToken();
                    String cencoEmail   = keytoken.nextToken();
                    ArrayList<EmpleadoMarcaRechazoVO> empleados = entry.getValue();
                    String csvRechazos = outputPath + File.separator + "rechazos_"+cencoNombre + ".csv";
                    createCsvFile(empleados, csvRechazos);
                    String asuntoMail=cencoNombre+" - Rechazos Marcas";
                    if (notifyCencos.compareTo("S")==0){
                        if (cencoEmail!=null && cencoEmail.compareTo("-1") != 0 && cencoEmail.compareTo("") != 0){
                            System.out.println(WEB_NAME+"[GestionFemase."
                                + "MarcasRechazadasFMCJob]Enviando correo a cenco.mail: " + cencoEmail);
                            MailSender.sendWithAttachment(csvRechazos, fromLabel, 
                                fromMail, cencoEmail, 
                                asuntoMail, mailBody);
                            System.out.println(WEB_NAME+"[GestionFemase."
                                + "MarcasRechazadasFMCJob]Correo enviado exitosamente...");
                        }
                    }else{
                        /**
                         * Solo se debe Notificar al administrador.
                         *  Se debe enviar un solo correo con todos los adjuntos
                         *  (un adjunto po cada centro de costo)
                         */
                        File initialFile1 = new File(csvRechazos);
                        InputStream targetStream1 = new FileInputStream(initialFile1);

                        AttachmentResource attachresource1 = new AttachmentResource(initialFile1.getName(), new ByteArrayDataSource(targetStream1, "text/plain"));
                        attachList.add(attachresource1);    
                            
                        System.out.println(WEB_NAME+"[GestionFemase."
                            + "MarcasRechazadasFMCJob]Enviando correo al admin.mail: " + cencoEmail);
                    }
                    
                }
                if (!attachList.isEmpty()){
                    System.out.println(WEB_NAME+"[GestionFemase."
                        + "MarcasRechazadasFMCJob]"
                        + "Enviando correo al admin, "
                        + "con todos los CSV, correo: " + m_properties.getKeyValue("mailAdmin"));
                    MailSender.send(attachList, fromLabel, 
                        fromMail, m_properties.getKeyValue("mailAdmin"), 
                        "Marcas rechazadas (Control Interno)", mailBody);
                    System.out.println(WEB_NAME+"[GestionFemase."
                        + "MarcasRechazadasFMCJob]"
                        + "Correo enviado exitosamente...");
                }
                System.out.println(WEB_NAME+"[GestionFemase."
                    + "MarcasRechadasFMCJob]"
                    + "FIN - Informando marcas rechazadas - " + new java.util.Date());
                
                String result="Marcas rechazadas OK";
                ejecucion.setFechaHoraFinEjecucion(sdf.format(new Date()));
                ejecucion.setResultado(result);
                ejecucion.setUsuario(execUser);
                daoProcesos.insertItinerario(ejecucion);
            } catch (IOException ex) {
                System.err.println("[GestionFemase."
                    + "MarcasRechazadasFMCJob]Error:"+ex.toString());
                String result="Marcas rechazadas Error";
                ejecucion.setFechaHoraFinEjecucion(sdf.format(new Date()));
                ejecucion.setResultado(result);
                ejecucion.setUsuario(execUser);
                daoProcesos.insertItinerario(ejecucion);
            }
        }//else no hay empleados
    }
    
    private static void createCsvFile(ArrayList<EmpleadoMarcaRechazoVO> _empleados, 
            String _csvFilePath) throws IOException{
        try (
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(_csvFilePath));
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withDelimiter(';')
                    .withHeader("rut", "nombres", 
                            "paterno", "materno",
                            "depto_nombre","cenco_nombre",
                            "tipo","fecha_hora",
                            "motivo_rechazo"));
        ) {
            if (_empleados.size()>0) {
                EmpleadoMarcaRechazoVO aux1= _empleados.get(0);
                System.out.println(WEB_NAME+"[GsonExamples]Generar CSV para cenco " 
                    + aux1.getCenco_nombre());    
            }
            
            for(EmpleadoMarcaRechazoVO empleado : _empleados) {
               csvPrinter.printRecord(empleado.getRut(), 
                    empleado.getNombres(), 
                    empleado.getPaterno(), 
                    empleado.getMaterno(),
                    empleado.getDepto_nombre(),
                    empleado.getCenco_nombre(),
                    empleado.getLabel_tipo_marca(),
                    empleado.getFecha_hora(),
                    empleado.getMotivo_rechazo());
            }
            
            // csvPrinter.printRecord(Arrays.asList("4", "Mark Zuckerberg", "CEO", "Facebook"));

            csvPrinter.flush();             
        }
    }
}
