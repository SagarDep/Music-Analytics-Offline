package in.thetechguru.musiclogger.musiclogger.datamodel.db;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import in.thetechguru.musiclogger.musiclogger.datamodel.modelclasses.apipojo.Tag;

/**
 * Created by abami on 24-Mar-18.
 */

public class Converter {
    @TypeConverter
    public static List<Tag> fromString(String value) {
        Type listType = new TypeToken<List<Tag>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayLisr(List<Tag> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}
