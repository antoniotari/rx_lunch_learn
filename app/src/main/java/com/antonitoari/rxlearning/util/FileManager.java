package com.antonitoari.rxlearning.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import com.antonitoari.rxlearning.models.UserSearchResponse;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by antoniotari on 2016-02-27.
 */
public enum FileManager {
    INSTANCE;

    public final static String KEY_SHARED_PREFERENCES = "com.antoniotari.android.key.shared";
    private static final String FILENAME_CACHE = "com.antoniotari.android.ContactsFriendsFragment";

    private SharedPreferences mSharedPreferences;
    private Context mContext;

    public void init(Context context){
        mContext = context;
        mSharedPreferences = context.getSharedPreferences(KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    public static FileManager getInstance() {
        return INSTANCE;
    }

    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

    public <T extends Serializable> T readSerializable(final String filename) {
        if (filename == null) {
            return null;
        }

        String filePath = storageDir(mContext) + filename;
        if (!fileExistsAndCanRead(filePath)) {
            return null;
        }

        T recoveredQuarks = null;
        try {
            //use buffering
            InputStream fis = new FileInputStream(filePath);
            InputStream buffer = new BufferedInputStream(fis);
            ObjectInput ois = new ObjectInputStream(buffer);
            try {
                //deserialize the List
                recoveredQuarks = (T) ois.readObject();
            } finally {
                ois.close();
            }
        } catch (ClassNotFoundException ex) {
        } catch (EOFException ex) {
        } catch (IOException ex) {
        } catch (ClassCastException ex){
        }
        return recoveredQuarks;
    }

    public synchronized boolean writeSerializable(final String filename, final Serializable object) {
        try {
            //use buffering
            OutputStream file = new FileOutputStream(storageDir(mContext) + filename);
            OutputStream buffer = new BufferedOutputStream(file);
            ObjectOutput output = new ObjectOutputStream(buffer);
            try {
                output.writeObject(object);
            } finally {
                //file.flush();
                //file.close();
                //buffer.flush();
                //buffer.close();
                //output.flush();
                output.close();
                return true;
            }
        } catch (IOException ex) {
        }
        return false;
    }

    public static String storageDir(Context context) {
        File mediaStorageDir = context.getFilesDir();
        return mediaStorageDir.getPath() + File.separator;
    }

    /**
     * checks if the file exists and is readable
     */
    public static boolean fileExistsAndCanRead(String filePath) {
        File f = new File(filePath);
        return (f.exists() && !f.isDirectory() && f.canRead());
    }


    public static void cacheUsers(final UserSearchResponse userSearchResponse) {
        Observable.create(new OnSubscribe<Boolean>() {
            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {
                FileManager.getInstance().writeSerializable(FILENAME_CACHE, userSearchResponse);
                subscriber.onNext(true);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    public static Observable<UserSearchResponse> getCachedUsers() {
        return Observable.create(new OnSubscribe<UserSearchResponse>() {
            @Override
            public void call(final Subscriber<? super UserSearchResponse> subscriber) {
                UserSearchResponse userSearchResponse = FileManager.getInstance().readSerializable(FILENAME_CACHE);
                if (userSearchResponse != null) {
                    subscriber.onNext(userSearchResponse);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new Throwable("error message"));
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}