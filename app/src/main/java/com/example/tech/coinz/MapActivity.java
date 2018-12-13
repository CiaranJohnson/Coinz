package com.example.tech.coinz;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;



import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonObject;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.mapbox.mapboxsdk.geometry.LatLng;
import android.support.annotation.NonNull;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;


import org.json.JSONException;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import com.mapbox.mapboxsdk.annotations.Icon;


import timber.log.Timber;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener, MapboxMap.OnMarkerClickListener {

    private static final String TAG = "MapActivity";

    private MapView mapView;
    private MapboxMap map;
    public Button GamesBtn, ProfileBtn, BankBtn;


    // variables for adding location layer

    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationLayerPlugin;
    private LocationEngine locationEngine;
    Location originLocation;

    public FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseFirestore db;
    private DocumentReference mCurrentUserRef;


    private ArrayList<Marker> markers;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timber.plant(new Timber.DebugTree());

        Mapbox.getInstance(MapActivity.this, getString(R.string.access_token));
        setContentView(R.layout.activity_map);


        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        if(mFirebaseUser == null) {
            Timber.d( "onCreate: no user");
            mapView.onDestroy();
            Intent intent = new Intent(MapActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }


        markers = new ArrayList<>();
        GamesBtn = findViewById(R.id.GamesBtn);
        BankBtn = findViewById(R.id.BankBtn);
        ProfileBtn = findViewById(R.id.ProfileBtn);
        mCurrentUserRef = db.collection("User").document(mFirebaseUser.getUid());

        String date = getDate();
        Timber.d( "onCreate: the current date is:%s", date);

        db.collection("User").document(mFirebaseUser.getUid()).collection("Date")
                .document("LastUsed").get().addOnSuccessListener(documentSnapshot -> {
                    Map<String, Object> dateInfo = documentSnapshot.getData();
                    if(dateInfo!= null){
                        if(!date.equals(dateInfo.get("LastUpdated"))){
                            Timber.d( "onSuccess: %s", date);
                            newDay(date);
                        }
                    }
                });



        BankBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MapActivity.this, BankActivity.class);
            startActivity(intent);
        });

        GamesBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MapActivity.this, BonusFeatureActivity.class);
            startActivity(intent);
        });

        ProfileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MapActivity.this, ProfileActivity.class);
            startActivity(intent);
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
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        Timber.d("newDay: %s", json);

        try {
            BankBackend.getExchangeRate(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        BankBackend.updateDate();
        deleteOldMap(json);
        BankBackend.moveCoinsToSpareChange();



    }

    private void deleteOldMap(String json){
        mCurrentUserRef.collection("Map").get().addOnSuccessListener(queryDocumentSnapshots -> {
            Timber.d("deleteOldMap: Successfully retrieved the old map");
            if(queryDocumentSnapshots.getDocuments().size() > 0) {
                for (DocumentSnapshot doc:queryDocumentSnapshots.getDocuments()){
                    doc.getReference().delete();
                }
            }
            getNewMap(json);
        }).addOnFailureListener(e -> Timber.e("deleteOldMap: %s", e.getMessage()));
    }


    private void getNewMap(String json){
        FeatureCollection fc = FeatureCollection.fromJson(json);
        List<Feature> features = fc.features();
        int counter = 0;

        if (features != null){
            for (Feature feature: features) {
                Point point = (Point) feature.geometry();
                List<Double> latlng = null;
                if (point != null) {
                    latlng = point.coordinates();
                }
                JsonObject properties = feature.properties();

                assert properties != null;
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
                coin.put("MarkerColor", markerColor);
                coin.put("Longitude", Objects.requireNonNull(latlng).get(0));
                coin.put("Latitude", latlng.get(1));
                counter++;
                mCurrentUserRef.collection("Map").document(id).set(coin).addOnSuccessListener(aVoid ->
                        Timber.d("onSuccess: Successfully added coin: %s", id)
                ).addOnFailureListener(e ->
                        Timber.e("onFailure: Failed to add coin: " + id + " due to " + e.getMessage())
                );
                Timber.d("getNewMap: %s", features.size());
                Timber.d("getNewMap: %s", counter);
                if (counter == features.size()) {
                    addMarkersToMap();
                }
            }

        }



    }



    @Override
    public void onMapReady(MapboxMap mapboxMap) {

        if(mapboxMap == null){

            Timber.d("onMapReady: mapBox is null");
        } else {

            this.map = mapboxMap;
            map.getUiSettings().setCompassEnabled(true);
            map.setOnMarkerClickListener(this);
            enableLocationPlugin();


            if(markers!= null){
                addMarkersToMap();
            }
        }

    }

    private void addMarkersToMap(){
        if(map == null){
            Timber.d("addMarkersToMap: Map not ready yet");
        } else {
            db.collection("User").document(mFirebaseUser.getUid()).collection("Map").get().addOnSuccessListener(queryDocumentSnapshots -> {
                for(DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()){
                    Map<String, Object> marker = doc.getData();
                    LatLng latLng = new LatLng(Double.parseDouble(marker.get("Latitude").toString()),Double.parseDouble(marker.get("Longitude").toString()));


                    IconFactory iconFactory = IconFactory.getInstance(MapActivity.this);


                    if(marker.get("Currency").equals("DOLR")){

                        Icon icon = iconFactory.fromResource(R.mipmap.ic_dollar_foreground);
                        markers.add(map.addMarker(new MarkerOptions().setPosition(latLng).setTitle(Objects.requireNonNull(marker.get("ID")).toString()).icon(icon)));

                    } else if(marker.get("Currency").equals("SHIL")){

                        Icon icon = iconFactory.fromResource(R.mipmap.ic_shilling_foreground);
                        markers.add(map.addMarker(new MarkerOptions().setPosition(latLng).setTitle(Objects.requireNonNull(marker.get("ID")).toString()).icon(icon)));

                    } else if(Objects.equals(marker.get("Currency"), "QUID")){

                        Icon icon = iconFactory.fromResource(R.mipmap.ic_quid_foreground);
                        markers.add(map.addMarker(new MarkerOptions().setPosition(latLng).setTitle(Objects.requireNonNull(marker.get("ID")).toString()).icon(icon)));

                    } else{

                        Icon icon = iconFactory.fromResource(R.mipmap.ic_peny_foreground);
                        markers.add(map.addMarker(new MarkerOptions().setPosition(latLng).setTitle(Objects.requireNonNull(marker.get("ID")).toString()).icon(icon)));
                    }

                }
            }).addOnFailureListener(e -> Log.e(TAG, "addMarkersToMap: Failed: " + e));
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
        locationEngine.setInterval(5000); // preferably every 5 seconds
        locationEngine.setFastestInterval(1000); // at most every second
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            originLocation = lastLocation;
            Log.d(TAG, "initializeLocationEngine: last known location " + originLocation.toString());
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
    @SuppressWarnings("MissingPermission")
    public void onConnected() {
        Log.d(TAG,"[onConnected] requesting location updates");
        locationEngine.requestLocationUpdates();

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: ");
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
        Log.d(TAG, "collectCoin: " + marker.getPosition().distanceTo(userLatLng));
        if(marker.getPosition().distanceTo(userLatLng)<25){
            Log.d(TAG, "collectCoin: " + marker.getTitle());
            map.removeMarker(marker);
            BankBackend.pickUpCoin(marker.getTitle(), getApplicationContext());

        }
    }



    @Override
    @SuppressWarnings("MissingPermission")
    public boolean onMarkerClick(@NonNull Marker marker) {
        Log.d(TAG, "onMarkerClick: Marker has been clicked.");
        Location lastLocation = locationEngine.getLastLocation();
        Double longitude = lastLocation.getLongitude();
        Double latitude = lastLocation.getLatitude();
        LatLng userLatLng = new LatLng(latitude, longitude);
        Toast.makeText(this, "You are " + Math.ceil(marker.getPosition().distanceTo(userLatLng)) + " metres from this coin.", Toast.LENGTH_LONG).show();
        return true;
    }



}
