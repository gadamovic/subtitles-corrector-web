package com.subtitlescorrector.adapters.in;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.subtitlescorrector.core.domain.UserData;
import com.subtitlescorrector.core.port.ExternalCacheServicePort;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class UserDataAdvice {

	@Autowired
	UserData user;
	
	@Autowired
	ExternalCacheServicePort cache;
	
    @ModelAttribute
    public void populateUserData(HttpServletRequest request) {
    	
    	String userId = request.getParameter("webSocketUserId");
		
    	if(StringUtils.isNotBlank(userId)) {
    		String webSocketSessionId = cache.getWebSocketSessionIdForUser(userId);
    		user.setUserId(userId);
    		user.setWebSocketSessionId(webSocketSessionId);    		
    	}
    }
	
}
