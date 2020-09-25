package com.flexiv.sfino.utill;

import android.content.Context;

import androidx.preference.PreferenceManager;

import com.flexiv.sfino.model.Card_cus_area;
import com.flexiv.sfino.model.Modal_Rep;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class SharedPreference {

    public final static String varID = "1.0.3";

    public static  DecimalFormat df = new DecimalFormat("0.00");
    public static String dateFormat_Format1 = "yyyy-MM-dd hh:mm:ss a";
    public static String dateFormat_Format2 = "MMddhhmmss";
    public static DateFormat dateFormat = new SimpleDateFormat(dateFormat_Format1);
    public static DateFormat dateFormat2 = new SimpleDateFormat(dateFormat_Format2);

    public final static SimpleDateFormat Date_serverFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    public final static SimpleDateFormat Date_App_Format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);


    public static String address = "No 69, kegalle Road, Rambukkana";
    public static String tp = "035 22 64 246";

//    public static String refid;
//    public static String disid;
//    public static String DisAID;
//    public static String printername;
    public static String settings_pin;
    public static String URL;
    public static String webapi;
    public static String hostname;
    public static String SFTYPE;

    public static Modal_Rep COM_REP;

    public static Card_cus_area COM_CUSTOMER;
    public static Card_cus_area COM_AREA;
    public static Card_cus_area COM_BANK;
    public static Card_cus_area COM_TOUR_X;


    public static DecimalFormat ds_formatter = new DecimalFormat("###,##0.00");


    public static void setSettings(Context context){


        webapi =  PreferenceManager.getDefaultSharedPreferences(context).getString("webapi",null);
        hostname =  PreferenceManager.getDefaultSharedPreferences(context).getString("hostname",null);
        settings_pin =  PreferenceManager.getDefaultSharedPreferences(context).getString("settings_pin",null);
        SFTYPE =PreferenceManager.getDefaultSharedPreferences(context).getString("type","-1");

        System.out.println(SFTYPE);

//        disid =  PreferenceManager.getDefaultSharedPreferences(context).getString("disid",null);



        URL = "http://"+hostname+"/"+webapi+"/api/";

    }

}
