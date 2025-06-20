/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.servlet;

import cl.femase.gestionweb.business.CentroCostoBp;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.ProcesosBp;
import cl.femase.gestionweb.business.TurnoRotativoBp;
import cl.femase.gestionweb.business.TurnosBp;
import cl.femase.gestionweb.business.VacacionesBp;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.vo.CentroCostoVO;
import cl.femase.gestionweb.vo.DepartamentoVO;
import cl.femase.gestionweb.vo.DispositivoVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.ProcesoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.TurnoRotativoVO;
import cl.femase.gestionweb.vo.TurnoVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import cl.femase.gestionweb.vo.VacacionesVO;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
*
* @author Alexander
*/
public class JsonListServlet extends BaseServlet {

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
        
        System.out.println(WEB_NAME+"[JsonListServlet]processRequest()");
        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
      
        CentroCostoBp cencosBp = 
            new CentroCostoBp(appProperties);
        EmpleadosBp empleadosBp = 
            new EmpleadosBp(appProperties);
        TurnoRotativoBp turnoRotativoBp = 
            new TurnoRotativoBp(appProperties);
        ProcesosBp procesosBp = 
            new ProcesosBp(appProperties);
        
        String empresaID = request.getParameter("empresaID");
        String deptoID = request.getParameter("deptoID");
        String cencoID = null;
        /**
         * En este parametro vendra 'deptoId|cencoId'
         */
        String paramCencoID         = request.getParameter("cencoID");
        System.out.println(WEB_NAME+"[JsonListServlet]"
            + "paramCencoID= " + paramCencoID);
        if (paramCencoID != null){
            try{
                StringTokenizer tokenCenco  = new StringTokenizer(paramCencoID, "|");
                if (tokenCenco.countTokens() > 0){
                    while (tokenCenco.hasMoreTokens()){
                        empresaID   = tokenCenco.nextToken();
                        deptoID     = tokenCenco.nextToken();
                        cencoID     = tokenCenco.nextToken();
                    }
                }
            }catch(NoSuchElementException nelex){
                empresaID = request.getParameter("empresaID");
                
                cencoID = paramCencoID;
            }
        }
                
        System.out.println(WEB_NAME+"[JsonListServlet]"
            + "paramCencoID= " + paramCencoID
            + ",empresaId: " + empresaID
            + ",deptoId: " + deptoID
            + ",cencoId: " + cencoID);
        
        String rutEmpleado = request.getParameter("rutEmpleado");
        String empresaTurnoID = request.getParameter("empresaTurnoID"); 
        String empresaProcesoID = request.getParameter("empresaProcesoID"); 
        String source = request.getParameter("source");
        System.out.println(WEB_NAME+"[JsonListServlet]param source: "+source);
        List<DepartamentoVO> listaDeptos;
        List<CentroCostoVO> listaCencos;
        List<EmpleadoVO> listaEmpleados;
        List<DispositivoVO> listaDispositivos;
        List<TurnoRotativoVO> listaTurnosRotativos;
        List<ProcesoVO> listaProcesos;
        List<VacacionesVO> listaVacaciones;
                
        String jsonFinalString = null;
        boolean cargarDeptos = false;
        boolean cargarCencos = false;
        boolean cargarEmpleados = false;
        boolean cargarDispositivos = false;
        boolean cargarTurnosRotativos = false;
        boolean cargarAllTurnos = false;
        boolean cargarProcesos = false;
        boolean cargarTurnosNormales = false;
        boolean cargarSaldoDiasVacaciones = false;
        boolean excluirArt22 = false;
        
        if (empresaID != null && deptoID != null && cencoID != null && source!=null) {
            cargarEmpleados = true;
        }
        else if (empresaID != null && deptoID != null && cencoID != null) cargarDispositivos = true;
        else if (empresaID != null && deptoID!=null) cargarCencos = true;
        else if (empresaID != null && deptoID == null) cargarDeptos = true;
        
        if (empresaTurnoID != null){
            cargarTurnosRotativos = true;
        }
        if (empresaProcesoID != null){
            cargarProcesos = true;
        }
        
