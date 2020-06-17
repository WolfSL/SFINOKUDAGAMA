package com.flexiv.sfino.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.flexiv.sfino.Order;
import com.flexiv.sfino.R;
import com.flexiv.sfino.model.Modal_Batch;
import com.flexiv.sfino.utill.SharedPreference;

import java.util.ArrayList;
import java.util.List;

public class Adapter_Batch extends RecyclerView.Adapter<Adapter_Batch.ViewsHolder> implements Filterable {

    private ArrayList<Modal_Batch> arr;
    private ArrayList<Modal_Batch> arr_full;
    private AlertDialog dialog;
    private Order context;

    @NonNull
    @Override
    public ViewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_batch_item, parent, false);
        ViewsHolder mvh = new ViewsHolder(v);
        return mvh;
    }

    public Adapter_Batch(ArrayList<Modal_Batch> arr, AlertDialog dialog, Order context) {
        this.arr = arr;
        this.arr_full = new ArrayList<>(arr);
        this.dialog = dialog;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewsHolder holder, int position) {
        Modal_Batch item = arr.get(position);
        holder.card_name.setText(SharedPreference.df.format(item.getRetialPrice()));
        holder.card_code.setText(item.getBatchNo());
        holder.card_code2.setText(SharedPreference.df.format(item.getSHI()));

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.LoadOrderSub(item);
                dialog.cancel();
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
            ArrayList<Modal_Batch> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(arr_full);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Modal_Batch c : arr_full
                ) {
                    if(c.getBatchNo().toLowerCase().contains(filterPattern)){
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
        public TextView card_code;
        public TextView card_code2;
        public TextView card_name;
        public CardView card;

        public ViewsHolder(@NonNull View itemView) {
            super(itemView);
            card_code = itemView.findViewById(R.id.card_code);
            card_code2 = itemView.findViewById(R.id.card_price);
            card_name = itemView.findViewById(R.id.card_name);
            card = itemView.findViewById(R.id.itembatch_card);

        }
    }


}
