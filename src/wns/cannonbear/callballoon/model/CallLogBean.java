package wns.cannonbear.callballoon.model;

import java.util.ArrayList;
import java.util.List;

import wns.cannonbear.callballoon.Const;
import android.provider.CallLog;

public class CallLogBean {
	private final List<CallLogEntry> logs;

	private int countIncoming;
	private int countMissed;
	private int countOutgoing;

	public CallLogBean() {
		this.logs = new ArrayList<CallLogEntry>(Const.MAX_CALL_LOG_HISTORY);
		this.countIncoming = 0;
		this.countMissed = 0;
		this.countOutgoing = 0;
	}

	public void addCallLogEntry(CallLogEntry entry) {
		if (entry != null) {
			while (this.logs.size() >= Const.MAX_CALL_LOG_HISTORY) {
				this.logs.remove(0);
			}

			this.logs.add(entry);

			// Increment counter
			switch (entry.getType()) {
			case CallLog.Calls.INCOMING_TYPE:
				countIncoming++;
				break;
			case CallLog.Calls.OUTGOING_TYPE:
				countOutgoing++;
				break;
			case CallLog.Calls.MISSED_TYPE:
				countMissed++;
				break;
			}
		}
	}

	public int getCountIncoming() {
		return countIncoming;
	}

	public int getCountMissed() {
		return countMissed;
	}

	public int getCountOutgoing() {
		return countOutgoing;
	}

	public CallLogEntry[] getLogs() {
		return this.logs.toArray(new CallLogEntry[this.logs.size()]);
	}
}
