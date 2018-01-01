package com.appprojects.arindam.synesthesia.util.recyclerui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.appprojects.arindam.synesthesia.ArtistActivity;
import com.appprojects.arindam.synesthesia.ListingActivity;
import com.appprojects.arindam.synesthesia.R;
import com.appprojects.arindam.synesthesia.util.Song;
import com.appprojects.arindam.synesthesia.util.SongDatabaseHelper;
import com.appprojects.arindam.synesthesia.util.SongDiscriminator;
import com.appprojects.arindam.synesthesia.util.StringJoiner;
import com.appprojects.arindam.synesthesia.util.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to contain RecyclerView(s).
 * @author Arindam Das
 * @version 17-12-2017.
 */

public class SongFragment extends Fragment {

    /* Instance variables of class SongFragment */
    //recycler view to be contained within this fragment
    private RecyclerView recyclerView;
    // ViewType for the SongAdapter
    private SongAdapter.ViewType viewType = SongAdapter.ViewType.LIST;
    //list of songs for song adapter
    private List<Song> songList = null;
    //layout manager for our fragment
    private RecyclerView.LayoutManager layoutManager;
    // metadata key for inflating the child views in the RecyclerView
    private int metaDataKey = MediaMetadataRetriever.METADATA_KEY_ARTIST; //default: artist
    //RecyclerView.Adapter child SongAdapter for our RecyclerView
    private SongAdapter songAdapter;
    //query string
    private String query;

    //modifiable ImageView
    private ImageView imageView;
    //whether or not to discriminate songs
    private boolean isRacist;



    public SongAdapter getSongAdapter() { return songAdapter; }

    public void beRacist(){ isRacist = true; }

    public void setImageView(ImageView imageView){ this.imageView = imageView; }

    public void setQuery(String query){ this.query = query; }

    public String[] getQueryArgs(){
        return (query == null)?
                new String[]{"*","*","*","*"}:
                this.query.split(Song.QUERY_DELIMITER);
    }

    public SongAdapter.ViewType getViewType() { return viewType; }

    public void setViewType(SongAdapter.ViewType viewType) { this.viewType = viewType; }

    public List<Song> getSongList() { return songList; }

    public int getMetaDataKey() { return metaDataKey; }

    public void setMetaDataKey(int metaDataKey) { this.metaDataKey = metaDataKey; }

    public RecyclerView getRecyclerView() { return recyclerView; }


    /**
     * Called to do initial creation of a fragment.  This is called after
     * {@link #onAttach(android.app.Activity)} and before
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * <p>
     * <p>Note that this can be called while the fragment's activity is
     * still in the process of being created.  As such, you can not rely
     * on things like the activity's content view hierarchy being initialized
     * at this point.  If you want to do work once the activity itself is
     * created, see {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>Any restored child fragments will be created before the base
     * <code>Fragment.onCreate</code> method returns.</p>
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null)
            onViewStateRestored(getArguments());

        filterSongs();
        if(isRacist) { discriminate(); }

        setRetainInstance(true);

        songAdapter = new SongAdapter(this.getSongList(),
                this.getViewType(), getMetaDataKey());

        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(songList.get(0).getPath());
        byte[] image = mediaMetadataRetriever.getEmbeddedPicture();
        if(image == null) return;
        Bitmap albumArt = BitmapFactory.decodeByteArray(image, 0, image.length);
        if(this.imageView != null){
            this.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            this.imageView.setImageBitmap(albumArt);
        }
    }

    /**
     * Called when all saved state has been restored into the view hierarchy
     * of the fragment.  This can be used to do initialization based on saved
     * state that you are letting the view hierarchy track itself, such as
     * whether check box widgets are currently checked.  This is called
     * after {@link #onActivityCreated(Bundle)} and before
     * {@link #onStart()}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState != null){
            setQuery(savedInstanceState.getString("query", null));
            setMetaDataKey(savedInstanceState.getInt("metadata_key", 1));
            boolean isGrid = savedInstanceState.getBoolean("is_grid", false);
            setViewType(isGrid? SongAdapter.ViewType.GRID: SongAdapter.ViewType.LIST);
            if(isGrid) beRacist();
            if(recyclerView != null)
                registerForContextMenu(recyclerView);
        }
    }

    /**
     * Called to ask the fragment to save its current dynamic state, so it
     * can later be reconstructed in a new instance of its process is
     * restarted.  If a new instance of the fragment later needs to be
     * created, the data you place in the Bundle here will be available
     * in the Bundle given to {@link #onCreate(Bundle)},
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}, and
     * {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>This corresponds to {@link Activity#onSaveInstanceState(Bundle)
     * Activity.onSaveInstanceState(Bundle)} and most of the discussion there
     * applies here as well.  Note however: <em>this method may be called
     * at any time before {@link #onDestroy()}</em>.  There are many situations
     * where a fragment may be mostly torn down (such as when placed on the
     * back stack with no UI showing), but its state will not be saved until
     * its owning activity actually needs to save its state.
     *
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("query", query);
        outState.putInt("metadata_key", getMetaDataKey());
        outState.putBoolean("is_grid", getViewType() == SongAdapter.ViewType.GRID);
    }

    /**
     * Collect all songs from the DAO accessible from the SongDatabaseHelper.
     * @return the {@link List} of collected songs.
     */
    private List<Song> collectAllSongs(SongDatabaseHelper songDatabaseHelper) {
        boolean error_occurred = true;
        List<Song> songs = new ArrayList<>();
        while(error_occurred) {
            try {
                songs = songDatabaseHelper.getDao().queryForAll();
                error_occurred = false;
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return songs;
    }

    /**
     * Filter out songs from the collected songs
     * with the provided query arguments.
     */
    private void filterSongs() {
        this.songList = new ArrayList<>();
        String[] queryArgs = getQueryArgs();
        for(Song song : collectAllSongs(getSongDatabaseHelper())){
            if(song.matchesQuery(queryArgs))
                this.songList.add(song);
        }
    }

    /**
     * Discriminate among the filtered songs using a SongDiscriminator
     * to reduce the list of songs to a unique set of songs according to a particular metadata.
     */
    private void discriminate() {
        SongDiscriminator songDiscriminator = new SongDiscriminator(this.getSongList(),
                this.getMetaDataKey());
        songDiscriminator.run();
        this.songList = songDiscriminator.getSongs();
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.frag_recycler, container, false);

        recyclerView = rootView.findViewById(R.id.recycler_view);
        assert container != null;
        int spanCount = Resources.getSystem().getDisplayMetrics().widthPixels / 240;
        switch (getViewType()) {
            case GRID:
                this.layoutManager = new GridLayoutManager(getContext(),
                        spanCount,
                        GridLayoutManager.VERTICAL,
                        false); break;

            case LIST:
                this.layoutManager = new LinearLayoutManager(getContext());
        }
        recyclerView.setLayoutManager(layoutManager);

        if(recyclerView.getAdapter() == null)
            recyclerView.setAdapter(songAdapter); //attach the adapter to our RecyclerView
        registerForContextMenu(recyclerView);
        return rootView;
    }

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        close();
        super.onDestroy();
    }

