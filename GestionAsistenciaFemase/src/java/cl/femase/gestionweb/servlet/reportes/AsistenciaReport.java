/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.servlet.reportes;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.CentroCostoBp;
import cl.femase.gestionweb.business.DepartamentoBp;
import cl.femase.gestionweb.business.DetalleAsistenciaBp;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.EmpresaBp;
import cl.femase.gestionweb.business.MaintenanceEventsBp;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.common.DatabaseLocator;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.vo.AsistenciaTotalesVO;
import cl.femase.gestionweb.vo.CentroCostoVO;
import cl.femase.gestionweb.vo.DepartamentoVO;
import cl.femase.gestionweb.vo.DetalleAsistenciaVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.EmpresaVO;
import cl.femase.gestionweb.vo.FiltroBusquedaJsonVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import com.google.gson.Gson;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.sf.jasperreports.engine.JasperRunManager;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
 
/**
 * Permite generar reportes de asistencia para uno o mas empleados.
 * Si se selecciona un solo empleado, se genera un PDF.
 * Si se seleccionan TODOS los empleados de un centro de costo,
 * se genera un PDF por cada empleado y luego se unen (metodo 'mergePdfFiles')
 * todos los PDF en uno solo. El PDF resultante tendra el nombre del cenco seleccionado.
 * 
 *
 * @author Alexander
 */
public class AsistenciaReport extends BaseServlet {

