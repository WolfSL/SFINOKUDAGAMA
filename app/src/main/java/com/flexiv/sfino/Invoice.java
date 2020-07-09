package com.flexiv.sfino;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.flexiv.sfino.fragment.Order_main;
import com.flexiv.sfino.fragment.Order_sub;
import com.flexiv.sfino.model.DefModal;
import com.flexiv.sfino.model.Modal_Batch;
import com.flexiv.sfino.model.Modal_Item;
import com.flexiv.sfino.model.TBLT_ORDDTL;
import com.flexiv.sfino.model.TBLT_ORDERHED;
import com.flexiv.sfino.utill.DBHelper;
import com.flexiv.sfino.utill.DBQ;
import com.flexiv.sfino.utill.Fragment_sub_batching;
import com.flexiv.sfino.utill.SharedPreference;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Invoice extends AppCompatActivity implements Fragment_sub_batching {

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
        setContentView(R.layout.activity_invoice);

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

        getSupportFragmentManager().beginTransaction().add(R.id.OrderFrame, new Inv_main(this)).commit();

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


    public void LoadFragment_sub() {

        try {
            DBHelper db = new DBHelper(this);
            openDialogSelector_Items(db.getItems(SharedPreference.COM_REP.getDiscode(),SharedPreference.COM_REP.getRepCode()), "SELECT ITEM", 0);
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
            openDialogSelector_batchs(db.getRepStock(SharedPreference.COM_REP.getDiscode(), itemCode,SharedPreference.COM_REP.getRepCode()), "SELECT BATCH", 0);
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
        transaction.replace(R.id.OrderFrame, new Inv_sub(this, item)).commit();
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
                    cv.put(DBQ._TBLT_ORDERHED_RefNo, maxNo);
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
                        cv.put(DBQ._TBLT_ORDDTL_FQTY, obj.getFQTY());
                        cv.put(DBQ._TBLT_ORDDTL_TotalQty, obj.getTotalQty());
                        cv.put(DBQ._TBLT_ORDDTL_ItemCode, obj.getItemCode());
                        cv.put(DBQ._TBLT_ORDDTL_ItQty, obj.getItQty());
                        cv.put(DBQ._TBLT_ORDDTL_LocCode, hed.getLocCode());
                        cv.put(DBQ._TBLT_ORDDTL_RecordLine, recLine);
                        cv.put(DBQ._TBLT_ORDDTL_UnitPrice, obj.getUnitPrice());
                        cv.put(DBQ._TBLT_ORDDTL_UsedQty, obj.getUsedQty());


                        db.insertOrThrow(DBQ._TBLT_ORDDTL, null, cv);
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
                cv.put(DBQ._TBLT_ORDDTL_SysFQTY, obj.getSysFQTY());
                cv.put(DBQ._TBLT_ORDDTL_FQTY, obj.getFQTY());
                cv.put(DBQ._TBLT_ORDDTL_TotalQty, obj.getTotalQty());
                cv.put(DBQ._TBLT_ORDDTL_ItemCode, obj.getItemCode());
                cv.put(DBQ._TBLT_ORDDTL_ItemCode, obj.getItemCode());
                cv.put(DBQ._TBLT_ORDDTL_ItQty, obj.getItQty());
                cv.put(DBQ._TBLT_ORDDTL_LocCode, hed.getLocCode());
                cv.put(DBQ._TBLT_ORDDTL_RecordLine, recLine);
                cv.put(DBQ._TBLT_ORDDTL_UnitPrice, obj.getUnitPrice());
                cv.put(DBQ._TBLT_ORDDTL_UsedQty, obj.getUsedQty());


                db.replaceOrThrow(DBQ._TBLT_ORDDTL, null, cv);
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

        Cursor c = db.rawQuery("SELECT * FROM " + DBQ._TBLT_ORDDTL + " WHERE " + DBQ._TBLT_ORDDTL_DocNo + " = ?", new String[]{String.valueOf(docNo)});
        TBLT_ORDDTL dtl;
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
            dtl.setFQTY(c.getDouble(c.getColumnIndex(DBQ._TBLT_ORDDTL_FQTY)));
            dtl.setTotalQty(c.getDouble(c.getColumnIndex(DBQ._TBLT_ORDDTL_TotalQty)));
            dtl.setRecordLine(c.getInt(c.getColumnIndex(DBQ._TBLT_ORDDTL_RecordLine)));
            dtl.setAmount(c.getDouble(c.getColumnIndex(DBQ._TBLT_ORDDTL_Amount)));

            ItemList.add(dtl);
        }
        c.close();
        db.close();
        dbHelper.close();
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
                        defModal.setVal2(orderhed.getDocNo());
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


}