    /**
     * Close the handle to the database.
     */
    private void close(){
        if(songDatabaseHelper != null) {
            songDatabaseHelper.close();
            songDatabaseHelper = null;
            this.songList = null;
        }
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * tied to {@link Activity#onResume() Activity.onResume} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        registerForContextMenu(getRecyclerView());
        super.onResume();
    }

    /**
     * Called when the Fragment is no longer resumed.  This is generally
     * tied to {@link Activity#onPause() Activity.onPause} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onPause() {
        unregisterForContextMenu(getRecyclerView());
        super.onPause();
    }

    /* Handle database operations*/

    private SongDatabaseHelper songDatabaseHelper = null;

    /**
     * Returns a new or a cached version of a {@link SongDatabaseHelper}
     * @return the {@link SongDatabaseHelper} being used.
     */
    public SongDatabaseHelper getSongDatabaseHelper() {
        if(songDatabaseHelper == null) {
            songDatabaseHelper = SongDatabaseHelper.getHelper(getContext());
        } return songDatabaseHelper;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = new MenuInflater(getContext());
        inflater.inflate(this.getViewType().menuResId, menu);
    }


    /**
     * This hook is called whenever an item in a context menu is selected. The
     * default implementation simply returns false to have the normal processing
     * happen (calling the item's Runnable or sending a message to its Handler
     * as appropriate). You can use this method for any items for which you
     * would like to do processing without those other facilities.
     * <p>
     * Use {@link MenuItem#getMenuInfo()} to get extra information set by the
     * View that added this menu item.
     * <p>
     * Derived classes should call through to the base class for it to perform
     * the default menu handling.
     *
     * @param item The context menu item that was selected.
     * @return boolean Return false to allow normal context menu processing to
     * proceed, true to consume it here.
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = songAdapter.getPosition();
        if(!getUserVisibleHint()) return false;
        switch (item.getItemId()) {

            case R.id.add_to_playlist:
            case R.id.add_to_queue:
            case R.id.set_as_ringtone:
            case R.id.c_add_to_playlist:
            case R.id.c_add_to_queue:
                Toast.makeText(getContext(), item.getTitle()+" applied to "+
                        getSongList().get(position).getTitle(), Toast.LENGTH_SHORT).show();
                return true;

            case R.id.c_go_to_cluster:
                Intent intent = null;
                StringJoiner stringJoiner = new StringJoiner(Song.QUERY_DELIMITER);
                switch(getMetaDataKey()) {

                    case MediaMetadataRetriever.METADATA_KEY_ALBUM:
                        stringJoiner.reset();
                        String artist = query == null? "*":
                                getSongList().get(position).getArtist();
                        stringJoiner.add(getSongList().get(position).getAlbum())
                                .add(artist).add("*").add("*");
                        intent = new Intent(getContext(), ListingActivity.class);
                        intent.putExtra(ListingActivity.INTENT_KEY, stringJoiner.toString());
                        break;

                    case MediaMetadataRetriever.METADATA_KEY_ARTIST:
                        stringJoiner.reset();
                        stringJoiner.add("*").add(getSongList().get(position).getArtist())
                                .add("*").add("*");
                        intent = new Intent(getContext(), ArtistActivity.class);
                        intent.putExtra(ArtistActivity.INTENT_KEY, stringJoiner.toString());
                        break;

                    case MediaMetadataRetriever.METADATA_KEY_GENRE:
                        stringJoiner.reset();
                        stringJoiner.add("*").add("*")
                                .add(getSongList().get(position).getGenre()).add("*");
                        intent = new Intent(getContext(), ListingActivity.class);
                        intent.putExtra(ListingActivity.INTENT_KEY, stringJoiner.toString());
                        break;

                } if(intent != null &&
                        getViewType() == SongAdapter.ViewType.GRID) {
                    assert getActivity() != null;
                    getActivity().startActivity(intent);
                } return true;
            default:
                return false;
        }
    }
}


