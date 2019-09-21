package com.example.pratyush.geofencing;

import android.Manifest;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;

//import com.example.Pratyush.myapplication.backend.MyEndpoint;
import com.google.android.gms.location.LocationListener;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVWriter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import static com.android.volley.Request.Method.*;
import static com.example.pratyush.geofencing.R.id.map;
import java.net.HttpURLConnection;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class MainActivity extends AppCompatActivity  implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener/*, AsyncTask<String, String, String>*/{
    protected GoogleApiClient googleApiClient;
    protected LocationManager locationManager;
    private Circle geoFenceLimits;
    protected LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

        }
    };

    LocationRequest mLocationRequest;
    private GoogleMap mMap;
    protected double lat;
    protected double lng;
    LatLng latLng;
    Marker currLocationMarker;

    private String nameRec = null;
    private String latRec = null;
    private String lngRec = null;
    private String radRec = null;
    private String typeRec = null;

    private volatile boolean nameRecFound = false;
    private volatile boolean latRecFound = false;
    private volatile boolean lngRecFound = false;
    private volatile boolean radRecFound = false;
    private volatile boolean typeRecFound = false;
    private String finalUrl = "https://spreadsheets.google.com/feeds/list/19N61Dim6O_9bfyHuQWn3L7gN_RZ1Vq1_FozjDiUitPI/od6/public/values";

    private XmlPullParserFactory xmlFactoryObject;
    public volatile boolean parsingComplete = true;
    ArrayList<String> nameList = new ArrayList<String>();
    ArrayList<String> latList = new ArrayList<String>();
    ArrayList<String> lngList = new ArrayList<String>();
    ArrayList<String> radList = new ArrayList<String>();
    ArrayList<String> typeList = new ArrayList<String>();

    protected synchronized void buildGoogleApiClient() {
        Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buildGoogleApiClient();
        googleApiClient.connect();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);

        Button mButton2 = (Button)findViewById(R.id.button2);
        Button mButton3 = (Button)findViewById(R.id.button3);
        Button mButton4 = (Button)findViewById(R.id.button4);
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLocationMonitoring();
                drawGeofence();
                Double latD = Double.valueOf(lat);
                Double lngD = Double.valueOf(lng);
                appengine(latD, lngD);
            }
        });
        mButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGeoFenceMonitoring();
            }
        });
        mButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopGeoFenceMonitoring();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        mMap = gMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        fetchXML(finalUrl);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (mLastLocation != null) {
            //place marker at current position
            //mGoogleMap.clear();
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currLocationMarker = mMap.addMarker(markerOptions);
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        lat = mLastLocation.getLatitude();
        lng = mLastLocation.getLongitude();

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest,locationListener);
    }


    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this,"onConnectionSuspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,"onConnectionFailed",Toast.LENGTH_SHORT).show();
    }

    public void onLocationChanged(Location location) {

        //place marker at current position
        //mGoogleMap.clear();
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        currLocationMarker = mMap.addMarker(markerOptions);

        Toast.makeText(this,"Location Changed",Toast.LENGTH_SHORT).show();

        //zoom to current position:
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));
    }

    //User Location
    private Location userLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3600000, 500, (android.location.LocationListener) locationListener);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        return location;
    }

    private void drawGeofence() {
        int maxLength = nameList.size();
        for (int i =0; i < maxLength; i++){
            if ( geoFenceLimits != null )
                geoFenceLimits.remove();
            float [] dist = new float[1];
            Location.distanceBetween(lat, lng, Double.valueOf(latList.get(i)), Double.valueOf(lngList.get(i)), dist);
            if (dist[0] < 1000){
                mMap.addCircle(new CircleOptions()
                        .center(new LatLng(Double.valueOf(latList.get(i)),Double.valueOf(lngList.get(i))))
                        .strokeColor(Color.argb(50, 70,70,70))
                        .radius(Double.valueOf(radList.get(i)))
                        .fillColor(Color.argb(100, 150,150,150)));
            }
        }
    }

    //Location Monitor
    private void startLocationMonitoring() {
        Log.d("geof", "Start Location Called");
        try {
            LocationRequest locationRequest = LocationRequest.create()
                    .setInterval(10000)
                    .setFastestInterval(5000)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new com.google.android.gms.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (currLocationMarker != null) {
                        currLocationMarker.remove();
                    }
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("Current Position");
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                    currLocationMarker = mMap.addMarker(markerOptions);
                }
            });
        } catch (SecurityException e){
            Log.d("GeoL", e.getMessage());
        }
    }

    private void startGeoFenceMonitoring() {
        try {
            int maxLength = nameList.size();
            for (int i = 0; i < maxLength; i++) {
                String geofenceID = "GEOFENCE_ID" + String.valueOf(i);
                Geofence geofence = new Geofence.Builder()
                        .setRequestId(geofenceID)
                        .setCircularRegion(Double.valueOf(latList.get(i)), Double.valueOf(lngList.get(i)), Integer.valueOf(radList.get(i)))
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setNotificationResponsiveness(1000)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build();

                GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                        .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                        .addGeofence(geofence)
                        .build();

                Intent intent = new Intent(this, GeoFenceService.class);
                PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                if (!googleApiClient.isConnected()) {
                    Log.d("geof", "GoogleAPiClient not connected");
                } else {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    LocationServices.GeofencingApi.addGeofences(googleApiClient, geofencingRequest, pendingIntent)
                            .setResultCallback(new ResultCallback<Status>() {
                                @Override
                                public void onResult(@NonNull Status status) {
                                    if (status.isSuccess()) {
                                    } else {
                                    }
                                }
                            });
                }
            }
        } catch (SecurityException e){
            Log.d("GeoL", e.getMessage());
        }
    }

    private void stopGeoFenceMonitoring() {
        try {
            int maxLength = nameList.size();
            for (int i = 0; i < maxLength; i++) {
                String geofenceID = "GEOFENCE_ID" + String.valueOf(i);
//                Log.d("geof", "Monitoring stopped");
                ArrayList<String> geofenceIds = new ArrayList<String>();
                geofenceIds.add(geofenceID);
                LocationServices.GeofencingApi.removeGeofences(googleApiClient, geofenceIds);

            }
        } catch (SecurityException e){
            Log.d("GeoL", e.getMessage());
        }
    }

    //Retrieving data from JSON
    public List<NetworkType> getData() throws JSONException {
        InputStream is = getResources().openRawResource(R.raw.tower_cellid);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String jsonString = writer.toString();

        //JSONString to JSONArray
        JSONArray jsonArray = new JSONArray(jsonString);
        String[] strArr = new String[jsonArray.length()];

        for (int i = 0; i < jsonArray.length(); i++) {
            strArr[i] = jsonArray.getString(i);
        }

        Gson gson = new GsonBuilder().create();
        List<NetworkType> networkTypes = new ArrayList<>();
        networkTypes = Arrays.asList(gson.fromJson(jsonString, NetworkType[].class));
        return networkTypes;
    }

    //Print Output
    public String print(String a, String b, String c) {
        String output = "{\"Latitude\":\"" + a.toString() +
                "\",\"Longitude\":\"" + b.toString() +
                "\",\"Accuracy:\"" + c.toString() + "\"}";
        return output;
    }


    public void parseXMLAndStoreIt(XmlPullParser myParser) {
        int event;
        try {
            event = myParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String name=myParser.getName();
                switch (event){
                    case XmlPullParser.START_TAG:

                        if(name.equals("gsx:name")){
                            nameRecFound = true;
                        } else if(name.equals("gsx:lat")){
                            latRecFound = true;
                        } else if(name.equals("gsx:lng")){
                            lngRecFound = true;
                        } else if(name.equals("gsx:radius")){
                            radRecFound = true;
                        } else if(name.equals("gsx:type")){
                            typeRecFound = true;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        if(nameRecFound){
                            nameRec = myParser.getText();
                            nameList.add(nameRec);
                            nameRecFound = false;
                        } else if(latRecFound){
                            latRec = myParser.getText();
                            latList.add(latRec);
                            latRecFound = false;
                        } else if(lngRecFound){
                            lngRec = myParser.getText();
                            lngList.add(lngRec);
                            lngRecFound = false;
                        } else if(radRecFound){
                            radRec = myParser.getText();
                            radList.add(radRec);
                            radRecFound = false;
                        } else if(typeRecFound){
                            typeRec = myParser.getText();
                            typeList.add(typeRec);
                            typeRecFound = false;
                        }
                        break;
                    case XmlPullParser.END_TAG:

                        break;
                }
                event = myParser.next();
            }
            parsingComplete = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void fetchXML(final String urlString){
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection)
                            url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream stream = conn.getInputStream();

                    xmlFactoryObject = XmlPullParserFactory.newInstance();
                    XmlPullParser myparser = xmlFactoryObject.newPullParser();

                    myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    myparser.setInput(stream, null);
                    parseXMLAndStoreIt(myparser);
                    stream.close();
                } catch (Exception e) {
                    parsingComplete = false;
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void appengine(double lat, double lng){
        String urlAppEngine = "https://geofencing-171207.appspot.com/nearHOTS?lat="+ lat +"&lng="+ lng;
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (GET, urlAppEngine, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject jsonObject = null;
                        jsonObject = response;
                        String result = String.valueOf(jsonObject);
                        Log.d("GeoL", result);
                        drawHotspot(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Response", "error" + error.toString());
                    }
                });
        queue.add(jsObjRequest);
    }

    private void drawHotspot(JSONObject jsonObjectAE) {
        JSONObject jsonObject = jsonObjectAE;
        JSONObject hotspotlatlngObject;
        int i = 0;
        Iterator<String> iter = jsonObject.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            i++;
            try {
                hotspotlatlngObject = jsonObject.getJSONObject(key);
                Log.d("GeoL", key);
                if ( geoFenceLimits != null )
                    geoFenceLimits.remove();
                mMap.addCircle(new CircleOptions()
                        .center(new LatLng(Double.valueOf(hotspotlatlngObject.getString("lat")),Double.valueOf(hotspotlatlngObject.getString("lng"))))
                        .strokeColor(Color.rgb(99,4,249))
                        .radius(Double.valueOf(radList.get(i)))
                        .fillColor(Color.rgb(4,181,249)));
                Log.d("GeoL", "Circle with LatLng " + hotspotlatlngObject.getString("lat") + " " + hotspotlatlngObject.getString("lng") + " printed.");
            } catch (JSONException e) {
                e.getMessage();
            }
        }
    }
}