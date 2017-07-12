package com.jb.filemanager.function.trash.adapter;

import android.app.Activity;
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
import com.jb.filemanager.function.scanframe.bean.common.FileType;
import com.jb.filemanager.function.scanframe.bean.common.god.BaseChildBean;
import com.jb.filemanager.function.scanframe.bean.common.itemcommon.GroupType;
import com.jb.filemanager.function.scanframe.bean.common.itemcommon.ItemBean;
import com.jb.filemanager.function.scanframe.bean.common.subitemcommon.SubItemBean;
import com.jb.filemanager.function.scanframe.bean.residuebean.FolderBean;
import com.jb.filemanager.function.scanframe.bean.residuebean.ResidueBean;
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
import com.jb.filemanager.util.imageloader.IconLoader;
import com.jb.filemanager.util.imageloader.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.jb.filemanager.function.scanframe.bean.common.itemcommon.GroupType.BIG_FILE;
import static com.jb.filemanager.util.file.FileSizeFormatter.formatFileSize;

/**
 * Desc: 不包含第三级列表的adapter
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/7/10 15:16
 */

public class NewCleanListAdapter extends AbsAdapter<CleanGroupsBean> {

    public NewCleanListAdapter(List<CleanGroupsBean> groups) {
        super(groups);
    }

    private CleanEventManager mEventManager;
    private TrashItemDetailDialog mItemDetailDialog;
    private TrashSubItemDetailDialog mSubItemDetailDialog;
    private TrashIgnoreDialog mIgnoreDialog;
    /**
     * 跳转应用详情页的应用包名
     */
    private String mSysCachePackageNameForDetails = "";
    private boolean mCheckButtonEnabled = true;


    private Activity mActivity;

    public NewCleanListAdapter(List<CleanGroupsBean> groups, Activity ctx) {
        super(groups, ctx.getApplicationContext());
        mActivity = ctx;
        //mFragment = frg;
        mEventManager = CleanEventManager.getInstance();
        initDialog(ctx);
        CleanProgressDoneEvent.cleanAllDone();
    }

    /**
     * 初始化弹窗
     */
    private void initDialog(Activity act) {
        mItemDetailDialog = new TrashItemDetailDialog(act, true);
        mSubItemDetailDialog = new TrashSubItemDetailDialog(act, true);
        mIgnoreDialog = new TrashIgnoreDialog(act, true);
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
        GroupViewHolder holder = null;
        if (convertView != null) {
            holder = (GroupViewHolder) convertView.getTag(R.layout.item_clean_trash_group);
        }
        if (holder == null) {
            holder = new GroupViewHolder();
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
        this.notifyDataSetChanged();
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
        final ItemBean item = (ItemBean) child;
        convertView = onGetItemView(convertView, parent,
                group, item);
        return convertView;
    }

    private View onGetItemView(View convertView,
                               ViewGroup parent, final CleanGroupsBean group,
                               final ItemBean item) {
        // 1. 初始化View对象
        ChildViewHolder holder = null;
        if (convertView != null) {
            holder = (ChildViewHolder) convertView
                    .getTag(R.layout.item_clean_trash_child);
        }
        if (holder == null) {
            holder = new ChildViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_clean_trash_child, parent, false);
            holder.mRoot = convertView.findViewById(R.id.item_child_item);
            holder.mIcon = (ImageView) convertView
                    .findViewById(R.id.item_child_iv);
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
            convertView.setTag(R.layout.item_clean_trash_child, holder);
        }

        holder.mSelectBox.setState(item.getState());
        holder.mSelectBox.setVisibility(group.getGroupType().equals(
                GroupType.SYS_CACHE) ? View.GONE : View.VISIBLE);
        holder.mSelectBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleItemCheck(group, item);
            }
        });


        // 2. 初始化界面
        holder.mTitle.setText(getChildTitle(item));
        final FileSizeFormatter.FileSize size = formatFileSize(item.getSize());
        String result = ConvertUtils.formatFileSize(item.getSize());
        holder.mSize.setText(result);

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
//                if (item.getSubItemList().isEmpty()) {
                    onItemClick(item, size);
//                } else {
//                    ArrayList<SubItemBean> subItemList = item
//                            .getSubItemList();
//                    int itemPosition = children.indexOf(item);
//                    if (item.isExpand()) {
//                        children.removeAll(subItemList);
//                    } else {
//                        for (int i = 0; i < subItemList.size(); i++) {
//                            children.add(itemPosition + 1 + i,
//                                    subItemList.get(i));
//                        }
////                        StatisticsTools.uploadClickData(StatisticsConstants.JUNK_SUB_OPEN);
//                    }
//                    item.setIsExpand(!item.isExpand());
//                    notifyDataSetChanged();
//                }
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

    @SuppressWarnings("deprecation")
    private void onItemClick(final ItemBean child, final FileSizeFormatter.FileSize size) {
        if (mEventManager.getCleanState().equals(CleanStateEvent.DELETE_ING)) {
            return;
        }
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
                }
            }
        });
    }

    private static class GroupViewHolder {
        private View mRoot;
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

    }

    private static class ChildViewHolder {
        private View mRoot;
        private TextView mTitle;
        private TextView mSize;
        private ImageView mIcon;
        /**
         * Group的多选框
         */
        private GroupSelectBox mSelectBox;
    }
}
