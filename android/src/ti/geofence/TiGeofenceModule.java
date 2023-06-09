/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * TiDev Titanium Mobile
 * Copyright TiDev, Inc. 04/07/2022-Present
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package ti.geofence;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;

import java.util.Objects;

@Kroll.module(name = "TiGeofence", id = "ti.geofence")
public class TiGeofenceModule extends KrollModule {

    @Kroll.constant
    public static final int TRANSITION_ENTER = Geofence.GEOFENCE_TRANSITION_ENTER;
    @Kroll.constant
    public static final int TRANSITION_EXIT = Geofence.GEOFENCE_TRANSITION_EXIT;
    // Standard Debugging variables
    private static final String LCAT = "TiGeofenceModule";
    static GeofencingClient geofencingClient;
    private final IntentFilter mIntentFilter;
    private final GeofenceLocalBroadcastReceiver mBroadcastReceiver;
    Geofence geofence;
    PendingIntent geofencePendingIntent;

    public TiGeofenceModule() {
        super();

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("transition");
        mBroadcastReceiver = new GeofenceLocalBroadcastReceiver();
        LocalBroadcastManager.getInstance(TiApplication.getAppRootOrCurrentActivity())
                .registerReceiver(mBroadcastReceiver, mIntentFilter);
    }

    @Kroll.onAppCreate
    public static void onAppCreate(TiApplication app) {
        Log.d(LCAT, "inside onAppCreate");
        geofencingClient = LocationServices.getGeofencingClient(app);
    }

    @Kroll.method
    public void addGeofence(KrollDict data) {
        geofence = new Geofence.Builder().setRequestId(data.getString("name"))
                .setCircularRegion(data.getDouble("lat"), data.getDouble("lon"), data.getInt("radius"))
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT).build();
    }

    @Kroll.method
    public void removeGeofences() {
        geofencingClient.removeGeofences(getGeofencePendingIntent());
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geofence);
        return builder.build();
    }


    @SuppressLint("MissingPermission")
    @Kroll.method
    public void startWatching() {
        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(TiApplication.getAppCurrentActivity(), aVoid -> {
                    Log.d(LCAT, "added");
                })
                .addOnFailureListener(TiApplication.getAppCurrentActivity(), e -> Log.i(LCAT, "error" + e.getMessage()));
    }


    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }

        Intent intent = new Intent(TiApplication.getAppCurrentActivity(), GeofenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(TiApplication.getAppCurrentActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        return geofencePendingIntent;
    }

    public class GeofenceLocalBroadcastReceiver extends BroadcastReceiver {
        /*
         * Define the required method for broadcast receivers
         * This method is invoked when a broadcast Intent triggers the receiver
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            KrollDict kd = new KrollDict();

            String action = intent.getAction();
            if (action.equals("transition")) {
                kd.put("name", intent.getStringExtra("name"));
                kd.put("transition", intent.getIntExtra("transition", -1));
                fireEvent("transition", kd);
            }
        }
    }
}

