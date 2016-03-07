package com.locator_app.locator.view;


import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.locator_app.locator.R;
import com.locator_app.locator.view.login.LoginRegisterStartActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocatorHeader {

    @Bind(R.id.locatorHeaderBack)
    public ImageView back;

    @Bind(R.id.locatorHeaderCancel)
    public ImageView cancel;

    @Bind(R.id.locatorHeaderTitle)
    public TextView title;

    Activity activity;

    public LocatorHeader(Activity activity) {
        this.activity = activity;
        ButterKnife.bind(this, activity);
    }

    public void setTitle(int title) {
        this.title.setText(title);
    }

    public void hideBackIcon() {
        back.setVisibility(View.INVISIBLE);
    }

    public void hideCancelIcon() {
        cancel.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.locatorHeaderBack)
    void onBackClick() {
        if (back.getVisibility() == View.VISIBLE) {
            activity.finish();
        }
    }

    @OnClick(R.id.locatorHeaderCancel)
    void onCancelClick() {
        if (cancel.getVisibility() == View.VISIBLE) {
            Intent intent = new Intent(activity, LoginRegisterStartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            activity.finish();
        }
    }
}
