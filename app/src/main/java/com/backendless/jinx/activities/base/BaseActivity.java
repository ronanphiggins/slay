package com.backendless.jinx.activities.base;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.jinx.R;
import com.backendless.jinx.activities.core.LoginActivity;
import com.backendless.jinx.activities.core.SettingsActivity;
import com.backendless.jinx.activities.peripheral.HomeActivity;
import com.backendless.jinx.utilities.dialogs.CustomDialogClass;
import com.backendless.jinx.utilities.strings.StringUtil;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.backendless.jinx.utilities.logging.LogUtil.logD;
import static com.backendless.jinx.utilities.logging.LogUtil.makeLogTag;

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = makeLogTag(BaseActivity.class);

    protected static final int NAV_DRAWER_ITEM_INVALID = -1;

    private DrawerLayout drawerLayout;
    private Toolbar actionBarToolbar;


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupNavDrawer();
    }



    /**
     * Sets up the navigation drawer.
     */
    private void setupNavDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout == null) {
            // current activity does not have a drawer.
            return;
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerSelectListener(navigationView);
            setSelectedItem(navigationView);
        }

        logD(TAG, "navigation drawer setup finished");


        UserIdStorageFactory.instance().init(this);
        String userId = UserIdStorageFactory.instance().getStorage().get();
        Log.i("backendless", userId);
        final String userIdSplit = StringUtil.splitString(userId);

        String url = getString(R.string.backendless_api) + getString(R.string.app_id) + "/" + getString(R.string.app_version) + getString(R.string.app_media) + userIdSplit + getString(R.string.file_format);

        Log.i("backendless", url);

        View hView =  navigationView.getHeaderView(0);
        final CircleImageView civProfilePic = (CircleImageView)hView.findViewById(R.id.navbarpic);
        final ProgressBar progressBar = (ProgressBar)hView.findViewById(R.id.progress);
        Glide.with(this)
                .load(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .fitCenter().into(civProfilePic);

        civProfilePic.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {


                Toast.makeText(getApplicationContext(), "nav image clicked",
                        Toast.LENGTH_LONG).show();


            }
        });




        /*final ImageView img = (ImageView)hView.findViewById(R.id.navbarpic);
        Glide.with(this).load(url).asBitmap().fitCenter().into(new BitmapImageViewTarget(img) {
            @Override
            protected void setResource(Bitmap resource) {

                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                img.setImageDrawable(circularBitmapDrawable);
            }
        });*/





    }

    /**
     * Updated the checked item in the navigation drawer
     * @param navigationView the navigation view
     */
    private void setSelectedItem(NavigationView navigationView) {
        // Which navigation item should be selected?
        int selectedItem = getSelfNavDrawerItem(); // subclass has to override this method
        navigationView.setCheckedItem(selectedItem);
    }

    /**
     * Creates the item click listener.
     * @param navigationView the navigation view
     */
    private void setupDrawerSelectListener(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        drawerLayout.closeDrawers();
                        onNavigationItemClicked(menuItem.getItemId());
                        return true;
                    }
                });
    }

    /**
     * Handles the navigation item click.
     * @param itemId the clicked item
     */
    private void onNavigationItemClicked(final int itemId) {
        if(itemId == getSelfNavDrawerItem()) {
            // Already selected
            closeDrawer();
            return;
        }

        goToNavDrawerItem(itemId);
    }

    /**
     * Handles the navigation item click and starts the corresponding activity.
     * @param item the selected navigation item
     */
    private void goToNavDrawerItem(int item) {
        switch (item) {
            case R.id.nav_home:
                startActivity(new Intent(this, HomeActivity.class));
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_logout:
                final CustomDialogClass cdd = new CustomDialogClass(BaseActivity.this);
                cdd.progressDialog("Signing off..");
                Backendless.UserService.logout(new AsyncCallback<Void>()

                {
                    public void handleResponse (Void response)
                    {
                        cdd.checkDialog();
                        Log.i("backendless", "logged out");
                        startActivity(new Intent(BaseActivity.this, LoginActivity.class));
                    }

                    public void handleFault(BackendlessFault fault) {
                        cdd.checkDialog();
                        Log.i("backendless", "logout failed");

                    }
                });
                break;
        }
    }

    /**
     * Provides the action bar instance.
     * @return the action bar.
     */
    protected ActionBar getActionBarToolbar() {
        if (actionBarToolbar == null) {
            actionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
            if (actionBarToolbar != null) {
                setSupportActionBar(actionBarToolbar);
            }
        }
        return getSupportActionBar();
    }


    /**
     * Returns the navigation drawer item that corresponds to this Activity. Subclasses
     * have to override this method.
     */
    protected int getSelfNavDrawerItem() {
        return NAV_DRAWER_ITEM_INVALID;
    }

    protected void openDrawer() {
        if(drawerLayout == null)
            return;

        drawerLayout.openDrawer(GravityCompat.START);
    }

    protected void closeDrawer() {
        if(drawerLayout == null)
            return;

        drawerLayout.closeDrawer(GravityCompat.START);
    }

    public abstract boolean providesActivityToolbar();

    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
