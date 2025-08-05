package com.subtitlescorrector.adapters.in;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.subtitlescorrector.adapters.out.configuration.ApplicationProperties;
import com.subtitlescorrector.core.domain.EmailSendStatus;
import com.subtitlescorrector.core.port.EmailServicePort;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/api/rest/1.0")
public class ContactFormController {

	@Autowired
	EmailServicePort emailService;
	
	@Autowired
	ApplicationProperties properties;
	
	@RequestMapping(path = "/submitContactForm", method = RequestMethod.POST)
	public ResponseEntity<EmailSendStatus> contactFormSubmit(HttpServletRequest request) {
		String email = request.getParameter("email");
		String description = request.getParameter("description");
		description += "\n\nProvided email: " + email;
		
		EmailSendStatus status = emailService.sendEmailOnlyIfProduction(description, properties.getAdminEmailAddress(), "Contact form");
		
		return createResponseFromStatus(status);
	}
	
	@RequestMapping(path = "/submitFeedback", method = RequestMethod.POST)
	public ResponseEntity<EmailSendStatus> feedbackFormSubmit(HttpServletRequest request) {

		String description = "Feedback content: \n\n" + request.getParameter("description");
		
		EmailSendStatus status = emailService.sendEmailOnlyIfProduction(description, properties.getAdminEmailAddress(), "Contact form");
		
		return createResponseFromStatus(status);
	}

	private ResponseEntity<EmailSendStatus> createResponseFromStatus(EmailSendStatus status) {
		if(status == EmailSendStatus.SUCCESS) {
			return ResponseEntity.ok(status);			
		}else if(status == EmailSendStatus.FAILURE_EMAIL_SEND_RATE_LIMIT){
			return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS.value()).body(status);
		}else if(status == EmailSendStatus.DEVELOPMENT_NOT_SENT){
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE.value()).body(status);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(status);
		}
	}
	
}
