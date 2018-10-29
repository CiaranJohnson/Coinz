package com.example.tech.coinz;

public class Coin {

    private String id;
    private String value;
    private String currency;
    private String markerSymbol;
    private String markerColor;

    public Coin(String id, String value, String currency, String markerSymbol, String markerColor) {
        this.id = id;
        this.value = value;
        this.currency = currency;
        this.markerSymbol = markerSymbol;
        this.markerColor = markerColor;
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
}
