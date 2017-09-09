<#if package??>package ${package};</#if>

import ${MailConfig_FQN};
import ${User_FQN};
import java.io.StringWriter;
import java.util.Locale;
import java.util.function.Function;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.apache.deltaspike.core.api.message.Message;
import org.apache.deltaspike.core.api.message.MessageContext;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;

/**
 * Service for sending e-mails.
 * <p>
 * CDI events is used to send e-mails asynchronously.
 * </p>
 */
@ApplicationScoped
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

    @Inject
    private Event<MailEvent> eventProducer;

    public void sendActivationEmail(User user) {
        log.debug("Sending activation e-mail to '{}'", user.getEmail());
        eventProducer.fireAsync(new MailEvent(user, "email.activation.title", "activationEmail"));
    }

    public void sendCreationEmail(User user) {
        log.debug("Sending creation e-mail to '{}'", user.getEmail());
        eventProducer.fireAsync(new MailEvent(user, "email.creation.title", "creationEmail"));
    }

    public void sendPasswordResetMail(User user) {
        log.debug("Sending password reset e-mail to '{}'", user.getEmail());
        eventProducer.fireAsync(new MailEvent(user, "email.reset.title", "passwordResetEmail"));
    }

    void sendEmail(@ObservesAsync MailEvent event) {
        if (!mailConfig.isEnable()) {
             log.debug("Mail servce is not enabled");         
        }
        String to = event.getUser().getEmail();
        Message message = getMessage(event.getUser());
        String subject = message.template(String.format("{%s}", event.getSubjectTemplate())).toString();
        String content = getContent(event, message);
        try {
            log.debug("Send e-mail to '{}' with subject '{}' and content={}", to, subject, content);
            // Prepare message
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
            log.warn("e-mail could not be sent to user '{}', exception is: {}", to, e.getMessage());
        }
    }

    private String getContent(MailEvent mailEvent, Message message) {
        Template t = engine.getTemplate(String.format("mails/%s.html", mailEvent.getContentTemplate()));
        VelocityContext context = new VelocityContext();
        context.put(USER, mailEvent.getUser());
        context.put(BASE_URL, mailConfig.getBaseUrl());
        context.put("props", (Function<String, Message>) message::template);
        StringWriter writer = new StringWriter();
        t.merge(context, writer);
        return writer.toString();
    }

    private Message getMessage(User user) {
        return messageContext.messageSource("i18n.messages")
                .localeResolver(() -> Locale.forLanguageTag(user.getLangKey()))
                .message();
    }

    class MailEvent {

        private final User user;
        private final String subjectTemplate;
        private final String contentTemplate;

        public MailEvent(User user, String subjectTemplate, String contentTemplate) {
            this.user = user;
            this.subjectTemplate = subjectTemplate;
            this.contentTemplate = contentTemplate;
        }

        public User getUser() {
            return user;
        }

        public String getSubjectTemplate() {
            return subjectTemplate;
        }

        public String getContentTemplate() {
            return contentTemplate;
        }

    }
}
