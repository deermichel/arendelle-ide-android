package org.arendelle.android;

import android.graphics.Bitmap;

/** item for projects list */
public class ProjectsListItem {

    /** name */
    public String name;

    /** preview */
    public Bitmap preview;


    public ProjectsListItem(String name, Bitmap preview) {
        this.name = name;
        this.preview = preview;
    }

}
