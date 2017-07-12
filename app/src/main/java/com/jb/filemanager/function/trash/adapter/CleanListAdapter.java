package com.jb.filemanager.function.trash.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.commomview.GroupSelectBox;
import com.jb.filemanager.commomview.ProgressWheel;
import com.jb.filemanager.function.applock.adapter.AbsAdapter;
import com.jb.filemanager.function.fileexplorer.FileBrowserActivity;
import com.jb.filemanager.function.scanframe.bean.CleanGroupsBean;
import com.jb.filemanager.function.scanframe.bean.adbean.AdBean;
import com.jb.filemanager.function.scanframe.bean.cachebean.AppCacheBean;
import com.jb.filemanager.function.scanframe.bean.cachebean.SysCacheBean;
import com.jb.filemanager.function.scanframe.bean.cachebean.subitem.SubAppCacheBean;
import com.jb.filemanager.function.scanframe.bean.cachebean.subitem.SubSysCacheBean;
import com.jb.filemanager.function.scanframe.bean.common.FileType;
import com.jb.filemanager.function.scanframe.bean.common.god.BaseChildBean;
import com.jb.filemanager.function.scanframe.bean.common.itemcommon.GroupType;
import com.jb.filemanager.function.scanframe.bean.common.itemcommon.ItemBean;
import com.jb.filemanager.function.scanframe.bean.common.subitemcommon.SubItemBean;
import com.jb.filemanager.function.scanframe.bean.residuebean.FolderBean;
import com.jb.filemanager.function.scanframe.bean.residuebean.ResidueBean;
import com.jb.filemanager.function.scanframe.clean.CleanConstants;
import com.jb.filemanager.function.scanframe.clean.CleanEventManager;
import com.jb.filemanager.function.scanframe.clean.CleanManager;
import com.jb.filemanager.function.scanframe.clean.event.CleanCheckedFileSizeEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanNoneCheckedEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanProgressDoneEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanStateEvent;
import com.jb.filemanager.function.trash.dialog.TrashIgnoreDialog;
import com.jb.filemanager.function.trash.dialog.TrashItemDetailDialog;
import com.jb.filemanager.function.trash.dialog.TrashSubItemDetailDialog;
import com.jb.filemanager.util.ConvertUtils;
import com.jb.filemanager.util.FileTypeUtil;
import com.jb.filemanager.util.IntentUtil;
import com.jb.filemanager.util.StorageUtil;
import com.jb.filemanager.util.file.FileSizeFormatter;
import com.jb.filemanager.util.file.FileUtil;
import com.jb.filemanager.util.imageloader.IconLoader;
import com.jb.filemanager.util.imageloader.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private TrashItemDetailDialog mItemDetailDialog;
    private TrashSubItemDetailDialog mSubItemDetailDialog;
    private TrashIgnoreDialog mIgnoreDialog;
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
        mItemDetailDialog = new TrashItemDetailDialog(act, true);
        mSubItemDetailDialog = new TrashSubItemDetailDialog(act, true);
