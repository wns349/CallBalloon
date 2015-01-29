package wns.cannonbear.callballoon.model;

import java.util.Date;

public class CallLogEntry {
	private String phoneNumber;
	private int type;
	private Date date;
	private String duration;

	public CallLogEntry(String phoneNumber, int type, Date date, String duration) {
		this.phoneNumber = phoneNumber;
		this.type = type;
		this.date = date;
		this.duration = duration;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

}
