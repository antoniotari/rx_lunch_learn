package com.antonitoari.rxlearning.models;

import android.graphics.Bitmap;

/**
 * Created by antoniotari on 2016-04-19.
 */
public class UserWithImage {
    UserProperties mUserProperties;
    Bitmap mUserBitmap;

    public UserWithImage(final UserProperties userProperties, final Bitmap userBitmap) {
        mUserProperties = userProperties;
        mUserBitmap = userBitmap;
    }

    public Bitmap getUserBitmap() {
        return mUserBitmap;
    }
}
