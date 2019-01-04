package com.example.a1.myapplication;

import android.Manifest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.*;
import android.os.AsyncTask;

import android.support.v4.content.ContextCompat;
import android.content.pm.PackageManager;

public class MainActivity extends AppCompatActivity {

    TextView tvEnabledGPS;
    TextView tvStatusGPS;
    TextView tvLocationGPS;
    TextView tvLocationGPSLat;
    TextView tvEnabledNet;
    TextView tvStatusNet;
    TextView tvLocationLat;
    TextView tvLocationLon;
    TextView tvLocationTime;


    private LocationManager locationManager;
    StringBuilder sbGPS = new StringBuilder();
    StringBuilder sbNet = new StringBuilder();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvEnabledGPS = (TextView) findViewById(R.id.tvEnabledGPS);
        tvStatusGPS = (TextView) findViewById(R.id.tvStatusGPS);
        tvEnabledNet = (TextView) findViewById(R.id.tvEnabledNet);
        tvStatusNet = (TextView) findViewById(R.id.tvStatusNet);
        tvLocationLat = (TextView) findViewById(R.id.tvLocationLat);
        tvLocationLon = (TextView) findViewById(R.id.tvLocationLon);
        tvLocationTime = (TextView) findViewById(R.id.tvLocationTime);

        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE};
        requestPermissions(perms, 1337);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }


    public void startService(View view){
        startService(new Intent(this, MyService.class));
    }

    public void stopService(View view){
        stopService(new Intent(this, MyService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                locationListener);
        checkEnabled();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                tvStatusGPS.setText("Status: " + String.valueOf(status));
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                tvStatusNet.setText("Status: " + String.valueOf(status));
            }
        }
    };

    private void showLocation(Location location) {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER))
        {
            tvLocationLat.setText(formatLocationLat(location));
            tvLocationLon.setText(formatLocationLon(location));
            tvLocationTime.setText(formatLocationTime(location));
        }
        else if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER))
        {
            tvLocationLat.setText(formatLocationLat(location));
            tvLocationLon.setText(formatLocationLon(location));
            tvLocationTime.setText(formatLocationTime(location));
        }
    }

    private String formatLocationLat(Location location) {
        if (location == null)
            return "";
        return String.format(
                "%1$.5f",
                location.getLatitude());
    }

    private String formatLocationLon(Location location) {
        if (location == null)
            return "";
        return String.format(
                "%1$.5f",
                location.getLongitude());
    }

    private String formatLocationTime(Location location) {
        if (location == null)
            return "";
        return String.format(
                "%1$tF[%1$tT]",
                new Date(location.getTime()));
    }/**/

    private void checkEnabled() {
        tvEnabledGPS.setText("Enabled: "
                + locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER));
        tvEnabledNet.setText("Enabled: "
                + locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    public void onClickLocationSettings(View view) {
        startActivity(new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

}
