package in.thetechguru.musiclogger.musiclogger.datamodel.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.List;

import in.thetechguru.musiclogger.musiclogger.datamodel.modelclasses.apipojo.Tag;

/**
 * Created by abami on 24-Mar-18.
 */

@Entity(tableName = "artist_info")
public class ArtistInfo {

    @PrimaryKey
    @ColumnInfo(name = "mbid")
    @NonNull
    public String mbid = "";

    @ColumnInfo(name = "original_artist")
    public String original_artist;

    @ColumnInfo(name = "corrected_artist")
    public String corrected_artist;

    @ColumnInfo(name = "artist_info")
    public String artist_info;

    @ColumnInfo(name = "artist_url")
    public String artist_url;

    @ColumnInfo(name = "artist_image_thumb")
    public String artist_image_thumb;

    @ColumnInfo(name = "artist_image_big")
    public String artist_image_big;

    @ColumnInfo(name = "tags")
    public List<Tag> tags;

}
