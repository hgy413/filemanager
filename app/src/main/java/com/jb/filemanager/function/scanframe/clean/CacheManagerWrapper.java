package com.jb.filemanager.function.scanframe.clean;

import android.content.Context;
import android.text.TextUtils;

import com.jb.filemanager.function.scanframe.bean.cachebean.AppCacheBean;
import com.jb.filemanager.function.scanframe.bean.cachebean.subitem.SubAppCacheBean;
import com.jb.filemanager.function.scanframe.bean.common.subitemcommon.SubItemBean;
import com.jb.ga0.commerce.util.io.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * 应用缓存管理器包装类<br>
 * 针对业务逻辑，对数据库读取出的数据进行处理
 *
 * @author chenbenbin
 */
public class CacheManagerWrapper {
    /**
     * 描述中的应用名占位符
     */
    private static final String DESC_PLACEHOLDER = "#";
    private static CacheManagerWrapper sInstance;
    private Context mContext;

    private CacheManagerWrapper(Context context) {
        mContext = context.getApplicationContext();
    }

    public synchronized static CacheManagerWrapper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new CacheManagerWrapper(context);
        }
        return sInstance;
    }

    public ArrayList<AppCacheBean> getAppCacheList(String sdPath) {
        ArrayList<AppCacheBean> resultList = new ArrayList<>();
        ArrayList<AppCacheBean> dbList = CacheManager.getInstance(mContext).getAppCacheList();
        if (dbList == null) {
            return resultList;
        }
        int size = dbList.size();
        for (int i = 0; i < size; i++) {
            AppCacheBean cacheBean = dbList.get(i);
            Iterator<SubItemBean> iterator = cacheBean.getSubItemList()
                    .iterator();
            while (iterator.hasNext()) {
                SubAppCacheBean appCacheBean = (SubAppCacheBean) iterator
                        .next();
                String srcPath = appCacheBean.getPath().trim();
                if (TextUtils.isEmpty(srcPath)) {
                    continue;
                }
                if (!srcPath.startsWith(File.separator)) {
                    srcPath = File.separator + srcPath;
                }
                String filePath = sdPath + srcPath;
                // 检测是否为带通配符的路径 /*/
                String[] split = filePath.split("/\\*/");
                if (split.length > 1) {
                    boolean isExist = handleRegularPath(appCacheBean, split);
                    if (isExist) {
                        appCacheBean.setChecked(appCacheBean.isDefaultCheck());
                        // 替换描述中的应用名占位符
                        String desc = appCacheBean.getDesc();
                        appCacheBean.setDesc(desc.replaceAll(DESC_PLACEHOLDER,
                                cacheBean.getTitle()));
                    } else {
                        iterator.remove();
                    }
                    continue;
                }

                if (FileUtil.isFileExist(filePath.trim())) {
                    // 对普通路径判断是否存在，存在则记录
                    appCacheBean.setPath(filePath);
                    appCacheBean.setChecked(appCacheBean.isDefaultCheck());
                    // 替换描述中的应用名占位符
                    String desc = appCacheBean.getDesc();
                    appCacheBean.setDesc(desc.replaceAll(DESC_PLACEHOLDER,
                            cacheBean.getTitle()));
                } else {
                    iterator.remove();
                }
            }
            if (!cacheBean.getSubItemList().isEmpty()) {
                resultList.add(cacheBean);
            }
        }
        return resultList;
    }

    /**
     * 解析通配符路径
     *
     * @param appCacheBean 数据Bean
     * @param split        路径按照分割后的数组
     * @return 通配符路径是否存在
     */
    private boolean handleRegularPath(SubAppCacheBean appCacheBean,
                                      String[] split) {
        // 先默认调整路径为星号之前部分
        appCacheBean.setPath(split[0]);

        // 缓存临时符合条件的路径
        HashSet<StringBuilder> matchPathSet = new HashSet<StringBuilder>();
        matchPathSet.add(new StringBuilder());
        for (int i = 0; i < split.length; i++) {
            Iterator<StringBuilder> iterator = matchPathSet.iterator();
            while (iterator.hasNext()) {
                // 拼接字段，若文件存在则继续匹配，否则移除
                StringBuilder ap = iterator.next();
                String tempPath = ap.append(File.separator).append(split[i])
                        .toString();
                if (!new File(tempPath).exists()) {
                    iterator.remove();
                }
            }

            if (matchPathSet.isEmpty()) {
                // 已经没有匹配的路径时跳出循环
                break;
            }

            if (i != split.length - 1) {
                // 对*号进行列举匹配
                // 若非最后一个拼接部分时，则将子路径添加到临时集合中
                HashSet<StringBuilder> tempSet = new HashSet<StringBuilder>();
                for (StringBuilder builder : matchPathSet) {
                    File file = new File(builder.toString());
                    final File[] list = file.listFiles();
                    if (list != null) {
                        for (File f : list) {
                            tempSet.add(new StringBuilder(f.getPath()));
                        }
                    }
                }
                matchPathSet.clear();
                matchPathSet.addAll(tempSet);
            }
        }

        for (StringBuilder builder : matchPathSet) {
            if (matchPathSet.size() == 1) {
                // 若只有一条子路径，则设置为自身路径
                appCacheBean.setPath(builder.toString());
            }
            appCacheBean.addChildFile(builder.toString());
        }

        return !matchPathSet.isEmpty();
    }
}