/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.jobs;
        
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.dao.ParametroDAO;
import cl.femase.gestionweb.dao.PermisosExamenSaludPreventivaDAO;
import cl.femase.gestionweb.dao.ProcesosDAO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.ParametroVO;
import cl.femase.gestionweb.vo.ProcesoEjecucionVO;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
* Job encargado de reiniciar el número maximo anual de días de 'permisos examen salud preventiva' (disponibles y utilizados)
*     Este Job debe ser ejecutado una vez al año, como sigue: el 01-enero.
*   
*       Insertar un nuevo registro en la tabla 'permiso_examen_salud_preventiva' para el anio en curso 
*       con dias_disponibles = param(maximo_anual_pesp) y dias_utilizados = 0.    
*   
*/

public class ResetAnualDiasPESPFMCJob extends BaseJobs implements Job {

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        System.out.println(WEB_NAME+"[GestionFemase."
            + "ResetAnualDiasPESPFMCJob]"
            + "INICIO " + new java.util.Date());
        //String outputPath = m_utilconfig.geyKeyValue("pathExportedFiles");
        SimpleDateFormat sdf        = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat anioFormat = new SimpleDateFormat("yyyy");
        
        Date currentDate = new Date();
        //Gson gson = new GsonBuilder().create();
        JobDataMap data = arg0.getJobDetail().getJobDataMap();
        //lectura de parametros definidos
        String empresaId = data.getString("empresa_id");
        String execUser = data.getString("exec_user");
        int procesoId = data.getInt("proceso_id");
        
        ProcesosDAO daoProcesos = new ProcesosDAO(null);
        ParametroDAO daoParams  = new ParametroDAO(null);
        PermisosExamenSaludPreventivaDAO daoPESP  = new PermisosExamenSaludPreventivaDAO(null);
        
        ParametroVO parametro = daoParams.getParametroByKey(empresaId, Constantes.ID_PARAMETRO_MAXIMO_ANUAL_DIAS_PESP);
        int MAXIMO_ANUAL_DIAS_PESP = (int)parametro.getValor();
        int CURRENT_YEAR =  Integer.parseInt(anioFormat.format(new Date()));
                
        ProcesoEjecucionVO ejecucion=new ProcesoEjecucionVO();
        ejecucion.setEmpresaId(empresaId);
        ejecucion.setProcesoId(procesoId);
        ejecucion.setFechaHoraInicioEjecucion(sdf.format(currentDate));
        
        System.out.println(WEB_NAME+"[GestionFemase."
            + "ResetAnualDiasPESPFMCJob]"
            + "Inicia reseteo de dias Examen salud preventiva (en la tabla 'permiso_examen_salud_preventiva') "
            + "para todos los empleados Vigentes a la fecha.");
        
        System.out.println(WEB_NAME+"[GestionFemase."
            + "ResetAnualDiasPESPFMCJob]"
            + "Anio= " + CURRENT_YEAR + " (dias disponibles = " + MAXIMO_ANUAL_DIAS_PESP + ").");
                
        System.out.println(WEB_NAME+"[GestionFemase."
            + "ResetAnualDiasPESPFMCJob]"
            + "Empresa:" + empresaId);
                  
        System.out.println(WEB_NAME+"[GestionFemase."
            + "ResetAnualDiasPESPFMCJob]"
            + "ID proceso= " + procesoId);
         System.out.println(WEB_NAME+"[GestionFemase."
            + "ResetAnualDiasPESPFMCJob]"
            + "Anio= " + CURRENT_YEAR);
           
        /*
            Procedimiento:
            - eliminar registro donde year = anio actual
            - insertar registros (todos los empleados de la empresa) para el anio, con los dias del respectivo para el anio actual      
       
        */
        String result="Reseteo de dias Examen Salud Preventiva anio [" + CURRENT_YEAR + "]"; 
        
        System.out.println(WEB_NAME+"[GestionFemase.ResetAnualDiasPESPFMCJob]Reset anio " + CURRENT_YEAR);
        boolean deleteRows = daoPESP.deleteResumenPESPAnio(empresaId, CURRENT_YEAR);
        if (deleteRows){
            ResultCRUDVO resultado = daoPESP.resetearDiasExamenSaludPreventivaAnio(empresaId, MAXIMO_ANUAL_DIAS_PESP, CURRENT_YEAR);

            if (resultado.isThereError()) result+= " Error: " + resultado.getMsgError();
            else result += " Exitoso."; 
        }
        
        System.out.println(WEB_NAME+"[GestionFemase."
            + "ResetAnualDiasPESPFMCJob]"
            + "Finaliza reseteo de dias Examen Salud Preventiva para el anio en curso (en la tabla 'permiso_examen_salud_preventiva')."
            + "Empresa:" + empresaId 
            + ", anio= " + CURRENT_YEAR   
            + ", id_proceso= " + procesoId
            + ", resultado: " + result);
        ejecucion.setFechaHoraFinEjecucion(sdf.format(new Date()));
        ejecucion.setResultado(result);
        ejecucion.setUsuario(execUser);
        daoProcesos.insertItinerario(ejecucion);
       
    }
   
}
