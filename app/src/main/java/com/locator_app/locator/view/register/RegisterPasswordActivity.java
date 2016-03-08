package com.locator_app.locator.view.register;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.apiservice.errorhandling.HttpError;
import com.locator_app.locator.apiservice.users.RegistrationRequest;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.view.LocatorHeader;
import com.locator_app.locator.view.UiError;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RegisterPasswordActivity extends Activity {

    @Bind(R.id.registerPassword)
    EditText registerPassword;

    HashMap<String, String> registerValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_password);
        ButterKnife.bind(this);

        LocatorHeader header = new LocatorHeader(this);
        header.setTitle(R.string.choose_password);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        registerValues =
                (HashMap<String, String>) getIntent().getSerializableExtra("registerValues");

        registerPassword.setOnKeyListener((v1, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                confirmInput();
                return true;
            }
            return false;
        });
    }

    private void confirmInput() {
        if (registerPassword.getText().length() >= 3 &&
                registerPassword.getText().length() <= 30) {

            HashMap<String, String> registerValues =
                (HashMap<String, String>)getIntent().getSerializableExtra("registerValues");
            registerValues.put("password", registerPassword.getText().toString());
            register(registerValues);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Der Name sollte zwischen 3 und 30 Zeichen lang sein",
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void register(HashMap<String, String> registerValues) {
        final Context context = getApplicationContext();
        UserController controller = UserController.getInstance();
        RegistrationRequest request = new RegistrationRequest();
        request.mail = registerValues.get("mail");
        request.name = registerValues.get("name");
        request.password = registerValues.get("password");
        request.residence = registerValues.get("residence");
        controller.register(request)
                .subscribe(
                        (loginResponse) -> {
                            Intent intent = new Intent(this, RegisterProfilePictureActivity.class);
                            startActivity(intent);
                        },
                        (error) -> {
                            if (error instanceof HttpError &&
                                ((HttpError) error).getErrorCode() == HttpError.HttpErrorCode.conflict) {
                                UiError.showError(this, error, "Diese E-Mail ist bereits registriert");
                            } else {
                                UiError.showError(this, error, "Da ist was schiefgelaufen, versuchs nochmal");
                            }
                        }
                );
    }
}
