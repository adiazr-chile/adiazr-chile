/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.servlet;

import cl.femase.gestionweb.business.ComunaBp;
import cl.femase.gestionweb.business.DepartamentoBp;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.MaintenanceEventsBp;
import cl.femase.gestionweb.business.TipoDispositivoBp;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.dao.DispositivoMovilDAO;
import cl.femase.gestionweb.vo.AfpVO;
import cl.femase.gestionweb.vo.AusenciaVO;
import cl.femase.gestionweb.vo.CargoVO;
import cl.femase.gestionweb.vo.DetalleAusenciaVO;
import cl.femase.gestionweb.vo.CentroCostoVO;
import cl.femase.gestionweb.vo.CodigoErrorRechazoVO;
import cl.femase.gestionweb.vo.ComunaVO;
import cl.femase.gestionweb.vo.DepartamentoVO;
import cl.femase.gestionweb.vo.DispositivoMovilVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.EmpresaVO;
import cl.femase.gestionweb.vo.PerfilUsuarioVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.RegionVO;
import cl.femase.gestionweb.vo.TipoAusenciaVO;
import cl.femase.gestionweb.vo.TipoDispositivoVO;
import cl.femase.gestionweb.vo.TipoMarcaManualVO;
import cl.femase.gestionweb.vo.TurnoVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander
 */
