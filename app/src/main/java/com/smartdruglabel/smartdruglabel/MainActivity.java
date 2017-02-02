package com.smartdruglabel.smartdruglabel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import android.view.Window;

public class MainActivity extends Activity {
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //font Setting
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/supermarket.ttf");
        TextView myTextview = (TextView) findViewById(R.id.member_login);
        myTextview.setTypeface(myTypeface);

        //Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        final AlertDialog.Builder ad = new AlertDialog.Builder(this);

        //txtUsername & txtPassword
        final EditText txtUser = (EditText) findViewById(R.id.txtUsername);
        final EditText txtPass = (EditText) findViewById(R.id.txtPassword);
        txtUser.setTypeface(myTypeface);
        txtPass.setTypeface(myTypeface);

        //btnLogin
        final Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setTypeface(myTypeface);

        //Perform action on click
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String url = "http://202.58.126.48:8081/smartdruglabel/checkLogin.php";

                //Call Progress Dialog
                //ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this,
                //"Loading", "Please wait...");
                //progressDialog.show();

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("strUser", txtUser.getText().toString().trim()));
                params.add(new BasicNameValuePair("strPass", txtPass.getText().toString().trim()));

                String resultServer = getHttpPost(url, params);

                /*** Default Value ***/
                String strStatusID = "0";
                String strName = "";
                String strError = "กรุณาตรวจสอบเครือข่าย";

                JSONObject c;
                try {
                    c = new JSONObject(resultServer);
                    strStatusID = c.getString("StatusID");
                    strName = c.getString("Username");
                    strError = c.getString("Error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Prepare Login
                if (strStatusID.equals("0")) {
                    //Dialog
                    MyAlert myAlert = new MyAlert(MainActivity.this); //Call MyAlert Class
                    myAlert.myDialog(R.drawable.warn_icon, "ข้อผิดพลาด", strError);
                    //ad.setTitle("Error");
                    //ad.setIcon(android.R.drawable.btn_star_big_on);
                    //ad.setPositiveButton("Close", null);
                    //ad.setMessage(strError);
                    //ad.show();
                    txtUser.setText("");
                    txtPass.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "ยินดีต้อนรับ คุณ " + strName, Toast.LENGTH_SHORT).show();
                    //progressDialog.dismiss();
                    Intent mainmenu_screen = new Intent(getApplicationContext(), MainMenuActivity.class);
                    mainmenu_screen.putExtra("Username", strName);
                    startActivity(mainmenu_screen);
                    finish();
                }
            }
        });

        TextView register_tv = (TextView) findViewById(R.id.register);
        register_tv.setTypeface(myTypeface);
        register_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register_screen = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(register_screen);
            }
        });

        TextView forgorpwd_tv = (TextView) findViewById(R.id.forgot_passwd);
        forgorpwd_tv.setTypeface(myTypeface);
        forgorpwd_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotpwd_screen = new Intent(getApplicationContext(), ForgotPwdActivity.class);
                startActivity(forgotpwd_screen);
            }
        });

    }

    public String getHttpPost(String url, List<NameValuePair> params) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = client.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) { // Status OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                Log.e("Log", "Failed to download result..");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }
}