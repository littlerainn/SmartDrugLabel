package com.smartdruglabel.smartdruglabel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.register_screen);

        //font Setting
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/supermarket.ttf");
        TextView myTextview = (TextView) findViewById(R.id.register);
        myTextview.setTypeface(myTypeface);

        final EditText txtUsername = (EditText) findViewById(R.id.username_edtext);
        final EditText txtEmail = (EditText) findViewById(R.id.email_edtext);
        final EditText txtPassword = (EditText) findViewById(R.id.passwd_edtext);
        final EditText txtConPassword = (EditText) findViewById(R.id.cnfpasswd_edtext);

        txtUsername.setTypeface(myTypeface);
        txtEmail.setTypeface(myTypeface);
        txtPassword.setTypeface(myTypeface);
        txtConPassword.setTypeface(myTypeface);

        // Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //btnSave
        final Button btnSave = (Button) findViewById(R.id.register_button);
        btnSave.setTypeface(myTypeface);
        //Perform action on click
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (SaveData()) {
                    //When Save Complete
                }
            }
        });

        TextView backbtn = (TextView) findViewById(R.id.member_login_tv);
        backbtn.setTypeface(myTypeface);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public boolean SaveData() {
        final EditText txtUsername = (EditText) findViewById(R.id.username_edtext);
        final EditText txtEmail = (EditText) findViewById(R.id.email_edtext);
        final EditText txtPassword = (EditText) findViewById(R.id.passwd_edtext);
        final EditText txtConPassword = (EditText) findViewById(R.id.cnfpasswd_edtext);

        //Dialog
        final AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle("ข้อผิดพลาด");
        ad.setIcon(R.drawable.warn_icon);
        ad.setPositiveButton("ตกลง", null);

        //Check Username
        if (txtUsername.getText().length() == 0) {
            ad.setMessage("กรุณาใส่ชื่อผู้ใช้งาน");
            ad.show();
            txtUsername.requestFocus();
            return false;
        }

        //Check Email
        if (txtEmail.getText().length() == 0) {
            ad.setMessage("กรุณาใส่อีเมล");
            ad.show();
            txtEmail.requestFocus();
            return false;
        }

        //Check Email Pattern
        String email = txtEmail.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (!email.matches(emailPattern)) {
            ad.setMessage("กรุณาตรวจสอบอีเมล");
            ad.show();
            txtEmail.requestFocus();
            return false;
        }

        //Check Password
        if (txtPassword.getText().length() == 0 || txtConPassword.getText().length() == 0) {
            ad.setMessage("กรุณาใส่รหัสผ่าน");
            ad.show();
            txtPassword.requestFocus();
            return false;
        }

        //Check Password and Confirm Password (Match)
        if (!txtPassword.getText().toString().equals(txtConPassword.getText().toString())) {
            ad.setMessage("กรุณาตรวจสอบรหัสผ่าน");
            ad.show();
            txtConPassword.requestFocus();
            return false;
        }

        String url = "http://202.58.126.48:8081/smartdruglabel/addRegister.php";
        /* String url = "http://www.kongtunmae-oncb.go.th/offer_hmf/smartdruglabel/addRegister.php"; */
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("sUsername", txtUsername.getText().toString()));
        params.add(new BasicNameValuePair("sPassword", txtPassword.getText().toString()));
        params.add(new BasicNameValuePair("sEmail", txtEmail.getText().toString()));

        /** Get result from Server (Return the JSON Code)
         * StatusID = ? [0=Failed,1=Complete]
         * Error	= ?	[On case error return custom error message]
         *
         * Eg Save Failed = {"StatusID":"0","Error":"Email Exists!"}
         * Eg Save Complete = {"StatusID":"1","Error":""}
         */

        String resultServer = getHttpPost(url, params);

        /*** Default Value ***/
        String strStatusID = "0";
        String strError = "กรุณาตรวจสอบเครือข่าย";

        JSONObject c;
        try {
            c = new JSONObject(resultServer);
            strStatusID = c.getString("StatusID");
            strError = c.getString("Error");
        } catch (JSONException e) {
            //TODO Auto-generated catch block
            e.printStackTrace();
        }

        //Prepare Save Data
        if (strStatusID.equals("0")) {
            ad.setMessage(strError);
            ad.show();
        } else {
            Toast.makeText(RegisterActivity.this, "ลงทะเบียนสำเร็จ", Toast.LENGTH_SHORT).show();
            txtUsername.setText("");
            txtPassword.setText("");
            txtConPassword.setText("");
            txtEmail.setText("");
            txtUsername.requestFocus();
        }
        return true;
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

