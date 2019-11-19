package com.example.billsyfy.entities;

import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity
//Creating Bill entity using entity manager
public class Bill {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "date")
    @TypeConverters(DateTypeConverter.class)
    public Date date;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "category")
    public String category;

    @ColumnInfo(name = "imageFilePath")
    public String imageFilePath;

    @ColumnInfo(name = "amount")
    public int amount;

    public Bill(String category, String description, Date date, int amount) {
        this.category = category;
        this.date = date;
        this.amount = amount;
        this.description = description;
    }

    @Ignore
    public Bill(){
        this.date = new Date();
        this.amount = 0;
        this.description = "";
        this.category = "General";
    }
}
