/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import javax.mail.Message;
import javax.mail.util.ByteArrayDataSource;
import org.simplejavamail.email.AttachmentResource;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.email.Recipient;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;

/**
 *
 * @author Alexander
 */
public class MailSender {
    
    private static GetPropertyValues m_properties = new GetPropertyValues();
    
    /**
     * Metodo que permite enviar un correo con archivo adjunto,
     * El correo se envia con autenticacion
     * 
     * @param _filePathAttach
     * @param _fromLabel
     * @param _fromEmail
     * @param _strDestinatarios
     * @param _asunto
     * @param _cuerpo
     * 
     * @return 
     */
    public static boolean sendWithAttachment(String _filePathAttach, 
            String _fromLabel,
            String _fromEmail,
            String _strDestinatarios,
            String _asunto,
            String _cuerpo){
        boolean isOk=true;
        try {
            // TODO code application logic here
            Collection<Recipient> destinatarios = new ArrayList<>();
            StringTokenizer tokenEmails=new StringTokenizer(_strDestinatarios, ",");
            while (tokenEmails.hasMoreTokens()){
                Recipient aux=new Recipient("", tokenEmails.nextToken(), Message.RecipientType.TO);
                destinatarios.add(aux);
            }
            Email email=null;
            if (_filePathAttach!=null){
                File initialFile = new File(_filePathAttach);
                InputStream targetStream = new FileInputStream(initialFile);
                email = EmailBuilder.startingBlank()
                    .from(_fromLabel, _fromEmail)
                    .withRecipients(destinatarios)
                    .withSubject(_asunto)
                    .withHTMLText(_cuerpo)
                    .withAttachment(initialFile.getName(), 
                        new ByteArrayDataSource(targetStream, "text/plain"))
                    .buildEmail();
            }else{
                email = EmailBuilder.startingBlank()
                    .from(_fromLabel, _fromEmail)
                    .withRecipients(destinatarios)
                    .withSubject(_asunto)
                    .withHTMLText(_cuerpo)
                    .buildEmail();
            }
            
            int intMailPort = Integer.parseInt(m_properties.getKeyValue("mailPort"));
            if (email != null){
                MailerBuilder
                    .withSMTPServer(m_properties.getKeyValue("mailHost"), 
                            intMailPort, 
                            m_properties.getKeyValue("mailUsuario"), 
                            m_properties.getKeyValue("mailPassword"))
                    .withTransportStrategy(TransportStrategy.SMTP_TLS)
                    .buildMailer()
                    .sendMail(email);
            }
        } catch (IOException | NumberFormatException ex) {
            System.err.println("[MailSender.sendWithAttachment]Error: " + ex.toString());
            isOk = false;
        }
    
        return isOk;
    }
    
    /**
     * Metodo que permite enviar un correo con varios archivos adjuntos,
     * El correo se envia con autenticacion
     * 
     * @param _attachList
     * @param _fromLabel
     * @param _fromEmail
     * @param _strDestinatarios
     * @param _asunto
     * @param _cuerpo
     * 
     * @return 
     */
    public static boolean send(List<AttachmentResource> _attachList, 
            String _fromLabel,
            String _fromEmail,
            String _strDestinatarios,
            String _asunto,
            String _cuerpo){
        boolean isok=true;
        try {
            // TODO code application logic here
            Collection<Recipient> destinatarios = new ArrayList<>();
            
            StringTokenizer tokenEmails=new StringTokenizer(_strDestinatarios, ",");
            while (tokenEmails.hasMoreTokens()){
                Recipient aux=new Recipient("", tokenEmails.nextToken(), Message.RecipientType.TO);
                destinatarios.add(aux);
            }
            Email email= EmailBuilder.startingBlank()
                    .from(_fromLabel, _fromEmail)
                    .withRecipients(destinatarios)
                    .withSubject(_asunto)
                    .withHTMLText(_cuerpo)
                    .withAttachments(_attachList)
                    .buildEmail();
            
            int intMailPort = Integer.parseInt(m_properties.getKeyValue("mailPort"));
            if (email != null){
                MailerBuilder
                    .withSMTPServer(m_properties.getKeyValue("mailHost"), 
                            intMailPort, 
                            m_properties.getKeyValue("mailUsuario"), m_properties.getKeyValue("mailPassword"))
                    .withTransportStrategy(TransportStrategy.SMTP_TLS)
                    .buildMailer()
                    .sendMail(email);
            }
        } catch (NumberFormatException ex) {
            System.err.println("[MailSender.send]Error: " + ex.toString());
            isok = false;
        }
    
        return false;
    }
    
    
    
}
