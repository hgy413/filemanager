package com.jb.filemanager.function.trash.adapter;

import android.app.Activity;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.commomview.GroupSelectBox;
import com.jb.filemanager.commomview.ProgressWheel;
import com.jb.filemanager.function.scanframe.bean.CleanGroupsBean;
import com.jb.filemanager.function.scanframe.bean.cachebean.AppCacheBean;
import com.jb.filemanager.function.scanframe.bean.cachebean.SysCacheBean;
import com.jb.filemanager.function.scanframe.bean.cachebean.subitem.SubSysCacheBean;
import com.jb.filemanager.function.scanframe.bean.common.FileType;
import com.jb.filemanager.function.scanframe.bean.common.god.BaseChildBean;
import com.jb.filemanager.function.scanframe.bean.common.itemcommon.GroupType;
import com.jb.filemanager.function.scanframe.bean.common.itemcommon.ItemBean;
import com.jb.filemanager.function.scanframe.bean.common.subitemcommon.SubItemBean;
import com.jb.filemanager.function.scanframe.bean.residuebean.ResidueBean;
import com.jb.filemanager.function.scanframe.clean.CleanEventManager;
import com.jb.filemanager.function.scanframe.clean.event.CleanCheckedFileSizeEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanNoneCheckedEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanProgressDoneEvent;
import com.jb.filemanager.function.trash.view.ShaderLine;
import com.jb.filemanager.util.ConvertUtils;
import com.jb.filemanager.util.file.FileSizeFormatter;
import com.jb.filemanager.util.imageloader.IconLoader;
import com.jb.filemanager.util.imageloader.ImageLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.jb.filemanager.commomview.GroupSelectBox.SelectState.MULT_SELECTED;
import static com.jb.filemanager.function.scanframe.bean.common.itemcommon.GroupType.BIG_FILE;
import static com.jb.filemanager.util.file.FileSizeFormatter.formatFileSize;


/**
 * 清理界面列表适配器
 *
 * @author chenbenbin
 */
@SuppressWarnings("Convert2Diamond")
public class CleanListAdapter extends AbsAdapter<CleanGroupsBean> {
    //private Fragment mFragment;
    private CleanEventManager mEventManager;
    //    private ResidueDialog mResidueDialog;
//    private ResidueDialog mResidueDialogNext;
//    private ResidueDialog mResidueDialogAll;
//    private ConfirmDialogStyle1 mGalleryDialog;
    /**
     * 跳转应用详情页的应用包名
     */
    private String mSysCachePackageNameForDetails = "";
    private boolean mCheckButtonEnabled = true;

    /**
     * 三级应用缓存数据：警告级别对应的提示语
     */
    private SparseIntArray mSubItemDescMap = new SparseIntArray() {
        {
            put(0, R.string.clean_subitem_detail_result_0);
            put(1, R.string.clean_subitem_detail_result_1);
            put(2, R.string.clean_subitem_detail_result_2);
            put(10, R.string.clean_subitem_detail_result_10);
        }
    };

    private Activity mActivity;

    public CleanListAdapter(List<CleanGroupsBean> groups, Activity ctx) {
        super(groups, ctx.getApplicationContext());
        mActivity = ctx;
        //mFragment = frg;
        mEventManager = CleanEventManager.getInstance();
        initDialog(ctx);
        CleanProgressDoneEvent.cleanAllDone();
    }

    public void setCheckButtonEnabled(boolean checkButtonEnabled) {
        mCheckButtonEnabled = checkButtonEnabled;
    }

    /**
     * 初始化弹窗
     */
    private void initDialog(Activity act) {

    }

    /**
     * ListView缓存
     *
     * @author chenbenbin
     */
    private class ViewHolder {
        private View mRoot;
        private ImageView mIcon;
        private ImageView mIndicator;
        private TextView mTitle;
        private ShaderLine mTopShader;
        private ShaderLine mBottomShader;
        /**
         * Group的加载动画
         */
        private ProgressWheel mProgress;
        /**
         * Group的多选框
         */
        private GroupSelectBox mSelectBox;
        private TextView mSize;
        private TextView mGroupSizeUnit;

