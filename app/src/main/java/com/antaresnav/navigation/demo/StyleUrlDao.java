package com.antaresnav.navigation.demo;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface StyleUrlDao {

    @Insert
    void insert(StyleUrl styleUrl);

    @Delete
    void delete(StyleUrl styleUrl);

    @Query("SELECT * FROM style_url ORDER BY name ASC")
    LiveData<List<StyleUrl>> loadAll();

    @Insert
    void insertAll(StyleUrl... styleUrls);
}
