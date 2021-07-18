/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.servlet.consultas;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.dao.CentroCostoDAO;
import cl.femase.gestionweb.dao.CompanyTreeDAO;
import cl.femase.gestionweb.dao.DepartamentoDAO;
import cl.femase.gestionweb.dao.EmpleadosDAO;
import static cl.femase.gestionweb.servlet.consultas.EventosMantencionController.m_logger;
import cl.femase.gestionweb.vo.CentroCostoVO;
import cl.femase.gestionweb.vo.DepartamentoVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.EmpresaVO;
import cl.femase.gestionweb.vo.NodeVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Alexander
 */
@WebServlet(name = "LoadTreeView", urlPatterns = {"/LoadTreeView"})
public class LoadTreeView extends BaseServlet {

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
        
        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        
        Gson gson = new Gson();
        response.setContentType("text/html;charset=UTF-8");
        
        CompanyTreeDAO empresasDAO = new CompanyTreeDAO();
        DepartamentoDAO deptosDAO = new DepartamentoDAO(appProperties);
        CentroCostoDAO cencosDAO = new CentroCostoDAO(appProperties);
        List<EmpresaVO> listaEmpresas = new ArrayList<EmpresaVO>();
        System.out.println("[LoadTreeView]Obtener lista de empresas, deptos y cencos...");
        listaEmpresas = empresasDAO.getEmpresas();
        
        //contiene todos los nodos
        //ArrayList<NodeVO> empresaNodes = new ArrayList<>();   
        
        ArrayList<NodeVO> deptosNodos    = new ArrayList<>();
        ArrayList<NodeVO> cencosNodos    = new ArrayList<>();
        ArrayList<NodeVO> empleadosNodos = new ArrayList<>();
        
        //asume la empresa 1: fundacion 
        String empresaId = userConnected.getEmpresaId();
        NodeVO nodoEmpresa = new NodeVO();
        nodoEmpresa.setHref(empresaId);
        nodoEmpresa.setText(userConnected.getEmpresaNombre());
        
            List<DepartamentoVO> departamentos = deptosDAO.getDepartamentosEmpresa(userConnected, empresaId);
            for (int idxdepto = 0; idxdepto < departamentos.size(); idxdepto++) {
                DepartamentoVO itDepto= departamentos.get(idxdepto);
                System.out.println("[LoadTreeView]"
                    + "Depto: " + itDepto.getId()
                    + ", nombre: " + itDepto.getNombre());
                //**********Nodo Depto
                NodeVO nodoDepto = new NodeVO();
                nodoDepto.setHref(itDepto.getId());
                nodoDepto.setText(itDepto.getNombre());
                nodoDepto.setParent_id(nodoEmpresa.getText());
                //get cencos
                
                cencosNodos    = new ArrayList<>();
                 //cargando cencos del depto
                List<CentroCostoVO> cencos = 
                    cencosDAO.getCentrosCostoDepto(userConnected, itDepto.getId());
                for (int idxcenco = 0; idxcenco < cencos.size(); idxcenco++) {
                    CentroCostoVO itCenco = cencos.get(idxcenco);
                    if (itCenco.getId() != -1){
                        //**********Nodo cenco
                        NodeVO nodoCenco = new NodeVO();
                        nodoCenco.setHref("" + itCenco.getId());
                        nodoCenco.setText(itCenco.getNombre());
                        nodoCenco.setParent_id(nodoDepto.getText());
                        
                        //cargando empleados al nodo
                        empleadosNodos     = new ArrayList<>();
                        List<EmpleadoVO> empleadosCenco= getListaEmpleados(appProperties, empresaId, itDepto.getId(), itCenco.getId());
                        for (int idxempleado = 0; idxempleado < empleadosCenco.size(); idxempleado++) {
                            EmpleadoVO itEmpleado = empleadosCenco.get(idxempleado);
                            NodeVO nodoEmpleado = new NodeVO();
                            nodoEmpleado.setHref("" + itEmpleado.getRut());
                            nodoEmpleado.setText(itEmpleado.getNombres() 
                                + " " + itEmpleado.getApeMaterno());
                            nodoEmpleado.setParent_id(itCenco.getNombre());
                            nodoEmpleado.setSelectable(true);
                            
                            empleadosNodos.add(nodoEmpleado);
                        }
                        nodoCenco.setNodes(empleadosNodos);
                        //set nums de empleados para el cenco
                        System.out.println("[LoadTreeViewServlet]"
                            + "Set nodo empleados. "
                            + "Empresa_id:" + empresaId
                            +", deptoId: " + itDepto.getId()
                            +", cencoId: " + itCenco.getId()
                            +", numEmpleados: " + empleadosCenco.size()
                        );
                        int[] tagsEmpleados = new int[1];
                        tagsEmpleados[0] = empleadosCenco.size();
                        nodoCenco.setTags(tagsEmpleados);
                        
                        cencosNodos.add(nodoCenco);

                        //add cencos al depto
                        nodoDepto.setNodes(cencosNodos);
                        
                    }

                }//fin iteracion de centros de costo
                //set nums de cencos para el depto
                System.out.println("[LoadTreeViewServlet] set nodo cenco. "
                    + "Empresa_id:" + empresaId
                    +", deptoId: " + nodoDepto.getText()
                    +", numCencos: " + cencos.size());
                int[] tagsCencos    = new int[1];
                tagsCencos[0] = cencos.size();
                nodoDepto.setTags(tagsCencos);
                //nodoDepto.setNodes(deptosNodos);
                deptosNodos.add(nodoDepto);

                System.out.println("\n\n");
                
            }//fin iteracion de departamentos de la empresa
        
