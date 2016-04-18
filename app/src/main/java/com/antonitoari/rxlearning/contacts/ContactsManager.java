package com.antonitoari.rxlearning.contacts;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;

import java.util.ArrayList;
import java.util.List;

import com.antonitoari.rxlearning.models.AddressBookContact;
/**
 * Created by antonio tari
 */
public class ContactsManager {

    private ContentResolver mContentResolver;

    public ContactsManager(Context context) {
        // ContentResolver holds a reference to the context
        mContentResolver = context.getContentResolver();
    }

    public List<AddressBookContact> getContacts() {
        List<AddressBookContact> contactList = new ArrayList<>();
        // get all the contacts
        Cursor cur = mContentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cur != null && cur.getCount() > 0) {

            //loop through the contact list
            while (cur.moveToNext()) {

                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));

                List<String> phoneNumbers = new ArrayList<>();
                // if the contact at the current cursor position has a phone number ...
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                    // select from the phone numbers table the numbers with the current contact id
                    Cursor pCur = mContentResolver.query(Phone.CONTENT_URI, null,
                            Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                    // loop through the phone numbers
                    while (pCur != null && pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(Phone.NUMBER));
                        phoneNumbers.add(phoneNo);
                    }
                    if (pCur != null) {
                        pCur.close();
                    }
                }

                List<String> emails = new ArrayList<>();
                Cursor pCur = mContentResolver.query(
                        Data.CONTENT_URI,
                        null,
                        Data.MIMETYPE + "=? AND " + Phone.CONTACT_ID + " = ?",
                        new String[]{Email.CONTENT_ITEM_TYPE, id},
                        Data.CONTACT_ID);

                while (pCur != null && pCur.moveToNext()) {
                    String emailAddr = pCur.getString(pCur.getColumnIndex(Email.ADDRESS));
                    emails.add(emailAddr);
                }
                if (pCur != null) {
                    pCur.close();
                }

                contactList.add(new AddressBookContact(id, name, phoneNumbers, emails));
            }
        }
        if (cur != null) {
            cur.close();
        }

        return contactList;
    }
}
