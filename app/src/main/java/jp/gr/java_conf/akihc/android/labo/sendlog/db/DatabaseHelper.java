package jp.gr.java_conf.akihc.android.labo.sendlog.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * データベースアクセス
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private final String TAG = this.getClass().getSimpleName();
    private final static String DB_NAME = "SEND_LOG_INS";
    private final static int DB_VERSION = 1;


    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     */
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    /**
     * 最初にDB接続をゲットしようとした時に、該当するDBファイルが無い場合に走る
     * （コンストラクタでは走らない）
     * <p>
     * 各テーブルを作成する
     *
     * @param db 使用するDB名
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            String sql = create_LOG_INFO_T_SQL();
            db.execSQL(sql);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            db.endTransaction();
        }
    }


    /**
     * DBが更新された時に走る
     *
     * @param db         DB名
     * @param oldVersion 旧
     * @param newVersion 新
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //まだ実装する中身はない
    }


    public Cursor getData_LOG_INFO_T(int type) {
        return getData_LOG_INFO_T("CATEGORY = ? and DELETE_FLG = ?", new String[]{String.valueOf(type), "0"});
    }


    public int updateInsertData_LOG_INFO_T(String mac, String info, int type) {
        int result = 0;
        Cursor c = getData_LOG_INFO_T(type);

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if (c.getCount() > 0) {
                //TODO: UPDATE
                ContentValues contentValue = new ContentValues();
                contentValue.put("CATEGORY", String.valueOf(type));
                contentValue.put("INFO", info);
                contentValue.put("UPDATE_DATE", System.currentTimeMillis());
                // アップデートを実行して、その後にDBをクローズ
                result = db.update("LOG_INFO_T", contentValue, "CATEGORY = ? AND DELETE_FLG = ?", new String[]{String.valueOf(type),"0"});
            } else {
                ContentValues insertValues = new ContentValues();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.JAPAN);
                Date date = new Date();
                Log.d(TAG, "[DEBUG] ID設定: " + sdf.format(date));
                insertValues.put("ID", sdf.format(date));
                insertValues.put("CATEGORY", String.valueOf(type));
                insertValues.put("INFO", info);

                insertValues.put("CREATE_DATE", System.currentTimeMillis());
                insertValues.put("UPDATE_DATE", System.currentTimeMillis());
                insertValues.put("DELETE_FLG", "0");
                //TODO: INSERT
                db.insert("LOG_INFO_T", "", insertValues);
            }
        } finally {
            db.close();
        }
        return result;
    }


    private Cursor getData_LOG_INFO_T(String selection, String[] selectionArgs) {
        final SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        try {
            String groupBy = null;
            String having = null;
            String orderBy = "ID ASC";
            c = db.query("LOG_INFO_T", new String[]{"_id", "ID", "CATEGORY", "INFO",  "CREATE_DATE", "UPDATE_DATE", "DELETE_FLG"}, selection, selectionArgs, groupBy, having, orderBy);
            c.moveToFirst();
        } catch (Exception ex) {
            ex.getStackTrace();
        } finally {
            db.close();
        }

        return c;
    }


    /**
     * DEVICE_NAME_Tテーブル作成SQL
     *
     * @return SQL文
     */
    private String create_LOG_INFO_T_SQL() {
        String sql = "CREATE TABLE LOG_INFO_T ( " +
                "_id INTEGER PRIMARY KEY, " +
                "ID TEXT UNIQUE ON CONFLICT REPLACE," + // 衝突したときに上書きする
                "CATEGORY TEXT," +
                "INFO TEXT," +
                "CREATE_DATE INTEGER " +
                "UPDATE_DATE INTEGER " +
                "DELETE_FLG INTEGER " +
                ");";
        return sql;
    }
}
