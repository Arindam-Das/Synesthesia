package com.appprojects.arindam.synesthesia.util;

import com.j256.ormlite.dao.Dao;

import java.io.File;
import java.io.FileFilter;
import java.security.InvalidAlgorithmParameterException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Set;

/**
 * <p>Utility class to recursively search for files on the basis of a {@link java.io.FileFilter} </p>
 * @author Arindam Das
 * @version 20-10-2017.
 */



public class SongCollector implements Runnable{

    public static final String[] SONG_EXTENSIONS = {".mp3", ".mp4", ".m4a", ".3gp", ".wav"};

    //public interface Task{ void doTask(); }

    /**
     * Root of the file tree to be searched for files according to
     * the given search criterion. Initially it refers to the main root directory
     */
    private Set<File> directories = new java.util.TreeSet<>();

    /* Task to be done after collecting all songs. */
    private Task task;

    public Set<File> getDirectories() { return directories; }

    /**
     * Sets the root of the directory tree to be searched.
     * @param dirPath string instance representing the path of a new 'root' director
     * @throws InvalidAlgorithmParameterException when the root is not a directory
     */
    public void addSearchDirectory(String dirPath) throws InvalidAlgorithmParameterException{
        File root = new File(dirPath);
        if(!root.isDirectory()){
            throw new InvalidAlgorithmParameterException("Invalid root directory path",
                    new Throwable("Given path doesn't represent a directory."));
        }
        this.directories.add(root);
    }


    public void setTaskToBeDoneOnCompletion(Task task){
        this.task = task;
    }

    private SongDatabaseHelper databaseHelper;

    private FileFilter isSong = file -> {
        String path = file.getAbsolutePath();
        for(String extension : SONG_EXTENSIONS)
        {if(path.endsWith(extension)) return true;}
        return false;
    };

    /**
     * Constructs a new instance of song collector.
     * @param helper SongDatabaseHelper for accessing the ORMSQLiteDatabase <br>
     *               <p>Note: The SongDatabaseHelper is automatically closed when
     *               the SongCollector is run on any thread. </p>
     */
    public SongCollector(SongDatabaseHelper helper){
        this.databaseHelper = helper;
    }

    public ArrayList<Song> collectSongs(){
        Deque<File> files = new LinkedList<>();
        ArrayList<Song> filteredSongs = new ArrayList<>();

        files.addAll(getDirectories());
        while(!files.isEmpty()){
            File file = files.removeFirst();
            if(file.isDirectory()){
                for(File f : file.listFiles()) files.addLast(f);
            }else{
                if(isSong.accept(file))
                    filteredSongs.add(new Song(file));
            }
        }
        return filteredSongs;
    }

    private ArrayList<File> collectSongFiles(){
        Deque<File> files = new LinkedList<>();
        ArrayList<File> filteredSongs = new ArrayList<>();

        files.addAll(getDirectories());
        while(!files.isEmpty()){
            File file = files.removeFirst();
            if(file.isDirectory()){
                for(File f : file.listFiles()) files.addLast(f);
            }else{
                if(isSong.accept(file))
                    filteredSongs.add(file);
            }
        }

        return filteredSongs;
    }

    @Override
    public void run() {

        //collect all songs in the filteredSong list
        ArrayList<File> filteredSongs = collectSongFiles();

        try {
            //obtain Dao from the SongDatabaseHelper instance
            Dao<Song, String> dao = databaseHelper.getDao();
            for(File file : filteredSongs){
                //add each song to the data through the dao
                if(dao.queryForId(file.getPath()) == null)
                    dao.create(new Song(file));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
            databaseHelper.close();
        }

        task.doTask();
    }
}
