package com.question;

import java.io.IOException;
import java.util.ArrayList;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Score extends Activity {
	
	EditText user, pass;
	Button login;
	String _user, _pass;
	ProgressDialog pd;
	TextView score_table;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.score);
		score_table = (TextView)findViewById(R.id.textView1);
        postJson  json = new postJson();
        json.execute("jj");
	}

	public class postJson extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String string = postData(null, null);
            return string;
        }
        @SuppressWarnings("unchecked")
        public String postData(String url,JSONObject object) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getResources().getString(R.string.web_service) + "score_table.php");
            System.out.println("url "+url);
            String str=null;
            try {
                // Add your data
                @SuppressWarnings("rawtypes")
                ArrayList localArrayList = new ArrayList();
                localArrayList.add(new BasicNameValuePair("score", "a"));
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

        protected void onPostExecute(String result)
        {
            try
            {
                JSONObject jsonObject = new JSONObject(result);
                score_table = (TextView)findViewById(R.id.textView1);
                score_table.setText(jsonObject.getString("score_table").toString());
            } catch (JSONException e) {
            } catch (NumberFormatException localNumberFormatException) {
            } catch (Exception e) {
            }
            super.onPostExecute(result);
        }
    }

    @Override
    public void onBackPressed() {
        Intent i=new Intent(Score.this,UserPage.class);
        startActivity(i);
        finish();
    }
}
