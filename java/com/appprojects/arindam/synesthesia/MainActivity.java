package com.appprojects.arindam.synesthesia;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.appprojects.arindam.synesthesia.util.recyclerui.SongAdapter;
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

    private float convertDPToPixels(int dp) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float logicalDensity = metrics.density;
        return dp * logicalDensity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        Toolbar toolbar = findViewById(R.id.toolbar_album);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout =  findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab =  findViewById(R.id.fab_song_classifier);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO : play last added songs
                Intent intent = new Intent(getApplicationContext(), ArtistActivity.class);
                intent.putExtra("@artist_activity", "*,Ed Sheeran,*,*,*");
                startActivity(intent);
            }
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
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private SongFragment[] pages;
        private SongFragment current;

        public SongFragment getCurrentPage() { return current; }


        private void initialize(){
            if(pages == null)
                { pages = new SongFragment[getCount()]; }
            for(int i = 0; i < pages.length; i++) {
                pages[i] = new SongFragment();
                final SongFragment page = pages[i];
                pages[i].setOnClickListener(view -> current = page);
                pages[i].setMetaDataKey(new int[]{
                        MediaMetadataRetriever.METADATA_KEY_ARTIST,
                        MediaMetadataRetriever.METADATA_KEY_ALBUM,
                        MediaMetadataRetriever.METADATA_KEY_TITLE
                }[i]); pages[i].setQuery(null);
                pages[i].setViewType(SongAdapter.ViewType.GRID);
                if(i < pages.length-1)pages[i].beRacist();
                //pages[i].collectSongs();
                //if(pages[i].isRacist())pages[i].discriminate();
            }
            pages[pages.length - 1].setViewType(SongAdapter.ViewType.LIST);
        }

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm); //initialize();
        }

        @Override
        public Fragment getItem(int position) {
            initialize();
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position < 0 || position > getCount())
                return null;

            return pages[position];
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position < 0 || position > getCount())
                return null;
            return new String[]{"Artists", "Albums", "Songs"}[position];
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
                    case MediaMetadataRetriever.METADATA_KEY_ARTIST:
                        intent = new Intent(this, ArtistActivity.class);
                        intent.putExtra("@artist_activity",
                                "*,"+currentSongAdapter.getSongList().get(position).getArtist()+",*,*");
                        break;
                    case MediaMetadataRetriever.METADATA_KEY_ALBUM:
                        intent = new Intent(this, AlbumActivity.class);
                        intent.putExtra("@album_activity",
                                currentSongAdapter.getSongList().get(position).getAlbum()+",*,*,*");
                        break;
                } if(intent != null) startActivity(intent);
                return true;

            default:
                return false;
        }
    }

}
