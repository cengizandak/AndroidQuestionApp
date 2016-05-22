package com.question;

import java.io.IOException;
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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class Main extends Activity {

    EditText user, pass;        //giris ekranında gözüken edittextler
    Button login;               //login butonu - artık xlm den fonksiyona yönleniyor, tanımlanmasada olur
    String _user, _pass;        //edittexte yazılan mail adresi ve mail adresini tutan değişkenler
    ProgressDialog pd;          //web servis ile baglantı kurulan zamanlarda işlem dialog pencereleri acilirken kullanılıyor
    LinearLayout not_user_info; //normalde de gizli olan kullanıcı girişi izni verilmeyen durumlarda görünür olan katman
    CheckBox remember;          //beni hatırla tiki
    Boolean rememberS;          //benihatirla verisini tutan değişken
    String s_time, c_time;      //webservisten gelen modların kullanıldığı zon zaman damgası için değişkenler
    int s_count, c_count, id;   //webservisten gelen modların kullanılma sayısı için değişkenler
    /*GCM değişkenleri uygulamaya her girişte regID yi servise göndererek güncelleme yapıyor*/
    String regId;
    GoogleCloudMessaging gcm;
    public static final String REG_ID = "regId";
    Context context;
    static final String TAG = "Register Activity";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //main layoutuna ilk girildiğinde çalışıyor
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = (EditText) findViewById(R.id.editText1);
        pass = (EditText) findViewById(R.id.editText2);
        login = (Button) findViewById(R.id.button1);
        not_user_info = (LinearLayout) findViewById(R.id.LayoutNotUser);
        not_user_info.setVisibility(View.INVISIBLE);                    //başta notuserinfo görünmez yapılıyor hata durumunda görünür oluyor
        remember = (CheckBox) findViewById(R.id.checkBox);
        SharedPreferences sharedPreferences = getSharedPreferences("user_id", MODE_PRIVATE); //daha önceki oturumlarda kaydedilen benihatirla verisi için
        rememberS = sharedPreferences.getBoolean("rememberS", false); //benihatirla icin deger aliniyor
        remember.setChecked(rememberS);     //benihatirla için checkbox checked değeri güncelleniyor
        if (rememberS) {
            user.setText(sharedPreferences.getString("user", ""));       //rememberme true ise mailadresi alanına alinan deger yaziliyor
            pass.setText(sharedPreferences.getString("pass", ""));       //rememberme true ise parola alanına alinan deger yaziliyor
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void login_func(View v) {
        _user = user.getText().toString();          //user edittexten veri alnıp _user değişkenine eşitleniyor
        _pass = pass.getText().toString();          //pass edittexten veri alnıp _pass değişkenine eşitleniyor
        not_user_info.setVisibility(View.INVISIBLE);

        Validation validation = new Validation();   //mail adresi ve parola koşulları için yazılan sınıf

        if (!validation.isValidEmail(_user)) {//mail adresi kontrolü yapılıyor
            user.setError("invalid email");
            user.setHintTextColor(Color.RED);
            user.setHint("invalid email");
            user.setText("");
            user.requestFocus();
        }

        if (!validation.isValidPassword(_pass)) {//şifre adresi kontrolü
            pass.setError("invalid password");
            pass.setHintTextColor(Color.RED);
            pass.setHint("invalid password");
            pass.setText("");
            user.requestFocus();
        }
        if (validation.isValidEmail(_user) && validation.isValidPassword(_pass)) {//mail adresi ve şifre izin verilen düzeyde ise dogruluk icin servise gönderiliyor
            pd = ProgressDialog.show(Main.this, "please wait...", "logging...", true);
            pd.setCancelable(false);
            registerGCM();
            /*PostJson  json = new PostJson();
            json.execute("jj");*/
        }
    }

    public void register_func(View v) {  //register butonuna tiklandiysa calisiyor
        Intent ii = new Intent(Main.this, Register.class);
        startActivity(ii);
        finish();
    }

    //******** Bu kısım internetten alınmıştır Baslangıc
    public String registerGCM() {       //her giriste GCM güncellemek için
        gcm = GoogleCloudMessaging.getInstance(this);
        regId = getRegistrationId(context);
        if (TextUtils.isEmpty(regId)) {
            registerInBackground();
            Log.d("RegisterActivity", "registerGCM - successfully registered with GCM server - regId: " + regId);
        } else {
            Toast.makeText(getApplicationContext(), "RegId already available. RegId: " + regId, Toast.LENGTH_LONG).show();
        }
        return regId;
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getSharedPreferences(Register.class.getSimpleName(), Context.MODE_PRIVATE);
        String registrationId = prefs.getString(REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        return registrationId;
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register(Config.GOOGLE_PROJECT_ID);
                    Log.d("RegisterActivity", "registerInBackground - regId: " + regId);
                    msg = "Device registered, registration ID=" + regId;

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.d("RegisterActivity", "Error: " + msg);
                }
                Log.d("RegisterActivity", "AsyncTask completed: " + msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                PostJson json = new PostJson();
                json.execute("jj"); //regID alindiktan sonra servise bilgiler gönderiliyor
            }
        }.execute(null, null, null);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.question/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.question/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public class PostJson extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String string = postData(null, null);
            return string;
        }

        @SuppressWarnings("unchecked")
        public String postData(String url, JSONObject object) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getResources().getString(R.string.web_service) + "login.php?");
            System.out.println("url " + url);
            String str = null;
            try {
                @SuppressWarnings("rawtypes")
                ArrayList localArrayList = new ArrayList();
                localArrayList.add(new BasicNameValuePair("login", _user + "|" + _pass + "|" + regId));
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
                //pd.dismiss();
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getString("access").equals("granted") && !jsonObject.getString("status").equals("not_user")) {
                    id = jsonObject.getInt("status");

                    SharedPreferences sharedPreferences = getSharedPreferences("user_id", MODE_PRIVATE);
                    Editor editor = sharedPreferences.edit();
                    editor.putInt("id", id);
                    editor.putString("user", _user);
                    if (remember.isChecked()) {//rememberme icin pass kaydediliyor yada kayitli olan bosaltiliyor
                        editor.putString("pass", _pass);
                    } else {
                        editor.putString("pass", "");
                    }
                    editor.putBoolean("rememberS", remember.isChecked());//bir sonraki oturum icin checkbox degeri kaydediliyor
                    editor.commit();


                    s_count = jsonObject.getInt("s_count");
                    s_time = jsonObject.getString("s_time");
                    c_count = jsonObject.getInt("c_count");
                    c_time = jsonObject.getString("c_time");

                    SharedPreferences sharedPreferencesS = getSharedPreferences("count_time", MODE_PRIVATE);
                    Editor editorS = sharedPreferencesS.edit();
                    editorS.putInt("s_count", s_count);
                    editorS.putString("s_time", s_time);
                    editorS.putInt("c_count", c_count);
                    editorS.putString("c_time", c_time);
                    editorS.putInt("id", id);
                    editorS.commit();

                    /*push mesaj için mail adresi kaydediliyor.*/
                    SharedPreferences sharedPreferences4User = getSharedPreferences("user_mail", MODE_PRIVATE);
                    Editor editor4User = sharedPreferences4User.edit();
                    editor4User.putString("user", _user);
                    editor4User.commit();

                    pd.dismiss();
                    Intent i = new Intent(Main.this, Welcome.class); //welcome sayfasına gidiyor
                    startActivity(i);
                    finish();
                } else {  //not user döndügünde user ve pass text lerini temizliyor
                    pd.dismiss();
                    not_user_info.setVisibility(View.VISIBLE);
                    user.setText("");
                    pass.setText("");
                }
            } catch (JSONException e) {
            } catch (NumberFormatException localNumberFormatException) {
            } catch (Exception e) {
            }
            super.onPostExecute(result);
        }
    }
    //******** Bu kısım internetten alınmıştır Bitis

    private Boolean exit = false;

    @Override
    public void onBackPressed() { //back tusuna basilinca cikis yapmak icin
        if (exit) {
            System.exit(0);
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {  //3 sn içinde iki defa basıldı mı kontrolu için
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }
}
