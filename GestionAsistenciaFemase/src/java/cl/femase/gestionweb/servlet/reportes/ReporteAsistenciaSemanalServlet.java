package cl.femase.gestionweb.servlet.reportes;

import cl.femase.gestionweb.business.DetalleAsistenciaBp;
import cl.femase.gestionweb.business.DetalleTurnosBp;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.EmpresaBp;
import cl.femase.gestionweb.business.TurnoRotativoBp;
import cl.femase.gestionweb.business.TurnosBp;
import cl.femase.gestionweb.common.DatabaseLocator;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.vo.DetalleAsistenciaVO;
import cl.femase.gestionweb.vo.DetalleTurnoVO;
import cl.femase.gestionweb.vo.DiferenciaHorasVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.EmpresaVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.TotalesSemanaVO;
import cl.femase.gestionweb.vo.UsuarioVO;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.jasperreports.engine.data.JRCsvDataSource;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@WebServlet(name = "ReporteAsistenciaSemanalServletServletServlet",
        urlPatterns = {"/api/reporte_asistencia_semanal"})
public class ReporteAsistenciaSemanalServlet extends HttpServlet {

    private static String JASPER_PATH;
    String jasperFilename = "asistencia_semanal.jasper";

    DatabaseLocator m_dbLocator;
    String m_dbpoolName;
    EmpleadosBp m_empleadosBp = new EmpleadosBp(null);

    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties = (PropertiesVO) application.getAttribute("appProperties");
        JASPER_PATH = appProperties.getReportesPath() + File.separator + jasperFilename;
        System.out.println("[ReporteAsistenciaSemanalServlet.init] Jasper Path: " + JASPER_PATH);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> body = mapper.readValue(
                request.getInputStream(),
                new TypeReference<Map<String, Object>>() {}
        );

        String centroCosto = (String) body.get("centroCosto");
        String fechaInicioStr = (String) body.get("fechaInicio");
        String fechaFinStr = (String) body.get("fechaFin");
        String formato = ((String) body.get("formato")).toLowerCase();
        @SuppressWarnings("unchecked")
        List<String> empleados = (List<String>) body.get("empleados");

        LocalDate fechaInicio = LocalDate.parse(fechaInicioStr);
        LocalDate fechaFin = LocalDate.parse(fechaFinStr);
        boolean singleEmployee = (empleados != null && empleados.size() == 1);

        System.out.println("[ReporteAsistenciaSemanalServlet.doPost]"
                + " Centro de costo: " + centroCosto
                + ", fechaInicio: " + fechaInicio
                + ", fechaFin: " + fechaFin
                + ", formato: " + formato
                + ", singleEmployee? " + singleEmployee);

        HttpSession session = request.getSession(true);
        UsuarioVO userConnected = (UsuarioVO) session.getAttribute("usuarioObj");

        DetalleAsistenciaBp detAsistenciaBp   = new DetalleAsistenciaBp(null, userConnected);
        
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties = (PropertiesVO) application.getAttribute("appProperties");

