/**
 * @author Adiazr
 * @version 1.0 
 * 
 * 
 */

package cl.femase.gestionweb.common;

import cl.femase.gestionweb.vo.CargoVO;
import cl.femase.gestionweb.vo.DetalleAusenciaVO;
import cl.femase.gestionweb.vo.DetalleAsistenciaVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfCopyFields;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;

public class PdfUtilWriter {
   // private StringUtils strutil = new StringUtils();
    HttpSession m_session;
    
    public PdfUtilWriter(HttpSession _session) {
       m_session = _session;
    }
    
    public boolean doReporteEmpleados(List<EmpleadoVO> _lista,
            String _headerimagepath,
            String _textheader, 
            String _textfooter,
            String _outputfilename){
        boolean isOk=true;
        try {
            LinkedHashMap<String,CargoVO> listaCargos=
                new LinkedHashMap<>(); 
            HashMap<Integer,String> hashEstados=new HashMap();
            hashEstados.put(1, "Vigente");
            hashEstados.put(2, "No Vigente");
            
            List<CargoVO> cargos = (List<CargoVO>) m_session.getAttribute("cargos");
            Iterator<CargoVO> cargosIterator = cargos.iterator();
            while (cargosIterator.hasNext()) {
                CargoVO aux = cargosIterator.next();
                listaCargos.put("" + aux.getId(), aux);
            }
                
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            OutputStream file = new FileOutputStream(new File(_outputfilename));
            
            Document document = new Document(PageSize.A4.rotate());
            document.setMargins(36, 72, 50, 50);
            
            PdfWriter.getInstance(document, file);
            
            int num_colums =8;
            //Inserting Image in PDF
            Image image = Image.getInstance (_headerimagepath);
            image.scaleAbsolute(182f, 120f);//image width,height   
            image.setAlignment(Element.ALIGN_CENTER);
            // ********* encabezado archivo *************** ***************
            PdfPTable tableHeader  = new PdfPTable(1);
            tableHeader.setWidthPercentage(100);
            
            Font headerfont        = FontFactory.getFont("Arial",14,Font.BOLD);
            headerfont.setColor(0,0,24);
            
            PdfPCell cellBlank  = new PdfPCell (new Paragraph (" ",headerfont));
            cellBlank.setBorder(0);
            cellBlank.setNoWrap(true);
            cellBlank.setHorizontalAlignment(Element.ALIGN_CENTER);
            /**espacio para el logo */
            tableHeader.addCell(cellBlank);      
            tableHeader.addCell(cellBlank);      
            tableHeader.addCell(cellBlank);      
            tableHeader.addCell(cellBlank);      
            tableHeader.addCell(cellBlank);      
            /***/
            PdfPCell cellHeader          = new PdfPCell (new Paragraph ("REPORTE EMPLEADOS",headerfont));
            cellHeader.setBorder(0);
            cellHeader.setNoWrap(true);
            cellHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableHeader.addCell(cellHeader);   
            
            tableHeader.addCell(cellBlank);
            tableHeader.addCell(cellBlank);
            tableHeader.addCell(cellBlank);
            tableHeader.addCell(cellBlank);
                                    
            //Inserting Table con datos
            PdfPTable tabledata=new PdfPTable(num_colums);
            Font tableheaderfont        = FontFactory.getFont("Arial",10,Font.BOLD);
            tabledata.setWidthPercentage(100);
            float[] columnWidths = new float[] {30f, 45f, 20f, 45f, 40f, 40f, 40f, 15f};
            tabledata.setWidths(columnWidths);
            tableheaderfont.setColor(0, 0, 24);
            BaseColor headercolor=new BaseColor (255, 255, 255);

            String[] cellheaders=new String[8];
            cellheaders[0]="Rut";
            cellheaders[1]="Nombre";
            cellheaders[2]="Fec Nac";
            cellheaders[3]="Email";
            cellheaders[4]="Departamento";
            cellheaders[5]="Centro Costo";
            cellheaders[6]="Cargo";
            cellheaders[7]="Estado";
                      
            //seteando cell headers
            for (int i = 0; i < cellheaders.length; i++){
                tabledata.addCell(setPdfCellValue(cellheaders[i], 
                    tableheaderfont, 
                    headercolor, 
                    Element.ALIGN_CENTER,0));
            }

            //llenando tabla con los datos
            Font datafont=new Font();
            datafont.setColor(0, 0, 0);
            datafont.setSize(9f);
            BaseColor datacolor=new BaseColor (255,255,255);
            
            Font datafontbold = FontFactory.getFont("Arial",14,Font.BOLD);
            datafontbold.setColor(0, 0, 0);
            datafontbold.setSize(10f);
          
            /**Iterando las filas a mostrar*/
            String aux1=" ";
            String aux2=" ";
            String aux3=" ";
            String aux4=" ";
            String aux5=" ";
            String aux6=" ";
            String aux7=" ";
            String aux8=" ";
            Iterator<EmpleadoVO> it = _lista.iterator();
            while(it.hasNext()){
                aux1=" ";
                aux2=" ";
                aux3=" ";
                aux4=" ";
                aux5=" ";
                aux6="";
                aux7=" ";
                aux8=" ";
                
                EmpleadoVO rowdata = it.next();
                aux1=rowdata.getRut();
                aux2=rowdata.getNombreCompleto();
            
                aux3 = rowdata.getFechaNacimientoAsStr();
                aux4 = rowdata.getEmail();
                aux5 = rowdata.getDepartamento().getNombre();
                aux6 = rowdata.getCentroCosto().getNombre();
                System.err.println("[PdfUtilWriter."
                    + "doReporteEmpleados]"
                    + "cargoId: " + rowdata.getIdCargo());
                if (rowdata.getIdCargo() != -1){
                    aux7 = listaCargos.get(""+rowdata.getIdCargo()).getNombre();
                }
                aux8 = hashEstados.get(rowdata.getEstado());
                
                tabledata.addCell(setPdfCellValue(aux1, datafont, datacolor, Element.ALIGN_RIGHT,0));
                tabledata.addCell(setPdfCellValue(aux2, datafont, datacolor, Element.ALIGN_LEFT,0));
                tabledata.addCell(setPdfCellValue(aux3, datafont, datacolor, Element.ALIGN_LEFT,0));//
                tabledata.addCell(setPdfCellValue(aux4, datafont, datacolor, Element.ALIGN_LEFT,0));//
                tabledata.addCell(setPdfCellValue(aux5, datafont, datacolor, Element.ALIGN_LEFT,0));// 
                tabledata.addCell(setPdfCellValue(aux6, datafont, datacolor, Element.ALIGN_LEFT,0));// 
                tabledata.addCell(setPdfCellValue(aux7, datafont, datacolor, Element.ALIGN_LEFT,0));// 
                tabledata.addCell(setPdfCellValue(aux8, datafont, datacolor, Element.ALIGN_LEFT,0));// 
               
            }
            
            //fin datos de tabla
            tabledata.setSpacingBefore(10.0f);       // Space Before table starts, like margin-top in CSS
            tabledata.setSpacingAfter(10.0f);        // Space After table starts, like margin-Bottom in CSS                                        
            
            //**Font para textos del header y footer*/
            Font headerAndfooterFont        = FontFactory.getFont("Arial",11,Font.NORMAL);
            headerAndfooterFont.setColor(0,0,24);
                         
            //-------------- Texto encabezado del certificado ----------------
            String fullname = "";//_signer.getNombres() + " " + _signer.getApePaterno()+ " " + _signer.getApeMaterno();
            System.out.println("Signer Fullname: "+fullname);
            String headerText=_textheader;
           
            /** parseando texto header como html*/
            Paragraph headerParagraph=getHTMLText(headerText, headerAndfooterFont);
            
            PdfPTable tableTextHeader  = new PdfPTable(1);          
            PdfPCell cellTextHeader = new PdfPCell (headerParagraph);
            cellTextHeader.setBorder(0);
            cellTextHeader.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
            tableTextHeader.addCell(cellTextHeader);      
            tableTextHeader.setWidthPercentage(100);
            
            //-------------- Fin encabezado del certificado ----------------
            
            //-------------- Texto pie de pagina del certificado ----------------
            String footerText=_textfooter;
            Paragraph footerParagraph=getHTMLText(footerText, headerAndfooterFont);
            
            // pie de pagina
            PdfPTable tableTextFooter  = new PdfPTable(1);
            PdfPCell cellFooter = new PdfPCell (footerParagraph);
            cellFooter.setBorder(0);
            cellFooter.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
            tableTextFooter.addCell(cellFooter);      
            tableTextFooter.setWidthPercentage(100);
              
            /**Estampar fecha de emision del certificado*/
            Calendar mycal1 = Calendar.getInstance(new Locale("es","CL"));
            SimpleDateFormat sdfaux = new SimpleDateFormat("MMMM");	
            int theday      = mycal1.get(Calendar.DAY_OF_MONTH);
            int theyear     = mycal1.get(Calendar.YEAR);
            //String themonth = sdfaux.format(mycal1.getTime());
            String themonth= mycal1.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("es", "CL"));  
            String strLabelDate = "Santiago, "+theday+" de " +themonth+ " de " + theyear;
            Paragraph labelDate = new Paragraph(strLabelDate,headerAndfooterFont);
            
            /** Insertando todo en el PDF*/
            document.open();//PDF document opened........                
            document.add(image);
            document.add(Chunk.NEWLINE);
            document.add(tableHeader);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(tableTextHeader);
            document.add(tabledata);
            document.add(Chunk.NEWLINE);
            document.add(tableTextFooter);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            
            document.add(labelDate);
            
            document.newPage();
            
            document.close();
            file.close();
 
            System.out.println("Pdf created successfully..");
 
        } catch (Exception e) {
            isOk    = false;
            e.printStackTrace();
        }
        
