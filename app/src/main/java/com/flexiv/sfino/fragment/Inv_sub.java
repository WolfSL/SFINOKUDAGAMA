package com.flexiv.sfino.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.flexiv.sfino.Invoice;
import com.flexiv.sfino.Order;
import com.flexiv.sfino.R;
import com.flexiv.sfino.model.Modal_Batch;
import com.flexiv.sfino.model.TBLT_ORDDTL;
import com.flexiv.sfino.model.TBLT_SALINVDET;
import com.flexiv.sfino.model.TBLT_SALINVHED;
import com.flexiv.sfino.utill.SharedPreference;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class Inv_sub extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inv_sub, container, false);
    }


    private Invoice context;
    private Modal_Batch item;

    //Components
    TextView textView_itemName;
    TextView textView_itemCode;
    TextView textView_BatchCode;
    TextView textView_Price;

    Button button_add;

    TextInputLayout textInputLayoutQty1;

    EditText dis_pre;
    EditText edt_disVal;
    EditText edt_FI1;

    EditText txt_avlQty;
    EditText edt_Qtyx;

    public Inv_sub(Invoice context, Modal_Batch item) {
        this.context = context;
        this.item = item;
        context.changeNavButton(2);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textView_itemName = view.findViewById(R.id.textView_itemName);
        textView_itemCode = view.findViewById(R.id.textView_itemCode);
        textView_BatchCode = view.findViewById(R.id.textView_BatchCode);
        textView_Price = view.findViewById(R.id.textView_Price);
        txt_avlQty = view.findViewById(R.id.txt_avlQty);
        dis_pre = view.findViewById(R.id.dis_pre);
        edt_disVal = view.findViewById(R.id.edt_disVal);
        edt_FI1 = view.findViewById(R.id.edt_FI1);
        edt_Qtyx = view.findViewById(R.id.edt_Qtyx);
        textInputLayoutQty1 = view.findViewById(R.id.textInputLayoutQty1);
        button_add = view.findViewById(R.id.button3);

        button_add.setOnClickListener(view1 -> createItem());

        edt_Qtyx.setOnFocusChangeListener((view1, b) -> dis_pre.setText(""));

        dis_pre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    edt_disVal.setText(SharedPreference.df.format(GenDisVal(charSequence)));
                } else {
                    edt_disVal.getText().clear();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        setItem();
    }

    private void setItem() {
        textView_BatchCode.setText(item.getBatchNo());
        textView_itemCode.setText(item.getItemCode());
        textView_itemName.setText(item.getDesc());
        textView_Price.setText(SharedPreference.df.format(item.getRetialPrice()));
        txt_avlQty.setText(SharedPreference.df.format(item.getSHI()));
    }

    private void createItem() {
        Order.hideKeyboard(context);

        if(edt_Qtyx.getText().length()<=0){
            textInputLayoutQty1.setError("Please Enter Qty");
            return;
        }
        TBLT_SALINVDET obj = new TBLT_SALINVDET();
        obj.setItemCode(item.getItemCode());
        obj.setItemName(item.getDesc());
        obj.setExpiryDate(item.getBatchNo());
        obj.setUnitPrice(item.getRetialPrice());
        obj.setDiscPer(Double.parseDouble(dis_pre.getText().length()>0?dis_pre.getText().toString():"0"));
        obj.setDiscAmt(Double.parseDouble(edt_disVal.getText().length()>0?edt_disVal.getText().toString():"0"));
        obj.setItQty(Double.parseDouble(edt_Qtyx.getText().toString()));
        //obj.setUsedQty(0);
        obj.setDate(SharedPreference.dateFormat.format(Calendar.getInstance().getTime()));
        obj.setCusCode(SharedPreference.COM_CUSTOMER.getTxt_code());
       // obj.setTradeFQTY(Double.parseDouble(edt_FI1.getText().length()>0?edt_FI1.getText().toString():"0"));
        obj.setLineID(0);
        obj.setAmount((item.getRetialPrice() * obj.getItQty())-obj.getDiscAmt());

       context.setItemList(obj);


        context.GoBackWithItems();
    }

    private double GenDisVal(CharSequence s) {
        if (edt_Qtyx.getText().length()>0) {
            double subTot = item.getRetialPrice() * Double.parseDouble(edt_Qtyx.getText().toString());
            return subTot * (Double.parseDouble(s.toString()) / 100);

        }
        return 0;

    }
}
