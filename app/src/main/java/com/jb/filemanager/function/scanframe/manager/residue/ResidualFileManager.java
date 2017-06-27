package com.jb.filemanager.function.scanframe.manager.residue;

import android.content.Context;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.scanframe.bean.common.FileType;
import com.jb.filemanager.function.scanframe.bean.residuebean.ResidueBean;
import com.jb.filemanager.function.scanframe.clean.CleanManager;
import com.jb.filemanager.function.scanframe.clean.event.CleanDBDataInitDoneEvent;
import com.jb.filemanager.function.scanframe.clean.event.GlobalDataLoadingDoneEvent;
import com.jb.filemanager.function.scanframe.clean.event.PackageAddedEvent;
import com.jb.filemanager.function.scanframe.clean.event.PackageRemovedEvent;
import com.jb.filemanager.function.scanframe.clean.event.ResidueUpdateDoneEvent;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.FileTypeUtil;
import com.jb.filemanager.util.StorageUtil;
import com.jb.filemanager.util.file.FileUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by xiaoyu on 2016/10/21.<br>
 * 获取残留文件管理者<br>
 */

public class ResidualFileManager {

    private Context mContext;

    private static ResidualFileManager sInstance;

    /**
     * 残留数据库Map：以包名为Key
     */
    private HashMap<String, ResidueBean> mResidueMap = new HashMap<>();

    /**
     * 残留数据库Map：以路径为Key
     */
    private HashMap<String, HashSet<ResidueBean>> mResiduePathMap = new HashMap<>();

    private ResidueDataManager mDataManager;

    private HashSet<String> mInstalledApp = new HashSet<>();

