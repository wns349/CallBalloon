package wns.cannonbear.callballoon;

import java.text.SimpleDateFormat;

import wns.cannonbear.callballoon.model.CallLogEntry;
import wns.cannonbear.callballoon.model.LogEntry;
import wns.cannonbear.callballoon.model.MessageLogEntry;
import android.content.Context;
import android.provider.CallLog;
import android.provider.Telephony.TextBasedSmsColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CallBalloonAdapter extends ArrayAdapter<LogEntry> {
	private final Context context;
	private final LogEntry[] values;

	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");

	public CallBalloonAdapter(Context context, LogEntry[] values) {
		super(context, R.layout.log_entry, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.log_entry, parent, false);
		View textView = rowView.findViewById(R.id.log_entry_text_layout);
		TextView txtDate = (TextView) textView
				.findViewById(R.id.log_entry_date);
		TextView txtDuration = (TextView) textView
				.findViewById(R.id.log_entry_duration);
		ImageView imageView = (ImageView) rowView
				.findViewById(R.id.log_entry_icon);

		LogEntry logEntry = values[position];
		switch (logEntry.getEntryType()) {
		case Call:
			getViewCallLogEntry((CallLogEntry) logEntry, txtDate, txtDuration,
					imageView);
			break;
		case Message:
			getViewMessageLogEntry((MessageLogEntry) logEntry, txtDate,
					txtDuration, imageView);
			break;
		}

		return rowView;
	}

	private void getViewMessageLogEntry(MessageLogEntry messageLogEntry,
			TextView txtDate, TextView txtDuration, ImageView imageView) {
		txtDate.setText(sdf.format(messageLogEntry.getDate()));
		txtDuration.setText(messageLogEntry.getText());

		if (messageLogEntry.getType() == TextBasedSmsColumns.MESSAGE_TYPE_INBOX) {
			imageView.setImageResource(R.drawable.msg_incoming);
		} else if (messageLogEntry.getType() == TextBasedSmsColumns.MESSAGE_TYPE_SENT) {
			imageView.setImageResource(R.drawable.msg_outgoing);
		}
	}

	private void getViewCallLogEntry(CallLogEntry callLogEntry,
			TextView txtDate, TextView txtDuration, ImageView imageView) {
		txtDate.setText(sdf.format(callLogEntry.getDate()));
		txtDuration.setText(formatDuration(callLogEntry.getDuration()));

		if (callLogEntry.getType() == CallLog.Calls.INCOMING_TYPE) {
			imageView.setImageResource(R.drawable.incoming);
		} else if (callLogEntry.getType() == CallLog.Calls.OUTGOING_TYPE) {
			imageView.setImageResource(R.drawable.outgoing);
		} else if (callLogEntry.getType() == CallLog.Calls.MISSED_TYPE) {
			imageView.setImageResource(R.drawable.missed);
		}
	}

	private String formatDuration(String duration) {
		long seconds = Long.parseLong(duration);

		int h = (int) (seconds / 3600);
		int m = (int) ((seconds % 3600) / 60);
		int s = (int) (seconds % 60);

		return String.format("%02d:%02d:%02d", h, m, s);
	}
}
