package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.MaintenanceEventsBp;
import cl.femase.gestionweb.business.MarcasEventosBp;
import cl.femase.gestionweb.business.MarcasBp;
import cl.femase.gestionweb.business.NotificacionBp;
import cl.femase.gestionweb.business.TurnosBp;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.MarcasEventosVO;
import cl.femase.gestionweb.vo.MarcaVO;
import cl.femase.gestionweb.vo.NotificacionVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.IOException;
import java.util.StringTokenizer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class MarcasController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 996L;
    //GetPropertyValues m_properties = new GetPropertyValues();
    
    public MarcasController() {
        
    }

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
            m_logger.debug("Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
        }

    }

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
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
            m_logger.debug("Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
        }
    }

    /**
    * 
    */
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        MarcasBp auxnegocio             = new MarcasBp(appProperties);
        MaintenanceEventsBp eventosBp   = new MaintenanceEventsBp(appProperties);
        TurnosBp turnoBp                = new TurnosBp(appProperties);
        EmpleadosBp empleadosBp         = new EmpleadosBp(appProperties);
        
        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME+"[MarcasController]"
                + "action is: " + request.getParameter("action"));
            LinkedHashMap<String, MarcaVO> hashMarcas = new LinkedHashMap<String, MarcaVO>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("MAR");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                   
            /**
            * En este parametro vendra 'deptoId|cencoId'
            */
            String empresaId= null;
            String deptoId  = null;
            String cencoId  = "-1";
            String paramCencoID         = request.getParameter("cencoId");
            System.out.println(WEB_NAME+"[MarcasController]"
                + "token param 'cencoID'= " + paramCencoID);
            if (paramCencoID != null && paramCencoID.compareTo("-1") != 0){
                StringTokenizer tokenCenco  = new StringTokenizer(paramCencoID, "|");
                if (tokenCenco.countTokens() > 0){
                    while (tokenCenco.hasMoreTokens()){
                        empresaId   = tokenCenco.nextToken();
                        deptoId     = tokenCenco.nextToken();
                        cencoId     = tokenCenco.nextToken();
                    }
                }
            }
            
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "rut_empleado,fecha_hora asc";
            /** filtros de busqueda */
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            System.out.println(WEB_NAME+"MarcasController." +
                " jtSorting: " + request.getParameter("jtSorting"));
            
            if (jtSorting.contains("codDispositivo")) jtSorting = jtSorting.replaceFirst("codDispositivo","cod_dispositivo");
            else if (jtSorting.contains("tipoMarca")) jtSorting = jtSorting.replaceFirst("tipoMarca","cod_tipo_marca");
            else if (jtSorting.contains("rutEmpleado")) jtSorting = jtSorting.replaceFirst("rutEmpleado","rut_empleado");
            else if (jtSorting.contains("fechaHoraStr")) jtSorting = jtSorting.replaceFirst("fechaHoraStr","fecha_hora");
                                         
