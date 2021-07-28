/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.servlet.procesos;

import cl.femase.gestionweb.business.CentroCostoBp;
import cl.femase.gestionweb.business.DepartamentoBp;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.dao.TraspasoHistoricoDAO;
import cl.femase.gestionweb.vo.CentroCostoVO;
import cl.femase.gestionweb.vo.DepartamentoVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author aledi
 */
@WebServlet(name = "TraspasoHistorico", urlPatterns = {"/servlet/TraspasoHistorico"})
public class TraspasoHistorico extends HttpServlet {

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
        
        HttpSession session         = request.getSession(true);
        ServletContext application  = this.getServletContext();
        PropertiesVO appProperties  = (PropertiesVO)application.getAttribute("appProperties");
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected     = (UsuarioVO)session.getAttribute("usuarioObj");
        
        String fecha            = request.getParameter("fecha");
        String[] tablas         = request.getParameterValues("tabla");
        String empresa          = request.getParameter("empresa");
        String todaLaEmpresa    = request.getParameter("toda_la_empresa");
        String deptoId          = request.getParameter("depto");
        String cencoId          = request.getParameter("cenco");
        String paramRUN              = request.getParameter("rut");
        int intCencoId          = Integer.parseInt(cencoId);
        
        System.out.println("[GestionFemase."
            + "servlet.TraspasoHistorico.processRequest]"
            + "Fecha: " + fecha
            + ", empresaId: " + empresa
            + ", todaLaEmpresa?: " + todaLaEmpresa
            + ", deptoId: " + deptoId
            + ", cencoId: " + cencoId
            + ", RUN empleado: " + paramRUN);
        
        TraspasoHistoricoDAO historicosDao = new TraspasoHistoricoDAO();
        List<String> empleados = getEmpleados(userConnected, 
            todaLaEmpresa, 
            empresa, 
            deptoId, 
            intCencoId, 
            paramRUN);
        String cadenaRuts = "";
        if (empleados != null && !empleados.isEmpty()){
            System.out.println("[GestionFemase."
                + "servlet.TraspasoHistorico.processRequest]"
                + "num empleados: "+ empleados.size());
            
            for (String rutEmpleado : empleados) {
                cadenaRuts += "'" + rutEmpleado + "',";
            }
            cadenaRuts = cadenaRuts.substring(0, cadenaRuts.length()-1);
        }
        System.out.println("[GestionFemase."
            + "servlet.TraspasoHistorico.processRequest]"
            + "cadenaRuts: " + cadenaRuts);
        
        HashMap<String, MaintenanceVO> hashFilasAfectadas = new HashMap<>();
        int affectedRows = 0;
        MaintenanceVO resultado = new MaintenanceVO();
        System.out.println("[GestionFemase."
            + "servlet.TraspasoHistorico.processRequest]"
            + "Obtener num registros antes de realizar el traspaso historico.");
        
//        hashFilasAfectadas = showNumAffectedRows(historicosDao, tablas, empresa, fecha, cadenaRuts);
        
