package com.flexiv.sfino.fragment;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

import java.math.BigDecimal;
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
    TextInputLayout textInputLayoutDis;

    EditText dis_pre;
    EditText edt_disVal;
    EditText edt_FI1;

    EditText txt_avlQty;
    EditText edt_Qtyx;
    int invStatus = 0;
    public Inv_sub(Invoice context, Modal_Batch item,int invStatus) {
        this.context = context;
        this.item = item;
        context.changeNavButton(2);

        this.invStatus = invStatus;
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
        textInputLayoutDis = view.findViewById(R.id.textInputLayoutDis);
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
        Log.e("item.getVolume()",String.valueOf(item.getVolume()));
        textView_Price.setText(SharedPreference.df.format(item.getRetialPrice()));
        txt_avlQty.setText(SharedPreference.df.format(item.getSHI()));

        if(invStatus==1){
            textInputLayoutQty1.setHint("Return Qty");
           // edt_Qtyx.setHint("Return Qty");
            edt_Qtyx.setTextColor(Color.RED);
           // edt_disVal.setHint("Return Price");
            textInputLayoutDis.setHint("Return Price");
            button_add.setText("Return Item");
            button_add.setBackgroundColor(Color.RED);
        }
    }

    private void createItem() {
        Order.hideKeyboard(context);

        if(edt_Qtyx.getText().length()<=0){
            textInputLayoutQty1.setError("Please Enter Qty");
            return;
        }
        if(edt_disVal.getText().length()<=0){
            textInputLayoutDis.setError("Please Enter Price");
            return;
        }
        TBLT_SALINVDET obj = new TBLT_SALINVDET();

        item.setCostPrice(item.getRetialPrice());
        item.setRetialPrice(Double.parseDouble(edt_disVal.getText().length()>0?edt_disVal.getText().toString():"0"));
        obj.setItemCode(item.getItemCode());
        obj.setItemName(item.getDesc());
        obj.setExpiryDate(item.getBatchNo());
        obj.setUnitPrice(item.getRetialPrice());
        obj.setCostPrice(item.getRetialPrice());
        obj.setExpDate(item.getExpDate());
        obj.setTourID(item.getTourID());
        obj.setDiscPer(0);
        obj.setDiscAmt(0);
        obj.setVolume(item.getVolume());
        obj.setItQty(Double.parseDouble(edt_Qtyx.getText().toString()));

        //obj.setUsedQty(0);
        obj.setDate(SharedPreference.dateFormat.format(Calendar.getInstance().getTime()));
        obj.setCusCode(SharedPreference.COM_CUSTOMER.getTxt_code());
       // obj.setTradeFQTY(Double.parseDouble(edt_FI1.getText().length()>0?edt_FI1.getText().toString():"0"));
        obj.setFQTY(Double.parseDouble(edt_FI1.getText().length()>0?edt_FI1.getText().toString():"0"));
        obj.setLineID(0);
        obj.setAmount((item.getRetialPrice() * obj.getItQty()));

        if(item.getSHI()<0){
            obj.setULQty(obj.getItQty());
        }
        else if(obj.getItQty()>item.getSHI()){
            obj.setULQty(obj.getItQty()-item.getSHI());
        }else{
            obj.setULQty(0);
        }

        if(invStatus==1){
            obj.setItQty(BigDecimal.valueOf(obj.getItQty()).negate().doubleValue());
            obj.setAmount(BigDecimal.valueOf(obj.getAmount()).negate().doubleValue());
        }

        if(obj.getUnitPrice()<item.getCostPrice()/item.getVolume()){
            new AlertDialog.Builder(context).setTitle("Price Warning!")
                    .setMessage("Price is lower than Cost. are you sure is this normal?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            context.setItemList(obj);
                            context.GoBackWithItems();
                        }
                    })
                    .setNegativeButton("No", null)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        }else{
            context.setItemList(obj);
            context.GoBackWithItems();
        }

    }

    private double GenDisVal(CharSequence s) {
        if (edt_Qtyx.getText().length()>0) {
            double subTot = item.getRetialPrice() * Double.parseDouble(edt_Qtyx.getText().toString());
            return subTot * (Double.parseDouble(s.toString()) / 100);

        }
        return 0;

    }
}
