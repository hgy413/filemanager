package com.jb.filemanager.function.image;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.function.image.adapter.ImageDetailsPagerAdapter;
import com.jb.filemanager.function.image.app.BaseFragment;
import com.jb.filemanager.function.image.modle.ImageModle;
import com.jb.filemanager.function.image.presenter.imagedetails.ImageDetailsContract;
import com.jb.filemanager.function.image.presenter.imagedetails.ImageDetailsPresenter;
import com.jb.filemanager.function.image.presenter.imagedetails.ImageDetailsSupport;
import com.jb.filemanager.util.DrawUtils;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        mImageDitalsViewPagerAdapter = new ImageDetailsPagerAdapter(imageModleList);
        mImageDitalsViewPager.setAdapter(mImageDitalsViewPagerAdapter);
        mImageDitalsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mPresenter != null) {
                    mPresenter.handlePagerChange(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
}
