package com.flexiv.sfino;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flexiv.sfino.utill.SharedPreference;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private ImageView flex_logo;
    private TextView appname;
    public static Activity fa;





    private void doAnimation(){
        fa= this;

        flex_logo = findViewById(R.id.flex_logo);
        appname = findViewById(R.id.appname);

        SharedPreference.setSettings(this);

        System.out.println(SharedPreference.settings_pin);

        int splash_tine = 1200;
        Handler h = new Handler();

        h.postDelayed(() -> {
            Pair[] pairs = new Pair[2];
            pairs[0] = new Pair<View,String>(flex_logo,"flex_logo");
            pairs[1] = new Pair<View,String>(appname,"appname");
            Intent i = new Intent(MainActivity.this,Login.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
            startActivity(i,options.toBundle());
        }, splash_tine);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        doAnimation();
    }
}
