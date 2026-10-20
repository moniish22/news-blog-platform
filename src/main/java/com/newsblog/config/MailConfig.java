package com.newsblog.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/** Email / newsletter settings read from application.properties. JavaMailSender itself is auto-configured by Spring Boot. */
@Configuration
@Getter
public class MailConfig {
    @Value("${app.newsblog.newsletter.from-email:noreply@newsblog.com}")
    private String fromEmail;

    /** Public URL of the site, used to build links inside emails. */
    @Value("${app.newsblog.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${app.newsblog.newsletter.verification-required:true}")
    private boolean verificationRequired;

    @Value("${app.newsblog.newsletter.notify-on-publish:true}")
    private boolean notifyOnPublish;

    @Value("${app.newsblog.comments.auto-approve-readers:false}")
    private boolean autoApproveReaders;
}
