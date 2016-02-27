package com.locator_app.locator.view.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.locator_app.locator.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FacebookLoginMailActivity extends AppCompatActivity {

    @Bind(R.id.facebookLoginMail)
    EditText facebookLoginMail;
    @Bind(R.id.facebookLogo)
    ImageView facebookLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login_mail);
        ButterKnife.bind(this);
        setCustomActionBar();
        loadImages();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        facebookLoginMail.setOnKeyListener((v1, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                String mail = facebookLoginMail.getText().toString();
                if (!isValidEmail(mail)) {
                    Toast.makeText(getApplicationContext(),
                            "E-Mail enthält unzulässige Zeichen mein Freund :-)",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(v1.getContext(), FacebookLoginPasswordActivity.class);
                    intent.putExtra("mail", mail);
                    startActivity(intent);
                    return true;
                }
            }
            return false;
        });
    }

    private void setCustomActionBar() {
        LoginCustomActionBar customActionBar = new LoginCustomActionBar(getSupportActionBar(), this);
        customActionBar.setTitle(getResources().getString(R.string.login));
        customActionBar.setCrossButtonJumpScreen(LoginRegisterStartActivity.class);
        customActionBar.setColor(R.color.colorFacebook);
    }

    private void loadImages() {
        Glide.with(this).load(R.drawable.facebook_logo).into(facebookLogo);
    }

    public boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}

