package com.jb.filemanager.function.privacy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.eventbus.IOnEventMainThreadSubscriber;
import com.jb.filemanager.function.privacy.event.PrivacyConfirmClosedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 用户同意协议的把关页，继承此类的界面，当打开时先检查是否同意协议， 不同意则跳转到协议页<br>
 */
public abstract class PrivacyGuardActivity extends BaseActivity implements IOnEventMainThreadSubscriber<PrivacyConfirmClosedEvent> {

    public final static String EXTRA_NEED_CHECK_AGREE_PRIVACY = "extra_need_check_agree_privacy";

    @Override
    @Subscribe
    public void onEventMainThread(PrivacyConfirmClosedEvent event) {
        if (!PrivacyHelper.isAgreePrivacy()) {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String action = intent.getAction();
        // 某些手机在首次安装后打开，再从桌面打开会增加IntentFlag BROUGHT_TO_FRONT
        // 这个应该只在launch mode = singleTask的时候添加。会导致有2个首页的bug
        // 某些手机启动activity的时候顺序不同，会在同意许可条件启动首页的时候还是非root
        if (!TextUtils.isEmpty(action) && action.equals(Intent.ACTION_MAIN) && !isTaskRoot()) {
            finish();
        } else {
            EventBus.getDefault().register(this);
            boolean needCheckAgreePrivacy = intent.getBooleanExtra(EXTRA_NEED_CHECK_AGREE_PRIVACY, true);
            if (needCheckAgreePrivacy) {
                checkAgreePrivacy();
            }
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 检查是否同意协议<br>
     */
    private void checkAgreePrivacy() {
        if (!PrivacyHelper.isAgreePrivacy()) {
            final Intent intent = new Intent(this, PrivacyActivity.class);
            startActivity(intent);
            finish();
        } else {
            agreePrivacy();
        }
    }

    protected abstract void agreePrivacy();
}