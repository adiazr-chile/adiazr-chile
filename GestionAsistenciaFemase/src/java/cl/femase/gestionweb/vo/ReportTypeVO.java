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
public class ReportTypeVO implements Serializable{

    private static final long serialVersionUID = 7447742295698776220L;

    private final String jasperFileName;
    private final String label;

    public ReportTypeVO(String jasperFileName, String label) {
        this.jasperFileName = jasperFileName;
        this.label = label;
    }

    public String getJasperFileName() {
        return jasperFileName;
    }

    public String getLabel() {
        return label;
    }
    
    
    
}
