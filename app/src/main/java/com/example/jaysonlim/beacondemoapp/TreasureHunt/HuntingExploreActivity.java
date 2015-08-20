package com.example.jaysonlim.beacondemoapp.TreasureHunt;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import android.media.Image;
import android.os.RemoteException;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.example.jaysonlim.beacondemoapp.R;
import com.example.jaysonlim.beacondemoapp.adapters.BeaconListAdapter;
import com.google.android.gms.maps.model.UrlTileProvider;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class HuntingExploreActivity extends Activity {

    private static final String TAG = HuntingExploreActivity.class.getSimpleName();
    private static final double RELATIVE_START_POS = 320.0 / 1110.0;
    private static final double RELATIVE_STOP_POS = 885.0 / 1110.0;
    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);
    private BeaconManager beaconManager;
    int count = 0;
    double[] oriPos = new double[100000];
    private ImageView[] dotView;
    private int startY = -1;
    private int segmentLength = -1;
    private static final int REQUEST_ENABLE_BT = 1234;
    private ArrayList<Beacon> beaconsList;
   int limit = 0, limit2= 0, limit3= 0,limit4 =0, limit5= 0, limit6=0;
    String strWebServiceReturnResult;
    HashSet<Integer> detected ;
    ArrayList<Beacon> tempListForBL, tempListForBs;
   ArrayList<Beacon> bs ;
    int retrievedMajor = 0;
    int called= 1;
    SharedPreferences sp ;
    FrameLayout fl ;
    static Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_hunting_explore);
        beaconsList = new ArrayList<>();
        beaconManager = new BeaconManager(this);

        detected = new HashSet<Integer>();
        tempListForBL = new ArrayList<>();
        tempListForBs = new ArrayList<>();
        bs = new ArrayList<Beacon>();
        dotView = new ImageView[100000];
        fl = (FrameLayout) findViewById(R.id.frame);

        dialog = new Dialog(HuntingExploreActivity.this);
        dialog.setContentView(R.layout.activity_claim_beacon);
        dialog.setTitle("Beacon Found");
    }

    protected void onStart() {
        super.onStart();
        // Check if device supports Bluetooth Low Energy.
        if (!beaconManager.hasBluetooth()) {
            Toast.makeText(this, "Device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show();
            return;
        }

        // If Bluetooth is not enabled, let user enable it.
        if (!beaconManager.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {

          //  beaconManager.setBackgroundScanPeriod(500, 3000);
          //  beaconManager.setForegroundScanPeriod(700, 500);
            connectToService();
            beaconDiscoveredAction();


        }
    }
    @Override
    protected void onStop() {
        try {
            beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
        } catch (RemoteException e) {
            Log.d(TAG, "Error while stopping ranging", e);
        }

        super.onStop();

        Log.i(TAG, "On Stop .....");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
               // beaconManager.setBackgroundScanPeriod(500, 3000);
               // beaconManager.setForegroundScanPeriod(700, 500);
                connectToService();
                beaconDiscoveredAction();
            } else {
                Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_LONG).show();
                getActionBar().setSubtitle("Bluetooth not enabled");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    String temp="";
    private void connectToService() {
                getActionBar().setSubtitle("Scanning...");
                // adapter.replaceWith(Collections.<Beacon>emptyList());
                beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                    @Override
                    public void onServiceReady() {
                        try {
                            beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
                        } catch (RemoteException e) {
                            Log.e(TAG, "Cannot start ranging", e);
                        }
                    }
                });
    }
    public void beaconDiscoveredAction(){

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
               final double[] newPos = new double[100000];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        getActionBar().setSubtitle("Found beacons: " + beacons.size());

                        //remove beacon on bs.
                        Iterator<Beacon> removeBeaconbs = bs.iterator();
                        //  tempListForBs = bs;
                        while(removeBeaconbs.hasNext()){
                            Beacon b = removeBeaconbs.next();
                            if(!beacons.contains(b) ){
                                removeBeaconbs.remove();
                            }else
                            if (oriPos != newPos) {
                                removeBeaconbs.remove();
                            }else{
                                continue;
                            }

                        }
                        //fixed list for used to compare whether th beacon is out of range.
                        for (Beacon rangedbeacon : beacons) {
                            //add beacon to hash set to avoid duplicate
                            if (!beaconsList.contains(rangedbeacon)) {
                                beaconsList.add(rangedbeacon);
                                // dotView = new ImageView(HuntingExploreActivity.this);
                                Log.d("added beacon", rangedbeacon.getMajor() + "");
                            }
                        }

                    }

                });
                Log.d("destination", "I reached here!!!");

                //Create and update the the position of dotView.
                for(Beacon beacon: beacons) {


                    if (!bs.contains(beacon)) {
                        bs.add(beacon);
                    }else{
                        continue;
                    }

                }
                //Remove the dotView which are not inside the list of beacons.
                for(Beacon bc: bs){
                    if(beacons.contains(bc)) {
                        newPos[bc.getMajor()] = computeDotPosY(bc);
                        if (oriPos != newPos) {
                            fl.removeView(dotView[bc.getMajor()]);
                            Log.d("OriPos vs NewPos", "OriPOS " + oriPos + " vs NewPos " + newPos);
                            dotView[bc.getMajor()] = new ImageView(HuntingExploreActivity.this);// dotView created here....
                            Log.d("new DotView", "New dotView Created");
                            findBeaconLocation(bc, dotView[bc.getMajor()]);
                            Log.d("added the dotview", "updated beacon dotview major" + bc.getMajor());
                        }else  {
                            continue;
                        }
                    }

                }

                for(Beacon bc : beaconsList) {
                    if (!beacons.contains(bc)) {
                        fl.removeView(dotView[bc.getMajor()]);
                        Log.d("RemoveView", "View Removed");
                    }
                }


                //remove beacon on beaconList.
                Iterator<Beacon> removeBeaconL = beaconsList.iterator();
              //  tempListForBL = beaconsList;
                while(removeBeaconL.hasNext()) {
                    Beacon b = removeBeaconL.next();
                    if (!beacons.contains(b)) {
                        removeBeaconL.remove();
                    }

                }


            }

        });

            /* for (Beacon rangedbeacon : beaconsList) {

                                                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                                StrictMode.setThreadPolicy(policy);
                                                String uuid = rangedbeacon.getProximityUUID();
                                                int major = rangedbeacon.getMajor();
                                                int minor = rangedbeacon.getMinor();

                                                String POST_PARAMS = "uuid=" + uuid + "&major=" + major + "&minor=" + minor;
                                                //String POST_PARAMS = "param1=abc";
                                                URL obj = null;
                                                HttpURLConnection con = null;
                                                try {
                                                    obj = new URL("http://snapxy.com/beaconTest/GetRandomBeacon.php");
                                                    Log.d("Sent URL", "Successful");
                                                    con = (HttpURLConnection) obj.openConnection();
                                                    con.setRequestMethod("POST");
                                                    Log.d("Posted", "Successful");
                                                    // For POST only - BEGIN
                                                    con.setDoOutput(true);
                                                    Log.d("OutputSet", "Successful");
                                                    OutputStream os = con.getOutputStream();
                                                    Log.d("gotOutputStream", "Successful");
                                                    os.write(POST_PARAMS.getBytes());
                                                    Log.d("wrote", "Successful");
                                                    os.flush();
                                                    os.close();
                                                    Log.d("success", "Success");

                                                    int responseCode = con.getResponseCode();
                                                    Log.i(TAG, "POST Response Code :: " + responseCode);

                                                    if (responseCode == HttpURLConnection.HTTP_OK) { //success
                                                        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                                                        String inputLine;
                                                        StringBuffer response = new StringBuffer();

                                                        while ((inputLine = in.readLine()) != null) {
                                                            response.append(inputLine);
                                                        }
                                                        in.close();

                                                        strWebServiceReturnResult = response.toString();
                                                        // print result
                                                        Log.i(TAG, "strWebServiceReturnResult is " + strWebServiceReturnResult);
                                                        JSONArray jsonArray = new JSONArray(strWebServiceReturnResult);

                                                        //Log.i(TAG, "jsonArray  is " + jsonArray);


                                                        String strDiscount = null;
                                                        for (int k = 0; k < jsonArray.length(); k++) {
                                                            JSONObject json_data = jsonArray.getJSONObject(k);
                                                            retrievedMajor = json_data.getInt("major");
                                                            // .. get all value here
                                                            Log.i("major", retrievedMajor + "");
                                                        }
                                                    }
                                                    // For POST only - END

                                                    Random rnd = new Random();
                                                    final int greenVals = rnd.nextInt(256);
                                                    final int blueVals = rnd.nextInt(256);

                                                    if (rangedbeacon != null) {
                                                        //  updateDistanceView(foundBeacon[j]);
                                                        calculateDistance(rangedbeacon.getMeasuredPower(), rangedbeacon.getRssi());
                                                        findBeaconLocation(rangedbeacon);
                                                       // updateDistanceView(rangedbeacon);

                                                    }

                                                    HashSet<Integer> list = retrieveTempBeacon();
                                                   *//**//* Integer[] compare = new Integer[retrieveTempBeacon().size()];
                                                    retrieveTempBeacon().toArray(compare);*//**//*
                                                    if (retrievedMajor != 0 && !list.contains(retrievedMajor) && calculateDistance(rangedbeacon.getMeasuredPower(), rangedbeacon.getRssi()) <= 0.6) {
                                                        //Toast.makeText(HuntingExploreActivity.this, compare.length+ "vs" + retrievedMajor, Toast.LENGTH_SHORT).show();
                                                        // limit = 1;
                                                        if (count < 6) {


                                                            ImageView paperBoat = (ImageView) dialog.findViewById(R.id.paperBoats);
                                                            paperBoat.setColorFilter(Color.rgb(255, greenVals, blueVals));

                                                            TextView tw = (TextView) dialog.findViewById(R.id.beaconID);
                                                            tw.setText(retrievedMajor + "");
                                                            storeTempBeacon(retrievedMajor);
                                                            Button claimBtn = (Button) dialog.findViewById(R.id.claimBtn);


                                                            claimBtn.setOnClickListener(new View.OnClickListener() {

                                                                @Override
                                                                public void onClick(View v) {

                                                                    dialog.dismiss();

                                                                    updatePaperBoat(count, greenVals, blueVals);
                                                                    count++;

                                                                    // beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);

                                                                }
                                                            });

                                                        } else {
                                                            finishHunting();
                                                        }
                                                        dialog.show();

                                                    }

                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                    Log.d("failed", "failed");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                // count++;
                                            }*/


                           /* Beacon[] beacon = beaconsList.toArray(new Beacon[beaconsList.size()]);
                            for(int i = 0; i < beacon.length; i++) {
                                Beacon eachBeacon = beacon[i];
                                findBeaconLocation(eachBeacon);
                            }*/







    }


    public void findBeaconLocation( final Beacon beacon, final ImageView dotView) {

        final View view = findViewById(R.id.sonar);
        Log.d("sonar here", "sonar sonar !!!!");
        view.getViewTreeObserver().
                addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        startY = (int) (RELATIVE_START_POS * view.getMeasuredHeight());
                        int stopY = (int) (RELATIVE_STOP_POS * view.getMeasuredHeight());
                        segmentLength = stopY - startY;

                       // FrameLayout fl = (FrameLayout) findViewById(R.id.frame);
                        Log.d("FrameLayout", "Added frameLayout");
                        dotView.setImageResource(R.drawable.dot);
                        Log.d("AddedImageResource", "AddedImageResource");
                        dotView.setLayoutParams(new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.WRAP_CONTENT));
                        // dotView = findViewById(R.id.dot);
                        dotView.setVisibility(View.VISIBLE);
                        dotView.setTranslationY(computeDotPosY(beacon));
                        oriPos[beacon.getMajor()] = computeDotPosY(beacon);
                        fl.addView(dotView);
                        Log.d("added layoutView", "added dotView to layout");

                    }

                });
    }


    public void updatePaperBoat(int count, int green, int blue){
        List<ImageView> paperBoats = new ArrayList<ImageView>();
        paperBoats.add((ImageView) findViewById(R.id.paperBoats1));
        paperBoats.add((ImageView) findViewById(R.id.paperBoats2));
        paperBoats.add((ImageView) findViewById(R.id.paperBoats3));
        paperBoats.add((ImageView) findViewById(R.id.paperBoats4));
        paperBoats.add((ImageView) findViewById(R.id.paperBoats5));
        paperBoats.add((ImageView) findViewById(R.id.paperBoats6));

        ImageView[] arrPB = new ImageView[paperBoats.size()];
        paperBoats.toArray(arrPB);

        if (count < 6 ) {
            EditText et = (EditText)findViewById(R.id.counter);
            int counter = count + 1;
            et.setText("" + counter + "");
            arrPB[count].setColorFilter(Color.rgb(255, green, blue));

        }
    }

    public void finishHunting(){
        Intent last = new Intent(HuntingExploreActivity.this,FinishHuntingActivity.class);
        startActivity(last);
    }

    private void updateDistanceView(Beacon foundBeacon, ImageView dotView) {
        if (segmentLength == -1) {
            return;
        }
        dotView.animate().translationY(computeDotPosY(foundBeacon)).start();
    }

    private int computeDotPosY(Beacon beacon) {
        // Let's put dot at the end of the scale when it's further than 6m.
        double distance = Math.min(Utils.computeAccuracy(beacon), 6.0);
        return startY + (int) (segmentLength * (distance / 6.0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hunting_explore, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected static double calculateDistance(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine distance, return -1.
        }

        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return accuracy;
        }
    }

    public void storeTempBeacon(int beaconMajor){

        detected.add(beaconMajor);

    }

    public HashSet<Integer> retrieveTempBeacon(){
        for(int d :detected){
            Log.d("storedMAJOR", d + "");
        }

        return detected;
    }
    @Override
    protected void onDestroy() {
        beaconManager.disconnect();

        super.onDestroy();

        Log.w(TAG, "On onDestroy .....");

    }

}

