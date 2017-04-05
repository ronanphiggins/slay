package com.backendless.jinx.utilities.swipedeck;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DeckContent {

    public static final List<DeckItem> ITEMS = new ArrayList<>();


    public static final Map<String, DeckItem> ITEM_MAP = new HashMap<>(5);

    static {
        //addItem(new BidderItem("1", R.drawable.dummypic, "Sarah Conway", "Let's go for a drink?", "Let's go for a drink?"));

    }

    public static void addItem(DeckItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static void removeItem(int index) {

        String str = Integer.toString(index);

        ITEMS.remove(index);
        ITEM_MAP.remove(str);
    }

    public static int count() {

        return ITEMS.size();

    }

    public static void clear() {
        ITEMS.clear();
        ITEM_MAP.clear();


    }

    public static class DeckItem {
        public final String id;
        public final String name;
        public final String age;
        public final Bitmap profile;


        public DeckItem(String id, String name, String age, Bitmap profile) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.profile = profile;

        }
    }
}
