package com.locator_app.locator.view.home;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
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
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeFragment extends Fragment {

    @Bind(R.id.bubbleLayout)
    RelativeBubbleLayout bubbleLayout;

    @Bind(R.id.schoenHierBubble)
    BubbleView schoenHierBubble;

    @Bind(R.id.userProfileBubble)
    BubbleView userProfileBubble;

    BubbleController bubbleController;

    GpsService gpsService;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        bubbleController = new BubbleController(bubbleLayout);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gpsService = new GpsService(getActivity());
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
        LocationCreationController.createLocation(getActivity());
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
                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        intent.putExtra("profile", user);
        startActivity(intent);
    }

    private void jumpToLoginScreen() {
        Intent intent = new Intent(getActivity(), LoginRegisterStartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        if (bubbleController != null) {
            bubbleController.initUserProfileBubble();
            bubbleController.initSchoenHierBubble();
            updateDashboard();
        }
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
        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
