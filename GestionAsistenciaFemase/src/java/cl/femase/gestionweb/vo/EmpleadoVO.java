/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.vo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

/**
 *
 * @author Alexander
 */
public class EmpleadoVO implements Serializable{

    private static final long serialVersionUID = 7599472295789776220L;

    private String rut;
    
    private String empresaId;
    private String deptoId;
    private int cencoId;
    
    private String codInterno;
    private String codInternoCaracterAdicional;
    private String rutParam;
    private String nombres;
    private String apePaterno;
    private String apeMaterno;
    private Date fechaNacimiento;
    private String fechaNacimientoAsStr;
    private String direccion="";
    private String email="";
    private Date fechaInicioContrato;
    private String fechaInicioContratoAsStr;
    private Date fechaTerminoContrato;
    private String fechaTerminoContratoAsStr="";
    private int estado;
    private String pathFoto;
    private String sexo;
    private String fonoFijo     = "";
    private String fonoMovil    = "";
    
    private String comunaNombre = "";
    private int comunaId        = -1;
    
    private int regionId        = -1;
    private String regionNombre = "";
    
    private EmpresaVO empresa;
    private DepartamentoVO departamento;
    private CentroCostoVO centroCosto;
    private Date fechaIngresoPersonal;
    private int estadoPersonal;
    private boolean autorizaAusencia;
    private boolean cambiarFoto;
    private String empresaNombre;
    private String empresaDireccion;
    private String empresaRut;
    private String deptoNombre;
    private String cencoNombre;
    private String nombreCompleto;
    private String claveMarcacion;

    private int idTurno=-1;
    private int idCargo=-1;
    private String nombreCargo;
    private String nombreTurno;
    
    private String action;
    private String action2;
    
    private int jtStartIndex=-1;
    private int jtPageSize=-1;
    private String jtSorting; 

    private boolean articulo22;
    private boolean contratoIndefinido;
    private String uploadMessageError;
    private boolean modificarEmpresaDeptoCenco;
    private String fechaDesdeTurno;
    private String fechaHastaTurno;

    /**
     * Campos agregados para parseo a objeto json
     */
    private String empresaid;
    private int idturno;
    private String deptoid;
    private int cencoid;
    private int iteracion;

    private int saldoDiasVacaciones = 0;
    private String diasEspeciales = "N";
    private boolean empleadoVigente = false;
    private String shortFullName;
    private String shortFullNameCapitalize;
    private String rutAutorizaVacacion;

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    
    
    public String getRutAutorizaVacacion() {
        return rutAutorizaVacacion;
    }

    public void setRutAutorizaVacacion(String rutAutorizaVacacion) {
        this.rutAutorizaVacacion = rutAutorizaVacacion;
    }
    
    public String getShortFullName() {
        String auxShortFullName = this.nombres + " " + this.apePaterno;
        return auxShortFullName;
    }

//    public void setFullName(String fullName) {
//        this.fullName = fullName;
//    }

    public String getShortFullNameCapitalize() {
        String auxShortFullName = this.nombres + " " + this.apePaterno;
        auxShortFullName = auxShortFullName.toLowerCase();
        // uppercase first letter of each word
        String output = Arrays.stream(auxShortFullName.split("\\s+"))
            .map(t -> t.substring(0, 1).toUpperCase() + t.substring(1))
            .collect(Collectors.joining(" "));
        return output;
    }

//    public void setFullNameCapitalize(String fullNameCapitalize) {
//        this.fullNameCapitalize = fullNameCapitalize;
//    }
    
    public boolean isEmpleadoVigente() {
        return empleadoVigente;
    }

    public void setEmpleadoVigente(boolean empleadoVigente) {
        this.empleadoVigente = empleadoVigente;
    }

    public String getDiasEspeciales() {
        return diasEspeciales;
    }

    public void setDiasEspeciales(String diasEspeciales) {
        this.diasEspeciales = diasEspeciales;
    }

    public String getCodInternoCaracterAdicional() {
        return codInternoCaracterAdicional;
    }

    public void setCodInternoCaracterAdicional(String codInternoCaracterAdicional) {
        this.codInternoCaracterAdicional = codInternoCaracterAdicional;
    }

    public int getSaldoDiasVacaciones() {
        return saldoDiasVacaciones;
    }

    public void setSaldoDiasVacaciones(int saldoDiasVacaciones) {
        this.saldoDiasVacaciones = saldoDiasVacaciones;
    }
    
    /**
     *
     * @return
     */
    public int getIteracion() {
        return iteracion;
    }

