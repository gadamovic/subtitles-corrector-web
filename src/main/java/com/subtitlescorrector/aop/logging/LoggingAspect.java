package com.subtitlescorrector.aop.logging;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Aspect
@Component
public class LoggingAspect {

	Logger log = LoggerFactory.getLogger(LoggingAspect.class);
	
	@Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
	public void loggingPointcut() {
		
	}
	
	@Around("loggingPointcut()")
	public Object log(ProceedingJoinPoint joinPoint) throws Throwable{
		
		Map<String, String> mdcParameters = new HashMap<>();
		if(joinPoint.getArgs().length > 0 && joinPoint.getArgs()[0] instanceof MultipartFile) {
			mdcParameters.put("uploaded_file_name", ((MultipartFile) joinPoint.getArgs()[0]).getOriginalFilename());
		}
		
		long start = System.currentTimeMillis();
		Object returnObject = joinPoint.proceed();
		long elapsedTimeMs = System.currentTimeMillis() - start;
		
		mdcParameters.put("execution_time", String.valueOf(elapsedTimeMs));
		
		ServletRequestAttributes attrs = 
	            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		
		String url = attrs.getRequest().getRequestURI().toString();
		mdcParameters.put("uri", url);
		
		addToMDC(mdcParameters);
		log.info("Logging aspect: " + mdcParameters.toString());
		removeFromMDC(mdcParameters);
		return returnObject;
	}

	private void removeFromMDC(Map<String, String> mdcParameters) {
		for(String key : mdcParameters.keySet()) {
			MDC.remove(key);
		}
	}

	private void addToMDC(Map<String, String> mdcParameters) {
		for(Map.Entry<String, String> entry : mdcParameters.entrySet()) {
			MDC.put(entry.getKey(), entry.getValue());
		}
	}
	
}
