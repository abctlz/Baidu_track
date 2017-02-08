package free.tlz.com.baidu_track;

/**
 * Created by root on 2017/2/7.
 */

public class Trackdetail {
    private int id;
    private double lat;//经度
    private double lng;//纬度
    private Track track;//轨迹

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public Trackdetail(int id, double lat, double lng) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
    }

    public Trackdetail(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Trackdetail() {
        super();
    }
}
