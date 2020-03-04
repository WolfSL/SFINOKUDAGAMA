package com.flexiv.sfino.utill;

import android.content.Context;
import android.preference.PreferenceManager;

import com.flexiv.sfino.model.Modal_Rep;

public class SharedPreference {

    public static String webapi;
    public static String hostname;
    public static String refid;
    public static String disid;
    public static String terminalid;
    public static String printername;
    public static String settings_pin;
    public static String URL;

    public static Modal_Rep COM_REP;

    public static void setSettings(Context context){

        webapi =  PreferenceManager.getDefaultSharedPreferences(context).getString("webapi",null);
        hostname =  PreferenceManager.getDefaultSharedPreferences(context).getString("hostname",null);
        settings_pin =  PreferenceManager.getDefaultSharedPreferences(context).getString("settings_pin",null);

        URL = "http://"+hostname+"/"+webapi+"/api/";

    }

}
