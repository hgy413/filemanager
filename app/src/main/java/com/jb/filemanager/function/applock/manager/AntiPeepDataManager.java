package com.jb.filemanager.function.applock.manager;

import android.content.ContentValues;
import android.content.Context;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.applock.event.AntiPeepAllUpdateDoneEvent;
import com.jb.filemanager.function.applock.event.AntiPeepUnreadUpdateDoneEvent;
import com.jb.filemanager.function.applock.event.OnIntruderReadPhotoChangedEvent;
import com.jb.filemanager.function.applock.event.OnIntruderUnreadPhotoChangedEvent;
import com.jb.filemanager.function.applock.model.bean.AntiPeepBean;
import com.jb.filemanager.function.applock.model.dao.AntiPeepDao;
import com.jb.filemanager.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 防偷窥数据库管理器
 *
 * @author chenbenbin
 */
public class AntiPeepDataManager {
    private static AntiPeepDataManager sInstance;
    private Context mContext;
    private AntiPeepDao mAntiPeepDao;
    private List<AntiPeepBean> mReadList = new ArrayList<>();
    private List<AntiPeepBean> mUnreadList = new ArrayList<>();
    private boolean mIsReadListUpdating = false;
    private boolean mIsUnreadListUpdating = false;


    private AntiPeepDataManager(Context context) {
        mContext = context.getApplicationContext();
        init();
    }

