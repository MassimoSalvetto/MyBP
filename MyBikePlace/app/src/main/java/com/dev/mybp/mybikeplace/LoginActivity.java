package com.dev.mybp.mybikeplace;

import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
//import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.String;
import android.os.AsyncTask;
import org.json.JSONException;
//import org.json.JSONObject;


import java.net.HttpURLConnection;
import java.net.URL;

import java.io.*;
import java.io.UnsupportedEncodingException;
import java.security.*;

public class LoginActivity extends MultiDexApplication {

    public final static String EXTRA_USERNAME = "com.example.zephyr.tutorialhtml.USERNAME";
    public final static String EXTRA_PASSWORD = "com.example.zephyr.tutorialhtml.PASSWORD";
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    // called when the user clicks the send button
    public void sendCredentials(View view) throws UnsupportedEncodingException {

        Intent i = new Intent(this, PersonalActivity.class);
        StringBuffer responseStr = null;

        EditText editUsername = (EditText) findViewById(R.id.usernameText);
        String username = editUsername.getText().toString();
        EditText editPassword = (EditText) findViewById(R.id.passwordText);
        String password = editPassword.getText().toString();

        if (username.isEmpty()) {
            editUsername.setHint("type a valid username");
            editUsername.setHintTextColor(getResources().getColor(R.color.red));
            editPassword.setText("");
        } else {
            if (password.isEmpty()) {
                editPassword.setHint("type a valid password");
                editPassword.setHintTextColor(getResources().getColor(R.color.red));
            } else {
                byte[] bytesOfUser = username.getBytes("UTF-8");
                byte[] bytesOfPwd  = password.getBytes("UTF-8");
                MessageDigest mdUser = null;
                MessageDigest mdPwd= null;
                try {
                    //Encryption of user and pwd
                    mdUser = MessageDigest.getInstance("MD5");
                    mdPwd  = MessageDigest.getInstance("MD5");
                    byte[] digestUser = mdUser.digest(bytesOfUser);
                    byte[] digestPwd  = mdPwd.digest(bytesOfPwd);
                    username = android.util.Base64.encodeToString(digestUser,  android.util.Base64.DEFAULT);
                    password = android.util.Base64.encodeToString(digestPwd,  android.util.Base64.DEFAULT);
                    //################ SOLO DI PROVA #########################//
                    JSONObject obj = new JSONObject();
                    obj.put("pwd_ode", "ee");
                    obj.put("user_code", "vv");
                    obj.put("registration_id", "lol");

                    JSONArray list = new JSONArray();
                    list.add("msg 1");
                    list.add("msg 2");
                    list.add("msg 3");
                    //BISOGNA CHIAMARE EXECUTE POST E INVIARE LA RICHIESTA
                    responseStr = executePost("http://localhost:5000/myBP_server/users/sign_in",  obj );
                } catch (NoSuchAlgorithmException e) {
                    username = "ERROR_ENCRYPTION";
                    password = "ERROR_ENCRYPTION";
                    //e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(responseStr==null) {
                    i.putExtra(EXTRA_USERNAME, username);
                    i.putExtra(EXTRA_PASSWORD, password);
                    //Check function to sign in
                    startActivity(i);
                }
                else
                {
                    i.putExtra(EXTRA_USERNAME, responseStr.toString());
                    i.putExtra(EXTRA_PASSWORD, responseStr.toString());
                    //Check function to sign in
                    startActivity(i);
                }
            }
        }
    }


    // called when user click on clear button
    public void clearCredentials(View view){

        EditText editUsername = (EditText) findViewById(R.id.usernameText);
        editUsername.setText("");
        editUsername.setHint(R.string.usernameHint);
        editUsername.setHintTextColor(getResources().getColor(R.color.hint_foreground_material_light));

        EditText editPassword = (EditText) findViewById(R.id.passwordText);
        editPassword.setText("");
        editPassword.setHint(R.string.passwordHint);
        editPassword.setHintTextColor(getResources().getColor(R.color.hint_foreground_material_light));

    }

    public void goToMaps(View view){

        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
    }

    public StringBuffer executePost(String targetURL, JSONObject data) throws IOException {
        StringBuffer jsonBody = null;
        try {
            String url = targetURL;

            URL object = new URL(url);

            HttpURLConnection con = (HttpURLConnection) object.openConnection();

            con.setDoOutput(true);

            con.setDoInput(true);

            con.setRequestProperty("Content-Type", "application/json");

            con.setRequestProperty("Accept", "application/json");

            con.setRequestMethod("POST");

            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());

            wr.write(data.toString());

            wr.flush();

            //display what returns the POST request

            StringBuilder sb = new StringBuilder();

            int HttpResult = con.getResponseCode();

            if (HttpResult == HttpURLConnection.HTTP_OK) {

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));

                String line = null;
                jsonBody = new StringBuffer();
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                    jsonBody.append(line);
                }

                br.close();
            }
        }         catch (IOException e)
        {
            // print the error
            //TODO: remove and replace with a more structured approach
            e.printStackTrace();
        }

            return jsonBody;
    }

}

