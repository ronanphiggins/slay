package com.backendless.jinx.activities.peripheral;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import android.widget.Toast;

import butterknife.ButterKnife;

import com.backendless.jinx.R;
import com.backendless.jinx.activities.base.BaseActivity;
import com.backendless.jinx.activities.core.LoginActivity;
import com.daprlabs.aaron.swipedeck.SwipeDeck;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private SwipeDeck cardStack;
    private Context context = this;
    private SwipeDeckAdapter adapter;
    private ArrayList<String> testData;

    private Bitmap theBitmap;
    private Bitmap theBitmap2;
    private ArrayList<Bitmap> bitmapArrayList;

    SharedPreferences prefs;

    //private CheckBox dragCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setupToolbar();


        cardStack = (SwipeDeck) findViewById(R.id.swipe_deck);
        //dragCheckbox = (CheckBox) findViewById(R.id.checkbox_drag);

        testData = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            testData.add(String.valueOf(i));
        }

        theBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.selfie);
        theBitmap2 = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.selfie2);

        bitmapArrayList = new ArrayList<Bitmap>();
        bitmapArrayList.add(theBitmap);
        bitmapArrayList.add(theBitmap2);
        bitmapArrayList.add(theBitmap);
        bitmapArrayList.add(theBitmap2);
        bitmapArrayList.add(theBitmap);
        bitmapArrayList.add(theBitmap2);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);









        adapter = new SwipeDeckAdapter(bitmapArrayList, this);
        if(cardStack != null){
            cardStack.setAdapter(adapter);
        }




        cardStack.setCallback(new SwipeDeck.SwipeDeckCallback() {
            @Override
            public void cardSwipedLeft(long stableId) {
                Log.i("MainActivity", "card was swiped left, position in adapter: " + stableId);

                //Boolean bo = prefs.getBoolean("pref_vi", true);

                int in = prefs.getInt("pref_distance", 2);

                Toast.makeText(HomeActivity.this, Integer.toString(in),
                        Toast.LENGTH_LONG).show();


            }

            @Override
            public void cardSwipedRight(long stableId) {
                Log.i("MainActivity", "card was swiped right, position in adapter: " + stableId);



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
        return R.id.nav_home;
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }


    public class SwipeDeckAdapter extends BaseAdapter {

        private List<Bitmap> data;
        private Context context;

        public SwipeDeckAdapter(List<Bitmap> data, Context context) {
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
            Bitmap bitmapholder = (Bitmap)getItem(position);
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
                    v.getContext().startActivity(i);*/
                }
            });
            return v;
        }
    }
}
