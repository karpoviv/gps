package com.example.a1.myapplication;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import android.telephony.TelephonyManager;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.util.Log;

public class MyService extends Service {

    private static final String TAG = "ServiceTag";
    public String imei;
    public String SimSerial;
    public String mPhoneNumber;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Сервис начал работу");

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        this.imei = telephonyManager.getDeviceId();

        this.mPhoneNumber = telephonyManager.getLine1Number();
        this.mPhoneNumber = this.mPhoneNumber.substring(1, this.mPhoneNumber.length());
        this.SimSerial = telephonyManager.getSimSerialNumber();

        Log.i(TAG, this.imei);
        Log.i(TAG, this.mPhoneNumber);
        Log.i(TAG, this.SimSerial);

    }

    public MyService() {

    }


    private void showLocation(Location location) {
        if (location == null)
            return;
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
    }

    class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            try {

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost http = new HttpPost("http://cargonet.live/log.php?id="+params[0]+"&lat="+params[1]+"&lon="+params[2]+"&time="+params[3]+"&type="+params[4]);
                List nameValuePairs = new ArrayList(2);
                nameValuePairs.add(new BasicNameValuePair("login", "user1"));
                nameValuePairs.add(new BasicNameValuePair("pswd", "1234"));
                http.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                String response = (String) httpclient.execute(http, new BasicResponseHandler());
            } catch (Exception e) {
                System.out.println("Exp=" + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            /*dialog.dismiss();*/
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
