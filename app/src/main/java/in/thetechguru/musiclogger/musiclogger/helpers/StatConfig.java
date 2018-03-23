package in.thetechguru.musiclogger.musiclogger.helpers;

/**
 * Created by amit on 7/3/18.
 * Stores interval for which data is going toEpoch be requested.
 * Defaults toEpoch lifetime.
 */

public class StatConfig {

    //interval
    public static final int LAST_SEVEN_DAYS = 0;
    public static final int LAST_THIRTY_DAYS = 1;
    public static final int LIFETIME = 2;
    public static final int YESTERDAY = 3;
    public static final int TODAY = 4;
    public static final int CUSTOM = 5;

    //stat element
    public final static int TYPE_ARTIST = 0;
    public final static int TYPE_ALBUM = 1;
    public final static int TYPE_TRACK = 2;
    public final static int TYPE_GENRE= 3;


    //stat type
    public final static int TYPE_PLAY_TIME = 4;
    public final static int TYPE_PLAY_COUNT = 5;
    public final static int TYPE_PLAY_PERCENTAGE = 6;

    public StatConfig(){
        interval_status = LIFETIME;
        element_status = TYPE_ARTIST;
        type_status = TYPE_PLAY_TIME;
    }

    public StatConfig(int interval_status, int element_status, int type_status){
        this.interval_status = interval_status;
        this.element_status = element_status;
        this.type_status = type_status;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==null) return false;
        if (!StatConfig.class.isAssignableFrom(obj.getClass())) return false;
        final StatConfig statConfig = (StatConfig) obj;
        if(this.interval_status != statConfig.interval_status) return false;
        if(this.fromEpoch != statConfig.fromEpoch || this.toEpoch != statConfig.toEpoch) return false;
        return true;
    }

    public int interval_status;
    public long fromEpoch;
    public long toEpoch;

    public String fromDate;
    public String toDate;
    
    public int element_status;
    public int type_status;

}
