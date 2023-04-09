/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.vo;

import cl.femase.gestionweb.common.Utilidades;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class UsuarioVO implements Serializable {

    private static final long serialVersionUID = 7526472295622776220L;
    private String username;
    private String password;
    private String runEmpleado;
    private String nombres;
    private String nombreCompleto;
    private String apPaterno;
    private String apMaterno;
    private String email;
    private int idPerfil;
    private String nomPerfil;
    private String adminEmpresa;
    private String horaConexion;
    private String fechaHoraUltimaConexion;
    private int estado;
    private LinkedHashMap<String, LinkedHashMap<String, ModuloAccesoPerfilVO>> accesosModulo;
    private String labelUsuario;
    private List<UsuarioCentroCostoVO> cencos;
    private int idCencoUsuario;
    private String cencoUsuario;
    private String empresaId;
    private String empresaNombre;
    private String empresaLogo;
    private String deptoId;
    private String deptoNombre;
    private String cencoId;
    private String cencoNombre;
    private String fechaCreacion;
    private String fechaActualizacion;
    
    /**
    * Campos relacionados con la asignacion de marcacion virtual
    */
    private String marcacionVirtual;
    
    private LinkedHashMap<String,String> fechasMarcacionVirtual;
    
    private String registrarUbicacion;//'S' o 'N'

    //-------------
    private String nombresEncode;
    private String nombreCompletoEncode;
    private String apPaternoEncode;
    private String apMaternoEncode;

    public String getRunEmpleado() {
        return runEmpleado;
    }

    public void setRunEmpleado(String _runEmpleado) {
        if (_runEmpleado != null) _runEmpleado = _runEmpleado.toUpperCase();
        this.runEmpleado = _runEmpleado;
    }
    
    public String getAdminEmpresa() {
        return adminEmpresa;
    }

    public void setAdminEmpresa(String adminEmpresa) {
        this.adminEmpresa = adminEmpresa;
    }

    public String getRegistrarUbicacion() {
        return registrarUbicacion;
    }

    public void setRegistrarUbicacion(String registrarUbicacion) {
        this.registrarUbicacion = registrarUbicacion;
    }

    public String getApPaternoEncode() {
        return apPaternoEncode;
    }

    public String getNombresEncode() {
        return nombresEncode;
    }

    public String getNombreCompletoEncode() {
        return nombreCompletoEncode;
    }

    public String getApMaternoEncode() {
        return apMaternoEncode;
    }

    /**
    *
    */
    public UsuarioVO() {
       
    }
    
    public UsuarioVO(String _username,String _password, String _empresaId) {
       this.username = _username;
       this.password = _password;
       this.empresaId = _empresaId;
    }

    public String getMarcacionVirtual() {
        return marcacionVirtual;
    }

    public LinkedHashMap<String, String> getFechasMarcacionVirtual() {
        return fechasMarcacionVirtual;
    }

    public void setFechasMarcacionVirtual(LinkedHashMap<String, String> fechasMarcacionVirtual) {
        this.fechasMarcacionVirtual = fechasMarcacionVirtual;
    }
    
    public void setMarcacionVirtual(String marcacionVirtual) {
        this.marcacionVirtual = marcacionVirtual;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(String fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
    
    public String getEmpresaNombre() {
        return empresaNombre;
    }
    
    public void setEmpresaNombre(String empresaNombre) {
        this.empresaNombre = empresaNombre;
    }

    public String getDeptoId() {
        return deptoId;
    }

    public void setDeptoId(String deptoId) {
        this.deptoId = deptoId;
    }

    public String getDeptoNombre() {
        return deptoNombre;
    }

    public void setDeptoNombre(String deptoNombre) {
        this.deptoNombre = deptoNombre;
    }

    public String getCencoId() {
        return cencoId;
    }

    public void setCencoId(String cencoId) {
        this.cencoId = cencoId;
    }

    public String getCencoNombre() {
        return cencoNombre;
    }

    /**
     *
     * @param cencoNombre
     */
    public void setCencoNombre(String cencoNombre) {
        this.cencoNombre = cencoNombre;
    }

    public int getIdCencoUsuario() {
        return idCencoUsuario;
    }

    public void setIdCencoUsuario(int idCencoUsuario) {
        this.idCencoUsuario = idCencoUsuario;
    }
    
    public String getEmpresaLogo() {
        return empresaLogo;
    }

    public void setEmpresaLogo(String empresaLogo) {
        this.empresaLogo = empresaLogo;
    }

    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public List<UsuarioCentroCostoVO> getCencos() {
        return cencos;
    }

    public void setCencos(List<UsuarioCentroCostoVO> cencos) {
        this.cencos = cencos;
    }

    public String getCencoUsuario() {
        return cencoUsuario;
    }

    public void setCencoUsuario(String cencoUsuario) {
        this.cencoUsuario = cencoUsuario;
    }

    public String getLabelUsuario() {
        return labelUsuario;
    }

    public void setLabelUsuario(String labelUsuario) {
        this.labelUsuario = labelUsuario;
    }

    
    public LinkedHashMap<String, LinkedHashMap<String, ModuloAccesoPerfilVO>> getAccesosModulo() {
        return accesosModulo;
    }

    public void setAccesosModulo(LinkedHashMap<String, LinkedHashMap<String, ModuloAccesoPerfilVO>> accesosModulo) {
        this.accesosModulo = accesosModulo;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
        if (nombreCompleto != null){
            this.nombreCompletoEncode = Utilidades.encodeDecodeString(nombreCompleto);
        }
    }

    public String getNombreCompleto() {
        return this.nombreCompleto;
    }

    public String getHoraConexion() {
        return horaConexion;
    }

    public void setHoraConexion(String horaConexion) {
        this.horaConexion = horaConexion;
    }
    
    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFechaHoraUltimaConexion() {
        return fechaHoraUltimaConexion;
    }

    public void setFechaHoraUltimaConexion(String fechaHoraUltimaConexion) {
        this.fechaHoraUltimaConexion = fechaHoraUltimaConexion;
    }
    
    public int getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(int idPerfil) {
        this.idPerfil = idPerfil;
    }

    /**
     *
     * @return
     */
    public String getNomPerfil() {
        return nomPerfil;
    }

    public void setNomPerfil(String nomPerfil) {
        this.nomPerfil = nomPerfil;
    }

    /**
     * Get the value of password
     *
     * @return the value of password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the value of password
     *
     * @param password new value of password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get the value of username
     *
     * @return the value of username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the value of username
     *
     * @param username new value of username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
        if (nombres != null){
            this.nombresEncode = Utilidades.encodeDecodeString(nombres);
        }
    }

    /**
    *
    * @return
    */
    public String getApPaterno() {
        return apPaterno;
    }

    public void setApPaterno(String apPaterno) {
        this.apPaterno = apPaterno;
        if (apPaterno != null){
            this.apPaternoEncode = Utilidades.encodeDecodeString(apPaterno);
        }
    }

    /**
     *
     * @return
     */
    public String getApMaterno() {
        return apMaterno;
    }

    public void setApMaterno(String apMaterno) {
        this.apMaterno = apMaterno;
        if (apMaterno != null){
            this.apMaternoEncode = Utilidades.encodeDecodeString(apMaterno);
        }
    }
    
    
}
