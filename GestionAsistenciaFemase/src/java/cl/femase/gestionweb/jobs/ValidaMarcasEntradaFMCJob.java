/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.jobs;
        
import cl.femase.gestionweb.business.MarcasBp;
import cl.femase.gestionweb.common.GetPropertyValues;
import cl.femase.gestionweb.common.MailSender;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.dao.ProcesosDAO;
import cl.femase.gestionweb.vo.EmpleadoJsonVO;
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
import java.util.Calendar;
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
 * Job encargado de validar la existencia de marcas de entrada/salida 
 * Entrega un listado de empleados con marcas faltantes.
 * Parametros para filtrar:
 *  .- dia asociado a la fecha actual {1:lunes, 2:martes, 3: miercoles .... 7: domingo}
 *  .- hora entrada (limite)  por ejemplo: 12:00:00
 *  .- empresa_id
 *  
 *  El listado debe ser enviado por correo al admin del sistema
 *  
 */

public class ValidaMarcasEntradaFMCJob implements Job {

    GetPropertyValues m_properties = new GetPropertyValues();
    
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        MarcasBp marcasBp = new MarcasBp(new PropertiesVO());
        System.out.println("\n[GestionFemase."
            + "ValidaMarcasEntradaFMCJob]"
            + "Validando marcas de de entrada - " + new java.util.Date());
        Calendar mycal = Calendar.getInstance();
        int year = mycal.get(Calendar.YEAR);
        int month = mycal.get(Calendar.MONTH)+1;
        int day = mycal.get(Calendar.DATE);
        int diaSemana = Utilidades.getDiaSemana(year, month, day);
        //diaSemana = 2;
                
//  0      marcasBp.getJsonEmpleadosSinMarca(empresaId, 5, "12:00:00", 1);
//        Gson gson = new GsonBuilder().create();
//        EmpleadoJsonVO empleados = gson.fromJson(reader, Person.class);

        Gson gson = new GsonBuilder().create();
        JobDataMap data = arg0.getJobDetail().getJobDataMap();
        String empresaId        = data.getString("empresa_id");
        String cencosSelected   = data.getString("cenco_id");//por ej: 35,38, 49
        String notifyCencos     = data.getString("notify_centrocosto");
        if (notifyCencos == null) notifyCencos = "N";
        
        String execUser = data.getString("exec_user");
        Date start = new Date();
        ProcesosDAO daoProcesos=new ProcesosDAO(null);
        ProcesoEjecucionVO ejecucion=new ProcesoEjecucionVO();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ejecucion.setEmpresaId(empresaId);
        ejecucion.setProcesoId(38);
        ejecucion.setFechaHoraInicioEjecucion(sdf.format(start));
        
        System.out.println("[GestionFemase." 
            + "ValidaMarcasEntradaFMCJob]empresa:" + empresaId
            + ", cencos seleccionados: " + cencosSelected
            + ", notificarCencos: " + notifyCencos);
        StringTokenizer tokenCencos=new StringTokenizer(cencosSelected, ",");
        
        while (tokenCencos.hasMoreTokens()){
            String strCencoId = tokenCencos.nextToken();
            int intCencoId = Integer.parseInt(strCencoId);
            String empleadosCencoJsonMsg = 
                marcasBp.getEmpleadosSinMarcaEntradaDiaJson(empresaId, diaSemana, "12:00:00", intCencoId);
            System.out.println("[GestionFemase."
                + "ValidaMarcasEntradaFMCJob]"
                + "Json Empleados sin marca entrada cencoID ["+intCencoId +"]:"+ empleadosCencoJsonMsg);
            EmpleadoJsonVO[] empleadosSinEntradaDia = gson.fromJson(empleadosCencoJsonMsg, 
                EmpleadoJsonVO[].class);
            if (empleadosSinEntradaDia!=null && empleadosSinEntradaDia.length>0){
                procesaEmpleadosCenco(empleadosSinEntradaDia, notifyCencos);
            }else{
                System.out.println("[GestionFemase."
                + "ValidaMarcasEntradaFMCJob]"
                + "No hay Empleados sin marca entrada para el cencoID ["+intCencoId +"]");
            }
        }
        
