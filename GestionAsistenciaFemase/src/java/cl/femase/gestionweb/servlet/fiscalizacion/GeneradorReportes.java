/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cl.femase.gestionweb.servlet.fiscalizacion;

import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.common.DatabaseLocator;
import cl.femase.gestionweb.common.GetPropertyValues;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.dao.AsignacionTurnoDAO;
import cl.femase.gestionweb.vo.ModificacionAlteracionTurnoVO;
import cl.femase.gestionweb.vo.ModificacionTurnoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.ReportTypeVO;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import net.sf.jasperreports.engine.*;
import java.util.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

/**
 *
 * @author aledi
 */
public class GeneradorReportes {
private final String jasperFilePath; // Ejemplo: "reportes/plantilla.jasper"

    private PropertiesVO appProperties;
    private String OUTPUT_FOLDER;
    
    /**
    * 
    * @param _appProperties
    * @param _jasperFileName
    */
    public GeneradorReportes(PropertiesVO _appProperties,
            String _jasperFileName) {
        
        this.appProperties = _appProperties;
        
        this.jasperFilePath = this.appProperties.getReportesPath() + File.separator + _jasperFileName;
        System.out.println("[GeneradorReportes]"
            + "Reporte Jasper full: " + this.jasperFilePath);
        OUTPUT_FOLDER = this.appProperties.getPathExportedFiles();
    }

