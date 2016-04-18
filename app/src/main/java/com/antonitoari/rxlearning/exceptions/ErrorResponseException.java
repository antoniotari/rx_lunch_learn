package com.antonitoari.rxlearning.exceptions;

import com.antonitoari.rxlearning.models.ErrorResponse;

/**
 * Created by antoniotari on 2016-02-27.
 */
public class ErrorResponseException extends Exception {
    private ErrorResponse mErrorResponse;
    public ErrorResponseException(ErrorResponse errorResponse){
        super(errorResponse.getRetrofitError());
        mErrorResponse = errorResponse;
    }

    public ErrorResponseException(final String errorResponseMessage){
        super(errorResponseMessage);
        mErrorResponse = new ErrorResponse();
        mErrorResponse.setMessage(errorResponseMessage);
    }

    public ErrorResponseException(final Exception exception){
        super(exception);
        mErrorResponse = new ErrorResponse();
        mErrorResponse.setMessage(exception.getLocalizedMessage());
    }

    public ErrorResponse getErrorResponse() {
        return mErrorResponse;
    }
}
