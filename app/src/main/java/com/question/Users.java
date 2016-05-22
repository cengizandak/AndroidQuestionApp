package com.question;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Users extends Activity {
    ProgressDialog pd, pd1;
    List<User> users = new ArrayList<User>();
    ListView listView;

    //String gcmRegIds = "APA91bHn0ThwOVwK755uBWFE_QwsfZaGKEzEEjt5gwN8U6rw0vWb0z6e6SKjuQkhwIUbE_BgPZ1W5zI2s8HCsVanMNbm5u_utDLScpK-iwMcD7V54W-rX6QM3H47KbGzvnrspzHC90Io";
    //String user = "eyupaltindal@gmail.com";
    String gcmRegIds, userHost, user4push;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        listView = (ListView) findViewById(R.id.listView);
        pd = ProgressDialog.show(Users.this, "please wait...", "loading users...", true);
        pd.setCancelable(false);
        SharedPreferences sharedPreferences4User = getSharedPreferences("user_mail",MODE_PRIVATE);
        userHost = sharedPreferences4User.getString("user","");
        PostJson json = new PostJson();
        json.execute("jj");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pd = ProgressDialog.show(Users.this, "please wait...", "pushing to user...", true);
                pd.setCancelable(false);
                gcmRegIds = users.get(position).getGcm_regid();
                user4push = users.get(position).getUser();

                if(!userHost.equals(user4push)){
                    PostJsonPush jsonPush = new PostJsonPush();
                    jsonPush.execute("jj");
                }else{
                    pd.dismiss();
                    Toast.makeText(Users.this, "can not push yourself", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    public void listAdd(){
        List<String> users4list = new ArrayList<String>();
        Iterator it = users.iterator();
        while (it.hasNext()){
            User u = (User) it.next();
            //if(!userHost.equals(u.getUser()))
                users4list.add(u.getNick());
        }

        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, android.R.id.text1, users4list);
        listView.setAdapter(stringArrayAdapter);
    }

    public void all(View v){
        pd = ProgressDialog.show(Users.this, "please wait...", "loading all users...", true);
        pd.setCancelable(false);
        users.clear();
        PostJson json = new PostJson();
        json.execute("jj");
    }

    public void online(View v){
        pd = ProgressDialog.show(Users.this, "please wait...", "loading online users...", true);
        pd.setCancelable(false);
        users.clear();
        PostJsonOnline jsonOnline = new PostJsonOnline();
        jsonOnline.execute("jj");
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
            HttpPost httppost = new HttpPost(getResources().getString(R.string.web_service) + "users.php");
            System.out.println("url " + url);
            String str = null;
            try {
                // Add your data
                @SuppressWarnings("rawtypes")
                ArrayList localArrayList = new ArrayList();
                localArrayList.add(new BasicNameValuePair("users", "users"));
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
                JSONArray jsonArray = new JSONArray(result);
                JSONObject jsonObject = new JSONObject();
                for (int i = 0; i < jsonArray.length(); i++) {
                    User user = new User();
                    jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString("status").equals("users")) {
                        user.setId(jsonObject.getInt("id"));
                        user.setUser(jsonObject.getString("user"));
                        user.setS_time(jsonObject.getString("s_time"));
                        user.setS_count(jsonObject.getString("s_count"));
                        user.setC_time(jsonObject.getString("c_time"));
                        user.setC_count(jsonObject.getString("c_count"));
                        user.setGcm_regid(jsonObject.getString("gcm_regid"));
                        user.setLast_login_time(jsonObject.getString("last_login_time"));
                        user.setNick(jsonObject.getString("nick"));
                    }
                       users.add(i, user);

                }
                pd.dismiss();
                listAdd();

            } catch (JSONException e) {
            } catch (NumberFormatException localNumberFormatException) {
            } catch (Exception e) {
            }
            super.onPostExecute(result);
        }
    }



    public class PostJsonOnline extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String string = postData(null, null);
            return string;
        }

        @SuppressWarnings("unchecked")
        public String postData(String url, JSONObject object) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getResources().getString(R.string.web_service) + "users_online.php");
            System.out.println("url " + url);
            String str = null;
            try {
                // Add your data
                @SuppressWarnings("rawtypes")
                ArrayList localArrayList = new ArrayList();
                localArrayList.add(new BasicNameValuePair("users", "users"));
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
                JSONArray jsonArray = new JSONArray(result);
                JSONObject jsonObject = new JSONObject();
                for (int i = 0; i < jsonArray.length(); i++) {
                    User user = new User();
                    jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString("status").equals("users")) {
                        user.setId(jsonObject.getInt("id"));
                        user.setUser(jsonObject.getString("user"));
                        user.setS_time(jsonObject.getString("s_time"));
                        user.setS_count(jsonObject.getString("s_count"));
                        user.setC_time(jsonObject.getString("c_time"));
                        user.setC_count(jsonObject.getString("c_count"));
                        user.setGcm_regid(jsonObject.getString("gcm_regid"));
                        user.setLast_login_time(jsonObject.getString("last_login_time"));
                        user.setNick(jsonObject.getString("nick"));
                    }

                       users.add(i, user);

                }
                pd.dismiss();
                listAdd();

            } catch (JSONException e) {//online kullanıcı yoksa buraya geliyor. lsite yenileniyor
                pd.dismiss();
                listAdd();
            } catch (NumberFormatException localNumberFormatException) {
            } catch (Exception e) {
            }
            super.onPostExecute(result);
        }
    }

    public class PostJsonPush extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String string = postData(null, null);
            return string;
        }


        @SuppressWarnings("unchecked")
        public String postData(String url,JSONObject object) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getResources().getString(R.string.web_service)  + "push.php?");
            System.out.println("url "+url);
            String str=null;
            try {
                // Add your data
                @SuppressWarnings("rawtypes")
                ArrayList localArrayList = new ArrayList();
                localArrayList.add(new BasicNameValuePair("push", gcmRegIds + "|" + userHost));
                System.out.println("push from " + userHost);
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
                pd.dismiss();
                Intent i = new Intent(Users.this, PushTest.class);
                i.putExtra("user4push", user4push);
                startActivity(i);
                finish();
                JSONObject jsonObject = new JSONObject(result);
            } catch (JSONException e) {
            } catch (NumberFormatException localNumberFormatException) {
            } catch (Exception e) {
            }
            super.onPostExecute(result);
        }
    }

    @Override
    public void onBackPressed() {
        Intent i=new Intent(Users.this, Welcome.class);
        startActivity(i);
        finish();
    }
}
