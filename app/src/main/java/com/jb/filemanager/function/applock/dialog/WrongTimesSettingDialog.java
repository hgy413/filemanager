package com.jb.filemanager.function.applock.dialog;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.ui.dialog.BaseDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nieyh on 2017/1/16.
 */

public class WrongTimesSettingDialog extends BaseDialog {

    private ListView mWrongTimesList;

    private List<Integer> mWrongTimesData = new ArrayList<>(4);
    private int mChosenNumber;

    public WrongTimesSettingDialog(Activity act) {
        super(act, true);
        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        mWrongTimesData.add(2);
        mWrongTimesData.add(3);
        mWrongTimesData.add(4);
        mWrongTimesData.add(5);
        setContentView(R.layout.dialog_wrong_times_setting);
        mWrongTimesList = (ListView) findViewById(R.id.dialog_wrong_times_settings_list);
        mWrongTimesList.setAdapter(new WrongTimesAdapter());
        mWrongTimesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onListChoiceListener != null) {
                    onListChoiceListener.onChoiced(mWrongTimesData.get(position));
                }
                dismiss();
            }
        });
    }

    public void setChosenNumber(int chosenNumber) {
        mChosenNumber = chosenNumber;
    }


    private class WrongTimesAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mWrongTimesData.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView mWrongTime;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_wrong_times, parent, false);
                mWrongTime = (TextView) convertView.findViewById(R.id.layout_wrong_times_time);
                convertView.setTag(mWrongTime);
            } else {
                mWrongTime = (TextView) convertView.getTag();
            }

            Integer time = mWrongTimesData.get(position);

            mWrongTime.setText(parent.getResources().getString(R.string.applock_wrong_times_txt, time.intValue()));

            if (position == ((mChosenNumber > 2 ? mChosenNumber : 2) - 2)) {
                mWrongTime.setTextColor(0xff219eff);
            }else {
                mWrongTime.setTextColor(0xff999999);
            }

            return convertView;
        }
    }

    private OnListChoiceListener onListChoiceListener;

    public void setOnListChoiceListener(OnListChoiceListener onListChoiceListener) {
        this.onListChoiceListener = onListChoiceListener;
    }

    public interface OnListChoiceListener {
        void onChoiced(int value);
    }
}
