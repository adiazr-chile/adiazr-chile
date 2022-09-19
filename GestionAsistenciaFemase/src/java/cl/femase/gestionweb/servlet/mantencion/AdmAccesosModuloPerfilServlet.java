/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.AccesoBp;
import cl.femase.gestionweb.business.ModuloAccesoPerfilBp;
import cl.femase.gestionweb.business.ModulosSistemaBp;
import cl.femase.gestionweb.business.PerfilUsuarioBp;
import cl.femase.gestionweb.vo.AccesoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ModuloAccesoPerfilVO;
import cl.femase.gestionweb.vo.ModuloSistemaVO;
import cl.femase.gestionweb.vo.PerfilUsuarioVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Alexander
 */
public class AdmAccesosModuloPerfilServlet extends BaseServlet {

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

            HttpSession session = request.getSession(true);
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        
            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("ABP");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
            
            System.out.println(WEB_NAME+"[gestionFemase."
                + "AdmAccesosModuloPerfilServlet]"
                + "modo: " + request.getParameter("modo"));
            //lista de modulos
            List<ModuloSistemaVO> lstmodulos = 
                    new ArrayList<>();
            //lista de perfiles
            List<PerfilUsuarioVO> lstperfiles = 
                new ArrayList<>();
            //lista de accesos disponibles
            LinkedHashMap<String, AccesoVO> lstAccesosDisponibles = 
                new LinkedHashMap<>();
            //lista de accesos seleccionados
            LinkedHashMap<String, ModuloAccesoPerfilVO> lstAccesosPerfilModulo = 
                new LinkedHashMap<>();
            String mode=request.getParameter("modo");
            
            PerfilUsuarioBp perfilesBp=new PerfilUsuarioBp(appProperties);
            ModulosSistemaBp modulosBp=new ModulosSistemaBp(appProperties);
            AccesoBp accesosBp=new AccesoBp(appProperties);
            ModuloAccesoPerfilBp accesosPerfilBp=new ModuloAccesoPerfilBp(appProperties);
            
            if (mode != null && mode.compareTo("startpage")==0){
                //cargar lista de perfiles y modulos (inicial)
                System.out.println(WEB_NAME+"[gestionFemase."
                    + "AdmAccesosModuloPerfilServlet]cargar lista de perfiles y modulos (inicial)");

                lstperfiles = perfilesBp.getPerfiles(null, 0, 0, "perfil_nombre");
                
                //modulos
                lstmodulos = modulosBp.getModulosSistema(null, 0, 0, "modulo_nombre");
                                
                //seteo modulos 
                session.setAttribute("modulos_sistema", lstmodulos);
                //seteo perfiles 
                session.setAttribute("perfiles_usuario", lstperfiles);
                
            }else if (mode != null && mode.compareTo("load")==0){
                //carga accesos disponibles y relacionados al perfil-modulo seleccionados
                String perfil_id=request.getParameter("perfil_id");
                String modulo_id=request.getParameter("modulo_id");
                                
                System.out.println(WEB_NAME+"[gestionFemase.AdmAccesosModuloPerfilServlet]PerfilId: "+perfil_id+", moduloId: "+modulo_id);
                /**
                 * cargar lista de accesos disponibles y lista de accesos seleccionados, ordenados por id
                 * Los accesos disponibles no deben aparacer en la lista de seleccionados
                 */
                
                List<AccesoVO> accesosAll = accesosBp.getAccesos(null, 0, 0, "acceso_label");
                lstAccesosPerfilModulo=
                    accesosPerfilBp.getAccesosByModuloPerfil(Integer.parseInt(modulo_id), Integer.parseInt(perfil_id));
                LinkedHashMap<String,String> lstAccesosKeyPerfilModulo=
                    accesosPerfilBp.getSoloAccesosKeyByModuloPerfil(Integer.parseInt(modulo_id), Integer.parseInt(perfil_id));
                //iterar todos los accesos  
                Iterator<AccesoVO> accesoIterator = accesosAll.iterator();
		while (accesoIterator.hasNext()) {
                    AccesoVO acceso=accesoIterator.next();
                    if (!lstAccesosKeyPerfilModulo.containsKey(""+acceso.getId())){
                        System.out.println(WEB_NAME+"[AdminAccesosPerfil]"
                            + "add acceso disponible: "+acceso.getLabel());
                        lstAccesosDisponibles.put(""+acceso.getId(), acceso);
                    } 
                }
                
                session.setAttribute("perfilSelected", perfil_id);
                session.setAttribute("moduloSelected", modulo_id);
                
            }else if (mode != null && mode.compareTo("save") == 0){
                    String perfil_id=request.getParameter("perfil_id");
                    String modulo_id=request.getParameter("modulo_id");
                    //eliminar accesos existentes antes de insertar los accesos definidos
                    ModuloAccesoPerfilVO deleteacc=new ModuloAccesoPerfilVO();
                    deleteacc.setPerfilId(Integer.parseInt(perfil_id));
                    deleteacc.setModuloId(Integer.parseInt(modulo_id));
                    accesosPerfilBp.delete(deleteacc, resultado);
                    
                    //mode guardar
                    String[] accesos=request.getParameterValues("accesos_selected");
                    if (accesos!=null){
                        for (int x=0;x<accesos.length;x++){
                            System.out.println(WEB_NAME+"acceso seleccionado["+x+"] = "+accesos[x]);
                            ModuloAccesoPerfilVO newAcceso=new ModuloAccesoPerfilVO();
                            newAcceso.setPerfilId(Integer.parseInt(perfil_id));
                            newAcceso.setModuloId(Integer.parseInt(modulo_id));
                            newAcceso.setAccesoId(Integer.parseInt(accesos[x]));
                            newAcceso.setTipoAcceso(2);
                            newAcceso.setOrdenDespliegue(x+1);
                            accesosPerfilBp.insert(newAcceso, resultado);
                        }     
                    }else System.out.println(WEB_NAME+"No hay items");
            }
            
            System.out.println(WEB_NAME+"[gestionFemase."
                + "AdmAccesosModuloPerfilServlet]"
                    + "cargar listas en request...");
            
            System.out.println(WEB_NAME+"[gestionFemase."
                + "AdmAccesosModuloPerfilServlet]"
                    + "perfiles.size= "+lstperfiles.size());
                        
            //seteo accesos disponibles 
            request.setAttribute("accesos_disponibles", lstAccesosDisponibles);
            //seteo accesos asociados a perfil y modulo seleccionado 
            request.setAttribute("accesos_perfilmodulo", lstAccesosPerfilModulo);
            
            request.getRequestDispatcher("/mantencion/admin_accesos_perfil_usuario.jsp").forward(request, response);//frameset
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
