package yangchengyu.shmtu.edu.cn.calligraphyrecognize.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.WordInfo;

/**
 * Created by kimi9 on 2018/3/18.
 */

public class WordDBhelper extends SQLiteOpenHelper {

    public final String TAG = this.getClass().getSimpleName();

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "WordDetailDB.db";
    public static final String KEY_ID = "_id";
    public static final String KEY_WORD = "word";
    public static final String KEY_HEIGHT = "height";
    public static final String KEY_WIDTH = "width";
    public static final String KEY_X_ARRAY = "x";
    public static final String KEY_Y_ARRAY = "y";
    public static final String KEY_STYLE = "style";
    public static final String KEY_PATH = "path";
    static final String SQLITE_TABLE = "WordDetailTable";

    public static final String CREATE_TABLE = "CREATE TABLE if not exists " + SQLITE_TABLE + " (" +
            KEY_ID + " integer PRIMARY KEY," +
            KEY_WORD + " text," +
            KEY_WIDTH + " integer," +
            KEY_HEIGHT + " integer," +
            KEY_X_ARRAY + " integer," +
            KEY_Y_ARRAY + " integer," +
            KEY_STYLE + " text," +
            KEY_PATH + " text" + ");";

    public WordDBhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
        onCreate(db);
    }

    public void addWord(WordInfo wordInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        //使用ContentValues添加数据
        ContentValues values = new ContentValues();
        values.put(KEY_WORD, wordInfo.getWord());
        values.put(KEY_WIDTH, wordInfo.getWidth());
        values.put(KEY_HEIGHT, wordInfo.getHeight());
        values.put(KEY_X_ARRAY, wordInfo.getX_array());
        values.put(KEY_Y_ARRAY, wordInfo.getY_array());
        values.put(KEY_STYLE, wordInfo.getStyle());
        try {
            db.insert(SQLITE_TABLE, null, values);
            Log.i(TAG, "Add into DB success");
        } catch (Exception e) {
            Log.e(TAG, String.valueOf(e));
        }
        db.close();
    }

    public void deleteWord(WordInfo wordInfo) {
        String whereClause = "id=?";
        String[] whereArgs = {String.valueOf(wordInfo.getId())};
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(SQLITE_TABLE, whereClause, whereArgs);
            Log.i(TAG, "Delete from DB success");
        } catch (Exception e) {
            Log.e(TAG, String.valueOf(e));
        }
        db.close();
    }

    public ArrayList<WordInfo> getALLWord() {
        ArrayList<WordInfo> wordInfoArrayList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = null;
        cursor = db.query(SQLITE_TABLE,
                new String[]{KEY_ID, KEY_WORD, KEY_WIDTH, KEY_HEIGHT, KEY_X_ARRAY, KEY_Y_ARRAY, KEY_STYLE, KEY_PATH},
                null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                WordInfo wordInfo = new WordInfo();
                wordInfo.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                wordInfo.setWord(cursor.getString(cursor.getColumnIndexOrThrow(KEY_WORD)));
                wordInfo.setWidth(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_WIDTH)));
                wordInfo.setHeight(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_HEIGHT)));
                wordInfo.setX_array(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_X_ARRAY)));
                wordInfo.setY_array(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_X_ARRAY)));
                wordInfo.setStyle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_STYLE)));
                wordInfo.setPic_path(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PATH)));
                wordInfoArrayList.add(wordInfo);
            } while (cursor.moveToNext());
        }
        if (cursor != null || !cursor.isClosed()) {
            cursor.close();
        }

        return wordInfoArrayList;
    }
}
