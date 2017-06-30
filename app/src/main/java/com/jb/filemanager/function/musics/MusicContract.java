package com.jb.filemanager.function.musics;

import java.util.List;
import java.util.Map;

/**
 * Created by bool on 17-6-30.
 */

public interface MusicContract {
    interface View {
    }

    interface Presenter {
    }

    interface Support {
        Map<String, List<MusicInfo>> getAllMusicInfo();
    }
}
