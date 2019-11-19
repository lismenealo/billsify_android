package com.example.billsyfy.entities;
import java.util.Date;

import androidx.room.TypeConverter;

// Converter for java.util.Date, to be able to persist dates in the DB
public class DateTypeConverter {
    @TypeConverter
    public Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public Long dateToTimestamp(Date date) {
        if (date == null) {
            return null;
        } else {
            return date.getTime();
        }
    }
}
