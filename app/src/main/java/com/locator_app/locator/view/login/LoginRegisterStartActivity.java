package com.locator_app.locator.view.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.locator_app.locator.R;
import com.locator_app.locator.view.LocatorHeader;
import com.locator_app.locator.view.home.HomeActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginRegisterStartActivity extends Activity {

    @Bind(R.id.locator_logo)
    ImageView locatorLogo;

    @Bind(R.id.login_yes)
    ImageView loginYes;

    @Bind(R.id.login_no)
    ImageView loginNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register_start);
        ButterKnife.bind(this);

        LocatorHeader header = new LocatorHeader(this);
        header.setTitle(R.string.welcome_to);
        header.hideCancelIcon();
        header.hideBackIcon();
    }

    @OnClick(R.id.login_yes)
    void onLoginYesClicked() {
        Intent intent = new Intent(getApplicationContext(), LoginRegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @OnClick(R.id.login_no)
    void onLoginNoClicked() {
        jumpToHomeScreen();
    }

    private void jumpToHomeScreen() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
