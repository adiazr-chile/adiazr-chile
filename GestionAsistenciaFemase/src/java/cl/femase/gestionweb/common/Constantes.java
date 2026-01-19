/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package cl.femase.gestionweb.common;

import cl.femase.gestionweb.vo.ReportTypeVO;
import java.util.HashMap;
import java.util.Map;

/**
*
* @author Alexander
*/
public class Constantes {
    
    public static final String MARCA_MODIFICADA = "M";
    public static final String MARCA_ELIMINADA  = "E";
    public static final int MARCA_ENTRADA       = 1;
    public static final int MARCA_SALIDA        = 2;
    public static final int ESTADO_VIGENTE      = 1;
    public static final int ESTADO_NO_VIGENTE   = 2;
    
    /**
    * Id en tabla tipo_marca_manual
    */
    public static final int COD_MARCA_VIRTUAL = 4;
    
    public static final String CAMBIO_MARCA_ENTRADA_POR_SALIDA = "CAMBIO DE ENTRADA POR SALIDA";
    public static final String CAMBIO_MARCA_SALIDA_POR_ENTRADA = "CAMBIO DE SALIDA POR ENTRADA";
    public static final String CAMBIO_FECHA_MARCA = "FECHA MODIFICADA";
    public static final String CAMBIO_HORA_MARCA = "HORA MODIFICADA";
    public static int ID_PERFIL_SUPER_ADMIN         = 100;
    
    /** 
    * perfil id = 1
    */
    public static int ID_PERFIL_ADMIN               = 1;
    
    /** 
    * perfil id = 2
    */
    public static int ID_PERFIL_RRHH               = 2;
    
    /** 
    * perfil id = 3
    */
    public static int ID_PERFIL_OPERACIONES         = 3;
    
    /** 
    * perfil id = 4
    */
    public static int ID_PERFIL_FISCALIZADOR        = 4;
    
    /** 
    * perfil id = 7
    */
    public static int ID_PERFIL_EMPLEADO            = 7;
    /** 
    * perfil id = 5
    */
    public static int ID_PERFIL_DIRECTOR            = 5;
    
    /** 
    * perfil id = 6
    */
    public static int ID_PERFIL_DIRECTOR_TR         = 6;
    
    /** 
    * perfil id = 9
    */
    public static int ID_PERFIL_DIRECTOR_GENERAL    = 9;
    
    /** 
    * perfil id = 12
    */
    public static int ID_PERFIL_JEFE_TECNICO_NACIONAL = 12;
    
    /** 
    * perfil id = 14
    */
    public static int ID_PERFIL_CONTROL_INTERNO = 14;
    
    /**
    *   id ausencia= 1
    */
    public static int ID_AUSENCIA_VACACIONES        = 1;
    
    /**
    *   id ausencia= 4
    */
    public static int ID_AUSENCIA_PERMISO_ADMINISTRATIVO = 4;
    
    /**
    *   ID del Permiso Examen Salud Preventiva = 44
    */
    public static int ID_AUSENCIA_PERMISO_EXAMEN_SALUD_PREVENTIVA = 44;
    
    public static String FECHA_FIN_CONTRATO_INDEFINIDO = "3000-12-31";
    
    /**
    * Para tabla log_error
    */
    public static final String LOG_MODULO_ASISTENCIA = "Asistencia";
    public static final String LOG_MODULO_MARCAS = "Registros Asistencia";
    public static final String LOG_MODULO_AUTENTICACION = "Autenticacion";
    
    public static final String LOG_EVENTO_AUTENTICACION = "Login";
    public static final String LOG_EVENTO_CALCULO_ASISTENCIA = "Calcular Asistencia";
    public static final String LOG_EVENTO_CONSULTA_ASISTENCIA = "Consulta Asistencia";
    public static final String LOG_EVENTO_CONSULTA_MARCAS = "Consulta Registros de Asistencia";
    public static final String LOG_EVENTO_REPORTE_ASISTENCIA = "Genera Reporte de Asistencia";
    
    public static final String GOOGLE_MAPS_PREFIJO = "Https://www.google.cl/maps/search/";
    
    public static final String ESTADO_SOLICITUD_PENDIENTE = "P";
    public static final String ESTADO_SOLICITUD_PENDIENTE_LABEL = "Pendiente";
    public static final String ESTADO_SOLICITUD_CANCELADA = "C";
    public static final String ESTADO_SOLICITUD_CANCELADA_LABEL = "Cancelada";
    public static final String ESTADO_SOLICITUD_APROBADA = "A";
    public static final String ESTADO_SOLICITUD_APROBADA_LABEL = "Aprobada";
    public static final String ESTADO_SOLICITUD_RECHAZADA = "R";
    public static final String ESTADO_SOLICITUD_RECHAZADA_LABEL = "Rechazada";
    
