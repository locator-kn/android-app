package com.locator_app.locator.view.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.view.map.MapsActivity;

import java.util.List;
import java.util.Vector;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {
    @Bind(R.id.viewPager)
    ToggleableViewPager viewPager;

    HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        setupViewPager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewPager.setPagingEnabled(true);
        viewPager.setCurrentItem(0, false);
    }

    private void setupViewPager() {
        List<Fragment> fragments = new Vector<>();
        homeFragment = (HomeFragment) Fragment.instantiate(this, HomeFragment.class.getName());
        fragments.add(homeFragment);
        fragments.add(Fragment.instantiate(this, MapDummyFragment.class.getName()));
        PagerAdapter adapter  = new HomePagerAdapter(super.getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(intent);
                    viewPager.setPagingEnabled(false);
                }
            }
        });
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        homeFragment.onWindowFocusChanged(hasFocus);
    }
}
