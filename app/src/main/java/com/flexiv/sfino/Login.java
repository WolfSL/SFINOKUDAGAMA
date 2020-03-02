package com.flexiv.sfino;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.flexiv.sfino.model.Modal_Rep;
import com.flexiv.sfino.utill.DBHelper;
import com.flexiv.sfino.utill.DBQ;
import com.flexiv.sfino.utill.SharedPreference;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    private FloatingActionButton fab;
    private BottomAppBar botNaw;
    private View contextView;
    private EditText txtUserName;
    private EditText txtPwd;
    private LazyLoader lazyLoader;
    private final String TAG = "Login";

    @SuppressLint("HardwareIds")
    @RequiresApi(api = Build.VERSION_CODES.M)
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


        botNaw.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.fa.finish();
                finish();
            }
        });

        fab.setOnClickListener(v -> {
            lazyLoader.setVisibility(View.VISIBLE);
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String imei;
            if (android.os.Build.VERSION.SDK_INT >= 26) {
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
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

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void MakeSnackBar(String msg, int Color) {
        Snackbar.make(contextView, msg, Snackbar.LENGTH_LONG).setTextColor(Color)
                .show();
    }

    public void getRepDetails(String deviceIMI, String repCode, String password, SQLiteDatabase db) {

        Modal_Rep rep = new Modal_Rep();
        try (Cursor cursor = db.query(DBQ._TBL_REP,
                new String[]{DBQ._TBL_REP_RepCode, DBQ._TBL_REP_Auth, DBQ._TBL_REP_DeviceIMI, DBQ._TBL_REP_Discode, DBQ._TBL_REP_RepName},
                DBQ._TBL_REP_RepCode + " = ? AND " + DBQ._TBL_REP_Password + " = ? AND " + DBQ._TBL_REP_Status + "=?",
                new String[]{repCode, password, "A"}, null, null, null)) {
            if (cursor.moveToNext()) {
                rep.setAuth(cursor.getInt(cursor.getColumnIndex(DBQ._TBL_REP_Auth)));
                Log.i(TAG + " Auth ", String.valueOf(rep.getAuth()));
                if (rep.getAuth() != 1) {
                    rep.setDeviceIMI(cursor.getString(cursor.getColumnIndex(DBQ._TBL_REP_DeviceIMI)));
                    if (!rep.getDeviceIMI().equals(deviceIMI)) {
                        MakeSnackBar("Can not sign in using this device.\nPlease use the device provide by the Distributor", Color.RED);
                    }else{
                        this.startActivity(db);
                    }
                } else {
                    this.startActivity(db);
                }
                rep.setRepCode(cursor.getString(cursor.getColumnIndex(DBQ._TBL_REP_RepCode)));
                rep.setDiscode(cursor.getString(cursor.getColumnIndex(DBQ._TBL_REP_Discode)));
                rep.setRepName(cursor.getString(cursor.getColumnIndex(DBQ._TBL_REP_RepName)));
                lazyLoader.setVisibility(View.GONE);
            } else {
//                MakeSnackBar("Invalid RepCode Or Password");
                getRepFromAPI(deviceIMI, repCode, password, db);
            }
        }
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
                        if (SaveRepToDB(db, modal_rep))
                            getRepDetails(imei, repCode, password, db);
                    } else {
                        MakeSnackBar(modal_rep.getRepCode(), Color.RED);
                        lazyLoader.setVisibility(View.GONE);
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

    private void startActivity(SQLiteDatabase db){
        db.close();
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
        MainActivity.fa.finish();
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
            c.put(DBQ._TBL_REP_RepName, rep.getRepName());
            c.put(DBQ._TBL_REP_Status, rep.getStatus());
            db.replace(DBQ._TBL_REP, null, c);

            lazyLoader.setVisibility(View.GONE);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}