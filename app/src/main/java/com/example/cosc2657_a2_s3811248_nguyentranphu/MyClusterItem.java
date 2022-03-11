package com.example.cosc2657_a2_s3811248_nguyentranphu;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyClusterItem implements ClusterItem {
    private final LatLng position;
    private final String title;

    public MyClusterItem(double lat, double lng, String title) {
        position = new LatLng(lat, lng);
        this.title = title;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return null;
    }
}