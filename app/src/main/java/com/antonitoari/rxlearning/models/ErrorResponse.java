package com.antonitoari.rxlearning.models;

import retrofit.RetrofitError;

/**
 * Created by antonio tari on 2016-02-23.
 */
public class ErrorResponse {

    private transient RetrofitError mRetrofitError;
    private boolean mNoConnection = false;
    private int mErrorCode = -1;

    public ErrorResponse() {

    }

    public boolean isNoConnection() {
        return mNoConnection;
    }



    public void setMessage(final String message){

    }

    public void setRetrofitError(final RetrofitError retrofitError) {
        mRetrofitError = retrofitError;
        if(retrofitError!=null && retrofitError.getResponse()!=null) {
            mErrorCode = retrofitError.getResponse().getStatus();

        }
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public RetrofitError getRetrofitError() {
        return mRetrofitError;
    }
}