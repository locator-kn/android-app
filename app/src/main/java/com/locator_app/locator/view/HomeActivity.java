package com.locator_app.locator.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.service.SchoenHierRequestManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeActivity extends AppCompatActivity {

    @Bind(R.id.button)
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        // 1. you wont need 'findViewById' anymore
        // 2. no need for 'setOnClickListener'
        button.setText("SchoenHiers");
    }

    @OnClick(R.id.button)
    public void loadSchoenHiers() {
        /*SchoenHierApiService service = ServiceFactory.createService(SchoenHierApiService.class);
        double lon = 9.169753789901733;
        double lat = 47.66868204997508;
        double dis = 2;
        int max = 3;
        service.getSchoenHiers(lon, lat, dis, max)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(schoenHierResponse -> schoenHierResponse.results)
                .flatMap(results -> rx.Observable.from(results))
                .map(result -> result.schoenHier)
                .map(schoenHier -> schoenHier.creationDate + "/" + schoenHier.modifiedDate)
                .subscribe(
                        (val) -> Toast.makeText(getApplicationContext(), val,
                                Toast.LENGTH_SHORT).show()/*,
                        (err) -> Toast.makeText(getApplicationContext(), err.toString(),
                                Toast.LENGTH_SHORT).show(),
                        () -> Toast.makeText(getApplicationContext(), "done",
                                Toast.LENGTH_SHORT).show()
                );*/

        SchoenHierRequestManager rqManager = new SchoenHierRequestManager();
        rqManager.getSchoenHiers()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(schoenHierResponse -> schoenHierResponse.results)
                .flatMap(results -> Observable.from(results))
                .map(result -> result.schoenHier)
                .map(schoenHier -> schoenHier.creationDate)
                .subscribe(
                        (val) -> Toast.makeText(getApplicationContext(), val, Toast.LENGTH_SHORT).show(),
                        (err) -> Toast.makeText(getApplicationContext(), err.toString(), Toast.LENGTH_SHORT).show()
                );
    }
}