    public static synchronized ResidualFileManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ResidualFileManager(context);
        }
        return sInstance;
    }

    private ResidualFileManager(Context context) {
        mContext = context;
        mDataManager = ResidueDataManager.getInstance(mContext);
        TheApplication.getGlobalEventBus().register(mEventReceiver);
    }

    /**
     * 事件接收器
     */
    @SuppressWarnings("FieldCanBeLocal")
    private Object mEventReceiver = new Object() {
        /**
         * 初始化数据
         */
        @SuppressWarnings("unused")
        @Subscribe(threadMode = ThreadMode.ASYNC)
        public void onEventAsync(GlobalDataLoadingDoneEvent event) {
            initDbData();
            CleanDBDataInitDoneEvent.RESIDUE.setIsDone(true);
            TheApplication.postEvent(CleanDBDataInitDoneEvent.RESIDUE);
        }

        /**
         * 初始化完数据后启用后台监听卸载
         */
        @SuppressWarnings("unused")
        @Subscribe
        public void onEventMainThread(CleanDBDataInitDoneEvent event) {
            if (event.isResidue()) {
                //UninstallReceiver.getInstance(mContext);
            }
        }

        /**
         * 监听应用的安装，更新应用列表
         */
        @SuppressWarnings("unused")
        @Subscribe
        public void onEventMainThread(PackageAddedEvent event) {
            mInstalledApp.add(event.getPackageName());
        }

        /**
         * 监听应用的卸载，更新应用列表
         */
        @SuppressWarnings("unused")
        @Subscribe
        public void onEventMainThread(PackageRemovedEvent event) {
            mInstalledApp.remove(event.getPackageName());
        }

        /**
         * 监听到残留数据更新
         */
        @SuppressWarnings("unused")
        @Subscribe
        public void onEventMainThread(ResidueUpdateDoneEvent event) {
            if (!CleanManager.getInstance(mContext).isScanning()) {
                updateDataIfNeed();
            }
        }
    };

    private void initDbData() {
        mResidueMap.clear();
        mResiduePathMap.clear();
        mDataManager.getResidueMap(mResidueMap, mResiduePathMap);
        mInstalledApp.addAll(AppUtils.getInstalledPackagesPackageNameOnly(mContext));
    }

    /**
     * 扫描残留文件
     */
    public ArrayList<ResidueBean> scanResidueData(File sdcardPath, HashSet<String> ignoreList) {
        ArrayList<ResidueBean> resultList = new ArrayList<>();
        File[] sdcardFiles = sdcardPath.listFiles();
        if (sdcardFiles == null) {
            return resultList;
        }
        HashSet<String> installApps = filterAppListByIgnoreApp(ignoreList);
        for (File file : sdcardFiles) {
            boolean isInstall = false;
            for (Map.Entry<String, HashSet<ResidueBean>> entry : mResiduePathMap
                    .entrySet()) {
                String residuePath = entry.getKey();
                boolean isPathExist = false;
                if (residuePath.toLowerCase().startsWith(
                        file.getName().toLowerCase())) {
                    if (file.getName().length() == residuePath.length()) {
                        // 一级路径，直接整合数据
                        // 长度匹配比直接equals匹配效率高
                        isPathExist = true;
                    } else if (residuePath.contains(File.separator)) {
                        // 匹配出来的数据库二级目录
                        isPathExist = FileUtil.isFileExist(sdcardPath
                                + File.separator + residuePath);
                    }
                }

                if (!isPathExist) {
                    continue;
                }
                // 路径存在，则判断应用是否全部都已卸载
                ResidueBean clone = null;
                HashSet<ResidueBean> value = entry.getValue();
                HashSet<String> pkgSet = new HashSet<String>();
                for (ResidueBean bean : value) {
                    if (installApps.contains(bean.getPackageName())) {
                        isInstall = true;
                    } else {
                        pkgSet.add(bean.getPackageName());
                        // 返回克隆数据，避免修改数据库的源数据
                        clone = bean.clone();
                        clone.setPath(sdcardPath + File.separator + residuePath);
                    }
                }

                if (clone == null || isInstall || value.isEmpty()) {
                    continue;
                }
                clone.setPkgNameSet(pkgSet);
                combineResidueData(resultList, clone);
            }
        }

        // 删除残留路径的子路径
        for (ResidueBean bean : resultList) {
            deleteResidueSubPath(bean);
        }
        return resultList;
    }

    private HashSet<String> filterAppListByIgnoreApp(HashSet<String> ignoreList) {
        HashSet<String> result = new HashSet<>();
        result.addAll(mInstalledApp);
        if (ignoreList != null) {
            result.addAll(ignoreList);
        }
        return result;
    }

    /**
     * 整合同个应用不同路径
     *
     * @param residueBeans 扫描结果队列
     * @param residueBean  符合条件的数据
     */
    private void combineResidueData(ArrayList<ResidueBean> residueBeans,
                                    ResidueBean residueBean) {
        boolean isUnique = true;
        for (ResidueBean bean : residueBeans) {
            if (bean.getPackageName().equals(residueBean.getPackageName())) {
                isUnique = false;
                bean.addPath(residueBean.getPath());
                break;
            }
        }
        if (isUnique) {
            residueBeans.add(residueBean);
        }
    }

    /**
     * 删除残留路径的子路径
     */
    private void deleteResidueSubPath(ResidueBean bean) {
        ArrayList<String> pathList = new ArrayList<String>();
        HashSet<String> srcPathSet = bean.getPaths();
        pathList.addAll(srcPathSet);
        // 将路径从短到长进行排序
        Collections.sort(pathList, new StringIncComparator());
        // 将集合中的路径逐个和后面的进行比较(因为已经从短到长，所以靠前的不会是后面路径的子路径)，子路径则删除
        for (int i = 0; i < pathList.size(); i++) {
            String tempPath = pathList.get(i);
            for (int k = i + 1; k < pathList.size(); k++) {
                if (pathList.get(k).contains(tempPath)) {
                    pathList.remove(k);
                }
            }
        }

        if (srcPathSet.size() != pathList.size()) {
            bean.setPaths(pathList);
        }
    }

    /**
     * 字符串递增的排序
     *
     * @author chenbenbin
     */
    private class StringIncComparator implements Comparator<String> {

        @Override
        public int compare(String lhs, String rhs) {
            if (lhs.length() < rhs.length()) {
                return -1;
            } else if (lhs.length() == rhs.length()) {
                return 0;
            } else {
                return 1;
            }
        }

    }

    /**
     * 根据卸载的包名获取残留文件
     *
     * @param unInstallName 被卸载的应用的包名
     */
    public ResidueBean getAppResidueData(String unInstallName) {
        if (mResidueMap.containsKey(unInstallName)) {
            // 1. 判断数据库是否存在被删除应用的数据
            ResidueBean bean = mResidueMap.get(unInstallName);
            ResidueBean cloneBean = bean.clone();
            for (String path : bean.getPaths()) {
                // 2. 判断该路径是否被其他应用所使用
                HashSet<ResidueBean> set = mResiduePathMap.get(path);
                for (ResidueBean pathBean : set) {
                    if (pathBean != bean) {
                        // 3. 判断其他应用是否安装，若已安装，则剔除此路径
                        if (AppUtils.isAppExist(mContext,
                                pathBean.getPackageName())) {
                            cloneBean.getPaths().remove(path);
                        }
                    }
                }
            }
            // 4. 删除子路径，避免文件大小重复计算
            deleteResidueSubPath(cloneBean);
            // 若数据库扫描出的残留路径在手机上都没有，则不返回数据
            boolean isPathExist = false;
            // 5. 遍历数据单元的所有路径，考虑多张SD卡的情况
            Set<String> sdPaths = StorageUtil.getAllExternalPaths(mContext);
            for (String path : cloneBean.getPaths()) {
                boolean isExternalStorage = isExternalStorage(path);
                for (String sdPath : sdPaths) {
                    File file = new File(sdPath + File.separator + path);
                    if (file.exists()) {
                        isPathExist = true;
                        scanResidueFolder(cloneBean, file, isExternalStorage);
                    }
                }
            }
            if (isPathExist) {
                return cloneBean;
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * 是否为外置存储路径
     */
    private boolean isExternalStorage(String path) {
        String lowerCasePath = path.toLowerCase();
        return lowerCasePath.startsWith("android/data")
                || lowerCasePath.startsWith("android/obb");
    }

    /**
     * 根据残留文件路径更新残留文件数据单元
     *
     * @param bean              残留数据单元
     * @param file              残留文件路径
     * @param isExternalStorage 是否为外置存储路径
     */
    private void scanResidueFolder(ResidueBean bean, File file,
                                   boolean isExternalStorage) {
        if (file.isFile()) {
            bean.setSize(bean.getSize() + file.length());
            FileType type = FileTypeUtil.getFileTypeSensitive(file.getPath());
            if (!type.equals(FileType.OTHER)) {
                bean.addFileType(type);
                if (!isExternalStorage) {
                    // 外置存储路径在卸载应用后自动删除，为了避免图片、视频缩略图加载不出，所以不加入到图片显示中
                    if (type.equals(FileType.IMAGE)) {
                        bean.addImage(file.getPath());
                    } else if (type.equals(FileType.VIDEO)) {
                        bean.addVideo(file.getPath());
                    }
                }
            }
        }
        File[] childList = file.listFiles();
        if (childList == null) {
            return;
        }
        for (File child : childList) {
            scanResidueFolder(bean, child, isExternalStorage);
        }
    }

    /**
     * 垃圾扫描完毕，根据数据库更新情况，更新内存数据
     */
    public void updateDataIfNeed() {
        if (!mDataManager.checkIsUpdated()) {
            return;
        }
        CleanDBDataInitDoneEvent.RESIDUE.setIsDone(false);
        TheApplication.postRunOnShortTaskThread(new Runnable() {
            @Override
            public void run() {
                mResidueMap.clear();
                mResiduePathMap.clear();
                mDataManager.getResidueMap(mResidueMap, mResiduePathMap);
                CleanDBDataInitDoneEvent.RESIDUE.setIsDone(true);
                TheApplication.postEvent(CleanDBDataInitDoneEvent.RESIDUE);
            }
        });
    }

}
