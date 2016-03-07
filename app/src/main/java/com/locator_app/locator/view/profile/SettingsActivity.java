package com.locator_app.locator.view.profile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.R;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.view.login.LoginRegisterStartActivity;
import com.locator_app.locator.view.register.RegisterProfilePictureActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends Activity {
    @Bind(R.id.mapOnSchoenhier)
    Switch mapOnSchoenHier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        SharedPreferences preferences = LocatorApplication.getSharedPreferences();
        mapOnSchoenHier.setChecked(preferences.getBoolean("map_on_sh_click", true));
        mapOnSchoenHier.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("map_on_sh_click", isChecked).apply();
        });
    }

    @OnClick(R.id.changeImage)
    public void onChangeImageClick() {
        Intent intent = new Intent(this, RegisterProfilePictureActivity.class);
        startActivity(intent);

    }

    @OnClick(R.id.changePassword)
    public void onChangePasswordClick() {
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.logOut)
    public void onLotOutClick() {
        UserController.getInstance().logout().subscribe(
                (responses) -> {logOut();},
                (err) -> {logOut();}
        );
    }

    private void logOut() {
        Intent intent = new Intent(this, LoginRegisterStartActivity.class);
        startActivity(intent);
    }
}
