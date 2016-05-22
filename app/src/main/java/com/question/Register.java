package com.question;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

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

public class Register extends Activity {
    EditText user, pass, nick;
    Button register;
    String _user, _pass, _nick;
    ProgressDialog pd;
    LinearLayout already_user;

    /**/
    String regId;
    GoogleCloudMessaging gcm;
    public static final String REG_ID = "regId";
    Context context;
    static final String TAG = "Register Activity";
    /**/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
        user = (EditText)findViewById(R.id.editText1);
        pass = (EditText)findViewById(R.id.editText2);
        nick = (EditText)findViewById(R.id.editText3);
        register = (Button)findViewById(R.id.button2);
        //not_user_info = (TextView)findViewById(R.id.textView1);
        already_user = (LinearLayout)findViewById(R.id.LayoutAlreadyUser);
        already_user.setVisibility(View.INVISIBLE);
	}

    public void register_func(View v){
        _user = user.getText().toString();
        _pass = pass.getText().toString();
        _nick = nick.getText().toString();
        already_user.setVisibility(View.INVISIBLE);

        Validation validation = new Validation();

        if(!validation.isValidEmail(_user)) {
            user.setError("invalid email");
            user.setHintTextColor(Color.RED);
            user.setHint("invalid email");
            user.setText("");
            user.requestFocus();
        }
        if(!validation.isValidPassword(_pass)){
            pass.setError("invalid password");
            pass.setHintTextColor(Color.RED);
            pass.setHint("invalid password");
            pass.setText("");
            user.requestFocus();
        }
        if(validation.isValidEmail(_user) && validation.isValidPassword(_pass)){
            pd = ProgressDialog.show(Register.this, "please wait...", "register...", true);
            pd.setCancelable(false);
            registerGCM();
            //postJson  json = new postJson();
            //json.execute("jj");
        }
    }

    /**/
    public String registerGCM() {

        gcm = GoogleCloudMessaging.getInstance(this);
        regId = getRegistrationId(context);

        if (TextUtils.isEmpty(regId)) {

            registerInBackground();

            Log.d("RegisterActivity",
                    "registerGCM - successfully registered with GCM server - regId: "
                            + regId);
        } else {
            Toast.makeText(getApplicationContext(),
                    "RegId already available. RegId: " + regId,
                    Toast.LENGTH_LONG).show();
        }
        return regId;
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getSharedPreferences(
                Register.class.getSimpleName(), Context.MODE_PRIVATE);
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
                    Log.d("RegisterActivity", "registerInBackground - regId: "
                            + regId);
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
                /*Toast.makeText(getApplicationContext(),
                        "Registered with GCM Server." + msg, Toast.LENGTH_LONG)
                        .show();*/
                //saveRegisterId(context, regId);
                postJson  json = new postJson();
                json.execute("jj");
            }
        }.execute(null, null, null);
    }
    /**/

    public class postJson extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String string = postData(null, null);
            return string;
        }

        @SuppressWarnings("unchecked")
        public String postData(String url,JSONObject object) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getResources().getString(R.string.web_service)+ "register.php?");
            System.out.println("url "+url);
            String str=null;

            try {
                // Add your data
                @SuppressWarnings("rawtypes")
                ArrayList localArrayList = new ArrayList();
                localArrayList.add(new BasicNameValuePair("register", _user + "|" + _pass + "|" + regId + "|" + _nick));
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

        protected void onPostExecute(String result) {
            try {
                pd.dismiss();
                JSONObject jsonObject = new JSONObject(result);
                if(jsonObject.getString("access").equals("granted")&&!jsonObject.getString("status").equals("already_user")){
                    SharedPreferences sharedPreferences = getSharedPreferences("user_id",MODE_PRIVATE);
                    Editor editor = sharedPreferences.edit();
                    editor.putInt("id", jsonObject.getInt("status"));
                    editor.putString("user", _user);
                    editor.commit();

                    SharedPreferences sharedPreferencesS = getSharedPreferences("user_score",MODE_PRIVATE);
                    Editor editorS = sharedPreferencesS.edit();
                    editorS.putInt("s_count", 9);
                    editorS.commit();

                    SharedPreferences sharedPreferences4User = getSharedPreferences("user_mail",MODE_PRIVATE);
                    Editor editor4User = sharedPreferences4User.edit();
                    editor4User.putString("user", _user);
                    editor4User.commit();

                    //Intent i=new Intent(Register.this,Questions.class);
                    Intent i = new Intent(Register.this, Welcome.class);
                    startActivity(i);
                    finish();
                }else{
                    already_user.setVisibility(View.VISIBLE);
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

    @Override
    public void onBackPressed() {
        Intent i=new Intent(Register.this,Main.class);
        startActivity(i);
        finish();
    }
}
