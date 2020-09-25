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
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.flexiv.sfino.Invoice;
import com.flexiv.sfino.R;
import com.flexiv.sfino.model.DefModal;
import com.flexiv.sfino.model.TBLT_SALINVHED;
import com.flexiv.sfino.Recept;

import java.util.ArrayList;
import java.util.List;

public class Adapter_OUTCUS_list extends RecyclerView.Adapter<Adapter_OUTCUS_list.ViewsHolder> implements Filterable {

    private List<DefModal> arr;
    private List<DefModal> arr_full;
    private int type;
    private Context con;
    private AlertDialog dialog;


    @NonNull
    @Override
    public ViewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_outcus, parent, false);
        return new ViewsHolder(v);
    }

    public Adapter_OUTCUS_list(List<DefModal> arr, int type, Context con,AlertDialog dialog) {
        this.arr = arr;
        this.arr_full = new ArrayList<>(arr);
        this.type = type;
        this.con = con;
        this.dialog = dialog;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewsHolder holder, int position) {
        DefModal orderhed = arr.get(position);
        holder.OI_DocNo.setText(orderhed.getVal2());
        String price = "Rs " + String.format("%,.2f", Double.parseDouble(orderhed.getVal3()));
        holder.OI_Rs.setText(price);

        if (type != 2) {
            if (Double.parseDouble(orderhed.getVal3()) <= 0) {
                holder.imageView_status.setImageResource(R.drawable.ic_up_done);
                holder.orderInvo_Card.setCardBackgroundColor(Color.LTGRAY);
                holder.orderInvo_Card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(con, Recept.class);
                        i.putExtra("hed", orderhed);
                        con.startActivity(i);
                        // Toast.makeText(con, "Can not Edit this Order", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                holder.imageView_status.setImageResource(R.drawable.ic_baseline_monetization_on_24);
                holder.orderInvo_Card.setCardBackgroundColor(Color.LTGRAY);
                holder.orderInvo_Card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(con, Recept.class);
                        i.putExtra("hed", orderhed);
                        con.startActivity(i);
                        //Toast.makeText(con, "Can not Edit this Order", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }else{
            holder.orderInvo_Card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Recept.curInv = orderhed;
                    dialog.cancel();
                    // Toast.makeText(con, "Can not Edit this Order", Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public Filter getXfilset() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<DefModal> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(arr_full);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (DefModal c : arr_full
                ) {
                    if (c.getVal2().toLowerCase().contains(filterPattern) || c.getVal3().toLowerCase().contains(filterPattern)) {
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
            arr.addAll((List) results.values);
            notifyDataSetChanged();


        }
    };

    public static class ViewsHolder extends RecyclerView.ViewHolder {
        public ImageView imageView_status;
        public TextView OI_DocNo;
        public TextView OI_Rs;
        public CardView orderInvo_Card;

        public ViewsHolder(@NonNull View itemView) {
            super(itemView);
            imageView_status = itemView.findViewById(R.id.imageView_status);
            OI_DocNo = itemView.findViewById(R.id.card_code);
            OI_Rs = itemView.findViewById(R.id.OI_Rs);
            orderInvo_Card = itemView.findViewById(R.id.orderInvo_Card);

        }
    }


}
