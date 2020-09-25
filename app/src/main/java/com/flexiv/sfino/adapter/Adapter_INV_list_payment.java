package com.flexiv.sfino.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.flexiv.sfino.Invoice;
import com.flexiv.sfino.R;
import com.flexiv.sfino.model.TBLT_CUSRECEIPTDTL;
import com.flexiv.sfino.model.TBLT_SALINVHED;
import com.flexiv.sfino.utill.SharedPreference;

import java.util.ArrayList;
import java.util.List;

public class Adapter_INV_list_payment extends RecyclerView.Adapter<Adapter_INV_list_payment.ViewsHolder>{

    private ArrayList<TBLT_CUSRECEIPTDTL> arr;
    private ArrayList<TBLT_CUSRECEIPTDTL> arr_full;
    private int type;
    private Context con;


    @NonNull
    @Override
    public ViewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_order_invoice, parent, false);
        return new ViewsHolder(v);
    }

    public Adapter_INV_list_payment(ArrayList<TBLT_CUSRECEIPTDTL> arr, int type, Context con) {
        this.arr = arr;
        this.arr_full = new ArrayList<>(arr);
        this.type = type;
        this.con = con;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewsHolder holder, int position) {
        TBLT_CUSRECEIPTDTL orderhed = arr.get(position);
        holder.OI_DocNo.setText(orderhed.getRefNo());

        String price = "Net Rs. " + SharedPreference.ds_formatter.format(orderhed.getNetAmt());
        String price2 = "Due Rs. " + SharedPreference.ds_formatter.format(orderhed.getDueAmt());
        String price3 = "Paid Rs. " + SharedPreference.ds_formatter.format(orderhed.getPaidAmt());
        holder.OI_Rs.setText(price);
        holder.OI_Date.setText(price2);
        holder.OI_Date2.setText(price3);

        if (orderhed.getStatus().equals("A")) {
            holder.imageView_status.setImageResource(R.drawable.ic_up_done);
            holder.orderInvo_Card.setCardBackgroundColor(Color.LTGRAY);

        } else if (orderhed.getStatus().equals("E")) {
            holder.imageView_status.setImageResource(R.drawable.ic_baseline_monetization_on_24);
            holder.orderInvo_Card.setCardBackgroundColor(Color.LTGRAY);

        }
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }



    public static class ViewsHolder extends RecyclerView.ViewHolder {
        public ImageView imageView_status;
        public TextView OI_DocNo;
        public TextView OI_Rs;
        public TextView OI_Date,OI_Date2;
        public CardView orderInvo_Card;

        public ViewsHolder(@NonNull View itemView) {
            super(itemView);
            imageView_status = itemView.findViewById(R.id.imageView_status);
            OI_DocNo = itemView.findViewById(R.id.card_code);
            OI_Rs = itemView.findViewById(R.id.OI_Rs);
            OI_Date = itemView.findViewById(R.id.OI_Date);
            orderInvo_Card = itemView.findViewById(R.id.orderInvo_Card);
            OI_Date2 = itemView.findViewById(R.id.OI_Date2);


        }
    }


}
