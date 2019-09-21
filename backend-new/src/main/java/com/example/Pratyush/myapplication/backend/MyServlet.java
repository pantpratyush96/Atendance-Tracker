package com.example.Pratyush.myapplication.backend;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyServlet extends HttpServlet {
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

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        JSONObject json = new JSONObject();
        resp.setContentType("text/json");
        String latReceived = req.getParameter("lat");
        String lngReceived = req.getParameter("lng");

        double dist = 0.00;
        fetchXML(finalUrl);
        for (int i =0; i < nameList.size(); i++){
            try {
                JSONObject mJSON = new JSONObject();
                String latHots = latList.get(i);
                String lngHots = lngList.get(i);
                dist = distance(Double.valueOf(latReceived), Double.valueOf(lngReceived), Double.valueOf(latHots), Double.valueOf(lngHots));
                if (dist < 500){
                    mJSON.put("lat", latHots);
                    mJSON.put("lng", lngHots);
                    json.put(nameList.get(i), mJSON);
                }
            } catch (JSONException e) {
                e.getMessage();
            }
        }
            resp.getWriter().println(json);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String name = req.getParameter("name");
        String age = req.getParameter("age");
        int ageP = Integer.valueOf(age) + 10;
        resp.setContentType("text/plain");
        resp.getWriter().println("Hello " + name + "\nAge: " + ageP+10);
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        double distInKM = dist * 1.609344;
        double distInMeter = distInKM * 1000;
        return (distInMeter);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
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
}
