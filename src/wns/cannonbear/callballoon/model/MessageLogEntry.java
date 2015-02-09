package wns.cannonbear.callballoon.model;

import java.util.Date;

public class MessageLogEntry extends LogEntry {

	private String text;

	public MessageLogEntry(int type, Date date, String text) {
		super(type, date);
		this.text = text;
	}

	public String getText() {
		return text;
	}

	@Override
	public EntryType getEntryType() {
		return EntryType.Message;
	}
}
