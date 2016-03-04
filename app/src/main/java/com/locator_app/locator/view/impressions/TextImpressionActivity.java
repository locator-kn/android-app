package com.locator_app.locator.view.impressions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.controller.LocationController;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TextImpressionActivity extends Activity {

    @Bind(R.id.textImpression)
    TextView textImpression;

    @Bind(R.id.sendTextImpression)
    ImageView sendTextImpression;

    String locationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_impression);
        ButterKnife.bind(this);

        locationId = getIntent().getStringExtra("locationId");

        updateSendIconAlphaValue();

        textImpression.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSendIconAlphaValue();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @OnClick(R.id.cancelButton)
    void onCancelButtonClick() {
        finish();
    }

    @OnClick(R.id.sendTextImpression)
    void onSendTextImpressionClick() {
        if (reachedMinImpressionLength()) {
            String impression = textImpression.getText().toString();
            LocationController.getInstance().createTextImpression(locationId, impression)
            .subscribe(
                    (val) -> {
                        Intent intent = new Intent();
                        intent.putExtra("success", true);
                        setResult(RESULT_OK, intent);
                        finish();
                    },
                    (err) -> {
                        Intent intent = new Intent();
                        intent.putExtra("success", false);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
            );
        }
    }

    private boolean reachedMinImpressionLength() {
        final int minImpressionLength = 3;
        return textImpression.getText().length() >= minImpressionLength;
    }

    private void updateSendIconAlphaValue() {
        if (reachedMinImpressionLength()) {
            float alphaEnabled = 1.0f;
            sendTextImpression.setAlpha(alphaEnabled);
        } else {
            float alphaDisabled = 0.4f;
            sendTextImpression.setAlpha(alphaDisabled);
        }
    }

}
