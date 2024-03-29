package com.byethost13.skeleton;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ServerCom extends server.ServerCom{

	private static ServerCom instance;
	
	//let the user have many instances so each one has different headers/properties
	public ServerCom(){	}
	
	public static ServerCom shared(){
		if(instance == null){
			instance = new ServerCom();
		}
		return instance;
	}
	
	@Override
	public boolean isNetworkAvailable() {
		boolean connected = false;
		ConnectivityManager cm = (ConnectivityManager) Platform.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		if ( cm != null ){
			//this method iterates thru all the networks available to check which one is connected
	//		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	//		for (NetworkInfo ni : netInfo) 
	//			if ( ni.isConnected() /*&& ni.getTypeName().equalsIgnoreCase("WIFI")*/ )
	//				connected = true;
			
			//this method gets the active network and checks if it is connected
			NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
			connected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
		}
		return connected;
	}
}