package com.flexiv.sfino;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.flexiv.sfino.adapter.Adapter_INV_list;
import com.flexiv.sfino.adapter.Adapter_OUTCUS_list;
import com.flexiv.sfino.model.DefModal;
import com.flexiv.sfino.model.DefModalList;
import com.flexiv.sfino.model.TBLT_SALINVHED;
import com.flexiv.sfino.utill.SharedPreference;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PaymentList extends AppCompatActivity {

    private FloatingActionButton floatingActionButton_OL;
    private TextView textView_Area, textView_cusName;
    private RecyclerView OrderSelectorRecView;
    private Adapter_OUTCUS_list adapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<DefModal> hedList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_list);

        textView_Area = findViewById(R.id.textView_Area);
        hedList = new ArrayList<>();

        OrderSelectorRecView = findViewById(R.id.OrderSelectorRecView);
        layoutManager = new LinearLayoutManager(this);
        adapter = new Adapter_OUTCUS_list(hedList, 1, this,null);
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

        //download();
    }

    private ProgressDialog dialog;
    public void download() {
        hedList.clear();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading... Please Wait!");
        dialog.show();
        String url = SharedPreference.URL + "Payment/getOutsCusList?disCode="+SharedPreference.COM_REP.getDiscode()+"&areaCode="+SharedPreference.COM_AREA.getTxt_code();
        System.out.println(url);

        RequestQueue rq = Volley.newRequestQueue(this);

        JsonObjectRequest jr = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    Gson gson = new Gson();
                    DefModalList master = gson.fromJson(response.toString(), DefModalList.class);

                    if(master!=null){
                        hedList.addAll(master.getList());
                        adapter.notifyDataSetChanged();
                    }

                    dialog.dismiss();
                },
                error -> {
                    Toast.makeText(PaymentList.this,error.getMessage(),Toast.LENGTH_LONG).show();
                    System.out.println("Rest Errr :" + error.toString());
                    dialog.dismiss();
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

    @Override
    protected void onResume() {
        super.onResume();
        download();
    }
}