package com.appprojects.arindam.synesthesia;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;

import com.appprojects.arindam.synesthesia.util.recyclerui.SongAdapter;
import com.appprojects.arindam.synesthesia.util.recyclerui.SongFragment;

public class ArtistActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    //query to be passed on to the songFragment to collect the
    //collect the songs from the dao on initialization in the songFragment
    private String query,
    //title of the appbar: this is set to the album name
    title; //as requested as requested in the query

    private SongFragment songFragment;

    private String getQuery(){
        if(query == null){
            query = getIntent()
                    .getStringExtra("@artist_activity");
            title = query.split(",")[1];
        } return query;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        getQuery();



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_album);
        toolbar.setTitle(this.title);
        setSupportActionBar(toolbar);


        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)actionBar.setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_artist);
        fab.setOnClickListener(view -> {
            //TODO: play all songs from this artist
            Snackbar.make(view, "Play all songs from this artist.", Snackbar.LENGTH_SHORT).show();
        });

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SongFragment albums, songs, current;

        private void initialize(){
            if(albums == null){
                albums = new SongFragment();
                albums.setViewType(SongAdapter.ViewType.GRID);
                albums.setQuery(getQuery());
                albums.beRacist();
                albums.setOnClickListener(view -> current = albums);
                albums.setMetaDataKey(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            }
            if(songs == null){
                songs = new SongFragment();
                songs.setQuery(getQuery());
                songs.setOnClickListener(view -> current = songs);
            }
        }

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm); initialize();
        }

        public SongFragment getCurrentPage() { return current; }

        @Override
        public Fragment getItem(int position) {
            //initialize();
            switch (position){
                case 0: return albums;
                case 1: return songs;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Albums";
                case 1:
                    return "Songs";
            }
            return null;
        }


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        SongAdapter currentSongAdapter = this.mSectionsPagerAdapter
                .getCurrentPage().getSongAdapter();
        int position = currentSongAdapter.getPosition();

        Toast.makeText(this, ""+position, Toast.LENGTH_SHORT).show();

        switch (item.getItemId()) {
            case R.id.add_to_playlist:
            case R.id.add_to_queue:
            case R.id.set_as_ringtone:
            case R.id.c_add_to_playlist:
            case R.id.c_add_to_queue:
                Toast.makeText(this, item.getTitle()+" applied to "+
                        currentSongAdapter.getSongList().get(position).getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.c_go_to_cluster:
                Intent intent = null;
                switch (this.mSectionsPagerAdapter.current.getMetaDataKey()){
                    case MediaMetadataRetriever.METADATA_KEY_ALBUM:
                        intent = new Intent(this, ListingActivity.class);
                        intent.putExtra("@album_activity",
                                currentSongAdapter.getSongList().get(position).getAlbum()+"," +
                                        currentSongAdapter.getSongList().get(position).getArtist()+",*,*");
                        break;
                } if(intent != null) startActivity(intent);
                return true;

            default:
                return false;
        }
    }
}
