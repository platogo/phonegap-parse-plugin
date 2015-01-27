package org.apache.cordova.core.parseplugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.parse.ParseAnalytics;
import com.parse.ParsePushBroadcastReceiver;

import org.apache.cordova.core.ParsePlugin;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.*;

public class Receiver extends ParsePushBroadcastReceiver {

    @Override
    public void onPushOpen(Context context, final Intent intent) {
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(new Runnable() {
            public void run() {
                ParseAnalytics.trackAppOpenedInBackground(intent);
            }
        }, 5, SECONDS);

        String uriString = null;
        String jsonData = "";
        try {
            JSONObject pushData = new JSONObject(intent.getStringExtra("com.parse.Data"));
            jsonData = pushData.toString();
            uriString = pushData.optString("uri");
        } catch (JSONException e) {
            Log.v("com.parse.ParsePushReceiver", "Unexpected JSONException when receiving push data: ", e);
        }

        Intent activityIntent;
        if (uriString != null && !uriString.isEmpty()) {
            activityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
        } else {
            String packageName = context.getPackageName();
            activityIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        }
        activityIntent.putExtras(intent.getExtras());

        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(activityIntent);

        ParsePlugin.triggerEvent(jsonData);
    }
}
