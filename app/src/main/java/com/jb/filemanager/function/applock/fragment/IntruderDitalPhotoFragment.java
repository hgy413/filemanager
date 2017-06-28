package com.jb.filemanager.function.applock.fragment;

import android.content.ContentResolver;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.applock.dialog.DeleteDialog;
import com.jb.filemanager.function.applock.event.AppLockImageDeleteEvent;
import com.jb.filemanager.function.applock.event.AppLockImageReadEvent;
import com.jb.filemanager.util.APIUtil;
import com.jb.filemanager.util.FileUtil;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.device.Machine;
import com.jb.filemanager.util.imageloader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nieyh on 2017/1/6.
 * 图片详情列表
 * 描述：<br/>
 * 此Fragment只是单纯用于展示图片 逻辑很少 没有必要强制分开成为MVP
 */

public class IntruderDitalPhotoFragment extends Fragment {
    /**
     * <ol>
     * <li>{@link #ARG_PHOTO} 需要展示的图片路径列表</li>
     * <li>{@link #ARG_CURRENT_POS} 展示的图片路径索引</li>
     * </ol>
     */
    private static final String ARG_PHOTO = "1";
    private static final String ARG_CURRENT_POS = "2";

    private ViewPager mPhotoPager;
    //返回键
    private ImageView mBack;
    //提示文字
    private TextView mNotice;
    //删除
    private View mDelete;
    //删除图标
    private ImageView mDeleteImg;
    //所有的路径的列表
    private List<String> mPathList;
    //当前显示位置
    private int mCurrentPosition;

    private DeleteDialog mDeleteDialog;

