/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.vo;

    import java.io.Serializable;

/**
 *
 * @author Alexander
 */
public class DetalleMarcaVO implements Serializable{

    private static final long serialVersionUID = 987774229569759620L;

    private final String empresaId;
    private final String rutEmpleado;
    private final String fechaEntrada;
    private final String fechaSalida;
    private final String horaEntrada;
    private final String horaSalida;
    private final String horaEntradaFull;
    private final String horaSalidaFull;
    private String comentarioMarcaEntrada;
    private String comentarioMarcaSalida;


    public DetalleMarcaVO(String _empresaId, 
            String _rutEmpleado, 
            String _horaEntrada,
            String _horaSalida,
            String _horaEntradaFull,
            String _horaSalidaFull,
            String _fechaEntrada,
            String _fechaSalida,
            String _comentarioEntrada,
            String _comentarioSalida) {
        this.empresaId = _empresaId;
        this.rutEmpleado = _rutEmpleado;
        this.fechaEntrada = _fechaEntrada;
        this.fechaSalida = _fechaSalida;
        this.horaEntrada = _horaEntrada;
        this.horaSalida = _horaSalida;
        this.horaEntradaFull = _horaEntradaFull;
        this.horaSalidaFull = _horaSalidaFull;
        this.comentarioMarcaEntrada = _comentarioEntrada;
        this.comentarioMarcaSalida = _comentarioSalida;
    }

    public String getHoraEntradaFull() {
        return horaEntradaFull;
    }

    public String getHoraSalidaFull() {
        return horaSalidaFull;
    }

    
    
    public String getFechaEntrada() {
        return fechaEntrada;
    }

    public String getFechaSalida() {
        return fechaSalida;
    }

    public String getHoraEntrada() {
        return horaEntrada;
    }

    public String getHoraSalida() {
        return horaSalida;
    }
   

}