        return isOk;
    }

    /**
     * Reporte de asistencia
     * 
     * @param _lista
     * @param _headerimagepath
     * @param _textheader
     * @param _textfooter
     * @param _outputfilename
     * 
     * @return 
     */
    public boolean doReporteAsistencia(List<DetalleAsistenciaVO> _lista,
            String _headerimagepath,
            String _textheader, 
            String _textfooter,
            String _outputfilename){
        boolean isOk=true;
        try {
            LinkedHashMap<String,CargoVO> listaCargos=
                new LinkedHashMap<>(); 
            HashMap<Integer,String> hashEstados=new HashMap();
            hashEstados.put(1, "Vigente");
            hashEstados.put(2, "No Vigente");
            
            List<CargoVO> cargos = (List<CargoVO>) m_session.getAttribute("cargos");
            Iterator<CargoVO> cargosIterator = cargos.iterator();
            while (cargosIterator.hasNext()) {
                CargoVO aux = cargosIterator.next();
                listaCargos.put("" + aux.getId(), aux);
            }
                
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            OutputStream file = new FileOutputStream(new File(_outputfilename));
            
            Document document = new Document(PageSize.A4.rotate());
            document.setMargins(36, 72, 50, 50);
            
            PdfWriter.getInstance(document, file);
            
            int num_colums =12;
            //Inserting Image in PDF
            Image image = Image.getInstance (_headerimagepath);
            image.scaleAbsolute(182f, 120f);//image width,height   
            image.setAlignment(Element.ALIGN_CENTER);
            // ********* encabezado archivo *************** ***************
            PdfPTable tableHeader  = new PdfPTable(1);
            tableHeader.setWidthPercentage(100);
            
            Font headerfont        = FontFactory.getFont("Arial",14,Font.BOLD);
            headerfont.setColor(0,0,24);
            
            PdfPCell cellBlank  = new PdfPCell (new Paragraph (" ",headerfont));
            cellBlank.setBorder(0);
            cellBlank.setNoWrap(true);
            cellBlank.setHorizontalAlignment(Element.ALIGN_CENTER);
            /**espacio para el logo */
            tableHeader.addCell(cellBlank);      
            tableHeader.addCell(cellBlank);      
            tableHeader.addCell(cellBlank);      
            tableHeader.addCell(cellBlank);      
            tableHeader.addCell(cellBlank);      
            /***/
            PdfPCell cellHeader          = new PdfPCell (new Paragraph ("REPORTE EMPLEADOS",headerfont));
            cellHeader.setBorder(0);
            cellHeader.setNoWrap(true);
            cellHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableHeader.addCell(cellHeader);   
            
            tableHeader.addCell(cellBlank);
            tableHeader.addCell(cellBlank);
            tableHeader.addCell(cellBlank);
            tableHeader.addCell(cellBlank);
                                    
            //Inserting Table con datos
            PdfPTable tabledata=new PdfPTable(num_colums);
            Font tableheaderfont        = FontFactory.getFont("Arial",10,Font.BOLD);
            tabledata.setWidthPercentage(100);
            float[] columnWidths = new float[] {20f, 20f, 20f, 20f, 20f, 20f, 
                                                20f, 20f, 20f, 20f, 20f, 20f};
            tabledata.setWidths(columnWidths);
            tableheaderfont.setColor(0, 0, 24);
            BaseColor headercolor=new BaseColor (255, 255, 255);

            String[] cellheaders = new String[num_colums];
            cellheaders[1]="Tinicio";
            cellheaders[2]="Tfin";
            cellheaders[3]="Fec.entrada";
            cellheaders[4]="R.entrada";
            cellheaders[5]="Fec.entrada";
            cellheaders[6]="R.salida";
            cellheaders[7]="Hteo";
            cellheaders[8]="Hreales";
            cellheaders[9]="Atraso";
            cellheaders[10]="He50";
            cellheaders[11]="He100";
            cellheaders[12]="Feriado";
            cellheaders[13]="Art22";
            
            //seteando cell headers
            for (int i = 0; i < cellheaders.length; i++){
                tabledata.addCell(setPdfCellValue(cellheaders[i], 
                    tableheaderfont, 
                    headercolor, 
                    Element.ALIGN_CENTER,0));
            }

            //llenando tabla con los datos
            Font datafont=new Font();
            datafont.setColor(0, 0, 0);
            datafont.setSize(9f);
            BaseColor datacolor=new BaseColor (255,255,255);
            
            Font datafontbold = FontFactory.getFont("Arial",14,Font.BOLD);
            datafontbold.setColor(0, 0, 0);
            datafontbold.setSize(10f);
          
            /**Iterando las filas a mostrar*/
            String aux1=" ";
            String aux2=" ";
            String aux3=" ";
            String aux4=" ";
            String aux5=" ";
            String aux6=" ";
            String aux7=" ";
            String aux8=" ";
            String aux9=" ";
            String aux10 =" ";
            String aux11 =" ";
            String aux12 =" ";
            String aux13 =" ";
            Iterator<DetalleAsistenciaVO> it = _lista.iterator();
            while(it.hasNext()){
                aux1=" ";
                aux2=" ";
                aux3=" ";
                aux4=" ";
                aux5=" ";
                aux6="";
                aux7=" ";
                aux8=" ";
                aux9=" ";
                aux10=" ";
                aux11=" ";
                aux12=" ";
                aux13=" ";
                
                /**
                    cellheaders[1]="Tinicio";
                    cellheaders[2]="Tfin";
                    cellheaders[3]="Fec.entrada";
                    cellheaders[4]="R.entrada";
                    cellheaders[5]="Fec.entrada";
                    cellheaders[6]="R.salida";
                    cellheaders[7]="Hteo";
                    cellheaders[8]="Hreales";
                    cellheaders[9]="Atraso";
                    cellheaders[10]="He50";
                    cellheaders[11]="He100";
                    cellheaders[12]="Feriado";
                    cellheaders[13]="Art22";
                 */
                DetalleAsistenciaVO rowdata = it.next();
                aux1=rowdata.getHoraEntradaTeorica();
                aux2=rowdata.getHoraSalidaTeorica();
                aux3=rowdata.getFechaEntradaMarca();
                aux4=rowdata.getHoraEntrada();
                aux5=rowdata.getFechaSalidaMarca();
                aux6=rowdata.getHoraSalida();
                aux7=""+rowdata.getMinutosTeoricos();
                aux8=""+rowdata.getMinutosReales();
                aux9=""+rowdata.getMinutosAtraso();
                aux10=""+rowdata.getMinutosExtrasAl50();
                aux11=""+rowdata.getMinutosExtrasAl100();
                aux12=""+rowdata.isEsFeriado();
                aux13=""+rowdata.isArt22();
                
                tabledata.addCell(setPdfCellValue(aux1, datafont, datacolor, Element.ALIGN_LEFT,0));
                tabledata.addCell(setPdfCellValue(aux2, datafont, datacolor, Element.ALIGN_LEFT,0));
                tabledata.addCell(setPdfCellValue(aux3, datafont, datacolor, Element.ALIGN_LEFT,0));//
                tabledata.addCell(setPdfCellValue(aux4, datafont, datacolor, Element.ALIGN_LEFT,0));//
                tabledata.addCell(setPdfCellValue(aux5, datafont, datacolor, Element.ALIGN_LEFT,0));// 
                tabledata.addCell(setPdfCellValue(aux6, datafont, datacolor, Element.ALIGN_LEFT,0));// 
                tabledata.addCell(setPdfCellValue(aux7, datafont, datacolor, Element.ALIGN_LEFT,0));// 
                tabledata.addCell(setPdfCellValue(aux8, datafont, datacolor, Element.ALIGN_LEFT,0));// 
                tabledata.addCell(setPdfCellValue(aux9, datafont, datacolor, Element.ALIGN_LEFT,0));
                tabledata.addCell(setPdfCellValue(aux10, datafont, datacolor, Element.ALIGN_LEFT,0));
                tabledata.addCell(setPdfCellValue(aux11, datafont, datacolor, Element.ALIGN_LEFT,0));
                tabledata.addCell(setPdfCellValue(aux12, datafont, datacolor, Element.ALIGN_LEFT,0));
            }
            
            //fin datos de tabla
            tabledata.setSpacingBefore(10.0f);       // Space Before table starts, like margin-top in CSS
            tabledata.setSpacingAfter(10.0f);        // Space After table starts, like margin-Bottom in CSS                                        
            
            //**Font para textos del header y footer*/
            Font headerAndfooterFont        = FontFactory.getFont("Arial",11,Font.NORMAL);
            headerAndfooterFont.setColor(0,0,24);
                         
            //-------------- Texto encabezado del certificado ----------------
            String fullname = "";//_signer.getNombres() + " " + _signer.getApePaterno()+ " " + _signer.getApeMaterno();
            System.out.println("Signer Fullname: "+fullname);
            String headerText=_textheader;
           
            /** parseando texto header como html*/
            Paragraph headerParagraph=getHTMLText(headerText, headerAndfooterFont);
            
            PdfPTable tableTextHeader  = new PdfPTable(1);          
            PdfPCell cellTextHeader = new PdfPCell (headerParagraph);
            cellTextHeader.setBorder(0);
            cellTextHeader.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
            tableTextHeader.addCell(cellTextHeader);      
            tableTextHeader.setWidthPercentage(100);
            
            //-------------- Fin encabezado del certificado ----------------
            
            //-------------- Texto pie de pagina del certificado ----------------
            String footerText=_textfooter;
            Paragraph footerParagraph=getHTMLText(footerText, headerAndfooterFont);
            
            // pie de pagina
            PdfPTable tableTextFooter  = new PdfPTable(1);
            PdfPCell cellFooter = new PdfPCell (footerParagraph);
            cellFooter.setBorder(0);
            cellFooter.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
            tableTextFooter.addCell(cellFooter);      
            tableTextFooter.setWidthPercentage(100);
              
            /**Estampar fecha de emision del certificado*/
            Calendar mycal1 = Calendar.getInstance(new Locale("es","CL"));
            SimpleDateFormat sdfaux = new SimpleDateFormat("MMMM");	
            int theday      = mycal1.get(Calendar.DAY_OF_MONTH);
            int theyear     = mycal1.get(Calendar.YEAR);
            //String themonth = sdfaux.format(mycal1.getTime());
            String themonth= mycal1.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("es", "CL"));  
            String strLabelDate = "Santiago, "+theday+" de " +themonth+ " de " + theyear;
            Paragraph labelDate = new Paragraph(strLabelDate,headerAndfooterFont);
            
            /** Insertando todo en el PDF*/
            document.open();//PDF document opened........                
            document.add(image);
            document.add(Chunk.NEWLINE);
            document.add(tableHeader);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(tableTextHeader);
            document.add(tabledata);
            document.add(Chunk.NEWLINE);
            document.add(tableTextFooter);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            
            document.add(labelDate);
            
            document.newPage();
            
            document.close();
            file.close();
 
            System.out.println("Pdf created successfully..");
 
        } catch (Exception e) {
            isOk    = false;
            e.printStackTrace();
        }
        
        return isOk;
    }
    
    public boolean doReporteOrganizacion(List<EmpleadoVO> _lista,
            String _headerimagepath,
            String _textheader, 
            String _textfooter,
            String _outputfilename){
        boolean isOk=true;
        try {
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            OutputStream file = new FileOutputStream(new File(_outputfilename));
            
            Document document = new Document(PageSize.A4.rotate());
            document.setMargins(36, 72, 50, 50);
            
            PdfWriter.getInstance(document, file);
            
            int num_colums =3;
            //Inserting Image in PDF
            Image image = Image.getInstance (_headerimagepath);
            image.scaleAbsolute(182f, 120f);//image width,height   
            image.setAlignment(Element.ALIGN_CENTER);
            // ********* encabezado archivo *************** ***************
            PdfPTable tableHeader  = new PdfPTable(1);
            tableHeader.setWidthPercentage(100);
            
            Font headerfont        = FontFactory.getFont("Arial",14,Font.BOLD);
            headerfont.setColor(0,0,24);
            
            PdfPCell cellBlank  = new PdfPCell (new Paragraph (" ",headerfont));
            cellBlank.setBorder(0);
            cellBlank.setNoWrap(true);
            cellBlank.setHorizontalAlignment(Element.ALIGN_CENTER);
            /**espacio para el logo */
           
            tableHeader.addCell(cellBlank);      
            tableHeader.addCell(cellBlank);      
            /***/
            PdfPCell cellHeader          = new PdfPCell (new Paragraph ("REPORTE ORGANIZACION",headerfont));
            cellHeader.setBorder(0);
            cellHeader.setNoWrap(true);
            cellHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableHeader.addCell(cellHeader);   
            
            tableHeader.addCell(cellBlank);
            tableHeader.addCell(cellBlank);
          
                                    
            //Inserting Table con datos
            PdfPTable tabledata=new PdfPTable(num_colums);
            Font tableheaderfont        = FontFactory.getFont("Arial",10,Font.BOLD);
            tabledata.setWidthPercentage(100);
            float[] columnWidths = new float[] {40f, 40f, 40f};
            tabledata.setWidths(columnWidths);
            tableheaderfont.setColor(0, 0, 24);
            BaseColor headercolor=new BaseColor (255, 255, 255);

            String[] cellheaders=new String[num_colums];
            cellheaders[0]="Empresa";
            cellheaders[1]="Departamento";
            cellheaders[2]="Centro Costo";
                      
            //seteando cell headers
            for (int i = 0; i < cellheaders.length; i++){
                tabledata.addCell(setPdfCellValue(cellheaders[i], 
                    tableheaderfont, 
                    headercolor, 
                    Element.ALIGN_CENTER,0));
            }

            //llenando tabla con los datos
            Font datafont=new Font();
            datafont.setColor(0, 0, 0);
            datafont.setSize(9f);
            BaseColor datacolor=new BaseColor (255,255,255);
            
            Font datafontbold = FontFactory.getFont("Arial",14,Font.BOLD);
            datafontbold.setColor(0, 0, 0);
            datafontbold.setSize(10f);
          
            /**Iterando las filas a mostrar*/
            String aux1=" ";
            String aux2=" ";
            String aux3=" ";
            Iterator<EmpleadoVO> it = _lista.iterator();
            while(it.hasNext()){
                aux1=" ";
                aux2=" ";
                aux3=" ";
                
                EmpleadoVO rowdata = it.next();
                aux1=rowdata.getEmpresaNombre();
                aux2=rowdata.getDeptoNombre();
                aux3 = rowdata.getCencoNombre(); 
               
                tabledata.addCell(setPdfCellValue(aux1, datafont, datacolor, Element.ALIGN_LEFT,0));
                tabledata.addCell(setPdfCellValue(aux2, datafont, datacolor, Element.ALIGN_LEFT,0));
                tabledata.addCell(setPdfCellValue(aux3, datafont, datacolor, Element.ALIGN_LEFT,0));//
            }
            
            //fin datos de tabla
            tabledata.setSpacingBefore(10.0f);       // Space Before table starts, like margin-top in CSS
            tabledata.setSpacingAfter(10.0f);        // Space After table starts, like margin-Bottom in CSS                                        
            
            //**Font para textos del header y footer*/
            Font headerAndfooterFont        = FontFactory.getFont("Arial",11,Font.NORMAL);
            headerAndfooterFont.setColor(0,0,24);
                         
            //-------------- Texto encabezado del certificado ----------------
            String fullname = "";//_signer.getNombres() + " " + _signer.getApePaterno()+ " " + _signer.getApeMaterno();
            System.out.println("Signer Fullname: "+fullname);
            String headerText=_textheader;
           
            /** parseando texto header como html*/
            Paragraph headerParagraph=getHTMLText(headerText, headerAndfooterFont);
            
            PdfPTable tableTextHeader  = new PdfPTable(1);          
            PdfPCell cellTextHeader = new PdfPCell (headerParagraph);
            cellTextHeader.setBorder(0);
            cellTextHeader.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
            tableTextHeader.addCell(cellTextHeader);      
            tableTextHeader.setWidthPercentage(100);
            
            //-------------- Fin encabezado del certificado ----------------
            
            //-------------- Texto pie de pagina del certificado ----------------
            String footerText=_textfooter;
            Paragraph footerParagraph=getHTMLText(footerText, headerAndfooterFont);
            
            // pie de pagina
            PdfPTable tableTextFooter  = new PdfPTable(1);
            PdfPCell cellFooter = new PdfPCell (footerParagraph);
            cellFooter.setBorder(0);
            cellFooter.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
            tableTextFooter.addCell(cellFooter);      
            tableTextFooter.setWidthPercentage(100);
              
            /**Estampar fecha de emision del certificado*/
            Calendar mycal1 = Calendar.getInstance(new Locale("es","CL"));
            SimpleDateFormat sdfaux = new SimpleDateFormat("MMMM");	
            int theday      = mycal1.get(Calendar.DAY_OF_MONTH);
            int theyear     = mycal1.get(Calendar.YEAR);
            //String themonth = sdfaux.format(mycal1.getTime());
            String themonth= mycal1.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("es", "CL"));  
            String strLabelDate = "Santiago, "+theday+" de " +themonth+ " de " + theyear;
            Paragraph labelDate = new Paragraph(strLabelDate,headerAndfooterFont);
            
            /** Insertando todo en el PDF*/
            document.open();//PDF document opened........                
            document.add(image);
            document.add(Chunk.NEWLINE);
            document.add(tableHeader);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(tableTextHeader);
            document.add(tabledata);
            document.add(Chunk.NEWLINE);
            document.add(tableTextFooter);
           
            document.add(Chunk.NEWLINE);
            
            document.add(labelDate);
            
            document.newPage();
            
            document.close();
            file.close();
 
            System.out.println("Pdf created successfully..");
 
        } catch (Exception e) {
            isOk    = false;
            e.printStackTrace();
        }
        
        return isOk;
    }
    
    public boolean doReporteUsuarios(List<UsuarioVO> _lista,
            String _headerimagepath,
            String _textheader, 
            String _textfooter,
            String _outputfilename){
        boolean isOk=true;
        try {
            HashMap<Integer,String> hashEstados=new HashMap();
            hashEstados.put(1, "Vigente");
            hashEstados.put(2, "No Vigente");
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            OutputStream file = new FileOutputStream(new File(_outputfilename));
            
            Document document = new Document(PageSize.A4.rotate());
            document.setMargins(36, 72, 50, 50);
            
            PdfWriter.getInstance(document, file);
            
            int num_colums =7;
            //Inserting Image in PDF
            Image image = Image.getInstance (_headerimagepath);
            image.scaleAbsolute(182f, 120f);//image width,height   
            image.setAlignment(Element.ALIGN_CENTER);
            // ********* encabezado archivo *************** ***************
            PdfPTable tableHeader  = new PdfPTable(1);
            tableHeader.setWidthPercentage(100);
            
            Font headerfont        = FontFactory.getFont("Arial",14,Font.BOLD);
            headerfont.setColor(0,0,24);
            
            PdfPCell cellBlank  = new PdfPCell (new Paragraph (" ",headerfont));
            cellBlank.setBorder(0);
            cellBlank.setNoWrap(true);
            cellBlank.setHorizontalAlignment(Element.ALIGN_CENTER);
            /**espacio para el logo */
            tableHeader.addCell(cellBlank);      
            tableHeader.addCell(cellBlank);      
            
            /***/
            PdfPCell cellHeader          = new PdfPCell (new Paragraph ("REPORTE USUARIOS",headerfont));
            cellHeader.setBorder(0);
            cellHeader.setNoWrap(true);
            cellHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableHeader.addCell(cellHeader);   
            
            tableHeader.addCell(cellBlank);
            tableHeader.addCell(cellBlank);
                                               
            //Inserting Table con datos
            PdfPTable tabledata=new PdfPTable(num_colums);
            Font tableheaderfont        = FontFactory.getFont("Arial",10,Font.BOLD);
            tabledata.setWidthPercentage(100);
            float[] columnWidths = new float[] {25f, 25f, 25f, 25f, 25f, 25f, 25f};
            tabledata.setWidths(columnWidths);
            tableheaderfont.setColor(0, 0, 24);
            BaseColor headercolor=new BaseColor (255, 255, 255);

            String[] cellheaders=new String[num_colums];
            cellheaders[0]="Username";
            cellheaders[1]="Nombres";
            cellheaders[2]="Ap Paterno";
            cellheaders[3]="Ap Materno";
            cellheaders[4]="Email";
            cellheaders[5]="Estado";
            cellheaders[6]="Perfil";
            
            //seteando cell headers
            for (int i = 0; i < cellheaders.length; i++){
                tabledata.addCell(setPdfCellValue(cellheaders[i], 
                    tableheaderfont, 
                    headercolor, 
                    Element.ALIGN_LEFT,0));
            }

            //llenando tabla con los datos
            Font datafont=new Font();
            datafont.setColor(0, 0, 0);
            datafont.setSize(9f);
            BaseColor datacolor=new BaseColor (255,255,255);
            
            Font datafontbold = FontFactory.getFont("Arial",14,Font.BOLD);
            datafontbold.setColor(0, 0, 0);
            datafontbold.setSize(10f);
          
            /**Iterando las filas a mostrar*/
            String aux1=" ";
            String aux2=" ";
            String aux3=" ";
            String aux4=" ";
            String aux5=" ";
            String aux6=" ";
            String aux7=" ";
            Iterator<UsuarioVO> it = _lista.iterator();
            while(it.hasNext()){
                aux1=" ";
                aux2=" ";
                aux3=" ";
                aux4=" ";
                aux5=" ";
                aux6=" ";
                aux7=" ";
                UsuarioVO rowdata = it.next();
                aux1 = rowdata.getUsername();
                aux2 = rowdata.getNombres();
                aux3 = rowdata.getApPaterno();
                aux4 = rowdata.getApMaterno();
                aux5 = rowdata.getEmail();
                aux6 = hashEstados.get(rowdata.getEstado());
                aux7 = rowdata.getNomPerfil();
                tabledata.addCell(setPdfCellValue(aux1, datafont, datacolor, Element.ALIGN_LEFT,0));
                tabledata.addCell(setPdfCellValue(aux2, datafont, datacolor, Element.ALIGN_LEFT,0));
                tabledata.addCell(setPdfCellValue(aux3, datafont, datacolor, Element.ALIGN_LEFT,0));
                tabledata.addCell(setPdfCellValue(aux4, datafont, datacolor, Element.ALIGN_LEFT,0));
                tabledata.addCell(setPdfCellValue(aux5, datafont, datacolor, Element.ALIGN_LEFT,0));
                tabledata.addCell(setPdfCellValue(aux6, datafont, datacolor, Element.ALIGN_LEFT,0));
                tabledata.addCell(setPdfCellValue(aux7, datafont, datacolor, Element.ALIGN_LEFT,0));
                
            }
            
            //fin datos de tabla
            tabledata.setSpacingBefore(10.0f);       // Space Before table starts, like margin-top in CSS
            tabledata.setSpacingAfter(10.0f);        // Space After table starts, like margin-Bottom in CSS                                        
            
            //**Font para textos del header y footer*/
            Font headerAndfooterFont        = FontFactory.getFont("Arial",11,Font.NORMAL);
            headerAndfooterFont.setColor(0,0,24);
                         
            //-------------- Texto encabezado del certificado ----------------
            
            String headerText=_textheader;
           
            /** parseando texto header como html*/
            Paragraph headerParagraph=getHTMLText(headerText, headerAndfooterFont);
            
            PdfPTable tableTextHeader  = new PdfPTable(1);          
            PdfPCell cellTextHeader = new PdfPCell (headerParagraph);
            cellTextHeader.setBorder(0);
            cellTextHeader.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
            tableTextHeader.addCell(cellTextHeader);      
            tableTextHeader.setWidthPercentage(100);
            
            //-------------- Fin encabezado del certificado ----------------
            
            //-------------- Texto pie de pagina del certificado ----------------
            String footerText=_textfooter;
            Paragraph footerParagraph=getHTMLText(footerText, headerAndfooterFont);
            
            // pie de pagina
            PdfPTable tableTextFooter  = new PdfPTable(1);
            PdfPCell cellFooter = new PdfPCell (footerParagraph);
            cellFooter.setBorder(0);
            cellFooter.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
            tableTextFooter.addCell(cellFooter);      
            tableTextFooter.setWidthPercentage(100);
              
            /**Estampar fecha de emision del certificado*/
            Calendar mycal1 = Calendar.getInstance(new Locale("es","CL"));
            SimpleDateFormat sdfaux = new SimpleDateFormat("MMMM");	
            int theday      = mycal1.get(Calendar.DAY_OF_MONTH);
            int theyear     = mycal1.get(Calendar.YEAR);
            //String themonth = sdfaux.format(mycal1.getTime());
            String themonth= mycal1.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("es", "CL"));  
            String strLabelDate = "Santiago, "+theday+" de " +themonth+ " de " + theyear;
            Paragraph labelDate = new Paragraph(strLabelDate,headerAndfooterFont);
            
            /** Insertando todo en el PDF*/
            document.open();//PDF document opened........                
            document.add(image);
            document.add(Chunk.NEWLINE);
            document.add(tableHeader);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(tableTextHeader);
            document.add(tabledata);
            document.add(Chunk.NEWLINE);
            document.add(tableTextFooter);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            
            document.add(labelDate);
            
            document.newPage();
            
            document.close();
            file.close();
 
            System.out.println("Pdf created successfully..");
 
        } catch (Exception e) {
            isOk    = false;
            e.printStackTrace();
        }
        
        return isOk;
    }
    
    public boolean doReporteDetalleAusencias(List<DetalleAusenciaVO> _lista,
            String _headerimagepath,
            String _textheader, 
            String _textfooter,
            String _outputfilename){
        boolean isOk=true;
        try {
            HashMap<Integer,String> hashEstados=new HashMap();
            hashEstados.put(1, "Vigente");
            hashEstados.put(2, "No Vigente");
            
           
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            OutputStream file = new FileOutputStream(new File(_outputfilename));
            
            Document document = new Document(PageSize.A4.rotate());
            document.setMargins(36, 72, 50, 50);
            
            PdfWriter.getInstance(document, file);
            
            int num_colums =11;
            //Inserting Image in PDF
            Image image = Image.getInstance (_headerimagepath);
            image.scaleAbsolute(182f, 120f);//image width,height   
            image.setAlignment(Element.ALIGN_CENTER);
            // ********* encabezado archivo *************** ***************
            PdfPTable tableHeader  = new PdfPTable(1);
            tableHeader.setWidthPercentage(100);
            
            Font headerfont        = FontFactory.getFont("Arial",14,Font.BOLD);
            headerfont.setColor(0,0,24);
            
            PdfPCell cellBlank  = new PdfPCell (new Paragraph (" ",headerfont));
            cellBlank.setBorder(0);
            cellBlank.setNoWrap(true);
            cellBlank.setHorizontalAlignment(Element.ALIGN_CENTER);
            /**espacio para el logo */
            tableHeader.addCell(cellBlank);      
            tableHeader.addCell(cellBlank);      
            
            /***/
            PdfPCell cellHeader          = new PdfPCell (new Paragraph ("REPORTE DETALLE AUSENCIAS",headerfont));
            cellHeader.setBorder(0);
            cellHeader.setNoWrap(true);
            cellHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableHeader.addCell(cellHeader);   
            
            tableHeader.addCell(cellBlank);
            tableHeader.addCell(cellBlank);
                                               
            //Inserting Table con datos
            PdfPTable tabledata=new PdfPTable(num_colums);
            Font tableheaderfont        = FontFactory.getFont("Arial",9,Font.BOLD);
            tabledata.setWidthPercentage(100);
            float[] columnWidths = new float[] {15f, 15f, 15f, 15f, 
                15f, 15f, 15f, 15f, 15f, 15f, 15f};
            tabledata.setWidths(columnWidths);
            tableheaderfont.setColor(0, 0, 24);
            BaseColor headercolor=new BaseColor (255, 255, 255);

            String[] cellheaders=new String[num_colums];
            cellheaders[0] = "Rut";
            cellheaders[1] = "Nombre";
            cellheaders[2] = "Fec. Ingreso";
            cellheaders[3] = "Tipo Ausencia";
            cellheaders[4] = "Ausencia por hora";
            cellheaders[5] = "Fecha inicio";
            cellheaders[6] = "Hora inicio";//hh:mm
            cellheaders[7] = "Fecha fin";
            cellheaders[8] = "Hora termino";//hh:mm
            cellheaders[9] = "Rut Autorizador";
            cellheaders[10] = "Autorizada";
            
            //seteando cell headers
            for (int i = 0; i < cellheaders.length; i++){
                tabledata.addCell(setPdfCellValue(cellheaders[i], 
                    tableheaderfont, 
                    headercolor, 
                    Element.ALIGN_LEFT,0));
            }

            //llenando tabla con los datos
            Font datafont=new Font();
            datafont.setColor(0, 0, 0);
            datafont.setSize(9f);
            BaseColor datacolor=new BaseColor (255,255,255);
            
            Font datafontbold = FontFactory.getFont("Arial",14,Font.BOLD);
            datafontbold.setColor(0, 0, 0);
            datafontbold.setSize(10f);
          
            /**Iterando las filas a mostrar*/
            String aux1=" ";
            String aux2=" ";
            String aux3=" ";
            String aux4=" ";
            String aux5=" ";
            String aux6=" ";
            String aux7=" ";
            String aux8=" ";
            String aux9=" ";
            String aux10=" ";
            String aux11=" ";
            Iterator<DetalleAusenciaVO> it = _lista.iterator();
            while(it.hasNext()){
                aux1=" ";
                aux2=" ";
                aux3=" ";
                aux4=" ";
                aux5=" ";
                aux6=" ";
                aux7=" ";
                aux8=" ";
                aux9=" ";
                aux10=" ";
                aux11=" ";
                DetalleAusenciaVO rowdata = it.next();
                aux1 = rowdata.getRutEmpleado();
                aux2 = rowdata.getNombreEmpleado();
                aux3 = rowdata.getFechaIngresoAsStr();
                aux4 = rowdata.getNombreAusencia();
                aux5 = rowdata.getPermiteHora();
                aux6 = rowdata.getFechaInicioAsStr();
                aux7 = rowdata.getHoraInicioFullAsStr();
                aux8 = rowdata.getFechaFinAsStr();
                aux9 = rowdata.getHoraFinFullAsStr();
                aux10 = rowdata.getRutAutorizador();
                aux11 = rowdata.getAusenciaAutorizada();
                
                tabledata.addCell(setPdfCellValue(aux1, datafont, datacolor, Element.ALIGN_LEFT,0));
                tabledata.addCell(setPdfCellValue(aux2, datafont, datacolor, Element.ALIGN_LEFT,0));
                tabledata.addCell(setPdfCellValue(aux3, datafont, datacolor, Element.ALIGN_LEFT,0));
                tabledata.addCell(setPdfCellValue(aux4, datafont, datacolor, Element.ALIGN_LEFT,0));
                tabledata.addCell(setPdfCellValue(aux5, datafont, datacolor, Element.ALIGN_LEFT,0));
                tabledata.addCell(setPdfCellValue(aux6, datafont, datacolor, Element.ALIGN_LEFT,0));
                tabledata.addCell(setPdfCellValue(aux7, datafont, datacolor, Element.ALIGN_LEFT,0));
                tabledata.addCell(setPdfCellValue(aux8, datafont, datacolor, Element.ALIGN_LEFT,0));
                tabledata.addCell(setPdfCellValue(aux9, datafont, datacolor, Element.ALIGN_LEFT,0));
                tabledata.addCell(setPdfCellValue(aux10, datafont, datacolor, Element.ALIGN_LEFT,0));
                tabledata.addCell(setPdfCellValue(aux11, datafont, datacolor, Element.ALIGN_LEFT,0));
                
            }
            
            //fin datos de tabla
            tabledata.setSpacingBefore(10.0f);       // Space Before table starts, like margin-top in CSS
            tabledata.setSpacingAfter(10.0f);        // Space After table starts, like margin-Bottom in CSS                                        
            
            //**Font para textos del header y footer*/
            Font headerAndfooterFont        = FontFactory.getFont("Arial",11,Font.NORMAL);
            headerAndfooterFont.setColor(0,0,24);
                         
            //-------------- Texto encabezado del certificado ----------------
            
            String headerText=_textheader;
           
            /** parseando texto header como html*/
            Paragraph headerParagraph=getHTMLText(headerText, headerAndfooterFont);
            
            PdfPTable tableTextHeader  = new PdfPTable(1);          
            PdfPCell cellTextHeader = new PdfPCell (headerParagraph);
            cellTextHeader.setBorder(0);
            cellTextHeader.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
            tableTextHeader.addCell(cellTextHeader);      
            tableTextHeader.setWidthPercentage(100);
            
            //-------------- Fin encabezado del certificado ----------------
            
            //-------------- Texto pie de pagina del certificado ----------------
            String footerText=_textfooter;
            Paragraph footerParagraph=getHTMLText(footerText, headerAndfooterFont);
            
            // pie de pagina
            PdfPTable tableTextFooter  = new PdfPTable(1);
            PdfPCell cellFooter = new PdfPCell (footerParagraph);
            cellFooter.setBorder(0);
            cellFooter.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
            tableTextFooter.addCell(cellFooter);      
            tableTextFooter.setWidthPercentage(100);
              
            /**Estampar fecha de emision del certificado*/
            Calendar mycal1 = Calendar.getInstance(new Locale("es","CL"));
            SimpleDateFormat sdfaux = new SimpleDateFormat("MMMM");	
            int theday      = mycal1.get(Calendar.DAY_OF_MONTH);
            int theyear     = mycal1.get(Calendar.YEAR);
            //String themonth = sdfaux.format(mycal1.getTime());
            String themonth= mycal1.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("es", "CL"));  
            String strLabelDate = "Santiago, "+theday+" de " +themonth+ " de " + theyear;
            Paragraph labelDate = new Paragraph(strLabelDate,headerAndfooterFont);
            
            /** Insertando todo en el PDF*/
            document.open();//PDF document opened........                
            document.add(image);
            document.add(Chunk.NEWLINE);
            document.add(tableHeader);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(tableTextHeader);
            document.add(tabledata);
            document.add(Chunk.NEWLINE);
            document.add(tableTextFooter);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            
            document.add(labelDate);
            
            document.newPage();
            
            document.close();
            file.close();
 
            System.out.println("Pdf created successfully..");
 
        } catch (Exception e) {
            isOk    = false;
            e.printStackTrace();
        }
        
        return isOk;
    }
    
   /**
     * Setea un valor string en una celda. 
     * Los valores que contienen texto simpre, se alinean a la izquierda (Element.ALIGN_LEFT)
     * Los valores que contienen numeros simpre, se alinean a la derecha (Element.ALIGN_RIGHT)
    */
    private static PdfPCell setPdfCellValue(
            String _value, 
            Font _thefont, 
            BaseColor _color, 
            int _alignment,
            int _colspan){
        
        PdfPCell cell = new PdfPCell (new Paragraph(_value,_thefont));
        cell.setBackgroundColor (_color);
        cell.setHorizontalAlignment(_alignment);
        cell.setBorder(0);
        cell.setColspan(_colspan);
        
        return cell;
    }
    
    /**
    * retorna texto parseado como html
    * @param _text
    * @param _font
    * @return 
    */
    public Paragraph getHTMLText(String _text, Font _font){
        Paragraph auxParagraph=new Paragraph();
        try {
            StringReader strReader = new StringReader(_text);
            java.util.List p = HTMLWorker.parseToList(strReader, null);
            auxParagraph.setFont(_font);
            for (int k = 0; k < p.size(); ++k){
                auxParagraph.add((Element)p.get(k));
            }
        } catch (IOException ex) {
            Logger.getLogger(PdfUtilWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return auxParagraph;
    }
    
    public boolean concatPDFFiles(List<String> listOfFiles,
        String outputfilepath) throws FileNotFoundException, DocumentException {
        PdfCopyFields copy = null;
        try {
            copy = new PdfCopyFields(new FileOutputStream(outputfilepath));
        } catch (DocumentException ex) {
            System.err.println("Error al crear archivo de salida: "+ex.toString());
        }
        try {
            for (String fileName : listOfFiles) {
                PdfReader reader1 = new PdfReader(fileName);
                copy.addDocument(reader1);
            }
        } catch (IOException ex) {
            System.err.println("Error al leer archivos: "+ex.toString());
        } finally {
            copy.close();
        }
        if (new File(outputfilepath).exists()) {
            double bytes = new File(outputfilepath).length();
            //double kilobytes = (bytes / 1024);
            if (bytes != 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}  