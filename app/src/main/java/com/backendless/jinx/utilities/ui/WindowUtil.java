package com.backendless.jinx.utilities.ui;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

import com.backendless.jinx.R;


public class WindowUtil {

    public static void changeWindowBarColour(Activity activity) {

        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(activity.getResources().getColor(R.color.theme_primary_dark));

    }
}
