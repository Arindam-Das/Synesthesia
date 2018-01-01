package com.appprojects.arindam.synesthesia.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * <p>
 * Utility to class to classify songs on the device on the basis of their metadata.
 * The classified files are stored as a map with the metadata mapped
 * to a {@link java.util.List} containing the corresponding files.
 * </p>
 * @author Arindam Das
 * @version 20-10-2017.
 */

public class SongClassifier {

    /**
     * <p> Classifies songs with respect to a particular metadata and returns the result as
     * a mapping from string metadata to the list of songs having that metadata </p>
     * @param songs The collection of songs to be classified.
     * @param keyCode The key for the metadata to be used.
     * @return The {@link Map} containing the mapping from string metadata
     * to the list of songs having that metadata
     */
    public static Map<String, List<Song>> classify(Collection<Song> songs,
                                                             int keyCode){
        Map<String, List<Song>> stringFileMap = new ConcurrentSkipListMap<>(String::compareTo);
        for(Song song : songs){
            String metadata = song.getMetadata(keyCode);
            if (!stringFileMap.containsKey(metadata))
                stringFileMap.put(metadata, new LinkedList<>());
            else stringFileMap.get(metadata).add(song);
        } return stringFileMap;
    }
}
