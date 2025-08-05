package com.subtitlescorrector.core.domain;

public class SubtitleTimestamp {
	
	short hour;
	short minute;
	short second;
	short millisecond;
	
	String formattedTimestamp;
	
	public short getHour() {
		return hour;
	}
	public void setHour(short hour) {
		this.hour = hour;
	}
	public short getMinute() {
		return minute;
	}
	public void setMinute(short minute) {
		this.minute = minute;
	}
	public short getSecond() {
		return second;
	}
	public void setSecond(short second) {
		this.second = second;
	}
	public short getMillisecond() {
		return millisecond;
	}
	public void setMillisecond(short millisecond) {
		this.millisecond = millisecond;
	}
	public String getFormattedTimestamp() {
		return formattedTimestamp;
	}
	public void setFormattedTimestamp(String formattedTimestamp) {
		this.formattedTimestamp = formattedTimestamp;
	}
	
}
