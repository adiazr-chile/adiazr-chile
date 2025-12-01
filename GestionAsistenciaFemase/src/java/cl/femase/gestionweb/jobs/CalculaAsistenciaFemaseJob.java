/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.jobs;
        
import cl.femase.gestionweb.business.CalculoAsistenciaBp;
import cl.femase.gestionweb.business.CentroCostoBp;
import cl.femase.gestionweb.business.DepartamentoBp;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.vo.CentroCostoVO;
import cl.femase.gestionweb.vo.DepartamentoVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
* 
*/
public class CalculaAsistenciaFemaseJob extends BaseJobs implements Job {

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        DepartamentoBp deptosBp         = new DepartamentoBp(new PropertiesVO());
        CentroCostoBp cencosBp          = new CentroCostoBp(new PropertiesVO());
        CalculoAsistenciaBp calculoBp   = new CalculoAsistenciaBp(new PropertiesVO(), new UsuarioVO("sistemas","",""));
        
        String empresaId="emp03";
        Locale localeCl = new Locale("es", "CL");
        Calendar mycal  = Calendar.getInstance(localeCl);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        
        Date dteEndDate = Utilidades.restaDias(6);
        
        String startDate    = sdf.format(dteEndDate);
        String endDate      = sdf.format(mycal.getTime());
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculaAsistenciaFemaseJob]"
            + "INICIO - Calculo de asistencia para "
            + "todos los centros de costo. "
            + "empresa_id: " + empresaId
            + ",Start_date: " + startDate
            + ",End_date: " + endDate);
        
//        startDate ="2018-05-01";
//        endDate ="2018-05-31";
        
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculaAsistenciaFemaseJob]"
            + "Obteniendo lista de deptos para la empresaId: " + empresaId);
        
        calculoBp.setParameters(empresaId, startDate,endDate);
        UsuarioVO auxUser = new UsuarioVO();
        auxUser.setIdPerfil(1);
            
        List<DepartamentoVO> departamentos = 
            deptosBp.getDepartamentosEmpresa(auxUser, empresaId);
        for (int i = 0; i < departamentos.size(); i++) {
            DepartamentoVO itDepto= departamentos.get(i);
            System.out.println(WEB_NAME+"[GestionFemase."
                + "CalculaAsistenciaFemaseJob]Depto: " + itDepto.getId()
                + ", nombre: " + itDepto.getNombre());
            
//            if (itDepto.getId().compareTo("05") == 0){
                List<CentroCostoVO> cencos = 
                    cencosBp.getCentrosCostoDepto(auxUser, itDepto.getId());
                for (int j = 0; j < cencos.size(); j++) {
                    CentroCostoVO itCenco = cencos.get(j);

                    ResultCRUDVO resultadoCalculo = new ResultCRUDVO();
                    if (itCenco.getId() != -1){
                        System.out.println(WEB_NAME+"[GestionFemase."
                            + "CalculaAsistenciaFemaseJob]INICIO Calculo para Cenco: " + itCenco.getId()
                            +", nombre: " + itCenco.getNombre()
                            +". startDate: " + startDate
                            +". endDate: " + endDate);
                        
                        List<String> empleadosCalculo = getListaEmpleados(empresaId, itDepto.getId(), itCenco.getId());
                        calculoBp.setAusencias(empleadosCalculo);
                        calculoBp.setMarcas(empresaId, empleadosCalculo);
                        calculoBp.setAsignacionTurnoRotativo(empresaId, empleadosCalculo);
                        
                        //calculoBp.setDetallesTurnoRotativoLaboral(empresaId, empleadosCalculo);
                        //calculoBp.setDetallesTurnoRotativoLibre(empresaId, empleadosCalculo);
                        
                        resultadoCalculo = calculoBp.calculaAsistencia(empresaId, 
                            itDepto.getId(), 
                            itCenco.getId(), 
                            empleadosCalculo, 
                            startDate, endDate);
                        System.out.println(WEB_NAME+"[GestionFemase."
                            + "CalculaAsistenciaFemaseJob]******FIN Calculo para Cenco: " + itCenco.getId()
                            +", nombre: " + itCenco.getNombre()
                            +". startDate: " + startDate
                            +". endDate: " + endDate);
                    }
//                }
            }
        }//fin iteracion de departamentos de la empresa
        
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculaAsistenciaFemaseJob]"
            + "FIN - Calculo de asistencia para "
            + "todos los centros de costo- " + new java.util.Date());
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
    
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculaAsistenciaFemaseJob]empresa: "+_empresaId
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
