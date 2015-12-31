package com.locator_app.locator.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.locator_app.locator.R;

public class LoginCustomActionBar {

    private ActionBar actionBar;
    private Activity activity;
    private View actionBarView;
    private ImageView backButton;
    private ImageView crossButton;
    private Class<?> cls;

    public LoginCustomActionBar(ActionBar actionBar, final AppCompatActivity activity) {
        this.actionBar = actionBar;
        this.actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        this.actionBar.setCustomView(R.layout.actionbar_custom);
        this.actionBarView = this.actionBar.getCustomView();
        this.backButton = (ImageView) this.actionBarView.findViewById(R.id.actionbar_back);
        this.crossButton = (ImageView) this.actionBarView.findViewById(R.id.actionbar_cross);
        this.activity = activity;

        this.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //close software keyboard
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                activity.finish();
            }
        });

        this.crossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jump to HomeActivity if no class was set
                if (cls == null) {
                    cls = HomeActivity.class;
                }

                //close software keyboard
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                Intent intent = new Intent(activity.getApplicationContext(), cls);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activity.startActivity(intent);
            }
        });
    }

    public void setTitle(String title) {
        TextView titleTxtView = (TextView) this.actionBarView.findViewById(R.id.actionbar_title);
        titleTxtView.setText(title);
    }

    public void setBackButtonVisibility(int visibility) {
        this.backButton.setVisibility(visibility);
    }

    public void setCrossButtonVisibility(int visibility) {
        this.crossButton.setVisibility(visibility);
    }

    public void setCrossButtonJumpScreen(Class<?> cls) {
        this.cls = cls;
    }

    public void setColor(int color) {
        this.actionBar.setBackgroundDrawable(new ColorDrawable
                (ContextCompat.getColor(this.activity.getApplicationContext(), color)));
    }
}
