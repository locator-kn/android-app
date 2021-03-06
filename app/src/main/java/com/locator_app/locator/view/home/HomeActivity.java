package com.locator_app.locator.view.home;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.tbouron.shakedetector.library.ShakeDetector;
import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.R;
import com.locator_app.locator.controller.DeviceController;
import com.locator_app.locator.controller.MyController;
import com.locator_app.locator.controller.SchoenHierController;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.model.User;
import com.locator_app.locator.service.GpsService;
import com.locator_app.locator.util.AppTracker;
import com.locator_app.locator.util.Debounce;
import com.locator_app.locator.view.OnSwipeTouchListener;
import com.locator_app.locator.view.StrokeTransformation;
import com.locator_app.locator.view.UiError;
import com.locator_app.locator.view.bubble.BubbleController;
import com.locator_app.locator.view.bubble.RelativeBubbleLayout;
import com.locator_app.locator.view.locationcreation.LocationCreationController;
import com.locator_app.locator.view.login.LoginRegisterStartActivity;
import com.locator_app.locator.view.map.MapsActivity;
import com.locator_app.locator.view.profile.ProfileActivity;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTouch;

public class HomeActivity extends Activity {

    LocationCreationController locationCreationController;

    @Bind(R.id.bubbleLayout)
    RelativeBubbleLayout bubbleLayout;

    @Bind(R.id.schoenHierBubble)
    ImageView schoenHierBubble;

    @Bind(R.id.userProfileBubble)
    ImageView userProfileBubble;

    @Bind(R.id.welcomeScreen)
    ImageView welcomeScreen;

    @Bind(R.id.bubbleScreen)
    View bubbleScreen;

    @Bind(R.id.glow)
    ImageView glow;

    BubbleController bubbleController;

    GpsService gpsService;

    Debounce shakeDebounce = new Debounce(2000);
    Debounce schoenHierDebounce = new Debounce(1500);

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
                    .doOnNext((val) -> AppTracker.getInstance().track("App | launch as user"))
                    .doOnError((val) -> AppTracker.getInstance().track("App | launch as guest"))
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

        gpsService = new GpsService(this);

        ShakeDetector.create(this, this::onShake);
        loadBubbleScreen();
    }

    private void onShake() {
        if (!shakeDebounce.calledRecently()) {
            AppTracker.getInstance().track("Bubble | shake");
            loadBubbleScreen();
        }
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        schoenHierBubble.setAlpha((float) 1);
        ShakeDetector.start();
        if (UserController.getInstance().loggedIn()) {
            refreshUserProfilePicture(UserController.getInstance().me());
        }
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

    YoYo.YoYoString glowAnimation;
    @OnTouch(R.id.schoenHierBubble)
    boolean onSchoenHierBubbleTouch(MotionEvent arg1) {
        if (arg1.getAction()== MotionEvent.ACTION_DOWN) {
            schoenHierBubble.setAlpha((float) 0.6);

            if (glowAnimation != null &&
                glowAnimation.isRunning()) {
                glowAnimation.stop(false);
            }
            glowAnimation = YoYo.with(Techniques.FadeIn)
                    .duration(100)
                    .withListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            glowAnimation = YoYo.with(Techniques.TakingOff)
                                    .duration(1000)
                                    .playOn(glow);
                        }
                    })
                    .playOn(glow);
        }
        else if (arg1.getAction()==MotionEvent.ACTION_UP){
            schoenHierBubble.setAlpha((float) 1);
            if (glowAnimation != null &&
                    glowAnimation.isRunning()) {
                glowAnimation.stop(false);
                glow.setAlpha(0.f);
            }
        }
        return false;
    }

    @OnClick(R.id.schoenHierBubble)
    void onSchoenHierBubbleClick() {
        if (!schoenHierDebounce.calledRecently()) {
            SchoenHierController.getInstance().markCurPosAsSchoenHier(gpsService)
                    .subscribe(
                            (response) -> {
                                SharedPreferences preferences = LocatorApplication.getSharedPreferences();
                                if (preferences.getBoolean("map_on_sh_click", true)) {
                                    Intent intent = new Intent(HomeActivity.this, MapsActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
                                }
                            },
                            (error) -> {
                                UiError.showError(this, error);
                            }
                    );
        }
    }

    @OnLongClick(R.id.schoenHierBubble)
    boolean onSchoenHierBubbleLongClick() {
        locationCreationController.startLocationCreation();
        return true;
    }

    Debounce profileBubbleDebounce = new Debounce(1500);

    @OnClick(R.id.userProfileBubble)
    void onUserProfileBubbleClick() {
        if (!profileBubbleDebounce.calledRecently()) {
            if (UserController.getInstance().loggedIn()) {
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("profile", UserController.getInstance().me());
                startActivity(intent);
            } else {
                jumpToLoginRegisterActivity();
            }
        }
    }

    @OnClick(R.id.imageViewShowHeatmap)
    void onImageViewShowHeatmapClicked() {
        Intent intent = new Intent(HomeActivity.this, MapsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    private void handleLoginError(Throwable throwable) {
        jumpToLoginRegisterActivity();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void onUserIsLoggedIn(User user) {
        refreshUserProfilePicture(user);
        new Handler().postDelayed(
                () -> welcomeScreen.setVisibility(View.INVISIBLE),
                1000);
    }

    private void jumpToLoginRegisterActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginRegisterStartActivity.class);
        startActivity(intent);
    }

    private void refreshUserProfilePicture(User user) {
        ImageView userProfileBubble = (ImageView) findViewById(R.id.userProfileBubble);
        Glide.with(this).load(user.getProfilePictureNormalSize())
                .error(R.drawable.profile)
                .bitmapTransform(new StrokeTransformation(this, 10, Color.WHITE))
                .dontAnimate()
                .into(userProfileBubble);
    }
}
