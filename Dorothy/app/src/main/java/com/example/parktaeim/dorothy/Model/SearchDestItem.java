package com.example.parktaeim.dorothy.Model;

/**
 * Created by parktaeim on 2017. 10. 1..
 */

public class SearchDestItem  {
    private String destination;
    private String address;
    private Double distance;

    private Double frontLat;
    private Double frontLon;
    private Double noorLat;
    private Double noorLon;

    private Double currentLat;
    private Double currentLon;

    public Double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(Double currentLat) {
        this.currentLat = currentLat;
    }

    public Double getCurrentLon() {
        return currentLon;
    }

    public void setCurrentLon(Double currentLot) {
        this.currentLon = currentLot;
    }

    public SearchDestItem(String destination, String address, Double distance, Double frontLat, Double frontLon, Double noorLat, Double noorLon, Double currentLat, Double currentLon) {

        this.destination = destination;
        this.address = address;
        this.distance = distance;
        this.frontLat = frontLat;
        this.frontLon = frontLon;
        this.noorLat = noorLat;
        this.noorLon = noorLon;
        this.currentLat = currentLat;
        this.currentLon = currentLon;
    }

    public SearchDestItem(String destination, String address, Double distance, Double frontLat, Double frontLon, Double noorLat, Double noorLon) {
        this.destination = destination;
        this.address = address;
        this.distance = distance;
        this.frontLat = frontLat;
        this.frontLon = frontLon;
        this.noorLat = noorLat;
        this.noorLon = noorLon;
    }

    public SearchDestItem(String destination, String address, Double distance) {
        this.destination = destination;
        this.address = address;
        this.distance = distance;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getFrontLat() {
        return frontLat;
    }

    public void setFrontLat(Double frontLat) {
        this.frontLat = frontLat;
    }

    public Double getFrontLon() {
        return frontLon;
    }

    public void setFrontLon(Double frontLon) {
        this.frontLon = frontLon;
    }

    public Double getNoorLat() {
        return noorLat;
    }

    public void setNoorLat(Double noorLat) {
        this.noorLat = noorLat;
    }

    public Double getNoorLon() {
        return noorLon;
    }

    public void setNoorLon(Double noorLon) {
        this.noorLon = noorLon;
    }
}