    DetalleAsistenciaBp m_detAsistenciaBp   = new DetalleAsistenciaBp(null);
    EmpleadosBp m_empleadosBp               = new EmpleadosBp(null);
    DatabaseLocator m_dbLocator;
    String m_dbpoolName;
    Connection m_databaseConnection         = null;
    
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    //private String DOWNLOAD_FILE_NAME="asistencia.pdf";
    LinkedHashMap<String, String> hashPdfFilesPaths=new LinkedHashMap<>();
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @param _empleado
     * @param _detalleAsistencia
     * 
     * @return 
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected FileGeneratedVO processRequestRut(HttpServletRequest request, 
                HttpServletResponse response, 
                EmpleadoVO _empleado, 
                List<DetalleAsistenciaVO> _detalleAsistencia)
            throws ServletException, IOException {
        
        HttpSession session         = request.getSession(true);
        ServletContext application  = this.getServletContext();
        PropertiesVO appProperties  = (PropertiesVO)application.getAttribute("appProperties");
        String tipoParam = request.getParameter("tipo");
        String formato = request.getParameter("formato");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        FileGeneratedVO fileGenerated = null;
        String fileName = "";        
        String fullPath = "";
        
        System.out.println(WEB_NAME+"[servlet.reportes."
            + "AsistenciaReport.processRequestRut]"
            + "tipoParam: "+tipoParam
            +", usuario conectado: "+userConnected.getUsername());
        if (tipoParam.compareTo("3") == 0){
            //Asistencia CSV resumido por empresa
            System.out.println(WEB_NAME+"[servlet.reportes."
                + "AsistenciaReport.processRequestRut]Generar "
                + "Asistencia CSV resumido, tipo: " + tipoParam);
            LinkedHashMap<String, AsistenciaTotalesVO> totalesAsistencia = 
                getTotalesAsistenciaEmpresa(request, userConnected);
            
            System.out.println(WEB_NAME+"[servlet.reportes."
                + "AsistenciaReport.processRequestRut]"
                + "totalesAsistencia.size: " + totalesAsistencia.size());
            if (totalesAsistencia.size() > 0){
                //mostrar CSV 
                //generar link para download del archivo csv
                fullPath = setTotalesAsistenciaToCSV(request, totalesAsistencia, _empleado);
                fileName = userConnected.getUsername()+"_resumen_asistencia.csv";
                fileGenerated = new FileGeneratedVO(fileName,fullPath);     
            }
        }else{
            if (_empleado == null) return null;
            
            String jasperfile= "detalle_asistencia.jasper";
            System.out.println(WEB_NAME+"[AsistenciaReport.processRequestRut]"
                + "tipo: " + tipoParam
                + ", formato: " + formato);
            fileName = "asistencia_" + _empleado.getRut() + "." + formato;
            if (tipoParam.compareTo("2") == 0){
                jasperfile="detalle_asistencia_marcas.jasper";
                fileName = "asistencia_marcas_" 
                    + _empleado.getRut() + "." + formato;
            }

            String jasperFileName = 
                appProperties.getReportesPath() + File.separator +jasperfile;
            System.out.println(WEB_NAME+"[AsistenciaReport."
                + "processRequestRut]jasperfile: " + jasperFileName);
            
            System.out.println(WEB_NAME+"[AsistenciaReport."
                + "processRequestRut]Iterar empleadoRut: " + _empleado.getRut());
            Map parameters = getParameters(request, _empleado, _detalleAsistencia);
            
            if (parameters != null){
                System.out.println(WEB_NAME+"[AsistenciaReport.processRequestRut]Generar archivo...");
                if (formato.compareTo("pdf") == 0){
                    System.out.println(WEB_NAME+"[processRequestRut.processRequestRut]"
                        + "Generar PDF."
                        + " fileName: " + fileName);
                    try{
                        fullPath = 
                            appProperties.getPathExportedFiles()+File.separator+fileName;
                        JasperRunManager.runReportToPdfFile(jasperFileName, fullPath, parameters, m_databaseConnection);
                    }catch(Exception e){
                        System.err.println("[processRequestRut.processRequestRut]"
                            + "Error al generar pdf: "+e.toString());
                        e.printStackTrace();
                    }
                }
                fileGenerated = new FileGeneratedVO(fileName,fullPath);     
            }else{
                System.out.println(WEB_NAME+"[AsistenciaReport."
                    + "processRequestRut]NO Generar archivo...");
                fileGenerated = null;
            }
        }
        
        return fileGenerated;
    }
   
        
        
//    /**
//     * obtiene parametros a setear en el reporte
//     */
//    private HashMap getParameters(HttpServletRequest _request){
//        
//        ServletContext application  = this.getServletContext();
//        PropertiesVO appProperties  = (PropertiesVO)application.getAttribute("appProperties");
//        EmpleadosBp empleadosBp     = new EmpleadosBp(appProperties);
//        DetalleAsistenciaBp detAsistenciaBp = new DetalleAsistenciaBp(appProperties);
//        
//        String rutParam = _request.getParameter("rut");
//        String startDateParam = _request.getParameter("startDate");
//        String endDateParam = _request.getParameter("endDate");
//        String empresaParam = _request.getParameter("empresa");
//        String tipoParam = _request.getParameter("tipo");
//        HashMap parameters = new HashMap();
//                
//        System.out.println(WEB_NAME+"AsistenciaReport."
//            + "getParameters. "
//            + "Param rut= " +rutParam
//            + ", startDate= " +startDateParam
//            + ", endDate= " +endDateParam
//            + ", tipo= " +tipoParam);
//        
//        String strAuxHp     = "";
//        String strAuxHt     = "";
//        String strAuxAtraso = "";
//        String strAuxHextras = "";
//        String strAuxHaus   = "";
//        String strAuxHrsJustificadas = "";
//        
//        int countDiasTrabajados = 0;
//        int countDiasAusente   = 0;
//        List<DetalleAsistenciaVO> registros 
//            = detAsistenciaBp.getDetalles(rutParam, startDateParam, endDateParam);
//        if (registros.size() >0){
//            ArrayList<String> listaHorasPresenciales = new ArrayList<>();
//            ArrayList<String> listaHorasTrabajadas = new ArrayList<>();
//            ArrayList<String> listaHorasAusencia = new ArrayList<>();
//            ArrayList<String> listaHorasAtraso = new ArrayList<>();
//            ArrayList<String> listaHorasExtras = new ArrayList<>();
//            ArrayList<String> listaHorasJustificadas = new ArrayList<>();
//            for (DetalleAsistenciaVO detalle : registros) {
//                strAuxHp = detalle.getHrsPresenciales();
//                listaHorasPresenciales.add(strAuxHp);
//
//                strAuxHt = detalle.getHrsTrabajadas();
//                listaHorasTrabajadas.add(strAuxHt);
//
//                strAuxHaus = detalle.getHrsAusencia();
//                listaHorasAusencia.add(strAuxHaus);
//
//                //marzo 11,2018
//                strAuxAtraso = detalle.getHhmmAtraso();
//                if (strAuxAtraso != null) listaHorasAtraso.add(strAuxAtraso);
//                strAuxHextras = detalle.getHorasMinsExtrasAutorizadas();
//                if (strAuxHextras != null) listaHorasExtras.add(strAuxHextras);
//                strAuxHrsJustificadas = detalle.getHhmmJustificadas();
//                if (strAuxHrsJustificadas != null) listaHorasJustificadas.add(strAuxHrsJustificadas);
//                if ((detalle.getHoraEntrada() != null && detalle.getHoraEntrada().compareTo("00:00:00") != 0 
//                        && detalle.getHoraSalida() != null && detalle.getHoraSalida().compareTo("00:00:00") != 0)
//                        || (detalle.getObservacion()!=null && detalle.getObservacion().compareTo("Dia libre") == 0)){
//                    countDiasTrabajados++;
//                }
//
//                if (detalle.getHrsAusencia() != null 
//                        && detalle.getHrsAusencia().compareTo("") != 0){
//                    countDiasAusente++;
//                }
//
//            }//fin iteracion detalle ausencia
//
//            String totalHrsPresenciales = "00:00";
//            String totalHrsTrabajadas = "00:00";
//            String totalHrsAusencia = "00:00";
//            String totalHrsExtras = "00:00";
//            String totalHrsAtraso = "00:00";
//            String totalHrsJustificadas = "00:00";
//
//            if (listaHorasPresenciales.size() > 0){
//                totalHrsPresenciales = Utilidades.sumTimesList(listaHorasPresenciales);
//            }
//            if (listaHorasTrabajadas.size() > 0){
//                totalHrsTrabajadas = Utilidades.sumTimesList(listaHorasTrabajadas);
//            }
//            if (listaHorasAusencia.size() > 0){
//                totalHrsAusencia = Utilidades.sumTimesList(listaHorasAusencia);
//            }
//            if (listaHorasExtras.size() > 0){
//                totalHrsExtras = Utilidades.sumTimesList(listaHorasExtras);
//            }
//            if (listaHorasAtraso.size() > 0){
//                totalHrsAtraso = Utilidades.sumTimesList(listaHorasAtraso);
//            }
//            if (listaHorasJustificadas.size() > 0){
//                totalHrsJustificadas = Utilidades.sumTimesList(listaHorasJustificadas);
//            }
//        
//            EmpleadoVO infoempleado = empleadosBp.getEmpleado(empresaParam, rutParam);
//
//            parameters.put("rut", rutParam);//cod_interno
//            parameters.put("rut_full", infoempleado.getCodInterno());
//            parameters.put("cod_interno", rutParam);
//            parameters.put("nombre", infoempleado.getNombres() + " " +infoempleado.getApeMaterno());
//            parameters.put("cargo", infoempleado.getNombreCargo());
//            parameters.put("empresa_id", infoempleado.getEmpresa().getId());
//            parameters.put("empresa_nombre", infoempleado.getEmpresa().getNombre());
//            parameters.put("cenco_id", ""+infoempleado.getCentroCosto().getId());
//            parameters.put("cenco_nombre", infoempleado.getCentroCosto().getNombre());
//            parameters.put("fecha_ingreso", infoempleado.getFechaInicioContratoAsStr());
//            parameters.put("startDate", startDateParam);
//            parameters.put("endDate", endDateParam);
//            parameters.put("totalHrsPresenciales", totalHrsPresenciales);
//            parameters.put("totalHrsTrabajadas", totalHrsTrabajadas);
//            parameters.put("totalHrsAusencia", totalHrsAusencia);
//            parameters.put("totalHrsAtraso", totalHrsAtraso);
//            parameters.put("totalHrsExtras", totalHrsExtras);
//            parameters.put("totalHrsJustificadas", totalHrsJustificadas);
//            parameters.put("diasTrabajados", countDiasTrabajados);
//            parameters.put("diasAusente", countDiasAusente);
//
//        }
//        return parameters;
//    }
    
