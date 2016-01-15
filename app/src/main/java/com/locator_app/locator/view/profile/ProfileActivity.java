package com.locator_app.locator.view.profile;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.model.User;
import com.locator_app.locator.util.CacheImageLoader;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends FragmentActivity {

    ProfilePagerAdapter profilePagerAdapter;

    @Bind(R.id.pager)
    ViewPager viewPager;

    @Bind(R.id.profileImageView)
    ImageView profileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        User user = (User) getIntent().getSerializableExtra("profile");
        displayUserInfo(user);

        profilePagerAdapter = new ProfilePagerAdapter(getSupportFragmentManager(), user);
        viewPager.setAdapter(profilePagerAdapter);
    }

    private void displayUserInfo(User user) {
        CacheImageLoader.getInstance().setImage(user.thumbnailUri(), profileImageView);
    }

    @OnClick(R.id.profileImageView)
    void onProfileImageClicked() {
        Toast.makeText(getApplicationContext(), "profile clicked", Toast.LENGTH_SHORT).show();
    }

}
