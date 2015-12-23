package com.locator_app.locator.view;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.locator_app.locator.R;
import com.locator_app.locator.controller.LoginController;
import com.locator_app.locator.service.LoginService;
import com.locator_app.locator.service.ServiceFactory;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginPasswordActivity extends AppCompatActivity {

    @Bind(R.id.loginPassword)
    EditText loginPassword;
    private LoginController loginController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_password);
        ButterKnife.bind(this);

        setCustomActionBar();

        loginController = LoginController.getInstance();

        loginPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    loginController.setPassword(loginPassword.getText().toString());
                    loginController.login();
                    LoginService service = ServiceFactory.createService(LoginService.class);
                    service.login(loginController.getMail(), loginController.getPassword());
                    //TODO: Start activity
                    //Intent intent = new Intent(v.getContext(), LoginRegisterActivity.class);
                    //startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }

    private void setCustomActionBar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom);
        View v = getSupportActionBar().getCustomView();
        TextView titleTxtView = (TextView) v.findViewById(R.id.actionbar_title);
        titleTxtView.setText(R.string.login);
    }


}
