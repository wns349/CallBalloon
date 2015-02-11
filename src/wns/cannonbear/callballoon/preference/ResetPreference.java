package wns.cannonbear.callballoon.preference;

import wns.cannonbear.callballoon.R;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

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

		TextView txtConfirm = new TextView(getContext());
		txtConfirm.setLayoutParams(layoutParams);

		txtConfirm.setText(getContext().getString(R.string.confirmation));

		FrameLayout dialogView = new FrameLayout(getContext());
		dialogView.addView(txtConfirm);

		setPositiveButtonText(android.R.string.ok);
		setNegativeButtonText(android.R.string.cancel);

		return dialogView;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			onClickPositive();
		} else if (which == DialogInterface.BUTTON_NEGATIVE) {
			dialog.dismiss();
		}
	}

	private void onClickPositive() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getContext());
		SharedPreferences.Editor editor = preferences.edit();
		editor.clear();
		editor.commit();

		// Save time, just to notify listener
		persistLong(System.currentTimeMillis());
		
		Toast.makeText(getContext(), getContext().getString(R.string.success),
				Toast.LENGTH_SHORT).show();
		;
	}
}
