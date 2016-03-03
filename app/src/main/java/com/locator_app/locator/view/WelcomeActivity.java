package com.locator_app.locator.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.locator_app.locator.R;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.model.User;
import com.locator_app.locator.view.home.HomeActivity;
import com.locator_app.locator.view.login.LoginRegisterStartActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WelcomeActivity extends Activity {

    @Bind(R.id.ahoiName)
    TextView ahoiName;

    @Bind(R.id.welcomeBack)
    TextView welcomeBack;

    @Bind(R.id.locatorLogo)
    ImageView locatorLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        UserController.getInstance().checkProtected()
                .subscribe(
                        this::onUserIsLoggedIn,
                        this::handleLoginError
                );
    }

    private void handleLoginError(Throwable throwable) {
        jumpToLoginRegisterActivity();
    }

    private void onUserIsLoggedIn(User user) {

        final int animationTime = 4500;

        ahoiName.setText(String.format("Ahoi %s!", user.name));

        final Animation fadeInAnimation = new AlphaAnimation(0.2f, 1.0f);
        final Animation moveLeftAnimation = new TranslateAnimation(100, -100, 0, 0);
        final Animation moveRightAnimation = new TranslateAnimation(80, 0, 0, 0);

        moveRightAnimation.setDuration(animationTime);
        moveLeftAnimation.setDuration(animationTime);
        fadeInAnimation.setDuration(animationTime);

        ahoiName.startAnimation(fadeInAnimation);
        welcomeBack.startAnimation(moveRightAnimation);
        locatorLogo.startAnimation(moveLeftAnimation);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }, 3000);
    }

    private void jumpToLoginRegisterActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginRegisterStartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
