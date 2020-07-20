package com.example.sneaky;

import android.os.Parcel;
import android.os.Parcelable;

public class Notes implements Parcelable {
    private Double lat,lng;
    private String title;
    private String date,time;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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


    public static Creator<Notes> getCREATOR() {
        return CREATOR;
    }

    public Notes(Double lat, Double lng, String title, String time, String date) {
        this.lat = lat;
        this.lng = lng;
        this.title = title;
        this.date = date;
        this.time = time;
    }

    public Notes() {

    }

    protected Notes(Parcel in) {
        title = in.readString();
        date = in.readString();
    }

    public static final Creator<Notes> CREATOR = new Creator<Notes>() {
        @Override
        public Notes createFromParcel(Parcel in) {
            return new Notes(in);
        }

        @Override
        public Notes[] newArray(int size) {
            return new Notes[size];
        }
    };


    @Override
    public String toString() {
        return "Notes{" +
                "title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(date);
    }
}
