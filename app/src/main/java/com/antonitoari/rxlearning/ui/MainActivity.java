package com.antonitoari.rxlearning.ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.antonitoari.rxlearning.APIService;
import com.antonitoari.rxlearning.R;
import com.antonitoari.rxlearning.util.FileManager;
import com.jakewharton.retrofit.Ok3Client;

import okhttp3.OkHttpClient;
import retrofit.RestAdapter;
import rx.Subscription;
import rx.android.observables.AndroidObservable;

public class MainActivity extends AppCompatActivity {

    private static final String ENDPOINT = "http://tari.ddns.net";
    private APIService mApiService;
    private Subscription mApiSubscr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FileManager.getInstance().init(getApplicationContext());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container,new ContactListFragment())
                .commit();

        mApiService = provideRestAdapter(ENDPOINT, APIService.class);

        // using subscription
        mApiSubscr = mApiService.findContacts("emails")
                .subscribe(userSearchResponse -> {});

        // using activity wrapper
        AndroidObservable.bindActivity(this, mApiService.findContacts("emails"))
                .subscribe(userSearchResponse -> {});

//        Observable.zip(mApiService.getUserPhoto(id),
//                mApiService.getPhotoMetadata(id),
//                (photo, metadata) -> createPhotoWithData(photo, metadata))
//                .subscribe(photoWithData -> showPhoto(photoWithData));

        // AndroidObservable.fromBroadcast() allows you to create an Observable that works like a BroadcastReceiver.
        // Here's a way to be notified whenever network connectivity changes:
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        AndroidObservable.fromBroadcast(getApplicationContext(), filter)
                .subscribe(this::handleConnectivityChange);
    }

    private void handleConnectivityChange(Intent intent) {
        Log.d("rxlearning",intent.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mApiSubscr!=null && !mApiSubscr.isUnsubscribed()) {
            mApiSubscr.unsubscribe();
        }
    }

    private <T> T provideRestAdapter(String endpoint, Class<? extends T> serviceClass) {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setClient(new Ok3Client(new OkHttpClient.Builder().build()))
                .setEndpoint(endpoint);
        return builder.build().create(serviceClass);
    }
}
