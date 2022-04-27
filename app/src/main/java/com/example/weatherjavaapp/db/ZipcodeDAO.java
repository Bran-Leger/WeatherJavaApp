package com.example.weatherjavaapp.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import java.util.List;

@Dao
public interface ZipcodeDAO {
    

    @Query("SELECT * FROM zipcodes where selected = 1")
    Zipcode getCurrent();
    
    @Query("SELECT * FROM zipcodes")
    LiveData<List<Zipcode>> getAll();
    
    @Insert
    void insert(Zipcode... zipcodes);
    
    @Update
    void update(Zipcode... zipcodes);

    @Query("DELETE FROM zipcodes")
    void deleteAll();
}
