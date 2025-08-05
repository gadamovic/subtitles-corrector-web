package com.subtitlescorrector.core.service;

import java.util.List;

import com.subtitlescorrector.core.domain.EditOperation;

public interface EditDistanceService {

	/**
	 * Returns all edit operations including 'keep' (no change) operation resulting in the entire target string
	 * @param source
	 * @param target
	 * @return
	 */
	List<EditOperation> getEditOperations(String source, String target);

}