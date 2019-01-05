package com.example.a1.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

public class MainActivity extends AppCompatActivity implements LocationListener {

    public static final float ZOOM = 10.0f;
    private static final String API_KEY = "92607412-b875-44bf-b021-fc8580820375";
    private MapView mapView;
    private LocationManager locationManager;
    private PlacemarkMapObject mapObjectCollection;
    private ImageProvider placeMaker;

    @Override
    public void onLocationChanged(final Location location) {
        if (location != null) {
            if (mapObjectCollection != null) {
                mapView.getMap().getMapObjects().remove(mapObjectCollection);
                setCamera(location);
            }
        }
    }

    @Override
    public void onStatusChanged(final String provider, final int status, final Bundle extras) {

    }

    @Override
    public void onProviderEnabled(final String provider) {

    }

    @Override
    public void onProviderDisabled(final String provider) {

    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey(API_KEY);
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_main);

        init();
        initLocation();
    }

    private void init() {

        mapView = findViewById(R.id.mapview);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //don't supported vector image
        placeMaker = ImageProvider.fromResource(this, R.drawable.geofence);
    }

    public void startService(final View view) {
        startService(new Intent(this, MyService.class));
    }

    public void stopService(final View view) {
        stopService(new Intent(this, MyService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            initLocation();
        } else {
            Toast.makeText(this, "set permissions", Toast.LENGTH_SHORT).show();
        }
    }

    private void initLocation() {
        final boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        final boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Location location1;
        if (!(isGPSEnabled || isNetworkEnabled)) {
        } else {
            if (isNetworkEnabled) {
                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED & ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.READ_PHONE_STATE}, 1);
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        5000, 10, this);
                location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        5000, 10, this);
                location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            if (location1 != null) {
                setCamera(location1);
            }
        }
    }

    public void setCamera(Location location) {
        if (location != null) {

            mapObjectCollection = mapView.getMap().getMapObjects().addPlacemark(
                    new Point(location.getLatitude(), location.getLongitude()), placeMaker);

            mapView.getMap().move(new CameraPosition(
                            new Point(location.getLatitude(), location.getLongitude()),
                            ZOOM, 0.0f, 0.0f),
                    new Animation(Animation.Type.SMOOTH, 0), null);

        }

    }

}
