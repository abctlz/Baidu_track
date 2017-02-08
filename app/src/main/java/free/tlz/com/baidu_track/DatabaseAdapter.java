package free.tlz.com.baidu_track;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by root on 2017/2/7.
 */

public class DatabaseAdapter {
    private DatabaseHelper helper;

    public DatabaseAdapter(Context context) {
        helper=new DatabaseHelper(context);
    }
    //添加线路
    public int addTrack(Track track){
        SQLiteDatabase db=helper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(DatabaseHelper.TRACK_NAME,track.getTrack_name());
        values.put(DatabaseHelper.CREATE_DATE,track.getCreate_date());
        values.put(DatabaseHelper.START_LOC,track.getStart_loc());
        values.put(DatabaseHelper.END_LOC,track.getEnd_loc());
        long id=db.insertOrThrow(DatabaseHelper.DB_TABLE,null,values);
        db.close();
        return (int) id;
    }
    //线路明细
    public void addTrackDetail(int currentTrackLineID,double currentLat,double currentLng){
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "insert into track_detail(tid,lat,lng) values(?,?,?)";
        db.execSQL(sql, new Object[] { currentTrackLineID, currentLat, currentLng });
        db.close();
    }

    //跟新终点位置
    public void updateEndLoc(String endLoc,int id){
        SQLiteDatabase db=helper.getWritableDatabase();
        String sql="update track set end_loc=? where _id= ?";
        db.execSQL(sql,new Object[]{endLoc,id});
        db.close();
    }
    /**
     * 查询所有路线
     *
     * @return
     */
    public ArrayList<Track> getTracks() {
        ArrayList<Track> tracks = new ArrayList<Track>();
        String sql = "select _id,track_name,create_date,start_loc,end_loc from track ";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);
        Track t = null;
        if (c != null) {
            while (c.moveToNext()) {
                t = new Track(c.getInt(0), c.getString(1), c.getString(2),
                        c.getString(3), c.getString(4));
                tracks.add(t);
            }
            c.close();
        }
        db.close();
        return tracks;
    }

}
