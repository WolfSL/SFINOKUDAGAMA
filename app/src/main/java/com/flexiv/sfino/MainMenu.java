package com.flexiv.sfino;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.flexiv.sfino.adapter.Adapter_Cus_area;
import com.flexiv.sfino.model.Bean_PromotionDetails;
import com.flexiv.sfino.model.Bean_PromotionMaster;
import com.flexiv.sfino.model.Bean_RepDeals;
import com.flexiv.sfino.model.Card_cus_area;
import com.flexiv.sfino.model.MasterDataModal;
import com.flexiv.sfino.model.TBLT_ORDDTL;
import com.flexiv.sfino.utill.DBHelper;
import com.flexiv.sfino.utill.DBQ;
import com.flexiv.sfino.utill.PrinterCommands;
import com.flexiv.sfino.utill.SharedPreference;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import me.anwarshahriar.calligrapher.Calligrapher;

public class MainMenu extends AppCompatActivity implements Runnable {

    private RecyclerView cusRecyclerView;
    private Adapter_Cus_area cusAdapter;
    private RecyclerView.LayoutManager cusLayoutManager;
    private TextView itemSelectTitle;
    private View contextView;
    private TextView textViewCustomer;
    private TextView textViewArea;
    //Card Button
    private CardView cardView_Order;
    private CardView cardView_inv;


    //Printing
    public static BluetoothAdapter mBluetoothAdapter;
    public static BluetoothDevice mBluetoothDevice;
    public static BluetoothSocket mBluetoothSocket;

