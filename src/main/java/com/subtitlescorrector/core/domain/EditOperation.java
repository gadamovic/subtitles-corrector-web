package com.subtitlescorrector.core.domain;

/**
 * Replacement operation uses char1 as source and char2 as target, other
 * operations use only char1
 * 
 * @author Gavrilo Adamovic
 *
 */
public class EditOperation {

	private OperationType type;
	private Character char1;
	private Character char2;

	public OperationType getType() {
		return type;
	}

	public void setType(OperationType type) {
		this.type = type;
	}

	public Character getChar1() {
		return char1;
	}

	public void setChar1(Character char1) {
		this.char1 = char1;
	}

	public Character getChar2() {
		return char2;
	}

	public void setChar2(Character char2) {
		this.char2 = char2;
	}

	public enum OperationType {
		REPLACE, KEEP, ADD, DELETE
	}

	@Override
	public String toString() {
		return "EditOperation [type=" + type.name() + ", char1=" + char1 + ", char2=" + char2 + "]";
	}

}
