package com.antonitoari.rxlearning.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.antonitoari.rxlearning.R;
import com.antonitoari.rxlearning.util.FileManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FileManager.getInstance().init(getApplicationContext());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container,new ContactListFragment())
                .commit();
    }
}
