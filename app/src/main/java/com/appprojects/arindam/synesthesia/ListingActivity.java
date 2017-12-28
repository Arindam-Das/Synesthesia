package com.appprojects.arindam.synesthesia;

import android.app.Activity;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.appprojects.arindam.synesthesia.util.SongDatabaseHelper;
import com.appprojects.arindam.synesthesia.util.Song;
import com.appprojects.arindam.synesthesia.util.recyclerui.SongAdapter;
import com.appprojects.arindam.synesthesia.util.recyclerui.SongFragment;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ListingActivity extends AppCompatActivity {

    //Fragment to host the RecyclerView for the album activity
    private SongFragment songFragment;
    //query to be passed on to the songFragment to collect the
    //collect the songs from the dao on initialization in the songFragment
    private String query,
    //title of the appbar: this is set to the album name
    title; //as requested as requested in the query

    private String getQuery(){
        if(query == null){
            query = getIntent()
                    .getStringExtra("@album_activity");
            String[] arr = query.split(",");
            title = arr[0].equals("*")?arr[2]:arr[0];
        } return query;
    }

    private SongFragment getSongFragment(){
        if(songFragment == null){
            this.songFragment = new SongFragment();
            songFragment.setQuery(getQuery());
        } return songFragment;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        ImageView imageView = findViewById(R.id.album_album_art);

        if(savedInstanceState == null){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                    .beginTransaction();
            getSongFragment().setImageView(imageView);
            fragmentTransaction.replace(R.id.frame_layout_album, getSongFragment());
            fragmentTransaction.commit();
        }

        FloatingActionButton floatingActionButton = findViewById(R.id.fab_album);
        floatingActionButton.setOnClickListener(view -> Snackbar.make(view, "Play all songs in the list.", Snackbar.LENGTH_SHORT).show());

        Toolbar toolbar = findViewById(R.id.toolbar_album);
        toolbar.setTitle(this.title);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // do something useful
                Toast.makeText(this, "Up button", Toast.LENGTH_SHORT).show();
                finish();
                return(true);
        }

        return(super.onOptionsItemSelected(item));
    }

}