public class LoadItems extends BaseServlet {

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    
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
        // Set standard HTTP/1.1 no-cache headers.
        
        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        if (session!=null) session.removeAttribute("mensaje");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        
        String type = request.getParameter("type");
        //System.out.println("[LoadItems]Type: "+type);
        String listData = "{\"Result\":\"OK\",\"Options\":["; 
        if (type.compareTo("estados") == 0) {
            LinkedHashMap<String,String> estados = 
                    new LinkedHashMap<String,String>();
            estados.put("1", "Vigente");
            estados.put("2", "No Vigente");
            
            for( String key : estados.keySet() ){
              String label = estados.get(key);
              listData += "{\"DisplayText\":\""+label+"\",\"Value\":\""+key+"\"},";
            }
        }else if (type.compareTo("sexos") == 0){
             LinkedHashMap<String,String> sexos = 
                    new LinkedHashMap<String,String>();
            sexos.put("M", "Masculino");
            sexos.put("F", "Femenino");
            for( String key : sexos.keySet() ){
              String label = sexos.get(key);
              listData += "{\"DisplayText\":\""+label+"\",\"Value\":\""+key+"\"},";
            }
        }else if (type.compareTo("tiposMarcas") == 0){
            LinkedHashMap<String,String> tipos = new LinkedHashMap<>();
            tipos.put("1", "Entrada");
            tipos.put("2", "Salida");
            tipos.put("100", "Falta Marca Entrada");
            tipos.put("200", "Falta Marca Salida");
            tipos.put("300", "Ausencia");
            tipos.put("400", "Faltan ambas marcas");
            tipos.put("500", "Libre");
            tipos.put("600", "Feriado");
        
            for( String key : tipos.keySet() ){
              String label = tipos.get(key);
              listData += "{\"DisplayText\":\""+label+"\",\"Value\":\""+key+"\"},";
            }
            System.out.println("[LoadItems]json tipos marcas: "+listData);
        }else if (type.compareTo("tipo_feriado_1") == 0){
                LinkedHashMap<String,String> tipos = new LinkedHashMap<>();
                tipos.put("Civil", "Civil");
                tipos.put("Religioso", "Religioso");
                
                for( String key : tipos.keySet() ){
                  String label = tipos.get(key);
                  listData += "{\"DisplayText\":\""+label+"\",\"Value\":\""+key+"\"},";
                }
        }
        else if (type.compareTo("tipos_feriados") == 0){
                LinkedHashMap<String,String> tipos = new LinkedHashMap<>();
                tipos.put("1", "Regional");
                tipos.put("2", "Comunal");
                tipos.put("3", "Nacional");
                
                for( String key : tipos.keySet() ){
                  String label = tipos.get(key);
                  listData += "{\"DisplayText\":\""+label+"\",\"Value\":\""+key+"\"},";
                }
        }else if (type.compareTo("irrenunciable") == 0){
                LinkedHashMap<String,String> tipos = new LinkedHashMap<>();
                tipos.put("0", "No");
                tipos.put("1", "Si");
                
                for( String key : tipos.keySet() ){
                  String label = tipos.get(key);
                  listData += "{\"DisplayText\":\""+label+"\",\"Value\":\""+key+"\"},";
                }
        }else if (type.compareTo("horasTope") == 0){
                String auxHHmm = request.getParameter("hhmm");
                System.out.println("[LoadItems.horasTope]hhmm: "+auxHHmm);
                StringTokenizer tokenhhmm = new StringTokenizer(auxHHmm, ":");
                String soloHoras = tokenhhmm.nextToken();
                int intHoras    = Integer.parseInt(soloHoras);
                
                LinkedHashMap<String,String> horasTope= new LinkedHashMap<>();
                String hh="";
                for (int x = 0; x <= intHoras; x++){
                    hh = String.valueOf(x);
                    if (x < 10) hh = "0"+x;
                    
                    horasTope.put(hh, hh);
                }
                
                for( String key : horasTope.keySet() ){
                  String label = horasTope.get(key);
                  listData += "{\"DisplayText\":\""+label+"\",\"Value\":\""+key+"\"},";
                }
        }else if (type.compareTo("horas") == 0){
                LinkedHashMap<String,String> horas = new LinkedHashMap<>();
                String hh="";
                for (int x = 0; x <= 23; x++){
                    hh = String.valueOf(x);
                    if (x < 10) hh = "0"+x;
                    
                    horas.put(hh + ":00:00", hh + ":00:00");
                    horas.put(hh + ":30:00", hh + ":30:00");
                }
                
                for( String key : horas.keySet() ){
                  String label = horas.get(key);
                  listData += "{\"DisplayText\":\""+label+"\",\"Value\":\""+key+"\"},";
                }
        }else if (type.compareTo("perfiles") == 0) {
//                PerfilUsuarioBp perfilesbp=new PerfilUsuarioBp(appProperties);    
                List<PerfilUsuarioVO> perfiles = (List<PerfilUsuarioVO>)session.getAttribute("perfiles");//perfilesbp.getPerfiles(null, 0, 0, "perfil_nombre");
                Iterator<PerfilUsuarioVO> iterator = perfiles.iterator();
                while (iterator.hasNext()) {
                    PerfilUsuarioVO auxobj = iterator.next();
                    listData += "{\"DisplayText\":\""+auxobj.getNombre()+"\",\"Value\":\""+auxobj.getId()+"\"},";
                }
        }else if (type.compareTo("regiones") == 0) {
                List<RegionVO> regiones = (List<RegionVO>)session.getAttribute("regiones");
                Iterator<RegionVO> iterator = regiones.iterator();
                while (iterator.hasNext()) {
                    RegionVO auxobj = iterator.next();
                    String label = "[" + auxobj.getNombreCorto()+"] " 
                        + auxobj.getNombre();
                    listData += "{\"DisplayText\":\""+auxobj.getNombre()+"\",\"Value\":\""+auxobj.getRegionId()+"\"},";
                }
        }else if (type.compareTo("comunas") == 0) {
                ComunaBp comunasbp=new ComunaBp(appProperties);
                String strRegionParam = request.getParameter("regionId");
                int intRegionParam = -1;
                if (strRegionParam != null){
                    intRegionParam = Integer.parseInt(strRegionParam);
                } 
                List<ComunaVO> comunas = comunasbp.getComunas(null, intRegionParam, 0, 0, "region.short_name");
                Iterator<ComunaVO> iterator = comunas.iterator();
                while (iterator.hasNext()) {
                    ComunaVO comunaObj = iterator.next();
                    String label = "["+comunaObj.getRegionShortName()+"] " + comunaObj.getNombre();
                    listData += "{\"DisplayText\":\""+label+"\",\"Value\":\""+comunaObj.getId()+"\"},";
                }
                //System.out.println("Comunas-JSON: " + listData);
        }else if (type.compareTo("comunas_feriados") == 0) {
                ComunaBp comunasbp=new ComunaBp(appProperties);
                String strRegionParam = request.getParameter("regionId");
                int intRegionParam = -1;
                if (strRegionParam != null){
                    intRegionParam = Integer.parseInt(strRegionParam);
                } 
                List<ComunaVO> comunas = 
                    comunasbp.getComunas(null, 
                        intRegionParam, 
                        0, 
                        0, 
                        "region.short_name");
                listData += "{\"DisplayText\":\""+"Cualquiera"+"\",\"Value\":\""+"-1"+"\"},";
                Iterator<ComunaVO> iterator = comunas.iterator();
                while (iterator.hasNext()) {
                    ComunaVO comunaObj = iterator.next();
                    String label = "["+comunaObj.getRegionShortName()+"] " + comunaObj.getNombre();
                    listData += "{\"DisplayText\":\""+label+"\",\"Value\":\""+comunaObj.getId()+"\"},";
                }
                
                //System.out.println("Comunas-JSON: " + listData);
        }else if (type.compareTo("moviles") == 0) {
                System.out.println("[LoadItems]Cargando lista de moviles...");
                String strCencoId = request.getParameter("cencoId");
                int intCencoId = (strCencoId != null)?Integer.parseInt(strCencoId):-1;
                DispositivoMovilDAO movilesDao = new DispositivoMovilDAO(appProperties);
                List<DispositivoMovilVO> moviles = movilesDao.getDispositivosByCencoId(intCencoId);
                listData += "{\"DisplayText\":\""+"Ninguno"+"\",\"Value\":\""+"-1"+"\"},";
                if (!moviles.isEmpty()){
                    Iterator<DispositivoMovilVO> iterator = moviles.iterator();
                    while (iterator.hasNext()) {
                        DispositivoMovilVO movil = iterator.next();
                        String label = "[" + movil.getId() + "]";
                        listData += "{\"DisplayText\":\"" + label + "\",\"Value\":\"" + movil.getId() + "\"},";
                    }
                }
        }else if (type.compareTo("empresas") == 0) {
                System.out.println("[LoadItems]Cargando lista de empresas...");
                List<EmpresaVO> empresas = (List<EmpresaVO>)session.getAttribute("empresas");//empresasbp.getEmpresas(null, 0, 0, "empresa_nombre");
                Iterator<EmpresaVO> iterator = empresas.iterator();
                while (iterator.hasNext()) {
                    EmpresaVO auxobj = iterator.next();
                    listData += "{\"DisplayText\":\""+auxobj.getNombre()+"\",\"Value\":\""+auxobj.getId()+"\"},";
                }
        }else if (type.compareTo("afps") == 0) {
                System.out.println("[LoadItems]Cargando lista de afps...");
                HashMap<String, AfpVO> hashAfps = (HashMap<String, AfpVO>)session.getAttribute("afps");
                for (Map.Entry mapElement : hashAfps.entrySet()) { 
                    String key = (String)mapElement.getKey(); 
                    AfpVO auxobj = (AfpVO)mapElement.getValue();
                    listData += "{\"DisplayText\":\""+auxobj.getNombre()+"\",\"Value\":\""+auxobj.getCode()+"\"},";
                }
        }
        else if (type.compareTo("departamentos") == 0) {
                //listData += "{\"DisplayText\":\""+"Ninguno"+"\",\"Value\":\""+"-1"+"\"},";
                DepartamentoBp deptosbp = new DepartamentoBp(appProperties);    
                String strEmpresaParam = request.getParameter("empresaId");
                List<DepartamentoVO> departamentos = 
                    deptosbp.getDepartamentos(strEmpresaParam, null, 0, 0, "depto_nombre");
                Iterator<DepartamentoVO> iterator = departamentos.iterator();
                while (iterator.hasNext()) {
                    DepartamentoVO auxobj = iterator.next();
                    //System.err.println("LoadItems.deptoID: "+auxobj.getId());
                    listData += "{\"DisplayText\":\""+auxobj.getNombre()+"\",\"Value\":\""+auxobj.getId()+"\"},";
                }
        }else if (type.compareTo("cencos") == 0) {
                listData += "{\"DisplayText\":\""+"Ninguno"+"\",\"Value\":\""+"-1"+"\"},";
                //CentroCostoBp cencosbp = new CentroCostoBp(appProperties);    
                List<CentroCostoVO> cencos = (List<CentroCostoVO>)session.getAttribute("cencos");//cencosbp.getCentrosCosto(null, 1, 0, 0, "ccosto_nombre");
                Iterator<CentroCostoVO> iterator = cencos.iterator();
                while (iterator.hasNext()) {
                    CentroCostoVO auxobj = iterator.next();
                    listData += "{\"DisplayText\":\""+auxobj.getNombre()+"\",\"Value\":\""+auxobj.getId()+"\"},";
                }
        }else if (type.compareTo("tipos_eventos") == 0) {
                    MaintenanceEventsBp eventosbp = new MaintenanceEventsBp(appProperties);    
                    LinkedHashMap<String, String> tipos = eventosbp.getEventTypes();
                    for(String key : tipos.keySet()) {
                        String name = tipos.get(key);
                        listData += "{\"DisplayText\":\""+name+"\",\"Value\":\""+key+"\"},";
                    }
        }else if (type.compareTo("tipos_dispositivos") == 0) {
                    TipoDispositivoBp tipos2bp = new TipoDispositivoBp(appProperties);    
                    List<TipoDispositivoVO> tipos2 = tipos2bp.getTipos(null, 0, 0, "dev_type_name");
                    Iterator<TipoDispositivoVO> iterator2 = tipos2.iterator();
                    while (iterator2.hasNext()) {
                        TipoDispositivoVO auxobj = iterator2.next();
                        listData += "{\"DisplayText\":\""+auxobj.getName()+"\",\"Value\":\""+auxobj.getId()+"\"},";
                    }
        }else if (type.compareTo("turnos") == 0) {
                //TurnosBp turnosBp = new TurnosBp(appProperties);    
                List<TurnoVO> auxturnos = (List<TurnoVO>)session.getAttribute("turnos");//turnosBp.getTurnos(null, 0, 0, "nombre_turno");
                Iterator<TurnoVO> iterTurnos = auxturnos.iterator();
                while (iterTurnos.hasNext()) {
                    TurnoVO auxobj = iterTurnos.next();
                    System.out.println("[LoadItems]"
                        + "itera turno id: " + auxobj.getId() 
                        + ", nombre:  " + auxobj.getNombre() );
                    listData += "{\"DisplayText\":\""+auxobj.getNombre()+"\",\"Value\":\""+auxobj.getId()+"\"},";
                }
        }else if (type.compareTo("tipos_ausencia") == 0) {
                //TipoAusenciaBp tipoAusenciaBp = new TipoAusenciaBp(appProperties);    
                List<TipoAusenciaVO> auxtiposausencia = (List<TipoAusenciaVO>)session.getAttribute("tipos_ausencia");//tipoAusenciaBp.getTipos(null, 0, 0, "tp_ausencia_nombre");
                Iterator<TipoAusenciaVO> iterTiposAusencia = auxtiposausencia.iterator();
                while (iterTiposAusencia.hasNext()) {
                    TipoAusenciaVO auxobj = iterTiposAusencia.next();
                    listData += "{\"DisplayText\":\""+auxobj.getNombre()+"\",\"Value\":\""+auxobj.getId()+"\"},";
                }
        }else if (type.compareTo("autorizadores") == 0) {
                //DetalleAusenciaBp autorizaAusenciaBp = new DetalleAusenciaBp(appProperties);    
                List<DetalleAusenciaVO> auxautorizadores = (List<DetalleAusenciaVO>)session.getAttribute("autorizadores");//autorizaAusenciaBp.getAutorizadoresDisponibles();
                Iterator<DetalleAusenciaVO> iterAutorizadores = auxautorizadores.iterator();
                while (iterAutorizadores.hasNext()) {
                    DetalleAusenciaVO auxobj = iterAutorizadores.next();
                    String label = auxobj.getNombreAutorizador()
                        + "[" + auxobj.getNomDeptoAutorizador()
                        + "-" + auxobj.getNomCencoAutorizador() + "]";
                    listData += "{\"DisplayText\":\"" + label + "\",\"Value\":\""+auxobj.getRutAutorizador()+"\"},";
                }
        }else if (type.compareTo("ausenciassinvacas") == 0) {//ausenciassinvacas
                List<AusenciaVO> auxAusencias = (List<AusenciaVO>)session.getAttribute("ausencias");
                Iterator<AusenciaVO> iterAusencias = auxAusencias.iterator();
                while (iterAusencias.hasNext()) {
                    AusenciaVO auxobj = iterAusencias.next();
                    if (auxobj.getId() != 1){
                        listData += "{\"DisplayText\":\""+auxobj.getNombre()+"\",\"Value\":\""+auxobj.getId()+"\"},";
                    }
                }
        }else if (type.compareTo("empleados") == 0) {
                EmpleadosBp empleadosBp = new EmpleadosBp(appProperties); 
                String paramEmpresa = request.getParameter("empresa");
                String paramDepto = request.getParameter("depto");
                String paramCenco = request.getParameter("cenco");
                int intCenco = -1;
                if (paramCenco != null) intCenco = Integer.parseInt(paramCenco);
                
                List<EmpleadoVO> auxEmpleados = new ArrayList<>();
                if ( (paramEmpresa != null && paramEmpresa.compareTo("-1") != 0)
                        && (paramDepto != null && paramDepto.compareTo("-1") != 0)
                        && (intCenco != -1) ){
                    auxEmpleados = 
                        empleadosBp.getEmpleados(paramEmpresa,
                            paramDepto,
                            intCenco,
                            -1,
                            null,
                            null,
                            null,
                            null,
                            1);
                }else{
                    listData += "{\"DisplayText\":\""+"Seleccione centro de costo"+"\",\"Value\":\""+"-1"+"\"},";
                }
                Iterator<EmpleadoVO> iterEmpleados = auxEmpleados.iterator();
                while (iterEmpleados.hasNext()) {
                    EmpleadoVO auxobj = iterEmpleados.next();
                    String auxLabel = "[" + auxobj.getRut()+"] "
                        + auxobj.getNombres() +" " 
                        + auxobj.getApePaterno()+" " 
                        + auxobj.getApeMaterno(); 
                    listData += "{\"DisplayText\":\"" + auxLabel +"\",\"Value\":\"" + auxobj.getRut() + "\"},";
                }
        }else if (type.compareTo("cargos") == 0) {
                //CargoBp ausenciasBp = new CargoBp(appProperties);    
                List<CargoVO> auxCargos =(List<CargoVO>)session.getAttribute("cargos"); 
                        //ausenciasBp.getCargos(null, -1, 0,0, "cargo_nombre");
                Iterator<CargoVO> iterCargos = auxCargos.iterator();
                while (iterCargos.hasNext()) {
                    CargoVO auxobj = iterCargos.next();
                    
                    listData += "{\"DisplayText\":\""+auxobj.getNombre()+"\",\"Value\":\""+auxobj.getId()+"\"},";
                }
//                System.out.println("cl.femase.gestionweb.servlet."
//                        + "LoadItems.processRequest(). Cargos: "+listData);
        }else if (type.compareTo("usuarios") == 0) {
                System.out.println("[LoadItems]Cargando lista de usuarios...");
                List<UsuarioVO> listaUsuarios =(List<UsuarioVO>)session.getAttribute("usuarios"); 
                System.out.println("[LoadItems]Numero de usuarios= "+listaUsuarios.size());
                Iterator<UsuarioVO> iterUsuarios = listaUsuarios.iterator();
                while (iterUsuarios.hasNext()) {
                    UsuarioVO auxobj = iterUsuarios.next();
                    listData += "{\"DisplayText\":\""+auxobj.getLabelUsuario()+"\",\"Value\":\""+auxobj.getUsername()+"\"},";
                }
        }else if (type.compareTo("empresas") == 0) {
                //CargoBp ausenciasBp = new CargoBp(appProperties);    
                List<EmpresaVO> auxEmpresas =(List<EmpresaVO>)session.getAttribute("empresas"); 
                        //ausenciasBp.getCargos(null, -1, 0,0, "cargo_nombre");
                Iterator<EmpresaVO> iterEmpresas = auxEmpresas.iterator();
                while (iterEmpresas.hasNext()) {
                    EmpresaVO auxobj = iterEmpresas.next();
                    listData += "{\"DisplayText\":\""+auxobj.getNombre()+"\",\"Value\":\""+auxobj.getId()+"\"},";
                }
        }else if (type.compareTo("tpmarcamanual") == 0) {
                //System.out.println("[LoadItems]Cargar lista de tipos de marcas manuales...");
                List<TipoMarcaManualVO> auxtipos = (List<TipoMarcaManualVO>)session.getAttribute("tiposMarcasManuales");
                Iterator<TipoMarcaManualVO> iterTipos = auxtipos.iterator();
                while (iterTipos.hasNext()) {
                    TipoMarcaManualVO auxobj = iterTipos.next();
                    listData += "{\"DisplayText\":\"" + auxobj.getNombre()
                            + "\",\"Value\":\"" + auxobj.getCode() + "\"},";
                    
                }
                //System.out.println("[LoadItems]json tipos marcas manuales: "+listData);
        }else if (type.compareTo("codErrorRechazo") == 0) {
                List<CodigoErrorRechazoVO> erroresRechazos = 
                    (List<CodigoErrorRechazoVO>)session.getAttribute("errores_rechazos");
                Iterator<CodigoErrorRechazoVO> iterator = erroresRechazos.iterator();
                while (iterator.hasNext()) {
                    CodigoErrorRechazoVO auxobj = iterator.next();
                    listData += "{\"DisplayText\":\""+auxobj.getDescripcion()+"\",\"Value\":\""+auxobj.getCodigo()+"\"},";
                }
        }else if (type.compareTo("acciones_solicitud") == 0) {
                String estadoSolicitud  = request.getParameter("estadoSolicitud");//estado actual de la solicitud
                String userSolicita     = request.getParameter("userSolicita");
                System.out.println("[servlet.LoadItems]"
                    + "estadoSolicitud: " + estadoSolicitud
                    + ", userSolicita: " + userSolicita);
                /**
                 * ID perfil:
                *   administrdor = 1
                *   director     = 5
                *   empleado     = 7
                * 
                *   SI (perfil = director && estadoSolicitud=´Pendiente'){
                *       estados = {'Aceptar','Rechazar'}
                *   }else SI (perfil = empleado && estadoSolicitud=´Pendiente'){
                *       estados = {'Cancelar'}
                *   }else SI (perfil = admin){
                *       estados = {'Ninguno'}
                *   }
                */
                LinkedHashMap<String,String> estados = 
                    new LinkedHashMap<>();
                if (userConnected.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR) {
                        System.out.println("[servlet.LoadItems]Perfil Director...");
                        //Si es Director
                        if (userSolicita != null && userConnected.getUsername().compareTo(userSolicita) != 0){    
                            if (estadoSolicitud.compareTo(Constantes.ESTADO_SOLICITUD_PENDIENTE) == 0){
                                estados.put("A", "Aprobar");
                                estados.put("R", "Rechazar");
                            }else estados.put("-1", "Ninguna");
                        }else estados.put("C", "Cancelar");
                }else if (userConnected.getIdPerfil() == Constantes.ID_PERFIL_EMPLEADO){
                        System.out.println("[servlet.LoadItems]Perfil Empleado...");
                        //Si es Empleado
                        if (estadoSolicitud.compareTo(Constantes.ESTADO_SOLICITUD_PENDIENTE) == 0){
                            estados.put("C", "Cancelar");
                        }else estados.put("-1", "Ninguna");
                }else if (userConnected.getIdPerfil() == Constantes.ID_PERFIL_ADMIN){
                        System.out.println("[servlet.LoadItems]Perfil Admin, Si es Admin no muestra elementos en el combo");
                        estados.put("-1", "Ninguna");
                }
                
                for( String key : estados.keySet() ){
                  String label = estados.get(key);
                  listData += "{\"DisplayText\":\""+label+"\",\"Value\":\""+key+"\"},";
                }
        }else if (type.compareTo("acciones_aprobar_rechazar") == 0) {
                String estadoSolicitud  = request.getParameter("estadoSolicitud");//estado actual de la solicitud
                String userSolicita     = request.getParameter("userSolicita");
                System.out.println("[servlet.LoadItems]"
                    + "estadoSolicitud: " + estadoSolicitud
                    + ", userSolicita: " + userSolicita);
                LinkedHashMap<String,String> estados = 
                    new LinkedHashMap<>();
                //if (userConnected.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR) {
                        System.out.println("[servlet.LoadItems]Perfil Director...");
                        //Si es Director
                        if (userSolicita != null && userConnected.getUsername().compareTo(userSolicita) != 0){    
                            if (estadoSolicitud.compareTo(Constantes.ESTADO_SOLICITUD_PENDIENTE) == 0){
                                estados.put("A", "Aprobar");
                                estados.put("R", "Rechazar");
                            }else estados.put("-1", "Ninguna");
                        }else estados.put("C", "Cancelar");
                //}
                
                for( String key : estados.keySet() ){
                  String label = estados.get(key);
                  listData += "{\"DisplayText\":\""+label+"\",\"Value\":\""+key+"\"},";
                }
        }
       
        listData    = listData.substring(0, listData.length()-1);
        listData    += "]}";//fin lineas
        //System.out.println("[LoadItems]listData final: "+listData);
        //response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().print(listData);
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
