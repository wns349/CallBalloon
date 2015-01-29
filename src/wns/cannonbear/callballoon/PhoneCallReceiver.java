package wns.cannonbear.callballoon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class PhoneCallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		TelephonyManager telephony = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		telephony.listen(new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {

				if (state == TelephonyManager.CALL_STATE_RINGING) {
					Intent serviceIntent = new Intent(context,
							CallBalloonService.class);
					serviceIntent.putExtra(Const.INCOMING_NUMBER,
							incomingNumber);
					context.startService(serviceIntent);
				} else {
					context.stopService(new Intent(context,
							CallBalloonService.class));
				}

				super.onCallStateChanged(state, incomingNumber);
			}
		}, PhoneStateListener.LISTEN_CALL_STATE);
	}
}
