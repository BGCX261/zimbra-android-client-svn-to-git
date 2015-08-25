package org.tpaagp.ZimbraClient;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

public class ProfileActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);

		if (getIntent().getData() != null) {
			Cursor cursor = managedQuery(getIntent().getData(), null, null, null, null);
			if (cursor.moveToNext()) {
				String username = cursor.getString(cursor.getColumnIndex("DATA1"));
				TextView tv = (TextView) findViewById(R.id.profiletext);
				tv.setText("This is the profile for " + username);
			}
		} else {
			// How did we get here without data?
			finish();
		}
	}
}
