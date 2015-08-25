package org.tpaagp.ZimbraClient;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class LoginActivity extends AccountAuthenticatorActivity {
	EditText mServer;
	EditText mPort;
	EditText mUsername;
	EditText mPassword;
	CheckBox mUseSSL;
	Button mLoginButton;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mServer = (EditText) findViewById(R.id.server);
		mPort = (EditText) findViewById(R.id.port);
		mUsername = (EditText) findViewById(R.id.username);
		mPassword = (EditText) findViewById(R.id.password);

		mUseSSL = (CheckBox) findViewById(R.id.use_ssl);
		mUseSSL.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked){
					mPort.setText("443");
				}
				else{
					mPort.setText("80");
				}
			}
		});
		mLoginButton = (Button) findViewById(R.id.login);
		mLoginButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String server = mServer.getText().toString().trim().toLowerCase();
				String port = mPort.getText().toString().trim().toLowerCase();
				String user = mUsername.getText().toString().trim().toLowerCase();
				String password = mPassword.getText().toString().trim().toLowerCase();
				boolean useSSL = mUseSSL.isChecked();
				String useSSLString = "true";
				if (!useSSL){
					useSSLString="false";
				}
				// Errors:	1: usuari buit, 2:passwd buit, 4:server buit o , 8:port buit o no 
				int errors = 0;
				if (user.length() < 1) errors += 1;
				if (password.length() < 1) errors += 2;
				if (server.length() < 1) errors +=4;
				if (port.length () < 1 || !port.matches("[0-9]*")) errors +=8;
				if (errors ==0) {
					LoginTask t = new LoginTask(LoginActivity.this);
					t.execute(user, password, server, port, useSSLString);
				}
				else {
					Log.i("AccountAuthenticator", "errors on form:"+errors);
/*					new AlertDialog.Builder(getApplication())

					.setTitle("BrightHub Alert!")

					.setMessage("Google Android How-to guides in the Bright Hub")

					.setNeutralButton("Ok",

					new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog,

					int which) {

					}

					}).show();
*/
				}
			}

		});
	}

	private class LoginTask extends AsyncTask<String, Void, Boolean> {
		Context mContext;
		ProgressDialog mDialog;

		LoginTask(Context c) {
			mContext = c;
			mLoginButton.setEnabled(false);

			mDialog = ProgressDialog.show(c, "", getString(R.string.authenticating), true, false);
			mDialog.setCancelable(true);
		}

		@Override
		public Boolean doInBackground(String... params) {
			String user = params[0];
			String pass = params[1];
			String server = params[2];
			String port = params[3];
			String useSSLString = params[4];
			boolean useSSL = true;
			if (useSSLString.compareTo("false")==0){
				useSSL=false;
			}
			// Do something internetty
			try {
				System.out.println("Aqui hauria de connectar amb el server");
				Thread.sleep(2000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Si tot ha anat bé, generem el paquet de dades d'usuari
			Bundle userdata = new Bundle(3);
			userdata.putString("server", server);
			userdata.putString("port", port);
			userdata.putString("useSSL", useSSLString);
			userdata.putString("md", "0");
			Bundle result = null;
			Account account = new Account(user, mContext.getString(R.string.ACCOUNT_TYPE));
			AccountManager am = AccountManager.get(mContext);
			
			if (am.addAccountExplicitly(account, pass, userdata)) {
				result = new Bundle();
				result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
				result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
				setAccountAuthenticatorResult(result);
				return true;
			} else {
		
				return false;
			}
		}

		@Override
		public void onPostExecute(Boolean result) {
			mLoginButton.setEnabled(true);
			mDialog.dismiss();
			if (result)
				finish();
		}
	}
}