        /**
         * Item的选择框
         */
        private ItemCheckBox mCheckBox;
        /**
         * Item的文件大小单位
         */
        private TextView mUnit;
        /**
         * 分割线
         */
        private View mDivider;
        /**
         * 前景色 内存所用
         **/
        private View mAppItemForeground;
    }

    //************************************************************** 一级 ***************************************************************//

    private List<Boolean> groupExpand = new ArrayList<>();

    @Override
    public View onGetGroupView(final int groupPosition, boolean isExpanded,
                               View convertView, ViewGroup parent) {
        if (groupPosition >= groupExpand.size()) {
            groupExpand.add(isExpanded);
        } else {
            groupExpand.set(groupPosition, isExpanded);
        }
        //Logger.e("CleanAdapter", "position = " + groupPosition + ">isExpanded = " + isExpanded);
        // 1. 初始化View对象
        ViewHolder holder = null;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag(R.layout.item_clean_trash_group);
        }
        if (holder == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_clean_trash_group, parent, false);
            holder.mIcon = (ImageView) convertView
                    .findViewById(R.id.item_group_iv);
            holder.mTitle = (TextView) convertView
                    .findViewById(R.id.item_group_name);
            holder.mProgress = (ProgressWheel) convertView
                    .findViewById(R.id.item_group_pb);
            holder.mSelectBox = (GroupSelectBox) convertView
                    .findViewById(R.id.item_group_iv_select);
            holder.mSize = (TextView) convertView
                    .findViewById(R.id.item_group_size);
            holder.mGroupSizeUnit = (TextView) convertView
                    .findViewById(R.id.item_group_size_unit);
            holder.mSelectBox
                    .setImageSource(R.drawable.choose_none,
                            R.drawable.choose_part,
                            R.drawable.choose_all);
            holder.mTopShader = (ShaderLine) convertView
                    .findViewById(R.id.item_group_top_shader);
            holder.mBottomShader = (ShaderLine) convertView
                    .findViewById(R.id.item_group_bottom_shader);
            convertView.setTag(R.layout.item_clean_trash_group, holder);
        }
        // 2. 初始化界面
        final CleanGroupsBean group = getGroup(groupPosition);
        holder.mIcon.setImageResource(group.getGroupType().getGroupIconId());
        holder.mTitle.setText(group.getTitle());
        String[] result = ConvertUtils.getFormatterTraffic(group.getSize());
        holder.mSize.setText(result[0]);
        holder.mGroupSizeUnit.setText(result[1]);
        holder.mSelectBox.setState(group.getState());
        holder.mSelectBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleGroupCheck(group);
            }
        });
        // 3. 更新逻辑状态
        if (group.isScanFinish()) {
            // 扫描结束，则进度圈执行完成动画
            holder.mProgress.setProgress(1);
            holder.mProgress.setSpinSpeed(1.5f);
        }
        if (group.isProgressFinish()) {
            // 进度圈动画结束，则隐藏进度圈，显示勾选框
            holder.mProgress.stopSpinning();
            holder.mProgress.setVisibility(View.GONE);
            holder.mSelectBox.setVisibility(View.VISIBLE);
        }
        holder.mProgress.setCallback(new ProgressWheel.ProgressCallback() {
            @Override
            public void onProgressUpdate(float progress) {
                if (progress == 1) {
                    GroupType groupType = group.getGroupType();
                    if (groupType != GroupType.APK
                            && groupType != BIG_FILE) {
                        // APK和大文件与临时文件都属于SD卡扫描，为了避免SD卡扫描完成
                        // 事件多次发送而引起流程问题，
                        // 将这个重任交给临时文件去发送
                        // 后期若三者关系有所调整，则需要进行修改
                        CleanProgressDoneEvent event = CleanProgressDoneEvent
                                .getEvent(groupType);
                        // 进度圈动画结束，则通知该项扫描结束
                        if (!event.isDone()) {
                            event.setDone(true);
                            mEventManager.sendProgressDoneEvent(event);
                            //Logger.w("CleanListAdapter", "发送-->" + event.name());
                        }
                    }
                }
            }
        });
        // top shader
        int aboveIndex = groupPosition - 1;
        if (aboveIndex >= 0 && aboveIndex < groupExpand.size()) {
            holder.mTopShader.setShaderVisibility(groupExpand.get(aboveIndex));
        } else {
            holder.mTopShader.setShaderVisibility(false);
        }
        // bottom shader
        holder.mBottomShader.setShaderVisibility(isExpanded);
        return convertView;
    }

    /**
     * 处理一级项的勾选逻辑
     */
    private void handleGroupCheck(final CleanGroupsBean group) {
        if (!mCheckButtonEnabled) {
            return;
        }
        // 是否直接更新勾选框的状态(即不需要弹窗)
        updateGroupSelectBox(group);
        /*boolean isUpdateCheckBoxDirectly;
        switch (group.getGroupType()) {
            case RESIDUE:
                isUpdateCheckBoxDirectly = handleResidueGroupCheck(group);
                break;
            default:
                isUpdateCheckBoxDirectly = true;
                break;
        }
        if (isUpdateCheckBoxDirectly) {
        }*/
    }

    /**
     * 处理残留一级的全选逻辑
     *
     * @param group 残留的组
     * @return 是否直接选中
     */
    @SuppressWarnings({"unchecked", "deprecation"})
    private boolean handleResidueGroupCheck(final CleanGroupsBean group) {
        if (group.isAllSelected()) {
            return true;
        }
        HashSet<FileType> fileType = new HashSet<FileType>();
        List<BaseChildBean> children = group.getChildren();
        // 包含敏感文件的残留数据队列
        final ArrayList<ResidueBean> senResidueList = new ArrayList<ResidueBean>();
        for (BaseChildBean child : children) {
            if (child.isTypeItem()) {
                ResidueBean residueBean = (ResidueBean) child;
                if (residueBean.isAllSelected()) {
                    continue;
                }
                HashSet<FileType> typeSet = residueBean.getFileType();
                if (!typeSet.isEmpty()) {
                    fileType.addAll(typeSet);
                    senResidueList.add(residueBean);
                } else {
                    mEventManager.sendCheckedFileSizeEvent(
                            CleanCheckedFileSizeEvent.ResidueFileSize,
                            residueBean.getSize());
                    residueBean.setCheck(true);
                }
            }
        }
        if (fileType.isEmpty()) {
            // 不包含特殊文件类型
            return true;
        } else if (children.size() != senResidueList.size()) {
            // 部分残留文件不包含敏感文件，则设置一级列表为多选
            group.setState(MULT_SELECTED);
            CleanListAdapter.this.notifyDataSetChanged();
        }
        notifyCheckedStateChange();
//        if (senResidueList.size() == 1) {
//            // 只要一个包含敏感的残留时，直接显示该弹窗
//            updateResidueDialog(mResidueDialog, senResidueList.get(0));
//            mResidueDialog
//                    .setOnConfirmListener(new ResidueDialog.OnConfirmListener() {
//                        @Override
//                        public void onConfirm(boolean isConfirm) {
//                            if (isConfirm) {
//                                StatisticsTools
//                                        .uploadClickData(StatisticsConstants.CLEAN_SEN_CHE);
//                                updateGroupSelectBox(group);
//                            }
//                        }
//                    });
//        } else {
//            mResidueDialogAll.setResidueBeanList(senResidueList);
//            StatisticsTools.uploadClickData(StatisticsConstants.CLEAN_SEN_FILE);
//            mResidueDialogAll.showDialog();
//            // 包含多个时，先弹总控制弹窗，再逐个弹出
//            mResidueDialogAll
//                    .setOnConfirmDetailListener(new ResidueDialog.OnConfirmDetailListener() {
//                        @Override
//                        public void onConfirm() {
//                            // 全部对话框逐个弹出
//                            mResidueDialogIndex = 0;
//                            showResidueAllDialog(group, senResidueList);
//                        }
//
//                        @Override
//                        public void onCancel() {
//                            // 全选
//                            updateGroupSelectBox(group);
//                        }
//
//                        @Override
//                        public void onBackPress() {
//                            // 取消
//                        }
//                    });
//        }
        return false;
    }

    /**
     * 多个残留对话框时候的下标
     */
    private int mResidueDialogIndex = 0;

    /**
     * 逐个弹出全部的残留窗口
     *
     * @param senResidueList 包含敏感文件的残留数据队列
     */
