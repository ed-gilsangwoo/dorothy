package com.example.parktaeim.dorothy.Model;

/**
 * Created by parktaeim on 2017. 10. 11..
 */

public class MapLocationItem {
    private String locationName;
    private double latitude;
    private double longitude;

    public MapLocationItem(){
        super();
    }

    public MapLocationItem(String locationName, double latitude, double longitude) {
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