    public void setIteracion(int iteracion) {
        this.iteracion = iteracion;
    }
    
    
    
    public String getEmpresaid() {
        return empresaid;
    }

    public void setEmpresaid(String empresaid) {
        this.empresaid = empresaid;
    }

    public int getIdturno() {
        return idturno;
    }

    public void setIdturno(int idturno) {
        this.idturno = idturno;
    }

    public String getDeptoid() {
        return deptoid;
    }

    /**
     *
     * @param deptoid
     */
    public void setDeptoid(String deptoid) {
        this.deptoid = deptoid;
    }

    public int getCencoid() {
        return cencoid;
    }

    public void setCencoid(int cencoid) {
        this.cencoid = cencoid;
    }
    
    public String getEmpresaDireccion() {
        return empresaDireccion;
    }

    public void setEmpresaDireccion(String empresaDireccion) {
        this.empresaDireccion = empresaDireccion;
    }

    public String getEmpresaRut() {
        return empresaRut;
    }

    public void setEmpresaRut(String empresaRut) {
        this.empresaRut = empresaRut;
    }

    
    
    public String getFechaDesdeTurno() {
        return fechaDesdeTurno;
    }

    public void setFechaDesdeTurno(String fechaDesdeTurno) {
        this.fechaDesdeTurno = fechaDesdeTurno;
    }

    public String getFechaHastaTurno() {
        return fechaHastaTurno;
    }

    /**
     *
     * @param fechaHastaTurno
     */
    public void setFechaHastaTurno(String fechaHastaTurno) {
        this.fechaHastaTurno = fechaHastaTurno;
    }
    
    
    
    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public String getDeptoId() {
        return deptoId;
    }

    public void setDeptoId(String deptoId) {
        this.deptoId = deptoId;
    }

    public int getCencoId() {
        return cencoId;
    }

    public void setCencoId(int cencoId) {
        this.cencoId = cencoId;
    }

    
    
    public String getClaveMarcacion() {
        return claveMarcacion;
    }

    public void setClaveMarcacion(String claveMarcacion) {
        this.claveMarcacion = claveMarcacion;
    }

    public String getAction2() {
        return action2;
    }

    /**
     *
     * @param action2
     */
    public void setAction2(String action2) {
        this.action2 = action2;
    }

    public String getCodInterno() {
        return codInterno;
    }

    public void setCodInterno(String codInterno) {
        this.codInterno = codInterno;
    }

    /**
     *
     * @return
     */
    public boolean isModificarEmpresaDeptoCenco() {
        return modificarEmpresaDeptoCenco;
    }

    public void setModificarEmpresaDeptoCenco(boolean modificarEmpresaDeptoCenco) {
        this.modificarEmpresaDeptoCenco = modificarEmpresaDeptoCenco;
    }
    
    public String getUploadMessageError() {
        return uploadMessageError;
    }

    public void setUploadMessageError(String uploadMessageError) {
        this.uploadMessageError = uploadMessageError;
    }
    
    
    
    public String getRegionNombre() {
        return regionNombre;
    }

    public void setRegionNombre(String regionNombre) {
        this.regionNombre = regionNombre;
    }

    
    public String getNombreCargo() {
        return nombreCargo;
    }

    public void setNombreCargo(String nombreCargo) {
        this.nombreCargo = nombreCargo;
    }

    public String getNombreTurno() {
        return nombreTurno;
    }

    public void setNombreTurno(String nombreTurno) {
        this.nombreTurno = nombreTurno;
    }

    public String getComunaNombre() {
        return comunaNombre;
    }

    public void setComunaNombre(String comunaNombre) {
        this.comunaNombre = comunaNombre;
    }

    public Date getFechaTerminoContrato() {
        return fechaTerminoContrato;
    }

    /**
     *
     * @param fechaTerminoContrato
     */
    public void setFechaTerminoContrato(Date fechaTerminoContrato) {
        this.fechaTerminoContrato = fechaTerminoContrato;
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
        this.fechaTerminoContratoAsStr = sdf.format(fechaTerminoContrato);
    }

    public String getFechaTerminoContratoAsStr() {
        return fechaTerminoContratoAsStr;
    }

    public void setFechaTerminoContratoAsStr(String fechaTerminoContratoAsStr) {
        this.fechaTerminoContratoAsStr = fechaTerminoContratoAsStr;
    }

    public boolean isArticulo22() {
        return articulo22;
    }

    public void setArticulo22(boolean articulo22) {
        this.articulo22 = articulo22;
    }

    public boolean isContratoIndefinido() {
        return contratoIndefinido;
    }

