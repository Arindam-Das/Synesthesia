package com.appprojects.arindam.synesthesia.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.widget.Toast;

import static android.media.MediaMetadataRetriever.*;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.File;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * A data class for the {@link android.widget.ListView} in the different {@link android.app.Fragment}
 * As evident from their implementation, they already store the only required metadata linked
 * with a particular media file. The {@link android.widget.ArrayAdapter} sub-class only
 * needs to extract the metadata directly and render it as dictated by he layout of the list element.
 * @author Arindam Das
 * @version 22-10-2017.
 */

@DatabaseTable(tableName = "songs")
public class Song implements Serializable{

    /*Static constants for field names in the ORM database */
    public static final String ALBUM_FIELD_NAME = "album";
    public static final String ARTIST_FIELD_NAME = "artist";
    public static final String GENRE_FIELD_NAME = "genre";
    public static final String TITLE_FIELD_NAME = "title";


    /*Instance variables*/

    @DatabaseField(id = true, unique = true, canBeNull = false)
    private String path;

    @DatabaseField(canBeNull = false, useGetSet = true,
            columnName = ALBUM_FIELD_NAME)
    private String album;

    @DatabaseField(canBeNull = false, useGetSet = true,
            columnName = ARTIST_FIELD_NAME)
    private String artist;

    @DatabaseField(canBeNull = false, useGetSet = true,
            index = true, columnName = TITLE_FIELD_NAME)
    private String title;

    @DatabaseField(canBeNull = false, useGetSet = true,
            columnName = GENRE_FIELD_NAME)
    private String genre;

    public Song(){} /*no-argument constructor for orm-lite*/

    @Override
    public String toString() {
        return "{\n\tPath:"+path+",\n\tTitle : "+title+" \n}";
    }

    public Song(File file){
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(file.getAbsolutePath());

        this.path = file.getAbsolutePath();

        this.title = metadataRetriever.extractMetadata(METADATA_KEY_TITLE);//7
        if(title == null){
            title = file.getName();
            title = title.substring(0, title.lastIndexOf('.'));
        }

        this.album = metadataRetriever.extractMetadata(METADATA_KEY_ALBUM);//1
        if(this.album == null) this.album = "Unknown album";

        this.genre = metadataRetriever.extractMetadata(METADATA_KEY_GENRE);//6
        if(this.genre == null) this.genre = "Unknown genre";

        this.artist = metadataRetriever.extractMetadata(METADATA_KEY_ARTIST);//2
        if(this.artist == null) this.artist = "Unknown artist";


    }

    public String getAlbum() { return album; }

    public String getArtist(){ return artist; }

    public String getGenre() { return genre;}

    public String getTitle() { return title;}

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMetadata(int key){ return getMetadata()[(key/4 + key%4 - 1)%4]; }

    public String[] getMetadata(){ return new String[]{ album, artist, genre, title }; }

    public boolean matchesQuery(String[] queryArgs){
        String[] metadata = getMetadata();
        for(int i = 0; i < metadata.length; i++){
            if(queryArgs[i].equals("*")) continue;
            if(!queryArgs[i].equalsIgnoreCase(metadata[i]))
                return false;
        } return true;
    }

    public String getPath() { return path; }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException { out.defaultWriteObject(); }

    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException{ in.defaultReadObject(); }

    private void readObjectNoData() throws ObjectStreamException {}
}
