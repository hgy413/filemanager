package com.jb.filemanager.function.applock.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;

import com.jb.filemanager.function.applock.manager.AntiPeepDataManager;
import com.jb.filemanager.util.CameraUtil;
import com.jb.filemanager.util.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 防偷窥相机集成器
 *
 * @author chenbenbin
 */
@SuppressWarnings("deprecation")
public class AntiPeepCameraHolder extends ViewHolder {
    public static final String TAG = "AntiPeepCameraHolder";
    private final Context mContext;
    private CameraPreview mCameraPreview;
    private Camera mCamera;
    private final int mCameraId;

    private AudioManager mAudioManager;
    private int mRingerMode;
    private AntiPeepDataManager mPeepDataManager;
    private CameraEventListener mCameraEventListener;
    private AntiPeepImageMask mImageMask;
    /**
     * 音频模式是否已经恢复
     */
    private boolean mIsRingerModeRevert = true;
    /**
     * 偷窥的应用包名
     */
    private String mPackageName;
    /**
     * 是否已经拍摄：用于实现自动拍摄，若在延迟时间内没有对焦并自动拍摄，则延迟时间到达，进行拍摄
     */
    private boolean mHasCapture = false;
    /**
     * 消息ID:延迟拍摄
     */
    private static final int ID_DELAY_CAPTURE = 0X256;
    /**
     * 消息ID:照片保存完毕
     */
    private static final int ID_IMAGE_SAVED = 0X257;

    /**
     * 延迟拍摄时间
     */
    private static final int DELAY_CAPTURE_TIME = 300;

    private static class WeakHandler extends Handler {
        private WeakReference<AntiPeepCameraHolder> mAntiPeepCameraHolderWeakReference;