    public void setContratoIndefinido(boolean contratoIndefinido) {
        this.contratoIndefinido = contratoIndefinido;
    }
    
    
    public boolean isCambiarFoto() {
        return cambiarFoto;
    }

    public void setCambiarFoto(boolean cambiarFoto) {
        this.cambiarFoto = cambiarFoto;
    }

    public int getIdCargo() {
        return idCargo;
    }

    public void setIdCargo(int idCargo) {
        this.idCargo = idCargo;
    }

    public String getNombreCompleto() {
        return getNombres()+" " + getApeMaterno();
    }

    public boolean isAutorizaAusencia() {
        return autorizaAusencia;
    }

    public void setAutorizaAusencia(boolean autorizaAusencia) {
        this.autorizaAusencia = autorizaAusencia;
    }

    public int getIdTurno() {
        return idTurno;
    }

    public void setIdTurno(int idTurno) {
        this.idTurno = idTurno;
    }
    
    public void setFechaNacimientoAsStr(String fechaNacimientoAsStr) {
        this.fechaNacimientoAsStr = fechaNacimientoAsStr;
    }

    public void setFechaInicioContratoAsStr(String fechaInicioContratoAsStr) {
        this.fechaInicioContratoAsStr = fechaInicioContratoAsStr;
        
    }

    public int getJtStartIndex() {
        return jtStartIndex;
    }

    public void setJtStartIndex(int jtStartIndex) {
        this.jtStartIndex = jtStartIndex;
    }

    public String getRutParam() {
        return rutParam;
    }

    public void setRutParam(String rutParam) {
        this.rutParam = rutParam;
    }

    public int getJtPageSize() {
        return jtPageSize;
    }

    public void setJtPageSize(int jtPageSize) {
        this.jtPageSize = jtPageSize;
    }
 

    public String getJtSorting() {
        return jtSorting;
    }

    public void setJtSorting(String jtSorting) {
        this.jtSorting = jtSorting;
    }

    
    
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
    
    
    
    public String getEmpresaNombre() {
        return empresaNombre;
    }

    public void setEmpresaNombre(String empresaNombre) {
        this.empresaNombre = empresaNombre;
    }

    public String getDeptoNombre() {
        return deptoNombre;
    }

    public void setDeptoNombre(String deptoNombre) {
        this.deptoNombre = deptoNombre;
    }

    public String getCencoNombre() {
        return cencoNombre;
    }

    public void setCencoNombre(String cencoNombre) {
        this.cencoNombre = cencoNombre;
    }

    public String getFechaInicioContratoAsStr() {
        return fechaInicioContratoAsStr;
    }

    public String getFechaNacimientoAsStr() {
        return fechaNacimientoAsStr;
    }

    public int getEstadoPersonal() {
        return estadoPersonal;
    }

    public void setEstadoPersonal(int estadoPersonal) {
        this.estadoPersonal = estadoPersonal;
    }
        
    public Date getFechaIngresoPersonal() {
        return fechaIngresoPersonal;
    }

    public void setFechaIngresoPersonal(Date fechaIngresoPersonal) {
        this.fechaIngresoPersonal = fechaIngresoPersonal;
    }

    public EmpresaVO getEmpresa() {
        return empresa;
    }

    public void setEmpresa(EmpresaVO empresa) {
        this.empresa = empresa;
    }

    public DepartamentoVO getDepartamento() {
        return departamento;
    }

    public void setDepartamento(DepartamentoVO departamento) {
        this.departamento = departamento;
    }

    public CentroCostoVO getCentroCosto() {
        return centroCosto;
    }

    /**
     *
     * @param centrocosto
     */
    public void setCentroCosto(CentroCostoVO centrocosto) {
        this.centroCosto = centrocosto;
    }
    
    public String getRut() {
        return rut;
    }

