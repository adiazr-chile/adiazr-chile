/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.jobs;
        
import cl.femase.gestionweb.common.Constantes;
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
* Job encargado de:
*     Reiniciar el número de días de 'permisos administrativos' (disponibles y utilizados)
*    Esto consiste en insertar un nuevo registro en la tabla 'permiso_administrativo' para el anio en curso 
*   con dias_disponibles=6 y dias_utilizados = 0.
* 
*/

public class ReseteoDiasAdministrativosFemaseJob implements Job {

    //GetPropertyValues m_properties = new GetPropertyValues();
    
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        System.out.println("\n[GestionFemase."
            + "ReseteoDiasAdministrativosFemaseJob]"
            + "INICIO " + new java.util.Date());
        //String outputPath = m_utilconfig.geyKeyValue("pathExportedFiles");
        SimpleDateFormat sdf        = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat anioFormat = new SimpleDateFormat("yyyy");
        
        Date start              = new Date();
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
                Constantes.ID_PARAMETRO_MAXIMO_ANUAL_DIAS_PA);
        int MAXIMO_ANUAL_DIAS_PA = (int)parametro.getValor();
        int CURRENT_YEAR =  Integer.parseInt(anioFormat.format(new Date()));
        
        //ProcesosBp procesosBp = new ProcesosBp(null);
        ProcesoEjecucionVO ejecucion=new ProcesoEjecucionVO();
        //ProcesoVO proceso = procesosBp.getProcesoByJobName(empresaId, "CargaVacacionesFMCJob");
        ejecucion.setEmpresaId(empresaId);
        ejecucion.setProcesoId(procesoId);
        ejecucion.setFechaHoraInicioEjecucion(sdf.format(start));
        
        System.out.println("[GestionFemase."
            + "ReseteoDiasAdministrativosFemaseJob]"
            + "Inicia reseteo de dias administrativos (en la tabla 'permiso_administrativo'). "
            + "(dias disponibles = " + MAXIMO_ANUAL_DIAS_PA + ") "
            + "para todos los empleados Vigentes a la fecha."
            + "Empresa:" + empresaId 
            + ", id_proceso= " + procesoId 
            + ", anio= " + CURRENT_YEAR);
        boolean deleteRows = daoPA.deleteResumenPAAnio(empresaId, CURRENT_YEAR);
        if (deleteRows){
            MaintenanceVO resultado = daoPA.resetearDiasAdministrativosAnio(empresaId, MAXIMO_ANUAL_DIAS_PA, CURRENT_YEAR);

            String result="Reseteo de dias administrativos anio " + CURRENT_YEAR + "= OK. " + resultado.getMsg();
            if (resultado.isThereError()) result="Error al resetear dias administrativos anio " + CURRENT_YEAR;

            System.out.println("[GestionFemase."
                + "ReseteoDiasAdministrativosFemaseJob]Finaliza reseteo de dias administrativos (en la tabla 'permiso_administrativo')."
                + "Empresa:" + empresaId 
                + ", anio= " + CURRENT_YEAR    
                + ", id_proceso= " + procesoId
                + ", resultado: " + result
                + ", detalle: " + resultado.getMsg());

            ejecucion.setFechaHoraFinEjecucion(sdf.format(new Date()));
            ejecucion.setResultado(result);
            ejecucion.setUsuario(execUser);
            daoProcesos.insertItinerario(ejecucion);
        }
    }
   
}
