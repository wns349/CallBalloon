package wns.cannonbear.callballoon.model;

import java.util.Date;

public class CallLogEntry extends LogEntry {

	private String duration;

	public CallLogEntry(int type, Date date, String duration) {
		super(type, date);
		this.duration = duration;
	}

	public String getDuration() {
		return duration;
	}

	@Override
	public EntryType getEntryType() {
		return EntryType.Call;
	}

}
