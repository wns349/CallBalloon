package wns.cannonbear.callballoon;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Display the fragment as the main content.
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new SettingsFragment()).commit();
	}

	public static class SettingsFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.preferences);

			// // Append finally category
			// PreferenceCategory finallyCategory = (PreferenceCategory)
			// findPreference(getString(R.string.pref_finally));
			// // Add reset preference
			// ResetPreference resetPreference = new ResetPreference(
			// getActivity(), null);
			// resetPreference.setKey(getString(R.string.pref_reset));
			// resetPreference.setTitle(getString(R.string.pref_reset_t));
			// resetPreference.setSummary(getString(R.string.pref_reset_s));
			// finallyCategory.addPreference(resetPreference);
		}
	}

	// @Override
	// protected void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	// setContentView(R.layout.activity_main);
	//
	// Log.d(Const.TAG, "Hello there");
	// // startService(new Intent(this, ChatHeadService.class));
	//
	// final EditText t = (EditText) findViewById(R.id.editText1);
	//
	// t.setText("1234");
	// Button btn = (Button) findViewById(R.id.button1);
	// btn.setOnClickListener(new View.OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// Intent i = new Intent(getApplicationContext(),
	// CallBalloonService.class);
	// i.putExtra(Const.INCOMING_NUMBER, t.getText().toString());
	//
	// startService(i);
	// }
	//
	// });
	// }
}
