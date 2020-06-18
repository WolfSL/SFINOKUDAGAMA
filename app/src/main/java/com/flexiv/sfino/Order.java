package com.flexiv.sfino;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flexiv.sfino.adapter.Adapter_Batch;
import com.flexiv.sfino.adapter.Adapter_Item;
import com.flexiv.sfino.fragment.Order_main;
import com.flexiv.sfino.fragment.Order_sub;
import com.flexiv.sfino.model.Modal_Batch;
import com.flexiv.sfino.model.Modal_Item;
import com.flexiv.sfino.model.TBLT_ORDDTL;
import com.flexiv.sfino.model.TBLT_ORDERHED;
import com.flexiv.sfino.utill.DBHelper;
import com.flexiv.sfino.utill.DBQ;
import com.flexiv.sfino.utill.SharedPreference;

import java.util.ArrayList;

public class Order extends AppCompatActivity {

    //Components
    ImageButton imageButton_done;
    ImageButton imageButton_back;

    TextView textView_cusName;
    TextView textView_Area;
    private TextView textView_InvNo;

    public TextView getTextView_InvNo(){
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
            onBackPressed();
        }



        Intent intent = getIntent();
        TBLT_ORDERHED obj = (TBLT_ORDERHED) intent.getSerializableExtra("hed");
        Order_main main;
        if(obj!=null){
            textView_InvNo.setText(obj.getRefNo());
             main = new Order_main(this,obj);
        }else{
             main = new Order_main(this);
        }
        getSupportFragmentManager().beginTransaction().add(R.id.OrderFrame, main).commit();

    }

    public void changeNavButton(int type) {
        if (type == 1) {
            imageButton_done.animate().alpha(1.0f);
            imageButton_back.setOnClickListener(v -> onBackPressed());
        } else if (type == 2) {
            imageButton_done.animate().alpha(0.0f);
            imageButton_back.setOnClickListener(v -> {
                imageButton_done.animate().alpha(1.0f);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.animator.in, R.animator.out);
                transaction.replace(R.id.OrderFrame, Order_main.getObj(this)).commit();
            });
        }
    }

    public void GoBackWithItems() {
        imageButton_done.animate().alpha(1.0f);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.in, R.animator.out);
        transaction.replace(R.id.OrderFrame, Order_main.getObj(this)).commit();
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
    public void LoadFragment_sub_batchs(String itemCode) {

        try {
            DBHelper db = new DBHelper(this);
            openDialogSelector_batchs(db.getBatchWiceStock(SharedPreference.COM_REP.getDiscode(), itemCode), "SELECT BATCH", 0);
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
        super.onBackPressed();
        Order_main.Distoy();
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
        DBHelper dbHelper = new DBHelper(this);

        //Get Last Order No
        SQLiteDatabase db = null;
        db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try
        {

            int maxNo = getMaxDocNo(db);
            if (maxNo > 0) {
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
                cv.put(DBQ._TBLT_ORDERHED_RefNo, hed.getRepCode().concat(String.valueOf(maxNo)));
                cv.put(DBQ._TBLT_ORDERHED_SalesDate, hed.getSalesDate());
                cv.put(DBQ._TBLT_ORDERHED_Status, "S");
                cv.put(DBQ._TBLT_ORDERHED_VatAmt, hed.getVatAmt());
                cv.put(DBQ._TBLT_ORDERHED_RepCode,hed.getRepCode());

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
                    cv.put(DBQ._TBLT_ORDDTL_DocNo, String.valueOf(maxNo));
                    cv.put(DBQ._TBLT_ORDDTL_FQTY, obj.getFQTY());
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
                msg = "INV Ref No. " + hed.getRepCode().concat(String.valueOf(maxNo));
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
        return msg;
    }

    String TAG = "ORDER";
    private int getMaxDocNo(SQLiteDatabase db) throws SQLiteException {

        Cursor c = db.rawQuery("SELECT MAX(CAST(DocNo as Int)) as a from TBLT_ORDERHED " +
                "WHERE RepCode = ? and Discode = ?", new String[]{String.valueOf(SharedPreference.COM_REP.getRepCode()), String.valueOf(SharedPreference.COM_REP.getDiscode())});

        Log.e(TAG+" Ref ID",SharedPreference.COM_REP.getRepCode());
        Log.e(TAG+" DIS ID",SharedPreference.COM_REP.getDiscode());

        if (c.moveToNext()) {
            int res = c.getInt(c.getColumnIndex("a"));
            Log.e(TAG+" MAX No. ",String.valueOf(res));
            c.close();
            return res + 1;
        } else {
            c.close();
            return -1;
        }
    }


    private void LoadItemsFromDB(String docNo){
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM "+DBQ._TBLT_ORDDTL+" WHERE "+DBQ._TBLT_ORDDTL_DocNo+" = ?",new String[]{String.valueOf(docNo)});
        TBLT_ORDDTL dtl;
        while(c.moveToNext()){
            dtl = new TBLT_ORDDTL();
        }
    }


}