        if (source != null && source.compareTo("cargar_turnos_by_cenco") == 0){
            cargarTurnosNormales = true;
        }else if (source != null && source.compareTo("cargar_allturnos_by_cenco") == 0){
            cargarAllTurnos = true;
        }else if (source != null 
                && (source.compareTo("informe_festivos_trabajados") == 0 
                    || source.compareTo("informe_hrs_extras") == 0
                    || source.compareTo("informe_jornada") == 0)){
            excluirArt22 = true;
        }else if (source != null && source.compareTo("carga_saldo_vacaciones") == 0){
            cargarSaldoDiasVacaciones = true;
        }
                   
        //lista de departamentos. Key=empresa_id
        LinkedHashMap<String,List<DepartamentoVO>> allDeptos = (LinkedHashMap<String,List<DepartamentoVO>>)session.getAttribute("allDepartamentos");
                    
        //lista de centros de costo. Key=depto_id
        LinkedHashMap<String,List<CentroCostoVO>> allCencos = (LinkedHashMap<String,List<CentroCostoVO>>)session.getAttribute("allCencos");
              
        System.out.println(WEB_NAME+"[JsonListServlet]"
            + "empresaID= " + empresaID
            + ",empresaTurnoID= " + empresaTurnoID
            + ",empresaProcesoID= " + empresaProcesoID
            + ", deptoID= " + deptoID
            + ", cencoID= " + cencoID
            + ", cargarDeptos= " + cargarDeptos
            + ", cargarCencos= " + cargarCencos
            + ", cargarEmpleados= " + cargarEmpleados
            + ", cargarDispositivos= " + cargarDispositivos
            + ", cargarTurnosRotativos= " + cargarTurnosRotativos
            + ", cargarProcesos= " + cargarProcesos);
        
