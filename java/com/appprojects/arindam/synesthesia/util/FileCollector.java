package com.appprojects.arindam.synesthesia.util;

import java.io.File;
import java.io.FileFilter;
import java.security.InvalidAlgorithmParameterException;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;

/**
 * <p>Utility class to recursively search for files on the basis of a {@link java.io.FileFilter} </p>
 * @author Arindam Das
 * @version 20-10-2017.
 */

public class FileCollector{

    /**
     * Root of the file tree to be searched for files according to
     * the given search criterion. Initially it refers to the main root directory
     */
    private File root = File.listRoots()[0];

    public File getRoot() { return root; }

    /**
     * Sets the root of the directory tree to be searched.
     * @param root File instance representing the path of the 'root'
     * @return The modified instance of {@link FileCollector}
     * @throws InvalidAlgorithmParameterException when the root is not a directory
     */
    public FileCollector setRoot(File root) throws InvalidAlgorithmParameterException{
        if(!root.isDirectory()){
            throw new InvalidAlgorithmParameterException("Invalid root directory path",
                    new Throwable("Given path doesn't represent a directory."));
        }
        this.root = root; return this;
    }

    /**
     * Collects files as filtered by this fileFilterer from all the files
     * obtained by recursively searching the file tree.
     * @param fileFilterer {@link FileFilter} instance for filtering files
     * @return {@link Collection} of filtered files.
     */
    public Collection<File> collectFilesBy(FileFilter fileFilterer){
        Deque<File> files = new LinkedList<>();
        Collection<File> filteredFiles = new LinkedList<>();
        files.add(getRoot());
        while(!files.isEmpty()){
            File file = files.removeFirst();
            if(file.isDirectory()){
                for(File f : file.listFiles()) files.addLast(f);
            }else{
                if(fileFilterer.accept(file)) filteredFiles.add(file);
            }
        }
        return filteredFiles;
    }


}