    public static AntiPeepDataManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AntiPeepDataManager(context);
        }
        return sInstance;
    }

    private void init() {
        mAntiPeepDao = new AntiPeepDao();
    }

    public synchronized File takePeepPhoto() {
        return AntiPeepFileUtil.createPeepFile();
    }

    public void onPhotoSaved(File file, String packageName) {
        AntiPeepBean bean = new AntiPeepBean();
        bean.setPackageName(TextUtils.isEmpty(packageName) ? "default" : packageName);
        bean.setCreateTime(file.lastModified());
        bean.setPath(file.getPath());
        mAntiPeepDao.insertPeep(bean);
        mUnreadList.add(bean);
        TheApplication.getGlobalEventBus().post(new OnIntruderUnreadPhotoChangedEvent());
    }

    @Deprecated
    public synchronized List<AntiPeepBean> getAllPhoto() {
        initReadList();
        initUnreadList();
        List<AntiPeepBean> list = new ArrayList<AntiPeepBean>();
        list.addAll(mReadList);
        list.addAll(mUnreadList);
        return list;
    }

    public synchronized List<AntiPeepBean> getAllPhotoAfterUpdate() {
        List<AntiPeepBean> list = new ArrayList<AntiPeepBean>();
        list.addAll(mReadList);
        list.addAll(mUnreadList);
        return list;
    }

    /**
     * 更新所有图片列表
     */
    public void updateAllPhoto() {
        if (mIsReadListUpdating) {
            return;
        }
        TheApplication.postRunOnShortTaskThread(new Runnable() {
            @Override
            public void run() {
                mIsReadListUpdating = true;
                initReadList();
                initUnreadList();
                TheApplication.getGlobalEventBus().post(new AntiPeepAllUpdateDoneEvent());
                mIsReadListUpdating = false;
            }
        });
    }

    /**
     * 更新已读图片列表
     */
    public void updateUnreadPhoto() {
        if (mIsUnreadListUpdating) {
            return;
        }
        TheApplication.postRunOnShortTaskThread(new Runnable() {
            @Override
            public void run() {
                mIsUnreadListUpdating = true;
                initUnreadList();
                TheApplication.getGlobalEventBus().post(new AntiPeepUnreadUpdateDoneEvent());
                mIsUnreadListUpdating = false;
            }
        });
    }

    private List<AntiPeepBean> cloneList(List<AntiPeepBean> list) {
        ArrayList<AntiPeepBean> clone = new ArrayList<AntiPeepBean>();
        clone.addAll(list);
        return clone;
    }

    public synchronized List<AntiPeepBean> getUnreadPhotoAfterUpdate() {
        return cloneList(mUnreadList);
    }

    public synchronized List<AntiPeepBean> getUnreadPhoto() {
        initUnreadList();
        return cloneList(mUnreadList);
    }

    /**
     * 初始化已读数据列表
     */
    private synchronized void initReadList() {
        if (!mReadList.isEmpty()) {
            if (checkPhotoExist(mReadList)) {
                TheApplication.getGlobalEventBus().post(new OnIntruderReadPhotoChangedEvent());
            }
            return;
        }
        List<File> readList = AntiPeepFileUtil.getReadList();
        for (File file : readList) {
            AntiPeepBean bean = new AntiPeepBean();
            bean.setIsRead(true);
            bean.setCreateTime(file.lastModified());
            bean.setPath(file.getPath());
            mReadList.add(bean);
        }
        if (!mReadList.isEmpty()) {
            TheApplication.getGlobalEventBus().post(new OnIntruderReadPhotoChangedEvent());
        }
        mAntiPeepDao.initAntiPeepBean(mReadList);
    }

    /**
     * 初始化未读数据列表
     */
    private synchronized void initUnreadList() {
        if (!mUnreadList.isEmpty()) {
            if (checkPhotoExist(mUnreadList)) {
                TheApplication.getGlobalEventBus().post(new OnIntruderUnreadPhotoChangedEvent());
            }
            return;
        }
        List<File> unreadList = AntiPeepFileUtil.getUnreadList();
        for (File file : unreadList) {
            AntiPeepBean bean = new AntiPeepBean();
            bean.setIsRead(false);
            bean.setCreateTime(file.lastModified());
            bean.setPath(file.getPath());
            mUnreadList.add(bean);
        }
        if (!mUnreadList.isEmpty()) {
            TheApplication
                    .getGlobalEventBus().post(new OnIntruderUnreadPhotoChangedEvent());
        }
        mAntiPeepDao.initAntiPeepBean(mUnreadList);
    }

    /**
     * 检测图片是否被外部应用删除
     */
    private boolean checkPhotoExist(List<AntiPeepBean> list) {
        boolean isChanged = false;
        Iterator<AntiPeepBean> iterator = list.iterator();
        while (iterator.hasNext()) {
            AntiPeepBean bean = iterator.next();
            if (!FileUtil.isFileExist(bean.getPath())) {
                mAntiPeepDao.deletePeep(bean);
                iterator.remove();
                isChanged = true;
            }
        }
        return isChanged;
    }

    /**
     * 将所有照片设置为已读
     */
    public synchronized void setAllPhotoRead() {
        if (!mUnreadList.isEmpty()) {
            AntiPeepFileUtil.setAllRead();
            updateUnreadBeanToRead();
            mAntiPeepDao.updatePeepPath(mUnreadList);
            mReadList.addAll(mUnreadList);
            TheApplication.getGlobalEventBus().post(new OnIntruderReadPhotoChangedEvent());
            mUnreadList.clear();
            TheApplication
                    .getGlobalEventBus().post(new OnIntruderUnreadPhotoChangedEvent());
        }
    }

    /**
     * 更新未读信息为已读
     */
    private void updateUnreadBeanToRead() {
        String readPath = AntiPeepFileUtil.getReadPath();
        if (readPath == null) {
            return;
        }
        ContentValues[] arrayValues = new ContentValues[mUnreadList.size()];
        for (AntiPeepBean bean : mUnreadList) {
            bean.setIsRead(true);
            bean.setPath(readPath + File.separator + FileUtil.getName(bean.getPath()));
            File file = new File(bean.getPath());
            bean.setCreateTime(file.lastModified());
            ContentValues values = new ContentValues(3);
            values.put(MediaStore.Images.Media.DISPLAY_NAME, FileUtil.getName(bean.getPath()));
            values.put(MediaStore.Images.Media.DATA, bean.getPath());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            arrayValues[mUnreadList.indexOf(bean)] = values;
        }
        // 插入媒体库
        // TODO DEVICE=hwp7,Unknown URL content://media/external/images/media
        try {
            mContext.getContentResolver().bulkInsert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    arrayValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deletePhoto(AntiPeepBean bean) {
        mAntiPeepDao.deletePeep(bean);
        FileUtil.deleteFile(bean.getPath());
        if (bean.isRead()) {
            mReadList.remove(bean);
            TheApplication.getGlobalEventBus().post(new OnIntruderReadPhotoChangedEvent());
        } else {
            mUnreadList.remove(bean);
            TheApplication
                    .getGlobalEventBus().post(new OnIntruderUnreadPhotoChangedEvent());
        }
    }

    public void deleteAllPhotos() {
        AntiPeepFileUtil.deleteAllFile();
        mAntiPeepDao.deleteAllPeep();
        mReadList.clear();
        TheApplication.getGlobalEventBus().post(new OnIntruderReadPhotoChangedEvent());
        mUnreadList.clear();
        TheApplication.getGlobalEventBus().post(new OnIntruderUnreadPhotoChangedEvent());
    }

}
