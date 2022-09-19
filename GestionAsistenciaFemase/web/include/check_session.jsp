
<%@page import="java.util.Date"%>
<%  
    response.setHeader("Cache-Control", "no-cache");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
    response.setHeader("Cache-Control","no-store");
    
    final long now = System.currentTimeMillis();
    Date ahora=new Date();
    //Date lastAccess = new Date(now - session.getLastAccessedTime());
    //Date age = new Date(now - session.getCreationTime());
    Date expirationDate = new Date(session.getLastAccessedTime() + session.getMaxInactiveInterval()*1000);
    System.out.println("[GestionFemaseWeb]check_session.jsp]fecha de expiracion de la sesion: " + expirationDate);
    
    if (request.getRequestedSessionId() != null
        && !request.isRequestedSessionIdValid()) {
            // Session is expired
            System.out.println("----------------------- "
                + "[check_session.jsp]Now: " + ahora +", la Sesion ha expirado"
                + "---------------------------");
            session.setAttribute("mensaje","Su sesion ha expirado. Debe reingresar al Sistema.");
            String redirectURL = request.getContextPath() + "/mensaje_sesion_expirada.jsp";
            response.sendRedirect(redirectURL);
            return;
    }
%>

 