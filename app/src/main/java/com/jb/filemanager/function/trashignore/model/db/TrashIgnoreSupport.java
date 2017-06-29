package com.jb.filemanager.function.trashignore.model.db;

import android.content.Context;

import com.jb.filemanager.function.scanframe.bean.common.itemcommon.GroupType;
import com.jb.filemanager.function.scanframe.clean.CleanManager;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreBean;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreGroupBean;
import com.jb.filemanager.function.trashignore.contract.Contract;
import com.jb.filemanager.os.ZAsyncTask;
import com.jb.filemanager.util.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by xiaoyu on 2017/2/28 13:56.
 */

public class TrashIgnoreSupport implements Contract.Support {

    private CleanManager mCleanManager;
    private AsyncLoadTask mAsyncLoadTask;
    private Runnable mRunnable;
    private ArrayList<CleanIgnoreGroupBean> mGroupList = new ArrayList<>();

    public TrashIgnoreSupport(Context context) {
        mCleanManager = CleanManager.getInstance(context);
    }

    @Override
    public void startObtainData(Runnable runnable) {
        mRunnable = runnable;
        mAsyncLoadTask = new AsyncLoadTask();
        mAsyncLoadTask.execute();
    }

    @Override
    public List<CleanIgnoreGroupBean> getDataFromDb() {
        return mGroupList;
    }

    @Override
    public void onExitFromDb() {
        if (mAsyncLoadTask != null && !mAsyncLoadTask.isCancelled()) {
            mAsyncLoadTask.cancel(true);
        }
    }

    /**
     * 异步数据加载器
     *
     * @author chenbenbin
     */
    private class AsyncLoadTask extends ZAsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            List<CleanIgnoreBean> cacheList = mCleanManager.queryCacheAppIgnore();
            Logger.e("Ignore", "TrashIgnoreSupport doInBackground Cahce App");
            cacheList.addAll(mCleanManager.queryCachePathIgnore());
            CleanIgnoreGroupBean cacheGroup = new CleanIgnoreGroupBean(cacheList, GroupType.APP_CACHE);
            CleanIgnoreGroupBean residueGroup = new CleanIgnoreGroupBean(mCleanManager.queryResidueIgnore(), GroupType.RESIDUE);
            CleanIgnoreGroupBean adGroup = new CleanIgnoreGroupBean(mCleanManager.queryAdIgnore(), GroupType.AD);
            mGroupList.add(cacheGroup);
            mGroupList.add(residueGroup);
            mGroupList.add(adGroup);
            removeEmptyGroup();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mRunnable != null) {
                mRunnable.run();
            }
        }
    }

    private void removeEmptyGroup() {
        Iterator<CleanIgnoreGroupBean> iterator = mGroupList.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getchildrenSize() == 0) {
                iterator.remove();
            }
        }
    }
}
