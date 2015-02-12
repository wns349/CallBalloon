package wns.cannonbear.callballoon.layout;

import wns.cannonbear.callballoon.R;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class BalloonLayout extends RelativeLayout {

	public BalloonLayout(Context context, ViewGroup root) {
		super(context);

		inflate(context, R.layout.balloon_layout, this);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		super.setVisibility(View.GONE);
		return super.dispatchKeyEvent(event);
	}
}
