package com.locator_app.locator.view;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.locator_app.locator.R;

public class LoginMailActivity extends AppCompatActivity {

    private EditText loginMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_mail);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom);

        //set action bar title
        View v = getSupportActionBar().getCustomView();
        TextView titleTxtView = (TextView) v.findViewById(R.id.actionbar_title);
        titleTxtView.setText(R.string.login);

        loginMail = (EditText) findViewById(R.id.loginMail);

        loginMail.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    Intent intent = new Intent(v.getContext(), LoginPasswordActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }
}
