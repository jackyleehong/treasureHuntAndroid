package com.example.jaysonlim.beacondemoapp.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.*;
import android.widget.*;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.example.jaysonlim.beacondemoapp.MainActivity;
import com.example.jaysonlim.beacondemoapp.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.*;
/**
 * Created by jaysonlim on 7/6/15.
 */
public class EstimoteManager {
    private static final int NOTIFICATION_ID = 123;
    private static BeaconManager beaconManager;
    private static NotificationManager notificationManager;
    public static final String EXTRAS_BEACON = "extrasBeacon";
    private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId",
            ESTIMOTE_PROXIMITY_UUID, null, null);

    private static Context currentContext;

    // Create everything we need to monitor the beacons
    public static void Create(NotificationManager notificationMngr,
                              Context context, final Intent i) {
        try {
            notificationManager = notificationMngr;
            currentContext = context;

            // Create a beacon manager
            beaconManager = new BeaconManager(currentContext);

            // We want the beacons heartbeat to be set at one second.
            beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1),
                    0);
            beaconManager.setForegroundScanPeriod(700, 500);

            // Method called when a beacon gets...
            beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
                // ... close to us.
                @Override
                public void onEnteredRegion(Region region, List<Beacon> beacons) {
                  //  postNotificationIntent("Beacon App Demo",
                       //     "I have found an estimote !!!", i);

                    String uuid=null;
                    int major=0;
                    int minor=0;
                    for (int i = 0; i < beacons.size(); i++) {
                         uuid = beacons.get(i).getProximityUUID();
                         major = beacons.get(i).getMajor();
                         minor =beacons.get(i).getMinor();
                    }

                   /* Intent intent = new Intent(currentContext,ShowContentActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("uuid", uuid);
                    intent.putExtra("major", major);
                    intent.putExtra("minor", minor);

                    currentContext.startActivity(intent);*/

                }

                // ... far away from us.
                @Override
                public void onExitedRegion(Region region) {
                    postNotificationIntent("Beacon Demo App",
                            "You are leaving the beacon area", i);

                    Log.w("EstimoteManager", "I have lost my estimote !!!");


                }
            });

            // Connect to the beacon manager...
            beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                @Override
                public void onServiceReady() {
                    try {
                        // ... and start the monitoring
                        beaconManager.startMonitoring(ALL_ESTIMOTE_BEACONS);
                    } catch (Exception e) {
                    }
                }
            });
        } catch (Exception e) {
        }
    }

    // Pops a notification in the task bar
    public static void postNotificationIntent(String title, String msg, Intent i) {
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(
                currentContext, 0, new Intent[] { i },
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(currentContext)
                .setSmallIcon(R.drawable.dot).setContentTitle(title)
                .setContentText(msg).setAutoCancel(true)
                .setContentIntent(pendingIntent).build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    // Stop beacons monitoring, and closes the service
    public static void stop() {
        try {
            beaconManager.stopMonitoring(ALL_ESTIMOTE_BEACONS);
            beaconManager.disconnect();
        } catch (Exception e) {
        }
    }


}