        for (int x = 0; x < tablas.length;x++){
            String tableName = tablas[x];
            if (tableName.compareTo("marca") == 0){
                resultado = historicosDao.insertMarcasHistoricas(empresa, fecha, cadenaRuts);
                affectedRows = resultado.getFilasAfectadas();
                if (affectedRows > 0) {
                    System.out.println("[GestionFemase."
                        + "servlet.TraspasoHistorico.processRequest]"
                        + "Eliminando marcas recien traspasadas a historico");
                    historicosDao.deleteMarcas(empresa, fecha, cadenaRuts);
                }
            }else if (tableName.compareTo("marca_rechazo") == 0){
                    resultado = historicosDao.insertMarcasRechazosHistoricas(empresa, fecha, cadenaRuts);
                    affectedRows = resultado.getFilasAfectadas();
                    if (affectedRows > 0) {
                        System.out.println("[GestionFemase."
                            + "servlet.TraspasoHistorico.processRequest]"
                            + "Eliminando marcas rechazadas recien traspasadas a historico");
                        historicosDao.deleteMarcasRechazadas(empresa, fecha, cadenaRuts);
                    }
            }else if (tableName.compareTo("detalle_ausencia") == 0){
                    resultado = historicosDao.insertAusenciasHistoricas(empresa, fecha, cadenaRuts);
                    affectedRows = resultado.getFilasAfectadas();
                    if (affectedRows > 0) {
                        System.out.println("[GestionFemase."
                            + "servlet.TraspasoHistorico.processRequest]"
                            + "Eliminando ausencias recien traspasadas a historico");
                        historicosDao.deleteAusencias(empresa, fecha, cadenaRuts);
                    }
            }else if (tableName.compareTo("detalle_asistencia") == 0){
                    resultado = historicosDao.insertDetalleAsistenciaHistoricos(empresa, fecha, cadenaRuts);
                    affectedRows = resultado.getFilasAfectadas();
                    if (affectedRows > 0) {
                        System.out.println("[GestionFemase."
                            + "servlet.TraspasoHistorico.processRequest]"
                            + "Eliminando calculos de asistencia recien traspasadas a historico");
                        historicosDao.deleteDetalleAsistencia(empresa, fecha, cadenaRuts);
                    }
            }else if (tableName.compareTo("mantencion_evento") == 0){
                    resultado = historicosDao.insertLogEventosHistoricos(empresa, fecha);
                    affectedRows = resultado.getFilasAfectadas();
                    if (affectedRows > 0) {
                        System.out.println("[GestionFemase."
                            + "servlet.TraspasoHistorico.processRequest]"
                            + "Eliminando registros de log recien traspasados a historico");
                        historicosDao.deleteLogEventos(empresa, fecha);
                    }
            }
            System.out.println("[GestionFemase."
                + "servlet.TraspasoHistorico.processRequest]"
                + "add resultado: [tabla, numRows] = [" + tableName + "," + resultado.getFilasAfectadas() + "]" );
            hashFilasAfectadas.put(tableName, resultado);
        }
        request.setAttribute("resultado", hashFilasAfectadas);
        
