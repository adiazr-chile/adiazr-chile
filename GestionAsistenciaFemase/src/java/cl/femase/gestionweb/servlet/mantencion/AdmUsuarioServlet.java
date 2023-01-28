/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.UsuarioBp;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PerfilUsuarioVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioCentroCostoVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Alexander
 */
public class AdmUsuarioServlet extends BaseServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            HttpSession session = request.getSession(true);
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        
            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("USR");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
            
            String action=request.getParameter("action");
            
            System.out.println(WEB_NAME+"[gestionFemase."
                + "AdmUsuarioServlet]"
                + "Accion: " + action);
            //lista de perfiles
            List<PerfilUsuarioVO> lstperfiles = 
                new ArrayList<>();
            //lista de accesos seleccionados
            List<UsuarioCentroCostoVO> lstCencosUsuario = 
                new ArrayList<>();
            
            String username     = request.getParameter("username");
            String strPerfil    = request.getParameter("perfil_id");
            String password     = request.getParameter("password");
            String run          = request.getParameter("run");
            String nombres      = request.getParameter("nombres");
            String apePaterno   = request.getParameter("paterno");
            String apeMaterno   = request.getParameter("materno");
            String email        = request.getParameter("email");
            String strEstado    = request.getParameter("estado");
            String empresaId    = request.getParameter("empresaId");
            
            int idEstado = (strEstado == null)?1:Integer.parseInt(strEstado);
            int idPerfil = (strPerfil == null)?1:Integer.parseInt(strPerfil);
            
            //objeto usado para update/insert
            UsuarioVO userdata = new UsuarioVO();
            userdata.setUsername(username);
            userdata.setPassword(password);
            userdata.setRunEmpleado(run);
            userdata.setNombres(nombres);
            userdata.setEstado(idEstado);
            userdata.setApPaterno(apePaterno);
            userdata.setApMaterno(apeMaterno);
            userdata.setIdPerfil(idPerfil);
            userdata.setEmail(email);
            userdata.setEmpresaId(empresaId);
                        
            UsuarioBp usuarioBp=new UsuarioBp(appProperties);
            
            if (action != null && action.compareTo("startpage")==0){
                //cargar info del usuario y lista de perfiles de usuario
                if (username!=null){
                    System.out.println(WEB_NAME+"[gestionFemase."
                        + "AdmUsuarioServlet]editar info "
                        + "para el usuario: "+username);
                    UsuarioVO userToEdit = usuarioBp.getUsuario(username);
                    userdata.setAdminEmpresa(userToEdit.getAdminEmpresa());
                    //modulos
                    lstCencosUsuario = usuarioBp.getCencosUsuario(userdata);
                    //seteo datos del usuario 
                    request.setAttribute("info_usuario", userToEdit);
                    //seteo cencos usuario 
                    request.setAttribute("cencos_usuario", lstCencosUsuario);
                }else{
                    System.out.println(WEB_NAME+"[gestionFemase."
                        + "AdmUsuarioServlet]crear usuario...");
                }
                request.getRequestDispatcher("/mantencion/usuario_form.jsp").forward(request, response);//frameset
            }else if (action != null && action.compareTo("update") == 0){
                    System.out.println(WEB_NAME+"[gestionFemase."
                        + "AdmUsuarioServlet]modificar datos del usuario");
                   
                    //actualizar info propia del usuario
                    ResultCRUDVO result = usuarioBp.update(userdata, resultado);
                    if (result.isThereError()){
                        System.out.println(WEB_NAME+"[gestionFemase."
                            + "AdmUsuarioServlet]Error al modificar usuario: "+result.getMsg());
                        request.setAttribute("mensaje", result.getMsg());
                        request.getRequestDispatcher("/mantencion/usuario_form.jsp").forward(request, response);//frameset
                    }else{
                        System.out.println(WEB_NAME+"[gestionFemase."
                            + "AdmUsuarioServlet]delete cencos del usuario");
                        //eliminar cencos existentes antes de insertar los cencos al usuario
                        usuarioBp.deleteCencos(username);

                        //mode guardar
                        String[] cencos = request.getParameterValues("cencos_selected");
                        String cencoKey="";
                        String auxEmpresaId="";
                        String auxDeptoId="";
                        String auxCencoId="";
                        if (cencos != null){
                            for (int x = 0; x < cencos.length;x++){
                                cencoKey = cencos[x];
                                System.out.println(WEB_NAME+"[gestionFemase."
                                    + "AdmUsuarioServlet]insert. "
                                    + "cencoKey seleccionado["+x+"] = "+cencoKey);
                                StringTokenizer tokenCenco = new StringTokenizer(cencoKey, "|");
                                while (tokenCenco.hasMoreTokens()){
                                    auxEmpresaId = tokenCenco.nextToken();
                                    auxDeptoId = tokenCenco.nextToken();
                                    auxCencoId = tokenCenco.nextToken();
                                }

                                UsuarioCentroCostoVO newUserCenco=
                                    new UsuarioCentroCostoVO(username,
                                        Integer.parseInt(auxCencoId),
                                        0,
                                        null,
                                        auxEmpresaId,
                                        auxDeptoId);
                                usuarioBp.insertCenco(newUserCenco);
                            }     
                        }else System.out.println(WEB_NAME+"[gestionFemase."
                                + "AdmUsuarioServlet]Update."
                                + "No hay cencos seleccionados");

                        System.out.println(WEB_NAME+"[gestionFemase.AdmUsuarioServlet]usuario modificado"
                            + "cargar lista de usuarios...");

                        response.sendRedirect(request.getContextPath()+"/mantencion/usuarios.jsp");
                        //request.getRequestDispatcher("/mantencion/usuarios.jsp").forward(request, response);//frameset
                    }
            }else if (action != null && action.compareTo("create") == 0){
                    System.out.println(WEB_NAME+"[gestionFemase."
                        + "AdmUsuarioServlet]Crear nuevo usuario");
                   
                    //insertar info propia del usuario
                    ResultCRUDVO result = usuarioBp.insert(userdata, resultado);
                    if (result.isThereError()){
                        System.out.println(WEB_NAME+"[gestionFemase.AdmUsuarioServlet]"
                            + "Error al crear usuario: "+result.getMsg());
                        request.setAttribute("mensaje", result.getMsg());
                        request.getRequestDispatcher("/mantencion/usuario_form.jsp").forward(request, response);//frameset
                    }else{
                        System.out.println(WEB_NAME+"[gestionFemase.AdmUsuarioServlet]"
                            + "delete cencos del usuario");
                        //eliminar cencos existentes antes de insertar los cencos al usuario
                        usuarioBp.deleteCencos(username);

                        //mode guardar
                        String cencoKey="";
                        String auxEmpresaId="";
                        String auxDeptoId="";
                        String auxCencoId="";
                        String[] cencos = request.getParameterValues("cencos_selected");
                        if (cencos != null){
                            for (int x = 0; x < cencos.length;x++){
                                cencoKey = cencos[x];
                                System.out.println(WEB_NAME+"[gestionFemase.AdmUsuarioServlet]insert. "
                                    + "cencoKey seleccionado["+x+"] = "+cencoKey);
                                StringTokenizer tokenCenco = new StringTokenizer(cencoKey, "|");
                                while (tokenCenco.hasMoreTokens()){
                                    auxEmpresaId = tokenCenco.nextToken();
                                    auxDeptoId = tokenCenco.nextToken();
                                    auxCencoId = tokenCenco.nextToken();
                                }

                                UsuarioCentroCostoVO newUserCenco=
                                    new UsuarioCentroCostoVO(username,
                                        Integer.parseInt(auxCencoId),
                                        0,
                                        null,
                                        auxEmpresaId,
                                        auxDeptoId);
                                usuarioBp.insertCenco(newUserCenco);
                            }//fin iteracion de cencos     
                        }else {
                            System.out.println(WEB_NAME+"[gestionFemase.AdmUsuarioServlet]Insert."
                                + "No hay cencos seleccionados");
                        }
                        System.out.println(WEB_NAME+"[gestionFemase."
                            + "AdmUsuarioServlet]usuario modificado"
                            + "cargar lista de usuarios...");

                        response.sendRedirect(request.getContextPath()+"/mantencion/usuarios.jsp");
                        //request.getRequestDispatcher("/mantencion/usuarios.jsp").forward(request, response);//frameset
                    }
            }
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
