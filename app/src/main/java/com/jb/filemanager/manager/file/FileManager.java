package com.jb.filemanager.manager.file;

import android.content.Intent;
import android.text.TextUtils;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.paste.DuplicateFilePasteActivity;
import com.jb.filemanager.function.paste.SubFolderPasteActivity;
import com.jb.filemanager.manager.file.task.CopyFileTask;
import com.jb.filemanager.manager.file.task.CutFileTask;
import com.jb.filemanager.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by bill wang on 2017/6/23.
 *
 */

public class FileManager {

    // 图片
    public static final int PICTURE = 0;

    // 视频
    public static final int VIDEO = 1;

    // 音乐
    public static final int MUSIC = 2;

    // 其他文件
    public static final int OTHERS = 3;

    // 应用
    public static final int APP = 4;

    // 文件夹
    public static final int DIR = 5;

    // 通讯录
    public static final int CONTACTS = 6;

    // 短信
    public static final int SMS = 7;

    // 通话记录
    public static final int PHONE = 8;

    // 书签
    public static final int BOOKMARKS = 9;

    // 日程
    public static final int SCHEDULE = 10;


    public static final int LOADER_IMAGE = 0;
    public static final int LOADER_VIDEO = 1;
    public static final int LOADER_APP = 2;
    public static final int LOADER_AUDIO = 3;
    public static final int LOADER_DOC = 4;
    public static final int LOADER_FILES = 5;

    private static FileManager sInstance;

    private ArrayList<File> mCopyFiles;
    private ArrayList<File> mCutFiles;

    private Comparator<File> mFileSort;

    private HashMap<String, Object> mPasteLockers;

    public static FileManager getInstance() {
        synchronized (FileManager.class) {
            if (sInstance == null) {
                sInstance = new FileManager();
            }
            return sInstance;
        }
    }


    public void setCopyFiles(ArrayList<File> copyFiles) {
        clearCopyFiles();
        mCopyFiles = new ArrayList<>(copyFiles);
    }

    public void setCutFiles(ArrayList<File> cutFiles) {
        clearCutFiles();
        mCutFiles = new ArrayList<>(cutFiles);
    }

    public ArrayList<File> getCopyFiles() {
        return mCopyFiles;
    }

    public ArrayList<File> getCutFiles() {
        return mCutFiles;
    }

    public void clearCopyFiles() {
        if (mCopyFiles != null) {
            mCopyFiles.clear();
            mCopyFiles = null;
        }
    }

    public void clearCutFiles() {
        if (mCutFiles != null) {
            mCutFiles.clear();
            mCutFiles = null;
        }
    }

    public void setFileSort(Comparator<File> sort) {
        mFileSort = sort;
    }

    public Comparator<File> getFileSort() {
        return mFileSort;
    }

