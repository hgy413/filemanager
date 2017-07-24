package com.jb.filemanager.function.image.presenter;


import android.database.Cursor;
import android.provider.MediaStore;

import com.jb.filemanager.commomview.GroupSelectBox;
import com.jb.filemanager.function.image.modle.ImageGroupModle;
import com.jb.filemanager.function.image.modle.ImageModle;
import com.jb.filemanager.util.TimeUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by bill wang on 2017/6/27.
 */

public class ImagePresenter implements ImageContract.Presenter {

    private ImageContract.View mView;
    private ImageContract.Support mSupport;
    //选择图片列表
    private List<ImageModle> mSelectedImageList = new ArrayList<>();
    private List<ImageGroupModle> mImageGroupModleList = new ArrayList<>();
    private int mTotalSize = 0;

    public ImagePresenter(ImageContract.View view, ImageContract.Support support) {
        mView = view;
        mSupport = support;
    }

    @Override
    public void handleBackClick() {
        if (mView != null) {
            mView.finish();
        }
    }

    @Override
    public void handleCancel() {
        for (int i = 0; i < mImageGroupModleList.size(); i++) {
            ImageGroupModle imageGroupModle = mImageGroupModleList.get(i);
            imageGroupModle.mSelectState = GroupSelectBox.SelectState.NONE_SELECTED;
        }
        for (int i = 0; i < mSelectedImageList.size(); i++) {
            mSelectedImageList.get(i).isChecked = false;
        }
        mView.dismissBobar();
        mView.notifyViewChg();
        mSelectedImageList.clear();
    }

    @Override
    public void handleCheck(boolean isCheck) {
        if (!isCheck) {
            //改成全取消
            handleCancel();
        } else {
            //改成全选
            if (mImageGroupModleList != null) {
                mSelectedImageList.clear();
                for (int i = 0; i < mImageGroupModleList.size(); i++) {
                    ImageGroupModle imageGroupModle = mImageGroupModleList.get(i);
                    imageGroupModle.mSelectState = GroupSelectBox.SelectState.ALL_SELECTED;
                    for (int i1 = 0; i1 < imageGroupModle.mImageModleList.size(); i1++) {
                        List<ImageModle> imageModles = imageGroupModle.mImageModleList.get(i1);
                        for (int i2 = 0; i2 < imageModles.size(); i2++) {
                            ImageModle imageModle = imageModles.get(i2);
                            imageModle.isChecked = true;
                            mSelectedImageList.add(imageModle);
                        }
                    }
                }
                if (mView != null) {
                    mView.showSelected(mSelectedImageList.size(), mTotalSize);
                    //全选
                    mView.notifyViewChg();
                }
            }
        }
    }

    @Override
    public void handleSelected(List<ImageGroupModle> imageGroupModleList) {
        if (imageGroupModleList != null) {
            mTotalSize = 0;
            mSelectedImageList.clear();
            for (int i = 0; i < imageGroupModleList.size(); i++) {
                ImageGroupModle imageGroupModle = imageGroupModleList.get(i);
                int selectNum = 0;
                int groupItemNums = 0;
                for (int i1 = 0; i1 < imageGroupModle.mImageModleList.size(); i1++) {
                    List<ImageModle> imageModles = imageGroupModle.mImageModleList.get(i1);
                    for (int i2 = 0; i2 < imageModles.size(); i2++) {
                        ImageModle imageModle = imageModles.get(i2);
                        if (imageModle.isChecked) {
                            mSelectedImageList.add(imageModle);
                            selectNum ++;
                        }
                        groupItemNums ++;
                    }
                    mTotalSize += imageModles.size();
                }
                if (selectNum == 0) {
                    imageGroupModle.mSelectState = GroupSelectBox.SelectState.NONE_SELECTED;
                } else if (selectNum < groupItemNums) {
                    imageGroupModle.mSelectState = GroupSelectBox.SelectState.MULT_SELECTED;
                } else {
                    imageGroupModle.mSelectState = GroupSelectBox.SelectState.ALL_SELECTED;
                }
            }
            if (mView != null) {
                mView.showSelected(mSelectedImageList.size(), mTotalSize);
            }
        }
    }

