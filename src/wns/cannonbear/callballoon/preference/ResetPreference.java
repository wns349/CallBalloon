package wns.cannonbear.callballoon.preference;

import wns.cannonbear.callballoon.R;
import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ResetPreference extends DialogPreference {
	public ResetPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ResetPreference(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected View onCreateDialogView() {
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.gravity = Gravity.CENTER;

		TextView txtContact = new TextView(getContext());
		txtContact.setLayoutParams(layoutParams);

		txtContact.setText(getContext().getString(R.string.pref_reset_s));

		FrameLayout dialogView = new FrameLayout(getContext());
		dialogView.addView(txtContact);

		setPositiveButtonText(android.R.string.ok);
		setNegativeButtonText(android.R.string.cancel);

		return dialogView;
	}
}
