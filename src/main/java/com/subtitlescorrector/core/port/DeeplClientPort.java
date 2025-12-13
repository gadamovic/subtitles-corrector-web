package com.subtitlescorrector.core.port;

import java.util.List;

public interface DeeplClientPort {
	public List<String> translate(List<String> lines);
}
