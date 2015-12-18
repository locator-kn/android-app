package com.locator_app.locator.view;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.locator_app.locator.R;

public class LoginMailActivity extends AppCompatActivity {

    private TextView actionbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_mail);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom);
        actionbarTitle = (TextView) findViewById(R.id.actionbar_title);
        actionbarTitle.setText(R.string.login);
    }
}
