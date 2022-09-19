/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.jobs;
        
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.dao.ParametroDAO;
import cl.femase.gestionweb.dao.PermisosAdministrativosDAO;
import cl.femase.gestionweb.dao.ProcesosDAO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.ParametroVO;
import cl.femase.gestionweb.vo.ProcesoEjecucionVO;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
* Job encargado de reiniciar el número de días de 'permisos administrativos' semestrales (disponibles y utilizados)
*     Este Job debe ser ejecutado dos veces al año, como sigue: el 01-enero y el 01-julio de cada anio
*   
*   SI (semestre_actual = 1)  ENTONCES {
*       Insertar un nuevo registro en la tabla 'permiso_administrativo' para el anio-semestre en curso 
*       con dias_disponibles_semestre1 = param(MAXIMO_DIAS_PA_SEMESTRE) y dias_utilizados_semestre1 = 0.    
*   }SINO {
*       Insertar un nuevo registro en la tabla 'permiso_administrativo' para el anio-semestre en curso 
*       con dias_disponibles_semestre2 = param(MAXIMO_DIAS_PA_SEMESTRE) y dias_utilizados_semestre2 = 0.    
*   } 
* 
*    
* 
*/

public class ResetSemestralDiasAdministrativosFMCJob extends BaseJobs implements Job {

    //GetPropertyValues m_properties = new GetPropertyValues();
    
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        System.out.println(WEB_NAME+"[GestionFemase."
            + "ResetSemestralDiasAdministrativosFMCJob]"
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
        PermisosAdministrativosDAO daoPA  = new PermisosAdministrativosDAO(null);
        
        ParametroVO parametro =
            daoParams.getParametroByKey(empresaId, 
                Constantes.ID_PARAMETRO_MAXIMO_SEMESTRAL_DIAS_PA);
        int MAXIMO_SEMESTRAL_DIAS_PA = (int)parametro.getValor();
        int CURRENT_YEAR =  Integer.parseInt(anioFormat.format(new Date()));
        
        int semestreActual = Utilidades.getSemestre(currentDate);
                
        ProcesoEjecucionVO ejecucion=new ProcesoEjecucionVO();
        ejecucion.setEmpresaId(empresaId);
        ejecucion.setProcesoId(procesoId);
        ejecucion.setFechaHoraInicioEjecucion(sdf.format(currentDate));
        
        System.out.println(WEB_NAME+"[GestionFemase."
            + "ResetSemestralDiasAdministrativosFMCJob]"
            + "Inicia reseteo de dias administrativos (en la tabla 'permiso_administrativo') "
            + "para todos los empleados Vigentes a la fecha.");
        
        System.out.println(WEB_NAME+"[GestionFemase."
            + "ResetSemestralDiasAdministrativosFMCJob]"
            + "Semestre= " + semestreActual + " (dias disponibles = " + MAXIMO_SEMESTRAL_DIAS_PA + ").");
                
        System.out.println(WEB_NAME+"[GestionFemase."
            + "ResetSemestralDiasAdministrativosFMCJob]"
            + "Empresa:" + empresaId);
                  
        System.out.println(WEB_NAME+"[GestionFemase."
            + "ResetSemestralDiasAdministrativosFMCJob]"
            + "ID proceso= " + procesoId);
         System.out.println(WEB_NAME+"[GestionFemase."
            + "ResetSemestralDiasAdministrativosFMCJob]"
            + "Anio= " + CURRENT_YEAR);
           
        /*
        si semestreActual=1 --> 
                - eliminar registro donde year = anio actual
                - insertar registros (todos los empleados de la empresa) para el anio, con los dias del respectivo para el semestre_1       
        sino 
            update registro en tabla permiso_administrativo donde year=anio_actual. Update dias_disponibles_semestre2 y dias_utilizados_semestre_2=0            
        */
        String result="Reseteo de dias administrativos anio-semestre [" + CURRENT_YEAR + "-" + semestreActual + "]"; 
        if (semestreActual == 1){
            System.out.println(WEB_NAME+"[GestionFemase.ResetSemestralDiasAdministrativosFMCJob]Reset 1er Semestre. anio " + CURRENT_YEAR);
            boolean deleteRows = daoPA.deleteResumenPAAnio(empresaId, CURRENT_YEAR);
            if (deleteRows){
                MaintenanceVO resultado = daoPA.resetearDiasAdministrativosSemestre(empresaId, MAXIMO_SEMESTRAL_DIAS_PA, CURRENT_YEAR, semestreActual);
                
                if (resultado.isThereError()) result+= " Error: " + resultado.getMsgError();
                else result += " Exitoso."; 
            }
            
        }else{
            MaintenanceVO resultadoUpdate = daoPA.updateDiasAdministrativosSemestre(empresaId, CURRENT_YEAR, semestreActual, MAXIMO_SEMESTRAL_DIAS_PA);
            if (resultadoUpdate.isThereError()) result += " Error: " + resultadoUpdate.getMsgError();
            else result += " Exitoso."; 
        }
        System.out.println(WEB_NAME+"[GestionFemase."
            + "ResetSemestralDiasAdministrativosFMCJob]"
            + "Finaliza reseteo de dias administrativos para el semestre en curso (en la tabla 'permiso_administrativo')."
            + "Empresa:" + empresaId 
            + ", anio= " + CURRENT_YEAR   
            + ", semestre= " + semestreActual    
            + ", id_proceso= " + procesoId
            + ", resultado: " + result);
        ejecucion.setFechaHoraFinEjecucion(sdf.format(new Date()));
        ejecucion.setResultado(result);
        ejecucion.setUsuario(execUser);
        daoProcesos.insertItinerario(ejecucion);
       
    }
   
}
