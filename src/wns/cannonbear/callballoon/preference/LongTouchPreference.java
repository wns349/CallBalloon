package wns.cannonbear.callballoon.preference;

import wns.cannonbear.callballoon.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

public class LongTouchPreference extends DialogPreference {

	public static final int MAX_VALUE = 1500;
	public static final int MIN_VALUE = 10;

	private NumberPicker picker;

	public LongTouchPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LongTouchPreference(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected View onCreateDialogView() {
		View v = LayoutInflater.from(getContext()).inflate(
				R.layout.diag_long_touch, null);

		picker = (NumberPicker) v.findViewById(R.id.number_picker);

		return v;
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
		picker.setMinValue(MIN_VALUE);
		picker.setMaxValue(MAX_VALUE);
		picker.setValue(getValue());
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult) {
			setValue(picker.getValue());
		}
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getInt(index, MIN_VALUE);
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue,
			Object defaultValue) {
		setValue(restorePersistedValue ? getPersistedInt(MIN_VALUE)
				: (Integer) defaultValue);
	}

	public void setValue(int value) {
		persistInt(value);
	}

	public int getValue() {
		return getPersistedInt(Integer.parseInt(getContext().getString(
				R.string.pref_balloon_long_touch_interval_d)));
	}

}