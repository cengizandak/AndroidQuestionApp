package com.question;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class EndChallenge extends Activity {
    String get, puan, responseS, userFrom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_challenge);

        SharedPreferences sharedPreferencesGCM = getSharedPreferences("gcm",MODE_PRIVATE);
        get = sharedPreferencesGCM.getString("userFrom", "");
        responseS = sharedPreferencesGCM.getString("responseS", "");
        puan = sharedPreferencesGCM.getString("responseSonuc", "");
        userFrom = get;

        if(responseS.equals("s")){
            SharedPreferences sharedPreferences = getSharedPreferences("user_score", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("t_score", Integer.parseInt(puan));
            editor.commit();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EndChallenge.this);
            alertDialogBuilder.setTitle("questions challenge mode result");
            alertDialogBuilder
                    .setMessage(puan + " points against " + get)
                    .setCancelable(false)
                    .setPositiveButton("oke", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            quit();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }else{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EndChallenge.this);
            alertDialogBuilder.setTitle("questions challenge mode waiting");
            alertDialogBuilder
                    .setMessage(get)
                    .setCancelable(false)
                    .setPositiveButton("oke", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            quit();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }
    }

    private void welcome(){
        Intent i = new Intent(EndChallenge.this, Welcome.class); //welcome sayfasina gidiyor
        startActivity(i);
        finish();
    }
    private void quit(){
        System.exit(0);
    }
}
