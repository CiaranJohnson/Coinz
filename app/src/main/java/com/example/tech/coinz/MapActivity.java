package com.example.tech.coinz;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntegerRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonObject;
import com.google.protobuf.DurationOrBuilder;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.mapbox.mapboxsdk.geometry.LatLng;
import android.support.annotation.NonNull;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import com.mapbox.mapboxsdk.annotations.Icon;

import javax.annotation.Nullable;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener, MapboxMap.OnMarkerClickListener {

    private static final String TAG = "MapActivity";

    private MapView mapView;
    private MapboxMap map;
    public Button SettingsBtn, ProfileBtn, BankBtn;

    private static final String URL_DATA = "http://homepages.inf.ed.ac.uk/stg/coinz/2019/12/31/coinzmap.geojson";
    private ArrayList<Coin> coinList;


    // variables for adding location layer

    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationLayerPlugin;
    private LocationEngine locationEngine;
    private Location originLocation;

    public FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseFirestore db;
    private DocumentReference mCurrentUserRef;


    private ArrayList<Marker> markers;
    private ArrayList<String> collectedCoins;

    private String date;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Mapbox.getInstance(MapActivity.this, getString(R.string.access_token));
        setContentView(R.layout.activity_map);


        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        if(mFirebaseUser == null) {
            Log.d(TAG, "onCreate: no user");
            mapView.onDestroy();
            Intent intent = new Intent(MapActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        collectedCoins = new ArrayList<>();
        coinList = new ArrayList<Coin>();
        markers = new ArrayList<>();
        SettingsBtn = (Button) findViewById(R.id.SettingsBtn);
        BankBtn = (Button) findViewById(R.id.BankBtn);
        ProfileBtn = (Button) findViewById(R.id.ProfileBtn);
        mCurrentUserRef = db.collection("User").document(mFirebaseUser.getUid());

        String date = getDate();
        Log.d(TAG, "onCreate: the current date is:" + date);

        db.collection("User").document(mFirebaseUser.getUid()).collection("Date")
                .document("LastUsed").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> dateInfo = documentSnapshot.getData();
                if(!date.equals(dateInfo.get("LastUpdated"))){
                    Log.d(TAG, "onSuccess: " + date);
                    newDay(date);
                }
            }
        });



        BankBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, BankActivity.class);
                startActivity(intent);
            }
        });

        SettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        ProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

    }

    private String getDate(){
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd", Locale.ENGLISH);
        return dateTimeFormatter.format(localDateTime);
    }

    private void newDay(String date){

        DownloadTaskFile downloadTaskFile = new DownloadTaskFile();
        String json = null;
        try {
            json = downloadTaskFile.execute("http://homepages.inf.ed.ac.uk/stg/coinz/" + date + "/coinzmap.geojson").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "newDay: " + json);

        try {
            Backend.getExchangeRate(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Backend.updateDate();
        deleteOldMap(json);
        Backend.moveCoinsToSpareChange(TAG);



    }

    private void deleteOldMap(String json){
        mCurrentUserRef.collection("Map").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d(TAG, "deleteOldMap: Successfully retrieved the old map");
                if(queryDocumentSnapshots.getDocuments() != null) {
                    for (DocumentSnapshot doc:queryDocumentSnapshots.getDocuments()){
                        doc.getReference().delete();
                    }
                }
                getNewMap(json, TAG);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "deleteOldMap: " + e.getMessage());
            }
        });
    }


    private void getNewMap(String json, String TAG){
        FeatureCollection fc = FeatureCollection.fromJson(json);
        List<Feature> features = (List<Feature>) fc.features();
        int counter = 0;

        for (Feature feature: features){
            Point point = (Point) feature.geometry();
            List<Double> latlng = point.coordinates();
            JsonObject properties = feature.properties();
            String id = properties.get("id").getAsString();
            String value = properties.get("value").getAsString();
            String currency = properties.get("currency").getAsString();
            String markerSymbol = properties.get("marker-symbol").getAsString();
            String markerColor = properties.get("marker-color").getAsString();

            Map<String, Object> coin = new HashMap<>();
            coin.put("ID", id);
            coin.put("Value", value);
            coin.put("Currency", currency);
            coin.put("MarkerSymbol", markerSymbol);
            coin.put("MarkerColor",markerColor);
            coin.put("Longitude", latlng.get(0));
            coin.put("Latitude", latlng.get(1));
            counter++;
            int count = counter;
            mCurrentUserRef.collection("Map").document(id).set(coin).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "onSuccess: Successfully added coin: " + id);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: Failed to add coin: " + id + " due to "+ e.getMessage());
                }
            });
        }
        Log.d(TAG, "getNewMap: " + features.size());
        Log.d(TAG, "getNewMap: " + counter);
        if(counter == features.size()){
            addMarkersToMap();
        }


    }



    @Override
    public void onMapReady(MapboxMap mapboxMap) {

        if(mapboxMap == null){

            Log.d(TAG, "onMapReady: mapBox is null");
        } else {

            this.map = mapboxMap;
            map.getUiSettings().setCompassEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);
            map.setOnMarkerClickListener(this);
            enableLocationPlugin();

            if(markers!= null){
                addMarkersToMap();
            }
        }

    }

    private void addMarkersToMap(){
        if(map == null){
            Log.d(TAG, "addMarkersToMap: Map not ready yet");
        } else {
            db.collection("User").document(mFirebaseUser.getUid()).collection("Map").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for(DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()){
                        Map<String, Object> marker = doc.getData();
                        LatLng latLng = new LatLng(Double.parseDouble(marker.get("Latitude").toString()),Double.parseDouble(marker.get("Longitude").toString()));


                        IconFactory iconFactory = IconFactory.getInstance(MapActivity.this);


                        if(marker.get("Currency").equals("DOLR")){

                            Icon icon = iconFactory.fromResource(R.mipmap.ic_dollar_foreground);
                            markers.add(map.addMarker(new MarkerOptions().setPosition(latLng).setTitle(marker.get("ID").toString()).icon(icon)));

                        } else if(marker.get("Currency").equals("SHIL")){

                            Icon icon = iconFactory.fromResource(R.mipmap.ic_shilling_foreground);
                            markers.add(map.addMarker(new MarkerOptions().setPosition(latLng).setTitle(marker.get("ID").toString()).icon(icon)));

                        } else if(marker.get("Currency").equals("QUID")){

                            Icon icon = iconFactory.fromResource(R.mipmap.ic_quid_foreground);
                            markers.add(map.addMarker(new MarkerOptions().setPosition(latLng).setTitle(marker.get("ID").toString()).icon(icon)));

                        } else{

                            Icon icon = iconFactory.fromResource(R.mipmap.ic_peny_foreground);
                            markers.add(map.addMarker(new MarkerOptions().setPosition(latLng).setTitle(marker.get("ID").toString()).icon(icon)));
                        }

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "addMarkersToMap: Failed: " + e);
                }
            });
        }
    }



    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationPlugin() {

        Log.d(TAG, "enableLoactionPlugin");

        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            initializeLocationEngine();
            // Create an instance of the plugin. Adding in LocationLayerOptions is also an optional
            // parameter
            LocationLayerPlugin locationLayerPlugin = new LocationLayerPlugin(mapView, map);

            // Set the plugin's camera mode
            locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
            getLifecycle().addObserver(locationLayerPlugin);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressWarnings( {"MissingPermission"})
    private void initializeLocationEngine() {
        LocationEngineProvider locationEngineProvider = new LocationEngineProvider(this);
        locationEngine = locationEngineProvider.obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            originLocation = lastLocation;
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void initializeLocationLayer(){
        if (mapView == null){
            Log.d(TAG, "map view is null");
        } else{
            if (map == null){
                Log.d(TAG, "mapbox map is null");
            } else{
                locationLayerPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
                locationLayerPlugin.setLocationLayerEnabled(true);
                locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
                locationLayerPlugin.setRenderMode(RenderMode.NORMAL);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationPlugin();
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @SuppressWarnings( {"MissingPermission"})
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStart();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    public void onConnected() {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null){
            Log.d(TAG, "onLocationChanged: Location is null");
        } else {
            Log.d(TAG, "onLocationChanged: Location has been changed");
            for (Marker marker: markers){
                collectCoin(location, marker);
            }
        }
    }


    private void collectCoin(Location location, Marker marker){
        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();
        LatLng userLatLng = new LatLng(latitude, longitude);


        if(marker.getPosition().distanceTo(userLatLng)<100){
            Backend.pickUpCoin(marker.getTitle(), TAG);
            map.removeMarker(marker);
        }
    }



    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Log.d(TAG, "onMarkerClick: Marker has been clicked.");
        collectCoin(originLocation, marker);
        return true;
    }



}
