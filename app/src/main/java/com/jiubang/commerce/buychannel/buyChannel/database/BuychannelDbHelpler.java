package com.jiubang.commerce.buychannel.buyChannel.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.jb.ga0.commerce.util.io.DataBaseHelper;
import java.util.ArrayList;

public class BuychannelDbHelpler extends DataBaseHelper {
    private static final String DATABASE_NAME = "buychannelsdk.db";
    public static final int DB_VERSION_MAX = 1;
    private static final String LOG_TAG = "buychannelsdk";
    private static BuychannelDbHelpler sInstance;

    private BuychannelDbHelpler(Context context) {
        super(context, DATABASE_NAME, 1);
    }

    public static BuychannelDbHelpler getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new BuychannelDbHelpler(context);
        }
        return sInstance;
    }

    public int getDbCurrentVersion() {
        return 1;
    }

    public String getDbName() {
        return DATABASE_NAME;
    }

    public void onCreateTables(SQLiteDatabase db) {
        db.execSQL(StaticsTable.CREATE_STATICS_TABLE);
    }

    public void onAddUpgrades(ArrayList<DataBaseHelper.UpgradeDB> arrayList) {
    }
}
