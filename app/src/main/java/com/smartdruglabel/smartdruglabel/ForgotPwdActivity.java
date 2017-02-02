package com.smartdruglabel.smartdruglabel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ForgotPwdActivity extends Activity {
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpwd_screen);

        //font Setting
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/supermarket.ttf");
        TextView myTextview = (TextView) findViewById(R.id.forgot_tv);
        myTextview.setTypeface(myTypeface);

        //Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        final AlertDialog.Builder ad = new AlertDialog.Builder(this);

        //txtEmail
        final EditText txtEmail = (EditText) findViewById(R.id.email_edtext);
        txtEmail.setTypeface(myTypeface);

        //btnLogin
        final Button btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setTypeface(myTypeface);

        //Perform action on click
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String url = "http://202.58.126.48:8081/smartdruglabel/sendEmail.php";
                /* String url = "http://www.kongtunmae-oncb.go.th/offer_hmf/smartdruglabel/sendEmail.php"; */
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("sEmail", txtEmail.getText().toString()));

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

                //Prepare Send Email
                if (strStatusID.equals("0")) {
                    //Dialog
                    ad.setTitle("ข้อผิดพลาด");
                    ad.setIcon(R.drawable.warn_icon);
                    ad.setPositiveButton("ตกลง", null);
                    ad.setMessage(strError);
                    ad.show();
                } else {
                    Toast.makeText(ForgotPwdActivity.this, "ส่งรหัสผ่านไปยังอีเมลสำเร็จ", Toast.LENGTH_SHORT).show();
                }
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