package com.locator_app.locator.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.model.SchoenHier;
import com.locator_app.locator.service.LoginResponse;
import com.locator_app.locator.service.LogoutResponse;
import com.locator_app.locator.service.RegistrationRequest;
import com.locator_app.locator.service.SchoenHierRequestManager;
import com.locator_app.locator.service.UsersRequestManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeActivity extends AppCompatActivity {

    @Bind(R.id.button)
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button)
    public void logout() {

        UsersRequestManager requestManager = new UsersRequestManager();
        requestManager.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (val) -> {
                            Toast.makeText(getApplicationContext(), "logged out", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), LoginRegisterStartActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        },
                        (err) -> {
                            Toast.makeText(getApplicationContext(), "could not log out, sorry!", Toast.LENGTH_LONG).show();
                        }
                );
    }
}
