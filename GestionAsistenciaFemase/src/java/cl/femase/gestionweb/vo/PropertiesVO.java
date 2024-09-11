/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.vo;

import java.util.HashMap;

/**
 *
 * @author Alexander
 */
public class PropertiesVO {

    //Endpoint del webservice calendario
    //private String calendarWSEndPoint;
    //Envio de correos desde la aplicacion
    private String mailHost;
    private String mailPort;
    private String mailFrom;
    private String[] mailTo;//destinatarios
    private String[] mailToOperaciones;//destinatarios area operaciones BEC
    private String mailSubject;
    private String mailBody;
    private String mailUsername;
    private String mailPassword;
    
    private String dbPoolName;
    private String pathExportedFiles;
    private String imagesPath;
    private String uploadsPath;
    private String reportesPath;
    
    private String version;
    private String startYear;
    private String currentYear;

    HashMap<String, Double> parametrosSistema= new HashMap<>();

    private String freemarkerTemplatesPath;

    private boolean vacacionesPeriodos = false;

    public boolean isVacacionesPeriodos() {
        return vacacionesPeriodos;
    }

    public void setVacacionesPeriodos(boolean vacacionesPeriodos) {
        this.vacacionesPeriodos = vacacionesPeriodos;
    }
    
    public String getFreemarkerTemplatesPath() {
        return freemarkerTemplatesPath;
    }

    public void setFreemarkerTemplatesPath(String freemarkerTemplatesPath) {
        this.freemarkerTemplatesPath = freemarkerTemplatesPath;
    }
    
    public HashMap<String, Double> getParametrosSistema() {
        return parametrosSistema;
    }

    public void setParametrosSistema(HashMap<String, Double> parametrosSistema) {
        this.parametrosSistema = parametrosSistema;
    }
    
    public String getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(String currentYear) {
        this.currentYear = currentYear;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStartYear() {
        return startYear;
    }

    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }
    
    public String getMailUsername() {
        return mailUsername;
    }

    /**
     *
     * @param mailUsername
     */
    public void setMailUsername(String mailUsername) {
        this.mailUsername = mailUsername;
    }

    public String getMailPassword() {
        return mailPassword;
    }

    public void setMailPassword(String mailPassword) {
        this.mailPassword = mailPassword;
    }

    /**
     *
     * @return
     */
    public String getReportesPath() {
        return reportesPath;
    }

    public void setReportesPath(String reportesPath) {
        this.reportesPath = reportesPath;
    }
    
    public String getUploadsPath() {
        return uploadsPath;
    }

    public void setUploadsPath(String uploadsPath) {
        this.uploadsPath = uploadsPath;
    }

    public String getDbPoolName() {
        return dbPoolName;
    }

    public void setDbPoolName(String dbPoolName) {
        this.dbPoolName = dbPoolName;
    }

    
    
    
    public String getImagesPath() {
        return imagesPath;
    }

    /**
     *
     * @param imagesPath
     */
    public void setImagesPath(String imagesPath) {
        this.imagesPath = imagesPath;
    }

    public String[] getMailToOperaciones() {
        return mailToOperaciones;
    }

    public void setMailToOperaciones(String[] mailToOperaciones) {
        this.mailToOperaciones = mailToOperaciones;
    }

   
    public String getPathExportedFiles() {
        return pathExportedFiles;
    }

    public void setPathExportedFiles(String pathExportedFiles) {
        this.pathExportedFiles = pathExportedFiles;
    }

    

    public String getMailBody() {
        return mailBody;
    }

    public void setMailBody(String mailBody) {
        this.mailBody = mailBody;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public String getMailHost() {
        return mailHost;
    }

    public void setMailHost(String mailHost) {
        this.mailHost = mailHost;
    }

    /**
     *
     * @return
     */
    public String getMailPort() {
        return mailPort;
    }

    public void setMailPort(String mailPort) {
        this.mailPort = mailPort;
    }

    public String getMailSubject() {
        return mailSubject;
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public String[] getMailTo() {
        return mailTo;
    }

    public void setMailTo(String[] mailTo) {
        this.mailTo = mailTo;
    }

}
