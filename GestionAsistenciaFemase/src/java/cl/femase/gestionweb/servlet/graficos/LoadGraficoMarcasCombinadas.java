/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.servlet.graficos;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.dao.GraficosDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Alexander
 */
@WebServlet(name = "LoadGraficoMarcasCombinadas", urlPatterns = {"/LoadGraficoMarcasCombinadas"})
public class LoadGraficoMarcasCombinadas extends BaseServlet {

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
            SimpleDateFormat hhmmFormat     = new SimpleDateFormat("HH:mm");
            Calendar calendarDesde = Calendar.getInstance();
            Calendar calendarHasta = Calendar.getInstance();
            
            GraficosDAO graficosdao = new GraficosDAO();
            //Obtener fecha maxima donde hay marcas
            String fecha = graficosdao.getMaximaFechaConMarcas();
            System.out.println(WEB_NAME+"[GestionWeb.LoadGraficoMarcasCombinadas]"
                + "FECHA: " + fecha);
            StringTokenizer tokenFecha = new StringTokenizer(fecha, "-");
            int anio = Integer.parseInt(tokenFecha.nextToken());
            int mes = Integer.parseInt(tokenFecha.nextToken());
            int dia = Integer.parseInt(tokenFecha.nextToken());

            calendarDesde.set(Calendar.DATE, dia);
            calendarDesde.set(Calendar.MONTH, (mes-1));
            calendarDesde.set(Calendar.YEAR, anio);
            
            calendarHasta.set(Calendar.DATE, dia);
            calendarHasta.set(Calendar.MONTH, (mes-1));
            calendarHasta.set(Calendar.YEAR, anio);
            
            //while (rs.next()) {
            for (int horaDesde = 7; horaDesde <= 23; horaDesde++){
                empObj = new JSONObject();
                
                calendarDesde.set(Calendar.HOUR_OF_DAY, horaDesde);
                calendarDesde.set(Calendar.MINUTE, 0);
                calendarDesde.set(Calendar.SECOND, 0);
                
                calendarHasta.set(Calendar.HOUR_OF_DAY, (horaDesde+1));
                calendarHasta.set(Calendar.MINUTE, 0);
                calendarHasta.set(Calendar.SECOND, 0);
                
                String startTime = hhmmFormat.format(calendarDesde.getTime());
                String endTime  = hhmmFormat.format(calendarHasta.getTime());
                int totalMarcasEntrada = 0;
                int totalMarcasSalida = 0;
                try{
                    totalMarcasEntrada = graficosdao.getTotalMarcas(fechaFormat.format(calendarDesde.getTime()), 
                        startTime+":00", 
                        endTime+":00", 1);
                    totalMarcasSalida = graficosdao.getTotalMarcas(fechaFormat.format(calendarDesde.getTime()), 
                        startTime+":00", 
                        endTime+":00", 2);
                }catch(Exception ex1){
                    System.err.println("[GestionWeb.LoadGraficoMarcasCombinadas]error 1: "+ex1.getMessage());
                    ex1.printStackTrace();
                }
                System.out.println(WEB_NAME+"[GestionWeb.LoadGraficoMarcasCombinadas]"
                    + "time desde: " + calendarDesde.getTime()
                    + ", conFormato: "+startTime
                    + ", time hasta: " + calendarHasta.getTime()
                    + ", conFormato: "+endTime
                    + ", totalMarcasEntrada: "+totalMarcasEntrada
                    + ", totalMarcasSalida: "+totalMarcasSalida);
                empObj.put("fechahora", hhmmFormat.format(calendarDesde.getTime()));
                empObj.put("entradas", totalMarcasEntrada);
                empObj.put("salidas", totalMarcasSalida);
                empdetails.add(empObj);
            }
            //cierra conexion a la base de datos
            graficosdao.closeConnection();
            
            responseObj.put("empdetails", empdetails);
            
            System.out.println(WEB_NAME+"[GestionWeb.LoadGraficoMarcasCombinadas]response string: "+ responseObj.toString());
                    
            out.print(responseObj.toString());
        } catch (JSONException ex) {
            System.err.println("[GestionWeb.LoadGraficoMarcasCombinadas]error 2: "+ex.getMessage());
            ex.printStackTrace();
        }catch (Exception ex) {
            System.err.println("[GestionWeb.LoadGraficoMarcasCombinadas]error 3: "+ex.getMessage());
            ex.printStackTrace();
        }
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
