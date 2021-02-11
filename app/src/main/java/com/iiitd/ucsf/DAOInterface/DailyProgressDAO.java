package com.iiitd.ucsf.DAOInterface;
 
 
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.iiitd.ucsf.RoomEntity.DailyProgressEntity;

import java.util.List;

@Dao
public interface DailyProgressDAO
{
    @Query("Select * from  DailyProgress_Records")
    List<DailyProgressEntity> getDailyProgressRecords();

    @Insert
    void addDailyProgressRecord(DailyProgressEntity ce);

    @Delete
    void delete(DailyProgressEntity ce);

    @Query("DELETE FROM DailyProgress_Records")
    void deleteAll();
}
