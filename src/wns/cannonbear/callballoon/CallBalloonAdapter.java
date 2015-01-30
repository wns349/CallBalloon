package wns.cannonbear.callballoon;

import java.text.SimpleDateFormat;

import wns.cannonbear.callballoon.model.CallLogEntry;
import android.content.Context;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CallBalloonAdapter extends ArrayAdapter<CallLogEntry> {
	private final Context context;
	private final CallLogEntry[] values;

	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");

	public CallBalloonAdapter(Context context, CallLogEntry[] values) {
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

		txtDate.setText(sdf.format(values[position].getDate()));
		txtDuration.setText(formatDuration(values[position].getDuration()));

		if (values[position].getType() == CallLog.Calls.INCOMING_TYPE) {
			imageView.setImageResource(R.drawable.incoming);
		} else if (values[position].getType() == CallLog.Calls.OUTGOING_TYPE) {
			imageView.setImageResource(R.drawable.outgoing);
		} else if (values[position].getType() == CallLog.Calls.MISSED_TYPE) {
			imageView.setImageResource(R.drawable.missed);
		}

		return rowView;
	}

	private String formatDuration(String duration) {
		long seconds = Long.parseLong(duration);

		int h = (int) (seconds / 3600);
		int m = (int) ((seconds % 3600) / 60);
		int s = (int) (seconds % 60);

		return String.format("%02d:%02d:%02d", h, m, s);
	}
}
