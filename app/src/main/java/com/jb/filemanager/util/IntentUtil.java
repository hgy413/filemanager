package com.jb.filemanager.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.jb.filemanager.function.scanframe.bean.common.FileType;
import com.jb.filemanager.function.scanframe.clean.FileInfo;

import java.io.File;

/**
 * 构建一些系统Intent的工具类
 *
 * @author lishen
 */
public class IntentUtil {

    /**
     * Android获取一个用于打开文本文件的intent
     *
     * @return
     * @path path
     * @path b
     */
    public static Intent getTextFileIntent(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(path));
        intent.setDataAndType(uri, "text/plain");
        return intent;
    }

    /**
     * Android获取一个用于打开所有文件类型的intent
     *
     * @return
     * @path path
     */
    public static Intent getAllIntent(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(path));
        intent.setDataAndType(uri, "*/*");
        return intent;
    }

    /**
     * Android获取一个用于打开APK文件的intent
     *
     * @return
     * @path path
     */
    public static Intent getApkFileIntent(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(path));
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        return intent;
    }

    /**
     * Android获取一个用于打开VIDEO文件的intent
     *
     * @param path
     * @return
     */
    public static Intent getVideoFileIntent(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(path));
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    /**
     * Android获取一个用于打开AUDIO文件的intent
     *
     * @param path
     * @return
     */
    public static Intent getAudioFileIntent(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(path));
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    /**
     * Android获取一个用于打开Html文件的intent
     *
     * @param path
     * @return
     */
    public static Intent getHtmlFileIntent(String path) {
        Uri uri = Uri.parse(path).buildUpon()
                .encodedAuthority("com.android.htmlfileprovider")
                .scheme("content").encodedPath(path).build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    /**
     * Android获取一个用于打开图片文件的intent
     *
     * @param path
     * @return
     */
    public static Intent getImageFileIntent(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(path));
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    /**
     * Android获取一个用于打开PPT文件的intent
     *
     * @param path
     * @return
     */
    public static Intent getPptFileIntent(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(path));
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    /**
     * Android获取一个用于打开Excel文件的intent
     *
     * @param path
     * @return
     */
    public static Intent getExcelFileIntent(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(path));
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    /**
     * Android获取一个用于打开Word文件的intent
     *
     * @param path
     * @return
     */
    public static Intent getWordFileIntent(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(path));
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    /**
     * Android获取一个用于打开CHM文件的intent
     *
     * @param path
     * @return
     */
    public static Intent getChmFileIntent(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(path));
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }

    /**
     * Android获取一个用于打开PDF文件的intent
     *
     * @param path
     * @return
     */
    public static Intent getPdfFileIntent(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(path));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

    /**
     * Android获取一个用于打开特定mimeType文件的intent
     *
     * @param path
     * @param mimeType
     * @return
     */
    public static Intent getFileIntent(String path, String mimeType) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(path));
        intent.setDataAndType(uri, mimeType);
        return intent;
    }

    /**
     * 通过扩展名获取文件的mime type
     *
     * @param extension
     * @return
     */
    private static String getMimeType(String extension) {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    /**
     * 使用系统的intent打开指定类型文件
     *
     * @param ctx
     * @param fi  文件信息对象
     * @return
     */
    public static boolean openFileWithIntent(Context ctx, FileInfo fi) {
        return openFileWithIntent(ctx, fi.getFileType(), fi.mPath);
    }

    /**
     * 使用系统的intent打开指定类型文件
     *
     * @param ctx
     * @param type 文件类型
     * @param path 文件路径
     * @return
     */
    public static boolean openFileWithIntent(Context ctx, FileType type,
                                             String path) {
        Intent intent = null;
        switch (type) {
            case DOCUMENT:
                intent = getTextFileIntent(path);
                break;
            case APK:
                intent = getApkFileIntent(path);
                break;
            case MUSIC:
                intent = getAudioFileIntent(path);
                break;
            case VIDEO:
                intent = getVideoFileIntent(path);
                break;
            case IMAGE:
                intent = getImageFileIntent(path);
                break;
            case COMPRESSION:
                final String mimeType = getMimeType(FileUtil.getExtension(path));
                if (!TextUtils.isEmpty(mimeType)) {
                    intent = getFileIntent(path, mimeType);
                }
                break;
            default:
                break;
        }
        boolean success = intent != null;
        if (success) {
            try {
                if (!(ctx instanceof Activity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                ctx.startActivity(intent);
            } catch (Exception e) {
                // 当没有应用可以处理的时候会抛出该错误
                success = false;
            }
        }
        return success;
    }

//	/**
//	 * 使用系统的intent打开指定类型文件,若打不开则弹Toast提示
//	 * @param ctx
//	 * @param type 文件类型
//	 * @param path 文件路径
//	 * @return
//	 */
//	public static void openFileAndToast(Context ctx, FileType type, String path) {
//		if (!openFileWithIntent(ctx, type, path)) {
//			Toast.makeText(ctx,
//					ctx.getResources().getString(R.string.no_app_to_open_file),
//					Toast.LENGTH_SHORT).show();
//		}
//	}

}