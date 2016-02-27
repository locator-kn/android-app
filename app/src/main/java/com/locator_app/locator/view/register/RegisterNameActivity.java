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
import com.locator_app.locator.view.login.LoginCustomActionBar;
import com.locator_app.locator.view.login.LoginRegisterStartActivity;

import java.util.HashMap;
import butterknife.Bind;
import butterknife.ButterKnife;

public class RegisterNameActivity extends AppCompatActivity {

    private static final int MIN_NAME_LENGTH = 3;

    @Bind(R.id.registerName)
    EditText registerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_name);
        ButterKnife.bind(this);
        setCustomActionBar();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        registerName.setOnKeyListener((v1, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                if (checkUsernameLength()) {
                    HashMap<String, String> registerValues = new HashMap<>();
                    registerValues.put("name", registerName.getText().toString());
                    Intent intent = new Intent(v1.getContext(), RegisterResidenceActivity.class);
                    intent.putExtra("registerValues", registerValues);
                    startActivity(intent);
                    return true;
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Dein Name sollte aus mindestens " + MIN_NAME_LENGTH + " Zeichen bestehen!",
                            Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        });
    }

    private void setCustomActionBar() {
        LoginCustomActionBar customActionBar = new LoginCustomActionBar(getSupportActionBar(), this);
        customActionBar.setTitle(getResources().getString(R.string.whats_your_name));
        customActionBar.setCrossButtonJumpScreen(LoginRegisterStartActivity.class);
        customActionBar.setColor(R.color.colorRegister);
    }

    private boolean checkUsernameLength() {
        if (registerName.getText().length() < MIN_NAME_LENGTH) {
            return false;
        }

        return true;
    }
}