        try {
            if (singleEmployee) {
                System.out.println("[ReporteAsistenciaSemanalServlet.doPost]"
                    + "Solo un empleado seleccionado");
                String rutEmpleadoSeleccionado = empleados.get(0);
                EmpleadoVO infoEmpleado = m_empleadosBp.getEmpleado(
                    userConnected.getEmpresaId(),
                    rutEmpleadoSeleccionado
                );
                Map<String, Object> params = buildParams(
                    request,
                    infoEmpleado,
                    centroCosto,
                    fechaInicio,
                    fechaFin
                );
                System.out.println("[ReporteAsistenciaSemanalServlet.doPost]"
                    + "Obteniendo detalle asistencia "
                    + "empleado rut: " + rutEmpleadoSeleccionado);
                List<DetalleAsistenciaVO> detallesAsistencia = 
                    detAsistenciaBp.getDetallesInformeEmpleado(userConnected.getEmpresaId(),
                        rutEmpleadoSeleccionado, 
                        fechaInicio.toString(), 
                        fechaFin.toString());
                //if (!detallesAsistencia.isEmpty()){
                    // Generar/obtener CSV para este empleado
                    String csvFilePath = appProperties.getPathExportedFiles()+
                        File.separator+
                        userConnected.getUsername()+"_asistencia_semanal.csv";
                    getCSVFile(appProperties, 
                        csvFilePath, 
                        infoEmpleado, 
                        fechaInicio, 
                        fechaFin, 
                        detallesAsistencia);
                    //params.put("CSV_SOURCE", csvFilePath); // nombre del parámetro usado en el .jasper [web:407]
                    System.out.println("[ReporteAsistenciaSemanalServlet.doPost] CSV file: "
                        + csvFilePath);

                    try (InputStream jasperStream = new FileInputStream(JASPER_PATH)) {
                        JRCsvDataSource ds = new JRCsvDataSource(new File(csvFilePath));
                        ds.setUseFirstRowAsHeader(true);
                        ds.setFieldDelimiter(',');
                        // si tus campos están entre comillas:
                        ds.setRecordDelimiter("\n");
                        //ds.setQuoteChar('"');
                        System.out.println("[ReporteAsistenciaSemanalServlet.doPost]"
                            + "Llenar data del reporte");
                        JasperPrint jp = JasperFillManager.fillReport(jasperStream, params, ds);
                        if ("xlsx".equals(formato)) {
                            exportSingleXlsx(jp, rutEmpleadoSeleccionado, response);
                        } else {
                            exportSinglePdf(jp, rutEmpleadoSeleccionado, response);
                        }
                    }
                //}
            } else {
                System.out.println("[ReporteAsistenciaSemanalServlet.doPost]"
                    + "Varios empleados seleccionados");
                String zipName = "reporte_asistencia_semanal.zip";
                response.setContentType("application/zip");
                response.setHeader("Content-Disposition", "attachment; filename=" + zipName);

                try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {

                    for (String rutEmpleadoSeleccionado : empleados) {
                        System.out.println("[ReporteAsistenciaSemanalServlet.doPost]"
                            + " rutEmpleadoSeleccionado: " + rutEmpleadoSeleccionado);

                        EmpleadoVO infoEmpleado = m_empleadosBp.getEmpleado(
                            userConnected.getEmpresaId(),
                            rutEmpleadoSeleccionado
                        );
                        System.out.println("[ReporteAsistenciaSemanalServlet.doPost]"
                            + "Obteniendo detalle asistencia "
                            + "empleado rut: " + rutEmpleadoSeleccionado);
                        List<DetalleAsistenciaVO> detallesAsistencia = 
                            detAsistenciaBp.getDetallesInformeEmpleado(userConnected.getEmpresaId(),
                                rutEmpleadoSeleccionado, 
                                fechaInicio.toString(), 
                                fechaFin.toString());

                        Map<String, Object> params = buildParams(
                            request,
                            infoEmpleado,
                            centroCosto,
                            fechaInicio,
                            fechaFin
                        );

                        // Generar/obtener CSV para este empleado
                        String csvFilePath = appProperties.getPathExportedFiles()+
                            File.separator+
                            userConnected.getUsername()+"_asistencia_semanal.csv";
                        getCSVFile(appProperties, 
                            csvFilePath, 
                            infoEmpleado, 
                            fechaInicio, 
                            fechaFin, 
                            detallesAsistencia);
                
                        //params.put("CSV_SOURCE", csvFilePath);

                        try (InputStream jasperStreamEmp = new FileInputStream(JASPER_PATH)) {
                            //JasperPrint jp = JasperFillManager.fillReport(jasperStreamEmp, params);
                            JRCsvDataSource ds = new JRCsvDataSource(new File(csvFilePath));
                            ds.setUseFirstRowAsHeader(true);
                            ds.setFieldDelimiter(',');
                            // si tus campos están entre comillas:
                            ds.setRecordDelimiter("\n");
                            //ds.setQuoteChar('"');
                            JasperPrint jp = JasperFillManager.fillReport(jasperStreamEmp, params, ds);
                        
                            String ext = "xlsx".equals(formato) ? ".xlsx" : ".pdf";
                            String fileName = rutEmpleadoSeleccionado + ext;

                            zos.putNextEntry(new ZipEntry(fileName));

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            if ("xlsx".equals(formato)) {
                                exportXlsxToStream(jp, baos);
                            } else {
                                exportPdfToStream(jp, baos);
                            }

                            baos.writeTo(zos);
                            zos.closeEntry();
                        }
                    }

                    zos.finish();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("Error al generar reporte: " + ex.getMessage());
        }
    }

    /**
    * 
    */
    private void getCSVFile(PropertiesVO _appProperties,
            String _csvFilePath,
            EmpleadoVO _infoEmpleado,
            LocalDate _fechaInicio,
            LocalDate _fechaFin,
            List<DetalleAsistenciaVO> _registrosAsistencia) throws Exception {

        try{
            //EmpleadosBp empleadosBp = new EmpleadosBp(new PropertiesVO());
            System.out.println("[ReporteAsistenciaSemanalServlet."
                + "getCSVFile]Generar archivo CSV, path: " + _csvFilePath);
            //write csv file
            List<DetalleAsistenciaVO>  registrosSemana = new ArrayList<>();
            FileWriter csvWriter = new FileWriter(_csvFilePath);
            
            int filas = 0;
            // Cabecera con los mismos nombres que en el jrxml
            csvWriter.append("\"Fecha\",\"Hora_Entrada\",\"Hora_Salida\","
                + "\"Entrada_comentario\",\"Salida_comentario\","
                + "\"Entrada_teorica\",\"Salida_teorica\",\"Hrs_teoricas\","
                + "\"Horas_presenciales\",\"Hhmm_atraso\",\"Hhmm_justificadas\","
                + "\"Hhmm_extras\",\"Hhmm_ausencia\",\"Hhmm_trabajadas\","
                + "\"Observacion\",\"Hhmm_no_trabajadas\"");
            csvWriter.append("\n");
            
            for (DetalleAsistenciaVO detalle : _registrosAsistencia) {
                System.out.println("[ReporteAsistenciaSemanalServlet."
                    + "getCSVFile]"
                    + "empresaId: " + _infoEmpleado.getEmpresaId()
                    + ", rutEmpleado: " +  _infoEmpleado.getRut()        
                    + ", idTurno: "+ _infoEmpleado.getIdTurno()    
                    + ", itera registro asistencia: " + detalle.toString()
                    + ", hrs presenciales: " + detalle.getHrsPresenciales());
                filas++;                               
                
                String labelFecha = detalle.getLabelFechaEntradaMarca();
                String entradaComentario = detalle.getComentarioMarcaEntrada();
                String salidaComentario = detalle.getComentarioMarcaSalida();
                String hrsPresenciales  = detalle.getHrsPresenciales();
                String hhmmAtraso       = detalle.getHhmmAtraso();
                String hhmmJustificadas = detalle.getHhmmJustificadas();
                String totalDelDia      = detalle.getHrsTrabajadas();
                String observacion      = detalle.getObservacion();
                String hrsAusencia      = detalle.getHrsAusencia();
                String hrsNoTrabajadas  = detalle.getHrsNoTrabajadas();
                String hrsMinsExtras    = detalle.getHorasMinsExtrasAutorizadas();
                String hrsTeoricasMenosColacion = null;
                
                System.out.println("[ReporteAsistenciaSemanalServlet."
                    + "getCSVFile]"
                    + "labelFecha:" + labelFecha
                    + ", hrsNoTrabajadas:" + hrsNoTrabajadas);
                
                //boolean esFeriado = detalle.isEsFeriado();
                System.out.println("[ReporteAsistenciaSemanalServlet."
                    + "getCSVFile]calcular hrs teoricas...");
                DetalleAsistenciaVO auxDetail = 
                    getHrsTeoricas(detalle, _infoEmpleado);
                hrsTeoricasMenosColacion = auxDetail.getHhmmTeoricas();
                detalle.setHhmmTeoricas(auxDetail.getHhmmTeoricas());
                //detalle.setObservacion(auxDetail.getObservacion());
                
                if (entradaComentario == null) entradaComentario = "";
                if (salidaComentario == null) salidaComentario = "";
                if (hrsPresenciales == null) hrsPresenciales = "-";
                if (hhmmAtraso == null) hhmmAtraso = "-";
                if (hhmmJustificadas == null) hhmmJustificadas = "-";
                if (totalDelDia == null) totalDelDia = "-";
                if (observacion == null) observacion = "-";
                if (hrsAusencia == null) hrsAusencia = "-";
                if (hrsNoTrabajadas == null) hrsNoTrabajadas = "-";
                if (hrsMinsExtras == null) hrsMinsExtras = "-";
                if (hrsTeoricasMenosColacion == null) hrsTeoricasMenosColacion = "-";
                System.out.println("[ReporteAsistenciaSemanalServlet."
                    + "getCSVFile]"
                    + "Fecha: " + detalle.getFechaEntradaMarca()
                    + ", hrsTeoricasMenosColacion(2): " + hrsTeoricasMenosColacion
                    + ", entradaComentario: " + entradaComentario
                    + ", salidaComentario: " + salidaComentario);
                /** campos:
                 * new String[] {"Fecha",
                    "Hora_Entrada",
                    "Hora_Salida",
                    "Entrada_comentario",
                    "Salida_comentario",
                    "Entrada_teorica",
                    "Salida_teorica",
                    "Horas_presenciales",
                    "Hhmm_atraso",
                    "Hhmm_justificadas",
                    "Hhmm_extras",
                    "Hhmm_ausencia",
                    "Hhmm_trabajadas",
                    "Observacion",
                    "Hhmm_no_trabajadas"};
                 * 
                 
                */
 
                /*    
                Se define el dia domingo como el dia de corte para calcular totales por semana.
                Por cada fecha incluida en el reporte.
                {
                        Guardar datos del reporte en nueva tabla 'asistencia_semanal'. (insert)
                        Si el dia de la fecha = Domingo --> insertar totales en nueva tabla 'asistencia_semanal' (insert). 
                }
                Calcular linea de totales de horas (va al final del reporte)
                */
                if (labelFecha.startsWith("Do") || filas == _registrosAsistencia.size()){//es dia domingo o es el ultimo dia de la lista
                    System.out.println("[ReporteAsistenciaSemanalServlet."
                        + "getCSVFile]Es domingo");
                    registrosSemana.add(detalle);
                    //mostrar detalle del dia domingo (si es que tiene turno este dia)
                    csvWriter.append("\"" + labelFecha
                        + "\",\"" + detalle.getHoraEntrada() 
                        + "\",\"" + detalle.getHoraSalida()
                        + "\",\"" + entradaComentario
                        + "\",\"" + salidaComentario
                        + "\",\"" + detalle.getHoraEntradaTeorica()
                        + "\",\"" + detalle.getHoraSalidaTeorica()
                        + "\",\"" + hrsTeoricasMenosColacion    
                        + "\",\"" + hrsPresenciales
                        + "\",\"" + hhmmAtraso
                        + "\",\"" + hhmmJustificadas
                        + "\",\"" + hrsMinsExtras 
                        + "\",\"" + hrsAusencia        
                        + "\",\"" + totalDelDia
                        + "\",\"" + observacion 
                        + "\",\"" + hrsNoTrabajadas    
                        + "\"");
                    csvWriter.append("\n");    
                    TotalesSemanaVO totalSemana = 
                        getTotalesSemana(registrosSemana);
                    registrosSemana.clear();
                    
                    //escribir linea de totales semana (lu-do)
                    String lineaTotSemana = "\"" + ""
                    + "\",\"" + "" 
                    + "\",\"" + ""
                    + "\",\"" + ""
                    + "\",\"" + ""
                    + "\",\"" + ""
                    + "\",\"" + "Total Semana"
                    + "\",\"" + totalSemana.getTotalHrsTeoricas()
                    + "\",\"" + totalSemana.getTotalHrsPresenciales()
                    + "\",\"" + totalSemana.getTotalHrsAtraso()
                    + "\",\"" + totalSemana.getTotalHrsJustificadas() 
                    + "\",\"" + totalSemana.getTotalHrsExtras()        
                    + "\",\"" + totalSemana.getTotalHrsNoTrabajadas()
                    + "\",\"" + totalSemana.getTotalHrsTrabajadas() + "\"";
                    
                    System.out.println("[ReporteAsistenciaSemanalServlet."
                        + "getCSVFile]lineaTotSemana: " + lineaTotSemana);
                    csvWriter.append(lineaTotSemana);
                    
                }else{
                    registrosSemana.add(detalle);
                    String auxdetail = "\"" + labelFecha
                        + "\",\"" + detalle.getHoraEntrada() 
                        + "\",\"" + detalle.getHoraSalida()
                        + "\",\"" + entradaComentario
                        + "\",\"" + salidaComentario
                        + "\",\"" + detalle.getHoraEntradaTeorica()
                        + "\",\"" + detalle.getHoraSalidaTeorica()
                        + "\",\"" + hrsTeoricasMenosColacion
                        + "\",\"" + hrsPresenciales
                        + "\",\"" + hhmmAtraso
                        + "\",\"" + hhmmJustificadas
                        + "\",\"" + hrsMinsExtras 
                        + "\",\"" + hrsAusencia        
                        + "\",\"" + totalDelDia
                        + "\",\"" + observacion 
                        + "\",\"" + hrsNoTrabajadas    
                        + "\"";
                    csvWriter.append(auxdetail);
                    System.out.println("[ReporteAsistenciaSemanalServlet."
                        + "getCSVFile]"
                        + "hrsAusencia: " + hrsAusencia
                        + ", totalDelDia: " + totalDelDia
                        + ", hrsNoTrabajadas: " + hrsNoTrabajadas);
                    System.out.println("[ReporteAsistenciaSemanalServlet."
                        + "getCSVFile]linea detalle: " + auxdetail);
                }
                csvWriter.append("\n");
            }
            
            csvWriter.flush();
            csvWriter.close();
        }catch(IOException ioex){
            System.err.println("[ReporteAsistenciaSemanalServlet."
                + "getCSVFile]"
                + "Error al escribir csv: "+ioex.toString());
        }
    }

    /**
     * 
    * Retorna totales para una semana
    */
    private TotalesSemanaVO getTotalesSemana(List<DetalleAsistenciaVO> _registrosSemana){
        
        TotalesSemanaVO totales = new TotalesSemanaVO();
        
        ArrayList<String> listaHorasPresenciales = new ArrayList<>();
        ArrayList<String> listaHorasTeoricas = new ArrayList<>();
        ArrayList<String> listaHorasTrabajadas = new ArrayList<>();
        ArrayList<String> listaHorasNoTrabajadas = new ArrayList<>();
        ArrayList<String> listaHorasAusencia = new ArrayList<>();
        ArrayList<String> listaHorasAtraso = new ArrayList<>();
        ArrayList<String> listaHorasExtras = new ArrayList<>();
        ArrayList<String> listaHorasJustificadas = new ArrayList<>();
        String strAuxHp             = "";
        String strAuxHorasTrabajadas = "";
        String strAuxAtraso         = "";
        String strAuxHextras         = "";
        String strAuxHaus           = "";
        String strAuxHrsJustificadas = "";
        String strHrsTeoricas       = "";
        String strAuxHorasNoTrabajadas  ="";
        int countDiasTrabajados     = 0;
        int countDiasAusente        = 0;    
        
        for (DetalleAsistenciaVO detalle : _registrosSemana) {
            strAuxHp = detalle.getHrsPresenciales();
            listaHorasPresenciales.add(strAuxHp);

            System.out.println("[ReporteAsistenciaSemanalServlet."
                + "getParameters]"
                + "getHrsTrabajadas: " + detalle.getHrsTrabajadas()
                + ", getHrsAusencia: " + detalle.getHrsAusencia()
                + ", getHhmmTeoricas: " + detalle.getHhmmTeoricas()
                + ", getHrsNoTrabajadas: " + detalle.getHrsNoTrabajadas());        
            
            strAuxHorasTrabajadas = detalle.getHrsTrabajadas();
            listaHorasTrabajadas.add(strAuxHorasTrabajadas);

            strAuxHaus = detalle.getHrsAusencia();
            listaHorasAusencia.add(strAuxHaus);
            
            strAuxHorasNoTrabajadas = detalle.getHrsNoTrabajadas();
            if (strAuxHorasNoTrabajadas!=null && strAuxHorasNoTrabajadas.compareTo("-") != 0){
                System.out.println("[ReporteAsistenciaSemanalServlet."
                    + "getParameters]Add hrs NO trabajadas: " + strAuxHorasNoTrabajadas);
                listaHorasNoTrabajadas.add(strAuxHorasNoTrabajadas);
            }
            //horas teoricas
            strHrsTeoricas = detalle.getHhmmTeoricas();
            listaHorasTeoricas.add(strHrsTeoricas);
            
            //marzo 11,2018
            strAuxAtraso = detalle.getHhmmAtraso();
            if (strAuxAtraso != null) listaHorasAtraso.add(strAuxAtraso);
            strAuxHextras = detalle.getHorasMinsExtrasAutorizadas();
            if (strAuxHextras != null) listaHorasExtras.add(strAuxHextras);
            strAuxHrsJustificadas = detalle.getHhmmJustificadas();
            System.out.println("[ReporteAsistenciaSemanalServlet."
                + "getParameters]fecha: " + detalle.getFechaEntradaMarca()
                + ", hrs trabajadas: " + strAuxHorasTrabajadas
                + ", hrs No trabajadas: " + strAuxHorasNoTrabajadas
                + ", observacion: " + detalle.getObservacion());

            if (strAuxHrsJustificadas != null) listaHorasJustificadas.add(strAuxHrsJustificadas);
                
            if ((detalle.getHoraEntrada() != null && detalle.getHoraEntrada().compareTo("00:00:00") != 0 
                    && detalle.getHoraSalida() != null && detalle.getHoraSalida().compareTo("00:00:00") != 0)
                    || (detalle.getObservacion()!=null && detalle.getObservacion().compareTo("Libre") == 0)
                    || (strAuxHorasTrabajadas != null) ){
                countDiasTrabajados++;
            }
            System.out.println("[ReporteAsistenciaSemanalServlet."
                + "getParameters]"
                + "Fecha entrada= " + detalle.getFechaEntradaMarca()
                + ", getObservacion= " + detalle.getHrsAusencia()
                + ", getObservacion= " + detalle.getObservacion());
            if (detalle.getObservacion() != null 
                && detalle.getObservacion().compareTo("Sin marcas") == 0)
            {
                System.out.println("[ReporteAsistenciaSemanalServlet."
                    + "getParameters]Sumo dias de ausencia");
                countDiasAusente++;
            }  

        }//fin iteracion detalle asistencia  

        if (!listaHorasPresenciales.isEmpty()){
            totales.setTotalHrsPresenciales(Utilidades.sumTimesList(listaHorasPresenciales));
        }
        if (!listaHorasTeoricas.isEmpty()){
            totales.setTotalHrsTeoricas(Utilidades.sumTimesList(listaHorasTeoricas));
        }
        if (!listaHorasTrabajadas.isEmpty()){
            totales.setTotalHrsTrabajadas(Utilidades.sumTimesList(listaHorasTrabajadas));
        }
        if (!listaHorasAusencia.isEmpty()){
            totales.setTotalHrsAusencia(Utilidades.sumTimesList(listaHorasAusencia));
        }
        if (!listaHorasExtras.isEmpty()){
            totales.setTotalHrsExtras(Utilidades.sumTimesList(listaHorasExtras));
        }
        if (!listaHorasAtraso.isEmpty()){
            totales.setTotalHrsAtraso(Utilidades.sumTimesList(listaHorasAtraso));
        }
        if (!listaHorasJustificadas.isEmpty()){
            totales.setTotalHrsJustificadas(Utilidades.sumTimesList(listaHorasJustificadas));
        }
        
        if (!listaHorasNoTrabajadas.isEmpty()){
            System.out.println("[ReporteAsistenciaSemanalServlet."
                + "getParameters]Sum lista de Hrs No Trabajadas");
            totales.setTotalHrsNoTrabajadas(Utilidades.sumTimesList(listaHorasNoTrabajadas));
        }
        
        totales.setDiasAusente(countDiasAusente);
        totales.setDiasTrabajados(countDiasTrabajados);
        
        System.out.println("[ReporteAsistenciaSemanalServlet."
            + "getParameters]Objeto Totales Semana: " + totales.toString());
        
        return totales;
    }
    
    // --- buildParams y resto de métodos tal como los tenías ---

    private Map<String, Object> buildParams(HttpServletRequest _request,
            EmpleadoVO _infoEmpleado,
            String centroCosto,
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        Map<String, Object> params = new HashMap<>();

        ServletContext application = this.getServletContext();
        PropertiesVO appProperties = (PropertiesVO) application.getAttribute("appProperties");
        HttpSession session = _request.getSession(true);
        UsuarioVO userConnected = (UsuarioVO) session.getAttribute("usuarioObj");

        DetalleAsistenciaBp detAsistenciaBp = new DetalleAsistenciaBp(null, userConnected);
        EmpresaBp empresaBp = new EmpresaBp(appProperties);

        String strAuxHp = "";
        String strAuxHt = "";
        String strAuxAtraso = "";
        String strAuxHextras = "";
        String strAuxHaus = "";
        String strAuxHrsJustificadas = "";
        String strHrsNoTrabajadas = "";
        //int countDiasTrabajados = 0;
        //int countDiasAusente = 0;
        List<DetalleAsistenciaVO> detallesAsistencia =
            detAsistenciaBp.getDetalles(_infoEmpleado.getRut(),
                fechaInicio.toString(), fechaFin.toString());

        System.out.println("[ReporteAsistenciaSemanalServlet.buildParams]"
            + " detallesAsistencia.isEmpty()? " + detallesAsistencia.isEmpty());
        String totalHrsPresenciales = "00:00";
        String totalHrsTrabajadas = "00:00";
        String totalHrsAusencia = "00:00";
        String totalHrsNoTrabajadas = "00:00";
        String totalHrsExtras = "00:00";
        String totalHrsAtraso = "00:00";
        String totalHrsJustificadas = "00:00";
        String totalHrsTeoricas = "00:00";
        int countDiasTrabajados = 0;
        int countDiasAusente = 0;
        
        if (!detallesAsistencia.isEmpty()) {
            HashMap parameters = new HashMap();
            ArrayList<String> listaHorasPresenciales = new ArrayList<>();
            ArrayList<String> listaHorasTeoricas = new ArrayList<>();
            ArrayList<String> listaHorasTrabajadas = new ArrayList<>();
            ArrayList<String> listaHorasNoTrabajadas = new ArrayList<>();
            ArrayList<String> listaHorasAusencia = new ArrayList<>();
            ArrayList<String> listaHorasAtraso = new ArrayList<>();
            ArrayList<String> listaHorasExtras = new ArrayList<>();
            ArrayList<String> listaHorasJustificadas = new ArrayList<>();
  
            for (DetalleAsistenciaVO detalle : detallesAsistencia) {
                strAuxHp = detalle.getHrsPresenciales();
                listaHorasPresenciales.add(strAuxHp);

                strAuxHt = detalle.getHrsTrabajadas();
                listaHorasTrabajadas.add(strAuxHt);

                strAuxHaus = detalle.getHrsAusencia();
                listaHorasAusencia.add(strAuxHaus);

                strAuxAtraso = detalle.getHhmmAtraso();
                if (strAuxAtraso != null) listaHorasAtraso.add(strAuxAtraso);

                strAuxHextras = detalle.getHorasMinsExtrasAutorizadas();
                if (strAuxHextras != null) listaHorasExtras.add(strAuxHextras);

                strAuxHrsJustificadas = detalle.getHhmmJustificadas();
                if (strAuxHrsJustificadas != null) listaHorasJustificadas.add(strAuxHrsJustificadas);

                strHrsNoTrabajadas = detalle.getHrsNoTrabajadas();
                if (strHrsNoTrabajadas != null && strHrsNoTrabajadas.compareTo("-") != 0) {
                    listaHorasNoTrabajadas.add(strHrsNoTrabajadas);
                }

                if ((detalle.getHoraEntrada() != null && detalle.getHoraEntrada().compareTo("00:00:00") != 0
                        && detalle.getHoraSalida() != null && detalle.getHoraSalida().compareTo("00:00:00") != 0)
                        || (detalle.getObservacion() != null && detalle.getObservacion().compareTo("Libre") == 0)
                        || (strAuxHt != null)) {
                    countDiasTrabajados++;
                } else {
                    // no suma día trabajado
                }

                DetalleAsistenciaVO auxDetail =
                        getHrsTeoricas(detalle, _infoEmpleado);
                detalle.setHhmmTeoricas(auxDetail.getHhmmTeoricas());

                listaHorasTeoricas.add(auxDetail.getHhmmTeoricas());
            }

            totalHrsPresenciales = "00:00";
            totalHrsTrabajadas = "00:00";
            totalHrsAusencia = "00:00";
            totalHrsNoTrabajadas = "00:00";
            totalHrsExtras = "00:00";
            totalHrsAtraso = "00:00";
            totalHrsJustificadas = "00:00";
            totalHrsTeoricas = "00:00";
            
            if (!listaHorasPresenciales.isEmpty()) {
                totalHrsPresenciales = Utilidades.sumTimesList(listaHorasPresenciales);
            }
            if (!listaHorasTeoricas.isEmpty()) {
                totalHrsTeoricas = Utilidades.sumTimesList(listaHorasTeoricas);
            }
            if (!listaHorasTrabajadas.isEmpty()) {
                totalHrsTrabajadas = Utilidades.sumTimesList(listaHorasTrabajadas);
            }
            if (!listaHorasAusencia.isEmpty()) {
                totalHrsAusencia = Utilidades.sumTimesList(listaHorasAusencia);
            }
            if (!listaHorasExtras.isEmpty()) {
                totalHrsExtras = Utilidades.sumTimesList(listaHorasExtras);
            }
            if (!listaHorasAtraso.isEmpty()) {
                totalHrsAtraso = Utilidades.sumTimesList(listaHorasAtraso);
            }
            if (!listaHorasJustificadas.isEmpty()) {
                totalHrsJustificadas = Utilidades.sumTimesList(listaHorasJustificadas);
            }
            if (!listaHorasNoTrabajadas.isEmpty()) {
                totalHrsNoTrabajadas = Utilidades.sumTimesList(listaHorasNoTrabajadas);
            }
        }
        
        EmpresaVO empresa = empresaBp.getEmpresaByKey(_infoEmpleado.getEmpresaId());
        params.put("empresa_id", _infoEmpleado.getEmpresa().getId());
        params.put("empresa_rut", empresa.getRut());
        params.put("empresa_nombre", _infoEmpleado.getEmpresa().getNombre());
        params.put("empresa_direccion", empresa.getDireccion());
        params.put("empresa_comuna", empresa.getComunaNombre());
        params.put("empresa_region", empresa.getRegionNombre());

        params.put("rut", _infoEmpleado.getRut());
        params.put("rut_full", _infoEmpleado.getCodInterno());
        params.put("cod_interno", _infoEmpleado.getRut());
        params.put("nombre", _infoEmpleado.getNombres() + " " + _infoEmpleado.getApePaterno()+ " " + _infoEmpleado.getApeMaterno());
        params.put("cargo", _infoEmpleado.getNombreCargo());

        params.put("cenco_id", "" + _infoEmpleado.getCentroCosto().getId());
        params.put("cenco_nombre", _infoEmpleado.getCentroCosto().getNombre());
        params.put("fecha_ingreso", _infoEmpleado.getFechaInicioContratoAsStr());
        params.put("startDate", fechaInicio.toString());
        params.put("endDate", fechaFin.toString());
            
        params.put("totalHrsPresenciales", totalHrsPresenciales);
        params.put("totalHrsTeoricas", totalHrsTeoricas);
        params.put("totalHrsTrabajadas", totalHrsTrabajadas);
        params.put("totalHrsAusencia", totalHrsAusencia);
        params.put("totalHrsNoTrabajadas", totalHrsNoTrabajadas);
        params.put("totalHrsAtraso", totalHrsAtraso);
        params.put("totalHrsExtras", totalHrsExtras);
        params.put("totalHrsJustificadas", totalHrsJustificadas);
        params.put("diasTrabajados", countDiasTrabajados);
        params.put("diasAusente", countDiasAusente);
        
        return params;
    }

    private DetalleAsistenciaVO getHrsTeoricas(DetalleAsistenciaVO _detalle,
                                               EmpleadoVO _infoEmpleado) {

        DetalleAsistenciaVO newDetail = new DetalleAsistenciaVO();
        TurnosBp turnoBp = new TurnosBp(new PropertiesVO());
        TurnoRotativoBp turnoRotativoBp = new TurnoRotativoBp(new PropertiesVO());
        DetalleTurnosBp detalleTurnoBp = new DetalleTurnosBp(new PropertiesVO());

        StringTokenizer tokenFecha =
                new StringTokenizer(_detalle.getFechaEntradaMarca(), "-");
        int diaSemana = Utilidades.getDiaSemana(Integer.parseInt(tokenFecha.nextToken()),
                Integer.parseInt(tokenFecha.nextToken()),
                Integer.parseInt(tokenFecha.nextToken()));

        DetalleTurnoVO detalleTurno = null;
        int idTurnoRotativo = turnoBp.getTurnoRotativo(_infoEmpleado.getEmpresaId());
        boolean tieneTurnoRotativo = false;
        boolean calcularHrsTeoricas = true;
        String hrsTeoricasMenosColacion = null;
        boolean esFeriado = _detalle.isEsFeriado();
        boolean tieneDetalleTurnoCodDiaFeriado = false;
        boolean soloMarcaEntrada = false;
        boolean soloMarcaSalida = false;
        boolean noTieneMarcas = false;

        if (idTurnoRotativo == _infoEmpleado.getIdTurno()) {
            tieneTurnoRotativo = true;
        }

        if (_detalle.getHoraEntrada().compareTo("00:00:00") != 0
                && _detalle.getHoraSalida().compareTo("00:00:00") == 0) soloMarcaEntrada = true;
        if (_detalle.getHoraEntrada().compareTo("00:00:00") == 0
                && _detalle.getHoraSalida().compareTo("00:00:00") != 0) soloMarcaSalida = true;
        if (_detalle.getHoraEntrada().compareTo("00:00:00") == 0
                && _detalle.getHoraSalida().compareTo("00:00:00") == 0) noTieneMarcas = true;

        if (tieneTurnoRotativo) {
            detalleTurno =
                    turnoRotativoBp.getAsignacionTurnoByFecha(_infoEmpleado.getEmpresaId(),
                            _infoEmpleado.getRut(), _detalle.getFechaEntradaMarca());
        } else {
            detalleTurno =
                    detalleTurnoBp.getDetalleTurno(_infoEmpleado.getEmpresaId(),
                            _infoEmpleado.getIdTurno(), diaSemana);

            if (detalleTurno == null && esFeriado) {
                detalleTurno =
                        detalleTurnoBp.getDetalleTurno(_infoEmpleado.getEmpresaId(),
                                _infoEmpleado.getIdTurno(), 8);
                if (detalleTurno != null) tieneDetalleTurnoCodDiaFeriado = true;
            }
        }

        if (detalleTurno != null) {
            DiferenciaHorasVO duracionTurno =
                    Utilidades.getTimeDifference(_detalle.getFechaEntradaMarca() + " " + detalleTurno.getHoraEntrada(),
                            _detalle.getFechaEntradaMarca() + " " + detalleTurno.getHoraSalida());
            String hhmmTurno = duracionTurno.getStrDiferenciaHorasMinutos();
        } else {
            // sin detalle turno
        }

        if (esFeriado && !tieneDetalleTurnoCodDiaFeriado) calcularHrsTeoricas = false;

        if (calcularHrsTeoricas) {
            DiferenciaHorasVO diferenciaTeorica =
                    Utilidades.getTimeDifference(_detalle.getFechaEntradaMarca()
                                    + " " + _detalle.getHoraEntradaTeorica(),
                            _detalle.getFechaEntradaMarca()
                                    + " " + _detalle.getHoraSalidaTeorica());
            String hrsTeoricas = diferenciaTeorica.getStrDiferenciaHorasMinutos();
            if (detalleTurno != null) {
                hrsTeoricasMenosColacion =
                        Utilidades.restarMinsHora(hrsTeoricas, detalleTurno.getMinutosColacion());
            }
            newDetail.setHhmmTeoricas(hrsTeoricasMenosColacion);
        }
        return newDetail;
    }

    /**
    * 
    */
    private void exportSinglePdf(JasperPrint jp, String rut, HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=" + rut + ".pdf");

        try (OutputStream out = response.getOutputStream()) {
            exportPdfToStream(jp, out);
            out.flush();
        }
    }

    private void exportSingleXlsx(JasperPrint jp, String rut, HttpServletResponse response) throws Exception {
        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=" + rut + ".xlsx");

        try (OutputStream out = response.getOutputStream()) {
            exportXlsxToStream(jp, out);
            out.flush();
        }
    }

    private void exportPdfToStream(JasperPrint jp, OutputStream out) throws Exception {
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(jp));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
        exporter.setConfiguration(new SimplePdfExporterConfiguration());
        exporter.exportReport();
    }

    private void exportXlsxToStream(JasperPrint jp, OutputStream out) throws Exception {
        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jp));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

        SimpleXlsxReportConfiguration config = new SimpleXlsxReportConfiguration();
        config.setOnePagePerSheet(false);
        config.setDetectCellType(true);
        config.setCollapseRowSpan(false);
        exporter.setConfiguration(config);

        exporter.exportReport();
    }
}
