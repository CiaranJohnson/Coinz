package com.example.tech.coinz;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class Coin {

    private String id;
    private String value;
    private String currency;
    private String markerSymbol;
    private String markerColor;
    private LatLng latLng;

    public Coin(String id, String value, String currency, String markerSymbol, String markerColor, LatLng latLng) {
        this.id = id;
        this.value = value;
        this.currency = currency;
        this.markerSymbol = markerSymbol;
        this.markerColor = markerColor;
        this.latLng = latLng;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public String getCurrency() {
        return currency;
    }

    public String getMarkerSymbol() {
        return markerSymbol;
    }

    public String getMarkerColor() {
        return markerColor;
    }

    public  LatLng getLatLng(){
        return latLng;
    }
}
