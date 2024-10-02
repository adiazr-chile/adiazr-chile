 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.jobs;
        
import cl.femase.gestionweb.business.CalculoVacacionesBp;
import cl.femase.gestionweb.business.CentroCostoBp;
import cl.femase.gestionweb.business.DepartamentoBp;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.common.CalculadoraPeriodo;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.dao.CalculoVacacionesDAO;
import cl.femase.gestionweb.dao.ProcesosDAO;
import cl.femase.gestionweb.vo.CentroCostoVO;
import cl.femase.gestionweb.vo.DepartamentoVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.ProcesoEjecucionVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.SetVBAEmpleadoVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import cl.femase.gestionweb.vo.VacacionesSaldoPeriodoVO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
* Job encargado de calcule el saldo VBA para cada período de todos los empleados. 
* Los períodos inician a partir de la fecha de inicio de contrato de c/empleado.    
*   
*/

public class SetSaldoVBAPeriodosJob extends BaseJobs implements Job {

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        System.out.println(WEB_NAME + "[GestionFemase."
            + "SetSaldoVBAPeriodosJob.execute]"
            + "INICIO " + new java.util.Date());
        DepartamentoBp deptosBp         = new DepartamentoBp(new PropertiesVO());
        CentroCostoBp cencosBp          = new CentroCostoBp(new PropertiesVO());
        EmpleadosBp empleadosBp         = new EmpleadosBp(new PropertiesVO());
        //ProcesosBp procesosBp           = new ProcesosBp(new PropertiesVO());
        CalculoVacacionesDAO calculoDao = new CalculoVacacionesDAO();
        CalculoVacacionesBp calculoVacacionesBp = new CalculoVacacionesBp(null);
        
        //UsuarioBp usuarioBp             = new UsuarioBp(new PropertiesVO());
        
        JobDataMap data = arg0.getJobDetail().getJobDataMap();
        ArrayList<String> listaStrEmpleados=new ArrayList<>();
        List<EmpleadoVO> empleadosCenco = new ArrayList<>();
        String empresaId=data.getString("empresa_id");
        String startDate = data.getString("start_date");
        String endDate = data.getString("end_date");
        String execUser = data.getString("exec_user");
        int processId   = data.getInt("proceso_id");
        
        UsuarioVO userConnectedVO = (UsuarioVO)data.get("exec_user_obj");
        
        Date start = new Date();
        //SimpleDateFormat fechaFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar currentCalendar = Calendar.getInstance(new Locale("es","CL"));
        int currentYear = currentCalendar.get(Calendar.YEAR);
        LocalDate currentLocalDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        SimpleDateFormat fechaHoraFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        //ProcesoVO proceso = procesosBp.getProcesoByJobName(empresaId, "SetSaldoVBAPeriodosJob");
        
        ProcesoEjecucionVO ejecucion=new ProcesoEjecucionVO();
        ejecucion.setEmpresaId(empresaId);
        ejecucion.setProcesoId(processId);
        ejecucion.setFechaHoraInicioEjecucion(fechaHoraFormat.format(start));
            
        System.out.println(WEB_NAME + "[GestionFemase.SetSaldoVBAPeriodosJob.execute]"
            + "INICIO - Calculo Saldo VBA para cada periodo. "
            + "Para todos los empleados de todos los centros de costo. "
            + "empresaId: " + empresaId    
            + ", Start_date: " + startDate
            + ", End_date: " + endDate);
        
