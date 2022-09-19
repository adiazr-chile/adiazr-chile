<%@ include file="/include/check_session.jsp" %>
<%@page import="java.io.DataInputStream"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="java.io.OutputStream"%>
<%@page import="java.io.File"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%  
    UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
    String filename = (String)session.getAttribute("downloadFile|" + userConnected.getUsername());
    System.out.println("[GestionFemaseWeb]download_resultados.jsp]full pathfile: " + filename);
    File auxFile = new File(filename);
    int length   = 0;    
    String mimetype = "application/octet-stream";
    response.setContentType(mimetype);
    response.setContentLength((int)auxFile.length());
    ServletOutputStream outStream = response.getOutputStream();
    // sets HTTP header
    response.setHeader("Content-Disposition", "attachment; filename=\"" + "asignacion_turnos.csv" + "\"");

    byte[] byteBuffer = new byte[4096];
    DataInputStream in = new DataInputStream(new FileInputStream(auxFile));

    // reads the file's bytes and writes them to the response stream
    while ((in != null) && ((length = in.read(byteBuffer)) != -1))
    {
        outStream.write(byteBuffer,0,length);
    }

    in.close();
    outStream.close();
    
    /*
    java.io.FileInputStream fileInputStream = new java.io.FileInputStream(filename);
    byte[] buffer = new byte[fileInputStream.available()];
    fileInputStream.read(buffer);
 
    File targetFile = new File("asignacion.csv");
    System.out.println("[GestionFemaseWeb]download_resultados.jsp]target File " + targetFile.getName());
    OutputStream outStream = new FileOutputStream(targetFile);
    outStream.write(buffer);
    
    response.setContentType("APPLICATION/OCTET-STREAM"); 
    response.setHeader("Content-Disposition","attachment; filename=\"" + targetFile.getName() + "\""); 

    int i; 
    while ((i=fileInputStream.read()) != -1) {
      out.write(i); 
    } 
    fileInputStream.close();
    */
    
%> 