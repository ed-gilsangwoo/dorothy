package com.example.parktaeim.dorothy.Model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Created by parktaeim on 2017. 10. 15..
 */

public class DestinationResponseItem {
    String type;
    JsonObject geometry;
    JsonObject properties;

    //geometry
    String geometryType;
    JsonArray coordinates;

    //properties
    int totalDistance;
    int totaltime;
    int totalFare;
    int taxiFare;
    int index;
    int pointIndex;
    String name;
    String description;
    String nextRoadName;
    int turnType;
    String pointType;

    int lineIndex;
    int roadType;
    int facilityType;
    int distance;
    int time;

    //처음 Array 분해
    public DestinationResponseItem(String type, JsonObject geometry, JsonObject properties) {
        this.type = type;
        this.geometry = geometry;
        this.properties = properties;
    }

    //geometry
    public DestinationResponseItem(String geometryType, JsonArray coordinates) {
        this.geometryType = geometryType;
        this.coordinates = coordinates;
    }

    //properties - first
    public DestinationResponseItem(int totalDistance, int totaltime, int totalFare, int taxiFare, int index, int pointIndex, String name, String description, String nextRoadName, int turnType, String pointType) {
        this.totalDistance = totalDistance;
        this.totaltime = totaltime;
        this.totalFare = totalFare;
        this.taxiFare = taxiFare;
        this.index = index;
        this.pointIndex = pointIndex;
        this.name = name;
        this.description = description;
        this.nextRoadName = nextRoadName;
        this.turnType = turnType;
        this.pointType = pointType;
    }

    //properties - point, finish
    public DestinationResponseItem(int index, int pointIndex, String name, String description, String nextRoadName, int turnType, String pointType) {
        this.index = index;
        this.pointIndex = pointIndex;
        this.name = name;
        this.description = description;
        this.nextRoadName = nextRoadName;
        this.turnType = turnType;
        this.pointType = pointType;
    }

    //properties - lineString
    public DestinationResponseItem(int index, int lineIndex, String name, String description, int distance,  int time, int roadType, int facilityType) {
        this.index = index;
        this.name = name;
        this.description = description;
        this.lineIndex = lineIndex;
        this.roadType = roadType;
        this.facilityType = facilityType;
        this.distance = distance;
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JsonObject getGeometry() {
        return geometry;
    }

    public void setGeometry(JsonObject geometry) {
        this.geometry = geometry;
    }

    public JsonObject getProperties() {
        return properties;
    }

    public void setProperties(JsonObject properties) {
        this.properties = properties;
    }

    public String getGeometryType() {
        return geometryType;
    }

    public void setGeometryType(String geometryType) {
        this.geometryType = geometryType;
    }

    public JsonArray getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(JsonArray coordinates) {
        this.coordinates = coordinates;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
    }

    public int getTotaltime() {
        return totaltime;
    }

    public void setTotaltime(int totaltime) {
        this.totaltime = totaltime;
    }

    public int getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(int totalFare) {
        this.totalFare = totalFare;
    }

    public int getTaxiFare() {
        return taxiFare;
    }

    public void setTaxiFare(int taxiFare) {
        this.taxiFare = taxiFare;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getPointIndex() {
        return pointIndex;
    }

    public void setPointIndex(int pointIndex) {
        this.pointIndex = pointIndex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNextRoadName() {
        return nextRoadName;
    }

    public void setNextRoadName(String nextRoadName) {
        this.nextRoadName = nextRoadName;
    }

    public int getTurnType() {
        return turnType;
    }

    public void setTurnType(int turnType) {
        this.turnType = turnType;
    }

    public String getPointType() {
        return pointType;
    }

    public void setPointType(String pointType) {
        this.pointType = pointType;
    }

    public int getLineIndex() {
        return lineIndex;
    }

    public void setLineIndex(int lineIndex) {
        this.lineIndex = lineIndex;
    }

    public int getRoadType() {
        return roadType;
    }

    public void setRoadType(int roadType) {
        this.roadType = roadType;
    }

    public int getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(int facilityType) {
        this.facilityType = facilityType;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
