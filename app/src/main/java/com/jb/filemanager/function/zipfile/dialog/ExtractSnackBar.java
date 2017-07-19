package com.jb.filemanager.function.zipfile.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.home.MainActivity;

import static com.jb.filemanager.home.MainPresenter.EXTRA_FOCUS_FILE;
import static com.jb.filemanager.home.MainPresenter.FILE_EXPLORER;

/**
 * Created by xiaoyu on 2017/7/19 19:08.
 */

public class ExtractSnackBar extends BaseActivity {

    public static final String DESTINATION_DIRECTORY = "destination_directory";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extarct_snack_bar);

        final String path = getIntent().getStringExtra(DESTINATION_DIRECTORY);
        final Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), path, Snackbar.LENGTH_INDEFINITE);
        final Snackbar.SnackbarLayout snackbarView = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarView.setPadding(0, 0, 0, 0);
        View addView = View.inflate(getApplicationContext(), R.layout.snack_bar_extract_finish, null);
        TextView tvPath = (TextView) addView.findViewById(R.id.extract_noti_finish_dest_path);
        tvPath.setText(path);
        View btnSee = addView.findViewById(R.id.extract_noti_see);
        btnSee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                Intent intent = new Intent(ExtractSnackBar.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(FILE_EXPLORER, true);
                intent.putExtra(EXTRA_FOCUS_FILE, path);
                startActivity(intent);
            }
        });


        View root = findViewById(R.id.root);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (snackbar != null) {
                    snackbar.dismiss();
                }
                finish();
            }
        });


        Snackbar.SnackbarLayout.LayoutParams params = new CollapsingToolbarLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        snackbarView.addView(addView, params);
        snackbar.show();

    }

    public static void showSnackBar(Context context, String desPath) {
        Intent intent = new Intent(context, ExtractSnackBar.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(DESTINATION_DIRECTORY, desPath);
        context.startActivity(intent);
    }
}
