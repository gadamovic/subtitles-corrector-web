package com.subtitlescorrector.core.port;

import com.subtitlescorrector.core.domain.EmailSendStatus;

public interface EmailServicePort {

	EmailSendStatus sendEmail(String text, String to, String subject);

	EmailSendStatus sendEmailOnlyIfProduction(String text, String to, String subject);

}