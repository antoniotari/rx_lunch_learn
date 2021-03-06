package com.antonitoari.rxlearning.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.antonitoari.rxlearning.R;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by antoniotari on 2016-04-20.
 */
public class Utils {
    public static int layoutForMainActivity(){
        return R.layout.activity_main;
    }

    public static Observable<Bitmap> getBitmapFromURL(final String src) {
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
