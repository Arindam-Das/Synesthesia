package com.appprojects.arindam.synesthesia.util.recyclerui;

import android.app.Activity;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.appprojects.arindam.synesthesia.R;
import com.appprojects.arindam.synesthesia.util.Song;
import com.appprojects.arindam.synesthesia.util.SongDatabaseHelper;
import com.appprojects.arindam.synesthesia.util.SongDiscriminator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.gpu.VignetteFilterTransformation;

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
    //album art for the collected songs
    private Bitmap albumArt;
    //modifiable imageview
    private ImageView imageView;

    private boolean hasLayoutManager;

    public boolean isRacist() {
        return isRacist;
    }

    private boolean isRacist;

    private View.OnClickListener onClickListener = this::doNothingWithView;

    private void doNothingWithView(View view) { }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public SongAdapter getSongAdapter() { return songAdapter; }

    public void beRacist(){ isRacist = true; }

    public void setImageView(ImageView imageView){ this.imageView = imageView; }

    public void setQuery(String query){ this.query = query; }

    public String[] getQueryArgs(){
        return (query == null)?
                new String[]{"*","*","*","*"}:
                this.query.split(",");
    }

    public Bitmap getAlbumArt(){ return  albumArt; }

    public SongAdapter.ViewType getViewType() { return viewType; }

    public void setViewType(SongAdapter.ViewType viewType) { this.viewType = viewType; }

    public List<Song> getSongList() { return songList; }

    public void setSongList(List<Song> songList) { this.songList = songList; }

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

        switch (getViewType()) {
            case GRID:
                this.layoutManager = new GridLayoutManager(getContext(),
                        2,
                        GridLayoutManager.VERTICAL,
                        false); break;

            case LIST:
                this.layoutManager = new LinearLayoutManager(getContext());
        }

        filterSongs();

        if(isRacist) { discriminate(); }

        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(songList.get(0).getPath());
        byte[] image = mediaMetadataRetriever.getEmbeddedPicture();
        if(image == null) return;
        this.albumArt = BitmapFactory.decodeByteArray(image, 0, image.length);
        if(this.imageView != null){
            this.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            this.imageView.setImageBitmap(albumArt);
        }
    }

    private ArrayList<Song> collectAllSongs() {
        boolean error_occurred = true;
        List<Song> songs = new ArrayList<>();
        while(error_occurred) {
            try {
                songs = getSongDatabaseHelper().getDao().queryForAll();
                error_occurred = false;
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return (ArrayList<Song>) songs;
    }

    private void filterSongs() {
        this.songList = new ArrayList<>();
        String[] queryArgs = getQueryArgs();
        for(Song song : collectAllSongs()){
            if(song.matchesQuery(queryArgs))
                this.songList.add(song);
        }
    }

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
        if(!hasLayoutManager)
            recyclerView.setLayoutManager(this.layoutManager);

        hasLayoutManager = true;
        songAdapter = new SongAdapter(this.getSongList(),
                this.getViewType(), getMetaDataKey());
        songAdapter.setOnClickListener(this.onClickListener);

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

    private void close(){

        Toast.makeText(getContext(),
                "NNNOOOOOOOOOOOOOO...",
                Toast.LENGTH_SHORT).show();

        if(songDatabaseHelper != null) {
            songDatabaseHelper.close();
            songDatabaseHelper = null;
            this.songList = null;
        }
    }

    /**
     * Called when the Fragment is no longer resumed.  This is generally
     * tied to {@link Activity#onPause() Activity.onPause} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onPause() {
        System.gc();
        super.onPause();
    }

    private SongDatabaseHelper songDatabaseHelper = null;

    public SongDatabaseHelper getSongDatabaseHelper() {
        if(songDatabaseHelper == null){
            songDatabaseHelper = SongDatabaseHelper.getHelper(getContext());
        }
        return songDatabaseHelper;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = new MenuInflater(getContext());
        inflater.inflate(this.getViewType().menuResId, menu);
    }




}
