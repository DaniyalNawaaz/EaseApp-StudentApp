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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Config.AppConfig;
import Config.AppController;
import Helper.ConnectionDetector;
import Helper.Constant;
import Helper.SQLiteHandler;
import Helper.ServiceHandler;
import Helper.SessionManager;
import ModelClasses.Student;


public class MainActivity extends ActionBarActivity {
    private Button btnLogin;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private TextView signupLink;
    List<Student> listStd;

    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        signupLink = (TextView) findViewById(R.id.link_signup);

        db = new SQLiteHandler(getApplicationContext());

        session = new SessionManager(getApplicationContext());

        cd = new ConnectionDetector(getApplicationContext());

        if (session.isLoggedIn()) {
            Toast.makeText(MainActivity.this,"You are already Login",Toast.LENGTH_SHORT).show();
            listStd = db.getStudent();
            Log.i("DB List Size",listStd.size()+"");
            if(listStd.size()==1){
                Student std = listStd.get(0);
                Constant.REG_ID = std.getRegid();
                Log.i("Registration ID",Constant.REG_ID);
                Intent i = new Intent(MainActivity.this,DashboardActivity.class);
                startActivity(i);
                finish();
            }
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                isInternetPresent = cd.isConnectingToInternet();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (!email.isEmpty() && !password.isEmpty()) {
                    if (isInternetPresent) {
                        db.deleteStudents();
                        checkLogin(email, password);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),
                                "Internet Connection is not Available", Toast.LENGTH_LONG)
                                .show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private void checkLogin(final String email, final String password) {
        nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("email",email));
        nameValuePairs.add(new BasicNameValuePair("password",password));
        new retrievedata().execute();
    }


    List<NameValuePair> nameValuePairs;
    String response = null;
    boolean login = false;
    String serverResponse="";

    private class retrievedata extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Logging in ...");
            pDialog.setCancelable(false);
            pDialog.show();
        }



        @Override
        protected Void doInBackground(Void... params) {
            ServiceHandler sh = new ServiceHandler();

            response = sh.makeServiceCall(AppConfig.URL_LOGIN, ServiceHandler.POST,nameValuePairs);
            Log.d("Response: ", "--> " + response);


            if (response != null) {
                if(response.contains("Unauthorized Student")){
                    serverResponse="Invalid email & password";
                }
                else{
                    try{
                        JSONObject obj = new JSONObject(response);
                        Student student = new Student();
                        student.setRegid(obj.getString("regid"));
                        student.setFirstname(obj.getString("firstname"));
                        student.setLastname(obj.getString("lastname"));
                        student.setEmail(obj.getString("email"));
                        student.setContact(obj.getString("contact"));
                        student.setPassword(obj.getString("password"));
                        student.setAccountStatus(obj.getString("acount_status"));

                        if(student.getAccountStatus().equals("Block")){
                            serverResponse="Your Account Approval is in Under Process Block\n" +
                                    "Please Wait for the Confirmation";
                        }
                        else{
                            db.addStudent(student);
                            session.setLogin(true);
                            login = true;
                            Constant.REG_ID = student.getRegid();
                            Log.i("Registration ID",Constant.REG_ID);
                            Intent i = new Intent(MainActivity.this,DashboardActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }

                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pDialog.isShowing())
                pDialog.dismiss();

            if(login){
                Toast.makeText(MainActivity.this,"Successfully Login",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(MainActivity.this,serverResponse,Toast.LENGTH_SHORT).show();
            }

        }
    }
}
