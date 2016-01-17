package com.locator_app.locator.view.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.locator_app.locator.R;
import com.locator_app.locator.controller.LocationController;
import com.locator_app.locator.model.User;
import com.locator_app.locator.view.MapsActivity;
import com.locator_app.locator.view.bubble.BubbleView;
import com.locator_app.locator.view.fragments.FragmentAdapter;
import com.locator_app.locator.view.fragments.LocationsFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
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
        profileImageBubbleView.loadImage(user.thumbnailUri());
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
                        (fragment.adapter::setLocations),
                        (error -> {
                        })
                );
    }

    private void addJourneysFragment(FragmentAdapter adapter) {
        Fragment fragment = new Fragment();
        adapter.addFragment(fragment, "Journeys");
    }

    private void addFollowerAdapter(FragmentAdapter adapter) {
        Fragment fragment = new Fragment();
        adapter.addFragment(fragment, "Followers");
    }

    private void addFollowsAdapter(FragmentAdapter adapter) {
        Fragment fragment = new Fragment();
        adapter.addFragment(fragment, user.name + " folgt");
    }

    @OnClick(R.id.goBack)
    public void onGoBackClicked() {
        finish();
    }

    @OnClick(R.id.showMap)
    public void onShowMapClicked() {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);
    }
}