    @Override
    public void handleDataFinish(Cursor cursor) {
        if (cursor != null) {
            try {
                mImageGroupModleList.clear();
                //使用HashMap数据结构来实现 O(1) 时间复杂度的查询速度
                HashMap<String, ImageGroupModle> mImageGroupModleMap = new HashMap<>();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
                while (cursor.moveToNext()) {
                    Calendar now;
                    //获取图片的路径
                    String path = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));
                    //此处返回数据为秒钟 所以乘以1000
                    long modifiedTime = cursor.getLong(cursor
                            .getColumnIndex(MediaStore.Images.Media.DATE_ADDED)) * 1000;
                    int id = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    now = Calendar.getInstance();
                    now.setTimeInMillis(modifiedTime);
                    String timeDateStr = TimeUtil.getTime(modifiedTime, simpleDateFormat);
                    ImageModle imageModle = new ImageModle(path, id, false, modifiedTime);

                    if (mImageGroupModleMap.containsKey(timeDateStr)) {
                        //存在时 直接添加 
                        ImageGroupModle imageGroupModle = mImageGroupModleMap.get(timeDateStr);

                        int length = imageGroupModle.mImageModleList.size();
                        List<ImageModle> lastImageModleList = imageGroupModle.mImageModleList.get(length - 1);
                        int subLength = lastImageModleList.size();
                        if (subLength < 3) {
                            lastImageModleList.add(imageModle);
                        } else {
                            List<ImageModle> imageModleList = new ArrayList<>(3);
                            imageModleList.add(imageModle);
                            imageGroupModle.mImageModleList.add(imageModleList);
                        }
                    } else {
                        //不存在时 直接创建
                        ImageGroupModle imageGroupModle = new ImageGroupModle();
                        imageGroupModle.mTimeDate = timeDateStr;
                        List<ImageModle> imageModleList = new ArrayList<>(3);
                        imageModleList.add(imageModle);
                        imageGroupModle.mImageModleList.add(imageModleList);
                        mImageGroupModleMap.put(timeDateStr, imageGroupModle);
                        mImageGroupModleList.add(imageGroupModle);
                    }
                }
                //释放内存
                mImageGroupModleMap.clear();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                cursor.close();
            }
            if (mView != null) {
                mView.bindData(mImageGroupModleList);
            }
        }
    }

    @Override
    public ArrayList<File> getCurrentSelectedFiles() {
        if (mSelectedImageList == null) {
            return new ArrayList<>();
        }
        ArrayList<File> result = new ArrayList<>(mSelectedImageList.size());
        for (int i = 0; i < mSelectedImageList.size(); i++) {
            ImageModle image = mSelectedImageList.get(i);
            result.add(new File(image.mImagePath));
        }
        return result;
    }

    @Override
    public void handleDeleted() {
        if (mSupport != null) {
            for (int i = 0; i < mSelectedImageList.size(); i++) {
                ImageModle imageModle = mSelectedImageList.get(i);
                mSupport.deleteImage(imageModle);
                //删除成功!!
            }
            removePhoto(mSelectedImageList);
            if (mView != null) {
                mView.bindData(mImageGroupModleList);
            }
        }
    }

    @Override
    public void handleRename() {
        if (mSupport != null) {
            for (int i = 0; i < mSelectedImageList.size(); i++) {
                ImageModle imageModle = mSelectedImageList.get(i);
                mSupport.renameImage(imageModle);
            }
        }
    }

    @Override
    public void handleDeletedBg(ImageModle imageModle) {
        if (imageModle != null) {
            if (mSupport != null) {
                mSupport.deleteImageInDb(imageModle);
            }
            //根据选中的图片 移除指定位置的图片
            Iterator<ImageGroupModle> i = mImageGroupModleList.iterator();
            OUT:
            while (i.hasNext()) {
                ImageGroupModle imageGroupModle = i.next();
                Iterator<List<ImageModle>> j = imageGroupModle.mImageModleList.iterator();
                while (j.hasNext()) {
                    List<ImageModle> tmpImageModleList = j.next();
                    Iterator<ImageModle> k = tmpImageModleList.iterator();
                    while (k.hasNext()) {
                        ImageModle tempImageModle = k.next();
                        if (imageModle.mImagePath.equals(tempImageModle.mImagePath)) {
                            k.remove();
                            //找到需要删除的数据 直接跳出整个循环
                            break OUT;
                        }
                    }
                }
            }
            //移动后面的图片来覆盖之前的位置
            Iterator<ImageGroupModle> i1 = mImageGroupModleList.iterator();
            while (i1.hasNext()) {
                ImageGroupModle imageGroupModle = i1.next();
                List<List<ImageModle>> j1 = imageGroupModle.mImageModleList;
                int size = j1.size();
                for (int i2 = 0; i2 < size; i2 ++) {
                    List<ImageModle> curList = j1.get(i2);
                    int nextPos = i2 + 1;
                    if (nextPos >= size) {
                        break;
                    }
                    List<ImageModle> nextList = j1.get(nextPos);
                    if (curList.size() > 0) {
                        int endPos = curList.size() - 1;
                        for (int startPos = endPos; startPos < 2; startPos++) {
                            if (nextList.size() > 0) {
                                curList.add(nextList.get(0));
                                nextList.remove(0);
                            }
                        }
                    }
                }

                Iterator<List<ImageModle>> j = imageGroupModle.mImageModleList.iterator();
                while (j.hasNext()) {
                    List<ImageModle> tmpImageModleList = j.next();
                    //如果列表为空则 直接移除
                    if (tmpImageModleList.size() == 0) {
                        j.remove();
                    }
                }
                //如果列表没有数据 则标题也删除
                if (imageGroupModle.mImageModleList.size() == 0 ) {
                    i1.remove();
                }
            }
            if (mView != null) {
                mView.notifyViewChg();
            }
        }
    }

    //删除图片
    private void removePhoto(List<ImageModle> imageModleList) {
        //根据选中的图片 移除指定位置的图片
        Iterator<ImageGroupModle> i = mImageGroupModleList.iterator();
        while (i.hasNext()) {
            ImageGroupModle imageGroupModle = i.next();
            Iterator<List<ImageModle>> j = imageGroupModle.mImageModleList.iterator();
            while (j.hasNext()) {
                List<ImageModle> tmpImageModleList = j.next();
                Iterator<ImageModle> k = tmpImageModleList.iterator();
                while (k.hasNext()) {
                    ImageModle tempImageModle = k.next();
                    if (imageModleList.contains(tempImageModle)) {
                        k.remove();
                    }
                }
            }
        }
        //移动后面的图片来覆盖之前的位置
        Iterator<ImageGroupModle> i1 = mImageGroupModleList.iterator();
        while (i1.hasNext()) {
            ImageGroupModle imageGroupModle = i1.next();
            List<List<ImageModle>> j1 = imageGroupModle.mImageModleList;
            int size = j1.size();
            for (int i2 = 0; i2 < size; i2 ++) {
                List<ImageModle> curList = j1.get(i2);
                int nextPos = i2 + 1;
                if (nextPos >= size) {
                    break;
                }
                List<ImageModle> nextList = j1.get(nextPos);
                if (curList.size() > 0) {
                    int endPos = curList.size() - 1;
                    for (int startPos = endPos; startPos < 2; startPos++) {
                        if (nextList.size() > 0) {
                            curList.add(nextList.get(0));
                            nextList.remove(0);
                        }
                    }
                }
            }

            Iterator<List<ImageModle>> j = imageGroupModle.mImageModleList.iterator();
            while (j.hasNext()) {
                List<ImageModle> tmpImageModleList = j.next();
                //如果列表为空则 直接移除
                if (tmpImageModleList.size() == 0) {
                    j.remove();
                }
            }
            //如果列表没有数据 则标题也删除
            if (imageGroupModle.mImageModleList.size() == 0 ) {
                i1.remove();
            }
        }
    }
}
