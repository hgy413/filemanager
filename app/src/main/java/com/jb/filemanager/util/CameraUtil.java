package com.jb.filemanager.util;

import android.hardware.Camera;

import java.util.List;

/**
 * 相机工具类
 *
 * @author chenbenbin
 */
@SuppressWarnings("deprecation")
public class CameraUtil {
    /**
     * 是否存在前置摄像头
     */
    public static boolean isFrontCameraAvailable() {
        return getFrontCameraId() != -1;
    }

    /**
     * 获取前置摄像头的Id
     *
     * @return 没有前置时返回-1
     */
    public static int getFrontCameraId() {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        try {
            for (int i = 0; i < numberOfCameras; i++) {
                Camera.getCameraInfo(i, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    return i;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 获取适配的尺寸值
     *
     * @param sizes  支持的尺寸列表
     * @param width  显示的宽度
     * @param height 显示的高度
     */
    public static Camera.Size getOptimalSize(List<Camera.Size> sizes, int width, int height) {
        final double aspectTolerance = 0.1;
        double targetRatio = (double) width / height;
        if (sizes == null) {
            return null;
        }

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > aspectTolerance) {
                continue;
            }
            if (Math.abs(size.height - height) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - height);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - height) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - height);
                }
            }
        }
        return optimalSize;
    }
}
