package com.flexiv.sfino;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.flexiv.sfino.fragment.Order_main;
import com.flexiv.sfino.fragment.Order_sub;
import com.flexiv.sfino.model.Bean_OrderPromotion;
import com.flexiv.sfino.model.DefModal;
import com.flexiv.sfino.model.Modal_Batch;
import com.flexiv.sfino.model.Modal_Item;
import com.flexiv.sfino.model.TBLT_ORDDTL;
import com.flexiv.sfino.model.TBLT_ORDERHED;
import com.flexiv.sfino.utill.DBHelper;
import com.flexiv.sfino.utill.DBQ;
import com.flexiv.sfino.utill.Fragment_sub_batching;
import com.flexiv.sfino.utill.PrinterCommands;
import com.flexiv.sfino.utill.SharedPreference;
import com.flexiv.sfino.utill.UnicodeFormatter;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import me.anwarshahriar.calligrapher.Calligrapher;

public class Order extends AppCompatActivity implements Fragment_sub_batching {
    String TAG = "ORDER";
    //Components
    ImageButton imageButton_done;
    ImageButton imageButton_back;

    TextView textView_cusName;
    TextView textView_Area;
    private TextView textView_InvNo;

    private TBLT_ORDERHED obj = null;

    public TBLT_ORDERHED getObj() {
        return obj;
    }

    public void setObj(TBLT_ORDERHED obj) {
        this.obj = obj;
    }

    public TextView getTextView_InvNo() {
        return textView_InvNo;
    }

    private ArrayList<TBLT_ORDDTL> ItemList = new ArrayList<>();

    public ArrayList<TBLT_ORDDTL> getItemList() {
        return ItemList;
    }

    public void setItemList(TBLT_ORDDTL itemList) {
        ItemList.add(itemList);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        imageButton_done = findViewById(R.id.imageButton_done);
        imageButton_back = findViewById(R.id.imageButton_back);

        textView_cusName = findViewById(R.id.textView_cusName);
        textView_Area = findViewById(R.id.textView_Area);
        textView_InvNo = findViewById(R.id.textView_InvNo);

        if (SharedPreference.COM_AREA != null && SharedPreference.COM_CUSTOMER != null) {
            textView_cusName.setText(SharedPreference.COM_CUSTOMER.getTxt_name());
            textView_Area.setText(SharedPreference.COM_AREA.getTxt_name());
        } else {
            Toast.makeText(this, "Customer or Area Cannot be Empty", Toast.LENGTH_LONG).show();
            super.onBackPressed();
        }


        Intent intent = getIntent();
        obj = (TBLT_ORDERHED) intent.getSerializableExtra("hed");

        if (obj != null) {
            String msg = "INV Ref No. " + obj.getRefNo();
            textView_InvNo.setText(msg);
            LoadItemsFromDB(obj.getDocNo());
        }

        getSupportFragmentManager().beginTransaction().add(R.id.OrderFrame, new Order_main(this)).commit();

        //TODO
        //PRINTER
        //printerInit();
        TextView tv = findViewById(R.id.textViewPS);
        if (MainMenu.mBluetoothSocket != null)
            if (MainMenu.mBluetoothSocket.isConnected()) {

                tv.setText("Connected");
            }

        imageButton_done.setOnClickListener(view -> {
            if (tv.getText().equals("Connected"))
                p2();
            else
                Toast.makeText(Order.this,"Please Connet the printer",Toast.LENGTH_LONG).show();
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
                transaction.replace(R.id.OrderFrame, new Order_main(this)).commit();
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
        transaction.replace(R.id.OrderFrame, new Order_main(this)).commit();
    }


    public void LoadFragment_sub() {

        try {
            DBHelper db = new DBHelper(this);
            openDialogSelector_Items(db.getItems(), "SELECT ITEM", 0);
            db.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    RecyclerView SelectorrecyclerView;
    private Adapter_Item adapter_item;
    private RecyclerView.LayoutManager cusLayoutManager;
    private TextView itemSelectTitle;

    private void openDialogSelector_Items(ArrayList<Modal_Item> arr_cus, String title, int type) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this, R.style.mydialog);

        View row = getLayoutInflater().inflate(R.layout.item_selector, null);
        SelectorrecyclerView = row.findViewById(R.id.itemSelectorRecView);


        alertBuilder.setView(row);
        AlertDialog dialog = alertBuilder.create();


        // SelectorrecyclerView.setHasFixedSize(true);
        cusLayoutManager = new LinearLayoutManager(this);
        adapter_item = new Adapter_Item(arr_cus, dialog, this);
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
    public void LoadFragment_sub_batchs(String itemCode) {

        try {
            DBHelper db = new DBHelper(this);
            openDialogSelector_batchs(db.getBatchWiceStock(SharedPreference.COM_REP.getDiscode(), itemCode), "SELECT BATCH", 0);
            db.close();
        } catch (Exception es) {
            es.printStackTrace();
        }


    }

    private Adapter_Batch adapter_batch;

    private void openDialogSelector_batchs(ArrayList<Modal_Batch> arr_cus, String title, int type) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this, R.style.mydialog);

        View row = getLayoutInflater().inflate(R.layout.item_selector, null);
        SelectorrecyclerView = row.findViewById(R.id.itemSelectorRecView);


        alertBuilder.setView(row);
        AlertDialog dialog = alertBuilder.create();


        // SelectorrecyclerView.setHasFixedSize(true);
        cusLayoutManager = new LinearLayoutManager(this);
        adapter_batch = new Adapter_Batch(arr_cus, dialog, this);
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

    public void LoadOrderSub(Modal_Batch item) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.in, R.animator.out);
        transaction.replace(R.id.OrderFrame, new Order_sub(this, item)).commit();
        //getSupportFragmentManager().beginTransaction().replace(R.id.OrderFrame,new Order_sub(this,item)).commit();
    }

