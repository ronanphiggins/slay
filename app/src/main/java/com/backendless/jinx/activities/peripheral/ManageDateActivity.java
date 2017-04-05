package com.backendless.jinx.activities.peripheral;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.jinx.R;
import com.backendless.jinx.activities.base.BaseActivity;
import com.backendless.jinx.utilities.backendless.Dates;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.daprlabs.aaron.swipedeck.SwipeDeck;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;


public class ManageDateActivity extends BaseActivity {



    private SharedPreferences prefs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_date);
        ButterKnife.bind(this);
        setupToolbar();


        prefs = PreferenceManager.getDefaultSharedPreferences(this);


        /*Backendless.Persistence.of(Dates.class).findById("C6FF2152-8E96-F824-FF17-16ACA7D9EF00", new AsyncCallback<Dates>() {
            @Override
            public void handleResponse(Dates date) {


                Log.i("backendless", "date id =" + date.getObjectId());

                BackendlessUser[] owner = date.getOwner();
                for (BackendlessUser user : owner) {

                    Log.i("backendless", "gender =" + String.valueOf(user.getProperty("gender")));

                }



            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {


            }
        });*/

        String whereClause = "gender = 1";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause( whereClause );

        Backendless.Persistence.of( Dates.class ).find( dataQuery, new AsyncCallback<BackendlessCollection<Dates>>(){


            @Override
            public void handleResponse( BackendlessCollection<Dates> dates )
            {

                Iterator<Dates> iterator = dates.getCurrentPage().iterator();

                while (iterator.hasNext()) {

                    Dates date = iterator.next();

                    Log.i("backendless", "found date id =" + date.getObjectId());



                }

            }
            @Override
            public void handleFault( BackendlessFault fault )
            {


            }


        });




    }


    private void setupToolbar() {
        final ActionBar ab = getActionBarToolbar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        setTitle("");
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
        return R.id.nav_manage_dates;
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }

}