    public void setRut(String _rut) {
        this.rut = _rut;
        setRutParam("'"+_rut+"'");
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApePaterno() {
        return apePaterno;
    }

    public void setApePaterno(String apePaterno) {
        this.apePaterno = apePaterno;
    }

    /**
     *
     * @return
     */
    public String getApeMaterno() {
        return apeMaterno;
    }

    /**
     *
     * @param apeMaterno
     */
    public void setApeMaterno(String apeMaterno) {
        this.apeMaterno = apeMaterno;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    /**
     *
     * @param _fechaNacimiento
     */
    public void setFechaNacimiento(Date _fechaNacimiento) {
        this.fechaNacimiento = _fechaNacimiento;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getFechaInicioContrato() {
        return fechaInicioContrato;
    }

    public void setFechaInicioContrato(Date _fechaInicioContrato) {
        this.fechaInicioContrato = _fechaInicioContrato;
        
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getPathFoto() {
        return pathFoto;
    }

    public void setPathFoto(String pathFoto) {
        this.pathFoto = pathFoto;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getFonoFijo() {
        return fonoFijo;
    }

    /**
     *
     * @param fonoFijo
     */
    public void setFonoFijo(String fonoFijo) {
        this.fonoFijo = fonoFijo;
    }

    public String getFonoMovil() {
        return fonoMovil;
    }

    public void setFonoMovil(String fonoMovil) {
        this.fonoMovil = fonoMovil;
    }

    public int getComunaId() {
        return comunaId;
    }

    public void setComunaId(int comunaId) {
        this.comunaId = comunaId;
    }

//    @Override
//    public String toString() {
//        return "EmpleadoVO{" + "rut=" + rut + ", rutParam=" + rutParam + ", nombres=" + nombres + ", apePaterno=" + apePaterno + ", apeMaterno=" + apeMaterno + ", fechaNacimiento=" + fechaNacimiento + ", fechaNacimientoAsStr=" + fechaNacimientoAsStr + ", direccion=" + direccion + ", email=" + email + ", fechaInicioContrato=" + fechaInicioContrato + ", fechaInicioContratoAsStr=" + fechaInicioContratoAsStr + ", estado=" + estado + ", pathFoto=" + pathFoto + ", sexo=" + sexo + ", fonoFijo=" + fonoFijo + ", fonoMovil=" + fonoMovil + ", comunaId=" + comunaId + ", empresa=" + empresa + ", departamento=" + departamento + ", centroCosto=" + centroCosto + ", fechaIngresoPersonal=" + fechaIngresoPersonal + ", estadoPersonal=" + estadoPersonal + ", autorizaAusencia=" + autorizaAusencia + ", cambiarFoto=" + cambiarFoto + ", empresaNombre=" + empresaNombre + ", deptoNombre=" + deptoNombre + ", cencoNombre=" + cencoNombre + ", nombreCompleto=" + nombreCompleto + ", idTurno=" + idTurno + ", idCargo=" + idCargo + ", action=" + action + ", jtStartIndex=" + jtStartIndex + ", jtPageSize=" + jtPageSize + ", jtSorting=" + jtSorting + '}';
//    }

    @Override
    public String toString() {
        return "EmpleadoVO{" + "rut=" + rut + ", codInterno=" + codInterno + ", rutParam=" + rutParam + ", nombres=" + nombres + ", apePaterno=" + apePaterno + ", apeMaterno=" + apeMaterno + ", fechaNacimiento=" + fechaNacimiento + ", fechaNacimientoAsStr=" + fechaNacimientoAsStr + ", direccion=" + direccion + ", email=" + email + ", fechaInicioContrato=" + fechaInicioContrato + ", fechaInicioContratoAsStr=" + fechaInicioContratoAsStr + ", fechaTerminoContrato=" + fechaTerminoContrato + ", fechaTerminoContratoAsStr=" + fechaTerminoContratoAsStr + ", estado=" + estado + ", pathFoto=" + pathFoto + ", sexo=" + sexo + ", fonoFijo=" + fonoFijo + ", fonoMovil=" + fonoMovil + ", comunaNombre=" + comunaNombre + ", comunaId=" + comunaId + ", regionNombre=" + regionNombre + ", empresa=" + empresa + ", departamento=" + departamento + ", centroCosto=" + centroCosto + ", fechaIngresoPersonal=" + fechaIngresoPersonal + ", estadoPersonal=" + estadoPersonal + ", autorizaAusencia=" + autorizaAusencia + ", cambiarFoto=" + cambiarFoto + ", empresaNombre=" + empresaNombre + ", deptoNombre=" + deptoNombre + ", cencoNombre=" + cencoNombre + ", nombreCompleto=" + nombreCompleto + ", claveMarcacion=" + claveMarcacion + ", idTurno=" + idTurno + ", idCargo=" + idCargo + ", nombreCargo=" + nombreCargo + ", nombreTurno=" + nombreTurno + ", action=" + action + ", action2=" + action2 + ", jtStartIndex=" + jtStartIndex + ", jtPageSize=" + jtPageSize + ", jtSorting=" + jtSorting + ", articulo22=" + articulo22 + ", contratoIndefinido=" + contratoIndefinido + ", uploadMessageError=" + uploadMessageError + ", modificarEmpresaDeptoCenco=" + modificarEmpresaDeptoCenco + '}';
    }

    
   


}
