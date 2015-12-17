package com.locator_app.locator.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.view.HomeActivity;

public class LoginRegisterStart extends AppCompatActivity {

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

        loginYes = (ImageView) findViewById(R.id.login_yes);
        loginNo = (ImageView) findViewById(R.id.login_no);

        loginYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Click on yes!", Toast.LENGTH_SHORT);
            }
        });

        loginNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Click on no!", Toast.LENGTH_SHORT);
                Intent intent = new Intent(v.getContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}
