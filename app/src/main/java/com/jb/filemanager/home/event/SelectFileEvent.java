package com.jb.filemanager.home.event;

import java.io.File;

/**
 * Created by bill wang on 2017/6/23.
 *
 */

public class SelectFileEvent {

    public File mFile;

    public SelectFileEvent(File file) {
        mFile = file;
    }
}
