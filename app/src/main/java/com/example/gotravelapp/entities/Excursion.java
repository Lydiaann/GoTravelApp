package com.example.gotravelapp.entities;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName="excursions")
public class Excursion {
    @PrimaryKey(autoGenerate = true)
    private int excursionID;
    private String excursionName;
    private int vacationID;
    private String excursionDate;

    public Excursion(String excursionName, int vacationID, String excursionDate) {
        this.excursionName = excursionName;
        this.excursionDate = excursionDate;
        this.vacationID = vacationID;
    }
    @Ignore
    public Excursion(int excursionID, String excursionName, int vacationID, String excursionDate) {
        this.excursionID = excursionID;
        this.excursionName = excursionName;
        this.vacationID = vacationID;
        this.excursionDate = excursionDate;
    }

    public int getExcursionID() {
        return excursionID;
    }

    public void setExcursionID(int excursionID) {
        this.excursionID = excursionID;
    }

    public String getExcursionName() {
        return excursionName;
    }

    public void setExcursionName(String excursionName) {
        this.excursionName = excursionName;
    }

    public String getExcursionDate() {
        return excursionDate;
    }

    public void setExcursionDate(String excursionDate) {
        this.excursionDate = excursionDate;
    }

    public int getVacationID() {
        return vacationID;
    }

    public void setVacationID(int vacationID) {
        this.vacationID = vacationID;
    }
}