    /**
     * obtiene parametros a setear en el reporte
     */
    private HashMap getParameters(HttpServletRequest _request, 
            EmpleadoVO _objEmpleado,
            List<DetalleAsistenciaVO> _registros){
        
        ServletContext application  = this.getServletContext();
        PropertiesVO appProperties  = (PropertiesVO)application.getAttribute("appProperties");
        
        //String rutParam = _request.getParameter("rut");
        String startDateParam = _request.getParameter("startDate");
        String endDateParam = _request.getParameter("endDate");
        String empresaParam = _request.getParameter("empresa");
        String tipoParam = _request.getParameter("tipo");
        String turnoParam = _request.getParameter("turno");
        HashMap parameters = null;
        EmpresaBp empresaBp         = new EmpresaBp(appProperties);
       
        System.out.println(WEB_NAME+"AsistenciaReport."
            + "getParameters. "
            + "Param rut= " + _objEmpleado.getRut()
            + ", startDate= " +startDateParam
            + ", endDate= " +endDateParam
            + ", tipo= " +tipoParam
            + ", turnoId= " +turnoParam);
        
        String strAuxHp     = "";
        String strAuxHt     = "";
        String strAuxAtraso = "";
        String strAuxHextras = "";
        String strAuxHaus   = "";
        String strAuxHrsJustificadas = "";
        
        int countDiasTrabajados = 0;
        int countDiasAusente   = 0;
        
        List<DetalleAsistenciaVO> registros = _registros;
        if (_registros == null){
            System.out.println(WEB_NAME+"[AsistenciaReport."
                + "getParameters]Consultar detalles_asistencia a la BD...");
            registros = m_detAsistenciaBp.getDetalles(_objEmpleado.getRut(), 
                    startDateParam, endDateParam);
        }
        
        if (registros.size() >0){
            parameters = new HashMap();
            ArrayList<String> listaHorasPresenciales = new ArrayList<>();
            ArrayList<String> listaHorasTrabajadas = new ArrayList<>();
            ArrayList<String> listaHorasAusencia = new ArrayList<>();
            ArrayList<String> listaHorasAtraso = new ArrayList<>();
            ArrayList<String> listaHorasExtras = new ArrayList<>();
            ArrayList<String> listaHorasJustificadas = new ArrayList<>();
            
            for (DetalleAsistenciaVO detalle : registros) {
                strAuxHp = detalle.getHrsPresenciales();
                listaHorasPresenciales.add(strAuxHp);

                strAuxHt = detalle.getHrsTrabajadas();
                listaHorasTrabajadas.add(strAuxHt);

                strAuxHaus = detalle.getHrsAusencia();
                listaHorasAusencia.add(strAuxHaus);

                //marzo 11,2018
                strAuxAtraso = detalle.getHhmmAtraso();
                if (strAuxAtraso != null) listaHorasAtraso.add(strAuxAtraso);
                strAuxHextras = detalle.getHorasMinsExtrasAutorizadas();
                if (strAuxHextras != null) listaHorasExtras.add(strAuxHextras);
                strAuxHrsJustificadas = detalle.getHhmmJustificadas();
                System.out.println(WEB_NAME+"[AsistenciaReport."
                    + "getParameters]fecha: " + detalle.getFechaEntradaMarca()
                    + ", hrs trabajadas: " + strAuxHt
                    + ", observacion: " + detalle.getObservacion());
                
                if (strAuxHrsJustificadas != null) listaHorasJustificadas.add(strAuxHrsJustificadas);
                
                if ((detalle.getHoraEntrada() != null && detalle.getHoraEntrada().compareTo("00:00:00") != 0 
                        && detalle.getHoraSalida() != null && detalle.getHoraSalida().compareTo("00:00:00") != 0)
                        || (detalle.getObservacion()!=null && detalle.getObservacion().compareTo("Dia libre") == 0)
                        || (strAuxHt != null) ){
                    countDiasTrabajados++;
                }
                System.out.println(WEB_NAME+"[AsistenciaReport."
                    + "getParameters]"
                    + "Fecha entrada= " + detalle.getFechaEntradaMarca()
                    + ", getObservacion= " + detalle.getHrsAusencia()
                    + ", getObservacion= " + detalle.getObservacion());
//                if (detalle.getHrsAusencia() != null 
//                        && detalle.getHrsAusencia().compareTo("") != 0){
//                    countDiasAusente++;
//                }

                if (detalle.getObservacion() != null 
                        && detalle.getObservacion().compareTo("Sin marcas") == 0){
                    System.out.println(WEB_NAME+"[AsistenciaReport.getParameters]Sumo dias de ausencia");
                    countDiasAusente++;
                }  

            }//fin iteracion detalle ausencia

            String totalHrsPresenciales = "00:00";
            String totalHrsTrabajadas = "00:00";
            String totalHrsAusencia = "00:00";
            String totalHrsExtras = "00:00";
            String totalHrsAtraso = "00:00";
            String totalHrsJustificadas = "00:00";

            if (listaHorasPresenciales.size() > 0){
                totalHrsPresenciales = Utilidades.sumTimesList(listaHorasPresenciales);
            }
            if (listaHorasTrabajadas.size() > 0){
                totalHrsTrabajadas = Utilidades.sumTimesList(listaHorasTrabajadas);
            }
            if (listaHorasAusencia.size() > 0){
                totalHrsAusencia = Utilidades.sumTimesList(listaHorasAusencia);
            }
            if (listaHorasExtras.size() > 0){
                totalHrsExtras = Utilidades.sumTimesList(listaHorasExtras);
            }
            if (listaHorasAtraso.size() > 0){
                totalHrsAtraso = Utilidades.sumTimesList(listaHorasAtraso);
            }
            if (listaHorasJustificadas.size() > 0){
                totalHrsJustificadas = Utilidades.sumTimesList(listaHorasJustificadas);
            }
            String auxRut = _objEmpleado.getRut();
            System.out.println(WEB_NAME+"[AsistenciaReport."
                + "getParameters]auxRut: " + auxRut);
            
            if (_objEmpleado.getAction() != null 
                    && _objEmpleado.getAction().compareTo("solo_un_rut") == 0){
                System.out.println(WEB_NAME+"[AsistenciaReport."
                    + "getParameters]Un solo rut, "
                    + "buscar datos faltantes en tabla empleado...");
                _objEmpleado = 
                    m_empleadosBp.getEmpleado(empresaParam, 
                    _objEmpleado.getRut(), 
                    Integer.parseInt(turnoParam));
            }
            if (_objEmpleado != null){
                System.out.println(WEB_NAME+"[AsistenciaReport]"
                    + "totalHrsTrabajadas: " + totalHrsTrabajadas
                    + ", countDiasTrabajados: " + countDiasTrabajados);
                
                EmpresaVO empresa = empresaBp.getEmpresaByKey(empresaParam);
                parameters.put("empresa_id", _objEmpleado.getEmpresa().getId());
                parameters.put("empresa_rut", empresa.getRut());
                parameters.put("empresa_nombre", _objEmpleado.getEmpresa().getNombre());
                parameters.put("empresa_direccion", empresa.getDireccion());
                //nuevos
                parameters.put("empresa_comuna", empresa.getComunaNombre());
                parameters.put("empresa_region", empresa.getRegionNombre());
                
                parameters.put("rut", _objEmpleado.getRut());//cod_interno
                parameters.put("rut_full", _objEmpleado.getCodInterno());
                parameters.put("cod_interno", _objEmpleado.getRut());
                parameters.put("nombre", _objEmpleado.getNombres() + " " +_objEmpleado.getApeMaterno());
                parameters.put("cargo", _objEmpleado.getNombreCargo());
                //parameters.put("turnoId", infoempleado.getNombreCargo());
                
                parameters.put("cenco_id", ""+_objEmpleado.getCentroCosto().getId());
                parameters.put("cenco_nombre", _objEmpleado.getCentroCosto().getNombre());
                parameters.put("fecha_ingreso", _objEmpleado.getFechaInicioContratoAsStr());
                parameters.put("startDate", startDateParam);
                parameters.put("endDate", endDateParam);
                parameters.put("totalHrsPresenciales", totalHrsPresenciales);
                parameters.put("totalHrsTrabajadas", totalHrsTrabajadas);
                parameters.put("totalHrsAusencia", totalHrsAusencia);
                parameters.put("totalHrsAtraso", totalHrsAtraso);
                parameters.put("totalHrsExtras", totalHrsExtras);
                parameters.put("totalHrsJustificadas", totalHrsJustificadas);
                parameters.put("diasTrabajados", countDiasTrabajados);
                parameters.put("diasAusente", countDiasAusente);
            }else{
                System.err.println("[AsistenciaReport."
                    + "getParameters]"
                    + "No se encontro empleado "
                    + "rut= " + auxRut
                    + ", turno= "  + turnoParam);
                parameters = null;
            }
            
            System.out.println(WEB_NAME+"[AsistenciaReport."
                + "getParameters]"
                + "startDate= " +startDateParam
                + ", endDate= " +endDateParam);
        }
        return parameters;
    }
    
