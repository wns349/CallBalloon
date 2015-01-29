package wns.cannonbear.callballoon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Log.d(Const.TAG, "Hello there");
		// startService(new Intent(this, ChatHeadService.class));

		final EditText t = (EditText) findViewById(R.id.editText1);

		t.setText("1234");
		Button btn = (Button) findViewById(R.id.button1);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						CallBalloonService.class);
				i.putExtra(Const.INCOMING_NUMBER, t.getText().toString());

				startService(i);
			}

		});
	}
}