//            System.out.println(WEB_NAME+"[gestionweb.MarcasController]"
//                + " FechaHoraKey: " + request.getParameter("fechaHoraKey")
//                + ", rutEmpleado: " + request.getParameter("rutEmpleado")
//                + ", empresaCod: " + request.getParameter("empresaCod")
//                + ", empresaKey: " + request.getParameter("empresaKey")    
//                + ", rutKey: " + request.getParameter("rutKey")
//                + ", aEmpresa: " + request.getParameter("aEmpresa")
//                + ", aRut: " + request.getParameter("aRut")      
//                 + ", aDevice: " + request.getParameter("aDevice")        
//                + ", fechaHora: " + request.getParameter("fechaHora")
//                + ", fechaHoraKey: " + request.getParameter("fechaHoraKey")
//                + ", fecha: " + request.getParameter("fecha")
//                + ", hora: " + request.getParameter("hora")
//                + ", minutos: " + request.getParameter("minutos")
//                + ", segundos: " + request.getParameter("segundos")    
//                + ", tipoMarca: " + request.getParameter("tipoMarca")
//                + ", codDispositivo: " + request.getParameter("codDispositivo")
//                + ", id: " + request.getParameter("id")
//                + ", hashcode: " + request.getParameter("hashcode")
//                + ", comentario: " + request.getParameter("comentario")
//                + ", jtSorting: " + request.getParameter("jtSorting"));

            //objeto usado para update/insert
            MarcaVO infomarca = new MarcaVO();
            
            if (request.getParameter("fecha") != null){
                infomarca.setFecha(request.getParameter("fecha"));
            }
            if (request.getParameter("hora") != null){
                infomarca.setHora(request.getParameter("hora"));
            }
            if (request.getParameter("minutos") != null){
                infomarca.setMinutos(request.getParameter("minutos"));
            }
            if (request.getParameter("segundos") != null){
                infomarca.setSegundos(request.getParameter("segundos"));
            }
            if (request.getParameter("rutKey") != null){
                infomarca.setRutEmpleado(request.getParameter("rutKey"));
            }
            if (request.getParameter("empresaCod") != null){
                infomarca.setEmpresaCod(request.getParameter("empresaCod"));
            }
            if (action.compareTo("create") != 0){
                if (request.getParameter("fechaHoraKey") != null){
                    infomarca.setFechaHora(request.getParameter("fechaHoraKey"));
                }
            }
            if (request.getParameter("tipoMarca") != null){
                infomarca.setTipoMarca(Integer.parseInt(request.getParameter("tipoMarca")));
            }
            if (request.getParameter("id") != null){
                infomarca.setId(request.getParameter("id"));
            }
            if (request.getParameter("hashcode") != null){
                infomarca.setHashcode(request.getParameter("hashcode"));
            }
            if (request.getParameter("comentario") != null){
                infomarca.setComentario(request.getParameter("comentario"));
            }
            if (request.getParameter("codTipoMarcaManual") != null){
                infomarca.setCodTipoMarcaManual(Integer.parseInt(request.getParameter("codTipoMarcaManual")));
            }
            boolean listar=true;
            
            if ((cencoId==null || cencoId.compareTo("-1") == 0) &&
                    (request.getParameter("rutEmpleado")!=null && request.getParameter("rutEmpleado").compareTo("-1") == 0) &&
                    request.getParameter("startDate").compareTo("") == 0 &&
                    request.getParameter("endDate").compareTo("") == 0 &&
                    request.getParameter("dispositivo") != null && 
                    request.getParameter("dispositivo").compareTo("-1") == 0){
                listar=false;
            }else if ((request.getParameter("startDate")!=null && request.getParameter("startDate").compareTo("") == 0) &&
                    request.getParameter("endDate").compareTo("") == 0){
                listar = false;
            }
            
            if (action.compareTo("list") == 0){
                //probar con empleado con turno rotativo...
                String paramHashcode=request.getParameter("param_hashcode");
                String paramRunEmpleado = request.getParameter("rutEmpleado");
                String paramStartDate = request.getParameter("startDate");
                String paramEndDate = request.getParameter("endDate");
                System.out.println(WEB_NAME+"[MarcasController]"
                    + "mostrando marcas."+
                        "CencoId= " +cencoId
                        +", rutEmpleado= " +paramRunEmpleado
                        +", dispositivoId= " +request.getParameter("dispositivoId")
                        +", startDate= " +paramStartDate
                        +", endDate= " +paramEndDate);
                try{
                    int objectsCount = 0;
//                    if (listar && (request.getParameter("rutEmpleado")!=null && request.getParameter("rutEmpleado").compareTo("-1") != 0) ){
                    
                    if (listar && (
                            (paramRunEmpleado != null && paramRunEmpleado.compareTo("-1") != 0)
                              || (paramHashcode != null && paramHashcode.compareTo("") != 0 ) 
                            ) 
                        ){    
                                                
                        if ( (paramRunEmpleado == null || paramRunEmpleado.compareTo("-1") == 0) && (paramHashcode != null && paramHashcode.compareTo("") != 0)){
                            System.out.println(WEB_NAME+"[MarcasController]Buscar info del empleado a partir del hashcode de la marca.");
                            MarcaVO marca       = auxnegocio.getMarcaByHashcode(paramHashcode, false);
                            empresaId           = marca.getEmpresaCod();
                            paramRunEmpleado    = marca.getRutEmpleado();
                            paramStartDate      = marca.getFecha();
                            paramEndDate        = marca.getFecha();
                        }
                        
                        request.setAttribute("empresa", empresaId);
                        request.setAttribute("rut_empleado", paramRunEmpleado);
                        
                        int idTurnoRotativo = turnoBp.getTurnoRotativo(empresaId);
                        EmpleadoVO infoEmpleado = 
                            empleadosBp.getEmpleado(empresaId, 
                                paramRunEmpleado);
                        
                        boolean tieneTurnoRotativo=false;
                        if (idTurnoRotativo == infoEmpleado.getIdTurno()){
                            System.out.println(WEB_NAME+"[MarcasController.mostrarMarcas]"
                                + "Empleado.rut: " + infoEmpleado.getRut()
                                + ", nombres: " + infoEmpleado.getNombres()
                                + ", Tiene turno rotativo");
                            tieneTurnoRotativo = true;
                        }
        
                        hashMarcas = auxnegocio.getHashMarcas(empresaId,
                            infoEmpleado.getDeptoId(),
                            infoEmpleado.getCencoId(),
                            paramRunEmpleado, 
                            request.getParameter("dispositivoId"), 
                            paramStartDate, 
                            paramEndDate,
                            paramHashcode,
                            tieneTurnoRotativo,
                            infoEmpleado.getRegionId(),
                            infoEmpleado.getComunaId(),
                            startPageIndex, 
                            numRecordsPerPage, 
                            jtSorting);

                        //Get Total Record Count for Pagination
                        objectsCount = auxnegocio.getMarcasCount(empresaId,
                            infoEmpleado.getDeptoId(),
                            infoEmpleado.getCencoId(),
                            paramRunEmpleado, 
                            request.getParameter("dispositivoId"), 
                            paramStartDate, 
                            paramEndDate,
                            paramHashcode);
                        
                        //agregar evento al log.
                        resultado.setRutEmpleado(paramRunEmpleado);
                        resultado.setEmpresaId(empresaId);
                        resultado.setDeptoId(deptoId);
                        resultado.setCencoId(Integer.parseInt(cencoId));
                        resultado.setDescription("Consulta marcas. "
                            + " Rut empleado: " + paramRunEmpleado    
                            + ", desde: " + paramStartDate
                            + ", hasta: " + paramEndDate
                            + ", dispositivo: " + request.getParameter("dispositivoId"));
                        eventosBp.addEvent(resultado);
                    
                    }else System.err.println("No ");
                    //Convert Java Object to Json
//                    JsonElement element = gson.toJsonTree(hashMarcas,
//                        new TypeToken<LinkedHashMap<String, MarcaVO>>() {}.getType());

                    String listData = gson.toJson(hashMarcas, LinkedHashMap.class);
                    
                    session.setAttribute("marcas|" + userConnected.getUsername(), hashMarcas);
                    
                    //Return Json in the format required by jTable plugin
                    listData="{\"Result\":\"OK\",\"Records\":" + 
                        listData+",\"TotalRecordCount\": " + 
                        objectsCount + "}";
                    response.getWriter().print(listData);
                    //request.getRequestDispatcher("/mantenedores/mantenedoresFrmSet.jsp").forward(request, response);
                }catch(IOException ex){
                    String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                    response.getWriter().print(error);
                    ex.printStackTrace();
                }   
            }else if (action.compareTo("create") == 0) {
                String fechaHora = request.getParameter("fechaHora")
                    +" "+request.getParameter("hora")
                    +":"+request.getParameter("minutos")
                    +":00";
                
                infomarca.setFecha(request.getParameter("fechaHora"));//solo fecha: yyyy-MM-dd
                infomarca.setHora(request.getParameter("hora")
                    +":"+request.getParameter("minutos")
                    +":00");//solo hora: hh:mm:ss
                
                infomarca.setEmpresaCod(request.getParameter("empresaKey"));//request.getParameter("empresaCod"));
                infomarca.setRutEmpleado(request.getParameter("aRut"));
                infomarca.setFechaHora(fechaHora);
                infomarca.setCodDispositivo(request.getParameter("aDevice"));
                infomarca.setId(request.getParameter("id"));
                infomarca.setComentario(request.getParameter("comentario"));
                infomarca.setTipoMarca(Integer.parseInt(request.getParameter("tipoMarca")));
                
                if (infomarca.getRutEmpleado()!=null && infomarca.getRutEmpleado().compareTo("")!=0){
                    EmpleadosBp empleadosbp=new EmpleadosBp(appProperties);
                    EmpleadoVO infoEmpleado = empleadosbp.getEmpleado(infomarca.getEmpresaCod(), infomarca.getRutEmpleado());
                    resultado.setEmpresaId(infoEmpleado.getEmpresa().getId());
                    resultado.setDeptoId(infoEmpleado.getDepartamento().getId());
                    resultado.setCencoId(infoEmpleado.getCentroCosto().getId());
                    infomarca.setCodInternoEmpleado(infoEmpleado.getCodInterno());
                    
                }
                /**
                 *  Empl_rut + Fecha + Hora + Evento
                 */
                String keyPhrase = infomarca.getRutEmpleado()+infomarca.getFechaHora()+infomarca.getTipoMarca();
                infomarca.setHashcode(Utilidades.getMD5EncryptedValue(keyPhrase));
                
                System.out.println(WEB_NAME+"[MarcasController]Insertar marca. "
                    + "Empresa: " + empresaId
                    + ", rut: " + infomarca.getRutEmpleado()
                    + ", fechaHora: " + infomarca.getFechaHora()
                    + ", CodDispositivo: " + infomarca.getCodDispositivo()
                    + ", TipoMarca: " + infomarca.getTipoMarca()
                    + ", hashcode(md5: " + infomarca.getHashcode());
                    
                ResultCRUDVO doCreate = auxnegocio.insertWithLog(infomarca, resultado);
                System.out.println(WEB_NAME+"[MarcasController]Mensaje "
                    + "del sp new_inserta_marca_manual: " + doCreate.getMsgFromSp());
                //if (!doCreate.isThereError()){
                    informaCreacionMarca(doCreate.getMsgFromSp(), infomarca, userConnected, request);
                //}else{
                    //informaCreacionMarca(doCreate.getMsgFromSp(), infomarca, userConnected, request);
                //}
                infomarca.setRowKey(infomarca.getEmpresaCod()
                    + "|" + infomarca.getRutEmpleado()
                    + "|" + infomarca.getFechaHora()
                    + "|" + infomarca.getTipoMarca());
                hashMarcas.put(infomarca.getRowKey(), infomarca);

                //Convert Java Object to Json
                String json=gson.toJson(infomarca);					
                
                //Return Json in the format required by jTable plugin
                String jsonMsgFromSp = gson.toJson(doCreate.getMsgFromSp());
                String jsonOkMessage="{\"Result\":\"OK\",\"Record\":"+json+"}";
                
                if (!doCreate.getMsgFromSp().startsWith("00-")){
                    String errorMsg="{\"Result\":\"ERROR\",\"Message\":"+jsonMsgFromSp+"}";
                    response.getWriter().print(errorMsg);
                }else{
                    response.getWriter().print(jsonOkMessage);
                }
                
            }else if (action.compareTo("update") == 0) {  
                    System.out.println(WEB_NAME+"[MarcasController]}"
                        + "Actualizar marca, "
                        + "rowKey: "+request.getParameter("rowKey")
                        + ", tipoMarca: " + infomarca.getTipoMarca());
                    String auxfecha = request.getParameter("fechaHora");
                    String newfechaHora = auxfecha + " " 
                        + infomarca.getHora()
                        + ":" + infomarca.getMinutos() + ":"+infomarca.getSegundos();
                    MarcaVO marcaOriginal=null;    
                    if (request.getParameter("rowKey")!=null){
                        StringTokenizer tokenKey = new StringTokenizer(request.getParameter("rowKey"), "|");
                        //emp01|15695517B|2018-01-15 08:34:00|1
                        infomarca.setEmpresaCod(tokenKey.nextToken());
                        infomarca.setRutEmpleado(tokenKey.nextToken());
                        String fechaHoraKey = tokenKey.nextToken();
                        infomarca.setFechaHora(fechaHoraKey);
                        int tipoMarcaKey = Integer.parseInt(tokenKey.nextToken());
                        ////infomarca.setTipoMarca(tipoMarcaKey);
                        
                        System.out.println(WEB_NAME+"[MarcasController]update marca."
                            + "tipoMarcaKey: " + tipoMarcaKey
                            + ",fechaHoraKey: " + fechaHoraKey);
                         
                        //buscar marca original existente
                        marcaOriginal = auxnegocio.getMarcaByKey(infomarca.getEmpresaCod(), 
                            infomarca.getRutEmpleado(),
                            fechaHoraKey, tipoMarcaKey, false);
                        if (marcaOriginal != null){
                            //enviar correo
                            System.out.println(WEB_NAME+"[MarcasController]update marca."
                                + "Marca Original. "
                                + "Empresa: " + marcaOriginal.getEmpresaCod()
                                + ", rut: " + marcaOriginal.getRutEmpleado()
                                + ", tipoMarca: " + marcaOriginal.getTipoMarca()
                                + ", fechaHora: " + marcaOriginal.getFechaHora()
                                + ", comentario: " + marcaOriginal.getComentario()    
                            );
                            infomarca.setFechaHora(newfechaHora);
                            System.out.println(WEB_NAME+"[MarcasController]update marca."
                                + "Marca a modificar: "
                                + "Empresa: " + infomarca.getEmpresaCod()
                                + ", rut: " + infomarca.getRutEmpleado()
                                + ", tipoMarca: " + infomarca.getTipoMarca()
                                + ", fechaHora: " + infomarca.getFechaHora()
                                + ", comentario: " + infomarca.getComentario()    
                            );
                            
                            /**
                            *  Empl_rut + Fecha + Hora + Evento
                            */
                            String keyPhrase = infomarca.getRutEmpleado()+infomarca.getFechaHora()+infomarca.getTipoMarca();
                            infomarca.setHashcode(Utilidades.getMD5EncryptedValue(keyPhrase));
                            
                        }
                    }
                    
                    try{
                        resultado.setEmpresaId(infomarca.getEmpresaCod());
                        resultado.setRutEmpleado(infomarca.getRutEmpleado());
                        if (infomarca.getRutEmpleado()!=null && infomarca.getRutEmpleado().compareTo("")!=0){
                            EmpleadosBp empleadosbp=new EmpleadosBp(appProperties);
                            EmpleadoVO infoEmpleado = empleadosbp.getEmpleado(infomarca.getEmpresaCod(), infomarca.getRutEmpleado());
                            resultado.setEmpresaId(infoEmpleado.getEmpresa().getId());
                            resultado.setDeptoId(infoEmpleado.getDepartamento().getId());
                            resultado.setCencoId(infoEmpleado.getCentroCosto().getId());
                        }
                        
                        /**
                        * 20200304-001: Admin marcas, 
                        * Cuando se modifique el tipo de evento, 
                        * se debe guardar texto en campo 'comentario': 
                        *    'CAMBIO DE ENTRADA POR SALIDA' o viceversa.
                        */
                        if (marcaOriginal.getTipoMarca() == Constantes.MARCA_ENTRADA){
                            //si la marca original era de ENTRADA y se modifica a SALIDA
                            if (infomarca.getTipoMarca() == Constantes.MARCA_SALIDA){
                                infomarca.setComentario(Constantes.CAMBIO_MARCA_ENTRADA_POR_SALIDA);
                            }
                        }else if (marcaOriginal.getTipoMarca() == Constantes.MARCA_SALIDA){
                            //si la marca original era de SALIDA y se modifica a ENTRADA
                            if (infomarca.getTipoMarca() == Constantes.MARCA_ENTRADA){
                                infomarca.setComentario(Constantes.CAMBIO_MARCA_SALIDA_POR_ENTRADA);
                            }
                        }
                        /**
                        * 20200304-002: Admin marcas, Cuando se modifique la hora de una marca, 
                        * se debe guardar texto en campo 'comentario': 'HORA MODIFICADA'.
                        * 
                        */
                        //fechaHora: 2020-02-10 08:30:00
                        //fechaHora: 2020-02-10 08:30:00
                        StringTokenizer tokenfechaHora=new StringTokenizer(infomarca.getFechaHora(), " ");
                        String soloFechaNew = tokenfechaHora.nextToken();
                        String soloHoraNew = tokenfechaHora.nextToken();
                        if (marcaOriginal.getSoloHora().compareTo(soloHoraNew) != 0){
                            //La hora de la marca original es distinta a la hora modificada
                            infomarca.setComentario(Constantes.CAMBIO_HORA_MARCA);
                        }
                                                
                        /**
                        * 20200304-003: Admin marcas, Cuando se modifique la fecha de una marca, 
                        * se debe guardar texto en campo 'comentario': 'FECHA MODIFICADA'.
                        * 
                        */
                        if (marcaOriginal.getFecha().compareTo(soloFechaNew) != 0){
                            //La fecha de la marca original es distinta a la fecha modificada
                            infomarca.setComentario(Constantes.CAMBIO_FECHA_MARCA);
                        }
                        
                        //actualiza la marca
                        ResultCRUDVO doUpdate = auxnegocio.update(infomarca, marcaOriginal, resultado);
                        
                        if (!doUpdate.isThereError()){
                            //enviar mail con info de marca original y marca modificada...
                            informaModificacionMarca("REGISTRO DE ASISTENCIA MODIFICADO", 
                                marcaOriginal, infomarca, userConnected, request);
                            
                            //guardar evento en nueva tabla
                            MarcasEventosBp marcamodif= new MarcasEventosBp();
                            MarcasEventosVO aux = new MarcasEventosVO();
                            
                            System.out.println(WEB_NAME+"[MarcasController]inserta "
                                + "marca evento: modificada.");
                            
                            aux.setCodDispositivo(marcaOriginal.getCodDispositivo());
                            aux.setCodUsuario(userConnected.getUsername());
                            aux.setEmpresaCod(marcaOriginal.getEmpresaCod());
                            aux.setRutEmpleado(marcaOriginal.getRutEmpleado());
                            aux.setId(marcaOriginal.getId());
                            aux.setHashcode(marcaOriginal.getHashcode());
                            
                            aux.setFechaHoraOriginal(marcaOriginal.getFechaHora());
                            aux.setTipoMarcaOriginal(marcaOriginal.getTipoMarca());
                            aux.setComentarioOriginal(marcaOriginal.getComentario());
                            
                            aux.setFechaHoraNew(infomarca.getFechaHora());
                            aux.setTipoMarcaNew(infomarca.getTipoMarca());
                            aux.setComentarioNew(infomarca.getComentario());
                            aux.setTipoEvento(Constantes.MARCA_MODIFICADA);
                            marcamodif.insert(aux, resultado);
                        }
                        
                        infomarca.setFechaHora(newfechaHora);
                        infomarca.setFechaHoraKey(newfechaHora);
                        infomarca.setFechaHoraStr(newfechaHora);
                        
                        infomarca.setRowKey(infomarca.getEmpresaCod()
                            + "|" + infomarca.getRutEmpleado()
                            + "|" + infomarca.getFechaHora()
                            + "|" + infomarca.getTipoMarca());
                        hashMarcas.put(infomarca.getRowKey(), infomarca);

                        //Convert Java Object to Json
                        String json=gson.toJson(infomarca);	
                        String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";
                          
                        response.getWriter().print(listData);
                        
                    }catch(Exception ex){
                        String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getStackTrace().toString()+"}";
                        response.getWriter().print(error);
                    }
            }else if (action.compareTo("delete") == 0) {
                    System.out.println(WEB_NAME+"[MarcasController]}"
                        + "Eliminar marca, rowKey: "+request.getParameter("rowKey"));
                    StringTokenizer tokenKey = new StringTokenizer(request.getParameter("rowKey"), "|");
                    //emp01|15695517B|2018-01-15 08:34:00|1
                    infomarca.setEmpresaCod(tokenKey.nextToken());
                    infomarca.setRutEmpleado(tokenKey.nextToken());
                    String fechaHoraKey = tokenKey.nextToken();
                    infomarca.setFechaHora(fechaHoraKey);
                    int tipoMarcaKey = Integer.parseInt(tokenKey.nextToken());
                    infomarca.setTipoMarca(tipoMarcaKey);
                    
                    String strHashcode = tokenKey.nextToken();
                    infomarca.setHashcode(strHashcode);
                    
                    int intCorrelativo = Integer.parseInt(tokenKey.nextToken());
                    infomarca.setCorrelativo(intCorrelativo);
                    
                    System.out.println(WEB_NAME+"[MarcasController]}"
                        + "Eliminar marca,"
                        + " rut: " + infomarca.getRutEmpleado()
                        + ", fechaHora: " + infomarca.getFechaHora()
                        + ", tipoMarca: " + infomarca.getTipoMarca()
                        + ", hashCode: " + infomarca.getHashcode()
                        + ", correlativo: " + infomarca.getCorrelativo()    
                    );
                    try{
                        resultado.setEmpresaId(infomarca.getEmpresaCod());
                        resultado.setRutEmpleado(infomarca.getRutEmpleado());
                        if (infomarca.getRutEmpleado()!=null && infomarca.getRutEmpleado().compareTo("")!=0){
                            EmpleadosBp empleadosbp=new EmpleadosBp(appProperties);
                            EmpleadoVO infoEmpleado = empleadosbp.getEmpleado(infomarca.getEmpresaCod(), infomarca.getRutEmpleado());
                            resultado.setEmpresaId(infoEmpleado.getEmpresa().getId());
                            resultado.setDeptoId(infoEmpleado.getDepartamento().getId());
                            resultado.setCencoId(infoEmpleado.getCentroCosto().getId());
                        }
                        //buscar marca antes de ser eliminada
                        MarcaVO marcaOriginal = auxnegocio.getMarcaByKey(infomarca.getEmpresaCod(), 
                            infomarca.getRutEmpleado(),
                            fechaHoraKey, tipoMarcaKey, false);
                        
                        ResultCRUDVO doDelete = auxnegocio.delete(infomarca, resultado);
                        
                        infomarca.setRowKey(infomarca.getEmpresaCod()
                            + "|" + infomarca.getRutEmpleado()
                            + "|" + infomarca.getFechaHora()
                            + "|" + infomarca.getTipoMarca()
                            + "|" + infomarca.getHashcode()
                            + "|" + infomarca.getCorrelativo());
                        hashMarcas.put(infomarca.getRowKey(), infomarca);

                        //guardar evento en tabla marcas_eventos
                        if (!doDelete.isThereError()){
                            MarcasEventosBp marcamodif= new MarcasEventosBp();
                            MarcasEventosVO aux = new MarcasEventosVO();
                            
                            System.out.println(WEB_NAME+"[MarcasController]inserta "
                                + "marca evento: ELIMINACION.");
                            aux.setCorrelativo(infomarca.getCorrelativo());
                            aux.setCodDispositivo(marcaOriginal.getCodDispositivo());
                            aux.setCodUsuario(userConnected.getUsername());
                            aux.setEmpresaCod(marcaOriginal.getEmpresaCod());
                            aux.setRutEmpleado(marcaOriginal.getRutEmpleado());
                            aux.setId(marcaOriginal.getId());
                            aux.setHashcode(marcaOriginal.getHashcode());
                            
                            aux.setFechaHoraOriginal(marcaOriginal.getFechaHora());
                            aux.setTipoMarcaOriginal(marcaOriginal.getTipoMarca());
                            aux.setComentarioOriginal(marcaOriginal.getComentario());
                            
                            aux.setFechaHoraNew(marcaOriginal.getFechaHora());
                            aux.setTipoMarcaNew(marcaOriginal.getTipoMarca());
                            aux.setComentarioNew("");
                            aux.setTipoEvento(Constantes.MARCA_ELIMINADA);
                            marcamodif.insert(aux, resultado);
                        }
                        //Convert Java Object to Json
                        String json=gson.toJson(infomarca);					
                        //Return Json in the format required by jTable plugin
                        String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                        response.getWriter().print(listData);
                        
                    }catch(Exception ex){
                        String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getStackTrace().toString()+"}";
                        response.getWriter().print(error);
                    }
            }
            
        }
    }
    
    /**
    * 
    */
    private void informaCreacionMarca(String _evento,
            MarcaVO _marcaCreada,
            UsuarioVO _userConnected, 
            HttpServletRequest _request){
    
        MarcasBp marcasBp = new MarcasBp();
        HashMap<Integer, String> tiposMarcas = marcasBp.getTiposMarca();
        
        EmpleadosBp empleadobp = new EmpleadosBp();
        EmpleadoVO empleado = 
            empleadobp.getEmpleado(_marcaCreada.getEmpresaCod(), 
            _marcaCreada.getRutEmpleado());
        String fromLabel = "Gestion asistencia";
        String fromMail = m_properties.getKeyValue("mailFrom");
        String asuntoMail   = "Sistema de Gestion-Registro de asistencia";
        String mailTo = empleado.getEmail();
        
        String mailBody = "Evento:" + _evento
            +"<br>RUN: " + empleado.getCodInterno()
            +"<br>Nombre: " + empleado.getNombreCompleto()
            +"<br>Institucion: " + empleado.getEmpresaNombre()
            +"<br>RUT: " + empleado.getEmpresaRut()
            +"<br>Ubicacion: " + empleado.getEmpresaDireccion()
            +"<br>Centro de costo: " + empleado.getCencoNombre()
            +"<br>Cod dispositivo: " + _marcaCreada.getCodDispositivo()    
            +"<br>Tipo Marca: " + tiposMarcas.get(_marcaCreada.getTipoMarca())+" manual"
            +"<br>Fecha/Hora Marca: " + _marcaCreada.getFechaHora()
            +"<br>Codigo Hash: " + _marcaCreada.getHashcode()
            +"<br>Usuario que crea el registro: " + _userConnected.getUsername()
            +"<br>---"
            +"<br>Sistema excepcional de jornada: NO APLICA"
            +"<br>Resolución Exenta: NO APLICA"
            +"<br>Geolocalización: NO APLICA"
            +"<br>Empresa Transitoria o Subcontratada: NO APLICA"
            +"<br>Nombre: NO APLICA"
            +"<br>RUT: NO APLICA";
             
        MaintenanceEventVO resultado=new MaintenanceEventVO();
        resultado.setUsername(_userConnected.getUsername());
        resultado.setDatetime(new Date());
        resultado.setUserIP(_request.getRemoteAddr());
        resultado.setType("NOT");
        resultado.setEmpresaIdSource(_userConnected.getEmpresaId());
        resultado.setEmpresaId(empleado.getEmpresaId());
        resultado.setDeptoId(empleado.getDeptoId());
        resultado.setCencoId(empleado.getCencoId());
        resultado.setRutEmpleado(empleado.getRut());
        
        System.out.println(WEB_NAME+"[mantencionEvento]"
            + "EmpresaId: " + resultado.getEmpresaId()
            + ", deptoId: " + resultado.getDeptoId()
            + ", cencoId: " + resultado.getCencoId());
        
        NotificacionBp notificacionBp=new NotificacionBp(null);
        NotificacionVO notificacion = new NotificacionVO();
        notificacion.setEmpresaId(empleado.getEmpresaId());
        notificacion.setCencoId(empleado.getCencoId());
        notificacion.setRutEmpleado(empleado.getRut());
        notificacion.setMailFrom(fromMail);
        notificacion.setMailTo(mailTo);
        notificacion.setMailSubject(asuntoMail);
        notificacion.setMailBody(mailBody);
        notificacion.setUsername(_userConnected.getUsername());
        notificacion.setComentario(_marcaCreada.getComentario());
        notificacionBp.insert(notificacion, resultado);
////        try{
////            UtilCorreo correo = new UtilCorreo();
////            correo.setUseAuth(true);
////            correo.setSmtpHost(m_properties.getKeyValue("mailHost"));
////            correo.setSmtpPort(m_properties.getKeyValue("mailPort"));
////            correo.setNomUsuario(m_properties.getKeyValue("mailUsuario"));
////            correo.setClaveUsuario(m_properties.getKeyValue("mailPassword"));
////            correo.setFrom(fromMail);
////            correo.setSubject(asuntoMail);
////            correo.setBody(mailBody);
////            String[] destinatarios = new String[1];
////            destinatarios[0] = mailTo;
////            correo.setDestinatarios(destinatarios);
////            //correo.setFileAttachment(null);
////            correo.setEnviarMensaje();
////        }catch(Exception ex){
////            System.err.println("[informaCreacionMarca]Error al enviar correo: "+ex.toString());
////        }
        ////MailSender.sendWithAttachment(null, fromLabel, fromMail, mailTo, asuntoMail,mailBody);    
        
    }
    
    private void informaModificacionMarca(String _evento,
            MarcaVO _marcaOriginal,MarcaVO _marcaModificada,
            UsuarioVO _userConnected,HttpServletRequest _request){
    
        MarcasBp marcasBp = new MarcasBp();
        HashMap<Integer, String> tiposMarcas = marcasBp.getTiposMarca();
        
        EmpleadosBp empleadobp = new EmpleadosBp();
        EmpleadoVO empleado = 
            empleadobp.getEmpleado(_marcaOriginal.getEmpresaCod(), 
            _marcaOriginal.getRutEmpleado());
        String fromLabel = "Gestion asistencia";
        String fromMail = m_properties.getKeyValue("mailFrom");
        String asuntoMail   = "Sistema de Gestion-Modificacion Registro de asistencia";
        String mailTo = empleado.getEmail();
        
        String mailBody = "Evento:" + _evento
            +"<br>RUN: " + empleado.getCodInterno()
            +"<br>Nombre: " + empleado.getNombreCompleto()
            +"<br>Institucion: " + empleado.getEmpresaNombre()
            +"<br>RUT: " + empleado.getEmpresaRut()
            +"<br>Ubicacion: " + empleado.getEmpresaDireccion()
            +"<br>Centro de costo: " + empleado.getCencoNombre()
            +"<br>Cod dispositivo: " + _marcaOriginal.getCodDispositivo()
            +"<br>Tipo Marca (original): " + tiposMarcas.get(_marcaOriginal.getTipoMarca())
            +"<br>Tipo Marca (modificada): " + tiposMarcas.get(_marcaModificada.getTipoMarca())    
            +"<br>Fecha/Hora Marca (original): " + _marcaOriginal.getFechaHora()
            +"<br>Fecha/Hora Marca (modificada): " + _marcaModificada.getFechaHora()
            +"<br>Codigo Hash (original): " + _marcaOriginal.getHashcode()
            +"<br>Codigo Hash (modificado): " + _marcaModificada.getHashcode()    
            +"<br>Usuario que modifica el registro: " + _userConnected.getUsername()
            +"<br>---"
            +"<br>Sistema excepcional de jornada: NO APLICA"
            +"<br>Resolución Exenta: NO APLICA"
            +"<br>Geolocalización: NO APLICA"
            +"<br>Empresa Transitoria o Subcontratada: NO APLICA"
            +"<br>Nombre: NO APLICA"
            +"<br>RUT: NO APLICA";
        MaintenanceEventVO resultado=new MaintenanceEventVO();
        resultado.setUsername(_userConnected.getUsername());
        resultado.setDatetime(new Date());
        resultado.setUserIP(_request.getRemoteAddr());
        resultado.setType("NOT");
        resultado.setEmpresaIdSource(_userConnected.getEmpresaId());
        resultado.setEmpresaId(empleado.getEmpresaId());
        resultado.setDeptoId(empleado.getDeptoId());
        resultado.setCencoId(empleado.getCencoId());
        resultado.setRutEmpleado(empleado.getRut());
            
        NotificacionBp notificacionBp=new NotificacionBp(null);
        NotificacionVO notificacion = new NotificacionVO();
        notificacion.setEmpresaId(empleado.getEmpresaId());
        notificacion.setCencoId(empleado.getCencoId());
        notificacion.setRutEmpleado(empleado.getRut());
        notificacion.setMailFrom(fromMail);
        notificacion.setMailTo(mailTo);
        notificacion.setMailSubject(asuntoMail);
        notificacion.setMailBody(mailBody);
        notificacion.setUsername(_userConnected.getUsername());
        notificacion.setComentario(_marcaModificada.getComentario());
        
        notificacionBp.insert(notificacion, resultado);
        
////        try{
////            UtilCorreo correo = new UtilCorreo();
////            correo.setUseAuth(true);
////            correo.setSmtpHost(m_properties.getKeyValue("mailHost"));
////            correo.setSmtpPort(m_properties.getKeyValue("mailPort"));
////            correo.setNomUsuario(m_properties.getKeyValue("mailUsuario"));
////            correo.setClaveUsuario(m_properties.getKeyValue("mailPassword"));
////            correo.setFrom(fromMail);
////            correo.setSubject(asuntoMail);
////            correo.setBody(mailBody);
////            String[] destinatarios = new String[1];
////            destinatarios[0] = mailTo;
////            correo.setDestinatarios(destinatarios);
////            //correo.setFileAttachment(null);
////            correo.setEnviarMensaje();
////        }catch(Exception ex){
////            System.err.println("[informaModificacionMarca]"
////                + "Error al enviar correo: "+ex.toString());
////            ex.printStackTrace();
////        }
        
        //////MailSender.sendWithAttachment(null, fromLabel, fromMail, mailTo, asuntoMail,mailBody);    
        
    }
}
