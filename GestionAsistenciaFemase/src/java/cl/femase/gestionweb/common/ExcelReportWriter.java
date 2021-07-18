/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.common;

import cl.femase.gestionweb.vo.ReportDetailHeaderVO;
import cl.femase.gestionweb.vo.ReportDetailVO;
import cl.femase.gestionweb.vo.ReportHeaderVO;
import cl.femase.gestionweb.vo.ReportVO;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author Alexander
 */
public class ExcelReportWriter {
    
    /**
    * 
    * @param _infoReporte
    * @param _excelFilePath
     * @param _reportType
    * @throws java.io.IOException
    */
    public void writeReportInExcel(ReportVO _infoReporte, 
            String _excelFilePath, 
            String _reportType) throws IOException {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        int rowCount = 0;
        //Row row = sheet.createRow(++rowCount);
        rowCount = writeHeaders(_infoReporte.getHeader(), sheet);
        
        sheet.createRow(++rowCount);
        Row rowHeadersDetail = sheet.createRow(++rowCount);
        
        //escribe cabeceras del detalle
        writeDetailHeaders(_infoReporte.getHeadersDetail(), 
            rowHeadersDetail, 
            _reportType);
        
        //escribe celdas con el detalle de los datos
        for (ReportDetailVO aDetail : _infoReporte.getDetalle()) {
            Row rowDetail = sheet.createRow(++rowCount);
            writeDetail(aDetail, rowDetail,_reportType);
        }

        try (FileOutputStream outputStream = new FileOutputStream(_excelFilePath)) {
            workbook.write(outputStream);
        }
    }
    
    /**
    * 
    */
    private int writeHeaders(ReportHeaderVO _aHeader, Sheet _sheet) {
        int rowCount=0;
        Row row = _sheet.createRow(++rowCount);
        Cell cell = row.createCell(0);
        cell.setCellValue(_aHeader.getReportLabelHeader());
        cell = row.createCell(1);
        cell.setCellValue(_aHeader.getReportLabel());
        
        row = _sheet.createRow(++rowCount);
        cell = row.createCell(0);
        cell.setCellValue(_aHeader.getFechaReporteHeader());
        cell = row.createCell(1);
        cell.setCellValue(_aHeader.getFechaReporte());

        row = _sheet.createRow(++rowCount);
        cell = row.createCell(0);
        cell.setCellValue(_aHeader.getRutTrabajadorHeader());
        cell = row.createCell(1);
        cell.setCellValue(_aHeader.getRutTrabajador());
        
        row = _sheet.createRow(++rowCount);
        cell = row.createCell(0);
        cell.setCellValue(_aHeader.getNombreTrabajadorHeader());
        cell = row.createCell(1);
        cell.setCellValue(_aHeader.getNombreTrabajador());
        
        row = _sheet.createRow(++rowCount);
        cell = row.createCell(0);
        cell.setCellValue(_aHeader.getEmpresaHeader());
        cell = row.createCell(1);
        cell.setCellValue(_aHeader.getEmpresaLabel());
        
        row = _sheet.createRow(++rowCount);
        cell = row.createCell(0);
        cell.setCellValue(_aHeader.getDeptoHeader());
        cell = row.createCell(1);
        cell.setCellValue(_aHeader.getDeptoLabel());
        
        row = _sheet.createRow(++rowCount);
        cell = row.createCell(0);
        cell.setCellValue(_aHeader.getCencoHeader());
        cell = row.createCell(1);
        cell.setCellValue(_aHeader.getCencoLabel());
        
        row = _sheet.createRow(++rowCount);
        cell = row.createCell(0);
        cell.setCellValue(_aHeader.getFechaInicioHeader());
        cell = row.createCell(1);
        cell.setCellValue(_aHeader.getFechaInicio());
        
        row = _sheet.createRow(++rowCount);
        cell = row.createCell(0);
        cell.setCellValue(_aHeader.getFechaFinHeader());
        cell = row.createCell(1);
        cell.setCellValue(_aHeader.getFechaFin());
        
        return rowCount;
    }
    
