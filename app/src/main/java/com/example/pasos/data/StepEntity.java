package com.example.pasos.data;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "steps")
public class StepEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String date;   // formato sugerido "2025-10-23"
    public int steps;

    public StepEntity(String date, int steps) {
        this.date = date;
        this.steps = steps;
    }

}
