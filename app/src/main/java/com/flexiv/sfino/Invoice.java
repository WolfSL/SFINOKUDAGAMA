package com.flexiv.sfino;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.flexiv.sfino.adapter.Adapter_Batch;
import com.flexiv.sfino.adapter.Adapter_Item;
import com.flexiv.sfino.fragment.Inv_main;
import com.flexiv.sfino.fragment.Inv_sub;
import com.flexiv.sfino.model.DefModal;
import com.flexiv.sfino.model.MasterDataModal;
import com.flexiv.sfino.model.Modal_Batch;
import com.flexiv.sfino.model.Modal_Item;
import com.flexiv.sfino.model.TBLT_SALINVDET;
import com.flexiv.sfino.model.TBLT_SALINVHED;
import com.flexiv.sfino.utill.DBHelper;
import com.flexiv.sfino.utill.DBQ;
import com.flexiv.sfino.utill.Fragment_sub_batching;
import com.flexiv.sfino.utill.PrinterCommands;
import com.flexiv.sfino.utill.SharedPreference;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Invoice extends AppCompatActivity implements Fragment_sub_batching {

    String TAG = "INVOICE";
    //Components
    ImageButton imageButton_done;
    ImageButton imageButton_back;

    TextView textView_cusName,textViewTourID;
    TextView textView_Area;
    private TextView textView_InvNo;

    private TBLT_SALINVHED obj = null;

    public TBLT_SALINVHED getObj() {
        return obj;
    }

    public void setObj(TBLT_SALINVHED obj) {
        this.obj = obj;
    }

    public TextView getTextView_InvNo() {
        return textView_InvNo;
    }

    private ArrayList<TBLT_SALINVDET> ItemList = new ArrayList<>();

    public ArrayList<TBLT_SALINVDET> getItemList() {
        return ItemList;
    }

    public void setItemList(TBLT_SALINVDET itemList) {
        ItemList.add(itemList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        imageButton_done = findViewById(R.id.imageButton_done);
        imageButton_done.setEnabled(false);
        imageButton_back = findViewById(R.id.imageButton_back);

        textView_cusName = findViewById(R.id.textView_cusName);
        textView_Area = findViewById(R.id.textView_Area);
        textView_InvNo = findViewById(R.id.textView_InvNo);
        textViewTourID = findViewById(R.id.textViewTourID);



        try {
            textViewTourID.setText(SharedPreference.COM_TOUR_X.getTxt_code());
        }catch (Exception e){
            Toast.makeText(this,"Invalid Tour. Please Contact Hed Office",Toast.LENGTH_LONG).show();
            this.finish();
        }

        if (SharedPreference.COM_AREA != null && SharedPreference.COM_CUSTOMER != null) {
            textView_cusName.setText(SharedPreference.COM_CUSTOMER.getTxt_name());
            textView_Area.setText(SharedPreference.COM_AREA.getTxt_name());
        } else {
            Toast.makeText(this, "Customer or Area Cannot be Empty", Toast.LENGTH_LONG).show();
            super.onBackPressed();
        }


        Intent intent = getIntent();
        obj = (TBLT_SALINVHED) intent.getSerializableExtra("hed");

        if (obj != null) {
            String msg = "INV Ref No. " + obj.getRefNo();
            textView_InvNo.setText(msg);
            textViewTourID.setText(obj.getTourID());
            imageButton_done.setEnabled(true);
            LoadItemsFromDB(obj.getDocNo());
        }

        getSupportFragmentManager().beginTransaction().add(R.id.OrderFrame, new Inv_main(this)).commit();

        //TODO
        //PRINTER
        //printerInit();
        TextView tv = findViewById(R.id.textViewPS2);
        if (MainMenu.mBluetoothSocket != null)
            if (MainMenu.mBluetoothSocket.isConnected()) {

                tv.setText("Connected");
            }
        imageButton_done.setOnClickListener(view -> {
            if (tv.getText().equals("Connected"))
                p2();
            else
                Toast.makeText(Invoice.this,"Please Connet the printer",Toast.LENGTH_LONG).show();
        });

    }

    public void changeNavButton(int type) {
        if (type == 1) {
            imageButton_done.animate().alpha(1.0f);
            imageButton_back.setOnClickListener(v -> mainBackPress());
        } else if (type == 2) {
            imageButton_done.animate().alpha(0.0f);
            imageButton_back.setOnClickListener(v -> {
                imageButton_done.animate().alpha(1.0f);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.animator.in, R.animator.out);
                transaction.replace(R.id.OrderFrame, new Inv_main(this)).commit();
            });
        }
    }

    private void mainBackPress() {
        onBackPressed();
    }

    public void GoBackWithItems() {
        imageButton_done.animate().alpha(1.0f);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.in, R.animator.out);
        transaction.replace(R.id.OrderFrame, new Inv_main(this)).commit();
    }


    public void LoadFragment_sub(int invStatus) {

        try {
            DBHelper db = new DBHelper(this);
            openDialogSelector_Items(db.getItems(SharedPreference.COM_REP.getDiscode(),SharedPreference.COM_REP.getRepCode(),invStatus), "SELECT ITEM", 0,invStatus);
            db.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    RecyclerView SelectorrecyclerView;
    private Adapter_Item adapter_item;
    private RecyclerView.LayoutManager cusLayoutManager;
    private TextView itemSelectTitle;

    private void openDialogSelector_Items(ArrayList<Modal_Item> arr_cus, String title, int type,int invStatus) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this, R.style.mydialog);

        View row = getLayoutInflater().inflate(R.layout.item_selector, null);
        SelectorrecyclerView = row.findViewById(R.id.itemSelectorRecView);


        alertBuilder.setView(row);
        AlertDialog dialog = alertBuilder.create();


        // SelectorrecyclerView.setHasFixedSize(true);
        cusLayoutManager = new LinearLayoutManager(this);
        adapter_item = new Adapter_Item(arr_cus, dialog, this,invStatus);
        SelectorrecyclerView.setLayoutManager(cusLayoutManager);
        SelectorrecyclerView.setAdapter(adapter_item);

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
                adapter_item.getFilter().filter(newText);
                return false;
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

    }


    //Load Batchs
    @Override
    public void LoadFragment_sub_batchs(String itemCode,int invStatus) {

        try {
            DBHelper db = new DBHelper(this);
            ArrayList<Modal_Batch> arr_cus;
            if(invStatus==1){
                arr_cus = db.getBatchWiceStock(SharedPreference.COM_REP.getDiscode(),itemCode);
            }else{
                arr_cus =db.getRepStock(SharedPreference.COM_REP.getDiscode(), itemCode,SharedPreference.COM_REP.getRepCode());
            }
            openDialogSelector_batchs(arr_cus, "SELECT BATCH", 0,invStatus);
            db.close();
        } catch (Exception es) {
            es.printStackTrace();
        }


    }

    private Adapter_Batch adapter_batch;

    private void openDialogSelector_batchs(ArrayList<Modal_Batch> arr_cus, String title, int type,int invStatus) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this, R.style.mydialog);

        View row = getLayoutInflater().inflate(R.layout.item_selector, null);
        SelectorrecyclerView = row.findViewById(R.id.itemSelectorRecView);


        alertBuilder.setView(row);
        AlertDialog dialog = alertBuilder.create();


        // SelectorrecyclerView.setHasFixedSize(true);
        cusLayoutManager = new LinearLayoutManager(this);
        adapter_batch = new Adapter_Batch(arr_cus, dialog, this, invStatus);
        SelectorrecyclerView.setLayoutManager(cusLayoutManager);
        SelectorrecyclerView.setAdapter(adapter_batch);

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
                adapter_batch.getFilter().filter(newText);
                return false;
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

    }

    public void LoadOrderSub(Modal_Batch item,int invStatus) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.in, R.animator.out);
        transaction.replace(R.id.OrderFrame, new Inv_sub(this, item,invStatus)).commit();
        //getSupportFragmentManager().beginTransaction().replace(R.id.OrderFrame,new Order_sub(this,item)).commit();
    }

    @Override
    public void onBackPressed() {
        if (!ItemList.isEmpty()) {
            new AlertDialog.Builder(this).setTitle("Confirm Exit!")
                    .setMessage("Are you sure you want to close this Invoice with out Saving?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Invoice.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("No", null)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    public void onBackPressed2() {
        super.onBackPressed();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public String Save(TBLT_SALINVHED hed) {

        System.out.println(hed.toString());
        String msg;
        if (!ItemList.isEmpty()) {
            DBHelper dbHelper = new DBHelper(this);

            //Get Last Order No
            SQLiteDatabase db;
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                String maxNo = getMaxDocNo(db);
                if (Integer.parseInt(maxNo) > 0) {
                    ContentValues cv = new ContentValues();


                    cv.put(DBQ._TBLT_INVHED_DocNo, maxNo);
                    cv.put(DBQ._TBLT_INVHED_Discode, hed.getDiscode());
                    cv.put(DBQ._TBLT_INVHED_DocType, 6);
                    cv.put(DBQ._TBLT_INVHED_SalesDate, hed.getSalesDate());
                    cv.put(DBQ._TBLT_INVHED_SupCode,"");
                    cv.put(DBQ._TBLT_INVHED_CusCode,hed.getCusCode());
                    cv.put(DBQ._TBLT_INVHED_RepCode,hed.getRepCode());
                    cv.put(DBQ._TBLT_INVHED_RefNo,"V"+maxNo);
                    cv.put(DBQ._TBLT_INVHED_InvType,1);
                    cv.put(DBQ._TBLT_INVHED_LocCode,"00001");
                    cv.put(DBQ._TBLT_INVHED_CrDrType,hed.getCrDrType());

                    cv.put(DBQ._TBLT_INVHED_DueDate,hed.getDueDate());
                    cv.put(DBQ._TBLT_INVHED_DueAmount,hed.getDueAmount());
                    cv.put(DBQ._TBLT_INVHED_GrossAmt,hed.getGrossAmt());
                    cv.put(DBQ._TBLT_INVHED_AddTax1,hed.getAddTax1());
                    cv.put(DBQ._TBLT_INVHED_AddTax2,hed.getAddTax2());
                    cv.put(DBQ._TBLT_INVHED_AddTax3,hed.getAddTax3());
                    cv.put(DBQ._TBLT_INVHED_DedAmt1,hed.getDedAmt1());
                    cv.put(DBQ._TBLT_INVHED_DedAmt2,hed.getDedAmt2());
                    cv.put(DBQ._TBLT_INVHED_DedAmt3,hed.getDedAmt3());
                    cv.put(DBQ._TBLT_INVHED_NetAmt,hed.getNetAmt());
                    cv.put(DBQ._TBLT_INVHED_RefDueAmt,hed.getRefDueAmt());
                    cv.put(DBQ._TBLT_INVHED_Discount,hed.getDiscount());
                    cv.put(DBQ._TBLT_INVHED_VatAmt,hed.getVatAmt());
                    cv.put(DBQ._TBLT_INVHED_OrdRefNo,hed.getOrdRefNo());
                    cv.put(DBQ._TBLT_INVHED_PayType,"");
                    cv.put(DBQ._TBLT_INVHED_CreateUser,hed.getCreateUser());
                    cv.put(DBQ._TBLT_INVHED_GLTransfer,hed.isGLTransfer());
                    cv.put(DBQ._TBLT_INVHED_Status,"S");
                    cv.put(DBQ._TBLT_INVHED_DisPer,hed.getDisPer());
                    cv.put(DBQ._TBLT_INVHED_trType,hed.getTrType());
                    cv.put(DBQ._TBLT_INVHED_AreaCode,hed.getAreaCode());
                    cv.put(DBQ._TBLT_INVHED_ISPRINT,hed.isISPRINT());
                    cv.put(DBQ._TBLT_INVHED_DamageQty,hed.getDamageQty());
                    cv.put(DBQ._TBLT_INVHED_IncreaseAmt,hed.getIncreaseAmt());
                    cv.put(DBQ._TBLT_INVHED_DamageDue,hed.getDamageDue());
                    cv.put(DBQ._TBLT_INVHED_SalesRep,hed.getSalesRep());
                    cv.put(DBQ._TBLT_INVHED_TourID,hed.getTourID());

                    db.insertOrThrow(DBQ._TBLT_SALINVHED, null, cv);

                    int recLine = 1;
                    for (TBLT_SALINVDET obj : ItemList) {
                        System.out.println(obj.toString());
                        cv.clear();
                        cv.put(DBQ._TBLT_SALINVDET_DocNo, maxNo);
                        cv.put(DBQ._TBLT_SALINVDET_Discode, hed.getDiscode());
                        cv.put(DBQ._TBLT_SALINVDET_DocType, hed.getDocType());
                        cv.put(DBQ._TBLT_SALINVDET_ItemCode, obj.getItemCode());
                        cv.put(DBQ._TBLT_SALINVDET_ItQty, obj.getItQty());
                        cv.put(DBQ._TBLT_SALINVDET_ULQty, obj.getULQty());
                        cv.put(DBQ._TBLT_SALINVDET_UnitPrice, obj.getUnitPrice());
                        cv.put(DBQ._TBLT_SALINVDET_CostPrice, obj.getCostPrice());
                        cv.put(DBQ._TBLT_SALINVDET_DiscPer, obj.getDiscPer());
                        cv.put(DBQ._TBLT_SALINVDET_DiscAmt, obj.getDiscAmt());
                        cv.put(DBQ._TBLT_SALINVDET_Amount, obj.getAmount());
                        cv.put(DBQ._TBLT_SALINVDET_Crep, "");
                        cv.put(DBQ._TBLT_SALINVDET_CusCode, hed.getCusCode());
                        cv.put(DBQ._TBLT_SALINVDET_ExpiryDate, obj.getExpiryDate());
                        cv.put(DBQ._TBLT_SALINVDET_Date, obj.getDate());
                        cv.put(DBQ._TBLT_SALINVDET_LocCode, hed.getLocCode());
                        cv.put(DBQ._TBLT_SALINVDET_ExpDate, obj.getExpDate());
                        cv.put(DBQ._TBLT_SALINVDET_FQTY, obj.getFQTY());
                        cv.put(DBQ._TBLT_SALINVDET_Is_Damage, obj.isIs_Damage());
                        cv.put(DBQ._TBLT_SALINVDET_IncreaseAmt, obj.getIncreaseAmt());
                        cv.put(DBQ._TBLT_SALINVDET_LineID, recLine);



                        db.insertOrThrow(DBQ._TBLT_SALINVDET, null, cv);
                        recLine++;
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    db.close();
                    dbHelper.close();
                    msg = "INV Ref No. " + hed.getRepCode().concat(maxNo);
                    textView_InvNo.setText(msg);
                } else {
                    db.close();
                    dbHelper.close();
                    msg = "Error : Can not Save Order!. Max Document number is Null";
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                db.endTransaction();
                db.close();
                msg = "Error : " + ex.getMessage();
                System.out.println(msg);
            }
        } else {
            msg = "Error : Can not Save Order Without Items!";
        }
        return msg;
    }

    public String SaveUpdate(TBLT_SALINVHED hed) {
        String msg;
        DBHelper dbHelper = new DBHelper(this);

        //Get Last Order No
        SQLiteDatabase db = null;
        db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {

            ContentValues cv = new ContentValues();
            cv.put(DBQ._TBLT_INVHED_DocNo, hed.getDocNo());
            cv.put(DBQ._TBLT_INVHED_Discode, hed.getDiscode());
            cv.put(DBQ._TBLT_INVHED_DocType, 6);
            cv.put(DBQ._TBLT_INVHED_SalesDate, hed.getSalesDate());
            cv.put(DBQ._TBLT_INVHED_SupCode,"");
            cv.put(DBQ._TBLT_INVHED_CusCode,hed.getCusCode());
            cv.put(DBQ._TBLT_INVHED_RepCode,hed.getRepCode());
            cv.put(DBQ._TBLT_INVHED_RefNo,hed.getRefNo());
            cv.put(DBQ._TBLT_INVHED_InvType,1);
            cv.put(DBQ._TBLT_INVHED_LocCode,"00001");
            cv.put(DBQ._TBLT_INVHED_CrDrType,hed.getCrDrType());

            cv.put(DBQ._TBLT_INVHED_DueDate,hed.getDueDate());
            cv.put(DBQ._TBLT_INVHED_DueAmount,hed.getDueAmount());
            cv.put(DBQ._TBLT_INVHED_GrossAmt,hed.getGrossAmt());
            cv.put(DBQ._TBLT_INVHED_AddTax1,hed.getAddTax1());
            cv.put(DBQ._TBLT_INVHED_AddTax2,hed.getAddTax2());
            cv.put(DBQ._TBLT_INVHED_AddTax3,hed.getAddTax3());
            cv.put(DBQ._TBLT_INVHED_DedAmt1,hed.getDedAmt1());
            cv.put(DBQ._TBLT_INVHED_DedAmt2,hed.getDedAmt2());
            cv.put(DBQ._TBLT_INVHED_DedAmt3,hed.getDedAmt3());
            cv.put(DBQ._TBLT_INVHED_NetAmt,hed.getNetAmt());
            cv.put(DBQ._TBLT_INVHED_RefDueAmt,hed.getRefDueAmt());
            cv.put(DBQ._TBLT_INVHED_Discount,hed.getDiscount());
            cv.put(DBQ._TBLT_INVHED_VatAmt,hed.getVatAmt());
            cv.put(DBQ._TBLT_INVHED_OrdRefNo,hed.getOrdRefNo());
            cv.put(DBQ._TBLT_INVHED_PayType,"");
            cv.put(DBQ._TBLT_INVHED_CreateUser,hed.getCreateUser());
            cv.put(DBQ._TBLT_INVHED_GLTransfer,hed.isGLTransfer());
            cv.put(DBQ._TBLT_INVHED_Status,hed.getStatus());
            cv.put(DBQ._TBLT_INVHED_DisPer,hed.getDisPer());
            cv.put(DBQ._TBLT_INVHED_trType,hed.getTrType());
            cv.put(DBQ._TBLT_INVHED_AreaCode,hed.getAreaCode());
            cv.put(DBQ._TBLT_INVHED_ISPRINT,hed.isISPRINT());
            cv.put(DBQ._TBLT_INVHED_DamageQty,hed.getDamageQty());
            cv.put(DBQ._TBLT_INVHED_IncreaseAmt,hed.getIncreaseAmt());
            cv.put(DBQ._TBLT_INVHED_DamageDue,hed.getDamageDue());
            cv.put(DBQ._TBLT_INVHED_SalesRep,hed.getSalesRep());
            cv.put(DBQ._TBLT_INVHED_TourID,hed.getTourID());

            db.replaceOrThrow(DBQ._TBLT_SALINVHED, null, cv);
            db.delete(DBQ._TBLT_SALINVDET,DBQ._TBLT_ORDDTL_Discode+"=? and "+DBQ._TBLT_ORDDTL_DocNo+"=?",
                    new String[]{hed.getDiscode(),hed.getDocNo()});

            int recLine = 1;
            for (TBLT_SALINVDET obj : ItemList) {
                cv.clear();
                cv.put(DBQ._TBLT_SALINVDET_DocNo, hed.getDocNo());
                cv.put(DBQ._TBLT_SALINVDET_Discode, hed.getDiscode());
                cv.put(DBQ._TBLT_SALINVDET_DocType, hed.getDocType());
                cv.put(DBQ._TBLT_SALINVDET_ItemCode, obj.getItemCode());
                cv.put(DBQ._TBLT_SALINVDET_ItQty, obj.getItQty());
                cv.put(DBQ._TBLT_SALINVDET_ULQty, obj.getULQty());
                cv.put(DBQ._TBLT_SALINVDET_UnitPrice, obj.getUnitPrice());
                cv.put(DBQ._TBLT_SALINVDET_CostPrice, obj.getCostPrice());
                cv.put(DBQ._TBLT_SALINVDET_DiscPer, obj.getDiscPer());
                cv.put(DBQ._TBLT_SALINVDET_DiscAmt, obj.getDiscAmt());
                cv.put(DBQ._TBLT_SALINVDET_Amount, obj.getAmount());
                cv.put(DBQ._TBLT_SALINVDET_Crep, "");
                cv.put(DBQ._TBLT_SALINVDET_CusCode, hed.getCusCode());
                cv.put(DBQ._TBLT_SALINVDET_ExpiryDate, obj.getExpiryDate());
                cv.put(DBQ._TBLT_SALINVDET_Date, obj.getDate());
                cv.put(DBQ._TBLT_SALINVDET_LocCode, hed.getLocCode());
                cv.put(DBQ._TBLT_SALINVDET_ExpDate, obj.getExpDate());
                cv.put(DBQ._TBLT_SALINVDET_FQTY, obj.getFQTY());
                cv.put(DBQ._TBLT_SALINVDET_Is_Damage, obj.isIs_Damage());
                cv.put(DBQ._TBLT_SALINVDET_IncreaseAmt, obj.getIncreaseAmt());
                cv.put(DBQ._TBLT_SALINVDET_LineID, recLine);



                db.insertOrThrow(DBQ._TBLT_SALINVDET, null, cv);
                recLine++;
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            dbHelper.close();
            msg = "INV Ref No. " + hed.getRefNo();
            textView_InvNo.setText(msg);

        } catch (Exception ex) {
            ex.printStackTrace();
            db.endTransaction();
            db.close();
            msg = "Error : " + ex.getMessage();
            System.out.println(msg);
        }
        return msg;
    }

    private String getMaxDocNo(SQLiteDatabase db) throws SQLiteException {

        Cursor c = db.rawQuery("SELECT MAX(CAST(DocNo as INt)) as a from " +DBQ._TBLT_SALINVHED+
                " WHERE RepCode = ? and Discode = ?", new String[]{String.valueOf(SharedPreference.COM_REP.getRepCode()), String.valueOf(SharedPreference.COM_REP.getDiscode())});

        Log.e(TAG + " Ref ID", SharedPreference.COM_REP.getRepCode());
        Log.e(TAG + " DIS ID", SharedPreference.COM_REP.getDiscode());

        if (c.moveToNext()) {
            String res = c.getString(c.getColumnIndex("a"));
            if (res.length() > 4) {
                res = res.substring(4);
            }
            Log.e(TAG + " MAX No. ", (res));
            Log.e(TAG + " REF ID. ", String.valueOf(SharedPreference.COM_REP.getRepID()));
            c.close();
            return "9"+String.valueOf(SharedPreference.COM_REP.getRepID()).concat(String.valueOf(Integer.parseInt(res) + 1));
        } else {
            c.close();
            return "-1";
        }
    }


    private void LoadItemsFromDB(String docNo) {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.rawQuery("select a.*, b.* from TBLT_SALINVDET as a left outer join TBLM_ITEM as b on a.ItemCode = b.ItemCode WHERE " + DBQ._TBLT_ORDDTL_DocNo + " = ?", new String[]{String.valueOf(docNo)});
        TBLT_SALINVDET dtl;
        while (c.moveToNext()) {
            dtl = new TBLT_SALINVDET();

            dtl.setItemCode(c.getString(c.getColumnIndex(DBQ._TBLT_SALINVDET_ItemCode)));
            dtl.setExpiryDate(c.getString(c.getColumnIndex(DBQ._TBLT_SALINVDET_ExpiryDate)));

            dtl.setUnitPrice(c.getDouble(c.getColumnIndex(DBQ._TBLT_SALINVDET_UnitPrice)));
            dtl.setCostPrice(c.getDouble(c.getColumnIndex(DBQ._TBLT_SALINVDET_CostPrice)));
            dtl.setDiscPer(c.getDouble(c.getColumnIndex(DBQ._TBLT_SALINVDET_DiscPer)));
            dtl.setDiscAmt(c.getDouble(c.getColumnIndex(DBQ._TBLT_SALINVDET_DiscAmt)));
            dtl.setItQty(c.getDouble(c.getColumnIndex(DBQ._TBLT_SALINVDET_ItQty)));
            dtl.setULQty(c.getDouble(c.getColumnIndex(DBQ._TBLT_SALINVDET_ULQty)));
            dtl.setDate(c.getString(c.getColumnIndex(DBQ._TBLT_SALINVDET_Date)));
            dtl.setCusCode(c.getString(c.getColumnIndex(DBQ._TBLT_SALINVDET_CusCode)));
            dtl.setFQTY(c.getDouble(c.getColumnIndex(DBQ._TBLT_SALINVDET_FQTY)));
            dtl.setAmount(c.getDouble(c.getColumnIndex(DBQ._TBLT_SALINVDET_Amount)));
            dtl.setVolume(c.getDouble(c.getColumnIndex("Volume")));
            dtl.setItemName(c.getString(c.getColumnIndex(DBQ._TBLM_ITEM_ItemDes)));
            dtl.setExpDate(c.getString(c.getColumnIndex(DBQ._TBLT_SALINVDET_ExpDate)));
            dtl.setLocCode(c.getString(c.getColumnIndex(DBQ._TBLT_SALINVDET_LocCode)));

            ItemList.add(dtl);
        }
        c.close();
        db.close();
        dbHelper.close();
    }


    private TBLT_SALINVHED GetFullOrderForSync() {
        if (obj != null) {
            obj.setStatus("A");
            obj.setItemList(ItemList);
        }
        return obj;
    }

    ProgressDialog pd;

    public void UploadOrder() throws JSONException {
        pd = new ProgressDialog(this);
        pd.setMessage("Processing..");
        pd.show();
        Gson gson = new Gson();

        TBLT_SALINVHED orderhed = GetFullOrderForSync();
        orderhed.setVarID(SharedPreference.varID);
        JSONObject obj = new JSONObject(gson.toJson(orderhed));


        Log.i(TAG, obj.toString());
        RequestQueue rq = Volley.newRequestQueue(this);

        String url = SharedPreference.URL + "Invoice/upload";


        JsonObjectRequest jr = new JsonObjectRequest(
                Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Rest Response :" + response.toString());

                        DefModal defModal = gson.fromJson(response.toString(), DefModal.class);
                        if (defModal.getVal1().equals("S")) {
                            new Thread(Invoice.this::download).start();

                            updateOrderFlag(defModal,"A");

                        } else if (defModal.getVal1().equals("E")) {
                            Toast.makeText(Invoice.this, defModal.getVal2(), Toast.LENGTH_LONG).show();
                            updateOrderFlag(defModal,"E");
                        }

                        pd.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Rest Errr :" + error.toString());
                        Toast.makeText(Invoice.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        pd.dismiss();
                        DefModal defModal = new DefModal();
                        defModal.setVal2(orderhed.getRefNo());
                        defModal.setVal3(orderhed.getDiscode());
                        updateOrderFlag(defModal,"E");
                    }
                }
        ) { //no semicolon or coma
            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        rq.add(jr);
        System.out.println("------------------------------------------------------------------------");

    }


    public void download() {
        String url = SharedPreference.URL + "MasterData?discode=" + PreferenceManager.getDefaultSharedPreferences(this).getString("disid", null) + "&repcode=" + SharedPreference.COM_REP.getRepCode() + "&functionid=ST" + SharedPreference.SFTYPE;
        System.out.println(url);

        RequestQueue rq = Volley.newRequestQueue(this);

        JsonObjectRequest jr = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    Gson gson = new Gson();
                    MasterDataModal master = gson.fromJson(response.toString(), MasterDataModal.class);

                    Thread t = new Thread(() -> {
                        new DBHelper(Invoice.this).insertMasterData(master);
                    });
                    t.start();


                },
                error -> {
                    System.out.println("Rest Errr :" + error.toString());
                    Toast.makeText(Invoice.this, error.toString(), Toast.LENGTH_LONG).show();
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

    private void updateOrderFlag(DefModal modal, String flag) {
        DBHelper dbHelper = new DBHelper(this);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DBQ._TBLT_ORDERHED_Status, flag);

        db.update(DBQ._TBLT_SALINVHED, cv, DBQ._TBLT_ORDERHED_RefNo + " = ? AND " +
                DBQ._TBLT_ORDERHED_Discode + " = ?", new String[]{String.valueOf(modal.getVal2()), String.valueOf(modal.getVal3())});

        db.close();

        //Toast.makeText(this, "Successfully Processed", Toast.LENGTH_LONG).show();
        onBackPressed2();

    }


    //Remove Item
    public boolean removeItem(int pos) {
        ItemList.remove(pos);
        return true;
    }


    public void p2() {

        Thread t = new Thread() {
            public void run() {
                try {
                    OutputStream os = MainMenu.mBluetoothSocket
                            .getOutputStream();

                    String freeSp = "  ";
                   // String lbreak = "\n";
                   // String line = "\n" + freeSp + "-----------------------------------------------\n";

                    printConfig("Kudagama Stores Pvt Limited", 1, 2, 1, os, true);
                    //printConfig("--------------------------------------------------------------", 3, 1, 1, os,true);
                    printConfig(SharedPreference.address, 3, 1, 1, os, true);
                    printConfig("Tel : ".concat(SharedPreference.tp), 3, 1, 1, os, true);
                    printConfig("----------------------------------------------", 3, 1, 1, os, true);
                    printConfig("++ INVOICE ++", 1, 2, 1, os, false);
                    if(obj.isISPRINT()){
                        printConfig("       REPRINT", 2, 1, 2, os, true);
                    }else{
                        printConfig("      ORIGINAL", 2, 1, 2, os, true);
                    }

                    printConfig("INV Ref No. ".concat(obj.getDocNo()), 2, 1, 0, os, true);
                    printConfig("Route : ".concat(SharedPreference.COM_AREA.getTxt_name()), 2, 1, 0, os, true);
                    printConfig("Customer : ".concat(textView_cusName.getText().toString()), 2, 1, 0, os, true);
                    printConfig(obj.getSalesDate().concat(" | Rep : ").concat(SharedPreference.COM_REP.getRepName()), 2, 1, 0, os, true);
                    printConfig("----------------------------------------------", 3, 1, 1, os, true);
                    printConfig("LN   Item       Price        QTY      Amount", 2, 2, 0, os, true);
                    printConfig("- - - - - - - - - - - - - - - - - - - - - - - ", 3, 1, 1, os, true);
                    int i = 1;
                    String s;
                    double linedis = 0;
                    for (TBLT_SALINVDET o : ItemList) {

                        s = i + "  " + o.getItemName() + " (" + o.getItemCode() + ")";
                        printConfig(s, 2, 1, 0, os, true);
                        if(o.getULQty()>0){
                            printConfig("*"+SharedPreference.ds_formatter.format(o.getULQty()) + "  ", 2, 2, 1, os, false);
                        }
                        printConfig(SharedPreference.ds_formatter.format(o.getUnitPrice()) + "  x ", 2, 1, 1, os, false);

                        int nos = (int) (o.getItQty()/o.getVolume());
                        int remind = (int) (o.getItQty()%o.getVolume());

                        if(remind>0){
                            printConfig(SharedPreference.ds_formatter.format(nos) + " nos & "+ SharedPreference.ds_formatter.format(remind)+" Kg  ", 2, 1, 1, os, true);
                        }else{
                            printConfig(SharedPreference.ds_formatter.format(nos) + "  ", 2, 1, 1, os, false);
                        }

                        //printConfig(SharedPreference.ds_formatter.format(o.getItQty()/o.getVolume()) + "  ", 2, 1, 1, os, false);
                        printConfig(SharedPreference.ds_formatter.format(o.getAmount()) + "  ", 2, 1, 2, os, true);
                        // printConfig("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -", 3, 1, 1, os,true);
                        linedis = linedis + o.getDiscAmt();
                        i++;

                    }
                    printConfig("----------------------------------------------", 3, 1, 1, os, true);

                    printConfig("Gross Amount : \t".concat(SharedPreference.ds_formatter.format(obj.getGrossAmt())) + "  ", 2, 1, 2, os, true);
                    //printNewLine(os);

                    printConfig("Bill Discount : \t".concat(SharedPreference.ds_formatter.format(obj.getDiscount())) + "  ", 2, 1, 2, os, true);
                    printConfig("Line Discount : \t".concat(SharedPreference.ds_formatter.format(linedis)) + "  ", 2, 1, 2, os, true);
                    printNewLine(os);
                    printConfig("Net Amount : \t".concat(SharedPreference.ds_formatter.format(obj.getNetAmt())) + "  ", 1, 1, 2, os, true);
                    printConfig("----------------------------------------------", 3, 1, 1, os, true);

                    printConfig("* - Unloaded Balance", 2, 1, 0, os, true);
                    printNewLine(os);

                    printConfig("Software - Flexiv.lk | Salesforce Android", 3, 1, 1, os, true);
                    printNewLine(os);
                    printNewLine(os);




                    SQLiteDatabase db = new DBHelper(Invoice.this).getWritableDatabase();
                    ContentValues c = new ContentValues();
                    c.put(DBQ._TBLT_INVHED_ISPRINT,true);
                    obj.setISPRINT(true);
                    db.update(DBQ._TBLT_SALINVHED,c,DBQ._TBLT_INVHED_RefNo+" = '"+obj.getRefNo()+"'",null);
                    db.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        t.start();

        try {
            System.out.println(obj.getStatus());
            if(!obj.getStatus().equals("A"))
            UploadOrder();
        } catch (JSONException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
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
}