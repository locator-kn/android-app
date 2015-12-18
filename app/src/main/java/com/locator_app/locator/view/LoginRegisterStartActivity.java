package com.locator_app.locator.view;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;;
import com.locator_app.locator.R;
import com.locator_app.locator.service.BitmapWorkerTask;

public class LoginRegisterStartActivity extends AppCompatActivity {

    private ImageView locatorLogo;
    private ImageView loginYes;
    private ImageView loginNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register_start);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom);
        //TODO: check if already authenticated
        //TODO: getDeviceId

        locatorLogo = (ImageView) findViewById(R.id.locator_logo);
        loginYes = (ImageView) findViewById(R.id.login_yes);
        loginNo = (ImageView) findViewById(R.id.login_no);
        loadBitmap(R.drawable.locator_logo, locatorLogo, 700, 400);
        loadBitmap(R.drawable.yes, loginYes, 200, 200);
        loadBitmap(R.drawable.no, loginNo, 200, 200);

        loginYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LoginRegisterActivity.class);
                startActivity(intent);
            }
        });

        loginNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    //Load every image in separate thread
    public void loadBitmap(int resId, ImageView imageView, int width, int height) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView, getResources());
        task.execute(resId, width, height);
    }
}
