package com.locator_app.locator.view.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.apiservice.errorhandling.HttpError;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.apiservice.users.LoginRequest;
import com.locator_app.locator.view.UiError;
import com.locator_app.locator.view.home.HomeActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginPasswordActivity extends AppCompatActivity {

    @Bind(R.id.loginPassword)
    TextView loginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_password);
        ButterKnife.bind(this);
        setCustomActionBar();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        loginPassword.setOnKeyListener((v1, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                String mail = getIntent().getStringExtra("mail");
                String password = loginPassword.getText().toString();
                login(mail, password);
                return true;
            }
            return false;
        });
    }

    private void setCustomActionBar() {
        LoginCustomActionBar customActionBar = new LoginCustomActionBar(getSupportActionBar(), this);
        customActionBar.setTitle(getResources().getString(R.string.login));
        customActionBar.setCrossButtonJumpScreen(LoginRegisterStartActivity.class);
    }

    void login(String mail, String password) {
        final Context context = getApplicationContext();

        UserController userController = UserController.getInstance();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.mail = mail;
        loginRequest.password = password;
        userController.login(loginRequest)
                .subscribe(
                        (loginResponse) -> {
                            Toast.makeText(context, "Hi " + loginResponse.name, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        },
                        (error) -> {
                            if (error instanceof HttpError &&
                                ((HttpError) error).getErrorCode() == HttpError.HttpErrorCode.unauthorized) {
                                UiError.showError(this, error, "E-Mail oder Passwort falsch");
                                LoginPasswordActivity.this.finish();
                            }
                            UiError.showError(this, error, "Da ist was komisches passiert");
                        });
    }
}
