/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.common;

import cl.femase.gestionweb.vo.DetalleAusenciaVO;
import cl.femase.gestionweb.vo.DiferenciaEntreFechasVO;
import cl.femase.gestionweb.vo.DiferenciaHorasVO;
import cl.femase.gestionweb.vo.MarcaVO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.validator.UrlValidator;
import org.joda.time.Days;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Alexander
 */
public class Utilidades {
    
    public static String WEB_NAME = "[GestionFemaseWeb]";
    
    private static final SimpleDateFormat horaformat = new SimpleDateFormat("HH:mm:ss");
        
    private static final SimpleDateFormat inputFmt = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
    private static final SimpleDateFormat outputFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
    static Calendar today=Calendar.getInstance();
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd H:m:s");
//    static int intYear = today.get(Calendar.YEAR);
//    static int intMonth = today.get(Calendar.MONTH)+1;
//    static int intDay = today.get(Calendar.DATE);
    
    /**
    * 
    * @param anio
    * @param semestre
    * @return 
    */
    public static Map<String, LocalDate> obtenerFechasSemestre(int anio, int semestre) {
        Map<String, LocalDate> fechas = new HashMap<>();
        if (semestre == 1) {
            fechas.put("inicio", LocalDate.of(anio, 1, 1));
            fechas.put("fin", LocalDate.of(anio, 6, 30));
        } else if (semestre == 2) {
            fechas.put("inicio", LocalDate.of(anio, 7, 1));
            fechas.put("fin", LocalDate.of(anio, 12, 31));
        } else {
            throw new IllegalArgumentException("El semestre debe ser 1 o 2");
        }
        return fechas;
    }
    
    
    /**
    * 
    * @return 
    */
    public static Map<String, String> getPeriodosFuturos() {
        LocalDate hoy = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Map<String, String> periodos = new HashMap<>();
        periodos.put("semana", hoy.plusWeeks(1).format(fmt));
        periodos.put("quincena", hoy.plusDays(15).format(fmt));
        periodos.put("mes", hoy.plusMonths(1).format(fmt));
        periodos.put("anio", hoy.plusYears(1).format(fmt));

        return periodos;
    }
    
    public static Map<String, String> getPeriodos() {
        LocalDate hoy = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Map<String, String> periodos = new HashMap<>();
        periodos.put("semana", hoy.minusWeeks(1).format(fmt));
        periodos.put("quincena", hoy.minusDays(15).format(fmt));
        periodos.put("mes", hoy.minusMonths(1).format(fmt));
        periodos.put("anio", hoy.minusYears(1).format(fmt));

        return periodos;
    }
    
    public static String getUsername(String input) {
        if (input.length() > 12) {
            String truncado = input.substring(0, 9);
            int cifraAleatoria = new Random().nextInt(900) + 100; // Genera número entre 100 y 999
            return truncado + cifraAleatoria;
        }
        return input;
    }
    
    // Utilidad para evitar NPE y trims
    public static String trimOrNull(String value) {
        return value == null ? null : value.trim();
    }
    
