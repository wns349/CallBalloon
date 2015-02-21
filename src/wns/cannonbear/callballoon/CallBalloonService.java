package wns.cannonbear.callballoon;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wns.cannonbear.callballoon.layout.BalloonLayout;
import wns.cannonbear.callballoon.model.CallLogEntry;
import wns.cannonbear.callballoon.model.LogBean;
import wns.cannonbear.callballoon.model.MessageLogEntry;
import wns.cannonbear.callballoon.preference.PreferenceBean;
import android.app.SearchManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.Telephony.TextBasedSmsColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CallBalloonService extends Service {
	private WindowManager windowManager;
	private BalloonLayout balloonLayout;
	private RelativeLayout removeLayout, chatHeadLayout;
	private Point screen = new Point();
	private PreferenceBean pref;

	private String incomingNumber;

	@Override
	public void onCreate() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
		this.registerReceiver(orientationChangeReceiver, filter);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null) {
			stopSelf();
		}

		pref = new PreferenceBean(getApplicationContext());
		Log.d(Const.TAG, "Preference: " + pref.toString());

		incomingNumber = intent.getStringExtra(Const.INCOMING_NUMBER);
		Log.d(Const.TAG, "Incoming number: " + incomingNumber);

		startChathead();

		return super.onStartCommand(intent, flags, startId);
	}

	private void startChathead() {
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getSize(screen);
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		clearLayouts();

		// Balloon Layout
		addBalloonLayout(inflater);

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
		showRemoveLayout(false);

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
		params.x = screen.x / 2 + 1;
		params.y = 100;
		windowManager.addView(chatHeadLayout, params);

		chatHeadLayout.setOnTouchListener(onChatHeadTouch);

		resetPosition(params.x);
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

	private void addBalloonLayout(LayoutInflater inflater) {
		final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
				PixelFormat.TRANSLUCENT);

		params.x = 0;
		params.y = 0;

		balloonLayout = new BalloonLayout(getApplicationContext(), null);
		balloonLayout.setOnTouchListener(onBalloonTouch);

		View loadingLayout = balloonLayout
				.findViewById(R.id.balloon_content_layout_loading);
		View contentLayout = balloonLayout
				.findViewById(R.id.balloon_content_layout);
		loadingLayout.setVisibility(View.VISIBLE);
		contentLayout.setVisibility(View.GONE);

		new LoadLogBeanTask()
				.execute(new View[] { loadingLayout, contentLayout });

		showBalloonLayout(false);

		windowManager.addView(balloonLayout, params);
	}

	private void showBalloonLayout(boolean isShow) {
		if (isShow && balloonLayout.getVisibility() != View.VISIBLE) {
			balloonLayout.setVisibility(View.VISIBLE);
		} else if (!isShow) {
			balloonLayout.setVisibility(View.GONE);
		}
	}

	private void showRemoveLayout(boolean isShow) {
		if (isShow && (removeLayout.getVisibility() != View.VISIBLE)) {
			WindowManager.LayoutParams paramRemove = (WindowManager.LayoutParams) removeLayout
					.getLayoutParams();
			int posX = (screen.x - removeLayout.getWidth()) / 2;
			int posY = screen.y
					- (removeLayout.getHeight() + getStatusBarHeight());

			paramRemove.x = posX;
			paramRemove.y = posY;
			removeLayout.setVisibility(View.VISIBLE);

			windowManager.updateViewLayout(removeLayout, paramRemove);
		} else if (!isShow) {
			removeLayout.setVisibility(View.GONE);
		}
	}

	private void onChatHeadClick() {
		showBalloonLayout(balloonLayout.getVisibility() != View.VISIBLE);
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
		this.unregisterReceiver(orientationChangeReceiver);

		clearLayouts();
		super.onDestroy();
	}

	private LogBean getMessageLogs(LogBean logBean, String incomingNumber) {
		String uriMessage = "content://sms";

		Cursor managedCursor = getContentResolver().query(
				Uri.parse(uriMessage), null, null, null, null);
		managedCursor.moveToFirst();

		int idxAddress = managedCursor
				.getColumnIndex(TextBasedSmsColumns.ADDRESS);
		int idxType = managedCursor.getColumnIndex(TextBasedSmsColumns.TYPE);
		int idxDate = managedCursor.getColumnIndex(TextBasedSmsColumns.DATE);
		int idxBody = managedCursor.getColumnIndex(TextBasedSmsColumns.BODY);

		while (managedCursor.moveToNext()) {
			String address = managedCursor.getString(idxAddress);

			String stripped = extractNumber(address);
			if (!stripped.isEmpty()
					&& stripped.equals(extractNumber(incomingNumber))) {
				int type = Integer.parseInt(managedCursor.getString(idxType));
				Date date = new Date(Long.valueOf(managedCursor
						.getString(idxDate)));

				String body = pref.isShowSMSContent() ? managedCursor
						.getString(idxBody) : getString(R.string.hidden);

				logBean.addLogEntry(new MessageLogEntry(type, date, body));
			}
		}
		managedCursor.close();

		return logBean;
	}

	private LogBean getCallLogs(LogBean logBean, String incomingNumber) {
		Cursor managedCursor = getContentResolver().query(
				CallLog.Calls.CONTENT_URI, null, null, null, null);
		int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
		int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
		int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
		int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

		while (managedCursor.moveToNext()) {
			String phoneNumber = managedCursor.getString(number);

			String stripped = extractNumber(phoneNumber);
			if (!stripped.isEmpty()
					&& stripped.equals(extractNumber(incomingNumber))) {
				int callType = Integer.parseInt(managedCursor.getString(type));
				Date callDate = new Date(Long.valueOf(managedCursor
						.getString(date)));
				String callDuration = managedCursor.getString(duration);

				logBean.addLogEntry(new CallLogEntry(callType, callDate,
						callDuration));
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

	View.OnTouchListener onBalloonTouch = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:

				View balloonContentLayout = balloonLayout
						.findViewById(R.id.balloon_content_layout);

				Log.d(Const.TAG, String.format("%d/%d/%d/%d/ %.3f / %.3f ",
						balloonContentLayout.getLeft(),
						balloonContentLayout.getRight(),
						balloonContentLayout.getTop(),
						balloonContentLayout.getBottom(), event.getRawX(),
						event.getRawY()));

				if (!(balloonContentLayout.getLeft() <= event.getRawX()
						&& event.getRawX() <= balloonContentLayout.getRight()
						&& event.getRawY() >= balloonContentLayout.getTop() && event
						.getRawY() <= balloonContentLayout.getBottom())) {
					showBalloonLayout(false);
				}

				break;
			}
			return true;
		}

	};

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
				if (System.currentTimeMillis() - startTime >= pref
						.getLongTouchDelay()) {
					isDragged = moveView(event, params, false);

					showRemoveLayout(true);

					if (isOnRemove = isOverRemoveLayout(event, params)) {
						placeOnRemoveLayout(params);
					}

					if (isDragged) {
						showBalloonLayout(false);
					}
				}

				break;
			case MotionEvent.ACTION_UP:
				showRemoveLayout(false);

				if (isDragged) {
					if (isOnRemove) {
						// Remove
						stopSelf();
					} else {
						// Move by drag
						resetPosition(params.x);
						showBalloonLayout(false);
					}
				} else {
					// Click
					onChatHeadClick();
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

			return (newX >= removeParams.x && newX <= removeParams.x + width && newY >= removeParams.y);
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
					&& Math.abs(newX - params.x) < pref
							.getTouchDetectionThreshold()
					&& Math.abs(newY - params.y) < pref
							.getTouchDetectionThreshold()) {
				return false; // Do nothing, if move is so trivial
			} else {
				params.x = newX;
				params.y = newY;
				windowManager.updateViewLayout(chatHeadLayout, params);
				return true;
			}
		}

	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private BroadcastReceiver orientationChangeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_CONFIGURATION_CHANGED)) {
				// orientation is changed
				Intent serviceIntent = new Intent(context,
						CallBalloonService.class);
				serviceIntent.putExtra(Const.INCOMING_NUMBER, incomingNumber);
				context.startService(serviceIntent);
			}
		}
	};

	class LoadLogBeanTask extends AsyncTask<View, Void, LogBean> {
		View loadingLayout;
		View contentLayout;

		@Override
		protected LogBean doInBackground(View... params) {
			loadingLayout = params[0];
			contentLayout = params[1];

			LogBean logBean = new LogBean(pref, incomingNumber);

			getCallLogs(logBean, incomingNumber);

			if (pref.isUseSMS()) {
				getMessageLogs(logBean, incomingNumber);
			}

			return logBean;
		}

		@Override
		protected void onPostExecute(LogBean logBean) {
			ListView listView = (ListView) balloonLayout
					.findViewById(R.id.log_entry_listview);
			TextView noHistory = (TextView) balloonLayout
					.findViewById(R.id.log_entry_no_listview);

			// Update call log history
			if (logBean.getLogs().length > 0) {
				CallBalloonAdapter adapter = new CallBalloonAdapter(
						getApplicationContext(), logBean.getLogs());
				listView.setAdapter(adapter);
				listView.setVisibility(View.VISIBLE);
				noHistory.setVisibility(View.GONE);
			} else {
				listView.setVisibility(View.GONE);
				noHistory.setVisibility(View.VISIBLE);
			}

			// Update textview
			LinearLayout countLayout = (LinearLayout) balloonLayout
					.findViewById(R.id.count_layout);
			TextView incomingCount = (TextView) countLayout
					.findViewById(R.id.txt_incoming);
			incomingCount
					.setText(String.valueOf(logBean.getCallCountIncoming()));
			TextView outgoingCount = (TextView) countLayout
					.findViewById(R.id.txt_outgoing);
			outgoingCount
					.setText(String.valueOf(logBean.getCallCountOutgoing()));
			TextView missedCount = (TextView) countLayout
					.findViewById(R.id.txt_missed);
			missedCount.setText(String.valueOf(logBean.getCallCountMissed()));
			TextView incomingMsgCount = (TextView) countLayout
					.findViewById(R.id.txt_msg_incoming);
			incomingMsgCount.setText(pref.isUseSMS() ? String.valueOf(logBean
					.getMsgCountIncoming()) : Const.COUNT_UNKNOWN);
			TextView outgoingMsgCount = (TextView) countLayout
					.findViewById(R.id.txt_msg_outgoing);
			outgoingMsgCount.setText(pref.isUseSMS() ? String.valueOf(logBean
					.getMsgCountOutgoing()) : Const.COUNT_UNKNOWN);

			// Set click listener
			Button btnSearchWeb = (Button) balloonLayout
					.findViewById(R.id.btn_search_web);
			final String num = incomingNumber;
			final View.OnClickListener btnSearchWebClickListener = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showBalloonLayout(false);
					showRemoveLayout(false);

					Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					String keyword = num;
					intent.putExtra(SearchManager.QUERY, keyword);
					startActivity(intent);
				}
			};
			btnSearchWeb.setOnClickListener(btnSearchWebClickListener);

			loadingLayout.setVisibility(View.GONE);
			contentLayout.setVisibility(View.VISIBLE);
		}

	}
}
