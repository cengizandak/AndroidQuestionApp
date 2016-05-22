package com.question;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class Response extends Activity {

    String get, check, responseS;
    String response, userHost, userFrom;
    private Bundle extras = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);

        SharedPreferences sharedPreferences4User = getSharedPreferences("user_mail",MODE_PRIVATE);
        userHost = sharedPreferences4User.getString("user", "");

        SharedPreferences sharedPreferencesGCM = getSharedPreferences("gcm",MODE_PRIVATE);
        get = sharedPreferencesGCM.getString("userFrom", "");
        responseS = sharedPreferencesGCM.getString("responseS", "");
        userFrom = get;


        if(responseS.equals("2")){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Response.this);
            alertDialogBuilder.setTitle("questions challenge mode");
            alertDialogBuilder
                .setMessage("challenge from " + get)
                .setCancelable(false)
                .setPositiveButton("accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        accept();
                    }
                })
                .setNegativeButton("ignore", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ignore();
                    }
                });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }else if(responseS.equals("1")){
            SharedPreferences sharedPreferencesChallenge = getSharedPreferences("sChallenge", MODE_PRIVATE);
            SharedPreferences.Editor editorChallenge = sharedPreferencesChallenge.edit();
            editorChallenge.putBoolean("isChallenge", true);
            editorChallenge.putString("user_challenger", userHost);
            editorChallenge.putString("user_challenged", userFrom);
            editorChallenge.commit();
            Intent i = new Intent(Response.this, Questions.class);
            startActivity(i);
            finish();
        }else if(responseS.equals("0")){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Response.this);
            alertDialogBuilder.setTitle("questions challenge mode");
            alertDialogBuilder
                    .setMessage("ignore from " + get)
                    .setCancelable(false)
                    .setPositiveButton("welcome page", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            welcome();
                        }
                    })
                    .setNegativeButton("quit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            quit();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private void accept(){
        response = "1";
        PostJson  json = new PostJson();
        json.execute("jj");
        SharedPreferences sharedPreferencesChallenge = getSharedPreferences("sChallenge", MODE_PRIVATE);
        SharedPreferences.Editor editorChallenge = sharedPreferencesChallenge.edit();
        editorChallenge.putBoolean("isChallenge", true);
        editorChallenge.putString("user_challenger", userFrom);
        editorChallenge.putString("user_challenged", userHost);
        editorChallenge.commit();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(Response.this, Questions.class);
                startActivity(i);
                finish();
            }
        }, 3 * 500);
    }
    private void ignore(){
        response = "0";
        PostJson  json = new PostJson();
        json.execute("jj");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                System.exit(0);
            }
        }, 3 * 500);
    }
    private void welcome(){
        Intent i = new Intent(Response.this, Welcome.class); //welcome sayfasina gidiyor
        startActivity(i);
        finish();
    }
    private void quit(){
        System.exit(0);
    }

    public class PostJson extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String string = postData(null, null);
            return string;
        }

        @SuppressWarnings("unchecked")
        public String postData(String url,JSONObject object) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getResources().getString(R.string.web_service)  + "response.php");
            System.out.println("url "+url);
            String str=null;
            try {
                @SuppressWarnings("rawtypes")
                ArrayList localArrayList = new ArrayList();
                localArrayList.add(new BasicNameValuePair("response", response + "|" + userFrom + "|" + userHost));
                httppost.setEntity(new UrlEncodedFormEntity(localArrayList));
                HttpResponse response = httpclient.execute(httppost);
                String responseStr = EntityUtils.toString(response.getEntity());
                str = responseStr;
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                str = e.getMessage();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                str = e.getMessage();
            }
            System.out.println(str);
            return str;
        }

        protected void onPostExecute(String result) {//servisten cevap dönünce calisiyor
            try {
                JSONObject jsonObject = new JSONObject(result);
            } catch (JSONException e) {
            } catch (NumberFormatException localNumberFormatException) {
            } catch (Exception e) {
            }
            super.onPostExecute(result);
        }
    }

    public String removeLast2(String str) {
        if (str.length() > 0 ) {
            str = str.substring(0, str.length() - 2);
        }
        return str;
    }
}
