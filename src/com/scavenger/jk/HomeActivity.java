package com.scavenger.jk;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.graphics.drawable.GradientDrawable;

public class HomeActivity extends ActionBarActivity {

	Event[] e = new Event[4];
	Button b1, b2, b3, b4;
	TextView e1, e2, e3, e4;
	String email;
	ArrayList<String> mailContent = new ArrayList<String>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		email = getIntent().getExtras().getString("email");
		b1 = (Button) findViewById(R.id.Button01);
		b2 = (Button) findViewById(R.id.Button02);
		b3 = (Button) findViewById(R.id.Button03);
		b4 = (Button) findViewById(R.id.Button04);
		e1 = (TextView) findViewById(R.id.eventtitle1);
		e2 = (TextView) findViewById(R.id.eventtitle2);
		e3 = (TextView) findViewById(R.id.eventtitle3);
		e4 = (TextView) findViewById(R.id.eventtitle4);
		// e[0] = new Event("Buffet @ UTown");
		// e[1] = new Event("Free Food at YIH");
		// e[2] = new Event("Event at U Hall with food available");
		// e[3] = new Event("A Lot of Leftovers at RVRC");
		try {
			new asyncMail().execute("").get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!mailContent.isEmpty()) {
			try {
				e[0] = new Event(mailContent.get(0));
				e[1] = new Event(mailContent.get(1));
				e[2] = new Event(mailContent.get(2));
				e[3] = new Event(mailContent.get(3));
				e1.setText(e[0].getName());
				e2.setText(e[1].getName());
				e3.setText(e[2].getName());
				e4.setText(e[3].getName());
				OnClickListener listen = new OnClickListener() {
					public void onClick(View v) {
						toSelectedActivity(v);
					}
				};
				e1.setOnClickListener(listen);
				e2.setOnClickListener(listen);
				e3.setOnClickListener(listen);
				e4.setOnClickListener(listen);
			} catch (Exception e) {

			}
		}
	}

	private class asyncMail extends AsyncTask<String, String, String> {
		protected String doInBackground(String... params) {
			try {
				EmailAccount account = new EmailAccount();
				EmailAuthenticator authenticator = new EmailAuthenticator(account);
				String mailServer = "imap.gmail.com";
				Session imapSession = Session.getDefaultInstance(new Properties(), authenticator);
				Store store = imapSession.getStore("imaps");
				store.connect(mailServer, account.username, account.password);
				Folder inbox = store.getFolder("Inbox");
				inbox.open(Folder.READ_WRITE);
				Message[] result = inbox.getMessages();
				for (int i = 1; i <= result.length; i++) {
					Message m = result[result.length - i];
					if (m.getSubject().equals("New_Event")) {
						Multipart mp = (Multipart) m.getContent();
						BodyPart bp = mp.getBodyPart(0);
						mailContent.add(bp.getContent().toString());
						break;
					} else {
						m.setFlag(Flags.Flag.DELETED, true);
					}
				}
				inbox.close(true);
				store.close();
			} catch (Exception e) {
				e.printStackTrace();
				return e.toString();
			}
			return "Done";
		}
	}

	public class EmailAuthenticator extends Authenticator {
		private EmailAccount account;

		public EmailAuthenticator(EmailAccount account) {
			super();
			this.account = account;
		}

		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(account.emailAddress, account.password);
		}
	}

	public class EmailAccount {
		public String urlServer = "";
		public String username = "";
		public String password = "";
		public String emailAddress;

		public EmailAccount() {
			this.emailAddress = username + "@" + urlServer;
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void toSelectedActivity(View v) {
		TextView a = (TextView) v;
		int i = 5;
		if (a.equals(e1)) {
			i = 0;
		} else if (a.equals(e2)) {
			i = 1;
		} else if (a.equals(e3)) {
			i = 2;
		} else if (a.equals(e4)) {
			i = 3;
		}
		Intent b = new Intent(getApplicationContext(), CommentActivityServer.class);
		b.putExtra("event", e[i]);
		b.putExtra("email", email);
		startActivity(b);
	}

	public void clicked(View v) {
		Button a = (Button) v;
		if (a.equals(b1)) {
			e[0].toggleAttend();
		} else if (a.equals(b2)) {
			e[1].toggleAttend();
		} else if (a.equals(b3)) {
			e[2].toggleAttend();
		} else if (a.equals(b4)) {
			e[3].toggleAttend();
		}
		if (a.getText().equals("CONFIRMED")) {
			a.setText("CONFIRM\nATTENDANCE");
			a.setTextColor(Color.BLACK);
			((GradientDrawable) a.getBackground()).setColor(Color.parseColor("#00FF00"));
		} else {
			a.setText("CONFIRMED");
			((GradientDrawable) a.getBackground()).setColor(Color.parseColor("#FF123A14"));
			a.setTextColor(Color.WHITE);
		}
	}
}
