package com.locator_app.locator.view.profile;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.locator_app.locator.view.fragments.LocationsFragment;

import java.util.LinkedHashMap;

import rx.Observable;

public class ProfilePagerAdapter extends FragmentPagerAdapter {

    LinkedHashMap<String, Fragment> fragments = new LinkedHashMap<>();

    public ProfilePagerAdapter(FragmentManager fm) {
        super(fm);
        fragments.put("Location", new LocationsFragment());
        fragments.put("Journeys", new LocationsFragment());
        fragments.put("Follower", new LocationsFragment());
        fragments.put("Ich folge", new LocationsFragment());
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
