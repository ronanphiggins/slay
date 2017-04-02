package com.backendless.jinx.activities.core;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.DeviceRegistration;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.jinx.R;
import com.backendless.jinx.activities.peripheral.HomeActivity;
import com.backendless.jinx.utilities.dialogs.CustomDialogClass;
import com.backendless.jinx.utilities.ui.WindowUtil;

public class LoginActivity extends AppCompatActivity {

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private TextView mButtonCancel;
    private TextView mButtonLogon;
    private TextView mButtonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        WindowUtil.changeWindowBarColour(this);

        Backendless.initApp(this, getString(R.string.app_id), getString(R.string.app_secret), getString(R.string.app_version));
        Log.i("backendless", "backendless successfully initialised");

        if (Backendless.UserService.loggedInUser() == "") { Log.i("backendless", "Logged out"); }
        else { startActivity(new Intent(LoginActivity.this, HomeActivity.class)); }


        mUsernameEditText = (EditText) findViewById(R.id.editText);
        mPasswordEditText = (EditText) findViewById(R.id.editText2);
        mButtonCancel = (TextView) findViewById(R.id.button2);
        mButtonLogon = (TextView) findViewById(R.id.button);
        mButtonRegister = (TextView) findViewById(R.id.register);

        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCredentials(v);
            }
        });

        mButtonLogon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String password = mPasswordEditText.getText().toString().replaceAll("\\s+", "");
                String username = mUsernameEditText.getText().toString().replaceAll("\\s+","");



                if (!password.isEmpty() && !username.isEmpty()) {


                    final CustomDialogClass cdd = new CustomDialogClass(LoginActivity.this);
                    cdd.progressDialog("Authenticating..");

                    Backendless.UserService.login( username, password, new AsyncCallback<BackendlessUser>()
                    {
                        public void handleResponse(final BackendlessUser user )
                        {
                            Log.i("backendless", "login success");

                            cdd.checkDialog();
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));



                        }

                        public void handleFault( BackendlessFault fault )
                        {
                            cdd.checkDialog();
                            Toast.makeText(LoginActivity.this, "Login failed. Please try again..",
                                    Toast.LENGTH_LONG).show();
                            Log.i("info", "login failed" + fault.getCode());
                        }
                    },true);




                } else {


                    Toast.makeText(LoginActivity.this, "Both fields must be completed..",
                            Toast.LENGTH_LONG).show();


                }




            }
        });


    }

    public void clearCredentials(View view) {
        mUsernameEditText.setText(null);
        mPasswordEditText.setText(null);
    }
}
