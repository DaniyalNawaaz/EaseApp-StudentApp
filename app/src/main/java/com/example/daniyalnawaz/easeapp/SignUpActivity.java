package com.example.daniyalnawaz.easeapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Config.AppConfig;
import Config.AppController;
import Helper.ConnectionDetector;
import Helper.SQLiteHandler;
import Helper.ServiceHandler;
import Helper.SessionManager;
import ModelClasses.Student;


public class SignUpActivity extends ActionBarActivity {
    private Button btnRegister;
    //private Button btnBrowseImage;
    //ImageView imgProfile;
    private TextView _loginLink;
    private EditText inputRegId;
    private EditText inputFirstName;
    private EditText inputLastName;
    private EditText inputEmail;
    private EditText inputContact;
    private EditText inputPassword;
    private EditText inputConfirmPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    Bitmap bitmap;

    //private static int PICK_IMAGE_REQUEST = 1;

    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    GoogleCloudMessaging gcmObj;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        inputRegId = (EditText) findViewById(R.id.reg_id);
        inputFirstName = (EditText) findViewById(R.id.first_name);
        inputLastName = (EditText) findViewById(R.id.last_name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputContact = (EditText) findViewById(R.id.contact);
        inputPassword = (EditText) findViewById(R.id.password);
        inputConfirmPassword = (EditText) findViewById(R.id.confirm_password);
        btnRegister = (Button) findViewById(R.id.btn_signup);
        //btnBrowseImage = (Button) findViewById(R.id.btn_browse_image);
        _loginLink = (TextView) findViewById(R.id.link_login);
        //imgProfile = (ImageView) findViewById(R.id.image_profile);

        session = new SessionManager(getApplicationContext());

        cd = new ConnectionDetector(getApplicationContext());

        db = new SQLiteHandler(getApplicationContext());

        if (session.isLoggedIn()) {
            Toast.makeText(SignUpActivity.this, "You are already Login", Toast.LENGTH_SHORT).show();
            finish();
        }

//        btnBrowseImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//            }
//        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                isInternetPresent = cd.isConnectingToInternet();

                String regid = inputRegId.getText().toString().trim();
                String firstname = inputFirstName.getText().toString().trim();
                String lastname = inputLastName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String contact = inputContact.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String confirmpassword = inputConfirmPassword.getText().toString().trim();


                if (!regid.isEmpty() &&  !firstname.isEmpty() && !lastname.isEmpty()
                        && !email.isEmpty() && !contact.isEmpty() &&!password.isEmpty() && !confirmpassword.isEmpty()) {

                    if(password.equals(confirmpassword)){
                        if (isInternetPresent) {
                            if(checkPlayServices()){
                                signUp(regid, firstname, lastname, email, contact, password);
                            }
                            else{
                                Toast.makeText(getApplicationContext(),
                                       "Google Play Service is not Available", Toast.LENGTH_LONG)
                                       .show();
                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(),
                                    "Internet Connection is not Available", Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                    else{
                        Toast.makeText(SignUpActivity.this,"password feilds must be same",Toast.LENGTH_SHORT).show();
                        inputPassword.setText("");
                        inputConfirmPassword.setText("");
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//
//            Uri uri = data.getData();
//
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                imgProfile.setImageBitmap(bitmap);
//
//                /*byte[] decodedString = Base64.decode(encoded, Base64.DEFAULT);
//                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//
//                ImageView imageView1 = (ImageView) findViewById(R.id.imageView1);
//                imageView1.setImageBitmap(decodedByte);*/
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        else{
//            bitmap = MediaStore.Images.Media.getBitmap(R.drawable.profile_picture);
//            imgProfile.setImageBitmap(R.drawable.profile_picture);
//        }
//    }

    private boolean checkPlayServices(){
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                GooglePlayServicesUtil.getErrorDialog(resultCode,this,PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else {
                Log.i("Check Play Service", "This device is not supported");
                finish();
            }
            return false;
        }
        return true;
    }
//String image = "iVBORw0KGgoAAAANSUhEUgAAAlkAAAHcCAIAAABqDyjmAAAAA3NCSVQFBgUzC42AAAAgAElEQVR4nKx93Y70OI7l+ZR0FFUTuYgA+qIL2L3YR5j3f5i5GWAWmAYydjNQ5kQSwl7w1xGZPT3Yjf7gdjltWZYoiYeHIn9d6J9BgIKZRYXBosIEUfh1qitMEMSPGCp5FBUmhordf7wOaN4PKAD4/UDeIwImv57vymdFbkwsALe3+7sAAP29THz7pkwBMSCctYUAbPXBy3sFwsQ38e9C3t9K+OaIYzneWCwQRrRhf5f88Kz+cA5hL+3YL1Lf3sqxFssrdbyJMEXdshwAhCjz52+M45X4I/p9Eu/Z7/DWA2Sirk/mj5QxqxWzQK7EHyJMl5SKibnrPmnu8vEkadkXk3gHJtmd2FWmlwMRYfBNbxcrjS8gMJrsASY5O2QSg+x7AYCZL+crSOb75fIX/p///X/yGXNO6AeIPz5v1/cLCB";

    private void signUp(final String regid, final String firstname,
                              final String lastname, final String email,
                              final String contact, final String password) {
        new registerUser().execute(regid,firstname,lastname,email,contact,password);
    }

    String response;
    List<NameValuePair> nameValuePairs;
    boolean registered = false;
    String gcmid;


    private class registerUser extends AsyncTask<String,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(SignUpActivity.this);
            pDialog.setMessage("Registering ...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            ServiceHandler sh = new ServiceHandler();
            String msg="";
            try {
                if (gcmObj == null) {
                    gcmObj = GoogleCloudMessaging
                            .getInstance(getApplicationContext());
                }
                gcmid = gcmObj.
                        register(AppConfig.GOOGLE_PROJ_ID);
                if(gcmid!=null){
//                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                    byte[] byteArray = byteArrayOutputStream .toByteArray();
//
//                    String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
//                    Log.i("Image String Encoding", encoded);

                    nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("regid",params[0]));
                    nameValuePairs.add(new BasicNameValuePair("firstname",params[1]));
                    nameValuePairs.add(new BasicNameValuePair("lastname",params[2]));
                    nameValuePairs.add(new BasicNameValuePair("email",params[3]));
                    nameValuePairs.add(new BasicNameValuePair("contact",params[4]));
                    nameValuePairs.add(new BasicNameValuePair("password",params[5]));
                    nameValuePairs.add(new BasicNameValuePair("gcmid",gcmid));
                    //nameValuePairs.add(new BasicNameValuePair("image",encoded));

                    response = sh.makeServiceCall(AppConfig.URL_REGISTER, ServiceHandler.POST,nameValuePairs);
                    Log.d("Response: ", "--> " + response);

                    if (response != null) {
                        if(response.contains("Successfully Registered")){
                            registered = true;
                        }
                        else{
                            if(response.equals("Error Found")){
                                registered = false;
                            }
                        }
                    }
                }

            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pDialog.isShowing())
                pDialog.dismiss();

            if(registered){
                Toast.makeText(getApplicationContext(),
                        "Successfully registered. Please Wait for Confirmation", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(),
                        "Your Request has been denied from Server", Toast.LENGTH_SHORT).show();
            }

            finish();
        }
    }

}