    private List<EmpleadoVO> getEmpleados(String _empresaId,
            String _deptoId, 
            int _cencoId, 
            int _turnoId){
    
        List<EmpleadoVO> listaEmpleados;
        listaEmpleados = new ArrayList<>();
        
        EmpleadosBp empleadosBp = new EmpleadosBp(new PropertiesVO());
        System.out.println(WEB_NAME+"[AsistenciaReport.getEmpleados]"
            + "empresaId: " + _empresaId
            + ",deptoId: " + _deptoId    
            + ", cenco Id: " + _cencoId
            + ", turno_Id: " + _turnoId);
        //empleados del cenco
        listaEmpleados = empleadosBp.getEmpleadosNew(_empresaId, 
            _deptoId, 
            _cencoId, 
            _turnoId);
                        
        return listaEmpleados;
    }
    
//////    /**
//////     * totales de asistencia para todos los empleados del cenco seleccionado
//////     */
//////    private LinkedHashMap<String, AsistenciaTotalesVO> getTotalesAsistenciaCenco(HttpServletRequest _request){
//////    
//////        LinkedHashMap<String, AsistenciaTotalesVO>  totalesAsistencia = new LinkedHashMap<>();
//////    
//////        //DepartamentoBp deptosBp = new DepartamentoBp(new PropertiesVO());
//////        //CentroCostoBp cencosBp  = new CentroCostoBp(new PropertiesVO());
//////        EmpleadosBp empleadosBp = new EmpleadosBp(new PropertiesVO());
//////        
//////        List<EmpleadoVO> listaEmpleados          = new ArrayList<>();
//////        String empresaId = _request.getParameter("empresa"); 
//////        String cencoId = _request.getParameter("cenco");
//////        String deptoId = _request.getParameter("depto");
////////        List<DepartamentoVO> departamentos = 
////////            deptosBp.getDepartamentosEmpresa(empresaId);
////////        for (int i = 0; i < departamentos.size(); i++) {
////////            DepartamentoVO itDepto= departamentos.get(i);
////////            System.out.println(WEB_NAME+"[AsistenciaReport.getTotalesAsistencia]"
////////                + "Depto: " + itDepto.getId()
////////                + ", nombre: " + itDepto.getNombre());
////////            UsuarioVO auxUser = new UsuarioVO();
////////            auxUser.setIdPerfil(1);
////////            List<CentroCostoVO> cencos = 
////////                cencosBp.getCentrosCostoDepto(auxUser, itDepto.getId());
//////            //for (int j = 0; j < cencos.size(); j++) {
//////                //CentroCostoVO itCenco = cencos.get(j);
//////                System.out.println(WEB_NAME+"[AsistenciaReport.getTotalesAsistencia]"
//////                    + "Cenco Id: " + cencoId);
//////                //if (itCenco.getId() != -1){
//////                    //empleados del cenco
//////                    listaEmpleados = empleadosBp.getListaEmpleados(empresaId, 
//////                        deptoId, Integer.parseInt(cencoId));
//////                    //iterar empleados...
//////                    if (listaEmpleados.size() > 0){
//////                        for (EmpleadoVO empleado : listaEmpleados) {
//////                            System.out.println(WEB_NAME+"[AsistenciaReport.getTotalesAsistencia]"
//////                                + "Empleado a mostrar: " + empleado.getRut()
//////                                +", Cenco nombre: " + empleado.getCentroCosto().getNombre());
//////
//////                            Map valores = getParameters(_request, empleado.getRut());
//////                            if (valores != null){
//////                                AsistenciaTotalesVO totalesRut = new AsistenciaTotalesVO();
//////                                String totalHrsPresenciales = (String)valores.get("totalHrsPresenciales"); 
//////                                String totalHrsAtraso       = (String)valores.get("totalHrsAtraso"); 
//////                                String totalHrsJustificadas = (String)valores.get("totalHrsJustificadas"); 
//////                                String totalHrsExtras       = (String)valores.get("totalHrsExtras"); 
//////                                String totalHrsAusencia     = (String)valores.get("totalHrsAusencia"); 
//////                                totalesRut.setCencoNombre(empleado.getCentroCosto().getNombre());
//////                                totalesRut.setRutEmpleado(empleado.getRut());
//////                                totalesRut.setNombreEmpleado(empleado.getNombres()+" "+empleado.getApeMaterno());
//////
//////                                totalesRut.setHrsPresenciales(totalHrsPresenciales);
//////                                totalesRut.setHrsAtraso(totalHrsAtraso);
//////                                totalesRut.setHrsJustificadas(totalHrsJustificadas);
//////                                totalesRut.setHrsExtras(totalHrsExtras);
//////                                totalesRut.setHrsNoTrabajadas(totalHrsAusencia);
//////                                //key: cencoId+|+rut
//////                                totalesAsistencia.put(empleado.getCentroCosto().getId()
//////                                    + "|" + empleado.getRut(), totalesRut);
//////                            }
//////                        }
//////                    }
//////                //}
//////            //}
//////        //}
//////        return totalesAsistencia;
//////    }
    
