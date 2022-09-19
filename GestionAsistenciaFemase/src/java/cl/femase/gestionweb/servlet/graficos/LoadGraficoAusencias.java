/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.servlet.graficos;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.dao.GraficosDAO;
import cl.femase.gestionweb.vo.GraficoAusenciaVO;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Alexander
 */
@WebServlet(name = "LoadGraficoAusencias", urlPatterns = {"/LoadGraficoAusencias"})
public class LoadGraficoAusencias extends BaseServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            List empdetails = new LinkedList();
            JSONObject responseObj = new JSONObject();
            JSONObject empObj = null;
            SimpleDateFormat fechaFormat    = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat anioMesFormat    = new SimpleDateFormat("yyyy-MM");
            SimpleDateFormat hhmmFormat     = new SimpleDateFormat("HH:mm");
            Calendar calendarHoy = Calendar.getInstance();
            GraficosDAO graficosdao = new GraficosDAO();
            String paramAction = request.getParameter("action");
            String paramAnioMes = request.getParameter("aniomes");
            String paramCencoId = request.getParameter("cencoId");
            System.out.println(WEB_NAME+"[GestionWeb.LoadGraficoAusencias]"
                + "parametros. "
                + "Action: " + paramAction
                + ", anioMes: " + paramAnioMes
                + ", cencoId: " + paramCencoId);
            
            if (paramAction!=null && paramAction.compareTo("torta") == 0){
                // ausencias del mes en curso
                int intMesActual = calendarHoy.get(Calendar.MONTH) + 1;
                String strMesActual = "" + intMesActual;
                if (intMesActual < 10) strMesActual = "0" + intMesActual;
                String anioMes = calendarHoy.get(Calendar.YEAR) + "-" + strMesActual;
                //String anioMes = "2018-07";
                System.out.println(WEB_NAME+"[GestionWeb.LoadGraficoAusencias]"
                    + "anioMes a consultar: " + anioMes);
                
                List<GraficoAusenciaVO> ausenciasMes;
                try{
                    ausenciasMes = graficosdao.getAusencias(anioMes);
                    Iterator<GraficoAusenciaVO> it = ausenciasMes.iterator();
                    while(it.hasNext()){
                        GraficoAusenciaVO detail = it.next(); 
                        empObj = new JSONObject();
                        empObj.put("ausencia", detail.getNombreAusencia());
                        empObj.put("total_ausencias", detail.getNumJustificaciones());
                        empdetails.add(empObj);
                    }
                }catch(Exception ex1){
                    System.err.println("[GestionWeb.LoadGraficoAusencias]error 1: "+ex1.getMessage());
                    ex1.printStackTrace();
                }
                    
            }
            
            //cierra conexion a la base de datos
            graficosdao.closeConnection();
            
            responseObj.put("empdetails", empdetails);
            
            System.out.println(WEB_NAME+"[GestionWeb.LoadGraficoAusencias]response string: "+ responseObj.toString());
                    
            out.print(responseObj.toString());
        } catch (JSONException ex) {
            System.err.println("[GestionWeb.LoadGraficoAusencias]error 2: "+ex.getMessage());
            ex.printStackTrace();
        }catch (Exception ex) {
            System.err.println("[GestionWeb.LoadGraficoAusencias]error 3: "+ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Retorna lista de 12 meses desde la fecha actual hacia atrás 
     */
    private List<String> getListaMeses(){
        List<String> listaMeses = new ArrayList<String>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM", Locale.ENGLISH);
        SimpleDateFormat anioMesFormat = new SimpleDateFormat("yyyy-MM");
        Date today = new Date();
        Calendar cal22 = new GregorianCalendar();
        
        cal22.setTime(today);
        cal22.add(Calendar.DAY_OF_MONTH, -365);
        Date anioAtras = cal22.getTime();
        System.out.println(WEB_NAME+"[LoadGraficoAusencias."
            + "getListaMeses]anioAtras: " + anioAtras 
            + ", hoy: " + today);
        
        //*************** anio y mes entre dos fechas
        YearMonth anioMesStart = YearMonth.parse(anioMesFormat.format(anioAtras), formatter);
        YearMonth anioMesEnd = YearMonth.parse(anioMesFormat.format(today), formatter);

        while(anioMesStart.isBefore(anioMesEnd) || anioMesStart.equals(anioMesEnd)) {
            System.out.println(WEB_NAME+"[LoadGraficoAusencias."
                + "getListaMeses]Itera aniomes: "+ anioMesStart.format(formatter));
            listaMeses.add(anioMesStart.format(formatter));
            anioMesStart = anioMesStart.plusMonths(1);
        }
        
        return listaMeses;
    }
    
    private List<LocalDate> getListaFechas(){
        Date today1 = new Date();
        Calendar cal22 = new GregorianCalendar();
        cal22.setTime(today1);
        cal22.add(Calendar.DAY_OF_MONTH, -30);
        Date today30 = cal22.getTime();
        System.out.println(WEB_NAME+"[LoadGraficoAusencias]"
            + "today: " + today1 + "today-30: " + today30);
        LocalDate startDate = today30.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
        LocalDate endDate = today1.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
        
        List<LocalDate> listaFechas = getDatesBetween(startDate, endDate);
        //Iterator iterator = listaFechas.iterator();
//        while(iterator.hasNext()) {
//           System.out.println(WEB_NAME+"fecha: " + iterator.next());
//        }
        return listaFechas;
    }
    
    public static List<LocalDate> getDatesBetween(
        LocalDate startDate, LocalDate endDate) { 

        long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate); 
        return IntStream.iterate(0, i -> i + 1)
          .limit(numOfDaysBetween)
          .mapToObj(i -> startDate.plusDays(i))
          .collect(Collectors.toList()); 
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setResponseHeaders(response);processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setResponseHeaders(response);processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
