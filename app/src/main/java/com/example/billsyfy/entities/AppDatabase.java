package com.example.billsyfy.entities;

import androidx.room.Database;
import androidx.room.RoomDatabase;

//Creating database to store billTable
@Database(entities = {Bill.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BillDao billDao();
}
