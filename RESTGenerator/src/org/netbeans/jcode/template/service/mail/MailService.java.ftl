<#if package??>package ${package};</#if>

import ${MailConfig_FQN};
import ${User_FQN};

import java.io.StringWriter;
import java.util.Locale;
import java.util.function.Function;
import org.slf4j.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.apache.deltaspike.core.api.message.Message;
import org.apache.deltaspike.core.api.message.MessageContext;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

/**
 * Service for sending e-mails.
 * <p>
 * @Asynchronous annotation is used to send e-mails asynchronously.
 * </p>
 */
@Stateless
public class MailService {

    private static final String USER = "user";
    private static final String BASE_URL = "baseUrl";
    
    @Inject
    private Logger log;

    @Inject
    private VelocityEngine engine;
    
    @Inject
    private MessageContext messageContext;
    
    @Inject
    private MailConfig mailConfig;

    @Asynchronous
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        try {
            log.debug("Send e-mail[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}", isMultipart, isHtml, to, subject, content);
            // Prepare message using a apache commons-email
            HtmlEmail email = new HtmlEmail();
            email.setHostName(mailConfig.getHost());
            email.setStartTLSEnabled(true);
            email.setSmtpPort(mailConfig.getPort());
            email.setAuthenticator(new DefaultAuthenticator(mailConfig.getUsername(), mailConfig.getPassword()));
            email.setFrom(mailConfig.getFrom());
            email.setSubject(subject);
            email.setHtmlMsg(content);
            email.addTo(to);
            email.send();
            log.debug("Sent e-mail to User '{}'", to);
        } catch (Exception e) {
            log.warn("E-mail could not be sent to user '{}', exception is: {}", to, e.getMessage());
        }
    }

  @Asynchronous
    public void sendActivationEmail(User user, String baseUrl) {
        log.debug("Sending activation e-mail to '{}'", user.getEmail());
        Message message = getMessage(user);
        String subject = message.template("{email.activation.title}").toString();
        String content = getContent("activationEmail", user, baseUrl, message);
        sendEmail(user.getEmail(), subject, content, false, true);
    }

    @Asynchronous
    public void sendCreationEmail(User user, String baseUrl) {
        log.debug("Sending creation e-mail to '{}'", user.getEmail());
        Message message = getMessage(user);
        String subject = message.template("{email.creation.title}").toString();
        String content = getContent("creationEmail", user, baseUrl, message);
        sendEmail(user.getEmail(), subject, content, false, true);
    }
    
    @Asynchronous
    public void sendPasswordResetMail(User user, String baseUrl) {
        log.debug("Sending password reset e-mail to '{}'", user.getEmail());
        Message message = getMessage(user);
        String subject = message.template("{email.reset.title}").toString();
        String content = getContent("passwordResetEmail", user, baseUrl, message);
        sendEmail(user.getEmail(), subject, content, false, true);
    }

    private String getContent(String template, User user, String baseUrl, Message message) {
        Template t = engine.getTemplate(String.format("mails/%s.html", template));
        VelocityContext context = new VelocityContext();
        context.put(USER, user);
        context.put(BASE_URL, baseUrl);
        context.put("props", (Function<String, Message>)message::template);
        StringWriter writer = new StringWriter();
        t.merge(context, writer);
        return writer.toString();
    }
    
    private Message getMessage(User user){
        return messageContext.messageSource("i18n.messages")
                .localeResolver(() -> Locale.forLanguageTag(user.getLangKey()))
                .message();
    }

}