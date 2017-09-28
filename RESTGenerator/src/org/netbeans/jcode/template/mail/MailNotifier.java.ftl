<#if package??>package ${package};</#if>

import ${MailConfig_FQN};
import ${User_FQN};
import java.io.StringWriter;
import java.util.Locale;
import java.util.function.Function;
import javax.enterprise.context.ApplicationScoped;
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

@ApplicationScoped
public class MailNotifier {

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

    public void sendEmail(@ObservesAsync MailEvent event) {
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

}
