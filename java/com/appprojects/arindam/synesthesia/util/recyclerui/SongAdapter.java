package com.appprojects.arindam.synesthesia.util.recyclerui;

import android.media.MediaMetadataRetriever;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.appprojects.arindam.synesthesia.R;
import com.appprojects.arindam.synesthesia.util.Song;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom {@link RecyclerView.Adapter} for our use.
 *
 * @author Arindam Das
 * @version 17-12-2017.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    /*Instance variables of class SongAdapter*/
    private List<Song> songList; // data source for list of songs
    private final ViewType viewType;
    private int metadataKey;

    private View.OnClickListener onClickListener;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    /**
     * Enum to specify the type of view to be used.
     * Each enumerate instance contains the resource id
     * for the layout to be used.
     */
    public enum ViewType {

        LIST(R.layout.thumbnailed_song_list_item, R.menu.menu_list_element),

        GRID(R.layout.song_card, R.menu.menu_card_grid_element);

        /* Instance variables of ViewType*/
        //resource id for the layout file with which the view is
        //to be inflated for different enumeration instances
        final int layoutResid;
        //resource id for the menu layout file with which to inflate
        //the context menu for the elements having this view type
        final int menuResId;

        /**
         *
         * @param layoutResId resource id for the layout being used
         * @param menuResId resource id for the menu being used
         */
        ViewType(int layoutResId, int menuResId) {
            this.layoutResid = layoutResId;
            this.menuResId = menuResId;
        }
    }

    /**
     * ViewHolder class for our RecyclerView.Adapter
     */
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        /* Instance variables of class ViewHolder. We store reference to
        *  the various widgets to be contained in the View to be held
        *  by this view holder. */
        final TextView title, metadata;
        final ImageView albumArt;
        final int menuResId;

        //monitor fo loading images
        private boolean imageLoaded = false;
        public boolean isImageLoaded() { return imageLoaded; }
        public void setImageLoaded(boolean status){ imageLoaded = status; }

        /**
         * Constructs a new instance of {@link ViewHolder}
         *
         * @param view The view to be held by this {@link ViewHolder}
         * @param type {@link ViewType} to be held.
         */
        ViewHolder(View view, ViewType type){
            super(view);
            switch (type) {
                case GRID: // grid of card views
                    title = view.findViewById(R.id.card_title);
                    metadata = null;
                    albumArt = view.findViewById(R.id.card_album_art);
                    break;
                case LIST: // simple list
                    title = view.findViewById(R.id.song_metadata_title);
                    metadata = view.findViewById(R.id.song_metadata_sub);
                    albumArt = view.findViewById(R.id.song_album_art);
                    break;
                default: // satisfy "final"
                    title = metadata = null;
                    albumArt = null;
            } menuResId = type.menuResId;
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view,
                                        ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuInflater inflater = new MenuInflater(view.getContext());
            inflater.inflate(this.menuResId, contextMenu);
        }


    }

    private SparseBooleanArray imageLoadedAtposition;

    /**
     * Constructs a new instance of SongAdapter.
     *
     * @param songs songs data-set
     * @param type type of view {@link ViewType} to use.
     * @param metadataKey key for the metadata to be used for inflating views
     */
    public SongAdapter(List<Song> songs, ViewType type, int metadataKey){
        this.songList = songs; this.viewType = type;
        this.metadataKey = metadataKey;
        imageLoadedAtposition = new SparseBooleanArray();
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ViewHolder, int)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.viewType.layoutResid, parent, false);
        return new ViewHolder(view, this.viewType);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Song song = this.songList.get(position);
        switch (this.viewType) {
            case LIST:
                holder.title.setText(song.getTitle());
                assert holder.metadata != null;
                holder.metadata.setText(song.getMetadata(this.metadataKey));
                break;
            case GRID:
                holder.title.setText(song.getMetadata(metadataKey));
                //if(imageLoadedAtposition.get(position, false)) break;
                MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
                metadataRetriever.setDataSource(song.getPath());
                byte[] image = metadataRetriever.getEmbeddedPicture();
                //imageLoadedAtposition.put(position, true);
                if(image == null || holder.albumArt == null) break;
                holder.albumArt.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(holder.itemView)
                        .asBitmap()
                        .load(image)
                        .apply(new RequestOptions().placeholder(R.drawable.synesthesia_logo)
                                .diskCacheStrategy(DiskCacheStrategy.ALL))
                        .thumbnail(1.0f)
                        .into(holder.albumArt);
                break;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            /**
             * @param view view that was clicked
             */
            @Override
            public void onClick(View view) {
                setPosition(holder.getAdapterPosition());
                Snackbar.make(view, getSongList().get(getPosition()).getTitle()+
                        " was selected.", Snackbar.LENGTH_SHORT).show();
                onClickListener.onClick(view);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            /**
             * @param view the view that was clicked and held
             * @return false if the event was handled here and no other
             * listeners are necessary, true otherwise
             */
            @Override
            public boolean onLongClick(View view) {
                onClickListener.onClick(view);
                setPosition(holder.getAdapterPosition());
                return false;
            }
        });
    }


    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return this.songList.size();
    }

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    public List<Song> getSongList() { return songList; }


}
