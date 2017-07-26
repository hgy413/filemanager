package com.jb.filemanager.function.image;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jb.filemanager.BaseFragment;
import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.image.adapter.ImageDetailsPagerAdapter;
import com.jb.filemanager.function.image.modle.ImageModle;
import com.jb.filemanager.function.image.presenter.imagedetails.ImageDetailsContract;
import com.jb.filemanager.function.image.presenter.imagedetails.ImageDetailsPresenter;
import com.jb.filemanager.function.image.presenter.imagedetails.ImageDetailsSupport;
import com.jb.filemanager.ui.dialog.DeleteFileDialog;
import com.jb.filemanager.util.DrawUtils;
import com.jb.filemanager.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nieyh on 17-7-4.
 */

public class ImageDetailFragment extends BaseFragment implements ImageDetailsContract.View {

    //图片详情列表
    private final String ARG_IMG_DITALS_LIST = "arg_img_ditals_list";
    //图片当前索引
    private final String ARG_IMG_DITALS_POS = "arg_img_ditals_pos";
    //图片详情的图片列表
    private ViewPager mImageDitalsViewPager;
    //返回
    private ImageView mBack;
    //设置壁纸
    private TextView mSetWallPaper;

    private ImageDetailsContract.Presenter mPresenter;
    //图片适配器
    private ImageDetailsPagerAdapter mImageDitalsViewPagerAdapter;
    //顶部栏 以及 删除按钮
    private View mHead, mDelete;
    //删除文件对话框
    private DeleteFileDialog mDeleteFileDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHead = view.findViewById(R.id.fragment_image_details_title_bar);
        mDelete = view.findViewById(R.id.ll_common_operate_bar_delete);
        mBack = (ImageView) view.findViewById(R.id.fragment_image_details_icon);
        mSetWallPaper = (TextView) view.findViewById(R.id.fragment_image_details_set_wallpaper);
        mImageDitalsViewPager = (ViewPager) view.findViewById(R.id.fragment_image_details_view_paper);
        mPresenter = new ImageDetailsPresenter(this, new ImageDetailsSupport());
        Bundle bundle = getArguments();
        if (bundle != null) {
            mPresenter.handleExtras(bundle.getParcelableArrayList(ARG_IMG_DITALS_LIST), bundle.getInt(ARG_IMG_DITALS_POS, 0));
        }

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToBack();
            }
        });

        mSetWallPaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPresenter != null) {
                    mPresenter.handleSetWallPaper();
                }
                ImageStatistics.upload(ImageStatistics.IMG_SET_WALLPAPER);
            }
        });

        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDeleteFileDialog == null) {
                    mDeleteFileDialog = new DeleteFileDialog(getActivity(), new DeleteFileDialog.Listener() {
                        @Override
                        public void onConfirm(DeleteFileDialog dialog) {
                            dialog.dismiss();
                            if (mPresenter != null) {
                                mPresenter.handleDelete();
                            }
                            ImageStatistics.upload(ImageStatistics.IMG_DETAIL_DELETE);
                        }

                        @Override
                        public void onCancel(DeleteFileDialog dialog) {
                            dialog.dismiss();
                        }
                    });
                }
                mDeleteFileDialog.show();
            }
        });
    }

    /**
     * 设置额外数据
     * */
    public void setExtras(ArrayList<ImageModle> imageModleList, int pos) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ARG_IMG_DITALS_LIST, imageModleList);
        bundle.putInt(ARG_IMG_DITALS_POS, pos);
        setArguments(bundle);
    }

    @Override
    public void bindData(List<ImageModle> imageModleList) {
        if (mImageDitalsViewPagerAdapter == null) {
            mImageDitalsViewPagerAdapter = new ImageDetailsPagerAdapter(imageModleList);
            mImageDitalsViewPager.setAdapter(mImageDitalsViewPagerAdapter);
            mImageDitalsViewPagerAdapter.setOnImgClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int visiable = mHead.getVisibility();
                    if (visiable == View.VISIBLE) {
                        mHead.setVisibility(View.GONE);
                        mDelete.setVisibility(View.GONE);
                    } else {
                        mHead.setVisibility(View.VISIBLE);
                        mDelete.setVisibility(View.VISIBLE);
                    }
                }
            });
            mImageDitalsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (mPresenter != null) {
                        mPresenter.handlePagerChange(position);
                    }
                    ImageStatistics.upload(ImageStatistics.IMG_DETAIL_SWTICH);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        } else {
            mImageDitalsViewPagerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setViewPos(int pos) {
        mImageDitalsViewPager.setCurrentItem(pos, false);
    }

    @Override
    public Bitmap getCurrentBitmap() {
        if (mImageDitalsViewPagerAdapter != null) {
            return DrawUtils.drawable2Bitmap(mImageDitalsViewPagerAdapter.getCurrentView().getDrawable());
        }
        return null;
    }

    @Override
    public void gotoSettingWallPager(Bitmap bitmap) {
        if (bitmap == null) {
            Toast.makeText(TheApplication.getAppContext(), R.string.toast_set_wallpaper_fail, Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("mimeType", "image/*");
        Uri uri = Uri.parse(MediaStore.Images.Media
                .insertImage(getActivity().getContentResolver(),
                        bitmap, null, null));
        intent.setData(uri);
        startActivity(Intent.createChooser(intent, getString(R.string.set_wall_paper)));
    }

    @Override
    public void closeView() {
        onToBack();
    }

    @Override
    public void onDestroy() {
        if (mDeleteFileDialog != null) {
            mDeleteFileDialog.dismiss();
        }
        super.onDestroy();
    }
}
