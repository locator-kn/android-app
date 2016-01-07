package com.locator_app.locator.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.controller.MyController;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.service.my.BubbleScreenResponse;
import com.locator_app.locator.service.users.LogoutResponse;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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
        Toast.makeText(getApplicationContext(), "schoenhier", Toast.LENGTH_SHORT).show();
    }

    @OnLongClick(R.id.schoenHierBubble)
    boolean onSchoenHierBubbleLongClick() {
        updateDashboard();
        return true;
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
        Toast.makeText(getApplicationContext(), logoutResponse.message, Toast.LENGTH_SHORT).show();
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
