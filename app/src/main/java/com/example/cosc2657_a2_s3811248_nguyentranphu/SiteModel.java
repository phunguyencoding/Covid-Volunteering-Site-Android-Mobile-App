package com.example.cosc2657_a2_s3811248_nguyentranphu;

import com.google.firebase.firestore.GeoPoint;
import java.util.List;
import java.util.ArrayList;

public class SiteModel {
    private List<String> participants;
    private GeoPoint geoPoint;
    private String description;
    private int testedPeople;
    private String host;
    private String location;
    private String date;
    private int thumbnail;

    public SiteModel(String location, String host, String date, String description, GeoPoint geoPoint, int thumbnail) {
        this.description = description;
        this.host = host;
        this.location = location;
        this.date = date;
        this.geoPoint = geoPoint;
        this.testedPeople = 0;
        this.participants = new ArrayList<>();
        this.thumbnail = thumbnail;
    }

    public SiteModel(String location, String host, String date, String description, GeoPoint geoPoint, int testedPeople, int thumbnail) {
        this.description = description;
        this.host = host;
        this.location = location;
        this.date = date;
        this.geoPoint = geoPoint;
        this.testedPeople = testedPeople;
        this.participants = new ArrayList<>();
        this.thumbnail = thumbnail;
    }


    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTestedPeople() {
        return testedPeople;
    }

    public void setTestedPeople(int testedPeople) {
        this.testedPeople = testedPeople;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public void addParticipant(String participant) {
        this.getParticipants().add(participant);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
