package wns.cannonbear.callballoon.model;

import java.util.Date;

public abstract class LogEntry {
	public enum EntryType {
		Call, Message
	}

	private int type;
	private Date date;

	public LogEntry(int type, Date date) {
		this.type = type;
		this.date = date;
	}

	public int getType() {
		return type;
	}

	public Date getDate() {
		return date;
	}

	public abstract EntryType getEntryType();
}
