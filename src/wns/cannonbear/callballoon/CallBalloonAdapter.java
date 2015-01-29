package wns.cannonbear.callballoon;

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
		TextView textView = (TextView) rowView
				.findViewById(R.id.log_entry_text);
		ImageView imageView = (ImageView) rowView
				.findViewById(R.id.log_entry_icon);
		textView.setText(values[position].getDate().toString());

		if (values[position].getType() == CallLog.Calls.INCOMING_TYPE) {
			imageView.setImageResource(R.drawable.incoming);
		} else if (values[position].getType() == CallLog.Calls.OUTGOING_TYPE) {
			imageView.setImageResource(R.drawable.outgoing);
		} else if (values[position].getType() == CallLog.Calls.MISSED_TYPE) {
			imageView.setImageResource(R.drawable.missed);
		}

		return rowView;
	}
}