//
        mIgnoreDialog = new TrashIgnoreDialog(act, true);
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

        /**
         * Group的加载动画
         */
        private ProgressWheel mProgress;
        /**
         * Group的多选框
         */
        private GroupSelectBox mSelectBox;
        private TextView mSize;

        /**
         * Item的选择框
         */
        private ItemCheckBox mCheckBox;
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

            holder.mTitle = (TextView) convertView
                    .findViewById(R.id.item_group_name);
            holder.mProgress = (ProgressWheel) convertView
                    .findViewById(R.id.item_group_pb);
            holder.mSelectBox = (GroupSelectBox) convertView
                    .findViewById(R.id.item_group_iv_select);
            holder.mSize = (TextView) convertView
                    .findViewById(R.id.item_group_size);
            holder.mSelectBox
                    .setImageSource(R.drawable.select_none,
                            R.drawable.select_multi,
                            R.drawable.select_all);
            convertView.setTag(R.layout.item_clean_trash_group, holder);
        }
        // 2. 初始化界面
        final CleanGroupsBean group = getGroup(groupPosition);
        holder.mTitle.setText(group.getTitle());
        String result = ConvertUtils.formatFileSize(group.getSize());
        holder.mSize.setText(result);
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
        return false;
    }

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
                    .setImageSource(R.drawable.select_none,
                            R.drawable.select_multi,
                            R.drawable.select_all);
            holder.mSize = (TextView) convertView
                    .findViewById(R.id.item_child_size);
            //holder.mAppItemForeground = convertView.findViewById(R.id.clean_main_list_item_foreground);
            convertView.setTag(R.layout.item_clean_trash_child, holder);
        }

        // 2. 初始化界面
        holder.mTitle.setText(getChildTitle(item));
        final FileSizeFormatter.FileSize size = formatFileSize(item.getSize());
        String result = ConvertUtils.formatFileSize(item.getSize());
        holder.mSize.setText(result);
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
                break;
            default:
                IconLoader.getInstance().cancelShowImage(holder.mIcon);
                ImageLoader.getInstance(mContext).cancelShowImage(holder.mIcon);
                holder.mIcon.setImageResource(item.getGroupType().getChildIconId());
                break;
        }

        // 3. 更新Item的点击相应
        final List children = group.getChildren();
        holder.mRoot.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                if (item.getSubItemList().isEmpty()) {
                    onItemClick(item, size);
                } else {
                    ArrayList<SubItemBean> subItemList = item
                            .getSubItemList();
                    int itemPosition = children.indexOf(item);
                    if (item.isExpand()) {
                        children.removeAll(subItemList);
                    } else {
                        for (int i = 0; i < subItemList.size(); i++) {
                            children.add(itemPosition + 1 + i,
                                    subItemList.get(i));
                        }
//                        StatisticsTools.uploadClickData(StatisticsConstants.JUNK_SUB_OPEN);
                    }
                    item.setIsExpand(!item.isExpand());
                    notifyDataSetChanged();
                }
            }
        });
        holder.mRoot.setOnLongClickListener(new View.OnLongClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean onLongClick(View v) {
                switch (group.getGroupType()) {
                    case APP_CACHE:
                        if (item instanceof AppCacheBean) {
//                            StatisticsTools.uploadClickData(StatisticsConstants.CLEAN_RAB_POP);
                            mIgnoreDialog.setName(item.getTitle());
                            mIgnoreDialog.setAppIcon(item.getPath());
//                            mIgnoreDialog.setContentText(Html.fromHtml(mContext.getString(R.string.ignore_dialog_content, item.getTitle())));
                            mIgnoreDialog.show();
                            mIgnoreDialog.setOnConfirmListener(new TrashIgnoreDialog.OnConfirmListener() {
                                @Override
                                public void onConfirm(boolean isConfirm) {
                                    if (isConfirm) {
                                        AppCacheBean bean = (AppCacheBean) item;
                                        CleanManager.getInstance(mContext).addCacheAppIgnore(bean);
                                        if (bean.isExpand()) {
                                            children.removeAll(bean.getSubItemList());
                                        }
                                        children.remove(item);
                                        if (!bean.isNoneSelected()) {
                                            // 更新一级状态
                                            group.updateStateByItem();
                                            notifyCheckedStateChange();
                                        }
                                        if (children.isEmpty()) {
                                            removeGroup(group);
                                        }
                                        notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                        break;
                    case RESIDUE:
//                        StatisticsTools.uploadClickData(StatisticsConstants.CLEAN_RAB_POP);
                        mIgnoreDialog.setAppIcon(GroupType.RESIDUE.getChildIconId());
                        mIgnoreDialog.setName(item.getTitle());
//                        mIgnoreDialog.setContentText(Html.fromHtml(mContext.getString(R.string.ignore_dialog_content, item.getTitle())));
                        mIgnoreDialog.show();
                        mIgnoreDialog.setOnConfirmListener(new TrashIgnoreDialog.OnConfirmListener() {
                            @SuppressWarnings("ConstantConditions")
                            @Override
                            public void onConfirm(boolean isConfirm) {
                                if (isConfirm) {
                                    ResidueBean bean = (ResidueBean) item;
                                    CleanManager.getInstance(mContext).addResidueIgnore(bean);
                                    children.remove(item);
                                    if (!bean.isNoneSelected()) {
                                        // 更新一级状态
                                        group.updateStateByItem();
                                        notifyCheckedStateChange();
                                    }
                                    if (children.isEmpty()) {
                                        removeGroup(group);
                                    }
                                    notifyDataSetChanged();
                                }
                            }
                        });
                        break;
                    case AD:
//                        StatisticsTools.uploadClickData(StatisticsConstants.CLEAN_RAB_POP);
                        mIgnoreDialog.setName(item.getTitle());
                        mIgnoreDialog.setAppIcon(GroupType.AD.getChildIconId());
//                        mIgnoreDialog.setContentText(Html.fromHtml(mContext.getString(R.string.ignore_dialog_content, item.getTitle())));
                        mIgnoreDialog.show();
                        mIgnoreDialog.setOnConfirmListener(new TrashIgnoreDialog.OnConfirmListener() {
                            @SuppressWarnings("ConstantConditions")
                            @Override
                            public void onConfirm(boolean isConfirm) {
                                if (isConfirm) {
                                    AdBean bean = (AdBean) item;
                                    CleanManager.getInstance(mContext).addAdIgnore(bean);
                                    children.remove(item);
                                    if (!bean.isNoneSelected()) {
                                        // 更新一级状态
                                        group.updateStateByItem();
                                        notifyCheckedStateChange();
                                    }
                                    if (children.isEmpty()) {
                                        removeGroup(group);
                                    }
                                    notifyDataSetChanged();
                                }
                            }
                        });
                        break;
                    case SYS_CACHE:
                    case TEMP:
                    case APK:
                    case BIG_FILE:
                    case MEMORY:
                        onItemClick(item, size);
                    default:
                        break;
                }
                return true;
            }
        });
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
        if (isUpdateCheckBoxDirectly) {
            updateItemSelectBox(group, child);
        }
    }

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
            holder.mCheckBox.setImageRes(R.drawable.select_none,
                    R.drawable.select_all);
            holder.mSize = (TextView) convertView
                    .findViewById(R.id.item_child_child_size);

            convertView.setTag(R.layout.item_clean_trash_child_child,
                    holder);
        }
        // 2. 初始化View
        final List children = group.getChildren();
//        holder.mRoot.setBackgroundResource(R.drawable.common_list_item_gray_white_selector);
        holder.mCheckBox.setImageRes(R.drawable.select_none,
                R.drawable.select_all);
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
        String result = ConvertUtils.formatFileSize(subItem.getSize());
        holder.mSize.setText(result);


        holder.mRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subItem instanceof SubAppCacheBean) {
                    onAppCacheSubItemClick(item, (SubAppCacheBean) subItem);
                } else if (subItem instanceof SubSysCacheBean) {
                    onSysCacheSubItemClick((SubSysCacheBean) subItem);
                }
            }
        });
        holder.mRoot.setOnLongClickListener(new View.OnLongClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean onLongClick(View v) {
                if (item instanceof AppCacheBean && subItem instanceof SubAppCacheBean) {
//                    StatisticsTools.uploadClickData(StatisticsConstants.CLEAN_RAB_POP);
                    mIgnoreDialog.setAppIcon(item.getPath());
                    mIgnoreDialog.setName(subItem.getTitle());
//                    mIgnoreDialog.setContentText(Html.fromHtml(mContext.getString(R.string.ignore_dialog_content, subItem.getTitle())));
                    mIgnoreDialog.show();
                    mIgnoreDialog.setOnConfirmListener(new TrashIgnoreDialog.OnConfirmListener() {
                        @Override
                        public void onConfirm(boolean isConfirm) {
                            if (isConfirm) {
                                AppCacheBean appCacheBean = (AppCacheBean) item;
                                SubAppCacheBean subAppCacheBean = (SubAppCacheBean) subItem;
                                CleanManager.getInstance(mContext).addCachePathIgnore(appCacheBean, subAppCacheBean);
                                if (appCacheBean.isExpand()) {
                                    children.remove(subAppCacheBean);
                                }

                                if (appCacheBean.getSubItemList().isEmpty()) {
                                    // 三级列表为空，移除二级节点
                                    children.remove(appCacheBean);
                                }
                                if (children.isEmpty()) {
                                    // 二级列表，移除一级节点
                                    removeGroup(group);
                                } else {
                                    // 根据对象tree的叶子更新整个tree
                                    group.updateStateBySubItem();
                                }
                                notifyCheckedStateChange();
                                notifyDataSetChanged();
                            }
                        }
                    });
                }
                return true;
            }
        });

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

    @SuppressWarnings("deprecation")
    private void onItemClick(final ItemBean child, final FileSizeFormatter.FileSize size) {
        if (mEventManager.getCleanState().equals(CleanStateEvent.DELETE_ING)) {
            return;
        }
//        if (child.getGroupType() == GroupType.MEMORY) {
//            // 内存模块特殊处理
//            CleanMemoryBean cleanMemoryBean = (CleanMemoryBean) child;
//            AddtoIgnorelistDialog dialog = buildDialog(cleanMemoryBean
//                    .getRunningAppModle());
//            dialog.show();
//            // 统计
//            StatisticsTools.uploadClickData(StatisticsConstants.JUNK_MB_BOM);
//            return;
//        }
        // 1. 更新对话框文案
        String path = child.getPath();
        Set<String> sdPaths = StorageUtil.getAllExternalPaths(mContext);
        for (String sdPath : sdPaths) {
            path = path.replace(sdPath, "");
        }
        mItemDetailDialog.setTitleText(getChildTitle(child));
        mItemDetailDialog.setMessage1(mContext.getResources().getString(R.string.size)
                + " : " + size.toFullString());
        if (child.getGroupType() == GroupType.RESIDUE
                || child.getGroupType() == GroupType.AD) {
            FolderBean folderBean = (FolderBean) child;
            mItemDetailDialog.setMessage2(mContext.getResources().getString(
                    R.string.clean_dialog_message_contain) + " : " + folderBean.getFolderCount() + " " + mContext.getResources().getString(
                    R.string.clean_dialog_message_folder) + " , " + folderBean.getFileCount() + " " + mContext.getResources().getString(
                    R.string.clean_dialog_message_file));
        } else {
            mItemDetailDialog.setMessage2(null);
            path = path.replace(child.getTitle(), "");
        }

        if (child.getGroupType() == GroupType.SYS_CACHE) {
            mItemDetailDialog.setMessage3(null);
        } else {
            mItemDetailDialog.setMessage3(mContext.getResources().getString(
                    R.string.clean_dialog_message_path)
                    + " : " + path);
        }
        mItemDetailDialog.show();
//        StatisticsTools.uploadClickData(StatisticsConstants.DET_DIA_SHOW);

        File file = new File(child.getPath());
        final boolean isFile = file.exists() && file.isFile();
        final FileType fileType = FileTypeUtil.getFileType(child.getPath());
        // 根据文件类型更新弹窗文案
        if (isFile) {
            switch (fileType) {
                case DOCUMENT:
                case APK:
                case IMAGE:
                case COMPRESSION:
                    mItemDetailDialog.setOkText(R.string.clean_dialog_file_yes_btn);
                    break;
                case MUSIC:
                case VIDEO:
                    mItemDetailDialog
                            .setOkText(R.string.clean_dialog_media_yes_btn);
                    break;
                default:
                    mItemDetailDialog
                            .setOkText(R.string.clean_dialog_folder_yes_btn);
                    break;
            }
        } else {
            if (child.getGroupType() == GroupType.SYS_CACHE) {
                mItemDetailDialog
                        .setOkText(R.string.clean_dialog_app_cache_yes_btn);
            } else {
                mItemDetailDialog
                        .setOkText(R.string.clean_dialog_folder_yes_btn);
            }
        }
        // 2. 更新对话框点击事件
        mItemDetailDialog.setOnConfirmListener(new TrashIgnoreDialog.OnConfirmListener() {
            @Override
            public void onConfirm(boolean isConfirm) {
                if (!isConfirm) {
                    return;
                }
                if (isFile && !fileType.equals(FileType.OTHER)) {
                    if (!IntentUtil.openFileWithIntent(mContext, fileType,
                            child.getPath())) {
                        FileBrowserActivity.browserFile(mContext,
                                child.getTitle(), child.getPath());
                    }
//                    StatisticsTools.uploadClickData(StatisticsConstants.DET_FO_OPEN);
                } else {
                    if (isFile) {
                        FileBrowserActivity.browserFile(mContext,
                                child.getTitle(), child.getPath());
                    } else {
                        HashSet<String> paths = child.getPaths();
                        String[] array = paths.toArray(new String[paths.size()]);
                        FileBrowserActivity.browserDirs(mContext,
                                child.getTitle(), array);
                    }
//                    StatisticsTools.uploadClickData(StatisticsConstants.DET_VIEW_CLI);
                }
            }
        });
    }

    private void onAppCacheSubItemClick(ItemBean item,
                                        final SubAppCacheBean subItem) {
        mSubItemDetailDialog.setTitleText(subItem.getTitle());

        String desc = subItem.getDesc();
        Spanned message1;
        if (TextUtils.isEmpty(desc)) {
            String itemTitle = item != null ? item.getTitle() : "";
            // 若描述为空，则自己用应用名和标题进行拼接
            desc = Html.fromHtml(
                    String.format(mContext
                                    .getString(R.string.clean_subitem_detail_connect),
                            subItem.getTitle(), itemTitle)).toString();
        }

        // 获取警告级别对对应的翻译内容
        int warnDescObject = mSubItemDescMap.get(subItem.getWarnLv());
        int warnDesc = warnDescObject == 0 ? mSubItemDescMap.get(0)
                : warnDescObject;
        desc += mContext.getString(R.string.common_comma);
        if (subItem.getWarnLv() >= 10) {
            // 根据警告级别显示不同的提示
            String pre = String.format(mContext
                            .getString(R.string.clean_subitem_detail_warn_desc_pre),
                    desc);
            String all = String.format(mContext.getString(warnDesc), pre);
            message1 = Html.fromHtml(all);
        } else {
            message1 = Html.fromHtml(String.format(
                    mContext.getString(warnDesc), desc));
        }
        mSubItemDetailDialog.setMessage1(message1);
        mSubItemDetailDialog.setMessage2(mContext.getResources().getString(
                R.string.size)
                + " : "
                + formatFileSize(subItem.getSize())
                .toFullString());
//        Logger.e("Error", "size = " + subItem.getSize());
        StringBuilder builder = new StringBuilder();
        int fileCount = subItem.getFileCount();
        int folderCount = subItem.getFolderCount();
        builder.append(
                mContext.getResources().getString(
                        R.string.clean_dialog_message_contain))
                .append(" : ")
                .append(folderCount)
                .append(" ")
                .append(mContext.getResources().getString(
                        R.string.clean_dialog_message_folder))
                .append(" , ")
                .append(fileCount)
                .append(" ")
                .append(mContext.getResources().getString(
                        R.string.clean_dialog_message_file));
        mSubItemDetailDialog.setMessage3(builder.toString());
        final boolean isFile = fileCount == 1 && folderCount == 0;
        mSubItemDetailDialog
                .setOkText(isFile ? R.string.clean_dialog_file_yes_btn
                        : R.string.clean_dialog_folder_yes_btn);

        mSubItemDetailDialog.setOnConfirmListener(new TrashIgnoreDialog.OnConfirmListener() {
            @Override
            public void onConfirm(boolean isConfirm) {
                if (!isConfirm) {
                    return;
                }
//                StatisticsTools.uploadClickData(StatisticsConstants.JUNK_FINE_VIEW);
                if (isFile) {
                    FileBrowserActivity.browserFile(mContext,
                            subItem.getTitle(mContext), subItem.getPath());
                } else {
                    FileBrowserActivity.browserDirs(mContext,
                            subItem.getTitle(mContext),
                            FileUtil.removeEndSeparator(subItem.getPath()));
                }
            }
        });
        if (!mSubItemDetailDialog.isShowing()) {
            mSubItemDetailDialog.show();
//            StatisticsTools.uploadClickData(StatisticsConstants.JUNK_FINE_OPEN);
        }
    }

    @SuppressWarnings("deprecation")
    private void onSysCacheSubItemClick(final SubSysCacheBean bean) {
        mItemDetailDialog.setTitleText(bean.getTitle());
        mItemDetailDialog.setMessage1(mContext.getResources().getString(
                R.string.size)
                + " : "
                + formatFileSize(bean.getSize())
                .toFullString());
        mItemDetailDialog.setMessage2(null);
        mItemDetailDialog.setMessage3(null);
        mItemDetailDialog.setOkText(R.string.clean_dialog_app_cache_yes_btn);
        mItemDetailDialog.setOnConfirmListener(new TrashIgnoreDialog.OnConfirmListener() {
            @Override
            public void onConfirm(boolean isConfirm) {
                if (!isConfirm) {
                    return;
                }
                Intent intent = new Intent(
                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                mSysCachePackageNameForDetails = bean.getPackageName();
                Uri uri = Uri.fromParts("package",
                        mSysCachePackageNameForDetails, null);
                intent.setData(uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivityForResult(intent, CleanConstants.REQUEST_CODE_FOR_SYS_CACHE);
//                StatisticsTools.uploadClickData(StatisticsConstants.DET_MC_CLI);
            }
        });
        if (!mItemDetailDialog.isShowing()) {
            mItemDetailDialog.show();
        }
//        StatisticsTools.uploadClickData(StatisticsConstants.DET_DIA_SHOW);
    }
}