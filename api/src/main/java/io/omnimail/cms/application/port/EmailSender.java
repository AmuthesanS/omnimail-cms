package io.omnimail.cms.application.port;

import java.util.List;

public interface EmailSender {

    void send(String from, List<String> recipients, String subject, String htmlBody);
}
