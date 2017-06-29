package com.jb.filemanager.function.permissionalarm.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.permissionalarm.event.PermissionViewDismissEvent;
import com.jb.filemanager.util.AppUtils;

/**
 * @description
 * @author: nieyh
 * @date: 2017-2-10 11:34
 */
public class RequestPermissionView extends RelativeLayout {

    public RequestPermissionView(Context context) {
        super(context);
        init();
    }

    public RequestPermissionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RequestPermissionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RequestPermissionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_app_permission_request, this);
        initView();
    }

    private void initView() {
        TextView desc = (TextView) this.findViewById(R.id.tv_app_permission_request_desc);
        TextView btnCancel = (TextView) this.findViewById(R.id.tv_app_permission_request_cancel);
        TextView btnOk = (TextView) this.findViewById(R.id.tv_app_permission_request_ok);
        String appName = getContext().getString(R.string.app_name);
        desc.setText(getContext().getString(R.string.request_app_permission_content, appName));

        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TheApplication.getGlobalEventBus().post(new PermissionViewDismissEvent(true));
            }
        });

        btnOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.openUsageAccess(TheApplication.getAppContext(), 1);
            }
        });
    }
}
