package com.locator_app.locator.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.service.users.LogoutResponse;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeActivity extends AppCompatActivity {

    @Bind(R.id.mainLayout)
    RelativeLayout mainLayout;

    @Bind(R.id.schoenHierBubble)
    BubbleView schoenHierBubble;

    @Bind(R.id.userProfileBubble)
    BubbleView userProfileBubble;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.userProfileBubble)
    void onUserProfileClick() {
        UserController controller = UserController.getInstance();
        controller.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (logoutResponse) -> onLogout(logoutResponse),
                        (error) -> onLogoutError(error)
                );
    }

    private void onLogout(LogoutResponse logoutResponse) {
        Intent intent = new Intent(getApplicationContext(), LoginRegisterStartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void onLogoutError(Throwable t) {
        Toast.makeText(getApplicationContext(), "could not log out", Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.schoenHierBubble)
    void onSchoenHierBubbleClick() {
        Toast.makeText(getApplicationContext(), "scheonhier", Toast.LENGTH_SHORT).show();
    }

    private void initUserProfileBubble() {
        userProfileBubble.loadImage("drawable://" + R.drawable.profile);
        setBubbleRadius(userProfileBubble, 75);
        setBubbleCenter(userProfileBubble, 0.5, 0.87);
    }

    private void initSchoenHierBubble() {
        schoenHierBubble.loadImage("drawable://" + R.drawable.schoenhier);
        setBubbleRadius(schoenHierBubble, 140);
        setBubbleCenter(schoenHierBubble, 0.62, 0.38);
    }

    void setBubbleRadius(BubbleView bubble, int radius) {
        Point center = getBubbleCenter(bubble);
        updateBubble(bubble, radius, center.x, center.y);
    }

    void setBubbleCenter(BubbleView bubble, double percentScreenWidth, double percentScreenHeight) {
        int x = (int) (mainLayout.getWidth() * percentScreenWidth);
        int y = (int) (mainLayout.getHeight() * percentScreenHeight);
        setBubbleCenter(bubble, x, y);
    }

    void setBubbleCenter(BubbleView bubble, int centerX, int centerY) {
        int radius = getBubbleRadius(bubble);
        updateBubble(bubble, radius, centerX, centerY);
    }

    void updateBubble(BubbleView bubble, int radius, int centerX, int centerY) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) bubble.getLayoutParams();
        params.width = radius * 2;
        params.height = radius * 2;
        params.leftMargin = centerX - radius;
        params.topMargin = centerY - radius;
        bubble.setLayoutParams(params);
    }

    int getBubbleRadius(BubbleView bubble) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) bubble.getLayoutParams();
        int width = params.width;
        int height = params.height;
        int radius = Math.min(width, height) / 2;
        return radius;
    }

    Point getBubbleCenter(BubbleView bubble) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) bubble.getLayoutParams();
        int radius = getBubbleRadius(bubble);
        int centerX = params.leftMargin + radius;
        int centerY = params.topMargin + radius;
        Point center = new Point(centerX, centerY);
        return center;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        initUserProfileBubble();
        initSchoenHierBubble();
    }
}
