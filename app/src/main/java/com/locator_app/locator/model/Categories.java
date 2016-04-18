package com.locator_app.locator.model;

import com.locator_app.locator.R;

public class Categories {

    public static final String NATURE    = "nature";
    public static final String NIGHTLIFE = "nightlife";
    public static final String CULTURE   = "culture";
    public static final String SECRET    = "secret";
    public static final String GASTRO    = "gastro";
    public static final String HOLIDAY   = "holiday";

    public static int NO_ICON = -1;

    public static Integer getLightCategoryIcon(String category) {
        switch (category) {
            case NATURE: return R.drawable.category_nature_light;
            case NIGHTLIFE: return R.drawable.category_party_light;
            case CULTURE: return R.drawable.category_culture_light;
            case SECRET: return R.drawable.category_secret_light;
            case HOLIDAY: return R.drawable.category_holiday_light;
            case GASTRO: return R.drawable.category_gastro_light;
        }
        return NO_ICON;
    }

    public static Integer getColorCategoryIcon(String category) {
        switch (category) {
            case NATURE: return R.drawable.category_nature_color;
            case NIGHTLIFE: return R.drawable.category_party_color;
            case CULTURE: return R.drawable.category_culture_color;
            case SECRET: return R.drawable.category_secret_color;
            case HOLIDAY: return R.drawable.category_holiday_color;
            case GASTRO: return R.drawable.category_gastro_color;
        }
        return NO_ICON;
    }

}
