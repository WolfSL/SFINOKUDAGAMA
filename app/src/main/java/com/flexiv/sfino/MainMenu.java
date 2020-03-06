package com.flexiv.sfino;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.flexiv.sfino.utill.SharedPreference;

import java.util.ArrayList;

public class MainMenu extends AppCompatActivity {

    private RecyclerView cusRecyclerView;
    private RecyclerView.Adapter cusAdapter;
    private RecyclerView.LayoutManager cusLayoutManager;

    private void initComp(){
        //Components
        TextView textViewRep_name = findViewById(R.id.textViewRep_name);

        //Arr
        ArrayList<Card_cus_area> arr_cus = new ArrayList<>();
        arr_cus.add(new Card_cus_area("test code","test name"));
        arr_cus.add(new Card_cus_area("test code","test name"));
        arr_cus.add(new Card_cus_area("test code","test name"));
        arr_cus.add(new Card_cus_area("test code","test name"));
        arr_cus.add(new Card_cus_area("test code","test name"));
        arr_cus.add(new Card_cus_area("test code","test name"));

        //setArgs
        textViewRep_name.setText(SharedPreference.COM_REP.getRepName());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        initComp();
    }
}
