package com.flexiv.sfino;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.flexiv.sfino.model.Bean_PromotionDetails;
import com.flexiv.sfino.model.Bean_PromotionMaster;
import com.flexiv.sfino.model.Bean_RepDeals;
import com.flexiv.sfino.model.Card_cus_area;
import com.flexiv.sfino.model.MasterDataModal;
import com.flexiv.sfino.model.Modal_Rep;
import com.flexiv.sfino.utill.DBHelper;
import com.flexiv.sfino.utill.DBQ;
import com.flexiv.sfino.utill.SharedPreference;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    private FloatingActionButton fab;
    private BottomAppBar botNaw;
    private View contextView;
    private EditText txtUserName;
    private EditText txtPwd;
    private LazyLoader lazyLoader;
    //ProgressDialog progressDialog;
    private final String TAG = "Login";


    private static final String SPF_NAME = "vidslogin"; //  <--- Add this
    private static final String USERNAME = "username";  //  <--- To save username

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        fab = findViewById(R.id.fab);
        botNaw = findViewById(R.id.botNaw);
        contextView = findViewById(R.id.context_view);
        txtPwd = findViewById(R.id.txtPwd);
        txtUserName = findViewById(R.id.txtUserName);
        lazyLoader = findViewById(R.id.lazyLoader);
        setSupportActionBar(botNaw);

        SharedPreferences loginPreferences = getSharedPreferences(SPF_NAME,
                Context.MODE_PRIVATE);
        txtUserName.setText(loginPreferences.getString(USERNAME, ""));


        botNaw.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MainActivity.fa.finish();
                finish();
            }
        });

        fab.setOnClickListener(v -> {
            //progressDialog = ProgressDialog.show(this, "","Please Wait...", true);
            lazyLoader.setVisibility(View.VISIBLE);
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String imei;
            if (android.os.Build.VERSION.SDK_INT >= 26) {
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                imei = telephonyManager.getImei();
            } else {
                imei = telephonyManager.getDeviceId();
            }
            DBHelper dbHelper = new DBHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            getRepDetails(imei, txtUserName.getText().toString(), txtPwd.getText().toString(), db);

            Log.i(TAG, imei);

        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottonactmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ba_inf:
                return true;

            case R.id.ba_settings:
                boolean flag = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("settings_auth", false);
                if (flag) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Only Authenticated person can access Settings");
                    View viewInflated = LayoutInflater.from(this).inflate(R.layout.sublayout_alertdialog_text, (ViewGroup) getWindow().getDecorView().getRootView(), false);
                    final EditText input = viewInflated.findViewById(R.id.alertDialogText);
                    builder.setView(viewInflated);

                    builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        dialog.dismiss();
                        if (input.getText().toString().equals(SharedPreference.settings_pin)) {
                            Intent i = new Intent(Login.this, SettingsActivity.class);
                            startActivityForResult(i, 101);
                        } else {
                            Toast.makeText(this, "Invalid Password", Toast.LENGTH_LONG).show();
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
                    builder.show();

                } else {
                    Intent i = new Intent(Login.this, SettingsActivity.class);
                    startActivityForResult(i, 101);
                }
                return true;
            case R.id.ba_sync:
                downloadMasterManual();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    /**
     * function id {1=area only, 2=customer only, 3 = area and customer}
     *
     * @param repCode
     */
    private ProgressDialog dialog;

    private void download(String repCode, String imei, String password, SQLiteDatabase db, boolean login) {
        // lazyLoader.setVisibility(View.VISIBLE);
        //progressDialog.setTitle("Downloading Master Data. Please Wait..");
        String url = SharedPreference.URL + "MasterData?discode=" + PreferenceManager.getDefaultSharedPreferences(this).getString("disid", null) + "&repcode=" + repCode + "&functionid=LM" + SharedPreference.SFTYPE;
        System.out.println(url);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Downloading Master Data. Please Wait!");
        dialog.show();

        RequestQueue rq = Volley.newRequestQueue(this);

        JsonObjectRequest jr = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                  //  System.out.println(response.toString());
                    Gson gson = new Gson();
                    MasterDataModal master = gson.fromJson(response.toString(), MasterDataModal.class);

                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            new DBHelper(Login.this).insertMasterData(master);
                            if (login)
                                getRepDetails(imei, repCode, password, db);
                            else {
                                lazyLoader.setVisibility(View.VISIBLE);

                            }
                            if (!SharedPreference.SFTYPE.equals("I"))
                                getPromoMaster(repCode);
                            else {
                                dialog.dismiss();
                                MakeSnackBar("Download Complete!", Color.GREEN);
                            }
                            //dialog.dismiss();
                        }
                    });
                    t.start();


                },
                error -> {
                    System.out.println("Rest Errr :" + error.toString());
                    if (error.toString().contains("No address associated with hostname")) {
                        MakeSnackBar("Can not Connect to the SFINO Server.\nPlease Enter Correct Hostname and WebAPI name", Color.RED);
                    } else {
                        MakeSnackBar(error.toString(), Color.YELLOW);
                    }
                    lazyLoader.setVisibility(View.GONE);
                    dialog.dismiss();
                    //progressDialog.dismiss();

                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                return params;
            }
        };

        rq.add(jr);


    }

    public void MakeSnackBar(String msg, int Color) {
        Snackbar.make(contextView, msg, Snackbar.LENGTH_LONG).setTextColor(Color)
                .show();
    }

    public void getRepDetails(String deviceIMI, String repCode, String password, SQLiteDatabase db) {

        Modal_Rep rep = new Modal_Rep();
        try (Cursor cursor = db.query(DBQ._TBL_REP,
                new String[]{DBQ._TBL_REP_RepCode, DBQ._TBL_REP_Auth, DBQ._TBL_REP_DeviceIMI, DBQ._TBL_REP_Discode, DBQ._TBL_REP_RepName, DBQ._TBL_REP_DisName, DBQ._TBL_REP_RepID},
                DBQ._TBL_REP_RepCode + " = ? AND " + DBQ._TBL_REP_Password + " = ? AND " + DBQ._TBL_REP_Status + "=?",
                new String[]{repCode, password, "A"}, null, null, null)) {
            if (cursor.moveToNext()) {

                rep.setAuth(cursor.getInt(cursor.getColumnIndex(DBQ._TBL_REP_Auth)));
                rep.setRepName(cursor.getString(cursor.getColumnIndex(DBQ._TBL_REP_RepName)));
                rep.setRepID(cursor.getInt(cursor.getColumnIndex(DBQ._TBL_REP_RepID)));
                rep.setDiscode(cursor.getString(cursor.getColumnIndex(DBQ._TBL_REP_Discode)));
                rep.setRepCode(cursor.getString(cursor.getColumnIndex(DBQ._TBL_REP_RepCode)));
                rep.setDisName(cursor.getString(cursor.getColumnIndex(DBQ._TBL_REP_DisName)));

                SharedPreference.COM_REP = rep;

                Log.i(TAG + " Auth ", String.valueOf(rep.getAuth()));
                if (rep.getDiscode().equals(androidx.preference.PreferenceManager.getDefaultSharedPreferences(this).getString("disid", null))) {
                    if (rep.getAuth() != 1) {
                        rep.setDeviceIMI(cursor.getString(cursor.getColumnIndex(DBQ._TBL_REP_DeviceIMI)));
                        if (!rep.getDeviceIMI().equals(deviceIMI)) {
                            MakeSnackBar("Can not sign in using this device.\nPlease use the device provide by the Distributor", Color.RED);
                        } else {
                            this.startActivity(db,repCode);
                        }
                    } else {
                        this.startActivity(db,repCode);
                    }

                } else {
                    MakeSnackBar("Invalid Distributor ID", Color.RED);
                }
                //  lazyLoader.setVisibility(View.GONE);
                // progressDialog.dismiss();
            } else {
//                MakeSnackBar("Invalid RepCode Or Password");
                getRepFromAPI(deviceIMI, repCode, password, db);
            }
        }
        SharedPreferences loginPreferences = getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        loginPreferences.edit().putString(USERNAME, repCode).apply();
    }

    public void downloadMasterManual() {
        lazyLoader.setVisibility(View.VISIBLE);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String imei;
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            imei = telephonyManager.getImei();
        } else {
            imei = telephonyManager.getDeviceId();
        }
        DBHelper dbHelper = new DBHelper(this);
        download(txtUserName.getText().toString(), imei, "password", dbHelper.getWritableDatabase(), false);
    }

    public boolean getRepFromAPI(String imei, String repCode, String password, SQLiteDatabase db) {
        String url = SharedPreference.URL + "User?id=" + repCode + "&password=" + password;
        System.out.println(url);

        RequestQueue rq = Volley.newRequestQueue(this);

        JsonObjectRequest jr = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    System.out.println("Rest Response :" + response.toString());
                    Gson gson = new Gson();
                    Modal_Rep modal_rep = gson.fromJson(response.toString(), Modal_Rep.class);
                    if (!modal_rep.getRepName().contains("Err")) {

                        //CHeck rep distributor
                        if (modal_rep.getDiscode().equals(androidx.preference.PreferenceManager.getDefaultSharedPreferences(this).getString("disid", null))) {
                            if (SaveRepToDB(db, modal_rep))
                                download(repCode, imei, password, db, true);
                            // getRepDetails(imei, repCode, password, db);
                        } else {
//                            if (SaveRepToDB(db, modal_rep))
//                                download(repCode,imei,password,db);
                            MakeSnackBar("Invalid Distributor ID", Color.YELLOW);

                        }

                    } else {
                        MakeSnackBar(modal_rep.getRepCode(), Color.RED);
                        lazyLoader.setVisibility(View.GONE);
                        //    progressDialog.dismiss();
                        db.close();
                    }
                },
                error -> {
                    System.out.println("Rest Errr :" + error.toString());
                    if (error.toString().contains("No address associated with hostname")) {
                        MakeSnackBar("Can not Connect to the SFINO Server.\nPlease Enter Correct Hostname and WebAPI name", Color.RED);
                    } else {
                        MakeSnackBar(error.toString(), Color.YELLOW);
                    }
                    lazyLoader.setVisibility(View.GONE);
                    // progressDialog.dismiss();
                    db.close();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                return params;
            }
        };

        rq.add(jr);
        return false;
    }

    private void startActivity(SQLiteDatabase db,String repcode) {
//        String sql = "SELECT top 1 TourID from TBLM_REPSTOCK where RepCode = '"+repcode+"'";
        String sql = "SELECT TourID from TBLM_REPSTOCK where RepCode = '"+repcode+"' group by TourID order by Createdate DESC\n";
        Cursor c = db.rawQuery(sql,null);
        if(c.moveToNext()){
            SharedPreference.COM_TOUR_X = new Card_cus_area(c.getString(c.getColumnIndex("TourID")),c.getString(c.getColumnIndex("TourID")));
        }

        c.close();
        db.close();
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
        // MainActivity.fa.finish();
        this.finish();
    }

    private boolean SaveRepToDB(SQLiteDatabase db, Modal_Rep rep) {
        try {
            ContentValues c = new ContentValues();
            c.put(DBQ._TBL_REP_Auth, rep.getAuth());
            c.put(DBQ._TBL_REP_DeviceIMI, rep.getDeviceIMI());
            c.put(DBQ._TBL_REP_Discode, rep.getDiscode());
            c.put(DBQ._TBL_REP_Password, rep.getPassword());
            c.put(DBQ._TBL_REP_RepCode, rep.getRepCode());
            c.put(DBQ._TBL_REP_RepID, rep.getRepID());
            c.put(DBQ._TBL_REP_RepName, rep.getRepName());
            c.put(DBQ._TBL_REP_Status, rep.getStatus());
            c.put(DBQ._TBL_REP_DisName, rep.getDisName());
            db.replace(DBQ._TBL_REP, null, c);

            //lazyLoader.setVisibility(View.GONE);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }


    //Download Promo
    public void getPromoMaster(String rep) {
        //String url = "http://" + Common.gvarIp + "/gammawebapi/api/Promo/Promo_Get?id="+ Common.gvarSalesRep;
        String url = SharedPreference.URL + "Promo/Promo_Get?id=" + rep + "&Discode=" + androidx.preference.PreferenceManager.getDefaultSharedPreferences(this).getString("disid", null);
        System.out.println(url);

        runOnUiThread(() -> dialog.setMessage("Downloading Promo..."));

        RequestQueue rq = Volley.newRequestQueue(this);

        JsonObjectRequest jr = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Rest Response :" + response.toString());
                        //activity.makeToas("success");
                        createInvoiceList(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Rest Errr :" + error.toString());

                    }
                }
        ) { //no semicolon or coma
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                return params;
            }
        };

        rq.add(jr);
    }

    private String createInvoiceList(JSONObject response) {

        SQLiteDatabase sqLiteDatabase = null;
        try {
            Gson gson = new Gson();
            JSONArray _master = response.getJSONArray("master");
            JSONArray _details = response.getJSONArray("details");
            JSONArray _deals = response.getJSONArray("deals");

            Type type = new TypeToken<ArrayList<Bean_PromotionMaster>>() {
            }.getType();
            ArrayList<Bean_PromotionMaster> list_master = gson.fromJson(_master.toString(), type);

            type = new TypeToken<ArrayList<Bean_PromotionDetails>>() {
            }.getType();
            ArrayList<Bean_PromotionDetails> list_detals = gson.fromJson(_details.toString(), type);

            type = new TypeToken<ArrayList<Bean_RepDeals>>() {
            }.getType();
            ArrayList<Bean_RepDeals> list_deals = gson.fromJson(_deals.toString(), type);

            DBHelper db = new DBHelper(this);
            sqLiteDatabase = db.getWritableDatabase();
            sqLiteDatabase.beginTransaction();

            //Truncate Table
            sqLiteDatabase.delete(DBQ._PROMOMASTER, null, null);
            sqLiteDatabase.delete(DBQ._PROMODETAILS, null, null);
            sqLiteDatabase.delete(DBQ._TBLM_PROMO_DEALS, null, null);

            //Insert PROMO MASTER
            for (Bean_PromotionMaster bean : list_master
            ) {
                db.Insert_TBLM_PROMO(sqLiteDatabase, bean);
            }

            for (Bean_PromotionDetails bean : list_detals
            ) {
                db.Insert_TBLM_PROMO(sqLiteDatabase, bean);
            }
            for (Bean_RepDeals bean : list_deals
            ) {
                db.Insert_TBLM_PROMO(sqLiteDatabase, bean);
            }

            //Insert PROMO DETAILS
            sqLiteDatabase.setTransactionSuccessful();
            dialog.dismiss();
            MakeSnackBar("Download Complete!", Color.GREEN);
            return "Successfully Downloaded!";
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            return e.getMessage();
        } finally {
            assert sqLiteDatabase != null;
            sqLiteDatabase.endTransaction();
            lazyLoader.setVisibility(View.GONE);
        }


    }
}
