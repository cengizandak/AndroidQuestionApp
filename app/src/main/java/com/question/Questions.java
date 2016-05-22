package com.question;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Questions extends Activity {
    ProgressDialog pd;
    ProgressDialog progressDialog;
    CountDown countDown;
    int chaa=0;
    TextView q_timer, t_timer, paragraph, question, answer_a, answer_b, answer_c, answer_d, answer_e, question_Counter,cha,cha1;
    int q_set = 10; //getResources().getInteger(R.integer.q_set);
    int score = 0, q_sid = 0, t_time = 0, backTime;
    List<Question> questions = new ArrayList<Question>();
    LinearLayout linearLayout;
    Button buttonA, buttonB, buttonC, buttonD, buttonE;
    boolean isChallenge = false;
    String user_challenger, user_challenged, challengerTy = "0";
    boolean iAmChallenger = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questions);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);

        pd = ProgressDialog.show(Questions.this, "please wait...", "questions loading...", true);
        pd.setCancelable(false);
        /*Challenge Mode dan geliryor mu Kontrolu*/
        SharedPreferences sharedPreferencesChallenge = getSharedPreferences("sChallenge",MODE_PRIVATE);
        isChallenge = sharedPreferencesChallenge.getBoolean("isChallenge",false);
        if(isChallenge){
            chaa=1;
            user_challenger = sharedPreferencesChallenge.getString("user_challenger", "");
            user_challenged = sharedPreferencesChallenge.getString("user_challenged", "");
            /*Challenger Kontrolü*/
            SharedPreferences sharedPreferences4User = getSharedPreferences("user_mail",MODE_PRIVATE);
            if(user_challenger.equals(sharedPreferences4User.getString("user",""))){
               chaa=1;
                iAmChallenger = true;
                challengerTy = "1";
            }
            /**/
        }
        /**/

        getQuestions();

        linearLayout = (LinearLayout) findViewById(R.id.container);
        //linearLayout.


    cha=(TextView) findViewById(R.id.textView);
        if(chaa==1){cha.setText("Challenge Mode");}
        else {cha.setText("Single Mode");}
        q_timer = (TextView) findViewById(R.id.textView1);
        t_timer = (TextView) findViewById(R.id.textViewTTime);
        paragraph = (TextView) findViewById(R.id.textView6);
        question = (TextView) findViewById(R.id.textView4);
        answer_a = (TextView) findViewById(R.id.question1);
        answer_b = (TextView) findViewById(R.id.question2);
        answer_c = (TextView) findViewById(R.id.question3);
        answer_d = (TextView) findViewById(R.id.question4);
        answer_e = (TextView) findViewById(R.id.question5);
        question_Counter = (TextView) findViewById(R.id.textViewQC);

        buttonA = (Button) findViewById(R.id.Button1);
        buttonB = (Button) findViewById(R.id.Button2);
        buttonC = (Button) findViewById(R.id.Button3);
        buttonD = (Button) findViewById(R.id.Button4);
        buttonE = (Button) findViewById(R.id.Button5);

        linearLayout.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                if (q_sid == (q_set-1))
                    Toast.makeText(Questions.this, "last question", Toast.LENGTH_SHORT).show();
                else {
                    progressDialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            question_func(++q_sid);
                        }
                    }, 200);
                }

            }

            @Override
            public void onSwipeRight() {
                if (q_sid == 0)
                    Toast.makeText(Questions.this, "first question", Toast.LENGTH_SHORT).show();
                else {
                    progressDialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            question_func(--q_sid);
                        }
                    }, 200);
                }
            }
        });

    }

    public void getQuestions() {
        PostJson json = new PostJson();
        json.execute("jj");
    }

    public void question_func(int i) {
        paragraph.setText(Html.fromHtml(questions.get(i).getParagraph()));
        q_timer.setText("recommended " + questions.get(i).getQ_time() + " second");
        if(questions.get(i).getParagraph().equals(""))
            paragraph.setVisibility(View.GONE);
        else
            paragraph.setVisibility(View.VISIBLE);

        question.setText(Html.fromHtml(questions.get(i).getQuestion()));
        if(questions.get(i).getQuestion().equals(""))
            question.setVisibility(View.GONE);
        else
            question.setVisibility(View.VISIBLE);
        answer_a.setText(questions.get(i).getA());
        answer_b.setText(questions.get(i).getB());
        answer_c.setText(questions.get(i).getC());
        answer_d.setText(questions.get(i).getD());
        answer_e.setText(questions.get(i).getE());
        question_Counter.setText((i+1)+"/"+(q_set));
        setOption(i);
    }

    private void setOption(int i) {
        buttonA.setBackground(getResources().getDrawable(R.drawable.button_default_bg));
        buttonB.setBackground(getResources().getDrawable(R.drawable.button_default_bg));
        buttonC.setBackground(getResources().getDrawable(R.drawable.button_default_bg));
        buttonD.setBackground(getResources().getDrawable(R.drawable.button_default_bg));
        buttonE.setBackground(getResources().getDrawable(R.drawable.button_default_bg));

        if (questions.get(i).getResponse().trim().equals("a"))
            buttonA.setBackground(getResources().getDrawable(R.drawable.button_pressed_bg));
        if (questions.get(i).getResponse().trim().equals("b"))
            buttonB.setBackground(getResources().getDrawable(R.drawable.button_pressed_bg));
        if (questions.get(i).getResponse().trim().equals("c"))
            buttonC.setBackground(getResources().getDrawable(R.drawable.button_pressed_bg));
        if (questions.get(i).getResponse().trim().equals("d"))
            buttonD.setBackground(getResources().getDrawable(R.drawable.button_pressed_bg));
        if (questions.get(i).getResponse().trim().equals("e"))
            buttonE.setBackground(getResources().getDrawable(R.drawable.button_pressed_bg));
    }

    public void a_func(View v) {
        buttonA.setBackgroundColor(android.R.color.holo_red_dark);
        questions.get(q_sid).setResponse("a");
        setOption(q_sid);
    }

    public void b_func(View v) {
        buttonB.setBackgroundColor(android.R.color.holo_red_dark);questions.get(q_sid).setResponse("b");
        setOption(q_sid);
    }

    public void c_func(View v) {
        buttonC.setBackgroundColor(android.R.color.holo_red_dark);  questions.get(q_sid).setResponse("c");
        setOption(q_sid);
    }

    public void d_func(View v) {
        buttonD.setBackgroundColor(android.R.color.holo_red_dark); questions.get(q_sid).setResponse("d");
        setOption(q_sid);
    }

    public void e_func(View v) {
        buttonE.setBackgroundColor(android.R.color.holo_red_dark);questions.get(q_sid).setResponse("e");
        setOption(q_sid);
    }

    public void q_end(View v) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Questions.this);
        alertDialogBuilder.setTitle("questions app name here");
        alertDialogBuilder
                .setMessage("Click yes to finish!")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        s_calc();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void s_calc() {
        for (int i = 0; i < 10; i++) {
            if (questions.get(i).getAnswer().trim().equals(questions.get(i).getResponse().trim())) {
                score = score + questions.get(i).getQ_xp();
            }
        }

        pd = ProgressDialog.show(Questions.this, "please wait...", "calculating score...", true);
        pd.setCancelable(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pd.dismiss();
                s_send();
            }
        }, 1000);
    }

    private void s_send() {
        if(isChallenge)
            pd = ProgressDialog.show(Questions.this, "please wait...", "?...", true);
        else
            pd = ProgressDialog.show(Questions.this, "please wait...", "transferring score...", true);
        pd.setCancelable(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PostJson_Score jsonScore = new PostJson_Score();
                jsonScore.execute("jj");
            }
        }, 2 * 1000);
    }

    public void left (View v) {
        if (q_sid == (q_set-1))
            Toast.makeText(Questions.this, "last question", Toast.LENGTH_SHORT).show();
        else {
            progressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    question_func(++q_sid);
                }
            }, 200);
        }

    }

    public void right(View v) {
        if (q_sid == 0)
            Toast.makeText(Questions.this, "first question", Toast.LENGTH_SHORT).show();
        else {
            progressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    question_func(--q_sid);
                }
            }, 200);
        }
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
            HttpPost httppost;
            if(isChallenge){
                httppost = new HttpPost(getResources().getString(R.string.web_service) + "questions_challenge.php");
            }else {
                httppost = new HttpPost(getResources().getString(R.string.web_service) + "questions_new.php");
            }
            System.out.println("url " + url);
            String str = null;
            try {
                // Add your data
                @SuppressWarnings("rawtypes")
                ArrayList localArrayList = new ArrayList();
                if(isChallenge){
                    localArrayList.add(new BasicNameValuePair("q_challenge", user_challenger + "|" + user_challenged));
                }else {
                    localArrayList.add(new BasicNameValuePair("q_set", Integer.toString(q_set)));
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

                JSONArray jsonArray = new JSONArray(result);
                JSONObject jsonObject = new JSONObject();
                for (int i = 0; i < jsonArray.length(); i++) {
                    Question questionCome = new Question();
                    jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString("status").equals("question")) {
                        questionCome.setId(jsonObject.getInt("id"));
                        questionCome.setQ_level(jsonObject.getInt("q_level"));
                        questionCome.setParagraph(jsonObject.getString("paragraph").toString());
                        questionCome.setQuestion(jsonObject.getString("question").toString());
                        questionCome.setA(jsonObject.getString("a").toString());
                        questionCome.setB(jsonObject.getString("b").toString());
                        questionCome.setC(jsonObject.getString("c").toString());
                        questionCome.setD(jsonObject.getString("d").toString());
                        questionCome.setE(jsonObject.getString("e").toString());
                        questionCome.setAnswer(jsonObject.getString("answer").toString());
                        t_time = t_time + questionCome.getQ_time();
                    }
                    questions.add(i, questionCome);
                }
                pd.dismiss();
                t_timer.setText(t_time + " second");
                countDown = new CountDown(t_time * 1000, 1000);
                countDown.start();
                question_func(0);

            } catch (JSONException e) {
            } catch (NumberFormatException localNumberFormatException) {
            } catch (Exception e) {
            }
            super.onPostExecute(result);
        }
    }

    public class PostJson_Score extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String string = postData(null, null);
            return string;
        }

        @SuppressWarnings("unchecked")
        public String postData(String url, JSONObject object) {
            SharedPreferences sharedPreferences = getSharedPreferences("user_id", MODE_PRIVATE);
            String _user = sharedPreferences.getString("user", "");
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost;
            if(isChallenge)
                httppost = new HttpPost(getResources().getString(R.string.web_service) + "challenge.php");
            else
                httppost = new HttpPost(getResources().getString(R.string.web_service) + "score.php");
            System.out.println("url " + url);
            String str = null;
            try {
                @SuppressWarnings("rawtypes")
                ArrayList localArrayList = new ArrayList();
                if(isChallenge)
                    localArrayList.add(new BasicNameValuePair("challenge", challengerTy + "|" + user_challenger + "|" + user_challenged + "|" + Integer.toString(backTime) + "|" + Integer.toString(score) ));
                else
                    localArrayList.add(new BasicNameValuePair("score", _user + "|" + Integer.toString(score)));
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

                if(isChallenge){
                    pd.dismiss();
                    Intent i = new Intent(Questions.this, UserPage.class);
                    //i.putExtra("back2welcome", false);
                    startActivity(i);
                    finish();
                }
                else{
                    JSONObject jsonObject = new JSONObject(result);
                    int t_score = jsonObject.getInt("t_score");
                    SharedPreferences sharedPreferences = getSharedPreferences("user_score", MODE_PRIVATE);
                    Editor editor = sharedPreferences.edit();
                    editor.putInt("s_score", score);
                    editor.putInt("t_score", t_score);
                    editor.commit();
                    pd.dismiss();
                    Intent i = new Intent(Questions.this, UserPage.class);
                    i.putExtra("back2welcome", false);
                    startActivity(i);
                    finish();
                }

            } catch (NumberFormatException localNumberFormatException) {
            } catch (Exception e) {
            }
            super.onPostExecute(result);
        }
    }

    public class PostJson_Challenge extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String string = postData(null, null);
            return string;
        }

        @SuppressWarnings("unchecked")
        public String postData(String url, JSONObject object) {
            SharedPreferences sharedPreferences = getSharedPreferences("user_id", MODE_PRIVATE);
            String _user = sharedPreferences.getString("user", "");
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getResources().getString(R.string.web_service) + "score.php");
            System.out.println("url " + url);
            String str = null;
            try {
                @SuppressWarnings("rawtypes")
                ArrayList localArrayList = new ArrayList();
                localArrayList.add(new BasicNameValuePair("score", _user + "|" + Integer.toString(score)));
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
                JSONObject jsonObject = new JSONObject(result);
                int t_score = jsonObject.getInt("t_score");
                SharedPreferences sharedPreferences = getSharedPreferences("user_score", MODE_PRIVATE);
                Editor editor = sharedPreferences.edit();
                editor.putInt("s_score", score);
                editor.putInt("t_score", t_score);
                editor.commit();
                pd.dismiss();
                Intent i = new Intent(Questions.this, UserPage.class);
                i.putExtra("back2welcome", false);
                startActivity(i);
                finish();
            } catch (NumberFormatException localNumberFormatException) {
            } catch (Exception e) {
            }
            super.onPostExecute(result);
        }
    }

    public class CountDown extends CountDownTimer {
        public CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            int time = (int) l / 1000;
            t_timer.setText(time + " second left");
            backTime = time;
        }

        @Override
        public void onFinish() {
            Toast.makeText(getApplicationContext(), "time's up", Toast.LENGTH_SHORT).show();
            s_calc();
        }
    }

    public class CountDownForOneQ extends CountDownTimer {
        public CountDownForOneQ (long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            int time = (int) l / 1000;
            q_timer.setText(time + " second left this questions");
            backTime = time;
        }

        @Override
        public void onFinish() {
            q_timer.setTextColor(Color.RED);
            q_timer.setText("0 second left this questions");
        }
    }

    private Boolean exit = false;

    @Override
    public void onBackPressed() {
        if (exit) {
            PostJson_Score json = new PostJson_Score();
            json.execute("jj");
            System.exit(0);
        } else {
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