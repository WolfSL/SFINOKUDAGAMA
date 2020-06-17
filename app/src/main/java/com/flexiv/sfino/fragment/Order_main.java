package com.flexiv.sfino.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flexiv.sfino.Order;
import com.flexiv.sfino.R;
import com.flexiv.sfino.adapter.Adapter_Oeder_Item;
import com.flexiv.sfino.model.TBLT_ORDDTL;
import com.flexiv.sfino.model.TBLT_ORDERHED;
import com.flexiv.sfino.utill.SharedPreference;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Order_main extends Fragment {
    private final static String TAG = "Order_main";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_main,container,false);
    }

    TextView textView_total;
    TextView textView_Nettotal;
    TextView textView_tax;

    EditText DisPre, DisAmt;

    Button button_save;

    private RecyclerView Order_recView;
    private Adapter_Oeder_Item adaper;
    private RecyclerView.LayoutManager layoutManager;

    //Components
    private Order context;

    private FloatingActionButton floatingActionButton_OM;

    private static Order_main order_main;
    public static Order_main getObj(Order context){
        if(order_main==null){
            order_main = new Order_main(context);
        }
        return order_main;

    }

    private Order_main(Order context) {
        this.context = context;
        context.changeNavButton(1);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        constraintLayout3 = view.findViewById(R.id.constraintLayout3);

        textView_total = view.findViewById(R.id.textView10);
        textView_Nettotal = view.findViewById(R.id.textView19);
        textView_tax = view.findViewById(R.id.textView17);
        DisPre = view.findViewById(R.id.editText2);
        DisAmt = view.findViewById(R.id.editText3);
        button_save = view.findViewById(R.id.button_save);

        //SAVE...................................
        button_save.setOnClickListener(view1 -> {
            Toast.makeText(context,context.Save(CreateORDERHED()),Toast.LENGTH_LONG).show();
        });

        Order_recView = view.findViewById(R.id.Order_recView);
        layoutManager = new LinearLayoutManager(context);
        adaper = new Adapter_Oeder_Item(context.getItemList(),context);
        Order_recView.setLayoutManager(layoutManager);
        Order_recView.setAdapter(adaper);
        //FAB Action
        view.findViewById(R.id.floatingActionButton_OM).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"FAB Onclic");
                context.LoadFragment_sub();
                //context.getSupportFragmentManager().beginTransaction().replace(R.id.OrderFrame,new Order_sub(context)).commit();
            }
        });

        DisPre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    DisAmt.setText(SharedPreference.df.format(GenDisVal(charSequence)));
                    textView_Nettotal.setText(SharedPreference.df.format(total-GenDisVal(charSequence)));
                } else {
                    DisAmt.getText().clear();
                    textView_Nettotal.setText(SharedPreference.df.format(total));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        DisAmt.setOnFocusChangeListener((view1, b) -> DisPre.getText().clear());
        DisAmt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    textView_Nettotal.setText(SharedPreference.df.format(total-Double.parseDouble(DisAmt.getText().toString())));
                } else {
                    textView_Nettotal.setText(SharedPreference.df.format(total));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    double total = 0;
    public void refreshItems(){
        total = 0;
        for(TBLT_ORDDTL obj : context.getItemList()){
            total=total+ obj.getAmount();
        }
        textView_total.setText(SharedPreference.df.format(total));
        double netTotal = total-Double.parseDouble(textView_tax.getText().toString());
        textView_Nettotal.setText(SharedPreference.df.format(netTotal));
        DisPre.getText().clear();
        DisAmt.getText().clear();
        adaper.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshItems();
    }

    ConstraintLayout constraintLayout3;

    private double GenDisVal(CharSequence s) {
        if (DisPre.getText().length()>0) {
            return total * (Double.parseDouble(s.toString()) / 100);
        }
        return 0;

    }

    private TBLT_ORDERHED   CreateORDERHED(){
        TBLT_ORDERHED obj = new TBLT_ORDERHED();
        obj.setAreaCode(SharedPreference.COM_AREA.getTxt_code());
        obj.setCreateUser(SharedPreference.COM_REP.getRepName());
        obj.setCusCode(SharedPreference.COM_CUSTOMER.getTxt_code());
        obj.setDiscode(SharedPreference.disid);
        obj.setDiscount(Double.parseDouble(DisAmt.getText().length()<=0?"0":DisAmt.getText().toString()));
        obj.setDisPer(Double.parseDouble(DisPre.getText().length()<=0?"0":DisPre.getText().toString()));
        obj.setDocNo("0");
        obj.setGrossAmt(Double.parseDouble(textView_total.getText().toString()));
        obj.setNetAmt(Double.parseDouble(textView_Nettotal.getText().toString()));
        obj.setISUSED(false);
        obj.setLocCode("");
        obj.setPayType("CASH");
        obj.setRepCode(SharedPreference.COM_REP.getRepCode());
        return obj;
    }
}
