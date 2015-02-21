package wns.cannonbear.callballoon.preference;

import wns.cannonbear.callballoon.CallBalloonService;
import wns.cannonbear.callballoon.Const;
import wns.cannonbear.callballoon.R;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

public class DemoPreference extends DialogPreference {
	private EditText edtIncomingNumber;

	public DemoPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DemoPreference(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected View onCreateDialogView() {
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.gravity = Gravity.CENTER;

		edtIncomingNumber = new EditText(getContext());
		edtIncomingNumber.setLayoutParams(layoutParams);

		edtIncomingNumber.setHint(getContext().getText(
				R.string.hint_incoming_number));

		FrameLayout dialogView = new FrameLayout(getContext());
		dialogView.addView(edtIncomingNumber);

		return dialogView;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

		if (which == DialogInterface.BUTTON_POSITIVE) {
			Intent i = new Intent(getContext(), CallBalloonService.class);
			i.putExtra(Const.INCOMING_NUMBER, edtIncomingNumber.getText()
					.toString());
			getContext().startService(i);
		} else if (which == DialogInterface.BUTTON_NEGATIVE) {
			dialog.dismiss();
		}
	}
}