//    @SuppressWarnings("deprecation")
//    private void showResidueAllDialog(final CleanGroupsBean group,final ArrayList<ResidueBean> senResidueList) {
//        if (senResidueList.isEmpty()
//                || mResidueDialogIndex >= senResidueList.size()) {
//            return;
//        }
//        final ResidueBean bean = senResidueList.get(mResidueDialogIndex);
//        mResidueDialogNext.setCountText(mResidueDialogIndex + 1,
//                senResidueList.size());
//        updateResidueDialog(mResidueDialogNext, bean);
//
//        mResidueDialogNext
//                .setOnConfirmListener(new ResidueDialog.OnConfirmListener() {
//                    @Override
//                    public void onConfirm(boolean isConfirm) {
//                        final ResidueBean bean = senResidueList
//                                .get(mResidueDialogIndex);
//                        if (isConfirm) {
//                            StatisticsTools
//                                    .uploadClickData(StatisticsConstants.CLEAN_SEN_CHE);
//                            updateItemSelectBox(group, bean);
//                        }
//                        mResidueDialogIndex++;
//                        if (mResidueDialogIndex >= senResidueList.size()) {
//                            return;
//                        }
//                        mResidueDialogNext.setResidueBean(senResidueList
//                                .get(mResidueDialogIndex));
//                        mResidueDialogNext.setCountText(
//                                mResidueDialogIndex + 1, senResidueList.size());
//                        updateResidueDialog(mResidueDialogNext,
//                                senResidueList.get(mResidueDialogIndex));
//                    }
//                });
//    }

    /**
     * 更新残留文件提示框
     */
