package com.jb.filemanager.globaldata.bean;

import java.io.File;

/**
 * Created by xiaoyu on 2017/7/27 14:04.
 * <p>所有数据结构的基类</p>
 */

public class BaseDataBean {

    // 文件属性FLAG
    /** 图片文件 */
    public static final int FLAG_IMAGE      = 0x00000001;
    /** 视频文件 */
    public static final int FLAG_VIDEO      = 0x00000002;
    /** 音频文件 */
    public static final int FLAG_AUDIO      = 0x00000004;
    /** 压缩文件 */
    public static final int FLAG_ZIP        = 0x00000008;
    /** apk文件 */
    public static final int FLAG_APK        = 0x00000010;
    /** txt文件 */
    public static final int FLAG_TXT        = 0x00000020;
    /** pdf文件 */
    public static final int FLAG_PDF        = 0x00000040;
    /** doc(x)文件 */
    public static final int FLAG_DOC        = 0x00000080;
    /** 其他文件 */
    public static final int FLAG_OTHER      = 0x00000100;

    // 文件类别FLAG
    /** 下载文件 */
    public static final int FLAG_DOWNLOAD   = 0x00000200;
    /** 文档文件 */
    public static final int FLAG_DOCUMENT   = 0x00000400;
    /** 最近文件 */
    public static final int FLAG_RECENT     = 0x00000800;



    /** id(数据库存储unique_id) */
    public long id;
    /** 文件名(有扩展名) */
    public String fileName;
    /** 文件路径 */
    public String fullPath;
    /** 父路径 */
    public String parentPath;
    /** 父路径在数据库中索引 */
    public int parentIndex;
    /** 是否为Directory */
    public boolean isDirectory;
    /** 标记 */
    public int flag;
    /** 大小 */
    public long size;
    /** 修改时间 */
    public long lastModify;
    /** 是否选中(List列表) */
    public boolean isSelected = false;


    public BaseDataBean(File file, long id, int parentIndex) {
        this.id = id;
        this.fileName = file.getName();
        this.fullPath = file.getPath();
        this.parentPath = file.getParent();
        this.parentIndex = parentIndex;
        this.isDirectory = file.isDirectory();
        this.size = file.length();
    }

    /**
     * 判断该文件是否符合某种flag, 一个文件可能符合多种flag
     *
     * @param flag f
     * @return true or false
     */
    public boolean isFlag(int flag) {
        return (this.flag & flag) > 0;
    }

    @Override
    public String toString() {
        return "BaseDataBean{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", fullPath='" + fullPath + '\'' +
                ", parentPath='" + parentPath + '\'' +
                ", parentIndex=" + parentIndex +
                ", isDirectory=" + isDirectory +
                ", flag=" + flag +
                ", size=" + size +
                ", lastModify=" + lastModify +
                ", isSelected=" + isSelected +
                '}';
    }
}
