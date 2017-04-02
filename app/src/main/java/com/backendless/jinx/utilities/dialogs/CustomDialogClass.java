package com.backendless.jinx.utilities.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.backendless.jinx.R;


public class CustomDialogClass extends Dialog implements
        View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes, no;
    public ProgressDialog pDialog;

    public CustomDialogClass(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        dismiss();
    }

    public void progressDialog(String text) {

        pDialog = new ProgressDialog(c);
        pDialog.setMessage(text);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public void checkDialog() {

        if (pDialog.isShowing())
            pDialog.dismiss();

    }
}
