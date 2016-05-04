package com.example.daniyalnawaz.easeapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Adapter.NotificationAdapter;
import Config.AppConfig;
import Helper.ConnectionDetector;
import Helper.Constant;
import Helper.ServiceHandler;
import ModelClasses.Notification;


public class NotificationActivity extends ActionBarActivity {

    //TextView txt_message;
    //TextView link_login;
    ListView listNotification;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    List<Notification> list;
    NotificationAdapter adapter;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        //txt_message = (TextView) findViewById(R.id.txt_notification);
        //link_login = (TextView) findViewById(R.id.link_sign_in);
        listNotification = (ListView) findViewById(R.id.list_notification);

        //String str = getIntent().getStringExtra("msg");
        list = new ArrayList<>();
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {Log.i("Registration ID",Constant.REG_ID);
            new getNotification().execute(Constant.REG_ID);
        }
        else{
            finish();
        }

//        if(checkPlayServices()){
//            if(str != null){
//                txt_message.setText(str);
//            }
//            else{
//                finish();
//            }
//        }

//        link_login.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
    }

    String response;
    List<NameValuePair> nameValuePairs;
    boolean isMessageAvailable = false;
    private ProgressDialog pDialog;

    private class getNotification extends AsyncTask<String,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(NotificationActivity.this);
            pDialog.setMessage("Loading ...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            ServiceHandler sh = new ServiceHandler();
            String msg="";
            try {
                    nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("regid",params[0]));
                    //nameValuePairs.add(new BasicNameValuePair("image",encoded));

                    response = sh.makeServiceCall(AppConfig.URL_NOTIFICATION, ServiceHandler.POST,nameValuePairs);
                    Log.d("Response: ", "--> " + response);

                    if (response != null) {
                        if(response.contains("No Notification Available")){
                            isMessageAvailable = false;
                        }
                        else{
                            isMessageAvailable = true;

                            JSONArray jArray = new JSONArray(response);

                            for(int i=0;i<jArray.length();i++){
                                JSONObject c = jArray.getJSONObject(i);
                                Notification notification = new Notification();
                                notification.setMessage(c.getString("message"));
                                notification.setDateTime(c.getString("notif_time"));

                                list.add(notification);
                                Log.i("Size",list.size()+"");
                            }
                        }
                    }


            } catch (Exception ex) {
                msg = "Error :" + ex.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pDialog.isShowing())
                pDialog.dismiss();
            Log.i("onPostExecute",isMessageAvailable+"");
            if(isMessageAvailable){
                adapter = new NotificationAdapter(NotificationActivity.this,list);
                listNotification.setAdapter(adapter);
                Log.i("onPostExecute IF",isMessageAvailable+"");
            }
            else{
                Log.i("onPostExecute Else",isMessageAvailable+"");
                Toast.makeText(getApplicationContext(),
                        "Your Request has been denied from Server", Toast.LENGTH_SHORT).show();
                finish();
            }


        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        // When Play services not found in device
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                // Show Error dialog to install Play services
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "This device doesn't support Play services, App will not work normally",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "This device supports Play services, App will work normally",
                    Toast.LENGTH_LONG).show();
        }
        return true;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notification, menu);
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
}
