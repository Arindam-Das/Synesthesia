package com.appprojects.arindam.synesthesia.util;

import android.media.MediaMetadataRetriever;

import java.io.File;
import java.io.FileFilter;
import java.security.InvalidAlgorithmParameterException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * <p>
 * Utility to class to classify songs on the device on the basis of their metadata.
 * Instances of this class use a {@link FileCollector} instance to collect
 * all media files on this device via the {@link FileCollector#collectFilesBy(FileFilter)}
 * method and then classifies them by retrieving their metadata with a
 * {@link android.media.MediaMetadataRetriever} instance. The classified files are stored
 * as a map with the metadata mapped to a {@link java.util.List} containing the corresponding
 * files.
 * </p>
 * @author Arindam Das
 * @version 20-10-2017.
 */

public class SongClassifier {

    public static final String[] SONG_EXTENSIONS = {".mp3", ".mp4", ".m4a", ".3gp", ".wav"};

    private static Map<String, List<Song>> classifySongsFrom(Collection<File> files,
                                                             int keyCode){
        Map<String, List<Song>> stringFileMap = new ConcurrentSkipListMap<>(
                new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return s.compareTo(t1);
            }
        });
        for(File file : files){
            MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
            metadataRetriever.setDataSource(file.getAbsolutePath());
            String metadata = metadataRetriever.extractMetadata(keyCode);
            metadata = metadata == null ? "Unknown" : metadata;
            if (!stringFileMap.containsKey(metadata))
                stringFileMap.put(metadata, new LinkedList<Song>());
            else stringFileMap.get(metadata).add(new Song(file));
        }
        return stringFileMap;
    }

    public static Map<String, List<Song>> classifySongsFrom(String path, int keyCode) {
        try{
            FileCollector fileCollector = new FileCollector().setRoot(new File(path));
            return classifySongsFrom(fileCollector.collectFilesBy(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    String path = file.getAbsolutePath();
                    for(String extension : SONG_EXTENSIONS){
                        if(path.endsWith(extension)){ return true; }
                    } return false;
                }
            }), keyCode);
        }
        catch (InvalidAlgorithmParameterException e){
            e.printStackTrace();
        }
        return null;
    }

    public static Map<Character, Map<String, List<Song>>> mapLexicographically(
            Map<String, List<Song>> stringFileMap){
        SortedMap<Character, Map<String, List<Song>>> charDataMap =
                new ConcurrentSkipListMap<>(new Comparator<Character>() {
                    @Override
                    public int compare(Character character, Character t1) {
                        return character - t1;
                    }
                });
        for(String key : stringFileMap.keySet()){
            char cKey = (key.contains("Unknown"))?'#':Character.toUpperCase(key.charAt(0));
            if(!charDataMap.containsKey(cKey)) charDataMap.put(cKey, new HashMap<String, List<Song>>());
            charDataMap.get(cKey).put(key, stringFileMap.get(key));
        }
        return charDataMap;
    }

}
