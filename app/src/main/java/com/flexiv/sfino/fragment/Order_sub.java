package com.flexiv.sfino.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.flexiv.sfino.Order;
import com.flexiv.sfino.R;
import com.flexiv.sfino.model.Modal_Batch;
import com.flexiv.sfino.utill.SharedPreference;

public class Order_sub extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_sub,container,false);
    }


    private Order context;
    private Modal_Batch item;

    //Components
    TextView textView_itemName;
    TextView textView_itemCode;
    TextView textView_BatchCode;
    TextView textView_Price;

    EditText txt_avlQty;

    public Order_sub(Order context, Modal_Batch item) {
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



        setItem();
    }

    private void setItem(){
        textView_BatchCode.setText(item.getCode());
        textView_itemCode.setText(item.getItemCode());
        textView_itemName.setText(item.getDesc());
        textView_Price.setText(SharedPreference.df.format(item.getPrice()));
        txt_avlQty.setText(SharedPreference.df.format(item.getSih()));
    }
}
