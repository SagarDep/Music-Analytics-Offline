package in.thetechguru.musiclogger.musiclogger.datamodel.modelclasses.roompojo;

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

    @Override
    public boolean equals(Object obj) {
        if(obj==null) return false;
        if (!Interval.class.isAssignableFrom(obj.getClass())) return false;
        final Interval interval = (Interval) obj;
        if(this.status != interval.status) return false;
        if(this.from != interval.from || this.to != interval.to) return false;
        return true;
    }

    public int status;
    public long from;
    public long to;
}
