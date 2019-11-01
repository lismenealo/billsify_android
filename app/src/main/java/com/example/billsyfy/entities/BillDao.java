package com.example.billsyfy.entities;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface BillDao {
    @Query("SELECT * FROM bill")
    List<Bill> getAll();

    @Query("SELECT * FROM bill WHERE uid IN (:userIds)")
    List<Bill> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM bill WHERE category LIKE :category")
    List<Bill> findByCategory(String category);

    @Insert
    void insertAll(Bill... bills);

    @Delete
    void delete(Bill bill);
}
