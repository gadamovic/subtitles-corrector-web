package com.subtitlescorrector.core.service;

import java.io.File;

import com.subtitlescorrector.core.domain.AdditionalData;
import com.subtitlescorrector.core.domain.exception.InvalidBusinessOperationException;

import jakarta.servlet.http.HttpServletRequest;

public interface UploadFileEntryPoint {
	String handleFileUpload(AdditionalData params, File storedFile, HttpServletRequest request) throws InvalidBusinessOperationException;
}