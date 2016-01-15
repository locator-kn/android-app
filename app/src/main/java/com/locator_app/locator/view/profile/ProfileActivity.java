package com.locator_app.locator.view.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.model.User;
import com.locator_app.locator.util.CacheImageLoader;
import com.locator_app.locator.view.MapsActivity;
import com.locator_app.locator.view.bubble.BubbleView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends FragmentActivity {

    ProfilePagerAdapter profilePagerAdapter;

    @Bind(R.id.pager)
    ViewPager viewPager;

    @Bind(R.id.profileImageBubbleView)
    BubbleView profileImageBubbleView;

    @Bind(R.id.residence)
    TextView residence;

    @Bind(R.id.userName)
    TextView userName;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        user = (User) getIntent().getSerializableExtra("profile");
        displayUserInfo();

        profilePagerAdapter = new ProfilePagerAdapter(getSupportFragmentManager(), user);
        viewPager.setAdapter(profilePagerAdapter);
    }

    private void displayUserInfo() {
        profileImageBubbleView.loadImage(user.thumbnailUri());
        userName.setText(user.name);
        residence.setText(user.residence);
    }

    @OnClick(R.id.profileImageBubbleView)
    void onProfileImageClicked() {
        String text = "Ahoi, ich bin " + user.name + "!";
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.goBack)
    void onGoBackClicked() {
        finish();
    }

    @OnClick(R.id.showMap)
    void onShowMapClicked() {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);
    }

}
