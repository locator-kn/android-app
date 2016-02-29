package com.locator_app.locator.view.locationcreation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.apiservice.locations.LocationsApiService;
import com.locator_app.locator.controller.LocationController;
import com.locator_app.locator.view.LocationDetailActivity;
import com.locator_app.locator.view.home.HomeActivity;
import com.locator_app.locator.view.LoadingSpinner;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseCategories extends Activity {
    Bundle extras;

    @Bind(R.id.nature)
    LinearLayout nature;
    @Bind(R.id.nightlife)
    LinearLayout nightlife;
    @Bind(R.id.culture)
    LinearLayout culture;
    @Bind(R.id.secret)
    LinearLayout secret;
    @Bind(R.id.gastro)
    LinearLayout gastro;
    @Bind(R.id.holiday)
    LinearLayout holiday;

    @Bind(R.id.cancelButton)
    ImageView cancelButton;

    LoadingSpinner loadingSpinner;

    private static final String NATURE_ID    = "nature";
    private static final String NIGHTLIFE_ID = "party";
    private static final String CULTURE_ID   = "culture";
    private static final String SECRET_ID    = "secret";
    private static final String GASTRO_ID    = "gastro";
    private static final String HOLIDAY_ID   = "holiday";

    @Bind(R.id.next)
    ImageView next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_categories);
        ButterKnife.bind(this);

        loadingSpinner = new LoadingSpinner(this);
        extras = getIntent().getExtras();
    }

    @OnClick(R.id.nature)
    void onNatureClicked()    { toggleCategory(nature, NATURE_ID); }
    @OnClick(R.id.nightlife)
    void onNightlifeClicked() { toggleCategory(nightlife, NIGHTLIFE_ID); }
    @OnClick(R.id.culture)
    void onCultureClicked()   { toggleCategory(culture, CULTURE_ID); }
    @OnClick(R.id.secret)
    void onSecretClicked()    { toggleCategory(secret, SECRET_ID); }
    @OnClick(R.id.gastro)
    void onGastroClicked()    { toggleCategory(gastro, GASTRO_ID); }
    @OnClick(R.id.holiday)
    void onHolidayClicked()   { toggleCategory(holiday, HOLIDAY_ID); }

    private final ArrayList<String> selectedCategories = new ArrayList<>();
    private static final int MAX_CATEGORIES = 3;

    synchronized
    private void toggleCategory(View v, String category) {
        synchronized (selectedCategories) {
            if (selectedCategories.contains(category)) {
                selectedCategories.remove(category);
                toggleAlpha(v);
            } else if (selectedCategories.size() < MAX_CATEGORIES) {
                selectedCategories.add(category);
                toggleAlpha(v);
            }

            if (selectedCategories.size() == 1) {
                next.setAlpha((float) 1);
            } else if (selectedCategories.size() == 0) {
                next.setAlpha((float) 0.3);
            }
        }
    }

    private void toggleAlpha(View v) {
        if (v.getAlpha() == 1) {
            v.setAlpha((float) 0.3);
        } else {
            v.setAlpha((float) 1);
        }
    }

    boolean loading = false;

    @OnClick(R.id.next)
    void onNextClicked() {
        synchronized (selectedCategories) {
            if (selectedCategories.size() > 0) {
                loading = true;
                loadingSpinner.showSpinner();
                cancelButton.setVisibility(View.GONE);
                String[] categories = new String[selectedCategories.size()];
                selectedCategories.toArray(categories);
                LocationController.getInstance().createLocation(extras.getString("name"),
                        extras.getDouble("lon"),
                        extras.getDouble("lat"),
                        categories,
                        (Bitmap) extras.get("picture"))
                .subscribe((location) -> {
                            Intent intent = new Intent(this, LocationDetailActivity.class);
                            intent.putExtra("location", location);
                            startActivity(intent);
                        },
                        (error) -> {
                            loadingSpinner.hideSpinner();
                            cancelButton.setVisibility(View.VISIBLE);
                            Toast.makeText(ChooseCategories.this, "Could not create location",
                                    Toast.LENGTH_SHORT).show();
                        }
                );
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!loading) {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.cancelButton)
    void onCancelButtonClicked() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
