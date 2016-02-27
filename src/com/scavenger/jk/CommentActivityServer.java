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
}
