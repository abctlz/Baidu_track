package free.tlz.com.baidu_track;

import java.util.ArrayList;

/**
 * Created by root on 2017/2/7.
 */

public class Track {
    private int id;//ID
    private String track_name;//线路名称
    private String create_date;//创建时间
    private String start_loc;//起点位置
    private String end_loc;//终点位置
    private ArrayList<Trackdetail> trackdetails;

    public Track() {
    }

    public Track(int id, String track_name, String create_date, String start_loc, String end_loc) {
        this.id = id;
        this.track_name = track_name;
        this.create_date = create_date;
        this.start_loc = start_loc;
        this.end_loc = end_loc;
    }

    public Track(String track_name, String create_date, String start_loc, String end_loc) {
        this.track_name = track_name;
        this.create_date = create_date;
        this.start_loc = start_loc;
        this.end_loc = end_loc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTrack_name() {
        return track_name;
    }

    public void setTrack_name(String track_name) {
        this.track_name = track_name;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getStart_loc() {
        return start_loc;
    }

    public void setStart_loc(String start_loc) {
        this.start_loc = start_loc;
    }

    public String getEnd_loc() {
        return end_loc;
    }

    public void setEnd_loc(String end_loc) {
        this.end_loc = end_loc;
    }

    public ArrayList<Trackdetail> getTrackdetails() {
        return trackdetails;
    }

    public void setTrackdetails(ArrayList<Trackdetail> trackdetails) {
        this.trackdetails = trackdetails;
    }
}
