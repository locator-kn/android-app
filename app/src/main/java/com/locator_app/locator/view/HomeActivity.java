package com.locator_app.locator.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.controller.LocationController;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.service.users.RegistrationRequest;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class HomeActivity extends AppCompatActivity {

    @Bind(R.id.logout)
    Button logout;

    @Bind(R.id.loadLocation)
    Button loadLocation;

    @Bind(R.id.loadLocationsNearby)
    Button loadLocationsNearby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.logout)
    public void logout() {

        UserController controller = UserController.getInstance();
        controller.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(logoutResponse -> logoutResponse.message)
                .subscribe(
                        (message) -> {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), LoginRegisterStartActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        },
                        (err) -> {
                            Toast.makeText(getApplicationContext(), "could not log out, sorry!", Toast.LENGTH_LONG).show();
                        }
                );
    }

    @OnClick(R.id.loadLocation)
    public void loadLocation() {

        final String locatorHqId = "567a96f3990007900125f56b";
        final String noSuchLocation = "067a96f3990007900125f56b";

        LocationController controller = LocationController.getInstance();
        controller.getLocationById(locatorHqId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(locatorLocation -> locatorLocation.title)
                .subscribe(
                        (title) -> Toast.makeText(getApplicationContext(), title, Toast.LENGTH_SHORT).show(),
                        (error) -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    @OnClick(R.id.loadLocationsNearby)
    public void loadLocationsNearby() {

        LocationController controller = LocationController.getInstance();
        controller.getLocationsNearby(9.169753789901733,
                47.66868204997508, 20000, 100)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(location -> location.city)
                .map(city -> city.title)
                .distinct()
                .subscribe(
                        (name) -> Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show(),
                        (err) -> Toast.makeText(getApplicationContext(), err.toString(), Toast.LENGTH_LONG).show(),
                        () -> Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_LONG).show()
                );
    }
}
