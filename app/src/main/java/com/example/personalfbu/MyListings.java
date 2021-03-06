package com.example.personalfbu;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MyListings extends AppCompatActivity {

    private RecyclerView rvStream;
    private ListingAdapter adapter;
    private List<Listing> listingList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_stream);

        // find elements
        rvStream = findViewById(R.id.rvStream);

        // set views initial default
        rvStream.setVisibility(View.VISIBLE);
        findViewById(R.id.avNoListings).setVisibility(View.INVISIBLE);
        findViewById(R.id.tvNoListings).setVisibility(View.INVISIBLE);

        // create list and set adapter on list
        listingList = new ArrayList<>();
        adapter = new ListingAdapter(MyListings.this, listingList);
        rvStream.setAdapter(adapter);
        rvStream.setLayoutManager(new LinearLayoutManager(MyListings.this));

        // get all listings that are by the currently logged in user
        queryMyListings();

    }

    // get all listings that are by the currently logged in user
    private void queryMyListings() {
        // specify which class to query
        ParseQuery<Listing> query = ParseQuery.getQuery(Listing.class);

        // include data referred by user key
        query.include(Listing.KEY_USER);

        // filter by posts from the current user
        query.whereEqualTo(Listing.KEY_USER, ParseUser.getCurrentUser());

        // Limit query to last 20 listings
        query.setLimit(20);

        // order listings by creation date descending
        query.addDescendingOrder(Listing.KEY_CREATED_KEY);

        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Listing>() {
            @Override
            public void done(List<Listing> listings, ParseException e) {
                if (e != null) {
                    // log issue getting listings
                    Log.e("MyListings", "Error querying listings", e);
                    return;
                }

                if ((listings == null) || listings.size() == 0) {
                    // set views default
                    rvStream.setVisibility(View.INVISIBLE);
                    findViewById(R.id.avNoListings).setVisibility(View.VISIBLE);
                    findViewById(R.id.tvNoListings).setVisibility(View.VISIBLE);
                }

                // add listings to recyclerview and bind
                listingList.addAll(listings);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
