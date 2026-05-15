package io.omnimail.cms.infrastructure.email;

import io.omnimail.cms.application.port.EmailSender;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "omnimail.email.provider", havingValue = "logging", matchIfMissing = true)
public class LoggingEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(LoggingEmailSender.class);

    @Override
    public void send(String from, List<String> recipients, String subject, String htmlBody) {
        log.info("EMAIL from={} to={} subject={} bodyLength={}", from, recipients, subject, htmlBody.length());
    }
}
