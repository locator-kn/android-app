package com.locator_app.locator.view.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.controller.LocationCreationController;
import com.locator_app.locator.controller.MyController;
import com.locator_app.locator.controller.SchoenHierController;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.model.User;
import com.locator_app.locator.service.GpsService;
import com.locator_app.locator.view.ImageActivity;
import com.locator_app.locator.view.LoadingSpinner;
import com.locator_app.locator.view.bubble.BubbleController;
import com.locator_app.locator.view.bubble.BubbleView;
import com.locator_app.locator.view.bubble.RelativeBubbleLayout;
import com.locator_app.locator.view.login.LoginRegisterStartActivity;
import com.locator_app.locator.view.map.MapsActivity;
import com.locator_app.locator.view.profile.ProfileActivity;

import java.util.List;
import java.util.Vector;

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

    BubbleController bubbleController;

    GpsService gpsService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        bubbleController = new BubbleController(bubbleLayout);
        locationCreationController = new LocationCreationController(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        gpsService = new GpsService(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        locationCreationController.onActivityResult(requestCode, resultCode, data);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        bubbleController.initUserProfileBubble();
        bubbleController.initSchoenHierBubble();
        updateDashboard();
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
        SchoenHierController.getInstance().markCurPosAsSchoenHier(gpsService);
    }

    @OnLongClick(R.id.schoenHierBubble)
    boolean onSchoenHierBubbleLongClick() {
        locationCreationController.createLocation();;
        return true;
    }

    @OnClick(R.id.userProfileBubble)
    void onUserProfileBubbleClick() {
        UserController controller = UserController.getInstance();
        controller.logout()
                .subscribe(
                        (logoutResponse) -> jumpToLoginScreen(),
                        (error) -> jumpToLoginScreen()
                );
    }

    @OnLongClick(R.id.userProfileBubble)
    boolean onUserProfileBubbleLongClick() {
        UserController.getInstance().getUser("569e4a83a6e5bb503b838301")
                .subscribe(
                        this::showUserProfile,
                        (error) -> {
                            Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("profile", user);
        startActivity(intent);
    }

    private void jumpToLoginScreen() {
        Intent intent = new Intent(this, LoginRegisterStartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void updateDashboard() {
        MyController controller = MyController.getInstance();
        controller.getBubbleScreen()
                .subscribe(
                        bubbleController::onBubbleScreenUpdate,
                        this::handleBubbleScreenError
                );
    }

    private void handleBubbleScreenError(Throwable error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