//    @SuppressWarnings("deprecation")
//    private void updateResidueDialog(ResidueDialog dialog, ResidueBean bean) {
//        StatisticsTools.uploadClickData(StatisticsConstants.CLEAN_SEN_FILE);
//        dialog.setResidueBean(bean);
//        dialog.showDialog();
//    }

    /**
     * 点击一级列表的勾选框
     */
    @SuppressWarnings("unchecked")
    private void updateGroupSelectBox(final CleanGroupsBean group) {
        // 更新自身状态
        group.switchState(group.getState());
        // 更新二级状态
        List<BaseChildBean> children = group.getChildren();
        for (BaseChildBean child : children) {
            if (child.isTypeItem()) {
                ItemBean item = (ItemBean) child;
                item.setState(group.getState());
                // 更新三级状态
                for (SubItemBean subItem : item.getSubItemList()) {
                    subItem.setChecked(group.isAllSelected());
                }
            }
        }
        CleanListAdapter.this.notifyDataSetChanged();
        notifyCheckedStateChange();
        // 通知勾选的文件大小总数发生改变
        long checkSize = group.isAllSelected() ? group.getSize() : 0;
        mEventManager.sendCheckedFileAllSizeEvent(
                CleanCheckedFileSizeEvent.get(group.getGroupType()), checkSize);
    }

    /**
     * 通知列表是否没有一项垃圾被勾选<br>
     * 用于更新底部按钮的状态
     */
    private void notifyCheckedStateChange() {
        boolean isEmpty = true;
        for (int i = 0; i < getGroupCount(); i++) {
            CleanGroupsBean groupBean = getGroup(i);
            isEmpty = groupBean.getState().equals(GroupSelectBox.SelectState.NONE_SELECTED);
            if (!isEmpty) {
                break;
            }
        }
        mEventManager
                .sendNoneFileCheckedEvent(isEmpty ? CleanNoneCheckedEvent.NONE
                        : CleanNoneCheckedEvent.NOT_NOTE);
    }

    //************************************************************** 二级 ***************************************************************//

    @Override
    public View onGetChildView(final int groupPosition, int childPosition,
                               boolean isLastChild, View convertView, ViewGroup parent) {
        final CleanGroupsBean group = getGroup(groupPosition);
        final BaseChildBean child = (BaseChildBean) getChild(groupPosition,
                childPosition);
        switch (child.getChildType()) {
            case ITEM:
                final ItemBean item = (ItemBean) child;
                convertView = onGetItemView(convertView, parent,
                        group, item);
                break;
            case SUB_ITEM:
                final SubItemBean subItem = (SubItemBean) child;
                final ItemBean item2 = getItemBySubItem(group, subItem);
                convertView = onGetSubItemView(convertView, parent,
                        group, item2, subItem);
                break;
            default:
                break;
        }
        return convertView;
    }

    /**
     * 根据三级项来获取对应的二级项
     */
    @SuppressWarnings("unchecked")
    private ItemBean getItemBySubItem(final CleanGroupsBean group,
                                      final SubItemBean subItem) {
        List<BaseChildBean> children = group.getChildren();
        for (BaseChildBean item : children) {
            if (item.getKey().equals(subItem.getKey())) {
                if (item.isTypeItem()) {
                    return (ItemBean) item;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private View onGetItemView(View convertView,
                               ViewGroup parent, final CleanGroupsBean group,
                               final ItemBean item) {
        // 1. 初始化View对象
        ViewHolder holder = null;
        if (convertView != null) {
            holder = (ViewHolder) convertView
                    .getTag(R.layout.item_clean_trash_child);
        }
        if (holder == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_clean_trash_child, parent, false);
            holder.mRoot = convertView.findViewById(R.id.item_child_item);
            holder.mIcon = (ImageView) convertView
                    .findViewById(R.id.item_child_iv);
            holder.mIndicator = (ImageView) convertView
                    .findViewById(R.id.item_child_indivator);
            holder.mTitle = (TextView) convertView
                    .findViewById(R.id.item_child_name);
            holder.mSelectBox = (GroupSelectBox) convertView
                    .findViewById(R.id.item_child_select_button);
            holder.mSelectBox
                    .setImageSource(R.drawable.choose_none,
                            R.drawable.choose_part,
                            R.drawable.choose_all);
            holder.mSize = (TextView) convertView
                    .findViewById(R.id.item_child_size);
            holder.mUnit = (TextView) convertView
                    .findViewById(R.id.item_child_size_unit);
            holder.mDivider = convertView.findViewById(R.id.item_trash_child_divider);
            //holder.mAppItemForeground = convertView.findViewById(R.id.clean_main_list_item_foreground);
            convertView.setTag(R.layout.item_clean_trash_child, holder);
        }

        // 2. 初始化界面
        holder.mTitle.setText(getChildTitle(item));
        final FileSizeFormatter.FileSize size = formatFileSize(item.getSize());
        String[] result = ConvertUtils.getFormatterTraffic(item.getSize());
        holder.mSize.setText(result[0]);
        holder.mUnit.setText(result[1]);
        holder.mSelectBox.setState(item.getState());
        holder.mSelectBox.setVisibility(group.getGroupType().equals(
                GroupType.SYS_CACHE) ? View.GONE : View.VISIBLE);
        holder.mSelectBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleItemCheck(group, item);
            }
        });
        // indicator
        holder.mIndicator.setVisibility(View.GONE);
        if (item instanceof SysCacheBean || item instanceof AppCacheBean) {
            holder.mIndicator.setVisibility(item.isExpand() ? View.GONE : View.VISIBLE);
        }
        // 前景色，只有内存才用
//        if (item.getGroupType() == GroupType.MEMORY
//                && item instanceof CleanMemoryBean) {
//            holder.mAppItemForeground.setVisibility(View.VISIBLE);
//            holder.mAppItemForeground
//                    .setBackgroundColor(((CleanMemoryBean) item).isIgnore() ? 0x80F6F6F6
//                            : 0);
//        } else {
//            holder.mAppItemForeground.setVisibility(View.GONE);
//        }
        switch (item.getGroupType()) {
            // 根据文件类型更新图标
            case APP_CACHE:
                if (item instanceof AppCacheBean) {
                    ImageLoader.getInstance(mContext).cancelShowImage(holder.mIcon);
                    IconLoader.getInstance().displayImage(item.getPath(),
                            holder.mIcon);
                } else {
                    IconLoader.getInstance().cancelShowImage(holder.mIcon);
                    ImageLoader.getInstance(mContext).cancelShowImage(holder.mIcon);
                    holder.mIcon.setImageResource(R.drawable.sys_cache);
                }
                break;
            case RESIDUE:
                holder.mIcon.setImageResource(R.drawable.child_residue);
                break;
            case AD:
                holder.mIcon.setImageResource(R.drawable.child_ad);
                break;
            case TEMP:
                holder.mIcon.setImageResource(R.drawable.child_temp);
                break;
            case APK:
                ImageLoader.getInstance(mContext).cancelShowImage(holder.mIcon);
                IconLoader.getInstance().displayImage(item.getPath(), holder.mIcon);
                break;
            case MEMORY:
                ImageLoader.getInstance(mContext).cancelShowImage(holder.mIcon);
                IconLoader.getInstance().displayImage(item.getPath(), holder.mIcon);
                break;
            case BIG_FILE:
                holder.mIcon.setImageResource(R.drawable.child_bf);
                //ImageLoader.getInstance(mContext).cancelShowImage(holder.mIcon);
                //IconLoader.getInstance().displayImage(item.getPath(), holder.mIcon);
//                FileType fileType = FileTypeUtil.getFileType(item.getPath());
//                switch (fileType) {
//                    case IMAGE:
//                    case VIDEO:
//                    case MUSIC:
//                        IconLoader.getInstance().cancelShowImage(holder.mIcon);
//                        ImageLoader.ImageLoaderBean bean = new ImageLoader.ImageLoaderBean(
//                                item.getPath(), holder.mIcon);
//                        bean.setDrawableId(item.getGroupType().getChildIconId());
//                        bean.setImageType(getImageType(fileType));
//                        bean.setShapeType(ImageLoaderBean.SHAPE_TYPE_ROUND);
//                        ImageLoader.getInstance(mContext).displayImage(bean);
//                        break;
//                    default:
//                        IconLoader.getInstance().cancelShowImage(holder.mIcon);
//                        ImageLoader.getInstance(mContext).cancelShowImage(holder.mIcon);
//                        holder.mIcon.setImageResource(item.getGroupType()
//                                .getChildIconId());
//                        break;
//                }
                break;
            default:
                IconLoader.getInstance().cancelShowImage(holder.mIcon);
                ImageLoader.getInstance(mContext).cancelShowImage(holder.mIcon);
                holder.mIcon.setImageResource(item.getGroupType().getChildIconId());
                break;
        }
//        holder.mRoot.setBackgroundResource(R.drawable.common_list_item_gray_white_selector);
        // 展开成三级列表时，分割线不显示
        holder.mDivider.setVisibility(item.isExpand() ? View.INVISIBLE : View.VISIBLE);
        if (group.getChildren().indexOf(item) == group.getchildrenSize() - 1) {
            //Logger.e("CleanAdapter", "indexOf = " + group.getChildren().indexOf(item) + ">size = " + group.getchildrenSize());
            holder.mDivider.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    /**
     * 处理子项的勾选逻辑
     */
    private void handleItemCheck(final CleanGroupsBean group,
                                 final ItemBean child) {
        if (!mCheckButtonEnabled) {
            return;
        }
        // 是否直接更新勾选框的状态(即不需要弹窗)
        boolean isUpdateCheckBoxDirectly = true;
//        switch (child.getGroupType()) {
//            case RESIDUE:
//                if (child.isAllSelected()) {
//                    // 选中状态
//                    isUpdateCheckBoxDirectly = true;
//                    break;
//                }
//                ResidueBean residueBean = (ResidueBean) child;
//                HashSet<FileType> fileType = residueBean.getFileType();
//                if (fileType.isEmpty()) {
//                    // 不包含敏感文件
//                    isUpdateCheckBoxDirectly = true;
//                    break;
//                }
                // 显示敏感文件的弹窗
//                mResidueDialog.setOkText(R.string.common_select);
//                mResidueDialog.setCancelText(R.string.common_cancel);
//                updateResidueDialog(mResidueDialog, residueBean);
//                mResidueDialog
//                        .setOnConfirmListener(new ResidueDialog.OnConfirmListener() {
//                            @SuppressWarnings("deprecation")
//                            @Override
//                            public void onConfirm(boolean isConfirm) {
//                                if (isConfirm) {
//                                    StatisticsTools
//                                            .uploadClickData(StatisticsConstants.CLEAN_SEN_CHE);
//                                    updateItemSelectBox(group, child);
//                                }
//                            }
//                        });
//                break;
//            case BIG_FILE:
//                FileBean bean = (FileBean) child;
//                if (bean.getFileFlag().equals(FileFlag.GALLERY_THUMBNAILS)
//                        && !bean.isCheck()) {
                    // 缩略图缓存清理提示框
//                    mGalleryDialog.setOnConfirmListener(new OnConfirmListener() {
//                        @Override
//                        public void onConfirm(boolean isConfirm) {
//                            if (isConfirm) {
//                                updateItemSelectBox(group, child);
//                            }
//                        }
//                    });
//                    mGalleryDialog.showDialog();
//                } else {
//                    isUpdateCheckBoxDirectly = true;
//                }
//                break;
//            default:
//                isUpdateCheckBoxDirectly = true;
//                break;
//        }
        if (isUpdateCheckBoxDirectly) {
            updateItemSelectBox(group, child);
        }
    }

//    private int getImageType(FileType type) {
//        switch (type) {
//            case MUSIC:
//                return ImageLoaderBean.IMAGE_TYPE_MUSIC;
//            case VIDEO:
//                return ImageLoaderBean.IMAGE_TYPE_VIDEO;
//            default:
//                return ImageLoaderBean.IMAGE_TYPE_PICTURE;
//        }
//    }

    private void updateItemSelectBox(final CleanGroupsBean group,
                                     final ItemBean item) {
        // 更新二级自身状态
        item.switchState(item.getState());
        // 更新三级状态
        for (SubItemBean subItem : item.getSubItemList()) {
            subItem.setChecked(item.isAllSelected());
        }
        // 更新一级状态
        group.updateStateByItem();
        long checkSize = item.isAllSelected() ? item.getSize() : -item
                .getSize();
        mEventManager.sendCheckedFileSizeEvent(
                CleanCheckedFileSizeEvent.get(group.getGroupType()), checkSize);
        notifyDataSetChanged();
        notifyCheckedStateChange();
    }

    /**
     * 获取子项的标题
     */
    private String getChildTitle(final ItemBean child) {
        if (child.getGroupType().equals(GroupType.RESIDUE)) {
            ResidueBean bean = (ResidueBean) child;
            return bean.getAppName();
        } else {
            return child.getTitle();
        }
    }

    public String getSysCachePackageNameForDetails() {
        return mSysCachePackageNameForDetails;
    }

    public void setSysCachePackageNameForDetails(String packageName) {
        mSysCachePackageNameForDetails = packageName;
    }

    /**
     * 内存的对话框
     */
//    private AddtoIgnorelistDialog buildDialog(final RunningAppModle app) {
//        AddtoIgnorelistDialog dialog = new AddtoIgnorelistDialog(
//                mFragment.getActivity(), app);
//        dialog.setOkButtonVisibility(View.GONE);
//        dialog.setOnButtonClickListener(new OnButtonClickListener() {
//            @Override
//            public void onMoreInfoClick() {
//                AppManagerUtils.openDetail(mFragment.getActivity(),
//                        app.mPackageName);
//            }
//
//            @Override
//            public void onButtonClick(boolean isOk) {
//            }
//        });
//        return dialog;
//    }

    //************************************************************** 三级 ***************************************************************//
    private View onGetSubItemView(View convertView,
                                  ViewGroup parent, final CleanGroupsBean group,
                                  final ItemBean item, final SubItemBean subItem) {
        // 1. 初始化View对象
        ViewHolder holder = null;
        if (convertView != null) {
            holder = (ViewHolder) convertView
                    .getTag(R.layout.item_clean_trash_child_child);
        }
        if (holder == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_clean_trash_child_child, parent, false);
            holder.mRoot = convertView.findViewById(R.id.item_child_child_root);
            holder.mIcon = (ImageView) convertView
                    .findViewById(R.id.item_child_child_iv);
            holder.mTitle = (TextView) convertView
                    .findViewById(R.id.item_child_child_name);
            holder.mCheckBox = (ItemCheckBox) convertView
                    .findViewById(R.id.item_child_child_select_button);
            holder.mCheckBox.setImageRes(R.drawable.choose_none,
                    R.drawable.choose_all);
            holder.mSize = (TextView) convertView
                    .findViewById(R.id.item_child_child_size);
            holder.mUnit = (TextView) convertView
                    .findViewById(R.id.item_child_child_size_unit);
            convertView.setTag(R.layout.item_clean_trash_child_child,
                    holder);
        }
        // 2. 初始化View
        final List children = group.getChildren();
//        holder.mRoot.setBackgroundResource(R.drawable.common_list_item_gray_white_selector);
        holder.mCheckBox.setImageRes(R.drawable.choose_none,
                R.drawable.choose_all);
        holder.mCheckBox.setChecked(subItem.isChecked());
        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSubItemCheckBox(group, subItem);
            }
        });

        if (subItem.isSysCache()) {
            holder.mCheckBox.setVisibility(View.GONE);
            SubSysCacheBean sysBean = (SubSysCacheBean) subItem;
            ImageLoader.getInstance(mContext).cancelShowImage(holder.mIcon);
            IconLoader.getInstance().displayImage(sysBean.getPackageName(),
                    holder.mIcon);
        } else {
            holder.mCheckBox.setVisibility(View.VISIBLE);
            IconLoader.getInstance().cancelShowImage(holder.mIcon);
            ImageLoader.getInstance(mContext).cancelShowImage(holder.mIcon);
            holder.mIcon.setImageResource(R.drawable.subitem_cache);
        }
        holder.mTitle.setText(subItem.getTitle());
        final FileSizeFormatter.FileSize size = formatFileSize(subItem.getSize());
        String[] result = ConvertUtils.getFormatterTraffic(subItem.getSize());
        holder.mSize.setText(result[0]);
        holder.mUnit.setText(result[1]);


        return convertView;
    }

    private void updateSubItemCheckBox(final CleanGroupsBean group,
                                       final SubItemBean subItem) {
        if (!mCheckButtonEnabled) {
            return;
        }
        subItem.setChecked(!subItem.isChecked());
        // 根据对象tree的叶子更新整个tree
        group.updateStateBySubItem();
        long checkSize = subItem.isChecked() ? subItem.getSize() : -subItem
                .getSize();
        mEventManager.sendCheckedFileSizeEvent(
                CleanCheckedFileSizeEvent.get(group.getGroupType()), checkSize);
        notifyDataSetChanged();
        notifyCheckedStateChange();
    }

}