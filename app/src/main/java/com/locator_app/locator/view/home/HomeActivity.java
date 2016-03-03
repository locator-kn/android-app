package com.locator_app.locator.view.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.tbouron.shakedetector.library.ShakeDetector;
import com.locator_app.locator.R;
import com.locator_app.locator.service.LocationCreationController;
import com.locator_app.locator.controller.MyController;
import com.locator_app.locator.controller.SchoenHierController;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.model.User;
import com.locator_app.locator.service.GpsService;
import com.locator_app.locator.view.bubble.BubbleController;
import com.locator_app.locator.view.bubble.BubbleView;
import com.locator_app.locator.view.bubble.RelativeBubbleLayout;
import com.locator_app.locator.view.login.LoginRegisterStartActivity;
import com.locator_app.locator.view.profile.ProfileActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTouch;

public class HomeActivity extends AppCompatActivity {

    LocationCreationController locationCreationController;

    @Bind(R.id.bubbleLayout)
    RelativeBubbleLayout bubbleLayout;

    @Bind(R.id.schoenHierBubble)
    BubbleView schoenHierBubble;

    @Bind(R.id.userProfileBubble)
    BubbleView userProfileBubble;

    @Bind(R.id.welcomeScreen)
    View welcomeScreen;

    @Bind(R.id.ahoiName)
    TextView ahoiName;

    @Bind(R.id.ahoi)
    TextView ahoi;

    @Bind(R.id.welcomeBack)
    TextView welcomeBack;

    @Bind(R.id.locatorLogo)
    ImageView locatorLogo;

    BubbleController bubbleController;

    GpsService gpsService;

    private static boolean isFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        if (isFirstTime) {
            welcomeScreen.setVisibility(View.VISIBLE);

            UserController.getInstance().checkProtected()
                    .subscribe(
                            this::onUserIsLoggedIn,
                            this::handleLoginError
                    );
            isFirstTime = false;
        }

        bubbleController = new BubbleController(bubbleLayout);
        locationCreationController = new LocationCreationController(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        gpsService = new GpsService(this);

        ShakeDetector.create(this, this::loadBubbleScreen);
        loadBubbleScreen();
    }

    private void loadBubbleScreen() {
        MyController.getInstance().getBubbleScreen()
                .subscribe(
                        bubbleController::onBubbleScreenUpdate,
                        (err) -> { }
                );
    }

    @Override
    protected void onStart() {
        super.onStart();
        //loadBubbleScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ShakeDetector.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ShakeDetector.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShakeDetector.destroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        locationCreationController.onActivityResult(requestCode, resultCode, data);
        gpsService.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        locationCreationController.onRequestPermissionsResult(requestCode, permissions, grantResults);
        gpsService.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @OnTouch(R.id.schoenHierBubble)
    boolean onSchoenHierBubbleTouch(MotionEvent arg1) {
        if (arg1.getAction()== MotionEvent.ACTION_DOWN) {
            schoenHierBubble.setAlpha((float) 0.6);
        }
        else if (arg1.getAction()==MotionEvent.ACTION_UP){
            schoenHierBubble.setAlpha((float) 1);
        }
        return false;
    }

    @OnClick(R.id.schoenHierBubble)
    void onSchoenHierBubbleClick() {
        SchoenHierController.getInstance().markCurPosAsSchoenHier(gpsService)
                .subscribe(
                        (response) -> {},
                        (error) -> {}
                );
    }

    @OnLongClick(R.id.schoenHierBubble)
    boolean onSchoenHierBubbleLongClick() {
        locationCreationController.startLocationCreation();
        return true;
    }

    @OnClick(R.id.userProfileBubble)
    void onUserProfileBubbleClick() {
        UserController userController = UserController.getInstance();
        if (userController.loggedIn()) {
            User me = userController.me();

            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("profile", me);
            startActivity(intent);
        } else {
            jumpToLoginRegisterActivity();
        }
    }

    private void handleLoginError(Throwable throwable) {
        jumpToLoginRegisterActivity();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void onUserIsLoggedIn(User user) {
        final int animationTime = 4500;

        ahoiName.setText(String.format("%s!", user.name));

        final Animation fadeInAnimation = new AlphaAnimation(0.4f, 1.0f);
        final Animation moveLeftAnimation = new TranslateAnimation(0, -200, 0, 0);
        final Animation moveRightAnimation = new TranslateAnimation(0, -80, 0, 0);

        moveRightAnimation.setDuration(animationTime);
        moveLeftAnimation.setDuration(animationTime);
        fadeInAnimation.setDuration(animationTime);

        ahoi.startAnimation(fadeInAnimation);
        ahoiName.startAnimation(fadeInAnimation);
        welcomeBack.startAnimation(moveRightAnimation);
        locatorLogo.startAnimation(moveLeftAnimation);

        new Handler().postDelayed(() -> {
            Animation animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout);
            welcomeScreen.startAnimation(animationFadeOut);
            welcomeScreen.setVisibility(View.INVISIBLE);
        }, 2000);
    }

    private void jumpToLoginRegisterActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginRegisterStartActivity.class);
        startActivity(intent);
    }

}
