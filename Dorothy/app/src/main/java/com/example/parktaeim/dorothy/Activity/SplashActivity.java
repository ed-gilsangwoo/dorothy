package com.example.parktaeim.dorothy.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.parktaeim.dorothy.R;

/**
 * Created by parktaeim on 2017. 9. 30..
 */

public class SplashActivity extends Activity{
    private Drawable drawable;
    private ImageView backgroundImg;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        backgroundImg = (ImageView) findViewById(R.id.splashImage);
        Glide.with(this).load(R.drawable.img_splash).into(backgroundImg);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this,StartActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);
    }
}


