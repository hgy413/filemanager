package com.jb.filemanager.ui.dialog;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.util.APIUtil;
import com.jb.filemanager.util.ConvertUtils;

/**
 * Created by bill wang on 2017/7/12.
 * 
 */

public class SpaceNotEnoughDialog extends FMBaseDialog {

    public SpaceNotEnoughDialog(Activity act, long moreSpace, final Listener listener) {
        super(act, true);

        View dialogView = View.inflate(act, R.layout.dialog_space_not_enough, null);
        TextView title = (TextView) dialogView.findViewById(R.id.tv_space_not_enough_title);
        if (title != null) {
            title.getPaint().setAntiAlias(true);
        }

        TextView desc = (TextView) dialogView.findViewById(R.id.tv_space_not_enough_desc);
        if (desc != null) {
            desc.getPaint().setAntiAlias(true);
            String moreSpaceString = ConvertUtils.getReadableSize(moreSpace);
            String descString = act.getString(R.string.dialog_space_not_enough_desc, moreSpaceString);

            SpannableStringBuilder ssb = new SpannableStringBuilder(descString);
            ssb.setSpan(new ForegroundColorSpan(APIUtil.getColor(getContext(), R.color.dialog_space_not_enough_need_more_text_color)),
                    descString.length() - moreSpaceString.length() - 1,
                    descString.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(new StyleSpan(Typeface.BOLD),
                    descString.length() - moreSpaceString.length() - 1,
                    descString.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            desc.setText(ssb);
        }

        TextView ok = (TextView) dialogView.findViewById(R.id.tv_space_not_enough_confirm);
        if (ok != null) {
            ok.getPaint().setAntiAlias(true);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onConfirm(SpaceNotEnoughDialog.this);
                }
            }); // 确定按钮点击事件
        }

        TextView cancel = (TextView) dialogView.findViewById(R.id.tv_space_not_enough_cancel);
        if (cancel != null) {
            cancel.getPaint().setAntiAlias(true);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCancel(SpaceNotEnoughDialog.this);
                }
            }); // 取消按钮点击事件
        }

        setContentView(dialogView);
    }

    public interface Listener {
        /**
         * 点击确定按钮
         */
        void onConfirm(SpaceNotEnoughDialog dialog);

        void onCancel(SpaceNotEnoughDialog dialog);
    }
}
