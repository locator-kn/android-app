package com.locator_app.locator.view.profile;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.locator_app.locator.controller.LocationController;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.model.User;
import com.locator_app.locator.view.fragments.LocationsFragment;

import java.util.LinkedHashMap;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ProfilePagerAdapter extends FragmentPagerAdapter {

    LinkedHashMap<String, Fragment> fragments = new LinkedHashMap<>();

    public ProfilePagerAdapter(FragmentManager fm, User user) {
        super(fm);
        createLocationsFragment(user);
        createJourneysFragment(user);
        createFollowerFragment(user);
        createFollowsFragment(user);
    }

    private void createLocationsFragment(User user) {
        String tabTitle = "Location";
        LocationsFragment fragment = new LocationsFragment();
        fragments.put(tabTitle, fragment);
        Observable<LocatorLocation> observable = LocationController.getInstance().getLocationsByUserId(user._id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        fragment.loadLocations(observable);
    }

    private void createJourneysFragment(User user) {
        String tabTitle = "Journeys";
        fragments.put(tabTitle, new LocationsFragment());
    }

    private void createFollowerFragment(User user) {
        String tabTitle = "Follower";
        fragments.put(tabTitle, new LocationsFragment());
    }

    private void createFollowsFragment(User user) {
        String tabTitle = getFollowsTabTitleByUser(user);
        fragments.put(tabTitle, new LocationsFragment());
    }

    private String getFollowsTabTitleByUser(User user) {
        boolean isMe = UserController.getInstance().me()._id.equals(user._id);
        return (isMe) ? "Ich folge" : user.name + " folgt";
    }

    @Override
    public Fragment getItem(int position) {
        return Observable.from(fragments.values())
                .elementAt(position).toBlocking().last();
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Observable.from(fragments.keySet())
                .elementAt(position).toBlocking().last();
    }
}
