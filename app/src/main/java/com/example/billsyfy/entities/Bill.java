package com.example.billsyfy.entities;

import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity
public class Bill {
    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "date")
    @TypeConverters(DateTypeConverter.class)
    public Date date;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "category")
    public String category;

    @ColumnInfo(name = "amount")
    public int amount;
}
