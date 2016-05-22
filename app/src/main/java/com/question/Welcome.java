package com.question;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


public class Welcome extends Activity {
    String s_time, c_time, time_s, time_c;
    int s_count, c_count, id;
    long dayInM = 86400000; //24 saat mili saniye cinsinden
    boolean d_use_s, d_use_c; //true ise tarih saat güncelleniyor false ise sayac güncelleniyor
    Button single, challenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        single = (Button) findViewById(R.id.buttonSM);
        challenge = (Button) findViewById(R.id.buttonCM);

        SharedPreferences sharedPreferences = getSharedPreferences("count_time",MODE_PRIVATE);
        s_count = sharedPreferences.getInt("s_count",0);
        s_time = sharedPreferences.getString("s_time","0000-00-00 00:00:00");
        c_count = sharedPreferences.getInt("c_count",0);
        c_time = sharedPreferences.getString("c_time","0000-00-00 00:00:00");
        id = sharedPreferences.getInt("id", 0);

        /*SharedPreferences sharedPreferencesS = getSharedPreferences("user_s",MODE_PRIVATE);
        Editor editorS = sharedPreferencesS.edit();
        editorS.putBoolean("pressed", false);
        editorS.commit();

        SharedPreferences sharedPreferencesC = getSharedPreferences("user_c",MODE_PRIVATE);
        Editor editorC = sharedPreferencesC.edit();
        editorC.putBoolean("pressed", false);
        editorC.commit();*/
    }

    public void single_mode_func(View v) {
        single.setEnabled(false);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date s_d_time = Calendar.getInstance().getTime();
        try {
            s_d_time = sdf.parse(s_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date s_c_time = Calendar.getInstance().getTime();
        long diff = s_c_time.getTime() - s_d_time.getTime(); //kayitli tarih ile sistem saati farkli milisaniye cinsinden

        SharedPreferences sharedPreferencesS = getSharedPreferences("user_s",MODE_PRIVATE);
        Editor editorS = sharedPreferencesS.edit();
        editorS.putBoolean("pressed", true);

        time_s = sdf.format(s_c_time);
        System.out.println(time_s);
        PostJson_S jsonS = new PostJson_S(); //sayac ve tarih güncellenmesi icin yeni posta gidiyor

        if(diff > dayInM ) {//fark 24 saatten fazla ise tarih güncelleniyor ve sayac sifirlaniyor
            d_use_s = true;
            editorS.putInt("s_count", 9);
            editorS.commit();
            jsonS.execute("jj");
        }else if(s_count < 10){//fark 24 saatten kücük ise sayaca bakiliyor
            s_count++;
            d_use_s = false;
            editorS.putInt("s_count", 10 - s_count);
            editorS.commit();
            jsonS.execute("jj");
        }else{//iki kosul saglanmaz ise uyari verilip cikiliyor
            Toast.makeText(getApplicationContext(), "remaining daily use 0", Toast.LENGTH_LONG).show();
        }
    }

    public void challenge_mode_func(View v) {
        challenge.setEnabled(false);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date c_d_time = Calendar.getInstance().getTime();
        try {
            c_d_time = sdf.parse(c_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date c_c_time = Calendar.getInstance().getTime();
        long diff = c_c_time.getTime() - c_d_time.getTime(); //kayitli tarih ile sistem saati farkli milisaniye cinsinden

        SharedPreferences sharedPreferencesC = getSharedPreferences("user_c",MODE_PRIVATE);
        Editor editorC = sharedPreferencesC.edit();
        editorC.putBoolean("pressed", true);

        time_c = sdf.format(c_c_time);
        System.out.println(time_c);
        PostJson_C jsonC = new PostJson_C(); //sayac ve tarih güncellenmesi icin yeni posta gidiyor

        if(diff > dayInM ) {//fark 24 saatten fazla ise tarih güncelleniyor ve sayac sifirlaniyor
            d_use_c = true;
            editorC.putInt("c_count", 9);
            editorC.commit();
            jsonC.execute("jj");
        }else if(c_count < 10){//fark 24 saatten kücük ise sayaca bakiliyor
            c_count++;
            d_use_c = false;
            editorC.putInt("c_count", 10 - c_count);
            editorC.commit();
            jsonC.execute("jj");
        }else{//iki kosul saglanmaz ise uyari verilip cikiliyor
            Toast.makeText(getApplicationContext(), "remaining daily use 0", Toast.LENGTH_LONG).show();
        }
    }

    public void my_profile_func(View v) {
        Intent i = new Intent(Welcome.this, UserPage.class);
        startActivity(i);
        finish();
    }



    public class PostJson_S extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String string = postData(null, null);
            return string;
        }

        @SuppressWarnings("unchecked")
        public String postData(String url,JSONObject object) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getResources().getString(R.string.web_service) + "login.php");
            System.out.println("url "+url);
            String str=null;
            try {
                @SuppressWarnings("rawtypes")
                ArrayList localArrayList = new ArrayList();
                if(d_use_s){
                    localArrayList.add(new BasicNameValuePair("session_t", id + "|" + time_s));
                }else{
                    localArrayList.add(new BasicNameValuePair("session_c", id + "|" + Integer.toString(s_count)));
                    SharedPreferences sharedPreferencesS = getSharedPreferences("count_time",MODE_PRIVATE);
                    Editor editorS = sharedPreferencesS.edit();
                    editorS.putInt("s_count", s_count);
                    editorS.commit();
                }
                httppost.setEntity(new UrlEncodedFormEntity(localArrayList));
                HttpResponse response = httpclient.execute(httppost);
                String responseStr = EntityUtils.toString(response.getEntity());
                str = responseStr;
            } catch (ClientProtocolException e) {
                str = e.getMessage();
            } catch (IOException e) {
                str = e.getMessage();
            }
            System.out.println(str);
            return str;
        }

        protected void onPostExecute(String result) {
            try {
                Intent i = new Intent(Welcome.this, Questions.class);
                SharedPreferences sharedPreferencesChallenge = getSharedPreferences("sChallenge",MODE_PRIVATE);
                Editor editorChallenge = sharedPreferencesChallenge.edit();
                editorChallenge.putBoolean("isChallenge", false);
                editorChallenge.commit();
                startActivity(i);
                finish();
            }catch (NumberFormatException localNumberFormatException) {
            }
            catch (Exception e ){
            }
            super.onPostExecute(result);
        }
    }

    public class PostJson_C extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String string = postData(null, null);
            return string;
        }

        @SuppressWarnings("unchecked")
        public String postData(String url,JSONObject object) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getResources().getString(R.string.web_service) + "login.php");
            System.out.println("url "+url);
            String str=null;
            try {
                @SuppressWarnings("rawtypes")
                ArrayList localArrayList = new ArrayList();
                if(d_use_c){
                    localArrayList.add(new BasicNameValuePair("session_tc", id + "|" + time_c));
                }else{
                    localArrayList.add(new BasicNameValuePair("session_cc", id + "|" + Integer.toString(c_count)));
                    SharedPreferences sharedPreferencesS = getSharedPreferences("count_time",MODE_PRIVATE);
                    Editor editorS = sharedPreferencesS.edit();
                    editorS.putInt("c_count", c_count);
                    editorS.commit();
                }
                httppost.setEntity(new UrlEncodedFormEntity(localArrayList));
                HttpResponse response = httpclient.execute(httppost);
                String responseStr = EntityUtils.toString(response.getEntity());
                str = responseStr;
            } catch (ClientProtocolException e) {
                str = e.getMessage();
            } catch (IOException e) {
                str = e.getMessage();
            }
            System.out.println(str);
            return str;
        }

        protected void onPostExecute(String result) {
            try {
                Intent i = new Intent(Welcome.this, Users.class);
                startActivity(i);
                finish();
                //Toast.makeText(Welcome.this, "under construction...", Toast.LENGTH_LONG).show();
            }catch (NumberFormatException localNumberFormatException) {
            }
            catch (Exception e ){
            }
            super.onPostExecute(result);
        }
    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit){
            System.exit(0);
        }
        else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }
}
