package in.thetechguru.musiclogger.musiclogger.datamodel.modelclasses.apipojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by abami on 20-Mar-18.
 */

public class Artistt {
    @SerializedName("artist")
    @Expose
    private ArtistLastFm artist;

    public ArtistLastFm getArtist() {
        return artist;
    }

    public void setArtist(ArtistLastFm artist) {
        this.artist = artist;
    }
}
