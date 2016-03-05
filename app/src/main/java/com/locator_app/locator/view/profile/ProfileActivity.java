package com.locator_app.locator.view.profile;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.locator_app.locator.R;
import com.locator_app.locator.controller.LocationController;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.model.User;
import com.locator_app.locator.view.fragments.FavoritesFragment;
import com.locator_app.locator.view.fragments.FragmentAdapter;
import com.locator_app.locator.view.fragments.LocationsFragment;
import com.locator_app.locator.view.fragments.UsersFragment;
import com.locator_app.locator.view.home.HomeActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Observable;

public class ProfileActivity extends FragmentActivity {


    @Bind(R.id.residence)
    TextView residence;

    @Bind(R.id.userName)
    TextView userName;

    @Bind(R.id.profileImageView)
    CircleImageView profileImageBubbleView;

    @Bind(R.id.tabLayout)
    TabLayout tabLayout;

    @Bind(R.id.viewPager)
    ViewPager viewPager;

    @Bind(R.id.countLocations)
    TextView countLocations;

    @Bind(R.id.countFollowers)
    TextView countFollowers;

    @Bind(R.id.unFollowUser)
    View unFollowUser;

    @Bind(R.id.unFollowImage)
    ImageView unFollowImage;

    List<String> followerIds = null;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        user = (User) getIntent().getSerializableExtra("profile");

        Glide.with(this).load(user.getProfilePictureNormalSize())
                .error(R.drawable.profile)
                .centerCrop()
                .into(profileImageBubbleView);

        hideActionBar();
        setupViewPager();
        setupTabLayout();
        updateUserInformation();
    }

    private void hideActionBar() {
        android.app.ActionBar ab = getActionBar();
        if (ab != null) {
            ab.hide();
        }
    }

    private void updateUserInformation() {
        userName.setText(user.name);
        residence.setText(user.residence);
        if (followerIds != null) {
            countFollowers.setText(String.format("%d", followerIds.size()));
        }
        if (isSelf()) {
            replaceFollowButtonWithSettings();
        } else if (iFollowThisUser()) {
            unFollowImage.setImageBitmap(BitmapFactory.decodeResource(getResources(),
                    R.drawable.small_follow_red));
        } else {
            unFollowImage.setImageBitmap(BitmapFactory.decodeResource(getResources(),
                    R.drawable.follow_user_small));
        }
    }

    private void replaceFollowButtonWithSettings() {
        Glide.with(this).load(R.drawable.ic_setting_dark).asBitmap().into(unFollowImage);
    }

    private void setupTabLayout() {
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager() {
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        addLocationsFragment(adapter);
        addFavoritesFragment(adapter);
        addFollowerAdapter(adapter);
        addFollowsAdapter(adapter);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());
    }

    private void addLocationsFragment(FragmentAdapter adapter) {
        LocationsFragment fragment = new LocationsFragment();
        adapter.addFragment(fragment, "Locations");

        LocationController.getInstance().getLocationsByUserId(user.id)
                .toList()
                .subscribe(
                        (locations) -> {
                            fragment.adapter.setLocations(locations);
                            countLocations.setText(Integer.toString(locations.size()));
                        },
                        (error) -> {
                        }
                );
    }

    private void addFavoritesFragment(FragmentAdapter adapter) {
        FavoritesFragment fragment = new FavoritesFragment();
        adapter.addFragment(fragment, "Favorites");

        LocationController.getInstance().getFavoritedLocations(user.id)
                .toList()
                .subscribe(
                        fragment.adapter::setLocations,
                        (error) -> {}
                );
    }

    private void addFollowerAdapter(FragmentAdapter adapter) {
        UsersFragment fragment = new UsersFragment();
        adapter.addFragment(fragment, "Followers");

        UserController.getInstance().getFollowers(user.id)
                .toList()
                .subscribe(
                        (followers) -> {
                            followerIds = Observable.from(followers)
                                    .map(follower -> follower.id)
                                    .toList().toBlocking().single();
                            fragment.adapter.setUsers(followers);
                            updateUserInformation();
                        },
                        (error) -> {}
                );
    }

    private void addFollowsAdapter(FragmentAdapter adapter) {
        UsersFragment fragment = new UsersFragment();
        adapter.addFragment(fragment, user.name + " folgt");
        Observable.from(user.following)
                .flatMap(following -> UserController.getInstance().getUser(following))
                .toList()
                .subscribe(
                        fragment.adapter::setUsers,
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
        startActivity(intent);
    }

    @OnClick(R.id.unFollowUser)
    public void onUnFollowUserClicked() {
        if (isSelf()) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        } else {
            doFollowOrUnfullowUser();
        }
    }

    private boolean isSelf() {
        return UserController.getInstance().loggedIn() &&
                UserController.getInstance().me().id.equals(user.id);
    }

    public void doFollowOrUnfullowUser() {
        if (followerIds == null) {
            return;
        }
        UserController userController = UserController.getInstance();
        if (!userController.loggedIn()) {
            Toast.makeText(getApplicationContext(), "hierfür musst du angemeldet sein :-)", Toast.LENGTH_SHORT).show();
            return;
        }
        if (iFollowThisUser()) {
            doUnfollowUser();
        } else {
            doFollowUser();
        }
    }

    private boolean iFollowThisUser() {
        return followerIds != null && UserController.getInstance().loggedIn() &&
                followerIds.contains(UserController.getInstance().me().id);
    }

    private void doUnfollowUser() {
        followerIds.remove(UserController.getInstance().me().id);
        countFollowers.setText(String.format("%d", followerIds.size()));
        UserController.getInstance().unfollowUser(user.id)
                .subscribe(
                        (res) -> {
                            updateUserInformation();
                        },
                        (err) -> {
                            followerIds.add(UserController.getInstance().me().id);
                            updateUserInformation();
                            Toast.makeText(getApplicationContext(),
                                    "uups, das hat leider nicht geklappt", Toast.LENGTH_SHORT).show();
                        }
                );
    }

    private void doFollowUser() {
        followerIds.add(UserController.getInstance().me().id);
        countFollowers.setText(String.format("%d", followerIds.size()));
        UserController.getInstance().followUser(user.id)
                .subscribe(
                        (res) -> {
                            updateUserInformation();
                        },
                        (err) -> {
                            followerIds.remove(UserController.getInstance().me().id);
                            updateUserInformation();
                            Toast.makeText(getApplicationContext(),
                                    "uups, das hat leider nicht geklappt", Toast.LENGTH_SHORT).show();
                        }
                );
    }

}
