package in.thetechguru.musiclogger.musiclogger.datamodel.api;

import in.thetechguru.musiclogger.musiclogger.datamodel.modelclasses.apipojo.ArtistWrapper;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by abami on 20-Mar-18.
 */

public interface ArtistInfoAPIService {
    @GET("/2.0/?method=artist.getinfo&autocorrect=1&api_key=4e464c9ca4e6763aca6d5a7a04728c77&format=json")
    Call<ArtistWrapper> getArtist(@Query("artist") String artist);
}

