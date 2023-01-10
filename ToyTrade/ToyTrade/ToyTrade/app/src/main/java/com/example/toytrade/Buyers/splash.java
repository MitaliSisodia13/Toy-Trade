package com.example.toytrade.Buyers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.toytrade.R;

public class splash extends AppCompatActivity {
    TextView t;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        t=findViewById(R.id.textv);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(splash.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        }, 5000);
        Animation myanimation= AnimationUtils.loadAnimation(splash.this,R.anim.animation);
        t.startAnimation(myanimation);
    }

}
