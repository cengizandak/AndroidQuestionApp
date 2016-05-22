package com.question;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class UserPage extends Activity {
    TextView t_score, s_score, s_use, c_score, c_use;
    //Button back;
    Boolean back2welcome;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userpage);
        back2welcome = getIntent().getBooleanExtra("back2welcome",true);
        t_score = (TextView) findViewById(R.id.textView1);
        s_use = (TextView) findViewById(R.id.textView3);
        c_use = (TextView) findViewById(R.id.textView5);
        //back = (Button) findViewById(R.id.buttonBack);

        /*if(!back2welcome)
            back.setEnabled(false);*/

        SharedPreferences sharedPreferences = getSharedPreferences("user_score",MODE_PRIVATE);
        SharedPreferences sharedPreferencesS = getSharedPreferences("user_s",MODE_PRIVATE);
        SharedPreferences sharedPreferencesC = getSharedPreferences("user_c",MODE_PRIVATE);
        SharedPreferences sharedPreferencesCT = getSharedPreferences("count_time",MODE_PRIVATE);

        t_score.setText("total score: " + sharedPreferences.getInt("t_score",0));

        if(sharedPreferencesS.getBoolean("pressed", false)){
            //s_score.setText("single session score: " + sharedPreferencesS.getInt("s_score",0));
            s_use.setText("single remaining daily use: " + sharedPreferencesS.getInt("s_count",0));
        }else{
            //s_score.setText("single session score: " + sharedPreferencesCT.getInt("s_score",0));
            s_use.setText("single remaining daily use: " + (10 - sharedPreferencesCT.getInt("s_count",0)));
        }

        if(sharedPreferencesC.getBoolean("pressed", false)){
            //c_score.setText("challenge session score: " + sharedPreferencesC.getInt("c_score",0));
            c_use.setText("challenge remaining daily use: " + sharedPreferencesC.getInt("c_count",0));
        }else{
            //c_score.setText("challenge session score: " + sharedPreferencesCT.getInt("c_score",0));
            c_use.setText("challenge remaining daily use: " + (10 - sharedPreferencesCT.getInt("c_count",0)));
        }
    }


	public void score_table_func(View v){
		Intent i=new Intent(UserPage.this, Score.class);
		startActivity(i);
        finish();
	}

    public void back_welcome_func(View v){
        Intent i = new Intent(UserPage.this, Welcome.class);
        startActivity(i);
        finish();
    }



    /*private Boolean exit = false;
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
    }*/

    @Override
    public void onBackPressed() {
        Intent i=new Intent(UserPage.this,Welcome.class);
        startActivity(i);
        finish();
    }
}
