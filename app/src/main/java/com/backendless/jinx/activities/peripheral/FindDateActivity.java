package com.backendless.jinx.activities.peripheral;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.jinx.R;
import com.backendless.jinx.activities.base.BaseActivity;
import com.backendless.jinx.utilities.backendless.Dates;
import com.backendless.jinx.utilities.strings.StringUtil;
import com.backendless.jinx.utilities.swipedeck.DeckContent;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.daprlabs.aaron.swipedeck.SwipeDeck;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class FindDateActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private SwipeDeck cardStack;
    private Context context = this;
    private SwipeDeckAdapter adapter;
    private ArrayList<String> testData;

    private ArrayList<Dates> datesArray;

    private ArrayList<String> dateholder;

    private String prefGender;
    private int prefDistance;


    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_date);
        ButterKnife.bind(this);
        setupToolbar();


        cardStack = (SwipeDeck) findViewById(R.id.swipe_deck);


        datesArray = new ArrayList<Dates>();

        dateholder = new ArrayList<String>();


        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefGender = prefs.getString("pref_seeking", "1");

        prefDistance = prefs.getInt("pref_distance", 1);


        findDates();



        cardStack.setCallback(new SwipeDeck.SwipeDeckCallback() {
            @Override
            public void cardSwipedLeft(long stableId) {
                Log.i("MainActivity", "card was swiped left, position in adapter: " + stableId);


                checkAdapter();

                Log.i("backendless", adapter.getUserId((int)stableId));



            }

            @Override
            public void cardSwipedRight(long stableId) {
                Log.i("MainActivity", "card was swiped right, position in adapter: " + stableId);

                checkAdapter();



            }


        });

        cardStack.setLeftImage(R.id.left_image);
        cardStack.setRightImage(R.id.right_image);


    }

    private void checkAdapter() {



        if (cardStack.getChildCount() == 1) {


            Toast.makeText(getApplicationContext(), "swipedeckfinished",
                    Toast.LENGTH_LONG).show();

            findDates();


        }


    }


    private void setupToolbar() {
        final ActionBar ab = getActionBarToolbar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        setTitle("");
    }


    private void findDates() {




        String whereClause = "likers.objectId = '" + UserIdStorageFactory.instance().getStorage().get() + "'";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery( whereClause );
        QueryOptions queryOptions = new QueryOptions();
        queryOptions.setRelationsDepth( 1 );
        dataQuery.setQueryOptions( queryOptions );

        Backendless.Persistence.of( Dates.class ).find( dataQuery, new AsyncCallback<BackendlessCollection<Dates>>(){


            @Override
            public void handleResponse( final BackendlessCollection<Dates> dates )
            {

                Iterator<Dates> iterator = dates.getCurrentPage().iterator();


                while (iterator.hasNext()) {

                    Dates date = iterator.next();

                    //datesArray.add(date);

                    dateholder.add(date.getObjectId());


                }

                StringBuilder builder = new StringBuilder();
                int count = 1;
                for (String value : dateholder) {
                    builder.append("'");
                    builder.append(value);
                    builder.append("'");
                    if (count != dateholder.size()) {
                        builder.append(",");
                    }

                    count++;

                }


                Log.i("backendless", builder.toString());


                String whereClause = "objectId NOT IN (" + builder.toString() + ") and gender = " + prefGender + "and distance( 53.348313, -6.281905, coordinates.latitude, coordinates.longitude ) < mi(" + String.valueOf(prefDistance) + ")";
                BackendlessDataQuery dataQuery = new BackendlessDataQuery( whereClause );
                QueryOptions queryOptions = new QueryOptions();
                queryOptions.setRelationsDepth( 1 );
                dataQuery.setQueryOptions( queryOptions );

                Backendless.Persistence.of( Dates.class ).find( dataQuery, new AsyncCallback<BackendlessCollection<Dates>>(){


                    @Override
                    public void handleResponse( final BackendlessCollection<Dates> dates )
                    {

                        Iterator<Dates> iterator = dates.getCurrentPage().iterator();

                        Log.i("backendless", "dateholder success");



                        while (iterator.hasNext()) {

                            Dates date = iterator.next();

                            datesArray.add(date);



                        }

                        adapter = new SwipeDeckAdapter(datesArray, FindDateActivity.this);
                        if(cardStack != null){


                            cardStack.setVisibility(View.INVISIBLE);
                            cardStack.setAdapter(adapter);


                        }



                    }
                    @Override
                    public void handleFault( BackendlessFault fault )
                    {

                        Log.i("backendless", fault.getMessage().toString());


                    }


                });

            }
            @Override
            public void handleFault( BackendlessFault fault )
            {

                Log.i("backendless", fault.getMessage().toString());


            }


        });






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
        return R.id.nav_find_date;
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }


    public class SwipeDeckAdapter extends BaseAdapter {

        private List<Dates> data;
        private Context context;

        public SwipeDeckAdapter(List<Dates> data, Context context) {
            this.data = data;
            this.context = context;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        public String getUserId(int position) {


            String theId = "";


            for (BackendlessUser user : data.get(position).getOwner()) {

                theId = user.getUserId();

            }

            return theId;

        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = getLayoutInflater();
                // normally use a viewholder
                v = inflater.inflate(R.layout.swipe_card, parent, false);
            }

            ImageView imageView = (ImageView) v.findViewById(R.id.offer_image);

            String userId = "";

            BackendlessUser[] owner = data.get(position).getOwner();

            String name = "";

            for (BackendlessUser user : owner) {

                userId = user.getUserId();
                name = (String)user.getProperty("name") + ", ";


            }

            name = name.substring(0, 1).toUpperCase() + name.substring(1);

            final String userIdSplit = StringUtil.splitString(userId);

            final String url = getString(R.string.backendless_api) + getString(R.string.app_id) + "/" + getString(R.string.app_version) + getString(R.string.app_media) + userIdSplit + getString(R.string.file_format);

            //Glide.with(context).load(url).fitCenter().into(imageView);

            //final ProgressBar progressBar = (ProgressBar)v.findViewById(R.id.progress);

            Glide.with(context)
                    .load(url)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            //progressBar.setVisibility(View.GONE);
                            //cardStack.setVisibility(View.VISIBLE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            //progressBar.setVisibility(View.GONE);
                            cardStack.setVisibility(View.VISIBLE);
                            return false;
                        }
                    })
                    .fitCenter().into(imageView);

            TextView textView = (TextView) v.findViewById(R.id.textView1);

            textView.setText(name);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("Layer type: ", Integer.toString(v.getLayerType()));
                    Log.i("Hardware Accel type:", Integer.toString(View.LAYER_TYPE_HARDWARE));

                }
            });
            return v;
        }


    }


}
