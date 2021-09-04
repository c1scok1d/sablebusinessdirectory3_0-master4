package com.macinternetservices.sablebusinessdirectory.db.common;

import androidx.room.TypeConverter;
import java.util.Date;

/**
 * Sable Business Directory on 12/27/17.
 * Contact Email : admin@sablebusinessdirectory.com
 */


public class Converters {

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
