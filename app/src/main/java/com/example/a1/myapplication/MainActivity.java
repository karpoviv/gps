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
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
    private WebView webView;
    private Location location;
    private LocationManager locationManager;
    private PlacemarkMapObject mapObjectCollection;
    private ImageProvider placeMaker;

    @Override
    public void onLocationChanged(final Location location) {
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
    }

    private void init() {

    //    mapView = findViewById(R.id.mapview);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        checkPermissions();
        //don't supported vector image
        placeMaker = ImageProvider.fromResource(this, R.drawable.geofence);
        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://cargonet.live/map2.html");

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

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            this.location = getLocationOnProvider();

        } else {
            Toast.makeText(this, "set permissions", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED & ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_PHONE_STATE}, 1);
            return false;
        }
        return true;
    }

    private Location getLocationOnProvider() {
        final boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        final boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Location location1 = null;
        if (isNetworkEnabled) {
            try {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        5000, 10, this);
                location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } catch (final SecurityException e) {
                Log.d("permition", "getPermition");
                throw new RuntimeException();
            }
        }

        if (isGPSEnabled) {
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        5000, 10, this);
                location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } catch (final SecurityException e) {
                Log.d("permition", "getPermition");
                throw new RuntimeException();
            }
        }

        return location1;
    }

}