        String result="Validacion marcas de entrada OK";
        ejecucion.setFechaHoraFinEjecucion(sdf.format(new Date()));
        ejecucion.setResultado(result);
        ejecucion.setUsuario(execUser);
        daoProcesos.insertItinerario(ejecucion);

    }
    
    private void procesaEmpleadosCenco(EmpleadoJsonVO[] _empleadosSinEntradaDia, String _notifyCencos){
        /**
         * Seteo de objeto para envio de correo
         * 
         */
        String outputPath = m_properties.getKeyValue("pathExportedFiles");
        String fromLabel = "Gestion asistencia";
        String fromMail = m_properties.getKeyValue("mailFrom");
        String mailBody = "<p>Junto con saludar, "
            + "en el presente correo se adjuntan los colaboradores "
            + "que no han efectuado a lo menos la marca "
            + "de entrada del dia de hoy.</p> "
            + "<p>Favor verificar o actualizar los datos "
            + "en el sistema WEB (vigencia, licencias, vacaciones, etc.). "
            + "</p><br><br><b> Atte. Equipo Soporte FEMASE</b>";
            
        /**
        * Iterar los resultados y generar archivo csv por cada cenco. Luego
        * enviar dicho archivo por correo al correo definido en el cenco
        */ 
        LinkedHashMap<String, ArrayList<EmpleadoJsonVO>> empleadosCenco = new LinkedHashMap<>();
        try {
            List<AttachmentResource> attachList = new ArrayList<>();
            /**
            * 1.- Generando CSV de empleados sin marca de entrada (semana)
            */
            String cencoName = "";
            String cencoMail = "";
            ArrayList<EmpleadoJsonVO> auxEmpleados = new ArrayList<>();
            for(EmpleadoJsonVO empleado : _empleadosSinEntradaDia) {
                cencoName = empleado.getCenco_nombre();
                cencoMail = empleado.getEmail_notificacion();
                auxEmpleados.add(empleado);
            }
            empleadosCenco.put(cencoName + "|" + cencoMail, auxEmpleados);
            
            for (Map.Entry<String, ArrayList<EmpleadoJsonVO>> entry : empleadosCenco.entrySet()) {
                String strkey = entry.getKey();//cenco nombre|email notificacion
                StringTokenizer keytoken= new StringTokenizer(strkey, "|");
                String cencoNombre  = keytoken.nextToken();
                String cencoEmail   = keytoken.nextToken();
                ArrayList<EmpleadoJsonVO> empleados = entry.getValue();
                String csvSinMarcasDia = outputPath + File.separator + "sinMarcaDia_"+cencoNombre + ".csv";
                System.out.println("\n[GestionFemase."
                    + "ValidaMarcasEntradaFMCJob.procesaEmpleadosCenco]"
                    + "Csv Salida:" + csvSinMarcasDia);
                createCsvFile(empleados, csvSinMarcasDia);
                if (_notifyCencos.compareTo("S")==0){
                    if (cencoEmail != null 
                            && cencoEmail.compareTo("-1") != 0 
                            && cencoEmail.compareTo("") != 0){
                        try {
                            MailSender.sendWithAttachment(csvSinMarcasDia, fromLabel, 
                                fromMail, cencoEmail, 
                                cencoNombre + " - Marcacion no efectuada", mailBody);
                            System.out.println("[GestionFemase."
                                + "ValidaMarcasEntradaFMCJob]Correo enviado exitosamente...");
                        } catch (Exception ex) {
                            System.err.println("[GestionFemase."
                                + "ValidaMarcasEntradaFMCJob]procesaEmpleadosCenco.Error "
                                + "al adjuntar archivo a correo: " + ex.toString());
                            ex.printStackTrace();
                        }
                    }
                }else{
                    /**
                     * Solo se debe Notificar al administrador.
                     *  Se debe enviar un solo correo con todos los adjuntos
                     *  (un adjunto po cada centro de costo)
                     */
                    File initialFile1 = new File(csvSinMarcasDia);
                    InputStream targetStream1 = new FileInputStream(initialFile1);

                    AttachmentResource attachresource1 = new AttachmentResource(initialFile1.getName(), new ByteArrayDataSource(targetStream1, "text/plain"));
                    attachList.add(attachresource1);    

                }
            }
            
            if (!attachList.isEmpty()){
                System.out.println("[GestionFemase."
                    + "ValidaMarcasEntradaFMCJob]"
                    + "Enviando correo al admin, "
                    + "con todos los CSV, correo: " + m_properties.getKeyValue("mailAdmin"));
                MailSender.send(attachList, fromLabel, 
                    fromMail, m_properties.getKeyValue("mailAdmin"), 
                    "Validacion Marcas (Control Interno)", mailBody);
                System.out.println("[GestionFemase."
                    + "ValidaMarcasEntradaFMCJob]"
                    + "Correo enviado exitosamente...");
            }
            
        } catch (IOException ex) {
            System.err.println("[GestionFemase."
                + "ValidaMarcasEntradaFMCJob.procesaEmpleadosCenco]Error:"+ex.toString());
        }
    
    }
    
    private static void createCsvFile(ArrayList<EmpleadoJsonVO> _empleados, String _csvFilePath) throws IOException{
        try (
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(_csvFilePath));
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withDelimiter(';')
                    .withHeader("rut", "nombres", "paterno", "materno","depto_nombre","cenco_nombre"));
        ) {
            if (_empleados.size()>0) {
                EmpleadoJsonVO aux1= _empleados.get(0);
                System.out.println("\n[GsonExamples]Generar CSV para cenco " 
                    + aux1.getCenco_nombre());    
            }
            
            for(EmpleadoJsonVO empleado : _empleados) {
               csvPrinter.printRecord(empleado.getRut(), 
                    empleado.getNombres(), 
                    empleado.getPaterno(), 
                    empleado.getMaterno(),
                    empleado.getDepto_nombre(),
                    empleado.getCenco_nombre());
            }
            
            // csvPrinter.printRecord(Arrays.asList("4", "Mark Zuckerberg", "CEO", "Facebook"));

            csvPrinter.flush();            
        }
    }
}
