package com.subtitlescorrector.domain;

import com.subtitlescorrector.domain.EditOperation.OperationType;

/**
 * Edit operation consisting of multiple 'regular' edit operations. Composite edit operations work with strings as opposed to
 * 'regular' edit operations which work with characters
 * @author GavriloPC
 *
 */
public class CompositeEditOperation {

	private OperationType type;
	private String str1 = "";
	private String str2 = "";
	public OperationType getType() {
		return type;
	}
	public void setType(OperationType type) {
		this.type = type;
	}
	public String getStr1() {
		return str1;
	}
	public void setStr1(String str1) {
		this.str1 = str1;
	}
	public String getStr2() {
		return str2;
	}
	public void setStr2(String str2) {
		this.str2 = str2;
	}

	public void appendToString1(Character c) {
		str1 += c;
	}
	
	public void appendToString2(Character c) {
		str2 += c;
	}
	
	@Override
	public String toString() {
		return "CompositeEditOperation [type=" + type + ", str1=" + str1 + ", str2=" + str2 + "]";
	}
	
}