        if (cargarDeptos){
            //cargando deptos para la empresa seleccionada
            System.out.println(WEB_NAME+"[JsonListServlet.processRequest]cargarDeptos: "
                + "empresaID= " + empresaID);
            listaDeptos = new ArrayList<>();
            if (empresaID.compareTo("-1") == 0) {
                listaDeptos.add(new DepartamentoVO("-1","Seleccione Departamento",""));
            }else{
                listaDeptos = allDeptos.get(empresaID);
            }
            jsonFinalString = new Gson().toJson(listaDeptos);
        }else if (cargarCencos){
            System.out.println(WEB_NAME+"[JsonListServlet.processRequest]cargarCencos: "
                + "empresaID= " + empresaID
                + ", deptoID= " + deptoID);
                
            //cargando centros de costo para la empresa y depto seleccionados
            listaCencos = new ArrayList<>();
            if (deptoID.compareTo("-1") == 0) {
                listaCencos.add(new CentroCostoVO(Integer.parseInt("-1"),"Seleccione Centro de costo",1,""));
            }else{
                listaCencos = allCencos.get(deptoID);
            }
            jsonFinalString = new Gson().toJson(listaCencos);
        }else if (cargarSaldoDiasVacaciones){
                    System.out.println(WEB_NAME+"[JsonListServlet.processRequest]Obtener Saldo dias vacaciones: "
                        + "empresaID= " + empresaID
                        + ", rutEmpleado= " + rutEmpleado);
                    VacacionesBp vacacionesbp = new VacacionesBp(appProperties);
                    List<VacacionesVO> infovacaciones = 
                        vacacionesbp.getInfoVacaciones(empresaID, rutEmpleado, -1, 0, -1, "rut_empleado");
                    VacacionesVO auxVacaciones = infovacaciones.get(0);
                    
                    listaVacaciones = new ArrayList<>();
                    if (infovacaciones.isEmpty()) {
                        listaVacaciones.add(new VacacionesVO());
                    }else{
                        listaVacaciones.add(auxVacaciones);
                    }
                    jsonFinalString = new Gson().toJson(listaVacaciones);
        }
        else if (!cargarTurnosNormales && cargarEmpleados){
            System.out.println(WEB_NAME+"[JsonListServlet.processRequest]cargarEmpleados: "
                + "empresaID= " + empresaID
                + ", deptoID= " + deptoID
                + ", cencoID= " + cencoID);
            //cargando empleados para la empresa, depto y cenco seleccionados
            listaEmpleados = new ArrayList<>();
            if (cencoID.compareTo("-1") == 0) {
                listaEmpleados.add(new EmpleadoVO());
            }else{
                System.out.println(WEB_NAME+"[JsonListServlet.processRequest]cargarEmpleados. "
                    + "nomPerfil: "+userConnected.getNomPerfil()
                    +", excluirArt22=" + excluirArt22);
                if (userConnected.getNomPerfil().compareTo("Empleado") != 0){
                    if (source != null 
                            && (source.compareTo("reporte_asistencia") == 0 
                            || source.compareTo("marcacion_virtual") == 0 || excluirArt22)){
                        System.out.println(WEB_NAME+"[JsonListServlet."
                            + "processRequest]cargarEmpleados Vigentes y sin articulo22");
                        EmpleadoVO filtroEmpleado = new EmpleadoVO();
                        filtroEmpleado.setEmpresaId(empresaID);
                        filtroEmpleado.setDeptoId(deptoID);
                        filtroEmpleado.setCencoId(Integer.parseInt(cencoID));
                        filtroEmpleado.setEstado(-1);
                        filtroEmpleado.setEmpleadoVigente(true);
                        filtroEmpleado.setArticulo22(false);
                        
                        listaEmpleados = 
                            empleadosBp.getEmpleadosByFiltro(filtroEmpleado);
                    }else{
                        System.out.println(WEB_NAME+"[JsonListServlet."
                            + "processRequest]"
                            + "CargarEmpleados 2. "
                            + "excluirArt22 = " + excluirArt22
                            + ", source = " + source);
                        if (source !=null && source.compareTo("vacaciones_desvincula2") != 0){
                            listaEmpleados = empleadosBp.getEmpleados(empresaID,
                                deptoID,
                                Integer.parseInt(cencoID),
                                -1,
                                null,
                                null,
                                null,
                                null,
                                Constantes.ESTADO_VIGENTE);
                        }else{
                            System.out.println(WEB_NAME+"[JsonListServlet."
                                + "processRequest]"
                                + "Mostrar Empleados desvinculados.");
                            listaEmpleados = empleadosBp.getEmpleadosDesvinculados(empresaID,
                                deptoID,
                                Integer.parseInt(cencoID));
                        }
                    }
                }else{
                    //Mostrar empleado filtrando por rut
                    String aux = userConnected.getUsername();
                    aux = aux.replace(".", "");
                    System.out.println(WEB_NAME+"[JsonListServlet.processRequest]cargarEmpleados. "
                        + " username: "+userConnected.getUsername()
                            + ", filtro rut: "+aux);
                    
                    //22.569.152-5
                    listaEmpleados = empleadosBp.getEmpleados(empresaID, 
                        deptoID, 
                        Integer.parseInt(cencoID),
                        -1,
                        aux, 
                        null, 
                        null, 
                        null, 
                        0, 
                        0, 
                        "empl.empl_rut");
                }
                
                System.out.println(WEB_NAME+"[JsonListServlet.processRequest]cargarEmpleados: "
                    + "size empleados= " + listaEmpleados.size());
            }
            jsonFinalString = new Gson().toJson(listaEmpleados);
        }else if (cargarDispositivos){
                System.out.println(WEB_NAME+"[JsonListServlet.processRequest]cargarDispositivos: "
                    + "cencoID= " + cencoID);
                //cargando dispositivos asociados a centro de costo
                listaDispositivos = new ArrayList<>();
                if (cencoID.compareTo("-1") == 0) {
                    DispositivoVO auxDevice = new DispositivoVO();
                    auxDevice.setId("-1");
                    auxDevice.setNombreTipo("Sin Asignar");
                    listaDispositivos.add(auxDevice);
                }else{
                    listaDispositivos = cencosBp.getDispositivosAsignados(Integer.parseInt(cencoID));
                }
                jsonFinalString = new Gson().toJson(listaDispositivos);
        }else if (cargarTurnosNormales){
                System.out.println(WEB_NAME+"[JsonListServlet."
                    + "processRequest]cargarTurnosNormales"
                    + ". empresa|depto|cencoID= " + empresaID + "|" + deptoID + "|" + cencoID);
                TurnosBp turnosBp      = new TurnosBp(appProperties);
                LinkedHashMap<Integer, TurnoVO> hashTurnosCenco = turnosBp.getTurnosAsignadosByCenco(empresaID, deptoID, Integer.parseInt(cencoID));

                // Converting HashMap Values into ArrayList
                List<TurnoVO> listaTurnos = new ArrayList<>(hashTurnosCenco.values());
                //Getting Collection of values from HashMap
                Collection<TurnoVO> values = hashTurnosCenco.values();

                //Creating an ArrayList of values
                List<TurnoVO> listOfValues = new ArrayList<>(values);
//                System.out.println(WEB_NAME+"[JsonListServlet."
//                    + "processRequest]turnos.size = "+listOfValues.size());
//                for (TurnoVO value : listOfValues) 
//                {
//                    System.out.println(WEB_NAME+"[JsonListServlet."
//                        + "processRequest]"
//                        + "turnos.empresaId = " + value.getEmpresaId()
//                        + ", turnos.id = " + value.getId()
//                        + ", turnos.nombre = " + value.getNombre());
//                }
                jsonFinalString = new Gson().toJson(listOfValues);
        }else if (cargarAllTurnos){
                System.out.println(WEB_NAME+"[JsonListServlet."
                    + "processRequest]cargar Turnos Normales y rotativos"
                    + ". empresa|depto|"
                    + "cencoID= " + empresaID + "|" + deptoID + "|" + cencoID);
                TurnosBp turnosBp      = new TurnosBp(appProperties);
                LinkedHashMap<Integer, TurnoVO> hashTurnosCenco = 
                    turnosBp.getAllTurnosAsignadosByCenco(empresaID, 
                        deptoID, 
                        Integer.parseInt(cencoID));

                // Converting HashMap Values into ArrayList
                //List<TurnoVO> listaTurnos = new ArrayList<>(hashTurnosCenco.values());
                //Getting Collection of values from HashMap
                Collection<TurnoVO> values = hashTurnosCenco.values();

                //Creating an ArrayList of values
                List<TurnoVO> listOfValues = new ArrayList<>(values);
                jsonFinalString = new Gson().toJson(listOfValues);
        }
        
