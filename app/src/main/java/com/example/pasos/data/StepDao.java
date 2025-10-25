package com.example.pasos.data;

@Dao
public class StepDao {

    @Insert
    void insert(StepEntity step);

    @Query("SELECT * FROM steps ORDER BY id DESC")
    List<StepEntity> getAllSteps();

    @Query("SELECT steps FROM steps WHERE date = :date LIMIT 1")
    Integer getStepsByDate(String date);

    @Query("UPDATE steps SET steps = :steps WHERE date = :date")
    void updateSteps(String date, int steps);

}
