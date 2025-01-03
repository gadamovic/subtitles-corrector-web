package com.subtitlescorrector.service;

import com.subtitlescorrector.domain.EmailSendStatus;

public interface EmailService {

	EmailSendStatus sendEmail(String text, String to, String subject);

	EmailSendStatus sendEmailOnlyIfProduction(String text, String to, String subject);

}