    /**
     * Comprime varios archivos en un único archivo ZIP.
     * @param archivosPaths Lista con rutas completas de los archivos a comprimir.
     * @param rutaZipSalida Ruta completa donde se guardará el archivo ZIP generado.
     * @throws IOException Si ocurre un error de lectura/escritura.
     */
    public static void comprimirArchivos(ArrayList<String> archivosPaths, 
            String rutaZipSalida) throws IOException {
        byte[] buffer = new byte[4096];

        try (FileOutputStream fos = new FileOutputStream(rutaZipSalida);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            for (String archivoPath : archivosPaths) {
                File archivo = new File(archivoPath);
                if (!archivo.exists() || !archivo.isFile()) {
                    System.out.println("Archivo no encontrado o no es un archivo válido: " + archivoPath);
                    continue; // O lanzar excepción si prefieres
                }

                try (FileInputStream fis = new FileInputStream(archivo)) {
                    // El nombre dentro del ZIP será solo el nombre del archivo, sin ruta
                    ZipEntry zipEntry = new ZipEntry(archivo.getName());
                    zos.putNextEntry(zipEntry);

                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }

                    zos.closeEntry();
                }
            }
        }
    }
    
    /**
    * Recibe todas las marcas de un rut y retorna una lista con un par unico de entrada y salida.
    * La entrada mas antigua y la salida mas reciente.
    * 
    * @param marcaciones
    * @return 
    */
    public static LinkedHashMap<String, MarcaVO> procesarMarcaciones(LinkedHashMap<String, MarcaVO> marcaciones) {
        LinkedHashMap<String, MarcaVO> resultado = new LinkedHashMap<>();
        LinkedHashMap<String, ArrayList<MarcaVO>> grupos = new LinkedHashMap<>();
        
        // 1. Agrupar por RUT
        for (MarcaVO marca : marcaciones.values()) {
            grupos.computeIfAbsent(marca.getRutEmpleado(), k -> new ArrayList<>()).add(marca);
        }

        // 2. Procesar cada grupo
        for (Map.Entry<String, ArrayList<MarcaVO>> grupo : grupos.entrySet()) {
            String rut = grupo.getKey();
            ArrayList<MarcaVO> marcas = grupo.getValue();
            
            MarcaVO entrada = null;
            MarcaVO salida = null;

            // Buscar entrada más antigua
            for (MarcaVO marca : marcas) {
                if (marca.getTipoMarca()== 1) {
                    entrada = marca;
                    break; // Primera entrada encontrada
                }
            }

            // Buscar salida más reciente
            for (int i = marcas.size() - 1; i >= 0; i--) {
                MarcaVO marca = marcas.get(i);
                if (marca.getTipoMarca()== 2) {
                    salida = marca;
                    break; // Última salida encontrada
                }
            }

            // Agregar al resultado con claves diferenciadas
            if (entrada != null) {
                resultado.put(rut + "-ENTRADA", entrada);
            }
            if (salida != null) {
                resultado.put(rut + "-SALIDA", salida);
            }
        }
        
        return resultado;
    }
    
    /**
    * 
    * @param fechaInicio
    * @param fechaFin
    * @param fechaActual
    * @return 
    */
    public static boolean esPeriodoEnCurso(LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaActual) {
        return (fechaActual.isEqual(fechaInicio) || fechaActual.isAfter(fechaInicio)) &&
               (fechaActual.isEqual(fechaFin) || fechaActual.isBefore(fechaFin));
    }
    
    public static JSONObject generateErrorMessage(String message, Exception e)
    {
        JSONObject error_message = new JSONObject();
        try{
            /*
            * Save full stack trace in object.
            */
            String stackTrace = ExceptionUtils.getStackTrace(e);
            String exception = e.toString();
            error_message.put("time", System.currentTimeMillis());
            error_message.put("hostname", InetAddress.getLocalHost().getHostName());
            error_message.put("message", message);
            error_message.put("exception", exception);
            error_message.put("stack", stackTrace);
        } catch (UnknownHostException|JSONException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        return error_message;
    }
    
    /**
    * 
    * @param _fecha
    * @return 
    */
    public static int getSemestre(Date _fecha){
        int semestre =1;
        //SimpleDateFormat sdfFull    = new SimpleDateFormat("yyyy-MM-dd", new Locale("es","CL"));
        try {
            Calendar cal5 = Calendar.getInstance(new Locale("es","CL"));
            cal5.setTime(_fecha);
            
            int month = cal5.get(Calendar.MONTH)+1;
            if (month > 6) semestre = 2;
            System.out.println(WEB_NAME + "[Utilidades.getSemestre]"
                + "Fecha Date = " + _fecha 
                + ", Semestre = " + semestre);
           
        } catch (Exception ex) {
            System.err.println(WEB_NAME 
                + "[Utilidades.getSemestre]Error al obtener semestre: " + ex.toString());
        }
        
        return semestre;
    }
    
    /**
    * 
    * @param _numero
    * @return 
    */
    public static String formatDouble(double _numero){
        NumberFormat nf = NumberFormat.getInstance(new Locale("es", "CL"));
        String val = nf.format(_numero);
        //System.out.println(WEB_NAME+"numero: " + registros+", con formato: " + val);
        
        return val;
    }
    
    /**
    * 
    * @param _ausencias
    * @return 
    */
    public static String getAusenciasConflictoStr(ArrayList<DetalleAusenciaVO> _ausencias){
        String retornar="";
        
        for (DetalleAusenciaVO ausencia: _ausencias) {
            retornar += ausencia.getNombreAusencia() 
                + ". Desde: " + ausencia.getFechaInicioAsStr() + " " + ausencia.getHoraInicioFullAsStr()
                + ". Hasta: " + ausencia.getFechaFinAsStr() + " " + ausencia.getHoraFinFullAsStr();    
        }
        
        return retornar;
    }
    
    /**
    * 
    * @param _string
    * @return 
    */
    public static String encodeDecodeString(String _string){
        
        byte[] bytesEncoded = Base64.getEncoder().encode(_string.getBytes());
        String encodedStr = (new String(bytesEncoded,Charset.forName("UTF-8")));
        byte[] valueDecoded = Base64.getDecoder().decode(encodedStr);
        String decodedStr = (new String(valueDecoded,Charset.forName("UTF-8")));
        
        return decodedStr;
    }
    
    /**
    * 
    * @param _fechaDesde
    * @param _fechaHasta
    * @return 
    */
    public static DiferenciaEntreFechasVO getDiferenciaEntreFechas(Date _fechaDesde, 
            Date _fechaHasta){
        DiferenciaEntreFechasVO resultado = new DiferenciaEntreFechasVO();
        Calendar calendarDesde = Calendar.getInstance();
        Calendar calendarHasta = Calendar.getInstance();
        calendarDesde.setTime(_fechaDesde);
        calendarHasta.setTime(_fechaHasta);
        LocalDateTime oldDate = 
            LocalDateTime.of(calendarDesde.get(Calendar.YEAR), 
                calendarDesde.get(Calendar.MONTH) + 1, 
                calendarDesde.get(Calendar.DATE), 0, 0, 0);
        LocalDateTime newDate = 
            LocalDateTime.of(calendarHasta.get(Calendar.YEAR), 
                calendarHasta.get(Calendar.MONTH) + 1, 
                calendarHasta.get(Calendar.DATE), 0, 0, 0);
        System.out.println(WEB_NAME+"[Utilidades.getDiferenciaEntreFechas]"
            + "fecha desde: " + oldDate 
            + ", fecha hasta: " + newDate);  
      
        // count between dates
        long years = ChronoUnit.YEARS.between(oldDate, newDate);
        long years2 = ChronoUnit.YEARS.between(newDate, oldDate);
        long months = ChronoUnit.MONTHS.between(oldDate, newDate);
        long weeks = ChronoUnit.WEEKS.between(oldDate, newDate);
        long days = ChronoUnit.DAYS.between(oldDate, newDate);
        long hours = ChronoUnit.HOURS.between(oldDate, newDate);
        long minutes = ChronoUnit.MINUTES.between(oldDate, newDate);
        long seconds = ChronoUnit.SECONDS.between(oldDate, newDate);
        long milis = ChronoUnit.MILLIS.between(oldDate, newDate);
        long nano = ChronoUnit.NANOS.between(oldDate, newDate);

        resultado.setYears(years2);
        resultado.setMonths(months);
        resultado.setWeeks(weeks);
        resultado.setDays(days);
        resultado.setHours(hours);
        resultado.setMinutes(minutes);
        resultado.setSeconds(seconds);
        resultado.setMilis(milis);
        resultado.setNano(nano);
        
        return resultado;
    }
    
    /**
    * Retorna una cadena de valores ordenados.
    * Recibe una cadena de valores separados por un caracter
    * y retorna una cadena con los valores ordenados y separados con
    * el mismo separador.
    * 
    * 
    * @param _cadenaValores
    * @param _separador
    * @return 
    */
    public static String getValoresOrdenados(String _cadenaValores, String _separador){
        String cadenaOrdenada = "";
        ArrayList<String> lstFechas=new ArrayList();
        StringTokenizer tokenfechas=new StringTokenizer(_cadenaValores,",");
        while(tokenfechas.hasMoreTokens()){
            lstFechas.add(tokenfechas.nextToken());
        }
        
        Collections.sort(lstFechas);
        for(int x=0;x<lstFechas.size();x++) {
            cadenaOrdenada += lstFechas.get(x)+",";
        }
        
        return cadenaOrdenada.substring(0, cadenaOrdenada.length() - 1);
    }
    
    /**
     * Permite sumar una lista de horas.
     * Las horas a sumar deben estar en formato HH:mm
     * @param _listaHoras
     * @return 
     */
    public static String sumTimesList(ArrayList<String> _listaHoras) {
        int hours = 0, minutes = 0, seconds = 0;
        for (String string : _listaHoras) {
            if (string != null){
                System.out.println("[Utilidades.sumTimesList]item: " + string);
                String temp[] = string.split(":");
                if (temp[0].compareTo("") !=0 
                        && temp[1].compareTo("") != 0){
                    hours = hours + Integer.valueOf(temp[0]);
                    minutes = minutes + Integer.valueOf(temp[1]);
                    seconds = seconds + 0;
                }
            }
        }
        if (seconds == 60) {
            minutes = minutes + 1;
            seconds = 0;
        } else if (seconds > 59) {
            minutes = minutes + (seconds / 60);
            seconds = seconds % 60;
        }
        if (minutes == 60) {
            hours = hours + 1;
            minutes = 0;
        } else if (minutes > 59) {
            hours = hours + (minutes / 60);
            minutes = minutes % 60;
        }
        //System.out.println(hours + ":" + minutes + ":" + seconds);
        String auxHoras = ""+hours;
        String auxMinutos = ""+minutes;
        if (hours < 10) auxHoras = "0" + hours;
        if (minutes < 10) auxMinutos = "0" + minutes;
        
        System.out.println("[Utilidades.sumTimesList]resultado suma: " + auxHoras + ":" + auxMinutos);
        return auxHoras + ":" + auxMinutos;
        
    }
    
    
//    /***
//     * Retorna la diferencia entre dos horas (incluye la fecha).
//     * Ambas horas deben estar en formato completo: yyyy-MM-dd HH:mm:ss
//     * La diferencia se retorna en formato completo: yyyy-MM-dd HH:mm:ss
//     * 
//     * @param _fechaHoraInicio
//     * @param _fechaHoraFin
//     * @return 
//     */
//    public static String restaHoras(String _fechaHoraInicio, String _fechaHoraFin){
////        System.out.println(WEB_NAME+"[Utilidades.restaHoras]"
////            + "_horaInicio= "+_horaInicio
////            + ", _horaFin= "+_horaFin);
//        String difHrsMins = "";
//        Date fechaInicial=null;
//        try {
//            fechaInicial = dateFormat.parse(_fechaHoraInicio);
//        } catch (ParseException ex) {
//            System.err.println("Error al parsear hora1: "+ex.toString());
//        }
//        Date fechaFinal=null;
//        try {
//            fechaFinal = dateFormat.parse(_fechaHoraFin);
//        } catch (ParseException ex) {
//            System.err.println("Error al parsear hora2: "+ex.toString());
//        }
//        
//        int diferencia = -1;
//        
//        diferencia=(int) ((fechaFinal.getTime()-fechaInicial.getTime())/1000);
//        if (diferencia < 0 ) diferencia *= -1;
//        
////        System.out.println(WEB_NAME+"[Utilidades.restaHoras]diferencia= "+diferencia);
//        
//        int dias=0;
//        int horas=0;
//        int minutos=0;
//        if(diferencia>86400) {
//            dias=(int)Math.floor(diferencia/86400);
//            diferencia=diferencia-(dias*86400);
//        }
//        if(diferencia>3600) {
//            horas=(int)Math.floor(diferencia/3600);
//            diferencia=diferencia-(horas*3600);
//        }
//        if(diferencia>=60 && diferencia < 3600) {
//            minutos=(int)Math.floor(diferencia/60);
////            System.out.println(WEB_NAME+"[Utilidades.restaHoras]minutos= "+minutos);
//            diferencia=diferencia-(minutos*60);
//        }
//        String strhh = ""+horas;
//        String strmm = ""+minutos;
//        if (horas < 10) strhh = "0" + horas;
//        if (minutos < 10) strmm = "0" + minutos;
//        difHrsMins = strhh + ":" + strmm;
//       
////        System.out.println(WEB_NAME+"[Utilidades.restaHoras]difHrsMins= "+difHrsMins);
//        return difHrsMins;
//    }
    
//    /***
//     * Retorna la diferencia entre dos horas.
//     * Y retorna el resultado en minutos (entero)
//     * 
//     * Ambas horas deben estar en formato completo: yyyy-MM-dd HH:mm:ss
//     * La diferencia se retorna en formato completo: yyyy-MM-dd HH:mm:ss
//     * 
//     * @param _fechaHoraInicio
//     * @param _fechaHoraFin
//     * @return 
//     */
//    public static int restaHorasEnMinutos(String _fechaHoraInicio, String _fechaHoraFin){
//        int diferencia = -1;
//        Date fechaInicial=null;
//        try {
//            fechaInicial = dateFormat.parse(_fechaHoraInicio);
//        } catch (ParseException ex) {
//            System.err.println("Error al parsear hora1: "+ex.toString());
//        }
//        Date fechaFinal=null;
//        try {
//            fechaFinal = dateFormat.parse(_fechaHoraFin);
//        } catch (ParseException ex) {
//            System.err.println("Error al parsear hora2: "+ex.toString());
//        }
//        
//        diferencia=(int) ((fechaFinal.getTime()-fechaInicial.getTime())/1000);
//        if (diferencia < 0 ) diferencia *= -1;
//    
//        return (diferencia/60);
//    }
    
    /**
    * 
    * @param time1
    * @param time2
    * @return 
    */
    public static String sumaHoras(String time1, String time2) {
        PeriodFormatter formatter = new PeriodFormatterBuilder()
            .minimumPrintedDigits(2)
            .printZeroAlways()
            .appendHours()
            .appendLiteral(":")
            .appendMinutes()
            .toFormatter();

        Period period1 = formatter.parsePeriod(time1);
        Period period2 = formatter.parsePeriod(time2);
        return formatter.print(period1.plus(period2));
    }
    
    public static String getHoraMinutos(int _totalMinutos){
        String horaMins = "";
        
        int mHours = _totalMinutos / 60; //since both are ints, you get an int
        int mMinutes = _totalMinutos % 60;
        String strHours = "" + mHours;
        String strMins = "" + mMinutes;
        
        if (mHours < 10) strHours = "0" + mHours;
        if (mMinutes < 10) strMins = "0" + mMinutes;
        
        horaMins = strHours +":" + strMins;
        
        return horaMins;
    }
    
    /**
     * Formatea una fecha hora en formato yyyyMMdd HH:mm:ss
     * y retorna fechaHora en formato yyyy-MM-dd HH:mm:ss
     * @param _fechaHora
     * @return 
     */
    public static String getFechaHora(String _fechaHora){
        String outputFmtDate="";
        try{
            Date origDate = inputFmt.parse(_fechaHora);
            outputFmtDate = outputFmt.format(origDate);
            
        }catch(ParseException pex){
            System.err.println("[Utilidades.getFechaHora]Error al parsear horas: "+pex.toString());
        }
        return outputFmtDate;
    }
    
    public static boolean isWSDLAvailable(String _wsdlAddr) {
        
        String[] schemes = {"http","https"}; // DEFAULT schemes = "http", "https", "ftp"
         UrlValidator urlValidator = new UrlValidator(schemes);
         return urlValidator.isValid(_wsdlAddr);
         
//        HttpURLConnection c = null;
//        try {
//            URL u = new URL(wsdlAddr);
//            c = (HttpURLConnection) u.openConnection();
//            c.setRequestMethod("HEAD");
//            c.getInputStream();
//            return c.getResponseCode() == 200;
//        } catch (Exception e) {
//            return false;
//        } finally {
//            if (c != null) c.disconnect();
//        }    
    }
    
    /**
     *
     * @param cadena
     * @param busqueda
     * @param reemplazo
     * @return
     */
    public static String reemplazarString(String cadena, String busqueda, String reemplazo) {
        return cadena.replaceAll(busqueda, reemplazo);
    }
    
    /**
     * Permite restar dos fechas (java.util.Date) 
     * y entrega la respectiva diferencia en dias.
     * 
     * @param _startDate
     * @param _endDate
     * @return 
     */
    public static long getDifferenceInDays(Date _startDate, 
        Date _endDate){
               
        System.out.println(WEB_NAME+"[Urilidades.getDifferenceInDays]"
            + "startDate: "+_startDate+", endDate: "+_endDate);       
        
        // Crear 2 instancias de Calendar para restarlas
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        // Establecer las fechas
        cal1.set(getDatePart(_startDate, "yyyy"), getDatePart(_startDate, "MM"), getDatePart(_startDate, "dd"),0,0,0);
        cal2.set(getDatePart(_endDate, "yyyy"), getDatePart(_endDate, "MM"), getDatePart(_endDate, "dd"),0,0,0);
        
        System.out.println(WEB_NAME+"[Urilidades.getDifferenceInDays]"
            + "cal.getTime: "+cal1.getTime()+", cal2.getTime: "+cal2.getTime());       
        
        // conseguir la representacion de la fecha en milisegundos
	long milis1 = cal1.getTimeInMillis();
        long milis2 = cal2.getTimeInMillis();
        // calcular la diferencia en milisengundos
        long diff = milis2 - milis1;
        // calcular la diferencia en segundos
        long diffSeconds = diff / 1000;

        // calcular la diferencia en minutos
        //long diffMinutes = diff / (60 * 1000);

        // calcular la diferencia en horas
        //long diffHours = diff / (60 * 60 * 1000);

        // calcular la diferencia en dias
        long diffDays = diff / (24 * 60 * 60 * 1000);

        //System.out.println(WEB_NAME+"En milisegundos: " + diff + " milisegundos.");
        //System.out.println(WEB_NAME+"En segundos: " + diffSeconds + " segundos.");
    
        return diffDays;
        
        
    }
    
    /**
     * Permite restar dos horas y entrega la respectiva diferencia en segundos.
     * startTime y endTime: en formato HH:mm:ss
     */
    public static long getDifferenceInSeconds(String _startTime, String _endTime){
        //hora 1
        Date dteHora1 = null;//1
        try {
            dteHora1 = horaformat.parse(_startTime);//1
        }catch(ParseException pex){
            System.err.println("Error al parsear hora2: "+pex.toString());
        }
        //hora 2
        Date dteHora2 = null;
        try{
            dteHora2 = horaformat.parse(_endTime);//2
        }catch(ParseException pex){
            System.err.println("Error al parsear hora2: "+pex.toString());
        }
        
        // Crear 2 instancias de Calendar para restarlas
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        // Establecer las fechas
        cal1.set(0,0,0,getDatePart(dteHora1, "HH"), getDatePart(dteHora1, "mm"), getDatePart(dteHora1, "ss"));
        cal2.set(0,0,0,getDatePart(dteHora2, "HH"), getDatePart(dteHora2, "mm"), getDatePart(dteHora2, "ss"));

        // conseguir la representacion de la fecha en milisegundos
	long milis1 = cal1.getTimeInMillis();
        long milis2 = cal2.getTimeInMillis();
        // calcular la diferencia en milisengundos
        long diff = milis2 - milis1;
        // calcular la diferencia en segundos
        long diffSeconds = diff / 1000;

        // calcular la diferencia en minutos
        //long diffMinutes = diff / (60 * 1000);

        // calcular la diferencia en horas
        //long diffHours = diff / (60 * 60 * 1000);

        // calcular la diferencia en dias
//        long diffDays = diff / (24 * 60 * 60 * 1000);

        //System.out.println(WEB_NAME+"En milisegundos: " + diff + " milisegundos.");
        //System.out.println(WEB_NAME+"En segundos: " + diffSeconds + " segundos.");
    
        return diffSeconds;
    }
    
    public static double roundHALF_UP(double d, int decimalPlace){
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace,BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }
    
    /**
    * Retorna solo la parte solicitada de una fecha.
    * params: date, fecha en formato yyyyMMdd
    * formato: parte de la fecha requerida. dd: dia, MM: mes, yyyy: year
    * @param _date
    * @param _formato
    * @return 
    */
    public static int getDatePart(Date _date, String _formato){
        if (null == _date){
            return 0;
        }else{
            SimpleDateFormat dateFormat = new SimpleDateFormat(_formato);
            return Integer.parseInt(dateFormat.format(_date));
        }
    }
    
    /**
    * Retorna fecha en el formato yyyy-MM-dd.
    * 
    * @param _fecha en formato dd-MM-yyyy
    * @return 
    */
    public static String getFechaYYYYmmdd(String _fecha){
        StringTokenizer tokenFecha = new StringTokenizer(_fecha, "-");
        String ddFecha = tokenFecha.nextToken();
        String mmFecha = tokenFecha.nextToken();
        String yyyyFecha = tokenFecha.nextToken();
        
        return yyyyFecha + "-" + mmFecha + "-" + ddFecha;
    }
    
    /**
    * Retorna solo la parte solicitada de una fecha.
    * params: date, fecha en formato yyyyMMdd
    * formato: parte de la fecha requerida. dd: dia, MM: mes, yyyy: year
    * @param _date
    * @param _formato
    * @return 
    */
    public static String getDatePartAsString(Date _date, String _formato){
        if (null == _date){
            return "";
        }else{
            SimpleDateFormat dateFormat = new SimpleDateFormat(_formato);
            return dateFormat.format(_date);
        }
    }
    
    /**
    * Retorna un timestamp con la fecha especificada.
    * 
    * @param _date, _formato
    * @param _formato, si es null se toma valor yyyy-MM-dd HH:mm:ss 
    * 
    * @return 
    */
    public static Timestamp getTimestamp(String _date, String _formato){
        Timestamp tsreturn = null;
        if (_date==null){
            return null;
        }else{
            try {
                if (_formato == null) _formato = "yyyy-MM-dd HH:mm:ss";
                SimpleDateFormat dateFormat = new SimpleDateFormat(_formato);
                tsreturn = new Timestamp(dateFormat.parse(_date).getTime());
            } catch (ParseException ex) {
                System.err.println("Error al parsear "
                    + "fecha a timestamp: "+ex.toString());
            }
            
        }
        
        return tsreturn;
    }
    
    /**
    * Retorna un timestamp con la fecha especificada.
    * 
    * @param _date, _formato
    * @param _formato, si es null se toma valor yyyy-MM-dd HH:mm:ss 
    * 
    * @return 
    */
    public static java.sql.Date getSqlDate(String _date, 
            String _formato){
        java.sql.Date dateReturn = null;
        if (_date == null){
            return null;
        }else{
            try {
                if (_formato == null) _formato = "yyyy-MM-dd";
                SimpleDateFormat dateFormat = new SimpleDateFormat(_formato);
                dateReturn = new java.sql.Date(dateFormat.parse(_date).getTime());
            } catch (ParseException ex) {
                System.err.println("[Utilidades.getSqlDate]Error al parsear "
                    + "fecha a java.sql.Date: " + ex.toString());
            }
            
        }
        
        return dateReturn;
    }
    
    //
    
    /**
     * Retorna valor minimo
     * de una lista de numeros (doubles)
     */
    public static double getMinValue(double[] _lista){
        double min = _lista[0];
        /**
         * Si el precio menor inicial es cero, se debe buscar el 
         * siguiente precio menor mayor a cero
         */
        if (min == 0){
            for (int y=0; y<_lista.length; y++) {
                if (_lista[y]>0) {
                    min = _lista[y];
                    break;
                }
            }
        }
        
//        System.out.println(WEB_NAME+"[getMinValue]precio menor inicial=" +min);
        for (int i=0; i<_lista.length; i++) {
            if (_lista[i] > 0 && _lista[i] < min) {
                min = _lista[i];
            }
//            System.out.println(WEB_NAME+"[getMinValue]lista[i]=" +_lista[i]+", min= "+min);
        }
        
        return min;
    }
    
    /**
     * Retorna valor maximo
     * de una lista de numeros (doubles)
     * @param _lista
     */
    public static double getMaxValue(double[] _lista){
        double max = _lista[0];
        for (int i=0; i<_lista.length; i++) {
            if (_lista[i]>max) {
                max = _lista[i];
            }
        }
        return max;
    }
    
    /**
     * Retorna valor promedio
     * de una lista de numeros (doubles)
     */
    public static double getMedValue(double[] _lista){
        double toReturn = 0.0;
        double prom = 0.0;
        for (int i=0; i<_lista.length; i++) {
            
            prom = prom + _lista[i];
        }
        toReturn=prom/_lista.length;
        
        return toReturn;
    }
    
    
    /**
     * Retorna objeto Date para una fecha en string, 
     * aplicando el formato especificado
     * @param _strDate
     * @param _formato
     * @return 
     */
    public static Date getDateFromString(String _strDate,String _formato){
        Date asDate = null;
        SimpleDateFormat dateformat = new SimpleDateFormat(_formato);
        try {
            asDate = dateformat.parse(_strDate);
        } catch (ParseException ex) {
            System.err.println("Error al parsear fecha "+_strDate+
                    ". Error es:" +ex.toString());
        }
        return asDate;
    }
    
    /**
     * Retorna objeto java.sql.Date para una fecha en string, 
     * aplicando el formato especificado
     * @param _strDate
     * @param _formato
     * @return 
     */
    public static java.sql.Date getJavaSqlDate(String _strDate,String _formato){
        java.util.Date utilDate = null;
        java.sql.Date sqlDate = null;
        SimpleDateFormat dateformat = new SimpleDateFormat(_formato);
        if (_strDate!=null && _strDate.compareTo("") != 0){    
            try {
                utilDate = dateformat.parse(_strDate);
                sqlDate = new java.sql.Date(utilDate.getTime());
            } catch (ParseException ex) {
                System.err.println("[Utilidades.getJavaSqlDate]"
                    + "Error al parsear fecha "+_strDate+
                    ". Error es:" +ex.toString());
            }catch (NullPointerException npex) {
                System.err.println("[Utilidades.getJavaSqlDate]"
                    + "Error al parsear fecha nula:" + npex.toString());
                sqlDate=null;
            }
        }
        return sqlDate;
    }
    
    /**
     * Retorna solo la hora de una fecha recibida en formato (yyyy-MM-dd HH:mm:ss)
     * La hora la retorna en formato HH:mm:ss
     * 
     */
    public String getHour(String _dateFull){
        String timeParsed   = "";
        Date theTime=null;
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        System.err.println("[Utilidades.getHour]hora entrante: " + _dateFull);
        try{
            theTime = sdf2.parse(_dateFull);
        }catch(ParseException pex){
            //System.err.println("[Utilidades.getHour]Error 1: " + pex.getMessage());
            sdf2 = new SimpleDateFormat("HH:mm:ss");
            try{
                theTime = sdf2.parse(_dateFull);
            }catch(ParseException pex2){
               // System.err.println("[Utilidades.getHour]Error 2: " + pex2.getMessage());
            }
        }
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(theTime);
            String strHH="";
            String strmi="";
            String strss="";
            strHH=String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
            strmi=String.valueOf(calendar.get(Calendar.MINUTE));
            strss=String.valueOf(calendar.get(Calendar.SECOND));
            
            if (calendar.get(Calendar.HOUR_OF_DAY)<10){
                strHH = "0" + String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
            }
            if (calendar.get(Calendar.MINUTE)<10){
                strmi = "0" + String.valueOf(calendar.get(Calendar.MINUTE));
            }
            if (calendar.get(Calendar.SECOND)<10){
                strss = "0" + String.valueOf(calendar.get(Calendar.SECOND));
            }
            timeParsed = strHH + ":" + strmi + ":" + strss;
            
        
        return timeParsed;

    }

    public String getHourWithoutSeparator(String _dateFull){
        String timeParsed   = "";
        Date theTime=null;
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try{
            theTime = sdf2.parse(_dateFull);
        }catch(ParseException pex){
            //System.err.println("[Utilidades.getHourWithoutSeparator]Error: " + pex.getMessage());
            sdf2 = new SimpleDateFormat("HH:mm:ss");
            try{
                theTime = sdf2.parse(_dateFull);
            }catch(ParseException pex2){}
        }  
            
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(theTime);
        String strHH="";
        String strmi="";
        String strss="";
        strHH=String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        strmi=String.valueOf(calendar.get(Calendar.MINUTE));
        strss=String.valueOf(calendar.get(Calendar.SECOND));

        if (calendar.get(Calendar.HOUR_OF_DAY)<10){
            strHH = "0" + String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        }
        if (calendar.get(Calendar.MINUTE)<10){
            strmi = "0" + String.valueOf(calendar.get(Calendar.MINUTE));
        }
        if (calendar.get(Calendar.SECOND)<10){
            strss = "0" + String.valueOf(calendar.get(Calendar.SECOND));
        }
        timeParsed = strHH + strmi + strss;

       
        return timeParsed;

    }
    
    public static String alinearConRelleno(String datos, 
            String alineacion, 
            String caracterRelleno, 
            String largoCampo){
        int largo = datos.length();
        int largoCampoComoEntero = Integer.parseInt(largoCampo);
        //System.out.println(WEB_NAME+"Alineacion.alinearConRelleno: largoCampoComoEntero  : " + largoCampoComoEntero);
        int relleno = largoCampoComoEntero - largo;
        String salida = datos;
        if (relleno > 0) {
                //System.out.println(WEB_NAME+"Alineacion.alinearConRelleno: relleno > 0 : " + relleno);
                if (alineacion.toUpperCase().equals("DERECHA")) {
                        for (int j=0; j < relleno; j++) salida = caracterRelleno + salida;
                } else if (alineacion.toUpperCase().equals("IZQUIERDA")) {
                        for (int i=0; i<relleno ; i++) salida += caracterRelleno;
                }

        } else {
                //System.out.println(WEB_NAME+"Alineacion.alinearConRelleno: relleno < 0: " + relleno);
                //Si el string no cabe en el espacio asignado, se corta por el final
                salida = salida.substring(0, largoCampoComoEntero);
        }

        return salida;
    }
    
    /**
    * Permite escribir lineas en un archivo de texto.
    * @param _pathFile
    * @param _content
    * 
    */
    public static void writeLinesInTextFile(String _pathFile, String _content){
        File target = new File(_pathFile);

        FileWriter writer = null;
        try {
            writer = new FileWriter(target, false);
            writer.write(_content);
        } catch (IOException ex) {
            System.err.println("Error al escribir lineas en archivo"+ex.toString());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    System.err.println("Error al cerrar archivo"+ex.toString());
                }
            }
        }
    }
    
    
    public static ArrayList<String> getClassNamesFromPackage(String packageName) throws IOException{
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL packageURL;
        ArrayList<String> names = new ArrayList<String>();;

        packageName = packageName.replace(".", "/");
        packageURL = classLoader.getResource(packageName);

        if(packageURL.getProtocol().equals("jar")){
            String jarFileName;
            JarFile jf ;
            Enumeration<JarEntry> jarEntries;
            String entryName;
    
            // build jar file name, then loop through zipped entries
            jarFileName = URLDecoder.decode(packageURL.getFile(), "UTF-8");
            jarFileName = jarFileName.substring(5,jarFileName.indexOf("!"));
            System.out.println(WEB_NAME+">jarFileName: " + jarFileName);
            jf = new JarFile(jarFileName);
            jarEntries = jf.entries();
            while(jarEntries.hasMoreElements()){
                entryName = jarEntries.nextElement().getName();
                if(entryName.startsWith(packageName) && entryName.length()>packageName.length()+5){
                    try{
                        entryName = entryName.substring(packageName.length(),entryName.lastIndexOf('.'));
                    }catch(StringIndexOutOfBoundsException siox){
                        entryName = entryName.substring(packageName.length(),entryName.lastIndexOf('/'));
                    }
                    names.add(entryName);
                }
            }
    
        // loop through files in classpath
        }else{
            URI uri=null;
            try {
                uri = new URI(packageURL.toString());
            } catch (URISyntaxException ex) {
                System.err.println("Error al leer ruta de clases: " + ex.toString());
            }

            if (uri != null){
                File folder = new File(uri.getPath());
                // won't work with path which contains blank (%20)
                // File folder = new File(packageURL.getFile()); 
                File[] contenuti = folder.listFiles();
                String entryName;
                for(File actual: contenuti){
                    entryName = actual.getName();
                    try{
                        entryName = entryName.substring(0, entryName.lastIndexOf('.'));
                        names.add(entryName);
                    }catch(StringIndexOutOfBoundsException siex){
                        System.err.println(entryName + " es un subpackage: " + siex.toString());
                    }

                }
            }
        }
        return names;
    }
    
    /**
    * 
    * @param _horaHHMM
    * @param _minutosASumar
    * @return 
    */
    public static String sumarMinsHora(String _horaHHMM, int _minutosASumar){
        //******************* restar minutos a una hora
        System.out.println("[sumarMinsHora](new)hora: " + _horaHHMM
            + ", minutosSumar: "+_minutosASumar);
        SimpleDateFormat fechaFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat horaFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat fechahoraFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar mycal = Calendar.getInstance();
        Date fechaActual = mycal.getTime();
        Date fechaAux = getDateFromString(fechaFormat.format(fechaActual)+ " " + _horaHHMM, "yyyy-MM-dd HH:mm");
        
        // Obtiene fecha y hora actuales
        Calendar fecha = Calendar.getInstance();
        fecha.setTime(fechaAux);
        //System.out.println("La fecha actual es: " +fechahoraFormat.format(fechaAux));
        
        // Restar minutos a la fecha
        fecha.add(Calendar.MINUTE, _minutosASumar);
        
        String result = horaFormat.format(fecha.getTimeInMillis());
        //System.out.println("La fecha actual nueva es: " + result);
        //System.out.println("[Utilidades.sumarMinsHora](new)resultado: " + result);
        
        return result;
    }
    
    /**
    * 
    * @param _year
    * @param _month
    * @param _day
    * @return 
    */
    public static int getDiaSemana(int _year, int _month, int _day){
        int intday = -1;
        LocalDate date=LocalDate.of(_year, _month, _day);
        intday = date.getDayOfWeek().getValue();
        
        return intday;
    }
 
    public static String getFormattedLocalDateTime(String _fechaHora, String _formato){
        LocalDateTime localDateTime = LocalDateTime.parse(_fechaHora,
            DateTimeFormatter.ofPattern(_formato));
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern(_formato);
        String formatDateTime = localDateTime.format(formatter1);

        return formatDateTime;
    }
    
    public static LocalDate getLocalDate(String _fecha, String _formatofecha){
        Locale localeCl = new Locale("es", "CL");
        Calendar calendarDate = Calendar.getInstance(localeCl);
        SimpleDateFormat dateformat = new SimpleDateFormat(_formatofecha);
        Date fechaAsDate  = null;
        try {
            fechaAsDate   = dateformat.parse(_fecha);
            calendarDate.setTime(fechaAsDate);
        } catch (ParseException ex) {
            System.err.println("[Utilidades.getLocalDate]"
                + "Error al parsear fecha: " + ex.toString());
        }

        LocalDate date = LocalDate.of(calendarDate.get(Calendar.YEAR), 
            calendarDate.get(Calendar.MONTH) + 1 ,
            calendarDate.get(Calendar.DATE) );
        
        return date;
    }
    
    /**
     * Retorna localDate en base a la fecha entregada en formato yyyy-MM-dd
     * 
     * @param _fecha
     * @return 
     */
    public static LocalDate getLocalDate(String _fecha){
        Locale localeCl = new Locale("es", "CL");
        Calendar calendarDate = Calendar.getInstance(localeCl);
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaAsDate  = null;
        try {
            fechaAsDate   = dateformat.parse(_fecha);
            calendarDate.setTime(fechaAsDate);
        } catch (ParseException ex) {
            System.err.println("[Utilidades.getLocalDate]"
                + "Error al parsear fecha: " + ex.toString());
        }

        LocalDate date = LocalDate.of(calendarDate.get(Calendar.YEAR), 
            calendarDate.get(Calendar.MONTH) + 1 ,
            calendarDate.get(Calendar.DATE) );
        
        return date;
    }
    
    /**
    * Retorna un objeto Time.
    * 
    */
    public static java.sql.Time getHora(String _hora){
        java.sql.Time theTime = null;
        
        SimpleDateFormat horaformat = new SimpleDateFormat("HH:mm:ss");
        Date asDate = null;
        try{
            asDate = horaformat.parse(_hora);
            theTime = new java.sql.Time(asDate.getTime());
        }catch(ParseException pex){
            System.err.println("[DetalleTurnosSrv.getHora]"
                + "Error al parsear hora entrada: "+pex.getMessage());
        }
        
        return theTime;
    }
    
    /**
     * Compara dos horas
     * 
     * Si input1 MAYOR input2 : resultado es menor que cero
     * Si input1 MENOR input2 : resultado es mayor que cero
     * 
     * @param _input1
     * @param _input2
     * @return 
     */
    public static long comparePeriods(String _input1,String _input2){
        LocalTime one = LocalTime.parse( _input1 );
        LocalTime two = LocalTime.parse( _input2 );
        
        long result = ChronoUnit.MINUTES.between(one, two);
        return result;
    }
    