        System.out.println(WEB_NAME+"[GestionFemase.SetSaldoVBAPeriodosJob.execute]"
            + "Obteniendo lista de deptos para la empresaId: " + empresaId);
        List<DepartamentoVO> departamentos = 
            deptosBp.getDepartamentosEmpresa(userConnectedVO, empresaId);
        for (int i = 0; i < departamentos.size(); i++) {
            DepartamentoVO itDepto= departamentos.get(i);
            System.out.println(WEB_NAME 
                + "[GestionFemase.SetSaldoVBAPeriodosJob.execute]"
                + "Depto: " + itDepto.getId()
                + ", nombre: " + itDepto.getNombre());
            
            List<CentroCostoVO> cencos = 
                cencosBp.getCentrosCostoDepto(userConnectedVO, itDepto.getId());
            for (int j = 0; j < cencos.size(); j++) {
                CentroCostoVO itCenco = cencos.get(j);

                ResultCRUDVO resultadoCalculo = new ResultCRUDVO();
                if (itCenco.getId() != -1){
                    System.out.println(WEB_NAME 
                        + "[SetSaldoVBAPeriodosJob.execute]"
                        + "INICIO Calculo para Cenco: " + itCenco.getId()
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
                        empleadosCenco = empleadosBp.getListaEmpleadosComplete(empresaId, 
                            itDepto.getId(), 
                            itCenco.getId(), 
                            listaStrEmpleados);
                        
                        for (EmpleadoVO empleado : empleadosCenco) {
                            System.out.println(WEB_NAME 
                                + "[SetSaldoVBAPeriodosJob.execute]Itera empleado. "
                                + "Run: " + empleado.getRut()
                                + ", fecha inicio contrato: " + empleado.getFechaInicioContratoAsStr());
                            
                            LocalDate fechaInicioContrato = LocalDate.parse(empleado.getFechaInicioContratoAsStr());
                            int mesInicioContrato = fechaInicioContrato.getMonth().getValue();
                            int diaInicioContrato = fechaInicioContrato.getDayOfMonth();

                            LocalDate fechaFinalDeseada = LocalDate.of(currentYear, mesInicioContrato, diaInicioContrato);
                            if (fechaFinalDeseada.isBefore(currentLocalDate)){
                                fechaFinalDeseada= fechaFinalDeseada.plusYears(1);
                            }
                            // Calcular los períodos transcurridos
                            List<VacacionesSaldoPeriodoVO> periodos = 
                                CalculadoraPeriodo.getPeriodosTranscurridos(fechaInicioContrato, fechaFinalDeseada);
                            
                            // Mostrar los períodos
                            List<SetVBAEmpleadoVO> empleadosSetVBA = new ArrayList<>();
                            for (int k = 0; k < periodos.size(); k++) {
                                VacacionesSaldoPeriodoVO periodo = periodos.get(k);
                                String strFecInicioPeriodo = periodo.getFechaInicio().format(formatter);
                                String strFecFinPeriodo = periodo.getFechaFin().format(formatter);
                                
                                System.out.println(WEB_NAME + "[SetSaldoVBAPeriodosJob.execute]"
                                    + "Periodo " + (k + 1) + ". Desde el " + strFecInicioPeriodo + " al " + strFecFinPeriodo);
                                
//                                System.out.println(WEB_NAME + "[SetSaldoVBAPeriodosJob.execute]"
//                                    + "Bloque comentado");
                                SetVBAEmpleadoVO empleadoInput = new SetVBAEmpleadoVO();

                                empleadoInput.setEmpresa_id(empleado.getEmpresaid());
                                empleadoInput.setRun_empleado(empleado.getRut());
                                empleadoInput.setFecha_inicio_contrato(empleado.getFechaInicioContratoAsStr());
                                empleadoInput.setFecha_inicio_periodo(strFecInicioPeriodo);
                                empleadoInput.setFecha_fin_periodo(strFecFinPeriodo);
                                empleadosSetVBA.add(empleadoInput);

                                System.out.println(WEB_NAME + "[SetSaldoVBAPeriodosJob.execute]"
                                    + "Seteando objeto final para serVBANew: " + empleadosSetVBA.toString());
                            }//fin iteracion de periodos
                            
                            ResultCRUDVO modifiedInfo=new ResultCRUDVO();
                            if (!empleadosSetVBA.isEmpty()){
                                System.out.println(WEB_NAME + "[SetSaldoVBAPeriodosJob.execute]"
                                    + "Antes de invocar a setVBANew...");
                                //transformar ArrayList a json
                                // Convertir la lista de Contratos a JSON usando Gson
                                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                String jsonEmpleadosToSet = gson.toJson(empleadosSetVBA);

                                /**        
                                * Generar un json con los datos del o los empleados seleccionados.(uno o mas)
                                * Este json es param de entrada de la funcion 'set_vba_empleados'
                                * El metodo 'calculoDao.setVBANew' invoca a la nueva función "set_vba_empleados"         
                                **/
                                modifiedInfo = calculoDao.setVBANew(jsonEmpleadosToSet);
                                System.out.println(WEB_NAME + "[SetSaldoVBAPeriodosJob.execute]"
                                    + "Despues de invocar a setVBANew. "
                                    + "modifiedInfo= " + modifiedInfo);
                                //if (modifiedInfo != null && modifiedInfo.getFilasAfectadasObj() != null){
//                                    System.out.println(WEB_NAME + "[SetSaldoVBAPeriodosJob.execute]"
//                                        + "Filas afectadas, post ejecucion de la funcion " + Constantes.fnSET_VBA_EMPLEADOS
//                                        + ": " + modifiedInfo.getFilasAfectadasObj().toString());
//                                    
                                    System.out.println(WEB_NAME+"[SetSaldoVBAPeriodosJob.execute]"
                                        + "Set estado de saldos de vacaciones por periodos...");
                                    ResultCRUDVO fnExec2 = 
                                        calculoVacacionesBp.setEstadoSaldosVacacionesPeriodos(empresaId, empleado.getRut());
                                //}        
                                        
                            }else{
                                System.out.println(WEB_NAME+"[SetSaldoVBAPeriodosJob.execute]No se encontraron empleados...");
                            }
                            
                        }//fin iteracion delos empleados del cenco
                        
                        System.out.println(WEB_NAME+"[GestionFemase."
                            + "SetSaldoVBAPeriodosJob.execute]FIN Calculo VBA por periodos para Cenco: " + itCenco.getId()
                            + ", nombre: " + itCenco.getNombre()
                            + ". startDate: " + startDate
                            + ". endDate: " + endDate);
                    }else{
                        System.out.println(WEB_NAME+"[GestionFemase."
                            + "SetSaldoVBAPeriodosJob.execute]No hay empleados para Cenco: " + itCenco.getId()
                            +", nombre: " + itCenco.getNombre());
                    }    
                }
            }
        }//fin iteracion de departamentos de la empresa
        
        System.out.println(WEB_NAME+"[GestionFemase."
            + "SetSaldoVBAPeriodosJob.execute]"
            + "FIN - Seteo VBA para todos los periodos de c/empleado- " + new java.util.Date());
        ProcesosDAO daoProcesos=new ProcesosDAO(null);
        String result="Calculo de asistencia OK";
        ejecucion.setFechaHoraFinEjecucion(fechaHoraFormat.format(new Date()));
        ejecucion.setResultado(result);
        ejecucion.setUsuario(execUser);
        daoProcesos.insertItinerario(ejecucion);
       
    }
   
}