    @Override
    public void onBackPressed() {
        if (!ItemList.isEmpty()) {
            new AlertDialog.Builder(this).setTitle("Confirm Exit!")
                    .setMessage("Are you sure you want to close this Order with out Saving?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Order.super.onBackPressed();
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


    public String Save(TBLT_ORDERHED hed) {
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
                    cv.put(DBQ._TBLT_ORDERHED_AreaCode, hed.getAreaCode());
                    cv.put(DBQ._TBLT_ORDERHED_CreateUser, hed.getCreateUser());
                    cv.put(DBQ._TBLT_ORDERHED_CusCode, hed.getCusCode());
                    cv.put(DBQ._TBLT_ORDERHED_Discode, hed.getDiscode());
                    cv.put(DBQ._TBLT_ORDERHED_Discount, hed.getDiscount());
                    cv.put(DBQ._TBLT_ORDERHED_DisPer, hed.getDisPer());
                    cv.put(DBQ._TBLT_ORDERHED_DocNo, maxNo);
                    cv.put(DBQ._TBLT_ORDERHED_GrossAmt, hed.getGrossAmt());
                    cv.put(DBQ._TBLT_ORDERHED_ISUSED, hed.isISUSED());
                    cv.put(DBQ._TBLT_ORDERHED_LocCode, hed.getLocCode());
                    cv.put(DBQ._TBLT_ORDERHED_NetAmt, hed.getNetAmt());
                    cv.put(DBQ._TBLT_ORDERHED_PayType, hed.getPayType());
                    cv.put(DBQ._TBLT_ORDERHED_RefNo, "V"+maxNo);
                    cv.put(DBQ._TBLT_ORDERHED_SalesDate, hed.getSalesDate());
                    cv.put(DBQ._TBLT_ORDERHED_Status, hed.getStatus());
                    cv.put(DBQ._TBLT_ORDERHED_VatAmt, hed.getVatAmt());
                    cv.put(DBQ._TBLT_ORDERHED_RepCode, hed.getRepCode());

                    db.insertOrThrow(DBQ._TBLT_ORDERHED, null, cv);

                    int recLine = 1;
                    for (TBLT_ORDDTL obj : ItemList) {
                        cv.clear();
                        cv.put(DBQ._TBLT_ORDDTL_Amount, obj.getAmount());
                        cv.put(DBQ._TBLT_ORDDTL_BATCH, obj.getBATCH());
                        cv.put(DBQ._TBLT_ORDDTL_CusCode, hed.getCusCode());
                        cv.put(DBQ._TBLT_ORDDTL_Date, obj.getDate());
                        cv.put(DBQ._TBLT_ORDDTL_DiscAmt, obj.getDiscAmt());
                        cv.put(DBQ._TBLT_ORDDTL_Discode, hed.getDiscode());
                        cv.put(DBQ._TBLT_ORDDTL_DiscPer, obj.getDiscPer());
                        cv.put(DBQ._TBLT_ORDDTL_DocNo, maxNo);
                        cv.put(DBQ._TBLT_ORDDTL_TradeFQTY, obj.getTradeFQTY());
                        cv.put(DBQ._TBLT_ORDDTL_SysFQTY, obj.getSysFQTY());
                        cv.put(DBQ._TBLT_ORDDTL_TotalQty, obj.getTotalQty());
                        cv.put(DBQ._TBLT_ORDDTL_ItemCode, obj.getItemCode());
                        cv.put(DBQ._TBLT_ORDDTL_ItQty, obj.getItQty());
                        cv.put(DBQ._TBLT_ORDDTL_LocCode, hed.getLocCode());
                        cv.put(DBQ._TBLT_ORDDTL_RecordLine, recLine);
                        cv.put(DBQ._TBLT_ORDDTL_UnitPrice, obj.getUnitPrice());
                        cv.put(DBQ._TBLT_ORDDTL_UsedQty, obj.getUsedQty());
                        db.insertOrThrow(DBQ._TBLT_ORDDTL, null, cv);
                        InsertOrderPromotions(obj.getPromotions(),db,maxNo);
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

    public void InsertOrderPromotions(ArrayList<Bean_OrderPromotion> arr, SQLiteDatabase db,String docNo) throws Exception{
       try {
           ContentValues values = new ContentValues();
           for (Bean_OrderPromotion bean : arr) {
               values.put(DBQ._ORDERPROMOTION_DealCode, bean.getDealCode());
               values.put(DBQ._ORDERPROMOTION_DisCode, SharedPreference.COM_REP.getDiscode());
               Log.w("SQL Qty", bean.getQty().toString());
               values.put(DBQ._ORDERPROMOTION_DocNo, docNo);
               Log.e("Doc No ",docNo);
               Log.e("Dis Code ", SharedPreference.COM_REP.getDiscode());
               Log.e("Item Code ",bean.getItemCode());
               values.put(DBQ._ORDERPROMOTION_DocType, bean.getDocType());
               values.put(DBQ._ORDERPROMOTION_FQty, bean.getFQTY().doubleValue());
               values.put(DBQ._ORDERPROMOTION_ItemCode, bean.getItemCode());
               values.put(DBQ._ORDERPROMOTION_NoOfDeals, bean.getNoOfDeals());
               values.put(DBQ._ORDERPROMOTION_PromoDesc, bean.getPromoDesc());
               values.put(DBQ._ORDERPROMOTION_PromoNo, bean.getPromoNo());
               values.put(DBQ._ORDERPROMOTION_CusCode, bean.getCusCode());
               values.put(DBQ._ORDERPROMOTION_DealCode, bean.getDealCode());
               values.put(DBQ._ORDERPROMOTION_PTCode, bean.getPTCode());
               values.put(DBQ._ORDERPROMOTION_Qty, bean.getQty().doubleValue());
               values.put(DBQ._ORDERPROMOTION_RepCode, bean.getRepCode());
               values.put(DBQ._ORDERPROMOTION_UserID, bean.getUserID());
               values.put(DBQ._ORDERPROMOTION_SysFQty, bean.getSysFQty().doubleValue());
               values.put(DBQ._ORDERPROMOTION_sync, 0);
               Log.i("SQL ", values.toString());
               db.insertOrThrow(DBQ.TBLT_ORDERPROMOTION, null, values);

           }
       }catch (Exception e){
           e.printStackTrace();
           throw e;
       }
    }

    public String SaveUpdate(TBLT_ORDERHED hed) {
        String msg;
        DBHelper dbHelper = new DBHelper(this);

        //Get Last Order No
        SQLiteDatabase db = null;
        db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {

            ContentValues cv = new ContentValues();
            cv.put(DBQ._TBLT_ORDERHED_AreaCode, hed.getAreaCode());
            cv.put(DBQ._TBLT_ORDERHED_CreateUser, hed.getCreateUser());
            cv.put(DBQ._TBLT_ORDERHED_CusCode, hed.getCusCode());
            cv.put(DBQ._TBLT_ORDERHED_Discode, hed.getDiscode());
            cv.put(DBQ._TBLT_ORDERHED_Discount, hed.getDiscount());
            cv.put(DBQ._TBLT_ORDERHED_DisPer, hed.getDisPer());
            cv.put(DBQ._TBLT_ORDERHED_DocNo, hed.getDocNo());
            cv.put(DBQ._TBLT_ORDERHED_GrossAmt, hed.getGrossAmt());
            cv.put(DBQ._TBLT_ORDERHED_ISUSED, hed.isISUSED());
            cv.put(DBQ._TBLT_ORDERHED_LocCode, hed.getLocCode());
            cv.put(DBQ._TBLT_ORDERHED_NetAmt, hed.getNetAmt());
            cv.put(DBQ._TBLT_ORDERHED_PayType, hed.getPayType());
            cv.put(DBQ._TBLT_ORDERHED_RefNo, hed.getRefNo());
            cv.put(DBQ._TBLT_ORDERHED_SalesDate, hed.getSalesDate());
            cv.put(DBQ._TBLT_ORDERHED_Status, hed.getStatus());
            cv.put(DBQ._TBLT_ORDERHED_VatAmt, hed.getVatAmt());
            cv.put(DBQ._TBLT_ORDERHED_RepCode, hed.getRepCode());

            db.replaceOrThrow(DBQ._TBLT_ORDERHED, null, cv);

            //Delete Details
            db.delete(DBQ._TBLT_ORDDTL,DBQ._TBLT_ORDDTL_Discode+"=? and "+DBQ._TBLT_ORDDTL_DocNo+"=?",
                    new String[]{hed.getDiscode(),hed.getDocNo()});

            db.delete(DBQ.TBLT_ORDERPROMOTION,DBQ._ORDERPROMOTION_DisCode+"=? and "+DBQ._ORDERPROMOTION_DocNo+"=?",
                    new String[]{hed.getDiscode(),hed.getDocNo()});

            int recLine = 1;
            for (TBLT_ORDDTL obj : ItemList) {
                cv.clear();
                cv.put(DBQ._TBLT_ORDDTL_Amount, obj.getAmount());
                cv.put(DBQ._TBLT_ORDDTL_BATCH, obj.getBATCH());
                cv.put(DBQ._TBLT_ORDDTL_CusCode, hed.getCusCode());
                cv.put(DBQ._TBLT_ORDDTL_Date, obj.getDate());
                cv.put(DBQ._TBLT_ORDDTL_DiscAmt, obj.getDiscAmt());
                cv.put(DBQ._TBLT_ORDDTL_Discode, hed.getDiscode());
                cv.put(DBQ._TBLT_ORDDTL_DiscPer, obj.getDiscPer());
                cv.put(DBQ._TBLT_ORDDTL_DocNo, hed.getDocNo());
                cv.put(DBQ._TBLT_ORDDTL_TradeFQTY, obj.getTradeFQTY());
                cv.put(DBQ._TBLT_ORDDTL_SysFQTY, obj.getSysFQTY());
                cv.put(DBQ._TBLT_ORDDTL_TotalQty, obj.getTotalQty());
                cv.put(DBQ._TBLT_ORDDTL_ItemCode, obj.getItemCode());
                cv.put(DBQ._TBLT_ORDDTL_ItQty, obj.getItQty());
                cv.put(DBQ._TBLT_ORDDTL_LocCode, hed.getLocCode());
                cv.put(DBQ._TBLT_ORDDTL_RecordLine, recLine);
                cv.put(DBQ._TBLT_ORDDTL_UnitPrice, obj.getUnitPrice());
                cv.put(DBQ._TBLT_ORDDTL_UsedQty, obj.getUsedQty());


                db.replaceOrThrow(DBQ._TBLT_ORDDTL, null, cv);

                InsertOrderPromotions(obj.getPromotions(),db,hed.getDocNo());
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

        Cursor c = db.rawQuery("SELECT MAX(CAST(DocNo as INt)) as a from TBLT_ORDERHED " +
                "WHERE RepCode = ? and Discode = ?", new String[]{String.valueOf(SharedPreference.COM_REP.getRepCode()), String.valueOf(SharedPreference.COM_REP.getDiscode())});

        Log.e(TAG + " Ref ID", SharedPreference.COM_REP.getRepCode());
        Log.e(TAG + " DIS ID", SharedPreference.COM_REP.getDiscode());

        if (c.moveToNext()) {
            String res = c.getString(c.getColumnIndex("a"));
            if (res.length() > 3) {
                res = res.substring(3);
            }
            Log.e(TAG + " MAX No. ", (res));
            c.close();
            return String.valueOf(SharedPreference.COM_REP.getRepID()).concat(String.valueOf(Integer.parseInt(res) + 1));
        } else {
            c.close();
            return "-1";
        }
    }


    private void LoadItemsFromDB(String docNo) {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.rawQuery("select a.*, b.ItemDes from TBLT_ORDDTL as a left outer join TBLM_ITEM as b on a.ItemCode = b.ItemCode WHERE " + DBQ._TBLT_ORDDTL_DocNo + " = ?", new String[]{String.valueOf(docNo)});
        TBLT_ORDDTL dtl;
        ItemList.clear();
        while (c.moveToNext()) {
            dtl = new TBLT_ORDDTL();
            dtl.setItemCode(c.getString(c.getColumnIndex(DBQ._TBLT_ORDDTL_ItemCode)));
            dtl.setBATCH(c.getString(c.getColumnIndex(DBQ._TBLT_ORDDTL_BATCH)));
            dtl.setUnitPrice(c.getDouble(c.getColumnIndex(DBQ._TBLT_ORDDTL_UnitPrice)));
            dtl.setDiscPer(c.getDouble(c.getColumnIndex(DBQ._TBLT_ORDDTL_DiscPer)));
            dtl.setDiscAmt(c.getDouble(c.getColumnIndex(DBQ._TBLT_ORDDTL_DiscAmt)));
            dtl.setItQty(c.getDouble(c.getColumnIndex(DBQ._TBLT_ORDDTL_ItQty)));
            dtl.setUsedQty(c.getDouble(c.getColumnIndex(DBQ._TBLT_ORDDTL_UsedQty)));
            dtl.setDate(c.getString(c.getColumnIndex(DBQ._TBLT_ORDDTL_Date)));
            dtl.setCusCode(c.getString(c.getColumnIndex(DBQ._TBLT_ORDDTL_CusCode)));
            dtl.setTradeFQTY(c.getDouble(c.getColumnIndex(DBQ._TBLT_ORDDTL_TradeFQTY)));
            dtl.setSysFQTY(c.getDouble(c.getColumnIndex(DBQ._TBLT_ORDDTL_SysFQTY)));
            dtl.setTotalQty(c.getDouble(c.getColumnIndex(DBQ._TBLT_ORDDTL_TotalQty)));
            dtl.setRecordLine(c.getInt(c.getColumnIndex(DBQ._TBLT_ORDDTL_RecordLine)));
            dtl.setAmount(c.getDouble(c.getColumnIndex(DBQ._TBLT_ORDDTL_Amount)));
            dtl.setItemName(c.getString(c.getColumnIndex(DBQ._TBLM_ITEM_ItemDes)));

            dtl.setPromotions(getOrderPromotions(db,SharedPreference.COM_REP.getDiscode(),docNo,dtl.getItemCode()));

            ItemList.add(dtl);
        }
        c.close();
        db.close();
        dbHelper.close();
    }

    public ArrayList<Bean_OrderPromotion> getOrderPromotions(SQLiteDatabase db,String Discode, String DocNo,String itemCode) {
        ArrayList<Bean_OrderPromotion> arr = null;
       String sql = "select * from TBLT_ORDERPROMOTION WHERE "+DBQ._ORDERPROMOTION_DisCode+" = '"+Discode+"' AND "+ DBQ._ORDERPROMOTION_DocNo+" = '"+DocNo+"' AND "+DBQ._ORDERPROMOTION_ItemCode + " = '"+itemCode+"'";
        //String sql = "select * from TBLT_ORDERPROMOTION";
        System.out.println(sql);
        Cursor c = db.rawQuery(sql, null);
//        Cursor c = db.rawQuery(sql, null);

        Bean_OrderPromotion bean;
        if (c.getColumnCount() > 0) {
            arr = new ArrayList<>();
            while (c.moveToNext()) {
                bean = new Bean_OrderPromotion();
                bean.setPromoNo(c.getInt(c.getColumnIndex(DBQ._ORDERPROMOTION_PromoNo)));
                bean.setItemCode(c.getString(c.getColumnIndex(DBQ._ORDERPROMOTION_ItemCode)));
                bean.setDealCode(c.getString(c.getColumnIndex(DBQ._ORDERPROMOTION_DealCode)));
                bean.setNoOfDeals(c.getInt(c.getColumnIndex(DBQ._ORDERPROMOTION_NoOfDeals)));
                bean.setQty(new BigDecimal(c.getDouble(c.getColumnIndex(DBQ._ORDERPROMOTION_Qty))));
                bean.setFQTY(new BigDecimal(c.getDouble(c.getColumnIndex(DBQ._ORDERPROMOTION_FQty))));
                bean.setDiscode(c.getString(c.getColumnIndex(DBQ._ORDERPROMOTION_DisCode)));
                bean.setPTCode(c.getString(c.getColumnIndex(DBQ._ORDERPROMOTION_PTCode)));
                bean.setDocNo(c.getString(c.getColumnIndex(DBQ._ORDERPROMOTION_DocNo)));
                bean.setDocType(c.getInt(c.getColumnIndex(DBQ._ORDERPROMOTION_DocType)));
                bean.setPromoDesc(c.getString(c.getColumnIndex(DBQ._ORDERPROMOTION_PromoDesc)));

                bean.setCusCode(c.getString(c.getColumnIndex(DBQ._ORDERPROMOTION_CusCode)));
                bean.setFreeItem(new BigDecimal(c.getDouble(c.getColumnIndex(DBQ._ORDERPROMOTION_FreeItem))));
                bean.setSysFQty(new BigDecimal(c.getDouble(c.getColumnIndex(DBQ._ORDERPROMOTION_SysFQty))));
                Log.w("SQL QTY", c.getString(c.getColumnIndex(DBQ._ORDERPROMOTION_Qty)));
                arr.add(bean);
            }
            c.close();
        }
        return arr;
    }


    private TBLT_ORDERHED GetFullOrderForSync() {
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

        TBLT_ORDERHED orderhed = GetFullOrderForSync();
        JSONObject obj = new JSONObject(gson.toJson(orderhed));


        Log.i(TAG, obj.toString());
        RequestQueue rq = Volley.newRequestQueue(this);

        String url = SharedPreference.URL + "Order/upload";


        JsonObjectRequest jr = new JsonObjectRequest(
                Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Rest Response :" + response.toString());

                        DefModal defModal = gson.fromJson(response.toString(), DefModal.class);
                        if (defModal.getVal1().equals("S")) {
                            updateOrderFlag(defModal, "A");

                        } else if (defModal.getVal1().equals("E")) {
                            Toast.makeText(Order.this, defModal.getVal2(), Toast.LENGTH_LONG).show();
                            updateOrderFlag(defModal, "E");
                        }

                        pd.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Rest Errr :" + error.toString());
                        Toast.makeText(Order.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        pd.dismiss();
                        DefModal defModal = new DefModal();
                        defModal.setVal2(orderhed.getDocNo());
                        defModal.setVal3(orderhed.getDiscode());
                        updateOrderFlag(defModal, "E");
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

    private void updateOrderFlag(DefModal modal, String flag) {
        DBHelper dbHelper = new DBHelper(this);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DBQ._TBLT_ORDERHED_Status, flag);

        db.update(DBQ._TBLT_ORDERHED, cv, DBQ._TBLT_ORDERHED_DocNo + " = ? AND " +
                DBQ._TBLT_ORDERHED_Discode + " = ?", new String[]{String.valueOf(modal.getVal2()), String.valueOf(modal.getVal3())});

        db.close();

        Toast.makeText(this, "Successfully Processed", Toast.LENGTH_LONG).show();
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
                    String lbreak = "\n";
                    String line = "\n" + freeSp + "-----------------------------------------------\n";

                    printConfig(SharedPreference.COM_REP.getDisName(), 1, 2, 1, os, true);
                    //printConfig("--------------------------------------------------------------", 3, 1, 1, os,true);
                    printConfig(SharedPreference.address, 3, 1, 1, os, true);
                    printConfig("Tel : ".concat(SharedPreference.tp), 3, 1, 1, os, true);
                    printConfig("--------------------------------------------------------------", 3, 1, 1, os, true);
                    printConfig("------ ORDER ------", 2, 2, 1, os, true);
                    printConfig("Order No. ".concat(obj.getDocNo()), 2, 1, 0, os, true);
                    printConfig(obj.getSalesDate().concat(" | Rep : ").concat(SharedPreference.COM_REP.getRepCode()), 2, 1, 0, os, true);
                    printConfig("--------------------------------------------------------------", 3, 1, 1, os, true);
                    printConfig("LN   Item       Price        QTY      Amount", 2, 2, 0, os, true);
                    printConfig("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -", 3, 1, 1, os, true);
                    int i = 1;
                    String s;
                    double linedis = 0;
                    for (TBLT_ORDDTL o : ItemList) {

                        s = i + "  " + o.getItemName() + " (" + o.getItemCode() + ")";
                        printConfig(s, 2, 1, 0, os, true);
                        printConfig(SharedPreference.ds_formatter.format(o.getUnitPrice()) + "  x  ", 2, 1, 1, os, false);
                        printConfig(SharedPreference.ds_formatter.format(o.getItQty()) + "  ", 2, 1, 1, os, false);
                        printConfig(SharedPreference.ds_formatter.format(o.getAmount()) + "  ", 2, 1, 2, os, true);
                        // printConfig("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -", 3, 1, 1, os,true);
                        linedis = linedis + o.getDiscAmt();
                        i++;

                    }
                    printConfig("--------------------------------------------------------------", 3, 1, 1, os, true);

                    printConfig("Gross Amount :    ".concat(SharedPreference.ds_formatter.format(obj.getGrossAmt())) + "  ", 2, 1, 2, os, true);
                    //printNewLine(os);
                    printConfig("Total Bill Discount :    ".concat(SharedPreference.ds_formatter.format(obj.getDiscount())) + "  ", 2, 1, 2, os, true);
                    printConfig("Line Discount :    ".concat(SharedPreference.ds_formatter.format(linedis)) + "  ", 2, 1, 2, os, true);
                    printNewLine(os);
                    printConfig("Net Amount :    ".concat(SharedPreference.ds_formatter.format(obj.getNetAmt())) + "  ", 1, 1, 2, os, true);
                    printConfig("--------------------------------------------------------------", 3, 1, 1, os, true);
                    printNewLine(os);

                    printConfig("Software - Flexiv.lk | Salesforce Android", 3, 1, 1, os, true);
                    printNewLine(os);
                    printNewLine(os);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        t.start();
    }


    //    //BlueTooth Settings..
//    private static final int REQUEST_CONNECT_DEVICE = 1;
//    private static final int REQUEST_ENABLE_BT = 2;
//
//    private UUID applicationUUID = UUID
//            .fromString("00001101-0000-1000-8000-00805F9B34FB");
//    private ProgressDialog mBluetoothConnectProgressDialog;
//    private BluetoothSocket mBluetoothSocket;
//
//
//    TextView textViewPS;
//    int printstat;
//
//    private ProgressDialog loading;
//    AlertDialog.Builder builder;
//
//    private void printerInit() {
//
//
//        System.out.println("-----------------------------------1");
//        Calligrapher calligrapher = new Calligrapher(this);
//        calligrapher.setFont(this, "fonts/abel-regular.ttf", true);
//        textViewPS = (TextView) findViewById(R.id.textViewPS);
//
//        if (MainMenu.mBluetoothDevice != null) {
//            // textViewPS.setText(MainMenu.mBluetoothDevice.getName());
//            run();
//        }
//
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//
//
//        imageButton_done.setOnClickListener(view -> {
//            Log.e(TAG, "Enter Print Test");
//            if (textViewPS.getText().equals("Disconnected")) {
//                MainMenu.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//                if (MainMenu.mBluetoothAdapter == null) {
//                    Toast.makeText(Order.this, "Message1", Toast.LENGTH_SHORT).show();
//                } else {
//                    if (!MainMenu.mBluetoothAdapter.isEnabled()) {
//                        Intent enableBtIntent = new Intent(
//                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                        startActivityForResult(enableBtIntent,
//                                REQUEST_ENABLE_BT);
//                    } else if (MainMenu.mBluetoothDevice == null) {
//                        ListPairedDevices();
//                        Intent connectIntent = new Intent(Order.this,
//                                DeviceList.class);
//                        startActivityForResult(connectIntent,
//                                REQUEST_CONNECT_DEVICE);
//
//                    }
//                }
//
//            } else {
//                //Print
//                p2();
//
//                int TIME = 10000; //5000 ms (5 Seconds)
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        //  p2(); //call function!
//
//                        printstat = 1;
//                    }
//                }, TIME);
//            }
//        });
//    }
//
//    private void ListPairedDevices() {
//        Set<BluetoothDevice> mPairedDevices = MainMenu.mBluetoothAdapter
//                .getBondedDevices();
//        if (mPairedDevices.size() > 0) {
//            for (BluetoothDevice mDevice : mPairedDevices) {
//                Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "
//                        + mDevice.getAddress());
//            }
//        }
//    }
//
//    public void onActivityResult(int mRequestCode, int mResultCode,
//                                 Intent mDataIntent) {
//        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);
//
//        switch (mRequestCode) {
//            case REQUEST_CONNECT_DEVICE:
//                if (mResultCode == Activity.RESULT_OK) {
//                    Bundle mExtra = mDataIntent.getExtras();
//                    String mDeviceAddress = mExtra.getString("DeviceAddress");
//                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
//                    MainMenu.mBluetoothDevice = MainMenu.mBluetoothAdapter
//                            .getRemoteDevice(mDeviceAddress);
//                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
//                            "Connecting...", MainMenu.mBluetoothDevice.getName() + " : "
//                                    + MainMenu.mBluetoothDevice.getAddress(), true, false);
//                    Thread mBlutoothConnectThread = new Thread(this);
//                    mBlutoothConnectThread.start();
//                    // pairToDevice(mBluetoothDevice); This method is replaced by
//                    // progress dialog with thread
//                }
//                break;
//
//            case REQUEST_ENABLE_BT:
//                if (mResultCode == Activity.RESULT_OK) {
//                    ListPairedDevices();
//                    Intent connectIntent = new Intent(Order.this,
//                            DeviceList.class);
//                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
//                } else {
//                    Toast.makeText(Order.this, "Message", Toast.LENGTH_SHORT).show();
//                }
//                break;
//        }
//    }
//
//    public void run() {
//        System.out.println("----------------------2");
//        try {
//
//            mBluetoothSocket = MainMenu.mBluetoothDevice
//                    .createRfcommSocketToServiceRecord(applicationUUID);
//            MainMenu.mBluetoothAdapter.cancelDiscovery();
//            if (!mBluetoothSocket.isConnected()) {
//                mBluetoothSocket.connect();
//                mHandler.sendEmptyMessage(0);
//            }
//        } catch (IOException eConnectException) {
//            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
//            closeSocket(mBluetoothSocket);
//            return;
//        }
//    }
//
//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            if (mBluetoothConnectProgressDialog != null) {
//                mBluetoothConnectProgressDialog.dismiss();
//            }
//
//
//            textViewPS.setText("");
//            textViewPS.setText("Connected");
//            //textViewPS.setTextColor(Color.rgb(97, 170, 74));
//            //mPrint.setEnabled(true);
//            // .setText("Disconnect");
//
//        }
//    };
//
//    private void closeSocket(BluetoothSocket nOpenSocket) {
//        try {
//            nOpenSocket.close();
//            Log.d(TAG, "SocketClosed");
//        } catch (IOException ex) {
//            Log.d(TAG, "CouldNotCloseSocket");
//        }
//    }
//
//    public void p2() {
//
//        Thread t = new Thread() {
//            public void run() {
//                try {
//                    OutputStream os = mBluetoothSocket
//                            .getOutputStream();
//
//                    String freeSp = "  ";
//                    String lbreak = "\n";
//                    String line = "\n" + freeSp + "-----------------------------------------------\n";
//
//                    printConfig(SharedPreference.COM_REP.getDisName(), 1, 2, 1, os, true);
//                    //printConfig("--------------------------------------------------------------", 3, 1, 1, os,true);
//                    printConfig(SharedPreference.address, 3, 1, 1, os, true);
//                    printConfig("Tel : ".concat(SharedPreference.tp), 3, 1, 1, os, true);
//                    printConfig("--------------------------------------------------------------", 3, 1, 1, os, true);
//                    printConfig("------ ORDER ------", 2, 2, 1, os, true);
//                    printConfig("Order No. ".concat(obj.getDocNo()), 2, 1, 0, os, true);
//                    printConfig(obj.getSalesDate().concat(" | Rep : ").concat(SharedPreference.COM_REP.getRepCode()), 2, 1, 0, os, true);
//                    printConfig("--------------------------------------------------------------", 3, 1, 1, os, true);
//                    printConfig("LN   Item       Price        QTY      Amount", 2, 2, 0, os, true);
//                    printConfig("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -", 3, 1, 1, os, true);
//                    int i = 1;
//                    String s;
//                    double linedis = 0;
//                    for (TBLT_ORDDTL o : ItemList) {
//
//                        s = i + "  " + o.getItemName() + " (" + o.getItemCode() + ")";
//                        printConfig(s, 2, 1, 0, os, true);
//                        printConfig(SharedPreference.df.format(o.getUnitPrice()) + "  x  ", 2, 1, 1, os, false);
//                        printConfig(SharedPreference.df.format(o.getItQty()) + "  ", 2, 1, 1, os, false);
//                        printConfig(SharedPreference.df.format(o.getAmount()) + "  ", 2, 1, 2, os, true);
//                        // printConfig("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -", 3, 1, 1, os,true);
//                        linedis = linedis + o.getDiscAmt();
//                        i++;
//
//                    }
//                    printConfig("--------------------------------------------------------------", 3, 1, 1, os, true);
//
//                    printConfig("Gross Amount :    ".concat(SharedPreference.df.format(obj.getGrossAmt())) + "  ", 2, 1, 2, os, true);
//                    //printNewLine(os);
//                    printConfig("Total Bill Discount :    ".concat(SharedPreference.df.format(obj.getDiscount())) + "  ", 2, 1, 2, os, true);
//                    printConfig("Line Discount :    ".concat(SharedPreference.df.format(linedis)) + "  ", 2, 1, 2, os, true);
//                    printNewLine(os);
//                    printConfig("Net Amount :    ".concat(SharedPreference.df.format(obj.getNetAmt())) + "  ", 1, 1, 2, os, true);
//                    printConfig("--------------------------------------------------------------", 3, 1, 1, os, true);
//                    printNewLine(os);
//
//                    printConfig("Software - Flexiv.lk | Salesforce Android", 3, 1, 1, os, true);
//                    printNewLine(os);
//
//
////                    StringBuilder com = new StringBuilder();
//
////
////                    //Header
////                    com.append(freeSp).append(SharedPreference.COM_REP.getDisName());
////                    com.append(line);
////                    String[] adress = SharedPreference.address.split(",");
////                    com.append(freeSp).append(adress[0].trim()).append(", ").append(adress[1].trim());
////                    com.append(lbreak);
////                    com.append(freeSp).append(adress[2].trim());
////                    com.append(lbreak);
////                    com.append(freeSp).append("Tel : ").append(SharedPreference.tp);
////                    com.append(line);
////                    com.append("Order No. ").append(obj.getDocNo());
////                    com.append(lbreak);
////                    com.append(freeSp).append(obj.getSalesDate()).append(" | Rep : ").append(SharedPreference.COM_REP.getRepCode());
////                    com.append(line2);
////                    com.append(freeSp).append("LN   Item        QTY               Amount");
////                    com.append(line);
////
////                    os.write(com.toString().getBytes());
////
////                    int i = 1;
////                    //order DTL
////                    for(TBLT_ORDDTL o : ItemList){
////                        com.setLength(0);
////
////                        com.append(freeSp).append(i).append(freeSp).append(o.getItemCode()).append(" - ").append(o.getItemName());
////                        com.append(lbreak);
////                        com.append("               ").append(SharedPreference.df.format(o.getItQty())).append("   ").append(o.getAmount());
////                        com.append(line);
////                        os.write(com.toString().getBytes());
////                    }
////
////                    os.write(line.getBytes());
//
//
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//        };
//        t.start();
//    }
//
//
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

//
//    public static byte intToByteArray(int value) {
//        byte[] b = ByteBuffer.allocate(4).putInt(value).array();
//
//        for (int k = 0; k < b.length; k++) {
//            System.out.println("Selva  [" + k + "] = " + "0x"
//                    + UnicodeFormatter.byteToHex(b[k]));
//        }
//
//        return b[3];
//    }
//
//    public byte[] sel(int val) {
//        ByteBuffer buffer = ByteBuffer.allocate(2);
//        buffer.putInt(val);
//        buffer.flip();
//        return buffer.array();
//    }

}