    /**
     * Totales de asistencia para todos los empleados del o los cencos correspondientes.
     * 
     */
    private LinkedHashMap<String, AsistenciaTotalesVO> 
            getTotalesAsistenciaEmpresa(HttpServletRequest _request, 
                UsuarioVO _userConnected){
    
        LinkedHashMap<String, AsistenciaTotalesVO>  totalesAsistencia = new LinkedHashMap<>();
    
        DepartamentoBp deptosBp = new DepartamentoBp(new PropertiesVO());
        CentroCostoBp cencosBp  = new CentroCostoBp(new PropertiesVO());
        EmpleadosBp empleadosBp = new EmpleadosBp(new PropertiesVO());
        
        List<EmpleadoVO> listaEmpleados          = new ArrayList<>();
        String empresaId = _request.getParameter("empresa"); 
        String deptoParamId = _request.getParameter("depto"); 
        String cencoParamId = _request.getParameter("cenco"); 
        String startDateParam = _request.getParameter("startDate");
        String endDateParam = _request.getParameter("endDate");
        
        System.out.println(WEB_NAME+"[AsistenciaReport."
            + "getTotalesAsistenciaEmpresa]"
            + "perfil usuario: " + _userConnected.getIdPerfil()
            + "param empresa: " + empresaId);
            
        List<DepartamentoVO> departamentos=null; 
        //deptosBp.getDepartamentosEmpresa(_userConnected, empresaId);
        List<CentroCostoVO> cencos=null;
        if (deptoParamId != null && deptoParamId.compareTo("-1") != 0){
            System.out.println(WEB_NAME+"[AsistenciaReport."
                + "getTotalesAsistenciaEmpresa]"
                + "Generar reporte solo para deptoId: " + deptoParamId);
            DepartamentoVO auxDepto = 
                new DepartamentoVO(deptoParamId,"", empresaId);
            departamentos = new ArrayList<DepartamentoVO>();
            departamentos.add(auxDepto);
        }else{
            System.out.println(WEB_NAME+"[AsistenciaReport."
                + "getTotalesAsistenciaEmpresa]"
                + "Generar reporte para todos los deptos de la empresaId: " + empresaId);
            departamentos = deptosBp.getDepartamentosEmpresa(_userConnected, empresaId);
        }
        
        for (int i = 0; i < departamentos.size(); i++) {
            DepartamentoVO itDepto= departamentos.get(i);
            System.out.println(WEB_NAME+"[AsistenciaReport.getTotalesAsistenciaEmpresa]"
                + "Depto: " + itDepto.getId()
                + ", nombre: " + itDepto.getNombre());
            if (cencoParamId != null && cencoParamId.compareTo("-1") != 0){
                System.out.println(WEB_NAME+"[AsistenciaReport."
                    + "getTotalesAsistenciaEmpresa]"
                    + "Generar reporte solo para cencoId: " + cencoParamId);
                CentroCostoVO auxCenco = 
                    new CentroCostoVO(Integer.parseInt(cencoParamId),"", 1, deptoParamId);
                cencos = new ArrayList<CentroCostoVO>();
                cencos.add(auxCenco);
            }else{
                System.out.println(WEB_NAME+"[AsistenciaReport."
                    + "getTotalesAsistenciaEmpresa]"
                    + "Generar reporte para todos los "
                    + "cencos de la empresaId: " + empresaId
                    + ", deptoId: " + itDepto.getId());
                cencos = 
                    cencosBp.getCentrosCostoDepto(_userConnected, itDepto.getId());
            }
            for (int j = 0; j < cencos.size(); j++) {
                CentroCostoVO itCenco = cencos.get(j);
                System.out.println(WEB_NAME+"[AsistenciaReport.getTotalesAsistenciaEmpresa]"
                    + "Cenco Id: " + itCenco.getId() 
                    + ", Cenco Name: " + itCenco.getNombre() 
                    + ". Solo empleados vigentes y sin articulo22");
                if (itCenco.getId() != -1){
                    //empleados del cenco
                    System.out.println(WEB_NAME+"[AsistenciaReport.getTotalesAsistenciaEmpresa]"
                        + "Buscar empleados para empresaId: " + empresaId 
                        + ", deptoId: " + itDepto.getId()
                        + ", cencoId: " + itCenco.getId());
                    listaEmpleados = 
                        empleadosBp.getEmpleadosSimpleByFiltro(empresaId, 
                        itDepto.getId(), itCenco.getId(), 1, false);
                    if (listaEmpleados.size() > 0){
                        System.out.println(WEB_NAME+"[AsistenciaReport.getTotalesAsistenciaEmpresa]"
                            + "Obteniendo detalle asistencia "
                            + "para todos los empleados vigentes y sin articulo22...");
                        LinkedHashMap<String,List<DetalleAsistenciaVO>> detalles 
                            = m_detAsistenciaBp.getDetallesInforme(listaEmpleados, 
                                    startDateParam, 
                                    endDateParam, -1);

                        //iterar empleados...
                    
                        for (EmpleadoVO empleado : listaEmpleados) {
                            System.out.println(WEB_NAME+"[AsistenciaReport.getTotalesAsistenciaEmpresa]"
                                + "Empleado a mostrar: " + empleado.getRut()
                                +", Cenco nombre: " + empleado.getCencoNombre());
                            List<DetalleAsistenciaVO> detalleAsistenciaRut = detalles.get(empleado.getRut());
                            System.out.println(WEB_NAME+"[AsistenciaReport.getTotalesAsistenciaEmpresa]aquiii "
                                + "detalleAsistenciaRut: " + detalleAsistenciaRut);
                            
                            //m_detAsistenciaBp.openDbConnection();
                            Map valores = getParameters(_request, empleado, detalleAsistenciaRut);
                            //m_detAsistenciaBp.closeDbConnection();
                            if (valores != null){
                                AsistenciaTotalesVO totalesRut = new AsistenciaTotalesVO();
                                String totalHrsPresenciales = (String)valores.get("totalHrsPresenciales"); 
                                String totalHrsAtraso       = (String)valores.get("totalHrsAtraso"); 
                                String totalHrsJustificadas = (String)valores.get("totalHrsJustificadas"); 
                                String totalHrsExtras       = (String)valores.get("totalHrsExtras"); 
                                String totalHrsAusencia     = (String)valores.get("totalHrsAusencia"); 
                                String totalHrsTrabajadas   = (String)valores.get("totalHrsTrabajadas"); 
                                
                                totalesRut.setCencoNombre(empleado.getCencoNombre());
                                totalesRut.setRutEmpleado(empleado.getRut());
                                totalesRut.setNombreEmpleado(empleado.getNombres());

                                totalesRut.setHrsPresenciales(totalHrsPresenciales);
                                totalesRut.setHrsAtraso(totalHrsAtraso);
                                totalesRut.setHrsJustificadas(totalHrsJustificadas);
                                totalesRut.setHrsExtras(totalHrsExtras);
                                totalesRut.setHrsNoTrabajadas(totalHrsAusencia);
                                totalesRut.setHrsTotalDelDia(totalHrsTrabajadas);
                                
                                //key: cencoId+|+rut
                                totalesAsistencia.put(empleado.getCencoId()
                                    + "|" + empleado.getRut(), totalesRut);
                            }
                        }
                    }else{
                        System.out.println(WEB_NAME+"[AsistenciaReport.getTotalesAsistenciaEmpresa]"
                            + "No hay empleados para empresaId: " + empresaId 
                            + ", deptoId: " + itDepto.getId()
                            + ", cencoId: " + itCenco.getId());
                    }
                }
            }
        }
        return totalesAsistencia;
    }
    
