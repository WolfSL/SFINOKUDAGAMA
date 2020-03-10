package com.flexiv.sfino;

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

import com.flexiv.sfino.model.Card_cus_area;
import com.flexiv.sfino.utill.SharedPreference;

import java.util.ArrayList;
import java.util.List;

public class Adapter_Cus_area extends RecyclerView.Adapter<Adapter_Cus_area.ViewsHolder> implements Filterable {

    private ArrayList<Card_cus_area> arr;
    private ArrayList<Card_cus_area> arr_full;
    private int type;
    private AlertDialog dialog;

    @NonNull
    @Override
    public ViewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_cus_area, parent, false);
        ViewsHolder mvh = new ViewsHolder(v);
        return mvh;
    }

    public Adapter_Cus_area(ArrayList<Card_cus_area> arr, int type, androidx.appcompat.app.AlertDialog dialog) {
        this.arr = arr;
        this.arr_full = new ArrayList<>(arr);
        this.type = type;
        this.dialog = dialog;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewsHolder holder, int position) {
        Card_cus_area cusArea = arr.get(position);
        holder.card_name.setText(cusArea.getTxt_name());
        holder.card_code.setText(cusArea.getTxt_code());

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type==1){
                    SharedPreference.COM_CUSTOMER = cusArea;
                }else if(type==0){
                    SharedPreference.COM_AREA = cusArea;
                }
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
            ArrayList<Card_cus_area> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(arr_full);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Card_cus_area c : arr_full
                ) {
                    if(c.getTxt_name().toLowerCase().contains(filterPattern)){
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
        public TextView card_name;
        public CardView card;

        public ViewsHolder(@NonNull View itemView) {
            super(itemView);
            card_code = itemView.findViewById(R.id.card_code);
            card_name = itemView.findViewById(R.id.card_name);
            card = itemView.findViewById(R.id.cusareacard);

        }
    }


}
