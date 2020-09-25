package com.flexiv.sfino;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.TextView;

import com.flexiv.sfino.adapter.Adapter_Batch;
import com.flexiv.sfino.adapter.Adapter_Item;
import com.flexiv.sfino.adapter.Adapter_Order_list;
import com.flexiv.sfino.adapter.Adapter_Stock;
import com.flexiv.sfino.model.Modal_Item;
import com.flexiv.sfino.model.TBLT_ORDERHED;
import com.flexiv.sfino.utill.DBHelper;
import com.flexiv.sfino.utill.SharedPreference;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class StockBal extends AppCompatActivity {

    private TextView textView_DisName;
    private RecyclerView OrderSelectorRecView;
    private Adapter_Stock adapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_bal);

        textView_DisName = findViewById(R.id.textView_DisName);

        textView_DisName.setText(SharedPreference.COM_REP.getDisName());

        adapter = new Adapter_Stock(getStock());

        OrderSelectorRecView = findViewById(R.id.OrderSelectorRecView);
        layoutManager = new LinearLayoutManager(this);
        OrderSelectorRecView.setLayoutManager(layoutManager);
        OrderSelectorRecView.setAdapter(adapter);

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

    private ArrayList<Modal_Item> getStock(){
        ArrayList<Modal_Item> stock = new ArrayList<>();

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "select st.ItemCode,item.ItemDes,AVG(st.RetialPrice) as RetialPrice,sum(st.SIH) as SIH from TBLM_ITEM as item left outer join TBLM_BATCHWISESTOCK  as st\n" +
                "on item.ItemCode = st.ItemCode where DisCode = ? group by st.ItemCode, item.ItemDes";
        Cursor c = db.rawQuery(sql,new String[]{SharedPreference.COM_REP.getDiscode()});
        Modal_Item item;
        while (c.moveToNext()){
            item = new Modal_Item();
            item.setItemCode(c.getString(c.getColumnIndex("ItemCode")));
            item.setDesc(c.getString(c.getColumnIndex("ItemDes")));
            item.setSih(c.getString(c.getColumnIndex("SIH")));
            item.setRetPrice(c.getString(c.getColumnIndex("RetialPrice")));
            stock.add(item);
        }

        return stock;
    }
}