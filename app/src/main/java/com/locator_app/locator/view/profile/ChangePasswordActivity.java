package com.locator_app.locator.view.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.apiservice.errorhandling.HttpError;
import com.locator_app.locator.controller.MyController;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePasswordActivity extends Activity {

    @Bind(R.id.oldPassword)
    EditText oldPassword;

    @Bind(R.id.newPassword1)
    EditText newPassword1;

    @Bind(R.id.newPassword2)
    EditText newPassword2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.changePassword)
    public void onChangePasswordClick() {
        changePassIfValid();
    }

    private void changePassIfValid() {
        String oldPass = oldPassword.getText().toString();
        if (oldPass.length() < 3) {
            Toast.makeText(this, "Geb dein altes Passwort ein", Toast.LENGTH_SHORT).show();
            return;
        }
        String pass1 = newPassword1.getText().toString();
        String pass2 = newPassword2.getText().toString();
        if (!pass1.equals(pass2)) {
            Toast.makeText(this, "Die Passwörter stimmen nicht überein", Toast.LENGTH_SHORT).show();
            return;
        }

        MyController.getInstance().changePassword(oldPass, pass1)
                .subscribe(
                        (result) -> {
                            Toast.makeText(this, "Passwort geändert", Toast.LENGTH_SHORT).show();
                            finish();
                        },
                        (error) -> {
                            if (error instanceof HttpError &&
                                ((HttpError) error).getErrorCode() == HttpError.HttpErrorCode.unauthorized) {
                                Toast.makeText(this, "Das alte Passwort war falsch", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Da war irgend ein Problem, versuchs nochmal besser", Toast.LENGTH_LONG).show();
                            }
                        });
    }
}
