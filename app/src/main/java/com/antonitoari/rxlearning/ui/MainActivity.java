package com.antonitoari.rxlearning.ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.util.Arrays;
import java.util.List;

import com.antonitoari.rxlearning.APIService;
import com.antonitoari.rxlearning.R;
import com.antonitoari.rxlearning.models.UserWithImage;
import com.antonitoari.rxlearning.util.FileManager;
import com.antonitoari.rxlearning.util.Log;
import com.antonitoari.rxlearning.util.MD5;
import com.antonitoari.rxlearning.util.Utils;
import com.jakewharton.retrofit.Ok3Client;

import okhttp3.OkHttpClient;
import retrofit.RestAdapter;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

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
                .flatMap(Utils::getBitmapFromURL)
                .subscribe(mRxImage::setImageBitmap, Log::error);


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

    private void example1() {
        Observable<String> obsStr = Observable.create(new OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {

            }
        });

        obsStr.map(new Func1<String, String>() {

            @Override
            public String call(final String s) {
                return MD5.md5(s);
            }
        })
                .map(new Func1<String, Integer>() {
                    @Override
                    public Integer call(final String s) {
                        return s.hashCode();
                    }
                })
                .map(new Func1<Integer, String>() {
                    @Override
                    public String call(final Integer integer) {
                        return String.valueOf(integer);
                    }
                })
                .subscribe(new Action1<String>() {
            @Override
            public void call(final String s) {

            }
        }, new Action1<Throwable>() {
            @Override
            public void call(final Throwable throwable) {

            }
        }, new Action0() {
            @Override
            public void call() {

            }
        });
    }

    private void example2() {
        Observable.from("url1", "url2", "url3")
                .take(5)
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(final String s) {
                        return s!=null;
                    }
                })
                .subscribe(url -> System.out.println(url));
    }

    private void example3() {
        List<String> strings = Arrays.asList("a","b","c");
        Observable.from(strings).subscribe(Log::log);
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
}
