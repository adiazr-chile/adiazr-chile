/**
 * UtilCorreo.java 07-03-2017
 *
 * 
 */

package cl.femase.gestionweb.common;

import com.sun.mail.smtp.SMTPTransport;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class UtilCorreo {

    private Properties m_props;
    private String m_protocol;
    private boolean m_useAuth;
    private Session m_session;
    private String nomUsuario   = "";
    private String claveUsuario = "";
    private String smtpHost = "";
    private String smtpPort = "25";
    private String mailFrom = "";
    private String mailTo = "";
    private String mailSubject = "";
    private String mailBody = "";
    private String codigoError = "";
    private String mensajeError = "";
    private boolean nivelDebug = false;
    private MimeBodyPart attachment = null;
    private String fileName = "";
    private String[] destinatarios = null;
    private String[] destinatariosCC = null;
    private String[] destinatariosNombres = null;
    public boolean messageSent = false;
    /**
     * Creates a new UtilCorreo object.
     */
    public UtilCorreo() {

        /*
         * Initialize the JavaMail Session.
         */
        m_useAuth   = true;
        m_protocol  = "smtp";
        m_props = System.getProperties();

        m_props.put("mail." + m_protocol + ".auth", "false");
        m_props.put("mail." + m_protocol + ".starttls.enable", "false");

        // Get a Session object
        m_session = Session.getInstance(m_props, null);
        m_session.setDebug(this.nivelDebug);
    }

    public MimeBodyPart getAttachment() {
        return attachment;
    }

    /**
     * DOCUMENT ME!
     *
     * @param from DOCUMENT ME!
     */
    public void setFrom(String from) {
        mailFrom = from;
    }

    /**
     * Recibe la ruta completa del archivo a atachar en el correo
     * */
    public void setFileAttachment(String _rutaArchivoAtachado)
    throws Exception, MessagingException {
    	attachment = new MimeBodyPart();
    	FileDataSource fds = new FileDataSource(_rutaArchivoAtachado);
    	attachment.setDataHandler(new DataHandler(fds));
    	attachment.setFileName(fds.getName());
    }

    /**
     * DOCUMENT ME!
     *
     * @param to DOCUMENT ME!
     */
    public void setTo(String to) {
        mailTo = to;
    }

    /**
     * DOCUMENT ME!
     *
     * @param subject DOCUMENT ME!
     */
    public void setSubject(String subject) {
        mailSubject = subject;
    }

    /**
     * DOCUMENT ME!
     *
     * @param body DOCUMENT ME!
     */
    public void setBody(String body) {
        mailBody = body;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getCodigoError() {
        return codigoError;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getMensajeError() {
        return mensajeError;
    }

    /**
     * DOCUMENT ME!
     *
     * @param _nivel DOCUMENT ME!
     */
    public void setNivelDebugSesion(boolean _nivel) {
        this.nivelDebug = _nivel;
    }

    /**
     * DOCUMENT ME!
     */
    public void setContentType() {
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     * @throws MessagingException DOCUMENT ME!
     */
    public void setEnviarMensaje() throws Exception, MessagingException {
        try {

//            if (smtpHost != null)
//                m_props.put("mail." + m_protocol + ".host", smtpHost);

//            if (smtpPort != null)
//                m_props.put("mail." + m_protocol + ".port", smtpPort);
//
//            if (m_useAuth){
//                m_props.put("mail." + m_protocol + ".auth", "true");
//                m_props.put("mail." + m_protocol + ".starttls.enable", "true");
//                //m_props.put("mail.smtp.starttls.enable", "true");
//            }

//            m_props.put("mail.smtp.user",senderEmailID);
            m_props.put("mail.smtp.host", smtpHost);
            m_props.put("mail.smtp.port", smtpPort);
            m_props.put("mail.smtp.starttls.enable", "true");
            m_props.put("mail.smtp.auth", "true");
            m_props.put("mail.smtp.socketFactory.port", smtpPort);
            m_props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
            m_props.put("mail.smtp.socketFactory.fallback", "false");
            
            
            MimeMessage msg = new MimeMessage(m_session);
            msg.setFrom(new InternetAddress(mailFrom));

            if (this.getDestinatarios() == null) {
                InternetAddress[] address = { new InternetAddress(mailTo) };
                msg.setRecipients(Message.RecipientType.TO, address);
            } else {
                InternetAddress[] addressesTo = new InternetAddress[this.getDestinatarios().length];

                for (int i = 0; i < this.getDestinatarios().length; i++) {
                    addressesTo[i] =
                        new InternetAddress(this.destinatarios[i].toString());
                    //,this.destinatariosNombres[i].toString());
                }

                msg.setRecipients(Message.RecipientType.TO, addressesTo);
            }

            if (this.getDestinatariosCC() != null) {
                InternetAddress[] addressesCC = new InternetAddress[this.getDestinatariosCC().length];
                for (int j = 0; j < this.getDestinatariosCC().length; j++) {
                    addressesCC[j] = new InternetAddress(this.destinatariosCC[j].toString());
                }
                msg.setRecipients(Message.RecipientType.CC, addressesCC);
            }

            //msg.setContent(mailBody, "text/html");
            msg.setSubject(mailSubject);
            msg.setSentDate(Calendar.getInstance().getTime());

            MimeBodyPart mbp1 = new MimeBodyPart();
            mbp1.setText(mailBody);

            Multipart mp = new MimeMultipart();
            mp.addBodyPart(mbp1);

            msg.setContent(mailBody, "text/html;charset=iso-8859-1");

            //msg.setContent(mp,"text/html");
            // deprecated Transport.send(msg);
            SMTPTransport t =
		(SMTPTransport)m_session.getTransport(m_protocol);
	    try {
                if (m_useAuth)
                    t.connect(smtpHost, nomUsuario, claveUsuario);
                else
                    t.connect();
                
                t.sendMessage(msg, msg.getAllRecipients());
	    } finally {
                //if (verbose)
                //System.out.println("Response: " + t.getLastServerResponse());
                t.close();
	    }

        } catch (MessagingException e1) {
            codigoError = "111";
            mensajeError = e1.getMessage();
            System.err.println("UtilCorreo.class: Error: " + e1.toString());
            throw e1;
        } catch (Exception e) {
            codigoError = "100";
            mensajeError = e.getMessage();
            System.err.println("UtilCorreo.class: Error 2: " + e.toString());
            throw e;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param contenido DOCUMENT ME!
     * @param nombre DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     * @throws MessagingException DOCUMENT ME!
     */
    public void setAttachment(String contenido, String nombre)
        throws Exception, MessagingException {
        attachment = new MimeBodyPart();

        try {
            attachment.setFileName(nombre);
            attachment.setText(contenido);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     * @throws MessagingException DOCUMENT ME!
     */
    public void setEnviarMensajeConAttachment() throws Exception, MessagingException {
        try {
            if (smtpHost != null)
                m_props.put("mail." + m_protocol + ".host", smtpHost);

            if (smtpPort != null)
                m_props.put("mail." + m_protocol + ".port", smtpPort);

            if (m_useAuth){
                m_props.put("mail." + m_protocol + ".auth", "true");
                m_props.put("mail." + m_protocol + ".starttls.enable", "true");
            }

            MimeMessage msg = new MimeMessage(m_session);
            
            msg.setFrom(new InternetAddress(mailFrom));

            if (this.getDestinatarios() == null) {
                InternetAddress[] address = { new InternetAddress(mailTo) };
                msg.setRecipients(Message.RecipientType.TO, address);
            } else {
                System.out.println("num destinatarios= " + this.getDestinatarios().length);
                InternetAddress[] addressesTo = new InternetAddress[this.getDestinatarios().length];

                for (int i = 0; i < this.getDestinatarios().length; i++) {
                    System.out.println("Email destinatario= " + this.destinatarios[i]);
                    //addressesTo[i] =
                    //        new InternetAddress(this.destinatarios[i].toString(),
                    //            this.destinatariosNombres[i].toString());
                    addressesTo[i] =
                    new InternetAddress(this.destinatarios[i].toString());
                }

                msg.setRecipients(Message.RecipientType.TO, addressesTo);
            }

            if (this.getDestinatariosCC() != null) {
                InternetAddress[] addressesCC = new InternetAddress[this.getDestinatariosCC().length];
                for (int j = 0; j < this.getDestinatariosCC().length; j++) {
                    addressesCC[j] = new InternetAddress(this.destinatariosCC[j].toString());
                }
                msg.setRecipients(Message.RecipientType.CC, addressesCC);
            }

            //msg.setContent(mailBody, "text/html");
            msg.setSubject(mailSubject);
            msg.setSentDate(Calendar.getInstance().getTime());

            MimeBodyPart mbp1 = new MimeBodyPart();

            mbp1.setText(mailBody);

            Multipart mp = new MimeMultipart();
            mp.addBodyPart(mbp1);
            mp.addBodyPart(this.attachment);

            msg.setContent(mp);

            SMTPTransport t =
                    (SMTPTransport) m_session.getTransport(m_protocol);
            try {
                if (m_useAuth) {
                    System.out.println("[setEnviarMensajeConAttachmentFile]autenticar!");
                    t.connect(smtpHost, nomUsuario, claveUsuario);
                } else {
                    t.connect();
                    System.out.println("[setEnviarMensajeConAttachmentFile]NO autenticar!");
                }
                t.sendMessage(msg, msg.getAllRecipients());
                this.messageSent = true;
            } finally {
                //if (verbose)
                System.out.println("Response: " + t.getLastServerResponse());
                t.close();
            }
        } catch (MessagingException e1) {
            codigoError = "111";
            mensajeError = e1.getMessage();
            System.err.println("UtilCorreo.setEnviarMensajeConAttachment: Error: " + e1.toString());
            throw e1;
        } catch (Exception e) {
            codigoError = "100";
            mensajeError = e.getMessage();
            System.err.println("UtilCorreo.setEnviarMensajeConAttachment: Error 2: " + e.toString());
            throw e;
        }
    }

    public boolean setEnviarMensajeConAttachmentFile(List lstNombresArchivo, String Path)
        throws Exception, MessagingException {
        boolean exito=true;
        try {
            if (smtpHost != null)
                m_props.put("mail." + m_protocol + ".host", smtpHost);

            if (smtpPort != null)
                m_props.put("mail." + m_protocol + ".port", smtpPort);

            if (m_useAuth){
                m_props.put("mail." + m_protocol + ".auth", "true");
                m_props.put("mail." + m_protocol + ".starttls.enable", "true");
            }

            MimeMessage msg = new MimeMessage(m_session);
            
            msg.setFrom(new InternetAddress(mailFrom));

            if (this.getDestinatarios() == null) {
                InternetAddress[] address = { new InternetAddress(mailTo) };
                msg.setRecipients(Message.RecipientType.TO, address);
            } else {
                InternetAddress[] addressesTo = new InternetAddress[this.getDestinatarios().length];

                for (int i = 0; i < this.getDestinatarios().length; i++) {
                    addressesTo[i] = new InternetAddress(this.destinatarios[i].toString());
                }

                msg.setRecipients(Message.RecipientType.TO, addressesTo);
            }

            //msg.setContent(mailBody, "text/html");
            msg.setSubject(mailSubject);
            msg.setSentDate(Calendar.getInstance().getTime());

            MimeBodyPart mbp1 = new MimeBodyPart();
            mbp1.setText(mailBody);

            Multipart mp = new MimeMultipart();
            mp.addBodyPart(mbp1);

            for (int i = 0; i < lstNombresArchivo.size(); i++) {
                mbp1 = new MimeBodyPart();

                DataSource source = new FileDataSource(Path + (String) lstNombresArchivo.get(i));
                mbp1.setDataHandler(new DataHandler(source));
                mbp1.setFileName(source.getName());
                mp.addBodyPart(mbp1);
            }

            //mp.addBodyPart(this.attachment);
            msg.setContent(mp);

            SMTPTransport t =
                    (SMTPTransport) m_session.getTransport(m_protocol);
            try {
                if (m_useAuth) {
                    System.out.println("[setEnviarMensajeConAttachmentFile]autenticar!");
                    t.connect(smtpHost, nomUsuario, claveUsuario);
                } else {
                    t.connect();
                    System.out.println("[setEnviarMensajeConAttachmentFile]NO autenticar!");
                }
                t.sendMessage(msg, msg.getAllRecipients());
            } finally {
                //if (verbose)
                System.out.println("Response: " + t.getLastServerResponse());
                t.close();
            }
            
        } catch (MessagingException e1) {
            codigoError = "111";
            mensajeError = e1.getMessage();
            System.err.println("UtilCorreo.setEnviarMensajeConAttachment: Error: " + e1.toString());
            exito=false;
            throw e1;
        } catch (Exception e) {
            codigoError = "100";
            mensajeError = e.getMessage();
            System.err.println("UtilCorreo.setEnviarMensajeConAttachment: Error 2: " + e.toString());
            exito=false;
            throw e;
        }

        return exito;
    }

    /**
     * @return Returns the fileName.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName The fileName to set.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return Returns the destinatarios.
     */
    public String[] getDestinatarios() {
        return destinatarios;
    }

    /**
     * @param destinatarios The destinatarios to set.
     */
    public void setDestinatarios(String[] destinatarios) {
        this.destinatarios = destinatarios;
    }

    public boolean useAuth() {
        return m_useAuth;
    }

    public void setUseAuth(boolean _useAuth) {
        this.m_useAuth = _useAuth;
    }

    public String getClaveUsuario() {
        return claveUsuario;
    }

    public void setClaveUsuario(String claveUsuario) {
        this.claveUsuario = claveUsuario;
    }

    public String getM_protocol() {
        return m_protocol;
    }

    public void setM_protocol(String m_protocol) {
        this.m_protocol = m_protocol;
    }

    public String getNomUsuario() {
        return nomUsuario;
    }

    public void setNomUsuario(String nomUsuario) {
        this.nomUsuario = nomUsuario;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public String getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(String smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String[] getDestinatariosCC() {
        return destinatariosCC;
    }

    public void setDestinatariosCC(String[] _destinatariosCC) {
        this.destinatariosCC = _destinatariosCC;
    }

    public String[] getDestinatariosNombres() {
        return destinatariosNombres;
    }

    public void setDestinatariosNombres(String[] _destinatariosNombres) {
        this.destinatariosNombres = _destinatariosNombres;
    }


}
