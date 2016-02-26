package com.locator_app.locator.view.locationcreation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.view.HomeActivity;
import com.locator_app.locator.view.LoginCustomActionBar;
import com.locator_app.locator.view.LoginRegisterStartActivity;
import com.locator_app.locator.view.RegisterResidenceActivity;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NameLocation extends Activity {
    private static final int MIN_NAME_LENGTH = 3;

    @Bind(R.id.locationName)
    EditText locationNameEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_location);
        ButterKnife.bind(this);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        locationNameEdit.setOnKeyListener((v1, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                return confirmInput();
            }
            return false;
        });

    }

    @OnClick(R.id.next)
    void onNextClick() {
        confirmInput();
    }

    private boolean confirmInput() {
        if (checkUsernameLength()) {
            Toast.makeText(getApplicationContext(),
                    "ok",
                    Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(getApplicationContext(),
                    "Dein Name sollte aus mindestens " + MIN_NAME_LENGTH + " Zeichen bestehen!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @OnClick(R.id.cancelButton)
    void onCancelButtonClicked() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private boolean checkUsernameLength() {
        return locationNameEdit.getText().length() >= MIN_NAME_LENGTH;
    }
}
