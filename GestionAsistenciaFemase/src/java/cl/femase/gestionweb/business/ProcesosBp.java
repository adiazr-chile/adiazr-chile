/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.ProcesoEjecucionVO;
import cl.femase.gestionweb.vo.ProcesoFiltroVO;
import cl.femase.gestionweb.vo.ProcesoProgramacionVO;
import cl.femase.gestionweb.vo.ProcesoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import static org.quartz.DateBuilder.evenMinuteDate;
import static org.quartz.JobBuilder.newJob;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import static org.quartz.TriggerBuilder.newTrigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author Alexander
 */
public class ProcesosBp  extends BaseBp{

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.ProcesosDAO procesosService;
    
    public ProcesosBp(PropertiesVO props) {
        this.props      = props;
        eventsService   = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        procesosService = new cl.femase.gestionweb.dao.ProcesosDAO(this.props);
    }

    public List<ProcesoVO> getProcesos(String _empresaId, String _nombre,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<ProcesoVO> lista;
        
        System.out.println(WEB_NAME+"[ProcesosBp.getProcesos]"
            + "empresaId: "+_empresaId
            +", nombre: "+_nombre);
        lista = procesosService.getProcesos(_empresaId, _nombre, _jtStartIndex, 
            _jtPageSize, _jtSorting);
        
        return lista;
    }
    
    public List<ProcesoVO> getProcesos(String _empresaId, String _nombre){
        List<ProcesoVO> lista;
        lista = procesosService.getProcesos(_empresaId, _nombre);
        return lista;
    }

    public ProcesoVO getProceso(String _empresaId, int _idProceso){
        return procesosService.getProceso(_empresaId, _idProceso);
    }
    
    public ProcesoVO getProcesoByJobName(String _empresaId, String _jobName){
        return procesosService.getProcesoByJobName(_empresaId, _jobName);
    }
    
    public int getProcesosCount(String _empresaId, String _nombre){
        return procesosService.getProcesosCount(_empresaId, _nombre);
    }
    
