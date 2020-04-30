package com.flexiv.sfino.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flexiv.sfino.Order;
import com.flexiv.sfino.R;
import com.flexiv.sfino.adapter.Adapter_Cus_area;
import com.flexiv.sfino.adapter.Adapter_Item;
import com.flexiv.sfino.model.Card_cus_area;
import com.flexiv.sfino.model.Modal_Item;
import com.flexiv.sfino.utill.SharedPreference;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class Order_main extends Fragment {
    private final static String TAG = "Order_main";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_main,container,false);
    }

    //Components
    private Order context;

    private FloatingActionButton floatingActionButton_OM;

    public Order_main(Order context) {
        this.context = context;
        context.changeNavButton(1);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //FAB Action
        view.findViewById(R.id.floatingActionButton_OM).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"FAB Onclic");
                context.LoadFragment_sub();
                //context.getSupportFragmentManager().beginTransaction().replace(R.id.OrderFrame,new Order_sub(context)).commit();
            }
        });
    }

}
