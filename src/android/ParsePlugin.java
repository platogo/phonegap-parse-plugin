package org.apache.cordova.core;

import java.util.List;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.PushService;
import com.parse.SaveCallback;
import com.parse.ParseException;

import android.provider.Settings.Secure;

// TODO
// import com.parse.ParsePushBroadcastReceiver;

public class ParsePlugin extends CordovaPlugin {
    public static final String ACTION_INITIALIZE = "initialize";
    public static final String ACTION_GET_INSTALLATION_ID = "getInstallationId";
    public static final String ACTION_GET_INSTALLATION_OBJECT_ID = "getInstallationObjectId";
    public static final String ACTION_GET_SUBSCRIPTIONS = "getSubscriptions";
    public static final String ACTION_SUBSCRIBE = "subscribe";
    public static final String ACTION_UNSUBSCRIBE = "unsubscribe";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals(ACTION_INITIALIZE)) {
            this.initialize(callbackContext, args);
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

    private void initialize(final CallbackContext callbackContext, final JSONArray args) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    String appId = args.getString(0);
                    String clientKey = args.getString(1);
                    Parse.initialize(cordova.getActivity(), appId, clientKey);
                    PushService.setDefaultPushCallback(cordova.getActivity(), cordova.getActivity().getClass());

                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                    String androidId = Secure.getString(cordova.getActivity().getContentResolver(), Secure.ANDROID_ID);
                    installation.put("UniqueId", androidId);
                    installation.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            // TODO replace with ParsePushBroadcastReceiver
                            // PushService.setDefaultPushCallback(cordova.getActivity(), cordova.getActivity().getClass());
                            if (e == null) {
                                callbackContext.success();
                            } else {
                                callbackContext.error(e.toString());
                            }
                        }
                    });
                } catch (JSONException e) {
                    callbackContext.error("JSONException");
                }
            }
        });
    }

    private SaveCallback createSaveCallback(final CallbackContext callbackContext) {
        return new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    callbackContext.success();
                } else {
                    callbackContext.error(e.toString());
                }
            }
        };
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
                ParsePush.subscribeInBackground(channel, createSaveCallback(callbackContext));
                callbackContext.success();
            }
        });
    }

    private void unsubscribe(final String channel, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                ParsePush.unsubscribeInBackground(channel, createSaveCallback(callbackContext));
                callbackContext.success();
            }
        });
    }

}
