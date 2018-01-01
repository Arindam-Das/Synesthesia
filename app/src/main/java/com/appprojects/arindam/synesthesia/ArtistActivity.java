package com.appprojects.arindam.synesthesia;

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

import com.appprojects.arindam.synesthesia.util.recyclerui.SongFragment;

import static com.appprojects.arindam.synesthesia.util.Song.*;

public class ArtistActivity extends AppCompatActivity {

    public static final String INTENT_KEY = "@artist_activity";

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
    private static String query,
    //title of the appbar: this is set to the album name
    title; //as requested as requested in the query

    private String getQuery(){
        if(getIntent() != null){
            query = getIntent()
                    .getStringExtra(INTENT_KEY);
            title = query.split(QUERY_DELIMITER)[1];
        } return query;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        getQuery();



        Toolbar toolbar = findViewById(R.id.toolbar_album);
        toolbar.setTitle(title);
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
    public static class SectionsPagerAdapter extends FragmentPagerAdapter {

        private static int NO_OF_PAGES = 2;
        private static Bundle[] arguments;

        static {
            arguments = new Bundle[NO_OF_PAGES];
            int[] mKeys = {
                    MediaMetadataRetriever.METADATA_KEY_ALBUM ,
                    MediaMetadataRetriever.METADATA_KEY_ARTIST };

            for(int i = 0; i < NO_OF_PAGES; i++){
                arguments[i] = new Bundle();
                arguments[i].putInt("metadata_key", mKeys[i]);
                arguments[i].putBoolean("is_grid", i < NO_OF_PAGES-1);
            }
        }

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position < 0 || position >= getCount())
                return null;
            SongFragment songFragment = new SongFragment();
            arguments[position].putString("query", query);
            songFragment.setArguments(arguments[position]);
            return songFragment;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return NO_OF_PAGES;
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
}
