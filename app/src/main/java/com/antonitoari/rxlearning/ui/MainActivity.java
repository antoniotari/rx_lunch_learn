package com.antonitoari.rxlearning.ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.antonitoari.rxlearning.APIService;
import com.antonitoari.rxlearning.R;
import com.antonitoari.rxlearning.models.UserWithImage;
import com.antonitoari.rxlearning.util.FileManager;
import com.antonitoari.rxlearning.util.Log;
import com.jakewharton.retrofit.Ok3Client;

import okhttp3.OkHttpClient;
import retrofit.RestAdapter;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String ENDPOINT = "http://tari.ddns.net";
    private static final String URL_IMAGE = "https://upload.wikimedia.org/wikipedia/en/6/6d/Van_Halen_-_The_Best_of_Both_Worlds.jpg";
    private APIService mApiService;
    private Subscription mApiSubscr;
    private ImageView mRxImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRxImage = (ImageView)findViewById(R.id.rxImage);
        FileManager.getInstance().init(getApplicationContext());

//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.container,new ContactListFragment())
//                .commit();

        mApiService = provideRestAdapter(ENDPOINT, APIService.class);

        // using subscription
        mApiSubscr = mApiService.findContacts("emails")
                .subscribe(userSearchResponse -> {});

        // using activity wrapper
        AndroidObservable.bindActivity(this, mApiService.findContacts("emails"))
                .subscribe(userSearchResponse -> {});

        mApiService.findContacts("")
                .flatMap(userSearchResponse -> Observable.just(userSearchResponse.getEntities().get(0).getProperties()))
                .flatMap(userProperties -> Observable.just(userProperties.getUrl()))
                .flatMap(this::getBitmapFromURL)
                .subscribe(mRxImage::setImageBitmap);


//        Observable.zip(mApiService.findContacts("").flatMap(userSearchResponse ->
//                        Observable.just(userSearchResponse.getEntities().get(0).getProperties())),
//                getBitmapFromURL(URL_IMAGE),
//                UserWithImage::new)
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

    private void showPhoto(UserWithImage userWithImage) {
        mRxImage.setImageBitmap(userWithImage.getUserBitmap());
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

    private Observable<Bitmap> getBitmapFromURL(final String src) {
        return Observable.create(new OnSubscribe<Bitmap>() {
            @Override
            public void call(final Subscriber<? super Bitmap> subscriber) {
                try {
                    if(subscriber.isUnsubscribed())return;
                    URL url = new URL(src);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    if(subscriber.isUnsubscribed())return;
                    subscriber.onNext(myBitmap);
                } catch (Exception e) {
                    if(subscriber.isUnsubscribed())return;
                    subscriber.onError(e);
                    Log.error(e);
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