    public void doPaste(String destDir, final Listener listener) {
        if (mCopyFiles != null && mCopyFiles.size() > 0) {
            long moreThenNeed = FileUtil.checkSpacePaste(mCopyFiles, destDir);

            if (moreThenNeed > 0) {
                new CopyFileTask(mCopyFiles, destDir, new CopyFileTask.Listener() {

                    @Override
                    public void onSubFolderCopy(CopyFileTask task, File file, String dest) {
                        if (mPasteLockers == null) {
                            mPasteLockers = new HashMap<>();
                        }
                        mPasteLockers.put(file.getAbsolutePath(), task);

                        Intent intent = new Intent(TheApplication.getInstance(), SubFolderPasteActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(SubFolderPasteActivity.SUB_FOLDER_PASTE_SOURCE_PATH, file.getAbsolutePath());
                        TheApplication.getInstance().startActivity(intent);
                    }

                    @Override
                    public void onDuplicate(CopyFileTask task, File file, ArrayList<File> copySource) {
                        if (mPasteLockers == null) {
                            mPasteLockers = new HashMap<>();
                        }
                        mPasteLockers.put(file.getAbsolutePath(), task);

                        Intent intent = new Intent(TheApplication.getInstance(), DuplicateFilePasteActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(DuplicateFilePasteActivity.DUPLICATE_FILE_PATH, file.getAbsolutePath());
                        intent.putExtra(DuplicateFilePasteActivity.DUPLICATE_FILE_IS_SINGLE, copySource.size() == 1);
                        TheApplication.getInstance().startActivity(intent);
                    }

                    @Override
                    public void onProgressUpdate(File file) {
                        // TODO 是否有其他操作还不清楚
                        if (listener != null) {
                            listener.onPasteProgressUpdate(file);
                        }
                    }

                    @Override
                    public void onPostExecute(Boolean aBoolean) {
                        // TODO 是否有其他操作还不清楚
                        if (listener != null) {
                            listener.onPastePostExecute(aBoolean);
                        }
                    }
                }).start();
            } else {
                listener.onPasteNeedMoreSpace(Math.abs(moreThenNeed));
            }
        } else if (mCutFiles != null && mCutFiles.size() > 0) {
            new CutFileTask(mCutFiles, destDir, new CutFileTask.Listener() {

                @Override
                public void onSubFolderCopy(CutFileTask task, File file, String dest) {
                    if (mPasteLockers == null) {
                        mPasteLockers = new HashMap<>();
                    }
                    mPasteLockers.put(file.getAbsolutePath(), task);

                    Intent intent = new Intent(TheApplication.getInstance(), SubFolderPasteActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(SubFolderPasteActivity.SUB_FOLDER_PASTE_SOURCE_PATH, file.getAbsolutePath());
                    TheApplication.getInstance().startActivity(intent);
                }

                @Override
                public void onDuplicate(CutFileTask task, File file, ArrayList<File> cutSource) {
                    if (mPasteLockers == null) {
                        mPasteLockers = new HashMap<>();
                    }
                    mPasteLockers.put(file.getAbsolutePath(), task);

                    Intent intent = new Intent(TheApplication.getInstance(), DuplicateFilePasteActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(DuplicateFilePasteActivity.DUPLICATE_FILE_PATH, file.getAbsolutePath());
                    intent.putExtra(DuplicateFilePasteActivity.DUPLICATE_FILE_IS_SINGLE, cutSource.size() == 1);
                    TheApplication.getInstance().startActivity(intent);
                }

                @Override
                public void onProgressUpdate(File file) {
                    // TODO 是否有其他操作还不清楚
                    if (listener != null) {
                        listener.onPasteProgressUpdate(file);
                    }
                }

                @Override
                public void onPostExecute(Boolean aBoolean) {
                    // TODO 是否有其他操作还不清楚
                    if (listener != null) {
                        listener.onPastePostExecute(aBoolean);
                    }
                }
            }).start();
        } else {
            listener.onPastePostExecute(false);
        }
    }

    public void continuePaste(String path, boolean skip, Boolean applyToAll) {
        if (!TextUtils.isEmpty(path) && mPasteLockers != null && mPasteLockers.containsKey(path)) {
            Object task = mPasteLockers.remove(path);
            if (task instanceof CopyFileTask) {
                ((CopyFileTask) task).continueCopy(skip, applyToAll);
            } else if (task instanceof CutFileTask) {
                ((CutFileTask) task).continueCut(skip, applyToAll);
            }
        }
    }

    public void stopPast(String path) {
        if (!TextUtils.isEmpty(path) && mPasteLockers != null && mPasteLockers.containsKey(path)) {
            Object task = mPasteLockers.remove(path);
            if (task instanceof CopyFileTask) {
                ((CopyFileTask) task).stopCopy();
            } else if (task instanceof CutFileTask) {
                ((CutFileTask) task).stopCut();
            }
        }
    }

    public interface Listener {
        void onPasteNeedMoreSpace(long needMoreSpace);
        void onPasteProgressUpdate(File file);
        void onPastePostExecute(Boolean aBoolean);
    }
}
