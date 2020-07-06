package com.flexiv.sfino;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.flexiv.sfino.adapter.Adapter_Order_list;
import com.flexiv.sfino.model.TBLT_ORDERHED;
import com.flexiv.sfino.utill.DBHelper;
import com.flexiv.sfino.utill.DBQ;
import com.flexiv.sfino.utill.SharedPreference;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class InvList extends AppCompatActivity {

    private FloatingActionButton floatingActionButton_OL;
    private TextView textView_Area, textView_cusName;
    private RecyclerView OrderSelectorRecView;
    private Adapter_Order_list adapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<TBLT_ORDERHED> hedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inv_list);

        hedList = new ArrayList<>();


        floatingActionButton_OL = findViewById(R.id.floatingActionButton_OL);
        textView_cusName = findViewById(R.id.textView_cusName);
        textView_Area = findViewById(R.id.textView_Area);

        floatingActionButton_OL.setOnClickListener(view -> {
            Intent i = new Intent(InvList.this, Invoice.class);
            startActivity(i);
        });

        if (SharedPreference.COM_AREA != null && SharedPreference.COM_CUSTOMER != null) {
            textView_cusName.setText(SharedPreference.COM_CUSTOMER.getTxt_name());
            textView_Area.setText(SharedPreference.COM_AREA.getTxt_name());
        } else {
            Toast.makeText(this, "Customer or Area Cannot be Empty", Toast.LENGTH_LONG).show();
            onBackPressed();
        }


        hedList.addAll(LocatOrderList());
        OrderSelectorRecView = findViewById(R.id.OrderSelectorRecView);
        layoutManager = new LinearLayoutManager(this);
        adapter = new Adapter_Order_list(hedList,1,this);
        OrderSelectorRecView.setLayoutManager(layoutManager);
        OrderSelectorRecView.setAdapter(adapter);

        if(hedList.isEmpty()){
            Intent i  = new Intent(InvList.this,Invoice.class);
            startActivity(i);
        }

        SearchView sv = findViewById(R.id.searchview);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        hedList.clear();
        hedList.addAll(LocatOrderList());
        adapter = new Adapter_Order_list(hedList,1,this);
        OrderSelectorRecView.setLayoutManager(layoutManager);
        OrderSelectorRecView.setAdapter(adapter);

        System.out.println(hedList.size());
        System.out.println("onPostResume");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("Distoy Object");

    }


    @NotNull
    private ArrayList<TBLT_ORDERHED> LocatOrderList(){
        ArrayList<TBLT_ORDERHED> hedList = new ArrayList<>();

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM "+ DBQ._TBLT_ORDERHED+" WHERE "+DBQ._TBLT_ORDERHED_Discode+ " = ? AND "+DBQ._TBLT_ORDERHED_RepCode + " = ? AND "+DBQ._TBLT_ORDERHED_CusCode+ " = ? order by Status desc, CAST(DocNo as INt) DESC" ,
                new String[]{String.valueOf(SharedPreference.COM_REP.getDiscode()),
                        String.valueOf(SharedPreference.COM_REP.getRepCode()),
                        String.valueOf(SharedPreference.COM_CUSTOMER.getTxt_code())});
        TBLT_ORDERHED orderhed;
        while (c.moveToNext()){
            orderhed = new TBLT_ORDERHED();

            orderhed.setVatAmt(c.getDouble(c.getColumnIndex(DBQ._TBLT_ORDERHED_VatAmt)));
            orderhed.setStatus(c.getString(c.getColumnIndex(DBQ._TBLT_ORDERHED_Status)));
            orderhed.setSalesDate(c.getString(c.getColumnIndex(DBQ._TBLT_ORDERHED_SalesDate)));
            orderhed.setRefNo(c.getString(c.getColumnIndex(DBQ._TBLT_ORDERHED_RefNo)));
            orderhed.setAreaCode(c.getString(c.getColumnIndex(DBQ._TBLT_ORDERHED_AreaCode)));
            orderhed.setCreateUser(c.getString(c.getColumnIndex(DBQ._TBLT_ORDERHED_CreateUser)));
            orderhed.setCusCode(c.getString(c.getColumnIndex(DBQ._TBLT_ORDERHED_CusCode)));
            orderhed.setDiscode(c.getString(c.getColumnIndex(DBQ._TBLT_ORDERHED_Discode)));
            orderhed.setDiscount(c.getDouble(c.getColumnIndex(DBQ._TBLT_ORDERHED_Discount)));
            orderhed.setDisPer(c.getDouble(c.getColumnIndex(DBQ._TBLT_ORDERHED_DisPer)));
            orderhed.setDocNo(c.getString(c.getColumnIndex(DBQ._TBLT_ORDERHED_DocNo)));
            orderhed.setGrossAmt(c.getDouble(c.getColumnIndex(DBQ._TBLT_ORDERHED_GrossAmt)));
            orderhed.setNetAmt(c.getDouble(c.getColumnIndex(DBQ._TBLT_ORDERHED_NetAmt)));
            orderhed.setISUSED(c.getInt(c.getColumnIndex(DBQ._TBLT_ORDERHED_ISUSED))>0);
            orderhed.setLocCode(c.getString(c.getColumnIndex(DBQ._TBLT_ORDERHED_LocCode)));
            orderhed.setPayType(c.getString(c.getColumnIndex(DBQ._TBLT_ORDERHED_PayType)));
            orderhed.setRepCode(c.getString(c.getColumnIndex(DBQ._TBLT_ORDERHED_RepCode)));

            hedList.add(orderhed);
        }
        c.close();
        db.close();
        return  hedList;
    }
}