    protected String setTotalesAsistenciaToCSV(HttpServletRequest _request, 
            LinkedHashMap<String, AsistenciaTotalesVO> _totalesAsistencia, EmpleadoVO _empleado)
    throws ServletException, IOException {
        
        String filePath="";
        PrintWriter outfile=null;
        try {
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            HttpSession session = _request.getSession(true);
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
            String empresaId        = _request.getParameter("empresa");
            String deptoId          = _request.getParameter("depto");
            String strCencoId       = _request.getParameter("cenco");
            String startDateParam   = _request.getParameter("startDate");
            String endDateParam     = _request.getParameter("endDate");
            int intCencoId          = Integer.parseInt(strCencoId);
            CentroCostoBp cencoBp   = new CentroCostoBp(appProperties);
            System.out.println(WEB_NAME+"[AsistenciaReport.setTotalesAsistenciaToCSV]"
                + "empresaId: " + empresaId
                + ", deptoId: " + deptoId
                + ", strCencoId: " + strCencoId
                + ", startDate: " + startDateParam    
                + ", endDate: " + endDateParam);
            
            String jsonOutput = cencoBp.getEmpresaDeptoCencoJson(empresaId, deptoId, intCencoId);
            FiltroBusquedaJsonVO labelsFiltro = (FiltroBusquedaJsonVO)new Gson().fromJson(jsonOutput, FiltroBusquedaJsonVO.class);
            
            String separatorFields = ";";
            if (_totalesAsistencia != null && _totalesAsistencia.size() > 0){
                filePath = appProperties.getPathExportedFiles()+
                    File.separator+
                    userConnected.getUsername()+"_resumen_asistencia.csv";
                FileWriter filewriter = new FileWriter(filePath);
                outfile     = new PrintWriter(filewriter);
                NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "ES"));
                Utilidades utils=new Utilidades();
                int filas=0;

                //cabecera del archivo
                outfile.println("Empresa;"+labelsFiltro.getEmpresanombre());
                outfile.println("Departamento;"+labelsFiltro.getDeptonombre());
                outfile.println("Centro de costo;"+labelsFiltro.getCenconombre());
                outfile.println("Fecha inicio;" + startDateParam);
                outfile.println("Fecha fin;" + endDateParam);
                outfile.println("");
                //cabecera de los datos
                outfile.println("Cenco;Rut Empleado;Nombre Empleado;Horas presenciales;"
                    + "Atrasos;Horas justificadas;Horas extras;Horas no trabajadas;Total del dia");

                Set<String> keys = _totalesAsistencia.keySet();
                for(String k:keys){
                    filas++;
                    AsistenciaTotalesVO detailObj = _totalesAsistencia.get(k);

                    //escribir lineas en el archivo
                    outfile.print(detailObj.getCencoNombre());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getRutEmpleado());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getNombreEmpleado());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getHrsPresenciales());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getHrsAtraso());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getHrsJustificadas());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getHrsExtras());
                    outfile.print(separatorFields);
                    outfile.print(detailObj.getHrsNoTrabajadas());
                    outfile.print(separatorFields);

                    if (filas < _totalesAsistencia.size()){
                        outfile.println(detailObj.getHrsTotalDelDia());
                    }else{
                        outfile.print(detailObj.getHrsTotalDelDia());
                    }
                }

               //Flush the output to the file
               outfile.flush();

               //Close the Print Writer
               outfile.close();

               //Close the File Writer
               filewriter.close();
            }
        } finally {
            if (outfile!=null) outfile.close();
        }

        return filePath;
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        System.out.println(WEB_NAME+"[AsistenciaReport.doGet]entrando...");
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
//
//        String startDateParam = request.getParameter("startDate");
//        String endDateParam = request.getParameter("endDate");
//        
        if (userConnected != null){
                doPost(request, response);
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                +" no valida");
            System.out.println(WEB_NAME+"Sesion de usuario "+request.getParameter("username")
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
        HttpSession session     = request.getSession(true);
        ServletContext application  = this.getServletContext();
        PropertiesVO appProperties  = (PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        String empresaId        = request.getParameter("empresa");
        String deptoId          = request.getParameter("depto");
        String strCencoId       = request.getParameter("cenco");
        String rutParam         = request.getParameter("rut");
        String turnoParam       = request.getParameter("turno");
        String tipoParam        = request.getParameter("tipo");
        String formato          = request.getParameter("formato");
        String startDateParam   = request.getParameter("startDate");
        String endDateParam     = request.getParameter("endDate");
        System.out.println(WEB_NAME+"[AsistenciaReport.doPost]"
            + "empresaId: " + empresaId
            + ", deptoId: " + deptoId
            + ", strCencoId: " + strCencoId
            + ", turnoId: " + turnoParam    
            + ", rutParam: " + rutParam
            + ", tipoParam: " + tipoParam
            + ", formato: " + formato);
        
        int cencoId = -1;
        if (strCencoId != null) cencoId = Integer.parseInt(strCencoId);
        int turnoId = -1;
        if (turnoParam != null) turnoId = Integer.parseInt(turnoParam);
           
        try {
            m_dbLocator             = DatabaseLocator.getInstance();
            m_dbpoolName            = appProperties.getDbPoolName();
            m_databaseConnection    = m_dbLocator.getConnection(m_dbpoolName,"[AsistenciaReport.doPost]");
            System.out.println(WEB_NAME+"[AsistenciaReport.doPost]"
                + "Abrir conexion a la BD. Datasource: " + m_dbpoolName);
        } catch (DatabaseException ex) {
            System.err.println("Error: "+ex.toString());
        }
            
        if (rutParam != null 
                && rutParam.compareTo("todos") == 0
                && tipoParam.compareTo("3") != 0){
            //generar informes por centro de costo
            LinkedHashMap<String, String> archivosGenerados = new LinkedHashMap<>();
            System.out.println(WEB_NAME+"[AsistenciaReport.doPost]"
                + "Generar informes para todos los empleados de "
                + "empresaId: " + empresaId
                + ", deptoId: " + deptoId
                + ", cencoId: " + cencoId
                + ", turnoId: " + turnoId);
            
            List<EmpleadoVO> listaEmpleados = 
                getEmpleados(empresaId, deptoId, cencoId, turnoId);
            
            FileGeneratedVO archivoGenerado;
            String nombreCenco="";
            
            System.out.println(WEB_NAME+"[AsistenciaReport.doPost]"
                + "Obteniendo detalle asistencia para todos los empleados seleccionados...");
            LinkedHashMap<String,List<DetalleAsistenciaVO>> detalles 
                = m_detAsistenciaBp.getDetallesInforme(listaEmpleados, startDateParam, endDateParam,-1);
                    
            for (EmpleadoVO empleado : listaEmpleados) {
                System.out.println(WEB_NAME+"[AsistenciaReport.doPost]"
                    + "Generar informe para empleado rut: " + empleado.getRut()
                    +", nombre: " + empleado.getNombreCompleto());
                if (nombreCenco.compareTo("") == 0) nombreCenco = empleado.getCencoNombre();
                List<DetalleAsistenciaVO> detalleAsistenciaRut = detalles.get(empleado.getRut());
                if (detalleAsistenciaRut!=null){
                    System.out.println(WEB_NAME+"[AsistenciaReport.doPost]"
                        + "detalleAsistenciaRut != null!");
                }else{
                    System.out.println(WEB_NAME+"[AsistenciaReport.doPost]"
                        + "detalleAsistenciaRut is null!");
                }
                
                archivoGenerado = processRequestRut(request, response, empleado, detalleAsistenciaRut);
                
                if (archivoGenerado != null){
                    System.out.println(WEB_NAME+"[AsistenciaReport.doPost]Add "
                        + "archivo generado: " + archivoGenerado.getFileName());
                    archivosGenerados.put(empleado.getRut(), archivoGenerado.getFilePath());
                }    
            }
                        
            if (archivosGenerados.size() > 0){
                archivoGenerado = mergePdfFiles(archivosGenerados, userConnected, nombreCenco);
                showFileToDownload(archivoGenerado, tipoParam, formato, response);
                File auxpdf=new File(archivoGenerado.getFilePath());
                auxpdf.delete();
                
                //agregar evento al log.
                MaintenanceEventsBp eventosBp   = new MaintenanceEventsBp(null);
                MaintenanceEventVO resultado=new MaintenanceEventVO();
                resultado.setUsername(userConnected.getUsername());
                resultado.setDatetime(new Date());
                resultado.setUserIP(request.getRemoteAddr());
                resultado.setType("DTA");
                resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                resultado.setEmpresaId(empresaId);
                resultado.setDeptoId(deptoId);
                resultado.setCencoId(cencoId);
                resultado.setDescription("Consulta reporte de asistencia.");
                eventosBp.addEvent(resultado);
                
            }else{
                session.setAttribute("mensaje", Constantes.REPORTE_SIN_DATOS);
                request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
            }
            
            //m_detAsistenciaBp.closeDbConnection();
        }else{
            System.out.println(WEB_NAME+"[AsistenciaReport.doPost]"
                + "Generar reporte asistencia solo para el rut: " + rutParam);
            EmpleadoVO singleEmpleado = new EmpleadoVO();
            singleEmpleado.setRut(rutParam);
            singleEmpleado.setAction("solo_un_rut");
            FileGeneratedVO filegenerated=processRequestRut(request, response, singleEmpleado, null);
            if (filegenerated != null){
                System.out.println(WEB_NAME+"[AsistenciaReport.doPost]Add "
                    + "archivo generado: " + filegenerated.getFileName());
                showFileToDownload(filegenerated, tipoParam, formato, response);
                File auxpdf=new File(filegenerated.getFilePath());
                auxpdf.delete();
                //agregar evento al log.
                MaintenanceEventsBp eventosBp   = new MaintenanceEventsBp(null);
                MaintenanceEventVO resultado=new MaintenanceEventVO();
                resultado.setUsername(userConnected.getUsername());
                resultado.setDatetime(new Date());
                resultado.setUserIP(request.getRemoteAddr());
                resultado.setType("DTA");
                resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                resultado.setEmpresaId(empresaId);
                resultado.setDeptoId(deptoId);
                resultado.setCencoId(cencoId);
                resultado.setDescription("Consulta reporte de asistencia.");
                eventosBp.addEvent(resultado);
            }else{
                System.out.println(WEB_NAME+"[AsistenciaReport.doPost]No Generar "
                    + "archivo. Mostrar mensaje...");
                session.setAttribute("mensaje", Constantes.REPORTE_SIN_DATOS);
                request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
            }
        }
        System.out.println(WEB_NAME+"[AsistenciaReport.doPost]Cerrar conexiones a la BD...");
        //m_empleadosBp.closeDbConnection();
        //m_detAsistenciaBp.closeDbConnection();
    }

    /**
    * 
    */
    private void showFileToDownload(FileGeneratedVO _fileGenerated,
            String _tipoParam,String _formato,
            HttpServletResponse _response){
        String contentType  = "application/pdf";
        File auxFile = new File(_fileGenerated.getFilePath());
        int length   = 0;
        try{
            if (_tipoParam.compareTo("3") == 0){
                ServletOutputStream outStream = _response.getOutputStream();
                ServletContext context  = getServletConfig().getServletContext();
                String mimetype = context.getMimeType(auxFile.getName());

                // sets response content type
                if (mimetype == null) {
                    mimetype = "application/octet-stream";
                }
                _response.setContentType(mimetype);
                _response.setContentLength((int)auxFile.length());

                // sets HTTP header
                _response.setHeader("Content-Disposition", "attachment; filename=\"" + _fileGenerated.getFileName() + "\"");

                byte[] byteBuffer = new byte[4096];
                DataInputStream in = new DataInputStream(new FileInputStream(auxFile));

                // reads the file's bytes and writes them to the response stream
                while ((in != null) && ((length = in.read(byteBuffer)) != -1))
                {
                    outStream.write(byteBuffer,0,length);
                }

                in.close();
                outStream.close();
            }else{
                //pdf
                if (_formato.compareTo("pdf") == 0){
                    System.out.println(WEB_NAME+"[showFileToDownload]Generar PDF."
                        + " fileName: " + _fileGenerated.getFileName());
                    try{
    //                    fullPath = 
    //                        appProperties.getPathExportedFiles()+File.separator+fileName;
    //                    JasperRunManager.runReportToPdfFile(jasperFileName, fullPath, parameters, dbConn);
                        File pointToFile = new File(_fileGenerated.getFilePath());
                        ByteArrayInputStream byteArrayInputStream = 
                            new ByteArrayInputStream(FileUtils.readFileToByteArray(pointToFile));
                        _response.setContentType(contentType);
                        _response.addHeader("Content-Disposition", 
                            "attachment; filename=" + _fileGenerated.getFileName());
                        OutputStream responseOutputStream = _response.getOutputStream();

                        byte[] buf = new byte[4096];
                        int len = -1;

                        while ((len = byteArrayInputStream.read(buf)) != -1) {
                          responseOutputStream.write(buf, 0, len);
                        }
                        responseOutputStream.flush();
                        responseOutputStream.close();
                    }catch(Exception e){
                        System.err.println("[showFileToDownload]Error al generar pdf: "+e.toString());
                        e.printStackTrace();
                    }
                }

            }
        }catch(IOException ioex){
            System.err.println("[showFileToDownload]Error al "
                + "abrir archivo: "+ioex.toString());
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

    /**
     * Permite unir todos los PDF de cada empleado en un solo PDF.
     * 
     * @param _archivos
     * @param _usuario
     * @param _cencoNombre
     * @return 
     */
    public FileGeneratedVO mergePdfFiles(LinkedHashMap<String, String> _archivos, 
            UsuarioVO _usuario, String _cencoNombre){
        ServletContext application  = this.getServletContext();
        PropertiesVO appProperties  = (PropertiesVO)application.getAttribute("appProperties");
        FileGeneratedVO archivoGenerado = null;
        File mergedFile = new File(appProperties.getPathExportedFiles()+File.separator
            +_usuario.getUsername()
            + "_"+_cencoNombre + "_todos.pdf");

        archivoGenerado=new FileGeneratedVO(mergedFile.getName(), mergedFile.getAbsolutePath());
        System.out.println(WEB_NAME+"[mergePdfFiles]"
            + "uniendo archivos en uno solo. "
            + "Path: " + mergedFile.getAbsolutePath());

        try
        {
            PDFMergerUtility mergePdf = new PDFMergerUtility();
            for( String key : _archivos.keySet() ){
                String pathFile = _archivos.get(key);
                File itFile = new File(pathFile);
                System.out.println(WEB_NAME+"[mergePdfFiles]itera archivo " + pathFile);
                
                mergePdf.addSource(itFile);
                
            }
            mergePdf.setDestinationFileName(archivoGenerado.getFilePath());
            mergePdf.mergeDocuments();
            
            for( String key : _archivos.keySet() ){
                String pathFile2 = _archivos.get(key);
                File itFile2 = new File(pathFile2);
                System.out.println(WEB_NAME+"[mergePdfFiles]Eliminando archivo " + pathFile2);
                itFile2.delete();
            }
            
        }
        catch(IOException e)
        {

        }
        
        return archivoGenerado;
    }
    
    
    public void createPdfTodos(UsuarioVO _usuario)
    {
        PDDocument document = null;
        try
        {
            ServletContext application  = this.getServletContext();
            PropertiesVO appProperties  = (PropertiesVO)application.getAttribute("appProperties");
            String filename=appProperties.getPathExportedFiles()+File.separator+_usuario.getUsername()+"_todos.pdf";
            document=new PDDocument();
            PDPage blankPage = new PDPage();
            document.addPage( blankPage );
            document.save( filename );
        }
        catch(Exception e)
        {

        }
    }

//    /**
//     * Permite unir todos los PDF de cada empleado en un solo PDF.
//     * 
//     * @param _archivos
//     * @param _usuario
//     * @return 
//     */
//    public FileGeneratedVO mergePdfFiles(LinkedHashMap<String, String> _archivos, UsuarioVO _usuario){
//        ServletContext application  = this.getServletContext();
//        PropertiesVO appProperties  = (PropertiesVO)application.getAttribute("appProperties");
//        File mergedFile = null;
//        mergedFile = new File(appProperties.getPathExportedFiles()+File.separator+_usuario.getUsername()+"_todos.pdf");
//        FileGeneratedVO archivoGenerado=new FileGeneratedVO(mergedFile.getName(), mergedFile.getAbsolutePath());
//        System.out.println(WEB_NAME+"[mergePdfFiles]uniendo archivos en uno solo...");
//        try{
//            Document document = new Document();
//            FileOutputStream fos = new FileOutputStream(mergedFile);
//            PdfCopy copy = new PdfCopy(document, fos);
//            document.open();
//            PdfImportedPage page;
//            PdfCopy.PageStamp stamp;
//            Phrase phrase;
//            BaseFont bf = BaseFont.createFont();
//            Font font = new Font(bf, 9);
//            LinkedHashMap<String, PdfReader> readers = new LinkedHashMap<>();
//            for( String key : _archivos.keySet() ){
//                String pathFile = _archivos.get(key);
//                System.out.println(WEB_NAME+"[mergePdfFiles]itera archivo " + pathFile);
//                PdfReader itReader = new PdfReader(pathFile);
//                int n = itReader.getNumberOfPages();
//                for (int i = 1; i <= itReader.getNumberOfPages(); i++) {
//                    page = copy.getImportedPage(itReader, i);
//                    stamp = copy.createPageStamp(page);
//                    phrase = new Phrase("page " + i, font);
//                    ColumnText.showTextAligned(stamp.getOverContent(), Element.ALIGN_CENTER, phrase, 520, 5, 0);
//                    stamp.alterContents();
//                    copy.addPage(page);
//                    
//                    itReader.close();
//                }
//            }
//            document.close();
////            reader1.close();
////            reader2.close();
//        }catch(FileNotFoundException ioex){
//            System.err.println("[mergePdfFiles]"
//                + "Error IO: " + ioex);
//        }catch(IOException ioex){
//            System.err.println("[mergePdfFiles]"
//                + "Error IO: " + ioex);
//        }catch(DocumentException dex){
//            System.err.println("[mergePdfFiles]"
//                + "Document error: " + dex);
//        }
//    
//        return archivoGenerado;
//    }
    
    private static class FileGeneratedVO {

        private final String fileName;
        private final String filePath;
        
        public FileGeneratedVO(String _fileName,String _filePath) {
            this.fileName = _fileName;
            this.filePath = _filePath;
        }

        public String getFileName() {
            return fileName;
        }

        public String getFilePath() {
            return filePath;
        }

        
        
    }

}