        request.getRequestDispatcher("/procesos/ejecutar_traspaso_historico.jsp").forward(request, response);
    }

    /**
    * Mostrar numero de filas que seran traspasadas a tablas historicas.
    * 
    * @param _historicosDao
    * @param _tablas
    * @param _empresaId
    * @param _fecha
    * @param _cadenaRuts
    * @return 
    */
    public HashMap<String, Integer> showNumAffectedRows(TraspasoHistoricoDAO _historicosDao, 
            String[] _tablas, 
            String _empresaId, 
            String _fecha, 
            String _cadenaRuts){
        HashMap<String, Integer> hashTableRows = new HashMap<>();
        int affectedRows = 0;
        for (int x = 0; x < _tablas.length;x++){
            String tableName = _tablas[x];
            if (tableName.compareTo("marca") == 0){
                affectedRows = _historicosDao.getCountMarcas(_empresaId, _fecha, _cadenaRuts);
                hashTableRows.put(tableName, affectedRows);
                System.out.println("[GestionFemase."
                    + "servlet.TraspasoHistorico.showNumEffectedRows]"
                    + "Marcas que seran traspasadas a historico) = " + affectedRows);
                
            }else if (tableName.compareTo("marca_rechazo") == 0){
                    affectedRows = _historicosDao.getCountMarcasRechazadas(_empresaId, _fecha, _cadenaRuts);
                    hashTableRows.put(tableName, affectedRows);
                    System.out.println("[GestionFemase."
                        + "servlet.TraspasoHistorico.showNumEffectedRows]"
                        + "Marcas rechazadas que seran traspasadas a historico) = " + affectedRows);
            }else if (tableName.compareTo("detalle_ausencia") == 0){
                    affectedRows = _historicosDao.getCountAusencias(_empresaId, _fecha, _cadenaRuts);
                    hashTableRows.put(tableName, affectedRows);
                    System.out.println("[GestionFemase."
                        + "servlet.TraspasoHistorico.showNumEffectedRows]"
                        + "Ausencias que seran traspasadas a historico) = " + affectedRows);
            }else if (tableName.compareTo("detalle_asistencia") == 0){
                    affectedRows = _historicosDao.getCountAsistencia(_empresaId, _fecha, _cadenaRuts);
                    hashTableRows.put(tableName, affectedRows);
                    System.out.println("[GestionFemase."
                        + "servlet.TraspasoHistorico.showNumEffectedRows]"
                        + "Detalles de asistencia que seran traspasadas a historico) = " + affectedRows);
            }else if (tableName.compareTo("mantencion_evento") == 0){
                    affectedRows = _historicosDao.getCountLogEventos(_empresaId, _fecha);
                    hashTableRows.put(tableName, affectedRows);
                    System.out.println("[GestionFemase."
                        + "servlet.TraspasoHistorico.showNumEffectedRows]"
                        + "Log de eventos (auditoria) que seran traspasadas a historico) = " + affectedRows);
            }
        }
        
        return hashTableRows;
    }
    
    /**
    * 
    */
    private List<String> getEmpleados(UsuarioVO _usuario, 
            String _todaLaEmpresa,
            String _empresaId, 
            String _deptoId, 
            int _cencoId, 
            String _rutEmpleado){
        
        System.out.println("[GestionFemase."
            + "servlet.TraspasoHistorico.getEmpleados]"
            + "EmpresaId: " + _empresaId
            + ", deptoId: " + _deptoId
            + ", cencoId: " + _cencoId
            + ", rutEmpleado: " + _rutEmpleado    
        );
        
        DepartamentoBp deptosBp         = new DepartamentoBp(new PropertiesVO());
        CentroCostoBp cencosBp          = new CentroCostoBp(new PropertiesVO());
        EmpleadosBp empleadosBp          = new EmpleadosBp(new PropertiesVO());
        
        List<String> listaEmpleadosFinal     = new ArrayList<>();
        List<String> listaEmpleadosIteracion = new ArrayList<>();
        List<DepartamentoVO> departamentos  = new ArrayList<>(); 
        List<CentroCostoVO> cencos          = new ArrayList<>(); 
        
        if ( _todaLaEmpresa.compareTo("S") == 0 || _deptoId.compareTo("-1") == 0 ){
            System.out.println("[GestionFemase."
                + "servlet.TraspasoHistorico.getEmpleados]"
                + "Traspasar a historico todos los departamentos "
                + "de la EmpresaId: " + _empresaId);
            departamentos = 
                deptosBp.getDepartamentosEmpresa(_usuario, _empresaId);
        }else{
            DepartamentoVO auxDepto = deptosBp.getDepartamentoByKey(_deptoId);
            departamentos.add(auxDepto);
            System.out.println("[GestionFemase."
                + "servlet.TraspasoHistorico.getEmpleados]"
                + "Traspasar a historico solo el departamento "
                + "ID: " + auxDepto.getId() + ", nombre: " + auxDepto.getNombre());
        }
        
        for (int i = 0; i < departamentos.size(); i++) {
            DepartamentoVO itDepto= departamentos.get(i);
            System.out.println("[GestionFemase."
                + "servlet.TraspasoHistorico.getEmpleados]Depto: " + itDepto.getId()
                + ", nombre: " + itDepto.getNombre());
            
            if (_cencoId == -1){
                System.out.println("[GestionFemase."
                    + "servlet.TraspasoHistorico.getEmpleados]"
                    + "Traspasar a historico todos los centros de costo "
                    + "de la EmpresaId: " + _empresaId);
                cencos = 
                    cencosBp.getCentrosCostoDepto(_usuario, itDepto.getId());
            }else{
                CentroCostoVO auxCenco = cencosBp.getCentroCostoByKey(_deptoId, _cencoId);
                cencos.add(auxCenco);
                System.out.println("[GestionFemase."
                    + "servlet.TraspasoHistorico.getEmpleados]"
                    + "Traspasar a historico solo el cenco "
                    + "ID: " + auxCenco.getId() + ", nombre: " + auxCenco.getNombre());
            }
                        
            for (int j = 0; j < cencos.size(); j++) {
                CentroCostoVO itCenco = cencos.get(j);
                if (itCenco.getId() != -1){
                    System.out.println("[GestionFemase."
                        + "servlet.TraspasoHistorico.getEmpleados]"
                        + "Buscar empleados para Cenco: " + itCenco.getId()
                        +", nombre: " + itCenco.getNombre());
					
                    if ( _rutEmpleado.compareTo("-1") == 0 ){
                        System.out.println("[GestionFemase."
                            + "servlet.TraspasoHistorico.getEmpleados]"
                            + "Traspasar a historico todos los empleados "
                            + "de la [empresaId,deptoId,cencoId] "
                            + "= [" + _empresaId 
                                + "," + itDepto.getId() 
                                + "," + itCenco.getId() 
                                + "]");
                        listaEmpleadosIteracion = 
                            getListaEmpleados(_empresaId, itDepto.getId(), itCenco.getId());
                        
                        for (int k = 0; k < listaEmpleadosIteracion.size(); k++) {
                            listaEmpleadosFinal.add(listaEmpleadosIteracion.get(k));
                        }
                    }else{
                        System.out.println("[GestionFemase."
                            + "servlet.TraspasoHistorico.getEmpleados]"
                            + "Traspasar a historico solo el RUN empleado: " + _rutEmpleado);
                        listaEmpleadosIteracion.add(_rutEmpleado);
                        listaEmpleadosFinal.add(_rutEmpleado);
                    }
                    
                    System.out.println("[GestionFemase."
                        + "servlet.TraspasoHistorico.getEmpleados]"
                        + "empleados.size()= " + listaEmpleadosFinal.size());
                }
            }
        }//fin iteracion de departamentos de la empresa
        
        return listaEmpleadosFinal;
    }
    
    /**
    * Obtiene lista de empleados para el centro de costo especificado
    * 
    * 
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @return 
    */
    private List<String> getListaEmpleados(String _empresaId,
            String _deptoId, 
            int _cencoId){
    
        System.out.println("[GestionFemase."
            + "CalculaAsistenciaFemaseJob]empresa: "+_empresaId
            +", depto: "+_deptoId
            +", cenco: "+_cencoId);
        
        EmpleadosBp empleadosBp = new EmpleadosBp();
        List<String> listaEmpleados = new ArrayList<>();
        if (_empresaId.compareTo("-1") != 0 
                && _deptoId.compareTo("-1") != 0
                && _cencoId != -1){
                    //todos los empleados del cenco
                    List<EmpleadoVO> auxListaEmpleados = empleadosBp.getEmpleadosShort(_empresaId, 
                            _deptoId, 
                            _cencoId, 
                            -1,  
                            null, 
                            null, 
                            null, 
                            null, 
                            0, 
                            0, 
                            "empleado.empl_rut");
                    
                    for (int i = 0; i < auxListaEmpleados.size(); i++) {
			listaEmpleados.add(auxListaEmpleados.get(i).getRut());
                    }
                    
        }
        return listaEmpleados;
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
        HttpSession session = request.getSession(true);
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        if (userConnected != null){
            setResponseHeaders(response);processRequest(request, response);
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                +" no valida");
            System.err.println("Sesion de usuario "+request.getParameter("username")
                +" no valida");
            request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
        }
    }

    /**
    * 
    * @param response
    */
    public void setResponseHeaders(final HttpServletResponse response) {
        
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
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
        HttpSession session = request.getSession(true);
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        if (userConnected != null){
            setResponseHeaders(response);processRequest(request, response);
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                +" no valida");
            System.err.println("Sesion de usuario "+request.getParameter("username")
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
