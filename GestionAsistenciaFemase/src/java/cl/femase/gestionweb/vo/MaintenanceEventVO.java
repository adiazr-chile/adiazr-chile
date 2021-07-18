/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.vo;

import java.util.Date;

/**
 *
 * @author Alexander
 */
public class MaintenanceEventVO {
    private static final long serialVersionUID = 8526476594332776147L;
    
    private String username;
    private String type;
    private Date datetime;
    private String strdate;
    private String description;
    private String userIP;

    private String start_date;
    private String end_date;
    
    private String empresaId;
    private String deptoId;
    private int cencoId;
    private String rutEmpleado;
    private String empresaIdSource;
    
    private String operatingSystem;
    private String browserName;

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getBrowserName() {
        return browserName;
    }

    public void setBrowserName(String browserName) {
        this.browserName = browserName;
    }
    
    

    public String getEmpresaIdSource() {
        return empresaIdSource;
    }

    public void setEmpresaIdSource(String empresaIdSource) {
        this.empresaIdSource = empresaIdSource;
    }

    
    
    public String getEmpresaId() {
        return empresaId;
    }

    /**
     *
     * @param empresaId
     */
    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    /**
     *
     * @return
     */
    public String getDeptoId() {
        return deptoId;
    }

    public void setDeptoId(String deptoId) {
        this.deptoId = deptoId;
    }

    public int getCencoId() {
        return cencoId;
    }

    public void setCencoId(int cencoId) {
        this.cencoId = cencoId;
    }

    public String getRutEmpleado() {
        return rutEmpleado;
    }

    public void setRutEmpleado(String rutEmpleado) {
        this.rutEmpleado = rutEmpleado;
    }
    
    
    
    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    
    public String getStrdate() {
        return strdate;
    }

    public void setStrdate(String strdate) {
        this.strdate = strdate;
    }

    
    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return
     */
    public String getUserIP() {
        return userIP;
    }

    public void setUserIP(String userIP) {
        this.userIP = userIP;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
