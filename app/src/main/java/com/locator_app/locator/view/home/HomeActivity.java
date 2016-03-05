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
import com.locator_app.locator.controller.DeviceController;
import com.locator_app.locator.view.UiError;
import com.locator_app.locator.view.locationcreation.LocationCreationController;
import com.locator_app.locator.controller.MyController;
import com.locator_app.locator.controller.SchoenHierController;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.model.User;
import com.locator_app.locator.service.GpsService;
import com.locator_app.locator.view.OnSwipeTouchListener;
import com.locator_app.locator.view.bubble.BubbleController;
import com.locator_app.locator.view.bubble.BubbleView;
import com.locator_app.locator.view.bubble.RelativeBubbleLayout;
import com.locator_app.locator.view.login.LoginRegisterStartActivity;
import com.locator_app.locator.view.map.MapsActivity;
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
    ImageView welcomeScreen;

    @Bind(R.id.bubbleScreen)
    View bubbleScreen;

    BubbleController bubbleController;

    GpsService gpsService;

    private static boolean isFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        if (isFirstTime) {
            DeviceController.getInstance().registerDevice(this);

            welcomeScreen.setVisibility(View.VISIBLE);

            UserController.getInstance().checkProtected()
                    .subscribe(
                            this::onUserIsLoggedIn,
                            this::handleLoginError
                    );
            isFirstTime = false;
        }

        bubbleScreen.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeLeft() {
                Intent intent = new Intent(HomeActivity.this, MapsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });

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
        gpsService.getCurLocation()
                .subscribe(
                        (position) -> {
                            loadBubbleScreenAt(position.getLongitude(), position.getLatitude());
                        },
                        (error) -> {
                        }
                );
    }

    private void loadBubbleScreenAt(double lon, double lat) {
        MyController.getInstance().getBubbleScreen(lon, lat)
                .subscribe(
                        bubbleController::onBubbleScreenUpdate,
                        (err) -> {
                            UiError.showError(this, err, "Der Bubblescreen konnte nicht geladen werden :(");
                        }
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
                        (response) -> {
                        },
                        (error) -> {
                            UiError.showError(this, error);
                        }
                );
    }

    @OnLongClick(R.id.schoenHierBubble)
    boolean onSchoenHierBubbleLongClick() {
        if (UserController.getInstance().loggedIn()) {
            locationCreationController.startLocationCreation();
        } else {
            Toast.makeText(this, "Melde dich an", Toast.LENGTH_SHORT).show();
        }
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
//        Animation animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout);
//        welcomeScreen.startAnimation(animationFadeOut);
        new Handler().postDelayed(
                () -> welcomeScreen.setVisibility(View.INVISIBLE),
                1000);
    }

    private void jumpToLoginRegisterActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginRegisterStartActivity.class);
        startActivity(intent);
    }

}
