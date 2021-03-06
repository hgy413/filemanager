package com.jb.filemanager.ui.dialog;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.home.event.SortByChangeEvent;
import com.jb.filemanager.manager.file.FileManager;
import com.jb.filemanager.statistics.StatisticsConstants;
import com.jb.filemanager.statistics.StatisticsTools;
import com.jb.filemanager.statistics.bean.Statistics101Bean;
import com.jb.filemanager.util.FileUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by bill wang on 2017/7/11.
 */

public class SortByDialog extends FMBaseDialog {

    public static final int SORT_BY_NAME = 0;
    public static final int SORT_BY_DATE = 1;
    public static final int SORT_BY_TYPE = 2;
    public static final int SORT_BY_SIZE = 3;

    public SortByDialog(Activity act, int currentSortBy, final Listener listener) {
        super(act, true);

        View dialogView = View.inflate(act, R.layout.dialog_main_sort, null);
        TextView descendButton = (TextView) dialogView.findViewById(R.id.tv_main_sort_descending);
        TextView ascendButton = (TextView) dialogView.findViewById(R.id.tv_main_sort_ascending);
        final RadioGroup itemGroup = (RadioGroup) dialogView.findViewById(R.id.rg_main_sort_by);
        itemGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_main_sort_by_name:
                        statisticsClickSortByName();
                        break;
                    case R.id.rb_main_sort_by_date:
                        statisticsClickSortByDate();
                        break;
                    case R.id.rb_main_sort_by_type:
                        statisticsClickSortByType();
                        break;
                    case R.id.rb_main_sort_by_size:
                        statisticsClickSortBySize();
                        break;
                }
            }
        });

        int currentId;
        switch (currentSortBy) {
            case SORT_BY_NAME:
                currentId = R.id.rb_main_sort_by_name;
                break;
            case SORT_BY_DATE:
                currentId = R.id.rb_main_sort_by_date;
                break;
            case SORT_BY_TYPE:
                currentId = R.id.rb_main_sort_by_type;
                break;
            case SORT_BY_SIZE:
                currentId = R.id.rb_main_sort_by_size;
                break;
            default:
                currentId = R.id.rb_main_sort_by_name;
                break;
        }
        itemGroup.check(currentId);

        descendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkId = itemGroup.getCheckedRadioButtonId();
                int sortBy;
                switch (checkId) {
                    case R.id.rb_main_sort_by_name:
                        sortBy = SORT_BY_NAME;
                        break;
                    case R.id.rb_main_sort_by_date:
                        sortBy = SORT_BY_DATE;
                        break;
                    case R.id.rb_main_sort_by_type:
                        sortBy = SORT_BY_TYPE;
                        break;
                    case R.id.rb_main_sort_by_size:
                        sortBy = SORT_BY_SIZE;
                        break;
                    default:
                        sortBy = SORT_BY_NAME;
                        break;
                }
                listener.onDescend(SortByDialog.this, sortBy);
                statisticsClickDescending();
            }
        }); // 降序按钮点击事件

        ascendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkId = itemGroup.getCheckedRadioButtonId();
                int sortBy;
                switch (checkId) {
                    case R.id.rb_main_sort_by_name:
                        sortBy = SORT_BY_NAME;
                        break;
                    case R.id.rb_main_sort_by_date:
                        sortBy = SORT_BY_DATE;
                        break;
                    case R.id.rb_main_sort_by_type:
                        sortBy = SORT_BY_TYPE;
                        break;
                    case R.id.rb_main_sort_by_size:
                        sortBy = SORT_BY_SIZE;
                        break;
                    default:
                        sortBy = SORT_BY_NAME;
                        break;
                }
                listener.onAscend(SortByDialog.this, sortBy);
                statisticsClickAscending();
            }
        }); // 升序按钮点击事件

        setContentView(dialogView);
    }

    private void statisticsClickSortByName() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.STORAGE_SORT_NAME;
        StatisticsTools.upload101InfoNew(bean);
    }

    private void statisticsClickSortByDate() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.STORAGE_SORT_DATE;
        StatisticsTools.upload101InfoNew(bean);
    }

    private void statisticsClickSortByType() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.STORAGE_SORT_TYPE;
        StatisticsTools.upload101InfoNew(bean);
    }

    private void statisticsClickSortBySize() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.STORAGE_SORT_SIZE;
        StatisticsTools.upload101InfoNew(bean);
    }

    private void statisticsClickAscending() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.STORAGE_SORT_ASCENDING;
        StatisticsTools.upload101InfoNew(bean);
    }

    private void statisticsClickDescending() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.STORAGE_SORT_DESCENDING;
        StatisticsTools.upload101InfoNew(bean);
    }

    public interface Listener {
        /**
         * 点击确定按钮
         */
        void onDescend(SortByDialog dialog, int sortBy);

        void onAscend(SortByDialog dialog, int sortBy);
    }
}
