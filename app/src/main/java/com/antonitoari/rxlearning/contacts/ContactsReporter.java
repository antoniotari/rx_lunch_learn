package com.antonitoari.rxlearning.contacts;

import android.content.Context;

import java.util.List;

import com.antonitoari.rxlearning.models.AddressBookContact;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by antonio tari
 *
 * looks up all the contacts in the phone
 */
public class ContactsReporter {
    public Observable<List<AddressBookContact>> addressBookService (final Context context){
        return Observable.create(new OnSubscribe<List<AddressBookContact>>() {
            @Override
            public void call(final Subscriber<? super List<AddressBookContact>> subscriber) {
                try {
                    List<AddressBookContact> contacts = new ContactsManager(context).getContacts();
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(contacts);
                        subscriber.onCompleted();
                    }
                }catch (Exception e){
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }
}