//    /**
//     * Obtiene total de horas transcurridas en un rango de horas
//     * @param _strHraIngreso
//     * @param _strHraSalida
//     * @return 
//     */
//    public static int getTotalMinutos(String _strHraIngreso, String _strHraSalida){
//        int totalHoras=0;
//        LocalTime entrada = null;
//        LocalTime salida = null;   
//        boolean seguir=true;
//        try {
//           entrada = LocalTime.parse(_strHraIngreso);
//           salida = LocalTime.parse(_strHraSalida);
//           // otra lógica
//        } catch(DateTimeParseException e) {
//            System.err.println("la hora de entrada "
//                + "o salida es invalida:"+e.getMessage());
//            seguir = false;
//        }
//        int minutes = 0;
//        if (seguir){
//            minutes = (int) ChronoUnit.MINUTES.between(entrada, salida);
//        }
//        return minutes;
//    }
    
    /**
    * Retorna un arreglo con las fechas comprendidas
    * entre startDate y endDate (ambas fechas incluidas)
    * 
    * @param _startDate en format
    * @param _endDate
    * @return String[]
    */
    public static String[] getFechas(String _startDate, String _endDate){
        SimpleDateFormat sdfyyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
        //SimpleDateFormat sdfddMMyyyy = new SimpleDateFormat("dd-MM-yyyy");
        String fDesde=_startDate;
        String fHasta=_endDate;
        System.out.println(WEB_NAME+"[Utilidades.getFechas]"
            + "fDesde: " + fDesde +  ", fHasta: " + fHasta);        
        Date defaultDate = new Date();
        String[] dates = null;
        try{
            if (_startDate != null && _startDate.compareTo("") == 0){
                try{
                    _startDate = sdfyyyyMMdd.format(defaultDate);
                    _endDate = _startDate;
                }catch(Exception ex1){
                    System.err.println("[Utilidades.getFechas]"
                        + "Error_1 al parsear fecha inicio: " + _startDate);
                }
            }
            org.joda.time.format.DateTimeFormatter dateTimeFormat_ddMMyyyy = DateTimeFormat.forPattern("dd-MM-yyyy")
                .withLocale(new Locale("et", "EE"));
            org.joda.time.format.DateTimeFormatter dateTimeFormat_yyyyMMdd = DateTimeFormat.forPattern("yyyy-MM-dd")
                .withLocale(new Locale("et", "EE"));
            
            org.joda.time.LocalDate dateTimeDesde = null;
            try{
                dateTimeDesde = dateTimeFormat_yyyyMMdd.parseLocalDate(fDesde);
            }catch(IllegalArgumentException ilex){
                dateTimeDesde = dateTimeFormat_ddMMyyyy.parseLocalDate(fDesde);
           }
            org.joda.time.LocalDate dateTimeHasta = null;
            try{
                dateTimeHasta = dateTimeFormat_yyyyMMdd.parseLocalDate(fHasta);
            }catch(IllegalArgumentException ilex){
                dateTimeHasta = dateTimeFormat_ddMMyyyy.parseLocalDate(fHasta);
            }
            System.out.println(WEB_NAME+"[gestionweb.Utilidades.getFechas]"
                + "startDate: " + dateTimeDesde
                + ", endDate: " + dateTimeHasta);

            /*
            * Calcular los días que hay entre dos fechas.
            */
            int dias = Days.daysBetween( dateTimeDesde, dateTimeHasta.plusDays(1)).getDays();
            dates = new String[dias];
            int x = 0;
            for(org.joda.time.LocalDate currentdate = dateTimeDesde; 
                    currentdate.isBefore(dateTimeHasta) || currentdate.isEqual(dateTimeHasta); 
                    currentdate= currentdate.plusDays(1)){
                        //System.out.println(currentdate);
                        dates[x] = currentdate.toString();
                        x++;
            }
            
        }catch(Exception excepcion){
            System.out.println(WEB_NAME+"[gestionweb."
                + "Utilidades.getFechas]"
                + "Error: "+excepcion.toString());
            excepcion.printStackTrace();
        }
        return dates;
    }
    
