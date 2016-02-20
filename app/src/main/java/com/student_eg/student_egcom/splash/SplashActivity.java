package com.student_eg.student_egcom.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.student_eg.student_egcom.MainActivity;
import com.student_eg.student_egcom.R;
import com.student_eg.student_egcom.login.LoginActivity;
import com.student_eg.student_egcom.utils.Constants;


public class SplashActivity extends AppCompatActivity {
    private ImageView image0;
    private Animation fade0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        image0 = (ImageView) findViewById(R.id.logo);
        fade0 = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        image0.startAnimation(fade0);
        fade0.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // check on user id
                SharedPreferences settings = getSharedPreferences(Constants.STUDENT_EG_PREF, 0);
                long user_id = settings.getLong(Constants.USER_ID, -1);
                if(user_id == -1){
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                }else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                }


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }


}
