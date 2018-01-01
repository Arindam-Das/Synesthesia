package com.appprojects.arindam.synesthesia;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.appprojects.arindam.synesthesia.util.recyclerui.SongFragment;

public class MainActivity extends AppCompatActivity {

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

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        Toolbar toolbar = findViewById(R.id.toolbar_album);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout =  findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab =  findViewById(R.id.fab_song_classifier);
        fab.setOnClickListener(view -> {
            //TODO : play last added songs
            Snackbar.make(view, "Play last added songs.", Snackbar.LENGTH_SHORT).show();
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_song_classifier, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.action_settings:
                startActivity(new Intent(getApplicationContext(), DirectorySetterActivity.class));
                return true;
            case R.id.refresh:
                Toast.makeText(this, "Refresh selected", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private static class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private static int NO_OF_PAGES = 4;
        private static Bundle[] arguments;

        static {
            arguments = new Bundle[NO_OF_PAGES];
            int[] mKeys = { MediaMetadataRetriever.METADATA_KEY_ARTIST,
                            MediaMetadataRetriever.METADATA_KEY_ALBUM ,
                            MediaMetadataRetriever.METADATA_KEY_GENRE ,
                            MediaMetadataRetriever.METADATA_KEY_ARTIST };

            for(int i = 0; i < NO_OF_PAGES; i++){
                arguments[i] = new Bundle();
                arguments[i].putInt("metadata_key", mKeys[i]);
                arguments[i].putBoolean("is_grid", i < NO_OF_PAGES -1);
            }
        }


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position < 0 || position > getCount())
                return null;

            SongFragment songFragment = new SongFragment();
            songFragment.setArguments(arguments[position]);
            return songFragment;
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position < 0 || position > getCount())
                return null;
            return new String[]{"Artists", "Albums", "Genre", "Songs"}[position];
        }
    }

}