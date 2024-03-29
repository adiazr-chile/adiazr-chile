/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.vo;

import java.util.ArrayList;

/**
*
* @author Alexander
*/
public class ResultCRUDVO {
    private static final long serialVersionUID = 852276598622776347L;
    
    private int codError;
    private String msgError;
    private boolean thereError;
    private String msg;
    private String msgFromSp;
    private int filasAfectadas;
    //private ArrayList<String> warningMessages = new ArrayList<String>(); 
    private FilasAfectadasJsonVO filasAfectadasObj=new FilasAfectadasJsonVO();

    ArrayList<FilasAfectadasJsonVO> empleadosAfectados = new ArrayList<>(); 

    public ArrayList<FilasAfectadasJsonVO> getEmpleadosAfectados() {
        return empleadosAfectados;
    }

    public void setEmpleadosAfectados(ArrayList<FilasAfectadasJsonVO> empleadosAfectados) {
        this.empleadosAfectados = empleadosAfectados;
    }
    
    public FilasAfectadasJsonVO getFilasAfectadasObj() {
        return filasAfectadasObj;
    }

    public void setFilasAfectadasObj(FilasAfectadasJsonVO filasAfectadasObj) {
        this.filasAfectadasObj = filasAfectadasObj;
    }
    
    public int getFilasAfectadas() {
        return filasAfectadas;
    }

    public void setFilasAfectadas(int filasAfectadas) {
        this.filasAfectadas = filasAfectadas;
    }
    
    public String getMsgFromSp() {
        return msgFromSp;
    }

    public void setMsgFromSp(String msgFromSp) {
        this.msgFromSp = msgFromSp;
    }
    
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    public int getCodError() {
        return codError;
    }

    public void setCodError(int codError) {
        this.codError = codError;
    }

    public String getMsgError() {
        return msgError;
    }

    public void setMsgError(String msgError) {
        this.msgError = msgError;
    }

    public boolean isThereError() {
        return thereError;
    }

    public void setThereError(boolean thereError) {
        this.thereError = thereError;
    }
    
    
}