    //Permisos administrativos
    public static final String JORNADA_PERMISO_ADMINISTRATIVO_TODO_EL_DIA= "TODO_EL_DIA";
    public static final String JORNADA_PERMISO_ADMINISTRATIVO_AM= "AM";
    public static final String JORNADA_PERMISO_ADMINISTRATIVO_PM= "PM";
        
    
    /**
    * 22
    */
    public static int ID_CARGO_DIRECTOR = 22;
    
    /**
    * 69
    */
    public static int ID_CARGO_JEFE_TECNICO_NACIONAL = 69;
    
    /**
    * "No hay info de num cotizaciones."
    */
    public static final String MENSAJE_1 = "No hay info de num cotizaciones.";
    
    /**
    * "No hay datos para el reporte"
    */
    public static final String REPORTE_SIN_DATOS = "No hay datos para el reporte";
    
    /**
    * "No hay informacion"
    */
    public static final String NO_HAY_INFORMACION = "No hay informacion";
    
    /**
    * "Sin datos"
    */
    public static final String SIN_DATOS = "Sin datos";
    
    /**
    * ""No hay datos para el reporte""
    */
    public static final String NO_HAY_DATOS_REPORTE = "No hay datos para el reporte";
    
    /**
    * "No hay informacion de asistencia"
    */
    public static final String NO_HAY_INFO_ASISTENCIA = "No hay informacion de asistencia";
        
    /**
    *  "No hay informacion de exceso de jornada" 
    */
    public static final String NO_HAY_INFO_EXCESO_JORNADA = "No hay informacion de exceso de jornada";
    
    /**
    *  "No hay informacion de turnos asignados" 
    */
    public static final String NO_HAY_INFO_TURNOS_ASIGNADOS = "No hay informacion de turnos asignados";
    
    /**
    *  "No hay informacion de turnos" 
    */
    public static final String NO_HAY_INFO_TURNOS = "No hay informacion de turnos";
    
    /**
    * Id parametro indica el numero maximo de dias de permiso administrativo anuales
    */
//    public static final String ID_PARAMETRO_MAXIMO_ANUAL_DIAS_PA = "maximo_anual_dias_pa";
    
    /**
    * Id parametro indica el numero maximo de dias de permiso administrativo para un semestre
    */
    public static final String ID_PARAMETRO_MAXIMO_SEMESTRAL_DIAS_PA = "max_dias_pa_semestre";
    
    /**
    * Id parametro Flag que indica si se debe validar saldo al solicitar vacaciones
    */
    public static final String FLG_VAC_SALDO = "FLG_VAC_SALDO";
    
    
    /**
    * Id parametro indica el numero maximo de dias de permiso examen salud preventiva
    */
    public static final String ID_PARAMETRO_MAXIMO_ANUAL_DIAS_PESP = "maximo_anual_pesp";

    
    
    public static String fnSET_FECHA_BASE_VP  = "set_fecha_base_vacacion_progresiva";
    
    /**
    * Db Function: 'set_saldo_vacaciones_basicas_anuales_empleado'
    * 
    *  Cálculo de vacaciones básicas anuales por empleado.
    *  Esta función realiza el cálculo de VBA para un empleado vigente, 
    *   a partir de la fecha de inicio de contrato 
    *   o de la nueva fecha de inicio de contrato si es que cuenta con continuidad laboral 
    */
    public static String fnSET_VBA_EMPLEADO   = "set_saldo_vacaciones_basicas_anuales_empleado";
    
    public static String fnSET_SALDO_PERMISO_ADMINISTRATIVO = "set_saldo_permiso_administrativo";
    
    //new
    public static String fnSET_VBA_EMPLEADOS   = "set_vba_empleados";
    
    /**
    *   Db Function: 'set_saldo_vacaciones_basicas_anuales' 
    * 
    * Cálculo de vacaciones básicas anuales por CENCO.
    * Esta función realiza el cálculo de VBA para un CENTRO DE COSTO, 
    * obtiene a todos los empleados vigentes y 
    * a partir de la fecha de inicio de contrato 
    * o de la nueva fecha de inicio de contrato, 
    * si es que cuenta con continuidad laboral.
    */
    public static String fnSET_VBA_CENCO      = "set_saldo_vacaciones_basicas_anuales";
    
