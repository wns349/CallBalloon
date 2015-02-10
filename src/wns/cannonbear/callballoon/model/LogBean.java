package wns.cannonbear.callballoon.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import wns.cannonbear.callballoon.model.LogEntry.EntryType;
import wns.cannonbear.callballoon.preference.PreferenceBean;
import android.provider.CallLog;
import android.provider.Telephony.TextBasedSmsColumns;

public class LogBean {
	private final List<LogEntry> logs;

	private int countCallIncoming;
	private int countCallMissed;
	private int countCallOutgoing;

	private int countMsgCountIncoming;
	private int countMsgCountOutgoing;

	private final String incomingNumberRaw;
	private final PreferenceBean pref;

	public LogBean(PreferenceBean pref, String incomingNumberRaw) {
		this.pref = pref;
		this.incomingNumberRaw = incomingNumberRaw;

		this.logs = new ArrayList<LogEntry>();
		this.countCallIncoming = 0;
		this.countCallMissed = 0;
		this.countCallOutgoing = 0;
		this.countMsgCountIncoming = 0;
		this.countMsgCountOutgoing = 0;
	}

	public void addLogEntry(LogEntry entry) {
		if (entry != null) {
			boolean addLogEntry = false;

			if (this.logs.size() >= pref.getNumOfLogsToDisplay()) {
				// Need to remove one with the oldest date
				// First sort to find out the oldest one
				sortLogs();

				if (this.logs.get(this.logs.size() - 1).getDate()
						.compareTo(entry.getDate()) < 0) {
					// remove last element, if new entry is newer
					this.logs.remove(this.logs.size() - 1);
					addLogEntry = true;
				} else {
					// do not add this entry
					addLogEntry = false;
				}
			} else {
				addLogEntry = true;
			}

			if (entry.getEntryType() == EntryType.Call) {
				addLogEntry = addLogEntry & addCallLogEntry(entry);
			} else if (entry.getEntryType() == EntryType.Message) {
				addLogEntry = addLogEntry & addCallLogMessage(entry);
			}

			// Add to logs
			if (addLogEntry) {
				this.logs.add(entry);
			}
		}
	}

	private void sortLogs() {
		Collections.sort(this.logs, new Comparator<LogEntry>() {
			@Override
			public int compare(LogEntry lhs, LogEntry rhs) {
				return rhs.getDate().compareTo(lhs.getDate());
			}
		});
	}

	private boolean addCallLogMessage(LogEntry entry) {

		// Increment counter
		switch (entry.getType()) {
		case TextBasedSmsColumns.MESSAGE_TYPE_INBOX:
			countMsgCountIncoming++;
			break;
		case TextBasedSmsColumns.MESSAGE_TYPE_SENT:
			countMsgCountOutgoing++;
			break;
		default:
			return false;
		}

		return true;
	}

	private boolean addCallLogEntry(LogEntry entry) {

		// Increment counter
		switch (entry.getType()) {
		case CallLog.Calls.INCOMING_TYPE:
			countCallIncoming++;
			break;
		case CallLog.Calls.OUTGOING_TYPE:
			countCallOutgoing++;
			break;
		case CallLog.Calls.MISSED_TYPE:
			countCallMissed++;
			break;
		default:
			// Do not add to the log entry
			return false;
		}

		return true;
	}

	public int getCallCountIncoming() {
		return countCallIncoming;
	}

	public int getCallCountMissed() {
		return countCallMissed;
	}

	public int getCallCountOutgoing() {
		return countCallOutgoing;
	}

	public int getMsgCountIncoming() {
		return countMsgCountIncoming;
	}

	public int getMsgCountOutgoing() {
		return countMsgCountOutgoing;
	}

	public LogEntry[] getLogs() {
		sortLogs();
		return this.logs.toArray(new LogEntry[this.logs.size()]);
	}

	public String getIncomingNumberRaw() {
		return incomingNumberRaw;
	}
}
