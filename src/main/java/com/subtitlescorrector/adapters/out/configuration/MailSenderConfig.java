package com.subtitlescorrector.adapters.out.configuration;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailSenderConfig {
	
	@Autowired
	ApplicationProperties properties;
	
	@Bean(name="mailSender")
    public MailSender javaMailService() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(properties.getMailSenderHost());
        javaMailSender.setPort(properties.getMailSenderPort());
        javaMailSender.setProtocol(properties.getMailSenderProtocol());
        javaMailSender.setUsername(properties.getEmailForSendingEmails());
        javaMailSender.setPassword(properties.getMailSenderPassword());
        Properties mailProperties = new Properties();
        mailProperties.put("mail.smtp.auth", properties.getMailSenderSmtpAuthProperty());
        mailProperties.put("mail.smtp.starttls.enable", properties.getMailSenderSmtpEnableStarttlsProperty());
        mailProperties.put("mail.smtp.debug", properties.getMailSenderSmtpDebugProperty());
        mailProperties.put("mail.smtp.ssl.trust", properties.getMailSenderSmtpSSlTrustProperty());
        javaMailSender.setJavaMailProperties(mailProperties);
        return javaMailSender;
    }
	
}
