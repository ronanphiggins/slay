package com.backendless.jinx.activities.core;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.jinx.R;
import com.backendless.jinx.activities.base.BaseActivity;
import com.backendless.jinx.activities.peripheral.HomeActivity;
import com.backendless.jinx.utilities.bitmaps.bitmapUtil;
import com.backendless.jinx.utilities.dialogs.CustomDialogClass;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.bumptech.glide.Glide;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * This Activity provides several settings. Activity contains {@link PreferenceFragment} as inner class.
 *
 * Created by Andreas Schrade on 14.12.2015.
 */
public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupToolbar();

        Button button = (Button) findViewById(R.id.changepicture);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("info", "change pic clicked");
                Crop.pickImage(SettingsActivity.this);

            }
        });
    }

    private void setupToolbar() {
        final ActionBar ab = getActionBarToolbar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                openDrawer();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return R.id.nav_settings;
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }

    public static class SettingsFragment extends PreferenceFragment {
        public SettingsFragment() {}

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_prefs);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(imageReturnedIntent.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, imageReturnedIntent);
        }

    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {

            final CustomDialogClass cdd = new CustomDialogClass(SettingsActivity.this);
            cdd.progressDialog("Changing profile picture..");

            Log.i("info", "crop worked");

            final Uri imageUri = Crop.getOutput(result);
            final InputStream imageStream;
            try {

                imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                Log.i("info", "image select success");

                String currentId = UserIdStorageFactory.instance().getStorage().get();
                String[] parts = currentId.split("-");
                final String part = parts[4];
                String imageLoad = part + ".png";

                Boolean overwrite = true;



                Bitmap scaled = bitmapUtil.BITMAP_RESIZER(selectedImage, 900, 900);


                Backendless.Files.Android.upload(scaled,
                        Bitmap.CompressFormat.PNG,
                        100,
                        imageLoad,
                        "media",
                        overwrite,
                        new AsyncCallback<BackendlessFile>() {
                            @Override
                            public void handleResponse(final BackendlessFile backendlessFile) {

                                Log.i("info", "upload success");

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Glide.get(SettingsActivity.this).clearDiskCache();

                                        cdd.checkDialog();

                                        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                                        //finish();


                                    }
                                }).start();


                            }

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {

                                Log.i("info", "upload failed");

                            }
                        });




            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }



        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
