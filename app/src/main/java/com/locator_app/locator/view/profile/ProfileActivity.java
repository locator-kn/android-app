package com.locator_app.locator.view.profile;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.locator_app.locator.R;

public class ProfileActivity extends FragmentActivity {

    ProfilePagerAdapter profilePagerAdapter;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilePagerAdapter = new ProfilePagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager)findViewById(R.id.pager);
        viewPager.setAdapter(profilePagerAdapter);
    }

}
