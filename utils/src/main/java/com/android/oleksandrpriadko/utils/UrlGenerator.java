package com.android.oleksandrpriadko.utils;

import android.view.View;

public class UrlGenerator {

    @SuppressWarnings("WeakerAccess")
    public static final int MAX_SIZE_MAP_IMAGE = 640;

    @SuppressWarnings("SpellCheckingInspection")
    public static String generateStaticMapUrl(double lat,
                                              double lon,
                                              int width,
                                              int height, String key) {
        return "http://maps.google.com/maps/api/staticmap?center="
                + lat + "," + lon
                + "&zoom=15"
                + "&size=" + width + "x" + height
                + "&sensor=false&markers=color:red%7Clabel%7C"
                + lat + "," + lon
                + "&key=" + key/*"AIzaSyBu6hLVBRiORrQlJlCURFDt3aoCQTBTO98"*/;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static String generateStaticMapUrl(View view, double lat, double lon, String key) {
        float coefficient = (float) MAX_SIZE_MAP_IMAGE / (float) view.getWidth();
        @SuppressWarnings("UnnecessaryLocalVariable") int width = MAX_SIZE_MAP_IMAGE;
        int height = (int) (view.getHeight() * coefficient);
        return "http://maps.google.com/maps/api/staticmap?center="
                + lat + "," + lon
                + "&zoom=15"
                + "&size=" + width + "x" + height
                + "&sensor=false&markers=color:red%7Clabel%7C"
                + lat + "," + lon
                + "&key=" + key/*"AIzaSyBu6hLVBRiORrQlJlCURFDt3aoCQTBTO98"*/;
    }
}
