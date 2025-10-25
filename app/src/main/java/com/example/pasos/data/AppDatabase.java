package com.example.pasos.data;

@Database(entities = {StepEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract StepDao stepDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "steps_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

}
