package wns.cannonbear.callballoon.layout;

import wns.cannonbear.callballoon.CallBalloonService;
import wns.cannonbear.callballoon.Const;
import wns.cannonbear.callballoon.R;
import wns.cannonbear.callballoon.preference.PreferenceBean;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingsFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PreferenceManager.getDefaultSharedPreferences(getActivity())
				.registerOnSharedPreferenceChangeListener(this);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

		updatePreferences();
	}

	private void updatePreferences() {
		PreferenceBean prefBean = new PreferenceBean(getActivity());

		updateLongTouchDelaySummary(
				prefBean,
				findPreference(getString(R.string.pref_balloon_long_touch_interval)));

		updateTouchDetection(
				prefBean,
				findPreference(getString(R.string.pref_balloon_touch_detection)));

		updateNumberOfLogs(prefBean,
				findPreference(getString(R.string.pref_num_logs)));

		updateUseSMS(prefBean, findPreference(getString(R.string.pref_use_sms)));

		updateShowSMSContent(prefBean,
				findPreference(getString(R.string.pref_show_sms_content)));

		updateVersion();

	}

	private void updateVersion() {
		Preference p = findPreference(getString(R.string.pref_version));
		try {
			p.setSummary(getActivity().getPackageManager().getPackageInfo(
					getActivity().getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			Log.e(Const.TAG, e.getLocalizedMessage(), e);
			p.setSummary(Const.COUNT_UNKNOWN);
		}
	}

	private void updateUseSMS(PreferenceBean prefBean, Preference p) {
		((CheckBoxPreference) p).setChecked(prefBean.isUseSMS());
	}

	private void updateShowSMSContent(PreferenceBean prefBean, Preference p) {
		((CheckBoxPreference) p).setChecked(prefBean.isShowSMSContent());
	}

	private void updateNumberOfLogs(PreferenceBean prefBean, Preference p) {
		p.setSummary(String.valueOf(prefBean.getNumOfLogsToDisplay()));
	}

	private void updateLongTouchDelaySummary(PreferenceBean prefBean,
			Preference p) {
		p.setSummary(String.valueOf(prefBean.getLongTouchDelay())
				+ getString(R.string.unit_ms));
	}

	private void updateTouchDetection(PreferenceBean prefBean, Preference p) {
		p.setSummary(String.valueOf(prefBean.getTouchDetectionThreshold()));
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Preference prefView = findPreference(key);

		PreferenceBean prefBean = new PreferenceBean(getActivity(),
				sharedPreferences);

		if (prefView == null) {
			return;
		}

		if (prefView.getKey().equals(
				getString(R.string.pref_balloon_long_touch_interval))) {
			updateLongTouchDelaySummary(prefBean, prefView);
		} else if (prefView.getKey().equals(
				getString(R.string.pref_balloon_touch_detection))) {
			updateTouchDetection(prefBean, prefView);
		} else if (prefView.getKey().equals(getString(R.string.pref_num_logs))) {
			updateNumberOfLogs(prefBean, prefView);
		} else if (prefView.getKey().equals(getString(R.string.pref_use_sms))) {
			updateUseSMS(prefBean, prefView);
		} else if (prefView.getKey().equals(
				getString(R.string.pref_show_sms_content))) {
			updateShowSMSContent(prefBean, prefView);
		} else if (prefView.getKey().equals(getString(R.string.pref_reset))) {
			updateLongTouchDelaySummary(
					prefBean,
					findPreference(getString(R.string.pref_balloon_long_touch_interval)));
			updateTouchDetection(
					prefBean,
					findPreference(getString(R.string.pref_balloon_touch_detection)));
			updateNumberOfLogs(prefBean,
					findPreference(getString(R.string.pref_num_logs)));
			updateUseSMS(prefBean,
					findPreference(getString(R.string.pref_use_sms)));
			updateShowSMSContent(prefBean,
					findPreference(getString(R.string.pref_show_sms_content)));
		}

		// stop service, if running
		getActivity().stopService(
				new Intent(getActivity(), CallBalloonService.class));
	}

}
