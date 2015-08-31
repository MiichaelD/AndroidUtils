package com.byethost13.skeleton;

import java.util.Calendar;

import com.byethost13.skeleton.androidutils.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public abstract class Notifier {
	
	static int NO_NOTIF = -1,  NOTIFICATION_ID = NO_NOTIF;
	
	Context mContext;
	
	NotificationManager mNotifMan = null;
	
	public static String ACTIVITY = "activity", TITLE = "title", TICKER = "ticker", WHEN = "when";
	public static String MESSAGE = "message", CONTENT_INFO = "content_info", NOTIF_KEY = "last_notif_id";
	public static String SMALL_ICON = "small", BIG_ICON = "big", VIBRATE = "vibrate", SOUND = "sound";
	public static String DIRECT_TO_GAME = "go_to_gamestate";
	
	
	protected int m_bigIconAsset = R.drawable.ic_launcher, m_smallIconAsset = android.R.drawable.ic_menu_crop;
	protected String m_defaultTitle = "Default";
	
	public Notifier(Context ctx){
		mContext = ctx;
		mNotifMan = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		SharedPreferences sp = getSharedPrefs();
		NOTIFICATION_ID = sp.getInt(NOTIF_KEY, NO_NOTIF);
	}
	
	/** Subclasses MUST define this function.
	 * @return shared_prefs object to store values locally*/
	protected abstract SharedPreferences getSharedPrefs();
	
	protected boolean showNotifications(){
		return true;
	}
	
	public static Notifier getInstance(Context ctx){
		return null;
	}
	   
	public void notify(String title, String message){
		final Bundle bundle = new Bundle();
		bundle.putString(TITLE, title);
		bundle.putString(MESSAGE, message);
		bundle.putString(TICKER, message);
		new Thread(){ public void run(){
			Notifier.this.notify(bundle);
		}}.start();
	}
	
	public void notify(String title, String message, int smallIcon, int largeIcon, Class<? extends Activity> classToStart){
		final Bundle bundle = new Bundle();
		bundle.putString(MESSAGE, message);
		bundle.putString(TITLE, title);
		bundle.putInt(NOTIF_KEY, ++NOTIFICATION_ID);
		bundle.putString(TICKER, message);
		bundle.putInt(SMALL_ICON, m_smallIconAsset);
		bundle.putInt(BIG_ICON, m_bigIconAsset);
		bundle.putString(ACTIVITY, classToStart.getCanonicalName());
		new Thread(new Runnable(){ public void run(){
			Notifier.this.notify(bundle);
		}}).start();
	}

	public void schedule(String title, String message, Class<? extends Activity> classToStart, int mins, boolean directToGame){
		Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.MINUTE, mins);

	    Intent intent = new Intent(mContext, AlarmReceiver.class);
	    intent.putExtra(MESSAGE, message);
	    intent.putExtra(TITLE, title);
	    intent.putExtra(NOTIF_KEY, ++NOTIFICATION_ID);
	    intent.putExtra(TICKER, message);
	    intent.putExtra(SMALL_ICON, m_smallIconAsset);
	    intent.putExtra(BIG_ICON, m_bigIconAsset);
	    intent.putExtra(ACTIVITY, classToStart.getCanonicalName());
	    if(directToGame)
	    	intent.putExtra(DIRECT_TO_GAME, true);

	    PendingIntent sender = PendingIntent.getBroadcast(mContext, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	    AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
	    am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);

		SharedPreferences.Editor spe =getSharedPrefs().edit();
		Log.v("Notifier", "We just Scheduled notif: "+NOTIFICATION_ID);
		spe.putInt(NOTIF_KEY, NOTIFICATION_ID);
		spe.commit();
	}
	
	public void notify(Bundle bundle){
		if (!showNotifications())
			return;
		
		//get variables from bundle
		int smallIcon = bundle.getInt(SMALL_ICON, m_smallIconAsset);
		int bigIcon = bundle.getInt(BIG_ICON, m_bigIconAsset);
		int notifId = bundle.getInt(NOTIF_KEY, ++NOTIFICATION_ID);
		String targetActivity = bundle.getString(ACTIVITY);
		String title = bundle.getString(TITLE);
		String message = bundle.getString(MESSAGE);
		String ticker = bundle.getString(TICKER);
		String contentInfo = bundle.getString(CONTENT_INFO);
		String when = bundle.getString(WHEN);
		boolean vibrate = bundle.getBoolean(VIBRATE);
		boolean sound = bundle.getBoolean(SOUND);
	    Class<? extends Activity> activityClass = null;
	    
	    if (message == null || message.length() == 0){
	    	Log.e(Notifier.class.getSimpleName(),"Message is null, cancelling sending notification.");
	    	return;
	    }
	    if(title == null || title.length() == 0){
	    	title = m_defaultTitle;
	    }
	    if (ticker == null || ticker.length() == 0){
	    	ticker = message;
	    }
	    if (contentInfo == null){
	    	contentInfo = Integer.toString(notifId);
	    }
	    
		try {
			if (targetActivity != null)
				activityClass = Class.forName(targetActivity).asSubclass(Activity.class);
		} catch (ClassNotFoundException e) { }
		
		Log.v("Notifier", "Starting notification: "+notifId);
		long t0 = System.currentTimeMillis();
		
		int notifFlags = NotificationCompat.DEFAULT_LIGHTS |  NotificationCompat.FLAG_AUTO_CANCEL | NotificationCompat.FLAG_ONLY_ALERT_ONCE;
		
	    //creating notification
		NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
		.setAutoCancel(true)
		.setSmallIcon(smallIcon)
		.setLargeIcon(BitmapLoader.getImage(mContext, bigIcon, true))
		.setContentTitle(title)
		.setContentText(message)
		.setTicker(ticker)
		.setWhen(when == null ? System.currentTimeMillis() : Long.parseLong(when)*1000)
		.setContentInfo(contentInfo)
		.setStyle(new NotificationCompat.BigTextStyle() // text to be displayed when expanded
			.setBigContentTitle(title)
			.bigText(message));
		
		if (vibrate){
			notifFlags |= NotificationCompat.DEFAULT_VIBRATE;
			builder.setVibrate(new long[] { 1000, 1000});
		}if (sound){
			notifFlags |= NotificationCompat.DEFAULT_SOUND;
			builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		}
		builder.setDefaults(notifFlags);
		
		//adding an action to the notification
		if (activityClass != null){
			Intent notIntent = new Intent(mContext, activityClass);
			notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			if(bundle.getBoolean(DIRECT_TO_GAME))
				notIntent.putExtra(DIRECT_TO_GAME, true);
			PendingIntent contIntent = PendingIntent.getActivity(mContext, NOTIFICATION_ID, notIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(contIntent);
		}
	   
		Notification notif = builder.build();
		
		//send the notification
		mNotifMan.notify(notifId, notif);
		saveNotifId();
		Log.v(this.getClass().getSimpleName(), "finihed in: "+(System.currentTimeMillis()-t0));
	}
	
	
	/** Clear pending and received notifications if we don't longer need them*/
	public void clearAll() {
		if (NOTIFICATION_ID == NO_NOTIF)
			return;
		  
	    Intent intent = new Intent(mContext, AlarmReceiver.class);
	    AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

	    //clear scheduled notifications 
	    for (int ind = 0; ind <= NOTIFICATION_ID; ++ind) {
	    	try {
	    		PendingIntent sender = PendingIntent.getBroadcast(mContext, ind, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	    		am.cancel(sender);
	    		Log.v("Notifier", "Cancelling pending notification: "+ind);
	    	} catch (Exception e) { }
	    }

	    //clear notifications already received
	    if (mNotifMan!=null) 
	    	mNotifMan.cancelAll();

		Log.v("Notifier", "Clearing notifs already in the drawer and resetting counter to 0");
		NOTIFICATION_ID = NO_NOTIF;
		
		SharedPreferences.Editor spe = getSharedPrefs().edit();
		spe.putInt(NOTIF_KEY, NO_NOTIF);
		spe.commit();
	}
	
	public void saveNotifId(){
		SharedPreferences sp = getSharedPrefs();
		int stored = sp.getInt(NOTIF_KEY, NO_NOTIF);
		if ( stored < NOTIFICATION_ID){
			SharedPreferences.Editor spe = sp.edit();
			spe.putInt(NOTIF_KEY, NOTIFICATION_ID);
			spe.commit();
		}
	}
}
