
package com.locator_app.locator.view.register;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.view.LocatorHeader;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RegisterMailActivity extends Activity {

    @Bind(R.id.registerMail)
    EditText registerMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_mail);
        ButterKnife.bind(this);

        LocatorHeader header = new LocatorHeader(this);
        header.setTitle(R.string.whats_your_email);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        registerMail.setOnKeyListener((v1, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                String mail = registerMail.getText().toString();
                if (!isValidEmail(mail)) {
                    Toast.makeText(getApplicationContext(),
                            "E-Mail nicht vollständig oder fehlerhaft!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    HashMap<String, String> registerValues =
                            (HashMap<String, String>) getIntent().getSerializableExtra("registerValues");
                    registerValues.put("mail", mail);
                    Intent intent = new Intent(v1.getContext(), RegisterPasswordActivity.class);
                    intent.putExtra("registerValues", registerValues);
                    startActivity(intent);
                    return true;
                }
            }
            return false;
        });
    }

    public boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
