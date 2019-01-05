package com.example.a1.myapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyService extends Service {

    private static final String TAG = "ServiceTag";
    public String imei;
    public String SimSerial;
    public String mPhoneNumber;
    private LocationManager locationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        android.os.Debug.waitForDebugger();
        inipProviderLocation();
        Log.i(TAG, "Сервис начал работу");

        final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        try {
            this.imei = telephonyManager.getDeviceId();

            this.mPhoneNumber = telephonyManager.getLine1Number();
            //  this.mPhoneNumber = this.mPhoneNumber.substring(1, this.mPhoneNumber.length());
            this.SimSerial = telephonyManager.getSimSerialNumber();

            Log.i(TAG, this.imei);
            Log.i(TAG, this.mPhoneNumber);
            Log.i(TAG, this.SimSerial);

            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 5000, 10f,
                    mLocationListeners[1]);

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 5000, 10f,
                    mLocationListeners[0]);
        } catch (final SecurityException e) {
            throw new RuntimeException();
        }

    }

    public MyService() {

    }

    protected void showLocation(final Location location) {
        if (location == null) {
            return;
        }
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {

            new MyService.RequestTask().execute(
                    this.mPhoneNumber,
                    formatLocationLat(location),
                    formatLocationLon(location),
                    formatLocationTime(location),
                    "GPS"
            );

        } else if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {

            new MyService.RequestTask().execute(
                    this.mPhoneNumber,
                    formatLocationLat(location),
                    formatLocationLon(location),
                    formatLocationTime(location),
                    "NET"
            );
        }
    }

    private String formatLocationLat(final Location location) {
        if (location == null) {
            return "";
        }
        return String.format(
                "%1$.5f",
                location.getLatitude());
    }

    private String formatLocationLon(final Location location) {
        if (location == null) {
            return "";
        }
        return String.format(
                "%1$.5f",
                location.getLongitude());
    }

    private String formatLocationTime(final Location location) {
        if (location == null) {
            return "";
        }
        return String.format(
                "%1$tF[%1$tT]",
                new Date(location.getTime()));
    }

    class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(final String... params) {
            try {

                final HttpClient httpclient = new DefaultHttpClient();
                final HttpPost http = new HttpPost("http://cargonet.live/log.php?id=" + params[0] + "&lat=" + params[1] + "&lon=" + params[2] + "&time=" + params[3] + "&type=" + params[4]);
                final List nameValuePairs = new ArrayList(2);
                nameValuePairs.add(new BasicNameValuePair("login", "user1"));
                nameValuePairs.add(new BasicNameValuePair("pswd", "1234"));
                http.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                final String response = (String) httpclient.execute(http, new BasicResponseHandler());
                Toast.makeText(getApplicationContext(), "send data", Toast.LENGTH_SHORT).show();
            } catch (final Exception e) {
                System.out.println("Exp=" + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String result) {
            /*dialog.dismiss();*/
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

    }

    @Override
    public IBinder onBind(final Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class LocationListenerImpl implements LocationListener {

        private Location location;

        public LocationListenerImpl(String provider) {
            location = new Location(provider);
            Log.d(TAG, String.valueOf(location));
        }

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

    }

    LocationListenerImpl[] mLocationListeners = new LocationListenerImpl[]{
            new LocationListenerImpl(LocationManager.GPS_PROVIDER),
            new LocationListenerImpl(LocationManager.NETWORK_PROVIDER)
    };

    private void inipProviderLocation() {
        if (locationManager == null) {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    public void onDestroy() {
        if (locationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    locationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
        super.onDestroy();
    }
}


