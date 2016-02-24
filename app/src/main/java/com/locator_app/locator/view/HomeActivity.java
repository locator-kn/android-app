package com.locator_app.locator.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.Toast;


import com.locator_app.locator.R;
import com.locator_app.locator.controller.MyController;
import com.locator_app.locator.controller.SchoenHierController;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.model.User;
import com.locator_app.locator.util.GpsService;
import com.locator_app.locator.view.bubble.BubbleController;
import com.locator_app.locator.view.bubble.BubbleView;
import com.locator_app.locator.view.bubble.RelativeBubbleLayout;
import com.locator_app.locator.view.map.MapsActivity;
import com.locator_app.locator.view.profile.ProfileActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTouch;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeActivity extends AppCompatActivity {

    @Bind(R.id.bubbleLayout)
    RelativeBubbleLayout bubbleLayout;

    @Bind(R.id.schoenHierBubble)
    BubbleView schoenHierBubble;

    @Bind(R.id.userProfileBubble)
    BubbleView userProfileBubble;

    BubbleController bubbleController;

    GpsService gpsService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        bubbleController = new BubbleController(bubbleLayout);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        gpsService = (GpsService) getSupportFragmentManager()
                .findFragmentById(R.id.gpsService);
    }


    @OnClick(R.id.showMap)
    void onShowMapClicked() {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);
    }

    @OnTouch(R.id.schoenHierBubble)
    boolean onSchoenHierBubbleTouch(MotionEvent arg1) {
        if (arg1.getAction()== MotionEvent.ACTION_DOWN) {
            schoenHierBubble.setAlpha((float) 0.8);
        }
        else if (arg1.getAction()==MotionEvent.ACTION_UP){
            schoenHierBubble.setAlpha((float) 1);
        }
        return false;
    }

    @OnClick(R.id.schoenHierBubble)
    void onSchoenHierBubbleClick() {
        SchoenHierController.getInstance().markCurPosAsSchoenHier(gpsService);
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);
    }

    @OnLongClick(R.id.schoenHierBubble)
    boolean onSchoenHierBubbleLongClick() {
        return true;
    }

    @OnClick(R.id.userProfileBubble)
    void onUserProfileBubbleClick() {
        UserController controller = UserController.getInstance();
        controller.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (logoutResponse) -> jumpToLoginScreen(),
                        (error) -> jumpToLoginScreen()
                );
    }

    @OnLongClick(R.id.userProfileBubble)
    boolean onUserProfileBubbleLongClick() {
        UserController.getInstance().getUser("569e4a83a6e5bb503b838301")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::showUserProfile,
                        (error) -> {
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                );
        /*User me = UserController.getInstance().me();
        if (me.loggedIn) {
            showUserProfile(me);
        } else {
            jumpToLoginScreen();
        }*/
        return true;
    }

    private void showUserProfile(User user) {
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        intent.putExtra("profile", user);
        startActivity(intent);
    }

    private void jumpToLoginScreen() {
        Intent intent = new Intent(getApplicationContext(), LoginRegisterStartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        bubbleController.initUserProfileBubble();
        bubbleController.initSchoenHierBubble();
        updateDashboard();
    }

    private void updateDashboard() {
        MyController controller = MyController.getInstance();
        controller.getBubbleScreen()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (response) -> bubbleController.onBubbleScreenUpdate(response),
                        (error) -> handleBubbleScreenError(error)
                );
    }

    private void handleBubbleScreenError(Throwable error) {
        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
