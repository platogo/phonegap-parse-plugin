package org.apache.cordova.core;

import android.content.Context;

import com.parse.ParseInstallation;
import com.parse.ParsePush;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ParsePlugin extends CordovaPlugin {
    public static final String ACTION_INITIALIZE = "initialize";
    public static final String ACTION_SET_INSTALLATION_DATA = "setInstallationData";
    public static final String ACTION_GET_INSTALLATION_ID = "getInstallationId";
    public static final String ACTION_GET_INSTALLATION_OBJECT_ID = "getInstallationObjectId";
    public static final String ACTION_GET_SUBSCRIPTIONS = "getSubscriptions";
    public static final String ACTION_SUBSCRIBE = "subscribe";
    public static final String ACTION_UNSUBSCRIBE = "unsubscribe";

    private static CordovaWebView webView = null;
    private static Boolean initialized = false;
    protected static Context context = null;
    private static ArrayList<String> eventQueue = new ArrayList<String>();

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals(ACTION_INITIALIZE)) {
            this.initialize(callbackContext);
            return true;
        }
        if (action.equals(ACTION_SET_INSTALLATION_DATA)) {
            this.setInstallationData(args.getInt(0), args.getString(1), args.getString(2), args.getString(3), callbackContext);
            return true;
        }
        if (action.equals(ACTION_GET_INSTALLATION_ID)) {
            this.getInstallationId(callbackContext);
            return true;
        }
        if (action.equals(ACTION_GET_INSTALLATION_OBJECT_ID)) {
            this.getInstallationObjectId(callbackContext);
            return true;
        }
        if (action.equals(ACTION_GET_SUBSCRIPTIONS)) {
            this.getSubscriptions(callbackContext);
            return true;
        }
        if (action.equals(ACTION_SUBSCRIBE)) {
            this.subscribe(args.getString(0), callbackContext);
            return true;
        }
        if (action.equals(ACTION_UNSUBSCRIBE)) {
            this.unsubscribe(args.getString(0), callbackContext);
            return true;
        }
        return false;
    }

    public static void triggerEvent(String json) {
        String js = "setTimeout('parsePlugin.onopen(" + json + ")',0)";
        if (initialized == false) {
            eventQueue.add(js);
        } else {
            webView.sendJavascript(js);
        }
    }

    private void initialize(final CallbackContext callbackContext) {
        ParsePlugin.webView = super.webView;
        ParsePlugin.context = super.cordova.getActivity().getApplicationContext();

        initialized = true;

        for (String js : eventQueue) {
            webView.sendJavascript(js);
        }
        eventQueue.clear();

        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                ParseInstallation currentInstallation = ParseInstallation.getCurrentInstallation();
                currentInstallation.saveEventually();
                callbackContext.success();
            }
        });
    }

    private void setInstallationData(final int uniqueId, final String language, final String country, final CallbackContext callbackContext, final String gcmSenderId) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                ParseInstallation currentInstallation = ParseInstallation.getCurrentInstallation();
                currentInstallation.put("GCMSenderId", gcmSenderId);
                currentInstallation.put("uniqueId", uniqueId);
                currentInstallation.put("language", language);
                currentInstallation.put("country", country);
                currentInstallation.saveEventually();
                callbackContext.success();
            }
        });
    }

    private void getInstallationId(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                String installationId = ParseInstallation.getCurrentInstallation().getInstallationId();
                callbackContext.success(installationId);
            }
        });
    }

    private void getInstallationObjectId(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                String objectId = ParseInstallation.getCurrentInstallation().getObjectId();
                callbackContext.success(objectId);
            }
        });
    }

    private void getSubscriptions(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                List<String> subscriptions = ParseInstallation.getCurrentInstallation().getList("channels");
                JSONArray subscriptionsJson = (subscriptions == null) ? new JSONArray() : new JSONArray(subscriptions);
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, subscriptionsJson));
            }
        });
    }

    private void subscribe(final String channel, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                ParsePush.subscribeInBackground(channel);
                callbackContext.success();
            }
        });
    }

    private void unsubscribe(final String channel, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                ParsePush.unsubscribeInBackground(channel);
                callbackContext.success();
            }
        });
    }
}
