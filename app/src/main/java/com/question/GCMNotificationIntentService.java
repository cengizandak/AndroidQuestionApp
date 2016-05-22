package com.question;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.MultiAutoCompleteTextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.ArrayList;

public class GCMNotificationIntentService extends IntentService {

	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;
	Boolean isPush = false, isKaza = false, isBera = false, isKayb = false, isWait = false;
	String message;

	public GCMNotificationIntentService() {
		super("GcmIntentService");
	}

	public static final String TAG = "GCMNotificationIntentService";

	@Override
	@SuppressLint("LongLogTag")
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);
		Log.e("test recieve", "------------------------------ Catch");

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification("Deleted messages on server: "
						+ extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {

				for (int i = 0; i < 3; i++) {
					Log.i(TAG,
							"Working... " + (i + 1) + "/5 @ "
									+ SystemClock.elapsedRealtime());
					try {
						Thread.sleep(3);
					} catch (InterruptedException e) {
					}

				}
				Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());

				String messageC = (String) extras.get(Config.MESSAGE_KEY);

				if(messageC.substring(0, 5).equals("@push"))
					isPush = true;
				if(messageC.substring(0, 5).equals("@kaza"))
					isKaza = true;
				if(messageC.substring(0, 5).equals("@bera"))
					isBera = true;
				if(messageC.substring(0, 5).equals("@kayb"))
					isKayb = true;
				if(messageC.substring(0, 5).equals("@wait"))
					isWait = true;

				message = messageC.substring(5);

				if(isKaza || isKayb || isBera)
					sendNotificationSonuc(message);
				else if (isWait)
					sendNotificationWait(message);
				else
					sendNotification(message);

				//sendNotification("From " + extras.get(Config.MESSAGE_KEY));
				Log.i(TAG, "Received: " + extras.toString());
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	@SuppressLint("LongLogTag")
	private void sendNotification(String msg) {
		Log.d(TAG, "Preparing to send notification...: " + msg);
		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

		String msg1, responseS;

		responseS = last(msg);
		msg = removeLast2(msg);

		if(isPush){
			msg1 = "from " + msg;
		}else{
			msg1 = "response form " + msg;
		}


		SharedPreferences sharedPreferencesGCM = getSharedPreferences("gcm", MODE_PRIVATE);
		SharedPreferences.Editor editorGCM = sharedPreferencesGCM.edit();
		editorGCM.putString("userFrom", msg);
		editorGCM.putString("responseS", responseS);
		editorGCM.commit();

		Intent in = new Intent(this, Response.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, in, 0);

		int numMessages = 0;
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.icon)
				.setContentTitle("questions challenge mode")
				.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg1))
				.setAutoCancel(true)
				.setContentText(msg1)
				.setNumber(++numMessages);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		Log.d(TAG, "Notification sent successfully.");
	}

	@SuppressLint("LongLogTag")
	private void sendNotificationSonuc(String msg) {
		Log.d(TAG, "Preparing to send notification...: " + msg);
		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

		String msg1, puan;

		//String[] parts = msg.split("|");
		puan = msg.substring(msg.lastIndexOf("|") + 1);
		msg = msg.substring(0, msg.lastIndexOf("|"));

		//msg = parts[0];
		//puan = parts[1];

		if(isKaza){
			msg1 = "you win " + puan + " " + msg;
		}else if (isBera){
			msg1 = "draws " + puan + " " + msg;
		}else{
			msg1 = "you lose " + puan + " " + msg;
		}


		SharedPreferences sharedPreferencesGCM = getSharedPreferences("gcm", MODE_PRIVATE);
		SharedPreferences.Editor editorGCM = sharedPreferencesGCM.edit();
		editorGCM.putString("userFrom", msg);
		editorGCM.putString("responseS", "s");
		editorGCM.putString("responseSonuc", puan);
		editorGCM.commit();

		Intent in = new Intent(this, EndChallenge.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, in, 0);

		int numMessages = 0;
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.icon)
				.setContentTitle("questions challenge mode result")
				.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg1))
				.setAutoCancel(true)
				.setContentText(msg1)
				.setNumber(++numMessages);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		Log.d(TAG, "Notification sent successfully.");
	}

	@SuppressLint("LongLogTag")
	private void sendNotificationWait(String msg) {
		Log.d(TAG, "Preparing to send notification...: " + msg);
		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

		msg = "waiting " + msg;

		SharedPreferences sharedPreferencesGCM = getSharedPreferences("gcm", MODE_PRIVATE);
		SharedPreferences.Editor editorGCM = sharedPreferencesGCM.edit();
		editorGCM.putString("userFrom", msg);
		editorGCM.putString("responseS", "w");
		editorGCM.commit();

		Intent in = new Intent(this, EndChallenge.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, in, 0);

		int numMessages = 0;
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.icon)
				.setContentTitle("questions challenge mode")
				.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setAutoCancel(true)
				.setContentText(msg)
				.setNumber(++numMessages);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		Log.d(TAG, "Notification sent successfully.");
	}

	public String removeLast2(String str) {
		if (str.length() > 0 ) {
			str = str.substring(0, str.length() - 2);
		}
		return str;
	}
	public String last(String str) {
		if (str.length() > 0 ) {
			str = str.substring(str.length() - 1, str.length());
		}
		return str;
	}
}