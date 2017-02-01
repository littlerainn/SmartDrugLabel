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

import android.os.Bundle;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class ChangePwdActivity extends Activity {
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepwd_screen);

        // Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        showinfo();

        // btnSave
        Button btnSave = (Button) findViewById(R.id.btnSave);
        // Perform action on click
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (SaveData()) {
                    // When Save Complete
                }
            }
        });
    }

    public void showinfo() {
        Intent intent = getIntent();
        final String Username = intent.getStringExtra("Username");
        EditText username = (EditText) findViewById(R.id.username_edtext);
        username.setText(Username);
    }

    public boolean SaveData() {
        final EditText txtUsername = (EditText) findViewById(R.id.username_edtext);
        final EditText txtPassword = (EditText) findViewById(R.id.passwd_edtext);
        final EditText txtConPassword = (EditText) findViewById(R.id.cnfpasswd_edtext);

        //Dialog
        final AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle("Error!");
        ad.setIcon(android.R.drawable.btn_star_big_on);
        ad.setPositiveButton("Close", null);

        //Check Password
        if (txtPassword.getText().length() == 0 || txtConPassword.getText().length() == 0) {
            ad.setMessage("Please enter [Password/Confirm Password]");
            ad.show();
            txtPassword.requestFocus();
            return false;
        }

        //Check Password and Confirm Password (Match)
        if (!txtPassword.getText().toString().equals(txtConPassword.getText().toString())) {
            ad.setMessage("Password and Confirm Password Not Match!");
            ad.show();
            txtConPassword.requestFocus();
            return false;
        }
        String url = "http://202.58.126.48:8081/smartdruglabel/updatePassword.php";
        /* String url = "http://www.kongtunmae-oncb.go.th/offer_hmf/smartdruglabel/updatePassword.php"; */

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("sUsername", txtUsername.getText().toString()));
        params.add(new BasicNameValuePair("sPassword", txtPassword.getText().toString()));

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
        String strError = "Unknow Status!";

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
            return false;
        } else {
            Toast.makeText(ChangePwdActivity.this, "Change Password Successfully", Toast.LENGTH_SHORT).show();
            txtPassword.setText("");
            txtConPassword.setText("");
            txtPassword.requestFocus();
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