        public WeakHandler(AntiPeepCameraHolder antiPeepCameraHolder) {
            mAntiPeepCameraHolderWeakReference = new WeakReference<>(antiPeepCameraHolder);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == ID_DELAY_CAPTURE) {
                AntiPeepCameraHolder antiPeepCameraHolder = mAntiPeepCameraHolderWeakReference.get();
                if (antiPeepCameraHolder != null) {
                    if (!antiPeepCameraHolder.mHasCapture) {
                        Logger.i(TAG, "Camera Delay Capture");
                        antiPeepCameraHolder.capture();
                    }
                }
            } else if (msg.what == ID_IMAGE_SAVED) {
                AntiPeepCameraHolder antiPeepCameraHolder = mAntiPeepCameraHolderWeakReference.get();
                if (antiPeepCameraHolder != null) {
                    if (antiPeepCameraHolder.mCameraEventListener != null) {
                        antiPeepCameraHolder.mCameraEventListener.onImageSaved();
                    }
                }
            }
        }

    }

    private static Handler mHandler;

    public AntiPeepCameraHolder(Context context) {
        mContext = context;
        mCameraPreview = new CameraPreview(mContext);
        setContentView(mCameraPreview);
        mCameraId = CameraUtil.getFrontCameraId();
        mAudioManager = (AudioManager) mContext.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        mPeepDataManager = AntiPeepDataManager.getInstance(mContext);
        mImageMask = new AntiPeepImageMask(mContext);
        mHandler = new WeakHandler(this);
    }

    /**
     * 设置偷窥的应用包名
     */
    public void setPackageName(String packageName) {
        mPackageName = packageName;
        mImageMask.setPackageName(mPackageName);
    }

    /**
     * 开启摄像机
     *
     * @return 是否打开成功
     */
    public boolean open() {
        Logger.i(TAG, "Camera Open");
        mHasCapture = false;
        try {
            mCamera = Camera.open(mCameraId);
            initPictureSize();
            updateCameraDisplayOrientation();
            mCameraPreview.setCamera(mCamera);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (mCameraEventListener != null) {
                mCameraEventListener.onOpenFail();
            }
            return false;
        }
    }

    /**
     * 初始化照片尺寸
     */
    private void initPictureSize() {
        if (mCamera == null) {
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
        if (supportedPictureSizes == null || supportedPictureSizes.isEmpty()) {
            return;
        }
        WindowManager wm = (WindowManager) mContext.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int screenWidth = wm.getDefaultDisplay().getWidth();
        int screenHeight = wm.getDefaultDisplay().getHeight();
        Camera.Size pictureSize = CameraUtil.getOptimalSize(supportedPictureSizes, screenWidth, screenHeight);
        parameters.setPictureSize(pictureSize.width, pictureSize.height);
        mCamera.setParameters(parameters);
    }

    /**
     * 前置摄像头设置为镜面反向
     */
    private void updateCameraDisplayOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, info);
        int result = (360 - info.orientation) % 360;
        mCamera.setDisplayOrientation(result);
    }

    /**
     * 关闭摄像机
     */
    public void close() {
        if (!mIsRingerModeRevert) {
            mAudioManager.setRingerMode(mRingerMode);
            mIsRingerModeRevert = true;
        }
        if (mCameraPreview != null) {
            mCameraPreview.setCamera(null);
        }
        if (mCamera != null) {
            Logger.i(TAG, "Camera Close");
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 自动拍照
     */
    public void captureAuto() {
        if (mCamera == null || mHasCapture) {
            return;
        }

        try {
            mCamera.cancelAutoFocus();
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (!mHasCapture) {
                        Logger.i(TAG, "Camera AutoFocus Capture");
                        capture();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        mHandler.sendEmptyMessageDelayed(ID_DELAY_CAPTURE, DELAY_CAPTURE_TIME);
    }

    /**
     * 拍照
     */
    public void capture() {
        mHasCapture = true;
        if (mCamera != null) {
            mRingerMode = mAudioManager.getRingerMode();
            Logger.i(TAG, "Camera capture, RingerMode : " + mRingerMode);
            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            mIsRingerModeRevert = false;
            try {
                mCamera.takePicture(null, null, mJpegCallback);
                if (mCameraEventListener != null) {
                    mCameraEventListener.onCapture();
                }
            } catch (Exception e) {
                e.printStackTrace();
                mAudioManager.setRingerMode(mRingerMode);
                mIsRingerModeRevert = true;
                if (mCameraEventListener != null) {
                    mCameraEventListener.onImageSaved();
                }
            }
        }
    }

    /**
     * 返回照片的JPEG格式的数据
     */
    private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            mAudioManager.setRingerMode(mRingerMode);
            mIsRingerModeRevert = true;
            Logger.i(TAG, "Camera JpegCallback, RingerMode : " + mRingerMode);
            if (camera.getParameters().getPictureFormat() == PixelFormat.JPEG) {
                //存储拍照获得的图片
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        saveJPG(data);
                        mHandler.sendEmptyMessage(ID_IMAGE_SAVED);
                    }
                }).start();
            }
        }
    };

    /**
     * 保存JPG图片
     *
     * @return 保存路径
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void saveJPG(byte[] data) {
        long l = System.currentTimeMillis();
        File file = mPeepDataManager.takePeepPhoto();
        FileOutputStream fileOutputStream = null;
        try {
            //判断是否装有SD卡
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                if (!file.exists()) {
                    File parentFile = file.getParentFile();
                    if (!parentFile.exists()) {
                        boolean isSuccuss = parentFile.mkdirs();
                        Logger.w(TAG, String.valueOf(isSuccuss));
                    }
                    //创建文件
                    file.createNewFile();
                }
                fileOutputStream = new FileOutputStream(file);
                Bitmap bitmap = handleBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    // 关闭流，否则会引起文件无法写入的问题：java.io.IOException: open failed: EBUSY (Device or resource busy)
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        long duration = System.currentTimeMillis() - l;
        Logger.i(TAG, "saveJPG : " + duration);
        mPeepDataManager.onPhotoSaved(file, mPackageName);
    }

    /**
     * 处理Bitmap效果:旋转 + 水印
     *
     * @param src 原始Bitmap
     * @return 处理后的Bitmap
     */
    private Bitmap handleBitmap(Bitmap src) {
        long l = System.currentTimeMillis();
        // 旋转照片
        Matrix matrix = new Matrix();
        matrix.setRotate(270);
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        // 添加水印
        mImageMask.drawWatermask(dst);
        long duration = System.currentTimeMillis() - l;
        Logger.i(TAG, "handleBitmap : " + duration);
        return dst;
    }

    public void setOnCameraEventListener(CameraEventListener listener) {
        mCameraEventListener = listener;
    }

    /**
     * 相机事件回调
     *
     * @author chenbenbin
     */
    public interface CameraEventListener {
        /**
         * 打开相机失败
         */
        void onOpenFail();

        /**
         * 拍照事件
         */
        void onCapture();

        /**
         * 照片保存完毕
         */
        void onImageSaved();
    }
}
