package com.jb.filemanager.function.applock.model.dao;

import com.jb.filemanager.function.applock.database.AntiPeepDatabaseHelper;
import com.jb.filemanager.function.applock.model.bean.AntiPeepBean;

import java.util.List;

/**
 * 防偷窥数据库操作类
 *
 * @author chenbenbin
 */
public class AntiPeepDao {
    private AntiPeepDatabaseHelper mDBHelper;

    public AntiPeepDao() {
        mDBHelper = AntiPeepDatabaseHelper.getInstance();
    }

    /**
     * 插入偷窥者信息
     */
    public void insertPeep(AntiPeepBean bean) {
        mDBHelper.insertPeep(bean);
    }

    /**
     * 删除所有偷窥者信息
     */
    public void deleteAllPeep() {
        mDBHelper.deleteAllPeep();
    }

    /**
     * 更新所有偷窥者路径信息
     */
    public void updatePeepPath(List<AntiPeepBean> list) {
        mDBHelper.updatePeepPath(list);
    }

    /**
     * 删除偷窥者路径信息
     */
    public void deletePeep(AntiPeepBean bean) {
        mDBHelper.deletePeep(bean);
    }

    /**
     * 根据创建时间获取应用包名
     */
    public void initAntiPeepBean(List<AntiPeepBean> srcList) {
        mDBHelper.initAntiPeepBean(srcList);
    }
}
