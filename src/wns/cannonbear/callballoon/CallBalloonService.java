package wns.cannonbear.callballoon;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wns.cannonbear.callballoon.model.CallLogBean;
import wns.cannonbear.callballoon.model.CallLogEntry;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.CallLog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CallBalloonService extends Service {
	private WindowManager windowManager;
	private ImageView removeImage;
	private RelativeLayout balloonLayout, removeLayout, chatHeadLayout;
	private Point screen = new Point();

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null) {
			stopSelf();
		}

		String incomingNumber = intent.getStringExtra(Const.INCOMING_NUMBER);
		Log.d(Const.TAG, "Incoming number: " + incomingNumber);

		CallLogBean logBean = getCallLogs(incomingNumber);

		startChathead(logBean);

		return super.onStartCommand(intent, flags, startId);
	}

	private void startChathead(CallLogBean logBean) {
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getSize(screen);
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		clearLayouts();

		// Listview
		addBalloonLayout(inflater, logBean);

		// Remove
		addRemoveLayout(inflater);

		// Chat head
		addChatHeadLayout(inflater);
	}

	private void addRemoveLayout(LayoutInflater inflater) {
		final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
						| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
				PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.TOP | Gravity.LEFT;

		removeLayout = (RelativeLayout) inflater.inflate(
				R.layout.remove_layout, null);
		removeImage = (ImageView) removeLayout.findViewById(R.id.img_remove);
		removeLayout.setVisibility(View.GONE);

		windowManager.addView(removeLayout, params);
	}

	private void addChatHeadLayout(LayoutInflater inflater) {
		chatHeadLayout = (RelativeLayout) inflater.inflate(
				R.layout.chathead_layout, null);

		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
						| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
				PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.x = 0;
		params.y = 100;
		windowManager.addView(chatHeadLayout, params);

		chatHeadLayout.setOnTouchListener(onChatHeadTouch);

	}

	private void resetPosition(int posX) {
		int w = chatHeadLayout.getWidth();

		if (posX == 0 || posX == screen.x - w) {

		} else if (posX + w / 2 <= screen.x / 2) {
			moveToLeft(posX);

		} else if (posX + w / 2 > screen.x / 2) {
			moveToRight(posX);

		}

	}

	private void moveToLeft(int posX) {

		final int x = posX;
		new CountDownTimer(500, 5) {
			WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) chatHeadLayout
					.getLayoutParams();

			public void onTick(long t) {
				long step = (500 - t) / 5;
				mParams.x = (int) (double) bounceValue(step, x);
				windowManager.updateViewLayout(chatHeadLayout, mParams);
			}

			public void onFinish() {
				mParams.x = 0;
				windowManager.updateViewLayout(chatHeadLayout, mParams);
			}
		}.start();
	}

	private void moveToRight(int posX) {
		final int x = posX;
		new CountDownTimer(500, 5) {
			WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) chatHeadLayout
					.getLayoutParams();

			public void onTick(long t) {
				long step = (500 - t) / 5;
				mParams.x = screen.x + (int) (double) bounceValue(step, x)
						- chatHeadLayout.getWidth();
				windowManager.updateViewLayout(chatHeadLayout, mParams);
			}

			public void onFinish() {
				mParams.x = screen.x - chatHeadLayout.getWidth();
				windowManager.updateViewLayout(chatHeadLayout, mParams);
			}
		}.start();
	}

	private double bounceValue(long step, long scale) {
		double value = scale * java.lang.Math.exp(-0.055 * step)
				* java.lang.Math.cos(0.08 * step);
		return value;
	}

	private int getStatusBarHeight() {
		int statusBarHeight = (int) Math.ceil(25 * getApplicationContext()
				.getResources().getDisplayMetrics().density);
		return statusBarHeight;
	}

	private void addBalloonLayout(LayoutInflater inflater, CallLogBean logBean) {
		final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
						| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
				PixelFormat.TRANSLUCENT);

		params.x = 0;
		params.y = getStatusBarHeight();

		balloonLayout = (RelativeLayout) inflater.inflate(
				R.layout.balloon_layout, null);
		ListView listView = (ListView) balloonLayout
				.findViewById(R.id.log_entry_listview);
		CallBalloonAdapter adapter = new CallBalloonAdapter(
				getApplicationContext(), logBean.getLogs());
		listView.setAdapter(adapter);

		LinearLayout countLayout = (LinearLayout) balloonLayout
				.findViewById(R.id.count_layout);
		TextView incomingCount = (TextView) countLayout
				.findViewById(R.id.txt_incoming);
		incomingCount.setText(String.valueOf(logBean.getCountIncoming()));
		TextView outgoingCount = (TextView) countLayout
				.findViewById(R.id.txt_outgoing);
		outgoingCount.setText(String.valueOf(logBean.getCountOutgoing()));
		TextView missedCount = (TextView) countLayout
				.findViewById(R.id.txt_missed);
		missedCount.setText(String.valueOf(logBean.getCountMissed()));

		balloonLayout.setVisibility(View.GONE);
		windowManager.addView(balloonLayout, params);
	}

	private void onClick() {
		if (balloonLayout.getVisibility() != View.VISIBLE) {
			balloonLayout.setVisibility(View.VISIBLE);
		} else {
			balloonLayout.setVisibility(View.GONE);
		}

	}

	private void showRemoveLayout() {
		WindowManager.LayoutParams paramRemove = (WindowManager.LayoutParams) removeLayout
				.getLayoutParams();
		int posX = (screen.x - removeLayout.getWidth()) / 2;
		int posY = screen.y - (removeLayout.getHeight() + getStatusBarHeight());

		paramRemove.x = posX;
		paramRemove.y = posY;
		removeLayout.setVisibility(View.VISIBLE);

		windowManager.updateViewLayout(removeLayout, paramRemove);
	}

	private void clearLayouts() {
		if (balloonLayout != null) {
			windowManager.removeView(balloonLayout);
			balloonLayout = null;
		}

		if (removeLayout != null) {
			windowManager.removeView(removeLayout);
			removeLayout = null;
		}

		if (chatHeadLayout != null) {
			windowManager.removeView(chatHeadLayout);
			chatHeadLayout = null;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		clearLayouts();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private CallLogBean getCallLogs(String incomingNumber) {
		Cursor managedCursor = getContentResolver().query(
				CallLog.Calls.CONTENT_URI, null, null, null, null);
		int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
		int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
		int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
		int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

		CallLogBean logBean = new CallLogBean();
		while (managedCursor.moveToNext()) {
			String phoneNumber = managedCursor.getString(number);

			String stripped = extractNumber(phoneNumber);
			if (!stripped.isEmpty()
					&& stripped.equals(extractNumber(incomingNumber))) {
				int callType = Integer.parseInt(managedCursor.getString(type));
				Date callDate = new Date(Long.valueOf(managedCursor
						.getString(date)));
				String callDuration = managedCursor.getString(duration);

				logBean.addCallLogEntry(new CallLogEntry(phoneNumber, callType,
						callDate, callDuration));
			}
		}
		managedCursor.close();

		return logBean;
	}

	public static String extractNumber(String number) {
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(number);
		String rtnVal = "";
		while (m.find()) {
			rtnVal += m.group();
		}
		return rtnVal;
	}

	View.OnTouchListener onChatHeadTouch = new View.OnTouchListener() {
		long startTime;
		int posX, posY;
		float posExactX, posExactY;
		boolean isDragged = false, isOnRemove = false;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			WindowManager.LayoutParams params = (WindowManager.LayoutParams) chatHeadLayout
					.getLayoutParams();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				posX = (int) (posExactX = event.getRawX());
				posY = (int) (posExactY = event.getRawY());
				isDragged = false;
				isOnRemove = false;
				startTime = System.currentTimeMillis();
				break;
			case MotionEvent.ACTION_MOVE:
				if (System.currentTimeMillis() - startTime >= Const.LONG_TOUCH_THRESHOLD) {
					isDragged = moveView(event, params, false);

					if (removeLayout.getVisibility() != View.VISIBLE) {
						showRemoveLayout();
					}

					if (isOnRemove = isOverRemoveLayout(event, params)) {
						placeOnRemoveLayout(params);
					}
				}

				break;
			case MotionEvent.ACTION_UP:
				removeLayout.setVisibility(View.GONE);

				if (isDragged) {
					if (isOnRemove) {
						// Remove
						stopSelf();
					} else {
						// Move by drag
						resetPosition(params.x);
					}
				} else {
					// Click
					onClick();
				}

				isDragged = false;
				break;
			default:
				break;
			}
			return true;
		}

		private void placeOnRemoveLayout(LayoutParams chatHeadParams) {
			WindowManager.LayoutParams removeParams = (WindowManager.LayoutParams) removeLayout
					.getLayoutParams();

			int width = removeLayout.getWidth();
			int height = removeLayout.getHeight();

			int newX = removeParams.x + width / 2 - chatHeadLayout.getWidth()
					/ 2;
			int newY = removeParams.y + height / 2 - chatHeadLayout.getHeight()
					/ 2;

			chatHeadParams.x = newX;
			chatHeadParams.y = newY;

			windowManager.updateViewLayout(chatHeadLayout, chatHeadParams);
		}

		private boolean isOverRemoveLayout(MotionEvent event,
				LayoutParams chatHeadParams) {
			int newX = calculateNewX(event.getRawX());
			int newY = calculateNewY(event.getRawY());

			WindowManager.LayoutParams removeParams = (WindowManager.LayoutParams) removeLayout
					.getLayoutParams();

			int width = removeLayout.getWidth();
			int height = removeLayout.getHeight();

			Log.d(Const.TAG, String.format("%d/%d/%d/%d/%d/%d", newX, newY,
					width, height, removeParams.x, removeParams.y));

			return (newX >= removeParams.x && newX <= removeParams.x + width
					&& newY <= removeParams.y && newY >= removeParams.y
					- height);
		}

		private int calculateNewX(float rawX) {
			return (int) (posX + (rawX - posExactX))
					- chatHeadLayout.getWidth() / 2;
		}

		private int calculateNewY(float rawY) {
			return (int) (posY + (rawY - posExactY))
					- chatHeadLayout.getHeight() / 2;
		}

		private boolean moveView(MotionEvent event,
				WindowManager.LayoutParams params, boolean ignoreTrivialMove) {
			int newX = calculateNewX(event.getRawX());
			int newY = calculateNewY(event.getRawY());

			if (ignoreTrivialMove
					&& Math.abs(newX - params.x) < Const.TOUCH_POSITION_THRESHOLD
					&& Math.abs(newY - params.y) < Const.TOUCH_POSITION_THRESHOLD) {
				return false; // Do nothing, if move is so trivial
			} else {
				params.x = newX;
				params.y = newY;
				windowManager.updateViewLayout(chatHeadLayout, params);
				return true;
			}
		}

	};

}
