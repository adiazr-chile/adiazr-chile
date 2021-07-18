/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.jobs;
        
import cl.femase.gestionweb.business.CalculoAsistenciaBp;
import cl.femase.gestionweb.business.CalculoAsistenciaRunnable;
import cl.femase.gestionweb.business.CentroCostoBp;
import cl.femase.gestionweb.business.DepartamentoBp;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.dao.ProcesosDAO;
import cl.femase.gestionweb.vo.CentroCostoVO;
import cl.femase.gestionweb.vo.DepartamentoVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.ProcesoEjecucionVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class CalculaAsistenciaFMCJob implements Job {

    /**
     *
     * @param arg0
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        DepartamentoBp deptosBp         = new DepartamentoBp(new PropertiesVO());
        CentroCostoBp cencosBp          = new CentroCostoBp(new PropertiesVO());
        CalculoAsistenciaBp calculoBp   = new CalculoAsistenciaBp(new PropertiesVO());
        EmpleadosBp empleadosBp         = new EmpleadosBp(new PropertiesVO());
        
        JobDataMap data = arg0.getJobDetail().getJobDataMap();
        //List<EmpleadoVO> empleados = new ArrayList<>();
        //set lista de empleados seleccionados como lista de string
        ArrayList<String> listaStrEmpleados=new ArrayList<>();
        List<EmpleadoVO> empleados = new ArrayList<>();
        String empresaId=data.getString("empresa_id");
        //Locale localeCl = new Locale("es", "CL");
        //Calendar mycal  = Calendar.getInstance(localeCl);
        //SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        
        //Date dteEndDate = Utilidades.restaDias(6);
        /*
        String startDate    = sdf.format(dteEndDate);
        String endDate      = sdf.format(mycal.getTime());
        */
        String startDate = data.getString("start_date");
        String endDate = data.getString("end_date");
        
        String execUser = data.getString("exec_user");
        Date start = new Date();
        ProcesoEjecucionVO ejecucion=new ProcesoEjecucionVO();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ejecucion.setEmpresaId(empresaId);
        ejecucion.setProcesoId(37);
        ejecucion.setFechaHoraInicioEjecucion(sdf.format(start));
            
        System.out.println("\n[GestionFemase."
            + "CalculaAsistenciaFMCJob]"
            + "INICIO - Calculo de asistencia para "
            + "todos los centros de costo. "
            + "empresaId: " + empresaId    
            + ", Start_date: " + startDate
            + ", End_date: " + endDate);
        
//        startDate ="2018-05-01";
//        endDate ="2018-05-31";
        
        System.out.println("[GestionFemase."
            + "CalculaAsistenciaFMCJob]"
            + "Obteniendo lista de deptos para la empresaId: " + empresaId);
        
        calculoBp.setParameters(empresaId, startDate, endDate);
        
        UsuarioVO auxUser = new UsuarioVO();
        auxUser.setIdPerfil(1);
        
        List<DepartamentoVO> departamentos = 
            deptosBp.getDepartamentosEmpresa(auxUser, empresaId);
        for (int i = 0; i < departamentos.size(); i++) {
            DepartamentoVO itDepto= departamentos.get(i);
            System.out.println("[GestionFemase."
                + "CalculaAsistenciaFMCJob]Depto: " + itDepto.getId()
                + ", nombre: " + itDepto.getNombre());
            
//            if (itDepto.getId().compareTo("05") == 0){
                List<CentroCostoVO> cencos = 
                    cencosBp.getCentrosCostoDepto(auxUser, itDepto.getId());
                for (int j = 0; j < cencos.size(); j++) {
                    CentroCostoVO itCenco = cencos.get(j);

                    MaintenanceVO resultadoCalculo = new MaintenanceVO();
                    if (itCenco.getId() != -1){
                        System.out.println("[GestionFemase."
                            + "CalculaAsistenciaFMCJob]INICIO Calculo para Cenco: " + itCenco.getId()
                            +", nombre: " + itCenco.getNombre()
                            +". startDate: " + startDate
                            +". endDate: " + endDate);
                        listaStrEmpleados=new ArrayList<>();
                        List<EmpleadoVO> listaEmpleados = 
                            empleadosBp.getListaEmpleadosJson(empresaId, itDepto.getId(), itCenco.getId());
                        if (listaEmpleados != null){
                            for (EmpleadoVO temp : listaEmpleados) {
                                listaStrEmpleados.add(temp.getRut());
                            }
                        }
                        if (listaStrEmpleados.size() > 0){
                            empleados = empleadosBp.getListaEmpleadosComplete(empresaId, 
                                itDepto.getId(), 
                                itCenco.getId(), 
                                listaStrEmpleados);

                            /**
                             * Set datos a utilizar en los calculos (carga fria)
                             */
                            CalculoAsistenciaRunnable.setData(startDate, endDate, empresaId, listaStrEmpleados);
                            /**
                             * Inicializa las hebras.
                             * Crea una hebra para cada empleado.
                             * El calculo de asistencia es iniciado para cada uno de los empleados.
                             */
                            CalculoAsistenciaRunnable.setHebras(empleados);
                            System.out.println("[GestionFemase."
                                + "DetalleAsistenciaController]"
                                + "FIN CALCULO ASISTENCIA...");

                            CalculoAsistenciaRunnable.listaCalculosEmpleado.clear();

    //                        resultadoCalculo = calculoBp.calculaAsistencia(empresaId, 
    //                            itDepto.getId(), 
    //                            itCenco.getId(), 
    //                            empleadosCalculo, 
    //                            startDate, endDate);
                            System.out.println("[GestionFemase."
                                + "CalculaAsistenciaFMCJob]******FIN Calculo para Cenco: " + itCenco.getId()
                                +", nombre: " + itCenco.getNombre()
                                +". startDate: " + startDate
                                +". endDate: " + endDate);
                        }else{
                            System.out.println("[GestionFemase."
                                + "CalculaAsistenciaFMCJob]No hay empleados para Cenco: " + itCenco.getId()
                                +", nombre: " + itCenco.getNombre());
                        }    
                    }
//                }
            }
        }//fin iteracion de departamentos de la empresa
        
        System.out.println("\n[GestionFemase."
            + "CalculaAsistenciaFMCJob]"
            + "FIN - Calculo de asistencia para "
            + "todos los centros de costo- " + new java.util.Date());
        ProcesosDAO daoProcesos=new ProcesosDAO(null);
        String result="Calculo de asistencia OK";
        ejecucion.setFechaHoraFinEjecucion(sdf.format(new Date()));
        ejecucion.setResultado(result);
        ejecucion.setUsuario(execUser);
        daoProcesos.insertItinerario(ejecucion);
    }
    
    /**
     * Obtiene lista de empleados para el centro de costo especificado
     * 
     * 
     * @param _empresaId
     * @param _deptoId
     * @param _cencoId
     * @return 
     */
    private List<String> getListaEmpleados(String _empresaId,
            String _deptoId, 
            int _cencoId){
    
        System.out.println("[GestionFemase."
            + "CalculaAsistenciaFMCJob]empresa: "+_empresaId
            +", depto: "+_deptoId
            +", cenco: "+_cencoId);
        
        EmpleadosBp empleadosBp = new EmpleadosBp();
        List<String> listaEmpleados = new ArrayList<>();
        if (_empresaId.compareTo("-1") != 0 
                && _deptoId.compareTo("-1") != 0
                && _cencoId != -1){
                    //todos los empleados del cenco
                    List<EmpleadoVO> auxListaEmpleados = empleadosBp.getEmpleadosShort(_empresaId, 
                            _deptoId, 
                            _cencoId, 
                            -1,  
                            null, 
                            null, 
                            null, 
                            null, 
                            0, 
                            0, 
                            "empleado.empl_rut");
                    
                    for (int i = 0; i < auxListaEmpleados.size(); i++) {
			listaEmpleados.add(auxListaEmpleados.get(i).getRut());
                    }
                    
        }
        return listaEmpleados;
    }
}