    /**
     * 展示所有图片
     *
     * @param pathList        图片路径列表
     * @param currentPosition 当前显示图片的序号
     */
    public static void startFragment(ArrayList<String> pathList, int currentPosition, @IdRes int resId, FragmentManager supportFragmentManager) {
        IntruderDitalPhotoFragment intruderDitalPhotoFragment = new IntruderDitalPhotoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_CURRENT_POS, currentPosition);
        if (pathList != null && pathList.size() > 0) {
            bundle.putStringArrayList(ARG_PHOTO, pathList);
        }
        intruderDitalPhotoFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.add(resId, intruderDitalPhotoFragment, IntruderDitalPhotoFragment.class.getSimpleName());
        fragmentTransaction.addToBackStack(IntruderDitalPhotoFragment.class.getName());
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 只是显示单独一个图片
     *
     * @param path 图片路径
     */
    public static void startFragment(String path, @IdRes int resId, FragmentManager supportFragmentManager) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        IntruderDitalPhotoFragment intruderDitalPhotoFragment = new IntruderDitalPhotoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_CURRENT_POS, -1);
        bundle.putString(ARG_PHOTO, path);
        intruderDitalPhotoFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.add(resId, intruderDitalPhotoFragment, IntruderDitalPhotoFragment.class.getSimpleName());
        fragmentTransaction.addToBackStack(IntruderDitalPhotoFragment.class.getName());
        fragmentTransaction.commitAllowingStateLoss();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            int currentPosition = arguments.getInt(ARG_CURRENT_POS);
            if (currentPosition == -1) {
                mPathList = new ArrayList<>(1);
                String path = arguments.getString(ARG_PHOTO);
                mCurrentPosition = 0;
                if (!TextUtils.isEmpty(path)) {
                    mPathList.add(path);
                } else {
                    onFinish();
                }
            } else {
                ArrayList<String> paths = arguments.getStringArrayList(ARG_PHOTO);
                if (paths != null && paths.size() > 0) {
                    mPathList = paths;
                    mCurrentPosition = currentPosition;
                } else {
                    onFinish();
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Logger.w("fragment >> ", "IntruderVertGalleryFragment >> onCreateView");
        return inflater.inflate(R.layout.fragment_intruder_photo_list, container, false);
    }

    @Override
    public void onViewCreated(View parent, @Nullable Bundle savedInstanceState) {
        Logger.w("fragment >> ", "IntruderVertGalleryFragment >> onViewCreated");
        View root = parent.findViewById(R.id.fragment_intruder_photo_list_root);
        int startColor = 0xff0084ff;
        int endColor = 0xff3bd6f2;
        GradientDrawable gradientDrawableLR = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{startColor, endColor});
        gradientDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradientDrawableLR.setShape(GradientDrawable.RECTANGLE);
        APIUtil.setBackground(root, gradientDrawableLR);
        //fitSystemWindows 只能用于非嵌入式Activity 所以此处fragment使用fitSystemwindows将无效 只能代码手动设置paddingtop
        if (Machine.HAS_SDK_KITKAT) {
            float paddingTop = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());
            root.setPadding(0, (int) paddingTop, 0, 0);
        }
        mBack = (ImageView) parent.findViewById(R.id.activity_title_icon);
        mNotice = (TextView) parent.findViewById(R.id.activity_title_word);
        mDelete = parent.findViewById(R.id.fragment_intruder_photo_list_delete);
        mDeleteImg = (ImageView) parent.findViewById(R.id.fragment_intruder_photo_list_delete_icon);
        mDeleteImg.setColorFilter(0xff0084ff, PorterDuff.Mode.SRC_ATOP);
        mPhotoPager = (ViewPager) parent.findViewById(R.id.fragment_intruder_photo_list_view_pager);
        mPhotoPager.setAdapter(mPhotoPagerAda);
        mPhotoPager.setCurrentItem(mCurrentPosition);
        mPhotoPager.setPageMargin((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));
        updateDataOnSlid(mCurrentPosition);
        mNotice.setText((mCurrentPosition + 1) + "/" + mPathList.size());
        initListener();
    }

    private void initListener() {
        mPhotoPager.addOnPageChangeListener(mPagerChangerListener);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFinish();
            }
        });
        mNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFinish();
            }
        });
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dealDataDelete();
            }
        });
    }

    private ViewPager.OnPageChangeListener mPagerChangerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            updateDataOnSlid(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * 当ViewPager滑动时更新数据
     *
     * @param position
     */
    private void updateDataOnSlid(int position) {
        mCurrentPosition = position;
        mNotice.setText((mCurrentPosition + 1) + "/" + mPathList.size());
        String path = mPathList.get(mCurrentPosition);
        //后发事件
        TheApplication.getGlobalEventBus().post(new AppLockImageReadEvent(path));
    }

    /**
     * 图片被删除
     */
    private void dealDataDelete() {
        if (mDeleteDialog == null) {
            mDeleteDialog = new DeleteDialog(getActivity());
            mDeleteDialog.setOnDeleteListener(new DeleteDialog.onDeleteListener() {
                @Override
                public void onDelete(View v) {
                    String path = mPathList.get(mCurrentPosition);
                    boolean delete = FileUtil.deleteFile(path);

                    if (delete) {
                        ArrayList<String> pathList = new ArrayList<String>();
                        pathList.add(path);
                        deleteFileUriByPath(path);

                        //先发事件， 后删除数据， 有严格先后顺序
                        TheApplication.getGlobalEventBus().post(new AppLockImageDeleteEvent(path));

                        mPathList.remove(mCurrentPosition);
                        int listSize = mPathList.size();
                        if (mCurrentPosition >= listSize) {
                            mCurrentPosition = listSize - 1 < 0 ? 0 : listSize - 1;
                        }

                        mNotice.setText((mCurrentPosition + 1) + "/" + mPathList.size());
                        mPhotoPagerAda.notifyDataSetChanged();

                        if (mPathList.size() == 0) {
                            onFinish();
                        }
                    }
                }
            });
        }
        mDeleteDialog.show();
    }

    private void deleteFileUriByPath(String path) {
        ContentResolver cr = getContext().getContentResolver();
        Uri uri = MediaStore.Images.Media.getContentUri("external");
        if (uri != null && !TextUtils.isEmpty(path)) {
            final String selection = MediaStore.Files.FileColumns.DATA + "=?";
            try {
                cr.delete(uri, selection, new String[]{path});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 返回上一层FragmentTransaction
     */
    private void onFinish() {
        if (isAdded() && isResumed() && !getActivity().getSupportFragmentManager().popBackStackImmediate()) {
            getActivity().finish();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDeleteDialog != null) {
            mDeleteDialog.setOnDeleteListener(null);
            mDeleteDialog = null;
        }
        mPhotoPager.removeOnPageChangeListener(mPagerChangerListener);
    }

    /**
     * 图片展示的适配器
     */
    private PagerAdapter mPhotoPagerAda = new PagerAdapter() {
        @Override
        public int getCount() {
            return mPathList.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageLoader imageLoader = ImageLoader.getInstance(container.getContext());
            ImageView imageView = new ImageView(container.getContext());
            imageView.setMaxWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 329, getResources().getDisplayMetrics()));
            imageView.setMaxHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 512, getResources().getDisplayMetrics()));
            container.addView(imageView);
            String path = mPathList.get(position);
            ImageLoader.ImageLoaderBean bean = new ImageLoader.ImageLoaderBean(path, imageView);
            bean.setCacheKey("key" + path);
            imageLoader.displayImage(bean);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }
    };
}
