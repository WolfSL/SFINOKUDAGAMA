package com.flexiv.sfino.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.flexiv.sfino.Order;
import com.flexiv.sfino.R;
import com.flexiv.sfino.model.TBLT_ORDDTL;
import com.flexiv.sfino.utill.Fragment_sub_batching;
import com.flexiv.sfino.utill.SharedPreference;

import java.util.ArrayList;

public class Adapter_Oeder_Item extends RecyclerView.Adapter<Adapter_Oeder_Item.ViewsHolder> {

    private ArrayList<TBLT_ORDDTL> arr;
    private ArrayList<TBLT_ORDDTL> arr_full;
    private Fragment_sub_batching context;

    @NonNull
    @Override
    public ViewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_order_item, parent, false);
        ViewsHolder mvh = new ViewsHolder(v);
        return mvh;
    }

    public Adapter_Oeder_Item(ArrayList<TBLT_ORDDTL> arr, Fragment_sub_batching context) {
        this.arr = arr;
        this.arr_full = new ArrayList<>(arr);
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewsHolder holder, int position) {
        TBLT_ORDDTL item = arr.get(position);
        holder.item_batch.setText(item.getBATCH());
        holder.item_code.setText(item.getItemCode());
        holder.item_name.setText(item.getItemName());
        holder.item_retPrice.setText(SharedPreference.df.format(item.getUnitPrice()));
        holder.item_disVal.setText(SharedPreference.df.format(item.getDiscAmt()));
        holder.item_nfqty.setText(SharedPreference.df.format(item.getFQTY()));
        holder.item_total.setText(SharedPreference.df.format(item.getAmount()));
        holder.item_rqqty.setText(SharedPreference.df.format(item.getItQty()));

        holder.orderItem_card.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }




    public static class ViewsHolder extends RecyclerView.ViewHolder {
        public TextView item_code;
        public TextView item_retPrice;
        public TextView item_batch;
        public TextView item_name;
        public TextView item_rqqty;
        public TextView item_nfqty;
        public TextView item_disVal;
        public TextView item_total;
        public CardView orderItem_card;

        public ViewsHolder(@NonNull View itemView) {
            super(itemView);
            item_code = itemView.findViewById(R.id.item_code);
            item_retPrice = itemView.findViewById(R.id.item_retPrice);
            item_batch = itemView.findViewById(R.id.item_batch);
            item_name = itemView.findViewById(R.id.item_name);
            item_rqqty = itemView.findViewById(R.id.item_rqqty);
            item_nfqty = itemView.findViewById(R.id.item_nfqty);
            item_disVal = itemView.findViewById(R.id.item_disVal);
            item_total = itemView.findViewById(R.id.item_total);
            orderItem_card = itemView.findViewById(R.id.orderItem_card);

        }
    }


}
