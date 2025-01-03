package com.subtitlescorrector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.applicationproperties.ApplicationProperties;
import com.subtitlescorrector.controller.rest.FileUploadController;
import com.subtitlescorrector.service.redis.RedisService;
import com.subtitlescorrector.util.Constants;

@Service
public class EmailService {

	Logger log = LoggerFactory.getLogger(EmailService.class);
	
	@Autowired
	MailSender mailSender;
	
	@Autowired
	ApplicationProperties properties;
	
	@Autowired
	RedisService redisService;
	
	@Async
	public void sendEmail(String text, String to, String subject) {
		
		if(redisService.getAndIncrementNumberOfEmailsInCurrentHour() <= Constants.EMAILS_SENT_PER_HOUR_LIMIT) {
			
			log.info("Sending email to: " + to + "...");
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(properties.getEmailForSendingEmails());
			message.setTo(to);
			message.setSubject(subject);
			message.setText(text);
			
			mailSender.send(message);
		
		}else {
			log.error("Emails for the current hour exceeded limit of " + Constants.EMAILS_SENT_PER_HOUR_LIMIT + ". Email not sent!");
		}
	}

	public void sendEmailOnlyIfProduction(String text, String to, String subject) {
		
		if(properties.isProdEnvironment()) {
			sendEmail(text, to, subject);
		}
		
	}
	
}
