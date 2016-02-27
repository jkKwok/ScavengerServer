package com.scavenger.jk;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends ActionBarActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		Button loginButton = (Button) findViewById(R.id.button1);
		loginButton.setOnClickListener(Login);

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	private View.OnClickListener Login = new View.OnClickListener() {
		public void onClick(View v) {
			String x =((EditText)findViewById(R.id.myemail)).getText().toString();
			if(!x.isEmpty()){
				Intent i = new Intent(getApplicationContext(), HomeActivity.class);
				i.putExtra("email", x);
				startActivity(i);
			}
		}
	};

	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