    public ResultCRUDVO insert(ProcesoVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = procesosService.insert(_objToInsert);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    public ResultCRUDVO update(ProcesoVO _objectToUpdate, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = procesosService.update(_objectToUpdate);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }

    public List<ProcesoProgramacionVO> getProgramacionProcesosDia(int _dia){
        return procesosService.getProgramacionProcesosDia(_dia);
    }
    
    public ProcesoProgramacionVO getProgramacion(String _empresaId, int _idProceso, int _codDia){
        ProcesoProgramacionVO programacion = 
            procesosService.getProgramacion(_empresaId, _idProceso, _codDia);
        return programacion;
    }
    
    public ResultCRUDVO insertProgramacion(ProcesoProgramacionVO _objectToUpdate,
        MaintenanceEventVO _eventdata){
    
        ResultCRUDVO updValues = procesosService.insertProgramacion(_objectToUpdate);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }
    
    
    public ResultCRUDVO deleteProgramacion(ProcesoProgramacionVO _objectToDelete,
        MaintenanceEventVO _eventdata){
    
        ResultCRUDVO updValues = procesosService.deleteProgramacion(_objectToDelete);
        
        //if (!updValues.isThereError()){
//            String msgFinal = updValues.getMsg();
//            updValues.setMsg(msgFinal);
//            _eventdata.setDescription(msgFinal);
//            //insertar evento 
//            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }
 
    public void scheduleJobs(){
        try {
            // step 1
            SchedulerFactory sf = new StdSchedulerFactory();
            Scheduler sched = sf.getScheduler();
            sched.clear();
            sched.start();
            SimpleDateFormat fechaFmt = new SimpleDateFormat("yyyy-MM-dd");
            Locale localeCl = new Locale("es", "CL");
            Calendar fechaActual=Calendar.getInstance(localeCl);
            int codDia = fechaActual.get(Calendar.DAY_OF_WEEK);
            System.out.println(WEB_NAME+"[GestionFemase.ProcesosBp.scheduleJobs]Cod_dia hoy= "+codDia);
            codDia--;
            if (codDia==0) codDia=7;
            Date currentDate = fechaActual.getTime();
            Date dteEndDate = Utilidades.restaDias(6);
            String startDate    = fechaFmt.format(dteEndDate);
            String endDate      = fechaFmt.format(fechaActual.getTime());
            
            //domingo   --> codDia=1
            //lunes     --> codDia=2
            //martes     --> codDia=3
            //miercoles     --> codDia=4
            //jueves     --> codDia=5
            //viernes     --> codDia=6
            //sabado     --> codDia=7
            //lunes     --> codDia=8
            Date runTime = evenMinuteDate(new Date());
            //rescatar los jobs que se deben ejecutar hoy
            
            List<ProcesoProgramacionVO> listaProcesos = this.getProgramacionProcesosDia(codDia);
            
            for (int i = 0; i < listaProcesos.size(); i++) {
                ProcesoProgramacionVO auxProceso = listaProcesos.get(i);
                System.out.println(WEB_NAME+"[GestionFemase.ProcesosBp.scheduleJobs]"
                    + "empresaId:" + auxProceso.getEmpresaId()
                    + ",idProceso:" + auxProceso.getProcesoId()
                    + ",horas_ejecucion:" + auxProceso.getHorasEjecucion()
                    + ",jobClass:" + auxProceso.getJobName());
                //obtener tokenizer de horas
                if (auxProceso.getHorasEjecucion() != null 
                        && auxProceso.getHorasEjecucion().compareTo("") != 0){
                    StringTokenizer tokenHorasEjecucion = new StringTokenizer(auxProceso.getHorasEjecucion(), ",");
                    int correlativo = 1;
                    while (tokenHorasEjecucion.hasMoreTokens()){
                        String horaEjecucion = tokenHorasEjecucion.nextToken();
                        System.out.println(WEB_NAME+"[GestionFemase.ProcesosBp.scheduleJobs]"
                            + "Hora ejecucion: " + horaEjecucion);
                        String fechaHora = fechaFmt.format(fechaActual.getTime())+" "+horaEjecucion;
                        Class jobClass=null;
                        String strClassRef=""; 
                        try {
                            // define the job and tie it to our MyJob class
                            strClassRef="cl.femase.gestionweb.jobs."+auxProceso.getJobName();
                            
                            jobClass = Class.forName(strClassRef);//, true, ClassLoader.getSystemClassLoader());
                        } catch (ClassNotFoundException ex) {
                            System.err.println("[GestionFemase.ProcesosBp.scheduleJobs]"
                                + "Error al referenciar clase Job: "+ex.getMessage());
                        }
                        System.out.println(WEB_NAME+"[GestionFemase.ProcesosBp.scheduleJobs]"
                            + "fechaHora ejecucion: " + fechaHora);
                        Date startDateTime=null;
                        if (fechaHora!=null && fechaHora.compareTo("")!=0){
                            try {
                                startDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fechaHora);
                            } catch (ParseException ex) {
                                System.err.println("[GestionFemase.ProcesosBp.scheduleJobs]Error al schedular Job: "+ex.toString());
                            }
                            if (startDateTime!=null){
                                String jobname = auxProceso.getJobName()+"_"+correlativo;
                                
                                JobDetail job = newJob(jobClass)
                                    .withIdentity(jobname, "group_"+auxProceso.getJobName())
                                    .build();
                                LinkedHashMap<String, ProcesoFiltroVO> filtros 
                                    = getFiltros(auxProceso.getEmpresaId(), auxProceso.getProcesoId());
                                Set<String> keysFiltros = filtros.keySet();
                                for(String k:keysFiltros){
                                    ProcesoFiltroVO filtro = filtros.get(k);
                                    String filtroValue = "";
                                    if (filtro.getCode().compareTo("empresa_id")==0){
                                        filtroValue = "emp01";
                                    }else if (filtro.getCode().compareTo("start_date")==0){
                                        filtroValue = startDate;
                                    }else if (filtro.getCode().compareTo("end_date")==0){
                                        filtroValue = endDate;
                                    }else if (filtro.getCode().compareTo("date")==0){
                                        filtroValue = fechaFmt.format(currentDate);
                                    }
                                    
                                    System.out.println(WEB_NAME+"[ProcesosController."
                                        + "ejecutar]filtro: "+filtro.getCode()
                                        + ", value: " + filtroValue);

                                    job.getJobDataMap().put(filtro.getCode(), filtroValue);

                                }//fin iteracion de filtros
                                
                                System.out.println(WEB_NAME+"[GestionFemase.ProcesosBp.scheduleJobs]"
                                    + "Programando Job at: "+startDateTime);
                                //ver pq está ejecutando el job...
                                SimpleTrigger trigger = (SimpleTrigger)newTrigger()
                                    .withIdentity("trigger"+correlativo, "group_"+auxProceso.getJobName())
                                    .startAt(startDateTime) // some Date
                                    .forJob(jobname, "group_"+auxProceso.getJobName()) // identify job with name, group strings
                                    .build();

                                // Tell quartz to schedule the job using our trigger
                                sched.scheduleJob(job, trigger);
                                    
                                  
                            }
                        }
                        correlativo++;  
                    }//ciclo itera horas
                }//fin 
                
                    
            }//fin iteracion programacion de ejecucion procesos
            
            // Set response content type
            System.out.println(WEB_NAME+"[GestionFemase.ProcesosBp.scheduleJobs]Procesos Quartz programados exitosamente...");
        }catch ( SchedulerException de) {
            System.err.println("[GestionFemase.ProcesosBp.scheduleJobs]Error 1 al schedular procesos: "+de.toString());
            //throw new IOException(de.getMessage());
        }catch ( Exception ex) {
            System.err.println("[GestionFemase.ProcesosBp.scheduleJobs]Error 2 al schedular procesos: "+ex.toString());
            //throw new IOException(de.getMessage());
        }
    
        
    }
    
    public LinkedHashMap<String, ProcesoFiltroVO> getFiltros(String _empresaId, int _idProceso){
       return procesosService.getFiltros(_empresaId, _idProceso);
    }
    
    public List<ProcesoEjecucionVO> getItinerario(String _empresaId, int _procesoId,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        return procesosService.getItinerario(_empresaId, _procesoId, _jtStartIndex, _jtPageSize, _jtSorting);
    }
    
    public int getItinerarioCount(String _empresaId, int _procesoId){
        return procesosService.getItinerarioCount(_empresaId, _procesoId);
    }
}
