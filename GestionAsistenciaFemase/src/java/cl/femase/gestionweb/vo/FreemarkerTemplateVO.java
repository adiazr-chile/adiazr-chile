/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.vo;

import java.io.Serializable;

/**
 *
 * @author Alexander
 */
public class FreemarkerTemplateVO implements Serializable{

    private static final long serialVersionUID = 4459692295698776220L;

    private String reportAbrev;
    private String reportTitle;
    private String reportLogo;
    private String templateName;
    
    public FreemarkerTemplateVO() {
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getReportAbrev() {
        return reportAbrev;
    }

    public void setReportAbrev(String reportAbrev) {
        this.reportAbrev = reportAbrev;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public String getReportLogo() {
        return reportLogo;
    }

    public void setReportLogo(String reportLogo) {
        this.reportLogo = reportLogo;
    }
    
    

        

}
