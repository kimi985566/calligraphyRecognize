package yangchengyu.shmtu.edu.cn.calligraphyrecognize.DB

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

import java.util.ArrayList

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.WordInfo

/**
 * Created by kimi9 on 2018/3/18.
 * 数据库处理工具类
 */

class WordDBhelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    val allWord: ArrayList<WordInfo>
        get() {
            val wordInfoArrayList = ArrayList<WordInfo>()
            val db = this.writableDatabase

            var cursor: Cursor? = null
            cursor = db.query(SQLITE_TABLE,
                    arrayOf(KEY_ID, KEY_WORD, KEY_WIDTH, KEY_HEIGHT, KEY_X_ARRAY, KEY_Y_ARRAY, KEY_STYLE, KEY_PATH, KEY_ZUAN, KEY_LI, KEY_KAI, KEY_CAO), null, null, null, null, null)

            if (cursor!!.moveToFirst()) {
                do {
                    val wordInfo = WordInfo()
                    wordInfo.id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID))
                    wordInfo.word = cursor.getString(cursor.getColumnIndexOrThrow(KEY_WORD))
                    wordInfo.width = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_WIDTH))
                    wordInfo.height = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_HEIGHT))
                    wordInfo.x_array = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_X_ARRAY))
                    wordInfo.y_array = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_X_ARRAY))
                    wordInfo.style = cursor.getString(cursor.getColumnIndexOrThrow(KEY_STYLE))
                    wordInfo.pic_path = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PATH))
                    wordInfo.zuanScore = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_ZUAN))
                    wordInfo.liScore = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_LI))
                    wordInfo.kaiScore = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_KAI))
                    wordInfo.caoScore = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_CAO))
                    wordInfoArrayList.add(wordInfo)
                } while (cursor.moveToNext())
            }
            if (cursor != null || !cursor.isClosed) {
                cursor.close()
            }

            return wordInfoArrayList
        }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $SQLITE_TABLE")
        onCreate(db)
    }

    fun addWord(wordInfo: WordInfo) {
        val db = this.writableDatabase
        //使用ContentValues添加数据
        val values = ContentValues()
        values.put(KEY_WORD, wordInfo.word)
        values.put(KEY_WIDTH, wordInfo.width)
        values.put(KEY_HEIGHT, wordInfo.height)
        values.put(KEY_X_ARRAY, wordInfo.x_array)
        values.put(KEY_Y_ARRAY, wordInfo.y_array)
        values.put(KEY_STYLE, wordInfo.style)
        values.put(KEY_PATH, wordInfo.pic_path)
        values.put(KEY_ZUAN, wordInfo.zuanScore)
        values.put(KEY_LI, wordInfo.liScore)
        values.put(KEY_KAI, wordInfo.kaiScore)
        values.put(KEY_CAO, wordInfo.caoScore)
        try {
            db.insert(SQLITE_TABLE, null, values)
            Log.i(TAG, "Add into DB success")
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }

        db.close()
    }

    fun deleteWord(wordInfo: WordInfo) {
        val whereClause = "$KEY_ID=?"
        val whereArgs = arrayOf(wordInfo.id.toString())
        val db = this.writableDatabase
        try {
            db.delete(SQLITE_TABLE, whereClause, whereArgs)
            Log.i(TAG, "Delete from DB success")
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }

        db.close()
    }

    companion object {

        var TAG = WordDBhelper::class.java.simpleName

        val DATABASE_VERSION = 2
        val DATABASE_NAME = "WordDetailDB.db"
        val KEY_ID = "_id"
        val KEY_WORD = "word"
        val KEY_HEIGHT = "height"
        val KEY_WIDTH = "width"
        val KEY_X_ARRAY = "x"
        val KEY_Y_ARRAY = "y"
        val KEY_STYLE = "style"
        val KEY_PATH = "path"
        val KEY_ZUAN = "zuan"
        val KEY_LI = "li"
        val KEY_KAI = "kai"
        val KEY_CAO = "cao"
        internal val SQLITE_TABLE = "WordDetailTable"

        val CREATE_TABLE = "CREATE TABLE if not exists " + SQLITE_TABLE + " (" +
                KEY_ID + " integer PRIMARY KEY," +
                KEY_WORD + " text," +
                KEY_WIDTH + " integer," +
                KEY_HEIGHT + " integer," +
                KEY_X_ARRAY + " integer," +
                KEY_Y_ARRAY + " integer," +
                KEY_STYLE + " text," +
                KEY_PATH + " text," +
                KEY_ZUAN + " real," +
                KEY_LI + " real," +
                KEY_KAI + " real," +
                KEY_CAO + " real" + ");"
    }
}
