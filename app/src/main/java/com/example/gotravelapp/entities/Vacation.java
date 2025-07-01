package com.example.gotravelapp.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "vacations")

public class Vacation {
    @PrimaryKey(autoGenerate = true)
    private int vacationID;
    private String vacationName;
    private String vacationHotel;
    private String startDate;
    private String endDate;

    public Vacation(String vacationName, String vacationHotel, String startDate, String endDate) {
        this.vacationName = vacationName;
        this.vacationHotel = vacationHotel;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Ignore
    public Vacation(int vacationID, String vacationName, String vacationHotel, String startDate, String endDate) {
        this.vacationID = vacationID;
        this.vacationName = vacationName;
        this.vacationHotel = vacationHotel;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getVacationID() {
        return vacationID;
    }

    public String toString() {
        return vacationName;
    }

    public void setVacationID(int vacationID) {
        this.vacationID = vacationID;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getVacationHotel() {
        return vacationHotel;
    }

    public void setVacationHotel(String vacationHotel) {
        this.vacationHotel = vacationHotel;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getVacationName() {
        return vacationName;
    }

    public void setVacationName(String vacationName) {
        this.vacationName = vacationName;
    }
}


