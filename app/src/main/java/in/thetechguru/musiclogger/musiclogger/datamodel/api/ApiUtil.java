package in.thetechguru.musiclogger.musiclogger.datamodel.api;

/**
 * Created by abami on 20-Mar-18.
 */

public class ApiUtil {

    private ApiUtil() {}

    private static final String BASE_URL = "http://ws.audioscrobbler.com/";

    public static ArtistInfoAPIService getAPIService() {
        return RetrofitClient.getClient(BASE_URL).create(ArtistInfoAPIService.class);
    }
}