    /**
    * Retorna ruta del zip para descarga que contiene todos los reportes generados 
    * (uno por cada empleado seleccionado)
    * 
    * @param _reportParams
    * @param _tipo
    * @param _outputFormat
    * @return 
    */
    public String generarReporte(List<Map<String, Object>> _reportParams, 
            String _tipo, String _outputFormat) {
        String zipFilePath = OUTPUT_FOLDER + File.separator + "reportes.zip";
        try {
            Connection sourceDbConnection;
            String m_dbpoolName;
            DatabaseLocator dbLocator  = DatabaseLocator.getInstance();
            GetPropertyValues m_properties = new GetPropertyValues();
            m_dbpoolName = m_properties.getKeyValue("dbpoolname");
            System.out.println("[GeneradorReportes.generarReporte]"
                + "m_dbpoolName: " + m_dbpoolName);
            sourceDbConnection = dbLocator.getConnection(m_dbpoolName,
                "[GeneradorReportes.generarReporte]");
            Class.forName("org.postgresql.Driver"); // Registrar el driver PostgreSQL (opcional en Java 6+)
            ReportTypeVO reportType = Constantes.REPORT_TYPE.get(_tipo);
            ArrayList<String> generatedFiles = new ArrayList<>();
            
            AsignacionTurnoDAO asignacionTurnoDao = new AsignacionTurnoDAO();
            
            // 1. Generar reportes individuales para cada empleado/trabajador
            for (Map<String, Object> parametros : _reportParams) {
                JasperPrint jasperPrint;
//                JRBeanCollectionDataSource listaDataSource=null;
//                if(_tipo.compareTo("modificaciones_turnos") == 0){
//                    //El datasourece es una lista con un solo elemento que contiene el registro a mostrar en el reporte
//                    //JasperReport reporteAux = (JasperReport) JRLoader.loadObjectFromFile(reportType.getJasperFileName());
//                    //AsignacionTurnoDAO daoAsignacion = new AsignacionTurnoDAO();
//                    ArrayList<ModificacionAlteracionTurnoVO> listaModificaciones = 
//                        asignacionTurnoDao.getModificacionAlteracionTurno((String)parametros.get("EMPRESA_ID"), 
//                        (String)parametros.get("RUT_TRABAJADOR"));
//                    for (ModificacionAlteracionTurnoVO obj : listaModificaciones) {
//                        System.out.println("[GeneradorReportes.generarReporte]"
//                            + "Objeto a mostrar: " + obj.toString());
//                    }
//
//                    // Crea el DataSource para JasperReports
//                    listaDataSource = new JRBeanCollectionDataSource(listaModificaciones);
//                    //Llenar el reporte
//                    System.out.println("[GeneradorReportes.generarReporte]"
//                        + "Llenar el reporte: " + jasperFilePath);
//                    jasperPrint = JasperFillManager.fillReport(
//                        jasperFilePath, 
//                        parametros, 
//                        listaDataSource);
//                }else{
                    //Llenar el reporte
                    jasperPrint = JasperFillManager.fillReport(
                        jasperFilePath, 
                        parametros, 
                        sourceDbConnection);
//                }
                
                if (_outputFormat.compareTo("PDF")==0){
                    String nombreArchivoSalidaEmpleado = 
                        OUTPUT_FOLDER + File.separator + reportType.getLabel() + "_" + parametros.get("RUT_TRABAJADOR")+".pdf";
                    System.out.println("[GeneradorReportes.generarReporte]PDF-"
                        + "Add Archivo generado: " + nombreArchivoSalidaEmpleado);
                    generatedFiles.add(nombreArchivoSalidaEmpleado);
                    String outputfile;
//                    if (listaDataSource == null){
                        outputfile = JasperFillManager.fillReportToFile(jasperFilePath,
                            parametros, sourceDbConnection);
//                    }else{
//                        System.out.println("[GeneradorReportes.generarReporte]PDF-"
//                            + "Genera reporte con datasource=lista con un elemento");
//                        outputfile = JasperFillManager.fillReportToFile(jasperFilePath,
//                            parametros, listaDataSource);
//                    }
                    System.out.println("[GeneradorReportes.generarReporte]"
                        + "outputfile: " + outputfile);
                    JasperExportManager.exportReportToPdfFile(outputfile,
                        nombreArchivoSalidaEmpleado);
                }else if (_outputFormat.compareTo("XLSX")==0){
                         // 5. Configurar exportador XLSX
                        JRXlsxExporter exporter = new JRXlsxExporter();
                        String nombreArchivoSalidaEmpleado = 
                            OUTPUT_FOLDER + File.separator 
                                + reportType.getLabel() 
                                + "_" 
                                + parametros.get("RUT_TRABAJADOR") + ".xlsx";
                        System.out.println("[GeneradorReportes.generarReporte]XLSX-"
                            + "Add Archivo generado: " + nombreArchivoSalidaEmpleado);
                        generatedFiles.add(nombreArchivoSalidaEmpleado);
                        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(
                            nombreArchivoSalidaEmpleado));

                        // 6. Configuración opcional (ajustar según necesidades)
                        SimpleXlsxReportConfiguration config = new SimpleXlsxReportConfiguration();
                        config.setDetectCellType(true);     // Detección automática de tipos de celda
                        config.setCollapseRowSpan(false);   // Mantener spans de filas
                        exporter.setConfiguration(config);

                        // 7. Exportar
                        exporter.exportReport();
                        System.out.println("Reporte XLSX generado exitosamente!");
                }else if (_outputFormat.compareTo("DOCX")==0){
                        // Crear exportador DOCX
                        JRDocxExporter exporter = new JRDocxExporter();
                        String nombreArchivoSalidaEmpleado = 
                            OUTPUT_FOLDER + File.separator 
                                + reportType.getLabel() 
                                + "_" 
                                + parametros.get("RUT_TRABAJADOR") + ".docx";
                        System.out.println("[GeneradorReportes.generarReporte]DOCX-"
                            + "Add Archivo generado: " + nombreArchivoSalidaEmpleado);
                        generatedFiles.add(nombreArchivoSalidaEmpleado);
                        // Configurar entrada y salida del exportador
                        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(
                            new File(nombreArchivoSalidaEmpleado)
                        ));

                        // Exportar
                        exporter.exportReport();
                        System.out.println("Reporte exportado a DOCX correctamente.");
                }else if (_outputFormat.compareTo("RTF")==0){
                        JRRtfExporter exporter = new JRRtfExporter();
                        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                        exporter.setExporterOutput(new SimpleWriterExporterOutput(OUTPUT_FOLDER + File.separator 
                            + reportType.getLabel() 
                            + "_" 
                            + parametros.get("RUT_TRABAJADOR") + ".rtf"));
                        exporter.exportReport();
                        System.out.println("Reporte exportado a RTF correctamente.");
                        
                        // Crear exportador DOCX
//                        JRDocxExporter exporter = new JRDocxExporter();

                        // Configurar entrada y salida del exportador
//                        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
//                        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(
//                            new File(OUTPUT_FOLDER + File.separator 
//                                + reportType.getLabel() 
//                                + "_" 
//                                + parametros.get("RUT_TRABAJADOR") + ".docx")
//                        ));

                        // Exportar
//                        exporter.exportReport();

//                        System.out.println("Reporte exportado a DOCX correctamente.");
                }
            }
             // Cerrar conexión si aplica
            if (sourceDbConnection != null) sourceDbConnection.close();
            
            try {
                System.out.println("[GeneradorReportes.generarReporte]"
                    + "Comprimiendo archivos generados...");
                Utilidades.comprimirArchivos(generatedFiles, zipFilePath);
            } catch (IOException ex) {
                System.err.println("[GeneradorReportes.generarReporte]Error al comprimir archivos: " + ex.toString());
            }

        } catch (DatabaseException | JRException | ClassNotFoundException | SQLException ex3) {
            System.err.println("[GeneradorReportes.generarReporte]Error_1 obtener info para los reportes: " + ex3.toString());
            ex3.printStackTrace();
            
            zipFilePath = null;
        }catch(Exception ex4){
            System.err.println("[GeneradorReportes.generarReporte]"
                + "Error_2 obtener info para los reportes: " + ex4.toString());
        }
        
        return zipFilePath;
    }
}
