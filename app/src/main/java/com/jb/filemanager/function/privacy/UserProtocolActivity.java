package com.jb.filemanager.function.privacy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.util.APIUtil;

/**
 * Created by nieyh on 2016/12/22.
 *
 */

public class UserProtocolActivity extends BaseActivity {

    private WebView mWebView;


    private TextView mTitle;

    private static final String PROTOCOL_TYPE = "protocol_type";
    private static final String SKIP_TYPE = "skip_type";

    private static final int USER_EXP = 1;
    private static final int PRIVACY_POLICY = 2;
    private boolean isCanBackSplash = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_protocol);
        initView();
        gainBundle();
    }

    /**
     * 获取传递数据
     * */
    private void gainBundle() {
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            int type = extra.getInt(PROTOCOL_TYPE, -1);
            isCanBackSplash = extra.getBoolean(SKIP_TYPE, false);
            switch (type) {
                case USER_EXP:
                    //强制去除掉 html标签
                    String title = APIUtil.fromHtml(getString(R.string.user_protocol_user_experience));
                    mTitle.setText(title);
                    initProtocolContent(getResources().getString(
                            R.string.privacy_uep_plan_info_page));
                    break;
                case PRIVACY_POLICY:
                    mTitle.setText(R.string.user_protocol_privacy_policy);
                    initProtocolContent(getResources().getString(
                            R.string.about_law_url));
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isCanBackSplash) {
            Intent intent = new Intent(UserProtocolActivity.this, PrivacyActivity.class);
            startActivity(intent);
        }
    }

    private void initView() {
        mWebView = (WebView) findViewById(R.id.activity_user_protocol_webview);

        mTitle = (TextView) findViewById(R.id.tv_common_action_bar_title);

        mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                if (isCanBackSplash) {
                    Intent intent = new Intent(UserProtocolActivity.this, PrivacyActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void initProtocolContent(String path) {
        if (mWebView != null) {
            mWebView.loadUrl(path);
        }
    }

    /**
     * 跳转到显示用户体验协议
     * */
    public static void goToUserExpActivity(Context context, boolean isCanBackSplash) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context , UserProtocolActivity.class);
        intent.putExtra(PROTOCOL_TYPE, USER_EXP);
        intent.putExtra(SKIP_TYPE, isCanBackSplash);
        context.startActivity(intent);
    }

    /**
     * 跳转到显示隐私协议
     * */
    public static void goToPrivacyPolicyActivity(Context context, boolean isCanBackSplash) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context , UserProtocolActivity.class);
        intent.putExtra(PROTOCOL_TYPE, PRIVACY_POLICY);
        intent.putExtra(SKIP_TYPE, isCanBackSplash);
        context.startActivity(intent);
    }
}