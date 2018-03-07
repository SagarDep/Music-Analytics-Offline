package in.thetechguru.musiclogger.musiclogger.data_view_model.model_classes;

/**
 * Created by amit on 7/3/18.
 * Stores interval for which data is going to be requested.
 * Defaults to lifetime.
 */

public class Interval {

    public static final int LAST_SEVEN_DAYS = 0;
    public static final int LAST_THIRTY_DAYS = 1;
    public static final int LIFETIME = 2;
    public static final int CUSTOM = 3;

    public Interval(){
        status = LIFETIME;
    }

    public Interval(int status){
        this.status = status;
    }

    public int status;
    public Long from;
    public Long to;
}
