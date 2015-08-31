package com.byethost13.skeleton;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

	//Application Context
	Context mContext;
	
	protected static String TAG = Preferences.class.getSimpleName();
	
	//SharedPreferences to read and edit
	SharedPreferences sp;
	SharedPreferences.Editor spEdit;
	
	//singleton instance
	private static Preferences instance;
	
	// create a new instance of Preferences
	public static Preferences init(Context context){
		instance = new Preferences(context);
		return instance;
	}
	
	//get preferences singleton
	public static Preferences getIns(){
		if (instance == null)
			throw new IllegalStateException("You must initialize Preferences before using it");
		return instance;
	}
		
	//get preferences singleton
	public static Preferences getIns(Context context){
		if(instance == null)
			init(context);
		return instance;
	}
	
	
	private Preferences(Context context){
		mContext = 	context;
		sp = 		context.getSharedPreferences(TAG, 0);
		spEdit = 	sp.edit();
		
		
	}
	
	public SharedPreferences getSharedPrefs(){
		return sp;
	}
	
	public SharedPreferences.Editor getSharedPrefsEditor(){
		return spEdit;
	}
	
	/** Simpler method to get saved boolean
	 * @param id resource id containing the key string
	 * @param Default default value in case there is nothing stored with given key*/
	@SuppressWarnings("unused")
	private boolean getBool(int id, boolean Default){
		return sp.getBoolean(mContext.getString(id), Default);
	}
	
	/** Simpler method to get saved int
	 * @param id resource id containing the key string
	 * @param Default default value in case there is nothing stored with given key*/
	@SuppressWarnings("unused")
	private int getInt(int id, int Default){
		return sp.getInt(mContext.getString(id),Default);
	}
}
