package com.jb.filemanager.function.privacy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.home.MainActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.anim.EaseCubicInterpolator;
import com.jb.filemanager.util.APIUtil;

/**
 * Created by bill wang on 16/8/19.
 *
 */
public class PrivacyActivity extends BaseActivity implements View.OnClickListener, PrivacyContract.View {

    private TextView mPrivacyAgreement;
    private TextView mStartView;
    private View mIcon;
    private View mName;
    private TextView mDesc;
    private TextView mDeclaration;
    private View mJoinUepContainer;
    private TextView mJoinUepTextView;
    private CheckBox mJoinUepCheckBox;
    private PrivacyContract.Presenter mPrivacyPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        mPrivacyAgreement = (TextView) findViewById(R.id.private_policy_tv);

        mStartView = (TextView) findViewById(R.id.start_btn);
        if (mStartView != null) {
            mStartView.setVisibility(View.INVISIBLE);
        }

        mIcon = findViewById(R.id.icon);

        mName = findViewById(R.id.name);

        mDesc = (TextView) findViewById(R.id.desc);

        mDeclaration = (TextView) findViewById(R.id.declaration);
        if (mDeclaration != null) {
            mDeclaration.setVisibility(View.INVISIBLE);
        }

        mJoinUepContainer = findViewById(R.id.join_uep_container);
        if (mJoinUepContainer != null) {
            mJoinUepContainer.setVisibility(View.INVISIBLE);
        }

        mJoinUepCheckBox = (CheckBox) findViewById(R.id.join_uep_cb);
        if (mJoinUepCheckBox != null) {
            mJoinUepCheckBox.setChecked(true);
        }

        mJoinUepTextView = (TextView) findViewById(R.id.join_uep_tv);
        if (mJoinUepTextView != null) {
            mJoinUepTextView.setOnClickListener(this);
        }
        mPrivacyAgreement.setOnClickListener(this);

        mStartView.setOnClickListener(this);

        checkAgree(true);
        TheApplication.postRunOnUiThread(new Runnable() {
            @Override
            public void run() {
                anim(true);
            }
        }, 400);

        updateTextViews();
        mPrivacyPresenter = new PrivacyPresenter(this, new PrivacySupport());
        mPrivacyPresenter.start();
    }

    @Override
    public void onClick(View v) {
        if (mQuickClickGuard != null && mQuickClickGuard.isQuickClick(v.getId())) {
            return;
        }
        if (v.equals(mPrivacyAgreement)) {
            UserProtocolActivity.goToPrivacyPolicyActivity(this, true);
            finish();
        } else if (v.equals(mJoinUepTextView)) {
            UserProtocolActivity.goToUserExpActivity(this, true);
            finish();
        } else if (v.equals(mStartView)) {
            mPrivacyPresenter.joinUepPlan(mJoinUepCheckBox.isChecked());
            mPrivacyPresenter.agreePrivacy();
            mPrivacyPresenter.sendPrivacyConfirmClosedMsg();
            mPrivacyPresenter.release();
            gotoHomePage();
        }
    }

    //判断启动的activity是那个
    private void gotoHomePage() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(MainActivity.ACTION_AGREE_PRIVACY);
        overridePendingTransition(R.anim.aty_enter, R.anim.aty_exit);
        startActivity(intent);
        finish();
    }

    public void updateTextViews() {
        mDeclaration.setText(R.string.about_copyright);
        mStartView.setText(R.string.private_start);
        mPrivacyAgreement.setText(APIUtil.fromHtml(getString(R.string.privacy_start_by_agree_policy)));
        mJoinUepTextView.setText(APIUtil.fromHtml(getString(R.string.private_join_uep)));
    }

    @Override
    protected void onPause() {
        super.onPause();
        TheApplication.removeAllFromHandler();
        anim(false);
    }

    @Override
    protected void onDestroy() {
        mPrivacyPresenter.sendPrivacyConfirmClosedMsg();
        super.onDestroy();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
    }

    @Override
    public void onBackPressed() {
        if (!mPrivacyPresenter.isAgreePrivacy()) {
            finish();
        } else {
            super.onBackPressed();
        }
        mPrivacyPresenter.sendPrivacyConfirmClosedMsg();
        mPrivacyPresenter.release();
    }


    private void checkAgree(boolean agree) {
        mPrivacyAgreement.setSelected(agree);
        mStartView.setEnabled(agree);
    }

    private void gotoPrivacyInfoPage() {
        PrivacyHelper.gotoPrivacyInfoPage(this);
    }

    private void gotoUepInfoPage() {
        PrivacyHelper.gotoUepInfoPage(this);
    }

    private AnimationSet getAnim() {

        int time = 1500;

        AnimationSet as = new AnimationSet(getApplicationContext(), null);
        TranslateAnimation ta = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 1f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0f);
        ta.setDuration(time);
        AlphaAnimation aa = new AlphaAnimation(0f, 1f);
        aa.setDuration(time);
        as.addAnimation(ta);
        as.addAnimation(aa);
        as.setInterpolator(new EaseCubicInterpolator(0, 1.0f, 0f, 1.0f));
        as.setFillAfter(true);

        return as;
    }

    private void anim(boolean start) {
        final View[] animViews = {mIcon, mName, mDesc, mStartView,
                mJoinUepContainer, mPrivacyAgreement, mDeclaration};

        for (int i = 0; i < animViews.length; i++) {
            final View view = animViews[i];
            if (start) {
                view.setVisibility(View.VISIBLE);
                AnimationSet as = getAnim();
                as.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        view.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                as.setStartOffset(i * 60);
                view.startAnimation(as);
            } else {
                view.clearAnimation();
            }
        }
    }

    @Override
    public void showVersion(String version) {
        mDesc.setText(version);
    }
}