        //set nums de deptos para la empresa
        System.out.println("[LoadTreeViewServlet]Final Nodo empresa_id:" + empresaId
            +", deptoId: " + nodoEmpresa.getText()
            +", numDeptos: " + departamentos.size());
        int[] tagsDeptos    = new int[1];
        tagsDeptos[0]=departamentos.size();
        nodoEmpresa.setTags(tagsDeptos);
        //set nodos=departamentos
        nodoEmpresa.setNodes(deptosNodos);
        
        //mostrar nodos
        for (int idxdepto = 0; idxdepto < deptosNodos.size(); idxdepto++) {
            NodeVO itDeptoNodo = deptosNodos.get(idxdepto);
            System.out.println("\n\n[LoadTreeViewServlet] "
                + "Depto: " + itDeptoNodo.getText());
            if (!itDeptoNodo.getNodes().isEmpty()){
                for (int idxcenco = 0; idxcenco < itDeptoNodo.getNodes().size(); idxcenco++) {
                    NodeVO itCencoNodo = itDeptoNodo.getNodes().get(idxcenco);
                    System.out.println("\t[LoadTreeViewServlet] "
                        + "Cenco: " + itCencoNodo.getText());
                }
            }
        }
        
        //empresaNodes.add(nodoEmpresa);
        
        String jsonStr = gson.toJson(nodoEmpresa);
        String finalJsonStr= "[" + jsonStr + "]";
//        System.out.println("[Treeview.LoadTreeViewServlet] "
//            + "jsonStr: " + finalJsonStr);
        
        response.getWriter().print(finalJsonStr);
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
    private List<EmpleadoVO> getListaEmpleados(PropertiesVO _appProperties, 
            String _empresaId,
            String _deptoId, 
            int _cencoId){
    
        System.out.println("[LoadTreeView]empresa: "+_empresaId
            +", depto: "+_deptoId
            +", cenco: "+_cencoId);
        
        EmpleadosDAO empleadosDao = new EmpleadosDAO(_appProperties);
        List<EmpleadoVO> listaEmpleados = new ArrayList<>();
        if (_empresaId.compareTo("-1") != 0 
                && _deptoId.compareTo("-1") != 0
                && _cencoId != -1){
                    //todos los empleados del cenco
                    listaEmpleados = empleadosDao.getEmpleadosShort(_empresaId, 
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
                    
//                    for (int i = 0; i < auxListaEmpleados.size(); i++) {
//			listaEmpleados.add(auxListaEmpleados.get(i));
//                    }
                    
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
        if (session!=null) session.removeAttribute("mensaje");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        if (userConnected != null){
            setResponseHeaders(response);processRequest(request, response);
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            m_logger.debug("Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
        }
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
        if (session!=null) session.removeAttribute("mensaje");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        if (userConnected != null){
            setResponseHeaders(response);processRequest(request, response);
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            m_logger.debug("Sesion de usuario "+request.getParameter("username")
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
