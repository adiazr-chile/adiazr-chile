
package cl.femase.gestionweb.servlet.vacaciones;

import cl.femase.gestionweb.business.CalculoVacacionesBp;
import cl.femase.gestionweb.business.DetalleAusenciaBp;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.vo.FilasAfectadasJsonVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author aledi
 */
@WebServlet(name = "CalculoVacacionesServlet", urlPatterns = {"/servlet/CalculoVacacionesServlet"})
public class NewCalculoVacacionesServlet extends BaseServlet {

    /**
    * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
    * methods.
    *
    * @param request servlet request
    * @param response servlet response
    * @throws ServletException if a servlet-specific error occurs
    * @throws IOException if an I/O error occurs
    */
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        ServletContext application              = this.getServletContext();
        PropertiesVO appProperties              = (PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected                 = (UsuarioVO)session.getAttribute("usuarioObj");
        CalculoVacacionesBp calculoVacacionesBp = new CalculoVacacionesBp(appProperties);
        DetalleAusenciaBp detAusenciaBp         = new DetalleAusenciaBp(appProperties);
        
        MaintenanceEventVO resultVO = new MaintenanceEventVO();
        resultVO.setUsername(userConnected.getUsername());
        resultVO.setDatetime(new Date());
        resultVO.setUserIP(request.getRemoteAddr());
        resultVO.setType("VAC");
        resultVO.setEmpresaIdSource(userConnected.getEmpresaId());

        String action = (String)request.getParameter("action");
        if (action.compareTo("calcula_saldo") == 0){
            String paramEmpresa = request.getParameter("empresa_id");
            String paramDeptoId = request.getParameter("depto_id");
            String paramCencoId = request.getParameter("cenco_id");
            String rutEmpleado = request.getParameter("rutEmpleado");
            //Calcular vacaciones...
            System.out.println(WEB_NAME+"[CalculoVacacionesServlet]"
                + "Calcular saldo dias de vacaciones. "
                + "empresa_id= " + paramEmpresa
                + ", depto_id= " + paramDeptoId
                + ", cenco_id= " + paramCencoId
                + ", rut_empleado= " + rutEmpleado);
            int intCencoId=-1;
            if (paramCencoId.compareTo("") != 0)
                intCencoId = Integer.parseInt(paramCencoId); 
            
            System.out.println(WEB_NAME+"[CalculoVacacionesServlet]"
                + "Calcular vacaciones (usando funciones). "
                + "empresa_id: " + paramEmpresa
                + ", cencoId: " + paramCencoId     
                + ", rutEmpleado: " + rutEmpleado);
            if (rutEmpleado != null && rutEmpleado.compareTo("-1") != 0){
                System.out.println(WEB_NAME+"[CalculoVacacionesServlet]"
                    + "Calcular saldo dias de vacaciones "
                    + "para un solo empleado.");
                
                //descomentar
////                Calcular vacaciones...para un solo empleado
////                Invocar al metodo 'calculoVacacionesBp.setVBANew', con rutEmpleado= RUT seleccionado        
////                        
////                ResultCRUDVO fnExec = calculoVacacionesBp.setVBA_Empleado(paramEmpresa, rutEmpleado, resultVO);
////                if (fnExec != null && fnExec.getFilasAfectadasObj() != null){
////                    System.out.println(WEB_NAME + "[CalculoVacacionesServlet]"
////                        + "Filas afectadas, post ejecucion de la funcion " + Constantes.fnSET_VBA_EMPLEADO
////                        + ": " + fnExec.getFilasAfectadasObj().toString());
////                }
                //descomentar
////                fnExec = calculoVacacionesBp.setVP_Empleado(paramEmpresa, rutEmpleado, resultVO);
////                if (fnExec != null && fnExec.getFilasAfectadasObj() != null){
////                    System.out.println(WEB_NAME + "[CalculoVacacionesServlet]"
////                        + "Filas afectadas, post ejecucion de la funcion " + Constantes.fnSET_VP_EMPLEADO
////                        + ": " + fnExec.getFilasAfectadasObj().toString());
////                }
                System.out.println(WEB_NAME+"[CalculoVacacionesServlet]"
                    + "Actualizar saldos de vacaciones "
                    + "en tabla detalle_ausencia "
                    + "Run: "+ rutEmpleado);
    
                detAusenciaBp.actualizaSaldosVacaciones(rutEmpleado);
                
            }else{
                System.out.println(WEB_NAME+"[CalculoVacacionesServlet]"
                    + "Calcular saldo dias de vacaciones "
                    + "para todos los empleados del centro de costo. "
                    + "empresa_id: " + paramEmpresa
                    + ", deptoId: " + paramDeptoId    
                    + ", cencoId: " + paramCencoId);
                //descomentar Invocar al metodo 'calculoVacacionesBp.setVBANew', con rutEmpleado= null
                ArrayList<FilasAfectadasJsonVO> empleadosAfectados;
                ResultCRUDVO fnExec = calculoVacacionesBp.setVBA_Cenco(paramEmpresa, intCencoId, resultVO);
                if (fnExec != null){
                    empleadosAfectados = fnExec.getEmpleadosAfectados();
                    for (int x = 0; x < empleadosAfectados.size(); x++) {
                        FilasAfectadasJsonVO empleado = empleadosAfectados.get(x);
                        System.out.println(WEB_NAME+"[CalculoVacacionesServlet]"
                            + "setVBA.Cenco -->Empleado afectado: " + empleado.toString() );
                    }
                }
                fnExec = calculoVacacionesBp.setVP_Cenco(paramEmpresa, intCencoId, resultVO);
                if (fnExec != null){
                    empleadosAfectados = fnExec.getEmpleadosAfectados();
                    for (int x = 0; x < empleadosAfectados.size(); x++) {
                        FilasAfectadasJsonVO empleado = empleadosAfectados.get(x);
                        System.out.println(WEB_NAME+"[CalculoVacacionesServlet]"
                            + "setVP.Cenco -->Empleado afectado: " + empleado.toString() );
                    }
                }
            }
            response.sendRedirect(request.getContextPath()
                + "/vacaciones/info_vacaciones.jsp");
        } else if (action.compareTo("calcula_vacaciones_desvincula2") == 0) {
                    String paramEmpresa = request.getParameter("empresa_id");
                    String paramDeptoId = request.getParameter("depto_id");
                    String paramCencoId = request.getParameter("cenco_id");
                    String rutEmpleado = request.getParameter("rutEmpleado");
                    System.out.println(WEB_NAME+"[CalculoVacacionesServlet]"
                        + "Calcular vacaciones para empleados desvinculados. "
                        + "empresa_id= " + paramEmpresa
                        + ", depto_id= " + paramDeptoId
                        + ", cenco_id= " + paramCencoId
                        + ", rut_empleado= " + rutEmpleado);
                    int intCencoId=-1;
                    if (paramCencoId.compareTo("") != 0)
                        intCencoId = Integer.parseInt(paramCencoId); 
                    
                    System.out.println(WEB_NAME+"[CalculoVacacionesServlet]"
                        + "Calcular vacaciones (desvinculado). "
                        + "empresa_id: " + paramEmpresa
                        + ", cencoId: " + paramCencoId     
                        + ", rutEmpleado: " + rutEmpleado);
                    if (rutEmpleado != null && rutEmpleado.compareTo("-1") != 0){
                        System.out.println(WEB_NAME+"[CalculoVacacionesServlet]"
                            + "Calcular vacaciones "
                            + "para un solo empleado desvinculado.");
                        
                        ResultCRUDVO fnExec = calculoVacacionesBp.setVBA_Empleado(paramEmpresa, rutEmpleado, resultVO);
                        if (fnExec != null && fnExec.getFilasAfectadasObj() != null){
                            System.out.println(WEB_NAME + "[CalculoVacacionesServlet]"
                                + "Filas afectadas, post ejecucion de la funcion " + Constantes.fnSET_VBA_EMPLEADO
                                + ": " + fnExec.getFilasAfectadasObj().toString());
                        }
                        fnExec = calculoVacacionesBp.setVP_Empleado(paramEmpresa, rutEmpleado, resultVO);
                        if (fnExec != null && fnExec.getFilasAfectadasObj() != null){
                            System.out.println(WEB_NAME + "[CalculoVacacionesServlet]"
                                + "Filas afectadas, post ejecucion de la funcion " + Constantes.fnSET_VP_EMPLEADO
                                + ": " + fnExec.getFilasAfectadasObj().toString());
                        }
                        System.out.println(WEB_NAME+"[CalculoVacacionesServlet]"
                            + "Actualizar saldos de vacaciones "
                            + "en tabla detalle_ausencia "
                            + "(usar nueva funcion setsaldodiasvacacionesasignadas). "
                            + "Run: "+ rutEmpleado);
                        detAusenciaBp.actualizaSaldosVacaciones(rutEmpleado);
                        
                    }else{
                        System.out.println(WEB_NAME+"[CalculoVacacionesServlet]"
                            + "Calcular saldo dias de vacaciones "
                            + "para todos los empleados DESVINCULADOS del centro de costo. "
                            + "empresa_id: " + paramEmpresa
                            + ", deptoId: " + paramDeptoId    
                            + ", cencoId: " + paramCencoId);
                        ArrayList<FilasAfectadasJsonVO> empleadosAfectados;
                                
                        ResultCRUDVO fnExec = calculoVacacionesBp.setVBA_Cenco(paramEmpresa, intCencoId, resultVO);
                        if (fnExec != null){    
                            empleadosAfectados = fnExec.getEmpleadosAfectados();
                            for (int x = 0; x < empleadosAfectados.size(); x++) {
                                FilasAfectadasJsonVO empleado = empleadosAfectados.get(x);
                                System.out.println(WEB_NAME+"[CalculoVacacionesServlet]"
                                    + "setVBA.Cenco -->Empleado desvinculado afectado: " + empleado.toString() );
                            }
                        }
                        fnExec = calculoVacacionesBp.setVP_Cenco(paramEmpresa, intCencoId, resultVO);
                        if (fnExec != null){
                            empleadosAfectados = fnExec.getEmpleadosAfectados();
                            for (int x = 0; x < empleadosAfectados.size(); x++) {
                                FilasAfectadasJsonVO empleado = empleadosAfectados.get(x);
                                System.out.println(WEB_NAME+"[CalculoVacacionesServlet]"
                                    + "setVP.Cenco -->Empleado desvinculado afectado: " + empleado.toString() );
                            }
                        }
                    }
                    response.sendRedirect(request.getContextPath()
                        + "/vacaciones/calculo_vacaciones_desvincula2.jsp");
        }     
        
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        if (userConnected != null){
            setResponseHeaders(response);processRequest(request, response);
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            System.out.println(WEB_NAME+"[CalculoVacacionesServlet]"
                + "Sesion de usuario " + request.getParameter("username")
                +" no valida");
            request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        if (userConnected != null){
            setResponseHeaders(response);processRequest(request, response);
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            System.out.println(WEB_NAME+"[CalculoVacacionesServlet]"
                + "Sesion de usuario " + request.getParameter("username")
                +" no valida");
            request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
        }
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
