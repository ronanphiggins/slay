package com.backendless.jinx.activities.peripheral;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FindDateActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private SwipeDeck cardStack;
    private Context context = this;
    private SwipeDeckAdapter adapter;
    private ArrayList<String> testData;

    private ArrayList<Dates> datesArray;

    private Bitmap theBitmap;
    //private Bitmap theBitmap2;
    //private ArrayList<Bitmap> bitmapArrayList;

    private SharedPreferences prefs;

    //private CheckBox dragCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_date);
        ButterKnife.bind(this);
        setupToolbar();


        cardStack = (SwipeDeck) findViewById(R.id.swipe_deck);
        //dragCheckbox = (CheckBox) findViewById(R.id.checkbox_drag);

        /*testData = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            testData.add(String.valueOf(i));
        }*/

        //theBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.selfie);
        //theBitmap2 = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.selfie2);

        datesArray = new ArrayList<Dates>();

        /*bitmapArrayList = new ArrayList<Bitmap>();
        bitmapArrayList.add(theBitmap);
        bitmapArrayList.add(theBitmap2);
        bitmapArrayList.add(theBitmap);
        bitmapArrayList.add(theBitmap2);
        bitmapArrayList.add(theBitmap);
        bitmapArrayList.add(theBitmap2);*/

        prefs = PreferenceManager.getDefaultSharedPreferences(this);


        String st = prefs.getString("pref_seeking", "1");

        int dis = prefs.getInt("pref_distance", 1);

        String whereClause = "distance( 53.348313, -6.281905, coordinates.latitude, coordinates.longitude ) < mi(" + String.valueOf(dis) + ") " + "and gender = " + st;
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


            }


        });

        /*Backendless.Persistence.of( Dates.class ).find( dataQuery, new AsyncCallback<BackendlessCollection<Dates>>(){


            @Override
            public void handleResponse( final BackendlessCollection<Dates> dates )
            {

                Iterator<Dates> iterator = dates.getCurrentPage().iterator();

                int addContentCounter = 1;

                while (iterator.hasNext()) {

                    final Dates date = iterator.next();

                    Log.i("backendless", "found date id =" + date.getObjectId());

                    String userId = "";

                    BackendlessUser[] owner2 = date.getOwner();
                    for (BackendlessUser user : owner2) {

                        userId = user.getUserId();

                    }


                    Log.i("backendless", userId);
                    final String userIdSplit = StringUtil.splitString(userId);

                    final String convert = String.valueOf(addContentCounter);

                    final int nonconvert = addContentCounter;

                    final String url = getString(R.string.backendless_api) + getString(R.string.app_id) + "/" + getString(R.string.app_version) + getString(R.string.app_media) + userIdSplit + getString(R.string.file_format);


                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            if (Looper.myLooper() == null)
                            {
                                Looper.prepare();
                            }
                            try {
                                theBitmap = Glide.
                                        with(FindDateActivity.this).
                                        load(url).
                                        asBitmap().
                                        into(400, 400).
                                        get();
                            } catch (final ExecutionException e) {

                            } catch (final InterruptedException e) {

                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void dummy) {
                            if (null != theBitmap) {

                                Log.i("backendless", "bitmap is not null");

                                BackendlessUser[] owner = date.getOwner();
                                for (BackendlessUser user : owner) {

                                    Log.i("backendless", "gender =" + String.valueOf(user.getProperty("gender")));

                                    DeckContent.addItem(new DeckContent.DeckItem(String.valueOf(convert), user.getProperty("name").toString(), "27", theBitmap));

                                }

                                if (dates.getTotalObjects() == nonconvert) {


                                    adapter = new SwipeDeckAdapter(DeckContent.ITEMS, FindDateActivity.this);
                                    if(cardStack != null){

                                        cardStack.setAdapter(adapter);


                                    }



                                }





                            }

                        }
                    }.execute();

                    addContentCounter ++;



                }

            }
            @Override
            public void handleFault( BackendlessFault fault )
            {


            }


        });*/















        cardStack.setCallback(new SwipeDeck.SwipeDeckCallback() {
            @Override
            public void cardSwipedLeft(long stableId) {
                Log.i("MainActivity", "card was swiped left, position in adapter: " + stableId);

                //Boolean bo = prefs.getBoolean("pref_vi", true);

                //int in = prefs.getInt("pref_distance", 2);

                //boolean bo = prefs.getBoolean("dateActive_pref", false);

                checkAdapter();


                /*Toast.makeText(FindDateActivity.this, String.valueOf(bo),
                        Toast.LENGTH_LONG).show();*/


            }

            @Override
            public void cardSwipedRight(long stableId) {
                Log.i("MainActivity", "card was swiped right, position in adapter: " + stableId);

                checkAdapter();



            }




            //@Override
            /*public boolean isDragEnabled(long itemId) {
                return dragCheckbox.isChecked();
            }*/
        });

        cardStack.setLeftImage(R.id.left_image);
        cardStack.setRightImage(R.id.right_image);



        /*Button btn = (Button) findViewById(R.id.button_left);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardStack.swipeTopCardLeft(500);

            }
        });
        Button btn2 = (Button) findViewById(R.id.button_right);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardStack.swipeTopCardRight(180);
            }
        });

        Button btn3 = (Button) findViewById(R.id.button_center);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                testData.add("a sample string.");
//                adapter.notifyDataSetChanged();
                cardStack.unSwipeCard();
            }
        });*/
    }

    private void checkAdapter() {



        if (cardStack.getChildCount() == 1) {


            Toast.makeText(getApplicationContext(), "swipedeckfinished",
                    Toast.LENGTH_LONG).show();


        }





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
        return R.id.nav_find_date;
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }


    /*public class SwipeDeckAdapter extends BaseAdapter {

        private List<DeckContent.DeckItem> data;
        private Context context;

        public SwipeDeckAdapter(List<DeckContent.DeckItem> data, Context context) {
            this.data = data;
            this.context = context;
        }

        @Override
        public int getCount() {
            return data.size();
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
            //((TextView) v.findViewById(R.id.textView2)).setText(data.get(position));
            ImageView imageView = (ImageView) v.findViewById(R.id.offer_image);
            //Bitmap bitmapholder = (Bitmap)getItem(position);



            Bitmap bitmapholder = data.get(position).profile;
            //Glide.with(context).load(bitmapholder).asBitmap().into(imageView);

            imageView.setImageBitmap(bitmapholder);


            //Picasso.with(context).load(bitmapholder).fit().centerCrop().into(imageView);
            //TextView textView = (TextView) v.findViewById(R.id.sample_text);
            //String item = (String)getItem(position);
            //textView.setText(item);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("Layer type: ", Integer.toString(v.getLayerType()));
                    Log.i("Hardware Accel type:", Integer.toString(View.LAYER_TYPE_HARDWARE));
                    /*Intent i = new Intent(v.getContext(), BlankActivity.class);
                    v.getContext().startActivity(i);
                }
            });
            return v;
        }


    }*/

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
            //((TextView) v.findViewById(R.id.textView2)).setText(data.get(position));
            ImageView imageView = (ImageView) v.findViewById(R.id.offer_image);
            //Bitmap bitmapholder = (Bitmap)getItem(position);

            //Bitmap bitmapholder = data.get(position).profile;
            //Glide.with(context).load(bitmapholder).asBitmap().into(imageView);

            String userId = "";

            BackendlessUser[] owner = data.get(position).getOwner();
            for (BackendlessUser user : owner) {

                userId = user.getUserId();

            }

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

            //imageView.setImageBitmap(bitmapholder);


            //Picasso.with(context).load(bitmapholder).fit().centerCrop().into(imageView);
            //TextView textView = (TextView) v.findViewById(R.id.sample_text);
            //String item = (String)getItem(position);
            //textView.setText(item);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("Layer type: ", Integer.toString(v.getLayerType()));
                    Log.i("Hardware Accel type:", Integer.toString(View.LAYER_TYPE_HARDWARE));
                    /*Intent i = new Intent(v.getContext(), BlankActivity.class);
                    v.getContext().startActivity(i);*/
                }
            });
            return v;
        }


    }


}
