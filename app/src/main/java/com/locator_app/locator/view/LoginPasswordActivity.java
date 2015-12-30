package com.locator_app.locator.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.controller.LoginController;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.service.LoginRequest;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginPasswordActivity extends AppCompatActivity {

    LoginController loginController;

    @Bind(R.id.loginPassword)
    TextView loginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_password);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom);
        ButterKnife.bind(this);

        //set action bar title
        View v = getSupportActionBar().getCustomView();
        TextView titleTxtView = (TextView) v.findViewById(R.id.actionbar_title);
        titleTxtView.setText(R.string.login);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        loginController = LoginController.getInstance();

        loginPassword.setOnKeyListener((v1, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                loginController.setPassword(loginPassword.getText().toString());
                login();
                return true;
            }
            return false;
        });
    }

    void login() {
        final Context context = getApplicationContext();

        UserController userController = UserController.getInstance();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.mail = loginController.getMail();
        loginRequest.password = loginController.getPassword();
        userController.login(loginRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (loginResponse) -> {
                            Toast.makeText(context, "Hi " + loginResponse.name, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        },
                        (error) -> {
                            Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                            LoginPasswordActivity.this.finish();
                        });
    }
}
