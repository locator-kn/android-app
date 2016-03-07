package com.locator_app.locator.view.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.view.LocatorHeader;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginMailActivity extends Activity {

    @Bind(R.id.loginMail)
    EditText loginMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_mail);
        ButterKnife.bind(this);

        LocatorHeader header = new LocatorHeader(this);
        header.setTitle(R.string.login);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        loginMail.setOnKeyListener((v1, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                String mail = loginMail.getText().toString();
                if (!isValidEmail(mail)) {
                    Toast.makeText(getApplicationContext(),
                            "E-Mail enthält unzulässige Zeichen mein Freund :-)",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(v1.getContext(), LoginPasswordActivity.class);
                    intent.putExtra("mail", mail);
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
