package com.locator_app.locator.view.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.controller.LocationController;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.model.User;
import com.locator_app.locator.view.HomeActivity;
import com.locator_app.locator.view.map.MapsActivity;
import com.locator_app.locator.view.bubble.BubbleView;
import com.locator_app.locator.view.fragments.FragmentAdapter;
import com.locator_app.locator.view.fragments.LocationsFragment;
import com.locator_app.locator.view.fragments.UsersFragment;

import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ProfileActivity extends FragmentActivity {


    @Bind(R.id.residence)
    TextView residence;

    @Bind(R.id.userName)
    TextView userName;

    @Bind(R.id.profileImageBubbleView)
    BubbleView profileImageBubbleView;

    @Bind(R.id.tabLayout)
    TabLayout tabLayout;

    @Bind(R.id.viewPager)
    ViewPager viewPager;

    @Bind(R.id.countLocations)
    TextView countLocations;

    @Bind(R.id.countFollowers)
    TextView countFollowers;

    @Bind(R.id.unFollowUser)
    ImageView unFollowUser;

    List<String> followerIds = null;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        user = (User) getIntent().getSerializableExtra("profile");

        hideActionBar();
        setupViewPager();
        setupTabLayout();
        setupUserInformation();
    }

    private void hideActionBar() {
        android.app.ActionBar ab = getActionBar();
        if (ab != null) {
            ab.hide();
        }
    }

    private void setupUserInformation() {
        userName.setText(user.name);
        residence.setText(user.residence);
        profileImageBubbleView.setImage(user.thumbnailUri());
        UserController userController = UserController.getInstance();
        if (userController.loggedIn() && user._id.equals(userController.me()._id)) {
            unFollowUser.setVisibility(View.INVISIBLE);
        }
    }

    private void setupTabLayout() {
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager() {
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        addLocationsFragment(adapter);
        addJourneysFragment(adapter);
        addFollowerAdapter(adapter);
        addFollowsAdapter(adapter);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());
    }

    private void addLocationsFragment(FragmentAdapter adapter) {
        LocationsFragment fragment = new LocationsFragment();
        adapter.addFragment(fragment, "Locations");

        LocationController.getInstance().getLocationsByUserId(user._id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toList()
                .subscribe(
                        (locations -> {
                            fragment.adapter.setLocations(locations);
                            countLocations.setText(Integer.toString(locations.size()));
                        }),
                        (error) -> { }
                );
    }

    private void addJourneysFragment(FragmentAdapter adapter) {
        Fragment fragment = new Fragment();
        adapter.addFragment(fragment, "Journeys");
    }

    private void addFollowerAdapter(FragmentAdapter adapter) {
        UsersFragment fragment = new UsersFragment();
        adapter.addFragment(fragment, "Followers");

        UserController.getInstance().getFollowers(user._id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toList()
                .subscribe(
                        (followers -> {
                            fragment.adapter.setUsers(followers);
                            countFollowers.setText(Integer.toString(followers.size()));

                            followerIds = Observable.from(followers)
                                            .map(follower -> follower._id)
                                            .toList().toBlocking().single();
                        }),
                        (error -> {
                        })
                );
    }

    private void addFollowsAdapter(FragmentAdapter adapter) {
        UsersFragment fragment = new UsersFragment();
        adapter.addFragment(fragment, user.name + " folgt");

        Observable.from(user.following)
                .flatMap(following -> UserController.getInstance().getUser(following))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toList()
                .subscribe(
                        (fragment.adapter::setUsers),
                        (error) -> { }
                );
    }

    @OnClick(R.id.goBack)
    public void onGoBackClicked() {
        finish();
    }

    @OnClick(R.id.goToHomeScreen)
    public void onGoToHomeScreenClicked() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @OnClick(R.id.unFollowUser)
    public void unFollowUser() {
        UserController userController = UserController.getInstance();
        if (!userController.loggedIn()) {
            Toast.makeText(getApplicationContext(), "hierfür musst du angemeldet sein :-)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (followerIds == null) {
            return;
        }

        if (!followerIds.contains(userController.me()._id)) {
            followerIds.add(userController.me()._id);
            countFollowers.setText(Integer.toString(followerIds.size()));
            userController.followUser(user._id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            (res) -> {
                            },
                            (err) -> {
                                followerIds.remove(userController.me()._id);
                                countFollowers.setText(Integer.toString(followerIds.size()));
                                Toast.makeText(getApplicationContext(),
                                        "uups, das hat leider nicht geklappt", Toast.LENGTH_SHORT).show();
                            }
                    );
        } else {
            // todo: unfollow
        }
    }
}
