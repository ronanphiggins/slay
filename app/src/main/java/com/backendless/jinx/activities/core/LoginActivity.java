package com.backendless.jinx.activities.core;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.backendless.jinx.R;
import com.backendless.jinx.utilities.ui.WindowUtil;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        WindowUtil.changeWindowBarColour(this);


    }
}
