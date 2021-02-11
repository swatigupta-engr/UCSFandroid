package com.iiitd.ucsf.utilities;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.iiitd.ucsf.DAOInterface.DailyProgressDAO;
import com.iiitd.ucsf.RoomEntity.DailyProgressEntity;


@Database(entities = { DailyProgressEntity.class},version = 1,exportSchema = false)
public abstract class DatabaseRoom extends RoomDatabase
{
    private static final String databaseName="UCSFAudioDB";
    private static DatabaseRoom instance;

    public static synchronized DatabaseRoom getInstance(Context context)
    {
        if(instance==null)
        {
            instance= Room.databaseBuilder(context.getApplicationContext(), DatabaseRoom.class,databaseName).fallbackToDestructiveMigration().build();
        }
        return instance;
    }
    public abstract DailyProgressDAO dailyProgressRecords();
}
