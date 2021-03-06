/* Author - Aykut Asil(@aykuttasil) */
package com.aykuttasil.sweetloc.service

import com.onesignal.OSNotification
import com.onesignal.OneSignal
import com.orhanobut.logger.Logger

class NotificationReceivedHandler : OneSignal.NotificationReceivedHandler {

    override fun notificationReceived(notification: OSNotification) {
        Logger.json(notification.toJSONObject().toString())

        /*
        try {
            Logger.json(notification.toJSONObject().toString());

            JSONObject data = notification.payload.additionalData;

            String customKey;

            if (data != null) {

                Logger.json(data.toString());

                String action = data.getString(Const.ACTION);
                switch (action) {
                    case Const.ACTION_KONUM_YOLLA: {
                        break;
                    }
                    default: {

                    }
                }

                customKey = data.optString("customkey", null);
                if (customKey != null) {
                    Logger.i("customkey set with value: " + customKey);
                }

            }
        } catch (JSONException e) {
            Logger.e(e, "HATA");
        }
        */
    }
}
