package com.scavenger.jk;

import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class GcmService extends GcmListenerService {

    public void onMessageReceived(String from, Bundle data) {
    	Log.v("GCM", "!!!!!!!!!!!!!!!!!!!");
        JSONObject jsonObject = new JSONObject();
        Set<String> keys = data.keySet();
        for (String key : keys) {
            try {
                jsonObject.put(key, data.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            sendNotification("Received: " + jsonObject.toString(5));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void onDeletedMessages() {
    	Log.v("GCM", "!!!!!!!!!!!!!!!!!!!");
        sendNotification("Deleted messages on server");
    }
    public void onMessageSent(String msgId) {
    	Log.v("GCM", "!!!!!!!!!!!!!!!!!!!");
        sendNotification("Upstream message sent. Id=" + msgId);
    }
    public void onSendError(String msgId, String error) {
    	Log.v("GCM", "!!!!!!!!!!!!!!!!!!!");
        sendNotification("Upstream message send error. Id=" + msgId + ", error" + error);
    }

    private void sendNotification(final String msg) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {                    
                if (CommentActivityServer.info != null) {
                	CommentActivityServer.info.setText(msg);
                }
            }
        });
    }
}