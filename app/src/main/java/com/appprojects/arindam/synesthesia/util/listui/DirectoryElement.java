package com.appprojects.arindam.synesthesia.util.listui;

import java.io.File;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * @author Arindam Das
 * @version 24-10-2017.
 */

public class DirectoryElement implements Serializable {
    //instance variables of class DirectoryElement
    private String name;
    private String path;
    private boolean selected;

    public DirectoryElement(String fileName){
        name = fileName;
        path = "/usr/files/"+name;
    }

    public DirectoryElement(File file) {
        this.path = file.getAbsolutePath();
        this.name = file.getName();
    }

    public boolean isSelected() { return selected; }

    public void setSelected(boolean selected) { this.selected = selected; }

    public String getPath() { return path; }

    public String getName() { return name; }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException { out.defaultWriteObject(); }

    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException{ in.defaultReadObject(); }

    private void readObjectNoData() throws ObjectStreamException {}

}
