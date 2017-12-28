package com.appprojects.arindam.synesthesia.util.listui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appprojects.arindam.synesthesia.R;
import com.appprojects.arindam.synesthesia.util.Song;

import java.util.List;
import static android.media.MediaMetadataRetriever.*;

/**
 * Adapter class for {@link android.widget.ListView}(s) containing instances of {@link Song}
 * @author Arindam Das
 * @version 22-10-2017.
 */

public class SongElementAdapter extends ArrayAdapter<Song> {

    private int metadataKey;

    public SongElementAdapter(@NonNull Context context, @LayoutRes int resource,
                              @NonNull List<Song> objects) {
        super(context, resource, objects);
        metadataKey = METADATA_KEY_ARTIST;
    }

    public void setMetadataKey(int metadataKey) { this.metadataKey = metadataKey; }

    @NonNull @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.thumbnailed_song_list_item, parent, false);
        }

        Song song = getItem(position);
        if(song == null) return convertView;
        ImageView albumArt = convertView.findViewById(R.id.song_album_art);

        TextView textViewTitle = convertView.findViewById(R.id.song_metadata_title);
        textViewTitle.setText(song.getTitle());
        TextView textViewSub = convertView.findViewById(R.id.song_metadata_sub);
        textViewSub.setText(song.getMetadata(metadataKey));

        return convertView;
    }
}