        if (cargarTurnosRotativos){
            //cargando turnos rotativos para la empresa seleccionada
            System.out.println(WEB_NAME+"[JsonListServlet.processRequest]"
                + "cargarTurnos rotativos: "
                + "empresaTurnoID= " + empresaTurnoID
                + ",source= " + source);
            listaTurnosRotativos = new ArrayList<>();
            if (empresaTurnoID.compareTo("-1") == 0) {
                listaTurnosRotativos.add(new TurnoRotativoVO(-1,"Seleccione turno"));
            }else{
                listaTurnosRotativos = 
                    turnoRotativoBp.getTurnos(empresaTurnoID, null, -1, 0, 0, "hora_entrada");
//                if (source!=null && source.compareTo("crearAsignacionTurnoRotativo") == 0){
//                    //mostrar solo los turnos rotativos sin detalle creado
//                    listaTurnosRotativos = turnoRotativoBp.getTurnosSinDetalle(empresaTurnoID);
//                }else{
//                    //mostrar solo los turnos rotativos con detalle creado
//                    listaTurnosRotativos = turnoRotativoBp.getTurnosConDetalle(empresaTurnoID);
//                }
            }
            jsonFinalString = new Gson().toJson(listaTurnosRotativos);
        }else if (cargarProcesos){
            //cargando procesos para la empresa seleccionada
            System.out.println(WEB_NAME+"[JsonListServlet.processRequest]"
                + "cargar Procesos: "
                + "empresaProcesoID= " + empresaProcesoID
                + ",source= " + source);
            listaProcesos = new ArrayList<>();
            if (empresaProcesoID != null && empresaProcesoID.compareTo("-1") == 0) {
                listaProcesos.add(new ProcesoVO(null,-1,"Seleccione proceso"));
            }else{
                listaProcesos = procesosBp.getProcesos(empresaProcesoID, null);
            }
            jsonFinalString = new Gson().toJson(listaProcesos);
        }
        
        response.setContentType("application/json");
        response.getWriter().write(jsonFinalString);
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
