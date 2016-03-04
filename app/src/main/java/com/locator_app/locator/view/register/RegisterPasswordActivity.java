package com.locator_app.locator.view.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.service.LocationCreationController;
import com.locator_app.locator.view.locationcreation.ChooseCategories;
import com.locator_app.locator.view.login.LoginCustomActionBar;
import com.locator_app.locator.view.login.LoginRegisterStartActivity;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterPasswordActivity extends AppCompatActivity {

    @Bind(R.id.registerPassword)
    EditText registerPassword;

    HashMap<String, String> registerValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_password);
        ButterKnife.bind(this);
        setCustomActionBar();

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

    private void setCustomActionBar() {
        LoginCustomActionBar customActionBar = new LoginCustomActionBar(getSupportActionBar(), this);
        customActionBar.setTitle(getResources().getString(R.string.register));
        customActionBar.setCrossButtonJumpScreen(LoginRegisterStartActivity.class);
        customActionBar.setColor(R.color.colorRegister);
    }

    @Override
    public void onBackPressed() {
    }

    private void confirmInput() {
        if (registerPassword.getText().length() >= 3 &&
                registerPassword.getText().length() <= 30) {

            registerValues.put("password", registerPassword.getText().toString());
            Intent intent = new Intent(this, RegisterProfilePictureActivity.class);
            intent.putExtra("registerValues", registerValues);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Der Name sollte zwischen 3 und 30 Zeichen lang sein",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
