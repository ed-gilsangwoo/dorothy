package com.example.parktaeim.dorothy.Model;

/**
 * Created by parktaeim on 2017. 10. 1..
 */

public class SearchDestItem  {
    private String destination;
    private String address;
    private Double distance;

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
}
