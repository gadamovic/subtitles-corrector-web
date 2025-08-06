package com.subtitlescorrector.adapters.out;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.core.domain.UserSubtitleConversionData;
import com.subtitlescorrector.core.domain.UserSubtitleData;
import com.subtitlescorrector.core.port.HttpServletResponseFileWriterPort;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class HttpServletResponseFileWriterAdapter implements HttpServletResponseFileWriterPort {

	Logger log = LoggerFactory.getLogger(HttpServletResponseFileWriterAdapter.class);
	
	@Override
	public void writeFileToResponse(UserSubtitleData data, HttpServletResponse response) {
       writeResponse(data.getFile(), data.getSubtitleFileData().getFilename(), response);
	}
	
	@Override
	public void writeFileToResponse(UserSubtitleConversionData data, HttpServletResponse response) {
		writeResponse(data.getFile(), data.getData().getFilename(), response);
	}
	
	private void writeResponse(File file, String filename, HttpServletResponse response) {
		 response.setContentType("application/octet-stream");
	        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
	        response.setContentLengthLong(file.length());
			
	        try (FileInputStream fis = new FileInputStream(file)) {
	            byte[] buffer = new byte[1024];
	            int bytesRead;
	            while ((bytesRead = fis.read(buffer)) != -1) {
	                response.getOutputStream().write(buffer, 0, bytesRead);
	            }
	        } catch (IOException e) {
	            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	            return;
	        } finally {
	            // Delete the file after response is sent
	            if (!file.delete()) {
	                log.error("Failed to delete the file: " + file.getAbsolutePath());
	            }
	        }
		
	}

}
