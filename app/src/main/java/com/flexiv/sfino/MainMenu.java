package com.flexiv.sfino;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.flexiv.sfino.utill.SharedPreference;

public class MainMenu extends AppCompatActivity {

    private void initComp(){
        //Components
        TextView textViewRep_name = findViewById(R.id.textViewRep_name);


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
