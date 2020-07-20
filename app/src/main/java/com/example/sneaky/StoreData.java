package com.example.sneaky;

public class StoreData{
    private double lat,lng;
    private String List_name,date,time;


    public StoreData()
    {
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getList_name() {
        return List_name;
    }

    public void setList_name(String list_name) {
        List_name = list_name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
