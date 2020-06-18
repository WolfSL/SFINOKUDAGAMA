package com.flexiv.sfino.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.flexiv.sfino.Order;
import com.flexiv.sfino.R;
import com.flexiv.sfino.model.TBLT_ORDERHED;
import com.flexiv.sfino.utill.SharedPreference;

import java.util.ArrayList;
import java.util.List;

public class Adapter_Order_list extends RecyclerView.Adapter<Adapter_Order_list.ViewsHolder> implements Filterable {

    private ArrayList<TBLT_ORDERHED> arr;
    private ArrayList<TBLT_ORDERHED> arr_full;
    private int type;
    private Context con;


    @NonNull
    @Override
    public ViewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_order_invoice, parent, false);
        return new ViewsHolder(v);
    }

    public Adapter_Order_list(ArrayList<TBLT_ORDERHED> arr, int type, Context con) {
        this.arr = arr;
        this.arr_full = new ArrayList<>(arr);
        this.type = type;
        this.con = con;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewsHolder holder, int position) {
        TBLT_ORDERHED orderhed = arr.get(position);
        holder.OI_DocNo.setText(orderhed.getRefNo());
        holder.OI_Date.setText(orderhed.getSalesDate());
        String price = "Rs "+ SharedPreference.df.format(orderhed.getNetAmt());
        holder.OI_Rs.setText(price);
        holder.orderInvo_Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(con, Order.class);
                i.putExtra("hed",orderhed);
                con.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public Filter getXfilset(){
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<TBLT_ORDERHED> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(arr_full);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (TBLT_ORDERHED c : arr_full
                ) {
                    if(c.getDocNo().toLowerCase().contains(filterPattern)){
                        filteredList.add(c);
                    }
                }

            }
            FilterResults res = new FilterResults();
            res.values = filteredList;

            return res;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
                arr.clear();
                arr.addAll((List)results.values);
                notifyDataSetChanged();


        }
    };

    public static class ViewsHolder extends RecyclerView.ViewHolder {
        public ImageView imageView_status;
        public TextView OI_DocNo;
        public TextView OI_Rs;
        public TextView OI_Date;
        public CardView orderInvo_Card;

        public ViewsHolder(@NonNull View itemView) {
            super(itemView);
            imageView_status = itemView.findViewById(R.id.imageView_status);
            OI_DocNo = itemView.findViewById(R.id.card_code);
            OI_Rs = itemView.findViewById(R.id.OI_Rs);
            OI_Date = itemView.findViewById(R.id.OI_Date);
            orderInvo_Card = itemView.findViewById(R.id.orderInvo_Card);

        }
    }


}
