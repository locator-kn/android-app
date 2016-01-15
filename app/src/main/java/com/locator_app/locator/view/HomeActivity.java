package com.locator_app.locator.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


import com.locator_app.locator.LocatorApplication;
import com.google.android.gms.maps.model.LatLng;
import com.locator_app.locator.R;
import com.locator_app.locator.apiservice.schoenhier.SchoenHierRequest;
import com.locator_app.locator.controller.MyController;
import com.locator_app.locator.controller.SchoenHierController;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.apiservice.users.LogoutResponse;
import com.locator_app.locator.model.User;
import com.locator_app.locator.util.GpsService;
import com.locator_app.locator.view.bubble.BubbleController;
import com.locator_app.locator.view.bubble.BubbleView;
import com.locator_app.locator.view.bubble.RelativeBubbleLayout;
import com.locator_app.locator.view.profile.ProfileActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        bubbleController = new BubbleController(bubbleLayout);
    }

    @OnClick(R.id.schoenHierBubble)
    void onSchoenHierBubbleClick() {
        markAsSchoenHier();

        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);
    }

    void markAsSchoenHier() {
        GpsService gpsService = GpsService.getInstance();
        if (!gpsService.isGpsEnabled()) {
            Toast.makeText(getApplicationContext(), "Gps is not Enabled", Toast.LENGTH_LONG).show();
            return;
        }

        android.location.Location location = gpsService.getGpsLocation();
        if (location == null) {
            return;
        }

        SchoenHierRequest request = new SchoenHierRequest();
        request.lon = location.getLongitude();
        request.lat = location.getLatitude();

        SchoenHierController.getInstance().markAsSchoenHier(request);
        Toast.makeText(getApplicationContext(), "geschönhiert", Toast.LENGTH_LONG).show();
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
                        (error) -> onLogoutError(error)
                );
    }

    @OnLongClick(R.id.userProfileBubble)
    boolean onUserProfileBubbleLongClick() {
        UserController.getInstance().getUser("56786fe35227864133663973")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (user) -> showUserProfile(user),
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
        user.thumb = "/api/v1/users/ec26fc9e9342d7df21a87ab2477d5cf7/profile.jpeg";
        intent.putExtra("profile", user);
        startActivity(intent);
    }

    private void jumpToLoginScreen() {
        Intent intent = new Intent(getApplicationContext(), LoginRegisterStartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void onLogoutError(Throwable t) {
        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
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
