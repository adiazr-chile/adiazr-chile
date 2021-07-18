/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.vo;

import java.util.ArrayList;

/**
 *
 * @author Alexander
 */
public class ReportVO {
    private ReportHeaderVO header;
    private ReportDetailHeaderVO headersDetail;
    private ArrayList<ReportDetailVO> detalle;

    public ReportDetailHeaderVO getHeadersDetail() {
        return headersDetail;
    }

    public void setHeadersDetail(ReportDetailHeaderVO headersDetail) {
        this.headersDetail = headersDetail;
    }

    public ReportHeaderVO getHeader() {
        return header;
    }

    public void setHeader(ReportHeaderVO header) {
        this.header = header;
    }

    public ArrayList<ReportDetailVO> getDetalle() {
        return detalle;
    }

    public void setDetalle(ArrayList<ReportDetailVO> detalle) {
        this.detalle = detalle;
    }
    
}