    /**
    *   Db Function: 'set_saldo_vacaciones_progresivas_empleado' 
    * 
    * Cálculo de vacaciones progresivas por empleado
    * Esta función realiza el cálculo de saldo de VP para un empleado, 
    * basándose en la fecha base VP y con la ultima fecha de inicio de vacación.
    */
    //public static String fnSET_VP_EMPLEADO    = "set_saldo_vacaciones_progresivas_empleado";
    public static String fnSET_VP_EMPLEADO    = "calcular_dias_vacacion_progresiva";
        
    /**
    *   Db Function: 'set_saldo_vacaciones_progresivas' 
    * 
    * Cálculo de vacaciones progresivas por CENCO
    * Esta función realiza el cálculo de saldo de VP para un CENTRO DE COSTO, 
    * basándose en las fecha base VP 
    * y con la ultima fecha de inicio de vacación de todos los empleados del CENCO.
    */
    public static String fnSET_VP_CENCO       = "set_saldo_vacaciones_progresivas";
    
    public static Map<Boolean, String> BOOLEAN_LABEL;
    static {
        BOOLEAN_LABEL = new HashMap<>();
        BOOLEAN_LABEL.put(true, "Si");
        BOOLEAN_LABEL.put(false, "No");
    }
    
    /**
    * 
    */
    public static Map<Integer, String> ESTADO_LABEL;
    static {
        ESTADO_LABEL = new HashMap<>();
        ESTADO_LABEL.put(1, "Vigente");
        ESTADO_LABEL.put(2, "No Vigente");
    }
    
    /**
    * 
    */
    public static Map<Integer, String> HASH_TIPO_MARCAS;
    static {
        HASH_TIPO_MARCAS = new HashMap<>();
        HASH_TIPO_MARCAS.put(1, "Entrada");
        HASH_TIPO_MARCAS.put(2, "Salida");
        HASH_TIPO_MARCAS.put(3, "Inicio colacion");
        HASH_TIPO_MARCAS.put(4, "Termino colacion");

        HASH_TIPO_MARCAS.put(100, "Falta marca entrada");
        HASH_TIPO_MARCAS.put(200, "Falta marca salida");
        HASH_TIPO_MARCAS.put(300, "Ausencia");
        HASH_TIPO_MARCAS.put(400, "Faltan ambas marcas");
        HASH_TIPO_MARCAS.put(500, "Libre");
        HASH_TIPO_MARCAS.put(600, "Feriado");
        HASH_TIPO_MARCAS.put(700, "Hay Marca");
        
    }
    
    /**
    * 
    */
    public static Map<String, String> DURACION_TURNOS_ROTATIVOS;
    static {
        DURACION_TURNOS_ROTATIVOS = new HashMap<>();
        DURACION_TURNOS_ROTATIVOS.put("1M", "1 mes");
        DURACION_TURNOS_ROTATIVOS.put("3M", "3 meses");
        DURACION_TURNOS_ROTATIVOS.put("6M", "6 meses");
        DURACION_TURNOS_ROTATIVOS.put("1", "1 A&ntilde;o");
        DURACION_TURNOS_ROTATIVOS.put("2", "2 A&ntilde;os");
        DURACION_TURNOS_ROTATIVOS.put("3", "3 A&ntilde;os");
        DURACION_TURNOS_ROTATIVOS.put("4", "4 A&ntilde;os");
        DURACION_TURNOS_ROTATIVOS.put("5", "5 A&ntilde;os");
        
    }
    
    /**
    * 
    */
    public static Map<String, ReportTypeVO> REPORT_TYPE;
    static {
        
        REPORT_TYPE = new HashMap<>();
        REPORT_TYPE.put("asistencia", new ReportTypeVO("fiscaliza_rpt_asistencia.jasper", "fiscaliza_asistencia"));
        REPORT_TYPE.put("jornada_diaria", new ReportTypeVO("fiscaliza_rpt_jornada_diaria.jasper", "fiscaliza_jornada"));
        REPORT_TYPE.put("domingos_festivos", new ReportTypeVO("fiscaliza_rpt_domingos_festivos.jasper", "domingos_festivos"));
        REPORT_TYPE.put("modificaciones_turnos", new ReportTypeVO("fiscaliza_rpt_modificaciones_turnos.jasper", "modificaciones_turnos"));
        REPORT_TYPE.put("diario", new ReportTypeVO("fiscaliza_rpt_diario.jasper", "diario"));
        REPORT_TYPE.put("incidentes_tecnicos", new ReportTypeVO("fiscaliza_rpt_incidentes_tecnicos.jasper", "incidentes_tecnicos"));
        
        
    }
}
