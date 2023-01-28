/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cl.femase.gestionweb.common.freemarker;

import cl.femase.gestionweb.vo.FreemarkerTemplateVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.ReportVO;
import com.itextpdf.html2pdf.HtmlConverter;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aledi
 */
public class PdfGenerator {

    Configuration CFG;
    Map<String, Object> TEMPLATE_DATA;
    Template TEMPLATE;
    String OUTPUT_PATH;
    FreemarkerTemplateVO TEMPLATE_VO;
    
    /**
    * 
     * @param _appProperties
     * @param _template
    */
    public PdfGenerator(PropertiesVO _appProperties, 
            FreemarkerTemplateVO _template) {
        try{
            System.err.println("[FemaseGestionWeb.freemarker.PdfGenerator]"
                + "Constructor. "
                + "Templates path: " + _appProperties.getFreemarkerTemplatesPath());
            
            CFG = new Configuration(Configuration.VERSION_2_3_29);
            CFG.setDirectoryForTemplateLoading(new File(_appProperties.getFreemarkerTemplatesPath()));
            // Recommended settings for new projects:
            CFG.setDefaultEncoding("UTF-8");
            CFG.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            CFG.setLogTemplateExceptions(false);
            CFG.setWrapUncheckedExceptions(true);
            CFG.setFallbackOnNullLoopVariable(false);

            /* You should do this ONLY ONCE in the whole application life-cycle:        */
            /* Create and adjust the configuration singleton */
            /* ------------------------------------------------------------------------ */
            /* You usually do these for MULTIPLE TIMES in the application life-cycle:   */
            Calendar currentCal = Calendar.getInstance(new Locale("es","CL"));
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
            String labelDate = sdf.format(currentCal.getTime());

            /* Get the template (uses cache internally) */
            TEMPLATE = CFG.getTemplate(_template.getTemplateName());
            
            /* Create a data-model */
            TEMPLATE_DATA   = new HashMap<>();
            TEMPLATE_DATA.put("date", labelDate);
            TEMPLATE_DATA.put("logo_path", _appProperties.getImagesPath() + File.separator + _template.getReportLogo());
            TEMPLATE_DATA.put("title", _template.getReportTitle());
            OUTPUT_PATH     = _appProperties.getPathExportedFiles();
            TEMPLATE_VO     = _template;
        }catch(IOException ioex){
            System.err.println("[FemaseGestionWeb.freemarker.PdfGenerator]"
                + "Error al inicializar freemarker template: " + ioex.toString());
            
        }
    }
 
    /**
    * 
    * @param _reportData
    * @return 
    */
    public String generateReport(ReportVO _reportData){
        TEMPLATE_DATA.put("header", _reportData.getHeader());
        TEMPLATE_DATA.put("details", _reportData.getDetalle());
        /* Merge data-model with template */
        //Freemaker genera un html en base al template utilizado.
        String outputHtmlFile = OUTPUT_PATH + File.separator + TEMPLATE_VO.getReportAbrev() + ".html";
        String outputPdfFile = OUTPUT_PATH + File.separator + TEMPLATE_VO.getReportAbrev() + ".pdf";
        System.err.println("[FemaseGestionWeb.freemarker.PdfGenerator.generateReport]"
            + "outputHtmlFile= " + outputHtmlFile
            +", outputPdfFile= " + outputPdfFile);
        
        Writer fileWriter=null;
        try {
            fileWriter = new FileWriter(new File(outputHtmlFile));
            TEMPLATE.process(TEMPLATE_DATA, fileWriter);
        }catch(IOException ioex){
            
        }catch(TemplateException tex){
            
        }
        finally {
            if (fileWriter != null) try {
                fileWriter.close();
            } catch (IOException ex) {
                System.err.println("[FemaseGestionWeb.freemarker."
                    + "PdfGenerator.generateReport]"
                    + "Error al escribir archivo de salida: " + ex.toString());
            }
        }

        try {
            //HtmlConverter.convertToPdf(new File(outputHtmlFile), new File(outputPdfFile));
            HtmlConverter.convertToPdf(new FileInputStream(outputHtmlFile), 
                new FileOutputStream(outputPdfFile));
        } catch (Exception e) {
            System.err.println("[FemaseGestionWeb.freemarker.PdfGenerator."
                + "generateReport]"
                + "Error al generar reporte: " + e.toString());
            e.printStackTrace();
        }
        
        return outputPdfFile;
    }
    
        
}
