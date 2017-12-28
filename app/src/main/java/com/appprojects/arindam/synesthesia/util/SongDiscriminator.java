package com.appprojects.arindam.synesthesia.util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author Arindam Das
 * @version 21-12-2017.
 */

public class SongDiscriminator implements Runnable {

    private List<Song> songs;
    private int metadataKey;
    private boolean ready = false;

    public SongDiscriminator(List<Song> songs, int metadataKey){
        this.songs = songs; this.metadataKey = metadataKey;
    }

    public List<Song> getSongs() { return songs; }

    public boolean isReady(){ return ready; }

    @Override
    public void run() {
        HashMap<String, Song> map = new HashMap<>();
        for(Song song : songs){
            if(!map.containsKey(song.getMetadata(metadataKey)))
                map.put(song.getMetadata(metadataKey), song);
        }
        songs = new ArrayList<>();
        songs.addAll(map.values());
        ready = true;
    }
}
