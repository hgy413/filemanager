package com.jb.filemanager.function.permissionalarm.utils;

import android.Manifest;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.StringRes;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nieyh on 2017/2/7.<br/> 权限操作工具
 */

public class PermissionHelper {

    private HashMap<String, Integer> mSensitivePermissions;

    public PermissionHelper() {
        mSensitivePermissions = new HashMap<>();

        /**********************第一等级*********************/
        mSensitivePermissions.put(Manifest.permission.WRITE_CONTACTS,
                R.string.permission_name_write_contacts);
        mSensitivePermissions.put(Manifest.permission.CALL_PHONE,
                R.string.permission_name_call_phone);
        mSensitivePermissions.put(Manifest.permission.PROCESS_OUTGOING_CALLS,
                R.string.permission_name_outgoing_call);
        if (Build.VERSION.SDK_INT >= 16) {
            mSensitivePermissions.put(Manifest.permission.WRITE_CALL_LOG,
                    R.string.permission_name_write_call_log);
        }
        mSensitivePermissions.put(Manifest.permission.SEND_SMS,
                R.string.permission_name_send_sms);

        /**********************第二等级*********************/
        mSensitivePermissions.put(Manifest.permission.CAMERA,
                R.string.permission_name_camera);
        mSensitivePermissions.put(Manifest.permission.ACCESS_FINE_LOCATION,
                R.string.permission_name_location);
        mSensitivePermissions.put(Manifest.permission.READ_SMS,
                R.string.permission_name_read_sms);
        mSensitivePermissions.put(Manifest.permission.RECEIVE_SMS,
                R.string.permission_name_receive_sms);
        mSensitivePermissions.put(Manifest.permission.READ_CONTACTS,
                R.string.permission_name_read_contacts);
        if (Build.VERSION.SDK_INT >= 16) {
            mSensitivePermissions.put(Manifest.permission.READ_CALL_LOG,
                    R.string.permission_name_read_call_log);
        }
        mSensitivePermissions.put(Manifest.permission.GET_ACCOUNTS,
                R.string.permission_name_get_account);

        /**********************第三等级*********************/
        mSensitivePermissions.put(Manifest.permission.INTERNET,
                R.string.permission_name_internet);
    }

    /**
     * 获取应用的所有权限
     */
    public synchronized String[] getAppPermission(String pkgName) {
        PackageManager packageManager = TheApplication.getAppContext().getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo != null) {
            String[] permissions = packageInfo.requestedPermissions;
            return /*Arrays.asList(*/permissions/*)*/;
        }
        return null;
    }

    /**
     * 获取敏感权限列表
     */
    public synchronized List<String> getSensitivePermissions(String[] permissions) {
        List<String> returnList = new ArrayList<>();
        if (permissions != null && permissions.length > 0) {
            for (String permission : permissions) {
                if (checkSinglePermission(permission)) {
                    returnList.add(permission);
                }
            }
        }
        if (returnList.size() > 0) {
            Collections.sort(returnList, new Comparator<String>() {
                @Override
                public int compare(String lhs, String rhs) {
                    int leftLevel = getLevel(lhs);
                    int rightLevel = getLevel(rhs);
                    if (leftLevel < rightLevel) {
                        return -1;
                    } else if (leftLevel > rightLevel) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
        }
        return returnList;
    }


    /**
     * @param newLists      新的总权限列表
     * @param oldPermissons 旧的总权限列表
     * @return 新增的权限列表
     */
    public synchronized List<String> getAddPermissions(String[] newLists, String[] oldPermissons) {
        List<String> addList = new ArrayList<>();
        for (String newPer : newLists) {
            boolean isNew = true;
            for (String oldPer : oldPermissons) {
                if (newPer.equalsIgnoreCase(oldPer)) {
                    isNew = false;
                    continue;
                }
            }
            if (isNew && checkSinglePermission(newPer)) {
                addList.add(newPer);
            }
        }
        return addList;
    }

    /**
     * 查看是否有新添加的权限
     */
    public synchronized boolean hasNewPermissionAdd(String[] oldPermissions, String[] permissions) {
        if (oldPermissions != null) {
            for (String newPer : permissions) {
                boolean isNew = true;
                for (String oldPer : oldPermissions) {
                    if (newPer.equalsIgnoreCase(oldPer)) {
                        isNew = false;
                        continue;
                    }
                }
                if (isNew && checkSinglePermission(newPer)) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取权限的级别
     */
    public synchronized int getLevel(String permission) {
        if (permission.equals(Manifest.permission.CALL_PHONE) ||
                permission.equals(Manifest.permission.WRITE_CONTACTS) ||
                permission.equals(Manifest.permission.PROCESS_OUTGOING_CALLS) ||
                permission.equals(Manifest.permission.WRITE_CALL_LOG) ||
                permission.equals(Manifest.permission.SEND_SMS)) {
            return 1;
        } else if (permission.equals(Manifest.permission.CAMERA) ||
                permission.equals(Manifest.permission.ACCESS_FINE_LOCATION) ||
                permission.equals(Manifest.permission.READ_SMS) ||
                permission.equals(Manifest.permission.RECEIVE_SMS) ||
                permission.equals(Manifest.permission.READ_CONTACTS) ||
                permission.equals(Manifest.permission.READ_CALL_LOG) ||
                permission.equals(Manifest.permission.GET_ACCOUNTS)) {
            return 2;
        } else if (permission.equals(Manifest.permission.INTERNET)) {
            return 3;
        } else {
            return 4;
        }
    }

    /**
     * @param permission 权限
     * @return 该权限是否在敏感权限列表中
     */
    private synchronized boolean checkSinglePermission(String permission) {
        for (String sysPermission : mSensitivePermissions.keySet()) {
            if (permission.equals(sysPermission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取权限名称资源文件id
     * */
    public synchronized @StringRes int getPermissionNameRes(String permission) {
        return mSensitivePermissions.get(permission);
    }
}
