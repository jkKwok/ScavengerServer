package com.scavenger.jk;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class CommentActivityServer extends AppCompatActivity {
	public void back(View v) {
		finish();
	}

	String email;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_comment);
		Event eventname = (Event) getIntent().getParcelableExtra("event");
		email = getIntent().getExtras().getString("email");
		TextView title = (TextView) findViewById(R.id.pagename);
		title.setText(eventname.getName());
		title.setSelected(true);
		Button b = (Button) findViewById(R.id.button1);
		b.getBackground().setColorFilter(Color.parseColor("#006400"), PorterDuff.Mode.MULTIPLY);
		
		if (eventname.attending()) {
			((TextView) findViewById(R.id.attendanceview)).setText("You are attending\n this event!");
		} else {
			((TextView) findViewById(R.id.attendanceview)).setText("You are not attending\n this event");
		}
		//////////////////
		buttonSend = (Button) findViewById(R.id.send);
		listView = (ListView) findViewById(R.id.msgview);

		chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);
		listView.setAdapter(chatArrayAdapter);

		chatText = (EditText) findViewById(R.id.msg);
		chatText.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					return sendChatMessage();
				}
				return false;
			}
		});
		buttonSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				sendChatMessage();
			}
		});

		listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		listView.setAdapter(chatArrayAdapter);

		// to scroll the list view to bottom on data change
		chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
			public void onChanged() {
				super.onChanged();
				listView.setSelection(chatArrayAdapter.getCount() - 1);
			}
		});
		//////////////////
		new GCMRequest().execute();
	}

	private ChatArrayAdapter chatArrayAdapter;
	private ListView listView;
	private EditText chatText;
	private Button buttonSend;
	private boolean side = false;
	
	private boolean sendChatMessage() {
		String x = chatText.getText().toString();
		if(!x.equals("")){
			chatArrayAdapter.add(new ChatMessage(side, "<font color=#cc0029>"+email+"</font>"+"\n"+x));
	        chatText.setText("");
	        side = !side;
		}
        return true;
    }
	
	static TextView info;
	TextView infoip;
	TextView msg;

	protected void onDestroy() {
		super.onDestroy();
	}

	private class GCMRequest extends AsyncTask<Void, Void, String> {
		protected String doInBackground(Void... voids) {

			final String API_KEY = "AIzaSyBv9caj2tnuTPzkDd6t2VpgJfyv8sIkWsA"; // An API key saved on the app server
											// that gives the app server
											// authorized access to Google
											// services
			final String CLIENT_REG_ID = "84154074811"; // An ID issued by the GCM
												// connection servers to the
												// client app that allows it to
												// receive messages
			final String postData = "{ \"registration_ids\": [ \"" + CLIENT_REG_ID + "\" ], "
					+ "\"delay_while_idle\": true, " + "\"data\": {\"tickerText\":\"My Ticket\", "
					+ "\"contentTitle\":\"My Title\", " + "\"message\": \"Test GCM message from GCMServer-Android\"}}";

			try {
				URL url = new URL("https://android.googleapis.com/gcm/send");
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setDoInput(true);
				urlConnection.setDoOutput(true);
				urlConnection.setRequestMethod("POST");
				urlConnection.setRequestProperty("Content-Type", "application/json");
				urlConnection.setRequestProperty("Authorization", "key=" + API_KEY);

				OutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
				writer.write(postData);
				writer.flush();
				writer.close();
				outputStream.close();

				int responseCode = urlConnection.getResponseCode();
				InputStream inputStream;
				if (responseCode < HttpURLConnection.HTTP_BAD_REQUEST) {
					inputStream = urlConnection.getInputStream();
				} else {
					inputStream = urlConnection.getErrorStream();
				}
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				String temp, response = "";
				while ((temp = bufferedReader.readLine()) != null) {
					response += temp;
				}
				Log.v("response code", Integer.toString(responseCode));
				Log.v("response message", response);
				
				return response;
			} catch (IOException e) {
				e.printStackTrace();
				return e.toString();
			}
		}

		@Override
		protected void onPostExecute(String message) {
			super.onPostExecute(message);

			if (info != null) {
				try {
					JSONObject jsonObject = new JSONObject(message);
					info.setText(jsonObject.toString(5));
				} catch (JSONException e) {
					e.printStackTrace();
					info.setText(e.toString());
				}
			}
		}
	}
}
