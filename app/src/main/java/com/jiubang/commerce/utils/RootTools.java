package com.jiubang.commerce.utils;

import com.jb.ga0.commerce.util.io.FileUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class RootTools {
    public static boolean isRootAvailable() {
        return findBinary("su", (List<String>) null).size() > 0;
    }

    private static List<String> findBinary(String binaryName, List<String> searchPaths) {
        List<String> foundPaths = new ArrayList<>();
        if (searchPaths == null) {
            searchPaths = getPath();
        }
        Iterator i$ = searchPaths.iterator();
        while (i$.hasNext()) {
            String path = i$.next();
            if (!path.endsWith("/")) {
                path = path + "/";
            }
            if (FileUtil.isFileExist(path + binaryName)) {
                foundPaths.add(path);
            }
        }
        return foundPaths;
    }

    private static List<String> getPath() {
        return Arrays.asList(System.getenv("PATH").split(":"));
    }
}
