package wns.cannonbear.callballoon.preference;

import wns.cannonbear.callballoon.R;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceBean {
	private int numOfLogsToDisplay;

	private int longTouchDelay;
	private int touchDetectionThreshold;

	private boolean useSMS;
	private boolean showSMSContent;

	public PreferenceBean(Context context, SharedPreferences pref) {

		setNumOfLogsToDisplay((pref.getInt(
				context.getString(R.string.pref_num_logs),
				Integer.parseInt(context.getString(R.string.pref_num_logs_d)))));

		setLongTouchDelay(pref
				.getInt(context
						.getString(R.string.pref_balloon_long_touch_interval),
						Integer.parseInt(context
								.getString(R.string.pref_balloon_long_touch_interval_d))));

		setTouchDetectionThreshold(pref.getInt(context
				.getString(R.string.pref_balloon_touch_detection), Integer
				.parseInt(context
						.getString(R.string.pref_balloon_touch_detection_d))));

		setUseSMS(pref
				.getBoolean(context.getString(R.string.pref_use_sms), Boolean
						.parseBoolean(context
								.getString(R.string.pref_use_sms_d))));

		setShowSMSContent(pref.getBoolean(context
				.getString(R.string.pref_show_sms_content), Boolean
				.parseBoolean(context
						.getString(R.string.pref_show_sms_content_d))));

	}

	public int getNumOfLogsToDisplay() {
		return numOfLogsToDisplay;
	}

	public void setNumOfLogsToDisplay(int numOfLogsToDisplay) {
		this.numOfLogsToDisplay = numOfLogsToDisplay;
	}

	public int getLongTouchDelay() {
		return longTouchDelay;
	}

	public void setLongTouchDelay(int longTouchDelay) {
		this.longTouchDelay = longTouchDelay;
	}

	public boolean isUseSMS() {
		return useSMS;
	}

	public void setUseSMS(boolean useSMS) {
		this.useSMS = useSMS;
	}

	public boolean isShowSMSContent() {
		return showSMSContent;
	}

	public void setShowSMSContent(boolean showSMSContent) {
		this.showSMSContent = showSMSContent;
	}

	public int getTouchDetectionThreshold() {
		return touchDetectionThreshold;
	}

	public void setTouchDetectionThreshold(int touchDetectionThreshold) {
		this.touchDetectionThreshold = touchDetectionThreshold;
	}
}
