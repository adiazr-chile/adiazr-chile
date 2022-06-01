/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.jobs;
        
import cl.femase.gestionweb.dao.ProcesosDAO;
import cl.femase.gestionweb.dao.VacacionesDAO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.ProcesoEjecucionVO;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
* Job encargado de:
*      Cargar todos los empleados faltantes en la tabla 'vacaciones'
*      
* 
*/

public class CargaVacacionesFMCJob implements Job {

    //GetPropertyValues m_properties = new GetPropertyValues();
    
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        System.out.println("\n[GestionFemase."
            + "CargaVacacionesFMCJob]"
            + "INICIO " + new java.util.Date());
        //String outputPath = m_utilconfig.geyKeyValue("pathExportedFiles");
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = new Date();
        //Gson gson = new GsonBuilder().create();
        JobDataMap data = arg0.getJobDetail().getJobDataMap();
        //lectura de parametros definidos
        String empresaId = data.getString("empresa_id");
        String execUser = data.getString("exec_user");
        int procesoId = data.getInt("proceso_id");
        
        ProcesosDAO daoProcesos=new ProcesosDAO(null);
        VacacionesDAO daoVacaciones = new VacacionesDAO(null);
        //ProcesosBp procesosBp = new ProcesosBp(null);
        ProcesoEjecucionVO ejecucion=new ProcesoEjecucionVO();
        //ProcesoVO proceso = procesosBp.getProcesoByJobName(empresaId, "CargaVacacionesFMCJob");
        ejecucion.setEmpresaId(empresaId);
        ejecucion.setProcesoId(procesoId);
        ejecucion.setFechaHoraInicioEjecucion(sdf.format(start));
        
        System.out.println("[GestionFemase."
            + "CargaVacacionesFMCJob]Inicia carga de registros faltantes en tabla vacaciones."
            + "Empresa:" + empresaId+", id_proceso= " + procesoId);
       
        MaintenanceVO resultado = daoVacaciones.insertVacacionesFaltantes(empresaId);
        
        String result="Carga de registros faltantes de vacaciones= OK. " + resultado.getMsg();
        if (resultado.isThereError()) result="Error al Carga de registros faltantes de vacaciones";
        
        System.out.println("[GestionFemase."
            + "CargaVacacionesFMCJob]Finaliza carga de registros faltantes en tabla vacaciones."
            + "Empresa:" + empresaId 
            + ", id_proceso= " + procesoId
            + ", resultado: " + result
            + ", detalle: " + resultado.getMsg());
        
        ejecucion.setFechaHoraFinEjecucion(sdf.format(new Date()));
        ejecucion.setResultado(result);
        ejecucion.setUsuario(execUser);
        daoProcesos.insertItinerario(ejecucion);
        
    }
    
////    private static void createCsvFile(ArrayList<EmpleadoMarcaRechazoVO> _empleados, 
////            String _csvFilePath) throws IOException{
////        try (
////            BufferedWriter writer = Files.newBufferedWriter(Paths.get(_csvFilePath));
////            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withDelimiter(';')
////                    .withHeader("rut", "nombres", 
////                            "paterno", "materno",
////                            "depto_nombre","cenco_nombre",
////                            "tipo","fecha_hora",
////                            "motivo_rechazo"));
////        ) {
////            if (_empleados.size()>0) {
////                EmpleadoMarcaRechazoVO aux1= _empleados.get(0);
////                System.out.println("[GestionFemase."
////                    + "CargaVacacionesFMCJob]Generar CSV para cenco " 
////                    + aux1.getCenco_nombre());    
////            }
////            
////            for(EmpleadoMarcaRechazoVO empleado : _empleados) {
////               csvPrinter.printRecord(empleado.getRut(), 
////                    empleado.getNombres(), 
////                    empleado.getPaterno(), 
////                    empleado.getMaterno(),
////                    empleado.getDepto_nombre(),
////                    empleado.getCenco_nombre(),
////                    empleado.getLabel_tipo_marca(),
////                    empleado.getFecha_hora(),
////                    empleado.getMotivo_rechazo());
////            }
////            
////            csvPrinter.flush();             
////        }
////    }
}
