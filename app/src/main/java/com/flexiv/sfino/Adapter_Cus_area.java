package com.flexiv.sfino;

import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Adapter_Cus_area extends RecyclerView.Adapter<Adapter_Cus_area.ViewsHolder> {

    @NonNull
    @Override
    public ViewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.)
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewsHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class  ViewsHolder extends RecyclerView.ViewHolder{
        public TextureView card_code;
        public TextureView card_name;

        public ViewsHolder(@NonNull View itemView) {
            super(itemView);
            card_code = itemView.findViewById(R.id.card_code);
            card_name = itemView.findViewById(R.id.card_name);


        }
    }


}
