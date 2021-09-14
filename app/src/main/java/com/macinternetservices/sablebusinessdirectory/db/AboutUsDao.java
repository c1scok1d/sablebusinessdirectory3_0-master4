package com.macinternetservices.sablebusinessdirectory.db;

import com.macinternetservices.sablebusinessdirectory.viewobject.AboutUs;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

/**
 * Sable Business Directory on 09/01/2021
 * Contact Email : admin@sablebusinessdirectory.com
 */

@Dao
public interface AboutUsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AboutUs aboutUs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AboutUs> aboutUsList);

    @Query("SELECT * FROM AboutUs LIMIT '1'")
    LiveData<AboutUs> get();

    @Query("SELECT * FROM AboutUs")
    LiveData<List<AboutUs>> getAll();

    @Query("DELETE FROM AboutUs")
    void deleteTable();

}