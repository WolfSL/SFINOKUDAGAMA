package com.flexiv.sfino.utill;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.flexiv.sfino.model.Area_Modal;
import com.flexiv.sfino.model.Customer_Modal;
import com.flexiv.sfino.model.MasterDataModal;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SFINO.db";
    private static final int DATABASE_VERSION = 14;


    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public synchronized void close() {
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBQ.CREATE_TBL_REP);
        db.execSQL(DBQ.CREATE_TBLM_AREA);
        db.execSQL(DBQ.CREATE_TBLM_CUSTOMER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String DELETE = "drop table if exists ";
        db.execSQL(DELETE+DBQ._TBL_REP);
        db.execSQL(DELETE+DBQ._TBLM_AREA);
        db.execSQL(DELETE+DBQ._TBLM_CUSTOMER);

        onCreate(db);
    }


    /*
     * Master Data Insertion
     */
    public void insertMasterData(MasterDataModal data){

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues c = new ContentValues();

            //Insert Area
            for (Area_Modal area : data.getAreaList()) {

                c.put(DBQ._TBLM_AREA_AreaCode, area.getAreaCode());
                c.put(DBQ._TBLM_AREA_AreaName, area.getAreaName());
                c.put(DBQ._TBLM_AREA_DisCode, area.getDisCode());

                db.replace(DBQ._TBLM_AREA, null, c);
                c.clear();
            }

            //Insert Customer
            for (Customer_Modal cus : data.getCustomerList()) {

                c.put(DBQ._TBLM_CUSTOMER_AreaCode, cus.getAreaCode());
                c.put(DBQ._TBLM_CUSTOMER_CusCode, cus.getCusCode());
                c.put(DBQ._TBLM_CUSTOMER_CusName, cus.getCusName());
                c.put(DBQ._TBLM_CUSTOMER_Discode, cus.getDiscode());
                c.put(DBQ._TBLM_CUSTOMER_VAT, cus.getVAT());

                db.replace(DBQ._TBLM_CUSTOMER, null, c);
                c.clear();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
