package com.flexiv.sfino.utill;

import android.content.Context;

import androidx.preference.PreferenceManager;

import com.flexiv.sfino.model.Card_cus_area;
import com.flexiv.sfino.model.Modal_Rep;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;


public class SharedPreference {

    public static  DecimalFormat df = new DecimalFormat("0.00##");
    public static String dateFormat_Format1 = "yyyy-MM-dd hh:mm:ss a";
    public static DateFormat dateFormat = new SimpleDateFormat(dateFormat_Format1);


    public static String refid;
    public static String disid;
    public static String terminalid;
    public static String printername;
    public static String settings_pin;
    public static String URL;
    public static String webapi;
    public static String hostname;

    public static Modal_Rep COM_REP;


    public static Card_cus_area COM_CUSTOMER;
    public static Card_cus_area COM_AREA;

    public static void setSettings(Context context){

        webapi =  PreferenceManager.getDefaultSharedPreferences(context).getString("webapi",null);
        hostname =  PreferenceManager.getDefaultSharedPreferences(context).getString("hostname",null);
        settings_pin =  PreferenceManager.getDefaultSharedPreferences(context).getString("settings_pin",null);

        disid =  PreferenceManager.getDefaultSharedPreferences(context).getString("disid",null);



        URL = "http://"+hostname+"/"+webapi+"/api/";

    }

}
