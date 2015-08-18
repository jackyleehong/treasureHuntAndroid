/*
package com.example.jaysonlim.beacondemoapp;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Nearable;
import com.estimote.sdk.Region;

import java.util.List;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.OutputStream;

import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.os.*;
import android.app.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import android.content.Intent;


public class ShowContentActivity extends Activity {


    String strWebServiceReturnResult;
    private static final String TAG = ShowContentActivity.class.getSimpleName();
    ProgressDialog progress;

    int intGetMajor=0;
    int intGetMinor=0;
    String strGetUUID=null;


    private static NotificationManager notificationManager;
    private static final int NOTIFICATION_ID = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_content);

        Log.w("ShowContentActivity", "onCreate");


        notificationManager=(NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            strGetUUID= extras.getString("uuid");
            intGetMajor = extras.getInt("major");
            intGetMinor= extras.getInt("minor");

        }
        Log.w("ShowContentActivity", "Major is "+intGetMajor);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        progress = new ProgressDialog(this);

        try {
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.show();

            postData();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }




    }

    public void postData() {

        //
        String POST_PARAMS = "uuid=" + strGetUUID + "&major=" + intGetMajor +"&minor="+ intGetMinor;
        //String POST_PARAMS = "param1=abc";
        URL obj = null;
        HttpURLConnection con = null;
        try {
            obj = new URL("http://localhost/GetData.php");
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");

            // For POST only - BEGIN
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write(POST_PARAMS.getBytes());
            os.flush();
            os.close();
            // For POST only - END

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

                strWebServiceReturnResult=response.toString();
                // print result
                Log.i(TAG, "strWebServiceReturnResult is "+strWebServiceReturnResult);

                JSONArray jsonArray = new JSONArray(strWebServiceReturnResult);

                //Log.i(TAG, "jsonArray  is " + jsonArray);

                String strGetImageUrlFromJson=null;
                String strDiscount=null;
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject json_data = jsonArray.getJSONObject(i);
                    strGetImageUrlFromJson =  json_data.getString("Image_URL");
                    // .. get all value here
                    strDiscount=json_data.getString("Message");
                    Log.i("Image_URL", strGetImageUrlFromJson);
                }
                postNotificationIntent("Beacon App Demo",
                        strDiscount, new Intent(this,MainActivity.class));


                String strFullImageURL="http://www.snapxy.com/beaconTest/"+strGetImageUrlFromJson;
                Log.i("Image_URL out is loop", strGetImageUrlFromJson);
                DownloadImageFromPath(strFullImageURL);

            } else {
                Log.i(TAG, "POST request did not work.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void DownloadImageFromPath(String path){
        InputStream in =null;
        Bitmap bmp=null;
        int responseCode = -1;
        try{

            URL url = new URL(path);//"http://192.xx.xx.xx/mypath/img1.jpg
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setDoInput(true);
            con.connect();
            responseCode = con.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK)
            {
                //download
                in = con.getInputStream();
                bmp = BitmapFactory.decodeStream(in);
                in.close();
               // iv.setImageBitmap(bmp);

                LinearLayout linearLayout= new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                linearLayout.setLayoutParams(new LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT));

                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(new LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT));

                //adding view to layout
                linearLayout.addView(imageView);
                //make visible to program
                setContentView(linearLayout);

                imageView.setImageBitmap(bmp);

                progress.dismiss();
            }

        }
        catch(Exception ex) {
            Log.e("Exception", ex.toString());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    // Pops a notification in the task bar
    public  void postNotificationIntent(String title, String msg, Intent i) {
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(
                this, 0, new Intent[] { i },
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.dot).setContentTitle(title)
                .setContentText(msg).setAutoCancel(true)
                .setContentIntent(pendingIntent).build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

}
*/