    /**
    * 
    */
    private void writeDetail(ReportDetailVO _aDetail, 
            Row row,
            String _reportType) {
        int rowCount=0;
        
        Cell cell = row.createCell(0);
        cell.setCellValue(_aDetail.getFecha());

        if (_reportType.compareTo("JORNADA") == 0){
            cell = row.createCell(++rowCount);
            cell.setCellValue(_aDetail.getLabelTurno());

            cell = row.createCell(++rowCount);
            cell.setCellValue(_aDetail.getHorasTrabajadas());
        }else if (_reportType.compareTo("FESTIVOS") == 0){
                cell = row.createCell(++rowCount);
                cell.setCellValue(_aDetail.getHoraEntrada());

                cell = row.createCell(++rowCount);
                cell.setCellValue(_aDetail.getHoraSalida());
                
                cell = row.createCell(++rowCount);
                cell.setCellValue(_aDetail.getObservacion());
        }else if (_reportType.compareTo("HRSEXTRAS") == 0){
            
                cell = row.createCell(++rowCount);
                cell.setCellValue(_aDetail.getLabelTurno());
                
                cell = row.createCell(++rowCount);
                cell.setCellValue(_aDetail.getHoraEntrada());

                cell = row.createCell(++rowCount);
                cell.setCellValue(_aDetail.getHoraSalida());
                
                cell = row.createCell(++rowCount);
                cell.setCellValue(_aDetail.getPresencia());
                
                cell = row.createCell(++rowCount);
                cell.setCellValue(_aDetail.getHorasExtras());
        }else if (_reportType.compareTo("ASISTENCIA") == 0){
                cell = row.createCell(++rowCount);
                cell.setCellValue(_aDetail.getAsistencia());

                cell = row.createCell(++rowCount);
                cell.setCellValue(_aDetail.getJustificacion());
        }else if (_reportType.compareTo("ASIGNACION_TURNOS") == 0){
                cell = row.createCell(++rowCount);
                cell.setCellValue(_aDetail.getHorario());

                cell = row.createCell(++rowCount);
                cell.setCellValue(_aDetail.getFechaAsignacionTurno());
                
                cell = row.createCell(++rowCount);
                cell.setCellValue(_aDetail.getNuevoHorario());
                
                cell = row.createCell(++rowCount);
                cell.setCellValue(_aDetail.getDescripcion());
                
        }
    }
    
    private void writeDetailHeaders(ReportDetailHeaderVO _aDetailHeader, 
            Row row, String _reportType) {
        int rowCount=0;
        
        Cell cell = row.createCell(0);
        cell.setCellValue(_aDetailHeader.getFechaHeader());

        if (_reportType.compareTo("JORNADA") == 0){
            cell = row.createCell(++rowCount);
            cell.setCellValue(_aDetailHeader.getLabelTurnoHeader());

            cell = row.createCell(++rowCount);
            cell.setCellValue(_aDetailHeader.getHorasTrabajadasHeader());
        }else if (_reportType.compareTo("FESTIVOS") == 0){
            cell = row.createCell(++rowCount);
            cell.setCellValue(_aDetailHeader.getHoraEntradaHeader());

            cell = row.createCell(++rowCount);
            cell.setCellValue(_aDetailHeader.getHoraSalidaHeader());
            
            cell = row.createCell(++rowCount);
            cell.setCellValue(_aDetailHeader.getObservacionHeader());
        }else if (_reportType.compareTo("HRSEXTRAS") == 0){
            cell = row.createCell(++rowCount);
            cell.setCellValue(_aDetailHeader.getLabelTurnoHeader());
            
            cell = row.createCell(++rowCount);
            cell.setCellValue(_aDetailHeader.getHoraEntradaHeader());

            cell = row.createCell(++rowCount);
            cell.setCellValue(_aDetailHeader.getHoraSalidaHeader());
            
            cell = row.createCell(++rowCount);
            cell.setCellValue(_aDetailHeader.getPresenciaHeader());
            
            cell = row.createCell(++rowCount);
            cell.setCellValue(_aDetailHeader.getHorasExtrasHeader());
        }else if (_reportType.compareTo("ASISTENCIA") == 0){
            cell = row.createCell(++rowCount);
            cell.setCellValue(_aDetailHeader.getAsistenciaHeader());

            cell = row.createCell(++rowCount);
            cell.setCellValue(_aDetailHeader.getJustificacionesHeader());
        }else if (_reportType.compareTo("ASIGNACION_TURNOS") == 0){
            cell = row.createCell(++rowCount);
            cell.setCellValue(_aDetailHeader.getHorarioHeader());

            cell = row.createCell(++rowCount);
            cell.setCellValue(_aDetailHeader.getFechaAsignacionTurnoHeader());
            
            cell = row.createCell(++rowCount);
            cell.setCellValue(_aDetailHeader.getNuevoHorarioHeader());
            
            cell = row.createCell(++rowCount);
            cell.setCellValue(_aDetailHeader.getDescripcionHeader());
            
        } 
        
    }
}