////    public static String[] getFechas(String _startDate, String _endDate){
////        SimpleDateFormat sdfyyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
////        SimpleDateFormat sdfddMMyyyy = new SimpleDateFormat("dd-MM-yyyy");
////        String fDesde=_startDate;
////        String fHasta=_endDate;
////        System.out.println(WEB_NAME+"[Utilidades.getFechas]"
////            + "fDesde: " + fDesde +  ", fHasta: " + fHasta);        
////        Date defaultDate = new Date();
////        String[] dates = null;
////        try{
////            if (_startDate != null && _startDate.compareTo("") == 0){
////                try{
////                    _startDate = sdfyyyyMMdd.format(defaultDate);
////                    _endDate = _startDate;
////                }catch(Exception ex1){
////                    System.err.println("[Utilidades.getFechas]"
////                        + "Error_1 al parsear fecha inicio: " + _startDate);
////                    
////                }
////            }
////            DateTime date1 = null;
////            try{
////                date1 = new DateTime(_startDate);
////            }catch(IllegalArgumentException ilex){
////                Date aux1 = sdfddMMyyyy.parse(fDesde);
////                date1 = new DateTime(aux1.getTime());
////                _startDate = date1.getYear() 
////                    + "-" + date1.getMonthOfYear() 
////                    + "-" + date1.getDayOfMonth();
////                System.err.println("[Utilidades.getFechas]"
////                    + "Error_A. new startDate: " + _startDate);
////                
////            }
////            
////            DateTime date2 = null;
////            try{
////                date2 = new DateTime(_endDate);
////            }catch(IllegalArgumentException ilex){
////                Date aux2 = sdfddMMyyyy.parse(fHasta);
////                date2 = new DateTime(aux2.getTime());
////                _endDate = date2.getYear() 
////                    + "-" + date2.getMonthOfYear() 
////                    + "-" + date2.getDayOfMonth();
////                System.err.println("[Utilidades.getFechas]"
////                    + "Error_B. new endDate: " + _endDate);
////            }
////            
////            //DateTime date2 = new DateTime(_endDate);
////            //date2.plusDays(1);
////
////            System.out.println(WEB_NAME+"[gestionweb.Utilidades.getFechas]"
////                + "startDate: " + _startDate
////                + ", endDate: " + _endDate);
////
////            /*
////            * Calcular los días que hay entre dos fechas.
////            */
////            int dias = Days.daysBetween(date1, date2.plusDays(1)).getDays();  
////
////            //System.out.println(WEB_NAME+"dias entre las fechas: " + Days.daysBetween(date1, date2.plusDays(1)).getDays());  
////            dates = new String[dias];
////            int x = 0;
////            StringTokenizer toketdate = new StringTokenizer(_startDate, "-");
////            Calendar start = Calendar.getInstance();
////            start.set(Integer.parseInt(toketdate.nextToken()), 
////                Integer.parseInt(toketdate.nextToken())-1, 
////                Integer.parseInt(toketdate.nextToken()));
////            
////            toketdate = new StringTokenizer(_endDate, "-");
////            Calendar end = Calendar.getInstance();
////            end.set(Integer.parseInt(toketdate.nextToken()), 
////                Integer.parseInt(toketdate.nextToken())-1, 
////                Integer.parseInt(toketdate.nextToken()));
////            //end.add(Calendar.DATE, 1);
////            for (Calendar date = start; (date.before(end) || date.equals(end)); date.add(Calendar.DATE, 1))
////            {
////                System.err.println("[gestionweb.Utilidades."
////                    + "getFechas]Itera fecha_1(parsear a yyyy-MM-dd): " + date.getTime());
////                String strdate = sdfyyyyMMdd.format(date.getTime());
////                System.err.println("[gestionweb.Utilidades."
////                    + "getFechas]Itera fecha_2: " + strdate);
////                try{
////                    dates[x] = strdate;
////                }catch(ArrayIndexOutOfBoundsException aiu){
////                    System.err.println("[gestionweb.Utilidades."
////                        + "getFechas]"
////                        + "Error: "+aiu.toString());
////                    //aiu.printStackTrace();
////                    strdate = sdfddMMyyyy.format(date.getTime());
////                    dates[x] = strdate;
////                }
////                x++;
////            }
////        }catch(Exception excepcion){
////            System.out.println(WEB_NAME+"[gestionweb."
////                + "Utilidades.getFechas]"
////                + "Error: "+excepcion.toString());
////            excepcion.printStackTrace();
////        }
////        return dates;
////    }
    
    
    //Utilidades.comparaHoras("08:07", "08:30");-->  1
    //Utilidades.comparaHoras("08:30", "08:07");--> -1
    
    /**
     * Compara dos fechas con hora, en formato
     *  yyyy-MM-dd HH:mm:ss
     * 
     *  Si _horaReal MAYOR _horaTeorica return 1;
     *  Si _horaReal MENOR _horaTeorica return -1;
     *  Si _horaReal = _horaTeorica return 0;
     * 
     * @param _horaTeorica
     * @param _horaReal
     * @return 
     */
    public static int comparaHoras(String _horaTeorica, String _horaReal){
        SimpleDateFormat horaformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int diff = 0;
        try{
            Date date1 = horaformat.parse(_horaReal);
            Date date2 = horaformat.parse(_horaTeorica);
            diff = date1.compareTo(date2);
            System.out.println(WEB_NAME+"HoraTeorica: "+date1
                +", hora2: "+date2+", diff: "+diff);
            
        }catch(ParseException pex){
            System.err.println("[Utilidades.comparaHoras]Error al parsear horas: "+pex.toString());
        }        
        
        return diff;
    }
    
    /**
    * 
    * @param fecha
    * @param sumaresta: si es > 0: suma, en caso contrario: resta
    * @param opcion: {DAYS, MONTHS, YEARS}
    * @return 
    */
    public static Date sumaRestarFecha(Date fecha, int sumaresta, String opcion){
        java.time.LocalDate date = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        //Con Java9
        //LocalDate date = LocalDate.ofInstant(input.toInstant(), ZoneId.systemDefault());
        TemporalUnit unidadTemporal=null;
        switch(opcion){
            case "DAYS":
                unidadTemporal = ChronoUnit.DAYS;
                break;
            case "MONTHS":
                unidadTemporal = ChronoUnit.MONTHS;
                break;
            case "YEARS":
                unidadTemporal = ChronoUnit.YEARS;
                break;
            default:
                //Controlar error
        }
        java.time.LocalDate dateResultado = null;
//        if (sumaresta < 0){
//            dateResultado = date.minus(sumaresta, unidadTemporal);
//        }else{
            dateResultado = date.plus(sumaresta, unidadTemporal);
//        }
        return Date.from(dateResultado.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    
     /**
     *  Resta dos fechas-hora y retorna un objeto con la resta de ambas horas. 
     *  Retorna un objeto con la diferencia en tres formatos:
     * 
     *  diferencia en horas(int), diferencia en minutos(int) y diferencia como string en formato HH:mm
     * 
     * @param _fechaHoraInicio en formato completo, incluye fecha: yyyy-MM-dd HH:mm:ss
     * @param _fechaHoraFin en formato completo, incluye fecha: yyyy-MM-dd HH:mm:ss
     * @return 
     */
//     public static DiferenciaHorasVO restaHoras(String _fechaHoraInicio, String _fechaHoraFin){
//        DiferenciaHorasVO resultado = new DiferenciaHorasVO();
//        
//        String difHrsMins = "";
//        Date fechaInicial=null;
//        try {
//            fechaInicial = dateFormat.parse(_fechaHoraInicio);
//        } catch (ParseException ex) {
//            System.err.println("[Utilidades.restaHoras]Error al parsear hora1: "+ex.toString());
//        }
//        Date fechaFinal=null;
//        try {
//            fechaFinal = dateFormat.parse(_fechaHoraFin);
//        } catch (ParseException ex) {
//            System.err.println("Error al parsear hora2: "+ex.toString());
//        }
//        
//        int diferencia = -1;
//        
//        diferencia=(int) ((fechaFinal.getTime()-fechaInicial.getTime())/1000);
//        if (diferencia < 0 ) diferencia *= -1;
//        
//        int dias=0;
//        int horas=0;
//        int minutos=0;
////        System.out.println(WEB_NAME+"[restaHoras]"
////            + "diferencia= " + diferencia);
//        if(diferencia > 86400) {
//            dias=(int)Math.floor(diferencia/86400);
//            diferencia=diferencia-(dias*86400);
//        }
//        if(diferencia>=3600) {
//            horas=(int)Math.floor(diferencia/3600);
//            diferencia=diferencia-(horas*3600);
//        }
//        if(diferencia>=60 && diferencia < 3600) {
//            minutos=(int)Math.floor(diferencia/60);
////            System.out.println(WEB_NAME+"[Utilidades.restaHoras]minutos= "+minutos);
//            diferencia=diferencia-(minutos*60);
//        }
//        String strhh = ""+horas;
//        String strmm = ""+minutos;
//        if (horas < 10) strhh = "0" + horas;
//        if (minutos < 10) strmm = "0" + minutos;
//        difHrsMins = strhh + ":" + strmm;
//       
//        resultado.setStrDiferenciaHorasMinutos(difHrsMins);
//        resultado.setIntDiferenciaHoras(Integer.parseInt(strhh));
//        resultado.setIntDiferenciaMinutos(Integer.parseInt(strmm));
////        System.out.println(WEB_NAME+"[Utilidades.restaHoras]difHrsMins= "+difHrsMins);
//        return resultado;
//     }
     
     
     /**
     *  Resta dos fechas-hora y retorna un objeto con la resta de ambas horas. 
     *  Retorna un objeto con la diferencia en tres formatos:
     * 
     *  diferencia en horas(int), diferencia en minutos(int) y diferencia como string en formato HH:mm
     * 
     *  1 minute = 60 seconds
     *  1 hour = 60 x 60 = 3600
     *  1 day = 3600 x 24 = 86400
     * 
     * @param _fechaHoraInicio en formato completo, incluye fecha: yyyy-MM-dd HH:mm:ss
     * @param _fechaHoraFin en formato completo, incluye fecha: yyyy-MM-dd HH:mm:ss
     * @return 
     */
    public static DiferenciaHorasVO getTimeDifference(String _fechaHoraInicio, String _fechaHoraFin){
//        System.out.println(WEB_NAME+"[Utilidades.getTimeDifference]"
//            + "fechaHoraInicio: " + _fechaHoraInicio
//            + ", fechaHoraFin: " + _fechaHoraFin);
        SimpleDateFormat fechaFormat = new SimpleDateFormat("yyyy-MM-dd");
        String soloFechaInicio  = "";
        String soloFechaFin     = "";
        DiferenciaHorasVO resultado = new DiferenciaHorasVO();
        Date fechaInicial=null;
        try {
            fechaInicial = dateFormat.parse(_fechaHoraInicio);
        } catch (ParseException ex) {
            System.err.println("Error al parsear hora1: "+ex.toString());
        }
        Date fechaFinal=null;
        try {
            fechaFinal = dateFormat.parse(_fechaHoraFin);
        } catch (ParseException ex) {
            System.err.println("Error al parsear hora2: "+ex.toString());
        }
    
        soloFechaInicio = fechaFormat.format(fechaInicial);
        soloFechaFin = fechaFormat.format(fechaFinal);
        
        /**
         * Validar resta de horas de turno nocturno, por ejemplo: 23hrs a 7am (dia sgte)
         */
        Calendar finalCalendar = Calendar.getInstance(new Locale("es", "CL"));
        finalCalendar.setTime(fechaFinal);
        
            if (fechaInicial!=null && (soloFechaInicio.compareTo(soloFechaFin)!=0 && fechaInicial.after(fechaFinal))){
                //sumar un dia a la fecha de salida(final)
//                System.out.println(WEB_NAME+"[Utilidades.getTimeDifference]Fechas distintas (turno de noche). "
//                    + "Sumar un dia a la fecha de salida(final)");
                Date newDate = sumaRestarFecha(fechaFinal, 1, "DAYS");
                Calendar newCal = Calendar.getInstance(new Locale("es", "CL"));
                newCal.setTime(newDate);
                newCal.set(Calendar.HOUR, finalCalendar.get(Calendar.HOUR));
                newCal.set(Calendar.MINUTE, finalCalendar.get(Calendar.MINUTE));
                newCal.set(Calendar.SECOND, finalCalendar.get(Calendar.SECOND));
                fechaFinal = newCal.getTime();
                resultado.setHoraInicialMayor(true);
            }
        
        //milliseconds
        long different = 0;
        if (fechaInicial != null && fechaFinal != null){
            different = fechaInicial.getTime() - fechaFinal.getTime();
            if (different < 0) different *= -1;
        }
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        String strhh = "" + elapsedHours;
        String strmm = "" + elapsedMinutes;
        String strss = "" + elapsedSeconds;
        if (elapsedHours < 10) strhh = "0" + elapsedHours;
        if (elapsedMinutes < 10) strmm = "0" + elapsedMinutes;
        if (elapsedSeconds < 10) strss = "0" + elapsedSeconds;
        resultado.setStrDiferenciaHorasMinutos(strhh + ":" + strmm);
        resultado.setStrDiferenciaHorasMinutosSegundos(strhh + ":" + strmm + ":" + strss);
        resultado.setIntDiferenciaHoras(Integer.parseInt(strhh));
        resultado.setIntDiferenciaMinutos(Integer.parseInt(strmm));
        resultado.setIntDiferenciaSegundos(Integer.parseInt(strss));
        
//        System.out.println(WEB_NAME+"[Utilidades.getTimeDifference]"
//            + "Diferencia hh:mm:ss: "+ (strhh + ":" + strmm + ":" + strss) );
        
        return resultado;
    }
////    public static DiferenciaHorasVO restaHoras(String _fechaHoraInicio, String _fechaHoraFin){
////        DiferenciaHorasVO resultado = new DiferenciaHorasVO();
////        
////        String difHrsMins = "";
////        Date fechaInicial=null;
////        try {
////            fechaInicial = dateFormat.parse(_fechaHoraInicio);
////        } catch (ParseException ex) {
////            System.err.println("Error al parsear hora1: "+ex.toString());
////        }
////        Date fechaFinal=null;
////        try {
////            fechaFinal = dateFormat.parse(_fechaHoraFin);
////        } catch (ParseException ex) {
////            System.err.println("Error al parsear hora2: "+ex.toString());
////        }
////        
////        int diferencia = -1;
////        
////        diferencia=(int) ((fechaFinal.getTime()-fechaInicial.getTime())/1000);
////        if (diferencia < 0 ) diferencia *= -1;
////        
////        int dias=0;
////        int horas=0;
////        int minutos=0;
////        if(diferencia>86400) {
////            dias=(int)Math.floor(diferencia/86400);
////            diferencia=diferencia-(dias*86400);
////        }
////        if(diferencia>3600) {
////            horas=(int)Math.floor(diferencia/3600);
////            diferencia=diferencia-(horas*3600);
////        }
////        if(diferencia>=60 && diferencia < 3600) {
////            minutos=(int)Math.floor(diferencia/60);
//////            System.out.println(WEB_NAME+"[Utilidades.restaHoras]minutos= "+minutos);
////            diferencia=diferencia-(minutos*60);
////        }
////        String strhh = ""+horas;
////        String strmm = ""+minutos;
////        if (horas < 10) strhh = "0" + horas;
////        if (minutos < 10) strmm = "0" + minutos;
////        difHrsMins = strhh + ":" + strmm;
////       
////        resultado.setStrDiferenciaHorasMinutos(difHrsMins);
////        resultado.setIntDiferenciaHoras(Integer.parseInt(strhh));
////        resultado.setIntDiferenciaMinutos(Integer.parseInt(strmm));
//////        System.out.println(WEB_NAME+"[Utilidades.restaHoras]difHrsMins= "+difHrsMins);
////        return resultado;
////    }
    
    public static boolean validarRut(String rut) {
 
        boolean validacion = false;
        try {
            rut =  rut.toUpperCase();
            rut = rut.replace(".", "");
            rut = rut.replace("-", "");
            int rutAux = Integer.parseInt(rut.substring(0, rut.length() - 1));
            char dv = rut.charAt(rut.length() - 1);
            int m = 0, s = 1;
            for (; rutAux != 0; rutAux /= 10) {
                s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;
            }
            if (dv == (char) (s != 0 ? s + 47 : 75)) {
                validacion = true;
            }

        } catch (java.lang.NumberFormatException e) {
        } catch (Exception e) {
        }
        return validacion;
    }
    
    public static java.util.Date restaDias(int _diasRestar){
    
        Locale localeCl_1 = new Locale("es", "CL");
        Calendar mycal_1  = Calendar.getInstance(localeCl_1);
        Calendar newCal_1 = Calendar.getInstance(localeCl_1);
        newCal_1.setTime(mycal_1.getTime());

        java.util.Date etimeAsDate1 = mycal_1.getTime();
        // restar 7 dias
        newCal_1.add(Calendar.DATE, - _diasRestar);            
//        System.out.println(WEB_NAME+"[Utilidades.restaDias]"
//            + "Fecha actual: " + etimeAsDate1
//            +" ,Fecha antes: " + newCal_1.getTime());
    
        return newCal_1.getTime();
    }
    
    /**
     * 
     * @param _hora1
     * @param _hora2
     * @return 
     */
    public static int restaMinutos(String _hora1, String _hora2){
        String hora1 = _hora1;
        String hora2 = _hora2;

        String[] h1 = hora1.split(":");
        String[] h2 = hora2.split(":");
        
        int diffMinutos = (Integer.parseInt(h2[1]) - Integer.parseInt(h1[1]));
        System.out.println(WEB_NAME+"[restarMinutos]minutosDiferencia= "+diffMinutos);
        
        return diffMinutos;
    }
    
    /**
    * Resta minutos a una hora. Hora en formato HH:mm
    * 
    * @param _horaHHMM
    * @param _minutosARestar
    * @return 
    */
    public static String restarMinsHora(String _horaHHMM, int _minutosARestar){
        //******************* restar minutos a una hora
//        System.out.println(WEB_NAME+"[Utilidades.restarMinsHora](new)hora: " + _horaHHMM
//            + ", minutosRestar: "+_minutosARestar);
        SimpleDateFormat fechaFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat horaFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat fechahoraFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar mycal = Calendar.getInstance();
        Date fechaActual = mycal.getTime();
        Date fechaAux = getDateFromString(fechaFormat.format(fechaActual)+ " " + _horaHHMM, "yyyy-MM-dd HH:mm");
        
        // Obtiene fecha y hora actuales
        Calendar fecha = Calendar.getInstance();
        fecha.setTime(fechaAux);
        System.out.println(WEB_NAME+"La fecha actual es: " +fechahoraFormat.format(fechaAux));
        
        // Restar minutos a la fecha
        fecha.add(Calendar.MINUTE, (_minutosARestar * -1));
        
        String result = horaFormat.format(fecha.getTimeInMillis());
        System.out.println(WEB_NAME+"La fecha actual nueva es: " + result);
        System.out.println(WEB_NAME+"[Utilidades.restarMinsHora](new)resultado: " + result);
        
        return result;
    }
    
    /**
    * 
     * @param password
     * @return 
    */
    public static String getMD5EncryptedValue(String password) {
        final byte[] defaultBytes = password.getBytes();
        try {
            final MessageDigest md5MsgDigest = MessageDigest.getInstance("MD5");
            md5MsgDigest.reset();
            md5MsgDigest.update(defaultBytes);
            final byte messageDigest[] = md5MsgDigest.digest();
            final StringBuffer hexString = new StringBuffer();
            for (final byte element : messageDigest) {
                final String hex = Integer.toHexString(0xFF & element);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            password = hexString + "";
        } catch (final NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
        }
        return password;
    }
    
    public static List<LocalDate> getFechas(LocalDate _start, LocalDate _stop){
        LocalDate ld = _start ;
        List<LocalDate> dates = new ArrayList<>() ;
        while ( ld.isBefore( _stop ) || ld.isEqual( _stop )) {  // Using "isBefore" for Half-Open approach.
            dates.add( ld ); // Collect this date.
            ld = ld.plusDays( 1 ) ;  // Setup the next loop.
        }
        
        return dates;
    }
    
    /**
    * 
    */
    private static List<Interval> splitDateTime(long start, long end, int intervalNo) {
        long interval = (end - start) / intervalNo;
        List<Interval> list = new ArrayList<>();
        for (long i = start + interval; i < end; i += interval) {
            list.add(new Interval(start, i));
            start=start + interval;
        }
        list.add(new Interval(start, end));
        return list;
    }
    
    
    /**
    * Retorna intervalos de tiempo (en horas) 
    * Sirve para dividir la jornada en partes iguales
    * 
    * @param _horaInicioTurno
    * @param _horaFinTurno
    * @param _numIntervalos
    * @return 
    */
    public static HashMap<Integer,IntervaloVO> getIntervalos(String _horaInicioTurno, 
        String _horaFinTurno, int _numIntervalos, int _minutosColacion){
        
        HashMap<Integer, IntervaloVO> hashIntervalos = new HashMap<>();
        SimpleDateFormat sdfHora    = new SimpleDateFormat("HH:mm:ss", new Locale("es","CL"));
        
        try{
            
            Date dteDesde = sdfHora.parse(_horaInicioTurno);
            Date dteHasta = sdfHora.parse(_horaFinTurno);
            
            List<Interval> listIntervals = splitDateTime(dteDesde.getTime(), dteHasta.getTime(), _numIntervalos);
            
            // Creating an instance of ListIterator
            int iteracion = 1;
            ListIterator<Interval> iterate = listIntervals.listIterator();
            System.out.println(WEB_NAME+"[getIntervalos]Iterando intervalos segun jornada...");
            while(iterate.hasNext()) {
                Interval intervalo = iterate.next();
                Date start = intervalo.getStart().toDate();
                Date end = intervalo.getEnd().toDate();
                
                String newHoraFinIntervalo = Utilidades.sumarMinsHora(sdfHora.format(end), _minutosColacion);
                StringTokenizer tokenHora = new StringTokenizer(newHoraFinIntervalo, ":");
                Calendar newCal = Calendar.getInstance(new Locale("es","CL"));
                newCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tokenHora.nextToken()));
                newCal.set(Calendar.MINUTE, Integer.parseInt(tokenHora.nextToken()));
                newCal.set(Calendar.SECOND, Integer.parseInt(tokenHora.nextToken()));
                Date newFechaFin = newCal.getTime();
                
                IntervaloVO objIntervalo = new IntervaloVO();
                objIntervalo.setHoraInicio(sdfHora.format(start));
                objIntervalo.setHoraFin(sdfHora.format(newFechaFin));
                
                hashIntervalos.put(iteracion, objIntervalo);
                
                System.out.println(WEB_NAME+"[getIntervalos]Intervalo[" + iteracion + "]. "
                    + "Inicio = " + sdfHora.format(start)
                    + ", fin = " + sdfHora.format(end));
                iteracion++;
            }
           
        }catch(ParseException pex){
            System.err.println("[getIntervalos]Error al parsear horas: " + pex.toString());
        }
        
        return hashIntervalos;
    }

    public static class IntervaloVO {

        private String horaInicio;
        private String horaFin;
    
        public IntervaloVO() {
        }
        
        public String getHoraInicio() {
            return horaInicio;
        }

        public void setHoraInicio(String horaInicio) {
            this.horaInicio = horaInicio;
        }

        public String getHoraFin() {
            return horaFin;
        }

        public void setHoraFin(String horaFin) {
            this.horaFin = horaFin;
        }

        @Override
        public String toString() {
            return "IntervaloVO{" + "horaInicio=" + horaInicio + ", horaFin=" + horaFin + '}';
        }
    }
}
