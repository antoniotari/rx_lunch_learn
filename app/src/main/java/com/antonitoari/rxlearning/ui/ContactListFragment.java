package com.antonitoari.rxlearning.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import com.antonitoari.rxlearning.APIService;
import com.antonitoari.rxlearning.R;
import com.antonitoari.rxlearning.contacts.ContactsReporter;
import com.antonitoari.rxlearning.exceptions.ErrorResponseException;
import com.antonitoari.rxlearning.models.AddressBookContact;
import com.antonitoari.rxlearning.models.UserProperties;
import com.antonitoari.rxlearning.models.UserResponse;
import com.antonitoari.rxlearning.models.UserSearchResponse;
import com.antonitoari.rxlearning.util.FileManager;
import com.antonitoari.rxlearning.util.MD5;
import com.jakewharton.retrofit.Ok3Client;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;


public class ContactListFragment extends Fragment {

    private static final String ENDPOINT = "http://tari.ddns.net";

    @Bind (R.id.listSearchUsers) RecyclerView mListView;

    protected UserListAdapter mUserAdapter;
    protected UserSearchResponse mUserSearchResponse;
    protected final Subject<UserSearchResponse, UserSearchResponse> mContactsSubject = PublishSubject.create();
    private APIService mApiService;
    private Context mApplicationContext;

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        mApplicationContext = activity.getApplicationContext();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApiService = provideRestAdapter(ENDPOINT, APIService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_friend, container, false);
        ButterKnife.bind(this, view);
        // set up recycler view
        mListView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mListView.setLayoutManager(llm);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FileManager.getCachedUsers()
                .filter(userSearchResponse -> userSearchResponse != null)
                .doOnNext(userSearchResponse -> mUserSearchResponse = userSearchResponse)
                .flatMap((UserSearchResponse userSearchResponse) -> Observable.just(userSearchResponse.getEntities()))
                .flatMap(this::userPropertiesService)
                .subscribe(this::setUserList, errorResponse -> {
                    errorResponseHandling(errorResponse);
                    getContacts();
                }, this::getContacts);
    }

    protected void getContacts() {
        new ContactsReporter().addressBookService(mApplicationContext)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(this::contactsToMd5Service)
                //.flatMap(Observable::from)
                //.reduce((String r,String r2) -> r+","+r2)
                .doOnNext(this::getRemoteContacts)
                .flatMap((String s) -> userSearchResponseService())
                .flatMap(this::userPropertiesService)
                .subscribe(this::setUserList, this::errorResponseHandling);
    }

    /**
     * Set the users of the list if you want to clear the list send empty list.
     */
    public void setUserList(List<UserProperties> users) {
        if (users != null && users.isEmpty() && isAdded()) {
        } else {
            initAdapter(users);
        }
    }

    private void initAdapter(List<UserProperties> users) {
        if (mUserAdapter == null) {
            mUserAdapter = new UserListAdapter(users);
            mUserAdapter.setOnUserClickListener((view, userProperties) ->
                    Toast.makeText(mApplicationContext, userProperties.getName(), Toast.LENGTH_LONG).show());
            mListView.setAdapter(mUserAdapter);
        } else {
            mUserAdapter.setContactList(users);
            mUserAdapter.notifyDataSetChanged();
        }
    }

    protected Observable<List<UserProperties>> userPropertiesService(List<UserResponse> entities) {
        return Observable.from(entities)
                .flatMap(userResponse -> Observable.just(userResponse.getProperties()))
                .toList();
    }

    protected Observable<String> emailListToMd5String(List<String> emailList) {
        return Observable.from(emailList)
                .flatMap((final String s) -> Observable.just(MD5.md5(s)))
                .toList()
                .flatMap(Observable::from)
                .reduce((String r, String r2) -> r + "," + r2);
    }

    protected Observable<List<UserResponse>> userSearchResponseService() {
        return mContactsSubject
                .filter(userSearchResponse -> userSearchResponse != null)
                .filter(userSearchResponse -> !userSearchResponse.equals(mUserSearchResponse))
                .doOnNext(userSearchResponse -> mUserSearchResponse = userSearchResponse)
                .doOnNext(FileManager::cacheUsers)
                .flatMap((UserSearchResponse userSearchResponse) -> Observable.just(userSearchResponse.getEntities()));
    }

    protected void errorResponseHandling(Throwable throwable) {
        if (throwable instanceof ErrorResponseException) {
            Log.e("tag",((ErrorResponseException) throwable).getErrorResponse().toString());
        } else {
            Log.e("tag",throwable.toString());
        }
    }

    protected void getRemoteContacts(String md5s) {
        mApiService.findContacts(md5s, new Callback<UserSearchResponse>() {
            @Override
            public void success(final UserSearchResponse userSearchResponse, final Response response) {
                Log.d("tag",userSearchResponse.getEntities().get(0).getProperties().getUrl());
                mContactsSubject.onNext(userSearchResponse);
            }

            @Override
            public void failure(final RetrofitError error) {
                mContactsSubject.onError(new ErrorResponseException(error));

            }
        });
    }

    private Observable<String> contactsToMd5Service(List<AddressBookContact> addressBookContacts) {
        return Observable.from(addressBookContacts)
                .filter(addressBookContact -> addressBookContact.getEmailList() != null)
                .filter(addressBookContact -> !addressBookContact.getEmailList().isEmpty())
                .flatMap((AddressBookContact addressBookContact) -> Observable.from(addressBookContact.getEmailList()))
                .toList()
                .flatMap(this::emailListToMd5String);
    }


    private <T> T provideRestAdapter(String endpoint, Class<? extends T> serviceClass) {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setClient(new Ok3Client(new OkHttpClient.Builder().build()))
                .setEndpoint(endpoint);
        return builder.build().create(serviceClass);
    }
}