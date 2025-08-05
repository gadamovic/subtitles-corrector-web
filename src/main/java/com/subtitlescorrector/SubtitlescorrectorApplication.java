package com.subtitlescorrector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.subtitlescorrector.adapters.in.SubtitleCorrectionEventConsumerImpl;

@SpringBootApplication
public class SubtitlescorrectorApplication {

	public static Logger log = LoggerFactory.getLogger(SubtitleCorrectionEventConsumerImpl.class);

	public static void main(String[] args) {

		SpringApplication.run(SubtitlescorrectorApplication.class, args);

	}

}
