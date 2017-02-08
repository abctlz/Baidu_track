package free.tlz.com.baidu_track;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by root on 2017/2/7.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;//版本号
    private static final String DB_NAME = "track.db";//数据库名
    public static final String DB_TABLE = "track";//数据表名
    public static final String TABLE_TRACK_DETAIL = "track_detail";//数据表名

    // 字段
    public static final String ID = "_id";
    // 跟踪表
    public static final String TRACK_NAME = "track_name";
    public static final String CREATE_DATE = "create_date";
    public static final String START_LOC = "start_loc";
    public static final String END_LOC = "end_loc";

    // 明细表
    public static final String TID = "tid";// 线路的ID
    public static final String LAT = "lat";// 纬度
    public static final String LNG = "lng";// 经度

    private static String CREATE_TABLE_TRACK =
            "create table track(_id integer primary key autoincrement," +
                    "track_name text," +
                    "create_date text," +
                    "start_loc text," +
                    "end_loc text)";
    private static String CREATE_TABLE_TRACK_DETAIL =
            "create table track_detail(_id integer primary key autoincrement," +
                    "tid integer not null," +
                    "lat real," +
                    "lng real)";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TRACK);//建表语句
        db.execSQL(CREATE_TABLE_TRACK_DETAIL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
