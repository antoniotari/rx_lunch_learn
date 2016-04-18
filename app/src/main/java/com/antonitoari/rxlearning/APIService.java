package com.antonitoari.rxlearning;

import com.antonitoari.rxlearning.models.UserSearchResponse;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by antonio tari on 2016-02-17.
 */
public interface APIService {

    @GET ("/rx/user_search.json")
    Observable<UserSearchResponse> findContacts(@Query ("emails") String emails);

    @GET ("/rx/user_search.json")
    void findContacts(@Query ("emails") String emails, Callback<UserSearchResponse> callback);
}