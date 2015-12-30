package com.locator_app.locator.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.service.users.RegistrationRequest;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

        UserController controller = UserController.getInstance();
        controller.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (logoutResponse) -> {
                            Toast.makeText(getApplicationContext(), logoutResponse.message,
                                    Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(),
                                    LoginRegisterStartActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        },
                        (err) -> {
                            Toast.makeText(getApplicationContext(), "could not log out, sorry!", Toast.LENGTH_LONG).show();
                        }
                );
    }
}
