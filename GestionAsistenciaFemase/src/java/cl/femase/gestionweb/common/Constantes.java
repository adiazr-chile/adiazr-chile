/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package cl.femase.gestionweb.common;

/**
*
* @author Alexander
*/
public class Constantes {
    
    public static final String MARCA_MODIFICADA = "M";
    public static final String MARCA_ELIMINADA = "E";
    public static final int MARCA_ENTRADA = 1;
    public static final int MARCA_SALIDA = 2;
    public static final int ESTADO_VIGENTE = 1;
    public static final int ESTADO_NO_VIGENTE = 2;
    
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
    
    public static int ID_AUSENCIA_VACACIONES        = 1;
    public static int ID_AUSENCIA_PERMISO_ADMINISTRATIVO = 4;
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
    public static final String ID_PARAMETRO_MAXIMO_ANUAL_DIAS_PA = "maximo_anual_dias_pa";
}