    private void initComp() {
        //Components
        TextView textViewRep_name = findViewById(R.id.textViewRep_name);
        TextView textViewDis_name = findViewById(R.id.textViewDist_name);
        textViewCustomer = findViewById(R.id.textViewCustomer);
        textViewArea = findViewById(R.id.textViewArea);
        cardView_Order = findViewById(R.id.cardView_Order);
        contextView = findViewById(R.id.context_view);
        cardView_inv = findViewById(R.id.cardView3);

        //set Click
        cardView_Order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SharedPreference.COM_AREA == null) {
                    Snackbar snackbar = Snackbar
                            .make(contextView, "Area Can not be Empty!", Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.RED)
                            .setAction("Select", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    openAreaSelector(view);
                                }
                            });

                    snackbar.show();
                } else if (SharedPreference.COM_CUSTOMER == null) {
                    Snackbar snackbar = Snackbar
                            .make(contextView, "Customer Can not be Empty!", Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.RED)
                            .setAction("Select", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    openCusSelector(view);
                                }
                            });

                    snackbar.show();
                } else {
                    Intent i = new Intent(MainMenu.this, OrderList.class);
                    startActivity(i);
                }
            }
        });

        cardView_inv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SharedPreference.COM_AREA == null) {
                    Snackbar snackbar = Snackbar
                            .make(contextView, "Area Can not be Empty!", Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.RED)
                            .setAction("Select", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    openAreaSelector(view);
                                }
                            });

                    snackbar.show();
                } else if (SharedPreference.COM_CUSTOMER == null) {
                    Snackbar snackbar = Snackbar
                            .make(contextView, "Customer Can not be Empty!", Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.RED)
                            .setAction("Select", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    openCusSelector(view);
                                }
                            });

                    snackbar.show();
                } else {
                    Intent i = new Intent(MainMenu.this, InvList.class);
                    startActivity(i);
                }
            }
        });

        //setArgs
        textViewRep_name.setText(SharedPreference.COM_REP == null ? "Dr.Wolf - Flexiv" : SharedPreference.COM_REP.getRepName());
        textViewDis_name.setText(SharedPreference.COM_REP == null ? "Please Contact Flexiv MicroSystem" : SharedPreference.COM_REP.getDisName());

        // textViewDis_name.startAnimation((Animation) AnimationUtils.loadAnimation(this,R.anim.scroll_anim));

        printerInit();

        if (SharedPreference.SFTYPE.trim().equals("I")) {
            cardView_Order.setVisibility(View.GONE);
        } else if (SharedPreference.SFTYPE.trim().equals("O")) {
            cardView_inv.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        initComp();
    }

    public void openAreaSelector(View v) {

        SharedPreference.COM_CUSTOMER = null;
        openDialogAreaCusSelector(new DBHelper(this).getAreas(SharedPreference.COM_REP.getDiscode()), "SELECT AREA", 0);

    }

    public void openCusSelector(View v) {

        try {
            ArrayList<Card_cus_area> card = new DBHelper(this).getCustomers(SharedPreference.COM_REP.getDiscode(), SharedPreference.COM_AREA.getTxt_code());
            assert card != null;
            if (!card.isEmpty()) {
                openDialogAreaCusSelector(new DBHelper(this).getCustomers(SharedPreference.COM_REP.getDiscode(), SharedPreference.COM_AREA.getTxt_code()), "SELECT CUSTOMER", 1);
            } else {
                Snackbar snackbar = Snackbar
                        .make(contextView, "There no Customers Available in this Area. Please Select another Area!", Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.RED)
                        .setAction("Select", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                openAreaSelector(view);
                            }
                        });

                snackbar.show();
            }
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openDialogAreaCusSelector(ArrayList<Card_cus_area> arr_cus, String title, int type) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this, R.style.mydialog);

        View row = getLayoutInflater().inflate(R.layout.item_selector, null);
        cusRecyclerView = row.findViewById(R.id.itemSelectorRecView);


        alertBuilder.setView(row);
        AlertDialog dialog = alertBuilder.create();


        cusRecyclerView.setHasFixedSize(true);
        cusLayoutManager = new LinearLayoutManager(this);
        cusAdapter = new Adapter_Cus_area(arr_cus, type, dialog);
        cusRecyclerView.setLayoutManager(cusLayoutManager);
        cusRecyclerView.setAdapter(cusAdapter);

        itemSelectTitle = row.findViewById(R.id.itemselectTitale);
        itemSelectTitle.setText(title);
        SearchView sv = row.findViewById(R.id.searchview);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                cusAdapter.getFilter().filter(newText);
                return false;
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (SharedPreference.COM_CUSTOMER != null) {
                    textViewCustomer.setText(SharedPreference.COM_CUSTOMER.getTxt_name());
                } else {
                    textViewCustomer.setText("");
                }
                if (SharedPreference.COM_AREA != null) {
                    textViewArea.setText(SharedPreference.COM_AREA.getTxt_name());
                } else {
                    textViewArea.setText("");
                }
            }
        });
    }


    //Printer
    private static String TAG = "Print Tag";
    //BlueTooth Settings..
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;


    int printstat;

    private ProgressDialog loading;
    AlertDialog.Builder builder;

    ImageButton imageButtonP;
    TextView textViewPrinter;

    int connect;

    private void printerInit() {


        System.out.println("-----------------------------------1");
        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this, "fonts/abel-regular.ttf", true);
        imageButtonP = findViewById(R.id.imageButtonP);
        textViewPrinter = findViewById(R.id.textViewPrinter);

        if (MainMenu.mBluetoothDevice != null) {
            // textViewPS.setText(MainMenu.mBluetoothDevice.getName());
            run();
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        imageButtonP.setOnClickListener(view -> {
            Log.e(TAG, "Enter Print Test");
            if (connect != 1) {
                MainMenu.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (MainMenu.mBluetoothAdapter == null) {
                    Toast.makeText(this, "Message1", Toast.LENGTH_SHORT).show();
                } else {
                    if (!MainMenu.mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent,
                                REQUEST_ENABLE_BT);
                    } else if (MainMenu.mBluetoothDevice == null) {
                        ListPairedDevices();
                        Intent connectIntent = new Intent(this,
                                DeviceList.class);
                        startActivityForResult(connectIntent,
                                REQUEST_CONNECT_DEVICE);

                    }
                }
            }
        });
    }

    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = MainMenu.mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
            }
        }
    }

    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                    MainMenu.mBluetoothDevice = MainMenu.mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                            "Connecting...", MainMenu.mBluetoothDevice.getName() + " : "
                                    + MainMenu.mBluetoothDevice.getAddress(), true, false);
                    Thread mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(this,
                            DeviceList.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(this, "Message", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void run() {
        System.out.println("----------------------2");
        try {

            mBluetoothSocket = MainMenu.mBluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            MainMenu.mBluetoothAdapter.cancelDiscovery();
            if (!mBluetoothSocket.isConnected()) {
                mBluetoothSocket.connect();
                mHandler.sendEmptyMessage(0);
            }
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
            return;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mBluetoothConnectProgressDialog != null) {
                mBluetoothConnectProgressDialog.dismiss();
            }

            connect = 1;
            textViewPrinter.setText("");
            textViewPrinter.setText(mBluetoothDevice.getName());

        }
    };

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }


    protected void printConfig(String bill, int size, int style, int align, OutputStream outputStream, boolean LF) {
        //size 1 = large, size 2 = medium, size 3 = small
        //style 1 = Regular, style 2 = Bold
        //align 0 = left, align 1 = center, align 2 = right


        try {

            byte[] format = new byte[]{27, 33, 0};
            byte[] change = new byte[]{27, 33, 0};

            outputStream.write(format);

            //different sizes, same style Regular
            if (size == 1 && style == 1)  //large
            {
                change[2] = (byte) (0x10); //large
                outputStream.write(change);
            } else if (size == 2 && style == 1) //medium
            {
                //nothing to change, uses the default settings
            } else if (size == 3 && style == 1) //small
            {
                change[2] = (byte) (0x3); //small
                outputStream.write(change);
            }

            //different sizes, same style Bold
            if (size == 1 && style == 2)  //large
            {
                change[2] = (byte) (0x10 | 0x8); //large
                outputStream.write(change);
            } else if (size == 2 && style == 2) //medium
            {
                change[2] = (byte) (0x8);
                outputStream.write(change);
            } else if (size == 3 && style == 2) //small
            {
                change[2] = (byte) (0x3 | 0x8); //small
                outputStream.write(change);
            }


            switch (align) {
                case 0:
                    //left align
                    outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    break;
                case 1:
                    //center align
                    outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    break;
                case 2:
                    //right align
                    outputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    break;
            }
            bill = "  " + bill;
            outputStream.write(bill.getBytes());
            if (LF)
                outputStream.write(PrinterCommands.LF);
        } catch (Exception ex) {
            Log.e("error", ex.toString());
        }
    }

    protected void printNewLine(OutputStream outputStream) {
        try {
            outputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }


    private ProgressDialog dialog;

    public void download(View v) {
        SQLiteDatabase db = new DBHelper(this).getWritableDatabase();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Downloading Master Data. Please Wait!");
        dialog.show();
        String url = SharedPreference.URL + "MasterData?discode=" + PreferenceManager.getDefaultSharedPreferences(this).getString("disid", null) + "&repcode=" + SharedPreference.COM_REP.getRepCode() + "&functionid=LM" + SharedPreference.SFTYPE;
        System.out.println(url);

        RequestQueue rq = Volley.newRequestQueue(this);

        JsonObjectRequest jr = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    Gson gson = new Gson();
                    MasterDataModal master = gson.fromJson(response.toString(), MasterDataModal.class);

                    Thread t = new Thread(() -> {
                        new DBHelper(MainMenu.this).insertMasterData(master);
//                        this.runOnUiThread(() ->{
//                            dialog.dismiss();
//                            Toast.makeText(MainMenu.this,"Download Complete!",Toast.LENGTH_LONG).show();
//                        });
                        if (!SharedPreference.SFTYPE.equals("I"))
                            getPromoMaster(SharedPreference.COM_REP.getRepCode());
                        else {
                            dialog.dismiss();
                        }
                    });
                    t.start();


                },
                error -> {
                    System.out.println("Rest Errr :" + error.toString());
                    Toast.makeText(MainMenu.this, error.toString(), Toast.LENGTH_LONG).show();
                    dialog.setMessage(error.toString());
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
            // MakeSnackBar("Download Complete!",Color.GREEN);
            this.runOnUiThread(() -> {
                dialog.dismiss();
                Toast.makeText(MainMenu.this, "Download Complete!", Toast.LENGTH_LONG).show();
            });
            return "Successfully Downloaded!";
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            this.runOnUiThread(() -> {
                dialog.setMessage("Error In Downloading Data");
                Toast.makeText(MainMenu.this, e.getMessage(), Toast.LENGTH_LONG).show();
            });
            return e.getMessage();
        } finally {
            assert sqLiteDatabase != null;
            sqLiteDatabase.endTransaction();
        }


    }

    public void LogOut(View v) {
        Intent i = new Intent(MainMenu.this, Login.class);
        startActivity(i);
        this.finish();
    }

    public void Stock(View v) {
        Intent i = new Intent(MainMenu.this, StockBal.class);
        startActivity(i);
    }
}
