package com.locator_app.locator.view.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.locator_app.locator.R;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.view.login.LoginRegisterStartActivity;

import butterknife.OnClick;

public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @OnClick(R.id.changeImage)
    public void onChangeImageClick() {

    }

    @OnClick(R.id.changePassword)
    public void onChangePasswordClick() {

    }

    @OnClick(R.id.logOut)
    public void onLotOutClick() {
        UserController.getInstance().logout().subscribe(
                (responses) -> {logOut();},
                (err) -> {logOut();}
        );
    }

    private void logOut() {
        Intent intent = new Intent(getApplicationContext(), LoginRegisterStartActivity.class);
        startActivity(intent);
    }
}
